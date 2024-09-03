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
package com.pump.text.html.css;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import com.pump.text.html.view.QViewHelper;

/**
 * This identifies when a CssValue was created. Each CssValueCreationToken
 * created within the same JVM session is guaranteed to be unique, so CssValues
 * that are sorted according to their CssValueCreationTokens will appear in the
 * order they were created.
 */
public class CssValueCreationToken
		implements Comparable<CssValueCreationToken>, Serializable {

	/**
	 * Return a list of key/value pairs that is sorted in order of creation.
	 * <p>
	 * The first element in the list is the oldest element, and the last element
	 * is the newest. This is useful when interpreting CSS, because if
	 * sequential CSS properties conflict then the most recent (last) one
	 * overrides any previous properties.
	 * 
	 * @param helper
	 *            the helper used to retrieve properties.
	 * @param propertyNames
	 *            the property names to retrieve.
	 * @return a list of sorted properties.
	 */
	public static Collection<Map.Entry<String, Object>> getOrderedProperties(
			QViewHelper helper, String... propertyNames) {
		Map<SortedKey, Map.Entry<String, Object>> sortedProperties = new TreeMap<>();
		for (String propertyName : propertyNames) {
			Object propertyValue = helper.getAttribute(propertyName, false);
			if (propertyValue != null) {
				SortedKey key = new SortedKey(propertyValue);
				Map.Entry<String, Object> entry = new AbstractMap.SimpleEntry<String, Object>(
						propertyName, propertyValue);
				sortedProperties.put(key, entry);
			}
		}

		return sortedProperties.values();
	}

	/**
	 * This helps sort values according to their creation timestamps, so we can
	 * safely identify the last (most recently defined) CSS statement.
	 */
	private static class SortedKey implements Comparable<SortedKey> {
		final CssValueCreationToken creationToken;

		SortedKey(Object value) {
			Objects.requireNonNull(value);

			if (value instanceof CssValue) {
				creationToken = ((CssValue) value).getCreationToken();
			} else if (value instanceof List) {
				creationToken = ((CssValue) ((List) value).get(0))
						.getCreationToken();
			} else {
				throw new RuntimeException("Unexpected value: "
						+ value.getClass().getName() + " " + value);
			}
		}

		@Override
		public int compareTo(SortedKey o2) {
			return creationToken.compareTo(o2.creationToken);
		}
	}

	private static final long serialVersionUID = 1L;

	static long lastTimeStamp = -1;

	private long timeStamp;

	/**
	 * Create a new CssValueCreationToken. This may block between a nanosecond
	 * and a millisecond to guarantee uniqueness.
	 */
	public CssValueCreationToken() {
		synchronized (CssValueCreationToken.class) {
			timeStamp = System.nanoTime();
			while (lastTimeStamp == timeStamp) {
				Thread.yield();
				timeStamp = System.nanoTime();
			}

			lastTimeStamp = timeStamp;
		}
	}

	@Override
	public String toString() {
		return Long.toHexString(timeStamp);
	}

	@Override
	public int compareTo(CssValueCreationToken o) {
		return Long.compare(timeStamp, o.timeStamp);
	}

	private void writeObject(java.io.ObjectOutputStream out)
			throws IOException {
		out.writeInt(0);
		out.writeLong(timeStamp);
	}

	private void readObject(ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		int version = in.readInt();
		if (version == 0) {
			timeStamp = in.readLong();
		} else {
			throw new IOException("unsupported internal version " + version);
		}
	}

}