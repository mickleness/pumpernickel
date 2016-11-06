/*
 * @(#)MutableLong.java
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
