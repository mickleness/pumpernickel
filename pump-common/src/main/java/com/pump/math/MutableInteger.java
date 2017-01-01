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
public class MutableInteger extends Number implements Comparable<Number> {
	private static final long serialVersionUID = 1L;

	public int value;

	public MutableInteger() {
	}

	public MutableInteger(int v) {
		this.value = v;
	}

	public int compareTo(Number n) {
		int i = n.intValue();
		if(value==i) return 0;
		if(value<i) return -1;
		return 1;
	}

	@Override
	public String toString() {
		return Integer.toString(value);
	}

	@Override
	public Object clone() {
		return new MutableInteger(value);
	}

	@Override
	public boolean equals(Object t) {
		if (t instanceof Number) {
			return ((Number) t).intValue() == value;
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
		return value;
	}

	@Override
	public long longValue() {
		return value;
	}

}