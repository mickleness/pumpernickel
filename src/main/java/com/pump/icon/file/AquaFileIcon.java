package com.pump.icon.file;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.swing.Icon;

public class AquaFileIcon extends FileIcon {

	Constructor constructor;

	public AquaFileIcon() {
		try {
			Class<?> z = Class.forName("com.apple.laf.AquaIcon$FileIcon");
			constructor = z.getConstructor(new Class[] { File.class });
			constructor.setAccessible(true);
		} catch (ClassNotFoundException | NoSuchMethodException
				| SecurityException e) {
			throw new RuntimeException(e);
		}
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
