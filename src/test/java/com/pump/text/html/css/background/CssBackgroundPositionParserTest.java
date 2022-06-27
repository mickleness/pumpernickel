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
package com.pump.text.html.css.background;

import java.util.List;

import com.pump.text.html.css.CssLength;

import junit.framework.TestCase;

public class CssBackgroundPositionParserTest extends TestCase {

	// tests for:
	// @formatter:off
	// [ left | center | right | top | bottom | <length-percentage> ]	
	// @formatter:on

	/**
	 * Test parsing "left"
	 */
	public void test_syntax1_left() {
		List<CssBackgroundPositionValue> list = new CssBackgroundPositionParser()
				.parse("left");
		assertEquals(1, list.size());
		assertEquals(new CssBackgroundPositionValue("0% 50%",
				new CssLength(0, "%"), true, new CssLength(50, "%"), true),
				list.get(0));
	}

	/**
	 * Test parsing "center"
	 */
	public void test_syntax1_center() {
		List<CssBackgroundPositionValue> list = new CssBackgroundPositionParser()
				.parse("center");
		assertEquals(1, list.size());
		assertEquals(new CssBackgroundPositionValue("50% 50%",
				new CssLength(50, "%"), true, new CssLength(50, "%"), true),
				list.get(0));
	}

	/**
	 * Test parsing "right"
	 */
	public void test_syntax1_right() {
		List<CssBackgroundPositionValue> list = new CssBackgroundPositionParser()
				.parse("right");
		assertEquals(1, list.size());
		assertEquals(new CssBackgroundPositionValue("100% 50%",
				new CssLength(100, "%"), true, new CssLength(50, "%"), true),
				list.get(0));
	}

	/**
	 * Test parsing "top"
	 */
	public void test_syntax1_top() {
		List<CssBackgroundPositionValue> list = new CssBackgroundPositionParser()
				.parse("top");
		assertEquals(1, list.size());
		assertEquals(new CssBackgroundPositionValue("50% 0%",
				new CssLength(50, "%"), true, new CssLength(0, "%"), true),
				list.get(0));
	}

	/**
	 * Test parsing "bottom"
	 */
	public void test_syntax1_bottom() {
		List<CssBackgroundPositionValue> list = new CssBackgroundPositionParser()
				.parse("bottom");
		assertEquals(1, list.size());
		assertEquals(new CssBackgroundPositionValue("50% 100%",
				new CssLength(50, "%"), true, new CssLength(100, "%"), true),
				list.get(0));
	}

	/**
	 * Test parsing "33%"
	 */
	public void test_syntax1_33_percent() {
		List<CssBackgroundPositionValue> list = new CssBackgroundPositionParser()
				.parse("33%");
		assertEquals(1, list.size());
		assertEquals(new CssBackgroundPositionValue("33% 100%",
				new CssLength(33, "%"), true, new CssLength(50, "%"), true),
				list.get(0));
	}

	/**
	 * Test parsing "debajo"
	 */
	public void test_syntax1_unsupported_keyword() {
		try {
			List<CssBackgroundPositionValue> list = new CssBackgroundPositionParser()
					.parse("debajo");
			fail();
		} catch (Exception e) {
			// success
		}
	}

	/**
	 * Test parsing "33kg"
	 */
	public void test_syntax1_unsupported_unit() {
		try {
			List<CssBackgroundPositionValue> list = new CssBackgroundPositionParser()
					.parse("33kg");
			fail();
		} catch (Exception e) {
			// success
		}
	}

	// tests for:
	// @formatter:off
	// [ left | center | right | <length-percentage> ] [ top | center | bottom | <length-percentage> ] |
	// @formatter:on

	/**
	 * Test parsing "left bottom"
	 */
	public void test_syntax2_left_bottom() {
		List<CssBackgroundPositionValue> list = new CssBackgroundPositionParser()
				.parse("left bottom");
		assertEquals(1, list.size());
		assertEquals(new CssBackgroundPositionValue("0% 100%",
				new CssLength(0, "%"), true, new CssLength(100, "%"), true),
				list.get(0));
	}

	/**
	 * Test parsing "right center"
	 */
	public void test_syntax2_right_center() {
		List<CssBackgroundPositionValue> list = new CssBackgroundPositionParser()
				.parse("right center");
		assertEquals(1, list.size());
		assertEquals(new CssBackgroundPositionValue("100% 50%",
				new CssLength(100, "%"), true, new CssLength(50, "%"), true),
				list.get(0));
	}

	/**
	 * Test parsing "right top"
	 */
	public void test_syntax2_right_top() {
		List<CssBackgroundPositionValue> list = new CssBackgroundPositionParser()
				.parse("right top");
		assertEquals(1, list.size());
		assertEquals(new CssBackgroundPositionValue("100% 0%",
				new CssLength(100, "%"), true, new CssLength(0, "%"), true),
				list.get(0));
	}

