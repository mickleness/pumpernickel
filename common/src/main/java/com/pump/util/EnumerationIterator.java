/*
 * @(#)EnumerationIterator.java
 *
 * $Date$
 *
 * Copyright (c) 2016 by Jeremy Wood.
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

import java.util.Enumeration;
import java.util.Iterator;

/** This helper class converts an Iterator into an Enumeration. */
public class EnumerationIterator<T> implements Enumeration<T> {
	protected final Iterator<T> iter;
	
	public EnumerationIterator(Iterator<T> i) {
		iter = i;
	}
	
	@Override
	public boolean hasMoreElements() {
		return iter.hasNext();
	}
	
	@Override
	public T nextElement() {
		return iter.next();
	}
}
