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

import java.awt.Point;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

public class PointIteratorTest extends TestCase {

	Point[] expectedPoints = new Point[] { new Point(0, 0), new Point(1, 0),
			new Point(1, 1), new Point(0, 1), new Point(-1, 1),
			new Point(-1, 0), new Point(-1, -1), new Point(0, -1),
			new Point(1, -1), new Point(2, -1), new Point(2, 0),
			new Point(2, 1), new Point(2, 2), new Point(1, 2), new Point(0, 2),
			new Point(-1, 2), new Point(-2, 2), new Point(-2, 1),
			new Point(-2, 0), new Point(-2, -1), new Point(-2, -2),
			new Point(-1, -2), new Point(0, -2), new Point(1, -2),
			new Point(2, -2), new Point(3, -2), new Point(3, -1),
			new Point(3, 0), new Point(3, 1), new Point(3, 2), new Point(3, 3),
			new Point(2, 3), new Point(1, 3), new Point(0, 3), new Point(-1, 3),
			new Point(-2, 3), new Point(-3, 3) };

	public void testIterator_origin() {
		PointIterator iter = new PointIterator(0, 0, expectedPoints.length);

		for (Point expectedPoint : expectedPoints) {
			assertTrue(iter.hasNext());
			assertEquals(expectedPoint, iter.next());
		}
		assertFalse(iter.hasNext());

		try {
			iter.next();
			fail();
		} catch (NoSuchElementException e) {
			// pass
		}

	}

	public void testIterator_offset() {
		int cx = 131;
		int cy = -99;
		PointIterator iter = new PointIterator(cx, cy, expectedPoints.length);

		for (Point expectedPoint : expectedPoints) {
			assertTrue(iter.hasNext());
			assertEquals(new Point(expectedPoint.x + cx, expectedPoint.y + cy),
					iter.next());
		}
		assertFalse(iter.hasNext());

		try {
			iter.next();
			fail();
		} catch (NoSuchElementException e) {
			// pass
		}
	}

	public void testGetPoint() {
		// one round of the spiral (based on a paper chart):
		assertEquals(new Point(2, -1), PointIterator.getPoint(9));
		assertEquals(new Point(2, 0), PointIterator.getPoint(10));
		assertEquals(new Point(2, 1), PointIterator.getPoint(11));
		assertEquals(new Point(2, 2), PointIterator.getPoint(12));
		assertEquals(new Point(1, 2), PointIterator.getPoint(13));
		assertEquals(new Point(0, 2), PointIterator.getPoint(14));
		assertEquals(new Point(-1, 2), PointIterator.getPoint(15));
		assertEquals(new Point(-2, 2), PointIterator.getPoint(16));
		assertEquals(new Point(-2, 1), PointIterator.getPoint(17));
		assertEquals(new Point(-2, 0), PointIterator.getPoint(18));
		assertEquals(new Point(-2, -1), PointIterator.getPoint(19));
		assertEquals(new Point(-2, -2), PointIterator.getPoint(20));
		assertEquals(new Point(-1, -2), PointIterator.getPoint(21));
		assertEquals(new Point(0, -2), PointIterator.getPoint(22));
		assertEquals(new Point(1, -2), PointIterator.getPoint(23));
		assertEquals(new Point(2, -2), PointIterator.getPoint(24));
		assertEquals(new Point(3, -2), PointIterator.getPoint(25));

		PointIterator iter = new PointIterator(0, 0, 1_000_000);
		int ctr = 0;
		while (iter.hasNext()) {
			Point p = iter.next();
			assertEquals(p, PointIterator.getPoint(ctr));
			ctr++;
		}
	}
}