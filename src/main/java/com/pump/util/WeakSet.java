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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A Set that only maintains weak references to all its members.
 */
public class WeakSet<T> implements Set<T> {

	static class WeakSetIterator<T> implements Iterator<T> {

		Iterator<WeakReference<T>> iter;

		T next;
		boolean complete = false;

		WeakSetIterator(Iterator<WeakReference<T>> iter) {
			this.iter = iter;
		}

		private void queueNext() {
			while (!complete) {
				if (!iter.hasNext()) {
					complete = true;
					return;
				}
				WeakReference<T> ref = iter.next();
				T v = ref.get();
				if (v != null) {
					next = v;
					return;
				}
				iter.remove();
			}
			next = null;
		}

		@Override
		public boolean hasNext() {
			if (next == null)
				queueNext();
			if (complete)
				return false;
			return next != null;
		}

		@Override
		public T next() {
			if (next == null)
				queueNext();
			if (complete)
				throw new NoSuchElementException();

			T returnValue = next;
			try {
				return returnValue;
			} finally {
				next = null;
			}
		}

		@Override
		public void remove() {
			iter.remove();
		}

	}

	Set<WeakReference<T>> internalSet = new HashSet<WeakReference<T>>();

	@Override
	public boolean add(T e) {
		if (contains(e))
			return false;
		internalSet.add(new WeakReference<T>(e));
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean returnValue = false;
		synchronized (c) {
			Iterator<? extends T> iter = c.iterator();
			while (iter.hasNext()) {
				T e = iter.next();
				if (add(e))
					returnValue = true;
			}
		}
		return returnValue;
	}

	@Override
	public void clear() {
		internalSet.clear();
	}

	@Override
	public boolean contains(Object o) {
		Iterator<T> iter = iterator();
		while (iter.hasNext()) {
			if (iter.next() == o)
				return true;
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		Iterator<?> incomingIter = c.iterator();
		while (incomingIter.hasNext()) {
			if (contains(incomingIter.next()) == false) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isEmpty() {
		Iterator<T> iter = iterator();
		return !iter.hasNext();
	}

	@Override
	public Iterator<T> iterator() {
		return new WeakSetIterator<T>(internalSet.iterator());
	}

	@Override
	public boolean remove(Object o) {
		Iterator<T> iter = iterator();
		while (iter.hasNext()) {
			if (iter.next() == o) {
				iter.remove();
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		Iterator<?> iter = c.iterator();
		boolean returnValue = false;
		while (iter.hasNext()) {
			boolean changed = remove(iter.next());
			if (changed)
				returnValue = true;
		}
		return returnValue;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		Iterator<T> iter = iterator();
		boolean returnValue = false;
		while (iter.hasNext()) {
			if (c.contains(iter.next()) == false) {
				iter.remove();
				returnValue = true;
			}
		}
		return returnValue;
	}

	/**
	 * This value may be larger than the number of elements that are available
	 * the next time this set is consulted, because at any point weak references
	 * may be garbage collected and will be skipped in future Iterators.
	 */
	@Override
	public int size() {
		int sum = 0;
		Iterator<T> iter = iterator();
		while (iter.hasNext()) {
			iter.next();
			sum++;
		}
		return sum;
	}

	private List<T> convertToList() {
		List<T> list = new ArrayList<T>(internalSet.size());
		Iterator<T> iter = iterator();
		while (iter.hasNext()) {
			list.add(iter.next());
		}
		return list;
	}

	@Override
	public Object[] toArray() {
		return convertToList().toArray();
	}

	@Override
	public <K> K[] toArray(K[] a) {
		List<T> list = convertToList();
		return list.toArray(a);
	}
}