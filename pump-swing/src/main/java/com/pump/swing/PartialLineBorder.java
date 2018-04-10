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
package com.pump.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;

import javax.swing.border.Border;

/** A border that only paints some of its edges. */
public class PartialLineBorder implements Border {
	Paint p;
	Insets i;

	public PartialLineBorder(Paint p, Insets i) {
		this.p = p;
		this.i = (Insets) i.clone();

	}

	public Insets getBorderInsets(Component c) {
		return (Insets) i.clone();
	}

	public boolean isBorderOpaque() {
		return false;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
		((Graphics2D) g).setPaint(p);
		for (int a = y; a < y + i.top; a++) {
			g.drawLine(x, a, x + w, a);
		}
		for (int a = x; a < x + i.left; a++) {
			g.drawLine(a, y, a, y + h);
		}
		for (int a = y + h - i.bottom; a < y + h; a++) {
			g.drawLine(x, a, x + w, a);
		}
		for (int a = x + w - i.right; a < x + w; a++) {
			g.drawLine(a, y, a, y + h);
		}
	}

}