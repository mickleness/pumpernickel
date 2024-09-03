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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class BasicReceiver<T> implements Receiver<T>, ListModel<T>, Iterable<T> {

	List<T> list = new ArrayList<T>();
	List<ListDataListener> listeners = new ArrayList<ListDataListener>();
	List<Receiver<T>> receivers = new ArrayList<Receiver<T>>();

	/**
	 * Bind another <code>Receiver</code> to this object, so when elements are
	 * added to this receiver, the argument receives those elements too.
	 * 
	 * @param receiver
	 *            the new receiver.
	 * @param addExistingElements
	 *            whether the new receiver should receive a copy of all existing
	 *            elements.
	 */
	public void add(Receiver<T> receiver, boolean addExistingElements) {
		synchronized (this) {
			if (addExistingElements) {
				// because we can't create an array of type T: we'll
				// add these one at a time
				for (int a = 0; a < list.size(); a++) {

					// ugh. Stupid varargs and ClassCastExceptions...
					T element = list.get(a);
					T[] array = (T[]) Array.newInstance(element.getClass(), 1);
					array[0] = element;
					receiver.add(array);
				}
			}
			receivers.add(receiver);
		}
	}

	public T[] toArray(T[] array) {
		for (int a = 0; a < list.size(); a++) {
			array[a] = list.get(a);
		}
		return array;
	}

	@Override
	public void add(T... elements) {
		if (elements.length == 0)
			return;

		int index1, index2;
		synchronized (this) {
			// to describe the event:
			index1 = list.size();
			index2 = index1 + elements.length - 1;

			for (T t : elements) {
				list.add(t);
			}
			for (Receiver<T> receiver : receivers) {
				receiver.add(elements);
			}
		}
		for (ListDataListener listener : listeners) {
			listener.intervalAdded(new ListDataEvent(this,
					ListDataEvent.INTERVAL_ADDED, index1, index2));
		}
	}

	@Override
	public Iterator<T> iterator() {
		return new UnmodifiableIterator<T>(list.iterator());
	}

	@Override
	public int getSize() {
		return list.size();
	}

	@Override
	public T getElementAt(int index) {
		return list.get(index);
	}

	/**
	 * Add a ListDataListener. This will only be issued INTERVAL_ADDED events,
	 * because a Receiver object only supports add operations.
	 */
	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
	}

	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}

}