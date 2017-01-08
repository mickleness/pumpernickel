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
import java.io.OutputStream;

/**
 * This is encoder analogous to the {@link com.pump.image.gif.lzw.LZWInputStream}.
 * 
 * <P>
 * This encodes a series of bytes as LZW-compressed data.
 * 
 * <P>
 * This does not in any way break data out into GIF subblocks; this simply takes
 * several bytes and compresses them.
 * 
 * <P>
 * This class is based on Jef Poskanzer LZW encoding file, which is based on the
 * LZW encoding of several others who came before him. :) See the source code
 * for scattered references to authors.
 * <P>
 * This class took Poskanzer code and adapted it to really take advantage of the
 * <code>OutputStream</code> structure: data is written in small, discrete
 * chunks as you call <code>.write()</code>.
 * <P>
 * The most novel innovation of this class is the
 * <code>writeFewestBytes()</code> method, which makes a kind of lossy GIF
 * compression possible. This considers several possible values, and if possible
 * it writes the first code in the list that will yield some compression.
 * 
 */
public class LZWOutputStream extends OutputStream {
	private OutputStream out;

	private int initCodeSize;
	private int initBits;
	/** This was a local variable in the old code */
	private int fcode;
	/** This was a local variable in the old code */
	private int i;
	/** This was a local variable in the old code */
	private int ent = -1;
	/** This was a local variable in the old code */
	private int disp;
	/** This was a local variable in the old code */
	private int hsize_reg;
	/** This was a local variable in the old code */
	private int hshift;

	public LZWOutputStream(OutputStream out, int colorDepth,
			boolean writeInitialCodeSize) throws IOException {
		this.out = out;
		initCodeSize = Math.max(2, colorDepth);
		if (writeInitialCodeSize)
			out.write(initCodeSize); // write "initial code size" byte
		initBits = initCodeSize + 1;

		// adapted from old code:

		// Set up the globals: g_init_bits - initial number of bits
		g_init_bits = initBits;

		// Set up the necessary values
		clear_flg = false;
		n_bits = g_init_bits;
		maxcode = getMaxCode(n_bits);

		ClearCode = 1 << (initBits - 1);
		EOFCode = ClearCode + 1;
		free_ent = ClearCode + 2;

		hshift = 0;
		for (fcode = hsize; fcode < 65536; fcode *= 2)
			++hshift;
		hshift = 8 - hshift; // set hash code range bound

		hsize_reg = hsize;
		resetCodeTable(hsize_reg); // clear hash table

		output(ClearCode);

	}

	private boolean finished = false;

	public void close() throws IOException {
		finish();
		out.close();
	}

	public void finish() throws IOException {
		if (finished)
			throw new IOException("This stream has already been finished.");
		// Put out the final code.
		output(ent);
		output(EOFCode);
		finished = true;
	}

	public void flush() throws IOException {
		out.flush();
	}

