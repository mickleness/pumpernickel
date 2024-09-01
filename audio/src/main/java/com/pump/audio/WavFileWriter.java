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
package com.pump.audio;

import java.io.*;

import com.pump.io.MeasuredOutputStream;

public class WavFileWriter implements Closeable {
	protected File file;
	private MeasuredOutputStream out;
	private boolean formatWritten = false;
	private long sizeAfterWritingFormat;
	private boolean dataHeaderWritten = false;

	public WavFileWriter(File file) throws IOException {
		this.file = file;
		out = new MeasuredOutputStream(new FileOutputStream(file));
		out.write(new byte[] { 0x52, 0x49, 0x46, 0x46 }); // RIFF
		out.write(new byte[4]); // chunk size -- written when .close() is called
		out.write(new byte[] { 0x57, 0x41, 0x56, 0x45 }); // WAVE
	}

	public void writeFormat(WavFormatChunk format) throws IOException {
		if (formatWritten)
			throw new IOException("the format was already written");
		format.write(out, true);
		formatWritten = true;
		sizeAfterWritingFormat = out.getBytesWritten();
	}

	public void writeSample(byte[] data, int offset, int len)
			throws IOException {
		if (formatWritten == false) {
			throw new IOException("the format was not yet written");
		} else if (dataHeaderWritten == false) {
			out.write(new byte[] { 0x64, 0x61, 0x74, 0x61 });
			out.write(new byte[] {});
			dataHeaderWritten = true;
		}
		out.write(data, offset, len);
	}

	@Override
	public void close() throws IOException {
		if (out == null)
			return; // someone already called .close()

		try {
			out.close();
			long size = file.length();
			byte[] sizeArray = new byte[4];

			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.seek(4);
			writeLong(sizeArray, size - 8, 4);
			raf.write(sizeArray);

			raf.seek(sizeAfterWritingFormat + 4);
			writeLong(sizeArray, size - sizeAfterWritingFormat - 9, 4);
			raf.write(sizeArray);

			raf.close();
		} finally {
			out = null;
		}
	}

	protected static void writeLong(byte[] bytes, long value, int len) {
		if (len > 8)
			throw new RuntimeException("len (" + len + ") > 8");

		for (int a = 0; a < len; a++) {
			bytes[a] = (byte) ((value >> (8 * a)) & 0xff);
		}
	}

	protected static void writeInt(byte[] bytes, int value, int len) {
		if (len > 4)
			throw new RuntimeException("len (" + len + ") > 4");

		for (int a = 0; a < len; a++) {
			bytes[a] = (byte) ((value >> (8 * a)) & 0xff);
		}
	}
}