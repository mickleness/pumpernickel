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
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingConstants;
import javax.swing.UIManager;

/**
 * Some static methods for some common painting functions.
 **/
public class PlafPaintUtils {

	/** Four shades of white, each with increasing opacity. */
	final static Color[] whites = new Color[] { new Color(255, 255, 255, 50),
			new Color(255, 255, 255, 100), new Color(255, 255, 255, 150) };

	/** Four shades of black, each with increasing opacity. */
	final static Color[] blacks = new Color[] { new Color(0, 0, 0, 50),
			new Color(0, 0, 0, 100), new Color(0, 0, 0, 150) };

	/**
	 * @return the color used to indicate when a component has focus. By default
	 *         this uses the color (64,113,167), but you can override this by
	 *         calling: <BR>
	 *         <code>UIManager.put("focusRing",customColor);</code>
	 */
	public static Color getFocusRingColor() {
		Object obj = UIManager.getColor("Focus.color");
		if (obj instanceof Color)
			return (Color) obj;
		obj = UIManager.getColor("focusRing");
		if (obj instanceof Color)
			return (Color) obj;
		return new Color(64, 113, 167);
	}

	/**
	 * Paints 3 different strokes around a shape to indicate focus. The widest
	 * stroke is the most transparent, so this achieves a nice "glow" effect.
	 * <P>
	 * The catch is that you have to render this underneath the shape, and the
	 * shape should be filled completely.
	 * 
	 * @param g
	 *            the graphics to paint to
	 * @param shape
	 *            the shape to outline
	 * @param pixelSize
	 *            the number of pixels the outline should cover.
	 */
	public static void paintFocus(Graphics2D g, Shape shape, int pixelSize) {
		paintFocus(g, shape, pixelSize, getFocusRingColor());
	}

