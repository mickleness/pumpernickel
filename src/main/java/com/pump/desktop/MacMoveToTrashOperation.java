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
package com.pump.desktop;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;

import com.pump.desktop.DesktopHelper.FileOperationType;

class MacMoveToTrashOperation implements DesktopHelper.FileOperation {

	@Override
	public boolean execute(File file) throws FileNotFoundException {
		if(Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.MOVE_TO_TRASH))
			return Desktop.getDesktop().moveToTrash(file);
		return false;
	}

	@Override
	public FileOperationType getType() {
		return FileOperationType.MOVE_TO_TRASH;
	}
}