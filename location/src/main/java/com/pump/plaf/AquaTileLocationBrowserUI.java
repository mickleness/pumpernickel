/*
 * @(#)AquaTileLocationBrowserUI.java
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
