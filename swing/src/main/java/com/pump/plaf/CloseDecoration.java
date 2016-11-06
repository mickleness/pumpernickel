/*
 * @(#)CloseDecoration.java
 *
 * $Date: 2014-11-27 01:55:25 -0500 (Thu, 27 Nov 2014) $
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

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JList;

import com.pump.icon.CloseIcon;
import com.pump.plaf.DecoratedListUI.ListDecoration;
import com.pump.swing.JFancyBox;
import com.pump.swing.JFancyBox.FancyCloseIcon;

public abstract class CloseDecoration extends ListDecoration {
	Icon normalIcon;
	Icon pressedIcon;
	
	public CloseDecoration() {
		normalIcon = new JFancyBox.FancyCloseIcon();
		pressedIcon = new JFancyBox.FancyCloseIcon();
		((FancyCloseIcon)pressedIcon).setXColor(Color.gray);
		((FancyCloseIcon)pressedIcon).setBorderColor(Color.lightGray);
	}
	
	public CloseDecoration(int size) {
		normalIcon = new CloseIcon(size);
		pressedIcon = new CloseIcon(size);
	}

	@Override
	public boolean isVisible(JList list, Object value, int row,
			boolean isSelected, boolean cellHasFocus) {
		return isSelected;
	}

	@Override
	public Point getLocation(JList list, Object value, int row,
			boolean isSelected, boolean cellHasFocus) {
		Rectangle r = list.getCellBounds(row, row);
		return new Point(r.width - normalIcon.getIconWidth()-2, 2);
	}

	@Override
	public Icon getIcon(JList list, Object value, int row,
			boolean isSelected, boolean cellHasFocus, boolean isRollover,
			boolean isPressed) {
		if(isPressed) {
			return pressedIcon;
		}
		return normalIcon;
	}
}
