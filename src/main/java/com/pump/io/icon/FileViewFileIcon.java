package com.pump.io.icon;

import java.io.File;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileView;

/**
 * This FileIcon creates a new JFileChooser and uses its
 * <code>javax.swing.filechooser.FileView</code> to generate icons.
 * <p>
 * On Mac this generates file icons correctly.
 */
public class FileViewFileIcon extends FileIcon {
	FileView fileView;

	public FileViewFileIcon() {

		JFileChooser chooser = new JFileChooser();
		fileView = chooser.getUI().getFileView(chooser);
	}

	@Override
	public Icon getIcon(File file) {
		Icon returnValue = fileView.getIcon(file);
		if (returnValue != null)
			return returnValue;
		return super.getDefaultIcon(file);
	}

}
