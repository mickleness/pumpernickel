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
package com.pump.image.pixel;

import java.awt.image.IndexColorModel;

/**
 * An interface for a {@link com.pump.image.pixel.BytePixelIterator} of type
 * <code>BufferedImage.TYPE_BYTE_INDEXED</code>.
 */
public interface IndexedBytePixelIterator extends BytePixelIterator {

	/**
	 * Returns the <code>IndexColorModel</code> this iterator uses.
	 */
	IndexColorModel getIndexColorModel();
}