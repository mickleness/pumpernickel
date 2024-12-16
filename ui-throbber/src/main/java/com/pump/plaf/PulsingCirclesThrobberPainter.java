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
 * A <code>ThrobberPainter</code> showing 8 circles pulsing in a clockwise direction.
 * <p>
 * <table summary="Sample Animations of PulsingCirclesThrobberPainter" cellpadding="10">
 * <tr>
 * <td><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/throbber/PulsingCirclesThrobberPainter-16x16.gif"
 * alt="PulsingCirclesThrobberPainter"></td>
 * <td><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/throbber/PulsingCirclesThrobberPainter-32x32.gif"
 * alt="PulsingCirclesThrobberPainter, Magnified 2x"></td>
 * <td><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/throbber/PulsingCirclesThrobberPainter-64x64.gif"
 * alt="PulsingCirclesThrobberPainter, Magnified 4x"></td>
 * </tr>
 * </table>
 * <p>
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
}