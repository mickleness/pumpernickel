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
package com.pump.math;



/**
 * This <code>Number</code> can change value, so for operations that need to
 * be computed millions of times, recycling 1 object will save a lot of memory
 * allocation.
 */
public class MutableLong extends Number implements Comparable<Number> {
	private static final long serialVersionUID = 1L;

	public long value;

	public MutableLong() {
	}

	public MutableLong(long v) {
		this.value = v;
	}

	@Override
	public String toString() {
		return Long.toString(value);
	}

	@Override
	public Object clone() {
		return new MutableLong(value);
	}

	@Override
	public boolean equals(Object t) {
		if (t instanceof Number) {
			return ((Number) t).longValue() == value;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return intValue();
	}

	@Override
	public double doubleValue() {
		return value;
	}

	@Override
	public float floatValue() {
		return value;
	}

	@Override
	public int intValue() {
		return (int) value;
	}

	@Override
	public long longValue() {
		return value;
	}

	public int compareTo(Number n) {
		long l = n.longValue();

		if(value==l) return 0;
		if(value<l) return -1;
		return 1;
	}
}