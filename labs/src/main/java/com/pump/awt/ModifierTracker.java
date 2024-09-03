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
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JApplet;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This keeps track of which modifiers are currently pressed.
 * <P>
 * For example, if you need the cursor to change when the alt key is pressed:
 * just add a listener to this class and you will be notified when appropriate.
 */
public class ModifierTracker {
	/**
	 * Add KeyListeners to everything in an applet.
	 * <P>
	 * Unsigned applets can't simply add an event listener to the toolkit for
	 * security reasons, so this method provides an alternative approach.
	 * <P>
	 * This alternative should work in controlled environments where components
	 * are not added to an applet after this method is called, but in other
	 * cases it might fail.
	 * 
	 * @param applet
	 *            the applet to track
	 */
	public static void track(JApplet applet) {
		if (isActive() == false) {
			track(applet.getContentPane());
		}
	}

	private static KeyListener keyListener = new KeyListener() {
		public void keyTyped(KeyEvent e) {
		}

		public void keyPressed(KeyEvent e) {
			key(e.getKeyCode(), true);
		}

		public void keyReleased(KeyEvent e) {
			key(e.getKeyCode(), false);
		}

		private void key(int code, boolean pressed) {
			if (code == KeyEvent.VK_ALT) {
				if (alt != pressed) {
					alt = pressed;
					fireAltChangeListeners();
				}
			} else if (code == KeyEvent.VK_ALT_GRAPH) {
				if (altGraph != pressed) {
					altGraph = pressed;
					fireAltGraphChangeListeners();
				}
			} else if (code == KeyEvent.VK_CONTROL) {
				if (ctrl != pressed) {
					ctrl = pressed;
					fireControlChangeListeners();
				}
			} else if (code == KeyEvent.VK_META) {
				if (meta != pressed) {
					meta = pressed;
					fireMetaChangeListeners();
				}
			} else if (code == KeyEvent.VK_SHIFT) {
				if (shift != pressed) {
					shift = pressed;
					fireShiftChangeListeners();
				}
			}
		}
	};

	private static void track(Component c) {
		c.addKeyListener(keyListener);

		if (c instanceof Container) {
			Container container = (Container) c;
			for (int a = 0; a < container.getComponentCount(); a++) {
				track(container.getComponent(a));
			}
		}
	}

	private static boolean alt = false;
	private static boolean altGraph = false;
	private static boolean meta = false;
	private static boolean shift = false;
	private static boolean ctrl = false;

	private static boolean securityProblem = false;
	static {
		try {
			Toolkit.getDefaultToolkit().addAWTEventListener(
					new AWTEventListener() {
						public void eventDispatched(AWTEvent e) {
							if (e instanceof KeyEvent) {
								KeyEvent k = (KeyEvent) e;
								boolean pressed = k.getID() == KeyEvent.KEY_PRESSED;
								int code = k.getKeyCode();
								switch (code) {
								case KeyEvent.VK_ALT:
									if (alt != pressed) {
										alt = pressed;
										fireAltChangeListeners();
									}
									break;
								case KeyEvent.VK_ALT_GRAPH:
									if (altGraph != pressed) {
										altGraph = pressed;
										fireAltGraphChangeListeners();
									}
									break;
								case KeyEvent.VK_META:
									if (meta != pressed) {
										meta = pressed;
										fireMetaChangeListeners();
									}
									break;
								case KeyEvent.VK_SHIFT:
									if (shift != pressed) {
										shift = pressed;
										fireShiftChangeListeners();
									}
									break;
								case KeyEvent.VK_CONTROL:
									if (ctrl != pressed) {
										ctrl = pressed;
										fireControlChangeListeners();
									}
									break;
								}
							}
						}
					}, AWTEvent.KEY_EVENT_MASK);
		} catch (SecurityException e) {
			System.err
					.println("the following exception means the ModifierTracker cannot globally listen to modifiers.");
			e.printStackTrace();
			securityProblem = true;
		}
	}

	public static boolean isActive() {
		return !securityProblem;
	}

