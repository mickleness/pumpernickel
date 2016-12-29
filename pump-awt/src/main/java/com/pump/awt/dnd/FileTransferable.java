/*
 * @(#)FileTransferable.java
 *
 * $Date: 2015-09-13 14:46:53 -0400 (Sun, 13 Sep 2015) $
 *
 * Copyright (c) 2015 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */
package com.pump.awt.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** A transferable for an array of files.
 * <p>This uses the standard {@link java.awt.datatransfer.DataFlavor#javaFileListFlavor}
 */
public class FileTransferable implements Transferable {
	File[] files;
	
	public FileTransferable(File... file) {
		this.files = file;
	}
	
	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { DataFlavor.javaFileListFlavor };
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return DataFlavor.javaFileListFlavor.equals(flavor);
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if(!isDataFlavorSupported(flavor)) {
			throw new UnsupportedFlavorException(flavor);
		}
		List<File> list = new ArrayList<>();
		for(File file : files) {
			list.add(file);
		}
		return list;
	}
	
}