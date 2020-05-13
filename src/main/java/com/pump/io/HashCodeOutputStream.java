package com.pump.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This OutputStream generates a hash code based on each incoming byte.
 */
public class HashCodeOutputStream extends OutputStream {
	int hashCode = 0;

	@Override
	public void write(int b) throws IOException {
		hashCode = (hashCode << 8) + b;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

}
