package com.pump.image.jpeg;

import java.awt.image.BufferedImage;

/**
 * This listens to new JPEG meta data as a JPEGMetaData object parses it.
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
	 * @return
	 */
	boolean isThumbnailAccepted(String markerName, int width, int height);

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
	void addProperty(String markerName, String propertyName, Object value);

	/**
	 * This is called after {@link #isThumbnailAccepted(String, int, int)}
	 * returns true.
	 * 
	 * @param markerName
	 *            the type of block that is currently being parsed.
	 * @param bi
	 *            the new thumbnail that was just constructed.
	 */
	void addThumbnail(String markerName, BufferedImage bi);

	/**
	 * This is called when a new comment is parsed.
	 * 
	 * @param markerName
	 *            the type of block that is currently being parsed.
	 * @param comment
	 *            the comment that was just parsed.
	 */
	void addComment(String markerName, String comment);

}
