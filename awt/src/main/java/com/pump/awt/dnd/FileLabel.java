/*
 * @(#)FileLabel.java
 *
 * $Date: 2015-12-26 20:42:44 -0600 (Sat, 26 Dec 2015) $
 *
 * Copyright (c) 2015 by Jeremy Wood.
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
package com.pump.awt.dnd;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JLabel;

import com.pump.icon.FileIcon;

public class FileLabel extends JLabel {
	private static final long serialVersionUID = 1L;


	/**
	 * @param file a file this label should represent.
	 * @param actions one of the DnDConstants.ACTION_X constants
	 */
	public FileLabel(File file,int actions) {
		this(actions);
		setFile(file);
	}
	
	/**
	 * 
	 * @param actions one of the DnDConstants.ACTION_X constants. A good default is COPY_OR_MOVE.
	 */
	public FileLabel(int actions) {
		addPropertyChangeListener(DnDUtils.KEY_FILE, new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				File file = getFile();
				if(file==null) {
					setIcon(null);
					setText("");
				} else {
					setIcon( FileIcon.getIcon(file) );
					setText( file.getName() );
				}
			}
			
		});
		DnDUtils.setupFileDragSource(this, actions);
	}
	
	public File getFile() {
		return (File)getClientProperty(DnDUtils.KEY_FILE);
	}
	
	public void setFile(File file) {
		putClientProperty(DnDUtils.KEY_FILE, file);
	}
}
