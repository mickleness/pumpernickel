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
package com.pump.icon;

import java.io.File;
import java.lang.reflect.Constructor;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;

import com.pump.util.JVM;

/**
 * This offers a static method to best retrieve a File's icon.
 */
public class FileIcon {

	/**
	 * Return the icon of a File.
	 * <p>
	 * Unfortunately Windows and Mac require different approaches.
	 * 
	 * @param file
	 *            the file to get the icon of.
	 * @return an icon for this file.
	 */
	public static Icon getIcon(File file) {
		if (file == null)
			throw new NullPointerException();

		if (JVM.isWindows) {
			// on Macs this appears to only return the vanilla folder/file
			// icons:
			FileSystemView fsv = FileSystemView.getFileSystemView();
			Icon icon = fsv.getSystemIcon(file);
			if (icon != null)
				return icon;
		}
		if (JVM.isMac) {
			try {
				Class<?> z = Class.forName("com.apple.laf.AquaIcon$FileIcon");
				Constructor<?> constructor = z
						.getConstructor(new Class[] { File.class });
				constructor.setAccessible(true);
				Icon icon = (Icon) constructor.newInstance(file);
				if (icon != null)
					return icon;
			} catch (Throwable t) {
			}
		}

		// but this returns different icons for different folders/icons:
		FileView fileView = getFileView();
		return fileView.getIcon(file);
	}

	private static FileView sharedFileView;

	protected static FileView getFileView() {
		if (sharedFileView == null) {
			JFileChooser chooser = new JFileChooser();
			sharedFileView = chooser.getUI().getFileView(chooser);
		}
		return sharedFileView;
	}
}