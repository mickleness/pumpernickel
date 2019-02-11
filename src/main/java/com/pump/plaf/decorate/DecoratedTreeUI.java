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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.AbstractLayoutCache.NodeDimensions;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * This <code>TreeUI</code> adds an arbitrary number of decorations on the right
 * side of the tree. These decorations are rendered with the
 * <code>TreeCellRenderer</code>, so they are not <code>JComponents</code>
 * capable of receiving events. However this UI is designed to recognize when
 * they are clicked, and therefore can notify <code>ActionListeners</code> so
 * the user will perceive these decorations as similar to buttons.
 * <p>
 * Regarding accessibility: you should not make decorations responsible for
 * essential functions. For example: a user cannot use the tab key to navigate
 * to a decoration, and there is no tooltip. These behaviors (and others) might
 * be essential behaviors for users with certain disabilities. I recommend
 * keeping menu items, contextual menus, and other redundant tools available to
 * also access features that decorations trigger.
 * <p>
 * This UI supplements the tree's <code>TreeCellRenderer</code> by placing the
 * component it returns inside another <code>TreeCellRenderer</code>, so the
 * original renderer should always be in tact.
 * <p>
 * There is a known bug that this UI cannot correctly transmit whether a tree
 * cell should have the keyboard focus, so that boolean is always relayed as
 * <code>false</code>.
 * 
 * @see <a
 *      href="https://javagraphics.blogspot.com/2012/05/treeuis-adding-decorations-to-cells.html">TreeUIs:
 *      Adding Decorations to Cells</a>
 */
public class DecoratedTreeUI extends BasicTreeUI {

	/**
	 * A tree must define this client property as an array of
	 * <code>TreeDecorations</code> for this UI to interact with the
	 * decorations.
	 */
	public static final String KEY_DECORATIONS = "pump.DecoratedTreeUI.Decorations";

	/**
	 * A <code>TreeDecoration</code> is an icon that is painted on the rightmost
	 * edge of the tree UI. This icon is not technically associated with a
	 * <code>JComponent</code>, but the <code>DecoratedTreeUI</code> will
	 * trigger ActionEvents when the icon is pressed.
	 */
	public interface TreeDecoration {
		/**
		 * Returns the icon this decoration should currently render.
		 * <p>
		 * It is assumed this icon will not change dimensions.
		 * 
		 * @param tree
		 *            the tree being rendered
		 * @param value
		 *            the tree node being rendered
		 * @param selected
		 *            whether the tree node is selected
		 * @param expanded
		 *            whether the tree node is expanded
		 * @param leaf
		 *            whether the tree node is a leaf node
		 * @param row
		 *            the row index of the tree node
		 * @param isRollover
		 *            whether the mouse is hovering over this decoration
		 * @param isPressed
		 *            whether the mouse is pressed to arm this icon, similar to
		 *            how buttons are armed.
		 * @return the icon this decoration should currently render.
		 */
		public abstract Icon getIcon(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean isRollover, boolean isPressed);

		/**
		 * Returns whether this decoration is visible.
		 * 
		 * @param tree
		 *            the tree being rendered
		 * @param value
		 *            the tree node being rendered
		 * @param selected
		 *            whether the tree node is selected
		 * @param expanded
		 *            whether the tree node is expanded
		 * @param leaf
		 *            whether the tree node is a leaf node
		 * @param row
		 *            the row index of the tree node
		 * @return whether this decoration is visible.
		 */
		public abstract boolean isVisible(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus);

		/**
		 * Returns an optional ActionListener to be notified when this
		 * decoration is clicked. If this returns null then no ActionEvent will
		 * be issued when this decoration is clicked.
		 * 
		 * @param tree
		 *            the tree being rendered
		 * @param value
		 *            the tree node being rendered
		 * @param selected
		 *            whether the tree node is selected
		 * @param expanded
		 *            whether the tree node is expanded
		 * @param leaf
		 *            whether the tree node is a leaf node
		 * @param row
		 *            the row index of the tree node
		 * @return an optional ActionListener to be notified when this
		 *         decoration is clicked.
		 */
		public abstract ActionListener getActionListener(JTree tree,
				Object value, boolean selected, boolean expanded, boolean leaf,
				int row, boolean hasFocus);
	}

	protected class ExtendedNodeDimensions extends NodeDimensions {
		NodeDimensions src;

		ExtendedNodeDimensions(NodeDimensions src) {
			this.src = src;
		}

