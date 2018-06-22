package com.pump.text;

import java.util.Arrays;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

public class WildcardPatternTest extends TestCase {

	/**
	 * Test the most basic usage for the WildcardPattern: "*"
	 */
	public void testFilename1() {
		WildcardPattern p = new WildcardPattern("*");
		assertTrue(p.matches("file.jpg"));
	}

	/**
	 * Test the second-most basic usage for the WildcardPattern: "*.jpg"
	 */
	public void testFilename2() {
		WildcardPattern p = new WildcardPattern("*.jpg");
		assertTrue(p.matches("file.jpg"));
		assertTrue(p.matches("file.JPG"));
		assertFalse(p.matches("file.png"));
		assertFalse(p.matches("file.jpeg"));
	}

	/**
	 * Test an empty WildcardPattern.
	 */
	public void testEmptyPattern() {
		WildcardPattern p = new WildcardPattern("");
		assertTrue(p.matches(""));
		assertFalse(p.matches("-"));
	}

	/**
	 * Test simple WildcardPatterns against empty Strings.
	 */
	public void testEmptyStrings() {
		WildcardPattern p;
		p = new WildcardPattern("a");
		assertFalse(p.matches(""));

		p = new WildcardPattern("[a]");
		assertFalse(p.matches(""));

		p = new WildcardPattern("[a,z]");
		assertFalse(p.matches(""));

		p = new WildcardPattern("[a-z]");
		assertFalse(p.matches(""));

		p = new WildcardPattern("?");
		assertFalse(p.matches(""));

		p = new WildcardPattern("*");
		assertTrue(p.matches(""));
	}

	public void testPair1() {
		WildcardPattern pattern = new WildcardPattern("**");
		assertTrue(pattern.matches(""));
		assertTrue(pattern.matches("abcdefghijklmnop"));
	}

	public void testPair2() {
		WildcardPattern pattern = new WildcardPattern("[a,b][c-d]");
		assertFalse(pattern.matches(""));
		assertFalse(pattern.matches("a"));
		assertFalse(pattern.matches("d"));
		assertTrue(pattern.matches("ac"));
		assertTrue(pattern.matches("ad"));
		assertTrue(pattern.matches("bc"));
		assertTrue(pattern.matches("bd"));
		assertFalse(pattern.matches("cd"));
		assertFalse(pattern.matches("ab"));
	}

	public void testPair3() {
		WildcardPattern pattern = new WildcardPattern("??");
		assertFalse(pattern.matches(""));
		assertFalse(pattern.matches("a"));
		assertFalse(pattern.matches("d"));
		assertTrue(pattern.matches("az"));
		assertTrue(pattern.matches("  "));
		assertFalse(pattern.matches("xyz"));
		assertFalse(pattern.matches("   "));
	}

	public void testExactMatch() {
		WildcardPattern pattern = new WildcardPattern("think");
		assertTrue(pattern.matches("think"));
	}

	public void testSubset1() {
		WildcardPattern pattern = new WildcardPattern("thin");
		assertFalse(pattern.matches("think"));
	}

	public void testSubset2() {
		WildcardPattern pattern = new WildcardPattern("think");
		assertFalse(pattern.matches("thin"));
	}

	public void testSquareBracket() {
		WildcardPattern pattern = new WildcardPattern("[h,p]okey");
		assertTrue(pattern.matches("hokey"));
		assertTrue(pattern.matches("pokey"));
		assertFalse(pattern.matches("lokey"));
		assertFalse(pattern.matches("smokey"));
	}

	public void testCombo1() {
		WildcardPattern pattern = new WildcardPattern("*?*");

		// here ? matches the 'a', and the *'s match zero-length strings
		assertTrue(pattern.matches("a"));

		assertFalse(pattern.matches(""));
		assertTrue(pattern.matches("ab"));
		assertTrue(pattern.matches("abcdefgh"));
	}

