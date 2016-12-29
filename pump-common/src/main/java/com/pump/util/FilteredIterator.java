/*
 * @(#)FilteredIterator.java
 *
 * $Date: 2015-12-26 01:54:45 -0600 (Sat, 26 Dec 2015) $
 *
 * Copyright (c) 2015 by Jeremy Wood.
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

import java.util.Iterator;

/** This filters another iterator. */
public abstract class FilteredIterator<T> implements Iterator<T> {
	
	private static Object NULL_PLACEHOLDER = new Object();
	
	protected Iterator<T> iter;
	protected Object nextElement;
	
	public FilteredIterator(Iterator<T> iter) {
		this.iter = iter;
		queueNext();
	}

	private void queueNext() {
		while(true) {
			if(!iter.hasNext()) {
				nextElement = null;
				return;
			}
			T e = iter.next();
			if(accepts(e)) {
				nextElement = e;
				return;
			}
		}
	}
	
	public abstract boolean accepts(T element);

	@Override
	public boolean hasNext() {
		return nextElement!=null;
	}

	@Override
	public T next() {
		T returnValue;
		if(nextElement==NULL_PLACEHOLDER) {
			returnValue = null;
		} else {
			returnValue = (T)nextElement;
		}
		queueNext();
		return returnValue;
	}

}
