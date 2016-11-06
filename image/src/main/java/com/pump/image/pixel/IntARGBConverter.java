/*
 * @(#)IntARGBConverter.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2011 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */
package com.pump.image.pixel;

import java.awt.image.BufferedImage;

/** A <code>PixelConverter</code> that converts all data to ARGB-formatted
 * integers.
 */
public class IntARGBConverter extends PixelConverter implements
		IntPixelIterator {

	byte[] rTable, gTable, bTable, aTable;
	byte[] byteScratch;

	public IntARGBConverter(PixelIterator i) {
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
					dest[a] = 0xff000000
							+ ((byteScratch[3 * a + 2] & 0xff) << 16)
							+ ((byteScratch[3 * a + 1] & 0xff) << 8)
							+ ((byteScratch[3 * a + 0] & 0xff));
				}
				break;
			case TYPE_4BYTE_BGRA:
				for (int a = 0; a < width; a++) {
					dest[a] = (( byteScratch[4 * a + 3] & 0xff ) << 24)
							+ (( byteScratch[4 * a + 0] & 0xff ) << 16)
							+ (( byteScratch[4 * a + 1] & 0xff ) << 8)
							+ (( byteScratch[4 * a + 2] & 0xff ));
				}
				break;
			case BufferedImage.TYPE_4BYTE_ABGR:
				for (int a = 0; a < width; a++) {
					dest[a] = ((byteScratch[4 * a + 0] & 0xff) << 24)
							+ ((byteScratch[4 * a + 3] & 0xff) << 16)
							+ ((byteScratch[4 * a + 2] & 0xff) << 8)
							+ ((byteScratch[4 * a + 1] & 0xff));
				}
				break;
			case BufferedImage.TYPE_4BYTE_ABGR_PRE:
				for (int a = 0; a < width; a++) {
					alpha = (byteScratch[4 * a + 0] & 0xff);
					if(alpha!=0) {
						dest[a] = ((byteScratch[4 * a + 0] & 0xff) << 24)
								+ ((byteScratch[4 * a + 3] & 0xff) * 255 / alpha << 16)
								+ ((byteScratch[4 * a + 2] & 0xff) * 255 / alpha << 8)
								+ ((byteScratch[4 * a + 1] & 0xff) * 255 / alpha);
					} else {
						dest[a] = ((byteScratch[4 * a + 0] & 0xff) << 24)
								+ ((byteScratch[4 * a + 3] & 0xff) << 16)
								+ ((byteScratch[4 * a + 2] & 0xff) << 8)
								+ ((byteScratch[4 * a + 1] & 0xff) );
					}
				}
				break;
			case PixelIterator.TYPE_3BYTE_RGB:
				for (int a = 0; a < width; a++) {
					dest[a] = 0xff000000
							+ ((byteScratch[3 * a + 2] & 0xff))
							+ ((byteScratch[3 * a + 1] & 0xff) << 8)
							+ ((byteScratch[3 * a + 0] & 0xff) << 16);
				}
				break;
			case PixelIterator.TYPE_4BYTE_ARGB:
				for (int a = 0; a < width; a++) {
					dest[a] = ((byteScratch[4 * a + 0] & 0xff) << 24)
							+ ((byteScratch[4 * a + 1] & 0xff) << 16)
							+ ((byteScratch[4 * a + 2] & 0xff) << 8)
							+ ((byteScratch[4 * a + 3] & 0xff));
				}
				break;
			case PixelIterator.TYPE_4BYTE_ARGB_PRE:
				for (int a = 0; a < width; a++) {
					alpha = (byteScratch[4 * a + 0] & 0xff);
					if(alpha!=0) {
						dest[a] = ((byteScratch[4 * a + 0] & 0xff) << 24)
								+ ((byteScratch[4 * a + 1] & 0xff) * 255 / alpha << 16)
								+ ((byteScratch[4 * a + 2] & 0xff) * 255 / alpha << 8)
								+ ((byteScratch[4 * a + 3] & 0xff) * 255 / alpha);
					} else {
						dest[a] = ((byteScratch[4 * a + 0] & 0xff) << 24)
								+ ((byteScratch[4 * a + 1] & 0xff) << 16)
								+ ((byteScratch[4 * a + 2] & 0xff) << 8)
								+ ((byteScratch[4 * a + 3] & 0xff) );
					}
				}
				break;
			case BufferedImage.TYPE_BYTE_GRAY:
				for (int a = 0; a < width; a++) {
					dest[a] = ((0xff000000) << 24)
							+ ((byteScratch[a] & 0xff) << 16)
							+ ((byteScratch[a] & 0xff) << 8)
							+ ((byteScratch[a] & 0xff) << 0);
				}
				break;
			case BufferedImage.TYPE_BYTE_INDEXED:
				if (rTable == null) {
					rTable = new byte[indexModel.getMapSize()];
					gTable = new byte[indexModel.getMapSize()];
					bTable = new byte[indexModel.getMapSize()];
					aTable = new byte[indexModel.getMapSize()];
					indexModel.getReds(rTable);
					indexModel.getGreens(gTable);
					indexModel.getBlues(bTable);
					indexModel.getAlphas(aTable);
				}
				for (int a = 0; a < width; a++) {
					try {
						alpha = (aTable[byteScratch[a] & 0xff] & 0xff);
						int red = (rTable[byteScratch[a] & 0xff] & 0xff);
						int green = (gTable[byteScratch[a] & 0xff] & 0xff);
						int blue = (bTable[byteScratch[a] & 0xff] & 0xff);
						dest[a] = (alpha << 24)
								+ (red << 16)
								+ (green << 8)
								+ (blue);
					} catch(RuntimeException e) {
						throw e;
					}
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
			case BufferedImage.TYPE_INT_ARGB:
				return;
			case BufferedImage.TYPE_INT_ARGB_PRE:
				for (int a = 0; a < width; a++) {
					alpha = ((dest[a] >> 24) & 0xff);
					if (alpha > 0 && alpha < 255) {
						dest[a] = (dest[a] & 0xff000000)
								+ (((dest[a] >> 16) & 0xff) * 255 / alpha << 16)
								+ (((dest[a] >> 8) & 0xff) * 255 / alpha << 8)
								+ (((dest[a]) & 0xff) * 255 / alpha);
					}
				}
				break;
			case BufferedImage.TYPE_INT_RGB:
				for (int a = 0; a < width; a++) {
					dest[a] = (0xff000000) + (dest[a] & 0xffffff);
				}
				break;
			case BufferedImage.TYPE_INT_BGR:
				for (int a = 0; a < width; a++) {
					dest[a] = (0xff000000) + (((dest[a] >> 16) & 0xff)) + // blue
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
		return BufferedImage.TYPE_INT_ARGB;
	}

	public int getPixelSize() {
		return 1;
	}
}
