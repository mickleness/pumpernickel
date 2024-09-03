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
package com.pump.thread;

import java.awt.EventQueue;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.SwingUtilities;

/**
 * This monitors Java's event dispatch thread and notifies listeners when that
 * thread appears unresponsive.
 * <p>
 * When listeners are attached: a daemon thread continually calls
 * {@link SwingUtilities#invokeLater(Runnable)} every few milliseconds. If that
 * Runnable is promptly executed: then the event dispatch thread appears to be
 * responsive. If that runnable is not executed: then we start to notify the
 * listeners. Each listener can have a unique threshold for when it gets
 * notified.
 * <h3>Recommendations</h3>
 * <p>
 * I recommend a sliding scale of listeners. For example:
 * <ol>
 * <li>If the EDT is unresponsive for 500 milliseconds: we can start a
 * {@link ThreadProfiler}. If the EDT recovers we can either log what it
 * observed, or auto-submit it to a dev team to study.</li>
 * <li>If the EDT is unresponsive for 2 seconds: consider autosaving the user's
 * changes, if applicable.</li>
 * <li>If the EDT is unresponsive for 5 seconds: consider calling
 * {@link Thread#interrupt()} on the EDT.</li>
 * <li>If the EDT is unresponsive for 30 seconds: call {@link Thread#stop()} on
 * the event dispatch thread. That method is deprecated and dangerous, but in my
 * experience this often works. A new event dispatch thread is promptly created,
 * and the user can recover. (You can also nudge them along to immediately save
 * and relaunch the application?) (In theory you should never call
 * {@link Thread#stop()}, but if your application is locked: what have you got
 * to lose? The user may force quit any second anyway.)</li>
 * </ol>
 * 
 * @see <a href=
 *      "https://javagraphics.blogspot.com/2008/06/event-dispatch-thread-responding-to.html">Event
 *      Dispatch Thread: Responding to Deadlocks</a>
 */
public final class EventDispatchThreadMonitor {
	static EventDispatchThreadMonitor monitor = new EventDispatchThreadMonitor();

	/**
	 * Return the EventDispatchThreadMonitor. There can only be one per JVM. The
	 * presence of a EventDispatchThreadMonitor requires one daemon thread.
	 */
	public static EventDispatchThreadMonitor get() {
		return monitor;
	}

	static class PingEDTRunnable implements Runnable {
		final long constructionMillis = System.currentTimeMillis();
		long executionMillis = -1;

		@Override
		public void run() {
			get().eventDispatchThread = Thread.currentThread();
			executionMillis = System.currentTimeMillis();
		}
	}

	private Thread eventDispatchThread;
	private final Thread monitorThread;
	private final TreeMap<Long, Collection<Listener>> listenersByThreshold = new TreeMap<>();

	private EventDispatchThreadMonitor() {
		Runnable monitorRunnable = new Runnable() {
			long minPingTime = 200;

			List<Listener> listenersWaitingForResponsiveNotification = new LinkedList<>();

			@Override
			public void run() {
				long lastResponsiveTime = System.currentTimeMillis();
				while (true) {
					try {
						Thread.sleep(50);
					} catch (Exception ignore) {
					}

					synchronized (listenersByThreshold) {
						if (listenersWaitingForResponsiveNotification.isEmpty()
								&& listenersByThreshold.isEmpty()
								&& isInitialized()) {
							// nobody is listening, so don't do anything

							// act like we just had a successful/responsive ping
							lastResponsiveTime = System.currentTimeMillis();

							continue;
						}
					}

					PingEDTRunnable r = new PingEDTRunnable();
					EventQueue.invokeLater(r);

					try {
						Thread.sleep(Math.max(20, minPingTime * 10));
					} catch (Exception ignore) {
						ignore.printStackTrace();
					}

					boolean isResponsive = r.executionMillis != -1;
					if (isResponsive) {
						long unresponsiveMillis = r.executionMillis
								- lastResponsiveTime;
						for (Listener listener : listenersWaitingForResponsiveNotification) {
							try {
								listener.becameResponsive(eventDispatchThread,
										unresponsiveMillis, lastResponsiveTime);
							} catch (Exception e) {
								Thread.currentThread()
										.getUncaughtExceptionHandler()
										.uncaughtException(
												Thread.currentThread(), e);
							}
						}
						listenersWaitingForResponsiveNotification.clear();

						lastResponsiveTime = r.executionMillis;
						long pingTime = r.executionMillis
								- r.constructionMillis;
						minPingTime = Math.min(pingTime, minPingTime);
					} else {
						if (eventDispatchThread == null) {
							// if we haven't successfully recorded the EDT yet:
							// we're probably tied up early in setting up the
							// app. Don't notify the listeners until we're more
							// confident the app has really started.
						} else {
							long unresponsiveMillis = System.currentTimeMillis()
									- lastResponsiveTime;
							List<Listener> newListeners = new LinkedList<>();
							synchronized (listenersByThreshold) {
								for (Map.Entry<Long, Collection<Listener>> e : listenersByThreshold
										.entrySet()) {
									if (e.getKey() > unresponsiveMillis) {
										// no more Listeners are interested in
										// our current delay
										break;
									}

									for (Listener l : e.getValue()) {
										if (!listenersWaitingForResponsiveNotification
												.contains(l)) {
											listenersWaitingForResponsiveNotification
													.add(l);
											newListeners.add(l);
										}
									}
								}
							}

							// notify listeners outside of our synchronization:
							for (Listener newListener : newListeners) {
								try {
									newListener.becameUnresponsive(
											eventDispatchThread,
											unresponsiveMillis,
											lastResponsiveTime);
								} catch (Exception e) {
									Thread.currentThread()
											.getUncaughtExceptionHandler()
											.uncaughtException(
													Thread.currentThread(), e);
								}
							}
						}
					}
				}
			}
		};

		monitorThread = new Thread(monitorRunnable,
				"EventDispatchThreadMonitor");
		monitorThread.setDaemon(true);
		monitorThread.start();
	}

