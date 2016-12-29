/*
 * @(#)MacToolbarSeparatorUI.java
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicSeparatorUI;

/** A fine dotted line.
 */
class MacToolbarSeparatorUI extends BasicSeparatorUI {

	@Override
	public Dimension getPreferredSize(JComponent c) {
		Dimension d = super.getPreferredSize(c);
		d.width = 8;
		return d;
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		if(c.isOpaque()) {
			g.setColor(c.getBackground());
			g.fillRect(0,0,c.getWidth(), c.getHeight());
		}
		paintSeparator(g,c.getWidth(),c.getHeight());
	}
	
	/** Paints a separator in the dimensions provided.
	 */
	protected static void paintSeparator(Graphics g,int w, int h) {
		int y = 0;
		g.setColor(new Color(128,128,128));
		int x = w/2;
		
		while(y<h) {
			g.fillRect(x, y, 1, 1);
			y+=3;
		}
	}
}
