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
package com.pump.graphics.geom;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import com.pump.geom.ShapeBounds;
import com.pump.geom.ShapeStringUtils;

import junit.framework.TestCase;

public class ShapeBoundsTest extends TestCase {

	/**
	 * Test a cubic polynomial that will degenerate into a quadratic polynomial.
	 */
	public void testDegenerateCubic() {

		Shape s = ShapeStringUtils.createPath(
				"m 50.0 0.0 c 10.0 0.0 10.0 100.0 50.0 100.0 c 90.0 100.0 90.0 0.0 50.0 0.0 z");
		Rectangle2D r = ShapeBounds.getBounds(s);
		assertTrue(Math.abs(r.getMinX() - 20) < .001);
		assertTrue(Math.abs(r.getMaxX() - 80) < .001);
	}
}