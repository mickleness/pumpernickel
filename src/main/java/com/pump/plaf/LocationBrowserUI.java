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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.plaf.ComponentUI;

import com.pump.io.location.IOLocation;
import com.pump.swing.JThrobber;
import com.pump.swing.io.GraphicCache;
import com.pump.swing.io.LocationBrowser;

public abstract class LocationBrowserUI extends ComponentUI {
	protected final LocationBrowser browser;

	private JThrobber loadingProgressBar = new JThrobber();

	/** This replaces the GUI list with the browser's list. */
	private Runnable syncList = new Runnable() {
		public void run() {
			updateProgressBar();
			synchronizeDirectoryContents();
		}
	};

	private class RepaintLocationRunnable implements Runnable {
		IOLocation loc;
		boolean isThumbnail;

		RepaintLocationRunnable(IOLocation l, boolean t) {
			loc = l;
			isThumbnail = t;
		}

		public void run() {
			repaint(loc, isThumbnail);
		}
	}

	private PropertyChangeListener graphicListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(GraphicCache.THUMBNAIL_PROPERTY)) {
				IOLocation loc = (IOLocation) evt.getSource();
				SwingUtilities.invokeLater(new RepaintLocationRunnable(loc,
						true));
			} else if (evt.getPropertyName().equals(GraphicCache.ICON_PROPERTY)) {
				IOLocation loc = (IOLocation) evt.getSource();
				SwingUtilities.invokeLater(new RepaintLocationRunnable(loc,
						false));
			}
		}
	};

	Runnable updateProgressBarRunnable = new Runnable() {
		public void run() {
			updateProgressBar();
		}
	};
	private ChangeListener loadingListener = new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
			SwingUtilities.invokeLater(updateProgressBarRunnable);
		}
	};

	private ListDataListener browserListListener = new ListDataListener() {

		public void contentsChanged(ListDataEvent e) {
			SwingUtilities.invokeLater(syncList);
		}

		public void intervalAdded(final ListDataEvent e) {
			SwingUtilities.invokeLater(syncList);
		}

		public void intervalRemoved(final ListDataEvent e) {
			SwingUtilities.invokeLater(syncList);
		}

	};

	public LocationBrowserUI(LocationBrowser b) {
		browser = b;

		loadingProgressBar.setVisible(false);

		Dimension d = loadingProgressBar.getPreferredSize();
		d.width *= 3;
		d.height *= 3;
		loadingProgressBar.setPreferredSize(d);
	}

	/**
	 * This will be called on the event dispatch thread when the GraphicsCache
	 * has indicated the loc has a new graphic available.
	 * 
	 * @param loc
	 * @param thumbnail
	 *            if true then a thumbnail is available, if false then an icon
	 *            is available.
	 */
	protected abstract void repaint(IOLocation loc, boolean thumbnail);

	/**
	 * This is called during the <code>installUI</code> method to set up the
	 * controls visible in this <code>LocationBrowserUI</code>.
	 * 
	 * @param comp
	 *            the panel to install the GUI in.
	 */
	protected abstract void installGUI(JComponent comp);

	/**
	 * This will be called on the event dispatch thread when
	 * <code>browser.getListModel()</code> has changed.
	 */
	protected abstract void synchronizeDirectoryContents();

	private void installRealGUI() {
		JPanel realContents = new JPanel();
		JPanel loadingProgressBarPanel = new JPanel(new GridBagLayout());

		browser.removeAll();
		browser.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		browser.add(loadingProgressBarPanel, c);
		browser.add(realContents, c);

		installGUI(realContents);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		loadingProgressBarPanel.setOpaque(false);
		loadingProgressBarPanel.add(loadingProgressBar, c);
	}

	protected static int getIndexOf(ListModel model, Object element) {
		for (int a = 0; a < model.getSize(); a++) {
			if (model.getElementAt(a).equals(element))
				return a;
		}
		return -1;
	}

	@Override
	public void installUI(JComponent c) {
		if (c != browser)
			throw new IllegalArgumentException(
					"this ui can only be used for the LocationBrowser it was constructed with");
		browser.getListModel().addListDataListener(browserListListener);
		browser.getGraphicCache().addPropertyChangeListener(graphicListener);
		browser.addChangeListener(loadingListener);

		installRealGUI();

		SwingUtilities.invokeLater(syncList);
		SwingUtilities.invokeLater(updateProgressBarRunnable);
	}

	@Override
	public void uninstallUI(JComponent c) {
		browser.removeAll();
		browser.getListModel().removeListDataListener(browserListListener);
		browser.getGraphicCache().removePropertyChangeListener(graphicListener);
		browser.removeChangeListener(loadingListener);
	}

	/**
	 * This is called by <code>typingListener</code> when the user has typed a
	 * string. By default it will select the first item in the directory that
	 * starts with the same text.
	 */
	protected void stringTyped(String s) {
		s = s.toLowerCase();

		ListModel m = browser.getListModel();
		int index = 0;
		synchronized (m) {
			while (index < m.getSize()) {
				IOLocation loc = (IOLocation) m.getElementAt(index);
				if (loc.getName().toLowerCase().startsWith(s)) {
					browser.getSelectionModel().setSelection(loc);
					return;
				}
				index++;
			}
		}
	}

	private long progressBarRequestedTime = -1;
	private Timer progressBarTimer = new Timer(50, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (updateProgressBar() == false) {
				progressBarTimer.stop();
			}
		}
	});

	/**
	 * Returns the number of currently visible locations. If this is zero, then
	 * an indeterminant progress bar may appear while this location is still
	 * loading.
	 */
	public abstract int getVisibleLocationSize();

	/**
	 * Call this method to update/monitor the visibility of the
	 * loadingProgressBar.
	 * 
	 * @return true if we need to continue to monitor this
	 */
	private boolean updateProgressBar() {
		/**
		 * This is surprisingly tricky because we don't want to display it
		 * *immediately* if the list is empty. This could result in annoying
		 * flickering if the user is still getting near-instant feedback. So
		 * instead we want to wait a short delay before showing it.
		 */
		boolean needFeedback = browser.isLoading()
				&& getVisibleLocationSize() == 0;
		long current = System.currentTimeMillis();
		if (needFeedback && progressBarRequestedTime == -1) {
			progressBarRequestedTime = current;
			progressBarTimer.start();
			return true;
		} else if (needFeedback && current - progressBarRequestedTime > 500) {
			loadingProgressBar.setVisible(true);
			return false;
		} else if (needFeedback) {
			return true;
		} else {
			progressBarRequestedTime = -1;
			loadingProgressBar.setVisible(false);
			return false;
		}
	}
}