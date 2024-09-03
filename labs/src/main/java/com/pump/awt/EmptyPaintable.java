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
package com.pump.awt;

import java.awt.Color;
import java.awt.Graphics2D;

public class EmptyPaintable implements Paintable {
	Color color;
	int width, height;

	public EmptyPaintable(int width, int height) {
		this(null, width, height);
	}

	public EmptyPaintable(Color color, int width, int height) {
		this.width = width;
		this.height = height;
		this.color = color;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void paint(Graphics2D g) {
		if (color != null) {
			g.setColor(color);
			g.fillRect(0, 0, width, height);
		}
	}

}