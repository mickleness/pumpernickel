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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.swing.JComponent;

/**
 * Twelve short line segments that rotate in a clockwise direction. The line
 * segments are painted in the component's foreground color, with varying levels
 * of opacity.
 * <p>
 * <table summary="Sample Animations of AquaThrobberUI" cellpadding="10">
 * <tr>
 * <td><img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/resources/throbber/AquaThrobberUI.gif"
 * alt="AquaThrobberUI"></td>
 * <td><img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/resources/throbber/AquaThrobberUIx2.gif"
 * alt="AquaThrobberUI Magnified 2x"></td>
 * <td><img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/resources/throbber/AquaThrobberUIx4.gif"
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
public class AquaThrobberUI extends ThrobberUI {

	/** The number of milliseconds for the default period (500). */
	public static final int DEFAULT_PERIOD = 500;

	private static final Line2D line = new Line2D.Float();
	private static final Map<Color, Color[]> foregroundTable = new HashMap<Color, Color[]>();

	private Color defaultForeground;

	public AquaThrobberUI() {
		this(Color.gray);
	}

	/**
	 * Create a AquaThrobberUI that will render with a default color. Note this
	 * color is overridden by the JComponent's foreground color, if possible.
	 */
	public AquaThrobberUI(Color foreground) {
		super(DEFAULT_PERIOD / 12);
		Objects.requireNonNull(foreground);
		this.defaultForeground = foreground;
	}

	/**
	 * Paints the 12 angular lines in a circle often used to indicate progress
	 * in the Aqua interface.
	 * <p>
	 * It is strongly recommended that you use quality rendering hints (include
	 * stroke control) to achieve an aesthetic look.
	 * 
	 * @param g
	 *            the graphics to paint to
	 * @param fraction
	 *            a fractional value between [0,1] indicating how far the angle
	 *            has progress. As this value increases the highlighted line
	 *            segment moves clockwise. The default behavior is for this
	 *            value to iterate from [0,1] in approximately 1 second.
	 * @param foreground
	 *            the color of the darkest line segment. All other line segments
	 *            are calculated as translucent shades of this color.
	 * @param centerX
	 *            the x-value of the center of this circle.
	 * @param centerY
	 *            the y-value of the center of this circle.
	 * @param r1
	 *            the radius of one end point of a line segment in this circle.
	 *            The default value is 5.
	 * @param r2
	 *            the radius of the other end point of a line segment in this
	 *            circle. The default value is 8.
	 * @param strokeWidth
	 *            the width of the stroke. The default value is 1.9f.
	 */
	public static void paint(Graphics2D g, float fraction, Color foreground,
			int centerX, int centerY, int r1, int r2, float strokeWidth) {

		if (fraction < 0)
			throw new IllegalArgumentException(
					"fraction (" + fraction + ") must be within [0, 1]");

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		int i = (int) (fraction * 12);

		Color[] colors = foregroundTable.get(foreground);
		if (colors == null) {
			int red = foreground.getRed();
			int green = foreground.getGreen();
			int blue = foreground.getBlue();
			colors = new Color[] { new Color(red, green, blue, 255),
					new Color(red, green, blue, 240),
					new Color(red, green, blue, 225),
					new Color(red, green, blue, 200),
					new Color(red, green, blue, 160),
					new Color(red, green, blue, 130),
					new Color(red, green, blue, 115),
					new Color(red, green, blue, 100),
					new Color(red, green, blue, 90),
					new Color(red, green, blue, 80),
					new Color(red, green, blue, 70),
					new Color(red, green, blue, 60)

			};
		}

		g.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_BEVEL));
		double theta;
		for (int a = 0; a < colors.length; a++) {
			g.setColor(colors[(i + a) % colors.length]);
			theta = -((double) a) / (colors.length) * Math.PI * 2;
			line.setLine(centerX + r1 * Math.cos(theta),
					centerY + r1 * Math.sin(theta),
					centerX + r2 * Math.cos(theta),
					centerY + r2 * Math.sin(theta));

			g.draw(line);
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(19, 19);
	}

	@Override
	public void paintForeground(Graphics2D g, JComponent jc, Dimension size,
			Float fixedFraction) {
		float f;
		if (fixedFraction == null) {
			int p = getPeriod(jc, DEFAULT_PERIOD);
			f = ((float) (System.currentTimeMillis() % p)) / ((float) p);
		} else {
			f = fixedFraction;
		}
		Color color = jc == null ? getDefaultForeground() : jc.getForeground();
		paint(g, f, color, size.width / 2, size.height / 2, 5, 8, 1.9f);
	}

	@Override
	public Color getDefaultForeground() {
		return defaultForeground;
	}
}