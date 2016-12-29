/*
 * @(#)GifLocalColorTable.java
 *
 * $Date: 2014-06-06 14:04:49 -0400 (Fri, 06 Jun 2014) $
 *
 * Copyright (c) 2014 by Jeremy Wood.
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
package com.pump.image.gif.block;

import java.awt.image.IndexColorModel;

/**
 * This immediately follows a {@link com.bric.image.gif.block.GifImageDescriptor}
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
