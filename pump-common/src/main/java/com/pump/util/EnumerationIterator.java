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