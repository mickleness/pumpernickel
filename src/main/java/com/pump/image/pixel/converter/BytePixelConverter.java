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

import com.pump.image.pixel.BytePixelIterator;
import com.pump.image.pixel.ImageType;
import com.pump.image.pixel.ImageTypeByte;
import com.pump.image.pixel.PixelIterator;

/**
 * Convert incoming data to a byte pixel format.
 */
public class BytePixelConverter extends PixelConverter<byte[], ImageTypeByte>
		implements BytePixelIterator {

	/**
	 * This is only used if the src data is packaged in ints; otherwise we use
	 * one byte array for both the src pixels and the converted dest pixels.
	 */
	private transient int[] intScratch;

	public BytePixelConverter(PixelIterator<?> iter, ImageTypeByte imageType) {
		super(iter, imageType);
	}

	@Override
	public int getMinimumArrayLength() {
		int minBytes = getWidth() * dstImageType.bytesPerPixel;
		if (srcByteIterator != null) {
			return Math.max(srcByteIterator.getMinimumArrayLength(), minBytes);
		}
		return minBytes;
	}

	@Override
	public void next(byte[] dest) {
		if (srcIntIterator != null) {
			if (intScratch == null) {
				intScratch = new int[srcIntIterator.getMinimumArrayLength()];
			}
			srcIntIterator.next(intScratch);
			switch (srcIter.getType()) {
			case BufferedImage.TYPE_INT_ARGB_PRE:
				dstImageType.convertFromARGBPre(intScratch, dest, getWidth());
				return;
			case BufferedImage.TYPE_INT_ARGB:
				dstImageType.convertFromARGB(intScratch, dest, getWidth());
				break;
			case BufferedImage.TYPE_INT_RGB:
				dstImageType.convertFromRGB(intScratch, dest, getWidth());
				break;
			case BufferedImage.TYPE_INT_BGR:
				dstImageType.convertFromBGR(intScratch, dest, getWidth());
				break;
			default:
				failUnsupportedSourceType();
			}
		} else if (srcByteIterator != null) {
			srcByteIterator.next(dest);
			switch (srcIter.getType()) {
			case BufferedImage.TYPE_3BYTE_BGR:
				dstImageType.convertFromBGR(dest, getWidth());
				break;
			case BufferedImage.TYPE_4BYTE_ABGR:
				dstImageType.convertFromABGR(dest, getWidth());
				break;
			case BufferedImage.TYPE_4BYTE_ABGR_PRE:
				dstImageType.convertFromABGRPre(dest, getWidth());
				break;
			case BufferedImage.TYPE_BYTE_GRAY:
				dstImageType.convertFromGray(dest, getWidth());
				break;
			case BufferedImage.TYPE_BYTE_INDEXED:
				dstImageType.convertFromIndex(dest, getWidth(),
						indexColorModelLUT);
				break;
			case ImageType.TYPE_4BYTE_BGRA:
				dstImageType.convertFromBGRA(dest, getWidth());
				break;
			case ImageType.TYPE_3BYTE_RGB:
				dstImageType.convertFromRGB(dest, getWidth());
				break;
			case ImageType.TYPE_4BYTE_ARGB:
				dstImageType.convertFromARGB(dest, getWidth());
				break;
			case ImageType.TYPE_4BYTE_ARGB_PRE:
				dstImageType.convertFromARGBPre(dest, getWidth());
				break;
			default:
				failUnsupportedSourceType();
			}
		}
	}
}