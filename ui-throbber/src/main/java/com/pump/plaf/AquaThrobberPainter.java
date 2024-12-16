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
import java.awt.geom.Line2D;

/**
 * Twelve short line segments that rotate in a clockwise direction. The line
 * segments are painted in the component's foreground color, with varying levels
 * of opacity.
 * <p>
 * <table summary="Sample Animations of AquaThrobberPainter" cellpadding="10">
 * <tr>
 * <td><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/throbber/AquaThrobberPainter-19x19.gif"
 * alt="AquaThrobberPainter"></td>
 * <td><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/throbber/AquaThrobberPainter-38x38.gif"
 * alt="AquaThrobberPainter Magnified 2x"></td>
 * <td><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/throbber/AquaThrobberPainter-76x76.gif"
 * alt="AquaThrobberPainter Magnified 4x"></td>
 * </tr>
 * </table>
 * <p>
 */
public class AquaThrobberPainter extends ScaledThrobberPainter {
	private static final int[] opacities = new int[] {255, 240, 225, 200, 160, 130, 115, 100, 90, 80, 70, 60};

	@Override
	protected void doPaint(Graphics2D g, float fraction, Color foreground) {
		g.setStroke(new BasicStroke(1.9f, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_BEVEL));
		int i = (int) (fraction * 12);
		for (int a = 0; a < 12; a++) {
			g.setColor(new Color(
					foreground.getRed(),
					foreground.getGreen(),
					foreground.getBlue(),
					opacities[(i + a) % 12]
			));
			double theta = -((double) a) / (12.0) * Math.PI * 2;
			g.draw(new Line2D.Double(9.5 + 5 * Math.cos(theta),
					9.5 + 5 * Math.sin(theta),
					9.5 + 8 * Math.cos(theta),
					9.5 + 8 * Math.sin(theta)));
		}
	}

	@Override
	public int getPreferredPeriod() {
		return 500;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(19, 19);
	}
}