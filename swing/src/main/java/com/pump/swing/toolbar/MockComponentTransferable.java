/*
 * @(#)MockComponentTransferable.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2011 by Jeremy Wood.
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
package com.pump.swing.toolbar;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import com.pump.swing.MockComponent;

/** A Transferable the encapsulates a MockComponent.
 * <P>This really isn't specifically necessary, but SOME transferable is.
 * And that transferable needs to <i>not</i> be text, or an image,
 * because then other eager apps would try to work with it.
 *
 */
class MockComponentTransferable implements Transferable {
	DataFlavor myDataFlavor;
	MockComponent mockComponent;
	public MockComponentTransferable(MockComponent mc) {
		try {
			myDataFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType);
		} catch (ClassNotFoundException e) {
			RuntimeException e2 = new RuntimeException();
			e2.initCause(e);
			throw e2;
		}
		this.mockComponent = mc;
	}
	
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException {
		if(flavor.equals(myDataFlavor)) {
			return mockComponent;
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}
	

	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { myDataFlavor };
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		DataFlavor[] flavors = getTransferDataFlavors();
		for(int a = 0; a<flavors.length; a++) {
			if(flavors[a].equals(flavor))
				return true;
		}
		return false;
	}
}
