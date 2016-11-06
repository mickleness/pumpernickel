/*
 * @(#)EmptyIcon.java
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

import javax.swing.Icon;

/** An empty icon, used for placeholders or stubs.
 * 
 */
public class EmptyIcon implements Icon {

	int w, h;
	
	public EmptyIcon(int width,int height) {
		w = width;
		h = height;
	}
	
	public int getIconHeight() {
		return h;
	}

	public int getIconWidth() {
		return w;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {}

}
