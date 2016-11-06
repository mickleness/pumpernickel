/*
 * @(#)LocationPaneDialog.java
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
package com.pump.swing.io;

import java.awt.Frame;

import javax.swing.JDialog;

/** The dialog used when <code>OpenLocationPane</code> or
 * <code>SaveLocationPane</code> invoke <code>showDialog()</code>
 *
 */
public class LocationPaneDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	protected LocationPaneDialog(Frame parent) {
		super(parent);
	}
}
