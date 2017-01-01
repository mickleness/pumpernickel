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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

import com.pump.io.GuardedInputStream;
import com.pump.io.GuardedOutputStream;
import com.pump.util.EnumerationIterator;

public class ParentAtom extends Atom {
	List<Atom> children = new ArrayList<Atom>();
	String id;
	
	public ParentAtom(String id) {
		super(null);
		this.id = id;
	}
	
	public void add(Atom a) {
		children.add(a);
		a.parent = this;
	}
	
	public ParentAtom(Atom parent,String id,GuardedInputStream in) throws IOException {
		super(parent);
		this.id = id;
		while(in.isAtLimit()==false) {
			children.add(AtomFactory.read(this,in));
		}
	}

	public Enumeration<Atom> children() {
		return new EnumerationIterator<Atom>(children.iterator());
	}

	public boolean getAllowsChildren() {
		return true;
	}

	public TreeNode getChildAt(int childIndex) {
		return children.get(childIndex);
	}

	public int getChildCount() {
		return children.size();
	}

	public int getIndex(TreeNode node) {
		return children.indexOf(node);
	}

	public boolean isLeaf() {
		return children.size()==0;
	}

	@Override
	protected long getSize() {
		long sum = 8;
		for(int a = 0; a<children.size(); a++) {
			Atom atom = children.get(a);
			sum += atom.getSize();
		}
		return sum;
	}

	@Override
	protected String getIdentifier() {
		return id;
	}

	@Override
	protected void writeContents(GuardedOutputStream out) throws IOException {
		for(int a = 0; a<children.size(); a++) {
			Atom atom = children.get(a);
			atom.write(out);
		}
	}
}