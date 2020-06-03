package com.pump.image.shadow;

import java.awt.image.BufferedImage;

public interface ShadowRenderer {

	/**
	 * Create a black translucent shadow image based on a source image.
	 * <p>
	 * The ShadowAttributes include the kernel size of the blur to apply. The
	 * resulting image should effectively be padded by a border of kernel-many
	 * pixels on all sides.
	 * <p>
	 * The source and output image are assumed to be INT_ARGB.
	 * 
	 * @param srcImage
	 *            the image to blur
	 * @param attr
	 *            a description of the shadow attributes.
	 * @return a new BufferedImage that contains the blurred shadow image.
	 */
	public default BufferedImage createShadow(BufferedImage srcImage,
			ShadowAttributes attr) {
		int k = attr.getShadowKernelSize();

		int destW = srcImage.getWidth() + 2 * k;
		int destH = srcImage.getHeight() + 2 * k;
		ARGBPixels destPixels = new ARGBPixels(destW, destH);
		ARGBPixels srcPixels = new ARGBPixels(srcImage);
		createShadow(srcPixels, destPixels, attr);

		return destPixels.createBufferedImage();
	}

	public ARGBPixels createShadow(ARGBPixels srcImage, ARGBPixels destImage,
			ShadowAttributes attr);
}
