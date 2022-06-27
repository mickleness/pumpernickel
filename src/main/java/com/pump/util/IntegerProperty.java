/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.util;

public class IntegerProperty extends Property<Integer> {
	final int min, max;

	/**
	 * Create a new IntegerProperty with no min or max value.
	 */
	public IntegerProperty(String name, int defaultValue) {
		this(name, Integer.MIN_VALUE, Integer.MAX_VALUE, defaultValue);
	}

	public IntegerProperty(String name, int minValue, int maxValue,
			int defaultValue) {
		super(name);
		min = minValue;
		max = maxValue;
		if (max < min)
			throw new IllegalArgumentException("the max (" + max
					+ ") is less than the min (" + min + ")");

		setValue(defaultValue);
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

	@Override
	protected void validateValue(Integer value) {
		if (value < min)
			throw new IllegalArgumentException("the value (" + value
					+ ") is less than the min (" + min + ")");
		if (value > max)
			throw new IllegalArgumentException("the value (" + value
					+ ") is greater than the max (" + max + ")");
	}
}