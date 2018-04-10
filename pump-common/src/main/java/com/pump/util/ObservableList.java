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
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.ComboBoxModel;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.pump.blog.Blurb;

/**
 * This wraps an existing <code>java.util.List</code> in an observable
 * structure.
 * <p>
 * This is primarily intended for UI development, so the existing
 * <code>java.awt.event.ListDataListener</code> interface is used to notify
 * observers about changes to this list. (Debatably a better observer interface
 * could be developed, but at least for the time being this doesn't seem
 * necessary.)
 * <p>
 * This object distinguishes between two types of listeners:
 * <ul>
 * <li>A synchronized listener (see
 * {@link #addSynchronizedListener(ListDataListener, boolean)}) will be notified
 * in a way that guarantees the list has not been modified since the
 * <code>ListDataEvent</code> occurred. That is: neither this listener nor any
 * preceding synchronized listeners can modify this list.</li>
 * <li>An unsynchronized listener (see
 * {@link #addUnsynchronizedListener(ListDataListener, boolean, boolean)}) has
 * the option to modify this list. And because any other preceding
 * unsynchronized listener may have already modified this listener: it is not
 * safe for an unsynchronized listener to assume the incoming
 * <code>ListDataEvent</code> still contains valid indices.</li>
 * </ul>
 * <p>
 * The level of detail used to calculate <code>ListDataEvents</code> will vary,
 * depending on whether any attached listener asked for a high level of detail.
 * Often in UIs a developer effectively needs a <code>ChangeListener</code> to
 * indicate something (anything!) changed: if that is all you're interested in,
 * then you can save some computational effort by not receiving highly detailed
 * events. Many operations (such as {@link #set(int, Object)} or
 * {@link #remove(int)}) always return precise events because they are trivial
 * to compute, but set operations like {@link #retainAll(Collection)} or
 * {@link #setAll(List)} can be expensive for large lists: in these methods the
 * level of detail you asked for is taken into consideration.
 * <h3>Threading and Synchronization</h3>
 * <p>
 * This list is designed to support access from multiple threads. Any read-like
 * operation ({@link #get(int)}, {@link #indexOf(Object)}, or even
 * {@link #containsAll(Collection)}) can run simultaneously with other read
 * operations. However write-like operations ({@link #set(int, Object)},
 * {@link #clear()}, {@link #setAll(List)}) are mutually exclusive. Even read
 * operations are not allowed during <i>part</i> of a write operation. (However
 * once the operation itself is completed -- but before all listeners have
 * completed -- read operations are allowed.)
 * <p>
 * Because this list uses its own model of synchronization, it is not necessary
 * for the underlying delegate list to be synchronized.
 * <p>
 * Note however that if you bypass this <code>ObservableList</code>, then you
 * have unsynchronized and unobserved changes:
 * <p>
 * <blockquote>
 * 
 * <pre>
 * ArrayList myList = new ArrayList();
 * ObservableList myObservableList = new ObservableList(myList);
 * myList.addAll(...);
 * </pre>
 * 
 * </blockquote>
 * <H3><code>ListModels</code></h3>
 * <p>
 * Also for further convenience this object has two methods related to
 * <code>ListModels</code>:
 * <ul>
 * <li>{@link #getListModelEDTMirror()}: this creates a separate list mirroring
 * this list that is only ever updated in the event dispatch thread. Pro's:
 * thread-safe. Con's: potentially expensive redundancy, and there will be
 * (brief) times when the EDT still refers to elements you already removed.</li>
 * <li>{@link #getListModelView(boolean)}: this provides a real-time view of
 * this list as a <code>ListModel</code>. Pro's: very light weight. Con's: if
 * used in a <code>JList</code>, then this <code>ObservableList</code> should
 * only ever be updated in the EDT.</li>
 * </ul>
 * <h3>Implementation Details</h3>
 * <p>
 * This class implements the <code>RandomAccess</code> interfaces, because most
 * of the time this list is expected to delegate to list that implements
 * <code>RandomAccess</code>, like the <code>java.util.ArrayList</code>. (The
 * <code>ArrayList</code> is the default list model inside this list.) In the
 * future if this becomes a questionable assumption we might introduce factory
 * methods to create the appropriate <code>ObservableList</code> subclass.
 */
@Blurb(title = "Lists: An Observable List for UIs", releaseDate = "July 2012", summary = "This article presents a <code>java.util.List</code> implementation designed with "
		+ "UI interaction in mind. This includes thread safety, efficiency, and listeners.", article = "http://javagraphics.blogspot.com/2012/07/lists-observable-list-for-uis.html")
public class ObservableList<T> extends AbstractList<T> implements RandomAccess {

	public abstract static class Filter<T> {
		public abstract boolean accept(T t);
	}

