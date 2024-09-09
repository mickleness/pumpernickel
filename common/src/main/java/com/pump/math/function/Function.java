/**
 * This software is released as part of the Pumpernickel project.
 * <p>
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * <p>
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.math.function;

/** This is a simple mathematical function: f(x) */
public interface Function {
	/**
	 * Evaluates f(x).
	 * 
	 * @param x
	 *            the input for this function.
	 * @return the output of this function.
	 * 
	 */
	double evaluate(double x);

	/**
	 * Returns all the x-values for the equation f(x) = y.
	 * 
	 * @param y
	 *            a possible output of this function.
	 * @return all the possible inputs that would map to the argument.
	 * 
	 */
	double[] evaluateInverse(double y);
}