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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class parses JPEG metadata to retrieve properties, comments and
 * thumbnails.
 * 
 * @see com.pump.showcase.JPEGMetaDataDemo
 * @see <a href=
 *      "https://javagraphics.blogspot.com/2010/03/images-reading-jpeg-thumbnails.html">Images:
 *      Reading JPEG Thumbnails</a>
 */
public class JPEGMetaData {

	/**
	 * Extract a thumbnail from a JPEG file, if possible. This may return null
	 * if no thumbnail can be found.
	 * 
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static BufferedImage getThumbnail(File file) throws IOException {
		try (InputStream in = new FileInputStream(file)) {
			return getThumbnail(in);
		}
	}

	/**
	 * Extract a thumbnail from a JPEG image, if possible. This may return null
	 * if no thumbnail can be found.
	 * 
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static BufferedImage getThumbnail(InputStream in)
			throws IOException {
		AtomicReference<BufferedImage> thumbnail = new AtomicReference<>();
		JPEGMetaDataListener listener = new JPEGMetaDataListener() {

			@Override
			public boolean isThumbnailAccepted(String markerName, int width,
					int height) {
				BufferedImage bi = thumbnail.get();
				if (bi == null || width > bi.getWidth()
						|| height > bi.getHeight())
					return true;
				return false;
			}

			@Override
			public void addThumbnail(String markerName, BufferedImage bi) {
				thumbnail.set(bi);
			}

			@Override
			public void addProperty(String markerName, String propertyName,
					Object value) {
				// intentionally empty
			}

			@Override
			public void addComment(String markerName, String comment) {
				// intentionally empty
			}
		};
		JPEGMetaData reader = new JPEGMetaData(listener);
		reader.read(in);
		return thumbnail.get();
	}

	/**
	 * Extract a thumbnail from a JPEG image, if possible. This may return null
	 * if no thumbnail can be found.
	 * 
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static BufferedImage getThumbnail(URL url) throws IOException {
		try (InputStream in = url.openStream()) {
			return getThumbnail(in);
		}
	}

	JPEGMetaDataListener listener;

	public JPEGMetaData(JPEGMetaDataListener listener) {
		Objects.requireNonNull(listener);
		this.listener = listener;
	}

	@SuppressWarnings("resource")
	public void read(InputStream in) throws IOException {
		JPEGMarkerInputStream jpegIn = new JPEGMarkerInputStream(in);
		String marker = jpegIn.getNextMarker();
		if (!JPEGMarkerInputStream.START_OF_IMAGE_MARKER.equals(marker)) {
			// did you see "0x4748"? as in the first two letters of "GIF89a"?
			throw new IOException("error: expecting \""
					+ JPEGMarkerInputStream.START_OF_IMAGE_MARKER
					+ "\", but found \"" + marker + "\"");
		}
		marker = jpegIn.getNextMarker();
		while (marker != null) {
			try {
				if (JPEGMarkerInputStream.APP0_MARKER.equals(marker)) {
					APP0DataReader.read(jpegIn, listener);
				} else if (JPEGMarkerInputStream.APP1_MARKER.equals(marker)) {
					APP1DataReader.read(jpegIn, listener);
				} else if (JPEGMarkerInputStream.APP2_MARKER.equals(marker)
						|| JPEGMarkerInputStream.APP13_MARKER.equals(marker)) {
					// I don't understand these markers and don't have a clear
					// spec for them, but they sometimes embed a JPEG:
					GenericDataReader.read(jpegIn, marker, listener);
				} else if (JPEGMarkerInputStream.COMMENT_MARKER
						.equals(marker)) {
					byte[] b = new byte[64];
					StringBuffer buffer = new StringBuffer();
					int t = jpegIn.read(b);
					while (t > 0) {
						for (int a = 0; a < t; a++) {
							char c = (char) (b[a] & 0xff);
							buffer.append(c);
						}
						t = jpegIn.read(b);
					}
					listener.addComment(JPEGMarkerInputStream.COMMENT_MARKER,
							buffer.toString());
				}
			} catch (Exception e) {
				processException(e, marker);
			}
			if (JPEGMarkerInputStream.START_OF_SCAN_MARKER.equals(marker)) {
				return;
			}
			marker = jpegIn.getNextMarker();
		}
	}

	/**
	 * This is called when an exception occurs trying to parse an block of data.
	 * The default implementation is simply to call
	 * <code>e.printStackTrace()</code>, but subclasses can override this as
	 * needed.
	 * 
	 * @param e
	 *            the exception that occurred.
	 * @param marker
	 *            the type of marker we were trying to process.
	 */
	protected void processException(Exception e, String marker) {
		e.printStackTrace();
	}
}