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
package com.pump.image.pixel.quantize;

import java.awt.image.BufferedImage;

import com.pump.image.pixel.ImageType;
import com.pump.image.pixel.IndexedBytePixelIterator;
import com.pump.image.pixel.IntPixelIterator;

/**
 * This implements image quantization using a nearest-neighbor approach.
 * <p>
 * This was largely developed as a baseline to test against. Unless you have a
 * specific reason for seeking this: I strongly recommend using
 * {@link ErrorDiffusionImageQuantization} instead.
 */
public class NearestNeighborImageQuantization extends ImageQuantization {

	/**
	 * The pixel iterator that implements the nearest neighbor image
	 * quantization.
	 */
	protected static class NearestNeighborIndexedBytePixelIterator
			extends AbstractIndexedBytePixelIterator {

		int[] incomingRow;
		IntPixelIterator iter;
		protected int y = 0;

		NearestNeighborIndexedBytePixelIterator(BufferedImage source,
				ColorLUT lut) {
			super(source, lut);
			incomingRow = new int[getWidth()];
			iter = ImageType.INT_ARGB.createConverter(source);
		}

		public void skip() {
			iter.skip();
			y++;
		}

		public void next(byte[] dest) {
			iter.next(incomingRow);

			if (isOpaque()) {

				for (int x = 0; x < iter.getWidth(); x++) {
					int r = (incomingRow[x] >> 16) & 0xff;
					int g = (incomingRow[x] >> 8) & 0xff;
					int b = (incomingRow[x] >> 0) & 0xff;

					int index = lut.getIndexMatch(r, g, b);

					dest[x] = (byte) (index);
				}
			} else {
				int t = icm.getTransparentPixel();
				for (int x = 0; x < iter.getWidth(); x++) {
					int index;
					int a = (incomingRow[x] >> 24) & 0xff;
					if (a < 128) {
						index = t;
					} else {
						int r = (incomingRow[x] >> 16) & 0xff;
						int g = (incomingRow[x] >> 8) & 0xff;
						int b = (incomingRow[x] >> 0) & 0xff;

						index = lut.getIndexMatch(r, g, b);
					}

					dest[x] = (byte) (index);
				}
			}
			y++;
		}

		public boolean isDone() {
			return y == getHeight();
		}
	}

	@Override
	public IndexedBytePixelIterator createImageData(BufferedImage source,
			ColorLUT colorLUT) {
		return new NearestNeighborIndexedBytePixelIterator(source, colorLUT);
	}
}