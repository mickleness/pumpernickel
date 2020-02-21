package com.pump.desktop;

import java.io.File;
import java.io.FileNotFoundException;

import com.apple.eio.FileManager;
import com.pump.desktop.DesktopHelper.FileOperationType;

class MacRevealOperation implements DesktopHelper.FileOperation {

	@Override
	public boolean execute(File file) throws FileNotFoundException {
		return FileManager.revealInFinder(file);
	}

	@Override
	public FileOperationType getType() {
		return FileOperationType.REVEAL;
	}
}