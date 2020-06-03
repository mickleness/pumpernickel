package com.pump.image.shadow;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

import com.pump.image.pixel.BufferedImageIterator;
import com.pump.image.pixel.IntARGBConverter;

public class ARGBPixels {
	int width, height;
	int[] pixels;

	public ARGBPixels(int width, int height) {
		if (width <= 0)
			throw new IllegalArgumentException(
					"width (" + width + ") must be greater than zero");
		if (height <= 0)
			throw new IllegalArgumentException(
					"height (" + height + ") must be greater than zero");
		this.width = width;
		this.height = height;
		pixels = new int[width * height];
	}

	public ARGBPixels(BufferedImage srcImage) {
		this(srcImage, false);
	}

	/**
	 * This constructor is private because I don't want to let you pass in true
	 * for referencePixels yet. I'm seeing a pronounced (but unexplained) impact
	 * on some performance tests. Since I don't understand what's happening, I
	 * don't want to meddle with it right now.
	 * 
	 * @param srcImage
	 * @param referencePixels
	 *            if true then (if possible) we'll reach inside and grab the
	 *            reference to the int[] array used to store the ARGB pixel
	 *            data. This can untrack/unmanage the image, which may adversely
	 *            effect platform-specific optimizations.
	 */
	private ARGBPixels(BufferedImage srcImage, boolean referencePixels) {
		this.width = srcImage.getWidth();
		this.height = srcImage.getHeight();

		if (referencePixels
				&& srcImage.getType() == BufferedImage.TYPE_INT_ARGB) {
			DataBufferInt dbi = (DataBufferInt) srcImage.getRaster()
					.getDataBuffer();
			if (dbi.getNumBanks() == 1 && dbi.getOffset() == 0) {
				pixels = dbi.getData();
			}
		}

		if (pixels == null) {
			pixels = new int[width * height];
			int[] row = new int[width];
			IntARGBConverter c = new IntARGBConverter(
					BufferedImageIterator.get(srcImage));
			int y = 0;
			while (!c.isDone()) {
				c.next(row);
				System.arraycopy(row, 0, pixels, y * width, width);
				y++;
			}
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int[] getPixels() {
		return pixels;
	}

	public BufferedImage createBufferedImage() {
		return createBufferedImage(false);
	}

	/**
	 * Create a new BufferedImage based on this ARGB pixel data.
	 * <p>
	 * This method is private because I don't understand the performance impact
	 * of passing in true for createUntrackable.
	 * 
	 * @param createUntrackable
	 *            if true then this returns a BufferedImage that is directly
	 *            backed by {@link #getPixels()}. This creates what is known as
	 *            an "untrackable" DataBuffer, and you may miss out on certain
	 *            platform-specific graphics optimizations. (I think I also
	 *            heard this refered to as an "unmanaged" image a long time
	 *            ago?). If false then this returns a new BufferedImage and
	 *            copies all of {@link #getPixels()} into the image. So the two
	 *            resources are separate, but the resulting BufferedImage may
	 *            perform faster.
	 * 
	 * @return an ARGB BufferedImage that displays {@link #getPixels()}.
	 */
	private BufferedImage createBufferedImage(boolean createUntrackable) {
		BufferedImage returnValue;
		if (createUntrackable) {
			ColorModel colorModel = ColorModel.getRGBdefault();
			DataBufferInt dataBuffer = new DataBufferInt(getPixels(),
					getWidth() * getHeight());
			int[] bandMasks = new int[] { 0x00ff0000, 0x0000ff00, 0x000000ff,
					0xff000000 };
			WritableRaster raster = Raster.createPackedRaster(dataBuffer,
					getWidth(), getHeight(), getWidth(), bandMasks, null);
			returnValue = new BufferedImage(colorModel, raster, false,
					new Hashtable<>());
		} else {
			returnValue = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_ARGB);
			returnValue.getRaster().setDataElements(0, 0, getWidth(),
					getHeight(), getPixels());
		}
		return returnValue;
	}
}
