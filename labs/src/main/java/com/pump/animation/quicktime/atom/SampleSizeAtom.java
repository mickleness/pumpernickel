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
 * You use sample size atoms to specify the size of each sample in the media.
 * Sample size atoms have an atom type of 'stsz'.
 * <p>
 * The sample size atom contains the sample count and a table giving the size of
 * each sample. This allows the media data itself to be unframed. The total
 * number of samples in the media is always indicated in the sample count. If
 * the default size is indicated, then no table follows.
 */
public class SampleSizeAtom extends LeafAtom {

	/** "stsz" */
	public static final String ATOM_TYPE = "stsz";

	protected int version = 0;
	protected int flags = 0;
	protected long sampleSize = 0;
	protected long sampleCount;
	protected long[] sizeTable;

	public SampleSizeAtom(int version, int flags, long sampleSize,
			int sampleCount, long[] table) {
		super(null);
		this.version = version;
		this.flags = flags;
		this.sampleSize = sampleSize;
		this.sampleCount = sampleCount;
		this.sizeTable = table;
	}

	public SampleSizeAtom() {
		super(null);
		sizeTable = new long[0];
	}

	public SampleSizeAtom(Atom parent, GuardedInputStream in)
			throws IOException {
		super(parent);
		version = in.read();
		flags = read24Int(in);
		sampleSize = read32Int(in);
		sampleCount = read32Int(in);
		if (in.isAtLimit() == false) {
			sizeTable = new long[(int) sampleCount];
			for (int a = 0; a < sizeTable.length; a++) {
				sizeTable[a] = read32Int(in);
			}
		}
	}

	public void setSampleSize(long sampleSize) {
		this.sampleSize = sampleSize;
	}

	public void setSampleCount(long sampleCount) {
		this.sampleCount = sampleCount;
	}

	public void setSizeTable(long[] sizeTable) {
		this.sizeTable = sizeTable;
	}

	public void addSampleSize(long size) {
		long[] newArray = new long[sizeTable.length + 1];
		System.arraycopy(sizeTable, 0, newArray, 0, sizeTable.length);
		newArray[newArray.length - 1] = size;
		sizeTable = newArray;
	}

	@Override
	public String getIdentifier() {
		return ATOM_TYPE;
	}

	@Override
	protected long getSize() {
		if (sizeTable == null)
			return 20;
		return 20 + sizeTable.length * 4;
	}

	@Override
	protected void writeContents(GuardedOutputStream out) throws IOException {
		out.write(version);
		write24Int(out, flags);
		write32Int(out, sampleSize);
		if (sizeTable == null) {
			write32Int(out, sampleCount);
		} else {
			write32Int(out, sizeTable.length);
			for (int a = 0; a < sizeTable.length; a++) {
				write32Int(out, sizeTable[a]);
			}
		}
	}

	@Override
	public String toString() {
		String entriesString;
		if (sizeTable != null) {
			if (sizeTable.length > 50 && ABBREVIATE) {
				entriesString = "[ ... ]";
			} else {
				StringBuffer sb = new StringBuffer();
				sb.append("[ ");
				for (int a = 0; a < sizeTable.length; a++) {
					if (a != 0) {
						sb.append(", ");
					}
					sb.append(sizeTable[a]);
				}
				sb.append(" ]");
				entriesString = sb.toString();
			}
		} else {
			entriesString = "undefined";
		}

		return "SampleSizeAtom[ version=" + version + ", " + "flags=" + flags
				+ ", " + "sampleSize=" + sampleSize + ", sampleCount = "
				+ sampleCount + ", " + "sizeTable=" + entriesString + "]";
	}

	/**
	 * Return a 1-byte specification of the version of this sample size atom.
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Return a 3-byte space for sample size flags. Set this field to 0.
	 */
	public int getFlags() {
		return flags;
	}

	/**
	 * Return a 32-bit integer specifying the sample size.
	 */
	public long getSampleSize() {
		return sampleSize;
	}

	/**
	 * Return a 32-bit integer containing the count of entries in the sample
	 * size table.
	 * <p>
	 * Note {@link #getSizeTable()} may return null when this value is well
	 * defined.
	 */
	public long getSampleCount() {
		return sampleCount;
	}

	/**
	 * Return a table containing the sample size information.
	 */
	public long[] getSizeTable() {
		if (sizeTable == null)
			return null;

		long[] copy = new long[sizeTable.length];
		System.arraycopy(sizeTable, 0, copy, 0, sizeTable.length);
		return copy;
	}
}