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
package com.pump.image.gif.block;

/**
 * This 1-byte block identifies the end of a GIF file.
 * <P>
 * It's value is simply <code>"0x3B"</code>.
 */
public class GifTrailerBlock extends GifBlock {

	public byte[] getBytes() {
		return new byte[] { 0x3B };
	}

	public int getByteCount() {
		return 1;
	}

}