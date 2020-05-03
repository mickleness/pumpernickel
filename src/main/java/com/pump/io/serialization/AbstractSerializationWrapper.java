package com.pump.io.serialization;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractSerializationWrapper<T>
		implements SerializationWrapper<T> {

	private static final long serialVersionUID = 1L;

	protected Map<String, Object> map = new HashMap<>();

	private void writeObject(java.io.ObjectOutputStream out)
			throws IOException {
		out.writeInt(0);
		out.writeObject(map);
		;
	}

	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {

		int version = in.readInt();
		if (version == 0) {
			map = (Map<String, Object>) in.readObject();
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
