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
	public BufferedImage createShadow(BufferedImage srcImage,
			ShadowAttributes attr);
}
