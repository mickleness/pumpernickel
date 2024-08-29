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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

/**
 * This Border is also capable of painting the un-bordered middle section
 * of a component using {@link #paintContents(Component, Graphics, int, int, int, int)}.
 * Both calls (painting the contents vs painting the border) ultimately call
 * {@link #paint(Component, Graphics2D, int, int, int, int)} with different clipping.
 * <p>
 * To fully paint a Background you can call:
 * <code>paintBorder(c, g, x, y, w, h);
 * paintContents(c, g, x, y, w, h);</code>
 * <p>
 * But if you've applied this as a Border to a JComponent: the call to paintBorder(..)
 * should automatically happen.
 */
public abstract class Background implements Border {

	/**
	 * Paint the contents of an outermost Background. This takes into account the padding
	 * of other borders (if a CompoundBorder is used). If the component does
	 * not include a Background as any of its Borders: then this method
	 * does nothing.
	 */
	public static boolean paintBackgroundContents(Graphics g, JComponent c) {
		int x = 0;
		int y = 0;
		int width = c.getWidth();
		int height = c.getHeight();

		List<Border> borders = getBorders(c.getBorder());
		int backgroundIndex = -1;
		for (int a = 0; a < borders.size(); a++) {
			if (borders.get(a) instanceof Background) {
				backgroundIndex = a;
				break;
			}
		}

		if (backgroundIndex != -1) {
			for (int a = 0; a < backgroundIndex; a++) {
				Border border = borders.get(a);
				Insets i = border.getBorderInsets(c);
				x += i.left;
				y += i.top;
				width -= i.left + i.right;
				height -= i.top + i.bottom;
			}

			Background bkgnd = (Background) borders.get(backgroundIndex);
			bkgnd.paintContents(c, g, x, y, width, height);
			return true;
		}
		return false;
	}

	private static List<Border> getBorders(Border border) {
		List<Border> list = new ArrayList<>();
		if (border instanceof CompoundBorder) {
			CompoundBorder c = (CompoundBorder) border;
			list.addAll(getBorders(c.getOutsideBorder()));
			list.addAll(getBorders(c.getInsideBorder()));
		} else if (border != null) {
			list.add(border);
		}
		return list;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height) {
		Graphics2D g2 = (Graphics2D) g.create(x, y, width, height);

		Area area = new Area(new Rectangle(0, 0, width, height));
		Insets insets = getBorderInsets(c);
		int x2 = x + insets.left;
		int y2 = y + insets.top;
		int width2 = width - insets.left - insets.right;
		int height2 = height - insets.top - insets.bottom;
		if (width2 > 0 && height2 > 0) {
			area.subtract(new Area(new Rectangle(x2, y2, width2, height2)));
			g2.clip(area);
		}
		paint(c, g2, x, y, width, height);
		g2.dispose();
	}

	/**
	 * Paint the middle part (the un-bordered part) of this Background.
	 * The dimensions provided here should be for the entire area.
	 */
	public void paintContents(Component c, Graphics g, int x, int y, int width,
			int height) {
		Insets insets = getBorderInsets(c);
		Rectangle insetRect = new Rectangle(x + insets.left, y + insets.top,
				width - insets.left - insets.right, height - insets.top
						- insets.bottom);
		if (insetRect.width > 0 && insetRect.height > 0) {
			Graphics2D g2 = (Graphics2D) g.create(x, y, width, height);
			g2.clipRect(insetRect.x, insetRect.y, insetRect.width,
					insetRect.height);
			paint(c, g2, 0, 0, width, height);
			g2.dispose();
		}

	}

	/**
	 * Paint the entire Background (border and contents).
	 */
	protected abstract void paint(Component c, Graphics2D g, int x, int y,
			int width, int height);

	@Override
	public boolean isBorderOpaque() {
		return false;
	}

}