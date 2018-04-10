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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.util.ObservableSet.SetDataEvent.Type;

/**
 * A {@code Set} that allows listeners.
 * <p>
 * This class is just a wrapper that manages the listeners; the actual work is
 * done by another {@code Set} of your choice.
 * <p>
 * The synchronization model here is similar to (but not as thorough as) the
 * architecture used for the ObservableList.
 * 
 * @param <T>
 *            the type of element in this set
 */
public class ObservableSet<T> implements Set<T> {

	public static class SetDataEvent<T> {
		public static enum Type {
			/** Identifies the addition of one or more elements to the set. */
			ELEMENTS_ADDED,
			/** Identifies the removal of one or more elements to the set. */
			ELEMENTS_REMOVED,
			/**
			 * Identifies the addition and removal of one or more elements to
			 * the set.
			 */
			BOTH
		}

		Type type;
		T[] addedElements;
		T[] removedElements;

		/**
		 * Create a SetDataEvent that adds and removes elements.
		 * 
		 * @param type
		 *            the type of this event.
		 * @param addedElements
		 *            the elements that were added during an operation.
		 * @param removedElements
		 *            the elements that were removed during an operation.
		 */
		private SetDataEvent(T[] addedElements, T[] removedElements) {
			if (addedElements.length == 0) {
				this.type = Type.ELEMENTS_REMOVED;
			} else if (removedElements.length == 0) {
				this.type = Type.ELEMENTS_ADDED;
			} else {
				this.type = Type.BOTH;
			}
			this.addedElements = addedElements;
			this.removedElements = removedElements;
		}

		/**
		 * 
		 * @return the type of this event.
		 */
		public Type getType() {
			return type;
		}

		/**
		 * 
		 * @return elements in this set that were added during an operation.
		 */
		public T[] getAddedElements() {
			return addedElements;
		}

		/**
		 * 
		 * @return elements in this set that were removed during an operation.
		 */
		public T[] getRemovedElements() {
			return removedElements;
		}
	}

	abstract class Operation<R> {
		/**
		 * This method is invoked safely in a read-only state, which the caller
		 * will stay in until <code>process()</code> or <code>nullOp()</code> is
		 * subsequently called.
		 * 
		 * @returns a SetDataEvent to describe this operation, or null if this
		 *          operation is a null-op and should be skipped. If this is a
		 *          null-op: then <code>nullOp()</code> is invoked. Otherwise we
		 *          acquire write-level access to this set and invoke
		 *          <code>process()</code>.
		 */
		abstract SetDataEvent<T> preProcess();

		/**
		 * Actually execute the operation. This is invoked while we have
		 * write-level access to the set. This should implement the SetDataEvent
		 * described by the <code>preProcess()</code> method.
		 */
		abstract R process();

		/**
		 * This is invoked if <code>preProcess()</code> returns null. This is
		 * still invoked inside a read-level access lock on this list, but it
		 * does not have write-level access. This immediately returns the return
		 * value of this operation.
		 */
		abstract R nullOp();
	}

	class AddOperation extends Operation<Boolean> {
		T newElement;

		AddOperation(T newElement) {
			this.newElement = newElement;
		}

		@Override
		SetDataEvent<T> preProcess() {
			if (contains(newElement))
				return null;
			T[] addedElements = (T[]) Array.newInstance(componentType, 1);
			addedElements[0] = newElement;
			return new SetDataEvent(addedElements, (T[]) Array.newInstance(
					componentType, 0));
		}

		@Override
		Boolean nullOp() {
			return false;
		}

		Boolean process() {
			return model.add(newElement);
		}
	}

	class AddAllOperation extends Operation<Boolean> {
		Collection<? extends T> newElements;

		AddAllOperation(Collection<? extends T> newElements) {
			this.newElements = newElements;
		}

