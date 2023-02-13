/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.image.pixel.converter;

import java.awt.image.IndexColorModel;
import java.util.Objects;

/**
 * This look up table prepares component arrays for fast identification.
 */
public class IndexColorModelLUT {
	public final IndexColorModel indexColorModel;
	public final byte[] redTable_byte, greenTable_byte, blueTable_byte,
			alphaTable_byte;
	public final byte[] redTable_pre_byte, greenTable_pre_byte, blueTable_pre_byte;

	public final int[] redTable_int, greenTable_int, blueTable_int,
			alphaTable_int;

	public IndexColorModelLUT(IndexColorModel indexColorModel) {
		this.indexColorModel = Objects.requireNonNull(indexColorModel);
		redTable_byte = new byte[indexColorModel.getMapSize()];
		greenTable_byte = new byte[indexColorModel.getMapSize()];
		blueTable_byte = new byte[indexColorModel.getMapSize()];
		alphaTable_byte = new byte[indexColorModel.getMapSize()];
		indexColorModel.getReds(redTable_byte);
		indexColorModel.getGreens(greenTable_byte);
		indexColorModel.getBlues(blueTable_byte);
		indexColorModel.getAlphas(alphaTable_byte);

		redTable_int = convertIntsToBytes(redTable_byte);
		greenTable_int = convertIntsToBytes(greenTable_byte);
		blueTable_int = convertIntsToBytes(blueTable_byte);
		alphaTable_int = convertIntsToBytes(alphaTable_byte);

		redTable_pre_byte = premultiplyBytes(redTable_byte, alphaTable_int);
		greenTable_pre_byte = premultiplyBytes(greenTable_byte, alphaTable_int);
		blueTable_pre_byte = premultiplyBytes(blueTable_byte, alphaTable_int);
	}

	private static byte[] premultiplyBytes(byte[] byteArray, int[] alphaTable) {
		byte[] returnValue = new byte[byteArray.length];
		for (int a = 0; a < byteArray.length; a++) {
			returnValue[a] = (byte)( (byteArray[a] & 0xff) * alphaTable[a] );
		}
		return returnValue;
	}

	private static int[] convertIntsToBytes(byte[] array) {
		int[] returnValue = new int[array.length];
		for (int a = 0; a < returnValue.length; a++) {
			returnValue[a] = array[a] & 0xff;
		}
		return returnValue;
	}
}