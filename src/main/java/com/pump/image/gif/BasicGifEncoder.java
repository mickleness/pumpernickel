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
package com.pump.image.gif;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;

import com.pump.image.gif.block.GifGraphicControlExtension;
import com.pump.image.gif.block.GifGraphicControlExtension.DisposalMethod;
import com.pump.image.gif.block.GifImageDataBlock;
import com.pump.image.gif.block.GifImageDescriptor;
import com.pump.image.gif.block.GifLocalColorTable;
import com.pump.image.pixel.ImageType;
import com.pump.image.pixel.PixelIterator;

/**
 * This tests out 3 possible frame disposal methods and chooses the one that
 * would result in the smallest frame dimensions.
 * <p>
 * To encode the nth frame, we need to see if the (n-1)th frame, the (n-2)th
 * frame, or (as a last resort) the background color should be our starting
 * template. Once we've decided this: we can reduce the dimensions of the nth
 * frame.
 */
public class BasicGifEncoder extends GifEncoder {

	static class Frame {
		/** The image representing all of this frame */
		BufferedImage wholeImage;

		/**
		 * A subset of wholeImage that we'll actually write to the OutputStream.
		 */
		BufferedImage optimizedImage;

		/** The duration (in cs) of this frame. */
		int durationInCentiseconds;

		/** The disposal method of this frame. */
		DisposalMethod disposalMethod = DisposalMethod.LEAVE;

		/** Whether we'll prepend a local color table before this frame. */
		boolean writeLocalColorTable;

		/** The global color table in use (may be null). */
		IndexColorModel globalColorModel;

		/** The topleft x-coordinate to render optimizedImage. */
		int x = 0;

		/** The topleft y-coordinate to render optimizedImage. */
		int y = 0;

		public Frame(BufferedImage image, int durationInCentiseconds,
				IndexColorModel globalColorModel,
				boolean writeLocalColorTable) {
			this.wholeImage = image;
			this.optimizedImage = image;
			this.globalColorModel = globalColorModel;
			this.durationInCentiseconds = Math.max(5, durationInCentiseconds);
			this.writeLocalColorTable = writeLocalColorTable;
		}

		void write(OutputStream out) throws IOException {
			if (durationInCentiseconds >= 0
					|| globalColorModel.getTransparentPixel() != -1
					|| disposalMethod != DisposalMethod.LEAVE) {
				GifGraphicControlExtension gce = new GifGraphicControlExtension(
						Math.max(0, durationInCentiseconds), disposalMethod,
						globalColorModel.getTransparentPixel());
				gce.write(out);
			}
			int localColorSize = 0;
			if (writeLocalColorTable) {
				localColorSize = globalColorModel.getMapSize();
				int k = 2;
				while (localColorSize > k) {
					k *= 2;
				}
				if (k > 256)
					throw new IllegalArgumentException(
							"Illegal number of colors (" + localColorSize
									+ ").  There can only be 256 at most.");
				localColorSize = k;
			}

			Dimension d = new Dimension(optimizedImage.getWidth(),
					optimizedImage.getHeight());

			GifImageDescriptor id = new GifImageDescriptor(x, y, d.width,
					d.height, false, localColorSize);
			id.write(out);
			if (localColorSize > 0) {
				GifLocalColorTable ct = new GifLocalColorTable(
						globalColorModel);
				ct.write(out);
			}
			GifImageDataBlock dataBlock = new GifImageDataBlock(optimizedImage,
					globalColorModel);
			dataBlock.write(out);
		}

		void optimize(Rectangle contrast) {
			optimizedImage = wholeImage.getSubimage(contrast.x, contrast.y,
					contrast.width, contrast.height);
			x = contrast.x;
			y = contrast.y;
		}
	}

	LinkedList<Frame> frameBuffer = new LinkedList<Frame>();

