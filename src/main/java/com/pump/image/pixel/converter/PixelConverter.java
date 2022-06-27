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
package com.pump.image.pixel.converter;

import java.util.Objects;

import com.pump.image.pixel.BufferedImageIterator;
import com.pump.image.pixel.BytePixelIterator;
import com.pump.image.pixel.ImageType;
import com.pump.image.pixel.IndexedBytePixelIterator;
import com.pump.image.pixel.IntPixelIterator;
import com.pump.image.pixel.PixelIterator;

/**
 * This is an abstract parent class for converter iterators.
 */
public abstract class PixelConverter<T, I extends ImageType>
		implements PixelIterator<T> {
	/**
	 * The source pixel data we're iterating over.
	 */
	protected final PixelIterator<?> srcIter;

	/**
	 * This will be null or srcIter.
	 */
	protected final BytePixelIterator srcByteIterator;

	/**
	 * This will be null or srcIter.
	 */
	protected final IntPixelIterator srcIntIterator;

	protected final I dstImageType;

	/**
	 * This will be non-null if the source pixels use an IndexColorModel.
	 */
	protected final IndexColorModelLUT indexColorModelLUT;

	/**
	 * 
	 * @param i
	 *            the incoming source data we iterate over
	 * @param dstImageType
	 *            the type of image data this converter ultimately produces.
	 */
	public PixelConverter(PixelIterator<?> srcIter, I dstImageType) {
		this.srcIter = srcIter;
		this.dstImageType = Objects.requireNonNull(dstImageType);
		if (srcIter instanceof IndexedBytePixelIterator) {
			IndexedBytePixelIterator ibpi = (IndexedBytePixelIterator) srcIter;
			srcByteIterator = ibpi;
			indexColorModelLUT = new IndexColorModelLUT(
					ibpi.getIndexColorModel());
			srcIntIterator = null;
		} else if (srcIter instanceof BytePixelIterator) {
			srcByteIterator = (BytePixelIterator) srcIter;
			indexColorModelLUT = null;
			srcIntIterator = null;
		} else if (srcIter instanceof IntPixelIterator) {
			srcIntIterator = (IntPixelIterator) srcIter;
			srcByteIterator = null;
			indexColorModelLUT = null;
		} else {
			throw new IllegalArgumentException(
					"the converted iterator must be a BytePixelIterator or an IntPixelIterator (not a "
							+ srcIter.getClass().getName() + ")");
		}
	}

	@Override
	public int getType() {
		return dstImageType.code;
	}

	@Override
	public int getHeight() {
		return srcIter.getHeight();
	}

	@Override
	public int getWidth() {
		return srcIter.getWidth();
	}

	@Override
	public boolean isDone() {
		return srcIter.isDone();
	}

	@Override
	public boolean isTopDown() {
		return srcIter.isTopDown();
	}

	@Override
	public void skip() {
		srcIter.skip();
	}

	/**
	 * This throws an exception describing the current source iterator.
	 */
	protected void failUnsupportedSourceType() {
		throw new RuntimeException("Unrecognized source type. "
				+ BufferedImageIterator.getTypeName(srcIter.getType()));
	}
}