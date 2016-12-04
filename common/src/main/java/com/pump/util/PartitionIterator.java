package com.pump.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This breaks a list up into a series of partitions. For example, if you pass
 * in the elements [A, B, C] with a partition count of 2, then this will return the lists:
 * [], [A, B, C]
 * [A], [B, C]
 * [A, B], [C]
 * [A, B, C], []
 * 
 * You can also define a minimum partition size. For example: if this is 1, then the
 * example above will only include:
 * [A], [B, C]
 * [A, B], [C]
 * 
 * The minimum partition size is enforced in the {@link #isValid(List)} method, which subclasses can
 * further modify if necessary to filter out combinations.
 */
public class PartitionIterator<T> implements Iterator<List<List<T>>> {
	List<T> elements;
	int minimumPartitionSize;
	
	List<List<T>> next;
	int[] indices;
	boolean exhausted = false;
	
	public PartitionIterator(List<T> elements, int partitionCount, int minimumPartitionSize) {
		if(partitionCount<2)
			throw new IllegalArgumentException("partitionCount ("+partitionCount+") must be greater than 1");
		this.elements = elements;
		this.minimumPartitionSize = minimumPartitionSize;
		indices = new int[partitionCount-1];
		queueNext();
	}
	
	private void queueNext() {
		if(exhausted) {
			next = null;
			return;
		}
		
		List<List<T>> q;
		do {
			q = new ArrayList<>(indices.length);
			int lastIndex = 0;
			for(int i = 0; i<indices.length; i++) {
				ArrayList<T> y = new ArrayList<>();
				for(int j = lastIndex; j<indices[i] && j<elements.size(); j++) {
					y.add(elements.get(j));
				}
				q.add(y);
				lastIndex = indices[i];
			}

			ArrayList<T> y = new ArrayList<>();
			for(int j = lastIndex; j<elements.size(); j++) {
				y.add(elements.get(j));
			}
			q.add(y);
			
			if(!incrementIndices()) {
				if(isValid(q)) {
					next = q;
				}
				return;
			}
		} while(!isValid(q));
		
		next = q;
	}
	
	private boolean incrementIndices() {
		int z = indices.length-1;
		while(true) {
			indices[z]++;
			if(indices[z]<=elements.size())
				return true;
			z--;
			if(z==-1)
			{
				exhausted = true;
				return false;
			}
			indices[z+1] = indices[z] + 1;
		}
	}

	protected boolean isValid(List<List<T>> q) {
		if(minimumPartitionSize>0) {
			for(List<T> l : q) {
				if(l.size()<minimumPartitionSize)
					return false;
			}
		}
		return true;
	}

	@Override
	public boolean hasNext() {
		return next!=null;
	}

	@Override
	public List<List<T>> next() {
		List<List<T>> returnValue = next;
		queueNext();
		return returnValue;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
