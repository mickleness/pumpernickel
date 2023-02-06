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

public class FloatProperty extends Property<Float> {
	final float min, max;

	public FloatProperty(String name, float minValue, float maxValue,
			float defaultValue) {
		super(name, defaultValue);
		min = minValue;
		max = maxValue;
		if (max < min)
			throw new IllegalArgumentException(
					"the max (" + max + ") is less than the min (" + min + ")");
	}

	public float getMin() {
		return min;
	}

	public float getMax() {
		return max;
	}

	public void setValue(float f) {
		setValue(new Float(f));
	}

	@Override
	protected void validateValue(Float value) {

		if (min == 0 && max == 0) {
			// we're high up in construction; this doesn't count
			return;
		}

		if (value < min)
			throw new IllegalArgumentException("the value (" + value
					+ ") is less than the min (" + min + ")");
		if (value > max)
			throw new IllegalArgumentException("the value (" + value
					+ ") is greater than the max (" + max + ")");
	}
}