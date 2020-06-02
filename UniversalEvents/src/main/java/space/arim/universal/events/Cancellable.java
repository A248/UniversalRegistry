/* 
 * UniversalEvents, a common server event-handling api
 * Copyright © 2019 Anand Beh <https://www.arim.space>
 * 
 * UniversalEvents is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * UniversalEvents is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with UniversalEvents. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.universal.events;

/**
 * An event which may be cancelled via {@link #setCancelled(boolean)}. <br>
 * <br>
 * This is an interface, and not an abstract class, so that users may
 * define their own object hierarchies. Nevertheless, to ensure the visibility
 * of cancellation in {@link #isCancelled()}, users should use {@link AbstractCancellable}
 * or understand and create a memory-concise cancellation flag.
 * 
 * @author A248
 *
 */
public interface Cancellable extends Event {
	
	/**
	 * Marks an event as cancelled. The event may never be "uncancelled". <br>
	 * <br>
	 * The effect of cancellation is determined by the implementer of the event.
	 * Event listeners are still invoked regardless of cancellation.
	 * 
	 */
	void cancel();
	
	/**
	 * Whether the event has been cancelled, possibly by another listener. <br>
	 * <br>
	 * To preserve the integrity of this method call in concurrent environments,
	 * implementers must guarantee memory-consistency effects. This is typically
	 * done with a volatile variable.
	 * 
	 * @return true if the event is cancelled, false otherwise
	 */
	boolean isCancelled();
	
}
