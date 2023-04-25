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
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

import com.pump.awt.Dimension2D;
import com.pump.image.jpeg.JPEGMetaData;
import com.pump.image.jpeg.JPEGMetaDataListener;
import com.pump.image.pixel.Scaling;

/**
 * This ThumbnailGenerator uses the JPEGMetaData class to retrieve thumbnails.
 * (If the meta data doesn't embed a thumbnail: this immediately returns null.)
 * This may scale that thumbnail down if necessary. If the requested thumbnail
 * size is larger than the JPEG's embedded thumbnail: this returns null.
 */
public class JPEGMetaDataThumbnailGenerator implements ThumbnailGenerator {

	@Override
	public BufferedImage createThumbnail(File file, int requestedMaxImageSize)
			throws Exception {
		AtomicReference<BufferedImage> thumbnail = new AtomicReference<>();
		JPEGMetaDataListener listener = new JPEGMetaDataListener() {

			@Override
			public boolean isThumbnailAccepted(String markerName, int width,
					int height) {
				if (requestedMaxImageSize <= 0) {
					BufferedImage bi = thumbnail.get();
					if (bi == null || width > bi.getWidth()
							|| height > bi.getHeight())
						return true;
					// we already have a larger thumbnail
					return false;
				}

				if (width > height) {
					return width >= requestedMaxImageSize;
				}
				return height >= requestedMaxImageSize;
			}

			@Override
			public void addProperty(String markerName, String propertyName,
					Object value) {
				// intentionally empty
			}

			@Override
			public void addThumbnail(String markerName, BufferedImage bi) {
				thumbnail.set(bi);
			}

			@Override
			public void addComment(String markerName, String comment) {
				// intentionally empty
			}

			@Override
			public void endFile() {
				// intentionally empty
			}

			@Override
			public void startFile() {
				// intentionally empty
			}

		};
		JPEGMetaData reader = new JPEGMetaData(listener);
		try (InputStream in = new FileInputStream(file)) {
			reader.read(in);
		}
		BufferedImage bi = thumbnail.get();

		if (requestedMaxImageSize > 0 && bi != null) {
			Dimension biSize = new Dimension(bi.getWidth(), bi.getHeight());
			int largestDimension = Math.max(biSize.width, biSize.height);
			if (largestDimension > requestedMaxImageSize) {
				Dimension maxSize = new Dimension(requestedMaxImageSize,
						requestedMaxImageSize);
				Dimension scaledSize = Dimension2D.scaleProportionally(biSize,
						maxSize);
				bi = Scaling.scale(bi, scaledSize, null, null);
			}
		}

		return bi;
	}

}