package com.pump.util;

import junit.framework.TestCase;

import org.junit.Test;

import com.pump.util.HumanStringComparator.ComparatorToken;

public class HumanStringComparatorTest extends TestCase {

	// test the package-level parsing methods:

	@Test
	public void testGetDigitClusterLength() {
		HumanStringComparator c = new HumanStringComparator();
		assertEquals(3, c.getDigitClusterLength("123".toCharArray(), 0));
		assertEquals(3, c.getDigitClusterLength("123x".toCharArray(), 0));
		assertEquals(0, c.getDigitClusterLength("x123".toCharArray(), 0));
		assertEquals(3, c.getDigitClusterLength("x123".toCharArray(), 1));
		assertEquals(3, c.getDigitClusterLength("x123x".toCharArray(), 1));
		assertEquals(0, c.getDigitClusterLength("xxx".toCharArray(), 1));
		assertEquals(1, c.getDigitClusterLength("x1xx".toCharArray(), 1));
	}

	@Test
	public void testParser() {
		HumanStringComparator c = new HumanStringComparator();

		{
			ComparatorToken[] t = c.parse("123");
			assertEquals(1, t.length);
			assertEquals("123", t[0].text);
		}

		{
			ComparatorToken[] t = c.parse("abc");
			assertEquals(1, t.length);
			assertEquals("abc", t[0].text);
		}

		{
			ComparatorToken[] t = c.parse(" \t\n\r");
			assertEquals(1, t.length);
			assertEquals(" \t\n\r", t[0].text);
		}

		{
			ComparatorToken[] t = c.parse("123abc");
			assertEquals(2, t.length);
			assertEquals("123", t[0].text);
			assertEquals("abc", t[1].text);
		}

		{
			ComparatorToken[] t = c.parse("123 abc");
			assertEquals(3, t.length);
			assertEquals("123", t[0].text);
			assertEquals(" ", t[1].text);
			assertEquals("abc", t[2].text);
		}

		{
			ComparatorToken[] t = c.parse(".123 .456.jpg");
			assertEquals(4, t.length);
			assertEquals(".123", t[0].text);
			assertEquals(" ", t[1].text);
			assertEquals(".456", t[2].text);
			assertEquals(".jpg", t[3].text);
		}

		{
			ComparatorToken[] t = c.parse("99.123 44.456.jpg");
			assertEquals(4, t.length);
			assertEquals("99.123", t[0].text);
			assertEquals(" ", t[1].text);
			assertEquals("44.456", t[2].text);
			assertEquals(".jpg", t[3].text);
		}

		{
			ComparatorToken[] t = c.parse("123.456.789.pdf");
			assertEquals(3, t.length);
			assertEquals("123.456", t[0].text);
			assertEquals(".789", t[1].text);
			assertEquals(".pdf", t[2].text);
		}
	}

	// test end results:

	@Test
	public void testSequence() {
		HumanStringComparator c = new HumanStringComparator();
		// this is how a String comparator would normally see these filenames:
		assertTrue("cat 9.jpg".compareTo("cat 10.jpg") > 0);

		// test that our comparator sees them "correctly":
		assertTrue(c.humanCompare("cat 9.jpg", "cat 10.jpg") < 0);
	}

	@Test
	public void testDates() {
		HumanStringComparator c = new HumanStringComparator();
		// this is how a String comparator would normally see these dates:
		assertTrue("8/1/15".compareTo("08/02/15") > 0);

		// test that our comparator sees them "correctly":
		assertTrue(c.humanCompare("8/1/15", "08/02/15") < 0);
	}

	@Test
	public void testSingleQuotes() {
		HumanStringComparator c = new HumanStringComparator();
		// this is how a String comparator would normally see these dates:
		assertTrue("'cat'".compareTo("‘cat’") < 0);

		// test that our comparator sees them "correctly":
		assertTrue(c.humanCompare("'cat'", "‘cat’") == 0);
	}
}
