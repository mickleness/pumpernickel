package com.pump.text.html.style;

import java.awt.Color;

import junit.framework.TestCase;

/**
 * These test the CSS color parser class.
 */
public class CssColorPropertyHandlerTest extends TestCase {

	/*
	 * These "pink" tests are based on
	 * https://developer.mozilla.org/en-US/docs/Web/CSS/color_value.
	 */

	void assertPink(String str) {
		Color pink = new Color(255, 0, 153);
		Color c = new CssColorPropertyHandler().parse(str);
		assertEquals(pink, c);
	}

	public void testPink_hex_3chars_lower() {
		assertPink("#f09");
	}

	public void testPink_hex_3chars_upper() {
		assertPink("#F09");
	}

	public void testPink_hex_6chars_lower() {
		assertPink("#ff0099");
	}

	public void testPink_hex_6chars_upper() {
		assertPink("#FF0099");
	}

	public void testPink_rgb_no_space() {
		assertPink("rgb(255,0,153)");
	}

	public void testPink_rgb_with_space() {
		assertPink("rgb(255, 0, 153)");
	}

	public void testPink_rgb_with_decimal() {
		assertPink("rgb(255, 0, 153.0)");
	}

	public void testPink_rgb_percent_no_space() {
		assertPink("rgb(100%,0%,60%)");
	}

	public void testPink_rgb_percent_with_space() {
		assertPink("rgb(100%, 0%, 60%)");
	}

	public void testPink_rgb_mixed_percent_and_int() {
		// let's support this, although it's highly discouraged.
		// I'm not sure if it's supposed to result in an error?
		assertPink("rgb(100%, 0, 60%)");
	}

	public void testPink_rgb_functional() {
		assertPink("rgb(255 0 153)");
	}

	public void testPink_hex_alpha_4chars_lower() {
		assertPink("#f09f");
	}

	public void testPink_hex_alpha_4chars_upper() {
		assertPink("#F09F");
	}

	public void testPink_hex_alpha_8chars_lower() {
		assertPink("#ff0099ff");
	}

	public void testPink_hex_alpha_8chars_upper() {
		assertPink("#FF0099FF");
	}

	public void testPink_rgb_functional_alpha() {
		assertPink("rgb(255, 0, 153, 1)");
	}

	public void testPink_rgb_functional_alpha_with_percent() {
		assertPink("rgb(255, 0, 153, 100%)");
	}

	public void testPink_rgb_whitespace_alpha() {
		assertPink("rgb(255 0 153 / 1)");
	}

	public void testPink_rgb_whitespace_alpha_with_percent() {
		assertPink("rgb(255 0 153 / 100%)");
	}

	public void testPink_rgb_functional_float() {
		assertPink("rgb(255, 0, 153.6, 1)");
	}

	/*
	 * These "cyan" tests are derived from
	 * https://css-tricks.com/converting-color-spaces-in-javascript/
	 */

	void assertCyan(String str) {
		Color cyan = new Color(0, 255, 255, 255);
		Color c = new CssColorPropertyHandler().parse(str);
		assertSimilar(cyan, c, 1);
	}

	private void assertSimilar(Color c1, Color c2, int maxChannelDifference) {
		int rDelta = Math.abs(c1.getRed() - c2.getRed());
		int gDelta = Math.abs(c1.getGreen() - c2.getGreen());
		int bDelta = Math.abs(c1.getBlue() - c2.getBlue());
		int aDelta = Math.abs(c1.getAlpha() - c2.getAlpha());
		if (rDelta > maxChannelDifference)
			fail("the reds " + c1.getRed() + " and " + c2.getRed()
					+ " are too far apart (" + rDelta + ">"
					+ maxChannelDifference + ")");
		if (gDelta > maxChannelDifference)
			fail("the greens " + c1.getGreen() + " and " + c2.getGreen()
					+ " are too far apart (" + gDelta + ">"
					+ maxChannelDifference + ")");
		if (bDelta > maxChannelDifference)
			fail("the blues " + c1.getBlue() + " and " + c2.getBlue()
					+ " are too far apart (" + bDelta + ">"
					+ maxChannelDifference + ")");
		if (aDelta > maxChannelDifference)
			fail("the alphas " + c1.getAlpha() + " and " + c2.getAlpha()
					+ " are too far apart (" + aDelta + ">"
					+ maxChannelDifference + ")");
	}

	public void testCyan_hsl_implied_degrees() {
		assertCyan("hsl(180 100% 50%)");
	}

	public void testCyan_hsl_degrees_comma() {
		assertCyan("hsl(180deg,100%,50%)");
	}

	public void testCyan_hsl_degrees_space() {
		assertCyan("hsl(180deg 100% 50%)");
	}

	public void testCyan_hsl_radians_comma() {
		assertCyan("hsl(3.14rad,100%,50%)");
	}

	public void testCyan_hsl_radians_space() {
		assertCyan("hsl(3.14rad 100% 50%)");
	}

	public void testCyan_hsl_turns_comma() {
		assertCyan("hsl(3.14rad,100%,50%)");
	}

	public void testCyan_hsl_turns_space() {
		assertCyan("hsl(0.5turn 100% 50%)");
	}

	// improvised tests:

	public void testCyan_hsl_negative_hue() {
		assertCyan("hsl(-180 100% 50%)");
	}

	public void testCyan_hsl_negative_hue2() {
		assertCyan("hsl(-540 100% 50%)");
	}

	public void testCyan_hsl_overflow_hue() {
		assertCyan("hsl(540 100% 50%)");
	}

	public void testCyan_hsl_overflow_hue2() {
		assertCyan("hsl(900 100% 50%)");
	}
}
