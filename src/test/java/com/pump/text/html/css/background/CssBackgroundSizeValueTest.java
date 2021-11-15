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
package com.pump.text.html.css.background;

import java.awt.Dimension;

import com.pump.text.html.css.CssLength;

import junit.framework.TestCase;

/**
 * Test a variety of inputs/outputs for different
 * CssBackgroundSizeValue.Calculators.
 *
 */
public class CssBackgroundSizeValueTest extends TestCase {

	public void testContains_portrait() {
		Dimension s = CssBackgroundSizeValue.CONTAIN.getSize(100, 100,
				new Dimension(3, 4));
		assertEquals(75, s.width);
		assertEquals(100, s.height);
	}

	public void testContains_landscape() {
		Dimension s = CssBackgroundSizeValue.CONTAIN.getSize(100, 100,
				new Dimension(4, 3));
		assertEquals(100, s.width);
		assertEquals(75, s.height);
	}

	public void testContains_square() {
		Dimension s = CssBackgroundSizeValue.CONTAIN.getSize(100, 100,
				new Dimension(2, 2));
		assertEquals(100, s.width);
		assertEquals(100, s.height);
	}

	public void testContains_null() {
		Dimension s = CssBackgroundSizeValue.CONTAIN.getSize(115, 100, null);
		assertEquals(115, s.width);
		assertEquals(100, s.height);
	}

	public void testCover_portrait() {
		Dimension s = CssBackgroundSizeValue.COVER.getSize(100, 100,
				new Dimension(3, 4));
		assertEquals(100, s.width);
		assertEquals(133, s.height);
	}

	public void testCover_landscape() {
		Dimension s = CssBackgroundSizeValue.COVER.getSize(100, 100,
				new Dimension(4, 3));
		assertEquals(133, s.width);
		assertEquals(100, s.height);
	}

	public void testCover_square() {
		Dimension s = CssBackgroundSizeValue.COVER.getSize(100, 100,
				new Dimension(2, 2));
		assertEquals(100, s.width);
		assertEquals(100, s.height);
	}

	public void testCover_null() {
		Dimension s = CssBackgroundSizeValue.CONTAIN.getSize(115, 100, null);
		assertEquals(115, s.width);
		assertEquals(100, s.height);
	}

	public void testAuto_portait() {
		Dimension s = CssBackgroundSizeValue.AUTO.getSize(100, 100,
				new Dimension(3, 4));
		assertEquals(3, s.width);
		assertEquals(4, s.height);
	}

	public void testAuto_landscape() {
		Dimension s = CssBackgroundSizeValue.AUTO.getSize(100, 100,
				new Dimension(4, 3));
		assertEquals(4, s.width);
		assertEquals(3, s.height);
	}

	public void testAuto_null() {
		Dimension s = CssBackgroundSizeValue.AUTO.getSize(115, 100, null);
		assertEquals(115, s.width);
		assertEquals(100, s.height);
	}

	public void testFixed_px() {
		// regardless of the incoming image dimension are output should be fixed
		Dimension landscape = new Dimension(4, 3);
		Dimension portrait = new Dimension(3, 4);

		for (Dimension imageSize : new Dimension[] { null, landscape,
				portrait }) {
			CssLength cssWidth = new CssLength(70, "px");
			CssLength cssHeight = new CssLength(25, "px");
			Dimension s = new CssBackgroundSizeValue.FixedSizeCalculator(
					cssWidth, cssHeight).getSize(200, 200, imageSize);
			assertEquals(70, s.width);
			assertEquals(25, s.height);
		}
	}

	public void testFixed_percent() {
		// regardless of the incoming image dimension are output should be fixed
		Dimension landscape = new Dimension(4, 3);
		Dimension portrait = new Dimension(3, 4);

		for (Dimension imageSize : new Dimension[] { null, landscape,
				portrait }) {
			CssLength cssWidth = new CssLength(70, "%");
			CssLength cssHeight = new CssLength(25, "%");
			Dimension s = new CssBackgroundSizeValue.FixedSizeCalculator(
					cssWidth, cssHeight).getSize(200, 200, imageSize);
			assertEquals(140, s.width);
			assertEquals(50, s.height);
		}
	}

	public void testAutoWidth_portrait_px() {
		CssLength cssHeight = new CssLength(25, "%");
		Dimension s = new CssBackgroundSizeValue.AutoWidthCalculator(cssHeight)
				.getSize(100, 200, new Dimension(3, 4));
		assertEquals(50, s.height);
		assertEquals(37, s.width);
	}

	public void testAutoWidth_landscape_px() {
		CssLength cssHeight = new CssLength(25, "%");
		Dimension s = new CssBackgroundSizeValue.AutoWidthCalculator(cssHeight)
				.getSize(100, 200, new Dimension(4, 3));
		assertEquals(50, s.height);
		assertEquals(66, s.width);
	}

	public void testAutoHeight_portrait_px() {
		CssLength cssWidth = new CssLength(25, "%");
		Dimension s = new CssBackgroundSizeValue.AutoHeightCalculator(cssWidth)
				.getSize(200, 100, new Dimension(3, 4));
		assertEquals(50, s.width);
		assertEquals(66, s.height);
	}

	public void testAutoHeight_landscape_px() {
		CssLength cssWidth = new CssLength(25, "%");
		Dimension s = new CssBackgroundSizeValue.AutoHeightCalculator(cssWidth)
				.getSize(200, 100, new Dimension(4, 3));
		assertEquals(50, s.width);
		assertEquals(37, s.height);
	}
}