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

		redTable_int = convert(redTable_byte);
		greenTable_int = convert(greenTable_byte);
		blueTable_int = convert(blueTable_byte);
		alphaTable_int = convert(alphaTable_byte);
	}

	private int[] convert(byte[] array) {
		int[] returnValue = new int[array.length];
		for (int a = 0; a < returnValue.length; a++) {
			returnValue[a] = array[a] & 0xff;
		}
		return returnValue;
	}
}