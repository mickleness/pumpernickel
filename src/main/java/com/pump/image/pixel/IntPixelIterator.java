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

public interface IntPixelIterator extends PixelIterator {
	/**
	 * Reads a row of pixel data.
	 * 
	 * @param dest
	 *            the array to store the pixels in
	 */
	public abstract void next(int[] dest);
}