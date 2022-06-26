/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
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
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import com.pump.util.Warnings;

/**
 * This class lets mouseClicked events be triggered even if the mouse moves a
 * few pixels.
 * <p>
 * By default Java only triggers mouseClicked messages when the mouse doesn't
 * move between mousePressed and mouseReleased. Trackpads and touchpads,
 * however, are less precise: as they grow in numbers, we need to allow a few
 * extra pixels to trigger a mouseClicked event.
 * 
 * @see <a
 *      href="https://javagraphics.blogspot.com/2011/02/mouse-click-events-adding-wiggle-room.html">Mouse
 *      Click Events: Adding Wiggle Room</a>
 */
public class ClickSensitivityControl implements AWTEventListener {
	/**
	 * The distance between the point where the mouse is pressed and where it is
	 * released that is allowed to constitute a "click".
	 * <p>
	 * Java by default effectively gives you a 0-pixel tolerance. The field is
	 * initialized to 10, but you're welcome to change it as needed.
	 * 
	 */
	public static int DEFAULT_CLICK_EVENT_TOLERANCE = 10;
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

		Key(Component c) {
			button = MouseEvent.BUTTON1;
			modifiers = MouseEvent.BUTTON1_MASK;
			component = new WeakReference<Component>(c);
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Key))
				return false;
			Key k = (Key) obj;
			if (button != k.button)
				return false;
			if (modifiers != k.modifiers)
				return false;
			Component c1 = component.get();
			Component c2 = k.component.get();
			if (c1 == null || c2 == null)
				return false;
			if (c1 != c2)
				return false;
			return true;
		}

		@Override
		public int hashCode() {
			Component c = component.get();
			if (c == null)
				return -1;
			return c.hashCode();
		}

		@Override
		public String toString() {
			return modifiers + " " + button + " " + component.get();
		}
	}

	/**
	 * A runnable that trigger the mouseClicked event.
	 * 
	 */
	private static class TriggerMouseClick implements Runnable {
		MouseEvent mouseEvent;
		Key key;

		public TriggerMouseClick(Key key, MouseEvent mouseEvent) {
			this.mouseEvent = mouseEvent;
			this.key = key;
		}

		public void run() {
			if (clickLocs.containsKey(key) == false)
				return;
			clickLocs.remove(key);
			MouseEvent newEvent = new MouseEvent(mouseEvent.getComponent(),
					MouseEvent.MOUSE_CLICKED, mouseEvent.getWhen(),
					mouseEvent.getModifiers(), mouseEvent.getX(),
					mouseEvent.getY(), 1, // click count
					false, // popup trigger
					mouseEvent.getButton());

			try {
				Toolkit.getDefaultToolkit().getSystemEventQueue()
						.postEvent(newEvent);
			} catch (SecurityException se) {
				Warnings.printOnce(se);
			}
		}
	}

	public synchronized static void install() {
		install(DEFAULT_CLICK_EVENT_TOLERANCE);
	}

	public synchronized static void install(int clickPixelTolerance) {
		ClickSensitivityControl csc = new ClickSensitivityControl(
				clickPixelTolerance);
		Toolkit.getDefaultToolkit().addAWTEventListener(csc,
				AWTEvent.MOUSE_EVENT_MASK + AWTEvent.MOUSE_MOTION_EVENT_MASK);
	}

	int clickPixelTolerance;

	protected ClickSensitivityControl(int clickPixelTolerance) {
		setClickPixelTolerance(clickPixelTolerance);
	}

	public void setClickPixelTolerance(int clickPixelTolerance) {
		this.clickPixelTolerance = clickPixelTolerance;
	}

	public int getClickPixelTolerance(Component c) {
		return clickPixelTolerance;
	}

	@Override
	public void eventDispatched(AWTEvent e) {
		MouseEvent m = (MouseEvent) e;
		if (m.getID() == MouseEvent.MOUSE_CLICKED) {
			mouseClicked(m);
		} else if (m.getID() == MouseEvent.MOUSE_DRAGGED) {
			mouseDragged(m);
		} else if (m.getID() == MouseEvent.MOUSE_ENTERED) {
			mouseEntered(m);
		} else if (m.getID() == MouseEvent.MOUSE_EXITED) {
			mouseExited(m);
		} else if (m.getID() == MouseEvent.MOUSE_MOVED) {
			mouseMoved(m);
		} else if (m.getID() == MouseEvent.MOUSE_PRESSED) {
			mousePressed(m);
		} else if (m.getID() == MouseEvent.MOUSE_RELEASED) {
			mouseReleased(m);
		}
	}

	protected void mouseDragged(MouseEvent e) {
		Key key = new Key(e);
		Point clickLoc = clickLocs.get(key);
		if (clickLoc == null)
			return;

		Point releaseLoc = e.getPoint();
		double distance = releaseLoc.distance(clickLoc);

		if (distance > getClickPixelTolerance(e.getComponent())) {
			clickLocs.remove(key);
			return;
		}
	}

	protected void mouseClicked(MouseEvent e) {
		Key key = new Key(e);
		clickLocs.remove(key);
	}

	protected void mousePressed(MouseEvent e) {
		Key key = new Key(e);
		clickLocs.put(key, e.getPoint());
	}

	protected void mouseReleased(MouseEvent e) {
		Key key = new Key(e);
		Point clickLoc = clickLocs.get(key);
		if (clickLoc == null)
			return;

		Point releaseLoc = e.getPoint();
		double distance = releaseLoc.distance(clickLoc);

		if (distance > getClickPixelTolerance(e.getComponent())) {
			clickLocs.remove(key);
			return;
		}

		SwingUtilities.invokeLater(new TriggerMouseClick(key, e));
	}

	protected void mouseEntered(MouseEvent e) {
	}

	protected void mouseExited(MouseEvent e) {
	}

	protected void mouseMoved(MouseEvent e) {
	}

	public boolean isClick(Component c) {
		Key key = new Key(c);
		return clickLocs.containsKey(key);
	}
}