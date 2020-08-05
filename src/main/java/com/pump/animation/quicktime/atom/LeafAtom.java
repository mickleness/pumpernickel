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
package com.pump.animation.quicktime.atom;

import java.util.Enumeration;

import javax.swing.tree.TreeNode;

/**
 * This is the parent class for atoms that have no children.
 */
public abstract class LeafAtom extends Atom {

	protected LeafAtom(Atom parent) {
		super(parent);
	}

	@Override
	public Enumeration<? extends TreeNode> children() {
		return (Enumeration) EMPTY_ENUMERATION;
	}

	@Override
	public boolean getAllowsChildren() {
		return false;
	}

	@Override
	public Atom getChildAt(int childIndex) {
		return null;
	}

	@Override
	public int getChildCount() {
		return 0;
	}

	@Override
	public int getIndex(TreeNode node) {
		return -1;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}
}