	public void testCombo2() {
		WildcardPattern pattern = new WildcardPattern("*?");

		assertTrue(pattern.matches("a"));
		assertFalse(pattern.matches(""));
	}

	public void testCombo3() {
		WildcardPattern pattern = new WildcardPattern("?*?");

		assertTrue(pattern.matches("xy"));
		assertFalse(pattern.matches("x"));
	}

	/**
	 * This makes sure the optimized getMatches(..) method returns the
	 * appropriate results.
	 */
	public void testGetMatches() {
		TreeSet<String> set = new TreeSet<>();
		set.add("button");
		set.add("butter");
		set.add("butterfly");
		set.add("butternut");
		set.add("buttered");
		set.add("butterbeer");
		set.add("cutter");
		set.add("clutter");
		set.add("hatter");
		set.add("guttersnipe");

		assertEquals(
				new TreeSet<>(Arrays.asList("butter", "buttered", "butterfly",
						"butternut", "butterbeer")), new WildcardPattern(
						"butter*").getMatches(set));
		assertEquals(
				new TreeSet<>(Arrays.asList("butter", "clutter", "cutter")),
				new WildcardPattern("*utter").getMatches(set));
		assertEquals(new TreeSet<>(Arrays.asList("clutter", "cutter")),
				new WildcardPattern("c*utter").getMatches(set));
		assertEquals(
				new TreeSet<>(Arrays.asList("buttered", "butterbeer",
						"guttersnipe")),
				new WildcardPattern("[b-g]utter*e*").getMatches(set));
	}

	/**
	 * This tests several patterns/matches involving 1 asterisk and confirms
	 * that we only call the package-level match method once.
	 * <p>
	 * This test is concerned with the efficiency of the WildcardPattern class;
	 * other tests exist to confirm its accuracy.
	 */
	public void testSingleStarWildcardMatchInvocationCount1() {
		assertEquals(1, getMatchInvocationCount("butter*", "butterfly"));
		assertEquals(1, getMatchInvocationCount("butter*", "butter"));
		assertEquals(1, getMatchInvocationCount("butter*", "buttle"));

		assertEquals(1, getMatchInvocationCount("*utter", "butter"));
		assertEquals(1, getMatchInvocationCount("*utter", "cutter"));
		assertEquals(1, getMatchInvocationCount("*utter", "clutter"));
		assertEquals(1, getMatchInvocationCount("*utter", "guttersnipe"));

		assertEquals(1, getMatchInvocationCount("c*utter", "cutter"));
		assertEquals(1, getMatchInvocationCount("c*utter", "clutter"));
		assertEquals(1, getMatchInvocationCount("c*utter", "flutter"));
	}

	/**
	 * A pattern with two asterisks usually requires more work
	 */
	public void testMultipleStarWildcardMatchInvocationCount() {
		assertTrue(getMatchInvocationCount("*u*ter", "butter") > 1);
		assertTrue(getMatchInvocationCount("*u*ter", "flutter") > 1);
		assertTrue(getMatchInvocationCount("*u*ter", "rebuke after") > 1);

		// but this one ends with "rer" and not "ter", so we should immediately
		// strike out
		assertEquals(1, getMatchInvocationCount("*u*ter", "mutterer"));
	}

	private int getMatchInvocationCount(String pattern, String phrase) {
		final AtomicInteger actualMatchesInvocationCount = new AtomicInteger(0);
		WildcardPattern p = new WildcardPattern(pattern) {

			@Override
			boolean matches(CharSequence string, int stringMinIndex,
					int stringMaxIndex, Placeholder[] placeholders,
					int placeholderMinIndex, int placeholderMaxIndex) {
				actualMatchesInvocationCount.incrementAndGet();
				return super.matches(string, stringMinIndex, stringMaxIndex,
						placeholders, placeholderMinIndex, placeholderMaxIndex);
			}

		};
		p.matches(phrase);
		return actualMatchesInvocationCount.intValue();
	}
}
