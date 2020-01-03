/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://raw.githubusercontent.com/mickleness/pumpernickel/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.audio;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.RandomAccessFile;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import com.pump.io.MeasuredInputStream;

/**
 * Reads uncompressed waves.
 */
public class WavReader {

	/**
	 * This method fixes a file with corrupt size fields.
	 * <p>
	 * A <code>RandomAccessFile</code> is used to redefine the size of the
	 * "RIFF" and "data" chunks based on the real file size.
	 * 
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static void fixCorruptSizeFields(File wavFile) throws IOException {
		long realFileSize = wavFile.length();
		RandomAccessFile raf = null;
		byte[] array = new byte[4];
		try {
			raf = new RandomAccessFile(wavFile, "rw");
			raf.seek(0);
			WavReader.read(raf, array, 4);
			String chunkID = readString(array, 0, 4);

			// validate the expected presence of the "RIFF" and "WAVE" strings:
			if (!"RIFF".equals(chunkID))
				throw new IOException(
						"the file did not begin with a RIFF header: "
								+ wavFile.getAbsolutePath());

			raf.seek(8);
			WavReader.read(raf, array, 4);
			chunkID = readString(array, 0, 4);

			if (!"WAVE".equals(chunkID))
				throw new IOException(
						"the file did not contain a WAVE identifier: "
								+ wavFile.getAbsolutePath());

			// we have 2 size fields to fix:

			// First: the size of the wave file (after the "RIFF" identifier)

			raf.seek(4);
			WavFileWriter.writeLong(array, realFileSize - 8, 4);
			raf.write(array, 0, 4);

			// Second: the size of the data chunk (after the "data" identifier)
			// To do this, we have to find where the "data" identifier is:

			raf.seek(12);
			while (raf.getFilePointer() < realFileSize) {
				WavReader.read(raf, array, 4);
				chunkID = readString(array, 0, 4);

				if ("data".equals(chunkID)) {
					long dataChunkSize = realFileSize - raf.getFilePointer()
							- 4;
					WavFileWriter.writeLong(array, dataChunkSize, 4);
					raf.write(array, 0, 4);
					return;
				}
				WavReader.read(raf, array, 4);
				long chunkSize = readLong(array, 0, 4);

				raf.seek(raf.getFilePointer() + chunkSize);
			}
		} finally {
			if (raf != null) {
				try {
					raf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * This validates the first header of a wave file.
	 * <p>
	 * This method was written in response to QTJ's MovieExporter occasionally
	 * converting MP3s to a wave file incorrectly. The resulting wave file
	 * contained all the correct PCM data, but the chunk size of the "RIFF" and
	 * "data" chunks are zero.
	 * <p>
	 * This method only validates the first header. If this method returns
	 * false, the method <code>fixCorruptSizeFields()</code> should fix the
	 * problem.
	 * 
	 * @param wavFile
	 * @return true if the the "RIFF" size header is consistent with the real
	 *         file size.
	 */
	public static boolean isValidSizeHeader(File wavFile) {
		long realFileSize = wavFile.length();
		FileInputStream in = null;

		byte[] array = new byte[4];

		try {
			in = new FileInputStream(wavFile);

			read(in, array, 4);
			String chunkID = readString(array, 0, 4);
			if (!chunkID.equals("RIFF")) {
				return false;
			}

			read(in, array, 4);
			long recordedFileSize = readLong(array, 0, 4);

			if (recordedFileSize + 8 != realFileSize) {
				return false;
			}

			read(in, array, 4);
			chunkID = readString(array, 0, 4);
			if (!chunkID.equals("WAVE")) {
				return false;
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static AudioInputStream createAudioInputStream(File file)
			throws IOException {
		FileInputStream fileIn = null;
		try {
			fileIn = new FileInputStream(file);
			return createAudioInputStream(fileIn);
		} catch (RuntimeException e) {
			if (fileIn != null) {
				fileIn.close();
			}
			throw e;
		} catch (IOException e) {
			if (fileIn != null) {
				fileIn.close();
			}
			throw e;
		}
	}

	/**
	 * Create an AudioInputStream based on a wave file's InputStream.
	 * <p>
	 * This was mostly an exercise on my part, I do not have any reason to
	 * recommend this method over using AudioSystem's default wave file support.
	 * 
	 * @param in
	 *            the input to create an AudioInputStream for.
	 * @return an AudioInputStream based on the argument.
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public static AudioInputStream createAudioInputStream(InputStream in)
			throws IOException {
		final PipedOutputStream pipedOut = new PipedOutputStream();
		PipedInputStream pipedIn = new PipedInputStream(pipedOut);

		final WavReader reader = new WavReader(in) {
			@Override
			protected void processSamples(byte[] sample, int offset,
					int length, int numberOfSamples) throws IOException {
				pipedOut.write(sample, offset, length);
			}
		};

		Thread thread = new Thread("WavReader: piping audio") {
			@Override
			public void run() {
				try {
					try {
						reader.read();
					} finally {
						pipedOut.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		thread.start();

		while (thread.isAlive()
				&& (reader.getFormatChunk() == null || reader.dataChunkSize == -1)) {
			try {
				Thread.sleep(15);
			} catch (Exception e) {
			}
		}

		AudioFormat format = reader.getAudioFormat();
		/** the length in sample frames of the data in this stream; */
		long sampleFrames = reader.dataChunkSize / format.getFrameSize();
		return new AudioInputStream(pipedIn, format, sampleFrames);
	}

	byte[] bytes = new byte[4];
	final MeasuredInputStream in;
	final long size;
	protected WavFormatChunk lastFormatChunk;
	private long dataChunkSize = -1;

	public WavReader(InputStream in) throws IOException {
		this.in = new MeasuredInputStream(in);
		String chunkID = readString(4);
		if (!chunkID.equals("RIFF")) {
			println(bytes, 4);
			throw new IOException("This stream began with \"" + chunkID
					+ "\" instead of \"RIFF\"");
		}
		size = readLong(4) + 8;
		String riffType = readString(4);
		if (!riffType.equals("WAVE")) {
			println(bytes, 4);
			throw new IOException("This RIFF type was \"" + riffType
					+ "\" instead of \"WAVE\".");
		}
	}

	/**
	 * Returns an AudioFormat object based on the last WavFormatChunk read. Note
	 * this will be null until <code>read()</code> is called.
	 * 
	 * @return the AudioFormat for this WavReader.
	 */
	public AudioFormat getAudioFormat() {
		if (lastFormatChunk == null)
			throw new NullPointerException();

		return new AudioFormat((lastFormatChunk.sampleRate) / 1000f,
				lastFormatChunk.sigBitsPerSample, lastFormatChunk.numChannels,
				lastFormatChunk.sigBitsPerSample != 8, false);
	}

	/**
	 * Read all the data in this file.
	 */
	public void read() throws IOException {
		while (this.in.getReadBytes() != size) {
			String chunkID;
			try {
				chunkID = readString(4);
			} catch (EmptyReadException e) {
				/**
				 * T4L bug 15259: Some encoders must write a byte "0" to signify
				 * EOF?
				 */
				break;
			}
			long chunkSize = readLong(4);
			readChunk(chunkID, chunkSize);
		}
	}

	/**
	 * Skips all the data in this file.
	 * 
	 * @return the number of bytes that contain audio data. This will be the sum
	 *         of the length of all the byte arrays passed to
	 *         {@link #processSamples(byte[], int, int, int)}.
	 * @throws IOException
	 */
	public long skip() throws IOException {
		long sum = 0;
		while (this.in.getReadBytes() != size) {
			String chunkID;
			try {
				chunkID = readString(4);
			} catch (EmptyReadException e) {
				/**
				 * T4L bug 15259: Some encoders must write a byte "0" to signify
				 * EOF?
				 */
				break;
			}
			long chunkSize = readLong(4);
			if ("fmt ".equals(chunkID)) {
				// we may need getAudioFormat to work later, plus this is a
				// pretty cheap call to make
				readChunk(chunkID, chunkSize);
			} else if ("data".equals(chunkID)) {
				sum += chunkSize;
				in.skip(chunkSize);
			} else {
				in.skip(chunkSize);
			}
		}
		return sum;
	}

	/**
	 * Returns the last WavFormatChunk read. Note this will be null until
	 * <code>read()</code> is called.
	 * 
	 * @return the format chunk for this WavReader.
	 */
	public WavFormatChunk getFormatChunk() {
		return lastFormatChunk;
	}

	protected synchronized void readChunk(String id, long size)
			throws IOException {
		if (id.equals("fmt ")) {
			byte[] array = new byte[(int) size];
			read(array, array.length);
			WavFormatChunk chunk = new WavFormatChunk(array);
			lastFormatChunk = chunk;
		} else if (id.equals("data")) {
			dataChunkSize = size;
			if (lastFormatChunk == null) {
				System.err
						.println("no format chunk available; unable to interpret data.");
				skip(size);
			} else if (lastFormatChunk.compressionCode != WavFormatChunk.COMPRESSION_PCM) {
				System.err.println("unsupported compression code: "
						+ lastFormatChunk.compressionCode
						+ "; unable to interpret data.");
				skip(size);
			} else {
				int bps = lastFormatChunk.sigBitsPerSample;
				int mod = bps % 8;
				bps = bps + mod;
				int sampleSize = bps / 8;
				byte[] sample = new byte[sampleSize * 2048];
				long dataRead = 0;
				while (dataRead < size) {
					int length = sample.length;
					if (size - dataRead < length) {
						length = (int) (size - dataRead);
						if (length % sampleSize != 0) {
							int k = length / sampleSize + 1;
							length = sampleSize * k;
						}
					}
					read(sample, length);
					processSamples(sample, 0, length, length / sampleSize);
					dataRead += sample.length;
				}
			}
		} else {
			System.err.println("skipping \"" + id + "\" of size " + size);
			skip(size);
		}
	}

	/**
	 * Subclasses should override this method to interpret the audio sample.
	 * 
	 */
	protected void processSamples(byte[] sample, int offset, int length,
			int numberOfSamples) throws IOException {

	}

	/**
	 * Reads a number off this stream.
	 * 
	 * @param len
	 *            the number of bytes this number requires.
	 * @return the number.
	 */
	private synchronized long readLong(int len) throws IOException {
		read(bytes, len);
		return readLong(bytes, 0, len);
	}

	/**
	 * Reads a number.
	 * 
	 * @param bytes
	 *            the array the number is stored in.
	 * @param len
	 *            the number of bytes this number requires.
	 * @return the number.
	 */
	protected static int readInt(byte[] bytes, int offset, int len)
			throws IOException {
		if (len > 4)
			throw new RuntimeException("len (" + len + ") > 4");

		int value = 0;
		for (int a = 0; a < len; a++) {
			value = (value << 8) + (bytes[offset + len - 1 - a] & 0xff);
		}
		return value;
	}

	/**
	 * Reads a number.
	 * 
	 * @param bytes
	 *            the array the number is stored in.
	 * @param len
	 *            the number of bytes this number requires.
	 * @return the number.
	 */
	protected static long readLong(byte[] bytes, int offset, int len)
			throws IOException {
		if (len > 8)
			throw new RuntimeException("len (" + len + ") > 8");

		long value = 0;
		for (int a = 0; a < len; a++) {
			value = (value << 8) + (bytes[offset + len - 1 - a] & 0xff);
		}
		return value;
	}

	private static final StringBuffer stringBuffer = new StringBuffer();

	/**
	 * Reads a string off this stream.
	 * 
	 * @param len
	 *            the number of bytes this string requires.
	 * @return the string.
	 */
	private synchronized String readString(int len) throws IOException {
		read(bytes, len);
		return readString(bytes, 0, len);
	}

	/**
	 * Reads a string.
	 * 
	 * @param bytes
	 *            the array this string is stored in.
	 * @param len
	 *            the number of bytes this string requires.
	 * @return the string.
	 */
	protected static String readString(byte[] bytes, int offset, int len) {
		synchronized (stringBuffer) {
			stringBuffer.setLength(0);
			for (int a = 0; a < len; a++) {
				char c = (char) bytes[offset + a];
				stringBuffer.append(c);
			}
			return stringBuffer.toString();
		}
	}

	private void println(byte[] bytes, int len) {
		StringBuffer sb = new StringBuffer();
		for (int a = 0; a < len; a++) {
			sb.append("'" + ((char) bytes[a]) + "' ");
		}

		sb.append("(");
		for (int a = 0; a < len; a++) {
			sb.append(Byte.toString(bytes[a]));
			if (a != len - 1) {
				sb.append(' ');
			}
		}
		sb.append(")");
		System.err.println(sb);
	}

	/**
	 * Reads a fixed amount of bytes off the input stream.
	 * 
	 * @param amt
	 * @throws IOException
	 */
	private synchronized void read(byte[] dest, int amt) throws IOException {
		read(in, dest, amt);
	}

	private static synchronized void read(InputStream in, byte[] dest, int amt)
			throws IOException {
		int off = 0;
		while (off < amt) {
			int read = in.read(dest, off, amt - off);
			if (read == -1) {
				if (off == 1 && dest[0] == 0) // if we read exactly one 0-byte
					throw new EmptyReadException();
				if (off == 0) // if we read nothing
					throw new EmptyReadException();
				throw new EOFException("expected " + amt + " bytes, but only "
						+ off + " were read before the stream ended.");
			}
			off += read;
		}
	}

	private static synchronized void read(RandomAccessFile raf, byte[] dest,
			int amt) throws IOException {
		int off = 0;
		while (off < amt) {
			int read = raf.read(dest, off, amt - off);
			if (read == -1) {
				if (off == 1 && dest[0] == 0) // if we read exactly one 0-byte
					throw new EmptyReadException();
				if (off == 0) // if we read nothing
					throw new EmptyReadException();
				throw new EOFException("expected " + amt + " bytes, but only "
						+ off + " were read before the stream ended.");
			}
			off += read;
		}
	}

	private synchronized void skip(long amt) throws IOException {
		long off = 0;
		while (off < amt) {
			long read = in.skip(amt - off);
			if (read == -1)
				throw new IOException("expected " + amt + " bytes, but only "
						+ off + " were skipped before the stream ended.");
			off += read;
		}
	}

	private static class EmptyReadException extends EOFException {
		private static final long serialVersionUID = 1L;

	}
}