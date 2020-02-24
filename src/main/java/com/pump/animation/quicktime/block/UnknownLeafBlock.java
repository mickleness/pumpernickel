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

import com.pump.io.GuardedInputStream;
import com.pump.io.GuardedOutputStream;

/**
 * This is a LeafBlock this particular architecture doesn't (yet) support.
 */
public class UnknownLeafBlock extends LeafBlock {
	protected byte[] data;
	protected String blockType;

	public UnknownLeafBlock(String blockType, byte[] data) {
		super(null);
		this.blockType = blockType;
		this.data = data;
	}

	public UnknownLeafBlock(Block parent, String blockType,
			GuardedInputStream in) throws IOException {
		super(parent);
		this.blockType = blockType;
		int size = (int) in.getRemainingLimit();
		try {
			data = new byte[size];
		} catch (OutOfMemoryError e) {
			System.err.println("size: " + size);
			throw e;
		}
		read(in, data);
	}

	@Override
	public String getBlockType() {
		return blockType;
	}

	@Override
	protected long getSize() {
		return 8 + data.length;
	}

	@Override
	protected void writeContents(GuardedOutputStream out) throws IOException {
		out.write(data);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int a = 0; a < Math.min(data.length, 64); a++) {
			sb.append((char) data[a]);
		}
		if (data.length > 64)
			sb.append("...");
		return "UnknownLeafBlock[ \"" + getBlockType() + "\", \""
				+ sb.toString() + "\" ]";
	}

	/**
	 * Return the byte data of this block. (This is the data excluding the block
	 * type and size.)
	 */
	public byte[] getData() {
		byte[] copy = new byte[data.length];
		System.arraycopy(data, 0, copy, 0, data.length);
		return copy;
	}
}