		@Override
		public Rectangle getNodeDimensions(Object value, int row, int depth,
				boolean expanded, Rectangle bounds) {
			Rectangle r = src.getNodeDimensions(value, row, depth, expanded,
					bounds);
			int maxWidth = getMaxWidth();
			r.width = Math.max(0, maxWidth - r.x);
			return r;
		}
	}

	protected class DecoratedRenderer implements TreeCellRenderer {
		TreeCellRenderer renderer;
		GridBagConstraints c = new GridBagConstraints();
		JPanel panel = new JPanel(new GridBagLayout());

		DecoratedRenderer(TreeCellRenderer renderer) {
			this.renderer = renderer;
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			panel.removeAll();
			Component core = renderer.getTreeCellRendererComponent(tree, value,
					selected, expanded, leaf, row, hasFocus);
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
			c.anchor = GridBagConstraints.WEST;
			panel.add(core, c);

			c.weightx = 0;
			c.anchor = GridBagConstraints.EAST;
			JComponent[] decorations = getDecorations(tree, value, selected,
					expanded, leaf, row, hasFocus);
			for (int a = 0; a < decorations.length; a++) {
				c.gridx++;
				panel.add(decorations[a], c);
			}

			if (stretchBackgroundHighlight) {
				if (core instanceof DefaultTreeCellRenderer) {
					DefaultTreeCellRenderer r = (DefaultTreeCellRenderer) core;
					if (selected) {
						panel.setBackground(r.getBackgroundSelectionColor());
					} else {
						panel.setBackground(r.getBackgroundNonSelectionColor());
					}
				} else {
					panel.setBackground(core.getBackground());
				}
			} else {
				panel.setBackground(tree.getBackground());
			}

			return panel;
		}
	}

	static class ArmedDecorationInfo {
		TreeDecoration decoration;
		JTree tree;
		Object value;
		boolean selected, expanded, leaf;
		int row;
		Rectangle decorationBounds;

		ArmedDecorationInfo(TreeDecoration decoration, JTree tree,
				Object value, boolean selected, boolean expanded, boolean leaf,
				int row, Rectangle decorationBounds) {
			this.decoration = decoration;
			this.tree = tree;
			this.value = value;
			this.selected = selected;
			this.expanded = expanded;
			this.leaf = leaf;
			this.row = row;
			this.decorationBounds = decorationBounds;
		}
	}

	ArmedDecorationInfo armedDecoration;

	class DecoratedMouseAdapter extends MouseAdapter {
		MouseListener mouseListener;

		public DecoratedMouseAdapter(MouseListener originalListener) {
			this.mouseListener = originalListener;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			mouseListener.mouseClicked(e);
		}

		int lastRowIndex = -1;

		/**
		 * 
		 * @param repaint
		 *            force a repaint, intended for mouse presses/releases.
		 */
		private void repaintDecorations(boolean repaint) {
			int rowIndex = tree.getRowForLocation(mouseX, mouseY);
			if (rowIndex != lastRowIndex || repaint) {
				Rectangle lastBounds = tree.getRowBounds(lastRowIndex);
				if (lastBounds != null)
					tree.repaint(lastBounds);
			}
			Rectangle bounds = tree.getRowBounds(rowIndex);
			if (bounds != null)
				tree.repaint(bounds);
			lastRowIndex = rowIndex;
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
			repaintDecorations(false);
			if (mouseListener instanceof MouseMotionListener)
				((MouseMotionListener) mouseListener).mouseDragged(e);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			mouseListener.mouseEntered(e);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			mouseListener.mouseExited(e);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
			repaintDecorations(false);
			if (mouseListener instanceof MouseMotionListener)
				((MouseMotionListener) mouseListener).mouseMoved(e);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			mousePressed = e.getButton() == MouseEvent.BUTTON1;
			mouseX = e.getX();
			mouseY = e.getY();
			repaintDecorations(true);

			armedDecoration = getDecorationAtMouseLoc();

			mouseListener.mousePressed(e);
		}

