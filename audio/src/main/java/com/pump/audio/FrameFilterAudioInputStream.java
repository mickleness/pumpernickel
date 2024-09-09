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
package com.pump.audio;

import java.io.BufferedInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;

public abstract class FrameFilterAudioInputStream extends AudioInputStream {

	/**
	 * This is a tiny buffer that stores 1 frame at a time. Ideally the
	 * externals calls to read() and skip() will involve thousands of bytes, but
	 * if a number of bytes is required that is less than the bytes in a single
	 * frame: this object is used to cluster frames together.
	 */
	class FrameBuffer {
		int bufferPos;
		byte[] data;
		int bufferLength = 0;

		FrameBuffer() {
			data = new byte[frameSize];
		}

		int read(byte[] dest, int offset, int length) {
			int m = Math.min(bufferLength, length);
			System.arraycopy(data, bufferPos, dest, offset, m);
			bufferLength -= m;
			bufferPos += m;
			return m;
		}

		int skip(long len) {
			int m = (int) Math.min(len, bufferLength);
			bufferPos += m;
			bufferLength -= m;
			return m;
		}

		int read() {
			if (bufferPos == bufferLength) {
				throw new RuntimeException();
			}

			int returnValue = data[bufferPos] & 0xff;
			bufferPos++;
			bufferLength--;
			return returnValue;
		}

		boolean isEmpty() {
			return bufferLength == 0;
		}

		int getSize() {
			return bufferLength;
		}

		/**
		 * Populate this buffer with a frame from the InputStream
		 * 
		 * @return false if the end of the stream was reached. True if data was
		 *         read.
		 */
		@SuppressWarnings("BooleanMethodIsAlwaysInverted")
		boolean populate() throws IOException {
			if (!isEmpty())
				throw new RuntimeException();
			int read = FrameFilterAudioInputStream.this.read(data);
			if (read == -1)
				return false;
			bufferPos = 0;
			bufferLength = read;
			return true;
		}
	}

	final private FrameBuffer buffer;

	public FrameFilterAudioInputStream(AudioInputStream audioIn) {
		super(new BufferedInputStream(audioIn), audioIn.getFormat(), audioIn
				.getFrameLength());
		if (!(audioIn.getFormat().getSampleSizeInBits() == 8 || audioIn
				.getFormat().getSampleSizeInBits() == 16))
			throw new IllegalArgumentException("unsupported sample size: "
					+ audioIn.getFormat().getSampleSizeInBits());
		buffer = new FrameBuffer();
	}

	@Override
	public int available() throws IOException {
		return super.available() + buffer.getSize();
	}

	@Override
	public void mark(int readLimit) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	@Override
	public synchronized int read() throws IOException {
		if (buffer.isEmpty()) {
			if (!buffer.populate())
				return -1;
		}
		return buffer.read();
	}

	/**
	 * When possible, this will round down the number of bytes to read, so we
	 * process a multiple of this.frameSize.
	 */
	@Override
	public synchronized int read(byte[] b, int off, int len) throws IOException {
		if (!buffer.isEmpty()) {
			return buffer.read(b, off, len);
		}

		int remainder = len % frameSize;
		int newLength = len - remainder;
		if (len > 0 && newLength == 0) {
			if (!buffer.populate())
				return -1;
			return buffer.read(b, off, len);
		}
		int returnValue = super.read(b, off, newLength);

		if (returnValue == -1)
			return -1;

		/*
		 * Since our underlying InputStream is buffered: returnValue will always
		 * be newLength unless the end of the stream is reached AND it is an
		 * uneven number.
		 */
		int framesToFilter = returnValue / frameSize;

		if (framesToFilter > 0)
			filterFrames(b, off, framesToFilter);

		return returnValue;
	}

	/**
	 * Filter several consecutive sample frames.
	 * 
	 * @param data
	 *            the array storing the sample frames.
	 * @param off
	 *            the offset in this array the frames begin at.
	 * @param frameCount
	 *            the number of frames to filter. So the number of bytes
	 *            filtered should equal `frameCount * this.frameSize`.
	 */
	protected abstract void filterFrames(byte[] data, int off, int frameCount);

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public void reset() throws IOException {
		throw new UnsupportedOperationException();
	}

	/**
	 * When possible, this will round down the number of bytes to skip, so we
	 * process a multiple of this.frameSize.
	 */
	@Override
	public long skip(long n) throws IOException {
		if (!buffer.isEmpty()) {
			return buffer.skip(n);
		}

		long remainder = n % frameSize;
		long newLength = n - remainder;
		if (n > 0 && newLength == 0) {
			if (!buffer.populate())
				return -1;
			return buffer.skip(n);
		}

		return super.skip(newLength);
	}
}