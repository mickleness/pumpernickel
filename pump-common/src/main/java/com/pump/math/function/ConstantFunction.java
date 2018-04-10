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

public class ConstantFunction implements Function {
	double value;

	public ConstantFunction(double v) {
		this.value = v;
	}

	public double evaluate(double x) {
		return value;
	}

	public double[] evaluateInverse(double y) {
		if (y == value)
			return new double[] { Double.NaN };
		return new double[] {};
	}
}