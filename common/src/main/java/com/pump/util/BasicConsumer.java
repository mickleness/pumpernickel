/**
 * This software is released as part of the Pumpernickel project.
 * <p>
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * <p>
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * This is a simple Consumer implementation that stores all incoming elements in a list.
 */
public class BasicConsumer<T> implements Consumer<T>, ListModel<T>, Iterable<T> {
	/**
	 * This is an Iterator that throws an
	 * <code>UnsupportedOperationException()</code> when <code>remove()</code> is
	 * called. This guarantees the underlying iterator won't be modified.
	 */
	private static class UnmodifiableIterator<T> implements Iterator<T> {

		private final Iterator<T> iter;

		public UnmodifiableIterator(Iterator<T> iter) {
			this.iter = iter;
		}

		@Override
		public boolean hasNext() {
			return iter.hasNext();
		}

		@Override
		public T next() {
			return iter.next();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	private final List<T> list = new ArrayList<>();
	private final List<ListDataListener> listeners = new CopyOnWriteArrayList<>();
	private final List<Consumer<T>> consumers = new CopyOnWriteArrayList<>();

	/**
	 * Bind another <code>Consumer</code> to this object, so when elements are
	 * added to this consumer, the argument receives those elements too.
	 * 
	 * @param consumer
	 *            the new consumer.
	 * @param addExistingElements
	 *            whether the new consumer should receive a copy of all existing
	 *            elements.
	 */
	public synchronized void add(Consumer<T> consumer, boolean addExistingElements) {
		synchronized (this) {
			if (addExistingElements) {
				for (T element : list) {
					consumer.accept(element);
				}
			}
			consumers.add(consumer);
		}
	}

	public synchronized T[] toArray(T[] array) {
		return list.toArray(array);
	}

	@Override
	public void accept(T newElement) {
		int index;
		synchronized (this) {
			// to describe the event:
			index = list.size();

			list.add(newElement);
			for (Consumer<T> consumer : consumers.toArray(new Consumer[0])) {
				consumer.accept(newElement);
			}
		}
		for (ListDataListener listener : listeners.toArray(new ListDataListener[0])) {
			listener.intervalAdded(new ListDataEvent(this,
					ListDataEvent.INTERVAL_ADDED, index, index));
		}
	}

	@Override
	public synchronized Iterator<T> iterator() {
		return new UnmodifiableIterator<>(list.iterator());
	}

	@Override
	public synchronized int getSize() {
		return list.size();
	}

	@Override
	public synchronized T getElementAt(int index) {
		return list.get(index);
	}

	/**
	 * Add a ListDataListener. This will only be issued INTERVAL_ADDED events,
	 * because a consumer object only supports add operations.
	 */
	public synchronized void addListDataListener(ListDataListener l) {
		listeners.add(l);
	}

	public synchronized void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}

}