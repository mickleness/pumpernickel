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
package com.pump.math;

import junit.framework.TestCase;

import org.junit.Test;

public class RangeTest extends TestCase {

	static boolean[] booleans = new boolean[] { true, false };

	@Test
	public void testSeparate_scenario1() {
		Range<Integer> r1 = new Range<>(0, 10, true, true);
		Range<Integer> r2 = new Range<>(20, 30, true, true);
		assertNull(r1.and(r2));
		assertNull(r2.and(r1));
		assertNull(r1.or(r2));
		assertNull(r2.or(r1));
	}

	@Test
	public void testSeparate_scenario2() {
		Range<Integer> r1 = new Range<>(0, 10, true, false);
		Range<Integer> r2 = new Range<>(10, 20, false, true);
		assertNull(r1.and(r2));
		assertNull(r2.and(r1));
		assertNull(r1.or(r2));
		assertNull(r2.or(r1));
	}

	@Test
	public void testSeparate_scenario3() {
		Range<Integer> r1 = new Range<>(null, 10, true, false);
		Range<Integer> r2 = new Range<>(100, null, false, true);
		assertNull(r1.and(r2));
		assertNull(r2.and(r1));
		assertNull(r1.or(r2));
		assertNull(r2.or(r1));
	}

	@Test
	public void testAdjacent_scenario1() {
		Range<Integer> r1 = new Range<>(0, 10, true, true);
		Range<Integer> r2 = new Range<>(10, 20, false, true);
		assertNull(r1.and(r2));
		assertNull(r2.and(r1));
		assertEquals(new Range<Integer>(0, 20, true, true), r1.or(r2));
		assertEquals(new Range<Integer>(0, 20, true, true), r2.or(r1));
	}

	@Test
	public void testAdjacent_scenario2() {
		Range<Integer> r1 = new Range<>(0, 10, true, false);
		Range<Integer> r2 = new Range<>(10, 20, true, true);
		assertNull(r1.and(r2));
		assertNull(r2.and(r1));
		assertEquals(new Range<Integer>(0, 20, true, true), r1.or(r2));
		assertEquals(new Range<Integer>(0, 20, true, true), r2.or(r1));
	}

	@Test
	public void testAdjacent_scenario3() {
		Range<Integer> r1 = new Range<>(null, 10, true, false);
		Range<Integer> r2 = new Range<>(10, null, true, true);
		assertNull(r1.and(r2));
		assertNull(r2.and(r1));
		assertEquals(new Range<Integer>(null, null, false, false), r1.or(r2));
		assertEquals(new Range<Integer>(null, null, false, false), r2.or(r1));
	}

	@Test
	public void testOverlap_scenario1() {
		for (boolean include0 : booleans) {
			for (boolean include10 : booleans) {
				for (boolean include5 : booleans) {
					for (boolean include15 : booleans) {
						Range<Integer> r1 = new Range<>(0, 10, include0,
								include10);
						Range<Integer> r2 = new Range<>(5, 15, include5,
								include15);
						assertEquals(new Range<Integer>(5, 10, include5,
								include10), r1.and(r2));
						assertEquals(new Range<Integer>(5, 10, include5,
								include10), r2.and(r1));
						assertEquals(new Range<Integer>(0, 15, include0,
								include15), r1.or(r2));
						assertEquals(new Range<Integer>(0, 15, include0,
								include15), r2.or(r1));
					}
				}
			}
		}
	}

	@Test
	public void testOverlap_scenario2() {
		Range<Integer> r1 = new Range<>(null, 10, true, false);
		Range<Integer> r2 = new Range<>(0, null, true, true);
		assertEquals(new Range<Integer>(0, 10, true, false), r1.and(r2));
		assertEquals(new Range<Integer>(0, 10, true, false), r2.and(r1));
		assertEquals(new Range<Integer>(null, null, true, true), r1.or(r2));
		assertEquals(new Range<Integer>(null, null, true, true), r2.or(r1));
	}

	@Test
	public void testOverlap_scenario3() {
		Range<Integer> r1 = new Range<>(null, 10, false, true);
		Range<Integer> r2 = new Range<>(0, 5, false, false);
		assertEquals(new Range<Integer>(0, 5, false, false), r1.and(r2));
		assertEquals(new Range<Integer>(0, 5, false, false), r2.and(r1));
		assertEquals(new Range<Integer>(null, 10, true, true), r1.or(r2));
		assertEquals(new Range<Integer>(null, 10, true, true), r2.or(r1));
	}

