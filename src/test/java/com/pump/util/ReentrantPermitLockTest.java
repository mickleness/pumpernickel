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

import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import org.junit.Test;

public class ReentrantPermitLockTest extends TestCase {

	/**
	 * This confirms that thread X can repeatedly call acquireUninterruptibly
	 * without running out of permits.
	 */
	@Test
	public void testReentrant() {
		ReentrantPermitLock lock = new ReentrantPermitLock(1);
		lock.acquireUninterruptibly();
		lock.acquireUninterruptibly();
		lock.acquireUninterruptibly();
		lock.release();
		lock.release();
		lock.release();
	}

	/**
	 * This confirms that given two threads X and Y and only one permit: only
	 * one thread can acquire that permit at a time.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testExclusive() throws Exception {
		final AtomicInteger stage = new AtomicInteger(0);
		final ReentrantPermitLock lock = new ReentrantPermitLock(1);
		lock.acquireUninterruptibly();

		Thread otherThread = new Thread() {
			public void run() {
				stage.set(1);
				lock.acquireUninterruptibly();
				stage.set(2);
				lock.release();
			}
		};

		otherThread.start();

		while (stage.intValue() == 0)
			Thread.yield();

		Thread.sleep(10);
		assertEquals(1, stage.intValue());

		lock.release();

		Thread.sleep(10);
		assertEquals(2, stage.intValue());
	}
}