/*
 * @(#)ByteBGRConverter.java
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
import java.awt.image.IndexColorModel;

/** A <code>PixelConverter</code> that converts all data to BGR-formatted
 * bytes.
 */
public class ByteBGRConverter extends PixelConverter implements
		BytePixelIterator {

	byte[] rTable, gTable, bTable;
	int[] intScratch;
	
	/**
	 * 
	 * @param i
	 */
	public ByteBGRConverter(PixelIterator i) {
		super(i);
	}

	public void skip() {
		if(byteIterator!=null) {
			byteIterator.skip();
		} else {
			intIterator.skip();
		}
	}

	public void next(byte[] dest) {
		if (byteIterator != null) {
			byteIterator.next(dest);
			byte swap;
			int rowLength = width*byteIterator.getPixelSize();
			
			switch (originalType) {
			case PixelIterator.TYPE_3BYTE_RGB:
				for (int a = 0; a < rowLength; a+=3) {
					swap = dest[a];
					dest[a] = dest[a+2];
					dest[a+2] = swap;
				}
				break;
			case BufferedImage.TYPE_3BYTE_BGR:
				break;
			case PixelIterator.TYPE_4BYTE_ARGB:
			case PixelIterator.TYPE_4BYTE_ARGB_PRE:
				for (int a = 0; a < rowLength; a+=3) {
					dest[a] = dest[a + 3];
					swap = dest[a + 1];
					dest[a + 1] = dest[a + 2];
					dest[a + 2] = swap;
				}
				break;
			case BufferedImage.TYPE_4BYTE_ABGR:
			case BufferedImage.TYPE_4BYTE_ABGR_PRE:
				for (int a = 0; a < rowLength; a+=4) {
					dest[a] = dest[a + 1];
					dest[a + 1] = dest[a + 2];
					dest[a + 2] = dest[a + 3];
				}
				break;
			case BufferedImage.TYPE_BYTE_GRAY:
				for (int a = width - 1; a >= 0; a--) {
					dest[3 * a] = dest[a];
					dest[3 * a + 1] = dest[a];
					dest[3 * a + 2] = dest[a];
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
				for (int a = width - 1; a >= 0; a--) {
					dest[3 * a + 2] = rTable[dest[a]];
					dest[3 * a + 1] = gTable[dest[a]];
					dest[3 * a] = bTable[dest[a]];
				}
				break;
			default:
				throw new RuntimeException("Unrecognized type ("
						+ BufferedImageIterator.getTypeName(originalType) + ")");
			}
		} else {
			if (intScratch == null) {
				intScratch = new int[intIterator.getMinimumArrayLength()];
			}
			intIterator.next(intScratch);
			switch (originalType) {
			case BufferedImage.TYPE_INT_ARGB:
			case BufferedImage.TYPE_INT_ARGB_PRE:
				// TODO: handle pre-multiplied alpha correctly
				// (see ByteBGRAConverter for example)
			case BufferedImage.TYPE_INT_RGB:
				for (int a = 0; a < width; a++) {
					dest[3 * a + 2] = (byte) ((intScratch[a] >> 16) & 0xff);
					dest[3 * a + 1] = (byte) ((intScratch[a] >> 8) & 0xff);
					dest[3 * a] = (byte) ((intScratch[a]) & 0xff);
				}
				break;
			case BufferedImage.TYPE_INT_BGR:
				for (int a = 0; a < width; a++) {
					dest[3 * a + 2] = (byte) ((intScratch[a]) & 0xff);
					dest[3 * a + 1] = (byte) ((intScratch[a] >> 8) & 0xff);
					dest[3 * a] = (byte) ((intScratch[a] >> 16) & 0xff);
				}
				break;
			default:
				throw new RuntimeException("Unrecognized type ("
						+ BufferedImageIterator.getTypeName(originalType) + ")");
			}
		}
	}

	public IndexColorModel getIndexColorModel() {
		return null;
	}

	public int getMinimumArrayLength() {
		if (byteIterator != null) {
			return Math.max(byteIterator.getMinimumArrayLength(),
					3 * getWidth());
		}
		return 3 * getWidth();
	}

	public int getType() {
		return BufferedImage.TYPE_3BYTE_BGR;
	}

	public int getPixelSize() {
		return 3;
	}
}
