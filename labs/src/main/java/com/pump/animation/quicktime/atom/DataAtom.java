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
import java.nio.charset.Charset;

import com.pump.io.GuardedInputStream;
import com.pump.io.GuardedOutputStream;

/**
 * This is a "data" atom based on the mp4 specifications.
 */
public class DataAtom extends LeafAtom {

	public static final String ATOM_TYPE = "data";

	public static final int FLAG_CONTAINS_TEXT = 0x001;
	public static final int FLAG_CONTAINS_DATA = 0x000;
	public static final int FLAG_FOR_TMPO_OR_CPIL = 0x004;
	public static final int FLAG_CONTAINS_IMAGE_DATA = 0x00D;

	protected int version;
	protected int flags;
	protected byte[] data;

	public DataAtom(Atom parent, GuardedInputStream in) throws IOException {
		super(parent);
		int size = (int) in.getRemainingLimit();

		version = in.read();
		flags = read24Int(in);

		// 4 reserved bytes (should be zero)
		read32Int(in);

		try {
			data = new byte[size - 8];
		} catch (OutOfMemoryError e) {
			System.err.println("size: " + size);
			throw e;
		}
		read(in, data);
	}

	/**
	 * If this data atom represents text, then this returns that text as a
	 * String. Otherwise this returns null.
	 */
	public String getText() {
		if (getFlags() == FLAG_CONTAINS_TEXT) {
			return new String(data, Charset.forName("UTF-8"));
		}
		return null;
	}

	@Override
	public String toString() {
		String text = getText();
		if (text != null) {
			return "DataAtom[ \"" + text + "\"]";
		}
		return super.toString();
	}

	@Override
	public String getIdentifier() {
		return ATOM_TYPE;
	}

	@Override
	protected void writeContents(GuardedOutputStream out) throws IOException {
		out.write(version);
		write24Int(out, flags);
		out.write(data);
	}

	@Override
	protected long getSize() {
		return 8 + data.length;
	}

	public int getVersion() {
		return version;
	}

	public int getFlags() {
		return flags;
	}

	public byte[] getData() {
		byte[] copy = new byte[data.length];
		System.arraycopy(data, 0, copy, 0, data.length);
		return copy;
	}

}