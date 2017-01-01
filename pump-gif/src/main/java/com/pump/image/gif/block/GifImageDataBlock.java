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

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.pump.image.gif.lzw.LZWInputStream;
import com.pump.image.gif.lzw.LZWOutputStream;
import com.pump.image.pixel.IndexedBytePixelIterator;
import com.pump.image.pixel.quantize.ColorLUT;
import com.pump.image.pixel.quantize.ImageQuantization;

/**
 * A block of rasterized image data, encoded via LZW compression.
 * <P>
 * When decoded, each value must correspond to a value in the active color
 * table.
 * <P>
 * This is not technically a
 * {@link com.bric.image.gif.block.GifGraphicRenderingBlock}, because it is, by
 * itself, meaningless. It must be preceded by a
 * {@link com.bric.image.gif.block.GifImageDescriptor} block; so that is considered
 * the real "graphic rendering block".
 */
public class GifImageDataBlock extends GifBlock {
	byte[] encodedData;
	int minimumLZWCodeSize;

	public GifImageDataBlock(IndexedBytePixelIterator imageData, IndexColorModel colorModel) {
		int w = imageData.getWidth();
		int h = imageData.getHeight();

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		int colorDepth = getColorDepth(colorModel);
		minimumLZWCodeSize = colorDepth;
		try {
			LZWOutputStream out = new LZWOutputStream(bytes, colorDepth, false);
			byte[] row = new byte[w];
			for (int y = 0; y < h; y++) {
				imageData.next(row);
				out.write(row);
			}
			out.close();
			encodedData = bytes.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	
	public GifImageDataBlock(BufferedImage src, IndexColorModel colorModel) {
		int w = src.getWidth();
		int h = src.getHeight();
		
		ColorLUT lut = new ColorLUT(colorModel);
		IndexedBytePixelIterator iter = ImageQuantization.MOST_DIFFUSION.createImageData(src, lut);
		
		byte[] block = new byte[w];

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		int colorDepth = getColorDepth(colorModel);
		minimumLZWCodeSize = colorDepth;
		try {
			LZWOutputStream out = new LZWOutputStream(bytes, colorDepth, false);
			for (int y = 0; y < h; y++) {
				iter.next(block);
				out.write(block);
			}
			out.close();
			encodedData = bytes.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	/**
	 * This returns the number of bits you'll need to represent all the colors
	 * in a color model, or this throws an exception if there are over 256
	 * colors.
	 * 
	 * @param colorModel
	 *            the color model
	 * @return the number of bits to represent a color in this color model
	 */
	public static int getColorDepth(IndexColorModel colorModel) {
		int mapSize = colorModel.getMapSize();
		if (mapSize == 2) {
			return 1;
		} else if (mapSize <= 4) {
			return 2;
		} else if (mapSize <= 8) {
			return 3;
		} else if (mapSize <= 16) {
			return 4;
		} else if (mapSize <= 32) {
			return 5;
		} else if (mapSize <= 64) {
			return 6;
		} else if (mapSize <= 128) {
			return 7;
		} else if (mapSize <= 256) {
			return 8;
		} else {
			throw new IllegalArgumentException(
					"Too many colors (\""
							+ mapSize
							+ ").  This method is designed for GIFs, and is limited to accept only 256 colors.");
		}
	}

	public GifImageDataBlock(int minimumLZWCodeSize, byte[] encodedData) {
		this.minimumLZWCodeSize = minimumLZWCodeSize;
		this.encodedData = encodedData;
	}

	public int getMinimumLZWCodeSize() {
		return minimumLZWCodeSize;
	}

	public byte[] getBytes() {
		byte[] b = new byte[1];
		b[0] = (byte) minimumLZWCodeSize;
		return concatenate(b, writeSubBlocks(encodedData));
	}

	public int getByteCount() {
		return getBytes().length;
	}

	public InputStream getUncompressedInputStream() {
		LZWInputStream in = new LZWInputStream(new ByteArrayInputStream(
				encodedData), minimumLZWCodeSize);
		return in;
	}

	public byte[] getUncompressedBytes() {
		try {
			InputStream in = getUncompressedInputStream();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			boolean reading = true;
			while (reading) {
				int t = in.read(b);
				if (t == -1) {
					reading = false;
				} else {
					out.write(b, 0, t);
				}
			}
			byte[] b2 = out.toByteArray();
			return b2;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}