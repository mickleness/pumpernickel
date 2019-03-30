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
package com.pump.swing.io;

import java.awt.Container;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.pump.UserCancelledException;
import com.pump.io.location.IOLocation;
import com.pump.io.location.IOLocationFilter;
import com.pump.plaf.LocationBrowserUI;
import com.pump.swing.BasicCancellable;
import com.pump.util.BasicReceiver;
import com.pump.util.ObservableList;
import com.pump.util.Receiver;

public class LocationBrowser extends JComponent {
	public static class MissingDirectoryException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public final IOLocation directory;

		public MissingDirectoryException(IOLocation dir) {
			super("mising directory: " + dir);
			this.directory = dir;
		}
	}

	private static final long serialVersionUID = 1L;

	private static final String uiClassID = "LocationBrowserUI";

	static IOLocationFilter acceptsEverythingFilter = new IOLocationFilter() {
		public IOLocation filter(IOLocation loc) {
			return loc;
		}
	};

	final GraphicCache graphicCache;
	final ObservableList<IOLocation> directoryContents = new ObservableList<IOLocation>();
	ListModel unmutableListModel = directoryContents.getListModelView(true);
	final List<ChangeListener> changeListeners = new ArrayList<ChangeListener>();
	final LocationHistory directoryStack;
	final IOSelectionModel selectionModel;
	IOLocationFilter filter = acceptsEverythingFilter;
	private boolean refreshOnWindowActivation = true;

	/**
	 * Why did the user switch windows around? Did the file system change?
	 */
	WindowListener windowListener = new WindowAdapter() {
		@Override
		public void windowActivated(WindowEvent e) {
			if (refreshOnWindowActivation) {
				try {
					refresh(false, false);
				} catch (RuntimeException e2) {
					System.err.println(e2.toString());
				}
			}
		}
	};

	static int idCtr = 0;
	int id = idCtr++;

	public LocationBrowser(boolean allowMultipleSelection) {
		this(new IOSelectionModel(allowMultipleSelection),
				new LocationHistory(), new GraphicCache());
	}

	public LocationBrowser(IOSelectionModel selectionModel,
			LocationHistory directoryStack, GraphicCache graphicCache) {
		this.selectionModel = selectionModel;
		this.directoryStack = directoryStack;
		this.graphicCache = graphicCache;
		directoryStack.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				considerRefreshing(true);
			}
		});
		this.addHierarchyListener(new HierarchyListener() {
			Window ownerWindow;

			public void hierarchyChanged(HierarchyEvent e) {
				Window w = SwingUtilities
						.getWindowAncestor(LocationBrowser.this);

				if (ownerWindow != null) {
					ownerWindow.removeWindowListener(windowListener);
				}
				if (w != null) {
					ownerWindow = w;
					ownerWindow.addWindowListener(windowListener);
				}
				considerRefreshing(false);
			}
		});
		this.addComponentListener(new ComponentListener() {

			public void componentHidden(ComponentEvent e) {
			}

			public void componentMoved(ComponentEvent e) {
			}

			public void componentResized(ComponentEvent e) {
			}

			public void componentShown(ComponentEvent e) {
				considerRefreshing(false);
			}

		});
		updateUI();
	}

	private boolean wasShowing = false;

	private void considerRefreshing(boolean contentsChanged) {
		boolean isShowing = isShowing();
		if ((isShowing && wasShowing == false)
				|| (isShowing && contentsChanged)) {
			refresh(true, false);
		}
		wasShowing = isShowing;
	}

	/**
	 * Refreshes this browser.
	 * <P>
	 * This means a new thread will start that asks the current directory for
	 * its contents.
	 * 
	 * @param clear
	 *            whether this request should first clear the current contents.
	 *            <P>
	 *            Eventually -- once the threads finish -- the contents of this
	 *            browser will exactly match the contents of the current
	 *            directory. But consider the case of adding a new folder to the
	 *            displayed directory: if we call <code>refresh(true)</code>
	 *            then all the other files will be removed and the list will
	 *            repopulate from scratch. This results in a flickering GUI for
	 *            the user (or worse, if the file system is slow to respond:
	 *            such as a slow FTP connection). But if we call
	 *            <code>refresh(false)</code> then there is no flicker. New
	 *            files are added where appropriate, and at the end a call to
	 *            <code>replaceAll()</code> guarantees that removed files will
	 *            also be noticed.
	 * @param blockUntilFinished
	 *            if true then this method won't return until the refresh is
	 *            complete.
	 */
	public void refresh(boolean clear, boolean blockUntilFinished) {
		IOLocation directory = LocationBrowser.this.directoryStack
				.getLocation();

		if (directory == null)
			return;

		if (directory.exists() == false) {
			directoryContents.clear();
			throw new MissingDirectoryException(directory);
		}

		ContentFetcher fetcher = new ContentFetcher(directory, clear);
		fetcher.start();
		if (blockUntilFinished) {
			while (fetcher.isAlive()) {
				try {
					fetcher.join();
				} catch (InterruptedException e) {
				}
			}
		}
	}

	/**
	 * @return the directory contents. Note that it will change until
	 *         <code>isLoading()</code> returns <code>false</code>, and these
	 *         changes will not occur on the event dispatch thread.
	 */
	public ListModel getListModel() {
		return unmutableListModel;
	}

	/**
	 * @return The model for which files are currently selected in the current
	 *         directory.
	 */
	public IOSelectionModel getSelectionModel() {
		return selectionModel;
	}

	/**
	 * @return the model for which directory is currently being displayed. If
	 *         this is changed then the <code>ListModel</code> and
	 *         <code>IOSelectionModel</code> will both be changed.
	 */
	public LocationHistory getLocationHistory() {
		return directoryStack;
	}

	public GraphicCache getGraphicCache() {
		return graphicCache;
	}

	private int activeThreadCount = 0;

	private void changeActiveThreadCount(int increment) {
		int oldValue = activeThreadCount;
		activeThreadCount += increment;
		if ((oldValue == 0) != (activeThreadCount == 0)) {
			fireChangeListeners();
		}
	}

	/**
	 * @return whether the <code>ListModel</code> is still being generated. If
	 *         you add <code>ChangeListeners</code> to this object: they will be
	 *         notified when this method changes its return value;
	 */
	public boolean isLoading() {
		return activeThreadCount > 0;
	}

	/**
	 * This listener is notified when <code>isLoading()</code> changes value.
	 * 
	 * @param l
	 *            the listener to add
	 */
	public void addChangeListener(ChangeListener l) {
		if (changeListeners.contains(l))
			return;
		changeListeners.add(l);
	}

	public void removeChangeListener(ChangeListener l) {
		changeListeners.remove(l);
	}

	protected void fireChangeListeners() {
		for (int a = 0; a < changeListeners.size(); a++) {
			ChangeListener l = changeListeners.get(a);
			try {
				l.stateChanged(new ChangeEvent(this));
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
	}

	private List<ContentFetcher> activeFetchers = new ArrayList<ContentFetcher>();

	class ContentFetcher extends Thread {
		ObservableList<IOLocation> myList = new ObservableList<IOLocation>();
		BasicCancellable cancellable = new BasicCancellable();
		IOLocation myLocation;
		boolean clear;

		ContentFetcher(IOLocation loc, boolean clear) {
			super("Opening " + loc);

			this.clear = clear;

			if (!clear) {
				myList.setAll(directoryContents);
			}

			myLocation = loc;
			myList.addUnsynchronizedChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					syncLists();
				}
			}, false);
			while (activeFetchers.size() > 0) {
				ContentFetcher f = activeFetchers.remove(0);
				f.cancellable.cancel();
			}
			activeFetchers.add(this);
		}

		private void syncLists() {
			synchronized (LocationBrowser.this) {
				if (cancellable.isCancelled())
					return;
			}

			List<IOLocation> incomingList = new ArrayList<IOLocation>();
			IOLocationFilter filter = getFilter();
			if (filter != null) {
				incomingList = new ArrayList<IOLocation>();
				for (int index = 0; index < myList.size(); index++) {
					IOLocation loc = myList.get(index);
					if (loc.isDirectory() && loc.isNavigable()) {
						// this condition added for T4L bug 20748; I'm not sure
						// that
						// I like this logic here (instead of belonging to
						// filters),
						// but it's acceptable for now.
						if (loc.canRead())
							incomingList.add(loc);
					} else {
						loc = filter.filter(loc);
						if (loc != null)
							incomingList.add(loc);
					}
				}
			} else {
				incomingList.addAll(myList);
			}

			directoryContents.setAll(incomingList);
		}

		@Override
		public void run() {
			changeActiveThreadCount(1);
			myLocation.flush();
			try {
				syncLists();
				if (clear) {
					SwingUtilities.invokeLater(clearAndRepaint);
					Receiver<IOLocation> receiver = new Receiver<IOLocation>() {
						public void add(IOLocation... elements) {
							myList.addAll(elements);
						}
					};
					myLocation.listChildren(receiver, cancellable);
				} else {
					// this is a refresh.
					// This is trickier: we need an in-between list.

					BasicReceiver<IOLocation> incoming = new BasicReceiver<IOLocation>();
					incoming.addListDataListener(new ListDataListener() {

						public void intervalAdded(ListDataEvent e) {
							@SuppressWarnings("unchecked")
							ListModel<IOLocation> in = (ListModel<IOLocation>) e
									.getSource();
							for (int a = e.getIndex0(); a <= e.getIndex1(); a++) {
								IOLocation newGuy = in.getElementAt(a);
								if (myList.contains(newGuy) == false) {
									if (a == 0) {
										myList.add(0, newGuy);
									} else {
										IOLocation prevGuy = in
												.getElementAt(a - 1);
										int prevIndex = myList.indexOf(prevGuy);
										if (prevIndex != -1) {
											myList.add(prevIndex + 1, newGuy);
										} else {
											// uh-oh. don't know what to do
											// here.
											// luckily this will be
											// caught in the "replaceAll" below.
										}
									}
								}
							}
						}

						public void contentsChanged(ListDataEvent e) {
						}

						public void intervalRemoved(ListDataEvent e) {
						}
					});

					myLocation.listChildren(incoming, cancellable);

					// If the refresh was only for additions: then
					// hopefully this won't do anything.
					// but a refresh can also sense deletions, and
					// that makes this line crucial if we want to sense those:
					myList.setAll(incoming.toArray(new IOLocation[incoming
							.getSize()]));
				}

			} catch (UserCancelledException e) {
				// do nothing
			} finally {
				changeActiveThreadCount(-1);
			}
		}
	}

	public IOLocationFilter getFilter() {
		Container c = getParent();
		while (c != null) {
			if (c instanceof LocationPane) {
				return ((LocationPane) c).getFilter();
			}
			if (c.getParent() instanceof Container) {
				c = c.getParent();
			} else {
				c = null;
			}
		}
		return filter;
	}

	public void setFilter(IOLocationFilter filter) {
		if (filter == null)
			filter = acceptsEverythingFilter;
		this.filter = filter;
	}

	Runnable clearAndRepaint = new Runnable() {
		public void run() {
			graphicCache.clear();
			repaint();
		}
	};

	@Override
	public String getUIClassID() {
		return uiClassID;
	}

	@Override
	public void updateUI() {
		if (UIManager.getDefaults().get(uiClassID) == null) {
			UIManager.getDefaults().put(uiClassID,
					"com.pump.plaf.ListLocationBrowserUI");
		}
		setUI((LocationBrowserUI) UIManager.getUI(this));
	}

	public void setUI(LocationBrowserUI ui) {
		super.setUI(ui);
	}

	public LocationBrowserUI getUI() {
		return (LocationBrowserUI) ui;
	}

	/**
	 * Control whether this component refreshes its directory listing when it
	 * observes the containing window has reactivated.
	 * <P>
	 * For example: if someone leaves this dialog and navigates to the file
	 * system, then delete/renames files, and switches back to this component,
	 * then it makes sense to refresh automatically. However if the user is
	 * navigated, for example, a company-specific server (cloud), then it is
	 * less likely the file system changed.
	 * 
	 * @param refreshOnWindowActivation
	 *            whether this component refreshes when the containing window is
	 *            reactivated.
	 * @see #isRefreshOnWindowActivation()
	 */
	public void setRefreshOnWindowActivation(boolean refreshOnWindowActivation) {
		this.refreshOnWindowActivation = refreshOnWindowActivation;
	}

	/**
	 * @return whether this component refreshes its directory listing when it
	 *         observes the containing window has reactivated.
	 * 
	 * @see #setRefreshOnWindowActivation(boolean)
	 * 
	 */
	public boolean isRefreshOnWindowActivation() {
		return refreshOnWindowActivation;
	}
}