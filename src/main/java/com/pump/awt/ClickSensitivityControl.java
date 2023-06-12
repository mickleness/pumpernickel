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

import javax.swing.*;

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
public class ClickSensitivityControl {

	/**
	 * This client property on a JComponent resolves to a Boolean indicating whether this class should
	 * simulate MOUSE_CLICKED events for this component.
	 */
	public static final String PROPERTY_ENABLED = ClickSensitivityControl.class.getName() + "#enabled";

	private static final ClickSensitivityControl globalInstance = new ClickSensitivityControl();


	/**
	 * The distance between the point where the mouse is pressed and where it is
	 * released that is allowed to constitute a "click".
	 * <p>
	 * Java by default effectively gives you a 0-pixel tolerance.
	 *
	 */
	public static final int DEFAULT_CLICK_EVENT_TOLERANCE = 10;

	public static ClickSensitivityControl get() {
		return globalInstance;
	}

	public static void install() {
		// intentionally empty; this will statically install itself if the class loads.
	}


	private final static Map<ClickKey, Point> clickMap = new HashMap<>();

	private static class ClickKey {
		int button;
		int modifiers;
		WeakReference<Component> component;

		ClickKey(MouseEvent m) {
			button = m.getButton();
			modifiers = m.getModifiers();
			component = new WeakReference<>(m.getComponent());
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ClickKey))
				return false;
			ClickKey k = (ClickKey) obj;
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
	 * A runnable that trigger the MOUSE_CLICKED event.
	 */
	private static class TriggerMouseClick implements Runnable {
		MouseEvent mouseEvent;
		ClickKey key;

		public TriggerMouseClick(ClickKey key, MouseEvent mouseEvent) {
			this.mouseEvent = mouseEvent;
			this.key = key;
		}

		public void run() {
			if (clickMap.containsKey(key) == false)
				return;
			clickMap.remove(key);
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

	int clickPixelTolerance = DEFAULT_CLICK_EVENT_TOLERANCE;

	AWTEventListener listener = new AWTEventListener() {
		@Override
		public void eventDispatched(AWTEvent event) {
			MouseEvent m = (MouseEvent) event;
			if (m.getID() == MouseEvent.MOUSE_CLICKED) {
				mouseClicked(m);
			} else if (m.getID() == MouseEvent.MOUSE_DRAGGED) {
				mouseDragged(m);
			} else if (m.getID() == MouseEvent.MOUSE_PRESSED) {
				mousePressed(m);
			} else if (m.getID() == MouseEvent.MOUSE_RELEASED) {
				mouseReleased(m);
			}
		}

		protected void mouseDragged(MouseEvent e) {
			ClickKey key = new ClickKey(e);
			Point clickLoc = clickMap.get(key);
			if (clickLoc == null)
				return;

			Point releaseLoc = e.getPoint();
			double distance = releaseLoc.distance(clickLoc);

			if (distance > getClickPixelTolerance())
				clickMap.remove(key);
		}

		protected void mouseClicked(MouseEvent e) {
			ClickKey key = new ClickKey(e);
			clickMap.remove(key);
		}

		protected void mousePressed(MouseEvent e) {
			if (e.getComponent() instanceof JComponent) {
				JComponent jc = (JComponent) e.getComponent();
				Boolean b = (Boolean) jc.getClientProperty(PROPERTY_ENABLED);
				if (Boolean.FALSE.equals(b))
					return;
			}

			ClickKey key = new ClickKey(e);
			clickMap.put(key, e.getPoint());
		}

		protected void mouseReleased(MouseEvent e) {
			ClickKey key = new ClickKey(e);
			Point clickLoc = clickMap.get(key);
			if (clickLoc == null)
				return;

			Point releaseLoc = e.getPoint();
			double distance = releaseLoc.distance(clickLoc);

			if (distance > getClickPixelTolerance()) {
				clickMap.remove(key);
				return;
			}

			SwingUtilities.invokeLater(new TriggerMouseClick(key, e));
		}
	};

	public ClickSensitivityControl() {
		Toolkit.getDefaultToolkit().addAWTEventListener(listener,
				AWTEvent.MOUSE_EVENT_MASK + AWTEvent.MOUSE_MOTION_EVENT_MASK);
	}

	public void setClickPixelTolerance(int clickPixelTolerance) {
		this.clickPixelTolerance = clickPixelTolerance;
	}

	public int getClickPixelTolerance() {
		return clickPixelTolerance;
	}
}