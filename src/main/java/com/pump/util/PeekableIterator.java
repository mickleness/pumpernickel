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
		if (queue.size() > 0)
			return true;
		return iter.hasNext();
	}

	@Override
	public synchronized T next() {
		if (queue.size() > 0) {
			Object e = queue.removeFirst();
			if (e == NULL_PLACEHOLDER) {
				return null;
			}
			return (T) e;
		}
		return iter.next();
	}

	public synchronized T peek(int i) {
		while (i >= queue.size()) {
			Object e = iter.next();
			if (e == null) {
				queue.add(NULL_PLACEHOLDER);
			} else {
				queue.add(e);
			}
		}

		Object e = queue.get(i);
		if (e == NULL_PLACEHOLDER) {
			return null;
		}
		return (T) e;
	}

	@Override
	public void remove() {
		iter.remove();
	}
}