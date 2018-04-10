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

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;

import com.pump.io.SuffixFilenameFilter;

public class FileDialogUtils {

	public static File showOpenDialog(Frame f, String title,
			String... extensions) {
		FileDialog fd = new FileDialog(f, title);
		fd.setMode(FileDialog.LOAD);
		if (extensions != null && extensions.length > 0)
			fd.setFilenameFilter(new SuffixFilenameFilter(extensions));
		fd.pack();
		fd.setLocationRelativeTo(null);
		fd.setVisible(true);
		if (fd.getFile() == null)
			return null;
		return new File(fd.getDirectory() + fd.getFile());
	}

	public static File showSaveDialog(Frame f, String title, String extension) {
		if (extension.startsWith("."))
			extension = extension.substring(1);

		FileDialog fd = new FileDialog(f, title);
		fd.setMode(FileDialog.SAVE);
		fd.setFilenameFilter(new SuffixFilenameFilter(extension));
		fd.pack();
		fd.setLocationRelativeTo(null);
		fd.setVisible(true);

		String s = fd.getFile();
		if (s == null)
			return null;

		if (s.toLowerCase().endsWith("." + extension)) {
			return new File(fd.getDirectory() + s);
		}

		return new File(fd.getDirectory() + fd.getFile() + "." + extension);
	}
}