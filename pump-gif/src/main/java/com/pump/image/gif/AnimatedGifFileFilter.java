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