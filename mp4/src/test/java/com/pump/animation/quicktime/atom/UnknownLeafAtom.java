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

import com.pump.io.GuardedInputStream;
import com.pump.io.GuardedOutputStream;

/**
 * This is a LeafAtom this particular architecture doesn't (yet) support.
 */
public class UnknownLeafAtom extends LeafAtom {
	protected byte[] data;
	protected String id;

	public UnknownLeafAtom(String id, byte[] data) {
		super(null);
		this.id = id;
		this.data = data;
	}

	public UnknownLeafAtom(Atom parent, String id, GuardedInputStream in)
			throws IOException {
		super(parent);
		this.id = id;
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
	public String getIdentifier() {
		return id;
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
		return "UnknownLeafAtom[ \"" + getIdentifier() + "\", \""
				+ sb.toString() + "\" ]";
	}

	/**
	 * Return the byte data of this atom. (This is the data excluding the atom
	 * type and size.)
	 */
	public byte[] getData() {
		byte[] copy = new byte[data.length];
		System.arraycopy(data, 0, copy, 0, data.length);
		return copy;
	}
}