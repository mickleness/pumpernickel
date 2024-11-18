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
package com.pump.image.jpeg;

import com.pump.image.QBufferedImage;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class parses JPEG metadata.
 *
 * @see <a href=
 *      "https://javagraphics.blogspot.com/2010/03/images-reading-jpeg-thumbnails.html">Images:
 *      Reading JPEG Thumbnails</a>
 */
public class JPEGMetaData {

	/**
	 * This describes which thumbnail(s) (if any) you want to preserve when reading a JPEG file.
	 */
	public enum PreserveThumbnails {
		ALL, ONLY_LARGEST, NONE
	}

	/**
	 * This property is set on QBufferedImages to identify which JPEG block a thumbnail originated from.
	 */
	public static final String PROPERTY_JPEG_MARKER = "jpeg-marker";

	/**
	 * Return the Orientation embedded in a JPEG file.
	 */
	public static Orientation getOrientation(InputStream in) throws IOException {
		AtomicReference<Orientation> returnValue = new AtomicReference<>(Orientation.NONE);
		read(in, new JPEGMetaDataListener() {
			@Override
			public void addProperty(String markerName, String propertyName, Object value) {
				if (value instanceof Orientation orientation)
					returnValue.set(orientation);
				// TODO: instead of returning `void` this method could return an instruction
				// to stop reading, similar to FileVisitor / FileVisitResult
			}
		});
		return returnValue.get();
	}

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
						StringBuilder buffer = new StringBuilder();
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

	public static class Property implements Serializable {

		@Serial
		private static final long serialVersionUID = 1L;

		private String markerName, propertyName;
		private Object value;

		public Property(String markerName, String propertyName, Object value) {
			this.markerName = Objects.requireNonNull(markerName);
			this.propertyName = Objects.requireNonNull(propertyName);
			this.value = Objects.requireNonNull(value);
		}

		@Serial
		private void writeObject(java.io.ObjectOutputStream out)
				throws IOException {
			out.writeInt(0);
			out.writeObject(markerName);
			out.writeObject(propertyName);
			out.writeObject(value);

		}
		@Serial
		private void readObject(java.io.ObjectInputStream in)
				throws IOException, ClassNotFoundException {
			int version = in.readInt();
			if (version == 0) {
				markerName = (String) in.readObject();
				propertyName = (String) in.readObject();
				value = in.readObject();
			} else {
				throw new UnsupportedEncodingException("unsupported internal version: " + version);
			}
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Property p) {
				return Objects.equals(propertyName, p.propertyName) &&
						Objects.equals(value, p.value) &&
						Objects.equals(markerName, p.markerName);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return Objects.hash(markerName, propertyName, value);
		}

		@Override
		public String toString() {
			return "Property[ " + propertyName + " = " + value + "]";
		}

		public String getName() {
			return propertyName;
		}

		public Object getValue() {
			return value;
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

	private final List<QBufferedImage> thumbnailImages = new ArrayList<>();
	private int width, height, bitsPerPixel, numberOfComponents;

	private final java.util.List<Property> properties = new LinkedList<>();

	public JPEGMetaData() {}

	public JPEGMetaData(InputStream in) throws IOException {
		read(in, PreserveThumbnails.ONLY_LARGEST);
	}

	/**
	 * Add the metadata of a JPEG file to this object.
	 *
	 * @param in the InputStream containing a JPEG file.
	 * @param preserveThumbnail this identifies which thumbnail(s) should be preserved (if any).
	 */
	public synchronized void read(InputStream in, PreserveThumbnails preserveThumbnail) throws IOException {
		read(in, new JPEGMetaDataListener() {
			int largestThumbnailWidth = -1;
			int largestThumbnailHeight = -1;

			@Override
			public boolean isThumbnailAccepted(String markerName, int width, int height) {
				if (preserveThumbnail == PreserveThumbnails.ALL) {
					return true;
				} else if (preserveThumbnail == PreserveThumbnails.NONE) {
					return false;
				}

				return width > largestThumbnailWidth || height > largestThumbnailHeight;
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
				JPEGMetaData.this.processException(e, markerCode);
			}
		});
	}

	protected void processException(Exception e, String markerCode) {
		e.printStackTrace();
	}

	public void removeThumbnail(int thumbnailIndex) {
		thumbnailImages.remove(thumbnailIndex);
	}

	public void addThumbnail(String markerName, BufferedImage bi) {
		if (getOrientation() != Orientation.NONE)
			bi = getOrientation().apply(bi);
		QBufferedImage qbi = (bi instanceof QBufferedImage) ? (QBufferedImage) bi : new QBufferedImage(bi);
		qbi.setProperty(PROPERTY_JPEG_MARKER, markerName);
		qbi.setProperty(JPEGPropertyConstants.PROPERTY_ORIENTATION, getOrientation());
		thumbnailImages.add(qbi);
	}

	public QBufferedImage getThumbnail(int thumbnailIndex) {
		return thumbnailImages.get(thumbnailIndex);
	}

	/**
	 * Return the Orientation associated with this JPEGMetaData.
	 */
	public Orientation getOrientation() {
		for (Property p : properties) {
			if (JPEGPropertyConstants.PROPERTY_ORIENTATION.equals(p.propertyName)) {
				return (Orientation) p.value;
			}
		}
		return Orientation.NONE;
	}


	protected void addProperty(String markerName, String propertyName, Object value) {
		properties.add(new Property(markerName, propertyName, value));
	}

	/**
	 * Return all the Properties of this JPEGMetaData.
	 */
	public List<Property> getProperties() {
		return Collections.unmodifiableList(properties);
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

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}