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
package com.pump.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.icon.TriangleIcon;
import com.pump.util.ObservableList;
import com.pump.util.WeakSet;

/**
 * This takes a collection of menu items and makes a scrolling menu.
 * <p>
 * As of this writing: it looks like if a popup menu is too large then Swing/AWT
 * does not help the user navigate it.
 * <p>
 * This is loosely based on/inspired by darrylbu.util.MenuScroller. I had
 * trouble sometimes with the placement of that popup menu, though, so this
 * implementation separates the JMenu (which has to constantly be revised as you
 * scroll menu items) from the list of menu items.
 */
public class ScrollingMenu {

	private static class CenteredTriangleIcon extends TriangleIcon {

		public CenteredTriangleIcon(int direction, int width, int height,
				Color color) {
			super(direction, width, height, color);
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Dimension d = c.getSize();
			super.paintIcon(c, g, d.width / 2 - getIconWidth() / 2, d.height
					/ 2 - getIconHeight() / 2);
		}

	}

	public static final Icon ENABLED_UP_ICON = new CenteredTriangleIcon(
			SwingConstants.NORTH, 8, 8, Color.BLACK);
	public static final Icon DISABLED_UP_ICON = new CenteredTriangleIcon(
			SwingConstants.NORTH, 8, 8, Color.GRAY);
	public static final Icon ENABLED_DOWN_ICON = new CenteredTriangleIcon(
			SwingConstants.SOUTH, 8, 8, Color.BLACK);
	public static final Icon DISABLED_DOWN_ICON = new CenteredTriangleIcon(
			SwingConstants.SOUTH, 8, 8, Color.GRAY);

	private class MenuScrollTimer extends Timer {
		private static final long serialVersionUID = 1L;

		public MenuScrollTimer(final int increment, int interval) {
			super(interval, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					increment(increment);
				}
			});
		}
	}

	private class ScrollingMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;

		MenuScrollTimer timer;

		ScrollingMenuItem(int incr, int interval) {
			timer = new MenuScrollTimer(incr, interval);

			if (incr > 0) {
				setIcon(ENABLED_UP_ICON);
				setDisabledIcon(DISABLED_UP_ICON);
			} else {
				setIcon(ENABLED_DOWN_ICON);
				setDisabledIcon(DISABLED_DOWN_ICON);
			}

			addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					if (isArmed() && !timer.isRunning()) {
						timer.start();
					}
					if (!isArmed() && timer.isRunning()) {
						timer.stop();
					}
				}
			});
		}
	}

	public static JMenu convert(JMenu menu) {
		ScrollingMenu sm = new ScrollingMenu();
		for (Component c : menu.getComponents()) {
			if (c instanceof JMenuItem) {
				sm.getMenuItems().add((JMenuItem) c);
			}
		}
		return sm.getMenu();
	}

	ObservableList<JComponent> menuItems = new ObservableList<>();
	protected boolean menuDirty = false;
	int firstIndex = 0;
	int scrollCount = 12;
	JMenuItem upMenuItem = new ScrollingMenuItem(-1, 125);
	JMenuItem downMenuItem = new ScrollingMenuItem(1, 125);
	MouseWheelListener mouseWheelListener = new MouseWheelListener() {
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			increment(e.getWheelRotation());
			e.consume();
		}
	};
	JMenu menu;
	Runnable refreshMenuRunnable = new Runnable() {
		Set<Component> registeredMenuItems = new WeakSet<>();

		public void run() {
			synchronized (ScrollingMenu.this) {
				if (!menuDirty)
					return;
				menuDirty = false;
			}

			menu.removeAll();
			menu.add(upMenuItem);
			JComponent[] menuItemsArray = menuItems.toArray(JComponent.class);

			firstIndex = Math.min(firstIndex, menuItemsArray.length
					- scrollCount);
			firstIndex = Math.max(firstIndex, 0);

			int max = Math.min(menuItemsArray.length, scrollCount + firstIndex);
			for (int i = firstIndex; i < max; i++) {
				if (registeredMenuItems.add(menuItemsArray[i])) {
					menuItemsArray[i].addMouseWheelListener(mouseWheelListener);
				}
				menu.add(menuItemsArray[i]);
			}
			menu.add(downMenuItem);

			boolean canScrollUp = firstIndex > 0;
			boolean canScrollDown = firstIndex < menuItemsArray.length
					- scrollCount;
			boolean canScroll = canScrollUp || canScrollDown;
			upMenuItem.setVisible(canScroll);
			downMenuItem.setVisible(canScroll);
			upMenuItem.setEnabled(canScrollUp);
			downMenuItem.setEnabled(canScrollDown);

			JComponent parent = (JComponent) upMenuItem.getParent();
			parent.revalidate();
			parent.repaint();
		}
	};

	String menuName;

	public ScrollingMenu() {
		this("");
	}

	public ScrollingMenu(String menuName) {
		this.menuName = menuName;
		this.menu = new JMenu(menuName);

		menuItems.addUnsynchronizedChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				invalidateMenu();
			}
		}, false);
		upMenuItem.setIcon(ENABLED_UP_ICON);
		upMenuItem.setDisabledIcon(DISABLED_UP_ICON);
		downMenuItem.setIcon(ENABLED_DOWN_ICON);
		downMenuItem.setDisabledIcon(DISABLED_DOWN_ICON);
		invalidateMenu();
	}

	protected void increment(int incr) {
		firstIndex += incr;
		invalidateMenu();
	}

	protected void invalidateMenu() {
		synchronized (this) {
			menuDirty = true;
		}
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(refreshMenuRunnable);
			return;
		}

		if (menu.isShowing()) {
			refreshMenuRunnable.run();
		} else {
			SwingUtilities.invokeLater(refreshMenuRunnable);
		}
	}

	public ObservableList<JComponent> getMenuItems() {
		return menuItems;
	}

	public JMenu getMenu() {
		return menu;
	}

}