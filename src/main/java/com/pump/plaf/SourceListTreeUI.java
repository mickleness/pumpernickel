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

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreePath;

import com.pump.icon.TriangleIcon;

/**
 * This {@code TreeUI} resembles Apple's "Source List" component. Specifically
 * this UI is customized to: <LI>Highlights the entire row (from x=0 to
 * x=width). (Normally only a minimum width is highlighted.) <LI>Make room
 * collapse/expand icons when the root is not visible. (Normally these would be
 * invisible unless the root was visible.)</LI>
 * <P>
 * If this UI is being used, it is important to use a {@code TreeCellRenderer}
 * that is not opaque. If it is opaque then it will partially cover the
 * background. Also the renderer should highlight the selected rows in a light
 * color (such as white) to contrast against the rich blue highlight that will
 * be behind them.
 * <P>
 * Note: in iTunes there are some nodes that cannot be collapsed. It would not
 * be hard to rig this class to not show the expand/collapse arrows for certain
 * nodes... the key trick would
 * 
 */
class SourceListTreeUI extends BasicTreeUI {
    protected static final Icon expandedIcon = new TriangleIcon(
	    SwingConstants.SOUTH, 9, 8, new Color(0x8993A0));
    protected static final Icon expandedSelectedIcon = new TriangleIcon(
	    SwingConstants.SOUTH, 9, 8, new Color(0xD9E4F3));
    protected static final Icon collapsedIcon = new TriangleIcon(
	    SwingConstants.EAST, 8, 10, new Color(0x8993A0));
    protected static final Icon collapsedSelectedIcon = new TriangleIcon(
	    SwingConstants.EAST, 8, 10, new Color(0xD9E4F3));

    /**
     * Nudge nodes that are children of the root this many pixels to the right
     * to show the expand/collapse arrows.
     * 
     */
    private static final int BASE_OFFSET = 20;

    PropertyChangeListener activeListener = new PropertyChangeListener() {

	public void propertyChange(PropertyChangeEvent evt) {
	    updateBackgroundColor();
	}
    };

    JTree tree;
    private boolean useSelectedIcon = false;

    SourceListTreeUI(JTree tree) {
	super();
	this.tree = tree;
    }

    @Override
    protected void paintRow(Graphics g0, Rectangle clipBounds, Insets insets,
	    Rectangle bounds, TreePath path, int row, boolean isExpanded,
	    boolean hasBeenExpanded, boolean isLeaf) {
	if (path.getPathCount() == 2) {
	    bounds.x += BASE_OFFSET;
	}

	Graphics2D g = (Graphics2D) g0;
	int[] selection = tree.getSelectionRows();
	boolean isSelected = false;
	if (selection != null) {
	    for (int a = 0; a < selection.length; a++) {
		if (selection[a] == row)
		    isSelected = true;
	    }
	}

	if (isSelected) {
	    Color topColor;
	    GradientPaint gradient;
	    boolean hasFocus = tree.hasFocus();
	    boolean isFrameActive = true;
	    Object obj = tree.getClientProperty("Frame.active");
	    if (obj != null && obj instanceof Boolean)
		isFrameActive = ((Boolean) obj).booleanValue();
	    if (isFrameActive == false) {
		topColor = new Color(0x979797);
		gradient = new GradientPaint(0, bounds.y, new Color(0xB5B5B5),
			0, bounds.y + bounds.height, new Color(0x8A8A8A));
	    } else if (hasFocus) {
		topColor = new Color(0x4580C8);
		gradient = new GradientPaint(0, bounds.y, new Color(0x5C93D6),
			0, bounds.y + bounds.height, new Color(0x1553AA));
	    } else {
		topColor = new Color(0x91A0C0);
		gradient = new GradientPaint(0, bounds.y, new Color(0xA2B1CF),
			0, bounds.y + bounds.height, new Color(0x6F82AA));
	    }
	    g.setPaint(gradient);
	    g.fillRect(0, bounds.y, tree.getWidth(), bounds.height);
	    g.setColor(topColor);
	    g.drawLine(0, bounds.y, tree.getWidth(), bounds.y);
	} else {
	    g.setPaint(tree.getBackground());
	    g.fillRect(0, bounds.y, tree.getWidth(), bounds.height);
	}

	/**
	 * Somehow the width is often off when used with the
	 * AquaOpenLocationPaneUI. The result was that strings in labels were
	 * being shortened with ellipses. This little hack gives us the max
	 * possible width to render strings.
	 */
	bounds.width = tree.getWidth() - bounds.x;

	super.paintRow(g, clipBounds, insets, bounds, path, row, isExpanded,
		hasBeenExpanded, isLeaf);

	useSelectedIcon = isSelected;
	paintExpandControl(g, clipBounds, insets, bounds, path, row,
		isExpanded, hasBeenExpanded, isLeaf);
	useSelectedIcon = false;
    }

    @Override
    public Icon getExpandedIcon() {
	if (useSelectedIcon)
	    return expandedSelectedIcon;
	return expandedIcon;
    }

    @Override
    public Icon getCollapsedIcon() {
	if (useSelectedIcon)
	    return collapsedSelectedIcon;
	return collapsedIcon;
    }

    @Override
    public Rectangle getPathBounds(JTree tree, TreePath path) {
	Rectangle r = super.getPathBounds(tree, path);
	if (r != null) {
	    r.x = 0;
	    r.width = tree.getWidth();
	}
	return r;
    }

    protected void updateBackgroundColor() {
	Object newValue = tree.getClientProperty("Frame.active");
	boolean active = true;
	if (newValue instanceof Boolean) {
	    Boolean b = (Boolean) newValue;
	    if (b.booleanValue() == false) {
		active = false;
	    }
	}
	if (active) {
	    tree.setBackground(new Color(0xD4DDE5));
	} else {
	    tree.setBackground(new Color(0xE8E8E8));
	}
    }

    @Override
    public void installUI(JComponent c) {
	if (!(c == tree))
	    throw new IllegalArgumentException(
		    "only install this UI on the tree it was constructed with");
	super.installUI(c);
	tree.addPropertyChangeListener("Frame.active", activeListener);
	updateBackgroundColor();
	tree.setRootVisible(false);
    }

    @Override
    public void uninstallUI(JComponent c) {
	super.uninstallUI(c);
	tree.removePropertyChangeListener("Frame.active", activeListener);
    }

    @Override
    protected boolean isLocationInExpandControl(TreePath path, int mouseX,
	    int mouseY) {
	if (path.getPathCount() == 2)
	    return mouseX < BASE_OFFSET;
	return super.isLocationInExpandControl(path, mouseX, mouseY);
    }
}