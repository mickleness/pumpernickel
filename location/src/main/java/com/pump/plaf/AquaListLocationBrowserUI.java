/*
 * @(#)AquaListLocationBrowserUI.java
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
import java.awt.Insets;
import java.awt.SystemColor;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.pump.icon.PaddedIcon;
import com.pump.io.location.IOLocation;
import com.pump.swing.io.GraphicCache;
import com.pump.swing.io.LocationBrowser;

public class AquaListLocationBrowserUI extends ListLocationBrowserUI {

	public AquaListLocationBrowserUI(LocationBrowser b) {
		super(b);
	}

	@Override
	public TableCellRenderer getTableCellRenderer() {
		return new AquaTableCellRenderer(browser.getGraphicCache());
	}
}

//TODO: put in separate file
class AquaTableCellRenderer implements TableCellRenderer {
	JLabel label = new JLabel();
	Color background = Color.white;
	Color altBackground = new Color(0xEDF3FE);
	Color foreground = Color.black;
	Insets iconPadding = new Insets(0,10,0,4);
	GraphicCache graphicCache;
	
	public AquaTableCellRenderer(GraphicCache gc) {
		graphicCache = gc;
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		String text = "";
		Icon icon = null;
		
		if(value instanceof IOLocation) {
			IOLocation l = (IOLocation)value;
			text = l.getName();
			icon = graphicCache.requestIcon(l);
			if(icon==null) {
				if(l.isDirectory()) {
					icon = IOLocation.FOLDER_ICON;
				} else {
					icon = IOLocation.FILE_ICON;
				}
			}
			icon = new PaddedIcon( icon, iconPadding);
		} else {
			text = value.toString();
		}
		
		label.setIcon(icon);
		label.setText(text);
		if(isSelected) {
			label.setForeground(SystemColor.controlLtHighlight);
			label.setBackground(SystemColor.controlHighlight);
		} else {
			label.setForeground(foreground);
			if(row%2==0) {
				label.setBackground(background);
			} else {
				label.setBackground(altBackground);
			}
		}
		label.setOpaque(true);
		
		return label;
	}	
}
