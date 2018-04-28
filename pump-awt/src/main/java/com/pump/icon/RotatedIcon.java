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
package com.pump.icon;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Icon;

/**
 * A rotated rendering of a given icon.
 * <P>
 * This is (currently) intended for animation, so for the sake of uniformity:
 * this icon uses the same width and height (calculated as the maximum that may
 * be required for the transformed icon). The source icon is always rendered in
 * the center of this space.
 * <p>
 * For example: if you have a 100x100 icon, then the RotatedIcon wrapper will be
 * 142x142 pixels (because it has to allow room for the icon to rotate 45
 * degrees).
 */
public class RotatedIcon implements Icon {
	float rotation;
	Icon icon;
	int size;

	public RotatedIcon(Icon icon, float radians) {
		this.rotation = radians;
		this.icon = icon;

		int h = icon.getIconHeight();
		int w = icon.getIconWidth();
		double k = Math.sqrt(h * h + w * w);
		size = (int) Math.ceil(k);
	}

	public void paintIcon(Component c, Graphics g0, int x, int y) {
		Graphics2D g = (Graphics2D) g0.create();
		g.rotate(rotation, x + size / 2, y + size / 2);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		icon.paintIcon(c, g, x + getIconWidth() / 2 - icon.getIconWidth() / 2,
				y + getIconHeight() / 2 - icon.getIconHeight() / 2);
		g.dispose();
	}

	public int getIconWidth() {
		return size;
	}

	public int getIconHeight() {
		return size;
	}
}