/* 
 * UniversalUtil
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
package space.arim.universal.util.concurrent;

/**
 * Provides default implementations for {@link EnhancedExecutor}'s scheduling methods' delay functions. <br>
 * <br>
 * Recall that "Advanced" operators take into account execution time, while "Simple" operators do not. <br>
 * Also, note that delay functions are applied after each execution. The first delay is determined by the initial
 * delay passed as a parameter to the scheduling methods themselves. Therefore,
 * <code>executor.schedule(this::runSomeTask, 5, DelayFunctions.exponential(2), TimeUnit.SECONDS)</code>, where <code>executor</code>
 * is an instance of <code>EnhancedExecutor</code>, will schedule some task so that, after five seconds, it is executed,
 * then there is a 10 second delay before the next execution, then a 20 second delay, etc. <br>
 * <br>
 * Fixed delay: {@link #fixedDelay()} <br>
 * Fixed rate: {@link #fixedRate()} <br>
 * Variable rate: {@link #variableRate(SimpleDelayCalculator)} <br>
 * Linear delay: {@link #linear(long)} <br>
 * Exponential delay: {@link #exponential(long)} <br>
 * Logarithmic delay: {@link #logarithmic(long)}
 * 
 * @author A248
 *
 */
public final class DelayFunctions {

	// Prevent instantiation
	private DelayFunctions() {}
	
	/**
	 * Schedules at a constant delay <i>between</i> executions.
	 * 
	 * @return a delay function which yields the same delay
	 */
	public static SimpleDelayCalculator fixedDelay() {
		return (d) -> (d);
	}
	
	/**
	 * Schedules at a fixed rate of delay, meaning execution initiates at constant intervals. <br>
	 * <br>
	 * Attempts to compensate for execution time by subtracting it from the next interval,
	 * in order to achieve a fixed rate of scheduling provided execution time is usually less than the rate. <br>
	 * <br>
	 * For example, if the fixed rate is 4 seconds, an execution timeline might be as follows: <br>
	 * {@literal |--- d ----|} |<i>E</i>| {@literal |- d -|} |{@literal --}<i>E</i>{@literal --}| {@literal |- d -|} <br>
	 * {@literal |- 4 secs -|} {@literal | 4 secs |} {@literal |-- 4 secs --|} <br>
	 * where <i>E</i> represents an execution and <i>d</i> is a period of delay. <br>
	 * <br>
	 * Should execution time take longer than the fixed rate of delay, the function will: <br>
	 * 1. Return a zero (nonexistent) delay to immediately commence the next execution.
	 * 2. Temporarily accelerate future executions in order to "correct" any waverings from the fixed rate schedule.
	 * Thus, if a single execution takes a long time, it will not permanently ruin the rate
	 * 
	 * @return a delay function which yields the same rate of delay
	 */
	public static AdvancedDelayCalculator fixedRate() {
		return variableRate(fixedDelay());
	}
	
	/**
	 * A more complicated version of {@link #fixedRate()}. Execution initiates at intervals
	 * determined by the rate function. <br>
	 * <br>
	 * Attempts to compensate for execution time in calculating the next delay.
	 * The goal is to achieve the rate of scheduling determined by the latest application of the rate function. <br>
	 * <br>
	 * Should execution time take longer than rate calculated, the function will: <br>
	 * 1. Return a zero (nonexistent) delay to immediately commence the next execution.
	 * 2. Temporarily accelerate future executions in order to "correct" any waverings from the rates desired.
	 * Thus, if a single execution takes a long time, it will not permanently ruin the rate. <br>
	 * <br>
	 * The rate function takes the last rate and calculates the next rate. Thus, programmers
	 * may combine other methods in this class to produce whatsoever rate they choose. <br>
	 * For example, <code>DelayFunctions.variableRate(DelayFunctions.exponential(2))</code>
	 * would produce a rate which doubles after each execution.
	 * 
	 * @param rateFunction the rate determining function
	 * @return a delay function which yields a rate of delay as specified by the rate function
	 */
	public static AdvancedDelayCalculator variableRate(SimpleDelayCalculator rateFunction) {
		return new VariableRateFunction(rateFunction);
	}
	
	/**
	 * A linearly increasing delay. <br>
	 * The delay is incremented each invocation by the slope. <br>
	 * The function may be made decreasing by specifying a negative slope; note
	 * that a negative delay indicates that the task halt further scheduling entirely.
	 * 
	 * @param slope the amount to add each invocation
	 * @return a linear delay function
	 */
	public static SimpleDelayCalculator linear(long slope) {
		return (previousDelay) -> previousDelay + slope;
	}
	
	/**
	 * An exponentially increasing delay. <br>
	 * <br>
	 * The delay is multiplied each invocation by the base. <br>
	 * The function may be made decreasing by specifying a base less than 1.
	 * 
	 * @param base the amount by which to multiply each invocation
	 * @return an exponential delay function
	 */
	public static SimpleDelayCalculator exponential(long base) {
		return (previousDelay) -> previousDelay*base;
	}
	
	/**
	 * A logarithmically increasing delay, computed as follows: <br>
	 * Let <i>x</i> be the previous delay and <i>b</i> be the specified base.
	 * The result is <code>ln(b^x+1)/lnb</code>. <br>
	 * 
	 * @param base the logarithmic base
	 * @return a logarithmic delay function
	 */
	public static SimpleDelayCalculator logarithmic(long base) {
		return (previousDelay) -> (long) (Math.log(base^previousDelay + 1)/Math.log(base));
	}
	
}

/**
 * Implementation of a variable rate delay function,
 * which compensates for execution time in determing the next delay
 * and attempts to "catch up" if it falls behind.
 * 
 * @author A248
 *
 */
class VariableRateFunction implements AdvancedDelayCalculator {
	
	/**
	 * The rate determining function
	 * 
	 */
	private final SimpleDelayCalculator rateFunction;
	
	/**
	 * The rate we want to schedule at, starts at {@literal -}1 to indicate unknown.
	 * 
	 */
	private volatile long rate = -1L;
	
	/**
	 * If we're behind, by how much are we late?
	 * Always a positive number
	 * 
	 */
	private volatile long offset = 0;
	
	VariableRateFunction(SimpleDelayCalculator rateFunction) {
		this.rateFunction = rateFunction;
	}
	
	@Override
	public long calculateNext(long previousDelay, long executionTime) {
		/*
		 * If this is the first invocation, we have to determine the initial rate
		 * otherwise, we'll use the rate function to calculate the next rate
		 * 
		 */
		long rate = this.rate;
		if (rate == -1L) {
			rate = previousDelay;
		} else {
			rate = rateFunction.calculateNext(rate);
			if (rate < 0) {
				// rate-determining function decided to cancel
				return -1L;
			}
		}
		this.rate = rate;

		long offset = this.offset;
		long delay = rate - executionTime - offset;
		if (delay >= 0L) {
			offset = 0L;
		} else {
			offset = -delay;
			delay = 0L;
		}
		this.offset = offset;
		return delay;
	}

}