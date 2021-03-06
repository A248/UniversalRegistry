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
package space.arim.omnibus.util.concurrent;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * A CompletableFuture with a specified default executor, which is used as the default
 * executor in all future async operations.
 * 
 * @author A248
 *
 * @param <T> the result type of the future
 */
public class CompetitiveFuture<T> extends CompletableFuture<T> {

	private final Executor defaultExecutor;
	
	/**
	 * Creates an incomplete future from the default executor to use
	 * 
	 * @param defaultExecutor the default executor for asynchronous operations
	 * @throws NullPointerException if {@code defaultExecutor} is null
	 */
	public CompetitiveFuture(Executor defaultExecutor) {
		this.defaultExecutor = Objects.requireNonNull(defaultExecutor, "defaultExecutor");
	}

	@Override
	public <U> CompletableFuture<U> newIncompleteFuture() {
		return new CompetitiveFuture<>(defaultExecutor);
	}

	@Override
	public Executor defaultExecutor() {
		return defaultExecutor;
	}
	
	/**
	 * Runs an action asynchronously. See {@link CompletableFuture#runAsync(Runnable)}. <br>
	 * If the specified default executor is null, the common ForkJoinPool is used.
	 * 
	 * @param command what to do
	 * @param defaultExecutor the default executor used for async methods not specifying an executor
	 * @return a competitive future running the specified command
	 */
	public static CompetitiveFuture<Void> runAsync(Runnable command, Executor defaultExecutor) {
		Objects.requireNonNull(command, "Runnable must not be null");
		return supplyAsync(() -> {
			command.run();
			return null;
		}, defaultExecutor);
	}
	
	/**
	 * Supplies a value asynchronously. See {@link CompletableFuture#supplyAsync(Supplier)}. <br>
	 * If the specified default executor is null, the common ForkJoinPool is used.
	 * 
	 * @param <T> the type of the future
	 * @param supplier the supplier from which to get the value
	 * @param defaultExecutor the default executor used for async methods not specifying an executor
	 * @return a competitive future supplying a value via the specified supplier
	 */
	public static <T> CompetitiveFuture<T> supplyAsync(Supplier<T> supplier, Executor defaultExecutor) {
		return (CompetitiveFuture<T>) new CompetitiveFuture<T>(defaultExecutor).completeAsync(supplier);
	}
	
	/**
	 * Creates a precompleted future with the specified value.
	 * 
	 * @param <T> the type of the future
	 * @param value the value with which to complete it
	 * @param defaultExecutor the default executor used for async methods not specifying an executor
	 * @return a competitive future completed with the specified value
	 */
	public static <T> CompetitiveFuture<T> completedFuture(T value, Executor defaultExecutor) {
		CompetitiveFuture<T> result = new CompetitiveFuture<>(defaultExecutor);
		result.complete(value);
		return result;
	}
	
	/**
	 * Creates a future completed exceptionally with the specified exception.
	 * 
	 * @param <T> the type of the future
	 * @param ex the exception with which to complete it
	 * @param defaultExecutor the default executor, used for async methods not specifying an executor
	 * @return a competitive future completed exceptionally with the specified exception
	 */
	public static <T> CompetitiveFuture<T> failedFuture(Throwable ex, Executor defaultExecutor) {
		CompetitiveFuture<T> result = new CompetitiveFuture<>(defaultExecutor);
		result.completeExceptionally(ex);
		return result;
	}
	
}
