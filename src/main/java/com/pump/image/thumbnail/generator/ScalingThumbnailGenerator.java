package com.pump.image.thumbnail.generator;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

import com.pump.awt.Dimension2D;
import com.pump.image.ImageLoader;
import com.pump.image.ImageSize;
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

		Dimension imageSize = ImageSize.get(file);
		Dimension maxSize = new Dimension(requestedMaxImageSize,
				requestedMaxImageSize);

		Dimension scaledImageSize = Dimension2D.scaleProportionally(imageSize,
				maxSize, true);
		if (scaledImageSize == null) {
			return ImageLoader.createImage(file);
		}

		// TODO: currently we're converting to ARGB. It'd be nice to include
		// an option to leave image type blank/default. For ex:
		// if an image would normally be BGR, then let the scaled image also
		// be BGR.

		return Scaling.scale(file, BufferedImage.TYPE_INT_ARGB,
				scaledImageSize);
	}

}
