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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.pump.io.FileTreeIterator;
import com.pump.util.SearchConstraints;
import com.pump.util.list.AbstractListFilter;
import com.pump.util.list.ObservableList;
import com.pump.util.list.ObservableList.UIMirror;

/**
 * This is a list of files in a certain directory. Each file is listed as one
 * <code>JComponent</code> in a vertical list.
 */
public abstract class FileList extends JPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * A client property used to associated JComponents with a File.
	 */
	protected static final String KEY_FILE = FileList.class.getName() + ".file";

	private static FileFilter ACCEPT_ALL_FILTER = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			return true;
		}
	};

	static class SearchConstraintsListFilter extends AbstractListFilter<File> {
		SearchConstraints<File> searchConstraints;

		@Override
		public boolean accept(File t) {
			SearchConstraints<File> c = searchConstraints;
			return c == null ? true : c.accepts(t);
		}

		public boolean setSearchConstraints(
				SearchConstraints<File> newSearchConstraints) {
			if (Objects.equals(searchConstraints, newSearchConstraints))
				return false;
			searchConstraints = newSearchConstraints;
			fireChangeListeners();
			return true;
		}
	}

	private File[] currentDirectories = null;
	private ObservableList<File> fileList = new ObservableList<File>();
	private SearchConstraintsListFilter searchConstraintsFilter = new SearchConstraintsListFilter();
	private UIMirror<File> constrainedList = fileList
			.createUIMirror(searchConstraintsFilter);
	private final FileFilter fileFilter;

	Runnable updateContentsRunnable = new Runnable() {
		public void run() {
			updateFileListComponents();
		}
	};

	private SearchConstraints<File> searchConstraints;
	private boolean finishedSearch = false;

	static ExecutorService executor = Executors.newFixedThreadPool(6);

	class SearchRunnable implements Runnable {

		long searchID;

		public SearchRunnable(long searchID) {
			this.searchID = searchID;
		}

		public void run() {
			try {
				fileList.clear();
				for (File dir : currentDirectories) {
					search(dir);
				}
			} finally {
				synchronized (FileList.this) {
					if (searchID == searchCounter.get()) {
						finishedSearch = true;
						SwingUtilities.invokeLater(updateContentsRunnable);
					}
				}
			}
		}

		private void search(File currentDirectory) {
			FileTreeIterator iter = new FileTreeIterator(currentDirectory);
			while (iter.hasNext()) {
				File file = iter.next();
				boolean accept = fileFilter.accept(file);
				synchronized (FileList.this) {
					if (searchID == searchCounter.get()) {
						if (accept) {

							int i = searchConstraints == null ? Collections
									.binarySearch(fileList, file) : Collections
									.binarySearch(fileList, file,
											searchConstraints);
							if (i >= 0) {
								// already exists? great
							} else {
								fileList.add(-i - 1, file);
							}
						}
					} else {
						return;
					}
				}
			}
		}
	};

	/**
	 * 
	 * @param primaryFileFilter
	 *            this optional filter is immutable and supersedes any possible
	 *            SearchConstraints.
	 * @param constraints
	 *            this optional set of constraints is used to fine-tune what the
	 *            primary FileFilter accepts.
	 */
	public FileList(FileFilter primaryFileFilter,
			SearchConstraints<File> constraints) {
		this.fileFilter = primaryFileFilter == null ? ACCEPT_ALL_FILTER
				: primaryFileFilter;
		setSearchConstraints(constraints);

		constrainedList.addListDataListener(new ListDataListener() {

			@Override
			public void intervalAdded(ListDataEvent e) {
				contentsChanged(e);
			}

			@Override
			public void intervalRemoved(ListDataEvent e) {
				contentsChanged(e);

			}

			@Override
			public void contentsChanged(ListDataEvent e) {
				SwingUtilities.invokeLater(updateContentsRunnable);

			}
		});

		setLayout(new GridBagLayout());
	}

	/**
	 * 
	 * @param directories
	 *            an initial set of directories this FileList refers to.
	 * @param primaryFileFilter
	 *            this optional filter is immutable and supersedes any possible
	 *            SearchConstraints.
	 * @param constraints
	 *            this optional set of constraints is used to fine-tune what the
	 *            primary FileFilter accepts.
	 */
	public FileList(File[] directories, FileFilter primaryFileFilter,
			SearchConstraints<File> constraints) {
		this(primaryFileFilter, constraints);
		if (directories != null)
			setDirectories(directories);
	}

	public synchronized boolean setSearchConstraints(
			SearchConstraints<File> constraints) {
		return searchConstraintsFilter.setSearchConstraints(constraints);
	}

	protected JThrobber throbber = new JThrobber();
	protected JLabel emptyLabel = new JLabel("No results found.");
	protected JLabel searchingLabel = new JLabel("No results found.");
	private Map<File, JComponent> componentCache = new HashMap<File, JComponent>();
	private JLabel fluff = new JLabel();

	/**
	 * Set the number of columns this list should use.
	 * 
	 * @param columnCount
	 */
	public void setColumnCount(int columnCount) {
		if (columnCount <= 0)
			throw new IllegalArgumentException("value (" + columnCount
					+ ") must be 1 or greater");
		putClientProperty("columnCount", columnCount);
	}

	/**
	 * 
	 * @return the number of columns this list should use. The default is 1.
	 */
	public int getColumnCount() {
		Integer i = (Integer) getClientProperty("columnCount");
		if (i == null)
			return 1;
		return i;
	}

	protected synchronized void updateFileListComponents() {
		throbber.setVisible(!finishedSearch);
		removeAll();
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = getColumnCount() - 1;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.EAST;
		add(throbber, c);

		c.gridx = 0;
		c.weightx = 1;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		if (constrainedList.getSize() == 0) {
			if (finishedSearch) {
				add(emptyLabel, c);
			} else {
				add(searchingLabel, c);
			}
		}

		for (int a = 0; a < constrainedList.getSize(); a++) {
			File file = constrainedList.getElementAt(a);
			JComponent jc = componentCache.get(file);
			if (jc == null) {
				jc = createComponent(file);
				jc.putClientProperty(KEY_FILE, file);
				componentCache.put(file, jc);
			}

			add(jc, c);
			c.gridx = (c.gridx + 1) % getColumnCount();
			if (c.gridx == 0)
				c.gridy++;
		}
		c.gridy++;
		c.weighty = 1;
		add(fluff, c);

		revalidate();
		repaint();
	}

	/**
	 * Returns the files currently displayed in this list.
	 * 
	 * @return the files current displayed in this list. This will include files
	 *         that pass the primary FileFilter and the current
	 *         SearchConstraints.
	 * 
	 * @see #getAllFiles()
	 */
	public File[] getVisibleFiles() {
		List<File> files = new ArrayList<File>();
		for (int a = 0; a < getComponentCount(); a++) {
			Component comp = getComponent(a);
			if (comp instanceof JComponent) {
				JComponent jc = (JComponent) comp;
				File file = (File) jc.getClientProperty(KEY_FILE);
				if (file != null) {
					files.add(file);
				}
			}
		}
		return files.toArray(new File[files.size()]);
	}

	/**
	 * Returns all available files -- although some might not be visible.
	 * 
	 * @return all known available files that pass the primary FileFilter. Note
	 *         this includes files that do NOT pass the current
	 *         SearchConstraints. These files may change if the thread that is
	 *         scanning for Files is still ongoing.
	 * 
	 * @see #getVisibleFiles()
	 */
	public File[] getAllFiles() {
		return fileList.toArray(File.class);
	}

	/**
	 * Add a ListDataListener to be notified when the visible files change.
	 * 
	 * @param listListener
	 * @see #removeListDataListener(ListDataListener)
	 */
	public void addListDataListener(ListDataListener listListener) {
		constrainedList.addListDataListener(listListener);
	}

	/**
	 * Remove a ListDataListener.
	 * 
	 * @param listListener
	 * @see #addListDataListener(ListDataListener)
	 */
	public void removeListDataListener(ListDataListener listListener) {
		constrainedList.removeListDataListener(listListener);
	}

	protected abstract JComponent createComponent(File file);

	public synchronized File[] getDirectories() {
		File[] copy = new File[currentDirectories.length];
		System.arraycopy(currentDirectories, 0, copy, 0, copy.length);
		return copy;
	}

	AtomicLong searchCounter = new AtomicLong(0);

	public synchronized void setDirectories(File[] files) {
		if (files == null)
			throw new NullPointerException();
		for (File f : files) {
			if (f == null)
				throw new NullPointerException();
		}
		if (Arrays.equals(currentDirectories, files))
			return;
		currentDirectories = files;
		finishedSearch = false;

		long id;
		synchronized (FileList.this) {
			id = searchCounter.incrementAndGet();
		}
		executor.execute(new SearchRunnable(id));
		SwingUtilities.invokeLater(updateContentsRunnable);
	}
}