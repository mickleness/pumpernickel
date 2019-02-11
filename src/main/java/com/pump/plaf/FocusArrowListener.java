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
package com.pump.plaf;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * This listens for arrow keys and shifts the keyboard focus accordingly. So if
 * you press the left arrow key, the component to the left of the source
 * component requests the focus.
 * <P>
 * This scans for the first available component whose <code>isFocusable()</code>
 * method returns <code>true</code>. If no such component is found: nothing
 * happens.
 */
public class FocusArrowListener extends KeyAdapter {

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		int dx = 0;
		int dy = 0;
		if (code == KeyEvent.VK_LEFT) {
			dx = -1;
		} else if (code == KeyEvent.VK_UP) {
			dy = -1;
		} else if (code == KeyEvent.VK_RIGHT) {
			dx = 1;
		} else if (code == KeyEvent.VK_DOWN) {
			dy = 1;
		}

		if ((dx == 0 && dy == 0) == false
				&& shiftFocus(dx, dy, (Component) e.getSource()))
			e.consume();
	}

	/**
	 * Return the first component available from a collection of candidates that
	 * is generally in the direction provided.
	 */
	public static Component getComponent(int dx, int dy, Component src,
			Collection<Component> candidates) {
		if (dx == 0 && dy == 0) // this would result in an infinite loop
			throw new IllegalArgumentException("dx (" + dx + ") and (" + dy
					+ ") cannot both be zero");

		int x = src.getWidth() / 2;
		int y = src.getHeight() / 2;
		Window window = SwingUtilities.getWindowAncestor(src);
		if (window == null)
			return null;
		Point p = SwingUtilities.convertPoint(src, x, y, window);

		Component comp = null;
		int windowWidth = window.getWidth();
		int windowHeight = window.getHeight();

		Map<Component, Rectangle> candidateWindowBounds = new HashMap<>();
		for (Component candidate : candidates) {
			Point z = SwingUtilities.convertPoint(candidate, new Point(0, 0),
					window);
			Rectangle r = new Rectangle(z.x, z.y, candidate.getWidth(),
					candidate.getHeight());
			candidateWindowBounds.put(candidate, r);
		}

		while (p.x > 0 && p.x < windowWidth && p.y > 0 && p.y < windowHeight
				&& (comp == null || comp == src || (comp instanceof JPanel))) {
			p.x += dx;
			p.y += dy;

			// this mostly works, but it fails when, say, an overlay tooltip is
			// covering up what we want to select:
			// comp = SwingUtilities.getDeepestComponentAt(window, p.x, p.y);

			comp = null;
			for (Entry<Component, Rectangle> entry : candidateWindowBounds
					.entrySet()) {
				if (entry.getValue().contains(p)) {
					comp = entry.getKey();
					break;
				}
			}
			boolean canAcceptFocus = candidates.contains(comp);
			if (comp != null && canAcceptFocus == false)
				comp = null;
		}

		// TODO: implement a more robust searching mechanism instead of the
		// above. If a component is below the src, but to the left or right of
		// the center: it should still be detected when you press the down arrow
		// key.

		return comp;
	}

	/**
	 * Shifts the focus in a certain direction.
	 * 
	 * @param dx
	 *            the amount to increment x.
	 * @param dy
	 *            the amount to increment y.
	 * @param src
	 *            the source to traverse from.
	 * @return true if another component requested the focus as a result of this
	 *         method. This may return false if no suitable component was found
	 *         to shift focus to. (If you press the right arrow key on the
	 *         right-most component, for example.)
	 */
	public static boolean shiftFocus(int dx, int dy, Component src) {

		Set<Component> focusableComponents = getFocusableComponents(src);

		Component comp = getComponent(dx, dy, src, focusableComponents);

		if (comp != null && comp != src && !(comp instanceof Window)
				&& (!(comp instanceof JPanel))) {
			comp.requestFocus();
			return true;
		}
		return false;
	}

	/**
	 * Returns a set of all the components that can have the keyboard focus.
	 * <P>
	 * My first implementation involved of this concept simply involved asking
	 * JCompnonents if they were focusable, but in the
	 * <code>FilledButtonTest</code> this resulted in shifting focus to the
	 * ContentPane. Although it is technically focusable: if I used the tab key
	 * I did <i>not</i> get this result. So I studied the inner workings for
	 * Component.transferFocus() and ended up with a method that involved calls
	 * to <code>getFocusCycleRootAncestor()</code>, and
	 * <code>getFocusTraversalPolicy()</code>.
	 * <P>
	 * (Also credit goes to Werner for originally tipping me off towards looking
	 * at FocusTraversalPolicies.)
	 * 
	 * @param currentFocusOwner
	 *            the current focus owner.
	 * @return all the JComponents that can receive the focus.
	 */
	public static Set<Component> getFocusableComponents(
			Component currentFocusOwner) {
		HashSet<Component> set = new HashSet<Component>();
		set.add(currentFocusOwner);

		Container rootAncestor = currentFocusOwner.getFocusCycleRootAncestor();
		Component comp = currentFocusOwner;
		while (rootAncestor != null
				&& !(rootAncestor.isShowing() && rootAncestor.isFocusable() && rootAncestor
						.isEnabled())) {
			comp = rootAncestor;
			rootAncestor = comp.getFocusCycleRootAncestor();
		}
		if (rootAncestor != null) {
			FocusTraversalPolicy policy = rootAncestor
					.getFocusTraversalPolicy();
			Component toFocus = policy.getComponentAfter(rootAncestor, comp);

			while (toFocus != null && set.contains(toFocus) == false) {
				set.add(toFocus);
				toFocus = policy.getComponentAfter(rootAncestor, toFocus);
			}

			toFocus = policy.getComponentBefore(rootAncestor, comp);

			while (toFocus != null && set.contains(toFocus) == false) {
				set.add(toFocus);
				toFocus = policy.getComponentBefore(rootAncestor, toFocus);
			}
		}
		return set;
	}
}