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

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * This creates image thumbnails for a file.
 */
public interface ThumbnailGenerator {

	/**
	 * If {@link #createThumbnail(File, int)} is invoked with a non-positive
	 * value then this is the value that subclasses may default to if they don't
	 * have their own preference.
	 */
	public static int MAX_SIZE_DEFAULT = 100;

	/**
	 * This value (-1) indicates the caller prefers any thumbnail that is
	 * fast/easy to generate regardless of the size. For example: if a file
	 * embeds a thumbnail in its metadata then that thumbnail should be
	 * returned.
	 */
	public static final int MAX_SIZE_UNDEFINED = -1;

	/**
	 * Create a file's image preview, or return null if this object can't read
	 * the file provided.
	 * 
	 * @param file
	 *            the file to create a preview for.
	 * @param requestedMaxImageSize
	 *            the optional maximum width or height of this thumbnail. If
	 *            this is not a positive integer then this argument is ignored.
	 *            <p>
	 *            This only applies when an image needs to scale down. If the
	 *            image is 16x16px and you request a maximum image size of
	 *            128px: then this method should return the 16x16px image.
	 */
	public BufferedImage createThumbnail(File file, int requestedMaxImageSize)
			throws Exception;
}