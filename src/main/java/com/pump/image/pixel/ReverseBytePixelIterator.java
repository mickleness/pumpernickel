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
package com.pump.image.pixel;

import java.awt.image.BufferedImage;

/**
 * This iterator swaps the order of color components. So if the components are
 * stored in the order {r1, g1, b1, r2, g2, b2, ...} then this will return
 * arrays as {b1, g1, r1, b2, g2, r2, ...}.
 */
public class ReverseBytePixelIterator implements BytePixelIterator {

	BytePixelIterator i;
	final int bytesPerPixel, len, k;

	public ReverseBytePixelIterator(BytePixelIterator i) {
		this.i = i;

		bytesPerPixel = i.getPixelSize();
		len = i.getWidth() * bytesPerPixel;
		k = bytesPerPixel / 2;
	}

	public void next(byte[] dest) {
		i.next(dest);
		if (bytesPerPixel == 3) {
			for (int x = 0; x < len; x += bytesPerPixel) {
				byte t = dest[x];
				dest[x] = dest[x + 2];
				dest[x + 2] = t;
			}
		} else if (bytesPerPixel == 4) {
			for (int x = 0; x < len; x += bytesPerPixel) {
				byte t = dest[x];
				dest[x] = dest[x + 3];
				dest[x + 3] = t;

				t = dest[x + 1];
				dest[x + 1] = dest[x + 2];
				dest[x + 2] = t;
			}
		} else if (bytesPerPixel == 1) {
			return;
		} else {
			for (int x = 0; x < len; x += bytesPerPixel) {
				for (int z = 0; z < k; z++) {
					byte t = dest[x + z];
					dest[x + z] = dest[x + bytesPerPixel - 1 - z];
					dest[x + bytesPerPixel - 1 - z] = t;
				}
			}
		}
	}

	public void skip() {
		i.skip();
	}

	public int getHeight() {
		return i.getHeight();
	}

	public int getMinimumArrayLength() {
		return i.getMinimumArrayLength();
	}

	public int getPixelSize() {
		return i.getPixelSize();
	}

	public int getType() {
		return BufferedImage.TYPE_CUSTOM;
	}

	public int getWidth() {
		return i.getWidth();
	}

	public boolean isDone() {
		return i.isDone();
	}

	public boolean isOpaque() {
		return i.isOpaque();
	}

	public boolean isTopDown() {
		return i.isTopDown();
	}
}