	@Test
	public void testOverlap_scenario4() {
		Range<Integer> r1 = new Range<>(5, null, false, true);
		Range<Integer> r2 = new Range<>(0, 10, false, false);
		assertEquals(new Range<Integer>(5, 10, false, false), r1.and(r2));
		assertEquals(new Range<Integer>(5, 10, false, false), r2.and(r1));
		assertEquals(new Range<Integer>(0, null, false, true), r1.or(r2));
		assertEquals(new Range<Integer>(0, null, false, true), r2.or(r1));
	}

	@Test
	public void testSubset_scenario1() {
		for (boolean include0 : booleans) {
			for (boolean include100 : booleans) {
				for (boolean include20 : booleans) {
					for (boolean include30 : booleans) {
						Range<Integer> r1 = new Range<>(0, 100, include0,
								include100);
						Range<Integer> r2 = new Range<>(20, 30, include20,
								include30);
						assertEquals(new Range<Integer>(20, 30, include20,
								include30), r1.and(r2));
						assertEquals(new Range<Integer>(20, 30, include20,
								include30), r2.and(r1));
						assertEquals(new Range<Integer>(0, 100, include0,
								include100), r1.or(r2));
						assertEquals(new Range<Integer>(0, 100, include0,
								include100), r2.or(r1));
					}
				}
			}
		}
	}

	@Test
	public void testSubset_scenario2() {
		Range<Integer> r1 = new Range<>(0, null, true, false);
		Range<Integer> r2 = new Range<>(5, 10, true, false);
		assertEquals(new Range<Integer>(5, 10, true, false), r1.and(r2));
		assertEquals(new Range<Integer>(5, 10, true, false), r2.and(r1));
		assertEquals(new Range<Integer>(0, null, true, true), r1.or(r2));
		assertEquals(new Range<Integer>(0, null, true, true), r2.or(r1));
	}

	@Test
	public void testSubset_scenario3() {
		Range<Integer> r1 = new Range<>(null, 100, true, false);
		Range<Integer> r2 = new Range<>(5, 10, true, false);
		assertEquals(new Range<Integer>(5, 10, true, false), r1.and(r2));
		assertEquals(new Range<Integer>(5, 10, true, false), r2.and(r1));
		assertEquals(new Range<Integer>(null, 100, true, false), r1.or(r2));
		assertEquals(new Range<Integer>(null, 100, true, false), r2.or(r1));
	}

	@Test
	public void testIncludeEndpoints_scenario1() {
		Range<Integer> r1 = new Range<>(0, 100, false, false);
		Range<Integer> r2 = new Range<>(0, 100, true, true);

		assertEquals(r1, r1.and(r2));
		assertEquals(r1, r2.and(r1));

		assertEquals(r2, r1.or(r2));
		assertEquals(r2, r2.or(r1));

		assertFalse(r1.equals(r2));
		assertFalse(r2.equals(r1));
	}

	@Test
	public void testIncludeEndpoints_scenario2() {
		Range<Integer> r1 = new Range<>(null, 100, false, false);
		Range<Integer> r2 = new Range<>(null, 100, true, true);

		assertEquals(r1, r1.and(r2));
		assertEquals(r1, r2.and(r1));

		assertEquals(r2, r1.or(r2));
		assertEquals(r2, r2.or(r1));

		assertFalse(r1.equals(r2));
		assertFalse(r2.equals(r1));
	}

	@Test
	public void testIncludeEndpoints_scenario3() {
		Range<Integer> r1 = new Range<>(0, null, false, false);
		Range<Integer> r2 = new Range<>(0, null, true, true);

		assertEquals(r1, r1.and(r2));
		assertEquals(r1, r2.and(r1));

		assertEquals(r2, r1.or(r2));
		assertEquals(r2, r2.or(r1));

		assertFalse(r1.equals(r2));
		assertFalse(r2.equals(r1));
	}

	@Test
	public void testInfiniteScenario() {
		Range<Integer> r1 = new Range<>(null, null, false, false);
		Range<Integer> r2 = new Range<>(0, 100, true, true);

		assertEquals(r2, r1.and(r2));
		assertEquals(r2, r2.and(r1));
		assertEquals(r1, r1.or(r2));
		assertEquals(r1, r2.or(r1));
	}
}