		private ArmedDecorationInfo getDecorationAtMouseLoc() {
			int row = tree.getRowForLocation(mouseX, mouseY);
			boolean selected = tree.getSelectionModel().isRowSelected(row);
			boolean expanded = tree.isExpanded(row);
			TreePath path = tree.getPathForRow(row);
			if (path == null)
				return null;
			Object value = path.getLastPathComponent();
			boolean leaf = value instanceof TreeNode
					&& ((TreeNode) value).getChildCount() == 0;
			boolean hasFocus = tree.hasFocus();
			Rectangle rowBounds = tree.getRowBounds(row);
			TreeDecoration[] decorations = (TreeDecoration[]) tree
					.getClientProperty(KEY_DECORATIONS);
			if (decorations == null)
				return null;
			int maxX = rowBounds.x + rowBounds.width;
			for (int a = decorations.length - 1; a >= 0; a--) {
				if (decorations[a].isVisible(tree, value, selected, expanded,
						leaf, row, hasFocus)) {
					Icon normalIcon = decorations[a].getIcon(tree, value,
							selected, expanded, leaf, row, false, false);
					Dimension size = new Dimension(normalIcon.getIconWidth(),
							normalIcon.getIconHeight());
					int minX = maxX - size.width;
					if (mouseX >= minX && mouseX < maxX) {
						Rectangle decorationBounds = new Rectangle(minX,
								rowBounds.y, maxX - minX, rowBounds.height);
						return new ArmedDecorationInfo(decorations[a], tree,
								value, selected, expanded, leaf, row,
								decorationBounds);
					}
					maxX = minX;
				}
			}
			return null;
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			mousePressed = e.getButton() != MouseEvent.BUTTON1;
			mouseX = e.getX();
			mouseY = e.getY();
			repaintDecorations(true);
			mouseListener.mouseReleased(e);

			try {
				if (armedDecoration != null
						&& armedDecoration.decorationBounds.contains(mouseX,
								mouseY)) {
					ActionListener actionListener = armedDecoration.decoration
							.getActionListener(tree, armedDecoration.value,
									armedDecoration.selected,
									armedDecoration.expanded,
									armedDecoration.leaf, armedDecoration.row,
									false);
					if (actionListener != null) {
						actionListener.actionPerformed(new ActionEvent(
								armedDecoration.decoration, 0,
								"decoration click"));
						// this isn't really our responsibility, but a sloppy
						// decoration may forget to
						// repaint if something changed... (and a one-time
						// repaint won't hurt, right?)
						tree.repaint(armedDecoration.row);
					}
				}
			} finally {
				armedDecoration = null;
			}
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			if (mouseListener instanceof MouseWheelListener)
				((MouseWheelListener) mouseListener).mouseWheelMoved(e);
		}
	}

	protected int mouseX, mouseY;
	protected boolean mousePressed = false;
	protected boolean stretchBackgroundHighlight;

	/**
	 * Create a DecoratedTreeUI that stretchs the background highlight.
	 */
	public DecoratedTreeUI() {
		this(true);
	}

	/**
	 * Create a DecoratedTreeUI.
	 * 
	 * @param stretchBackgroundHighlight
	 *            whether the background highlight should be stretched to the
	 *            width of the tree or not.
	 */
	public DecoratedTreeUI(boolean stretchBackgroundHighlight) {
		this.stretchBackgroundHighlight = stretchBackgroundHighlight;
	}

	private transient List<JLabel> labels = new ArrayList<JLabel>();
	int insideGetDecorations = 0;

	protected JComponent[] getDecorations(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		/**
		 * This can lead to a recursive loop when calculating bounds if we let
		 * it...
		 */
		if (insideGetDecorations > 0)
			return new JComponent[] {};

		insideGetDecorations++;
		try {
			TreeDecoration[] decorations = (TreeDecoration[]) tree
					.getClientProperty(KEY_DECORATIONS);
			if (decorations == null)
				return new JComponent[] {};
			Rectangle rowBounds = tree.getRowBounds(row);
			if (rowBounds == null)
				return new JComponent[] {};

			JLabel[] c = new JLabel[decorations.length];
			for (int a = 0; a < c.length; a++) {
				if (a >= labels.size()) {
					JLabel label = new JLabel();
					label.setBorder(null);
					labels.add(label);
				}
				boolean isVisible = decorations[a].isVisible(tree, value,
						selected, expanded, leaf, row, hasFocus);
				c[a] = labels.get(a);
				c[a].setVisible(isVisible);
			}
			int maxX = rowBounds.x + rowBounds.width;
			for (int a = c.length - 1; a >= 0; a--) {
				if (c[a].isVisible()) {
					Icon normalIcon = decorations[a].getIcon(tree, value,
							selected, expanded, leaf, row, false, false);
					Dimension size = new Dimension(normalIcon.getIconWidth(),
							normalIcon.getIconHeight());
					int minX = maxX - size.width;
					if (mousePressed && armedDecoration != null
							&& armedDecoration.value == value
							&& armedDecoration.decoration == decorations[a]) {
						Icon pressedIcon = decorations[a].getIcon(tree, value,
								selected, expanded, leaf, row, true, true);
						c[a].setIcon(pressedIcon);
					} else if ((!mousePressed) && mouseX >= minX
							&& mouseX < maxX && mouseY >= rowBounds.y
							&& mouseY < (rowBounds.y + rowBounds.height)) {
						Icon rolloverIcon = decorations[a].getIcon(tree, value,
								selected, expanded, leaf, row, true, false);
						c[a].setIcon(rolloverIcon);
					} else {
						c[a].setIcon(normalIcon);
					}

					maxX = minX;
				} else {
					c[a].setIcon(null);
				}
			}
			return c;
		} finally {
			insideGetDecorations--;
		}
	}

