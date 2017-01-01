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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.basic.BasicListUI;

public class DecoratedListUI extends BasicListUI {
	
	public abstract static class ListDecoration {

		/** Returns the icon this decoration should currently render.
		 * <p>It is assumed this icon will not change dimensions.
		 * 
		 * @param list the list being rendered
		 * @param value the list value being rendered
		 * @param row the row index being rendered
		 * @param isSelected whether this list cell is selected
		 * @param cellHasFocus whether the list cell has focus
		 * @param isRollover whether the mouse is hovering over this decoration
		 * @param isPressed whether the mouse is pressed to arm this icon, similar to how
		 * buttons are armed.
		 */
		public abstract Icon getIcon(JList list, Object value,int row,boolean isSelected,boolean cellHasFocus, boolean isRollover,boolean isPressed);
		
		/** Returns whether this decoration is visible.
		 * 
		 * @param list the list being rendered
		 * @param value the list value being rendered
		 * @param row the row index being rendered
		 * @param isSelected whether this list cell is selected
		 * @param cellHasFocus whether the list cell has focus
		 * @return whether this decoration is visible.
		 */
		public abstract boolean isVisible(JList list, Object value,int row,boolean isSelected,boolean cellHasFocus);
		
		/** Returns an optional ActionListener to be notified when this decoration is clicked.
		 * If this returns null then no ActionEvent will be issued when this decoration is clicked.
		 * 
		 * @param list the list being rendered
		 * @param value the list value being rendered
		 * @param row the row index being rendered
		 * @param isSelected whether this list cell is selected
		 * @param cellHasFocus whether the list cell has focus
		 */
		public ActionListener getActionListener(JList list, Object value,int row,boolean isSelected,boolean cellHasFocus) {
			return null;
		}

		/** Returns the position this decoration be painted at.
		 * 
		 * @param list the list being rendered
		 * @param value the list value being rendered
		 * @param row the row index being rendered
		 * @param isSelected whether this list cell is selected
		 * @param cellHasFocus whether the list cell has focus
		 */
		public abstract Point getLocation(JList list, Object value,int row,boolean isSelected,boolean cellHasFocus);
	}
	
