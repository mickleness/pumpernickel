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

import java.awt.image.IndexColorModel;

public class GifGlobalColorTable extends GifColorTable {
	protected GifGlobalColorTable(byte[] b) {
		super(b);
	}

	public GifGlobalColorTable(IndexColorModel i) {
		super(i);
	}
}