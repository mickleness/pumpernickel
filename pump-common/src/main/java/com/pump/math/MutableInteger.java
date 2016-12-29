/*
 * @(#)MutableInteger.java
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
