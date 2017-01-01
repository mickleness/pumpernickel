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