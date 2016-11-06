/*
 * @(#)PartialLineBorder.java
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
package com.pump.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;

import javax.swing.border.Border;

/** A border that only paints some of its edges. */
public class PartialLineBorder implements Border {
	Paint p;
	Insets i;
	
	public PartialLineBorder(Paint p,Insets i) {
		this.p = p;
		this.i = (Insets)i.clone();
		
	}
	
	public Insets getBorderInsets(Component c) {
		return (Insets)i.clone();
	}

	public boolean isBorderOpaque() {
		return false;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
		((Graphics2D)g).setPaint(p);
		for(int a = y; a<y+i.top; a++) {
			g.drawLine(x,a,x+w,a);
		}
		for(int a = x; a<x+i.left; a++) {
			g.drawLine(a,y,a,y+h);
		}
		for(int a = y+h-i.bottom; a<y+h; a++) {
			g.drawLine(x,a,x+w,a);
		}
		for(int a = x+w-i.right; a<x+w; a++) {
			g.drawLine(a,y,a,y+h);
		}
	}

}