	/**
	 * @return whether or not <code>finish()</code> or <code>close()</code>
	 *         has been called.
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * This takes a list of possible values to write and writes the first value
	 * in this list that will compress, or the first value in this list if none
	 * of the values fit into the current compression codes.
	 * 
	 * <P>
	 * This introduces a kind of "lossy" compression into GIFs, with which you
	 * can pass, for example, 6 very similar colors, and this write the color
	 * that will yield a smaller byte array.
	 * 
	 * @param c
	 *            a sorted list of values. The best possible match from this
	 *            list will be written.
	 * @param arrayLength
	 *            the length of values we should read from <code>c</code>
	 * @return the index that was used
	 * @throws IOException if an IO problem occurs.
	 */
	public int writeFewestBytes(int[] c, int arrayLength) throws IOException {
		if (finished)
			throw new IOException(
					"This stream has already been closed.  You cannot write data to a closed OutputStream.");
		for (int a = 0; a < arrayLength; a++) {
			if (c[a] < 0 || c[a] > 255) {
				System.err.println("a = " + a);
				throw new IOException("This value (" + c[a]
						+ ") must be between 0 and 255.");
			}
		}
		if (c.length == 0)
			throw new IOException("There were no values passed to this method.");
		if (ent == -1) {
			/** The very first thing we write... there will be no codes around: */
			write(c[0]);
			return 0;
		}

		int preferredIndex = -1;
		for (int a = 0; a < arrayLength; a++) {
			fcode = (c[a] << maxbits) + ent;
			i = (c[a] << hshift) ^ ent; // xor hashing
			if (preferredIndex == -1 && htab[i] == fcode) {
				preferredIndex = a;
			}
		}
		if (preferredIndex == -1)
			preferredIndex = 0;

		fcode = (c[preferredIndex] << maxbits) + ent;
		i = (c[preferredIndex] << hshift) ^ ent; // xor hashing

		if (htab[i] == fcode) {
			ent = codetab[i];
			return preferredIndex;
		} else if (htab[i] >= 0) {
			// non-empty slot
			// TODO: can this also consider the entire array and be improved?
			disp = hsize_reg - i; // secondary hash (after G. Knott)
			if (i == 0) {
				disp = 1;
			}
			do {
				if ((i -= disp) < 0)
					i += hsize_reg;

				if (htab[i] == fcode) {
					ent = codetab[i];
					return preferredIndex;
				}
			} while (htab[i] >= 0);
		}
		output(ent);
		ent = c[preferredIndex];
		if (free_ent < maxmaxcode) {
			codetab[i] = free_ent++; // code -> hashtable
			htab[i] = fcode;
		} else {
			clearHashTable();
		}
		return preferredIndex;
	}

	public void write(int c) throws IOException {
		if (finished)
			throw new IOException(
					"This stream has already been closed.  You cannot write data to a closed OutputStream.");
		c = (((byte) c) & 0xff);
		if (c < 0 || c > 255)
			throw new IOException("This value (" + c
					+ ") must be between 0 and 255.");
		if (ent == -1) {
			/** The very first thing we write! how exciting. */
			ent = c;
			return;
		}

		fcode = (c << maxbits) + ent;
		i = (c << hshift) ^ ent; // xor hashing

		if (htab[i] == fcode) {
			ent = codetab[i];
			return;
		} else if (htab[i] >= 0) {
			// non-empty slot
			disp = hsize_reg - i; // secondary hash (after G. Knott)
			if (i == 0) {
				disp = 1;
			}
			do {
				if ((i -= disp) < 0)
					i += hsize_reg;

				if (htab[i] == fcode) {
					ent = codetab[i];
					return;
				}
			} while (htab[i] >= 0);
		}
		output(ent);
		ent = c;
		if (free_ent < maxmaxcode) {
			codetab[i] = free_ent++; // code -> hashtable
			htab[i] = fcode;
		} else {
			clearHashTable();
		}
	}

	// ****************************************************************************
	// (J.E.) I didn't touch Jef Poskanzer's code from this point on. (Well, OK,
	// I changed the name of the sole outside method it accesses.) I figure
	// if I have no idea how something works, I shouldn't play with it :)
	//		
	// Despite its unencapsulated structure, this section is actually highly
	// self-contained. The calling code merely calls compress(), and the present
	// code calls nextPixel() in the caller. That's the sum total of their
	// communication. I could have dumped it in a separate class with a callback
	// via an interface, but it didn't seem worth messing with.
	// ****************************************************************************

	// GIFCOMPR.C - GIF Image compression routines
	//		
	// Lempel-Ziv compression based on 'compress'. GIF modifications by
	// David Rowley (mgardi@watdcsu.waterloo.edu)

	// General DEFINEs

	private static final int BITS = 12;
	private static final int HSIZE = 5003; // 80% occupancy

	// GIF Image compression - modified 'compress'
	//		
	// Based on: compress.c - File compression ala IEEE Computer, June 1984.
	//		
	// By Authors: Spencer W. Thomas (decvax!harpo!utah-cs!utah-gr!thomas)
	// Jim McKie (decvax!mcvax!jim)
	// Steve Davies (decvax!vax135!petsd!peora!srd)
	// Ken Turkowski (decvax!decwrl!turtlevax!ken)
	// James A. Woods (decvax!ihnp4!ames!jaw)
	// Joe Orost (decvax!vax135!petsd!joe)

