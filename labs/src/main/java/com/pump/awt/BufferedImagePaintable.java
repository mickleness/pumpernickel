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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/** A Paintable that paints a BufferedImage. */
public class BufferedImagePaintable implements Paintable {
	BufferedImage image;

	public BufferedImagePaintable(BufferedImage img) {
		if (img == null)
			throw new NullPointerException();
		image = img;
	}

	public BufferedImage getImage() {
		return image;
	}

	public int getWidth() {
		return image.getWidth();
	}

	public int getHeight() {
		return image.getHeight();
	}

	public void paint(Graphics2D g) {
		g.drawImage(image, 0, 0, null);
	}
}