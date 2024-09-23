/**
 * This software is released as part of the Pumpernickel project.
 * <p>
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * <p>
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.image.jpeg;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * This stream reads discrete sections of a JPEG file. It is modeled after the
 * <code>ZipInputStream</code>: the stream appears to end and no more data will
 * be read as one marker ends. You have to call <code>getNextMarker()</code> to
 * progress to the next section of the JPEG.
 * <p>
 * This object is designed for and tested against markers that precede the image
 * data. When the start of scan marker (0xFFDA) is reached: this is when you
 * need to stop using this object. That marker is special because it technically
 * contains a finite amount of data, but once it ends the JPEG image data
 * begins.
 */
class JPEGMarkerInputStream extends InputStream {

	InputStream in;
	String currentMarker;
	int remainingMarkerLength = 0;
	byte[] scratch = new byte[2];
	boolean reverse = false;

	public JPEGMarkerInputStream(InputStream in) {
		this.in = in;
	}

	/**
	 * Returns the current 4-character marker in uppercase, or null if this
	 * stream hasn't been opened yet or is finished.
	 * <p>
	 * For example: a start of image marker will return "FFD8".
	 * 
	 */
	public String getCurrentMarker() {
		return currentMarker;
	}

	public String getNextMarker() throws IOException {
		skip(remainingMarkerLength);

		remainingMarkerLength = 2;
		if (readFully(scratch, 2, false, reverse) != 2)
			throw new EOFException("EOF reached");
		int i = (scratch[0] & 0xff) * 256 + (scratch[1] & 0xff);
		currentMarker = Integer.toString(i, 16).toUpperCase();
		if (JPEGMarker.START_OF_IMAGE_MARKER.getByteCode().equals(currentMarker)
				|| JPEGMarker.END_OF_IMAGE_MARKER.getByteCode()
						.equals(currentMarker)) {
			remainingMarkerLength = 0;
		} else {
			remainingMarkerLength = 2;
			if (readFully(scratch, 2, false, reverse) != 2)
				throw new IOException("EOF reached");
			i = (scratch[0] & 0xff) * 256 + (scratch[1] & 0xff);
			remainingMarkerLength = i - 2;
		}
		return currentMarker;

	}

	@Override
	public int read() throws IOException {
		if (remainingMarkerLength == 0)
			return -1;
		int returnValue = in.read();
		remainingMarkerLength--;
		return returnValue;
	}

	@Override
	public int read(byte[] b) throws IOException {
		int amountToRead = Math.min(b.length, remainingMarkerLength);
		return read(b, 0, amountToRead);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int amountToRead = Math.min(len, remainingMarkerLength);
		if (amountToRead == 0 && len != 0)
			return -1;

		int returnValue = in.read(b, off, amountToRead);
		if (returnValue > 0) {
			remainingMarkerLength -= returnValue;
		}
		return returnValue;
	}

	@Override
	public long skip(long n) throws IOException {
		return skipFully(n);
	}

	@Override
	public int available() throws IOException {
		int returnValue = super.available();
		return Math.min(returnValue, remainingMarkerLength);
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

	@Override
	public synchronized void mark(int readlimit) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void reset() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	protected int readFully(byte[] dest, int amt) throws IOException {
		return readFully(dest, amt, false, false);
	}

	/**
	 * @param overrideExpectedMarkerLength if false then we respect the current anticipated marker length. For
	 *                                     example: if the current marker is supposed to be 20 bytes, then this
	 *                                     will stop reading at 20 bytes. If true then this method will continue
	 *                                     to read past the current marker length.
	 */
	protected int readFully(byte[] dest, int amt, boolean overrideExpectedMarkerLength) throws IOException {
		return readFully(dest, amt, overrideExpectedMarkerLength, false);
	}

	/**
	 * @param overrideExpectedMarkerLength if false then we respect the current anticipated marker length. For
	 *                                     example: if the current marker is supposed to be 20 bytes, then this
	 *                                     will stop reading at 20 bytes. If true then this method will continue
	 *                                     to read past the current marker length.
	 */
	protected int readFully(byte[] dest, int amt, boolean overrideExpectedMarkerLength, boolean reverse)
			throws IOException {
		if (!overrideExpectedMarkerLength)
			amt = Math.min(amt, remainingMarkerLength);
		int returnValue = readFully(in, dest, amt, reverse);
		remainingMarkerLength -= returnValue;
		return returnValue;
	}

	protected static int readFully(InputStream in, byte[] dest, int amt,
			boolean reverse) throws IOException {
		int ctr = 0;
		int t = in.read(dest, ctr, amt - ctr);
		while (t + ctr != amt && t != -1) {
			ctr += t;
			t = in.read(dest, ctr, amt - ctr);
		}
		if (t != -1)
			ctr += t;

		if (reverse) {
			t = ctr / 2;
			for (int i = 0; i < t; i++) {
				byte k = dest[amt - 1 - i];
				dest[amt - 1 - i] = dest[i];
				dest[i] = k;
			}
		}
		return ctr;
	}

	protected long skipFully(long amt) throws IOException {
		amt = Math.min(amt, remainingMarkerLength);
		long returnValue = skipFully(in, amt);
		remainingMarkerLength -= returnValue;
		return returnValue;
	}

	protected static long skipFully(InputStream in, long amount)
			throws IOException {
		/*
		 * The description of InputStream.skip() is unfulfilling. It is
		 * perfectly OK for InputStream.skip() to return zero, but not
		 * necessarily mean EOF.
		 */

		if (amount < 0)
			return 0;

		long sum = 0;
		long t = in.skip(amount - sum);
		while (t + sum != amount) {
			if (t == 0) {
				// in.read() has a clear EOF indicator, though:
				t = in.read();
				if (t == -1)
					return sum;
				sum++;
			} else {
				sum += t;
			}
			t = in.skip(amount - sum);
		}

		return t + sum;
	}

	protected static String toString(byte[] array, int length) {
		StringBuilder sb = new StringBuilder();
		for (int a = 0; a < length; a++) {
			sb.append((char) array[a]);
		}
		return sb.toString();
	}
}