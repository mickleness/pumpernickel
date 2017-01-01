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
package com.pump.util;

import java.util.Iterator;

/** This is an Iterator that throws an <code>UnsupportedOperationException()</code>
 * when <code>remove()</code> is called. This guarantees the underlying iterator
 * won't be modified.
 */
public class UnmodifiableIterator<T> implements Iterator<T> {

	Iterator<T> iter;
	
	public UnmodifiableIterator(Iterator<T> iter) {
		this.iter = iter;
	}
	
	public boolean hasNext() {
		return iter.hasNext();
	}

	public T next() {
		return iter.next();
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}