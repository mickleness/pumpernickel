/**
 * This software is released as part of the Pumpernickel project.
 * <p>
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * <p>
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.image.shadow;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * This interface can create and render a shadow of an image.
 */
public interface ShadowRenderer {

	/**
	 * Paint an image with a shadow.
	 * <p>
	 * This will create and paint a shadow layer first, and then paint the image
	 * layer second.
	 * 
	 * @param g
	 *            the Graphics2D to paint to.
	 * @param img
	 *            the image to paint with a shadow
	 * @param x
	 *            the x offset to paint the image at
	 * @param y
	 *            the y offset to paint the image at
	 * @param attr
	 *            the attributes used to render and position the shadow
	 */
	default void paint(Graphics2D g, BufferedImage img, int x, int y,
			ShadowAttributes attr) {
		GaussianKernel k = getKernel(attr.getShadowKernelRadius());
		BufferedImage shadow = createShadow(img, attr.getShadowKernelRadius(),
				attr.getShadowColor());

		AffineTransform tx = AffineTransform.getTranslateInstance(
				x - k.getKernelRadius() + attr.getShadowXOffset(),
				y - k.getKernelRadius() + attr.getShadowYOffset());
		g.drawImage(shadow, tx, null);
		g.drawImage(img, x, y, null);
	}

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
	 * @param kernelRadius
	 *            the kernel radius. The actual kernel should be [2 * r + 1]
	 *            elements long.
	 * @param shadowColor
	 *            the shadow color, including the alpha component.
	 */
	default BufferedImage createShadow(BufferedImage srcImage,
			float kernelRadius, Color shadowColor) {
		int k = getKernel(kernelRadius).getKernelRadius();

		int destW = srcImage.getWidth() + 2 * k;
		int destH = srcImage.getHeight() + 2 * k;
		ARGBPixels destPixels = new ARGBPixels(destW, destH);
		ARGBPixels srcPixels = new ARGBPixels(srcImage);
		createShadow(srcPixels, destPixels, kernelRadius, shadowColor);

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
	 * @param kernelRadius
	 *            the kernel radius. The actual kernel should be [2 * r + 1]
	 *            elements long.
	 * @param shadowColor
	 *            the shadow color, including the alpha component.
	 * @return a set of ARGB pixels representing the shadow.
	 */
	ARGBPixels createShadow(ARGBPixels srcImage, ARGBPixels destImage,
			float kernelRadius, Color shadowColor);

	/**
	 * Return the GaussianKernel this renderer will apply based on a kernel
	 * radius.
	 * 
	 * @param kernelRadius
	 *            the kernel radius. The actual kernel should be [2 * r + 1]
	 *            elements long.
	 */
	GaussianKernel getKernel(float kernelRadius);
}