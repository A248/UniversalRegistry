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
package space.arim.omnibus.util.concurrent.impl;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Awaiter {

	private final CompletableFuture<?> future = new CompletableFuture<>();
	
	void complete() {
		future.complete(null);
	}
	
	void await(Duration duration) {
		future.orTimeout(duration.toNanos(), TimeUnit.NANOSECONDS).join();
	}
	
}
