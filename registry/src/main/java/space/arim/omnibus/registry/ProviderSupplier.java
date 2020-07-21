/* 
 * Omnibus-registry
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Omnibus-registry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Omnibus-registry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Omnibus-registry. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.omnibus.registry;

import java.util.function.Supplier;

/**
 * A {@link Supplier} which specifically returns the provider of a service in a {@link Registry}.
 * 
 * @author A248
 *
 * @param <T> the service type
 */
public interface ProviderSupplier<T> extends Supplier<T> {

	/**
	 * Gets the current provider for the service
	 * 
	 */
	@Override
	T get();
	
}