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
package com.pump.image;

import com.pump.image.pixel.ImageType;
import com.pump.image.pixel.PixelIterator;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * This contains static helper methods to get the bounding box of translucent pixels in a BufferedImage.
 */
public class ImageBounds {

	static int DEFAULT_THRESHOLD = 125;

	/**
	 * Returns the smallest rectangle enclosing the pixels in this image that
	 * are non-translucent.
	 * 
	 * 
	 * @param bi
	 *            a TYPE_INT_ARGB image.
	 * @return the smallest rectangle enclosing the pixels in this image that
	 *         are non-translucent.
	 */
	public static Rectangle getBounds(BufferedImage bi) {
		return getBounds(bi, DEFAULT_THRESHOLD);
	}

	/**
	 * Returns the smallest rectangle enclosing the pixels in this image that
	 * are non-translucent.
	 * 
	 * 
	 * @param bi
	 *            a TYPE_INT_ARGB image.
	 * @param alphaThreshold
	 *            the alpha channel threshold for a pixel that is included in
	 *            the rectangle. For example: if the alpha component of a pixel
	 *            is 50 and the threshold is 60 then that pixel is ignored.
	 * @return the smallest rectangle enclosing the pixels in this image that
	 *         are non-translucent, or null if no pixels exceeded the given threshold
	 */
	public static Rectangle getBounds(BufferedImage bi, int alphaThreshold) {
		if (bi.getTransparency() == Transparency.OPAQUE)
			return new Rectangle(0,0,bi.getWidth(),bi.getHeight());

		int w = bi.getWidth();
		int h = bi.getHeight();
		PixelIterator<int[]> pixelIter = ImageType.INT_ARGB.createPixelIterator(bi);
		int[] row = new int[w];

		int x1 = -1;
		int x2 = -1;
		int y1 = -1;
		int y2 = -1;

		int y = 0;
		while(!pixelIter.isDone()) {
			pixelIter.next(row, 0);
			boolean foundPixel = false;
			for (int x = 0; x < w; x++) {
				int alpha = (row[x] >> 24) & 0xff;
				if (alpha > alphaThreshold) {
					foundPixel = true;
					if (x1 == -1) {
						x1 = x2 = x;
						y1 = y2 = y;
					} else {
						x1 = Math.min(x, x1);
						y2 = Math.max(y, y2);
					}
					break;
				}
			}
			if (foundPixel) {
				for (int x = w - 1; x >= 0; x--) {
					int alpha = (row[x] >> 24) & 0xff;
					if (alpha > alphaThreshold) {
						x2 = Math.max(x, x2);
					}
				}
			}
			y++;
		}

		if (x1 == -1)
			return null;
		return new Rectangle(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
	}
}