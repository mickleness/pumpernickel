/*
 * @(#)TexturePaintPanelUI.java
 *
 * $Date: 2014-05-04 12:08:30 -0400 (Sun, 04 May 2014) $
 *
 * Copyright (c) 2014 by Jeremy Wood.
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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.plaf.PanelUI;

/** A <code>PanelUI</code> that paints a <code>TexturePaint</code>
 * in the background.
 */
public class TexturePaintPanelUI extends PanelUI {
	Paint paint;
	
	public TexturePaintPanelUI(BufferedImage bi) {
		this(new TexturePaint(bi, new Rectangle(0,0,bi.getWidth(),bi.getHeight())));
	}
	
	public TexturePaintPanelUI(TexturePaint p) {
		if(p==null) throw new NullPointerException();
		paint = p;
	}
	
	@Override
	public void paint(Graphics g, JComponent c) {
		Graphics2D g2 = (Graphics2D)g.create();
		try {
			g2.setPaint(paint);
			g2.fillRect(0,0,c.getWidth(),c.getHeight());
		} finally {
			g2.dispose();
		}
	}
}