	@Override
	public synchronized void writeImage(OutputStream out, BufferedImage image,
			int durationInCentiseconds, IndexColorModel globalColorModel,
			boolean writeLocalColorTable) throws IOException {
		if (frameBuffer.size() > 0) {
			Frame lastFrame = frameBuffer.get(frameBuffer.size() - 1);
			if (getContrastRectangle(lastFrame.wholeImage, image) == null) {
				lastFrame.durationInCentiseconds += durationInCentiseconds;
				return;
			}
		}

		Frame newFrame = new Frame(image, durationInCentiseconds,
				globalColorModel, writeLocalColorTable);
		frameBuffer.add(newFrame);

		try {
			if (frameBuffer.size() >= 3) {
				Rectangle contrast1 = getContrastRectangle(
						frameBuffer.get(0).wholeImage, newFrame.wholeImage);
				Rectangle contrast2 = getContrastRectangle(
						frameBuffer.get(1).wholeImage, newFrame.wholeImage);
				int area1 = contrast1 == null ? Integer.MAX_VALUE
						: contrast1.width * contrast1.height;
				int area2 = contrast2 == null ? Integer.MAX_VALUE
						: contrast2.width * contrast2.height;
				if (area2 <= area1) {
					frameBuffer.get(1).disposalMethod = DisposalMethod.LEAVE;
					frameBuffer.get(2).optimize(contrast2);
				} else {
					frameBuffer.get(1).disposalMethod = DisposalMethod.PREVIOUS;
					frameBuffer.get(2).optimize(contrast1);
				}
			} else if (frameBuffer.size() == 2) {

			} else if (frameBuffer.size() == 1) {

			}
		} finally {
			while (frameBuffer.size() > 2) {
				frameBuffer.pop().write(out);
			}
		}
	}

	/**
	 * Returns the smallest possible rectangle that encloses the differences
	 * between two images, or null if they are exactly the same.
	 */
	private static Rectangle getContrastRectangle(BufferedImage background,
			BufferedImage incoming) {
		if (background.getWidth() != incoming.getWidth()
				|| background.getHeight() != incoming.getHeight())
			throw new IllegalArgumentException("images are different sizes: "
					+ background.getWidth() + "x" + background.getHeight()
					+ ", " + incoming.getWidth() + "x" + incoming.getHeight());

		PixelIterator<int[]> c1 = ImageType.INT_ARGB.createPixelIterator(background);
		PixelIterator<int[]> c2 = ImageType.INT_ARGB.createPixelIterator(incoming);
		int[] row1 = new int[c1.getWidth() * c1.getPixelSize()];
		int[] row2 = new int[c2.getWidth() * c2.getPixelSize()];
		Rectangle r = null;

		int y = 0;
		while (!c1.isDone()) {
			c1.next(row1);
			c2.next(row2);
			int w = incoming.getWidth();
			for (int x = 0; x < w; x++) {
				int a1 = (row1[x] >> 24) & 0xff;
				int r1 = (row1[x] >> 16) & 0xff;
				int g1 = (row1[x] >> 8) & 0xff;
				int b1 = (row1[x] >> 0) & 0xff;
				int a2 = (row2[x] >> 24) & 0xff;
				int r2 = (row2[x] >> 16) & 0xff;
				int g2 = (row2[x] >> 8) & 0xff;
				int b2 = (row2[x] >> 0) & 0xff;
				if (a2 < a1 || r1 != r2 || g1 != g2 || b1 != b2) {
					if (r == null) {
						r = new Rectangle(x, y, 1, 1);
					} else {
						if (x < r.x) {
							// new left boundary
							r.width += r.x - x;
							r.x = x;
						} else if (x >= r.x + r.width) {
							// new right boundary
							r.width = x - r.x + 1;
						}

						// new bottom boundary
						r.height = y - r.y + 1;
					}
				}
			}
			y++;
		}
		return r;
	}

	/**
	 * This writes remaining frames.
	 */
	@Override
	public synchronized void flush(OutputStream out) throws IOException {
		while (frameBuffer.size() > 0) {
			frameBuffer.pop().write(out);
		}
	}
}