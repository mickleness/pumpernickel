package com.pump.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

/**
 * This looks through a nested web of Collections and Maps and arrays to find
 * all the instances of a requested class.
 * <p>
 * This should iterate over every possible element, but the order of iteration
 * is not guaranteed.
 */
public class NestedDataIterator<T> implements Iterator<T> {
	protected Class<T> returnValueType;
	protected LinkedList<Iterator<?>> activeIters = new LinkedList<>();
	protected LinkedList<Object> dataSources = new LinkedList<>();

	private T nextReturnValue;

	/**
	 * Create a new NestedDataIterator.
	 * 
	 * @param returnValueType
	 *            the type of object to look for
	 * @param dataSources
	 *            all the data sources to iterate through. This can include
	 *            instances of returnValueType, maps, lists, or arrays.
	 */
	public NestedDataIterator(Class<T> returnValueType, Object... dataSources) {
		Objects.requireNonNull(returnValueType);
		Objects.requireNonNull(dataSources);

		this.returnValueType = returnValueType;
		this.dataSources.addAll(Arrays.asList(dataSources));

		queueNext();
	}

	/**
	 * Populate nextReturnValue, or leave it null if this iterator is finished.
	 */
	@SuppressWarnings("unchecked")
	private void queueNext() {
		while (true) {
			while (activeIters.isEmpty()) {
				if (dataSources.isEmpty()) {
					return;
				}
				Object dataSource = dataSources.removeFirst();
				if (returnValueType.isInstance(dataSource)) {
					nextReturnValue = (T) dataSource;
					return;
				}
				Iterator<?> iter = getIterator(dataSource);
				if (iter != null) {
					activeIters.add(iter);
				}
			}

			Iterator<?> topActiveIterator = activeIters.peekLast();
			if (topActiveIterator.hasNext()) {
				Object e = topActiveIterator.next();

				if (returnValueType.isInstance(e)) {
					nextReturnValue = (T) e;
					return;
				}
				Iterator<?> iter = getIterator(e);
				if (iter != null) {
					activeIters.add(iter);
				}
			} else {
				activeIters.removeLast();
			}
		}
	}

	/**
	 * Convert a data source to an iterator.
	 * 
	 * @param dataSource
	 *            an object, such as a Collection, Map or Array
	 * @return an iterator over this data source.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Iterator<?> getIterator(Object dataSource) {
		if (dataSource instanceof Collection) {
			Collection c = (Collection) dataSource;
			return c.iterator();
		} else if (dataSource instanceof Map) {
			Map map = (Map) dataSource;
			return new NestedDataIterator(returnValueType, map.keySet(),
					map.values());
		} else if (dataSource != null && dataSource.getClass().isArray()) {
			Object[] array = (Object[]) dataSource;
			return Arrays.asList(array).iterator();
		}
		return null;
	}

	@Override
	public boolean hasNext() {
		return nextReturnValue != null;
	}

	@Override
	public T next() {
		T returnValue = nextReturnValue;
		nextReturnValue = null;
		queueNext();
		return returnValue;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
