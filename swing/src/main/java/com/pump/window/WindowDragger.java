/*
 * @(#)WindowDragger.java
 *
 * $Date: 2015-02-28 15:59:45 -0500 (Sat, 28 Feb 2015) $
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
package com.pump.window;

import java.awt.Component;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import com.pump.blog.Blurb;
import com.pump.util.JVM;

/** This listens to drag events and drags a Component's window as the user
 * drags the mouse.
 * <P>This is especially convenient for floating palettes, but it also
 * can be used for frames, too.
 * <P>In Mac OS 10.5 (Java 1.5) there is a system property
 * discussed <A HREF="http://developer.apple.com/technotes/tn2007/tn2196.html">here</A>
 * that achieves about the same goal.  To my knowledge they are the same in
 * function, but I haven't explored the subject in depth.
 */
@Blurb (
filename = "WindowDragger",
title = "Windows: Dragging Made Easy",
releaseDate = "April 2007",
summary = "Sometimes you want to be able to click and drag a window, palette, dialog etc.",
link = "http://javagraphics.blogspot.com/2007/04/windows-dragging-made-easy.html",
sandboxDemo = false
)
public class WindowDragger extends MouseInputAdapter {	
	Point mouseLoc;
	boolean dragging;
	boolean active;
	
	@Override
	public void mousePressed(MouseEvent e) {
		mouseLoc = e.getPoint();
		dragging = true;
		SwingUtilities.convertPointToScreen(mouseLoc,(Component)e.getSource());
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		dragging = false;
		mouseLoc = null;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if(mouseLoc==null || dragging==false) {
			return;
		}
		synchronized(mouseLoc) {
			Point p = e.getPoint();
			SwingUtilities.convertPointToScreen(p,(Component)e.getSource());
			if(JVM.isMac) p.y = Math.max(0,p.y);
			if(active) {
				WindowDragger.translateWindow(p.x-mouseLoc.x,
						p.y-mouseLoc.y, 
						SwingUtilities.getWindowAncestor((Component)e.getSource()));
			}
			mouseLoc.setLocation(p);
		}
	}
		

	public WindowDragger() {
		
	}
	public WindowDragger(Component c) {
		this(new Component[] {c});
	}
	
	public WindowDragger(Component[] c) {
		for(int a = 0; a<c.length; a++) {
			c[a].addMouseListener(this);
			c[a].addMouseMotionListener(this);
		}
	}
		
	/** Translates a window, after possibly adjusting dx and dy for
	 * OS-based restraints.
	 */
	protected static void translateWindow(int dx,int dy,Window window) {
		Point p = window.getLocation();
		p.x+=dx;
		p.y+=dy;
		if(JVM.isMac) p.y = Math.max(0,p.y);
		window.setLocation(p);
	}
	
	public void setActive(boolean b) {
		active = b;
	}
	
	public boolean isActive() {
		return active;
	}
}
