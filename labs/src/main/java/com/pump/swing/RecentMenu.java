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
package com.pump.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

/**
 * A submenu containing a list of recently opened files.
 */
public abstract class RecentMenu extends JMenu {
	private static final long serialVersionUID = 1L;

	/** A Listener to notify when a menu item is selected to open a file. */
	public static abstract class Listener {
		public abstract void fileSelected(File file);
	}

	/**
	 * An implementation of RecentMenu that relies of java.util.Preferences.
	 */
	public static class Preference extends RecentMenu {
		private static final long serialVersionUID = 1L;

		private static String FILE_ENTRY = RecentMenu.class.getName()
				+ "#file-entry";

		protected Preferences prefs;

		/**
		 * 
		 * @param includeClearItem
		 *            true if this menu should include a "Clear" menu item.
		 * @param t
		 *            this is used to construct a Preferences object to store
		 *            and retrieve the list of files.
		 */
		public Preference(boolean includeClearItem, Class<?> t) {
			super(includeClearItem);
			prefs = Preferences.userNodeForPackage(t);
		}

		@Override
		protected boolean doSetFiles(File[] files) {
			for (int a = 0; a < files.length; a++) {
				String key = FILE_ENTRY + a;
				prefs.put(key, files[a].getAbsolutePath());
			}
			String lastKey = FILE_ENTRY + files.length;
			prefs.remove(lastKey);

			try {
				prefs.flush();
			} catch (RuntimeException e) {
				throw e;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			return true;
		}

		@Override
		public File[] getFiles() {
			List<File> list = new ArrayList<File>();
			int ctr = 0;
			while (true) {
				String key = FILE_ENTRY + ctr;
				String s = prefs.get(key, null);
				if (s == null) {
					return list.toArray(new File[list.size()]);
				}
				File file = new File(s);

				// because sometimes files move around:
				if (file.exists())
					list.add(file);

				ctr++;
			}
		}
	}

	List<Listener> listeners = new ArrayList<Listener>();
	JMenuItem clearItem = new JMenuItem("Clear");
	JMenuItem emptyItem = new JMenuItem("Empty");
	boolean includeClear = false;

	public RecentMenu(boolean includeClear) {
		super("Open Recent");
		this.includeClear = includeClear;
		clearItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setFiles(new File[] {});
			}
		});
		emptyItem.setEnabled(false);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				refresh();
			}
		});
	}

	/**
	 * Add a Listener to be notified when a menu item is selected.
	 * 
	 * @param l
	 *            the Listener to add.
	 * 
	 */
	public void addListener(Listener l) {
		listeners.add(l);
	}

	/**
	 * Remove a Listener to be notified when a menu item is selected.
	 * 
	 * @param l
	 *            the Listener to remove.
	 */
	public void removeListener(Listener l) {
		listeners.remove(l);
	}

	protected void fireListeners(File file) {
		for (Listener l : listeners) {
			try {
				l.fileSelected(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Clear this menu and replace its UI contents with the current contents of
	 * <code>getFiles()</code>.
	 */
	protected void refresh() {
		File[] files = getFiles();
		removeAll();
		for (final File file : files) {
			JMenuItem item = new JMenuItem(file.getName());
			add(item);

			ActionListener actionListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					fireListeners(file);
				}
			};
			item.addActionListener(actionListener);
		}

		if (includeClear && files.length > 0) {
			addSeparator();
			add(clearItem);
		} else if (files.length == 0) {
			add(emptyItem);
		}
	}

	/**
	 * @return a list of files in the order they should be displayed to the
	 *         user.
	 */
	public abstract File[] getFiles();

	/**
	 * Replace the files this menu represents. This method automatically invokes
	 * <code>refresh()</code>, so the UI is updated.
	 * 
	 * @param files
	 *            a list of files (in the order they should be shown)
	 * @return true if this resulted in a change, false if it was a redundant
	 *         operation.
	 */
	public boolean setFiles(File[] files) {
		boolean returnValue = doSetFiles(files);
		if (returnValue)
			refresh();
		return returnValue;
	}

	/**
	 * This abstract method actually talks to the storage model (which is
	 * subclass-dependent).
	 * 
	 * @param files
	 *            an ordered list of files to store.
	 * @return true if this resulted in a change, false if it was a redundant
	 *         operation.
	 */
	protected abstract boolean doSetFiles(File[] files);

	/**
	 * Add a file to this list in the zeroeth spot (so it is the most recently
	 * touched file).
	 * 
	 * @param file
	 *            the file to add to the recently used file list.
	 * @return true if a change occurred, false if this was a redundant call.
	 */
	public boolean addFile(File file) {
		if (file == null)
			throw new NullPointerException();
		File[] origFiles = getFiles();
		List<File> list = new ArrayList<File>(Arrays.asList(origFiles));
		if (list.size() > 0 && list.get(0) == file)
			return false;
		list.remove(file);
		list.add(0, file);
		while (list.size() > 10) {
			list.remove(10);
		}
		setFiles(list.toArray(new File[list.size()]));
		return true;
	}

}