	/**
	 * Paints 3 different strokes around a shape to indicate focus. The widest
	 * stroke is the most transparent, so this achieves a nice "glow" effect.
	 * <P>
	 * The catch is that you have to render this underneath the shape, and the
	 * shape should be filled completely.
	 * 
	 * @param g
	 *            the graphics to paint to
	 * @param shape
	 *            the shape to outline
	 * @param pixelSize
	 *            the number of pixels the outline should cover.
	 * @param focusColor
	 *            the color of the focus ring to paint
	 */
	public static void paintFocus(Graphics2D g, Shape shape, int pixelSize,
			Color focusColor) {
		g = (Graphics2D) g.create();
		try {
			Color[] focusArray = new Color[] {
					new Color(focusColor.getRed(), focusColor.getGreen(),
							focusColor.getBlue(),
							235 * focusColor.getAlpha() / 255),
					new Color(focusColor.getRed(), focusColor.getGreen(),
							focusColor.getBlue(),
							130 * focusColor.getAlpha() / 255),
					new Color(focusColor.getRed(), focusColor.getGreen(),
							focusColor.getBlue(),
							80 * focusColor.getAlpha() / 255) };

			g.setStroke(new BasicStroke(2 * pixelSize + 1,
					BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g.setColor(focusArray[2]);
			g.draw(shape);
			if (2 * pixelSize - 2 + 1 > 0) {
				g.setStroke(new BasicStroke(2 * pixelSize - 2 + 1,
						BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				g.setColor(focusArray[1]);
				g.draw(shape);
			}
			if (2 * pixelSize - 4 + 1 > 0) {
				g.setStroke(new BasicStroke(2 * pixelSize - 4 + 1,
						BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				g.setColor(focusArray[0]);
				g.draw(shape);
			}
		} finally {
			g.dispose();
		}
	}

	/**
	 * Uses translucent shades of white and black to draw highlights and shadows
	 * around a rectangle, and then frames the rectangle with a shade of gray
	 * (120).
	 * <P>
	 * This should be called to add a finishing touch on top of existing
	 * graphics.
	 * 
	 * @param g
	 *            the graphics to paint to.
	 * @param r
	 *            the rectangle to paint.
	 */
	public static void drawBevel(Graphics2D g, Rectangle r) {
		g.setStroke(new BasicStroke(1));
		drawColors(blacks, g, r.x, r.y + r.height, r.x + r.width,
				r.y + r.height, SwingConstants.SOUTH);
		drawColors(blacks, g, r.x + r.width, r.y, r.x + r.width, r.y + r.height,
				SwingConstants.EAST);

		drawColors(whites, g, r.x, r.y, r.x + r.width, r.y,
				SwingConstants.NORTH);
		drawColors(whites, g, r.x, r.y, r.x, r.y + r.height,
				SwingConstants.WEST);

		g.setColor(new Color(120, 120, 120));
		g.drawRect(r.x, r.y, r.width, r.height);
	}

	private static void drawColors(Color[] colors, Graphics g, int x1, int y1,
			int x2, int y2, int direction) {
		for (int a = 0; a < colors.length; a++) {
			g.setColor(colors[colors.length - a - 1]);
			if (direction == SwingConstants.SOUTH) {
				g.drawLine(x1, y1 - a, x2, y2 - a);
			} else if (direction == SwingConstants.NORTH) {
				g.drawLine(x1, y1 + a, x2, y2 + a);
			} else if (direction == SwingConstants.EAST) {
				g.drawLine(x1 - a, y1, x2 - a, y2);
			} else if (direction == SwingConstants.WEST) {
				g.drawLine(x1 + a, y1, x2 + a, y2);
			}
		}
	}

	private static Map<String, TexturePaint> checkers;

	public static TexturePaint getCheckerBoard(int checkerSize) {
		return getCheckerBoard(checkerSize, Color.white, Color.lightGray);
	}

	public static TexturePaint getCheckerBoard(int checkerSize, Color color1,
			Color color2) {
		String key = checkerSize + " " + color1.toString() + " "
				+ color2.toString();
		if (checkers == null)
			checkers = new HashMap<>();
		TexturePaint paint = checkers.get(key);
		if (paint == null) {
			BufferedImage bi = new BufferedImage(2 * checkerSize,
					2 * checkerSize, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = bi.createGraphics();
			g.setColor(color1);
			g.fillRect(0, 0, 2 * checkerSize, 2 * checkerSize);
			g.setColor(color2);
			g.fillRect(0, 0, checkerSize, checkerSize);
			g.fillRect(checkerSize, checkerSize, checkerSize, checkerSize);
			g.dispose();
			paint = new TexturePaint(bi,
					new Rectangle(0, 0, bi.getWidth(), bi.getHeight()));
			checkers.put(key, paint);
		}
		return paint;
	}

	private static Map<Object, TexturePaint> diagonalStripes;

	/**
	 * Create a diagonal stripe pattern.
	 * 
	 * @param stripeWidth
	 *            the stripe width. The exact size of the resulting image will
	 *            be approximately (sqrt(8)*stripeWidth)
	 * @param color1
	 *            the color of one stripe
	 * @param color2
	 *            the color of the other stripe
	 * @return
	 */
	public static TexturePaint getDiagonalStripes(int stripeWidth, Color color1,
			Color color2) {
		Object key = Arrays.asList(stripeWidth, color1.toString(),
				color2.toString());
		if (diagonalStripes == null)
			diagonalStripes = new HashMap<>();
		TexturePaint paint = diagonalStripes.get(key);
		if (paint == null) {
			int k = (int) (Math.sqrt(2) * stripeWidth * 2 + .5);
			BufferedImage bi = new BufferedImage(k, k,
					BufferedImage.TYPE_INT_ARGB);

			// now cheat just a little and revise our stripe width based on the
			// real int-based tile:
			float kf = k;
			float realStripeWidth = (float) (kf / 2f / Math.sqrt(2));

			kf = kf / 2f;
			Graphics2D g = bi.createGraphics();
			g.setStroke(new BasicStroke(realStripeWidth, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER));
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(color1);
			g.draw(new Line2D.Float(kf * (-1), kf * .5f, kf * .5f, -kf));
			g.draw(new Line2D.Float(kf * (-1), kf * 2.5f, kf * 2.5f, -kf));
			g.draw(new Line2D.Float(kf * .5f, kf * 3, kf * 3f, kf * .5f));
			g.setColor(color2);
			g.draw(new Line2D.Float(kf * (-1), kf * 1.5f, kf * 1.5f,
					kf * (-1)));
			g.draw(new Line2D.Float(kf * (-.5f), kf * 3, kf * 3, kf * (-.5f)));
			g.draw(new Line2D.Float(kf * 1.5f, kf * 3, kf * 3, kf * (1.5f)));
			g.dispose();

			paint = new TexturePaint(bi,
					new Rectangle(0, 0, bi.getWidth(), bi.getHeight()));
			diagonalStripes.put(key, paint);
		}
		return paint;
	}

	/**
	 * Paint a String centered at a given (x,y) coordinate.
	 */
	public static void paintCenteredString(Graphics2D g, String str, Font font,
			int centerX, int centerY) {
		g.setFont(font);
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D r = fm.getStringBounds(str, g);
		float x = (float) (centerX - r.getWidth() / 2);
		float y = (float) (centerY - r.getHeight() / 2 - r.getY());
		g.drawString(str, x, y);
	}
}