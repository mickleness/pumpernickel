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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This is effectively a reentrant semaphore. If a thread has already
 * successfully obtained a permit: future calls to
 * {@link #acquireUninterruptibly()} return immediately and no more permits are
 * consumed.
 */
public class ReentrantPermitLock {

	protected Semaphore semaphore;
	protected Map<Thread, AtomicInteger> map;

	/**
	 * 
	 * @param permitCount
	 *            the number of permits/threads this lock allows
	 */
	public ReentrantPermitLock(int permitCount) {
		semaphore = new Semaphore(permitCount, true);
		map = new HashMap<>();
	}

	/**
	 * This blocks until a permit becomes available, or it returns immediately
	 * if this thread already has a permit.
	 */
	public void acquireUninterruptibly() {
		Thread t = Thread.currentThread();
		synchronized (map) {
			AtomicInteger i = map.get(t);
			if (i != null) {
				// we already have a permit, so we're just reentering:
				i.incrementAndGet();
				return;
			}
		}
		semaphore.acquireUninterruptibly();
		synchronized (map) {
			map.put(t, new AtomicInteger(1));
		}
	}

	/**
	 * Release a permit previously obtained by calling
	 * {@link #acquireUninterruptibly()}.
	 */
	public void release() {
		Thread t = Thread.currentThread();
		synchronized (map) {
			AtomicInteger i = map.get(t);
			if (i == null)
				throw new NullPointerException("The thread \"" + t.getName()
						+ "\" did not have a permit.");
			i.decrementAndGet();
			if (i.intValue() == 0) {
				map.remove(t);
				semaphore.release();
			}
		}
	}

}