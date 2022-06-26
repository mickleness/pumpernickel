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

import java.awt.Container;
import java.awt.EventQueue;
import java.awt.LayoutManager;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

/**
 * This LayoutManager may arrange Components so that the overlap.
 * <p>
 * This installs a custom RepaintManager to ensure repaints correctly
 * originate from the highest OverlappingLayoutManager.
 * <p>
 * The original use case for this class was: I arranged a JScrollPane
 * with a close button that slightly overlapped one of the scrollbars.
 * When the user interacts with the scrollbar: the JScrollBar would
 * request repaints and would end up painting *over* the button, which
 * was supposed to always float on top. This class solves this problem
 * by passing the repaint request up to the highest ancestor with
 * a OverlappingLayoutManager. This guarantees that the containing
 * panel will paint the appropriate area and all other descendants
 * -- including descendants that should float above the repainted
 * component.
 * <p>
 * This assumes no other entity has already override the RepaintManager,
 * and that no other entity will come along afterward and override this
 * RepaintManager.
 */
public abstract class OverlappingLayoutManager implements LayoutManager {

	static class OverlappingRepaintManager extends RepaintManager {

		/**
		 * This needs to run on the event dispatch thread because
		 * it checks the component's location on the screen.
		 */
		class AddDirtyRunnable implements Runnable {
			JComponent c, bottomMostContainer;
			int x, y, w, h;

			public AddDirtyRunnable(JComponent c,
					JComponent bottomMostContainer, int x, int y, int w,
					int h) {
				this.c = c;
				this.bottomMostContainer = bottomMostContainer;
				this.x = x;
				this.y = y;
				this.w = w;
				this.h = h;
			}

			@Override
			public void run() {
				Point p = new Point(x, y);
				p = SwingUtilities.convertPoint(c, p, bottomMostContainer);
				addDirtyRegion(bottomMostContainer, p.x, p.y, w, h);
			}

		}

		@Override
		public void addDirtyRegion(JComponent c, int x, int y, int w, int h) {

			if ((w <= 0) || (h <= 0) || (c == null)) {
				return;
			}

			if ((c.getWidth() <= 0) || (c.getHeight() <= 0)) {
				return;
			}

			JComponent t = c;
			JComponent bottomMostComponent = null;
			while (t != null) {
				if (t.getLayout() instanceof OverlappingLayoutManager) {
					bottomMostComponent = t;
				}

				Container parent = t.getParent();
				if (parent instanceof JComponent) {
					t = (JComponent) parent;
				} else {
					t = null;
				}
			}

			if (bottomMostComponent == null || bottomMostComponent == c) {
				super.addDirtyRegion(c, x, y, w, h);
			} else {
				Runnable r = new AddDirtyRunnable(c, bottomMostComponent, x, y,
						w, h);
				if (EventQueue.isDispatchThread()) {
					r.run();
				} else {
					EventQueue.invokeLater(r);
				}
			}
		}
	}

	static {
		RepaintManager.setCurrentManager(new OverlappingRepaintManager());
	}
}