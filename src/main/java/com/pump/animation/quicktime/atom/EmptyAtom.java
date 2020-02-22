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

import com.pump.io.GuardedOutputStream;

/**
 * This is an empty atom. (I'm not sure why these exist, but they do.)
 */
public class EmptyAtom extends Atom {

	public EmptyAtom(Atom parent) {
		super(parent);
	}

	@Override
	protected String getIdentifier() {
		return null;
	}

	@Override
	protected long getSize() {
		return 0;
	}

	@Override
	protected void writeContents(GuardedOutputStream out) {
	}

	public Enumeration<Object> children() {
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