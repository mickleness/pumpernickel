package com.pump.util;



/** A simple key for Hashtables that is made up
* several smaller keys.
* <P>You could alternatively concatenate every object's
* toString() results, but that starts to add up to a lot
* of memory allocation with StringBuilders.
* (Besides some classes don't always have an
* accurate toString() method.)
*/
public class CompoundKey {
	Object[] array;
	public CompoundKey(Object[] array) {
		this.array = array;
	}
	@Override
	public int hashCode() {
		int sum = 0;
		for(int a = 0; a<array.length; a++) {
			sum += array[a].hashCode();
		}
		return sum;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof CompoundKey))
			return false;
		CompoundKey key2 = (CompoundKey)obj;
		if(array.length!=key2.array.length)
			return false;
		for(int a = 0; a<array.length; a++) {
			if(array[a].equals(key2.array[a])==false)
				return false;
		}
		return true;
	}
}