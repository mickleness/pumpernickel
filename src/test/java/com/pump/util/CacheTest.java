package com.pump.util;

import java.util.Arrays;
import java.util.HashSet;

import junit.framework.TestCase;

import org.junit.Test;

public class CacheTest extends TestCase {

	/**
	 * Test basic get/put functionality
	 */
	@Test
	public void testStorage() {
		Cache<Integer, String> cache = new Cache<>(4);

		// test an empty cache:
		assertEquals(null, cache.get(1));

		// add elements
		assertEquals(null, cache.put(1, "A"));
		assertEquals(null, cache.put(2, "B"));
		assertEquals(null, cache.put(3, "C"));
		assertEquals(null, cache.put(4, "D"));

		// retrieve elements
		assertEquals("A", cache.get(1));
		assertEquals("B", cache.get(2));
		assertEquals("C", cache.get(3));
		assertEquals("D", cache.get(4));

		// test a non-empty cache with a non-existent key
		assertEquals(null, cache.get(5));

		// confirm replacing elements works as expected
		assertEquals("A", cache.put(1, "X"));
		assertEquals("B", cache.put(2, "Y"));

		// retrieve elements again
		assertEquals("X", cache.get(1));
		assertEquals("Y", cache.get(2));
		assertEquals("C", cache.get(3));
		assertEquals("D", cache.get(4));

	}

	/**
	 * Confirm that after we reach a maximum number of elements we bump other
	 * elements out of the cache.
	 */
	@Test
	public void testMaxSize_scenario1() {
		Cache<Integer, String> cache = new Cache<>(4);
		cache.put(1, "A");
		cache.put(2, "B");
		cache.put(3, "C");
		cache.put(4, "D");

		assertEquals(cache.getKeys(), new HashSet<>(Arrays.asList(1, 2, 3, 4)));

		assertEquals("A", cache.get(1));
		assertEquals("B", cache.get(2));
		assertEquals("C", cache.get(3));
		assertEquals("D", cache.get(4));

		cache.put(5, "E");
		assertEquals(4, cache.size());

		// we should remove the 1/A element:
		assertEquals(null, cache.get(1));
	}

	/**
	 * Confirm that the order we accessed elements in affects which element is
	 * purged
	 */
	@Test
	public void testMaxSize_scenario2() {
		Cache<Integer, String> cache = new Cache<>(4);
		cache.put(1, "A");
		cache.put(2, "B");
		cache.put(3, "C");
		cache.put(4, "D");

		assertEquals("B", cache.get(2));
		assertEquals("D", cache.get(4));
		assertEquals("A", cache.get(1));
		assertEquals("C", cache.get(3));

		cache.put(5, "E");
		assertEquals(4, cache.size());

		// this time we should purge "B", because it was the last element to be
		// touched
		assertEquals(null, cache.get(2));
	}

	/**
	 * Confirm that putting elements in also monitors the last-touched property
	 * correctly.
	 */
	@Test
	public void testMaxSize_scenario3() {
		Cache<Integer, String> cache = new Cache<>(4);
		cache.put(3, "C");
		cache.put(1, "A");
		cache.put(2, "B");
		cache.put(4, "D");

		cache.put(5, "E");
		assertEquals(4, cache.size());

		// this time we should purge "C", because it was the last element to be
		// touched
		assertEquals(null, cache.get(3));
	}

	/**
	 * Confirm that retrieving the most recently active key/value pair doesn't
	 * affect the ticket ID counter
	 */
	@Test
	public void testTicketCounterManagement_get() {
		Cache<Integer, String> cache = new Cache<>(4);
		cache.put(1, "A");
		cache.put(2, "B");
		cache.put(3, "C");
		cache.put(4, "D");
		long idCtr = cache.idCtr;

		// this shouldn't increment idCtr, because it was the most recently
		// touched record:
		cache.get(4);
		assertEquals(idCtr, cache.idCtr);

		// this should increment idCtr
		cache.get(3);
		assertFalse(idCtr == cache.idCtr);

		// this shouldn't increment it:
		idCtr = cache.idCtr;
		cache.get(3);
		assertEquals(idCtr, cache.idCtr);
	}

	/**
	 * Confirm that overwriting the most recently active key/value pair doesn't
	 * affect the ticket ID counter
	 */
	@Test
	public void testTicketCounterManagement_put() {
		Cache<Integer, String> cache = new Cache<>(4);
		cache.put(1, "A");
		cache.put(2, "B");
		cache.put(3, "C");
		cache.put(4, "D");
		long idCtr = cache.idCtr;

		// this shouldn't increment idCtr, because it was the most recently
		// touched record:
		cache.put(4, "Z");
		assertEquals("Z", cache.get(4));
		assertEquals(idCtr, cache.idCtr);

		// this should increment idCtr
		cache.put(3, "Y");
		assertEquals("Y", cache.get(3));
		assertFalse(idCtr == cache.idCtr);

		// this shouldn't increment it:
		idCtr = cache.idCtr;
		cache.put(3, "X");
		assertEquals("X", cache.get(3));
		assertEquals(idCtr, cache.idCtr);
	}

	/**
	 * Confirm that we safely reset ticket ID after a lot (years?) of activity.
	 */
	@Test
	public void testIDRollover() {
		Cache<Integer, String> cache = new Cache<>(4);
		cache.idCtr = Long.MAX_VALUE - 2;
		cache.put(1, "A");
		cache.put(2, "B");
		cache.put(3, "C");
		cache.put(4, "D");
		cache.put(5, "E");

		assertEquals(Long.MIN_VALUE + 5, cache.idCtr);
		assertEquals(null, cache.get(1));
		assertEquals("B", cache.get(2));
		assertEquals("C", cache.get(3));
		assertEquals("D", cache.get(4));
		assertEquals("E", cache.get(5));
	}

	/**
	 * This confirms that our timer will automatically purge older elements
	 */
	@Test
	public void testTimer() throws Exception {
		Cache<Integer, String> cache = new Cache<>(1000, 100, 50);
		cache.put(1, "A");
		cache.put(2, "B");
		assertEquals(2, cache.size());
		assertTrue(cache.contains(1));
		assertTrue(cache.contains(2));
		assertFalse(cache.contains(3));
		assertFalse(cache.contains(4));
		assertFalse(cache.contains(5));

		Thread.sleep(200);

		cache.put(3, "C");
		cache.put(4, "D");
		cache.put(5, "E");
		assertEquals(3, cache.size());
		assertFalse(cache.contains(1));
		assertFalse(cache.contains(2));
		assertTrue(cache.contains(3));
		assertTrue(cache.contains(4));
		assertTrue(cache.contains(5));
	}
}
