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
package com.pump.plaf.decorate;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.tree.TreePath;

/**
 * A <code>ListDecoration</code> and <code>TreeDecoration</code> that
 * continually repaints itself as long as it is visible. This is used for
 * animating decorations.
 */
public class RepaintingDecoration implements ListTreeDecoration {

	private static class CellInfo {
		JList list;
		JTree tree;
		int row;
		Object value;

		CellInfo(JList list, int row) {
			this.row = row;
			this.value = row >= 0 && row < list.getModel().getSize() ? list
					.getModel().getElementAt(row) : null;
			this.list = list;
		}

		CellInfo(JTree tree, int row) {
			this.row = row;
			TreePath path = tree.getPathForRow(row);
			this.value = path == null ? null : path.getLastPathComponent();
			this.tree = tree;
		}

		@Override
		public int hashCode() {
			return row;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof CellInfo))
				return false;
			CellInfo other = (CellInfo) obj;
			if (other.row != row)
				return false;
			if (other.list != list)
				return false;
			if (other.tree != tree)
				return false;
			if (!Objects.equals(value, other.value))
				return false;
			return true;
		}

		/**
		 * 
		 * @return true if this CellInfo should persist and keep repainting.
		 *         False if the list/tree appears to have changed and this
		 *         CellInfo should be purged from the repeating timer.
		 */
		boolean repaint() {
			if (list != null) {
				Rectangle rowBounds = list.getCellBounds(row, row);
				if (rowBounds != null)
					list.repaint(rowBounds);
				if (rowBounds == null || !equals(new CellInfo(list, row))) {
					return false;
				}
				return true;
			}

			Rectangle rowBounds = tree.getRowBounds(row);
			if (rowBounds != null)
				tree.repaint(rowBounds);
			if (rowBounds == null || !equals(new CellInfo(tree, row))) {
				return false;
			}
			return true;
		}
	}

	Timer repaintTimer;
	ActionListener repaintListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			CellInfo[] cells;
			synchronized (repaintingCells) {
				cells = repaintingCells.toArray(new CellInfo[repaintingCells
						.size()]);
				if (cells.length == 0) {
					repaintTimer.stop();
				}
			}
			for (int a = 0; a < cells.length; a++) {
				if (!cells[a].repaint()) {
					synchronized (repaintingCells) {
						repaintingCells.remove(cells[a]);
					}
				}
			}
		}
	};
	Set<CellInfo> repaintingCells = new HashSet<CellInfo>();
	protected final ListTreeDecoration decoration;

	public RepaintingDecoration(Icon normalIcon, int repaintInterval) {
		this(new BasicDecoration(normalIcon), repaintInterval);
	}

	public RepaintingDecoration(ListTreeDecoration listTreeDecoration,
			int repaintInterval) {
		Objects.requireNonNull(listTreeDecoration);
		this.decoration = listTreeDecoration;
		repaintTimer = new Timer(repaintInterval, repaintListener);
	}

	@Override
	public Icon getIcon(JList list, Object value, int row, boolean isSelected,
			boolean cellHasFocus, boolean isRollover, boolean isPressed) {
		return decoration.getIcon(list, value, row, isSelected, cellHasFocus,
				isRollover, isPressed);
	}

	/**
	 * Returns whether this decoration should be visible.
	 * <p>
	 * Do not override this method. To customize the visibility of this object,
	 * change the <code>ListDecoration</code> this decoration delegates to.
	 */
	@Override
	public final boolean isVisible(JList list, Object value, int row,
			boolean isSelected, boolean cellHasFocus) {
		boolean returnValue = decoration.isVisible(list, value, row,
				isSelected, cellHasFocus);
		synchronized (repaintingCells) {
			CellInfo cellInfo = new CellInfo(list, row);
			if (returnValue) {
				if (repaintingCells.add(cellInfo)
						&& (!repaintTimer.isRunning())) {
					repaintTimer.start();
				}
			} else {
				repaintingCells.remove(cellInfo);
			}
		}
		return returnValue;
	}

	@Override
	public ActionListener getActionListener(JList list, Object value, int row,
			boolean isSelected, boolean cellHasFocus) {
		return decoration.getActionListener(list, value, row, isSelected,
				cellHasFocus);
	}

	@Override
	public Point getLocation(JList list, Object value, int row,
			boolean isSelected, boolean cellHasFocus) {
		return decoration.getLocation(list, value, row, isSelected,
				cellHasFocus);
	}

	/**
	 * Returns whether this decoration should be visible.
	 * <p>
	 * Do not override this method. To customize the visibility of this object,
	 * change the <code>TreeDecoration</code> this decoration delegates to.
	 */
	@Override
	public final boolean isVisible(JTree tree, Object value, boolean selected,
			boolean expanded, boolean leaf, int row, boolean hasFocus) {
		/*
		 * This method is final because this implementation is what guarantees
		 * this decoration will continually repeat. I overrode this by mistake
		 * when I wrote the PulsingTreeDecoration, and ended up with a timer
		 * that continually repainted even when the decoration was not actually
		 * visible. Instead the correct behavior is for the decoration field to
		 * have specialized visibility instructions.
		 */
		boolean returnValue = decoration.isVisible(tree, value, selected,
				expanded, leaf, row, hasFocus);
		synchronized (repaintingCells) {
			CellInfo cellInfo = new CellInfo(tree, row);
			if (returnValue) {
				if (repaintingCells.add(cellInfo)
						&& (!repaintTimer.isRunning())) {
					repaintTimer.start();
				}
			} else {
				repaintingCells.remove(cellInfo);
			}
		}
		return returnValue;
	}

	@Override
	public Icon getIcon(JTree tree, Object value, boolean selected,
			boolean expanded, boolean leaf, int row, boolean isRollover,
			boolean isPressed) {
		return decoration.getIcon(tree, value, selected, expanded, leaf, row,
				isRollover, isPressed);
	}

	@Override
	public ActionListener getActionListener(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		return decoration.getActionListener(tree, value, selected, expanded,
				leaf, row, hasFocus);
	}
}