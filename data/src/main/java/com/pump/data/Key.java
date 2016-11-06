package com.pump.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Key<T> implements CharSequence, Serializable {
	private static final long serialVersionUID = 1L;

	protected final String name;
	protected final Class<T> type;
	protected List<BoundsChecker<T>> checkers;
	
	public Key(Class<T> type,String name) {
		if(type==null)
			throw new NullPointerException();
		if(name==null)
			throw new NullPointerException();
		if(name.length()==0)
			throw new IllegalArgumentException();
		
		this.type = type;
		this.name = name;
	}
	
	public void addBoundsChecker(BoundsChecker<T> bc) {
		if(checkers==null)
			checkers = new ArrayList<>();
		
		checkers.add(bc);
	}
	
	public Class<T> getType() {
		return type;
	}

	@Override
	public int length() {
		return name.length();
	}

	@Override
	public char charAt(int index) {
		return name.charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return name.subSequence(start, end);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Key))
			return false;
		Key<?> other = (Key<?>)obj;
		if(!name.equals(other.name))
			return false;
		if(!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name;
	}
	
	public List<BoundsChecker<T>> getBoundsCheckers() {
		ArrayList<BoundsChecker<T>> returnValue = new ArrayList<>();
		if(checkers!=null) {
			for(BoundsChecker<T> b : checkers) {
				returnValue.add(b);
			}
		}
		return returnValue;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public T put(Map attributes, T value) {
		if(checkers!=null) {
			for(BoundsChecker<T> checker : checkers) {
				checker.check(this, value);
			}
		}
		
		if(value==null) {
			return (T) attributes.remove(toString());
		} else {
			return (T) attributes.put(toString(), value);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public T get(Map attributes) {
		return (T) attributes.get(toString());
	}
}
