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
package com.pump.swing;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

class ImageTransferable implements Transferable {
	Image img;
	
	public ImageTransferable(Image i) {
		img = i;
	}

	public Object getTransferData(DataFlavor f)
			throws UnsupportedFlavorException, IOException {
		if(f.equals(DataFlavor.imageFlavor)==false)
			throw new UnsupportedFlavorException(f);
		return img;
	}

	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] {DataFlavor.imageFlavor};
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return(flavor.equals(DataFlavor.imageFlavor));
	}
	
}