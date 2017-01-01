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

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.io.OutputStream;

import com.pump.image.gif.block.GifGraphicControlExtension;
import com.pump.image.gif.block.GifGraphicControlExtension.DisposalMethod;
import com.pump.image.gif.block.GifImageDataBlock;
import com.pump.image.gif.block.GifImageDescriptor;
import com.pump.image.gif.block.GifLocalColorTable;

/**
 * The minimal, bare-bones GIF encoder.
 * <p>I recommend not using this encoder except as a baseline to test against. This
 * doesn't optimize anything: so files sizes will be unnecessarily large.
 */
public class MinimalGifEncoder extends GifEncoder {
	
	@Override
	public void writeImage(OutputStream out,BufferedImage image,int frameDurationInCentiseconds, IndexColorModel globalColorModel,
			boolean writeLocalColorTable)
			throws IOException {
		
		if (frameDurationInCentiseconds >= 0 || globalColorModel.getTransparentPixel() != -1) {
			if (frameDurationInCentiseconds < 0)
				frameDurationInCentiseconds = 0;
			GifGraphicControlExtension gce = new GifGraphicControlExtension(
					frameDurationInCentiseconds,
					DisposalMethod.RESTORE_BACKGROUND,
					globalColorModel.getTransparentPixel());
			gce.write(out);
		}
		int localColorSize = 0;
		if (writeLocalColorTable) {
			localColorSize = globalColorModel.getMapSize();
			int k = 2;
			while (localColorSize > k) {
				k *= 2;
			}
			if (k > 256)
				throw new IllegalArgumentException("Illegal number of colors ("
						+ localColorSize + ").  There can only be 256 at most.");
			localColorSize = k;
		}

		Dimension d = new Dimension(image.getWidth(), image.getHeight());

		GifImageDescriptor id = new GifImageDescriptor(0, 0, d.width, d.height,
				false, localColorSize);
		id.write(out);
		if (localColorSize > 0) {
			GifLocalColorTable ct = new GifLocalColorTable(globalColorModel);
			ct.write(out);
		}
		GifImageDataBlock dataBlock = new GifImageDataBlock(image, globalColorModel);
		dataBlock.write(out);
	}

	/** This does nothing for the SimpleGifEncoder */
	@Override
	public void flush(OutputStream out) {}
}