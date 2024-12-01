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

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;

/**
 * A <code>ThrobberUI</code> showing 3 circles pulsing outward that also move in
 * a clockwise rotation.
 * <p>
 * <table summary="Sample Animations of PulsingCirclesThrobberUI" cellpadding="10">
 * <tr>
 * <td><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/throbber/PulsingCirclesThrobberUI.gif"
 * alt="PulsingCirclesThrobberUI"></td>
 * <td><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/throbber/PulsingCirclesThrobberUIx2.gif"
 * alt="PulsingCirclesThrobberUI, Magnified 2x"></td>
 * <td><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/throbber/PulsingCirclesThrobberUIx4.gif"
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
public class PulsingCirclesThrobberPainter extends ScaledThrobberPainter {

	@Override
	public void doPaint(Graphics2D g, float f, Color foreground) {
		g.setColor(foreground);
		for (int a = 0; a < 8; a++) {
			double z = a / 8.0;
			double r = 6;
			double x = 8 + r * Math.cos(Math.PI * 2 * z);
			double y = 8 + r * Math.sin(Math.PI * 2 * z);
			double k = 2.2f * ((z - f + 1) % 1);

			// this is what makes it accelerate quickly to full size and decelerate slowly:
			k = k / 2;
			k = -(2 * k * k - 1) * (2 * k * k - 1) + 1;
			k *= 1.7f;

			Ellipse2D dot = new Ellipse2D.Double(x - k, y - k, 2 * k, 2 * k);
			g.fill(dot);
		}
	}

	@Override
	public int getPreferredPeriod() {
		return 750;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(16, 16);
	}

	@Override
	public Color getPreferredForeground() {
		return Color.darkGray;
	}
}