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
import java.io.*;

import com.pump.awt.Dimension2D;
import com.pump.image.jpeg.JPEGMetaData;
import com.pump.image.jpeg.JPEGMetaDataListener;
import com.pump.image.pixel.Scaling;

/**
 * This ThumbnailGenerator uses the JPEGMetaData class to retrieve thumbnails.
 * (If the meta data doesn't embed a thumbnail: this returns null.)
 * This may scale that thumbnail down if necessary. If the requested thumbnail
 * size is larger than the JPEG's embedded thumbnail: this returns null. This always
 * returns the first thumbnail that is large enough. (And if no requested
 * size is defined: all thumbnails are considered "all enough", so this returns
 * the first embedded thumbnail.)
 */
public class JPEGMetaDataThumbnailGenerator implements ThumbnailGenerator {

	@Override
	public BufferedImage createThumbnail(File file, int requestedMaxImageSize)
			throws Exception {
		JPEGMetaData metaData = createMetaData(file, requestedMaxImageSize);
		if (metaData.getThumbnailCount() > 0) {
			BufferedImage bi = metaData.getThumbnail(0);

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
		return null;
	}

	public JPEGMetaData createMetaData(File file, int requestedMaxImageSize) throws IOException {
		JPEGMetaData returnValue = new JPEGMetaData();
		JPEGMetaDataListener listener = new JPEGMetaDataListener() {

			@Override
			public boolean isThumbnailAccepted(String markerName, int width,
					int height) {
				if (returnValue.getThumbnailCount() > 0)
					return false;

				if (requestedMaxImageSize <= 0) {
					return true;
				}

				return width >= requestedMaxImageSize && height >= requestedMaxImageSize;
			}

			@Override
			public void addProperty(String markerName, String propertyName,
					Object value) {
				// intentionally empty
			}

			@Override
			public void addThumbnail(String markerName, BufferedImage bi) {
				returnValue.addThumbnail(markerName, bi);
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
			public void imageDescription(int bitsPerPixel, int width, int height, int numberOfComponents) {
				returnValue.setImageSize(width, height);
			}

			@Override
			public void processException(Exception e, String markerCode) {
				e.printStackTrace();
			}

			@Override
			public void startFile() {
				// intentionally empty
			}

		};

		try (InputStream in = new FileInputStream(file)) {
			JPEGMetaData.read(in, listener);
		}

		return returnValue;
	}

}