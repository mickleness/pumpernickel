package com.pump.io.serialization;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Objects;

public class FilteredObjectInputStream {

	protected ObjectInputStream delegate;

	public FilteredObjectInputStream(ObjectInputStream objIn)
			throws IOException, SecurityException {
		Objects.requireNonNull(objIn);
		this.delegate = objIn;
	}

	public Object readObject() throws IOException, ClassNotFoundException {
		Object returnValue = delegate.readObject();
		if (returnValue instanceof SerializationWrapper) {
			returnValue = ((SerializationWrapper<?>) returnValue).create();
		}
		return returnValue;
	}

	public boolean readBoolean() throws IOException {
		return delegate.readBoolean();
	}

	public byte readByte() throws IOException {
		return delegate.readByte();
	}

	public char readChar() throws IOException {
		return delegate.readChar();
	}

	public short readShort() throws IOException {
		return delegate.readShort();
	}

	public int readInt() throws IOException {
		return delegate.readInt();
	}

	public long readLong() throws IOException {
		return delegate.readLong();
	}

	public float readFloat() throws IOException {
		return delegate.readFloat();
	}

	public double readDouble() throws IOException {
		return delegate.readDouble();
	}
}
