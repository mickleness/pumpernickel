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
package com.pump.image.pixel.converter;

import java.awt.image.IndexColorModel;

import com.pump.image.pixel.BytePixelIterator;
import com.pump.image.pixel.IndexedBytePixelIterator;
import com.pump.image.pixel.IntPixelIterator;
import com.pump.image.pixel.PixelIterator;

/**
 * This is the abstract base class for most of the converters in this package.
 * 
 */
public abstract class PixelConverter<T> implements PixelIterator<T> {
	private final PixelIterator<?> i;
	protected final BytePixelIterator byteIterator;
	protected final IntPixelIterator intIterator;
	final int originalType;
	final int width;
	protected final IndexColorModel indexModel;

	public PixelConverter(PixelIterator<?> i) {
		this.i = i;
		originalType = i.getType();
		width = i.getWidth();

		if (i instanceof IndexedBytePixelIterator) {
			IndexedBytePixelIterator ibpi = (IndexedBytePixelIterator) i;
			byteIterator = ibpi;
			indexModel = ibpi.getIndexColorModel();
			intIterator = null;
		} else if (i instanceof BytePixelIterator) {
			byteIterator = (BytePixelIterator) i;
			indexModel = null;
			intIterator = null;
		} else if (i instanceof IntPixelIterator) {
			intIterator = (IntPixelIterator) i;
			byteIterator = null;
			indexModel = null;
		} else {
			throw new IllegalArgumentException(
					"the converted iterator must be a BytePixelIterator or an IntPixelIterator (not a "
							+ i.getClass().getName() + ")");
		}
	}

	@Override
	public int getHeight() {
		return i.getHeight();
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public boolean isDone() {
		return i.isDone();
	}

	@Override
	public boolean isTopDown() {
		return i.isTopDown();
	}

}