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
 * This filtered stream places an initial limit on the number of bytes that can
 * be read. Once this limit is reached, all methods in this object return -1
 * indicating we're at the EOF, although the underlying InputStream is not
 * touched.
 * <P>
 * This object is useful when parsing files where specific blocks of a file have
 * a predetermined size: with this object you can guarantee you read only a
 * fixed number of bytes. So if data inside the block is corrupt, or if you want
 * to loosely guard how you read each block, this object will make sure you
 * don't read too far.
 * <P>
 * The <code>mark()</code> and <code>reset()</code> methods are not supported.
 * 
 */
public class GuardedInputStream extends MeasuredInputStream {
	long limit;

	/**
	 * Constructs a new <code>GuardedInputStream</code>.
	 * 
	 * @param in
	 *            the underlying <code>InputStream</code> to use.
	 * @param limit
	 *            the maximum number of bytes that will be read.
	 * @param canClose
	 *            if this is <code>false</code>, then calling <code>close</code>
	 *            will not actually close the underlying stream.
	 */
	public GuardedInputStream(InputStream in, long limit, boolean canClose) {
		super(in);
		this.limit = limit;
		this.setCloseable(canClose);
	}

	/**
	 * Whether any more data can be read from this stream (due to the limit it
	 * was constructed with).
	 * 
	 * @return true if the limit has been reached, and no more data can be read.
	 */
	public boolean isAtLimit() {
		return limit == 0;
	}

	@Override
	public synchronized void mark(int readlimit) {
		throw new RuntimeException("mark is unsupported");
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	@Override
	public int read() throws IOException {
		if (limit == 0)
			return -1;

		limit--;
		return super.read();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (limit == 0)
			return -1;

		if (len > limit) {
			// we can cast here because len, which is an int, is greater than
			// limit.
			// therefore limit can be expressed as an int, too.
			return read(b, off, (int) limit);
		}
		int returnValue = super.read(b, off, len);
		limit = limit - returnValue;
		return returnValue;
	}

	/**
	 * Returns the number of bytes that are allowed to be read.
	 * <P>
	 * This number has nothing to do with the number of bytes that are actually
	 * remaining in the underlying <code>InputStream</code>. For example, if
	 * this <code>GuardedInputStream</code> was designed to read at most 1000
	 * bytes, then this method may return 1000 -- even though there may only be
	 * 500 bytes available in the underlying <code>InputStream</code>.
	 * 
	 * @return the number of bytes that are allowed to be read.
	 */
	public long getRemainingLimit() {
		return limit;
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public synchronized void reset() throws IOException {
		throw new RuntimeException("mark is unsupported");
	}

	@Override
	public long skip(long n) throws IOException {
		if (limit == 0)
			return -1;

		if (n > limit) {
			return skip(limit);
		}
		long returnValue = super.skip(n);
		limit = limit - returnValue;
		return returnValue;
	}
}