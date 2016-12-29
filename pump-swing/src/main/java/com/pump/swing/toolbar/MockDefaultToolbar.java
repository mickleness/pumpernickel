/*
 * @(#)MockDefaultToolbar.java
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
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JSeparator;

import com.pump.swing.MockComponent;

/** This is a rendering of the default toolbar.
 * This may look different from the actual toolbar because all
 * flexible spaces are compacted to the minimum/default space
 * width.
 */
class MockDefaultToolbar extends MockComponent {

	private static final long serialVersionUID = 1L;
	
	public MockDefaultToolbar(CustomizedToolbar toolbar) {
		super(createImage(toolbar));
	}
	
	private static BufferedImage createImage(CustomizedToolbar toolbar) {
		String[] contents = toolbar.getDefaultContents();
		Insets insets = toolbar.getComponentInsets();
		Dimension[] sizes = new Dimension[contents.length];
		JComponent[] components = new JComponent[contents.length];
		
		int minWidth = 0;
		for(int a = 0; a<contents.length; a++) {
			minWidth += insets.left+insets.right;
			components[a] = toolbar.getComponent(contents[a]);
			sizes[a] = components[a].getPreferredSize();
			if(components[a] instanceof JSeparator) {
				sizes[a].height = toolbar.minimumHeight;
			}
			if(contents[a].length()>0 && contents[a].charAt(0)=='\t') {
				sizes[a].width = SpaceComponent.SPACE_COMPONENT_WIDTH;
			}
			minWidth += sizes[a].width;
		}
		
		BufferedImage bi = new BufferedImage(minWidth,toolbar.minimumHeight+insets.top+insets.bottom,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();

		
		g.setColor(new Color(0,0,0,80));
		g.drawRect(0, 0, bi.getWidth()-1, bi.getHeight()-1);
		
		g.translate(0,insets.top);
		for(int a = 0; a<contents.length; a++) {
			g.translate(insets.left, 0);
			int dy = toolbar.minimumHeight/2-sizes[a].height/2;
			g.translate(0, dy);
			
			if(contents[a].length()>0 && contents[a].charAt(0)=='\t') {
				SpaceComponent.paintSpace(g,false,true,sizes[a].width,sizes[a].height);
			} else if(contents[a].length()>0 && contents[a].charAt(0)=='-') {
				MacToolbarSeparatorUI.paintSeparator(g,sizes[a].width,sizes[a].height);
			} else {
				components[a].paint(g);
			}
			
			g.translate(sizes[a].width, 0);
			g.translate(insets.right, 0);
			g.translate(0, -dy);
		}
		
		g.dispose();
		return bi;
	}
}
