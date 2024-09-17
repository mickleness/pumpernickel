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
package com.pump.animation.quicktime.atom;

import java.util.Enumeration;

import javax.swing.tree.TreeNode;

import com.pump.io.GuardedOutputStream;

/**
 * This is an unusual atom, and maybe an undocumented atom. The file format
 * specs say all atoms MUST start with 4 bytes indicating their size, and then
 * 4 bytes (a String) indicating their name. However sometimes when we try
 * to read an atom's size: we get `0`. So this EmptyAtom class is used in that
 * peculiar circumstance. Its {@link #getSize()} method technically returns `4`
 * (because it took 4 bytes just to express the `0`).
 */
public class EmptyAtom extends Atom {

	public EmptyAtom(Atom parent) {
		super(parent);
	}

	@Override
	public String getIdentifier() {
		return null;
	}

	@Override
	protected long getSize() {
		return 4;
	}

	@Override
	protected void writeContents(GuardedOutputStream out) {
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