		@Override
		SetDataEvent<T> preProcess() {
			List<T> addedElements = new ArrayList<T>();
			for (T e : newElements) {
				if (!contains(e)) {
					addedElements.add(e);
				}
			}
			if (addedElements.size() == 0)
				return null;
			T[] array = addedElements.toArray((T[]) Array.newInstance(
					componentType, addedElements.size()));
			return new SetDataEvent(array, (T[]) Array.newInstance(
					componentType, 0));
		}

		@Override
		Boolean nullOp() {
			return false;
		}

		Boolean process() {
			return model.addAll(newElements);
		}
	}

	class RetainAllOperation extends Operation<Boolean> {
		Collection<?> collection;

		RetainAllOperation(Collection<?> c) {
			collection = c;
		}

		@Override
		SetDataEvent<T> preProcess() {
			List<T> removedElements = new ArrayList<T>();
			for (T e : model) {
				if (!collection.contains(e)) {
					removedElements.add(e);
				}
			}
			if (removedElements.size() == 0)
				return null;
			T[] array = removedElements.toArray((T[]) Array.newInstance(
					componentType, removedElements.size()));
			return new SetDataEvent((T[]) Array.newInstance(componentType, 0),
					array);
		}

		@Override
		Boolean process() {
			return model.retainAll(collection);
		}

		@Override
		Boolean nullOp() {
			return false;
		}

	}

	class SetOperation extends Operation<Boolean> {
		Collection<T> newElements;
		Collection<T> elementsToAdd;
		Collection<T> elementsToRemove;

		SetOperation(Collection<T> newElements) {
			this.newElements = newElements;
		}

		@Override
		SetDataEvent<T> preProcess() {
			calculate();

			if (elementsToRemove.size() == 0 && elementsToAdd.size() == 0)
				return null;

			T[] removeArray = elementsToRemove.toArray((T[]) Array.newInstance(
					componentType, elementsToRemove.size()));
			T[] addArray = elementsToAdd.toArray((T[]) Array.newInstance(
					componentType, elementsToAdd.size()));
			return new SetDataEvent(addArray, removeArray);
		}

		@Override
		Boolean nullOp() {
			return false;
		}

		private void calculate() {
			if (elementsToAdd == null) {
				elementsToAdd = new HashSet<>();
				elementsToRemove = new HashSet<>();
				for (T newElement : newElements) {
					if (!contains(newElement)) {
						elementsToAdd.add(newElement);
					}
				}
				for (T oldElement : model) {
					if (!newElements.contains(oldElement)) {
						elementsToRemove.add(oldElement);
					}
				}
			}
		}

		Boolean process() {
			calculate();

			if (elementsToRemove.size() == 0 && elementsToAdd.size() == 0)
				return false;

			model.removeAll(elementsToRemove);
			model.addAll(elementsToAdd);

			return true;
		}
	}

	class RemoveAllOperation extends Operation<Boolean> {
		Collection<?> elementsToRemove;

		RemoveAllOperation(Collection<?> elementsToRemove) {
			this.elementsToRemove = elementsToRemove;
		}

		@Override
		SetDataEvent<T> preProcess() {
			List<T> removedElements = new ArrayList<T>();
			for (Object e : elementsToRemove) {
				if (contains(e)) {
					removedElements.add((T) e);
				}
			}
			if (removedElements.size() == 0)
				return null;
			T[] array = removedElements.toArray((T[]) Array.newInstance(
					componentType, removedElements.size()));
			return new SetDataEvent((T[]) Array.newInstance(componentType, 0),
					array);
		}

		@Override
		Boolean nullOp() {
			return false;
		}

		Boolean process() {
			return model.removeAll(elementsToRemove);
		}
	}

	class RemoveOperation extends Operation<Boolean> {
		Object elementToRemove;

		RemoveOperation(Object elementToRemove) {
			this.elementToRemove = elementToRemove;
		}

		@Override
		SetDataEvent<T> preProcess() {
			if (!contains(elementToRemove))
				return null;

			T[] removedElements = (T[]) Array.newInstance(componentType, 1);
			removedElements[0] = (T) elementToRemove;
			return new SetDataEvent<T>(
					(T[]) Array.newInstance(componentType, 0), removedElements);
		}

