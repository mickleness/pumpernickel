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

/**
 * This immediately follows a {@link com.pump.image.gif.block.GifImageDescriptor}
 * block if its <code>hasLocalColorTable()</code> method returns
 * <code>true</code>. The GIF file format specification points out:
 * <P>
 * "...at most one Local Color Table may be present per Image Descriptor and its
 * scope is the single image associated with the Image Descriptor that precedes
 * it.
 */
public class GifLocalColorTable extends GifColorTable {
	public GifLocalColorTable(byte[] b) {
		super(b);
	}

	public GifLocalColorTable(IndexColorModel i) {
		super(i);
	}
}