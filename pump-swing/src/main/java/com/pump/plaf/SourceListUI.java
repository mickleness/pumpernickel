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