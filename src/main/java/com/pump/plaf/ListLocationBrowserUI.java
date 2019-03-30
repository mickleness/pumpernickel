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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.pump.io.location.IOLocation;
import com.pump.swing.io.LocationBrowser;

public abstract class ListLocationBrowserUI extends LocationBrowserUI {
	public static ListLocationBrowserUI createUI(JComponent browser) {
		return new AquaListLocationBrowserUI((LocationBrowser) browser);
	}

	JScrollPane scrollPane;
	JTable table;
	DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
	DefaultTableModel tableModel = new DefaultTableModel();

	private int adjustingModels = 0;
	Runnable syncGUIFromRealSelection = new Runnable() {
		public void run() {
			synchronizeDirectoryContents();
			if (adjustingModels > 0)
				return;
			adjustingModels++;
			try {
				IOLocation[] obj = browser.getSelectionModel().getSelection();
				List<Integer> ints = new ArrayList<Integer>();
				ListModel listModel = browser.getListModel();
				synchronized (listModel) {
					for (int a = 0; a < obj.length; a++) {
						int k = getIndexOf(listModel, obj[a]);
						if (k != -1) {
							ints.add(new Integer(k));
						}
					}
					int[] indices = new int[ints.size()];
					for (int a = 0; a < ints.size(); a++) {
						indices[a] = (ints.get(a)).intValue();
					}
					// seriously? there isn't a way to set the selection all at
					// once?
					table.getSelectionModel().clearSelection();
					for (int a = 0; a < indices.length; a++) {
						table.getSelectionModel().addSelectionInterval(
								indices[a], indices[a]);
					}

					if (indices.length > 0) {
						Rectangle r = table.getCellRect(indices[0], 0, false);
						table.scrollRectToVisible(r);
					}
				}
			} finally {
				adjustingModels--;
			}
		}
	};
	Runnable syncRealSelectionFromGUI = new Runnable() {
		public void run() {
			if (adjustingModels > 0)
				return;
			adjustingModels++;
			try {
				int[] rows = table.getSelectedRows();
				IOLocation[] array = new IOLocation[rows.length];
				for (int a = 0; a < rows.length; a++) {
					array[a] = (IOLocation) table.getValueAt(rows[a], 0);
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

	private IOLocation[] getListModelAsArray() {
		ListModel model = browser.getListModel();
		synchronized (model) {
			IOLocation[] array = new IOLocation[model.getSize()];
			for (int a = 0; a < array.length; a++) {
				array[a] = (IOLocation) model.getElementAt(a);
			}
			return array;
		}
	}

	@Override
	protected void synchronizeDirectoryContents() {
		IOLocation[] array;

		array = getListModelAsArray();

		/**
		 * The likely cases here are: 1. The table and array exactly match,
		 * because redundant runnables have piled up in the queue. 2. New
		 * elements were added to list that the table needs to add. 3. The list
		 * was emptied.
		 * 
		 * We should also plan for the unintended "other" scenario: 4. The list
		 * changed in unpredicted ways, and the table needs to adjust.
		 * 
		 */

		int rowCount = tableModel.getRowCount();
		if (array.length == rowCount) { // scenario #1
			boolean equals = true;
			for (int a = 0; a < array.length && equals; a++) {
				IOLocation tableValue = (IOLocation) tableModel
						.getValueAt(a, 0);
				if (tableValue.equals(array[a]) == false) {
					equals = false;
				}
			}
			if (equals)
				return;
		} else if (array.length > rowCount) { // this might be scenario #2
			int rowIndex = 0;
			IOLocation rowValue = null;
			if (rowIndex < rowCount)
				rowValue = (IOLocation) tableModel.getValueAt(rowIndex, 0);

			List<Integer> arrayElementsToAdd = new ArrayList<Integer>();
			for (int a = 0; a < array.length; a++) {
				if (rowValue != null && rowValue.equals(array[a])) {
					rowIndex++;
					if (rowIndex < rowCount) {
						rowValue = (IOLocation) tableModel.getValueAt(rowIndex,
								0);
					} else {
						rowValue = null;
					}
				} else {
					arrayElementsToAdd.add(new Integer(a));
				}
			}
			if (rowIndex == rowCount) {
				// excellent! This means the table is an ordered subset of the
				// array.
				// new elements were inserted: that's all. This really is
				// scenario #2
				for (int a = 0; a < arrayElementsToAdd.size(); a++) {
					int index = (arrayElementsToAdd.get(a)).intValue();
					IOLocation loc = array[index];
					long date = 0;
					try {
						date = loc.getModificationDate();
					} catch (Exception e) {
						e.printStackTrace();
					}
					tableModel.insertRow(index, new Object[] { loc,
							new Date(date) });
				}

				/*
				 * used for debugging to validate the results:
				 * if(array.length!=tableModel.getRowCount()) throw new
				 * RuntimeException();
				 * 
				 * for(int a = 0; a<array.length; a++ ){ rowValue =
				 * (IOLocation)tableModel.getValueAt(a, 0);
				 * if(array[a].equals(rowValue)==false) throw new
				 * RuntimeException("a = "+a+", "+array[a]+", "+rowValue); }
				 */
				return;
			}
		}

		// scenarios #3 and #4:
		while (tableModel.getRowCount() > 0) {
			tableModel.removeRow(0);
		}

		if (array.length != 0)

			for (int a = 0; a < array.length; a++) {
				IOLocation loc = array[a];
				long date = 0;
				try {
					date = loc.getModificationDate();
				} catch (Exception e) {
					e.printStackTrace();
				}
				tableModel.addRow(new Object[] { loc, new Date(date) });
			}
	}

	@Override
	public int getVisibleLocationSize() {
		return tableModel.getRowCount();
	}

	MouseListener mouseListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 1) {
				openSelectedRow();
			}
		}
	};
	KeyListener enterKeyListener = new KeyAdapter() {
		@Override
		public void keyTyped(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				openSelectedRow();
				e.consume();
			}
		}
	};

