package com.pump.io.serialization;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FilteredObjectOutputStream {
	List<SerializationFilter> filters = new ArrayList<>();

	protected ObjectOutputStream delegate;

	public FilteredObjectOutputStream(ObjectOutputStream objOut)
			throws IOException {
		Objects.requireNonNull(objOut);
		this.delegate = objOut;
	}

	public void addFilter(SerializationFilter filter) {
		synchronized (filters) {
			filters.add(filter);
		}
	}

	public void removeFilter(SerializationFilter filter) {
		synchronized (filters) {
			filters.remove(filter);
		}
	}

	public SerializationFilter[] getFilters() {
		synchronized (filters) {
			return filters.toArray(new SerializationFilter[filters.size()]);
		}
	}

	public void writeObject(Object object) throws IOException {
		Serializable serializable = null;
		if (object == null || object instanceof Serializable) {
			serializable = (Serializable) object;
		} else {
			for (SerializationFilter filter : getFilters()) {
				serializable = filter.filter(object);
				if (serializable != null)
					break;
			}
			if (serializable == null)
				throw new NotSerializableException(object.getClass().getName());
		}

		delegate.writeObject(serializable);
	}

	public void writeBoolean(boolean val) throws IOException {
		delegate.writeBoolean(val);
	}

	public void writeByte(int val) throws IOException {
		delegate.writeByte(val);
	}

	public void writeShort(int val) throws IOException {
		delegate.writeShort(val);
	}

	public void writeChar(int val) throws IOException {
		delegate.writeChar(val);
	}

	public void writeInt(int val) throws IOException {
		delegate.writeInt(val);
	}

	public void writeLong(long val) throws IOException {
		delegate.writeLong(val);
	}

	public void writeFloat(float val) throws IOException {
		delegate.writeFloat(val);
	}

	public void writeDouble(double val) throws IOException {
		delegate.writeDouble(val);
	}
}
