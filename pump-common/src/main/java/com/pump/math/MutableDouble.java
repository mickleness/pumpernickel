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
public class MutableDouble extends Number implements Comparable<Number> {
	private static final long serialVersionUID = 1L;
	
	public static class ComparisonException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public ComparisonException(String s) {
			super(s);
		}
	}

	public double value;

	public MutableDouble() {
	}

	public MutableDouble(double v) {
		this.value = v;
	}

	@Override
	public int hashCode() {
		return (int) (100 * value);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Number) {
			Number n = (Number)obj;
			try {
				return compareTo(n)==0;
			} catch(ComparisonException e) {
				return false;
			}
		}
		return false;
	}

	@Override
	public double doubleValue() {
		return value;
	}

	@Override
	public float floatValue() {
		return (float) value;
	}

	@Override
	public int intValue() {
		return (int) value;
	}

	@Override
	public long longValue() {
		return (long) value;
	}

	@Override
	public String toString() {
		return Double.toString(value);
	}

	public int compareTo(Number n) {
		double d = n.doubleValue();

		if(Double.isNaN(d) && Double.isNaN(value))
			return 0;
		if(Double.isInfinite(d) && Double.isInfinite(value))
			return 0;
		if(Double.isNaN(d) || Double.isNaN(value))
			throw new ComparisonException("internal value = "+value+" incoming value = "+d);
		if(Double.isInfinite(d) || Double.isInfinite(value))
			throw new ComparisonException("internal value = "+value+" incoming value = "+d);
		
		if(value==d) return 0;
		if(value<d) return -1;
		return 1;
	}
}