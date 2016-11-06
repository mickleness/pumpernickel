/*
 * @(#)TableSearchDialog.java
 *
 * $Date: 2015-09-13 14:46:53 -0400 (Sun, 13 Sep 2015) $
 *
 * Copyright (c) 2015 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */
package com.pump.swing;

import javax.swing.JTable;

public class TableSearchDialog extends AbstractSearchDialog 
{

	private static final long serialVersionUID = 1L;

	/** This creates a <code>TableSearchDialog</code> for the user
	 * to search a table.
	 */
	public static void search(JTable table) {
		TableSearchDialog dialog = new TableSearchDialog(table);
		dialog.setVisible(true);
	}
	
	protected JTable table;
	
	public TableSearchDialog(JTable table)
	{
		super(table);
		this.table = table;
	}

	@Override
	protected boolean doNextSearch(boolean forward)
	{
		return SwingSearch.find(table, textField.getText(), forward, false);
	}

}
