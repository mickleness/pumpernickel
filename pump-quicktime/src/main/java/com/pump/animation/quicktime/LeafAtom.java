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
package com.pump.animation.quicktime;

import java.util.Enumeration;

import javax.swing.tree.TreeNode;

/** This is not a public class because I expect to make some significant
 * changes to this project in the next year.
 * <P>Use at your own risk.  This class (and its package) may change in future releases.
 * <P>Not that I'm promising there will be future releases.  There may not be.  :)
 */
abstract class LeafAtom extends Atom {
	
	protected LeafAtom(Atom parent) {
		super(parent);
	}
	
	public Enumeration<?> children() {
		return EMPTY_ENUMERATION;
	}
	
	public boolean getAllowsChildren() {
		return false;
	}
	
	public TreeNode getChildAt(int childIndex) {
		return null;
	}
	
	public int getChildCount() {
		return 0;
	}
	
	public int getIndex(TreeNode node) {
		return -1;
	}
	
	public boolean isLeaf() {
		return true;
	}
}