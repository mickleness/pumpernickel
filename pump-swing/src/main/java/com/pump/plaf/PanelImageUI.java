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