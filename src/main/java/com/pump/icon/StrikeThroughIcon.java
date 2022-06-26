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
package com.pump.icon;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.Objects;

import javax.swing.Icon;

import com.pump.geom.TransformUtils;

/**
 * This icon is a circle with a diagonal line from the top-left to the
 * bottom-right.
 * <p>
 * This is typically superimposed on another icon to indicate something is
 * banned. For example: a "no smoking" icon might consist of a cigarette icon
 * with this icon layered above it. This is also culturally identifiable via the
 * "ghostbusters" logo, although in that icon the opposite diagonal line is
 * used.
 */
public class StrikeThroughIcon implements Icon {
	Shape outline;
	{
		Path2D p = new Path2D.Double();
		p.append(new Ellipse2D.Float(-10, -10, 20, 20), false);
		p.append(new Line2D.Float(0, -9, 0, 9), false);
		p.transform(AffineTransform.getRotateInstance(-Math.PI / 4));
		outline = new BasicStroke(2).createStrokedShape(p);
	}
	int size;
	Color color;

	/**
	 * Create a 30x30 strikethrough icon with a given color.
	 * 
	 * @param color
	 *            the color to render this icon with.
	 */
	public StrikeThroughIcon(Color color) {
		this(color, 30);
	}

	/**
	 * Create a strikethrough icon.
	 * 
	 * @param color
	 *            the color to render this icon with.
	 * @param size
	 *            the width and height of this icon.
	 */
	public StrikeThroughIcon(Color color, int size) {
		Objects.requireNonNull(color);
		this.color = color;
		this.size = size;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		Rectangle2D dest = new Rectangle2D.Double(x, y, getIconWidth(),
				getIconHeight());
		Rectangle2D src = outline.getBounds2D();
		g2.setColor(color);
		AffineTransform tx = TransformUtils.createAffineTransform(src, dest);
		g2.fill(tx.createTransformedShape(outline));
		g2.dispose();

	}

	@Override
	public int getIconWidth() {
		return size;
	}

	@Override
	public int getIconHeight() {
		return size;
	}

}