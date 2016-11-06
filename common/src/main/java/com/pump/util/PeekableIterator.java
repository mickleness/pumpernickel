/*
 * @(#)PeekableIterator.java
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
import java.util.LinkedList;

/** This iterator lets you peek ahead at upcoming elements. */
public class PeekableIterator<T> implements Iterator<T> {
	
	private static Object NULL_PLACEHOLDER = new Object();
	
	@SuppressWarnings("rawtypes")
	LinkedList queue = new LinkedList();
	
	Iterator<T> iter;
	
	public PeekableIterator(Iterator<T> iter) {
		this.iter = iter;
	}

	@Override
	public synchronized boolean hasNext() {
		if(queue.size()>0)
			return true;
		return iter.hasNext();
	}

	@Override
	public synchronized T next() {
		if(queue.size()>0) {
			Object e = queue.removeFirst();
			if(e==NULL_PLACEHOLDER) {
				return null;
			}
			return (T)e;
		}
		return iter.next();
	}
	
	public synchronized T peek(int i) {
		while(i>=queue.size()) {
			Object e = iter.next();
			if(e==null) {
				queue.add(NULL_PLACEHOLDER);
			} else {
				queue.add(e);
			}
		}

		Object e = queue.get(i);
		if(e==NULL_PLACEHOLDER) {
			return null;
		}
		return (T)e;
	}

	@Override
	public void remove() {
		iter.remove();
	}
}
