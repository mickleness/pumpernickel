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
package com.pump.plaf;

import javax.swing.JComponent;

import com.pump.swing.io.LocationBrowser;

public class AquaTileLocationBrowserUI extends TileLocationBrowserUI {

	public AquaTileLocationBrowserUI(LocationBrowser b) {
		super(b);
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		thumbnail.setUI(new AquaThumbnailLabelUI());
	}
}