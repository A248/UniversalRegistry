/* 
 * UniversalUtil, simple utilities for Spigot and BungeeCord
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * UniversalUtil is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * UniversalUtil is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with UniversalUtil. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.universal.util.proxy;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * A parent class for iterators which simply redirect, a.k.a <i>proxy</i>, calls to another iterator (the backing iterator). <br>
 * Such iterators do nothing themselves except refer calls to their backing iterators. <br>
 * <br>
 * However, the proxy iterator's additional call layer provides fine tuned control over specific implementations. <br>
 * Programmers may extend this class to utilise this enhanced control. <br>
 * <br>
 * Note that a reference is retained to the backing iterator. Changes to the backing iterator are reflected in proxied iterators.
 * 
 * @author A248
 *
 * @param <E>
 */
public class ProxiedIterator<E> implements Iterator<E> {
	
	private final Iterator<E> original;
	
	/**
	 * Creates a ProxiedIterator based on a backing iterator
	 * 
	 * @param original the original, backing iterator
	 */
	public ProxiedIterator(Iterator<E> original) {
		this.original = original;
	}
	
	protected Iterator<E> getOriginal() {
		return original;
	}
	
	@Override
	public boolean hasNext() {
		return getOriginal().hasNext();
	}
	
	@Override
	public E next() {
		return getOriginal().next();
	}
	
	@Override
	public void remove() {
		getOriginal().remove();
	}
	
	@Override
	public void forEachRemaining(Consumer<? super E> action) {
		getOriginal().forEachRemaining(action);
	}

}
