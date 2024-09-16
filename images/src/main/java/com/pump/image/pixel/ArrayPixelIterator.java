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
package com.pump.image.pixel;

import java.lang.reflect.Array;

/**
 * This iterates over an array of pixels.
 */
public class ArrayPixelIterator<T> implements PixelIterator<T> {

	T srcData;
	int dataIndex, rowCtr;
	final int width, height, scanSize;
	final ImageType imageType;

	private boolean isClosed = false;

	/**
	 * Create a ArrayPixelIterator.
	 * 
	 * @param srcData
	 *            the srcData to iterate over.
	 * @param width
	 *            the width of each row.
	 * @param height
	 *            the number of rows to iterate over.
	 * @param offset
	 *            the offset in the original source data to start reading pixel data.
	 * @param scanSize
	 *            the distance from one row of pixels to the next in the pixels
	 *            array. This must be the width or larger.
	 * @param pixelType
	 *            a constant like BufferedImage.TYPE_INT_ARGB
	 */
	public ArrayPixelIterator(T srcData, int width, int height,
			int offset, int scanSize, int pixelType) {
		if (width <= 0)
			throw new IllegalArgumentException(
					"width must be greater than zero. (width = " + width + ")");
		if (height <= 0)
			throw new IllegalArgumentException(
					"height must be greater than zero. (height = " + height
							+ ")");

		imageType = ImageType.get(pixelType);

		if (scanSize < width * imageType.getSampleCount())
			throw new IllegalArgumentException(
					"scanSize should be equal to or greater than the width (scanSize = "
							+ scanSize + ", width = " + width + ", pixelSize = " + imageType.getSampleCount() + ")");

		int lastIndex = offset + scanSize * height;
		if (lastIndex > Array.getLength(srcData))
			throw new IllegalArgumentException("this array is too short");

		this.srcData = srcData;
		this.dataIndex = offset;
		this.width = width;
		this.height = height;
		this.scanSize = scanSize;
		rowCtr = 0;
	}

	@Override
	public int getType() {
		return imageType.getCode();
	}

	@Override
	public int getPixelSize() {
		return imageType.getSampleCount();
	}

	@Override
	public boolean isDone() {
		return rowCtr >= height;
	}

	@Override
	public boolean isTopDown() {
		return true;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void skip() {
		if (isClosed)
			throw new ClosedException();

		dataIndex += scanSize;

		if (isDone())
			close();
	}

	@Override
	public void next(T dest, int offset) {
		if (isClosed)
			throw new ClosedException();

		System.arraycopy(srcData, dataIndex, dest, offset, width * imageType.getSampleCount());
		dataIndex += scanSize;
		rowCtr++;

		if (isDone())
			close();
	}

	/**
	 * The ArrayPixelIterator doesn't have any resources to close, but subsequent calls
	 * to some methods may throw a ClosedException.
	 */
	@Override
	public void close() {
		isClosed = true;
	}
}