	private static List<ChangeListener> altListeners;
	private static List<ChangeListener> altGraphListeners;
	private static List<ChangeListener> metaListeners;
	private static List<ChangeListener> shiftListeners;
	private static List<ChangeListener> ctrlListeners;

	public static int getModifiers() {
		return (alt ? InputEvent.ALT_MASK : 0)
				+ (altGraph ? InputEvent.ALT_GRAPH_MASK : 0)
				+ (shift ? InputEvent.SHIFT_MASK : 0)
				+ (ctrl ? InputEvent.CTRL_MASK : 0)
				+ (meta ? InputEvent.META_MASK : 0);

	}

	public static void addAltChangeListener(ChangeListener l) {
		if (altListeners == null)
			altListeners = new ArrayList<>();
		if (altListeners.contains(l))
			return;
		altListeners.add(l);
	}

	public static void addAltGraphChangeListener(ChangeListener l) {
		if (altGraphListeners == null)
			altGraphListeners = new ArrayList<>();
		if (altGraphListeners.contains(l))
			return;
		altGraphListeners.add(l);
	}

	public static void addMetaChangeListener(ChangeListener l) {
		if (metaListeners == null)
			metaListeners = new ArrayList<>();
		if (metaListeners.contains(l))
			return;
		metaListeners.add(l);
	}

	public static void addShiftChangeListener(ChangeListener l) {
		if (shiftListeners == null)
			shiftListeners = new ArrayList<>();
		if (shiftListeners.contains(l))
			return;
		shiftListeners.add(l);
	}

	public static void addControlChangeListener(ChangeListener l) {
		if (ctrlListeners == null)
			ctrlListeners = new ArrayList<>();
		if (ctrlListeners.contains(l))
			return;
		ctrlListeners.add(l);
	}

	public static boolean isAltDown() {
		return alt;
	}

	public static boolean isAltGraphyDown() {
		return altGraph;
	}

	public static boolean isShiftDown() {
		return shift;
	}

	public static boolean isMetaDown() {
		return meta;
	}

	public static boolean isControlDown() {
		return ctrl;
	}

	public static void removeAltChangeListener(ChangeListener l) {
		if (altListeners == null)
			return;
		altListeners.remove(l);
	}

	public static void removeAltGraphChangeListener(ChangeListener l) {
		if (altGraphListeners == null)
			return;
		altGraphListeners.remove(l);
	}

	public static void removeMetaChangeListener(ChangeListener l) {
		if (metaListeners == null)
			return;
		metaListeners.remove(l);
	}

	public static void removeShiftChangeListener(ChangeListener l) {
		if (shiftListeners == null)
			return;
		shiftListeners.remove(l);
	}

	public static void removeControlChangeListener(ChangeListener l) {
		if (ctrlListeners == null)
			return;
		ctrlListeners.remove(l);
	}

	private static void fireAltGraphChangeListeners() {
		if (altGraphListeners == null)
			return;
		for (int a = 0; a < altGraphListeners.size(); a++) {
			ChangeListener l = altGraphListeners.get(a);
			try {
				l.stateChanged(new ChangeEvent(ModifierTracker.class));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void fireAltChangeListeners() {
		if (altListeners == null)
			return;
		for (int a = 0; a < altListeners.size(); a++) {
			ChangeListener l = altListeners.get(a);
			try {
				l.stateChanged(new ChangeEvent(ModifierTracker.class));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void fireControlChangeListeners() {
		if (ctrlListeners == null)
			return;
		for (int a = 0; a < ctrlListeners.size(); a++) {
			ChangeListener l = ctrlListeners.get(a);
			try {
				l.stateChanged(new ChangeEvent(ModifierTracker.class));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void fireMetaChangeListeners() {
		if (metaListeners == null)
			return;
		for (int a = 0; a < metaListeners.size(); a++) {
			ChangeListener l = metaListeners.get(a);
			try {
				l.stateChanged(new ChangeEvent(ModifierTracker.class));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void fireShiftChangeListeners() {
		if (shiftListeners == null)
			return;
		for (int a = 0; a < shiftListeners.size(); a++) {
			ChangeListener l = shiftListeners.get(a);
			try {
				l.stateChanged(new ChangeEvent(ModifierTracker.class));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}