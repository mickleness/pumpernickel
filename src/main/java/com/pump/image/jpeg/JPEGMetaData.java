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
package com.pump.image.jpeg;

import com.pump.image.QBufferedImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class parses JPEG metadata.
 *
 * @see <a href=
 *      "https://javagraphics.blogspot.com/2010/03/images-reading-jpeg-thumbnails.html">Images:
 *      Reading JPEG Thumbnails</a>
 */
public class JPEGMetaData {

	/**
	 * This property is set on QBufferedImages to identify which JPEG block a thumbnail originated from.
	 */
	public static final String PROPERTY_JPEG_MARKER = "jpeg-marker";

	/**
	 * Read the data from a JPEG input stream and notify the listener as data is read (or considered)
	 *
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static void read(InputStream in, JPEGMetaDataListener listener) throws IOException {
		listener.startFile();
		try (JPEGMarkerInputStream jpegIn = new JPEGMarkerInputStream(in)) {
			String markerCode = jpegIn.getNextMarker();
			JPEGMarker marker = JPEGMarker.getMarkerForByteCode(markerCode);
			if (marker != JPEGMarker.START_OF_IMAGE_MARKER) {
				// did you see "0x4748"? as in the first two letters of
				// "GIF89a"?
				throw new IOException("error: expecting \""
						+ JPEGMarker.START_OF_IMAGE_MARKER.getByteCode()
						+ "\", but found \"" + markerCode + "\"");
			}
			markerCode = jpegIn.getNextMarker();
			while (markerCode != null) {
				marker = JPEGMarker.getMarkerForByteCode(markerCode);
				try {
					if (marker == JPEGMarker.APP0_MARKER) {
						APP0DataReader.read(jpegIn, listener);
					} else if (marker == JPEGMarker.APP1_MARKER) {
						APP1DataReader.read(jpegIn, listener);
					} else if (marker == JPEGMarker.BASELINE_MARKER) {
						byte[] array = new byte[8];
						if (jpegIn.readFully(array, 8) != 8)
							throw new IOException("Error reading start of frame marker");

						int[] array2 = new int[array.length];
						for (int a= 0; a < array2.length; a++) {
							array2[a] = array[a] & 0xff;
						}

						int bitsPerPixel = array[0] & 0xff;
						int height = (array[1] & 0xff) * 256 + (array[2] & 0xff);
						int width = (array[3] & 0xff) * 256 + (array[4] & 0xff);
						int numberOfComponents = array[5] & 0xff;
						listener.imageDescription(bitsPerPixel, width, height, numberOfComponents);
					} else if (marker == JPEGMarker.APP2_MARKER
							|| marker == JPEGMarker.APP13_MARKER) {
						// I don't understand these markers and don't have a
						// clear spec for them, but they sometimes embed a JPEG:
						GenericDataReader.read(jpegIn, markerCode, listener);
					} else if (marker == JPEGMarker.COMMENT_MARKER) {
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
						listener.addComment(
								JPEGMarker.COMMENT_MARKER.getByteCode(),
								buffer.toString());
					}
				} catch (Exception e) {
					listener.processException(e, markerCode);
				}
				if (marker == JPEGMarker.START_OF_SCAN_MARKER) {
					return;
				}
				markerCode = jpegIn.getNextMarker();
			}
		} finally {
			listener.endFile();
		}
	}

	/**
	 * Return the largest thumbnail available in a JPEG file.
	 */
	public static BufferedImage getThumbnail(InputStream in)
			throws IOException {
		JPEGMetaData m = new JPEGMetaData(in);
		if (m.getThumbnailCount() > 0)
			return m.getThumbnail(0);
		return null;
	}

	/**
	 * Return the dimensions of a JPEG image, or null if the dimensions couldn't be identified (which should never
	 * happen for a valid JPEG file)
	 */
	public static Dimension getSize(File file) throws IOException {
		Dimension returnValue = new Dimension(-1, -1);
		try (FileInputStream in = new FileInputStream(file)) {
			read(in, new JPEGMetaDataListener() {

				@Override
				public boolean isThumbnailAccepted(String markerName, int width, int height) {
					return false;
				}

				@Override
				public void addProperty(String markerName, String propertyName, Object value) {
					// intentionally empty
				}

				@Override
				public void addThumbnail(String markerName, BufferedImage bi) {
					// intentionally empty
				}

				@Override
				public void addComment(String markerName, String comment) {
					// intentionally empty
				}

				@Override
				public void startFile() {
					// intentionally empty
				}

				@Override
				public void endFile() {
					// intentionally empty
				}

				@Override
				public void imageDescription(int bitsPerPixel, int width, int height, int numberOfComponents) {
					returnValue.width = width;
					returnValue.height = height;
				}

				@Override
				public void processException(Exception e, String markerCode) {
					// intentionally empty
				}
			});
			if (returnValue.width == -1)
				return null;
			return returnValue;
		}
	}

