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
package space.arim.omnibus.registry;

import java.util.List;
import java.util.Optional;

/**
 * A framework for registering and loading services. <br>
 * <br>
 * Used to register providers of services which will be utilised
 * by dependents according to the priority of registration. <br>
 * <br>
 * A <i>service</i> is a type, usually an interface or abstract class, which
 * multiple programs may decide to implement. An implementation of a service
 * is considered a <i>provider</i>. <br>
 * <br>
 * Providers may be registered with a priority and an optional user friendly name.
 * These details comprise a <i>registration</i> (which is really just a wrapper
 * class for the provider, priority, and name). See {@link Registration}. <br>
 * <br>
 * The service class is the class corresponding to the service type.
 * 
 * @author A248
 *
 */
public interface Registry {
	
	/**
	 * Registers a resource as a specific service and generates a {@link Registration}
	 * to represent the registration which was just added. <br>
	 * <br>
	 * The provider and name must be nonnull. <br>
	 * Higher priority registrations will be preferred for {@link #getProvider(Class)}. <br>
	 * <br>
	 * Services may be registered under multiple intended service types.
	 * 
	 * @param <T> the service type
	 * @param service the service class
	 * @param priority the registration priority
	 * @param provider the resource to register, must not be null
	 * @param name a user friendly name for the implementation, must not be null
	 * @return the registration which was added to the registry, formed from the parameters
	 * @throws DuplicateRegistrationException if the provider is already registered for the service type
	 */
	<T> Registration<T> register(Class<T> service, byte priority, T provider, String name);
	
	/**
	 * Registers a resource as a specific service and returns the highest priority
	 * registration for the service. <br>
	 * This is similar to {@link #register(Class, byte, Object, String)}, but instead of returning
	 * a {@link Registration} based on the resource registered, this returns the highest priority
	 * registration <i>after</i> computations. <br>
	 * <br>
	 * The provider and name must be nonnull.
	 * 
	 * @param <T> the service type
	 * @param service the service class
	 * @param priority the registration priority
	 * @param provider the resource to register, must not be null
	 * @param name a user friendly name for the implementation, must not be null
	 * @return the highest priority registration after the resource is registered
	 * @throws DuplicateRegistrationException if the provider is already registered for the service type
	 */
	<T> Registration<T> registerAndGet(Class<T> service, byte priority, T provider, String name);
	
	/**
	 * Finds the highest priority registration for a service and returns its provider. <br>
	 * If no registration for the service is found, an empty optional is returned. <br>
	 * <br>
	 * The proper way to retrieve registrations is to call this method once, and check
	 * if the value is present. If the optional is empty, there is no registration
	 * for the service.
	 * 
	 * @param <T> the service type
	 * @param service the service class
	 * @return the highest priority provider or an empty optional if not found
	 */
	<T> Optional<T> getProvider(Class<T> service);
	
	/**
	 * Retrieves the highest priority registration for a service. <br>
	 * <br>
	 * If no registration for the service is found, an empty optional is returned.
	 * 
	 * @param <T> the service type
	 * @param service the service class
	 * @return the highest priority registration or an empty optional if not found
	 */
	<T> Optional<Registration<T>> getRegistration(Class<T> service);
	
	/**
	 * Gets all registrations for a specific service, as an immutable copy. <br>
	 * The list is sorted in ascending priority. The last element has the highest priority. <br>
	 * <br>
	 * If no registrations for the service are found, an empty list is returned.
	 * 
	 * @param <T> the service type
	 * @param service the service class
	 * @return an unmodifiable copy of all registrations for the service, never null
	 */
	<T> List<Registration<T>> getAllRegistrations(Class<T> service);
	
	/**
	 * Checks whether a service has any accompanying provider <br>
	 * <br>
	 * This method should only be used in the rare case where one needs to check
	 * whether a service is registered but does not need to retrieve the registration itself.
	 * 
	 * @param <T> the service type
	 * @param service the service class
	 * @return true if the service is provided for, false if not
	 */
	<T> boolean isProvidedFor(Class<T> service);
	
	/**
	 * Unregisters the specified {@link Registration}, and returns the updated highest priority
	 * registration after the specified {@code Registration} is unregistered. <br>
	 * <br>
	 * If the specified {@code Registration} was not registered, this is a no-op.
	 * 
	 * @param <T> the service type
	 * @param service the service class
	 * @param registration the registration to unregister
	 * @return the updated highest priority registration for the service, or an empty optional if there is none
	 */
	<T> Optional<Registration<T>> unregister(Class<T> service, Registration<T> registration);
	
}
