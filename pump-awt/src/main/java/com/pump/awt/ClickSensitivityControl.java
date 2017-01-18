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
package com.pump.awt;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import com.pump.blog.Blurb;


/** This class lets mouseClicked events be triggered even if the
 * mouse moves a few pixels.
 * <p>By default Java only triggers mouseClicked messages when the
 * mouse doesn't move between mousePressed and mouseReleased.
 * Trackpads and touchpads, however, are less precise: as they grow
 * in numbers, we need to allow a few extra pixels to trigger
 * a mouseClicked event.
 */
@Blurb (
title = "Mouse Click Events: Adding Wiggle Room",
releaseDate = "February 2011",
summary = "This triggers mouseClicked events even if the mouse moves a few pixels between click and release.",
article = "http://javagraphics.blogspot.com/2011/02/mouse-click-events-adding-wiggle-room.html")
public class ClickSensitivityControl {
	/** The distance between the point where the mouse is
	 * pressed and where it is released that is allowed to
	 * constitute a "click".
	 * <p>Java by default effectively gives you a 0-pixel
	 * tolerance.  The field is initialized to 10, but you're
	 * welcome to change it as needed.
	 * 
	 */
	public static double CLICK_EVENT_TOLERANCE = 10;
	private static Map<Key, Point> clickLocs = new HashMap<Key, Point>();
	
	private static class Key {
		int button;
		int modifiers;
		WeakReference<Component> component;
		Key(MouseEvent m) {
			button = m.getButton();
			modifiers = m.getModifiers();
			component = new WeakReference<Component>(m.getComponent());
		}
		
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof Key))
				return false;
			Key k = (Key)obj;
			if(button!=k.button);
			if(modifiers!=k.modifiers);
			Component c1 = component.get();
			Component c2 = k.component.get();
			if(c1==null || c2==null) return false;
			return c1==c2;
		}
		
		@Override
		public int hashCode() {
			Component c = component.get();
			if(c==null) return -1;
			return c.hashCode();
		}
	}
	
	public static boolean printSecurityExceptionOnlyOnce = true;
	private static boolean seenSecurityException = false;
	
	/** A runnable that trigger the mouseClicked event.
	 * 
	 */
	private static class TriggerMouseClick implements Runnable {
		MouseEvent mouseEvent;
		Key key;
		
		public TriggerMouseClick(Key key,MouseEvent mouseEvent) {
			this.mouseEvent = mouseEvent;
			this.key = key;
		}
		
		public void run() {
			if(clickLocs.containsKey(key)==false)
				return;
			clickLocs.remove(key);
			MouseEvent newEvent = new MouseEvent(
					mouseEvent.getComponent(), 
					MouseEvent.MOUSE_CLICKED, 
					mouseEvent.getWhen(), 
					mouseEvent.getModifiers(),
					mouseEvent.getX(),
					mouseEvent.getY(), 
					1, //click count
					false, //popup trigger
					mouseEvent.getButton());
					
			try {
				Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(newEvent);
			} catch(SecurityException se) {
				if(printSecurityExceptionOnlyOnce && seenSecurityException)
					return;
				seenSecurityException = true;
				se.printStackTrace();
			}
		}
	}
	
	static class ClickMonitor implements AWTEventListener, MouseListener, MouseMotionListener {

		public void eventDispatched(AWTEvent e) {
			//we should only hear MouseEvents, but this is
			//top-level stuff, so let's not risk any exceptions:
			if(!(e instanceof MouseEvent))
				return;
			MouseEvent m = (MouseEvent)e;
			if(m.getID()==MouseEvent.MOUSE_CLICKED) {
				mouseClicked(m);
			} else if(m.getID()==MouseEvent.MOUSE_DRAGGED) {
				mouseDragged(m);
			} else if(m.getID()==MouseEvent.MOUSE_ENTERED) {
				mouseEntered(m);
			} else if(m.getID()==MouseEvent.MOUSE_EXITED) {
				mouseExited(m);
			} else if(m.getID()==MouseEvent.MOUSE_MOVED) {
				mouseMoved(m);
			} else if(m.getID()==MouseEvent.MOUSE_PRESSED) {
				mousePressed(m);
			} else if(m.getID()==MouseEvent.MOUSE_RELEASED) {
				mouseReleased(m);
			}
		}

		public void mouseDragged(MouseEvent e) {
			Key key = new Key(e);
			Point clickLoc = clickLocs.get(key);
			if(clickLoc==null) return;
			
			Point releaseLoc = e.getPoint();
			double distance = releaseLoc.distance(clickLoc);
			
			if(distance>CLICK_EVENT_TOLERANCE) {
				clickLocs.remove(key);
				return;
			}
		}

		public void mouseClicked(MouseEvent e) {
			Key key = new Key(e);
			clickLocs.remove(key);
		}

		public void mousePressed(MouseEvent e) {
			Key key = new Key(e);
			clickLocs.put(key, e.getPoint());
		}

		public void mouseReleased(MouseEvent e) {
			Key key = new Key(e);
			Point clickLoc = clickLocs.get(key);
			if(clickLoc==null) return;
			
			Point releaseLoc = e.getPoint();
			double distance = releaseLoc.distance(clickLoc);
			
			if(distance>CLICK_EVENT_TOLERANCE) {
				clickLocs.remove(key);
				return;
			}
			
			SwingUtilities.invokeLater(new TriggerMouseClick(key, e));
		}

		public void mouseEntered(MouseEvent e) {}

		public void mouseExited(MouseEvent e) {}

		public void mouseMoved(MouseEvent e) {}
	}
	
	private static Boolean installResult = null;
	public synchronized static boolean install() {
		if(installResult!=null)
			return installResult.booleanValue();
		try {
			Toolkit.getDefaultToolkit().addAWTEventListener(new ClickMonitor(), AWTEvent.MOUSE_EVENT_MASK+AWTEvent.MOUSE_MOTION_EVENT_MASK);
			installResult = Boolean.TRUE;
			return installResult.booleanValue();
		} finally {
			if(installResult==null)
				installResult = Boolean.FALSE;
		}
	}
}