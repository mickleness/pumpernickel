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
package com.pump.window;

import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This is a Window menu resembling <A HREF=
 * "http://developer.apple.com/documentation/UserExperience/Conceptual/AppleHIGuidelines/XHIGMenus/chapter_17_section_4.html#//apple_ref/doc/uid/TP30000356-TPXREF106"
 * >the menu</A> found in most Cocoa applications.
 * <P>
 * This menu will automatically update itself to always list all visible Frames.
 * Their title will appear in this menu, or the text "Untitled" will be used if
 * no frame title is available.
 * <P>
 * This uses the {@link com.pump.window.WindowList} to keep track of frames,
 * their order, and their layering.
 * <P>
 * As of this version, this class is not a perfect replica of Apple's menu. It
 * lacks a few key elements: <BR>
 * 1. The "Zoom" menu item. In Java it is not directly possible to emulate this
 * behavior. Probably a JNI-based approach would be simplest way to add this
 * feature. <BR>
 * 2. Window titles do not have a bullet displayed next to their name when they
 * have unsaved changes, or a diamond displayed next to their name when
 * minimized. I started to develop a <code>FrameMenuItemUI</code> to address
 * this problem, but then realized that if a Java program on Mac uses the screen
 * menubar (which is the preferred behavior): customized MenuItemUI's are
 * ignored. Apple does some slight-of-hand and maps every JMenuItem to some sort
 * of Cocoa peer, so the UI is ignored. <BR>
 * 3. Holding down the option/alt key doesn't toggle menu items like "Minimize".
 * I was able to implement this when a JMenuBar is placed in the JFrame, but not
 * when the screen menubar is used.
 * 
 * <P>
 * So ironically: I can get more Mac-like behavior on non-Macs. (Which defeats
 * the purpose.) But in the mean time: really all I personally need from my
 * Window menu is a list of available frames, so this meets my needs for now.
 * <P>
 * This can't run inside a Java sandbox because it refers to the WindowList
 * which invokes
 * <code>Toolkit.getDefaultToolkit().addAWTEventListener(..)</code>.
 *
 * @see <a
 *      href="https://javagraphics.blogspot.com/2008/11/windows-adding-window-menu.html">Windows:
 *      Adding a Window Menu</a>
 */
public class WindowMenu extends JMenu {
	private static final long serialVersionUID = 1L;

	/** The menu item that minimizes this window. */
	JMenuItem minimizeItem = new JMenuItem("Minimize");

	/**
	 * The "Bright All to Front" menu item. TODO: this is implemented hackish-ly
	 * and causes windows to flicker over one another. I'm not sure it's worth
	 * keeping; for now the lines that add it to the menu are commented out.
	 */
	JMenuItem bringItem = new JMenuItem("Bring All To Front");

	Runnable updateRunnable = new Runnable() {
		public void run() {
			removeAll();
			add(minimizeItem);
			if (customItems.length != 0) {
				addSeparator();
				for (int a = 0; a < customItems.length; a++) {
					add(customItems[a]);
				}
			}
			addSeparator();
			add(bringItem);
			addSeparator();
			Frame[] frames = WindowList.getFrames(false, false, true);
			for (int a = 0; a < frames.length; a++) {
				JCheckBoxMenuItem item = new SummonMenuItem(frames[a]);
				item.setSelected(frames[a] == myFrame);
				add(item);
			}
			fixMenuBar(myFrame, WindowMenu.this);
		}
	};

	/**
	 * On Mac often the menus won't really update without this hack-ish twist:
	 * remove the menu and re-add it. Voila! It's both unnecessary and crucial
	 * at the same time.
	 * 
	 * @param f
	 *            the frame whose menubar you need to update.
	 * @param menu
	 *            the menu you need to update.
	 */
	static void fixMenuBar(JFrame f, JMenu menu) {
		JMenuBar mb = f.getJMenuBar();
		if (mb != null) {
			JMenu[] menus = new JMenu[mb.getMenuCount()];
			for (int a = 0; a < menus.length; a++) {
				menus[a] = mb.getMenu(a);
			}

			boolean found = false;
			List<JMenu> menusToAdd = new ArrayList<>();
			for (int a = 0; a < menus.length; a++) {
				if (menus[a] == menu)
					found = true;

				if (found) {
					mb.remove(menus[a]);
					menusToAdd.add(menus[a]);
				}
			}
			for (JMenu menuToAdd : menusToAdd) {
				mb.add(menuToAdd);
			}
		}
	}

	private JFrame myFrame = null;

	private ChangeListener changeListener = new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
			SwingUtilities.invokeLater(updateRunnable);
		}
	};

	ActionListener actionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if (src == minimizeItem) {
				myFrame.setExtendedState(Frame.ICONIFIED);
			} else if (src == bringItem) {
				Frame[] frames = WindowList.getFrames(false, false, true);
				for (int a = 0; a < frames.length; a++) {
					if (frames[a].isVisible()
							|| frames[a].getExtendedState() == Frame.ICONIFIED) {
						frames[a].toFront();
						if (frames[a].getExtendedState() == Frame.ICONIFIED)
							frames[a].setExtendedState(Frame.NORMAL);
					}
				}
			}
		}
	};

	JMenuItem[] customItems;

	/**
	 * Creates a new WindowMenu for a specific JFrame.
	 * 
	 * @param frame
	 *            the frame that this menu belongs to.
	 */
	public WindowMenu(JFrame frame) {
		this(frame, new JMenuItem[] {});
	}

	/**
	 * Creates a new WindowMenu for a specific JFrame.
	 * 
	 * @param frame
	 *            the frame that this menu belongs to.
	 * @param extraItems
	 *            an optional array of extra items to put in this menu.
	 * */
	public WindowMenu(JFrame frame, JMenuItem... extraItems) {
		super("Window");
		minimizeItem.addActionListener(actionListener);
		bringItem.addActionListener(actionListener);

		customItems = new JMenuItem[extraItems.length];
		System.arraycopy(extraItems, 0, customItems, 0, extraItems.length);

		myFrame = frame;
		minimizeItem.setAccelerator(KeyStroke.getKeyStroke('M', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));

		WindowList.addChangeListener(changeListener);
		updateRunnable.run();
	}
}