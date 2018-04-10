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
package com.pump.debug;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import com.pump.blog.Blurb;
import com.pump.util.JVM;

/**
 * This class monitors the event dispatch thread.
 * <P>
 * After 2 seconds if the thread is unresponsive, a list of stack traces is
 * printed to the console.
 * <P>
 * After 15 seconds if the thread is unresponsive, the
 * <code>panicListener</code> is notified (if it is non-null). By default the
 * <code>panicListener</code> is a {@link com.pump.debug.PanicDialogPrompt}, but
 * you can nullify this value or create your own
 * {@link com.pump.debug.AWTPanicListener}.
 * 
 */
@Blurb(title = "Event Dispatch Thread: Responding to Deadlocks", releaseDate = "June 2008", summary = "What can you do when your event dispatch thread is blocked?\n"
		+ "<P>For starters: this article mentions how to automatically detect this situation and get great console output to help pinpoint the problem. "
		+ "Also this delves into a very murky partial solution.  (It has worked multiple times for me, but it uses unsafe deprecated black magic.)", article = "http://javagraphics.blogspot.com/2008/06/event-dispatch-thread-responding-to.html")
public class AWTMonitor {
	static class AWTRunnable implements Runnable {
		boolean flag;

		public void run() {
			flag = true;
		}
	}

	/** The AWTPanicListeners. */
	private final static List<AWTPanicListener> panicListeners = new ArrayList<AWTPanicListener>();
	/**
	 * We keep one private copy around, but it isn't added until the install()
	 * method.
	 */
	private final static PanicDialogPrompt defaultPrompt = new PanicDialogPrompt();

	/**
	 * Adds a <code>AWTPanicListener</code> to be notified when the EDT appears
	 * blocked.
	 * 
	 * @param l
	 *            the listener to add.
	 */
	public static void addPanicListener(AWTPanicListener l) {
		if (panicListeners.contains(l) == true)
			return;
		panicListeners.add(l);
	}

	/**
	 * Removes a <code>AWTPanicListener</code>.
	 * 
	 * @param l
	 *            the listener to remove.
	 */
	public static void removePanicListener(AWTPanicListener l) {
		panicListeners.remove(l);
	}

	private static Thread awtMonitorThread;

	/**
	 * This installs a thread that monitors the AWT thread.
	 * <P>
	 * If the AWT thread is unresponsive for over 2 seconds, then thread stacks
	 * are dumped to the console. If the event dispatch thread is unresponsive
	 * for over 15 seconds, the <code>panicListener</code> (if non-null) is
	 * notified.
	 * 
	 * @param applicationName
	 *            the name of the application to present to the user
	 * @param addPanicDialogPrompt
	 *            if true then the <code>PanicDialogPrompt</code> is added.
	 */
	public static void installAWTListener(final String applicationName,
			boolean addPanicDialogPrompt) {
		installAWTListener(applicationName, 2000, 15000, addPanicDialogPrompt);
	}

	/**
	 * Installs a thread that monitors the AWT thread.
	 * 
	 * @param applicationName
	 *            the name of the application to present to the user
	 * @param stackTraceDelay
	 *            the delay before stack traces are printed to the console
	 * @param panicListenerDelay
	 *            the delay before invoking the PanicListener.
	 * @param addPanicDialogPrompt
	 *            if true then the <code>PanicDialogPrompt</code> is added.
	 */
	public synchronized static void installAWTListener(String applicationName,
			long stackTraceDelay, final long panicListenerDelay,
			boolean addPanicDialogPrompt) {
		if (addPanicDialogPrompt)
			addPanicListener(defaultPrompt);
		if (awtMonitorThread == null || awtMonitorThread.isAlive() == false) {
			awtMonitorThread = createAWTMonitorThread(applicationName,
					stackTraceDelay, panicListenerDelay,
					"AWT Listener (Debug Tool)", System.err);
			awtMonitorThread.start();
		}
	}

