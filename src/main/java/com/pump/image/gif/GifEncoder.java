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
package com.pump.image.gif;

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This is simple model for an object that can encode a GIF. This is designed to
 * work tightly with the {@link com.pump.image.gif.GifWriter}.
 */
public abstract class GifEncoder {
	/**
	 * This method is responsible writing image date to the output stream.
	 * <P>
	 * (The header, logical screen descriptor, and global color table will
	 * already have been written by the time this is called.)
	 * 
	 * @param out
	 *            the output stream to write the
	 *            {@link com.pump.image.gif.block.GifBlock}'s to.
	 * @param image
	 *            the image to write. This must be the full size of the GIF.
	 * @param frameDurationInCentiseconds
	 *            the duration (in centiseconds) of a frame.
	 * @param globalModel
	 *            the color model used for this GIF.
	 * @param writeLocalColorTable
	 *            if this is <code>true</code> then <code>modelInUse</code>
	 *            should be written as the local color table for this image.
	 * @throws IOException
	 *             if the <code>OutputStream</code> gives us any trouble.
	 */
	public abstract void writeImage(OutputStream out, BufferedImage image,
			int frameDurationInCentiseconds, IndexColorModel globalModel,
			boolean writeLocalColorTable) throws IOException;

	/**
	 * Flush any remaining data. Simple encoders will not need to do anything
	 * here, but a more complex encoder might not have committed certain data
	 * yet.
	 * <p>
	 * (This method is not supposed to close the <code>OutputStream</code>, or
	 * append a {@link com.pump.image.gif.block.GifTrailerBlock}.)
	 * <p>
	 * After this is invoked: no more images should be written.
	 * 
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public abstract void flush(OutputStream out) throws IOException;
}