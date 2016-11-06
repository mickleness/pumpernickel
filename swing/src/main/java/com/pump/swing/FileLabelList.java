/*
 * @(#)FileLabelList.java
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
package com.pump.swing;

import java.io.File;
import java.io.FileFilter;

import javax.swing.JComponent;
import javax.swing.JLabel;

import com.pump.util.SearchConstraints;

public class FileLabelList extends FileList {
	private static final long serialVersionUID = 1L;

	public FileLabelList(File[] directories, FileFilter primaryFilter,
			SearchConstraints<File> constraints) {
		super(directories, primaryFilter, constraints);
	}

	@Override
	final protected JComponent createComponent(File file) {
		return new JLabel(getText(file));
	}

	/** Returns the text a label should display for a given File.
	 * 
	 */
	protected String getText(File file) {
		return file.getName();
	}
}
