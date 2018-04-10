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

import javax.swing.JTable;

public class TableSearchDialog extends AbstractSearchDialog {

	private static final long serialVersionUID = 1L;

	/**
	 * This creates a <code>TableSearchDialog</code> for the user to search a
	 * table.
	 */
	public static void search(JTable table) {
		TableSearchDialog dialog = new TableSearchDialog(table);
		dialog.setVisible(true);
	}

	protected JTable table;

	public TableSearchDialog(JTable table) {
		super(table);
		this.table = table;
	}

	@Override
	protected boolean doNextSearch(boolean forward) {
		return SwingSearch.find(table, textField.getText(), forward, false);
	}

}