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
package com.pump.plaf;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;

import javax.swing.JComponent;

/**
 * A <code>ThrobberPainter</code> of two arrows that rotate clockwise.
 * <p>
 * <table summary="Sample Animations of ChasingArrowsThrobberPainter" cellpadding="10">
 * <tr>
 * <td><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/throbber/ChasingArrowsThrobberPainter-16x16.gif"
 * alt="ChasingArrowsThrobberPainter"></td>
 * <td><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/throbber/ChasingArrowsThrobberPainter-32x32.gif"
 * alt="ChasingArrowsThrobberPainter Magnified 2x"></td>
 * <td><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/throbber/ChasingArrowsThrobberPainter-64x64
 * .gif"
 * alt="ChasingArrowsThrobberPainter Magnified 4x"></td>
 * </tr>
 * </table>
 * <p>
 */
public class ChasingArrowsThrobberPainter extends ScaledThrobberPainter {

	private static final float PI = (float) Math.PI;
	private static final int[] x = new int[] { 8, 8, 11 };
	private static final int[] y = new int[] { 0, 6, 3 };
	private static final BasicStroke stroke = new BasicStroke(1,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);

	private AffineTransform transform = new AffineTransform();
	private Arc2D arc = new Arc2D.Float(3, 3, 10, 10, 65, 140, Arc2D.OPEN);
	private GeneralPath path = new GeneralPath();

	@Override
	public void doPaint(Graphics2D g, float f, Color foreground) {
		f = f * 2 * PI;

		g.setStroke(stroke);
		g.setColor(foreground);

		for (int k = 0; k < 2; k++) {
			transform.setToRotation(f + k * Math.PI, 8, 8);

			path.reset();
			path.moveTo(x[0], y[0]);
			path.lineTo(x[1], y[1]);
			path.lineTo(x[2], y[2]);
			path.lineTo(x[0], y[0]);
			path.transform(transform);

			g.fill(path);

			path.reset();
			path.append(arc.getPathIterator(transform), false);
			g.draw(path);
		}
	}

	@Override
	public int getPreferredPeriod() {
		return 2000;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(16, 16);
	}
}