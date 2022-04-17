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
package com.pump.image.shadow;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Hashtable;

import com.pump.image.pixel.BufferedImageIterator;
import com.pump.image.pixel.converter.IntARGBConverter;

/**
 * This represents ARGB-encoded pixel data for an image.
 * <p>
 * Some methods include the option to directly reference or assign the int
 * arrays used by BufferedImages. The DataBuffer documentation cautions against
 * this and says it makes those images "untracked". There may be
 * platform-specific optimizations we'll miss out on when a buffer becomes
 * untracked. I definitely saw this a decade ago when Mac used the Quartz
 * rendering system, but I checked just now and didn't observe a performance
 * difference when I tried painting to an untracked DataBuffer.
 * <p>
 * However: it's also worth noting I didn't see a significant improvement in
 * performance either. So I'm not convinced it matters one way or the other.
 */
public class ARGBPixels implements Serializable {
	private static final long serialVersionUID = 1L;

	private int width, height;
	private int[] pixels;

	/**
	 * Create a blank ARGBPixels.
	 * 
	 * @param width
	 * @param height
	 */
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

	/**
	 * Create a ARGBPixels that copies all the pixel data from the source image.
	 * 
	 * @param srcImage
	 *            the image to copy data from.
	 */
	public ARGBPixels(BufferedImage srcImage) {
		this(srcImage, false);
	}

	/**
	 * Create a ARGBPixels based on an image.
	 * 
	 * @param srcImage
	 *            the image used to copy or reference the pixel data.
	 * @param referencePixels
	 *            if true then (if possible) we'll reach inside and grab the
	 *            reference to the int[] array used to store the ARGB pixel
	 *            data. This can untrack/unmanage the image, which may adversely
	 *            effect platform-specific optimizations. If false -- or if the
	 *            pixels can't be easily grabbed -- then this ARGBPixels object
	 *            creates a new int array to store the data.
	 */
	public ARGBPixels(BufferedImage srcImage, boolean referencePixels) {
		this.width = srcImage.getWidth();
		this.height = srcImage.getHeight();

		if (srcImage.getType() == BufferedImage.TYPE_INT_ARGB) {
			if (referencePixels) {
				DataBufferInt dbi = (DataBufferInt) srcImage.getRaster()
						.getDataBuffer();
				if (dbi.getNumBanks() == 1 && dbi.getOffset() == 0) {
					pixels = dbi.getData();
				}
			}

			if (pixels == null) {
				pixels = new int[width * height];
				srcImage.getRaster().getDataElements(0, 0, srcImage.getWidth(),
						srcImage.getHeight(), pixels);
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

	/**
	 * Return the width of this pixel data.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Return the height of this pixel data.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Return the pixel data in this object.
	 * <p>
	 * This returns the actually pixel data (not a copy of the array).
	 */
	public int[] getPixels() {
		return pixels;
	}

	/**
	 * Create a new BufferedImage that copies the pixel data in this object.
	 */
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
	public BufferedImage createBufferedImage(boolean createUntrackable) {
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

	@Override
	public int hashCode() {
		int k = getWidth() * getHeight();
		k ^= pixels[getHeight() * getWidth() / 2];
		return k;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ARGBPixels))
			return false;
		ARGBPixels other = (ARGBPixels) obj;
		if (other.getWidth() != getWidth())
			return false;
		if (other.getHeight() != getHeight())
			return false;
		if (!Arrays.equals(getPixels(), other.getPixels()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[ width=" + getWidth()
				+ ", height=" + getHeight() + "]";
	}

	private void writeObject(java.io.ObjectOutputStream out)
			throws IOException {
		out.writeInt(0);
		out.writeInt(getWidth());
		out.writeInt(getHeight());
		out.writeObject(getPixels());
	}

	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		int version = in.readInt();
		if (version == 0) {
			width = in.readInt();
			height = in.readInt();
			pixels = (int[]) in.readObject();
		} else {
			throw new IOException("unsupported internal version: " + version);
		}
	}

}