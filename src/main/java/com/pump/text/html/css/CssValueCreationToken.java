package com.pump.text.html.css;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * This identifies when a CssValue was created. Each CssValueCreationToken
 * created within the same JVM session is guaranteed to be unique, so CssValues
 * that are sorted according to their CssValueCreationTokens will appear in the
 * order they were created.
 */
public class CssValueCreationToken
		implements Comparable<CssValueCreationToken>, Serializable {

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
