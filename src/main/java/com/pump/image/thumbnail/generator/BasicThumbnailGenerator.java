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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import com.pump.awt.Dimension2D;
import com.pump.image.ImageSize;
import com.pump.image.jpeg.JPEGMetaData;
import com.pump.image.pixel.Scaling;
import com.pump.util.JVM;

/**
 * This ThumbnailGenerator multiplexes to several more specific
 * ThumbnailGenerators.
 */
public class BasicThumbnailGenerator implements ThumbnailGenerator {

	public BasicThumbnailGenerator() {}

	@Override
	public BufferedImage createThumbnail(File file, int requestedMaxImageSize)
			throws Exception {

		String filename = file.getName().toLowerCase();

		boolean isJPG = filename.endsWith(".jpg")
				|| filename.endsWith(".jpeg");

		// we're storing the width & height in this object -- even if it's not a JPEG.
		JPEGMetaData imageMetaData;
		if (isJPG) {
			JPEGMetaDataThumbnailGenerator g = new JPEGMetaDataThumbnailGenerator();
			imageMetaData = g.createMetaData(file, requestedMaxImageSize);
		} else {
			imageMetaData = new JPEGMetaData();
			Dimension d = ImageSize.get(file);
			imageMetaData.setImageSize(d.width, d.height);
		}

		if (imageMetaData.getThumbnailCount() > 0) {
			BufferedImage bi = imageMetaData.getThumbnail(0);
			if (requestedMaxImageSize == ThumbnailGenerator.MAX_SIZE_UNDEFINED)
				return bi;

			Dimension d = Dimension2D.scaleProportionally(new Dimension(bi.getWidth(), bi.getHeight()),
					new Dimension(requestedMaxImageSize, requestedMaxImageSize));
			return Scaling.scale(bi, d, null, null);
		}

		BufferedImage bi = null;
		int maxDimension = Math.max(imageMetaData.getWidth(), imageMetaData.getHeight());
		if (requestedMaxImageSize < maxDimension / 2) {
			// ImageIO can outperform Scaling when we have a lot of downsampling:
			try {
				bi = new ImageIOThumbnailGenerator().createThumbnail(file, requestedMaxImageSize);
			} catch(Exception e) {
				// do nothing; keep trying
			}
			if (bi != null)
				return bi;
		}

		try {
			bi = new ScalingThumbnailGenerator().createThumbnail(file, requestedMaxImageSize);
		} catch(Exception e) {
			// do nothing; keep trying
		}
		if (bi != null)
			return bi;

		if (JVM.isMac) {
			try {
				bi = new MacQuickLookThumbnailGenerator().createThumbnail(file, requestedMaxImageSize);
			} catch(Exception e) {
				// do nothing
			}
		}

		return bi;
	}
}