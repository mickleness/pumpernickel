/*
 * @(#)PanelImageUI.java
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

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.plaf.PanelUI;

public class PanelImageUI extends PanelUI {
	BufferedImage img;
	float opacity;
	
	public PanelImageUI(BufferedImage img) {
		this(img, 1);
	}

	public PanelImageUI(BufferedImage img,float opacity) {
		this.img = img;
		this.opacity = opacity;
	}
	
	@Override
	public Dimension getPreferredSize(JComponent c) {
		return new Dimension(img.getWidth(), img.getHeight());
	}

	@Override
	public void paint(Graphics g0, JComponent c) {
		super.paint(g0, c);
		
		Graphics2D g = (Graphics2D)g0;
		g = (Graphics2D)g.create();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		g.drawImage(img, 
				c.getWidth()/2-img.getWidth()/2, 
				c.getHeight()/2-img.getHeight()/2, 
				null);
		g.dispose();
	}
}
