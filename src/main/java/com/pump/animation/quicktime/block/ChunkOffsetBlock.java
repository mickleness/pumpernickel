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
package com.pump.animation.quicktime.block;

import java.io.IOException;
import java.io.InputStream;

import com.pump.io.GuardedOutputStream;

/**
 * Chunk offset blocks identify the location of each chunk of data in the
 * media’s data stream. Chunk offset blocks have an block type of 'stco'.
 * <p>
 * The chunk-offset table gives the index of each chunk into the containing
 * file. There are two variants, permitting the use of 32-bit or 64-bit offsets.
 * The latter is useful when managing very large movies. Only one of these
 * variants occurs in any single instance of a sample table block.
 * <p>
 * Note that offsets are file offsets, not the offset into any block within the
 * file (for example, a 'mdat' block). This permits referring to media data in
 * files without any block structure. However, be careful when constructing a
 * self-contained QuickTime file with its metadata (movie block) at the front
 * because the size of the movie block affects the chunk offsets to the media
 * data.
 * <p>
 * Note: The sample table block can contain a 64-bit chunk offset block
 * (STChunkOffset64AID = 'co64'). When this block appears, it is used in place
 * of the original chunk offset block, which can contain only 32-bit offsets.
 * When QuickTime writes movie files, it uses the 64-bit chunk offset block only
 * if there are chunks that use the high 32-bits of the chunk offset. Otherwise,
 * the original 32-bit chunk offset block is used to ensure compatibility with
 * previous versions of QuickTime.
 */
public class ChunkOffsetBlock extends LeafBlock {

	/** "stco" */
	public static final String BLOCK_TYPE = "stco";

	protected int version = 0;
	protected int flags = 0;
	protected long[] offsetTable = new long[0];

	public ChunkOffsetBlock(int version, int flags) {
		super(null);
		this.version = version;
		this.flags = flags;
	}

	public ChunkOffsetBlock() {
		super(null);
	}

	public ChunkOffsetBlock(Block parent, InputStream in) throws IOException {
		super(parent);
		version = in.read();
		flags = read24Int(in);
		int arraySize = (int) read32Int(in);
		offsetTable = new long[arraySize];
		for (int a = 0; a < offsetTable.length; a++) {
			offsetTable[a] = read32Int(in);
		}
	}

	public long getChunkOffset(int index) {
		return offsetTable[index];
	}

	public int getChunkOffsetCount() {
		return offsetTable.length;
	}

	/**
	 * Return a 1-byte specification of the version of this chunk offset block.
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Return a 3-byte space for chunk offset flags. Set this field to 0.
	 */
	public int getFlags() {
		return flags;
	}

	/**
	 * Set a chunk offset.
	 * 
	 * @param index
	 *            the element in the table to replace
	 * @value the new value to insert into the table
	 */
	public void setChunkOffset(int index, long value) {
		offsetTable[index] = value;
	}

	/**
	 * Add a new chunk offset to this table.
	 */
	public void addChunkOffset(long offset) {
		long[] newArray = new long[offsetTable.length + 1];
		System.arraycopy(offsetTable, 0, newArray, 0, offsetTable.length);
		newArray[newArray.length - 1] = offset;
		offsetTable = newArray;
	}

	@Override
	public String getBlockType() {
		return BLOCK_TYPE;
	}

	@Override
	protected long getSize() {
		return 16 + offsetTable.length * 4;
	}

	@Override
	protected void writeContents(GuardedOutputStream out) throws IOException {
		out.write(version);
		write24Int(out, flags);
		write32Int(out, offsetTable.length);
		for (int a = 0; a < offsetTable.length; a++) {
			write32Int(out, offsetTable[a]);
		}
	}

	@Override
	public String toString() {
		String entriesString;
		if (offsetTable.length > 50 && ABBREVIATE) {
			entriesString = "[ ... ]";
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append("[ ");
			for (int a = 0; a < offsetTable.length; a++) {
				if (a != 0) {
					sb.append(", ");
				}
				sb.append(offsetTable[a]);
			}
			sb.append(" ]");
			entriesString = sb.toString();
		}

		return "ChunkOffsetBlock[ version=" + version + ", " + "flags=" + flags
				+ ", " + "sizeTable=" + entriesString + "]";
	}
}