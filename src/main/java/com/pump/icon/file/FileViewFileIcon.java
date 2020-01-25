package com.pump.icon.file;

import java.io.File;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileView;

public class FileViewFileIcon extends FileIcon {
	FileView fileView;

	public FileViewFileIcon() {

		JFileChooser chooser = new JFileChooser();
		fileView = chooser.getUI().getFileView(chooser);
	}

	@Override
	public Icon getIcon(File file) {
		return fileView.getIcon(file);
	}

}
