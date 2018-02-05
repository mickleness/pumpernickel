package com.pump.plaf;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * This changes the selection in a JTree based on text the user typed.
 */
public class TreeKeyListener extends KeyListenerNavigator {

	@Override
	protected boolean changeSelectionUsingText(KeyEvent e, String inputStr) {
		inputStr = inputStr.toUpperCase();
		JTree tree = (JTree) e.getComponent();

		int[] selection = tree.getSelectionRows();
		Arrays.sort(selection);
		int i = selection.length > 0 ? selection[selection.length - 1] : 0;
		int rowCount = tree.getRowCount();
		for (int offset = 0; offset < rowCount; offset++) {
			int row = (i + offset) % rowCount;
			TreePath path = tree.getPathForRow(row);
			TreeNode node = (TreeNode) path.getLastPathComponent();
			Component renderer = tree.getCellRenderer()
					.getTreeCellRendererComponent(tree, node, false,
							tree.isExpanded(path), node.isLeaf(), row,
							tree.isFocusOwner());
			String str = getText(renderer);
			if (str != null && str.length() >= 0
					&& str.toUpperCase().startsWith(inputStr)) {
				tree.setSelectionPath(path);
				e.consume();
				return true;
			}
		}

		return false;
	}

}