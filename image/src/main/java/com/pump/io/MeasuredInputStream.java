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
package com.pump.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * This <code>InputStream</code> relays information from an underlying
 * <code>InputStream</code> while measuring how many bytes have been read or
 * skipped.
 * <P>
 * Note marking is not supported in this object.
 */
public class MeasuredInputStream extends InputStream {
	InputStream in;
	long read = 0;
	boolean closeable = true;

	public MeasuredInputStream(InputStream i) {
		this.in = i;
	}

	public MeasuredInputStream(InputStream i, boolean closeable) {
		this(i);
		setCloseable(closeable);
	}

	/**
	 * Control whether calling <code>{@link #close()}</code> affects the
	 * underlying InputStream. This is useful in cases when you pass an
	 * InputStream to a 3rd party decoder that helpfully tries to close the
	 * stream as it wraps up, but there is still data to be read later (such as
	 * when working with a ZipInputStream).
	 * 
	 * @param b
	 *            whether calling <code>close()</code> will close the underlying
	 *            InputStream.
	 */
	public void setCloseable(boolean b) {
		closeable = b;
	}

	public boolean isCloseable() {
		return closeable;
	}

	@Override
	public int available() throws IOException {
		return in.available();
	}

	/**
	 * Returns the number of bytes that have been read (or skipped).
	 * 
	 * @return the number of bytes that have been read (or skipped).
	 */
	public long getReadBytes() {
		return read;
	}

	@Override
	public void close() throws IOException {
		if (isCloseable())
			in.close();
	}

	@Override
	public synchronized void mark(int readlimit) {
		throw new RuntimeException();
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	@Override
	public int read() throws IOException {
		int k = in.read();
		if (k == -1)
			return -1;
		read++;
		return k;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int returnValue = in.read(b, off, len);
		if (returnValue == -1)
			return -1;
		read += returnValue;

		return returnValue;
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public synchronized void reset() throws IOException {
		throw new RuntimeException();
	}

	@Override
	public long skip(long n) throws IOException {
		long returnValue = in.skip(n);
		if (returnValue == -1)
			return -1;
		read += returnValue;

		return returnValue;
	}

	/**
	 * This skips forward to the requested position. If the requested position
	 * is less than the current position, then an exception is thrown.
	 * 
	 * @param pos
	 * @throws IOException
	 */
	public void seek(long pos) throws IOException {
		if (pos == read)
			return;
		if (pos < read)
			throw new IOException("Cannot seek backwards.");
		skip(pos - read);
	}
}