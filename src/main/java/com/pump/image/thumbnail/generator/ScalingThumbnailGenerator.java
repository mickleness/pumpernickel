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
package com.pump.image.thumbnail.generator;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.BiFunction;

import com.pump.awt.Dimension2D;
import com.pump.image.pixel.Scaling;

/**
 * This ThumbnailGenerator reads the entire image and using the
 * {@link com.pump.image.pixel.Scaling} class to resize the image to create the
 * appropriate thumbnail.
 *
 */
public class ScalingThumbnailGenerator implements ThumbnailGenerator {

	@Override
	public BufferedImage createThumbnail(File file, int requestedMaxImageSize)
			throws Exception {
		if (requestedMaxImageSize <= 0)
			requestedMaxImageSize = MAX_SIZE_DEFAULT;

		// this shouldn't happen. Who messed up the default size?
		if (requestedMaxImageSize <= 0)
			requestedMaxImageSize = 100;

		Dimension maxSize = new Dimension(requestedMaxImageSize, requestedMaxImageSize);
		BiFunction<Dimension, Boolean, Dimension> sizeFunction = new BiFunction<>() {
			@Override
			public Dimension apply(Dimension srcImageSize, Boolean isEmbeddedThumbnail) {
				Dimension scaledImageSize = Dimension2D.scaleProportionally(srcImageSize, maxSize, true);
				if (scaledImageSize == null) {
					return srcImageSize;
				}
				return scaledImageSize;
			}
		};
		return Scaling.scale(file, sizeFunction, null, null);
	}

}