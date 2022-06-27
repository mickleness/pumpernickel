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
package com.pump.image.pixel.converter;

import java.awt.image.BufferedImage;

import com.pump.image.pixel.ImageType;
import com.pump.image.pixel.ImageTypeInt;
import com.pump.image.pixel.IntPixelIterator;
import com.pump.image.pixel.PixelIterator;

/**
 * Convert incoming data to an int pixel format.
 */
public class IntPixelConverter extends PixelConverter<int[], ImageTypeInt>
		implements IntPixelIterator {

	/**
	 * This is only used if the src data is packaged in bytes; otherwise we use
	 * one int array for both the src pixels and the converted dest pixels.
	 */
	private transient byte[] byteScratch;

	public IntPixelConverter(PixelIterator<?> iter, ImageTypeInt imageType) {
		super(iter, imageType);
	}

	@Override
	public int getMinimumArrayLength() {
		if (srcIntIterator != null) {
			return Math.max(srcIntIterator.getMinimumArrayLength(), getWidth());
		}
		return getWidth();
	}

	@Override
	public void next(int[] dest) {
		if (srcIntIterator != null) {
			srcIntIterator.next(dest);
			switch (srcIter.getType()) {
			case BufferedImage.TYPE_INT_ARGB_PRE:
				dstImageType.convertFromARGBPre(dest, getWidth());
				return;
			case BufferedImage.TYPE_INT_ARGB:
				dstImageType.convertFromARGB(dest, getWidth());
				break;
			case BufferedImage.TYPE_INT_RGB:
				dstImageType.convertFromRGB(dest, getWidth());
				break;
			case BufferedImage.TYPE_INT_BGR:
				dstImageType.convertFromBGR(dest, getWidth());
				break;
			default:
				failUnsupportedSourceType();
			}
		} else if (srcByteIterator != null) {
			if (byteScratch == null) {
				byteScratch = new byte[srcByteIterator.getMinimumArrayLength()];
			}
			srcByteIterator.next(byteScratch);
			switch (srcIter.getType()) {
			case BufferedImage.TYPE_3BYTE_BGR:
				dstImageType.convertFromBGR(byteScratch, dest, getWidth());
				break;
			case BufferedImage.TYPE_4BYTE_ABGR:
				dstImageType.convertFromABGR(byteScratch, dest, getWidth());
				break;
			case BufferedImage.TYPE_4BYTE_ABGR_PRE:
				dstImageType.convertFromABGRPre(byteScratch, dest, getWidth());
				break;
			case BufferedImage.TYPE_BYTE_GRAY:
				dstImageType.convertFromGray(byteScratch, dest, getWidth());
				break;
			case BufferedImage.TYPE_BYTE_INDEXED:
				dstImageType.convertFromIndex(byteScratch, dest, getWidth(),
						indexColorModelLUT);
				break;
			case ImageType.TYPE_4BYTE_BGRA:
				dstImageType.convertFromBGRA(byteScratch, dest, getWidth());
				break;
			case ImageType.TYPE_3BYTE_RGB:
				dstImageType.convertFromRGB(byteScratch, dest, getWidth());
				break;
			case ImageType.TYPE_4BYTE_ARGB:
				dstImageType.convertFromARGB(byteScratch, dest, getWidth());
				break;
			case ImageType.TYPE_4BYTE_ARGB_PRE:
				dstImageType.convertFromARGBPre(byteScratch, dest, getWidth());
				break;
			default:
				failUnsupportedSourceType();
			}
		}
	}
}