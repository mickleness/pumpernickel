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
package com.pump.animation.quicktime.atom;

import java.io.IOException;
import java.io.InputStream;

import com.pump.io.GuardedOutputStream;

/**
 * Chunk offset atoms identify the location of each chunk of data in the media’s
 * data stream. Chunk offset atoms have an atom type of 'stco'.
 * <p>
 * The chunk-offset table gives the index of each chunk into the containing
 * file. There are two variants, permitting the use of 32-bit or 64-bit offsets.
 * The latter is useful when managing very large movies. Only one of these
 * variants occurs in any single instance of a sample table atom.
 * <p>
 * Note that offsets are file offsets, not the offset into any atom within the
 * file (for example, a 'mdat' atom). This permits referring to media data in
 * files without any atom structure. However, be careful when constructing a
 * self-contained QuickTime file with its metadata (movie atom) at the front
 * because the size of the movie atom affects the chunk offsets to the media
 * data.
 * <p>
 * Note: The sample table atom can contain a 64-bit chunk offset atom
 * (STChunkOffset64AID = 'co64'). When this atom appears, it is used in place of
 * the original chunk offset atom, which can contain only 32-bit offsets. When
 * QuickTime writes movie files, it uses the 64-bit chunk offset atom only if
 * there are chunks that use the high 32-bits of the chunk offset. Otherwise,
 * the original 32-bit chunk offset atom is used to ensure compatibility with
 * previous versions of QuickTime. See {@link ChunkOffset64Atom}.
 */
public class ChunkOffsetAtom extends AbstractChunkOffsetAtom {

	/** "stco" */
	public static final String ATOM_TYPE = "stco";

	protected int version = 0;
	protected int flags = 0;
	protected long[] offsetTable = new long[0];

	public ChunkOffsetAtom(int version, int flags) {
		super(null);
		this.version = version;
		this.flags = flags;
	}

	public ChunkOffsetAtom() {
		super(null);
	}

	public ChunkOffsetAtom(Atom parent, InputStream in) throws IOException {
		super(parent);
		version = in.read();
		flags = read24Int(in);
		int arraySize = (int) read32Int(in);
		offsetTable = new long[arraySize];
		for (int a = 0; a < offsetTable.length; a++) {
			offsetTable[a] = read32Int(in);
		}
	}

    @Override
	public long getChunkOffset(int index) {
		return offsetTable[index];
	}

    @Override
	public int getChunkOffsetCount() {
		return offsetTable.length;
	}

    @Override
	public int getVersion() {
		return version;
	}

    @Override
	public int getFlags() {
		return flags;
	}

    @Override
	public void setChunkOffset(int index, long value) {
		offsetTable[index] = value;
	}

    @Override
	public void addChunkOffset(long offset) {
		long[] newArray = new long[offsetTable.length + 1];
		System.arraycopy(offsetTable, 0, newArray, 0, offsetTable.length);
		newArray[newArray.length - 1] = offset;
		offsetTable = newArray;
	}

	@Override
	public String getIdentifier() {
		return ATOM_TYPE;
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

		return "ChunkOffsetAtom[ version=" + version + ", " + "flags=" + flags
				+ ", " + "sizeTable=" + entriesString + "]";
	}
}