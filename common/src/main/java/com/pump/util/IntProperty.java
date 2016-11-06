/*
 * @(#)IntProperty.java
 *
 * $Date: 2014-04-28 00:08:51 -0400 (Mon, 28 Apr 2014) $
 *
 * Copyright (c) 2011 by Jeremy Wood.
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
package com.pump.util;


public class IntProperty extends Property<Integer> {
	final int min, max;
	
	public IntProperty(String name,int minValue,int maxValue,int defaultValue) {
		super(name);
		min = minValue;
		max = maxValue;
		if(max<min)
			throw new IllegalArgumentException("the max ("+max+") is less than the min ("+min+")");
	
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
		if(value<min)
			throw new IllegalArgumentException("the value ("+value+") is less than the min ("+min+")");
		if(value>max)
			throw new IllegalArgumentException("the value ("+value+") is greater than the max ("+max+")");
	}
}