	protected KeyAdapter keyListener = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				int row = tree.getLeadSelectionRow();
				if (row >= 0) {
					tree.expandRow(row);
				}
				e.consume();
			} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				int row = tree.getLeadSelectionRow();
				if (row >= 0) {
					tree.collapseRow(row);
				}
				e.consume();
			}
		}
	};

	protected ComponentListener invalidationComponentListener = new ComponentAdapter() {
		@Override
		public void componentResized(ComponentEvent e) {
			treeState.invalidateSizes();
		}
	};

	private int getMaxWidth() {
		int maxWidth = tree.getWidth();
		if (tree.getParent() instanceof JViewport) {
			JViewport viewport = (JViewport) tree.getParent();
			maxWidth = viewport.getViewRect().width;
		}
		return maxWidth;
	}

	@Override
	protected NodeDimensions createNodeDimensions() {
		NodeDimensions returnValue = super.createNodeDimensions();
		return new ExtendedNodeDimensions(returnValue);
	}

	/**
	 * This is copied from BasicTreeUI.java. The only modifications are:
	 * <ul>
	 * <li>The background color is painted across the entire width of the tree.</li>
	 * <li>Because of the above requirement: the expand control is redundantly
	 * painted on top of that rectangle for visibility.</li>
	 * <li>We never inform the CellRenderer whether focus is present. (That
	 * argument is left false, because we don't have access to private fields
	 * the original logic wanted.)</li>
	 * </ul>
	 */
	@Override
	protected void paintRow(Graphics g, Rectangle clipBounds, Insets insets,
			Rectangle bounds, TreePath path, int row, boolean isExpanded,
			boolean hasBeenExpanded, boolean isLeaf) {
		// Don't paint the renderer if editing this row.
		if (editingComponent != null && editingRow == row)
			return;

		// this is in BasicTreeUI, but getLeadSelectionRow() is private.
		/*
		 * int leadIndex;
		 * 
		 * if(tree.hasFocus()) { leadIndex = getLeadSelectionRow(); } else
		 * leadIndex = -1;
		 */

		Component component;

		component = currentCellRenderer.getTreeCellRendererComponent(tree,
				path.getLastPathComponent(), tree.isRowSelected(row),
				isExpanded, isLeaf, row, false);

		if (component.isOpaque()) {
			Color bkgnd = component.getBackground();
			g.setColor(bkgnd);
			g.fillRect(0, bounds.y, tree.getWidth(), bounds.height);

			if (shouldPaintExpandControl(path, row, isExpanded,
					hasBeenExpanded, isLeaf)) {
				paintExpandControl(g, bounds, insets, bounds, path, row,
						isExpanded, hasBeenExpanded, isLeaf);
			}
		}

		rendererPane.paintComponent(g, component, tree, bounds.x, bounds.y,
				bounds.width, bounds.height, true);
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		if (tree != null) {
			tree.addComponentListener(invalidationComponentListener);
			tree.addKeyListener(keyListener);
		}
	}

	@Override
	public void uninstallUI(JComponent c) {
		if (tree != null) {
			tree.removeComponentListener(invalidationComponentListener);
			tree.removeKeyListener(keyListener);
		}
		super.uninstallUI(c);
	}

	@Override
	protected void updateRenderer() {
		super.updateRenderer();
		if (currentCellRenderer != null) {
			if (!(currentCellRenderer instanceof DecoratedRenderer)) {
				currentCellRenderer = new DecoratedRenderer(currentCellRenderer);
			}
		}
	}

	@Override
	protected MouseListener createMouseListener() {
		MouseAdapter decoratedMouseAdapter = new DecoratedMouseAdapter(
				super.createMouseListener());
		return decoratedMouseAdapter;
	}
}