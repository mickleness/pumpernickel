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
package com.pump.animation.quicktime.atom;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.pump.io.GuardedOutputStream;

/**
 * Data reference atoms contain tabular data that instructs the data handler
 * component how to access the media’s data.
 */
public class DataReferenceAtom extends LeafAtom {

	public static class DataReferenceEntry {
		byte[] data;
		String type;
		int version;
		int flags;

		public DataReferenceEntry(String type, int version, int flags,
				byte[] data) {
			this.type = type;
			this.version = version;
			this.flags = flags;
			this.data = data;
		}

		public DataReferenceEntry(InputStream in) throws IOException {
			long size = Atom.read32Int(in);
			type = Atom.read32String(in);
			version = in.read();
			flags = Atom.read24Int(in);
			data = new byte[(int) size - 12];
			Atom.read(in, data);
		}

		public String getType() {
			return type;
		}

		public int getVersion() {
			return version;
		}

		public int getFlags() {
			return flags;
		}

		protected long getSize() {
			return 12 + data.length;
		}

		protected void write(OutputStream out) throws IOException {
			Atom.write32Int(out, 12 + data.length);
			Atom.write32String(out, type);
			out.write(version);
			Atom.write24Int(out, flags);
			out.write(data);
		}

		@Override
		public String toString() {
			return "DataReferenceEntry[ type=\"" + type + "\", " + "version="
					+ version + ", " + "flags=" + flags + ", " + "data=\""
					+ (new String(data)) + "\"]";
		}
	}

	/** "dref" */
	public static final String ATOM_TYPE = "dref";

	protected int version = 0;
	protected int flags = 0;
	protected DataReferenceEntry[] entries = new DataReferenceEntry[0];

	public DataReferenceAtom(int version, int flags) {
		super(null);
		this.version = version;
		this.flags = flags;
	}

	public DataReferenceAtom() {
		super(null);
	}

	public void addEntry(String type, int version, int flags, byte[] data) {
		DataReferenceEntry e = new DataReferenceEntry(type, version, flags,
				data);
		DataReferenceEntry[] newArray = new DataReferenceEntry[entries.length
				+ 1];
		System.arraycopy(entries, 0, newArray, 0, entries.length);
		newArray[newArray.length - 1] = e;
		entries = newArray;
	}

	public DataReferenceAtom(Atom parent, InputStream in) throws IOException {
		super(parent);

		version = in.read();
		flags = read24Int(in);
		int entryCount = (int) read32Int(in);
		entries = new DataReferenceEntry[entryCount];
		for (int a = 0; a < entries.length; a++) {
			entries[a] = new DataReferenceEntry(in);
		}
	}

	@Override
	public String getIdentifier() {
		return ATOM_TYPE;
	}

	@Override
	protected long getSize() {
		long sum = 16;
		for (int a = 0; a < entries.length; a++) {
			sum += entries[a].getSize();
		}
		return sum;
	}

	@Override
	protected void writeContents(GuardedOutputStream out) throws IOException {
		out.write(version);
		write24Int(out, flags);
		write32Int(out, entries.length);
		for (int a = 0; a < entries.length; a++) {
			entries[a].write(out);
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[ ");
		for (int a = 0; a < entries.length; a++) {
			if (a != 0) {
				sb.append(", ");
			}
			sb.append(entries[a].toString());
		}
		sb.append(" ]");
		String entriesString = sb.toString();

		return "DataReferenceAtom[ version=" + version + ", " + "flags=" + flags
				+ ", " + "entries=" + entriesString + "]";
	}

	/**
	 * Return a 3-byte space for data reference flags. Set this field to 0.
	 */
	public int getFlags() {
		return flags;
	}

	/**
	 * Return a 1-byte specification of the version of this data reference atom.
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Return the number of data reference entries in this table.
	 */
	public int getEntryCount() {
		return entries.length;
	}

	/**
	 * Return a specific data reference entry.
	 */
	public DataReferenceEntry getEntry(int index) {
		return entries[index];
	}
}