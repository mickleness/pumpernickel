package com.pump.icon.file;

import java.io.File;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

public class FileSystemViewFileIcon extends FileIcon {

	@Override
	public Icon getIcon(File file) {
		FileSystemView fsv = FileSystemView.getFileSystemView();
		Icon icon = fsv.getSystemIcon(file);
		if (icon != null)
			return icon;
		return null;
	}

}
