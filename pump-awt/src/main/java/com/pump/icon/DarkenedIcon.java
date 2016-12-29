/*
 * @(#)DarkenedIcon.java
 *
 * $Date: 2015-07-13 13:22:24 -0400 (Mon, 13 Jul 2015) $
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.AbstractButton;
import javax.swing.Icon;

/** Renders an icon with a darkened tint */
public class DarkenedIcon implements Icon {
	AbstractButton button;
	float f;
	Icon icon;
	BufferedImage img;
	
	public DarkenedIcon(AbstractButton b,float f) {
		this.button = b;
		this.f = f;
	}
	
	public DarkenedIcon(Icon icon,float f) {
		this.icon = icon;
		this.f = f;
	}
	
	private Icon getIcon() {
		if(button!=null)
			return button.getIcon();
		return icon;
	}

	public int getIconHeight() {
		return getIcon().getIconHeight();
	}

	public int getIconWidth() {
		return getIcon().getIconWidth();
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		if(img==null) {
			img = createImage(c);
		}
		g.drawImage(img, x, y, null);
	}

	/**
	 * 
	 * @param c the optional component used to invoke icon.paintIcon().
	 * Unfortunately for some icons (like UIManager.getIcon("IthisnternalFrame.maximizeIcon"))
	 * on Windows: we'll get a NPE if this is null.
	 * 
	 * @return
	 */
	private BufferedImage createImage(Component c) {
		Icon i = getIcon();
		BufferedImage bi = new BufferedImage(i.getIconWidth(), i.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		i.paintIcon(c, g, 0, 0);
		g.dispose();
		
		int[] row = new int[bi.getWidth()];
		float[] hsbvals = new float[3];
		for(int y = 0; y<bi.getHeight(); y++) {
			bi.getRaster().getDataElements(0, y, bi.getWidth(), 1, row);
			for(int x = 0; x<bi.getWidth(); x++) {
				int red = (row[x] & 0x00ff0000) >> 16;
				int green = (row[x] & 0x00ff00) >> 8;
				int blue = (row[x] & 0x00ff) >> 0;
				Color.RGBtoHSB(red, green, blue, hsbvals);
				hsbvals[2] = hsbvals[2]*(1-f);
				int k = Color.HSBtoRGB(hsbvals[0], hsbvals[1], hsbvals[2]);
				k = (k & 0xffffff) + (row[x] & 0xff000000);
				row[x] = k;
			}
			bi.getRaster().setDataElements(0, y, bi.getWidth(), 1, row);
		}
		return bi;
	}

}