	/**
	 * Create a Thread that monitors the AWT thread.
	 * <p>
	 * This is intended for internal use, but is given package-level access here
	 * for the AWTMonitorDemo panel.
	 */
	static Thread createAWTMonitorThread(final String applicationName,
			final long stackTraceDelay, final long panicListenerDelay,
			final String threadName, final PrintStream dest) {
		return new Thread(threadName) {
			AWTRunnable awtRunnable = new AWTRunnable();

			@Override
			public void run() {
				/**
				 * There are two actions that can happen here: 1. "reporting"
				 * gives a dump stack to the console. 2. "panicking" invokes the
				 * AWTPanicListener.
				 * 
				 * An extra half second delay is added before either action. If
				 * a computer was put to sleep, then immediately after waking
				 * either action might be triggered: a half-second delay will
				 * give the EDT a chance to catch up and prove there's not
				 * really a problem.
				 */

				while (true) {
					awtRunnable.flag = false;
					try {
						SwingUtilities.invokeLater(awtRunnable);
					} catch (RuntimeException e) {
						// this can happen super early in construction:
						// we can get a NPE from the code:
						// Toolkit.getEventQueue().postEvent(
						// new InvocationEvent(Toolkit.getDefaultToolkit(),
						// runnable));
						awtRunnable.flag = true;
					}
					long start = System.currentTimeMillis();
					if (!awtRunnable.flag) {
						boolean reportedToConsole = false;
						boolean panicked = false;
						if (awtRunnable.flag == false) {
							long reportNeeded = -1;
							long panicNeeded = -1;
							while (awtRunnable.flag == false) {
								idle();
								long current = System.currentTimeMillis();
								if (reportedToConsole == false
										&& current - start > stackTraceDelay) {
									if (reportNeeded == -1) {
										reportNeeded = System
												.currentTimeMillis();
									} else if (current - reportNeeded > 500) {
										dumpThreads(
												"The AWT thread was unresponsive for "
														+ stackTraceDelay
														/ 1000
														+ " seconds.  Here is a stack trace from all available threads:",
												dest);
										reportedToConsole = true;
									}
								}
								if (panicListeners.size() > 0
										&& panicked == false
										&& current - start > panicListenerDelay) {
									if (panicNeeded == -1) {
										panicNeeded = System
												.currentTimeMillis();
									} else if (current - panicNeeded > 500) {
										panicked = true;
										Thread panicThread = new Thread(
												"Panic Thread") {
											@Override
											public void run() {
												for (int a = 0; a < panicListeners
														.size(); a++) {
													AWTPanicListener panicListener = panicListeners
															.get(a);
													try {
														panicListener
																.AWTPanic(applicationName);
													} catch (Exception e) {
														e.printStackTrace();
													}
												}
											}
										};
										panicThread.start();
									}
								}
							}
						}
					}
					idle();

					while (System.currentTimeMillis() - start < stackTraceDelay / 10) {
						idle();
					}
				}
			}

			private void idle() {
				try {
					Thread.sleep(200);
				} catch (Exception e) {
					Thread.yield();
				}
			}
		};
	}

	/**
	 * This effectively calls Thread.dumpStack() for every available thread.
	 * 
	 * @param headerText
	 *            text to print that precedes the stack traces.
	 * @param dest
	 *            the PrintStream to write data to.
	 */
	public static void dumpThreads(String headerText, PrintStream dest) {
		try {
			Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
			Iterator<Thread> i = map.keySet().iterator();

			dest.println(headerText);
			while (i.hasNext()) {
				Thread key = i.next();
				StackTraceElement[] array = map.get(key);
				String id = "";

				try {
					id = " (id = " + key.getId() + ")";
				} catch (Throwable e) {
				} // we ignore this

				dest.println(key.getName() + id);
				for (int a = 0; a < array.length; a++) {
					dest.println("\t" + array[a]);
				}
			}
		} catch (Throwable e1) {
			if (JVM.getMajorJavaVersion() >= 1.5) {
				e1.printStackTrace();
			} else {
				dest.println("Stack traces were requested, but this feature is not supported in this Java version ("
						+ JVM.getMajorJavaVersion() + ").");
			}
		}
	}
}