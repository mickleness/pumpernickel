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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicPanelUI;

/**
 * This is a PanelUI with a two-color vertical gradient.
 */
public class GradientPanelUI extends BasicPanelUI {
	protected Color fillColor_top, fillColor_bottom;
	protected Color strokeColor_top, strokeColor_bottom;

	public GradientPanelUI(Color fillColor) {
		this(fillColor, fillColor);
	}

	public GradientPanelUI(Color fillColor1, Color fillColor2) {
		setUpperFillColor(fillColor1);
		setLowerFillColor(fillColor2);
		setUpperStrokeColor(new Color(0, 0, 0, 0));
		setLowerStrokeColor(new Color(0, 0, 0, 0));
	}

	/**
	 * Return the upper gradient color.
	 */
	public Color getUpperFillColor() {
		return fillColor_top;
	}

	/**
	 * Set the upper gradient color.
	 */
	public void setUpperFillColor(Color c) {
		fillColor_top = c;
	}

	/**
	 * Set the upper and lower gradient color to the same value, which will make the background color uniform.
	 */
	public void setFillColor(Color c) {
		setUpperFillColor(c);
		setLowerFillColor(c);
	}

	/**
	 * Return the lower gradient color.
	 */
	public Color getLowerFillColor() {
		return fillColor_bottom;
	}

	/**
	 * Set the lower gradient color.
	 */
	public void setLowerFillColor(Color c) {
		fillColor_bottom = c;
	}

	@Override
	public void paint(Graphics g0, JComponent c) {
		Graphics2D g = (Graphics2D) g0;

		Insets i = c.getInsets();
		int topY = i.top;
		int bottomY = c.getHeight() - i.bottom;
		int leftX = i.left;
		int rightX = c.getWidth() - i.right;
		Rectangle r = new Rectangle(leftX, topY, rightX - leftX,
				bottomY - topY);
		paintGradient(g, r.y, r.height, r);
	}

	/**
	 * Return the upper color of the stroke gradient.
	 */
	public Color getUpperStrokeColor() {
		return strokeColor_top;
	}

	/**
	 * Set the upper and lower stroke gradient to the same value, which will make the stroke color uniform.
	 */
	public void setStrokeColor(Color color) {
		setUpperStrokeColor(color);
		setLowerStrokeColor(color);
	}

	/**
	 * Set the upper color of the stroke gradient.
	 */
	public void setUpperStrokeColor(Color c) {
		strokeColor_top = c;
	}

	/**
	 * Return the lower color of the stroke gradient.
	 */
	public Color getLowerStrokeColor() {
		return strokeColor_bottom;
	}

	/**
	 * Set the lower color of the stroke gradient.
	 */
	public void setLowerStrokeColor(Color c) {
		strokeColor_bottom = c;
	}

	protected void paintGradient(Graphics2D g0, int y, int h, Shape fillShape) {
		if (h <= 0)
			return;

		Graphics2D g = (Graphics2D) g0.create();
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		Paint p = createGradient(y, h, fillColor_top, fillColor_bottom);
		if (p != null) {
			g.setPaint(p);
			g.fill(fillShape);
		}
		p = createGradient(y, h, strokeColor_top, strokeColor_bottom);
		if (p != null) {
			g.setPaint(p);
			g.setStroke(new BasicStroke(1));
			g.draw(fillShape);
		}
		g.dispose();
	}

	private Paint createGradient(int y, int h, Color color1, Color color2) {
		if (color1 == null && color2 == null)
			return null;
		if (color1 == null)
			color1 = new Color(color2.getRed(), color2.getGreen(),
					color2.getBlue(), 0);
		if (color2 == null)
			color2 = new Color(color1.getRed(), color1.getGreen(),
					color1.getBlue(), 0);

		return new GradientPaint(0, y, color1, 0, y + h, color2);
	}
}