/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
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
		while (true) {
			if (!iter.hasNext()) {
				nextElement = null;
				return;
			}
			T e = iter.next();
			if (accepts(e)) {
				nextElement = e;
				return;
			}
		}
	}

	public abstract boolean accepts(T element);

	@Override
	public boolean hasNext() {
		return nextElement != null;
	}

	@Override
	public T next() {
		T returnValue;
		if (nextElement == NULL_PLACEHOLDER) {
			returnValue = null;
		} else {
			returnValue = (T) nextElement;
		}
		queueNext();
		return returnValue;
	}

}