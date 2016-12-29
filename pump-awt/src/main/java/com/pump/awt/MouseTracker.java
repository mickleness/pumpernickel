/*
 * @(#)MouseTracker.java
 *
 * $Date: 2016-01-30 19:07:08 -0500 (Sat, 30 Jan 2016) $
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
package com.pump.awt;

import java.awt.AWTEvent;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/** Tracks the mouse location and button state. */
public class MouseTracker {
	private static Point mouseLoc = new Point();
	private static Map<Integer, Boolean> pressedButtons = new HashMap<>();
	static {
		AWTEventListener listener = new AWTEventListener() {
			public void eventDispatched(AWTEvent e) {
				if(e instanceof MouseEvent) {
					MouseEvent k = (MouseEvent)e;
					boolean change = false;
					
					int oldX = mouseLoc.x;
					int oldY = mouseLoc.y;
					synchronized(mouseLoc) {
						mouseLoc.x = k.getX();
						mouseLoc.y = k.getY();
						SwingUtilities.convertPointToScreen(mouseLoc, k.getComponent());
					}
					if(oldX!=mouseLoc.x || oldY!=mouseLoc.y)
						change = true;
					
					if(k.getID()==MouseEvent.MOUSE_PRESSED) {
						Integer key = new Integer(k.getButton());
						Boolean state = pressedButtons.get(key);
						if(state==null) state = Boolean.FALSE;
						if(state.equals(Boolean.FALSE)) {
							pressedButtons.put(key,Boolean.TRUE);
							change = true;
						}
					} else if(k.getID()==MouseEvent.MOUSE_MOVED && k.getButton()==0) {
						/** This is a general precaution, and is especially
						 * essential for drag and drop gestures.
						 * 
						 * We never receive a MOUSE_RELEASED event if a drag
						 * and drop gesture is initiated, which can leave the
						 * mistaken impressed that a button is pressed when it
						 * really isn't.  But as soon as a MOUSE_MOVED event comes
						 * along we can clear the pressed buttons and be OK.
						 * 
						 * This means we'll be inaccurate when a drag and
						 * drop gesture ends only until the user moves the mouse
						 * again.  It's not perfect, but it's acceptable.
						 */
						Iterator<Boolean> values = pressedButtons.values().iterator();
						while(values.hasNext()) {
							if(Boolean.TRUE.equals(values.next()))
								change = true;
						}
						pressedButtons.clear();
					} else if(k.getID()==MouseEvent.MOUSE_RELEASED) {
						Integer key = new Integer(k.getButton());
						Boolean state = pressedButtons.get(key);
						if(state==null) state = Boolean.FALSE;
						if(state.equals(Boolean.TRUE)) {
							pressedButtons.put(key,Boolean.FALSE);
							change = true;
						}
					}
					
					if(change)
						fireChangeListeners();
				}
			}
		};
		Toolkit.getDefaultToolkit().addAWTEventListener(listener,
				AWTEvent.MOUSE_MOTION_EVENT_MASK);
		Toolkit.getDefaultToolkit().addAWTEventListener(listener,
				AWTEvent.MOUSE_EVENT_MASK);
	}
	
	/** Return true if the specified button is pressed.
	 * 
	 * @param buttonMask a button mask, such as MouseEvent.BUTTON1
	 * @return true if that button is pressed
	 */
	public synchronized static boolean isButtonPressed(int buttonMask) {
		Integer key = new Integer(buttonMask);
		Boolean b = pressedButtons.get(key);
		if(b==null) return false;
		return b.booleanValue();
	}
	
	/** @return true if any button is pressed.
	 */
	public synchronized static boolean isButtonPressed() {
		Iterator<Integer> e = pressedButtons.keySet().iterator();
		while(e.hasNext()) {
			Boolean b = pressedButtons.get(e.next());
			if(b.equals(Boolean.TRUE)) {
				return true;
			}
		}
		return false;
	}
	
	/** @return the x-coordinate of the mouse location. */
	public static int getX() {
		synchronized(mouseLoc) {
			return mouseLoc.x;
		}
	}

	/** @return the y-coordinate of the mouse location. */
	public static int getY() {
		synchronized(mouseLoc) {
			return mouseLoc.y;
		}
	}
	
	private static List<ChangeListener> listeners;
	
	public static void addChangeListener(ChangeListener l) {
		if(listeners==null)
			listeners = new ArrayList<ChangeListener>();
		listeners.add(l);
	}
	
	public static void removeChangeListener(ChangeListener l) {
		if(listeners==null) return;
		listeners.remove(l);
	}
	
	private static void fireChangeListeners() {
		if(listeners==null) return;
		for(int a = 0; a<listeners.size(); a++) {
			ChangeListener l = listeners.get(a);
			try {
				l.stateChanged(new ChangeEvent(MouseTracker.class));
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
