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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * This Semaphore monitors how many permits each thread has. This value can be
 * observed by calling <code>getUsedPermitsForCurrentThread()</code>.
 *
 */
public class ThreadedSemaphore {
	Semaphore semaphore;
	Map<Thread, Integer> threadToPermitMap = new HashMap<Thread, Integer>();

	public ThreadedSemaphore(int permitCount) {
		semaphore = new Semaphore(permitCount);
	}

	@Override
	public int hashCode() {
		return semaphore.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof ThreadedSemaphore))
			return false;
		ThreadedSemaphore ts = (ThreadedSemaphore) obj;
		return semaphore.equals(ts.semaphore)
				&& threadToPermitMap.equals(ts.threadToPermitMap);
	}

	private void changeThreadCount(int amount) {
		/**
		 * We don't need to synchronize this map, because no two threads will
		 * ever read/write the same entry simultaneously.
		 */
		Thread thread = Thread.currentThread();
		Integer intValue = threadToPermitMap.get(thread);
		if (intValue == null) {
			intValue = new Integer(amount);
		} else {
			intValue = new Integer(intValue.intValue() + amount);
		}
		if (intValue.intValue() == 0) {
			threadToPermitMap.remove(thread);
		} else {
			threadToPermitMap.put(thread, intValue);
		}
	}

	public int getUsedPermitsForCurrentThread() {
		Thread thread = Thread.currentThread();
		Integer intValue = threadToPermitMap.get(thread);
		if (intValue == null)
			return 0;
		return intValue.intValue();
	}

	public void acquire() throws InterruptedException {
		semaphore.acquire();
		changeThreadCount(1);

	}

	public void acquireUninterruptibly() {
		semaphore.acquireUninterruptibly();
		;
		changeThreadCount(1);
	}

	public boolean tryAcquire() {
		boolean returnValue = semaphore.tryAcquire();
		if (returnValue)
			changeThreadCount(1);
		return returnValue;
	}

	public void release() {
		semaphore.release();
		changeThreadCount(-1);
	}

	public void acquire(int permits) throws InterruptedException {
		semaphore.acquire(permits);
		changeThreadCount(permits);
	}

	public void acquireUninterruptibly(int permits) {
		semaphore.acquireUninterruptibly(permits);
		changeThreadCount(permits);
	}

	public boolean tryAcquire(int permits) {
		boolean returnValue = semaphore.tryAcquire(permits);
		if (returnValue)
			changeThreadCount(permits);
		return returnValue;
	}

	public boolean tryAcquire(int permits, long timeout, TimeUnit unit)
			throws InterruptedException {
		boolean returnValue = semaphore.tryAcquire(permits, timeout, unit);
		if (returnValue)
			changeThreadCount(permits);
		return returnValue;
	}

	public void release(int permits) {
		semaphore.release(permits);
		changeThreadCount(-permits);
	}

	public int availablePermits() {
		return semaphore.availablePermits();
	}

	public int drainPermits() {
		int returnValue = semaphore.drainPermits();
		changeThreadCount(returnValue);
		return returnValue;
	}

	public boolean isFair() {
		return semaphore.isFair();
	}

	public final boolean hasQueuedThreads() {
		return semaphore.hasQueuedThreads();
	}

	public final int getQueueLength() {
		return semaphore.getQueueLength();
	}

	@Override
	public String toString() {
		return "ThreadedSemaphore[ semaphore=" + semaphore.toString() + ", "
				+ threadToPermitMap.toString() + "]";
	}
}