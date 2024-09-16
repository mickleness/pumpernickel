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

import java.awt.image.IndexColorModel;

/**
 * An interface for a {@link PixelIterator} of type
 * <code>BufferedImage.TYPE_BYTE_INDEXED</code>.
 */
public interface IndexedBytePixelIterator extends PixelIterator<byte[]> {

	/**
	 * Returns the <code>IndexColorModel</code> this iterator uses.
	 */
	IndexColorModel getIndexColorModel();
}