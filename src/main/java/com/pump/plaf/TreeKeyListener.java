/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.plaf;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * This changes the selection in a JTree based on text the user typed.
 * <p>
 * Note the BasicTreeUI already has a KeyListener that effectively tries to do
 * what this does (except it doesn't support creating short Strings with more
 * than one character). That mechanism relies on
 * {@link JTree#convertValueToText(Object, boolean, boolean, boolean, int, boolean)}
 * , so if you want leverage that with a {@link LabelCellRenderer}, you can
 * override that method so it resembles:
 * 
 * <pre>
 * int recursionCtr = 0;
 * 
 * &#064;Override
 * public String convertValueToText(Object value, boolean selected,
 * 		boolean expanded, boolean leaf, int row, boolean hasFocus) {
 * 	if (recursionCtr &gt; 0) {
 * 		// the default renderer call this method, so we need to
 * 		// abort for recursive calls
 * 		return super.convertValueToText(value, selected, expanded, leaf, row,
 * 				hasFocus);
 * 	}
 * 	recursionCtr++;
 * 	try {
 * 		Component c = getCellRenderer().getTreeCellRendererComponent(this,
 * 				value, selected, expanded, leaf, row, hasFocus);
 * 		String str = KeyListenerNavigator.getText(c);
 * 		if (str != null)
 * 			return str;
 * 		return super.convertValueToText(value, selected, expanded, leaf, row,
 * 				hasFocus);
 * 	} finally {
 * 		recursionCtr--;
 * 	}
 * }
 * </pre>
 */
public class TreeKeyListener extends KeyListenerNavigator {

	public TreeKeyListener() {
		super(false);
	}

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
				return true;
			}
		}

		return false;
	}

}