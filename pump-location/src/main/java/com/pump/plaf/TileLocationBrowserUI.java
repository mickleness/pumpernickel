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

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.pump.io.location.IOLocation;
import com.pump.swing.io.IOLocationTileList;
import com.pump.swing.io.IOLocationTileList.BasicTileCellRenderer;
import com.pump.swing.io.LocationBrowser;
import com.pump.swing.io.LocationPane;
import com.pump.swing.io.OpenLocationPane;
import com.pump.util.JVM;
import com.pump.util.ObservableList;

public abstract class TileLocationBrowserUI extends LocationBrowserUI {
    protected ObservableList<IOLocation> threadsafeListModel = new ObservableList<IOLocation>();
    protected ListModel listUIModel = threadsafeListModel
	    .getListModelEDTMirror();
    protected IOLocationTileList list = new IOLocationTileList(listUIModel);
    protected JScrollPane scrollPane;
    protected JLabel thumbnail = new JLabel();

    MouseListener mouseListener = new MouseAdapter() {
	@Override
	public void mouseClicked(MouseEvent e) {
	    if (e.getClickCount() > 1) {
		openSelectedItem();
	    }
	}
    };
    KeyListener keyListener = new KeyAdapter() {
	@Override
	public void keyTyped(KeyEvent e) {
	    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
		openSelectedItem();
		e.consume();
	    }
	}
    };

    private int adjustingModels = 0;
    Runnable syncGUIFromRealSelection = new Runnable() {
	public void run() {
	    synchronizeDirectoryContents();
	}
    };
    Runnable syncRealSelectionFromGUI = new Runnable() {
	public void run() {
	    if (adjustingModels > 0)
		return;
	    adjustingModels++;
	    try {
		Object[] obj = list.getSelectedValues();
		IOLocation[] array = new IOLocation[obj.length];
		for (int a = 0; a < obj.length; a++) {
		    array[a] = (IOLocation) obj[a];
		}
		browser.getSelectionModel().setSelection(array);
	    } finally {
		adjustingModels--;
	    }
	}
    };
    ListSelectionListener guiListener = new ListSelectionListener() {
	public void valueChanged(ListSelectionEvent e) {
	    if (SwingUtilities.isEventDispatchThread()) {
		syncRealSelectionFromGUI.run();
	    } else {
		SwingUtilities.invokeLater(syncRealSelectionFromGUI);
	    }
	}
    };
    ChangeListener realModelListener = new ChangeListener() {
	public void stateChanged(ChangeEvent e) {
	    if (SwingUtilities.isEventDispatchThread()) {
		syncGUIFromRealSelection.run();
	    } else {
		SwingUtilities.invokeLater(syncGUIFromRealSelection);
	    }
	}
    };

    public TileLocationBrowserUI(LocationBrowser b) {
	super(b);

	list.setFilter(browser.getFilter());
	list.setCellRenderer(getListCellRenderer());
	list.addMouseListener(mouseListener);
	list.addKeyListener(keyListener);

	listUIModel.addListDataListener(new ListDataListener() {
	    public void contentsChanged(ListDataEvent e) {
		updateRowCount();
	    }

	    public void intervalAdded(ListDataEvent e) {
		contentsChanged(e);
	    }

	    public void intervalRemoved(ListDataEvent e) {
		contentsChanged(e);
	    }
	});

	scrollPane = new JScrollPane(list,
		ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
		ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

	// list.getParent() is a viewport, not the scrollpane
	list.getParent().addComponentListener(new ComponentAdapter() {
	    @Override
	    public void componentResized(ComponentEvent e) {
		updateRowCount();
	    }
	});

	list.getSelectionModel().addListSelectionListener(guiListener);
    }

    protected void openSelectedItem() {
	IOLocation loc = (IOLocation) list.getSelectedValue();
	LocationPane locPane = getLocationPane();
	boolean navigable = loc != null && loc.isNavigable();
	if (locPane instanceof OpenLocationPane && loc != null
		&& navigable == false) {
	    locPane.getLocationPaneUI().getCommitButton().doClick();
	    return;
	}
	if (loc != null && loc.canRead() && navigable) {
	    browser.getLocationHistory().append(loc);
	}
    }

    /**
     * Returns the LocationPane, if one exists, that is a parent to the
     * LocationBrowser this object represents.
     */
    protected LocationPane getLocationPane() {
	Container c = browser;
	while (c != null) {
	    if (c instanceof LocationPane)
		return (LocationPane) c;
	    c = c.getParent();
	}
	return null;
    }

    static Comparator<IOLocation> locComparator = new Comparator<IOLocation>() {

	public int compare(IOLocation loc1, IOLocation loc2) {
	    if (JVM.isMac == false) {
		if (loc1.isDirectory() && loc2.isDirectory() == false) {
		    return -1;
		} else if (loc1.isDirectory() == false && loc2.isDirectory()) {
		    return 1;
		}
	    }
	    String n1 = loc1.getName().toLowerCase();
	    String n2 = loc2.getName().toLowerCase();
	    return n1.compareTo(n2);
	}

    };

    /**
     * Returns the location comparator used to sort our children. Subclasses may
     * override this.
     * 
     * @return the location comparator used to sort our children.
     */
    protected Comparator<IOLocation> getLocationComparator() {
	return locComparator;
    }

    @Override
    protected void synchronizeDirectoryContents() {
	List<IOLocation> v = new ArrayList<IOLocation>();
	ListModel model = browser.getListModel();
	synchronized (model) {
	    for (int a = 0; a < model.getSize(); a++) {
		IOLocation loc = (IOLocation) model.getElementAt(a);
		if (loc.isHidden() == false)
		    v.add(loc);
	    }
	}
	Collections.sort(v, getLocationComparator());
	synchronized (threadsafeListModel) {
	    threadsafeListModel.setAll(v);
	}

	// synchronize the selection
	if (adjustingModels > 0)
	    return;
	adjustingModels++;
	try {
	    IOLocation[] obj = browser.getSelectionModel().getSelection();
	    List<Integer> ints = new ArrayList<Integer>();
	    Rectangle visibleBounds = null;
	    int[] indices;
	    synchronized (threadsafeListModel) {
		for (int a = 0; a < obj.length; a++) {
		    int k = threadsafeListModel.indexOf(obj[a]);
		    if (k != -1) {
			ints.add(new Integer(k));
		    }
		}
		indices = new int[ints.size()];
		for (int a = 0; a < ints.size(); a++) {
		    indices[a] = (ints.get(a)).intValue();
		}
		list.setSelectedIndices(indices);
		if (indices.length > 0) {
		    visibleBounds = list.getCellBounds(indices[0], indices[0]);
		}
	    }

	    if (visibleBounds != null) {
		try {
		    list.scrollRectToVisible(visibleBounds);
		} catch (RuntimeException e) {
		    System.err.println("indices[0] = " + indices[0]
			    + " out of:");
		    for (int a = 0; a < list.getModel().getSize(); a++) {
			System.err.println("\tlist[a] = "
				+ list.getModel().getElementAt(a));
		    }
		    throw e;
		}
	    }
	} finally {
	    adjustingModels--;
	}
    }

    @Override
    protected void installGUI(JComponent comp) {
	comp.setLayout(new GridBagLayout());
	GridBagConstraints c = new GridBagConstraints();
	c.gridx = 0;
	c.gridy = 0;
	c.weightx = 1;
	c.weighty = 1;
	c.fill = GridBagConstraints.NONE;
	c.anchor = GridBagConstraints.CENTER;
	c.fill = GridBagConstraints.BOTH;
	comp.add(scrollPane, c);
    }

    @Override
    public void installUI(JComponent c) {
	super.installUI(c);
	syncGUIFromRealSelection.run();
	browser.getSelectionModel().addChangeListener(realModelListener);
	if (browser.getSelectionModel().allowsMultipleSelection()) {
	    list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	} else {
	    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
    }

    @Override
    public void uninstallUI(JComponent c) {
	super.uninstallUI(c);
	browser.getSelectionModel().removeChangeListener(realModelListener);
    }

    private void updateRowCount() {
	// TODO: adjust the visible row(s)?
	// that is: emphasize the selection, or else cells that were
	// previously visible before changing the row count.
	int width = list.getWidth();
	if (list.getParent() instanceof JViewport) {
	    JViewport p = (JViewport) list.getParent();
	    width = p.getWidth();
	}
	list.updateVisibleRowCount(width);
    }

    @Override
    protected void repaint(IOLocation loc, boolean thumbnail) {
	int size = list.getModel().getSize();
	for (int index = 0; index < size; index++) {
	    if (list.getModel().getElementAt(index) == loc) {
		Rectangle bounds = list.getUI().getCellBounds(list, index,
			index);
		list.repaint(bounds);
		return;
	    }
	}
    }

    @Override
    public int getVisibleLocationSize() {
	return list.getModel().getSize();
    }

    protected ListCellRenderer getListCellRenderer() {
	return new BasicTileCellRenderer(browser.getGraphicCache(), thumbnail);
    }

    /** Return the <code>JList</code> this UI displays. */
    public JList getList() {
	return list;
    }
}