/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://raw.githubusercontent.com/mickleness/pumpernickel/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.image.jpeg;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

/**
 * Use this data block when we expect a small thumbnail JPEG is somewhere inside
 * a larger block of data. (But we don't have specs to properly parse the data,
 * so we're just looking for starting/ending markers.)
 */
class GenericDataReader {

	final static byte[] start = new byte[] { (byte) 0xff, (byte) 0xd8,
			(byte) 0xff };
	final static byte[] end = new byte[] { (byte) 0xff, (byte) 0xd9 };

	public static void read(JPEGMarkerInputStream in, String markerName,
			JPEGMetaDataListener listener) throws IOException {
		byte[] dest = new byte[in.remainingMarkerLength];
		if (in.readFully(dest, dest.length) != dest.length)
			throw new IOException();
		int startIndex = indexOf(dest, start);
		if (startIndex != -1) {
			int endIndex = lastIndexOf(dest, end) + end.length;
			if (endIndex != -1) {
				ByteArrayInputStream imageData = new ByteArrayInputStream(dest,
						startIndex, endIndex - startIndex);
				readThumbnail(markerName, imageData, listener);
			}
		}
	}

	protected static final int indexOf(byte[] searchable, byte[] phrase) {
		boolean match = true;
		for (int a = 0; a < searchable.length - phrase.length; a++) {
			match = true;
			for (int b = 0; b < phrase.length && match; b++) {
				if (searchable[a + b] != phrase[b])
					match = false;
			}
			if (match)
				return a;
		}
		return -1;
	}

	protected static final int lastIndexOf(byte[] searchable, byte[] phrase) {
		boolean match = true;
		for (int a = searchable.length - phrase.length; a >= 0; a--) {
			match = true;
			for (int b = 0; b < phrase.length && match; b++) {
				if (searchable[a + b] != phrase[b])
					match = false;
			}
			if (match)
				return a;
		}
		return -1;
	}

	/**
	 * This uses ImageIO to identify the width/height of the image in the
	 * InputStream provided, then verify that the listener wants to receive the
	 * image, and then (if needed) parse the image and pass to the listener.
	 */
	static void readThumbnail(String markerName, InputStream in,
			JPEGMetaDataListener listener) throws IOException {

		Iterator<ImageReader> iterator = ImageIO
				.getImageReadersBySuffix("jpeg");
		while (iterator.hasNext()) {
			ImageReader reader = iterator.next();
			reader.setInput(ImageIO.createImageInputStream(in));
			int height = reader.getHeight(0);
			int width = reader.getWidth(0);
			if (listener.isThumbnailAccepted(markerName, width, height)) {
				BufferedImage thumbnail = reader.read(0);
				listener.addThumbnail(JPEGMarker.APP1_MARKER.getByteCode(),
						thumbnail);
			}
		}
	}
}