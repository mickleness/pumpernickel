package com.pump.desktop;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;

import com.pump.desktop.DesktopHelper.FileOperationType;

class MacRevealOperation implements DesktopHelper.FileOperation {

	@Override
	public boolean execute(File file) throws FileNotFoundException {
		if(Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE_FILE_DIR)) {
			Desktop.getDesktop().browseFileDirectory(file);
			return true;
		}
		return false;
	}

	@Override
	public FileOperationType getType() {
		return FileOperationType.REVEAL;
	}
}