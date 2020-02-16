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
 * <p>
 * An "icon" is not the same as a "thumbnail": usually all files with the same
 * file type have the same icon. A "file icon" is supposed to look good at
 * dimensions under 20x20.
 */
public abstract class FileIcon {
	static FileIcon DEFAULT_FILE_ICON = getDefaultFileIcon();

	/**
	 * Return the current default FileIcon implementation.
	 */
	public static FileIcon get() {
		return DEFAULT_FILE_ICON;
	}

	/**
	 * Assign the current default FileIcon.
	 */
	public static void set(FileIcon fileIcon) {
		if (fileIcon == null)
			fileIcon = getDefaultFileIcon();
		DEFAULT_FILE_ICON = fileIcon;
	}

	private static FileIcon getDefaultFileIcon() {
		if (JVM.isMac) {
			try {
				return new AquaFileIcon();
			} catch (Exception e) {
				// weird; did Apple's classes change significantly?
			}
		} else if (JVM.isWindows) {
			return new FileSystemViewFileIcon();
		}
		return new FileViewFileIcon();
	}

	/**
	 * Return an icon for a File.
	 * 
	 * @param file
	 *            the file to retrieve the icon for.
	 * @param canReturnNull
	 *            if true then this method may return null (if the file doesn't
	 *            exist, or some other problem occurs). If false then this
	 *            method will never return null. This may default to
	 *            <code>UIManager.getIcon("FileView.fileIcon")</code> and
	 *            <code>UIManager.getIcon("FileView.directoryIcon")</code> as a
	 *            last resort.
	 * @return an icon for the File.
	 */
	public Icon getIcon(File file, boolean canReturnNull) {
		Icon i = getIcon(file);
		if (i == null && !canReturnNull) {
			if (file.isDirectory()) {
				return UIManager.getIcon("FileView.fileIcon");
			}
			return UIManager.getIcon("FileView.directoryIcon");
		}
		return i;
	}

	/**
	 * Return an icon for a File.
	 * <p>
	 * This may return null if the file does not exist or no icon can be
	 * identified.
	 * 
	 * @param file
	 *            the file to retrieve the icon for.
	 * @return an icon, or null.
	 */
	public abstract Icon getIcon(File file);
}