	private List<QBufferedImage> thumbnailImages = new ArrayList<>();
	private int width, height, bitsPerPixel, numberOfComponents;

	public JPEGMetaData() {}

	public JPEGMetaData(InputStream in) throws IOException {
		read(in, true);
	}

	/**
	 * Add the metadata of a JPEG file to this object.
	 *
	 * @param in the InputStream containing a JPEG file.
	 * @param keepOnlyLargestThumbnail if true then this JPEGMetaData will retain
	 *                                 only the largest thumbnail from the incoming InputStream.
	 *                                 If false then this will retain *all* incoming thumbnails.
	 *                                 (Note the InputStream may contain 0 thumbnails, so regardless
	 *                                 of this boolean this JPEGMetaData may have 0 new thumbnails added
	 *                                 as a result of this method.)
	 * @throws IOException
	 */
	public synchronized void read(InputStream in, boolean keepOnlyLargestThumbnail) throws IOException {
		read(in, new JPEGMetaDataListener() {
			int largestThumbnailWidth = -1;
			int largestThumbnailHeight = -1;

			@Override
			public boolean isThumbnailAccepted(String markerName, int width, int height) {
				if (!keepOnlyLargestThumbnail) {
					return true;
				}

				if (width > largestThumbnailWidth || height > largestThumbnailHeight) {
					return true;
				}

				return false;
			}

			@Override
			public void addProperty(String markerName, String propertyName, Object value) {
				JPEGMetaData.this.addProperty(markerName, propertyName, value);
			}

			@Override
			public void addThumbnail(String markerName, BufferedImage bi) {
				if (largestThumbnailWidth > 0) {
					removeThumbnail(getThumbnailCount());
				}
				JPEGMetaData.this.addThumbnail(markerName, bi);
				largestThumbnailWidth = bi.getWidth();
				largestThumbnailHeight = bi.getHeight();
			}

			@Override
			public void addComment(String markerName, String comment) {
				JPEGMetaData.this.addComment(markerName, comment);
			}

			@Override
			public void startFile() {
				// intentionally empty
			}

			@Override
			public void endFile() {
				// intentionally empty
			}

			@Override
			public void imageDescription(int bitsPerPixel, int width, int height, int numberOfComponents) {
				setImageSize(width, height);
				setBitsPerPixel(bitsPerPixel);
				setNumberOfComponents(numberOfComponents);
			}

			@Override
			public void processException(Exception e, String markerCode) {
				e.printStackTrace();
			}
		});
	}

	public void removeThumbnail(int thumbnailIndex) {
		thumbnailImages.remove(thumbnailIndex);
	}

	public void addThumbnail(String markerName, BufferedImage bi) {
		QBufferedImage qbi = (bi instanceof QBufferedImage) ? (QBufferedImage) bi : new QBufferedImage(bi);
		qbi.setProperty(PROPERTY_JPEG_MARKER, markerName);
		thumbnailImages.add(qbi);
	}

	public QBufferedImage getThumbnail(int thumbnailIndex) {
		return thumbnailImages.get(thumbnailIndex);
	}

	private void addProperty(String markerName, String propertyName, Object value) {
		// TODO: implement if we ever get a use case, add accompanying getter method(s)
	}

	public int getThumbnailCount() {
		return thumbnailImages.size();
	}

	private void addComment(String markerName, String comment) {
		// TODO: implement if we ever get a use case, add accompanying getter method(s)
	}

	public void setImageSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void setNumberOfComponents(int numberOfComponents) {
		this.numberOfComponents = numberOfComponents;
	}

	public void setBitsPerPixel(int bitsPerPixel) {
		this.bitsPerPixel = bitsPerPixel;
	}

	public int getBitsPerPixel() {
		return bitsPerPixel;
	}

	public int getNumberOfComponents() {
		return numberOfComponents;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}