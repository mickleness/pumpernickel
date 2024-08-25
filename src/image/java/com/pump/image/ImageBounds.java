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

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.pump.reflect.Reflection;

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
	 *         are non-translucent.
	 */
	public static Rectangle getBounds(BufferedImage bi, int alphaThreshold) {
		int type = bi.getType();
		if (type == BufferedImage.TYPE_INT_ARGB) {
			return getARGBBounds(bi, alphaThreshold);
		}
		throw new IllegalArgumentException("Illegal image type ("
				+ Reflection.nameStaticField(BufferedImage.class, new Integer(
						type)));
	}

	private static Rectangle getARGBBounds(BufferedImage bi, int alphaThreshold) {
		int[] array = new int[bi.getWidth()];

		int h = bi.getHeight();
		int w = bi.getWidth();
		int minX = -1;
		int maxX = -1;
		int minY = -1;
		int maxY = -1;

		findMinY: for (int y = 0; y < h; y++) {
			bi.getRaster().getDataElements(0, y, w, 1, array);
			for (int x = 0; x < w; x++) {
				int alpha = (array[x] >> 24) & 0xff;
				if (alpha > alphaThreshold) {
					minX = x;
					maxX = x;
					minY = y;
					break findMinY;
				}
			}
		}

		if (minY == -1)
			return null;

		findMaxY: for (int y = h - 1; y >= 0; y--) {
			bi.getRaster().getDataElements(0, y, w, 1, array);
			for (int x = 0; x < w; x++) {
				int alpha = (array[x] >> 24) & 0xff;
				if (alpha > alphaThreshold) {
					minX = (x < minX) ? x : minX;
					maxX = (x > maxX) ? x : maxX;
					maxY = y;
					break findMaxY;
				}
			}
		}

		for (int y = minY; y <= maxY; y++) {
			bi.getRaster().getDataElements(0, y, w, 1, array);
			minSearch: for (int x = 0; x < minX; x++) {
				int alpha = (array[x] >> 24) & 0xff;
				if (alpha > alphaThreshold) {
					minX = x;
					break minSearch;
				}
			}
			maxSearch: for (int x = w - 1; x > maxX; x--) {
				int alpha = (array[x] >> 24) & 0xff;
				if (alpha > alphaThreshold) {
					maxX = x;
					break maxSearch;
				}
			}
		}

		return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
	}
}