	protected void openSelectedRow() {
		int row = table.getSelectedRow();
		if (row != -1) {
			IOLocation loc = (IOLocation) table.getValueAt(row, 0);
			if (loc.canRead() && loc.isDirectory()) {
				browser.getLocationHistory().append(loc);
			}
		}
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		syncGUIFromRealSelection.run();
		browser.getSelectionModel().addChangeListener(realModelListener);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		browser.getSelectionModel().removeChangeListener(realModelListener);
	}

	public ListLocationBrowserUI(LocationBrowser b) {
		super(b);

		tableModel.addColumn("Name");
		tableModel.addColumn("Date Modified");
		table = new JTable(tableModel) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean editCellAt(int row, int col,
					java.util.EventObject obj) {
				// TODO: edit this to let the user double-click to edit file
				// names
				// (if we want that.)
				return false;
			}
		};

		TableCellRenderer renderer = getTableCellRenderer();
		table.getColumnModel().getColumn(0).setCellRenderer(renderer);
		table.getColumnModel().getColumn(1).setCellRenderer(renderer);
		table.setRowHeight(18);

		table.addMouseListener(mouseListener);
		table.addKeyListener(enterKeyListener);
		// TODO: reinstate, but make abstract/codified. See IOLocationTileList
		// table.addKeyListener(typingListener);

		scrollPane = new JScrollPane(table);

		table.getSelectionModel().addListSelectionListener(guiListener);
	}

	protected abstract TableCellRenderer getTableCellRenderer();

	@Override
	protected void repaint(IOLocation loc, boolean thumbnail) {
		int rowCount = table.getRowCount();
		int rowHeight = table.getRowHeight();
		for (int row = 0; row < rowCount; row++) {
			if (table.getValueAt(row, 0) == loc) {
				int y = row * rowHeight;
				table.repaint(0, y, table.getWidth(), rowHeight);
				return;
			}
		}
	}

	@Override
	protected void installGUI(JComponent comp) {
		comp.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		comp.add(scrollPane, c);
	}
}