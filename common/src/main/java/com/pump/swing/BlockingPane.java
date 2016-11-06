/*
 * @(#)BlockingPane.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2013 by Jeremy Wood.
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
package com.pump.swing;

import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;

public class BlockingPane extends JComponent implements MouseListener, MouseMotionListener {

	private static final long serialVersionUID = 1L;
	
	public BlockingPane() {
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	ComponentListener rootPaneComponentListener = new ComponentListener() {

		public void componentHidden(ComponentEvent e) {}

		public void componentMoved(ComponentEvent e) {}

		public void componentResized(ComponentEvent e) {
			Component c = e.getComponent();
			setBounds(0,0,c.getWidth(),c.getHeight());
		}

		public void componentShown(ComponentEvent e) {}
		
	};
	
	private JRootPane rootPaneInUse = null;
	public void install(RootPaneContainer rpc) {
		if(getParent()!=rpc.getLayeredPane())
			rpc.getLayeredPane().add( this , JLayeredPane.POPUP_LAYER);
		
		if(rootPaneInUse!=null)
			rootPaneInUse.removeComponentListener(rootPaneComponentListener);
		
		rootPaneInUse = rpc.getRootPane();
		setBounds(0,0,rootPaneInUse.getWidth(),rootPaneInUse.getHeight());
		setVisible(true);
		rootPaneInUse.addComponentListener(rootPaneComponentListener);
	}
	
	public void mouseDragged(MouseEvent e) {
		e.consume();
	}
	
	public void mouseMoved(MouseEvent e) {
		e.consume();
	}
	
	public void mouseClicked(MouseEvent e) {
		e.consume();
	}
	
	public void mouseEntered(MouseEvent e) {
		e.consume();
	}
	
	public void mouseExited(MouseEvent e) {
		e.consume();
	}
	
	public void mousePressed(MouseEvent e) {
		e.consume();
	}
	
	public void mouseReleased(MouseEvent e) {
		e.consume();
	}
}
