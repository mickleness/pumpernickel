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
package com.pump.window;

import java.awt.Component;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import com.pump.util.JVM;

/**
 * This listens to drag events and drags a Component's window as the user drags
 * the mouse.
 * <P>
 * This is especially convenient for floating palettes, but it also can be used
 * for frames, too.
 * <P>
 * In Mac OS 10.5 (Java 1.5) there is a system property discussed
 * <A HREF="http://developer.apple.com/technotes/tn2007/tn2196.html">here</A>
 * that achieves about the same goal. To my knowledge they are the same in
 * function, but I haven't explored the subject in depth.
 * 
 * @see <a href=
 *      "https://javagraphics.blogspot.com/2007/04/windows-dragging-made-easy.html">Windows:
 *      Dragging Made Easy</a>
 */
public class WindowDragger extends MouseInputAdapter {
	Point mouseLoc;
	boolean dragging;
	boolean active;

	@Override
	public void mousePressed(MouseEvent e) {
		mouseLoc = e.getLocationOnScreen();
		dragging = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		dragging = false;
		mouseLoc = null;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (mouseLoc == null || dragging == false) {
			return;
		}
		synchronized (mouseLoc) {
			Point p = e.getLocationOnScreen();
			if (JVM.isMac)
				p.y = Math.max(0, p.y);
			if (active) {
				Window w = e.getSource() instanceof Window
						? (Window) e.getSource()
						: SwingUtilities
								.getWindowAncestor((Component) e.getSource());
				WindowDragger.translateWindow(p.x - mouseLoc.x,
						p.y - mouseLoc.y, w);
			}
			mouseLoc.setLocation(p);
		}
	}

	public WindowDragger() {

	}

	public WindowDragger(Component c) {
		this(new Component[] { c });
	}

	public WindowDragger(Component[] c) {
		for (int a = 0; a < c.length; a++) {
			c[a].addMouseListener(this);
			c[a].addMouseMotionListener(this);
		}
	}

	/**
	 * Translates a window, after possibly adjusting dx and dy for OS-based
	 * restraints.
	 */
	protected static void translateWindow(int dx, int dy, Window window) {
		Point p = window.getLocation();
		p.x += dx;
		p.y += dy;
		if (JVM.isMac)
			p.y = Math.max(0, p.y);
		window.setLocation(p);
	}

	public void setActive(boolean b) {
		active = b;
	}

	public boolean isActive() {
		return active;
	}
}