	private int n_bits; // number of bits/code
	private int maxbits = BITS; // user settable max # bits/code
	private int maxcode; // maximum code, given n_bits
	private int maxmaxcode = 1 << BITS; // should NEVER generate this code

	private int getMaxCode(int n_bits) {
		return (1 << n_bits) - 1;
	}

	private int[] htab = new int[HSIZE];
	private int[] codetab = new int[HSIZE];

	private int hsize = HSIZE; // for dynamic table sizing

	private int free_ent = 0; // first unused entry

	// block compression parameters -- after all codes are used up,
	// and compression rate changes, start over.
	private boolean clear_flg = false;

	// Algorithm: use open addressing double hashing (no chaining) on the
	// prefix code / next character combination. We do a variant of Knuth's
	// algorithm D (vol. 3, sec. 6.4) along with G. Knott's relatively-prime
	// secondary probe. Here, the modular division first probe is gives way
	// to a faster exclusive-or manipulation. Also do block compression with
	// an adaptive reset, whereby the code table is cleared when the compression
	// ratio decreases, but after the table fills. The variable-length output
	// codes are re-sized at this point, and a special CLEAR code is generated
	// for the decompressor. Late addition: construct the table according to
	// file size for noticeable speed improvement on small files. Please direct
	// questions about this implementation to ames!jaw.

	private int g_init_bits;

	private int ClearCode;
	private int EOFCode;

	// output
	//		
	// Output the given code.
	// Inputs:
	// code: A n_bits-bit integer. If == -1, then EOF. This assumes
	// that n_bits =< wordsize - 1.
	// Outputs:
	// Outputs code to the file.
	// Assumptions:
	// Chars are 8 bits long.
	// Algorithm:
	// Maintain a BITS character long buffer (so that 8 codes will
	// fit in it exactly). Use the VAX insv instruction to insert each
	// code in turn. When the buffer fills up empty it and start over.

	private int cur_accum = 0;
	private int cur_bits = 0;

	private int masks[] = { 0x0000, 0x0001, 0x0003, 0x0007, 0x000F, 0x001F,
			0x003F, 0x007F, 0x00FF, 0x01FF, 0x03FF, 0x07FF, 0x0FFF, 0x1FFF,
			0x3FFF, 0x7FFF, 0xFFFF };

	byte[] ARRAY_1 = new byte[1];

	private void output(int code) throws IOException {
		cur_accum &= masks[cur_bits];

		if (cur_bits > 0)
			cur_accum |= (code << cur_bits);
		else
			cur_accum = code;

		cur_bits += n_bits;

		while (cur_bits >= 8) {
			ARRAY_1[0] = (byte) (cur_accum & 0xff);
			out.write(ARRAY_1);
			cur_accum >>= 8;
			cur_bits -= 8;
		}

		// If the next entry is going to be too big for the code size,
		// then increase it, if possible.
		if (free_ent > maxcode || clear_flg) {
			if (clear_flg) {
				maxcode = getMaxCode(n_bits = g_init_bits);
				clear_flg = false;
			} else {
				++n_bits;
				if (n_bits == maxbits)
					maxcode = maxmaxcode;
				else
					maxcode = getMaxCode(n_bits);
			}
		}

		if (code == EOFCode) {
			// At EOF, write the rest of the buffer.
			while (cur_bits > 0) {
				out.write(new byte[] { (byte) (cur_accum & 0xff) });
				cur_accum >>= 8;
				cur_bits -= 8;
			}
		}
	}

	/**
	 * Empties the hash table
	 * 
	 */
	private void clearHashTable() throws IOException {
		resetCodeTable(hsize);
		free_ent = ClearCode + 2;
		clear_flg = true;

		output(ClearCode);
	}

	/**
	 * Resets the table of codes so each value maps ot -1.
	 * 
	 * @param hsize
	 *            the number of entries to iterate through
	 */
	private void resetCodeTable(int hsize) {
		for (int i = 0; i < hsize; ++i)
			htab[i] = -1;
	}
}