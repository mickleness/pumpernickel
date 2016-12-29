/*
 * @(#)FloatProperty.java
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


public class FloatProperty extends Property<Float> {
	final float min, max;
	
	public FloatProperty(String name,float minValue,float maxValue,float defaultValue) {
		super(name);
		min = minValue;
		max = maxValue;
		if(max<min)
			throw new IllegalArgumentException("the max ("+max+") is less than the min ("+min+")");
	
		setValue(defaultValue);
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
		if(value<min)
			throw new IllegalArgumentException("the value ("+value+") is less than the min ("+min+")");
		if(value>max)
			throw new IllegalArgumentException("the value ("+value+") is greater than the max ("+max+")");
	}
}
