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
package com.pump.image.gif.lzw;

import java.io.IOException;
import java.io.InputStream;

import com.pump.util.ResourcePool;

/**
 * This is designed to read LZW-compressed GIF image data.
 * <P>
 * Note this decompress pure LZW-compressed GIF image data, it is assumed that
 * the information it reads is <i>not</i> divided into subblocks... this class
 * is not designed to deal with parsing out sublocks.
 * <P>
 * General info about LZW compression: <BR>
 * LZW compression dates back to the 80's. It's surrounded by a lot of
 * controversy because it was published and widely used for many years <i>before</i>
 * Unisys decided to enforce their patent on it... but all such patents --
 * including international ones -- are now expired. Meanwhile, I read once that
 * ZIP compression is generally as good or better than LZW compression, so for
 * my regular compression needs, I'm sticking with Java's built in
 * ZipInputStream and ZipOutputStream classes.
 * <P>
 * <P>
 * The pseudo-code for basic LZW decompression reads as follows: <BR>
 * <code>read OLD_CODE</code> <BR>
 * <code>output OLD_CODE</code> <BR>
 * <code>CHARACTER = OLD_CODE</code> <BR>
 * <code>WHILE there are still input characters DO</code> <BR>
 * <code>&nbsp; Read NEW_CODE</code> <BR>
 * <code>&nbsp; IF NEW_CODE is not in table THEN</code> <BR>
 * <code>&nbsp; &nbsp; STRING = get translation of OLD_CODE</code> <BR>
 * <code>&nbsp; &nbsp; STRING = STRING + CHARACTER</code> <BR>
 * <code>&nbsp; ELSE</code> <BR>
 * <code>&nbsp; &nbsp; STRING = get translation of NEW_CODE</code> <BR>
 * <code>&nbsp; END of IF</code> <BR>
 * <code>&nbsp; output STRING</code> <BR>
 * <code>&nbsp; CHARACTER = first character in STRING</code> <BR>
 * <code>&nbsp; add OLD_CODE + CHARACTER to table</code> <BR>
 * <code>&nbsp; OLD_CODE = NEW CODE</code> <BR>
 * <code>END of WHILE</code>
 * <P>
 * <P>
 * There are a couple of extra twists to GIF decompression:
 * <ul><LI>There is a <code>clearCode</code>. Any time this is encountered, the
 * table of keys will be emptied.</li>
 * <LI>The first entry has to be the <code>clearCode</code>.</li>
 * <LI>There are 2 ways to stop reading data. One is a special end-of-file
 * code; when this is encountered, we stop reading. The other is to simply run
 * out of incoming bytes.</li>
 * <LI>Codes can only reach 12 bits at most.</LI></ul>
 * <P>
 * <P>
 * These LZW classes would not be possible without Jef Poskanzer's GIF classes.
 * I have made a couple of improvements, and really shuffled the structures
 * around to [hopefully] organize the code a little better, but his examples
 * really helped. (By "improvements" I mean: over the years I've found one or
 * two GIFs that didn't work well with the codecs Jef originally wrote, and I
 * patched up the code accordingly.)
 */
public class LZWInputStream extends InputStream {

	private BitReader reader;

	private boolean outgoingDataCanBeRecycled = false;

	/** The data ready to be read */
	private byte[] outgoingData = null;
	/**
	 * The ptr to outgoingData indicating the first byte that has not yet been
	 * sent.
	 */
	private int outgoingDataPtr = -1;

	/** The magic table that stores all our keys */
	private byte[][] data = new byte[4096][];

	/** The number of bits we read at a time */
	private int n_bits;

	/** The code that tells us to clear out the table */
	private int clearCode;

	/** The code that tells us the file is finished */
	private int eofCode;

	/** The index in the table we're pointing to */
	private int availableCode;

	/** Whether we're finished reading or not */
	private boolean finished = false;

	/** The last code we read */
	private int lastCode = -1;

	/** A variable described in the algorithm */
	private int character = -1;

	/** The initial size we were passed */
	private int initialSize;

	/**
	 * Constructs a <code>LZWInputStream</code>.
	 * 
	 * @param in
	 *            the compressed input stream we're decompressing
	 * @param initSize
	 *            this is the initial size of data we read.
	 *            <P>
	 *            If this is 6, then this means we're reading 6-bits off of
	 *            every byte (at first) to identify our keys and patterns.
	 *            <P>
	 *            This also happens to be the first byte in a GIF image data
	 *            block.
	 */
	public LZWInputStream(InputStream in, int initSize) {
		reader = new BitReader(in);
		clearCode = (int) (Math.pow(2, initSize) + .5);
		initialSize = initSize;
		n_bits = initSize + 1;
		eofCode = clearCode + 1;
		availableCode = eofCode + 1;
	}

