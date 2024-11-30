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
 * <table summary="Sample Animations of AquaThrobberUI" cellpadding="10">
 * <tr>
 * <td><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/throbber/AquaThrobberUI.gif"
 * alt="AquaThrobberUI"></td>
 * <td><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/throbber/AquaThrobberUIx2.gif"
 * alt="AquaThrobberUI Magnified 2x"></td>
 * <td><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/throbber/AquaThrobberUIx4.gif"
 * alt="AquaThrobberUI Magnified 4x"></td>
 * </tr>
 * </table>
 * <p>
 * On installation: the component's foreground is set to gray, but if that is
 * changed then that color is used to render this animation.
 * <P>
 * The default period for this animation is 500, but you can modify this with
 * the period client properties {@link ThrobberUI#PERIOD_KEY} or
 * {@link ThrobberUI#PERIOD_MULTIPLIER_KEY}.
 */
public class AquaThrobberPainter extends ThrobberPainter {
	private static final int[] opacities = new int[] {255, 240, 225, 200, 160, 130, 115, 100, 90, 80, 70, 60};

	@Override
	protected void doPaint(Graphics2D g, float fraction, Color foreground) {
		g.setStroke(new BasicStroke(1.9f, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_BEVEL));
		int i = (int) (fraction * 12);
		for (int a = 0; a < 12; a++) {
			g.setColor(new Color(
					foreground.getRed(),
					foreground.getBlue(),
					foreground.getGreen(),
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
	public int getPreferredRepaintInterval() {
		return 500/12;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(19, 19);
	}

	@Override
	public Color getPreferredForeground() {
		return Color.gray;
	}
}