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
package com.pump.text;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position.Bias;

public class UnderlineHighlightPainter implements HighlightPainter {
	Color color;
	int thickness;
	boolean squiggle;

	public UnderlineHighlightPainter(float hue, int thickness) {
		this(createColor(hue), thickness);
	}

	public UnderlineHighlightPainter(Color color, int thickness) {
		this(color, thickness, false);
	}

	public UnderlineHighlightPainter(Color color, int thickness,
			boolean squiggle) {
		this.color = color;
		this.thickness = thickness;
		this.squiggle = squiggle;
	}

	private static Color createColor(float hue) {
		return new Color(Color.HSBtoRGB(hue, 1, 1));
	}

	@Override
	public void paint(Graphics g0, int p0, int p1, Shape bounds,
			JTextComponent c) {
		Graphics2D g = (Graphics2D) g0.create();
		try {
			int length = c.getDocument().getLength();
			p0 = Math.min(Math.max(0, p0), length);
			p1 = Math.min(Math.max(0, p1), length);
			Rectangle rect0 = c.modelToView(p0);
			Rectangle rect1 = c.modelToView(p1);
			if (rect0.y + rect0.height == rect1.y + rect1.height) {
				drawLine(g, p0, p1, rect0.x, rect1.x + rect1.width, rect0.y
						+ rect0.height, squiggle);
			} else {
				int currentY = rect0.y + rect0.height;
				int startX = rect0.x;

				Rectangle r = rect0;
				Point p = new Point(1000000, 0);
				int lastPos = p0;
				drawLines: while (true) {
					p.y = r.y + r.height / 2;
					int pos = c.viewToModel(p);
					if (p1 <= pos) {
						drawLine(g, lastPos, p1, startX, rect1.x + rect1.width,
								currentY, squiggle);
						break drawLines;
					} else {
						r = c.modelToView(Math.min(pos, p1));
						drawLine(g, lastPos, pos, startX, r.x + r.width,
								currentY, squiggle);

						lastPos = pos + 1;
						r = c.getUI().modelToView(c, lastPos, Bias.Forward);
						startX = r.x;
						currentY = r.y + r.height;
					}
				}
			}
		} catch (BadLocationException e) {
			throw new RuntimeException(e);
		} finally {
			g.dispose();
		}
	}

	/**
	 * 
	 * @param g
	 * @param p0
	 *            the character position where this highlight starts
	 * @param p1
	 *            the character position where this highlight ends
	 * @param x1
	 *            the x-position where this highlight starts
	 * @param x2
	 *            the x-position where this highlight ends
	 * @param y
	 *            the y-position this line is on.
	 */
	protected void drawLine(Graphics2D g, int p0, int p1, int x1, int x2,
			int y, boolean squiggle) {

		if (g.hitClip(x1, y - thickness - 2, x2 - x1, 2 * thickness + 4)) {
			Shape line;
			if (!squiggle) {
				line = new Line2D.Double(x1, y + thickness / 2, x2, y
						+ thickness / 2);
			} else {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				y += thickness / 2;
				GeneralPath path = new GeneralPath();
				int x = x1 + 2;
				int ctr = 0;
				path.moveTo(x1, y);
				while (x < x2) {
					if (ctr % 2 == 0) {
						path.curveTo(x + 1, y, x, y - 1, x + 1, y - 1);
					} else {
						path.curveTo(x + 1, y - 1, x, y, x + 1, y);
					}
					ctr++;
					x += 2;
				}
				line = path;
			}
			g = (Graphics2D) g.create();
			g.setColor(color);
			g.setStroke(new BasicStroke(thickness));
			g.draw(line);
			g.dispose();
		}
	}
}