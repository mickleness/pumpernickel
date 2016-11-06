/*
 * @(#)IOLabelCellRenderer.java
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

import com.pump.icon.PaddedIcon;
import com.pump.io.location.IOLocation;
import com.pump.swing.io.GraphicCache;

public class IOLabelCellRenderer extends LabelCellRenderer {
	final JComboBox comboBox;
	final GraphicCache graphicCache;

	public IOLabelCellRenderer(JComboBox jc,GraphicCache graphicsCache) {
		this.comboBox = jc;
		this.graphicCache = graphicsCache;
		graphicCache.addPropertyChangeListener(new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) {
				SwingUtilities.invokeLater( new RepaintRunnable(evt) );
			}
		});
	}
	
	class RepaintRunnable implements Runnable {
		PropertyChangeEvent evt;
		
		public RepaintRunnable(PropertyChangeEvent e) {
			evt = e;
		}
		
		public void run() {
			if(evt.getPropertyName().equals(GraphicCache.ICON_PROPERTY)) {
				IOLocation loc = (IOLocation)evt.getSource();
				for(int a = 0; a<comboBox.getItemCount(); a++) {
					if(comboBox.getItemAt(a).equals(loc)) {
						comboBox.repaint();
						return;
					}
				}
			}
		}
	}
	
	@Override
	protected void formatLabel(Object value) {
		String text;
		Icon icon = null;
		if(value instanceof IOLocation) {
			IOLocation l = (IOLocation)value;
			text = l.getName();

			icon = graphicCache.requestIcon(l);
		} else if(value!=null) {
			text = value.toString();
		} else {
			text = "";
		}
		if(icon==null) {
			icon = IOLocation.FOLDER_ICON;
		}
		
		icon = new PaddedIcon( icon, iconPadding);
		
		label.setIcon(icon);
		label.setText(text);
	}
	
}
