package com.pump.image.shadow;

import java.awt.image.BufferedImage;

public interface ShadowRenderer {

	/**
	 * Create a black translucent shadow image based on a source image.
	 * <p>
	 * The ShadowAttributes include the kernel size of the blur to apply. The
	 * resulting image should effectively be padded by a border of
	 * kernel-radius-many pixels on all sides.
	 * <p>
	 * The source is assumed to be INT_ARGB, and the output will be INT_ARGB.
	 * 
	 * @param srcImage
	 *            the image to create a shadow for.
	 * @param attr
	 *            a description of the shadow attributes.
	 * @return a new BufferedImage that contains the blurred shadow image.
	 */
	public default BufferedImage createShadow(BufferedImage srcImage,
			ShadowAttributes attr) {
		int k = getKernel(attr).getKernelRadius();

		int destW = srcImage.getWidth() + 2 * k;
		int destH = srcImage.getHeight() + 2 * k;
		ARGBPixels destPixels = new ARGBPixels(destW, destH);
		ARGBPixels srcPixels = new ARGBPixels(srcImage);
		createShadow(srcPixels, destPixels, attr);

		return destPixels.createBufferedImage();
	}

	/**
	 * Write a black translucent shadow based on the source image.
	 * 
	 * @param srcImage
	 *            the image to create a shadow for.
	 * @param destImage
	 *            an optional destination to write to. If null then a new
	 *            destination is created.
	 * @param attr
	 *            a description of the shadow attributes.
	 * @return a set of ARGB pixels representing the shadow.
	 */
	public ARGBPixels createShadow(ARGBPixels srcImage, ARGBPixels destImage,
			ShadowAttributes attr);

	/**
	 * Return the GaussianKernel this renderer will apply based on a set of
	 * attributes.
	 */
	public GaussianKernel getKernel(ShadowAttributes attr);
}
