package com.pump.inspector;

import java.awt.Component;
import java.awt.Component.BaselineResizeBehavior;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicPanelUI;

/**
 * This is a grid of components that flows left-to-right with a fixed number of
 * columns. If a component is invisible: it is skipped and its allocated cell is
 * given to the next component.
 * <p>
 * This layout has a notion of container groups. All the groups that share this
 * layout will be consulted together to allocate the width/height of each
 * column/row.
 * <p>
 * The original intent of this layout is to be used with an Inspector. Imagine
 * one row that contains a grid of 5 checkboxes, and another row contains a grid
 * of 8 checkboxes. This layout lets those grids align, even if there are other
 * rows of controls between them.
 */
public class ControlGridLayout implements LayoutManager {
	class TopBaselinePanelUI extends BasicPanelUI {

		@Override
		public int getBaseline(JComponent c, int width, int height) {
			Map<Component, Rectangle> blueprint = ControlGridLayout.this
					.createBlueprint(c);

			for (Component child : c.getComponents()) {
				Rectangle r = blueprint.get(child);
				if (r != null) {
					int y = child.getBaseline(r.width, r.height);
					return y + r.y;
				}
			}

			return super.getBaseline(c, width, height);
		}

		@Override
		public BaselineResizeBehavior getBaselineResizeBehavior(JComponent c) {
			return BaselineResizeBehavior.CONSTANT_ASCENT;
		}

	}

	int columns;
	int horizontalPadding = 2;
	int verticalPadding = 2;
	Collection<Container> group = new HashSet<>();

	public ControlGridLayout(int columns) {
		if (columns <= 0)
			throw new IllegalArgumentException(
					"columns must be greater than zero (" + columns + ")");
		this.columns = columns;
	}

	public void addContainer(Container parent) {
		group.add(parent);
	}

	public JPanel createGrid(JComponent... components) {
		JPanel panel = new JPanel();
		panel.setUI(new TopBaselinePanelUI());
		panel.putClientProperty(Inspector.PROPERTY_WRAPPED, Boolean.TRUE);
		addContainer(panel);
		panel.setOpaque(false);
		panel.setLayout(this);
		for (JComponent c : components) {
			c.setOpaque(false);
			panel.add(c);
		}
		return panel;
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
	}

	@Override
	public void removeLayoutComponent(Component comp) {
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		Map<Component, Rectangle> blueprint = createBlueprint(parent);
		Rectangle bounds = new Rectangle(0, 0, 0, 0);
		for (Entry<Component, Rectangle> entry : blueprint.entrySet()) {
			bounds.add(entry.getValue());
		}
		if (parent instanceof JComponent) {
			Border b = ((JComponent) parent).getBorder();
			if (b != null) {
				Insets i = b.getBorderInsets(parent);
				bounds.width += i.right;
				bounds.height += i.bottom;
			}
		}
		return new Dimension(bounds.width, bounds.height);
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return preferredLayoutSize(parent);
	}

	@Override
	public void layoutContainer(Container parent) {
		Map<Component, Rectangle> blueprint = createBlueprint(parent);
		for (Entry<Component, Rectangle> entry : blueprint.entrySet()) {
			entry.getKey().setBounds(entry.getValue());
		}
	}

	private static class CellLayout {
		int[] rowHeights;
		int[] columnWidths;

		public CellLayout() {
			rowHeights = new int[0];
			columnWidths = new int[0];
		}

		public CellLayout(int[] columnWidths, int[] rowHeights) {
			this.columnWidths = columnWidths;
			this.rowHeights = rowHeights;
		}

		public void add(CellLayout other) {
			rowHeights = add(rowHeights, other.rowHeights);
			columnWidths = add(columnWidths, other.columnWidths);
		}

		private int[] add(int[] z1, int[] z2) {
			int[] returnValue = z2.length > z1.length ? new int[z2.length] : z1;
			for (int a = 0; a < returnValue.length; a++) {
				int v1 = a < z1.length ? z1[a] : 0;
				int v2 = a < z2.length ? z2[a] : 0;
				returnValue[a] = Math.max(v1, v2);
			}
			return returnValue;
		}
	}

	private Map<Component, Rectangle> createBlueprint(Container parent) {
		CellLayout masterLayout;
		if (group.contains(parent)) {
			masterLayout = new CellLayout();
			for (Container container : group) {
				if (container.isVisible()) {
					CellLayout cellLayout = getCellLayout(container);
					masterLayout.add(cellLayout);
				}
			}
		} else {
			masterLayout = getCellLayout(parent);
		}

		int x = 0;
		int y = 0;
		if (parent instanceof JComponent) {
			Border b = ((JComponent) parent).getBorder();
			if (b != null) {
				Insets i = b.getBorderInsets(parent);
				x = i.left;
				y = i.top;
			}
		}

		List<List<Component>> grid = createGrid(parent);

		Map<Component, Rectangle> returnValue = new HashMap<>();
		int y0 = y;
		for (int row = 0; row < grid.size(); row++) {
			int x0 = x;
			for (int column = 0; column < grid.get(row).size(); column++) {
				Component c = grid.get(row).get(column);
				Dimension d = c.getPreferredSize();
				Rectangle r = new Rectangle(x0, y0, d.width, d.height);
				// masterLayout.columnWidths[column],
				// masterLayout.rowHeights[row]);
				returnValue.put(c, r);

				x0 += masterLayout.columnWidths[column];
				x0 += horizontalPadding;
			}
			y0 += masterLayout.rowHeights[row];
			y0 += verticalPadding;
		}

		return returnValue;
	}

	private List<List<Component>> createGrid(Container container) {
		int rowIndex = 0;
		List<List<Component>> grid = new ArrayList<>();
		for (Component child : container.getComponents()) {
			if (child.isVisible()) {
				List<Component> currentRow;
				if (rowIndex >= grid.size()) {
					currentRow = new ArrayList<>(columns);
					grid.add(currentRow);
				} else {
					currentRow = grid.get(rowIndex);
				}
				currentRow.add(child);

				if (currentRow.size() == columns) {
					rowIndex++;
				}
			}
		}
		return grid;
	}

	private CellLayout getCellLayout(Container container) {
		List<List<Component>> grid = createGrid(container);

		Map<Component, Dimension> preferredSizeMap = new HashMap<>();
		int[] rowHeights = new int[grid.size()];
		for (int row = 0; row < grid.size(); row++) {
			int height = 0;
			for (Component c : grid.get(row)) {
				Dimension d = c.getPreferredSize();
				preferredSizeMap.put(c, d);
				height = Math.max(height, d.height);
			}
			rowHeights[row] = height;
		}

		int[] columnWidths = new int[columns];
		for (int column = 0; column < columns; column++) {
			columnWidths[column] = 0;
			for (int row = 0; row < grid.size(); row++) {
				if (column < grid.get(row).size()) {
					Component c = grid.get(row).get(column);
					Dimension d = preferredSizeMap.get(c);
					columnWidths[column] = Math.max(columnWidths[column],
							d.width);
				}
			}
		}
		return new CellLayout(columnWidths, rowHeights);
	}
}