	private void iterate() throws IOException {
		if (outgoingData != null && outgoingDataPtr < outgoingData.length) {
			throw new RuntimeException(
					"New data should not have be unpacked if there is still some outgoing data that has not yet been read.");
		}
		int newCode = reader.read(n_bits); // Read NEW_CODE
		if (outgoingData != null && outgoingDataCanBeRecycled)
			ResourcePool.get().put(outgoingData);
		outgoingData = null;
		if (newCode == clearCode) {
			clearTable();

			newCode = reader.read(n_bits);
			if (newCode != -1) {
				lastCode = newCode; // read OLD_CODE
				outgoingData = new byte[1]; // output
																// OLD_CODE
				outgoingData[0] = (byte) lastCode;
				character = lastCode; // CHARACTER = OLD_CODE
			} else {
				finished = true;
			}
		} else if (newCode == eofCode || newCode == -1) {
			finished = true;
		} else {
			byte[] string = data[newCode]; // get translation of NEW_CODE)
			if (string == null) { // if NEW_CODE is not in table
				if (lastCode == -1)
					throw new RuntimeException(
							"The previous code read was -1, which probably means that this LZW data didn't begin with a clear-code like it should have.");
				string = data[lastCode]; // STRING = get translation of
											// OLD_CODE

				if (string == null) {
					throw new NullPointerException("Code " + lastCode
							+ " not in table.");
				}
				string = concatenate(string, (byte) character);// STRING =
																// STRING +
																// CHARACTER
				outgoingDataCanBeRecycled = true;
			} else {
				outgoingDataCanBeRecycled = false;
			}
			outgoingData = string; // output STRING

			character = string[0]; // CHARACTER = first character in STRING
			// add OLD_CODE+CHARACTER to table:
			data[availableCode] = concatenate(data[lastCode], (byte) character);

			availableCode++;
			lastCode = newCode;
		}
		n_bits = Math.min(12,
				(int) (Math.log(availableCode) / Math.log(2) + 1.0D));

		outgoingDataPtr = 0;
	}

	public int read() throws IOException {
		if (finished)
			return -1;
		if (outgoingData != null) {
			if (outgoingDataPtr < outgoingData.length) {
				int v = outgoingData[outgoingDataPtr++] & 0xFF;
				return v;
			}
		}
		iterate();
		return read();
	}

	public int read(byte[] b, int offset, int len) throws IOException {
		if (finished)
			return -1;
		if (outgoingData == null || outgoingDataPtr == outgoingData.length) {
			iterate();
			return read(b, offset, len);
		}
		int available = outgoingData.length - outgoingDataPtr;
		if (available < len)
			len = available;
		System.arraycopy(outgoingData, outgoingDataPtr, b, offset, len);
		outgoingDataPtr += len;
		return len;
	}

	public void close() throws IOException {
		reader.close();
	}

	public int available() throws IOException {
		if (outgoingData == null)
			return 0;
		return outgoingData.length - outgoingDataPtr;
	}

	private void clearTable() {
		int a = 0;
		while (a < clearCode) {
			data[a] = new byte[1];
			data[a][0] = (byte) a;
			a++;
		}
		while (a < data.length) {
			data[a] = null;
			a++;
		}
		availableCode = eofCode + 1;
		n_bits = initialSize + 1;
	}

	/** Just a convenient call to concatenate b1+b2 */
	private byte[] concatenate(byte[] b1, byte b2) {
		byte[] t = ResourcePool.get().getByteArray(b1.length + 1);
		System.arraycopy(b1, 0, t, 0, b1.length);
		t[b1.length] = b2;
		return t;
	}
}

/**
 * This slices the right-most bits of new bytes off an InputStream.
 */
class BitReader {
	private InputStream in;

	/** Used to extract the right-most bits out of bytes of data. */
	private static final int[] masks = { 0x0000, 0x0001, 0x0003, 0x0007,
			0x000F, 0x001F, 0x003F, 0x007F, 0x00FF, 0x01FF, 0x03FF, 0x07FF,
			0x0FFF, 0x1FFF, 0x3FFF, 0x7FFF, 0xFFFF };

	/**
	 * The number of bits that have been read off of the current byte already
	 */
	private int bitCounter = 0;

	/** The current byte we're splicing */
	private int current = 0;

	/** If this is true, calls to read() will always return -1 */
	private boolean finishedReading = false;

	/** We use this to jump-start the reading process */
	private boolean startedReading = false;

	/**
	 * Constructs a <code>BitReader</code>.
	 * 
	 * @param in
	 *            the <code>InputStream</code> to gather bytes from.
	 */
	public BitReader(InputStream in) {
		this.in = in;
	}

	/**
	 * This is only used if the LZWInputStream gets closed early for some
	 * reason.
	 * @throws IOException if an IO problem occurs.
	 */
	public void close() throws IOException {
		finishedReading = true;
		in.close();
	}

	/**
	 * Reads <code>bitsToRead</code>-many bits from the underlying
	 * <code>InputStream</code>.
	 * 
	 * @param bitsToRead
	 *            the number of bits to read.
	 *            <P>
	 *            If the <code>InputStream</code> is empty, then
	 *            <code>-1</code> is returned.
	 *            <P>
	 *            If this exceeds the number of bits left in the
	 *            <code>InputStream</code>, then an <code>IOException</code>
	 *            is thrown.
	 * @return the value of <code>bitsToRead</code>-many bits from the
	 *         <code>InputStream</code>, or <code>-1</code> if there is no
	 *         more data to read.
	 * @throws IOException
	 *             if an <code>IOException</code> occurred while reading the
	 *             underlying stream, or if
	 */
	public int read(int bitsToRead) throws IOException {
		if (finishedReading)
			return -1;
		if (startedReading == false) {
			current = in.read();
			startedReading = true;
		}
		int code = 0;
		int bitsRead = 0;
		int toRead = 0;
		while (bitsRead < bitsToRead) {
			// how many bits can we get this pass?
			// we want n_bits, but we might have to carry over into the next bit
			toRead = Math.min(bitsToRead - bitsRead, 8 - bitCounter);
			code = ((current >> bitCounter & masks[toRead]) << bitsRead) + code;
			bitCounter += toRead;
			if (bitCounter == 8) {
				bitCounter = 0;
				current = in.read();
				if (current == -1) {
					/**
					 * This is a cheap trick. Some GIFs simply stop... without
					 * an EOF code.
					 */
					finishedReading = true;
					return -1;
				}
			}
			bitsRead += toRead;
		}
		return code;
	}
}