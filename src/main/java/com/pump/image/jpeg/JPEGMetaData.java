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
import java.util.HashMap;
import java.util.Map;

/**
 * This class parses JPEG metadata to retrieve properties or thumbnails.
 * 
 * @see com.pump.showcase.JPEGMetaDataDemo
 * @see <a
 *      href="https://javagraphics.blogspot.com/2010/03/images-reading-jpeg-thumbnails.html">Images:
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
		JPEGMetaData data = new JPEGMetaData(file, true);
		return data.thumbnail;
	}

	/**
	 * Extract a thumbnail from a JPEG image, if possible. This may return null
	 * if no thumbnail can be found.
	 * 
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static BufferedImage getThumbnail(InputStream in) throws IOException {
		JPEGMetaData data = new JPEGMetaData(in, true);
		return data.thumbnail;
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
			JPEGMetaData data = new JPEGMetaData(in, true);
			return data.thumbnail;
		}
	}

	Map<String, Object> properties = new HashMap<String, Object>();
	BufferedImage thumbnail;
	String[] comments = new String[0];

	/**
	 * Creates a JPEGMetaData object.
	 * 
	 * @param file
	 *            the JPEG image.
	 * @param fetchThumbnail
	 *            whether the thumbnail should be retrieved. If this is false
	 *            then <code>getThumbnail()</code> will return false, but
	 *            properties will still be loaded (if possible).
	 * @throws IOException
	 */
	public JPEGMetaData(File file, boolean fetchThumbnail) throws IOException {
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			init(in, fetchThumbnail);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Creates a JPEGMetaData object.
	 * 
	 * @param in
	 *            the JPEG image.
	 * @param fetchThumbnail
	 *            whether the thumbnail should be retrieved. If this is false
	 *            then <code>getThumbnail()</code> will return false, but
	 *            properties will still be loaded (if possible).
	 * @throws IOException
	 */
	public JPEGMetaData(InputStream in, boolean fetchThumbnail)
			throws IOException {
		init(in, fetchThumbnail);
	}

	private void init(InputStream in, boolean fetchThumbnail)
			throws IOException {
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
					APP0Data data = new APP0Data(jpegIn, fetchThumbnail);
					processAPP0(data);
				} else if (JPEGMarkerInputStream.APP1_MARKER.equals(marker)) {
					APP1Data data = new APP1Data(jpegIn, fetchThumbnail);
					processAPP1(data);
				} else if (JPEGMarkerInputStream.APP2_MARKER.equals(marker)) {
					APP2Data data = new APP2Data(jpegIn, fetchThumbnail);
					processAPP2(data);
				} else if (JPEGMarkerInputStream.APP13_MARKER.equals(marker)) {
					APP13Data data = new APP13Data(jpegIn, fetchThumbnail);
					processAPP13(data);
				} else if (JPEGMarkerInputStream.COMMENT_MARKER.equals(marker)) {
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
					processComment(buffer.toString());
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

	/**
	 * This is called when an <code>APP0Data</code> object has been parsed.
	 * <P>
	 * The default implementation is simply to retrieve the thumbnail, but
	 * subclasses can override this to do extra work.
	 * 
	 * @param data
	 *            the newly parsed data.
	 */
	protected void processAPP0(APP0Data data) {
		considerAddingThumbnail(data.getThumbnail());
	}

	/**
	 * This is called when an <code>APP13Data</code> object has been parsed.
	 * <P>
	 * The default implementation is simply to retrieve the thumbnail, but
	 * subclasses can override this to do extra work.
	 * 
	 * @param data
	 *            the newly parsed data.
	 */
	protected void processAPP13(APP13Data data) {
		considerAddingThumbnail(data.getThumbnail());
	}

	/**
	 * This is called when an <code>APP2Data</code> object has been parsed.
	 * <P>
	 * The default implementation is simply to retrieve the thumbnail, but
	 * subclasses can override this to do extra work.
	 * 
	 * @param data
	 *            the newly parsed data.
	 */
	protected void processAPP2(APP2Data data) {
		considerAddingThumbnail(data.getThumbnail());
	}

	/**
	 * This is called when an <code>APP1Data</code> object has been parsed.
	 * <P>
	 * The default implementation is to retrieve the thumbnail and add the
	 * properties, but subclasses can override this to do extra work.
	 * 
	 * @param data
	 *            the newly parsed data.
	 */
	protected void processAPP1(APP1Data data) {
		properties.putAll(data.getProperties());
		considerAddingThumbnail(data.getThumbnail());
	}

	/**
	 * This is called when a comment marker has been parsed.
	 * <P>
	 * The default implementation is to add this comment, but subclasses can
	 * override this to do extra work.
	 * 
	 * @param comment
	 *            the newly parsed comment.
	 */
	protected void processComment(String comment) {
		String[] newComments = new String[comments.length + 1];
		System.arraycopy(comments, 0, newComments, 0, comments.length);
		newComments[newComments.length - 1] = comment;
		comments = newComments;
	}

	private void considerAddingThumbnail(BufferedImage bi) {
		if (bi == null)
			return;

		if (thumbnail == null) {
			thumbnail = bi;
			return;
		}
		if (bi.getWidth() > thumbnail.getWidth()
				&& bi.getHeight() > thumbnail.getHeight()) {
			thumbnail = bi;
			return;
		}
	}

	/**
	 * Returns the map of properties found in this JPEG. This will not be null,
	 * but it may be empty.
	 * <p>
	 * Note this does not return a clone of this map, so if you change the
	 * contents of this map then this metadata object is permanently altered.
	 */
	public Map<String, Object> getProperties() {
		return properties;
	}

	/**
	 * Return the comments found in this JPEG, if any.
	 */
	public String[] getComments() {
		String[] copy = new String[comments.length];
		System.arraycopy(comments, 0, copy, 0, comments.length);
		return copy;
	}

	/**
	 * Returns the thumbnail if one exists. This may return null.
	 */
	public BufferedImage getThumbnail() {
		return thumbnail;
	}
}