		@Override
		Boolean process() {
			return model.remove(elementToRemove);
		}

		@Override
		Boolean nullOp() {
			return false;
		}
	}

	class ClearOperation extends Operation<Boolean> {

		@Override
		SetDataEvent<T> preProcess() {
			if (size() == 0)
				return null;
			T[] elementsRemoved = toComponentArray();
			SetDataEvent<T> event = new SetDataEvent<T>(
					(T[]) Array.newInstance(componentType, 0), elementsRemoved);
			return event;
		}

		@Override
		Boolean process() {
			model.clear();
			return true;
		}

		@Override
		Boolean nullOp() {
			return false;
		}

	}

	/**
	 * This is thrown when notifying a <code>Listener</code> resulted in further
	 * modifying this set.
	 */
	public static class RecursiveListenerModificationException extends
			RuntimeException {
		private static final long serialVersionUID = 1L;

	}

	/**
	 * Enough permits for an arbitrarily large number of threads.
	 */
	private static int PERMIT_MAX = 10000;

	/**
	 * A listener for set modifications. Note this listener is allowed read this
	 * set, but a RecursiveListenerModificationException will be thrown if it
	 * attempts to modify it.
	 * 
	 * @param <T>
	 *            the type of element this set interacts with
	 */
	public static interface Listener<T> {
		public void elementsAdded(SetDataEvent<T> event);

		public void elementsChanged(SetDataEvent<T> event);

		public void elementsRemoved(SetDataEvent<T> event);
	}

	Set<T> model;
	List<Listener<T>> listeners = new ArrayList<Listener<T>>();
	List<ChangeListener> changeListeners = new ArrayList<ChangeListener>();
	Class<T> componentType;
	private Thread writingThread = null;

	protected final Semaphore readSemaphore = new Semaphore(PERMIT_MAX);

	/**
	 * Create an ObservableSet that uses a HashSet.
	 * 
	 * @param componentType
	 *            the component type to be used when converting this set to an
	 *            array.
	 */
	public ObservableSet(Class<T> componentType) {
		this(componentType, new HashSet<T>());
	}

	/**
	 * @param componentType
	 *            the component type to be used when converting this set to an
	 *            array.
	 * @param s
	 *            the model used for data storage.
	 */
	public ObservableSet(Class<T> componentType, Set<T> s) {
		this.componentType = componentType;
		this.model = s;
	}

	public void addListener(Listener<T> l) {
		synchronized (listeners) {
			listeners.add(l);
		}
	}

	public void removeListener(Listener<T> l) {
		synchronized (listeners) {
			listeners.remove(l);
		}
	}

	public int size() {
		readSemaphore.acquireUninterruptibly();
		try {
			return model.size();
		} finally {
			readSemaphore.release();
		}
	}

	public boolean isEmpty() {
		readSemaphore.acquireUninterruptibly();
		try {
			return model.isEmpty();
		} finally {
			readSemaphore.release();
		}
	}

	public boolean contains(Object o) {
		readSemaphore.acquireUninterruptibly();
		try {
			return model.contains(o);
		} finally {
			readSemaphore.release();
		}
	}

	public Iterator<T> iterator() {
		return new ListenerIterator(model.iterator());
	}

	class ListenerIterator implements Iterator<T> {
		Iterator<T> iterModel;
		T lastValue = null;

		protected ListenerIterator(Iterator<T> i) {
			iterModel = i;
		}

		@Override
		public boolean hasNext() {
			readSemaphore.acquireUninterruptibly();
			try {
				return iterModel.hasNext();
			} finally {
				readSemaphore.release();
			}
		}

		@Override
		public T next() {
			readSemaphore.acquireUninterruptibly(1);
			try {
				lastValue = iterModel.next();
				return lastValue;
			} finally {
				readSemaphore.release(1);
			}
		}

		@Override
		public void remove() {
			if (lastValue == null)
				throw new NullPointerException(
						"this method should be called after first calling next()");
			execute(new RemoveOperation(lastValue));
		}
	}

