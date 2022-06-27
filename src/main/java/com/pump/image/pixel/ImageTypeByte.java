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
package com.pump.image.pixel;

import java.awt.image.BufferedImage;

import com.pump.image.pixel.converter.BytePixelConverter;
import com.pump.image.pixel.converter.IndexColorModelLUT;

public abstract class ImageTypeByte extends ImageType {

	public final int bytesPerPixel;

	protected ImageTypeByte(String name, int code, int bytesPerPixel) {
		super(name, code);
		this.bytesPerPixel = bytesPerPixel;
	}

	public abstract void convertFromARGBPre(int[] intIn, byte[] pixels,
			int width);

	public abstract void convertFromARGB(int[] intIn, byte[] pixels, int width);

	public abstract void convertFromRGB(int[] intIn, byte[] pixels, int width);

	public abstract void convertFromBGR(int[] intIn, byte[] pixels, int width);

	public abstract void convertFromBGR(byte[] pixels, int width);

	public abstract void convertFromABGR(byte[] pixels, int width);

	public abstract void convertFromABGRPre(byte[] pixels, int width);

	public abstract void convertFromRGB(byte[] pixels, int width);

	public abstract void convertFromARGB(byte[] pixels, int width);

	public abstract void convertFromARGBPre(byte[] pixels, int width);

	public abstract void convertFromBGRA(byte[] pixels, int width);

	public abstract void convertFromGray(byte[] pixels, int width);

	public abstract void convertFromIndex(byte[] pixels, int width,
			IndexColorModelLUT indexLUT);

	@Override
	public BytePixelConverter createConverter(PixelIterator<?> srcIter) {
		return new BytePixelConverter(srcIter, this);
	}

	@Override
	public BytePixelConverter createConverter(BufferedImage bi) {
		return new BytePixelConverter(BufferedImageIterator.get(bi), this);
	}
}