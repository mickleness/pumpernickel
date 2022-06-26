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

public class FractionTest extends TestCase {

	public void testCompareTo() {
		Fraction f1 = new Fraction(1, 2);
		Fraction f2 = new Fraction(3, 4);
		assertTrue(f1.compareTo(f2) < 0);
		assertTrue(f2.compareTo(f1) > 0);
	}

	public void testIsInteger() {
		Fraction f1 = new Fraction(5, 5);
		Fraction f2 = new Fraction(0, 5);
		Fraction f3 = new Fraction(-5, 5);
		Fraction f4 = new Fraction(10, 5);
		assertTrue(f1.isInteger());
		assertTrue(f2.isInteger());
		assertTrue(f3.isInteger());
		assertTrue(f4.isInteger());

		Fraction f5 = new Fraction(6, 5);
		Fraction f6 = new Fraction(2, 5);
		Fraction f7 = new Fraction(-1, 5);
		Fraction f8 = new Fraction(151, 5);
		assertFalse(f5.isInteger());
		assertFalse(f6.isInteger());
		assertFalse(f7.isInteger());
		assertFalse(f8.isInteger());
	}
}