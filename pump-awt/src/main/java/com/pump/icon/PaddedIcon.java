/*
 * @(#)PaddedIcon.java
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
package com.pump.icon;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.Icon;

/** An icon that pads another icon with <code>Insets</code>. */
public class PaddedIcon implements Icon {
	final Icon icon;
	final Insets i;

	public PaddedIcon(Icon orig, int padding) {
		this(orig, new Insets(padding, padding, padding, padding));
	}

	public PaddedIcon(Icon orig, Insets i) {
		if(orig==null) throw new NullPointerException();
		if(i==null) throw new NullPointerException();
		this.icon = orig;
		this.i = i;
	}

	public int getIconHeight() {
		return icon.getIconHeight() + i.top + i.bottom;
	}

	public int getIconWidth() {
		return icon.getIconWidth() + i.left + i.right;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		icon.paintIcon(c, g, x+i.left, y+i.top);
	}

}
