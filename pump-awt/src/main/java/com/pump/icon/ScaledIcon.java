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
import java.awt.geom.AffineTransform;

import javax.swing.Icon;

import com.pump.geom.TransformUtils;

public class ScaledIcon implements Icon {
	int w, h;
	Icon icon;

	public ScaledIcon(Icon i, int w, int h) {
		this.icon = i;
		this.w = w;
		this.h = h;
	}

	public int getIconHeight() {
		return h;
	}

	public int getIconWidth() {
		return w;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		AffineTransform oldTransform = g2.getTransform();
		g2.transform(TransformUtils.createAffineTransform(x, y,
				x + icon.getIconWidth(), y, x, y + icon.getIconHeight(), x, y,
				x + w, y, x, y + h));
		icon.paintIcon(c, g2, x, y);
		g2.setTransform(oldTransform);
	}
}