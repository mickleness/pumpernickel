package com.pump.image.pixel;

import java.awt.image.BufferedImage;

import com.pump.image.pixel.converter.IndexColorModelLUT;
import com.pump.image.pixel.converter.IntPixelConverter;

public abstract class ImageTypeInt extends ImageType {

	protected ImageTypeInt(String name, int code) {
		super(name, code);
	}

	public abstract void convertFromARGBPre(int[] pixels, int width);

	public abstract void convertFromARGB(int[] pixels, int width);

	public abstract void convertFromRGB(int[] pixels, int width);

	public abstract void convertFromBGR(int[] pixels, int width);

	void invertLast3Channels_noAlpha(int[] pixels, int width) {
		for (int a = 0; a < width; a++) {
			pixels[a] = ((pixels[a] & 0xff0000) >> 16) | (pixels[a] & 0xff00)
					| ((pixels[a] & 0xff) << 16);
		}
	}

	void invertLast3Channels_replaceAlpha(int[] pixels, int width) {
		for (int a = 0; a < width; a++) {
			pixels[a] = 0xff000000 | ((pixels[a] & 0xff0000) >> 16)
					| (pixels[a] & 0xff00) | ((pixels[a] & 0xff) << 16);
		}
	}

	void replaceAlpha(int[] pixels, int width) {
		for (int a = 0; a < width; a++) {
			pixels[a] = 0xff000000 | (pixels[a] & 0xffffff);
		}
	}

	public abstract void convertFromBGR(byte[] bytesIn, int[] pixels,
			int width);

	void invertLast3Channels_replaceAlpha(byte[] bytesIn, int[] pixels,
			int width) {
		for (int byteCtr = 0, intCtr = 0; intCtr < width;) {
			pixels[intCtr++] = 0xff000000 | (bytesIn[byteCtr++] & 0xff)
					| ((bytesIn[byteCtr++] & 0xff) << 8)
					| ((bytesIn[byteCtr++] & 0xff) << 16);
		}
	}

	public abstract void convertFromABGRPre(byte[] bytesIn, int[] pixels,
			int width);

	public abstract void convertFromABGR(byte[] bytesIn, int[] pixels,
			int width);

	public abstract void convertFromARGB(byte[] bytesIn, int[] pixels,
			int width);

	public abstract void convertFromARGBPre(byte[] bytesIn, int[] pixels,
			int width);

	public abstract void convertFromBGRA(byte[] bytesIn, int[] pixels,
			int width);

	public abstract void convertFromGray(byte[] bytesIn, int[] pixels,
			int width);

	public abstract void convertFromIndex(byte[] bytesIn, int[] pixels,
			int width, IndexColorModelLUT indexLUT);

	public abstract void convertFromRGB(byte[] bytesIn, int[] pixels,
			int width);

	@Override
	public IntPixelConverter createConverter(PixelIterator<?> srcIter) {
		return new IntPixelConverter(srcIter, this);
	}

	@Override
	public IntPixelConverter createConverter(BufferedImage bi) {
		return createConverter(BufferedImageIterator.get(bi));
	}
}
