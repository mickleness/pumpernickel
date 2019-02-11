/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://raw.githubusercontent.com/mickleness/pumpernickel/master/License.txt
 * 
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
	public double evaluate(double x);

	/**
	 * Returns all the x-values for the equation f(x) = y.
	 * 
	 * @param y
	 *            a possible output of this function.
	 * @return all the possible inputs that would map to the argument.
	 * 
	 */
	public double[] evaluateInverse(double y);
}