	/** This fires first the Listeners, and then the ChangeListeners. */
	protected void fireAllListeners(SetDataEvent<T> event) {
		synchronized (listeners) {
			for (Listener<T> l : listeners) {
				try {
					Type type = event.getType();
					switch (type) {
					case ELEMENTS_ADDED:
						l.elementsAdded(event);
						break;
					case ELEMENTS_REMOVED:
						l.elementsRemoved(event);
						break;
					case BOTH:
						l.elementsChanged(event);
						break;
					default:
						throw new RuntimeException("Unexpected condition: "
								+ type);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		synchronized (changeListeners) {
			for (ChangeListener l : changeListeners) {
				try {
					l.stateChanged(new ChangeEvent(this));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Object[] toArray() {
		return toComponentArray();
	}

	public <S> S[] toArray(S[] a) {
		readSemaphore.acquireUninterruptibly();
		try {
			return model.toArray(a);
		} finally {
			readSemaphore.release();
		}
	}

	protected <R> R execute(Operation<R> op) {
		R returnValue;
		readSemaphore.acquireUninterruptibly();
		int borrowedPermits = 1;
		boolean acquiredWritingThread = false;
		try {
			if (writingThread != null) {
				throw new RecursiveListenerModificationException();
			}
			writingThread = Thread.currentThread();
			acquiredWritingThread = true;

			SetDataEvent<T> event = op.preProcess();
			if (event == null) {
				returnValue = op.nullOp();
			} else {
				readSemaphore.acquireUninterruptibly(PERMIT_MAX
						- borrowedPermits);
				borrowedPermits = PERMIT_MAX;

				returnValue = op.process();
				readSemaphore.release(borrowedPermits - 1);
				borrowedPermits = 1;

				fireAllListeners(event);
			}
			return returnValue;
		} finally {
			readSemaphore.release(borrowedPermits);
			if (acquiredWritingThread)
				writingThread = null;
		}
	}

	public boolean add(T e) {
		return execute(new AddOperation(e));
	}

	public boolean remove(Object o) {
		return execute(new RemoveOperation(o));
	}

	public boolean containsAll(Collection<?> c) {
		readSemaphore.acquireUninterruptibly();
		try {
			return model.containsAll(c);
		} finally {
			readSemaphore.release();
		}
	}

	public boolean addAll(Collection<? extends T> c) {
		return execute(new AddAllOperation(c));
	}

	public boolean retainAll(Collection<?> c) {
		return execute(new RetainAllOperation(c));
	}

	public boolean removeAll(Collection<?> c) {
		return execute(new RemoveAllOperation(c));
	}

	public boolean set(Collection<T> newContents) {
		return execute(new SetOperation(newContents));
	}

	public void clear() {
		execute(new ClearOperation());
	}

	public boolean equals(Object o) {
		if (o instanceof ObservableSet) {
			o = ((ObservableSet) o).model;
		}

		readSemaphore.acquireUninterruptibly();
		try {
			return model.equals(o);
		} finally {
			readSemaphore.release();
		}
	}

	public int hashCode() {
		readSemaphore.acquireUninterruptibly();
		try {
			return model.hashCode();
		} finally {
			readSemaphore.release();
		}
	}

	/**
	 * Create an array returning all the elements of this set.
	 * <p>
	 * This method uses the existing read/write locking architecture the
	 * ObservableSet uses, so it is safer than synchronizing this list to
	 * extract the elements separately.
	 * 
	 * @return an array representation of all elements in this set.
	 */
	public T[] toComponentArray() {
		readSemaphore.acquireUninterruptibly();
		try {
			T[] array = (T[]) Array.newInstance(componentType, size());
			toArray(array);
			return array;
		} finally {
			readSemaphore.release();
		}
	}

	public void addChangeListener(ChangeListener changeListener) {
		synchronized (changeListeners) {
			changeListeners.add(changeListener);
		}
	}

	public void removeChangeListener(ChangeListener changeListener) {
		synchronized (changeListeners) {
			changeListeners.remove(changeListener);
		}
	}
}