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
package com.pump.plaf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;

/**
 * A <code>ThrobberUI</code> showing 3 circles pulsing outward that also move in
 * a clockwise rotation.
 * <p>
 * <table summary="Sample Animations of PulsingCirclesThrobberUI" cellpadding="10">
 * <tr>
 * <td><img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/resources/throbber/PulsingCirclesThrobberUI.gif"
 * alt="PulsingCirclesThrobberUI"></td>
 * <td><img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/resources/throbber/PulsingCirclesThrobberUIx2.gif"
 * alt="PulsingCirclesThrobberUI, Magnified 2x"></td>
 * <td><img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/resources/throbber/PulsingCirclesThrobberUIx4.gif"
 * alt="PulsingCirclesThrobberUI, Magnified 4x"></td>
 * </tr>
 * </table>
 * <p>
 * On installation: the component's foreground is set to dark gray, but if that
 * is changed then that color is used to render this animation.
 * <P>
 * The default period for this animation is 750, but you can modify this with
 * the period client properties {@link ThrobberUI#PERIOD_KEY} or
 * {@link ThrobberUI#PERIOD_MULTIPLIER_KEY}.
 *
 */
public class PulsingCirclesThrobberUI extends ThrobberUI {

	/**
	 * The default duration (in ms) it takes to complete a cycle.
	 */
	public static final int DEFAULT_PERIOD = 750;

	public PulsingCirclesThrobberUI() {
		super(1000 / 24);
	}

	@Override
	protected synchronized void paintForeground(Graphics2D g, JComponent jc,
			Dimension size, Float fixedFraction) {
		float f;
		if (fixedFraction != null) {
			f = fixedFraction;
		} else {
			int p = getPeriod(jc, DEFAULT_PERIOD);
			float t = System.currentTimeMillis() % p;
			f = t / p;
		}

		boolean spiral = false;
		double maxDotSize = spiral ? 2 : 2.2;

		Color color = jc == null ? getDefaultForeground() : jc.getForeground();
		g.setColor(color);
		for (int a = 0; a < 8; a++) {
			double z = a / 8.0;
			double r = spiral ? 6 * ((z - f + 1) % 1) : 6;
			double x = size.width / 2 + r * Math.cos(Math.PI * 2 * z);
			double y = size.width / 2 + r * Math.sin(Math.PI * 2 * z);
			double k = maxDotSize * ((z - f + 1) % 1);
			Ellipse2D dot = new Ellipse2D.Double(x - k, y - k, 2 * k, 2 * k);
			g.fill(dot);
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(16, 16);
	}

	@Override
	public Color getDefaultForeground() {
		return Color.darkGray;
	}
}