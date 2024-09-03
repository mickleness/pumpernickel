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
package com.pump.image.gif.block;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This efficiently parses out an <code>InputStream</code> into the appropriate
 * {@link com.pump.image.gif.block.GifBlock}'s.
 */
public class GifParser {
	private final InputStream in;
	private GifLogicalScreenDescriptor lsd;
	private GifGlobalColorTable globalTable;
	private boolean finishedReading = false;
	int cachedNextType = -1;
	int lastBlockType = -1;
	private boolean readHeader = false;

	/**
	 * Creates a <code>GifParser</code>
	 * 
	 * @param in
	 *            this is <i>not</i> closed, unless you explicitly call the
	 *            <code>close()</code> method.
	 */
	public GifParser(InputStream in) {
		this.in = in;
	}

	private GifHeaderBlock readHeader() throws IOException {
		byte[] b = new byte[6];
		read2(b, false);
		return new GifHeaderBlock(b);
	}

	private GifLogicalScreenDescriptor readLogicalScreenDescriptor()
			throws IOException {
		byte[] byte7 = new byte[7];
		read2(byte7, true);
		lsd = new GifLogicalScreenDescriptor(byte7);
		return lsd;
	}

	private GifGlobalColorTable readGlobalColorTable() throws IOException {
		byte[] b = new byte[lsd.getGlobalColorTableSize() * 3];

		read2(b, true);
		globalTable = new GifGlobalColorTable(b);
		return globalTable;
	}

	/**
	 * Reads the entire array. (The usual InputStream.read() method reads an
	 * arbitrary amount of data.
	 */
	private void read2(byte[] array, boolean canDisplayOutput)
			throws IOException {
		int offset = 0;
		int t = 0;
		while (offset != array.length) {
			t = in.read(array, offset, array.length - offset);
			// System.out.println("offset="+offset+" t="+t+"
			// array.length="+array.length);

			if (t == -1) {
				if (canDisplayOutput)
					System.err.println("array.length = " + array.length
							+ " offset = " + offset);
				throw new IOException("Unexpected end of input stream.");
			}
			offset += t;
		}
	}

	/** The last GifImageDescriptor that was read */
	private GifImageDescriptor lastImageDescriptor;

