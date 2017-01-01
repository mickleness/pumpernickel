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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/** A Set that only maintains weak references to all its members.
 */
public class WeakSet<T> implements Set<T> {
	
	static class WeakSetIterator<T> implements Iterator<T> {

		Iterator<WeakReference<T>> iter;
		T next;
		
		WeakSetIterator( Iterator<WeakReference<T>> iter ) {
			this.iter = iter;
			queueNext();
		}
		
		private void queueNext() {
			while(iter.hasNext()) {
				WeakReference<T> ref = iter.next();
				T v = ref.get();
				if(v!=null) {
					next = v;
					return;
				}
			}
			next = null;
		}
		
		public boolean hasNext() {
			return next!=null;
		}

		public T next() {
			T returnValue = next;
			try {
				return returnValue;
			} finally {
				queueNext();
			}
		}

		public void remove() {
			iter.next();
		}
		
	}
	
	Set<WeakReference<T>> internalSet = new HashSet<WeakReference<T>>();

	public boolean add(T e) {
		if(contains(e))
			return false;
		internalSet.add(new WeakReference<T>(e));
		return true;
	}

	public boolean addAll(Collection<? extends T> c) {
		boolean returnValue = false;
		synchronized(c) {
			Iterator<? extends T> iter = c.iterator();
			while(iter.hasNext()) {
				T e = iter.next();
				if(add(e))
					returnValue = true;
			}
		}
		return returnValue;
	}

	public void clear() {
		internalSet.clear();
	}

	public boolean contains(Object o) {
		Iterator<T> iter = iterator();
		while(iter.hasNext()) {
			if(iter.next()==o) return true;
		}
		return false;
	}

	public boolean containsAll(Collection<?> c) {
		Iterator<?> incomingIter = c.iterator();
		while(incomingIter.hasNext()) {
			if( contains(incomingIter.next())==false ) {
				return false;
			}
		}
		return true;
	}

	public boolean isEmpty() {
		Iterator<T> iter = iterator();
		return !iter.hasNext();
	}

	public Iterator<T> iterator() {
		return new WeakSetIterator<T>( internalSet.iterator() );
	}

	public boolean remove(Object o) {
		Iterator<T> iter = iterator();
		while(iter.hasNext()) {
			if(iter.next()==o) {
				iter.remove();
				return true;
			}
		}
		return false;
	}

	public boolean removeAll(Collection<?> c) {
		Iterator<?> iter = c.iterator();
		boolean returnValue = false;
		while(iter.hasNext()) {
			boolean changed = remove(iter.next());
			if(changed)
				returnValue = true;
		}
		return returnValue;
	}

	public boolean retainAll(Collection<?> c) {
		Iterator<T> iter = iterator();
		boolean returnValue = false;
		while(iter.hasNext()) {
			if( c.contains( iter.next() )==false ) {
				iter.remove();
				returnValue  = true;
			}
		}
		return returnValue;
	}

	public int size() {
		int sum = 0;
		Iterator<T> iter = iterator();
		while(iter.hasNext()) {
			sum++;
		}
		return sum;
	}
	
	private List<T> convertToList() {
		List<T> list = new ArrayList<T>(internalSet.size());
		Iterator<T> iter = iterator();
		while(iter.hasNext()) {
			list.add( iter.next() );
		}
		return list;
	}

	public Object[] toArray() {
		return convertToList().toArray();
	}

	public <K> K[] toArray(K[] a) {
		List<T> list = convertToList();
		return list.toArray(a);
	}
}