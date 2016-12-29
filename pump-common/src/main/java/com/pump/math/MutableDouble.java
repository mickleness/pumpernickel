/*
 * @(#)MutableDouble.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2012 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
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