	/**
	 * This reads the next {@link com.pump.image.gif.block.GifBlock}
	 * <P>
	 * Note that if this method returns a <code>APPLICATION_EXTENSION</code>, it
	 * may be a {@link com.pump.image.gif.block.GifLoopingApplicationExtension}
	 * or a {@link com.pump.image.gif.block.GifApplicationExtension}.
	 * 
	 * @return the next {@link com.pump.image.gif.block.GifBlock}, or
	 *         <code>null</code> if there are no more blocks to read.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public GifBlock readNextBlock() throws IOException {
		int i = getNextBlockType();
		if (i == -1)
			return null;
		lastBlockType = i;
		cachedNextType = -1;
		if (i == GifBlock.HEADER) {
			readHeader = true;
			return readHeader();
		} else if (i == GifBlock.TRAILER) {
			finishedReading = true;
			return new GifTrailerBlock();
		} else if (i == GifBlock.GLOBAL_COLOR_TABLE) {
			globalTable = readGlobalColorTable();
			return globalTable;
		} else if (i == GifBlock.LOGICAL_SCREEN_DESCRIPTOR) {
			lsd = readLogicalScreenDescriptor();
			return lsd;
		} else if (i == GifBlock.IMAGE_DESCRIPTOR) {
			byte[] b = new byte[9];
			read2(b, true);
			GifImageDescriptor d = new GifImageDescriptor(b);
			lastImageDescriptor = d;
			return d;
		} else if (i == GifBlock.LOCAL_COLOR_TABLE) {
			byte[] b = new byte[lastImageDescriptor.getLocalColorTableSize() * 3];
			read2(b, true);
			GifLocalColorTable t = new GifLocalColorTable(b);
			return t;
		} else if (i == GifBlock.IMAGE_DATA) {
			byte[] b = new byte[1];
			read2(b, true);
			byte[] data = readSubBlocks();
			GifImageDataBlock id = new GifImageDataBlock(b[0] & 0xFF, data);
			return id;
		} else if (i == GifBlock.GRAPHIC_CONTROL_EXTENSION) {
			i = in.read() & 0xFF;
			if (i != 4)
				throw new IOException(
						"A graphic control extension should have exactly 4 bytes of data.  Illegal block size: "
								+ i);
			byte[] b = new byte[4];
			read2(b, true);
			GifGraphicControlExtension gce = new GifGraphicControlExtension(b);
			in.read(); // the terminator byte
			return gce;
		} else if (i == GifBlock.COMMENT_EXTENSION) {
			byte[] b = readSubBlocks();
			String s = new String(b);
			GifCommentExtension gce = new GifCommentExtension(s);
			return gce;
		} else if (i == GifBlock.PLAIN_TEXT_EXTENSION) {
			byte[] b = new byte[12];
			in.read(); // read the block size, ignore it
			read2(b, true);
			byte[] b2 = readSubBlocks();
			GifPlainTextExtension pte = new GifPlainTextExtension(b, b2);
			return pte;
		} else if (i == GifBlock.APPLICATION_EXTENSION) {
			byte[] b = new byte[11];
			in.read(); // read the block size, ignore it
			read2(b, true);
			byte[] b2 = readSubBlocks();
			GifApplicationExtension pte = new GifApplicationExtension(b, b2);
			if (pte.isLoopingExtension())
				return pte.convertToLoopingExtension();
			return pte;
		} else {
			throw new RuntimeException("Unrecognized GIF block type (" + i
					+ ")");
		}
	}

	/**
	 * This closes the underlying <code>InputStream</code>.
	 * 
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public void close() throws IOException {
		in.close();
	}

	/**
	 * @return a field from {@link com.pump.image.gif.block.GifBlock}, or
	 *         <code>-1</code> if there are no more blocks.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public int getNextBlockType() throws IOException {
		if (finishedReading == true)
			return -1;
		if (cachedNextType != -1)
			return cachedNextType;
		if (readHeader == false)
			return GifBlock.HEADER;

		if (lsd == null) {
			cachedNextType = GifBlock.LOGICAL_SCREEN_DESCRIPTOR;
			return cachedNextType;
		} else if (globalTable == null && lsd.hasGlobalColorTable()) {
			cachedNextType = GifBlock.GLOBAL_COLOR_TABLE;
			return cachedNextType;
		}

		/**
		 * There are a few basic rules here... depending on the last block, we
		 * MAY already know what the next block should be:
		 */
		if (lastBlockType == GifBlock.LOCAL_COLOR_TABLE) {
			cachedNextType = GifBlock.IMAGE_DATA;
			return cachedNextType;
		} else if (lastBlockType == GifBlock.IMAGE_DESCRIPTOR) {
			if (lastImageDescriptor.hasLocalColorTable()) {
				cachedNextType = GifBlock.LOCAL_COLOR_TABLE;
			} else {
				cachedNextType = GifBlock.IMAGE_DATA;
			}
			return cachedNextType;
		}

		int i = in.read();
		if (i == -1) {
			System.err.println("A GIF file is supposed to end with 0x3B.");
			throw new IOException("This GIF file did not terminate correctly.");
		}

