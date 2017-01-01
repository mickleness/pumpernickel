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

import java.awt.image.BufferedImage;

/** A <code>PixelConverter</code> that converts all data to RGB-formatted
 * integers.
 */
public class IntRGBConverter extends PixelConverter implements
		IntPixelIterator {

	byte[] rTable, gTable, bTable;
	byte[] byteScratch;

	public IntRGBConverter(PixelIterator i) {
		super(i);
	}

	public void skip() {
		if (byteIterator != null) {
			byteIterator.skip();
		} else if(intIterator!=null) {
			intIterator.skip();
		}
	}
	
	public void next(int[] dest) {
		if (byteIterator != null) {
			if (byteScratch == null) {
				byteScratch = new byte[byteIterator.getMinimumArrayLength()];
			}
			byteIterator.next(byteScratch);
			int alpha;
			switch (originalType) {
			case BufferedImage.TYPE_3BYTE_BGR:
				for (int a = 0; a < width; a++) {
					dest[a] = ((byteScratch[3 * a + 2] & 0xff) << 16)
							+ ((byteScratch[3 * a + 1] & 0xff) << 8)
							+ ((byteScratch[3 * a + 0] & 0xff));
				}
				break;
			case TYPE_4BYTE_BGRA:
				for (int a = 0; a < width; a++) {
					dest[a] = (( byteScratch[4 * a + 0] & 0xff ) << 16)
							+ (( byteScratch[4 * a + 1] & 0xff ) << 8)
							+ (( byteScratch[4 * a + 2] & 0xff ));
				}
				break;
			case BufferedImage.TYPE_4BYTE_ABGR:
				for (int a = 0; a < width; a++) {
					dest[a] = ((byteScratch[4 * a + 3] & 0xff) << 16)
							+ ((byteScratch[4 * a + 2] & 0xff) << 8)
							+ ((byteScratch[4 * a + 1] & 0xff));
				}
				break;
			case BufferedImage.TYPE_4BYTE_ABGR_PRE:
				for (int a = 0; a < width; a++) {
					alpha = (dest[4 * a + 3] & 0xff);
					dest[a] = ((byteScratch[4 * a + 0] & 0xff) << 24)
							+ ((byteScratch[4 * a + 3] & 0xff) * 255 / alpha << 16)
							+ ((byteScratch[4 * a + 2] & 0xff) * 255 / alpha << 8)
							+ ((byteScratch[4 * a + 1] & 0xff) * 255 / alpha);
				}
				break;
			case PixelIterator.TYPE_3BYTE_RGB:
				for (int a = 0; a < width; a++) {
					dest[a] = ((byteScratch[3 * a + 0] & 0xff) << 16)
							+ ((byteScratch[3 * a + 1] & 0xff) << 8)
							+ ((byteScratch[3 * a + 2] & 0xff));
				}
				break;
			case PixelIterator.TYPE_4BYTE_ARGB:
				for (int a = 0; a < width; a++) {
					dest[a] = ((byteScratch[4 * a + 1] & 0xff) << 16)
							+ ((byteScratch[4 * a + 2] & 0xff) << 8)
							+ ((byteScratch[4 * a + 3] & 0xff));
				}
			case PixelIterator.TYPE_4BYTE_ARGB_PRE:
				for (int a = 0; a < width; a++) {
					alpha = (dest[4 * a + 3] & 0xff);
					dest[a] = ((byteScratch[4 * a + 0] & 0xff) << 24)
							+ ((byteScratch[4 * a + 1] & 0xff) * 255 / alpha << 16)
							+ ((byteScratch[4 * a + 2] & 0xff) * 255 / alpha << 8)
							+ ((byteScratch[4 * a + 3] & 0xff) * 255 / alpha);
				}
				break;
			case BufferedImage.TYPE_BYTE_GRAY:
				for (int a = 0; a < width; a++) {
					dest[a] = ((byteScratch[a] & 0xff) << 16)
							+ ((byteScratch[a] & 0xff) << 8)
							+ ((byteScratch[a] & 0xff) << 0);
				}
				break;
			case BufferedImage.TYPE_BYTE_INDEXED:
				if (rTable == null) {
					rTable = new byte[indexModel.getMapSize()];
					gTable = new byte[indexModel.getMapSize()];
					bTable = new byte[indexModel.getMapSize()];
					indexModel.getReds(rTable);
					indexModel.getGreens(gTable);
					indexModel.getBlues(bTable);
				}
				for (int a = 0; a < width; a++) {
					dest[a] = ((rTable[dest[a]] & 0xff) << 16)
							+ ((gTable[dest[a]] & 0xff) << 8)
							+ ((bTable[dest[a]] & 0xff));
				}
				break;
			default:
				throw new RuntimeException("Unrecognized type ("
						+ BufferedImageIterator.getTypeName(originalType) + ")");
			}
		} else {
			intIterator.next(dest);
			int alpha;
			switch (originalType) {
			case BufferedImage.TYPE_INT_RGB:
				return;
			case BufferedImage.TYPE_INT_ARGB_PRE:
				for (int a = 0; a < width; a++) {
					alpha = ((dest[a] >> 24) & 0xff);
					if (alpha > 0 && alpha < 255) {
						dest[a] = (((dest[a] >> 16) & 0xff) * 255 / alpha << 16)
								+ (((dest[a] >> 8) & 0xff) * 255 / alpha << 8)
								+ (((dest[a]) & 0xff) * 255 / alpha);
					}
				}
				break;
			case BufferedImage.TYPE_INT_ARGB:
				for (int a = 0; a < width; a++) {
					dest[a] = (dest[a] & 0xffffff);
				}
				break;
			case BufferedImage.TYPE_INT_BGR:
				for (int a = 0; a < width; a++) {
					dest[a] = (((dest[a] >> 16) & 0xff)) + // blue
							(dest[a] & 0x00ff00) + // green stays put
							(((dest[a] & 0xff) << 16)); // red
				}
				break;
			default:
				throw new RuntimeException("Unrecognized type ("
						+ BufferedImageIterator.getTypeName(originalType) + ")");
			}
		}
	}

	public int getMinimumArrayLength() {
		if (intIterator != null) {
			return Math.max(intIterator.getMinimumArrayLength(), getWidth());
		}
		return getWidth();
	}

	public int getType() {
		return BufferedImage.TYPE_INT_RGB;
	}

	public int getPixelSize() {
		return 1;
	}
}