	/**
	 * This listens to EventDispatchThreadMonitor notifications.
	 * <p>
	 * When you first add a Listener you can specify the threshold (in
	 * milliseconds) that it's interested in receiving notifications for.
	 */
	public interface Listener {

		/**
		 * This method is called when the event dispatch thread has been
		 * unresponsive past this listener's target threshold.
		 * <p>
		 * For example: if you asked this listener to be notified when the event
		 * dispatch thread is unresponsive for over 500 milliseconds, then this
		 * method may be called if the event dispatch thread is unresponsive for
		 * about 530 milliseconds. The timing is approximate, but it should
		 * generally be within 100 milliseconds of your target threshold.
		 * 
		 * @param eventDispatchThread
		 *            the last known event dispatch thread.
		 * @param unresponsiveMillis
		 *            the approximate length of time the event dispatch thread
		 *            was unresponsive (in milliseconds) so far. This should be
		 *            equal to or greater than the threshold this Listener was
		 *            originally registered with.
		 * @param lastSuccessfulPingMillis
		 *            the last System time the monitor observed the event
		 *            dispatch thread was responsive.
		 */
		void becameUnresponsive(Thread eventDispatchThread,
				long unresponsiveMillis, long lastSuccessfulPingMillis);

		/**
		 * This is always called after {@link #becameUnresponsive(Thread, long)}
		 * when the event dispatch thread recovers. (If it recovers.)
		 * 
		 * @param eventDispatchThread
		 *            the current event dispatch thread.
		 * @param unresponsiveMillis
		 *            the approximate length of time the event dispatch thread
		 *            was unresponsive (in milliseconds) before it recovered.
		 * @param lastSuccessfulPingMillis
		 *            the exact System time the previous successful ping was
		 *            received.
		 */
		void becameResponsive(Thread eventDispatchThread,
				long unresponsiveMillis, long lastSuccessfulPingMillis);
	}

	/**
	 * Add a Listener that is notified after the event dispatch thread has been
	 * unresponsive for a specific threshold.
	 * 
	 * @param threshold
	 *            the number of milliseconds the event dispatch thread must be
	 *            unresponsive for before the Listener is notified.
	 */
	public void addListener(long threshold, Listener listener) {
		synchronized (listenersByThreshold) {
			Collection<Listener> c = listenersByThreshold.get(threshold);
			if (c == null) {
				c = new LinkedList<>();
				listenersByThreshold.put(threshold, c);
			}
			c.add(listener);
		}
	}

	/**
	 * Remove a Listener.
	 * 
	 * @return true if the listener was found and removed, false if not.
	 */
	public boolean removeListener(Listener listener) {
		synchronized (listenersByThreshold) {
			Iterator<Map.Entry<Long, Collection<Listener>>> iter = listenersByThreshold
					.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<Long, Collection<Listener>> entry = iter.next();
				if (entry.getValue().remove(listener)) {
					if (entry.getValue().isEmpty()) {
						iter.remove();
					}
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Remove all Listeners.
	 * 
	 * @return the previous listeners, sorted by threshold time.
	 */
	public Map<Long, Collection<Listener>> removeAllListeners() {
		Map<Long, Collection<Listener>> returnValue = new HashMap<>();
		synchronized (listenersByThreshold) {
			returnValue.putAll(listenersByThreshold);
			listenersByThreshold.clear();
		}
		return returnValue;
	}

	/**
	 * Return true if this monitor has issued and received one successful ping
	 * on the event dispatch thread.
	 */
	boolean isInitialized() {
		return eventDispatchThread != null;
	}
}