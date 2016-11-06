/*
 * @(#)SourceListUI.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2011 by Jeremy Wood.
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
package com.pump.plaf;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.plaf.basic.BasicListUI;


/** This abstract {@code ListUI} offers subclasses the chance to customize
 * how the rectangle behind the selected cell is rendered.
 * This was originally intended for "Source List" components in file dialogs.
 * <P>If this UI is being used, it is important to use a {@code ListCellRenderer}
 * that is not opaque.  If it is opaque then it will cover the background
 * rendered by <code>paintBackground</code>.
 * 
 */
public abstract class SourceListUI extends BasicListUI {

	@Override
	protected void paintCell(Graphics g0, int row, Rectangle rowBounds,
			ListCellRenderer cellRenderer, ListModel dataModel,
			ListSelectionModel selModel, int leadIndex) {

		Graphics2D g = (Graphics2D)g0;
        Object value = dataModel.getElementAt(row);
        boolean cellHasFocus = list.hasFocus() && (row == leadIndex);
        boolean isSelected = selModel.isSelectedIndex(row);
        
		paintBackground(g, rowBounds, value, row, isSelected, cellHasFocus);
		super.paintCell(g, row, rowBounds, cellRenderer, dataModel, selModel,
						leadIndex);
	}

	protected abstract void paintBackground(Graphics2D g,Rectangle rowBounds, Object value, int row,
			boolean isSelected, boolean cellHasFocus);
}
