/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.io.icon;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.swing.Icon;

/**
 * This {@link FileIcon} uses the <code>com.apple.laf.AquaIcon$FileIcon</code>
 * class to render scalable icons from the OS.
 * <p>
 * This is constructed using reflection to access a public com.apple class. If
 * this ever fails the {@link FileViewFileIcon} also works on Mac.
 */
public class AquaFileIcon extends FileIcon {

	Constructor constructor;

	/**
	 * This constructor throws an exception if we can't access the
	 * <code>com.apple.laf.AquaIcon$FileIcon</code> using reflection.
	 */
	public AquaFileIcon() throws ClassNotFoundException, NoSuchMethodException,
			SecurityException {
		Class<?> z = Class.forName("com.apple.laf.AquaIcon$FileIcon");
		constructor = z.getConstructor(new Class[] { File.class });
		constructor.setAccessible(true);
	}

	@Override
	public Icon getIcon(File file) {
		try {
			Icon icon = (Icon) constructor.newInstance(file);
			if (icon != null)
				return icon;
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

}