	private static class CellInfo {
		int cellIndex;
		JList list;
		CellInfo(JList list, int cellIndex) {
			this.list = list;
			this.cellIndex = cellIndex;
		}
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof CellInfo))
				return false;
			CellInfo i = (CellInfo)obj;
			if(i.cellIndex!=cellIndex)
				return false;
			if(i.list!=list)
				return false;
			return true;
		}
		@Override
		public int hashCode() {
			return cellIndex;
		}
	}
	
	/** A ListDecoration that continually repaints itself as long as it is visible.
	 * This is used for animating decorations.
	 */
	public static class RepaintingListDecoration extends ListDecoration {
		Timer repaintTimer;
		ActionListener repaintListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CellInfo[] cells;
				synchronized(repaintingCells) {
					cells = repaintingCells.toArray(new CellInfo[repaintingCells.size()]);
					if(cells.length==0) {
						repaintTimer.stop();
					}
				}
				for(int a = 0; a<cells.length; a++) {
					Rectangle rowBounds = cells[a].list.getCellBounds(cells[a].cellIndex, cells[a].cellIndex);
					cells[a].list.repaint(rowBounds);
				}
			}
		};
		Set<CellInfo> repaintingCells = new HashSet<CellInfo>();
		protected final ListDecoration decoration;
		
		//TODO: uncomment this method when the BasicListDecoration exists
		//public RepaintingListDecoration(Icon normalIcon,int repaintInterval) {
		//	this(new BasicListDecoration(normalIcon), repaintInterval);
		//}
		
		public RepaintingListDecoration(ListDecoration listDecoration,int repaintInterval) {
			if(listDecoration==null) throw new NullPointerException();
			this.decoration = listDecoration;
			repaintTimer = new Timer(repaintInterval, repaintListener);
		}

		@Override
		public Icon getIcon(JList list, Object value, int row,
				boolean isSelected, boolean cellHasFocus, boolean isRollover,
				boolean isPressed) {
			return decoration.getIcon(list, value, row, isSelected, cellHasFocus, isRollover, isPressed);
		}

		/** Returns whether this decoration should be visible.
		 * <p>Do not override this method. To customize the visibility of
		 * this object, change the <code>ListDecoration</code> this decoration
		 * delegates to.
		 */
		@Override
		public final boolean isVisible(JList list, Object value, int row,
				boolean isSelected, boolean cellHasFocus) {
			boolean returnValue = decoration.isVisible(list, value, row, isSelected, cellHasFocus);
			synchronized(repaintingCells) {
				CellInfo cellInfo = new CellInfo(list, row);
				if(returnValue) {
					if(repaintingCells.add(cellInfo) && (!repaintTimer.isRunning())) {
						repaintTimer.start();
					}
				} else {
					repaintingCells.remove(cellInfo);
				}
			}
			return returnValue;
		}

		@Override
		public ActionListener getActionListener(JList list, Object value,
				int row, boolean isSelected, boolean cellHasFocus) {
			return decoration.getActionListener(list, value, row, isSelected, cellHasFocus);
		}

		@Override
		public Point getLocation(JList list, Object value, int row,
				boolean isSelected, boolean cellHasFocus) {
			return decoration.getLocation(list, value, row, isSelected, cellHasFocus);
		}
	}
	
	/** A list must define this client property as an array of <code>ListDecorations</code>
	 * for this UI to interact with the decorations.
	 */
	public static final String KEY_DECORATIONS = "bric.DecoratedListUI.Decorations";
	
	static class ArmedDecorationInfo {
		ListDecoration decoration;
		JList list;
		Object value;
		boolean selected;
		int row;
		Rectangle decorationBounds;
		
		ArmedDecorationInfo(ListDecoration decoration,JList list,Object value,boolean selected,int row,Rectangle decorationBounds) {
			this.decoration = decoration;
			this.list = list;
			this.value = value;
			this.selected = selected;
			this.row = row;
			this.decorationBounds = decorationBounds;
		}
	}

	ArmedDecorationInfo armedDecoration;
	int mouseX, mouseY;
	
	class DecorationMouseListener implements MouseInputListener {
		MouseInputListener mouseListener;
		boolean delegatingToMouseListener = false;
		
		DecorationMouseListener(MouseInputListener l) {
			mouseListener = l;
		}

		public void mouseClicked(MouseEvent e) {
			if(delegatingToMouseListener)
				mouseListener.mouseClicked(e);
		}

		public void mouseEntered(MouseEvent e) {
			mouseListener.mouseEntered(e);
		}

		public void mouseExited(MouseEvent e) {
			mouseListener.mouseExited(e);
		}

		public void mousePressed(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
			repaintDecorations(true);
			
			armedDecoration = getDecorationAtMouseLoc();
			delegatingToMouseListener = armedDecoration==null;
			if(delegatingToMouseListener) {
				mouseListener.mousePressed(e);
			}
		}

		public void mouseReleased(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
			repaintDecorations(true);
			if(delegatingToMouseListener)
				mouseListener.mouseReleased(e);
			
			try {
				if(armedDecoration!=null && armedDecoration.decorationBounds.contains(mouseX, mouseY)) {
					ActionListener actionListener = armedDecoration.decoration.getActionListener(list, 
							armedDecoration.value,  
							armedDecoration.row, 
							false, false );
					if(actionListener!=null) {
						actionListener.actionPerformed(new ActionEvent(armedDecoration.decoration, 0, "decoration click"));
						//this isn't really our responsibility, but a sloppy decoration may forget to
						//repaint if something changed... (and a one-time repaint won't hurt, right?)
						list.repaint(armedDecoration.row);
					}
				}
			} finally {
				armedDecoration = null;
			}
		}

		public void mouseDragged(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
			repaintDecorations(false);
			if(delegatingToMouseListener)
				mouseListener.mouseDragged(e);
		}

		public void mouseMoved(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
			repaintDecorations(false);
			mouseListener.mouseMoved(e);
		}

		int lastCellIndex = -1;
		/** 
		 * 
		 * @param repaint force a repaint, intended for mouse presses/releases.
		 */
		private void repaintDecorations(boolean repaint) {
			int cellIndex = locationToIndex(list, new Point(mouseX, mouseY));
			if(cellIndex!=lastCellIndex || repaint) {
				Rectangle lastBounds = list.getCellBounds(lastCellIndex, lastCellIndex);
				if(lastBounds!=null)
					list.repaint(lastBounds);
			}
			Rectangle bounds = list.getCellBounds(cellIndex, cellIndex);
			if(bounds!=null)
				list.repaint(bounds);
			lastCellIndex = cellIndex;
		}
		
		private ArmedDecorationInfo getDecorationAtMouseLoc() {
			int index = locationToIndex(list, new Point(mouseX, mouseY));
			if(index<0 || index>=list.getModel().getSize())
				return null;
			Object value = list.getModel().getElementAt(index);
			ListDecoration[] decorations = getDecorations(list);
			if(decorations==null) return null;
			Rectangle cellBounds = list.getCellBounds(index, index);
			
			boolean isSelected = list.isSelectedIndex(index);
			for(int a = decorations.length-1; a>=0; a--) {
				if(decorations[a].isVisible(list, value, index, isSelected, false)) {
					Icon icon = decorations[a].getIcon(list, value, index, isSelected, false, false, false);
					int width = icon.getIconWidth();
					int height = icon.getIconHeight();
					Point p = decorations[a].getLocation(list, value, index, isSelected, false);
					if(mouseX >= p.x + cellBounds.x && 
							mouseX < p.x + cellBounds.x + width && 
							mouseY >= p.y + cellBounds.y && 
							mouseY < p.y + cellBounds.y + height) {
						Rectangle decorationBounds = new Rectangle(p.x + cellBounds.x, 
								p.y + cellBounds.y, 
								width, height);
						return new ArmedDecorationInfo(decorations[a], list, value, isSelected, index, decorationBounds);
					}
				}
			}
			return null;
		}
	}
	
	/** Returns a non-null array of the decorations associated with this list.
	 */
	protected ListDecoration[] getDecorations(JList list) {
		ListDecoration[] decorations = (ListDecoration[])list.getClientProperty(KEY_DECORATIONS);
		if(decorations==null) return new ListDecoration[] {};
		return decorations;
	}

	@Override
    protected void paintCell(
        Graphics g,
        int row,
        Rectangle rowBounds,
        ListCellRenderer cellRenderer,
        ListModel dataModel,
        ListSelectionModel selModel,
        int leadIndex) {
		super.paintCell(g, row, rowBounds, cellRenderer, dataModel, selModel, leadIndex);
		

        Object value = dataModel.getElementAt(row);
        boolean cellHasFocus = list.hasFocus() && (row == leadIndex);
        boolean isSelected = selModel.isSelectedIndex(row);

        ListDecoration[] decorations = getDecorations(list);
        for(int a = 0; a<decorations.length; a++) {
        	if( decorations[a].isVisible(list, value, row, isSelected, cellHasFocus) ) {
        		Point p = decorations[a].getLocation(list, value, row, isSelected, cellHasFocus);
        		Icon icon;
        		if(armedDecoration!=null && armedDecoration.value==value && armedDecoration.decoration==decorations[a]) {
        			icon = decorations[a].getIcon(list, value, row, isSelected, cellHasFocus, false, true);
        		} else if(rowBounds.contains(mouseX, mouseY)) {
        			icon = decorations[a].getIcon(list, value, row, isSelected, cellHasFocus, true, false);
        		} else {
        			icon = decorations[a].getIcon(list, value, row, isSelected, cellHasFocus, false, false);
        		}
        		Graphics g2 = g.create();
        		try {
        			icon.paintIcon(list, g2, rowBounds.x + p.x, rowBounds.y + p.y);
        		} finally {
        			g2.dispose();
        		}
        	}
        }
	}

	@Override
	protected MouseInputListener createMouseInputListener() {
		return new DecorationMouseListener( super.createMouseInputListener() );
	}
}