/*
 * @(#)AquaSourceListUI.java
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
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;


/** This {@code ListUI} resembles Apple's "Source List" component.
 * <P>If this UI is being used, it is important to use a {@code ListCellRenderer}
 * that is not opaque.  If it is opaque then it will cover the gradient
 * background.  Also the renderer should highlight the selected rows in a light
 * color (such as white) to contrast against the rich blue highlight that
 * will be behind them.
 * 
 */
public class AquaSourceListUI extends SourceListUI {

	@Override
	protected void paintBackground(Graphics2D g,Rectangle rowBounds, Object value, int row,
			boolean isSelected, boolean cellHasFocus) {

		if(isSelected) {
			Color topColor;
			GradientPaint gradient;
			boolean hasFocus = list.hasFocus();
			boolean isFrameActive = true;
			Object obj = list.getClientProperty("Frame.active");
			if(obj!=null && obj instanceof Boolean)	
				isFrameActive = ((Boolean)obj).booleanValue();
			if(isFrameActive==false) {
				topColor = new Color(0x979797);
				gradient = new GradientPaint(0,rowBounds.y,new Color(0xB5B5B5),
						0,rowBounds.y+rowBounds.height,new Color(0x8A8A8A));
			} else if(hasFocus) {
				topColor = new Color(0x4580C8);
				gradient = new GradientPaint(0,rowBounds.y,new Color(0x5C93D6),
						0,rowBounds.y+rowBounds.height,new Color(0x1553AA));
			} else {
				topColor = new Color(0x91A0C0);
				gradient = new GradientPaint(0,rowBounds.y,new Color(0xA2B1CF),
						0,rowBounds.y+rowBounds.height,new Color(0x6F82AA));
			}
			g.setPaint(gradient);
			g.fillRect(0, rowBounds.y, list.getWidth(), rowBounds.height);
			g.setColor(topColor);
			g.drawLine(0,rowBounds.y, list.getWidth(), rowBounds.y);
		} else {
			g.setPaint(list.getBackground());
			g.fillRect(0, rowBounds.y, list.getWidth(), rowBounds.height);
		}
	}

	@Override
	protected void installListeners() {
		super.installListeners();
		list.addPropertyChangeListener("Frame.active", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				updateBackgroundColor();
			}
		});
	}

	protected void updateBackgroundColor() {
		Object newValue = list.getClientProperty("Frame.active");
		boolean active = true;
		if(newValue instanceof Boolean) {
			Boolean b = (Boolean)newValue;
			if(b.booleanValue()==false) {
				active = false;
			}
		}
		if(active) {
			list.setBackground(new Color(0xD4DDE5));
		} else {
			list.setBackground(new Color(0xE8E8E8));
		}
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		list.setFixedCellHeight(20);
		updateBackgroundColor();
	}
}