	/**
	 * Enough permits for an arbitrarily large number of threads. (If you have
	 * over 100,000 threads: this data structure is the least of your
	 * problems... it's OK for the other threads to wait for permits in that
	 * situation...)
	 */
	private static int PERMIT_MAX = 100000;

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

	}

	/**
	 * Receive ListDataEvents and broadcast a ChangeEvent to a ChangeListener.
	 */
	private static class ListDataChangeListener implements ListDataListener {
		ChangeListener changeListener;

		ListDataChangeListener(ChangeListener l) {
			changeListener = l;
		}

		public void contentsChanged(ListDataEvent e) {
			changeListener.stateChanged(new ChangeEvent(e.getSource()));
		}

		public void intervalAdded(ListDataEvent e) {
			contentsChanged(e);
		}

		public void intervalRemoved(ListDataEvent e) {
			contentsChanged(e);
		}

	}

	protected static class Listener {
		final ListDataListener listener;
		final boolean modificationAllowed;
		final boolean detailedEvents;

		Listener(ListDataListener l, boolean calculateDetailedEvents,
				boolean allowModification) {
			listener = l;
			detailedEvents = calculateDetailedEvents;
			modificationAllowed = allowModification;
		}
	}

	protected final List<T> list;
	protected final ReentrantLock writeLock = new ReentrantLock();
	protected final Semaphore readSemaphore = new Semaphore(PERMIT_MAX);
	protected final ThreadedSemaphore listenerSyncedSemaphore = new ThreadedSemaphore(
			PERMIT_MAX);
	protected final ThreadedSemaphore listenerUnsyncedSemaphore = new ThreadedSemaphore(
			PERMIT_MAX);

	protected final List<Listener> synchronizedListeners = new ArrayList<Listener>();
	protected final List<Listener> unsynchronizedListeners = new ArrayList<Listener>();
	protected final Set<Thread> readOnlyAccess = new HashSet<Thread>();
	private boolean calculateDetailedEvents = false;

	/**
	 * Create a ObservableList using an <code>ArrayList</code> as its
	 * implementation.
	 */
	public ObservableList() {
		this(new ArrayList<T>());
	}

	/**
	 * Create a ObservableList using an <code>ArrayList</code> as its
	 * implementation with the elements provided.
	 */
	public ObservableList(T... elements) {
		this(new ArrayList<T>());
		addAll(elements);
	}

	/**
	 * Create a ObservableList using the argument provided.
	 */
	public ObservableList(List<T> list) {
		this.list = list;
	}

	// // methods related to the listeners:

	/**
	 * Adds a <code>ListDataListener</code> that will be notified of changes to
	 * this list in a synchronized lock. This lock guarantees that no other
	 * thread will alter the contents of this list until this listener returns
	 * control of the thread.
	 * <p>
	 * For example: if this list received an addition then the listener's
	 * <code>intervalAdded()</code> method will be notified. Other threads may
	 * simultaneously poll this list (by calling {@link #get(int)} or
	 * {@link #size()} or even {@link #containsAll(Collection)}), but no other
	 * thread can modify the contents of this list (for example by calling
	 * {@link #remove(int)} or {@link #clear()}) until this listener is finished
	 * processing the event.
	 * <p>
	 * Because of this lock: it is important that synchronized listeners be
	 * relatively lightweight. If this listener can trigger a cascade of other
	 * listeners and complex operations: it should either:
	 * <ul>
	 * <li>Invoke those complicated operations later (in a separate thread, or
	 * by calling <code>SwingUtilities.invokeLater(Runnable)</code>), or</li>
	 * <li>This listener should be an unsynchronized listener.</li>
	 * </ul>
	 * <p>
	 * Because there might be other <code>ListDataListeners</code> waiting to be
	 * notified when this listener returns, and because those listeners are also
	 * guaranteed that the list will exactly reflect the
	 * <code>ListDataEvent</code> they are receiving: this listener is not
	 * allowed to further modify this list. If this listener tries to modify
	 * this list (directly or indirectly) a
	 * <code>RecursiveListenerModificationException</code> will be thrown.
	 * 
	 * @param listener
	 *            the listener to add to this list.
	 * @param detailedEvents
	 *            if true then the <code>ListDataEvents</code> all listeners
	 *            receive will be as detailed as possible. For example: when a
	 *            complicated operation is performed (like
	 *            {@link #retainAll(Collection)} or {@link #setAll(List)})
	 *            calculating a "detailed event" involves inspecting the before
	 *            and after states of this list, and after reviewing 1,000,000
	 *            elements: it might be clear that the event the appropriate
	 *            event is just a <code>ListDataEvent.INTERVAL_REMOVED</code>
	 *            event of 2 elements. However if no listeners are interested in
	 *            detailed events, then the same operation might notify
	 *            listeners of a <code>ListDataEvent.CONTENTS_CHANGED</code>
	 *            event ranging from elements [0, 1000000]. If you just want to
	 *            know when something (anything) changed: then
	 *            <code>detailedEvents</code> should be false for performance.
	 *            But if you want to know more specific information, this
	 *            argument should be <code>true</code>.
	 * 
	 * @see #removeSynchronizedListener(ListDataListener)
	 * @see #addUnsynchronizedListener(ListDataListener, boolean, boolean)
	 * 
	 * @param listener
	 *            the listener to add to this list.
	 */
	public void addSynchronizedListener(ListDataListener listener,
			boolean detailedEvents) {
		int requiredPermits = PERMIT_MAX
				- listenerSyncedSemaphore.getUsedPermitsForCurrentThread();
		listenerSyncedSemaphore.acquireUninterruptibly(requiredPermits);
		try {
			if (detailedEvents)
				calculateDetailedEvents = true;
			Listener l = new Listener(listener, detailedEvents, false);
			synchronizedListeners.add(l);
		} finally {
			listenerSyncedSemaphore.release(requiredPermits);
		}
	}

	/**
	 * Remove this synchronized listener from this list.
	 *
	 * @see #addSynchronizedListener(ListDataListener, boolean)
	 */
	public boolean removeSynchronizedListener(ListDataListener listener) {
		int requiredPermits = PERMIT_MAX
				- listenerSyncedSemaphore.getUsedPermitsForCurrentThread();
		listenerSyncedSemaphore.acquireUninterruptibly(requiredPermits);
		try {
			for (int a = 0; a < synchronizedListeners.size(); a++) {
				Listener l = synchronizedListeners.get(a);
				if (l.listener == listener) {
					synchronizedListeners.remove(a);
					if (l.detailedEvents)
						updateCalculateDetailedEvents();
					return true;
				}
			}
			return false;
		} finally {
			listenerSyncedSemaphore.release(requiredPermits);
		}
	}

	/**
	 * Add an unsynchronized <code>ListDataListener</code> that will be notified
	 * of changes to this list in an unprotected thread.
	 * <p>
	 * For example: if an addition is made to this list, then the listener's
	 * <code>intervalAdded()</code> method is called. However during this
	 * method: another thread might call <code>list.clear()</code>, so the
	 * <code>ListDataEvent</code> the listener received may not contain specific
	 * reliable data anymore.
	 * <p>
	 * This method should be used if the listener is not especially interested
	 * in specifics, or may be very expensive and cannot risk blocking further
	 * modifications for a long period of time.
	 * 
	 * @param listener
	 *            the listener to add to this list.
	 * @param detailedEvents
	 *            if true then the <code>ListDataEvents</code> all listeners
	 *            receive will be as detailed as possible. For example: when a
	 *            complicated operation is performed (like
	 *            {@link #retainAll(Collection)} or {@link #setAll(List)})
	 *            calculating a "detailed event" involves inspecting the before
	 *            and after states of this list, and after reviewing 1,000,000
	 *            elements: it might be clear that the event the appropriate
	 *            event is just a <code>ListDataEvent.INTERVAL_REMOVED</code>
	 *            event of 2 elements. However if no listeners are interested in
	 *            detailed events, then the same operation might notify
	 *            listeners of a <code>ListDataEvent.CONTENTS_CHANGED</code>
	 *            event ranging from elements [0, 1000000]. If you just want to
	 *            know when something (anything) changed: then
	 *            <code>detailedEvents</code> should be false for performance.
	 *            But if you want to know more specific information, this
	 *            argument should be <code>true</code>.
	 * @param allowsModification
	 *            if this is false then and if this listener tries to further
	 *            modify this list: a
	 *            <code>RecursiveListenerModificationException</code> will be
	 *            thrown. This is largely intended as a safeguard for developers
	 *            to prevent against unintended looping/cascading listeners.
	 * 
	 * @see #removeUnsynchronizedListener(ListDataListener)
	 * @see #addSynchronizedListener(ListDataListener, boolean)
	 */
	public void addUnsynchronizedListener(ListDataListener listener,
			boolean detailedEvents, boolean allowsModification) {
		int requiredPermits = PERMIT_MAX
				- listenerUnsyncedSemaphore.getUsedPermitsForCurrentThread();
		listenerUnsyncedSemaphore.acquireUninterruptibly(requiredPermits);
		try {
			if (detailedEvents)
				calculateDetailedEvents = true;
			Listener l = new Listener(listener, detailedEvents,
					allowsModification);
			unsynchronizedListeners.add(l);
		} finally {
			listenerUnsyncedSemaphore.release(requiredPermits);
		}
	}

	/**
	 * Adds a <code>ChangeListener</code> that will be notified of changes to
	 * this list in a synchronized lock. This lock guarantees that no other
	 * thread will alter the contents of this list until this listener returns
	 * control of the thread.
	 * 
	 * @param changeListener
	 *            the listener to add to this list.
	 * 
	 * @see #removeSynchronizedChangeListener(ChangeListener)
	 * @see #addSynchronizedListener(ListDataListener, boolean)
	 * 
	 * @param changeListener
	 *            the listener to add to this list.
	 */
	public void addSynchronizedChangeListener(ChangeListener changeListener) {
		addSynchronizedListener(new ListDataChangeListener(changeListener),
				false);
	}

	/**
	 * Add an unsynchronized <code>ChangeListener</code> that will be notified
	 * of changes to this list in an unprotected thread.
	 * 
	 * @param changeListener
	 *            the listener to add to this list.
	 * @param allowsModification
	 *            if this is false then and if this listener tries to further
	 *            modify this list: a
	 *            <code>RecursiveListenerModificationException</code> will be
	 *            thrown. This is largely intended as a safeguard for developers
	 *            to prevent against unintended looping/cascading listeners.
	 * 
	 * @see #removeUnsynchronizedChangeListener(ChangeListener)
	 * @see #addUnsynchronizedListener(ListDataListener, boolean, boolean)
	 */
	public void addUnsynchronizedChangeListener(ChangeListener changeListener,
			boolean allowsModification) {
		addUnsynchronizedListener(new ListDataChangeListener(changeListener),
				false, allowsModification);
	}

	/**
	 * Remove this unsynchronized listener from this list.
	 *
	 * @see #addUnsynchronizedChangeListener(ChangeListener,boolean)
	 */
	public boolean removeUnsynchronizedChangeListener(
			ChangeListener changeListener) {
		int requiredPermits = PERMIT_MAX
				- listenerUnsyncedSemaphore.getUsedPermitsForCurrentThread();
		listenerUnsyncedSemaphore.acquireUninterruptibly(requiredPermits);
		try {
			for (int a = 0; a < unsynchronizedListeners.size(); a++) {
				Listener l = unsynchronizedListeners.get(a);
				if (l.listener instanceof ListDataChangeListener) {
					ListDataChangeListener ldcl = (ListDataChangeListener) l.listener;
					if (ldcl.changeListener == changeListener) {
						return removeUnsynchronizedListener(ldcl);
					}
				}
			}
			return false;
		} finally {
			listenerUnsyncedSemaphore.release(requiredPermits);
		}
	}

	/**
	 * Remove this synchronized listener from this list.
	 *
	 * @see #addSynchronizedChangeListener(ChangeListener)
	 */
	public boolean removeSynchronizedChangeListener(
			ChangeListener changeListener) {
		int requiredPermits = PERMIT_MAX
				- listenerSyncedSemaphore.getUsedPermitsForCurrentThread();
		listenerSyncedSemaphore.acquireUninterruptibly(requiredPermits);
		try {
			for (int a = 0; a < synchronizedListeners.size(); a++) {
				Listener l = synchronizedListeners.get(a);
				if (l.listener instanceof ListDataChangeListener) {
					ListDataChangeListener ldcl = (ListDataChangeListener) l.listener;
					if (ldcl.changeListener == changeListener) {
						return removeSynchronizedListener(ldcl);
					}
				}
			}
			return false;
		} finally {
			listenerSyncedSemaphore.release(requiredPermits);
		}
	}

	/**
	 * Remove this unsynchronized listener from this list.
	 *
	 * @see #addUnsynchronizedListener(ListDataListener, boolean, boolean)
	 */
	public boolean removeUnsynchronizedListener(ListDataListener listener) {
		int requiredPermits = PERMIT_MAX
				- listenerUnsyncedSemaphore.getUsedPermitsForCurrentThread();
		listenerUnsyncedSemaphore.acquireUninterruptibly(requiredPermits);
		try {
			for (int a = 0; a < unsynchronizedListeners.size(); a++) {
				Listener l = unsynchronizedListeners.get(a);
				if (l.listener == listener) {
					unsynchronizedListeners.remove(a);
					if (l.detailedEvents)
						updateCalculateDetailedEvents();
					return true;
				}
			}
			return false;
		} finally {
			listenerUnsyncedSemaphore.release(requiredPermits);
		}
	}

	private void updateCalculateDetailedEvents() {
		boolean newValue = false;
		for (int a = 0; a < unsynchronizedListeners.size() && (!newValue); a++) {
			Listener l = unsynchronizedListeners.get(a);
			if (l.detailedEvents)
				newValue = true;
		}
		for (int a = 0; a < synchronizedListeners.size() && (!newValue); a++) {
			Listener l = synchronizedListeners.get(a);
			if (l.detailedEvents)
				newValue = true;
		}
		calculateDetailedEvents = newValue;
	}

	protected void fireEvent(List<Listener> listeners,
			ThreadedSemaphore semaphore, ListDataEvent event) {
		if (event == null)
			return;

		semaphore.acquireUninterruptibly();
		try {
			if (listeners.size() == 0)
				return;

			for (int a = 0; a < listeners.size(); a++) {
				Listener l = listeners.get(a);
				Thread restrictedThread = null;
				if (l.modificationAllowed == false) {
					restrictedThread = Thread.currentThread();
				}
				if (restrictedThread != null)
					readOnlyAccess.add(restrictedThread);

				try {
					int type = event.getType();
					switch (type) {
					case ListDataEvent.CONTENTS_CHANGED:
						l.listener.contentsChanged(event);
						break;
					case ListDataEvent.INTERVAL_ADDED:
						l.listener.intervalAdded(event);
						break;
					case ListDataEvent.INTERVAL_REMOVED:
						l.listener.intervalRemoved(event);
						break;
					default:
						throw new IllegalArgumentException(
								"unexpected event type: " + type);
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				} finally {
					if (restrictedThread != null)
						readOnlyAccess.remove(restrictedThread);
				}
			}
		} finally {
			semaphore.release();
		}
	}

	// // methods that modify this list:

	static abstract class Operation<R> {
		/**
		 * This method is invoked safely in a read-only state, which the caller
		 * will stay in until <code>process()</code> or <code>nullOp()</code> is
		 * subsequently called.
		 * 
		 * @returns a ListDataEvent to describe this operation, or null if this
		 *          operation is a null-op and should be skipped. If this
		 *          returns null: then <code>nullOp()</code> is invoked.
		 *          Otherwise we acquire write-level access to this list and
		 *          invoke <code>process()</code>.
		 */
		abstract ListDataEvent preProcess();

		/**
		 * Actually execute the operation. This is invoked while we have
		 * write-level access to the list. This should implement the
		 * ListDataEvent described by the <code>preProcess()</code> method.
		 * <p>
		 * Note this class does not worry about bounds checking, because the
		 * underlying list will double-check that separately. (So a
		 * RuntimeException may occur here that will prevent the list from being
		 * changed and listeners from being modified.)
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

	/**
	 * This method executes all Operations, keeping thread safety and listeners
	 * in mind.
	 * 
	 * @param <R>
	 *            the parameterized type of the operation
	 * @param operation
	 *            the operation to execute
	 * @return the return value of this operation
	 */
	protected <R> R execute(Operation<R> operation) {
		ListDataEvent event = null;

		/*
		 * To edit the list we want PERMIT_MAX-many permits. But first, we want
		 * to obtain just 1 permit to read it, and evaluate if we even have to
		 * make a change at all
		 */
		int requiredPermits = PERMIT_MAX;

		writeLock.lock();
		readSemaphore.acquireUninterruptibly();
		R returnValue;
		try {
			event = operation.preProcess();
			if (event != null) {

				Thread thread = Thread.currentThread();
				if (readOnlyAccess.contains(thread))
					throw new RecursiveListenerModificationException();

				/*
				 * Earlier just acquired 1 permit to read the list. Now we want
				 * to soak up the remaining permits to modify the list:
				 */
				int writePermits = requiredPermits - 1;
				try {
					readSemaphore.acquireUninterruptibly(writePermits);
					returnValue = operation.process();
				} finally {
					readSemaphore.release(writePermits);
				}
			} else {
				returnValue = operation.nullOp();
			}
			fireEvent(synchronizedListeners, listenerSyncedSemaphore, event);
		} finally {
			/*
			 * Release the final permit, so now other threads can modify this
			 * list.
			 */
			readSemaphore.release();
			writeLock.unlock();
		}
		fireEvent(unsynchronizedListeners, listenerUnsyncedSemaphore, event);
		return returnValue;
	}

	class AddOperation extends Operation<Boolean> {
		final T newElement;
		Integer index;

		/**
		 * @param newElement
		 * @param index
		 *            if null: then the element is appended to the end of this
		 *            list.
		 */
		AddOperation(T newElement, Integer index) {
			this.newElement = newElement;
			this.index = index;
		}

		@Override
		ListDataEvent preProcess() {
			if (index == null) {
				index = list.size();
			}
			return new ListDataEvent(ObservableList.this,
					ListDataEvent.INTERVAL_ADDED, index, index);
		}

		@Override
		Boolean process() {
			list.add(index, newElement);
			return true;
		}

		@Override
		Boolean nullOp() {
			return false;
		}
	}

	class AddAllOperation extends Operation<Boolean> {
		final Collection<? extends T> newCollection;
		Integer index;

		/**
		 * @param index
		 *            if null: then the elements are appended to the end of this
		 *            list.
		 */
		AddAllOperation(Collection<? extends T> newCollection, Integer index) {
			this.newCollection = newCollection;
			this.index = index;
		}

		@Override
		ListDataEvent preProcess() {
			int size = newCollection.size();
			if (size == 0)
				return null;

			if (index == null) {
				index = list.size();
			}
			return new ListDataEvent(ObservableList.this,
					ListDataEvent.INTERVAL_ADDED, index, index + size - 1);
		}

		@Override
		Boolean process() {
			list.addAll(index, newCollection);
			return true;
		}

		@Override
		Boolean nullOp() {
			return false;
		}
	}

	class RemoveRangeOperation extends Operation<Boolean> {
		final Integer index0;
		final Integer index1;

		private int min, max;

		/** Used to remove all */
		RemoveRangeOperation() {
			this.index0 = null;
			this.index1 = null;
		}

		/** Used to remove a fixed range. */
		RemoveRangeOperation(Integer index0, Integer index1) {
			this.index0 = index0;
			this.index1 = index1;

			if (index0 == null)
				throw new NullPointerException();
			if (index1 == null)
				throw new NullPointerException();
		}

		@Override
		ListDataEvent preProcess() {
			if (index0 == null) {
				int size = list.size();
				if (size == 0)
					return null;

				min = 0;
				max = size - 1;
			} else {
				max = Math.max(index1, index0);
				min = Math.min(index1, index0);
			}
			return new ListDataEvent(ObservableList.this,
					ListDataEvent.INTERVAL_REMOVED, min, max);
		}

		@Override
		Boolean process() {
			if (min == 0 && max == list.size() - 1) {
				list.clear();
			} else {
				for (int index = max; index >= min; index--) {
					list.remove(index);
				}
			}
			return true;
		}

		@Override
		Boolean nullOp() {
			return false;
		}
	}

	class RemoveElementOperation extends Operation<Boolean> {
		final Object elementToRemove;
		private int index;

		RemoveElementOperation(Object elementToRemove) {
			this.elementToRemove = elementToRemove;
		}

		@Override
		ListDataEvent preProcess() {
			index = list.indexOf(elementToRemove);
			if (index == -1)
				return null;
			return new ListDataEvent(ObservableList.this,
					ListDataEvent.INTERVAL_REMOVED, index, index);
		}

		@Override
		Boolean process() {
			list.remove(index);
			return true;
		}

		@Override
		Boolean nullOp() {
			return false;
		}
	}

	class RemoveAllOperation extends Operation<Boolean> {
		final Collection<?> collection;

		RemoveAllOperation(Collection<?> collection) {
			this.collection = collection;
		}

		@Override
		ListDataEvent preProcess() {
			if (calculateDetailedEvents) {
				int elementsRemoved = 0;
				int minIndex = Integer.MAX_VALUE;
				int maxIndex = -1;
				Iterator<?> iter = collection.iterator();
				while (iter.hasNext()) {
					Object element = iter.next();
					int index = list.indexOf(element);
					if (index != -1) {
						elementsRemoved++;
						minIndex = Math.min(minIndex, index);
						maxIndex = Math.max(maxIndex, index);
					}
				}
				if (elementsRemoved == 0)
					return null;
				if (elementsRemoved == maxIndex - minIndex + 1) {
					return new ListDataEvent(ObservableList.this,
							ListDataEvent.INTERVAL_REMOVED, minIndex, maxIndex);
				}
				return new ListDataEvent(ObservableList.this,
						ListDataEvent.CONTENTS_CHANGED, minIndex, maxIndex);
			}

			// no detailed event:
			int oldSize = list.size();
			return new ListDataEvent(ObservableList.this,
					ListDataEvent.CONTENTS_CHANGED, 0, oldSize);
		}

		@Override
		Boolean process() {
			// this is the rare case where process() may still
			// return false if we didn't carefully construct
			// an event in preprocess():
			return list.removeAll(collection);
		}

		@Override
		Boolean nullOp() {
			return false;
		}
	}

	class RetainAllOperation extends Operation<Boolean> {
		final Collection<?> collection;

		RetainAllOperation(Collection<?> collection) {
			this.collection = collection;
		}

		@Override
		ListDataEvent preProcess() {
			if (calculateDetailedEvents) {
				int elementsKept = 0;
				int minKeptIndex = Integer.MAX_VALUE;
				int maxKeptIndex = -1;
				int minSkippedIndex = Integer.MAX_VALUE;
				int maxSkippedIndex = -1;
				int size = list.size();
				for (int a = 0; a < size; a++) {
					Object element = list.get(a);
					if (collection.contains(element)) {
						elementsKept++;
						minKeptIndex = Math.min(minKeptIndex, a);
						maxKeptIndex = Math.max(maxKeptIndex, a);
					} else {
						minSkippedIndex = Math.min(minSkippedIndex, a);
						maxSkippedIndex = Math.max(maxSkippedIndex, a);
					}
				}
				if (elementsKept == list.size())
					return null;

				if (elementsKept == maxKeptIndex - minKeptIndex + 1) {
					// we kept a series of consecutive elements
					if (minKeptIndex == 0) {
						return new ListDataEvent(ObservableList.this,
								ListDataEvent.INTERVAL_REMOVED,
								maxKeptIndex + 1, size - 1);
					} else if (maxKeptIndex == size - 1) {
						return new ListDataEvent(ObservableList.this,
								ListDataEvent.INTERVAL_REMOVED, 0,
								minKeptIndex - 1);
					}
				}

				if (minKeptIndex == 0) {
					return new ListDataEvent(ObservableList.this,
							ListDataEvent.CONTENTS_CHANGED, minSkippedIndex,
							size - 1);
				} else if (maxKeptIndex == size - 1) {
					return new ListDataEvent(ObservableList.this,
							ListDataEvent.CONTENTS_CHANGED, 0, maxSkippedIndex);
				}
				return new ListDataEvent(ObservableList.this,
						ListDataEvent.CONTENTS_CHANGED, 0, size);
			}
			int oldSize = list.size();
			return new ListDataEvent(ObservableList.this,
					ListDataEvent.CONTENTS_CHANGED, 0, oldSize);
		}

		@Override
		Boolean process() {
			// like removeAll(): this is one of the rare
			// cases where process() can return false.
			return list.retainAll(collection);
		}

		@Override
		Boolean nullOp() {
			return false;
		}
	}

	private static boolean isEqual(Object t1, Object t2) {
		if (t1 == null && t2 == null)
			return true;
		if (t1 == null || t2 == null)
			return false;
		return t1.equals(t2);
	}

	class SetAllOperation extends Operation<Boolean> {
		final List<T> replacementList;

		SetAllOperation(List<T> list) {
			this.replacementList = list;
		}

		@Override
		ListDataEvent preProcess() {
			ListDataEvent event = null;
			int mySize = list.size();
			int otherSize = replacementList.size();
			if (!calculateDetailedEvents && mySize != otherSize) {
				// go ahead and always do *some* calculation if the sizes match
				// to make sure an event is necessary. That is:
				// the method setAll() returns a boolean,
				// and we should at least guarantee that boolean is correct.

				event = new ListDataEvent(ObservableList.this,
						ListDataEvent.CONTENTS_CHANGED, 0, mySize - 1);
			} else {
				int maxSize = Math.max(mySize, otherSize);
				int minSize = Math.min(mySize, otherSize);
				int sizeDiff = maxSize - minSize;

				Integer firstChange = null;
				for (int a = 0; a < maxSize && firstChange == null; a++) {
					Object myElement = a < mySize ? list.get(a) : null;
					Object otherElement = a < otherSize ? replacementList
							.get(a) : null;
					if (!isEqual(myElement, otherElement)) {
						firstChange = a;
					}
				}

				if (firstChange == null) {
					// we iterated over everything and found no differences:
					return null;
				} else if (firstChange == mySize && mySize < otherSize) {
					// if this list is [ a b c ], then the new incoming list is
					// [ a b c d e f ]
					event = new ListDataEvent(ObservableList.this,
							ListDataEvent.INTERVAL_ADDED, firstChange,
							otherSize - 1);
				} else if (firstChange == otherSize && otherSize < mySize) {
					// if this list is [ a b c d e f], then the new incoming
					// list is [ a b c ]
					event = new ListDataEvent(ObservableList.this,
							ListDataEvent.INTERVAL_REMOVED, firstChange,
							mySize - 1);
				} else if (!calculateDetailedEvents) {
					// we iterated over everything, found differences, but
					// shouldn't research anything further:
					event = new ListDataEvent(ObservableList.this,
							ListDataEvent.CONTENTS_CHANGED, firstChange,
							mySize - 1);
				} else {
					Integer lastChange = null; // relative to the current state
					for (int a = 0; a < maxSize && lastChange == null; a++) {
						Object myElement = a < mySize ? list
								.get(mySize - 1 - a) : null;
						Object otherElement = a < otherSize ? replacementList
								.get(otherSize - 1 - a) : null;
						if (!isEqual(myElement, otherElement)) {
							lastChange = mySize - 1 - a;
						}
					}

					if (lastChange < 0) {
						// this two lists end with exactly the same elements
						if (mySize > otherSize) {
							event = new ListDataEvent(ObservableList.this,
									ListDataEvent.INTERVAL_REMOVED, 0,
									sizeDiff - 1);
						} else if (otherSize > mySize) {
							event = new ListDataEvent(ObservableList.this,
									ListDataEvent.INTERVAL_ADDED, 0,
									sizeDiff - 1);
						}
					}
					if (event == null
							&& (lastChange - firstChange + 1) == sizeDiff
							&& mySize > otherSize) {
						event = new ListDataEvent(ObservableList.this,
								ListDataEvent.INTERVAL_REMOVED, firstChange,
								lastChange);
					} else if (event == null) {
						event = new ListDataEvent(ObservableList.this,
								ListDataEvent.CONTENTS_CHANGED, firstChange,
								lastChange);
					}
				}
			}
			return event;
		}

		Boolean process() {
			list.clear();
			list.addAll(replacementList);
			return true;
		}

		Boolean nullOp() {
			return false;
		}
	}

	class RemoveIndexOperation extends Operation<T> {
		final int index;
		T returnValue;

		RemoveIndexOperation(int index) {
			this.index = index;
		}

		@Override
		ListDataEvent preProcess() {
			return new ListDataEvent(ObservableList.this,
					ListDataEvent.INTERVAL_REMOVED, index, index);
		}

		@Override
		T process() {
			return list.remove(index);
		}

		/** This method will never be called. */
		@Override
		T nullOp() {
			return null;
		}
	}

	class SetIndexOperation extends Operation<T> {
		final int index;
		final T newElement;
		T returnValue;

		SetIndexOperation(int index, T newElement) {
			this.index = index;
			this.newElement = newElement;
		}

		@Override
		ListDataEvent preProcess() {
			returnValue = list.get(index);
			if (isEqual(newElement, returnValue)) {
				return null;
			}
			return new ListDataEvent(ObservableList.this,
					ListDataEvent.CONTENTS_CHANGED, index, index);
		}

		@Override
		T process() {
			list.set(index, newElement);
			return returnValue;
		}

		@Override
		T nullOp() {
			return returnValue;
		}
	}

	@Override
	public boolean add(T element) {
		return execute(new AddOperation(element, null));
	}

	@Override
	public void add(int index, T element) {
		execute(new AddOperation(element, index));
	}

	@Override
	public boolean addAll(Collection<? extends T> collection) {
		if (collection instanceof ObservableList)
			collection = ((ObservableList<? extends T>) collection).list;
		return execute(new AddAllOperation(collection, null));
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> collection) {
		if (collection instanceof ObservableList)
			collection = ((ObservableList<? extends T>) collection).list;
		return execute(new AddAllOperation(collection, index));
	}

	public boolean addAll(T... array) {
		return addAll(new ImmutableArrayWrapper<T>(array));
	}

	public boolean addAll(int index, T... array) {
		return addAll(index, new ImmutableArrayWrapper<T>(array));
	}

	@Override
	public void clear() {
		execute(new RemoveRangeOperation());
	}

	/**
	 * Remove a range of elements from this list.
	 * 
	 * @param index0
	 *            the first element to remove
	 * @param index1
	 *            the last element to remove
	 */
	@Override
	public void removeRange(int index0, int index1) {
		execute(new RemoveRangeOperation(index0, index1));
	}

	@Override
	public boolean remove(Object element) {
		return execute(new RemoveElementOperation(element));
	}

	@Override
	public T remove(int index) {
		return execute(new RemoveIndexOperation(index));
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean removeAll(Collection<?> collection) {
		if (collection instanceof ObservableList)
			collection = ((ObservableList<? extends T>) collection).list;
		if (collection == list) {
			if (!isEmpty()) {
				clear();
				return true;
			}
			return false;
		}
		return execute(new RemoveAllOperation(collection));
	}

	public boolean removeAll(Object... array) {
		return removeAll(new ImmutableArrayWrapper<Object>(array));
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		return execute(new RetainAllOperation(collection));
	}

	public boolean retainAll(Object... array) {
		return retainAll(new ImmutableArrayWrapper<Object>(array));
	}

	public boolean setAll(List<T> newList) {
		if (newList instanceof ObservableList)
			newList = ((ObservableList<T>) newList).list;
		if (newList == list) {
			return false;
		}
		return execute(new SetAllOperation(newList));
	}

	public boolean setAll(T... array) {
		return setAll(new ImmutableArrayWrapper<T>(array));
	}

	@Override
	public T set(int index, T newElement) {
		return execute(new SetIndexOperation(index, newElement));
	}

	// // methods that only read from this list (no modifications):

	@Override
	public T get(int index) {
		readSemaphore.acquireUninterruptibly();
		try {
			return list.get(index);
		} finally {
			readSemaphore.release();
		}
	}

	@Override
	public boolean contains(Object element) {
		readSemaphore.acquireUninterruptibly();
		try {
			return list.contains(element);
		} finally {
			readSemaphore.release();
		}
	}

	@Override
	public boolean containsAll(Collection<?> collection) {
		readSemaphore.acquireUninterruptibly();
		try {
			return list.containsAll(collection);
		} finally {
			readSemaphore.release();
		}
	}

	public boolean containsAll(Object... array) {
		return containsAll(new ImmutableArrayWrapper<Object>(array));
	}

	@Override
	public int indexOf(Object element) {
		readSemaphore.acquireUninterruptibly();
		try {
			return list.indexOf(element);
		} finally {
			readSemaphore.release();
		}
	}

	@Override
	public boolean isEmpty() {
		readSemaphore.acquireUninterruptibly();
		try {
			return list.isEmpty();
		} finally {
			readSemaphore.release();
		}
	}

	@Override
	public int lastIndexOf(Object element) {
		readSemaphore.acquireUninterruptibly();
		try {
			return list.lastIndexOf(element);
		} finally {
			readSemaphore.release();
		}
	}

	@Override
	public int size() {
		readSemaphore.acquireUninterruptibly();
		try {
			return list.size();
		} finally {
			readSemaphore.release();
		}
	}

	// // other methods:

	/**
	 * This returns a non-mutable <code>ListModel</code> mirror of this list
	 * that is guaranteed to only update in the event dispatch thread.
	 * <p>
	 * This is intended for <code>JLists</code>, or other UI elements.
	 * <p>
	 * Because this is intentionally multithreaded: it is likely that this
	 * mirror will briefly contain outdated elements. That is to say: removing
	 * an element from this list does not mean the UI is done interacting with
	 * it.
	 * <p>
	 * This mirror requires maintaining a separate copy of this list. Most
	 * references in Java are 8 bytes, so if you have a 1,024 element list: this
	 * list will be approximately 8KB, and each additional EDT mirror you
	 * construct will be another 8KB. This may be an acceptable amount
	 * (depending on how many of these lists you have), but if you have a
	 * 1,000,000-element list, then this list is about 8 MB, and each EDT mirror
	 * is another 8 MB. My point is: if this list may have several thousand
	 * elements, then this mirror is not trivial to construct and maintain.
	 * 
	 * @return a non-mutable <code>ListModel</code> mirror of this list that is
	 *         guaranteed to only update in the event dispatch thread.
	 * 
	 * @see #getListModelView(boolean)
	 */
	public EDTMirror<T> getListModelEDTMirror() {
		return new EDTMirror<T>(this, null);
	}

	/**
	 * This returns a non-mutable <code>ListModel</code> mirror of this list
	 * that is guaranteed to only update in the event dispatch thread.
	 * <p>
	 * This is intended for <code>JLists</code>, or other UI elements.
	 * <p>
	 * Because this is intentionally multithreaded: it is likely that this
	 * mirror will briefly contain outdated elements. That is to say: removing
	 * an element from this list does not mean the UI is done interacting with
	 * it.
	 * <p>
	 * This mirror requires maintaining a separate copy of this list. Most
	 * references in Java are 8 bytes, so if you have a 1,024 element list: this
	 * list will be approximately 8KB, and each additional EDT mirror you
	 * construct will be another 8KB. This may be an acceptable amount
	 * (depending on how many of these lists you have), but if you have a
	 * 1,000,000-element list, then this list is about 8 MB, and each EDT mirror
	 * is another 8 MB. My point is: if this list may have several thousand
	 * elements, then this mirror is not trivial to construct and maintain.
	 * 
	 * @return a non-mutable <code>ListModel</code> mirror of this list that is
	 *         guaranteed to only update in the event dispatch thread.
	 * 
	 * @param t
	 *            an optional filter that restricts what the mirrored list
	 *            contains.
	 * @see #getListModelView(boolean)
	 */
	public EDTMirror<T> getListModelEDTMirror(Filter<T> t) {
		return new EDTMirror<T>(this, t);
	}

	/**
	 * This returns a non-mutable <code>ListModel</code> view of this list. This
	 * <code>ListModel</code> notifies its listeners of changes exactly as they
	 * occur on the original thread.
	 * <p>
	 * This view is an interface that interacts directly with this list; it is
	 * <i>not</i> a copy of this list like {@link #getListModelEDTMirror()}. So
	 * if this list occupies 8 MB of memory: invoking this method will allocate
	 * less than 1 KB of memory.
	 * <p>
	 * The downside to this view is: it does not make any guarantees regarding
	 * the event dispatch thread. So if this new <code>ListModel</code> is being
	 * used in a <code>JList</code>: you need to take responsibility to only
	 * update this list in the event dispatch thread.
	 * <p>
	 * If the <code>ObservableList</code> implemented the <code>ListModel</code>
	 * interface: then this method wouldn't be unnecessary. (That is: the list
	 * model view and this list would be the same object.) The decision to not
	 * make this class a <code>ListModel</code> was a deliberate design choice
	 * to force developers to think about whether they needed a view or an
	 * EDT-specific mirror.
	 * 
	 * @see #getListModelEDTMirror()
	 */
	public ListModel getListModelView(boolean updateListenersSynchronously) {
		return new ListModelView(updateListenersSynchronously);
	}

	/**
	 * Creates a <code>ObservableList</code> with the same list structure, but
	 * with no listeners.
	 */
	@Override
	public Object clone() {
		readSemaphore.acquireUninterruptibly();
		try {
			List<T> returnValue = new ObservableList<T>();
			returnValue.addAll(list);
			return returnValue;
		} finally {
			readSemaphore.release();
		}
	}

	@Override
	public boolean equals(Object obj) {
		readSemaphore.acquireUninterruptibly();
		try {
			if (obj == this)
				return true;
			if (!(obj instanceof List))
				return false;
			List<?> otherList = (List<?>) obj;
			int size = list.size();
			if (otherList.size() != size)
				return false;

			for (int a = 0; a < size(); a++) {
				Object myElement = list.get(a);
				Object otherElement = otherList.get(a);
				if (myElement == null && otherElement == null) {
					// we're OK
				} else if (myElement == null || otherElement == null) {
					return false;
				} else if (!myElement.equals(otherElement)) {
					return false;
				}
			}
			return true;
		} finally {
			readSemaphore.release();
		}
	}

	@Override
	public int hashCode() {
		readSemaphore.acquireUninterruptibly();
		try {
			return list.hashCode();
		} finally {
			readSemaphore.release();
		}
	}

	@Override
	public String toString() {
		// When debugging this class it really helps to not
		// touch the readSemaphore... since a step-by-step
		// debugger may be mid-write-operation and still
		// want to invoke this.toString(). This condition
		// should be false in deployment, but may be true
		// during development/debugging.
		@SuppressWarnings("unused")
		Semaphore semaphore = false ? new Semaphore(1) : readSemaphore;

		semaphore.acquireUninterruptibly();
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("ObservableList[ ");
			int size = list.size();
			for (int a = 0; a < size; a++) {
				if (a != 0) {
					sb.append(", ");
				}
				sb.append(list.get(a));
				if ((a == 1000 && a + 1 < size) || // if we've printed over 1000
													// items ... OR
						(a > 5 && sb.length() > 2000)) { // if we've printed
															// over 2000
															// characters, and
															// expressed at
															// least 5 items
					int remaining = size - a - 1;
					sb.append(", << + " + remaining + " elements >> ]");
					return sb.toString();
				}
			}
			sb.append(" ]");
			return sb.toString();
		} finally {
			semaphore.release();
		}
	}

	class ListModelView implements ListModel<T> {

		Semaphore myListenerSemaphore = new Semaphore(PERMIT_MAX);

		ListDataListener listener = new ListDataListener() {

			public void contentsChanged(ListDataEvent e) {
				ListDataEvent newEvent = new ListDataEvent(ListModelView.this,
						e.getType(), e.getIndex0(), e.getIndex1());
				myListenerSemaphore.acquireUninterruptibly();
				try {
					for (int a = 0; a < listeners.size(); a++) {
						ListDataListener l = listeners.get(a);
						try {
							l.contentsChanged(newEvent);
						} catch (Exception e2) {
							e2.printStackTrace();
						}
					}
				} finally {
					myListenerSemaphore.release();
				}
			}

			public void intervalAdded(ListDataEvent e) {
				ListDataEvent newEvent = new ListDataEvent(ListModelView.this,
						e.getType(), e.getIndex0(), e.getIndex1());
				myListenerSemaphore.acquireUninterruptibly();
				try {
					for (int a = 0; a < listeners.size(); a++) {
						ListDataListener l = listeners.get(a);
						try {
							l.intervalAdded(newEvent);
						} catch (Exception e2) {
							e2.printStackTrace();
						}
					}
				} finally {
					myListenerSemaphore.release();
				}
			}

			public void intervalRemoved(ListDataEvent e) {
				ListDataEvent newEvent = new ListDataEvent(ListModelView.this,
						e.getType(), e.getIndex0(), e.getIndex1());
				myListenerSemaphore.acquireUninterruptibly();
				try {
					for (int a = 0; a < listeners.size(); a++) {
						ListDataListener l = listeners.get(a);
						try {
							l.intervalRemoved(newEvent);
						} catch (Exception e2) {
							e2.printStackTrace();
						}
					}
				} finally {
					myListenerSemaphore.release();
				}
			}
		};

		List<ListDataListener> listeners = new ArrayList<ListDataListener>();

		ListModelView(boolean updateListenersSynchronously) {
			if (updateListenersSynchronously) {
				ObservableList.this.addSynchronizedListener(listener, true);
			} else {
				ObservableList.this.addUnsynchronizedListener(listener, true,
						false);
			}
		}

		public void addListDataListener(ListDataListener l) {
			myListenerSemaphore.acquireUninterruptibly(PERMIT_MAX);
			try {
				listeners.add(l);
			} finally {
				myListenerSemaphore.release(PERMIT_MAX);
			}
		}

		public T getElementAt(int index) {
			return ObservableList.this.get(index);
		}

		public int getSize() {
			return ObservableList.this.size();
		}

		public void removeListDataListener(ListDataListener l) {
			myListenerSemaphore.acquireUninterruptibly(PERMIT_MAX);
			try {
				listeners.remove(l);
			} finally {
				myListenerSemaphore.release(PERMIT_MAX);
			}
		}
	}

	public static class EDTMirror<S> implements ComboBoxModel<S> {

		class UpdateEDTList implements Runnable {
			public void run() {
				if (edtListDirty) {
					edtListDirty = false;

					if (filter == null) {

						/*
						 * First we acquire read-level access to
						 * ObservableList.this and copy its data. Then (having
						 * released the read-level access): we can change the
						 * edtList.
						 * 
						 * Originally this method just called: edtList.setAll(
						 * ObservableList.this ); inside a read-level access,
						 * but this set off a cascade of listeners in T4L bug
						 * 22221 that eventually resulted in a write op to
						 * ObservableList.this. This op required write-level
						 * access, which could never be granted because this
						 * method held 1 semaphore permit. So that is why the
						 * redundant copy of the data is necessary.
						 * 
						 * In this example (bug 22221): there's still some
						 * sloppy listener activity that could be resolved, but
						 * the biggest crisis (a locked EDT) should be safely
						 * avoided.
						 */

						masterList.readSemaphore.acquireUninterruptibly();
						Object[] copy;
						try {
							// remember the list could be 10,000+ elements long,
							// so
							// each line here may take several seconds:
							copy = new Object[masterList.size()];
							if (edtListDirty)
								return;
							masterList.toArray(copy);
							if (edtListDirty)
								return;
						} finally {
							masterList.readSemaphore.release();
						}
						edtList.setAll((S[]) copy);
					} else {
						List<S> filteredElements = new ArrayList<>();
						masterList.readSemaphore.acquireUninterruptibly();
						try {
							for (int a = 0; a < masterList.size(); a++) {
								S e = masterList.get(a);
								if (filter.accept(e))
									filteredElements.add((e));
							}
						} finally {
							masterList.readSemaphore.release();
						}
						if (edtListDirty)
							return;
						edtList.setAll(filteredElements);
					}
				}
			}
		}

		boolean edtListDirty = false;

		/**
		 * This list is only modified in the EDT.
		 */
		ObservableList<S> edtList = new ObservableList<S>();

		ListDataListener masterListListener = new ListDataListener() {

			public void contentsChanged(ListDataEvent e) {
				refresh(false);
			}

			public void intervalAdded(ListDataEvent e) {
				contentsChanged(e);
			}

			public void intervalRemoved(ListDataEvent e) {
				contentsChanged(e);
			}
		};

		List<ListDataListener> listeners = new ArrayList<ListDataListener>();
		Filter<S> filter;
		ObservableList<S> masterList;

		EDTMirror(ObservableList<S> masterList, Filter<S> t) {
			this.filter = t;
			this.masterList = masterList;
			edtList.addUnsynchronizedListener(new ListDataListener() {

				public void contentsChanged(ListDataEvent e) {
					ListDataEvent newEvent = new ListDataEvent(EDTMirror.this,
							e.getType(), e.getIndex0(), e.getIndex1());
					for (int a = 0; a < listeners.size(); a++) {
						try {
							listeners.get(a).contentsChanged(newEvent);
						} catch (Exception e2) {
							e2.printStackTrace();
						}
					}
				}

				public void intervalAdded(ListDataEvent e) {
					ListDataEvent newEvent = new ListDataEvent(EDTMirror.this,
							e.getType(), e.getIndex0(), e.getIndex1());
					for (int a = 0; a < listeners.size(); a++) {
						try {
							listeners.get(a).intervalAdded(newEvent);
						} catch (Exception e2) {
							e2.printStackTrace();
						}
					}
				}

				public void intervalRemoved(ListDataEvent e) {
					ListDataEvent newEvent = new ListDataEvent(EDTMirror.this,
							e.getType(), e.getIndex0(), e.getIndex1());
					for (int a = 0; a < listeners.size(); a++) {
						try {
							listeners.get(a).intervalRemoved(newEvent);
						} catch (Exception e2) {
							e2.printStackTrace();
						}
					}
				}

			}, true, true);
			masterList.addSynchronizedListener(masterListListener, false);
			refresh(true);
		}

		/**
		 * Refresh the contents of this list. This should only be called by an
		 * external agent if the filter has changed. This will automatically be
		 * invoked as the master list changes.
		 * 
		 * @param immediately
		 *            if true then this is a blocking call, otherwise this uses
		 *            SwingUtilities.invokeLater to update the list.
		 */
		public void refresh(boolean immediately) {
			edtListDirty = true;
			UpdateEDTList update = new UpdateEDTList();
			if (immediately) {
				update.run();
			} else {
				SwingUtilities.invokeLater(update);
			}
		}

		@Override
		public void addListDataListener(ListDataListener listener) {
			listeners.add(listener);
		}

		@Override
		public S getElementAt(int index) {
			return edtList.get(index);
		}

		@Override
		public int getSize() {
			return edtList.size();
		}

		@Override
		public void removeListDataListener(ListDataListener listener) {
			listeners.remove(listener);
			if (listeners.size() == 0) {
				listeners.remove(listener);
			}
		}

		private Object selectedItem;

		@Override
		public void setSelectedItem(Object anItem) {
			selectedItem = anItem;
		}

		@Override
		public Object getSelectedItem() {
			return selectedItem;
		}

		public int indexOf(S element) {
			edtList.readSemaphore.acquireUninterruptibly();
			try {
				for (int a = 0; a < edtList.size(); a++) {
					Object e = edtList.get(a);
					if (e == element)
						return a;
				}
				return -1;
			} finally {
				edtList.readSemaphore.release();
			}
		}
	}

	private static class ImmutableArrayWrapper<T> extends AbstractList<T>
			implements RandomAccess {
		T[] array;

		ImmutableArrayWrapper(T[] array) {
			this.array = array;
		}

		@Override
		public T get(int index) {
			return array[index];
		}

		@Override
		public int size() {
			return array.length;
		}
	}

	/**
	 * Create an array returning all the elements of this list.
	 * <p>
	 * This method uses the existing read/write locking architecture the
	 * ObservableList uses, so it is safer than synchronizing this list to
	 * extract the elements separately.
	 * 
	 * @param componentType
	 *            the component type of the array.
	 * @return an array representation of all elements in this list.
	 */
	public T[] toArray(Class<T> componentType) {
		readSemaphore.acquireUninterruptibly();
		try {
			T[] array = (T[]) Array.newInstance(componentType, size());
			toArray(array);
			return array;
		} finally {
			readSemaphore.release();
		}
	}
}