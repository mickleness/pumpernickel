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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import junit.framework.TestCase;

import org.junit.Test;

public class NestedDataIteratorTest extends TestCase {
	String word1 = "thanks";
	String word2 = "for";
	String word3 = "all";
	String word4 = "the";
	String word5 = "fish";

	@SuppressWarnings("rawtypes")
	@Test
	public void testSimpleCollection() {
		Collection c = Arrays.asList(word1, word2, word3, word4, word5, 0, 'z',
				null, word3);
		NestedDataIterator<String> iter = new NestedDataIterator<>(
				String.class, c);
		Collection<String> results = toList(iter);
		assertEquals(6, results.size());
		assertTrue(results.contains(word1));
		assertTrue(results.contains(word2));
		assertTrue(results.contains(word3));
		assertTrue(results.contains(word4));
		assertTrue(results.contains(word5));

		assertEquals(2, count(results, word3));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testSimpleMap() {
		Map map = new HashMap();
		map.put(1, word1);
		map.put(word2, null);
		map.put(3, word3);
		map.put(null, word4);
		map.put(word5, 5);
		map.put(6, word3);

		NestedDataIterator<String> iter = new NestedDataIterator<>(
				String.class, map);
		Collection<String> results = toList(iter);
		assertEquals(6, results.size());
		assertTrue(results.contains(word1));
		assertTrue(results.contains(word2));
		assertTrue(results.contains(word3));
		assertTrue(results.contains(word4));
		assertTrue(results.contains(word5));

		assertEquals(2, count(results, word3));
	}

	@Test
	public void testSimpleArray() {
		Object[] array = new Object[] { word1, null, word2, word3, 'z', word4,
				word5, word3 };

		NestedDataIterator<String> iter = new NestedDataIterator<>(
				String.class, array);
		Collection<String> results = toList(iter);
		assertEquals(6, results.size());
		assertTrue(results.contains(word1));
		assertTrue(results.contains(word2));
		assertTrue(results.contains(word3));
		assertTrue(results.contains(word4));
		assertTrue(results.contains(word5));

		assertEquals(2, count(results, word3));
	}

	/**
	 * Test 3-layers of map that ultimately contain an array and a List.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testComplexStructure1() {
		Map map1 = new HashMap();
		Map map2 = new HashMap();
		Map map3 = new HashMap();
		map1.put(1, map2);
		map2.put(word1, word2);
		map2.put(word3, map3);
		map3.put('z', new Object[] { word4, word5 });
		map3.put('y', Arrays.asList(word3, word3));

		NestedDataIterator<String> iter = new NestedDataIterator<>(
				String.class, map1);
		Collection<String> results = toList(iter);
		assertEquals(7, results.size());
		assertTrue(results.contains(word1));
		assertTrue(results.contains(word2));
		assertTrue(results.contains(word3));
		assertTrue(results.contains(word4));
		assertTrue(results.contains(word5));

		assertEquals(3, count(results, word3));
	}

	/**
	 * Test a list that contains raw elements, a map containing an array and an
	 * array containing a map.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testComplexStructure2() {
		List list = new LinkedList();
		Map map1 = new HashMap();
		Map map2 = new HashMap();

		map1.put(word2, null);
		map2.put(new String[] { word4, word5 }, word3);

		list.add(word1);
		list.add(null);
		list.add(new Object[] { map1, word3, word3 });
		list.add(map2);

		NestedDataIterator<String> iter = new NestedDataIterator<>(
				String.class, list);
		Collection<String> results = toList(iter);
		assertEquals(7, results.size());
		assertTrue(results.contains(word1));
		assertTrue(results.contains(word2));
		assertTrue(results.contains(word3));
		assertTrue(results.contains(word4));
		assertTrue(results.contains(word5));

		assertEquals(3, count(results, word3));
	}

	/**
	 * Convert the elements from an Iterator into a List
	 */
	private static <T> List<T> toList(Iterator<T> iter) {
		List<T> c = new LinkedList<>();
		while (iter.hasNext()) {
			c.add(iter.next());
		}
		return c;
	}

	/**
	 * Count the number of times an element occurs in a collection of results.
	 */
	private static <T> int count(Collection<T> results, T element) {
		int sum = 0;
		Iterator<T> iter = results.iterator();
		while (iter.hasNext()) {
			if (Objects.equals(element, iter.next()))
				sum++;
		}
		return sum;
	}
}