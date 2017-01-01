/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://raw.githubusercontent.com/mickleness/pumpernickel/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
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