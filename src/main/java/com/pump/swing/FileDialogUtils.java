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
import java.io.FilenameFilter;

import com.pump.io.SuffixFilenameFilter;

public class FileDialogUtils {

	/**
	 * Returns a file the user selected or null if the user cancelled the
	 * dialog.
	 */
	public static File showOpenDialog(Frame f, String title,
			String... extensions) {
		FilenameFilter filter = null;
		if (extensions != null && extensions.length > 0)
			filter = new SuffixFilenameFilter(extensions);
		return showOpenDialog(f, title, filter);
	}

	/**
	 * Returns a file the user selected or null if the user cancelled the
	 * dialog.
	 */
	public static File showOpenDialog(Frame f, String title,
			FilenameFilter filter) {
		FileDialog fd = new FileDialog(f, title);
		fd.setMode(FileDialog.LOAD);
		if (filter != null)
			fd.setFilenameFilter(filter);
		fd.pack();
		fd.setLocationRelativeTo(null);
		fd.setVisible(true);
		if (fd.getFile() == null)
			return null;
		return new File(fd.getDirectory() + fd.getFile());
	}

	/**
	 * Show a save FileDialog.
	 * 
	 * @param f
	 *            the frame that owns the FileDialog.
	 * @param title
	 *            the dialog title
	 * @param extension
	 *            the file extension ("xml", "png", etc.)
	 * @return a File the user chose.
	 */
	public static File showSaveDialog(Frame f, String title, String extension) {
		return showSaveDialog(f, title, null, extension);
	}

	/**
	 * Show a save FileDialog.
	 * 
	 * @param f
	 *            the frame that owns the FileDialog.
	 * @param title
	 *            the dialog title
	 * @param filename
	 *            the optional default filename shown in the file dialog.
	 * @param extension
	 *            the file extension ("xml", "png", etc.)
	 * @return a File the user chose.
	 */
	public static File showSaveDialog(Frame f, String title, String filename,
			String extension) {
		if (extension.startsWith("."))
			extension = extension.substring(1);

		FileDialog fd = new FileDialog(f, title);
		fd.setMode(FileDialog.SAVE);
		if (filename != null)
			fd.setFile(filename);
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

		// TODO: show a 'are you sure you want to replace' dialog here, if we
		// change the filename
		// native FileDialogs don't always show the right warning dialog IF the
		// file extension
		// isn't present

		return new File(fd.getDirectory() + fd.getFile() + "." + extension);
	}
}