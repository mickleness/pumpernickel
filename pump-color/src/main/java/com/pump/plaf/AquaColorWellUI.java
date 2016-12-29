/*
 * @(#)AquaColorWellUI.java
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;

public class AquaColorWellUI extends ColorWellUI {

	public static ComponentUI createUI(JComponent jc) {
		return new AquaColorWellUI();
	}
	
	@Override
	public Dimension getMaximumSize(JComponent c) {
		Border border = c.getBorder();
		Insets i = border.getBorderInsets(c);
		return new Dimension(40+i.left+i.right,40+i.top+i.bottom);
	}

	@Override
	public Dimension getMinimumSize(JComponent c) {
		Border border = c.getBorder();
		Insets i = border.getBorderInsets(c);
		return new Dimension(11+i.left+i.right,11+i.top+i.bottom);
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		Border border = c.getBorder();
		Insets i = border.getBorderInsets(c);
		return new Dimension(49+i.left+i.right,11+i.top+i.bottom);
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.setBorder(new AquaColorWellBorder());
	}
	
	static class AquaColorWellBorder implements Border {

		public Insets getBorderInsets(Component c) {
			return new Insets(6,6,6,6);
		}

		public boolean isBorderOpaque() {
			return true;
		}
		
		static Color[] colors = new Color[] {
			new Color(0xF1F1F1), //fill
			new Color(0x8A8A8A), //border
			new Color(0x08000000,true), //bottom shade #1
			new Color(0x5FFFFFFF,true), //bottom shade #2
			new Color(0x8FFFFFFF,true), //top shade #1
			new Color(0x0D000000,true), //top shade #2
			new Color(0x575757), //border (selected)
			new Color(0x1FFFFFFF,true), //bottom shade #2 (selected)
			new Color(0x2FFFFFFF,true) //top shade #1 (selected)
		};
		
		static Color[] normalGradient = new Color[] {new Color(0x00000000,true), 
			new Color(0x0A000000,true), 
			new Color(0x00000000,true)};
		
		static Color[] selectedGradient = new Color[] {new Color(0x20000000,true), 
			new Color(0x4A000000,true), 
			new Color(0x1A000000,true)};

		static Color[] selectedFillGradient = new Color[] {new Color(0x7F7F7F), 
			new Color(0x616161), 
			new Color(0xA5A5A5)};

		public void paintBorder(Component c, Graphics g0, int x, int y,
				int width, int height) {
			
			Graphics2D g = (Graphics2D)g0;
			
			boolean selected = c.hasFocus();
			if(c instanceof JComponent) {
				JComponent jc = (JComponent)c;
				Boolean pressed = (Boolean)jc.getClientProperty("mousePressed");
				if(pressed==null) pressed = Boolean.FALSE;
				if(pressed.booleanValue())
					selected = true;
			}
			
			GeneralPath body = new GeneralPath(Path2D.WIND_EVEN_ODD);
			body.moveTo(x, y);
			body.lineTo(x+width,y);
			body.lineTo(x+width,y+height);
			body.lineTo(x,y+height);
			body.closePath();
			body.moveTo(x+5, y+5);
			body.lineTo(x+width-5,y+5);
			body.lineTo(x+width-5,y+height-5);
			body.lineTo(x+5,y+height-5);
			body.closePath();
			
			if(!selected) {
				g.setColor( colors[0] );
			} else {
				Paint paint = PlafPaintUtils.getVerticalGradient("aquaSelectedColorWellFill", height, y, 
						new float[] {0, .1f, 1}, 
						selectedFillGradient );
				g.setPaint(paint);
			}
			g.fill(body);
			
			if(!selected) {
				g.setColor( colors[1] );
			} else {
				g.setColor( colors[6] );
			}
			g.drawLine(x,y,x+width-1,y);
			g.drawLine(x,y+height-1,x+width-1,y+height-1);
			g.drawLine(x, y, x, y+height-1);
			g.drawLine(x+width-1, y, x+width-1, y+height-1);

			g.drawLine(x+5,y+5,x+width-1-5,y+5);
			g.drawLine(x+5,y+height-1-5,x+width-1-5,y+height-1-5);
			g.drawLine(x+5, y+5, x+5, y+height-1-5);
			g.drawLine(x+width-1-5, y+5, x+width-1-5, y+height-1-5);
			
			//bottom shade:
			g.setColor( colors[2] );
			g.drawLine(x+1, y+height-2, x+width-2, y+height-2);
			if(!selected) {
				g.setColor( colors[3] );
			} else {
				g.setColor( colors[8] );
			}
			g.drawLine(x+1, y+height-3, x+width-2, y+height-3);

			//top shade:
			if(!selected) {
				g.setColor( colors[4] );
			} else {
				g.setColor( colors[8] );
			}
			g.drawLine(x+1, y+1, x+width-2, y+1);
			g.setColor( colors[5] );
			g.drawLine(x+1, y+3, x+width-2, y+3);
			
			//side shades:
			if(!selected) {
				g.setPaint( PlafPaintUtils.getVerticalGradient("aquaColorWell", height, y, 
						new float[] {0, .1f, 1}, 
						normalGradient ));
			} else {
				g.setPaint( PlafPaintUtils.getVerticalGradient("aquaSelectedColorWell", height, y, 
						new float[] {0, .1f, 1}, 
						selectedGradient ));
			}
			g.drawLine(x+1, y+1, x+1, y+height-2);
			g.drawLine(x+width-2, y+1, x+width-2, y+height-2);
		}
		
	}
}
