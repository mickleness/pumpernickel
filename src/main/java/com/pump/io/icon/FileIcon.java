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
import javax.swing.UIManager;

import com.pump.util.JVM;

/**
 * This offers a static method to best retrieve a File's icon.
 */
public abstract class FileIcon {
	static FileIcon DEFAULT_FILE_ICON = getDefaultFileIcon();

	public static FileIcon get() {
		return DEFAULT_FILE_ICON;
	}

	public static void set(FileIcon fileIcon) {
		if (fileIcon == null)
			fileIcon = getDefaultFileIcon();
		DEFAULT_FILE_ICON = fileIcon;
	}

	private static FileIcon getDefaultFileIcon() {
		if (JVM.isMac) {
			try {
				return new AquaFileIcon();
			} catch (RuntimeException e) {
			}
		} else if (JVM.isWindows) {
			return new FileSystemViewFileIcon();
		}
		return new FileViewFileIcon();
	}

	public Icon getIcon(File file, boolean canReturnNull) {
		Icon i = getIcon(file);
		if (i == null && !canReturnNull) {
			if (file.isDirectory()) {
				return UIManager.getIcon("Tree.closedIcon");
			}
			return UIManager.getIcon("Tree.leafIcon");
		}
		return i;
	}

	public abstract Icon getIcon(File file);
}