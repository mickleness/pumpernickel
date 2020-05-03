package com.pump.io.serialization;

import java.io.Serializable;

public interface SerializationWrapper<T> extends Serializable {
	public T create();
}
