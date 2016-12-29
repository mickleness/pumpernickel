/*
 * @(#)TreeIterator.java
 *
 * $Date: 2014-06-06 14:04:49 -0400 (Fri, 06 Jun 2014) $
 *
 * Copyright (c) 2013 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */
package com.pump.util;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

public abstract class TreeIterator<T> implements Iterator<T> {

	class Node {
		T parent;
		T[] children;
		int ctr = -1;
		
		public Node(T node) {
			this(node, listChildren(node));
		}
		
		@SuppressWarnings("unchecked")
		public Node(T node,T[] c) {
			this.parent = node;
			if(c==null) {
				c = (T[])Array.newInstance(node.getClass(), 0);
			}
			this.children = c;
		}
	}
	
	Stack<Node> stack = new Stack<Node>();
	final T root;
	
	/** Creates a new <code>TreeIterator</code>
	 * 
	 * @param parent the root to begin searching in.
	 * @param includeRoot whether the first argument should be
	 * one of the values returned by this iterator
	 */
	public TreeIterator(T parent,boolean includeRoot) {
		Node newNode = new Node(parent);
		if(!includeRoot)
			newNode.ctr++;
		stack.push(newNode);
		this.root = parent;
	}
	
	/** List the children of a parent this object should iterate over.
	 * This method does not need to worry about filtering the children.
	 * <p>(In fact too much filtering at this step can be a problem.
	 * For example: if a file iterator wants to only return files with
	 * a certain extension this method still needs to return directories,
	 * or the iterator won't search deep enough.)
	 * <p>This may return null, or include null elements in the return array.
	 */
	protected abstract T[] listChildren(T parent);
	
	/** Return true if this node can be returned
	 * by this iterator. This method acts like a filter:
	 * it may be called hundreds of times as we iterate
	 * over a tree until we find an acceptable value for
	 * <code>next()</code> to return.
	 * <p>The implementation is simply to return <code>true</code>.
	 * 
	 * @param node a possible return value for <code>next()</code>
	 * @return <code>false</code> if this is unacceptable and
	 * we have to keep searching.
	 */
	protected boolean isReturnValue(T node) {
		return true;
	}
	
	/** Returns the root this iterator is searching.
	 */
	public T getRoot() {
		return root;
	}

	/** Returns <code>true</code> if there are more values in this iterator.
	 */
	public boolean hasNext() {
		T returnValue = current();
		return returnValue!=null;
	}

	/** Returns the next node in this iterator.
	 */
	public T next() {
		if(stack.size()==0)
			throw new NoSuchElementException();
		
		T returnValue = current();
		
		iterate();
		
		return returnValue;
	}
	
	private void iterate() {
		//force the next call to .current() to reevaluate:
		if(stack.size()>0)
			stack.peek().ctr++;
	}
	
	private T current() {
		while(true) {
			if(stack.size()==0)
				return null;
			
			Node current = stack.peek();
			T candidate = null;
			if(current.ctr==-1) {
				candidate = current.parent;
			} else if(current.ctr<current.children.length) {
				candidate = current.children[current.ctr];
				
				if(candidate!=null) {
					T[] children = listChildren(candidate);
					if(children!=null && children.length>0) {
						Node newNode = new Node(candidate, children);
						stack.push(newNode);
					}
				}
			} else {
				stack.pop();
			}
			
			if(candidate!=null && isReturnValue(candidate)) {
				return candidate;
			}
			
			iterate();
		}
	}
	
	/** Throws an UnsupportedOperationException(). */
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
