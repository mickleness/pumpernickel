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
package com.pump.image.pixel;

/**
 * This iterates over an image, one row of pixels at a time.
 * <P>
 * Note this interface is not complete by itself: the
 * {@link com.pump.image.pixel.BytePixelIterator} and the
 * {@link com.pump.image.pixel.IntPixelIterator} interfaces add the crucial
 * <code>next(array)</code> method (where the <code>array</code> argument is
 * expressed either in bytes or ints).
 * 
 */
public interface PixelIterator<T> {

	/**
	 * The format of this pixel iterator. This should be a "TYPE" constant from BufferedImage or
	 * ImageType, such as {@link java.awt.image.BufferedImage#TYPE_INT_ARGB} or {@link ImageType#TYPE_4BYTE_BGRA}.
	 * You can call {@link ImageType#get(int)} to identify more information about this image type in
	 * many cases.
	 */
	int getType();

	/**
	 * Returns true if this image is guaranteed to be an opaque image. This has
	 * to do with the pixel type of this image and not its content. (For
	 * example: an image with an alpha channel that happens to be opaque may
	 * still return false.)
	 */
	default boolean isOpaque() {
		return ImageType.get(getType()).isOpaque();
	}

	/**
	 * The number of array elements used to store 1 pixel.
	 * <P>
	 * So in TYPE_4BYTE_ARGB this will be 4, but in TYPE_INT_ARGB this will be
	 * 1.
	 *
	 * @return the number of array elements used to store 1 pixel.
	 */
	default int getPixelSize() {
		return ImageType.get(getType()).getSampleCount();
	}

	/**
	 * Whether this iterator is finished or not
	 * 
	 * @return <code>true</code> if there is no more pixel data to read.
	 */
	boolean isDone();

	/**
	 * Indicates whether this iterator returns rows in a top-to-bottom order or
	 * a bottom-to-top order.
	 */
	boolean isTopDown();

	/**
	 * Returns the width of this image.
	 * 
	 * @return the width of this image.
	 */
	int getWidth();

	/**
	 * Returns the height of this image.
	 * 
	 * @return the height of this image.
	 */
	int getHeight();

	/**
	 * The minimum length an array should be that is used to retrieve a row of
	 * pixel data.
	 * <P>
	 * When you call <code>next(array)</code> on this iterator, the array's
	 * length should at least be <code>getMinimumArrayLength()</code>.
	 * <P>
	 * You should not assume you know what this value is. For example: if your
	 * original data source is a 3-byte BGR image but it is being converted to a
	 * 1-byte grayscale image, then it is wrong to assume this method will
	 * return <code>getWidth()</code>. It may (or may not) return
	 * <code>3 * getWidth()</code>, because the converter layer may be passing
	 * the same array to the source.
	 * 
	 * @return The minimum length an array should be that is used to retrieve a
	 *         row of pixel data.
	 */
	int getMinimumArrayLength();

	/**
	 * Skips a row of pixel data.
	 */
	void skip();

	/**
	 * Reads a row of pixel data.
	 * 
	 * @param dest
	 *            the array to store the pixels in
	 */
	void next(T dest);
}