	/**
	 * Test parsing "center center"
	 */
	public void test_syntax2_center_center() {
		List<CssBackgroundPositionValue> list = new CssBackgroundPositionParser()
				.parse("center center");
		assertEquals(1, list.size());
		assertEquals(new CssBackgroundPositionValue("50% 50%",
				new CssLength(50, "%"), true, new CssLength(50, "%"), true),
				list.get(0));
	}

	/**
	 * Test parsing "left 33%"
	 */
	public void test_syntax2_left_33() {
		List<CssBackgroundPositionValue> list = new CssBackgroundPositionParser()
				.parse("left 33%");
		assertEquals(1, list.size());
		assertEquals(new CssBackgroundPositionValue("0% 33%",
				new CssLength(0, "%"), true, new CssLength(33, "%"), true),
				list.get(0));
	}

	/**
	 * Test parsing "center 75%"
	 */
	public void test_syntax2_center_75() {
		List<CssBackgroundPositionValue> list = new CssBackgroundPositionParser()
				.parse("center 75%");
		assertEquals(1, list.size());
		assertEquals(new CssBackgroundPositionValue("50% 75%",
				new CssLength(50, "%"), true, new CssLength(75, "%"), true),
				list.get(0));
	}

	/**
	 * Test parsing "bottom center" This isn't technically part of the formal
	 * spec, but Chrome supports its.
	 */
	public void test_syntax2_bottom_center() {
		List<CssBackgroundPositionValue> list = new CssBackgroundPositionParser()
				.parse("bottom center");
		assertEquals(1, list.size());
		assertEquals(new CssBackgroundPositionValue("50% 100%",
				new CssLength(50, "%"), true, new CssLength(100, "%"), true),
				list.get(0));
	}

	// tests for
	// @formatter:off
	// [ center | [ left | right ] <length-percentage>? ] && [ center | [ top | bottom ] <length-percentage>? ]
	// @formatter:on

	/**
	 * Test parsing "left 20% top 30px"
	 */
	public void test_syntax3_left_20_top_30px() {
		List<CssBackgroundPositionValue> list = new CssBackgroundPositionParser()
				.parse("left 20% top 30px");
		assertEquals(1, list.size());
		assertEquals(new CssBackgroundPositionValue("20% 30px",
				new CssLength(20, "%"), true, new CssLength(30, "px"), true),
				list.get(0));
	}

	/**
	 * Test parsing "right 60% bottom 25px"
	 */
	public void test_syntax3_right_60_bottom_25px() {
		List<CssBackgroundPositionValue> list = new CssBackgroundPositionParser()
				.parse("right 60% bottom 25px");
		assertEquals(1, list.size());
		assertEquals(new CssBackgroundPositionValue("~60% 25px",
				new CssLength(60, "%"), false, new CssLength(25, "px"), false),
				list.get(0));
	}

	/**
	 * Test parsing "center top 33%"
	 */
	public void test_syntax3_center_top_33() {
		List<CssBackgroundPositionValue> list = new CssBackgroundPositionParser()
				.parse("center top 33%");
		assertEquals(1, list.size());
		assertEquals(new CssBackgroundPositionValue("50% 33%",
				new CssLength(50, "%"), true, new CssLength(33, "%"), true),
				list.get(0));
	}

	/**
	 * Test parsing "left 88px center"
	 */
	public void test_syntax3_left_88px_center() {
		List<CssBackgroundPositionValue> list = new CssBackgroundPositionParser()
				.parse("left 88px center");
		assertEquals(1, list.size());
		assertEquals(new CssBackgroundPositionValue("88px 50%",
				new CssLength(88, "px"), true, new CssLength(50, "%"), true),
				list.get(0));
	}

	/**
	 * Test parsing "left 33% top"
	 */
	public void test_syntax3_left_33_top() {
		List<CssBackgroundPositionValue> list = new CssBackgroundPositionParser()
				.parse("left 33% top");
		assertEquals(1, list.size());
		assertEquals(new CssBackgroundPositionValue("33% 0%",
				new CssLength(33, "%"), true, new CssLength(0, "%"), true),
				list.get(0));
	}

	/**
	 * Test parsing "center 40% top 33%".
	 */
	public void test_syntax3_unsupported_center_40_top_33() {
		try {
			List<CssBackgroundPositionValue> list = new CssBackgroundPositionParser()
					.parse("center 40% top 33%");
			fail();
		} catch (Exception e) {
			// success
		}
	}

	/**
	 * Test parsing "left 10% center 33%".
	 */
	public void test_syntax3_unsupported_left_10_center_33() {
		try {
			List<CssBackgroundPositionValue> list = new CssBackgroundPositionParser()
					.parse("left 10% center 33%");
			fail();
		} catch (Exception e) {
			// success
		}
	}
}