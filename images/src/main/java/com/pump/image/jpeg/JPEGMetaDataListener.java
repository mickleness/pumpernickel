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

import java.awt.image.BufferedImage;

/**
 * This listens to new JPEG metadata as a JPEGMetaData object parses it.
 */
public interface JPEGMetaDataListener {
	/**
	 * If this returns true then the reader will attempt to parse an incoming
	 * BufferedImage and pass it to
	 * {@link #addThumbnail(String, BufferedImage)}.
	 * <p>
	 * If this method returns false then the parser will skip this thumbnail.
	 * 
	 * @param markerName
	 *            the type of block that is currently being parsed.
	 * @param width
	 *            the width of the new thumbnail.
	 * @param height
	 *            the height of the new thumbnail.
	 */
	default boolean isThumbnailAccepted(String markerName, int width, int height) {
		return false;
	}

	/**
	 * This is called when the reader identifies a new property
	 * 
	 * @param markerName
	 *            the type of block that is currently being parsed.
	 * @param propertyName
	 *            the name of the property
	 * @param value
	 *            the property value
	 */
	default void addProperty(String markerName, String propertyName, Object value) {}

	/**
	 * This is called after {@link #isThumbnailAccepted(String, int, int)}
	 * returns true.
	 * 
	 * @param markerName
	 *            the type of block that is currently being parsed.
	 * @param bi
	 *            the new thumbnail that was just constructed.
	 */
	default void addThumbnail(String markerName, BufferedImage bi) {}

	/**
	 * This is called when a new comment is parsed.
	 * 
	 * @param markerName
	 *            the type of block that is currently being parsed.
	 * @param comment
	 *            the comment that was just parsed.
	 */
	default void addComment(String markerName, String comment) {}

	/**
	 * This is called to indicate a new InputStream is being read. (The default
	 * implementation is single-threaded, so you should see a simple/clear path
	 * of execution from this start method to the close method.)
	 */
	default void startFile() {}

	/**
	 * This is called to indicate an InputStream is finished.
	 */
	default void endFile() {}

	/**
	 * This is called while reading the "baseline" or "start of frame" block.
	 *
	 * @param bitsPerPixel the bits per pixel (usually 8)
	 * @param width the width of the image
	 * @param height the height of the image
	 * @param numberOfComponents the number of components (1 or 3)
	 */
	default void imageDescription(int bitsPerPixel, int width, int height, int numberOfComponents) {}

	/**
	 * This is called when an exception occurs reading from a JPEG input stream.
	 */
	default void processException(Exception e, String markerCode) {}
}