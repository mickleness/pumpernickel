/*
 * @(#)AnimatedGifFileFilter.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
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
package com.pump.image.gif;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ResourceBundle;

/** A filefilter that only shows animated GIFs.
 * @see com.bric.image.gif.GifReader#isAnimatedGIF(File)
 */
public class AnimatedGifFileFilter extends javax.swing.filechooser.FileFilter
		implements FilenameFilter, java.io.FileFilter {
	public static ResourceBundle strings = ResourceBundle.getBundle("com.pump.image.gif.AnimatedGifFileFilter");
	
	String description;

	public AnimatedGifFileFilter() {
		this(strings.getString("filterName"));
	}

	public AnimatedGifFileFilter(String d) {
		description = d;
	}

	public boolean accept(File f, String s) {
		return accept(new File(f, s));
	}

	public boolean accept(File f) {
		return GifReader.isAnimatedGIF(f);
	}

	public String getDescription() {
		return description;
	}

}
