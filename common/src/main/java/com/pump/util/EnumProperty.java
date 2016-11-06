/*
 * @(#)EnumProperty.java
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


public class EnumProperty<T> extends Property<T> {
	T[] values;
	public EnumProperty(String name,T[] values,T defaultValue) {
		super(name);
		this.values = values;
		for(int a = 0; a<values.length; a++) {
			if(values[a]==null)
				throw new NullPointerException("none of the enum values may be null");
		}
		if(values.length==0)
			throw new IllegalArgumentException("there were no values");
		setValue(defaultValue);
	}
	
	public T[] getValues() {
		return values;
	}
	
	@Override
	protected void validateValue(Object obj) {
		if(obj==null)
			throw new NullPointerException("the value must not be null");
		for(int a = 0; a<values.length; a++) {
			if(values[a].equals(obj))
				return;
		}
		throw new IllegalArgumentException("the value (\""+obj+"\") did not match any of the allowed values ("+listValues()+")");
	}
	
	protected String listValues() {
		if(values.length==1)
			return values[0].toString();
		
		StringBuffer sb = new StringBuffer();
		for(int a = 0; a<values.length; a++) {
			if(a>0) {
				if(a==values.length-1) {
					sb.append(" and ");
				} else {
					sb.append(", ");
				}
			}
			sb.append(values[a]);
		}
		return sb.toString();
	}
}
