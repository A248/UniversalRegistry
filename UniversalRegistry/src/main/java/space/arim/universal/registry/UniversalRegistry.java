/* 
 * UniversalRegistry
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * UniversalRegistry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * UniversalRegistry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with UniversalRegistry. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.universal.registry;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

import space.arim.universal.events.Events;
import space.arim.universal.events.UniversalEvents;

/**
 * <b>UniversalRegistry</b>: Main class <br>
 * <br>
 * Used for retrieving {@link Registry} instances: <br>
 * * {@link #get()} <br>
 * * {@link #getByClass(Class)} <br>
 * * {@link #threadLocal()} <br>
 * * {@link #getOrDefault(Class, Supplier)}
 * 
 * @author A248
 *
 */
public final class UniversalRegistry implements Registry {
	
	/**
	 * The id of the instance
	 * 
	 */
	private final String id;
	
	/**
	 * The registry itself
	 * 
	 */
	private final ConcurrentHashMap<Class<?>, List<Registration<?>>> registry = new ConcurrentHashMap<Class<?>, List<Registration<?>>>();
	
	/**
	 * The corresponding {@link Events} instance
	 * 
	 */
	private final Events events;
	
	/**
	 * A list of registration events we'll fire. <br>
	 * We don't want to spend time firing events inside computation lambdas,
	 * particularly if listeners spend a long time.
	 * 
	 */
	private final BlockingQueue<PartialRegistrationEvent<?>> eventQueue = new LinkedBlockingQueue<>();
	
	/**
	 * Instances map to prevent duplicate ids
	 * 
	 */
	private static final ConcurrentHashMap<String, UniversalRegistry> INSTANCES = new ConcurrentHashMap<String, UniversalRegistry>();
	
	/**
	 * The thread local
	 * 
	 */
	private static final ThreadLocal<Registry> THREAD_LOCAL = ThreadLocal.withInitial(() -> byEvents(UniversalEvents.threadLocal().get()));
	
	// Control instantiation
	private UniversalRegistry(String id, Events events) {
		this.id = id;
		this.events = events;
	}
	
	static Registry demandRegistry(String id, Events events) {
		return INSTANCES.computeIfAbsent(id, (instanceId) -> new UniversalRegistry(instanceId, events));
	}
	
	static Registry byEvents(Events events) {
		return demandRegistry(((UniversalEvents) events).getId(), events);
	}
	
	/**
	 * Registry instances are thread safe; however, you may wish for a thread specific instance nonetheless.
	 * 
	 * @return ThreadLocal a {@link ThreadLocal}
	 */
	public static ThreadLocal<Registry> threadLocal() {
		return THREAD_LOCAL;
	}
	
	/**
	 * Retrieves a Registry instance by class.
	 * If no instance for the classname exists, a new one is created.<br>
	 * <br>
	 * This is the preferred approach to using your own UniversalRegistry instances.
	 * 
	 * @param clazz the class
	 * @return the instance. If none exists, a new instance is created.
	 */
	public static Registry getByClass(Class<?> clazz) {
		return byEvents(UniversalEvents.getByClass(clazz));
	}
	
	/**
	 * Gets a Registry instance by class with a default value, issued by the Supplier, if it does not exist. <br>
	 * <br>
	 * This method is useful for checking for a specific instance and falling back to a default value. <br>
	 * 
	 * @param clazz see {@link #getByClass(Class)}
	 * @param defaultSupplier from which to return back default values.
	 * @return the instance if it exists, otherwise the default value
	 */
	public static Registry getOrDefault(Class<?> clazz, Supplier<Registry> defaultSupplier) {
		Registry registry = INSTANCES.get("class-" + clazz.getName());
		return registry != null ? registry : defaultSupplier.get();
	}
	
	/**
	 * Gets the main instance of UniversalRegistry
	 * 
	 * @return the instance
	 */
	public static Registry get() {
		return byEvents(UniversalEvents.get());
	}
	
	/**
	 * Returns the id of this Registry instance. <br>
	 * <b>This method is purposefully not exposed since it is not part of the officially supported API.</b>
	 * (There may be other Registry implementations which do not use an id based system, further,
	 * UniversalRegistry may itself change its internal implementation in the future).
	 * 
	 * @return String the id
	 */
	public String getId() {
		return id;
	}
	
	@Override
	public Events getEvents() {
		return events;
	}
	
	private <T> List<Registration<?>> computeRegistrations(Class<T> service, Registration<T> registration) {
		return registry.compute(service, (s, registrations) -> {

			if (registrations == null) {
				registrations = new CopyOnWriteArrayList<Registration<?>>();
				registrations.add(registration);

			} else {
				// adds the element into the list according to priority
				// this is faster than using List#sort
				int position = Collections.binarySearch(registrations, registration);
				if (position < 0) {
					registrations.add(-(position + 1), registration);
				} else {
					registrations.add(position, registration);
				}
			}

			addEventToFire(service, registration);
			return registrations;
		});
	}
	
