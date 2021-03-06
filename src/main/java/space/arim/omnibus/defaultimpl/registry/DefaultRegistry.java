/* 
 * Omnibus
 * Copyright © 2020 Anand Beh
 * 
 * Omnibus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Omnibus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Omnibus. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package space.arim.omnibus.defaultimpl.registry;

import space.arim.omnibus.events.EventBus;
import space.arim.omnibus.registry.DuplicateRegistrationException;
import space.arim.omnibus.registry.Registration;
import space.arim.omnibus.registry.Registry;
import space.arim.omnibus.util.ArraysUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The default implementation of {@link Registry}
 * 
 * @author A248
 *
 */
public class DefaultRegistry implements Registry {

	/**
	 * The registry itself
	 * 
	 */
	private final ConcurrentHashMap<Class<?>, Registration<?>[]> registry = new ConcurrentHashMap<>();
	
	/**
	 * The corresponding {@link EventBus} instance
	 * 
	 */
	private final EventBus eventBus;
	
	private final FifoEventQueue eventQueue = new FifoEventQueue();
	
	/**
	 * Creates from an event bus
	 * 
	 * @param eventBus the event bus
	 */
	public DefaultRegistry(EventBus eventBus) {
		this.eventBus = eventBus;
	}
	
	private void fireRegistryEvents() {
		eventQueue.fireEvents(eventBus);
	}
	
	@SuppressWarnings("unchecked")
	private <T> Registration<T>[] addRegistration(Class<T> service, Registration<T> registration, T provider) {
		eventBus.fireAsyncEventWithoutFuture(new RegistrationAddEventImpl<>(service, registration));

		Registration<?>[] result = registry.compute(service, (s, registers) -> {
			if (registers == null) {
				// no existing registrations
				eventQueue.offer(new ServiceChangeEventImpl<>(service, null, registration));
				return new Registration<?>[] {registration};
			}
			for (Registration<?> existing : registers) {
				if (existing.getProvider() == provider) {
					throw new DuplicateRegistrationException(
							"Provider " + provider + " already registered for service " + service);
				}
			}
			int insertionIndex = - (Arrays.binarySearch(registers, registration) + 1);
			if (registers.length == insertionIndex) {
				Registration<T> previous = (Registration<T>) registers[registers.length - 1];
				eventQueue.offer(new ServiceChangeEventImpl<>(service, previous, registration));
			}
			return ArraysUtil.expandAndInsert(registers, registration, insertionIndex);
		});
		fireRegistryEvents();
		return (Registration<T>[]) result;
	}
	
	@Override
	public <T> Registration<T> register(Class<T> service, byte priority, T provider, String name) {
		service.cast(provider);
		Registration<T> registration = new Registration<>(priority, provider, name); // constructor checks for null params

		addRegistration(service, registration, provider);
		return registration;
	}
	
	@Override
	public <T> Registration<T> registerAndGet(Class<T> service, byte priority, T provider, String name) {
		service.cast(provider);
		Registration<T> registration = new Registration<>(priority, provider, name); // constructor checks for null params

		Registration<T>[] updated = addRegistration(service, registration, provider);
		return updated[updated.length - 1];
	}
	
	@SuppressWarnings("unchecked")
	private <T> Registration<T>[] getRegistered(Class<T> service) {
		return (Registration<T>[]) registry.get(service);
	}
	
	@Override
	public <T> Optional<T> getProvider(Class<T> service) {
		Registration<T>[] registrations = getRegistered(service);
		if (registrations == null) {
			return Optional.empty();
		}
		return Optional.of(registrations[registrations.length - 1].getProvider());
	}
	
	@Override
	public <T> Optional<Registration<T>> getRegistration(Class<T> service) {
		Registration<T>[] registrations = getRegistered(service);
		if (registrations == null) {
			return Optional.empty();
		}
		return Optional.of(registrations[registrations.length - 1]);
	}
	
	@Override
	public <T> List<Registration<T>> getAllRegistrations(Class<T> service) {
		Registration<T>[] registrations = getRegistered(service);
		return (registrations == null) ? List.of() : List.of(registrations);
	}
	
	@Override
	public <T> boolean isProvidedFor(Class<T> service) {
		return registry.containsKey(service);
	}
	
	@Override
	public <T> Optional<Registration<T>> unregister(Class<T> service, Registration<T> registration) {
		@SuppressWarnings("unchecked")
		Registration<T>[] result = (Registration<T>[]) registry.computeIfPresent(service, (s, registers) -> {
			int locationIndex = Arrays.binarySearch(registers, registration);
			if (locationIndex < 0) {
				// silently ignore
				return registers;
			}
			eventQueue.offer(new RegistrationRemoveEventImpl<>(service, registration));
			if (registers.length == 1) {
				eventQueue.offer(new ServiceChangeEventImpl<>(service, registration, null));
				return null;
			}
			@SuppressWarnings("unchecked")
			Registration<T>[] updated = (Registration<T>[]) ArraysUtil.contractAndRemove(registers, locationIndex);
			if (locationIndex == registers.length - 1) {
				eventQueue.offer(new ServiceChangeEventImpl<>(service, registration, updated[updated.length - 1]));
			}
			return updated;
		});
		fireRegistryEvents();
		if (result == null) {
			return Optional.empty();
		}
		return Optional.of(result[result.length - 1]);
	}
	
}