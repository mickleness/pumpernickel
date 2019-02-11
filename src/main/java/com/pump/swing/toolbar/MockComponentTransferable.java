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
package com.pump.swing.toolbar;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import com.pump.swing.MockComponent;

/**
 * A Transferable the encapsulates a MockComponent.
 * <P>
 * This really isn't specifically necessary, but SOME transferable is. And that
 * transferable needs to <i>not</i> be text, or an image, because then other
 * eager apps would try to work with it.
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
		if (flavor.equals(myDataFlavor)) {
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
		for (int a = 0; a < flavors.length; a++) {
			if (flavors[a].equals(flavor))
				return true;
		}
		return false;
	}
}