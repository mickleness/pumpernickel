package com.pump.util;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * These tests make sure garbage collection changes the state of a WeakValueMap.
 */
public class WeakValueMapTest extends TestCase {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testPutGet() throws Exception {
		Object value = new Object();
		WeakValueMap map = new WeakValueMap();
		map.put("1", value);

		assertEquals(value, map.get("1"));
		assertEquals(null, map.get("2"));

		// a garbage collection here should make no difference:
		System.gc();

		assertEquals(value, map.get("1"));
		assertEquals(1, map.map.size());

		value = null;

		// now we've nullified value, so our map should change
		System.gc();

		assertEquals(null, map.get("1"));
		assertEquals(0, map.size());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testContains() throws Exception {
		Object value = new Object();
		WeakValueMap map = new WeakValueMap();
		map.put("1", value);

		assertTrue(map.containsKey("1"));
		assertFalse(map.containsKey("0"));

		// a garbage collection here should make no difference:
		System.gc();

		assertTrue(map.containsKey("1"));

		value = null;

		// now we've nullified value, so our map should change
		System.gc();

		assertFalse(map.containsKey("1"));
		assertEquals(0, map.size());
	}
}
