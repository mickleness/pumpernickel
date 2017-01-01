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