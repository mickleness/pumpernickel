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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;

import javax.swing.JComponent;
import javax.swing.SwingConstants;

import com.pump.util.ObservableProperties.Key;
import com.pump.util.ObservableProperties.SetBoundsChecker;

/**
 * This is a PanelUI with a two-color vertical gradient.
 * 
 */
public class GradientPanelUI extends AbstractPanelUI {

	public static final Key<Color> KEY_FILL_COLOR_1 = new Key<Color>(
			"fill-color-1", Color.class);

	public static final Key<Color> KEY_FILL_COLOR_2 = new Key<Color>(
			"fill-color-2", Color.class);

	public static final Key<Color> KEY_STROKE_COLOR_1 = new Key<Color>(
			"stroke-color-1", Color.class);

	public static final Key<Color> KEY_STROKE_COLOR_2 = new Key<Color>(
			"stroke-color-2", Color.class);

	public static final Key<Integer> KEY_GRADIENT_ORIENTATION = new Key<Integer>(
			"gradient-orientation", Integer.class,
			new SetBoundsChecker<Integer>(SwingConstants.HORIZONTAL,
					SwingConstants.VERTICAL));

	public GradientPanelUI(Color fillColor) {
		this(fillColor, fillColor);
	}

	public GradientPanelUI(Color fillColor1, Color fillColor2) {
		setFillColor1(fillColor1);
		setFillColor2(fillColor2);
		setGradientOrientation(SwingConstants.VERTICAL);
		setStrokeColor1(new Color(0, 0, 0, 0));
		setStrokeColor2(new Color(0, 0, 0, 0));
	}

	/**
	 * Create a GradientPanelUI from a GradientPaint.
	 * 
	 * @param gradientPaint
	 *            if this is not a horizontal or vertical gradient then an
	 *            IllegalArgumentException is thrown. The exact endpoints of
	 *            this argument are disregarded, because this GradientPanelUI
	 *            always paints gradients from one end to the other.
	 */
	public GradientPanelUI(GradientPaint gradientPaint) {
		this(Color.white);

		Point2D p1 = gradientPaint.getPoint1();
		Point2D p2 = gradientPaint.getPoint2();
		boolean forward;
		if (p1.getX() == p2.getX()) {
			setGradientOrientation(SwingConstants.VERTICAL);
			forward = p2.getY() > p1.getY();
		} else if (p1.getY() == p2.getY()) {
			setGradientOrientation(SwingConstants.HORIZONTAL);
			forward = p2.getX() > p1.getX();
		} else {
			throw new IllegalArgumentException(
					"The gradient provided must be either a horizontal or vertical gradient. "
							+ gradientPaint);
		}

		if (forward) {
			setFillColor1(gradientPaint.getColor1());
			setFillColor2(gradientPaint.getColor2());
		} else {
			setFillColor1(gradientPaint.getColor2());
			setFillColor2(gradientPaint.getColor1());
		}
	}

	@Override
	protected boolean isSupported(Key<?> key) {
		return KEY_FILL_COLOR_1.equals(key) || KEY_FILL_COLOR_2.equals(key)
				|| KEY_STROKE_COLOR_1.equals(key)
				|| KEY_STROKE_COLOR_2.equals(key)
				|| KEY_GRADIENT_ORIENTATION.equals(key);
	}

	/**
	 * Return the primary color of the fill gradient.
	 * 
	 * @see #KEY_FILL_COLOR_1
	 */
	public Color getFillColor1() {
		return getProperty(KEY_FILL_COLOR_1);
	}

	/**
	 * Set the secondary color of the fill gradient.
	 * 
	 * @see #KEY_FILL_COLOR_2
	 */
	public void setFillColor1(Color c) {
		setProperty(KEY_FILL_COLOR_1, c);
	}

	public int getGradientOrientation() {
		Integer k = getProperty(KEY_GRADIENT_ORIENTATION);
		if (k == null)
			k = SwingConstants.VERTICAL;
		return k;
	}

	public void setGradientOrientation(int orientation) {
		setProperty(KEY_GRADIENT_ORIENTATION, orientation);
	}

