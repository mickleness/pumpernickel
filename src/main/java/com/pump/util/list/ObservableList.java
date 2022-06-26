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
package com.pump.util.list;

import java.io.Serializable;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import javax.swing.ComboBoxModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * This is a List which supports three types of listeners.
 * <p>
 * All operations take place inside either a read or write lock. Listeners are
 * notified inside a write lock during which no other thread can interact with
 * this list.
 * <p>
 * All attempts to interact with this list are subject to a timeout if the lock
 * doesn't become available. If that timeout is exceeded a
 * {@link TimeoutException} is thrown. (The default timeout is 10 seconds.)
 * <p>
 * When you add a listener you have the option of designating that listener as
 * allowing modifications or not. This is a convenience/safety feature to help
 * avoid cascading/competing listeners from changing this list in unexpected
 * ways.
 * <p>
 * You can also call {@link #createUIMirror(ListFilter)} or
 * {@link #createUIView()} to create <code>java.awt.event.ListModel</code> based
 * on this list.
 * 
 * @param <T>
 */
public class ObservableList<T> implements List<T>, Serializable {
	private static final long serialVersionUID = 1L;

	// TODO: add a vetoable listener that can alter modifications before they
	// are made.

	private static abstract class AbstractComboBoxModel<T> implements
			ComboBoxModel<T> {
		Object selectedItem;
		List<ListDataListener> listDataListeners = new ArrayList<>();

		@Override
		public void addListDataListener(ListDataListener l) {
			listDataListeners.add(l);
		}

		@Override
		public void removeListDataListener(ListDataListener l) {
			listDataListeners.remove(l);
		}

		@Override
		public void setSelectedItem(Object anItem) {
			if (Objects.equals(anItem, getSelectedItem()))
				return;
			selectedItem = anItem;
			fireListDataListeners(new ListDataEvent(this,
					ListDataEvent.CONTENTS_CHANGED, -1, -1));
		}

		protected void fireListDataListeners(ListDataEvent event) {
			for (ListDataListener listener : listDataListeners) {
				switch (event.getType()) {
				case ListDataEvent.CONTENTS_CHANGED:
					listener.contentsChanged(event);
					break;
				case ListDataEvent.INTERVAL_ADDED:
					listener.intervalAdded(event);
					break;
				case ListDataEvent.INTERVAL_REMOVED:
					listener.intervalRemoved(event);
					break;
				default:
					throw new IllegalArgumentException("Unsupported event: "
							+ event);
				}
			}
		}

		@Override
		public Object getSelectedItem() {
			return selectedItem;
		}
	}

	/**
	 * This converts an ObservableList into a
	 * <code>java.awt.event.ListModel</code>. You should only use this class if
	 * the ObservableList is modified on the event dispatch thread. For all
	 * other cases: you should use the {@link UIMirror}.
	 * 
	 * @param <T>
	 */
	public static class UIView<T> extends AbstractComboBoxModel<T> {
		ObservableList<T> masterList;

		UIView(ObservableList<T> masterList) {
			this.masterList = masterList;
			masterList.addListListener(new ListListener<T>() {

				@Override
				public void elementsAdded(AddElementsEvent<T> event) {
					UIView.this.fireListDataListeners(event
							.createListDataEvent());
				}

				@Override
				public void elementsRemoved(RemoveElementsEvent<T> event) {
					UIView.this.fireListDataListeners(event
							.createListDataEvent());
				}

				@Override
				public void elementChanged(ChangeElementEvent<T> event) {
					UIView.this.fireListDataListeners(event
							.createListDataEvent());
				}

				@Override
				public void elementsReplaced(ReplaceElementsEvent<T> event) {
					UIView.this.fireListDataListeners(event
							.createListDataEvent());
				}

			}, true);
		}

		@Override
		public int getSize() {
			return masterList.size();
		}

		@Override
		public T getElementAt(int index) {
			return masterList.get(index);
		}

	}

	/**
	 * This is a <code>java.awt.event.ListModel</code> that mirrors an
	 * ObservableList. The ObservableList can be modified in any thread at any
	 * time, and this object will maintain a separate copy of that list that is
	 * only modified on the event dispatch thread. This object is always safe to
	 * use with UI elements. It is possible that it can be temporarily
	 * out-of-sync with its parent ObservableList.
	 * 
	 * @param <T>
	 */
	public static class UIMirror<T> extends AbstractComboBoxModel<T> {
		List<T> mirrorList = new ArrayList<>();
		List<ListEvent<T>> eventQueue = new ArrayList<>();
		ListFilter<T> filter;
		ObservableList<T> masterList;

		Runnable eventQueueRunnable = new Runnable() {
			public void run() {
				ListEvent<T>[] events;
				synchronized (eventQueue) {
					events = (ListEvent<T>[]) eventQueue
							.toArray(new ListEvent[eventQueue.size()]);
					eventQueue.clear();
				}
				for (ListEvent<T> event : events) {
					event.execute(mirrorList);
					fireListDataListeners(event.createListDataEvent());
				}
			}
		};
		ListEvent<T> resetListEvent;

		private UIMirror(ObservableList<T> masterList, ListFilter<T> filter) {
			this.filter = filter;
			this.masterList = masterList;

			resetListEvent = new ListEvent<T>(UIMirror.this) {
				@Override
				public void execute(List<T> list) {
					UIMirror.this.masterList.acquireReadLock();
					try {
						mirrorList.clear();
						if (UIMirror.this.filter == null
								|| !UIMirror.this.filter.isActive()) {
							mirrorList.addAll(UIMirror.this.masterList);
						} else {
							for (T element : UIMirror.this.masterList) {
								if (UIMirror.this.filter.accept(element))
									mirrorList.add(element);
							}
						}
					} finally {
						UIMirror.this.masterList.readLock.unlock();
					}
				}

				@Override
				protected ListDataEvent createListDataEvent() {
					return new ListDataEvent(UIMirror.this,
							ListDataEvent.CONTENTS_CHANGED, 0,
							mirrorList.size() - 1);
				}
			};

			masterList.acquireWriteLock(false);
			try {
				masterList.addListListener(new ListListener<T>() {

					@Override
					public void elementsAdded(AddElementsEvent<T> event) {
						processEvent(event);
					}

					@Override
					public void elementsRemoved(RemoveElementsEvent<T> event) {
						processEvent(event);
					}

					@Override
					public void elementChanged(ChangeElementEvent<T> event) {
						processEvent(event);
					}

					@Override
					public void elementsReplaced(ReplaceElementsEvent<T> event) {
						processEvent(event);
					}

					private void processEvent(ListEvent<T> event) {
						synchronized (eventQueue) {
							eventQueue.add(event);
						}
						if (SwingUtilities.isEventDispatchThread()) {
							eventQueueRunnable.run();
						} else {
							SwingUtilities.invokeLater(eventQueueRunnable);
						}
					}

				}, false);
				if (filter != null) {
					filter.addChangeListener(new ChangeListener() {

						@Override
						public void stateChanged(ChangeEvent e) {
							synchronized (eventQueue) {
								eventQueue.clear();
								eventQueue.add(resetListEvent);
							}
							if (SwingUtilities.isEventDispatchThread()) {
								eventQueueRunnable.run();
							} else {
								SwingUtilities.invokeLater(eventQueueRunnable);
							}
						}

					});
				}
				eventQueue.add(resetListEvent);
				eventQueueRunnable.run();
			} finally {
				masterList.writeLock.unlock();
			}
		}

		@Override
		public int getSize() {
			return mirrorList.size();
		}

		@Override
		public T getElementAt(int index) {
			return mirrorList.get(index);
		}

		/**
		 * Return the index of an element, or -1 if that element isn't found.
		 */
		public int indexOf(T element) {
			for (int a = 0; a < getSize(); a++) {
				if (Objects.equals(element, getElementAt(a)))
					return a;
			}
			return -1;
		}
	}

	/**
	 * This listener is notified with the before and after state of the contents
	 * of this list.
	 * 
	 * @param <T>
	 */
	public static interface ArrayListener<T> {
		/**
		 * This is called when the ObservableList is modified.
		 * 
		 * @param source
		 *            the list that was modified
		 * @param oldList
		 *            the original contents of the list before the current
		 *            operation.
		 * @param newList
		 *            the contents of the list after the current operation.
		 *            <p>
		 *            Note in rare cases this may be different than the source
		 *            list itself if previous listeners have already modified
		 *            the ObservableList.
		 */
		public void listChanged(ObservableList<T> source, T[] oldList,
				T[] newList);
	}

	private static class DefaultUncaughtExceptionHandler implements
			UncaughtExceptionHandler {

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			e.printStackTrace();
		}

	}

	private static class ListenerManager<T> implements Cloneable {
		LinkedHashMap<Object, Boolean> listeners = new LinkedHashMap<>();

		boolean containsArrayListener() {
			for (Object listener : listeners.keySet()) {
				if (listener instanceof ArrayListener)
					return true;
			}
			return false;
		}

		boolean containsListListener() {
			for (Object listener : listeners.keySet()) {
				if (listener instanceof ListListener)
					return true;
			}
			return false;
		}

		ChangeListener[] getChangeListeners() {
			List<ChangeListener> l = new ArrayList<>();

			for (Object listener : listeners.keySet()) {
				if (listener instanceof ChangeListener)
					l.add((ChangeListener) listener);
			}
			return l.toArray(new ChangeListener[l.size()]);
		}

		ListListener<T>[] getListListeners() {
			List<ListListener<T>> l = new ArrayList<>();

			for (Object listener : listeners.keySet()) {
				if (listener instanceof ListListener)
					l.add((ListListener<T>) listener);
			}
			return l.toArray(new ListListener[l.size()]);
		}

		ArrayListener<T>[] getArrayListeners() {
			List<ArrayListener> l = new ArrayList<>();

			for (Object listener : listeners.keySet()) {
				if (listener instanceof ArrayListener)
					l.add((ArrayListener) listener);
			}
			return l.toArray(new ArrayListener[l.size()]);
		}
	}

	public static class TimeoutException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public TimeoutException(String msg) {
			super(msg);
		}

		public TimeoutException(String msg, Throwable cause) {
			super(msg, cause);
		}
	}

	transient ListenerManager<T> listenerManager;
	ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	ReadLock readLock = lock.readLock();
	WriteLock writeLock = lock.writeLock();
	List<T> data;
	int timeoutSeconds = 10;
	protected transient AtomicInteger modCount;
	private UncaughtExceptionHandler uncaughtExceptionHandler;
	private transient Boolean allowRecursiveListenerModification;
	private boolean allowAnyModification;

	/**
	 * Create a new empty ObservableList.
	 */
	public ObservableList() {
		this(new ArrayList<T>());
	}

	/**
	 * Create a new ObservableList that stores its data using the argument
	 * provided.
	 */
	public ObservableList(List<T> data) {
		this(data, new ListenerManager<T>(), new AtomicInteger(0),
				new DefaultUncaughtExceptionHandler(), true);
	}

	private ObservableList(List<T> data, ListenerManager<T> listenerManager,
			AtomicInteger modCount,
			UncaughtExceptionHandler uncaughtExceptionHandler,
			boolean allowAnyModification) {
		Objects.requireNonNull(data);
		Objects.requireNonNull(listenerManager);
		Objects.requireNonNull(modCount);
		this.data = data;
		this.listenerManager = listenerManager;
		this.modCount = modCount;
		this.allowAnyModification = allowAnyModification;
		setListenerUncaughtExceptionHandler(uncaughtExceptionHandler);
	}

	/**
	 * Add a ListListener.
	 * 
	 * @param listListener
	 *            the new listener to add.
	 * @param allowModification
	 *            if false then an exception will be thrown if this listener
	 *            attempts to further modify this ObservableList.
	 */
	public void addListListener(ListListener<T> listListener,
			boolean allowModification) {
		Objects.requireNonNull(listListener);
		acquireWriteLock(false);
		try {
			listenerManager.listeners.put(listListener, allowModification);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Remove a ListListener.
	 * 
	 * @param listListener
	 *            the listener to remove.
	 */
	public void removeListListener(ListListener<T> l) {
		acquireWriteLock(false);
		try {
			listenerManager.listeners.remove(l);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Add a ArrayListener.
	 * 
	 * @param ArrayListener
	 *            the new listener to add.
	 * @param allowModification
	 *            if false then an exception will be thrown if this listener
	 *            attempts to further modify this ObservableList.
	 */
	public void addArrayListener(ArrayListener<T> l, boolean allowModification) {
		Objects.requireNonNull(l);
		acquireWriteLock(false);
		try {
			listenerManager.listeners.put(l, allowModification);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Remove a ArrayListener.
	 * 
	 * @param ArrayListener
	 *            the listener to remove.
	 * @param allowModification
	 *            if false then an exception will be thrown if this listener
	 *            attempts to further modify this ObservableList.
	 */
	public void removeArrayListener(ArrayListener<T> l) {
		acquireWriteLock(false);
		try {
			listenerManager.listeners.remove(l);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Add a ChangeListener.
	 * 
	 * @param ChangeListener
	 *            the new listener to add.
	 * @param allowModification
	 *            if false then an exception will be thrown if this listener
	 *            attempts to further modify this ObservableList.
	 */
	public void addChangeListener(ChangeListener l, boolean allowModification) {
		Objects.requireNonNull(l);
		acquireWriteLock(false);
		try {
			listenerManager.listeners.put(l, allowModification);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Remove a ChangeListener.
	 * 
	 * @param ChangeListener
	 *            the listener to remove.
	 * @param allowModification
	 *            if false then an exception will be thrown if this listener
	 *            attempts to further modify this ObservableList.
	 */
	public void removeChangeListener(ChangeListener l) {
		acquireWriteLock(false);
		try {
			listenerManager.listeners.remove(l);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Return all the ChangeListeners attached to this list.
	 */
	public ChangeListener[] getChangeListeners() {
		acquireReadLock();
		try {
			return listenerManager.getChangeListeners();
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * Return all the ArrayListeners attached to this list.
	 */
	public ArrayListener<T>[] getArrayListeners() {
		acquireReadLock();
		try {
			return listenerManager.getArrayListeners();
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * Return all the ListListeners attached to this list.
	 */
	public ListListener<T>[] getListListeners() {
		acquireReadLock();
		try {
			return listenerManager.getListListeners();
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public int size() {
		acquireReadLock();
		try {
			return data.size();
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * Set the handler that is notified when a listener throws an exception.
	 * <p>
	 * The default handler just calls {@link Exception#printStackTrace()}.
	 * 
	 * @param uncaughtExceptionHandler
	 */
	public void setListenerUncaughtExceptionHandler(
			UncaughtExceptionHandler uncaughtExceptionHandler) {
		Objects.requireNonNull(uncaughtExceptionHandler);
		this.uncaughtExceptionHandler = uncaughtExceptionHandler;
	}

	/**
	 * Return the handler that is notified when a listener throws an exception.
	 */
	public UncaughtExceptionHandler getListenerUncaughtExceptionHandler() {
		return uncaughtExceptionHandler;
	}

	/**
	 * This is thrown when notifying a <code>ListDataListener</code> resulted in
	 * further modifying this list when it shouldn't have.
	 * <p>
	 * Synchronized listeners are always forbidden from altering the list, and
	 * unsynchronized listeners have an optional argument when they are added to
	 * declare whether they can modify the list or not.
	 */
	public static class RecursiveListenerModificationException extends
			RuntimeException {
		private static final long serialVersionUID = 1L;

		RecursiveListenerModificationException(String msg) {
			super(msg);
		}

	}

	private void acquireWriteLock(boolean listDataModification) {

		if (listDataModification) {
			// when you add this listener: pass in "true" for allowModification
			if (allowRecursiveListenerModification != null
					&& !allowRecursiveListenerModification)
				throw new RecursiveListenerModificationException(
						"A listener is attempting to modify this list without permission.");
			if (!allowAnyModification)
				throw new IllegalStateException(
						"This list is a read-only view of another list.");
		}

		int timeoutSeconds = getTimeoutSeconds();
		try {
			if (!writeLock.tryLock(timeoutSeconds, TimeUnit.SECONDS)) {
				throw new TimeoutException(
						"Failed to acquire a write lock after "
								+ NumberFormat.getInstance().format(
										timeoutSeconds) + " seconds.");
			}
		} catch (InterruptedException e) {
			if (!writeLock.tryLock()) {
				throw new TimeoutException("Failed to acquire a write lock.");
			}
			throw new TimeoutException("Failed to acquire a write lock.", e);
		}
	}

	private void acquireReadLock() {
		int timeoutSeconds = getTimeoutSeconds();
		try {
			if (!readLock.tryLock(timeoutSeconds, TimeUnit.SECONDS)) {
				throw new TimeoutException(
						"Failed to acquire a read lock after "
								+ NumberFormat.getInstance().format(
										timeoutSeconds) + " seconds.");
			}
		} catch (InterruptedException e) {
			if (!readLock.tryLock()) {
				throw new TimeoutException("Failed to acquire a read lock.");
			}
			throw new TimeoutException("Failed to acquire a read lock.", e);
		}
	}

	/**
	 * Return the number of seconds this list will wait to acquire a lock.
	 */
	public int getTimeoutSeconds() {
		return timeoutSeconds;
	}

	/**
	 * Assign the number of seconds this list will wait to acquire a lock.
	 */
	public void setTimeoutSeconds(int timeoutSeconds) {
		this.timeoutSeconds = timeoutSeconds;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean contains(Object element) {
		acquireReadLock();
		try {
			return data.contains(element);
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public T get(int index) {
		acquireReadLock();
		try {
			return data.get(index);
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public int indexOf(Object element) {
		acquireReadLock();
		try {
			return data.indexOf(element);
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public int lastIndexOf(Object element) {
		acquireReadLock();
		try {
			return data.lastIndexOf(element);
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		acquireReadLock();
		try {
			return data.containsAll(c);
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public Object[] toArray() {
		acquireReadLock();
		try {
			return data.toArray();
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * Create an array returning all the elements of this list.
	 * <p>
	 * This method uses the existing read/write locking architecture the
	 * ObservableList uses, so it is safer than synchronizing this list to
	 * extract the elements separately.
	 * 
	 * @param arrayClass
	 *            the component type of the array.
	 * @return an array representation of all elements in this list.
	 */
	public <S> S[] toArray(Class<S> arrayClass) {
		acquireReadLock();
		try {
			S[] array = (S[]) Array.newInstance(arrayClass, size());
			toArray(array);
			return array;
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public <S> S[] toArray(S[] a) {
		acquireReadLock();
		try {
			return data.toArray(a);
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public Iterator<T> iterator() {
		return listIterator();
	}

	@Override
	public boolean add(T e) {
		acquireWriteLock(true);
		try {
			AddOperation op = new AddOperation(Arrays.asList(e));
			boolean returnValue = op.execute();
			op.notifyListeners();
			return returnValue;
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public boolean remove(Object o) {
		acquireWriteLock(true);
		try {
			RemoveElementsOperation op = new RemoveElementsOperation(o);
			boolean returnValue = op.execute();
			op.notifyListeners();
			return returnValue;
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		acquireWriteLock(true);
		try {
			AddOperation op = new AddOperation(c);
			boolean returnValue = op.execute();
			op.notifyListeners();
			return returnValue;
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		acquireWriteLock(true);
		try {
			AddOperation op = new AddOperation(index, c);
			boolean returnValue = op.execute();
			op.notifyListeners();
			return returnValue;
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Add a series of elements.
	 * 
	 * @param array
	 *            the new elements to add.
	 * @return true if the argument has one or more elements.
	 */
	public boolean addAll(T... array) {
		return addAll(Arrays.asList(array));
	}

	/**
	 * Add a series of elements.
	 * 
	 * @param index
	 *            the index to insert the elements at.
	 * @param array
	 *            the new elements to add.
	 * @return true if the argument has one or more elements.
	 */
	public boolean addAll(int index, T... array) {
		return addAll(index, Arrays.asList(array));
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		acquireWriteLock(true);
		try {
			RemoveElementsOperation op = new RemoveElementsOperation(
					c.toArray());
			boolean returnValue = op.execute();
			op.notifyListeners();
			return returnValue;
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		acquireWriteLock(true);
		try {
			List<T> elementsToRemove = new ArrayList<>();
			Iterator<T> iter = iterator();
			while (iter.hasNext()) {
				T element = iter.next();
				if (!c.contains(element)) {
					elementsToRemove.add(element);
				}
			}
			if (elementsToRemove.isEmpty())
				return false;

			removeAll(elementsToRemove);
			return true;
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public void clear() {
		acquireWriteLock(true);
		try {
			RemoveElementsOperation op = new RemoveElementsOperation(toArray());
			op.execute();
			op.notifyListeners();
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public T set(int index, T element) {
		acquireWriteLock(true);
		try {
			SetOperation op = new SetOperation(index, element);
			T returnValue = (T) op.execute();
			if (!Objects.equals(returnValue, element)) {
				op.notifyListeners();
			}
			return returnValue;
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public void add(int index, T element) {
		acquireWriteLock(true);
		try {
			AddOperation op = new AddOperation(index,
					Collections.singleton(element));
			op.execute();
			op.notifyListeners();
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public T remove(int index) {
		acquireWriteLock(true);
		try {
			RemoveIndexOperation op = new RemoveIndexOperation(index);
			T returnValue = (T) op.execute();
			op.notifyListeners();
			return returnValue;
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public ListIterator<T> listIterator() {
		return listIterator(0);
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		acquireReadLock();
		try {
			return new MyIterator(index);
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return new ObservableList<T>(data.subList(fromIndex, toIndex),
				listenerManager, modCount,
				getListenerUncaughtExceptionHandler(), allowAnyModification);
	}

	abstract class Operation {

		LinkedHashMap<Object, Boolean> listeners;
		Object[] oldArray;
		boolean nullOp;

		Operation() {
			listeners = new LinkedHashMap<>(listenerManager.listeners);
			if (listenerManager.containsArrayListener())
				oldArray = toArray();
		}

		abstract Object execute();

		void notifyListeners() {
			if (nullOp)
				return;

			Object[] newArray = oldArray == null ? null : toArray();
			ChangeEvent changeEvent = null;

			for (Entry<Object, Boolean> listenerEntry : listeners.entrySet()) {
				Object listener = listenerEntry.getKey();
				Boolean oldModificationAllowed = allowRecursiveListenerModification;
				allowRecursiveListenerModification = listenerEntry.getValue();
				try {
					if (listener instanceof ArrayListener) {
						((ArrayListener) listener).listChanged(
								ObservableList.this, oldArray, newArray);
					} else if (listener instanceof ChangeListener) {
						if (changeEvent == null)
							changeEvent = new ChangeEvent(ObservableList.this);
						((ChangeListener) listener).stateChanged(changeEvent);
					} else if (listener instanceof ListListener) {
						notifyListListener((ListListener<T>) listener);
					}
				} catch (Exception e) {
					getListenerUncaughtExceptionHandler().uncaughtException(
							Thread.currentThread(), e);
				} finally {
					allowRecursiveListenerModification = oldModificationAllowed;
				}
			}
		}

		void setNullOp() {
			nullOp = true;
		}

		abstract void notifyListListener(ListListener<T> listener);
	}

	class SetOperation extends Operation {
		int index;
		T oldElement;
		T newElement;

		public SetOperation(int index, T newElement) {
			this.index = index;
			this.newElement = newElement;
		}

		@Override
		T execute() {
			if (Objects.equals(oldElement, newElement)) {
				setNullOp();
				return oldElement;
			}
			oldElement = data.set(index, newElement);
			modCount.incrementAndGet();
			return oldElement;
		}

		@Override
		void notifyListListener(ListListener<T> listener) {
			listener.elementChanged(new ChangeElementEvent<T>(
					ObservableList.this, index, oldElement, newElement));
		}

	}

	class AddOperation extends Operation {
		int index;
		List<T> newElements;

		public AddOperation(int index, Collection<? extends T> newElements) {
			this.index = index;
			this.newElements = Collections.unmodifiableList(new ArrayList<>(
					newElements));
		}

		public AddOperation(Collection<? extends T> newElements) {
			this(size(), newElements);
		}

		@Override
		Boolean execute() {
			if (newElements.size() == 0) {
				setNullOp();
				return false;
			}
			data.addAll(index, newElements);
			modCount.incrementAndGet();
			return true;
		}

		@Override
		void notifyListListener(ListListener<T> listener) {
			listener.elementsAdded(new AddElementsEvent<T>(ObservableList.this,
					index, newElements));
		}

	}

	class RemoveIndexOperation extends Operation {
		int index;
		T oldElement;

		public RemoveIndexOperation(int index) {
			this.index = index;
		}

		@Override
		T execute() {
			oldElement = data.remove(index);
			return oldElement;
		}

		@Override
		void notifyListListener(ListListener<T> listener) {
			TreeMap<Integer, T> removedElements = new TreeMap<>();
			removedElements.put(index, oldElement);
			listener.elementsRemoved(new RemoveElementsEvent<T>(
					ObservableList.this, removedElements));
		}

	}

	class RemoveElementsOperation extends Operation {

		TreeMap<Integer, T> removedElements;
		Object[] elements;

		public RemoveElementsOperation(Object... elements) {
			this.elements = elements;
			if (listenerManager.containsListListener()) {
				// TODO: this will yield inaccurate indices for multiple
				// equivalent objects
				removedElements = new TreeMap<>();
				for (Object element : elements) {
					int index = data.indexOf(element);
					if (index >= 0)
						removedElements.put(index, (T) element);
				}
			}
		}

		@Override
		Boolean execute() {
			boolean returnValue = data.removeAll(Arrays.asList(elements));
			if (returnValue) {
				modCount.incrementAndGet();
			} else {
				setNullOp();
			}
			return returnValue;
		}

		@Override
		void notifyListListener(ListListener<T> listener) {
			if (removedElements == null) {
				// this should never happen
				throw new IllegalStateException();
			}

			listener.elementsRemoved(new RemoveElementsEvent<T>(
					ObservableList.this, removedElements));
		}

	}

	class ReplaceAllOperation extends Operation {
		Collection<T> newElements;
		List<T> newElementsAsList;
		List<T> oldElements;

		public ReplaceAllOperation(Collection<T> newElements) {
			this.newElements = newElements;
			if (listenerManager.containsListListener()) {
				newElementsAsList = new ArrayList<>(newElements.size());
				newElementsAsList.addAll(newElements);
				oldElements = new ArrayList<>(data.size());
				oldElements.addAll(data);
			}
		}

		@Override
		Boolean execute() {
			if (data.equals(newElements)) {
				setNullOp();
				return false;
			}

			data.clear();
			data.addAll(newElements);
			modCount.incrementAndGet();
			return true;
		}

		@Override
		void notifyListListener(ListListener<T> listener) {
			if (newElementsAsList == null || oldElements == null) {
				// this should never happen
				throw new IllegalStateException();
			}

			listener.elementsReplaced(new ReplaceElementsEvent<T>(
					ObservableList.this, oldElements, newElementsAsList));
		}

	}

	/**
	 * This is adapted from the code in AbstractList.
	 */
	private class MyIterator implements ListIterator<T> {
		/**
		 * Index of element to be returned by subsequent call to next.
		 */
		int cursor = 0;

		/**
		 * Index of element returned by most recent call to next or previous.
		 * Reset to -1 if this element is deleted by a call to remove.
		 */
		int lastRet = -1;

		/**
		 * The modCount value that the iterator believes that the backing List
		 * should have. If this expectation is violated, the iterator has
		 * detected concurrent modification.
		 */
		int expectedModCount = modCount.get();

		MyIterator(int index) {
			cursor = index;
		}

		@Override
		public boolean hasNext() {
			return cursor != size();
		}

		@Override
		public T next() {
			checkForComodification();
			try {
				int i = cursor;
				T next = get(i);
				lastRet = i;
				cursor = i + 1;
				return next;
			} catch (IndexOutOfBoundsException e) {
				checkForComodification();
				throw new NoSuchElementException();
			}
		}

		@Override
		public void remove() {
			if (lastRet < 0)
				throw new IllegalStateException();
			checkForComodification();

			try {
				ObservableList.this.remove(lastRet);
				if (lastRet < cursor)
					cursor--;
				lastRet = -1;
				expectedModCount = modCount.intValue();
			} catch (IndexOutOfBoundsException e) {
				throw new ConcurrentModificationException();
			}
		}

		final void checkForComodification() {
			if (modCount.intValue() != expectedModCount)
				throw new ConcurrentModificationException();
		}

		@Override
		public boolean hasPrevious() {
			return cursor != 0;
		}

		@Override
		public T previous() {
			checkForComodification();
			try {
				int i = cursor - 1;
				T previous = get(i);
				lastRet = cursor = i;
				return previous;
			} catch (IndexOutOfBoundsException e) {
				checkForComodification();
				throw new NoSuchElementException();
			}
		}

		@Override
		public int nextIndex() {
			return cursor;
		}

		@Override
		public int previousIndex() {
			return cursor - 1;
		}

		@Override
		public void set(T e) {
			if (lastRet < 0)
				throw new IllegalStateException();
			checkForComodification();

			try {
				ObservableList.this.set(lastRet, e);
				expectedModCount = modCount.intValue();
			} catch (IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}

		@Override
		public void add(T e) {
			checkForComodification();

			try {
				int i = cursor;
				ObservableList.this.add(i, e);
				lastRet = -1;
				cursor = i + 1;
				expectedModCount = modCount.intValue();
			} catch (IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}
	}

	/**
	 * Return a view of this list that will throw an exception if you attempt to
	 * modify it.
	 */
	public ObservableList<T> getUnmodifiableView() {
		return new ObservableList<>(data, listenerManager, modCount,
				uncaughtExceptionHandler, false);
	}

	/**
	 * Create a ListModel/ComboBoxModel that mirrors that data in this list but
	 * is only updated in the event dispatch thread.
	 * 
	 * @param filter
	 *            an optional filter to apply.
	 */
	public UIMirror<T> createUIMirror(ListFilter<T> filter) {
		return new UIMirror<T>(this, filter);
	}

	/**
	 * Create ListModel/ComboBoxModel that directly accesses this list. This
	 * should only be used if this ObservableList is only modified on the event
	 * dispatch thread.
	 */
	public UIView<T> createUIView() {
		return new UIView<T>(this);
	}

	/**
	 * Replace the contents of this list with another list.
	 * 
	 * @param newContents
	 *            the new elements to apply.
	 * @return true if this call changed the contents of this list.
	 */
	public boolean setAll(T[] newContents) {
		return setAll(Arrays.asList(newContents));
	}

	/**
	 * Replace the contents of this list with another list.
	 * 
	 * @param newContents
	 *            the new elements to apply.
	 * @return true if this call changed the contents of this list.
	 */
	public boolean setAll(Collection<T> newContents) {
		acquireWriteLock(true);
		try {
			if (listenerManager.containsListListener()) {
				int mySize = size();
				int otherSize = newContents.size();

				if (mySize == 0 && otherSize == 0) {
					return false;
				} else if (mySize == 0) {
					addAll(newContents);
					return true;
				} else if (otherSize == 0) {
					clear();
					return true;
				} else if (mySize == otherSize) {
					if (equals(newContents))
						return false;

					replaceWithSetElementOperation: {
						int diffIndex = -1;
						T newElement = null;
						Iterator<T> iter1 = newContents.iterator();
						Iterator<T> iter2 = iterator();
						int index = 0;
						while (iter1.hasNext()) {
							T e1 = iter1.next();
							T e2 = iter2.next();
							if (!Objects.equals(e1, e2)) {
								if (diffIndex == -1) {
									diffIndex = index;
									newElement = e1;
								} else {
									break replaceWithSetElementOperation;
								}
							}
							index++;
						}

						if (diffIndex != -1) {
							set(diffIndex, newElement);
							return true;
						}
					}
				} else if (mySize < otherSize) {
					replaceWithAddOperation: {
						// TODO: implement this optimization
					}
				} else if (mySize > otherSize) {
					replaceWithRemoveOperation: {
						// TODO: implement this optimization
					}
				}
			}

			ReplaceAllOperation op = new ReplaceAllOperation(newContents);
			T returnValue = (T) op.execute();
			op.notifyListeners();

			return true;
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public int hashCode() {
		acquireReadLock();
		try {
			return data.hashCode();
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof List))
			return false;
		acquireReadLock();
		try {
			return data.equals((List) obj);
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public ObservableList<T> clone() {
		acquireReadLock();
		try {
			ObservableList<T> x = new ObservableList<>();
			x.addAll(data);
			return x;
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public String toString() {
		return toString(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	/**
	 * Create a String describing this list.
	 * <p>
	 * This method includes optional controls to truncate the return value. For
	 * example, if a list can get very long you may want to return
	 * "[A, B, C, …]"
	 * 
	 * @param maxStringLength
	 *            the maximum String length to allow before truncation.
	 * @param elementMax
	 *            the maximum number of elements to display before truncation.
	 * @return a String representation of this list.
	 */
	public String toString(int maxStringLength, int elementMax) {
		acquireReadLock();
		try {
			StringBuilder sb = new StringBuilder(size() * 3);
			sb.append("[");
			Iterator<T> iter = data.iterator();
			int index = 0;
			while (iter.hasNext()) {
				T element = iter.next();
				if (index > 0) {
					sb.append(", ");
				}
				sb.append(String.valueOf(element));
				index++;
				if (index > elementMax || sb.length() > maxStringLength) {
					sb.append("…");
					break;
				}
			}
			sb.append("]");
			return sb.toString();
		} finally {
			readLock.unlock();
		}
	}
}