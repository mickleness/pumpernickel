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

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;

public class ColorModelUtils {

	public static final DirectColorModel MODEL_ARGB = (DirectColorModel) ColorModel
			.getRGBdefault();

	public static final DirectColorModel MODEL_RGB = new DirectColorModel(32,
			0xff0000, 0xff00, 0xff, 0);

	/**
	 * This return value for {@link #getBufferedImageType(ColorModel)} indicates
	 * the image type wasn't recognized.
	 */
	public static final int TYPE_UNRECOGNIZED = -329123;

	public static int getBufferedImageType(ColorModel colorModel) {
		if (colorModel instanceof IndexColorModel)
			return BufferedImage.TYPE_BYTE_INDEXED;

		int pxSize = colorModel.getPixelSize();
		int numComponents = colorModel.getNumComponents();
		int transferType = colorModel.getTransferType();

		if (transferType == DataBuffer.TYPE_BYTE) {
			if (numComponents == 3 && pxSize == 24) {
				return BufferedImage.TYPE_3BYTE_BGR;
			} else if (numComponents == 4 && pxSize == 32) {
				if (colorModel.isAlphaPremultiplied()) {
					return BufferedImage.TYPE_4BYTE_ABGR_PRE;
				}
				return BufferedImage.TYPE_4BYTE_ABGR;
			} else if (numComponents == 1 && pxSize == 8) {
				return BufferedImage.TYPE_BYTE_GRAY;
			}
		} else if (transferType == DataBuffer.TYPE_INT
				&& colorModel instanceof DirectColorModel) {
			DirectColorModel dcm = (DirectColorModel) colorModel;
			int redMask = dcm.getRedMask();
			int greenMask = dcm.getGreenMask();
			int blueMask = dcm.getBlueMask();

			if (numComponents == 3 && pxSize == 24 && greenMask == 0xff00) {
				if (blueMask == 0xff0000 && redMask == 0xff)
					return BufferedImage.TYPE_INT_BGR;
				if (redMask == 0xff0000 && blueMask == 0xff)
					return BufferedImage.TYPE_INT_RGB;
			} else if (numComponents == 4 && pxSize == 32) {
				if (redMask == 0xff0000 && greenMask == 0xff00
						&& blueMask == 0xff) {
					if (colorModel.isAlphaPremultiplied()) {
						return BufferedImage.TYPE_INT_ARGB_PRE;
					} else {
						return BufferedImage.TYPE_INT_ARGB;
					}
				}
			}
		}

		return TYPE_UNRECOGNIZED;
	}
}