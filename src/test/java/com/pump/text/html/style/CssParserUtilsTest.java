package com.pump.text.html.style;

import org.junit.Test;

import com.pump.text.html.css.CssParserUtils;

import junit.framework.TestCase;

public class CssParserUtilsTest extends TestCase {

	@Test
	public void testGetClosingParentheses() {
		String str = "hello(world)";

		StringBuilder sb = new StringBuilder();
		assertEquals(11, CssParserUtils.getClosingParentheses(str, 5, sb));
		assertEquals("world", sb.toString());
	}

	@Test
	public void testGetClosingParentheses_nested() {
		String str = "hello(wor()ld)";

		StringBuilder sb = new StringBuilder();
		assertEquals(13, CssParserUtils.getClosingParentheses(str, 5, sb));
		assertEquals("wor()ld", sb.toString());
	}

	/**
	 * This tests has two closing parentheses so the parentheses are unbalanced,
	 * but our parser only cares about matching the first opening parentheses to
	 * the first closing parentheses so this is OK. (At least as far as this
	 * method is concerned.)
	 */
	@Test
	public void testGetClosingParentheses_unbalanced_but_passing() {
		String str = "hello(wor)ld)";

		StringBuilder sb = new StringBuilder();
		assertEquals(9, CssParserUtils.getClosingParentheses(str, 5, sb));
		assertEquals("wor", sb.toString());
	}

	@Test
	public void testGetClosingParentheses_fail_wrong_start() {
		String str = "hello(world";

		try {
			CssParserUtils.getClosingParentheses(str, 4, null);
			fail();
		} catch (Exception e) {
			// pass
		}
	}

	@Test
	public void testGetClosingParentheses_fail_unclosed() {
		String str = "hello(world";

		try {
			CssParserUtils.getClosingParentheses(str, 5, null);
			fail();
		} catch (Exception e) {
			// pass
		}
	}

	@Test
	public void testGetClosingParentheses_fail_unclosed_nested() {
		String str = "hello(wor(ld)";

		try {
			CssParserUtils.getClosingParentheses(str, 5, null);
			fail();
		} catch (Exception e) {
			// pass
		}
	}
}
