package com.pump.text.html.css.background;

import com.pump.text.html.css.CssLength;

import junit.framework.TestCase;

public class CssBackgroundPositionValueTest extends TestCase {

	/**
	 * Make sure two positions are considered equal whether they start from the
	 * top-left or the bottom-right.
	 */
	public void test_equals_percent() {
		CssBackgroundPositionValue fromTopLeft = new CssBackgroundPositionValue(
				"", new CssLength(10, "%"), true, new CssLength(20, "%"), true);
		CssBackgroundPositionValue fromBottomRight = new CssBackgroundPositionValue(
				"", new CssLength(90, "%"), false, new CssLength(80, "%"),
				false);
		assertEquals(fromTopLeft, fromBottomRight);
	}

	/**
	 * Repeat test_equals_percent, but use a "px" for one term instead of "%".
	 */
	public void test_equals_px() {
		CssBackgroundPositionValue fromTopLeft = new CssBackgroundPositionValue(
				"", new CssLength(10, "%"), true, new CssLength(20, "px"),
				true);
		CssBackgroundPositionValue fromBottomRight = new CssBackgroundPositionValue(
				"", new CssLength(90, "%"), false, new CssLength(80, "%"),
				false);
		assertFalse(fromTopLeft.equals(fromBottomRight));
	}

}
