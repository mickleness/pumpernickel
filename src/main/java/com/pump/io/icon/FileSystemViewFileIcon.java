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
package com.pump.io.icon;

import java.io.File;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

/**
 * This FileIcon uses a <code>javax.swing.filechooser.FileSystemView</code> to
 * generate icons.
 */
public class FileSystemViewFileIcon extends FileIcon {

	@Override
	public Icon getIcon(File file) {
		FileSystemView fsv = FileSystemView.getFileSystemView();
		Icon icon = fsv.getSystemIcon(file);
		if (icon != null)
			return icon;
		return getDefaultIcon(file);
	}

}