		if (i == 0x2C) {
			cachedNextType = GifBlock.IMAGE_DESCRIPTOR;
		} else if (i == 0x3B) {
			cachedNextType = GifBlock.TRAILER;
		} else if (i == 0x21) {
			// we have an extension of SOME sort...
			i = in.read() & 0xFF;
			if (i == 0xF9) {
				// a graphic control extension
				cachedNextType = GifBlock.GRAPHIC_CONTROL_EXTENSION;
			} else if (i == 0xFE) {
				cachedNextType = GifBlock.COMMENT_EXTENSION;
			} else if (i == 0x01) {
				cachedNextType = GifBlock.PLAIN_TEXT_EXTENSION;
			} else if (i == 0xFF) {
				cachedNextType = GifBlock.APPLICATION_EXTENSION;
			} else {
				String s = Integer.toString(i, 16);
				while (s.length() < 2) {
					s = "0" + s;
				}
				throw new IOException("Unrecognized extension type: " + s);
			}
		} else if (i == 0x00) {
			// Isn't this a treat? There can be empty "blocks"
			// scattered around that do absolutely nothing.
			// what encoders write these? Why aren't they documented?

			// empirical mucking around suggests these are simply
			// 1-byte, empty blocks, and they can be skipped.
			return getNextBlockType();
		} else {
			String s = Integer.toString(i, 16);
			while (s.length() < 2) {
				s = "0" + s;
			}
			throw new RuntimeException("Unrecognized GIF block: 0x" + s);
		}
		return cachedNextType;
	}

	/**
	 * Skips the next block.
	 * <P>
	 * This method may be useful if you only want to skim an input stream to see
	 * if a GIF file has more than 1 image block (that is, is it an "animated
	 * GIF" or not), or if you want to skim an input stream to count the number
	 * of frames, or the total duration of all the frames, etc.
	 * 
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public void skipNextBlock() throws IOException {
		int i = getNextBlockType();
		if (i == GifBlock.HEADER) {
			readHeader = true;
			in.skip(6);
		} else if (i == GifBlock.TRAILER) {
			finishedReading = true;
		} else if (i == GifBlock.LOGICAL_SCREEN_DESCRIPTOR) {
			// can't really skip this block
			// instead read it, because IT tells us
			// if the next block is a GifGlobalColorTable
			// block or not... we can't live without
			// that info:
			readLogicalScreenDescriptor();
		} else if (i == GifBlock.GLOBAL_COLOR_TABLE) {
			readGlobalColorTable();
		} else if (i == GifBlock.IMAGE_DESCRIPTOR) {
			// can't really skip this block
			// instead read it, because IT tells us
			// if the next block is a GifLocalColorTable
			// block or not... we can't live without
			// that info:
			readNextBlock();
		} else if (i == GifBlock.LOCAL_COLOR_TABLE) {
			in.skip(lastImageDescriptor.getLocalColorTableSize() * 3);
		} else if (i == GifBlock.IMAGE_DATA) {
			in.read(); // ignore the "LZW Minimum Code Size" byte
			skipSubBlocks();
		} else if (i == GifBlock.GRAPHIC_CONTROL_EXTENSION) {
			in.skip(6);
		} else if (i == GifBlock.COMMENT_EXTENSION) {
			skipSubBlocks();
		} else if (i == GifBlock.PLAIN_TEXT_EXTENSION) {
			in.read(); // read the block size, ignore it
			in.skip(12); // skip the data
			skipSubBlocks(); // skip the text
		} else if (i == GifBlock.APPLICATION_EXTENSION) {
			in.read(); // read the block size, ignore it
			in.skip(11); // skip the header
			skipSubBlocks(); // skip the text
		}
		lastBlockType = i;
		cachedNextType = -1;
	}

	ByteArrayOutputStream byteBuffer;

	/**
	 * Assuming the input stream is pointed to the beginning of several
	 * subblocks, this reads them.
	 */
	private byte[] readSubBlocks() throws IOException {
		if (byteBuffer == null) {
			byteBuffer = new ByteArrayOutputStream();
		} else {
			byteBuffer.reset();
		}
		int i = in.read() & 0xFF;
		byte[] b = null;
		while (i != 0) {
			if (b == null || b.length != i) {
				b = new byte[i];
			}
			read2(b, true);
			byteBuffer.write(b);
			i = in.read() & 0xFF;
		}
		return byteBuffer.toByteArray();
	}

	/**
	 * Assuming the input stream is pointed to the beginning of several
	 * subblocks, this skips over them.
	 */
	private void skipSubBlocks() throws IOException {
		int i = in.read() & 0xFF;
		while (i != 0) {
			in.skip(i);
			i = in.read() & 0xFF;
		}
	}

	/**
	 * @return <code>true</code> if there are more
	 *         {@link com.pump.image.gif.block.GifBlock}s available to read.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public boolean hasMoreBlocks() throws IOException {
		int i = getNextBlockType();
		if (i == -1)
			return false;
		return true;
	}
}