	/**
	 * Set the primary and secondary colors to the same value, making this panel
	 * a uniform color.
	 * 
	 * @see #KEY_FILL_COLOR_1
	 * @see #KEY_FILL_COLOR_2
	 */
	public void setFillColor(Color c) {
		setFillColor1(c);
		setFillColor2(c);
	}

	/**
	 * Return the bottom color of the vertical gradient.
	 * 
	 * @see #KEY_COLOR_BOTTOM
	 */
	public Color getFillColor2() {
		return getProperty(KEY_FILL_COLOR_2);
	}

	/**
	 * Set the bottom color of the vertical gradient.
	 * 
	 * @see #KEY_COLOR_BOTTOM
	 */
	public void setFillColor2(Color c) {
		setProperty(KEY_FILL_COLOR_2, c);
	}

	@Override
	public void paint(Graphics g0, JComponent c) {
		Graphics2D g = (Graphics2D) g0;

		Insets i = c.getInsets();
		int topY = i.top;
		int bottomY = c.getHeight() - i.bottom;
		int leftX = i.left;
		int rightX = c.getWidth() - i.right;
		Rectangle r = new Rectangle(leftX, topY, rightX - leftX, bottomY - topY);
		paintGradient(g, r.x, r.y, r.width, r.height, r);
	}

	/**
	 * Return the primary color of the stroke gradient.
	 * 
	 * @see #KEY_STROKE_COLOR_1
	 */
	public Color getStrokeColor1() {
		return getProperty(KEY_STROKE_COLOR_1);
	}

	/**
	 * Set the primary and secondary stroke colors to the same value, making the
	 * border a uniform color.
	 * 
	 * @see #KEY_STROKE_COLOR_1
	 * @see #KEY_STROKE_COLOR_2
	 */
	public void setStrokeColor(Color color) {
		setStrokeColor1(color);
		setStrokeColor2(color);
	}

	/**
	 * Set the secondary color of the stroke gradient.
	 * 
	 * @see #KEY_STROKE_COLOR_1
	 */
	public void setStrokeColor1(Color c) {
		setProperty(KEY_STROKE_COLOR_1, c);
	}

	/**
	 * Return the primary color of the stroke gradient.
	 * 
	 * @see #KEY_STROKE_COLOR_2
	 */
	public Color getStrokeColor2() {
		return getProperty(KEY_STROKE_COLOR_2);
	}

	/**
	 * Set the secondary color of the stroke gradient.
	 * 
	 * @see #KEY_STROKE_COLOR_2
	 */
	public void setStrokeColor2(Color c) {
		setProperty(KEY_STROKE_COLOR_2, c);
	}

	protected void paintGradient(Graphics2D g0, int x, int y, int w, int h,
			Shape fillShape) {
		Graphics2D g = (Graphics2D) g0.create();
		GradientPaint p = createGradient(x, y, w, h, KEY_FILL_COLOR_1,
				KEY_FILL_COLOR_2);
		if (p != null) {
			g.setPaint(p);
			g.fill(fillShape);
		}
		p = createGradient(x, y, w, h, KEY_STROKE_COLOR_1, KEY_STROKE_COLOR_2);
		if (p != null) {
			g.setPaint(p);
			g.setStroke(new BasicStroke(1));
			g.draw(fillShape);
		}
		g.dispose();
	}

	private GradientPaint createGradient(int x, int y, int w, int h,
			Key<Color> k1, Key<Color> k2) {
		Color color1 = getProperty(k1);
		Color color2 = getProperty(k2);
		if (color1 == null && color2 == null)
			return null;
		if (color1 == null)
			color1 = new Color(color2.getRed(), color2.getGreen(),
					color2.getBlue(), 0);
		if (color2 == null)
			color2 = new Color(color1.getRed(), color1.getGreen(),
					color1.getBlue(), 0);

		GradientPaint p;
		if (SwingConstants.VERTICAL == getGradientOrientation()) {
			p = new GradientPaint(0, y, color1, 0, y + h, color2);
		} else {
			p = new GradientPaint(x, y, color1, x + w, y, color2);
		}
		return p;
	}
}