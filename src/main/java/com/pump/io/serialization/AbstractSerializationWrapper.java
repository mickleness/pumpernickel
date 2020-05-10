package com.pump.io.serialization;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public abstract class AbstractSerializationWrapper<T>
		implements SerializationWrapper<T> {

	private static final long serialVersionUID = 1L;

	protected Map<String, Object> map = new HashMap<>();

	private void writeObject(java.io.ObjectOutputStream out)
			throws IOException {
		out.writeInt(0);

		out.writeInt(map.size());
		SortedSet<String> sortedKeys = new TreeSet<>(map.keySet());
		for (String key : sortedKeys) {
			out.writeObject(key);
			out.writeObject(map.get(key));
		}
	}

	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {

		int version = in.readInt();
		if (version == 0) {
			int size = in.readInt();
			map = new HashMap<>(size);
			while (size > 0) {
				String key = (String) in.readObject();
				Object value = in.readObject();
				map.put(key, value);
				size--;
			}
		} else {
			throw new IOException("unsupported internal version " + version);
		}
	}

	@Override
	public int hashCode() {
		return map.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!getClass().equals(obj.getClass()))
			return false;
		AbstractSerializationWrapper<?> other = (AbstractSerializationWrapper<?>) obj;
		if (!map.equals(other.map))
			return false;
		return true;
	}

}
