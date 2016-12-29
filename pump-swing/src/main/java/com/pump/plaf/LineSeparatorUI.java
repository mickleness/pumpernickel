/*
 * @(#)LineSeparatorUI.java
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.plaf.SeparatorUI;

public class LineSeparatorUI extends SeparatorUI {
	Color[] colors;
	
	public LineSeparatorUI(Color c) {
		this(new Color[] { c });
	}
	
	public LineSeparatorUI(Color[] array) {
		colors = new Color[array.length];
		System.arraycopy(array,0,colors,0,array.length);
	}

	@Override
	public Dimension getMaximumSize(JComponent c) {
		return getSize((JSeparator)c, Integer.MAX_VALUE);
	}
	
	private Dimension getSize(JSeparator separator,int otherDimension) {
		if(separator.getOrientation()==SwingConstants.VERTICAL) {
			return new Dimension(colors.length, otherDimension);
		}
		return new Dimension(otherDimension, colors.length);
	}

	@Override
	public Dimension getMinimumSize(JComponent c) {
		return getSize( (JSeparator)c, 1);
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		return getSize( (JSeparator)c, 1);
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		JSeparator separator = (JSeparator)c;
		int w = separator.getWidth();
		int h = separator.getHeight();
		for(int a = 0; a<colors.length; a++) {
			if(colors[a]!=null) {
				g.setColor(colors[a]);
				if(separator.getOrientation()==SwingConstants.VERTICAL) {
					g.drawLine(a,0,a,h-1);
				} else {
					g.drawLine(0,a,w-1,a);
				}
			}
		}
	}
}
