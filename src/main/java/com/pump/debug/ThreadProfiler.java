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
package com.pump.debug;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ThreadProfiler {

	static class ProfilerThread extends Thread {

		public ProfilerThread(Runnable runnable) {
			super(runnable);
		}

	}

	private static int compareString(String str1, String str2) {
		if (str1 == null && str2 == null)
			return 0;
		if (str1 == null)
			return -1;
		if (str2 == null)
			return 1;
		return str1.compareTo(str2);
	}

	static class StackTraceElementNode
			implements Comparable<StackTraceElementNode> {
		StackTraceElement element;
		Map<StackTraceElementNode, StackTraceElementNode> children = new TreeMap<>();
		int frequency = 1;
		SynchronizedStatus isSynchronized;

		StackTraceElementNode(StackTraceElement e) {
			element = e;
		}

		public StackTraceElementNode catalog(StackTraceElement e,
				long timestamp) {

			StackTraceElementNode newNode = new StackTraceElementNode(e);
			StackTraceElementNode oldNode = children.get(newNode);
			if (oldNode != null) {
				oldNode.frequency++;
				return oldNode;
			}

			children.put(newNode, newNode);
			return newNode;
		}

		@Override
		public String toString() {
			return "StackTraceElementNode[ frequency=" + frequency
					+ ", element=" + element + "]";
		}

		@Override
		public int compareTo(StackTraceElementNode o) {
			int k = compareString(element.getFileName(),
					o.element.getFileName());
			if (k != 0)
				return k;
			k = compareString(element.getClassName(), o.element.getClassName());
			if (k != 0)
				return k;
			k = compareString(element.getMethodName(),
					o.element.getMethodName());
			if (k != 0)
				return k;
			k = Integer.compare(element.getLineNumber(),
					o.element.getLineNumber());
			return k;
		}

		public int getMaxFrequency() {
			int returnValue = frequency;
			for (StackTraceElementNode child : children.keySet()) {
				returnValue = Math.max(returnValue, child.getMaxFrequency());
			}
			return returnValue;
		}

		/**
		 * Return the SynchronizedStatus of this method, which may be UNKNOWN if
		 * it can't be determined.
		 */
		public SynchronizedStatus getSynchronizedStatus() {
			if (isSynchronized == null) {
				try {
					Collection<Method> candidates = new HashSet<>();

					Class z = Class.forName(element.getClassName());
					while (z != null) {
						Method[] methods = z.getDeclaredMethods();
						for (Method method : methods) {
							if (method.getName()
									.equals(element.getMethodName()))
								candidates.add(method);
						}
						z = z.getSuperclass();
					}

					int synchronizedCount = 0;
					for (Method m : candidates) {
						if ((m.getModifiers() & Modifier.SYNCHRONIZED) > 0) {
							synchronizedCount++;
						}
					}

					if (synchronizedCount == candidates.size()) {
						isSynchronized = synchronizedCount == 0
								? SynchronizedStatus.IS_NOT_SYNCHRONIZED
								: SynchronizedStatus.IS_SYNCHRONIZED;
					} else {
						isSynchronized = SynchronizedStatus.UNKNOWN;
					}
					return isSynchronized;
				} catch (Throwable t) {
					// in my tests this was often a
					// "java.lang.ClassNotFoundException:
					// com.apple.laf.AquaPainter$AquaSingleImagePainter$$Lambda$175/0x0000000800db35f0",
					// but of course this could also be a security issue. This
					// is optional info: if ANYTHING comes up, just call it
					// UNKNOWN
					isSynchronized = SynchronizedStatus.UNKNOWN;
				}
			}
			return isSynchronized;
		}
	}

	enum SynchronizedStatus {
		IS_SYNCHRONIZED, IS_NOT_SYNCHRONIZED, UNKNOWN
	}

	static class ListenerInfo {
		final ChangeListener listener;
		final long notificationInterval;
		long lastNotification = -1;
		int notificationCtr = 0;

		public ListenerInfo(ChangeListener listener,
				long notificationInterval) {
			this.listener = listener;
			this.notificationInterval = notificationInterval;
		}

		/**
		 * @param src
		 *            the source of the potential ChangeEvent
		 * @param forceDispatch
		 */
		public void dispatchEvent(ThreadProfiler src, long currentTimeMillis,
				boolean forceDispatch) {
			boolean dispatch = forceDispatch;
			if (!dispatch) {
				long elapsed = currentTimeMillis - lastNotification;
				if (elapsed >= notificationInterval) {
					lastNotification = currentTimeMillis;
					dispatch = true;
				}
			}

			if (dispatch) {
				if (notificationCtr == 0)
					dispatch = false;

				notificationCtr++;
			}

			if (dispatch) {
				try {
					listener.stateChanged(new ChangeEvent(src));
				} catch (Exception e) {
					Thread t = Thread.currentThread();
					t.getUncaughtExceptionHandler().uncaughtException(t, e);
				}
			}
		}
	}

	static class RootStackTraceElementNode extends StackTraceElementNode {

		Map<Thread.State, AtomicInteger> stateMap = new TreeMap<>();

		RootStackTraceElementNode(StackTraceElement e) {
			super(e);
		}

		public void catalogState(Thread.State state) {
			AtomicInteger i = stateMap.get(state);
			if (i == null) {
				i = new AtomicInteger(1);
				stateMap.put(state, i);
			} else {
				i.incrementAndGet();
			}
		}
	}

	private static DecimalFormat format = new DecimalFormat("#.0");
	private static DecimalFormat intFormat = new DecimalFormat("#");

	private static Comparator<StackTraceElementNode> NODE_FREQUENCY_COMPARATOR = new Comparator<StackTraceElementNode>() {
		@Override
		public int compare(StackTraceElementNode o1, StackTraceElementNode o2) {
			int max1 = o1.getMaxFrequency();
			int max2 = o2.getMaxFrequency();
			int k = Integer.compare(max1, max2);
			if (k != 0)
				return -k;
			return o1.compareTo(o2);
		}
	};

	boolean active = false;
	Thread profilingThread;
	long sampleMillisInterval = 50;
	Runnable profilingRunnable;
	Function<Thread, Boolean> threadFilter;
	Map<Thread, RootStackTraceElementNode> stackTraceData = new HashMap<>();
	List<ListenerInfo> listeners = new LinkedList<>();

	public ThreadProfiler() {
		this(null);
	}

	/**
	 * @param threadFilter
	 *            an optional filter; if this is null then all threads are
	 *            profiled.
	 */
	public ThreadProfiler(Function<Thread, Boolean> threadFilter) {
		this.threadFilter = threadFilter;

		profilingRunnable = new Runnable() {
			@Override
			public void run() {
				long lastTimestamp = -1;
				try {
					while (true) {
						synchronized (ThreadProfiler.this) {
							if (!active || Thread
									.currentThread() != profilingThread)
								return;
						}
						long currentMillis = System.currentTimeMillis();

						notifyListeners(false, currentMillis);

						long elapsed = System.currentTimeMillis()
								- lastTimestamp;
						long delay = sampleMillisInterval - elapsed;
						if (delay > 0) {
							try {
								Thread.sleep(delay);
							} catch (Exception e) {
								// this shouldn't happen
								e.printStackTrace();
							}
							lastTimestamp = currentMillis + delay;
						} else {
							lastTimestamp = currentMillis;
						}

						Map<Thread, StackTraceElement[]> map = Thread
								.getAllStackTraces();

						// prepare: prune the map, reverse the
						// StackTraceElement[] order:
						Iterator<Map.Entry<Thread, StackTraceElement[]>> iter = map
								.entrySet().iterator();
						while (iter.hasNext()) {
							Map.Entry<Thread, StackTraceElement[]> entry = iter
									.next();
							Thread thread = entry.getKey();

							if (thread instanceof ProfilerThread) {
								iter.remove();
								continue;
							} else if (threadFilter != null
									&& !threadFilter.apply(thread)) {
								iter.remove();
								continue;
							}

							// invert the order of StackTraceElements:
							StackTraceElement[] elements = entry.getValue();

							if (elements.length == 0) {
								iter.remove();
								continue;
							}

							for (int a = 0; a < elements.length / 2; a++) {
								StackTraceElement t = elements[a];
								elements[a] = elements[elements.length - 1 - a];
								elements[elements.length - 1 - a] = t;
							}
						}

						// record what's left:

						synchronized (stackTraceData) {
							for (Map.Entry<Thread, StackTraceElement[]> entry : map
									.entrySet()) {
								Thread thread = entry.getKey();
								StackTraceElement[] elements = entry.getValue();
								RootStackTraceElementNode root = stackTraceData
										.get(thread);
								if (root == null) {
									root = new RootStackTraceElementNode(
											elements[0]);
									stackTraceData.put(thread, root);
								}
								root.catalogState(thread.getState());
								root.frequency++;
								if (elements.length > 1) {
									StackTraceElementNode node = root;
									for (int a = 1; a < elements.length; a++) {
										node = node.catalog(elements[a],
												currentMillis);
									}
								}
							}
						}
					}
				} finally {
					notifyListeners(true, System.currentTimeMillis());
				}
			}
		};

		reset();
	}

	private synchronized void notifyListeners(boolean force, long timestamp) {
		Iterator<ListenerInfo> iter = listeners.iterator();
		while (iter.hasNext()) {
			ListenerInfo i = iter.next();
			i.dispatchEvent(this, timestamp, force);
		}
	}

	public synchronized void setSampleInterval(long millis) {
		if (millis < 10)
			throw new IllegalArgumentException(
					"sampleMillisInterval must be at least 10 ms");
		sampleMillisInterval = millis;
	}

	/**
	 * Add a listener that will be notified after a given interval when this
	 * profiler is running.
	 * 
	 * @param intervalMillis
	 *            the number of millis between updates.
	 * @param changeListener
	 *            the listener to dispatch ChangeEvents to.
	 */
	public synchronized void addDataListener(long intervalMillis,
			ChangeListener changeListener) {
		listeners.add(new ListenerInfo(changeListener, intervalMillis));
	}

	/**
	 * Remove a ChangeListener.
	 * <p>
	 * If there are more than one instance of the requested ChangeListener: this
	 * removes the first one that was added.
	 * 
	 * @param changeListener
	 *            the listener to remove
	 * @return true if this call successfully remove the listener; false if the
	 *         listener was null or couldn't be found.
	 */
	public synchronized boolean removeDataListener(
			ChangeListener changeListener) {
		if (changeListener == null)
			return false;
		Iterator<ListenerInfo> iter = listeners.iterator();
		while (iter.hasNext()) {
			ListenerInfo i = iter.next();
			if (i.listener == changeListener) {
				iter.remove();
				return true;
			}
		}
		return false;
	}

	public synchronized boolean isActive() {
		return active;
	}

	public synchronized void setActive(boolean active) {
		if (this.active == active)
			return;

		this.active = active;
		if (active) {
			profilingThread = new ProfilerThread(profilingRunnable);
			profilingThread.start();
		}
	}

	public synchronized String getOutput() {
		StringBuilder sb = new StringBuilder();

		// sort highest-priority first, then the most active, then sort by name
		Comparator<Thread> comparator = new Comparator<Thread>() {

			public int compare(Thread o1, Thread o2) {
				int k = -Integer.compare(o1.getPriority(), o2.getPriority());
				if (k != 0)
					return k;

				RootStackTraceElementNode n1 = stackTraceData.get(o1);
				RootStackTraceElementNode n2 = stackTraceData.get(o2);

				if (n1 == null && n2 != null) {
					return -1;
				} else if (n1 != null && n2 == null) {
					return 1;
				}

				AtomicInteger r1 = n1.stateMap.get(Thread.State.RUNNABLE);
				AtomicInteger r2 = n2.stateMap.get(Thread.State.RUNNABLE);
				double q1 = r1 == null ? 0 : r1.intValue();
				double q2 = r2 == null ? 0 : r2.intValue();
				q1 = q1 / ((double) n1.frequency);
				q2 = q2 / ((double) n2.frequency);

				k = -Double.compare(q1, q2);
				if (k != 0)
					return k;

				k = compareString(o1.getName(), o2.getName());
				if (k != 0)
					return k;
				return Long.compare(o1.getId(), o2.getId());
			}

		};
		Collection<Thread> threads = new TreeSet<>(comparator);

		int maxFrequency = 0;
		for (Map.Entry<Thread, RootStackTraceElementNode> entry : stackTraceData
				.entrySet()) {
			threads.add(entry.getKey());
			maxFrequency = Math.max(maxFrequency, entry.getValue().frequency);
		}

		sb.append(intFormat.format(maxFrequency) + " samples collected.\n\n");

		for (Thread thread : threads) {
			RootStackTraceElementNode node = stackTraceData.get(thread);

			float sum = 0;
			for (AtomicInteger i : node.stateMap.values()) {
				sum += i.intValue();
			}

			StringBuilder stateStr = new StringBuilder();
			for (Map.Entry<Thread.State, AtomicInteger> e : node.stateMap
					.entrySet()) {
				float fraction = e.getValue().floatValue() / sum * 100;
				String percent = format.format(fraction);
				if (stateStr.length() > 0) {
					stateStr.append(", ");
				}
				stateStr.append(e.getKey() + "=" + percent + "%");
			}

			sb.append("-------- " + thread.getName() + ", priority = "
					+ thread.getPriority() + ", " + stateStr.toString() + "\n");
			int max = node.getMaxFrequency();
			StringBuilder indent = new StringBuilder();
			write(sb, node, max, indent);
		}
		return sb.toString();
	}

	private void write(StringBuilder output, StackTraceElementNode node,
			int maxFrequency, StringBuilder indent) {
		float fraction = node.frequency * 100f / maxFrequency;
		String percentStr = format.format(fraction);

		// if(node.active) {
		// output.append("-> ");
		// } else {
		// output.append(" ");
		// }

		output.append(percentStr);
		output.append("%");
		for (int a = percentStr.length(); a < 6; a++) {
			output.append(' ');
		}
		output.append(indent);
		output.append(
				node.element.getClassName() + "#" + node.element.getMethodName()
						+ "(" + node.element.getFileName() + ":"
						+ node.element.getLineNumber() + ")");

		SynchronizedStatus s = node.getSynchronizedStatus();
		if (s == SynchronizedStatus.IS_SYNCHRONIZED) {
			output.append(" (synchronized)");
		}

		output.append("\n");
		indent.append(" ");
		TreeSet<StackTraceElementNode> children = new TreeSet<>(
				NODE_FREQUENCY_COMPARATOR);
		children.addAll(node.children.keySet());
		for (StackTraceElementNode child : children) {
			write(output, child, maxFrequency, indent);
		}
		indent.delete(indent.length() - 1, indent.length());
	}

	public void writeOutput(File file) throws IOException {
		try (FileOutputStream fileOut = new FileOutputStream(file)) {
			writeOutput(fileOut);
		}
	}

	public void writeOutput(OutputStream fileOut) throws IOException {
		try (OutputStreamWriter writer = new OutputStreamWriter(fileOut,
				Charset.forName("UTF-8"))) {
			writer.write(getOutput());
		}
	}

	public void reset() {
		stackTraceData.clear();
	}
}