	/**
	 * Get the highest priority element in the list in a thread safe,
	 * efficient manner, using the iterator. <br>
	 * CopyOnWriteArrayList's iterator returns a snapshot of the list state.
	 * 
	 * @param <T> the registration type to cast to
	 * @param registrations the list of registrations
	 * @return the registration, casted
	 */
	@SuppressWarnings("unchecked")
	private <T> Registration<T> getHighestPriorityFrom(List<Registration<?>> registrations) {
		/*
		 * Note that our lists are sorted in ascending order because Collections#binarySearch
		 * requires ascending order. Therefore we have to get the last element, because
		 * the last element will have the highest priority.
		 * 
		 */
		Registration<?> registration = null;
		for (Iterator<Registration<?>> it = registrations.iterator(); it.hasNext();) {
			registration = it.next();
		}
		return (Registration<T>) registration;
	}
	
	private <T> void addEventToFire(Class<T> service, Registration<T> registration) {
		eventQueue.add(new PartialRegistrationEvent<>(service, registration));
	}
	
	private void fireRegistrationEvents() {
		PartialRegistrationEvent<?> partial;
		while ((partial = eventQueue.poll()) != null) {
			events.fireEvent(partial.toFullEvent(events.getUtil().isAsynchronous()));
		}
	}
	
	@Override
	public <T> Registration<T> register(Class<T> service, byte priority, T provider, String name) {
		Registration<T> registration = new Registration<T>(priority, provider, name); // constructor checks for null params

		computeRegistrations(service, registration);
		fireRegistrationEvents();

		return registration;
	}
	
	@Override
	public <T> Registration<T> registerAndGet(Class<T> service, byte priority, T provider, String name) {
		Registration<T> registration = new Registration<T>(priority, provider, name); // constructor checks for null params

		Registration<T> result = getHighestPriorityFrom(computeRegistrations(service, registration));
		fireRegistrationEvents();

		return result;
	}
	
	@Override
	public <T> Registration<T> getRegistration(Class<T> service) {
		List<Registration<?>> registrations = registry.get(service);
		return (registrations != null) ? getHighestPriorityFrom(registrations) : null;
	}
	
	@Override
	public <T> boolean isProvidedFor(Class<T> service) {
		/*
		 * A service is provided for if there are registrations for it.
		 * 
		 * Because #unregister removes empty lists atomically, we can simply check if
		 * the key is contained in the registry map.
		 * 
		 */
		return registry.containsKey(service);
	}
	
	@Override
	public <T> Registration<T> registerIfAbsent(Class<T> service, Supplier<Registration<T>> computer) {
		List<Registration<?>> registrations = registry.computeIfAbsent(service, (c) -> {

			Registration<T> registration = computer.get();
			if (registration == null) {
				return null; // don't enter the mapping
			}

			List<Registration<?>> list = new CopyOnWriteArrayList<Registration<?>>();
			list.add(registration);
			addEventToFire(service, registration);
			return list;
		});
		/*
		 * Now, there are 4 possibilities regarding the list we now have:
		 * 
		 * 1. The list has just been created and the result of the supplier has
		 * been added to it, so is nonempty.
		 * 2. The list is null because the result of the supplier was null.
		 * 3. The list contains existing registrations, so is nonempty.
		 * 4. The list is empty because it is in an intermediate state where
		 * #unregister is in progress and has just removed the last element from the list.
		 * 
		 * 
		 * If #2, we just return null per the specifications of Registry.
		 */
		if (registrations == null) {
			return null;
		}
		fireRegistrationEvents();
		/*
		 * Otherwise, we have to return a nonnull value, either an existing registration
		 * or a registration we have just added.
		 * 
		 * If the list is nonempty (majority of cases) #getHighestPriorityFrom
		 * will return the nonnull registration, so we can return that.
		 * 
		 * If the list is empty, we have to double back until the intermediate state
		 * has passed. So we recurse.
		 */
		Registration<T> registration = getHighestPriorityFrom(registrations);
		return (registration != null) ? registration : registerIfAbsent(service, computer);
	}
	
	@Override
	public <T> void unregister(Class<T> service, Registration<T> registration) {
		/*
		 * If the list of registrations for the service does not exist, nothing happens.
		 * Otherwise, if the removal of the registration changed the registration list,
		 * and the resulting list is therefore empty, remove it.
		 * 
		 * This way, the registry will not retain empty lists after unregistration.
		 * 
		 */
		registry.compute(service, (clazz, registrations) -> (registrations != null && registrations.remove(registration)
				&& registrations.isEmpty()) ? null : registrations);
	}
	
}