package com.pump.desktop;

import java.io.File;
import java.io.FileNotFoundException;

import com.apple.eio.FileManager;
import com.pump.desktop.DesktopHelper.FileOperationType;

class MacMoveToTrashOperation implements DesktopHelper.FileOperation {

	@Override
	public boolean execute(File file) throws FileNotFoundException {
		return FileManager.moveToTrash(file);
	}

	@Override
	public FileOperationType getType() {
		return FileOperationType.MOVE_TO_TRASH;
	}
}