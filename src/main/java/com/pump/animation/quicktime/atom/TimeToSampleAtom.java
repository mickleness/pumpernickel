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
 * Time-to-sample atoms store duration information for a media’s samples,
 * providing a mapping from a time in a media to the corresponding data sample.
 * The time-to-sample atom has an atom type of 'stts'.
 * <p>
 * You can determine the appropriate sample for any time in a media by examining
 * the time-to-sample atom table, which is contained in the time-to-sample atom.
 * <p>
 * The atom contains a compact version of a table that allows indexing from time
 * to sample number. Other tables provide sample sizes and pointers from the
 * sample number. Each entry in the table gives the number of consecutive
 * samples with the same time delta, and the delta of those samples. By adding
 * the deltas, a complete time-to-sample map can be built.
 * <p>
 * The atom contains time deltas: <code>DT(n+1) = DT(n) + STTS(n)</code> where
 * <code>STTS(n)</code> is the (uncompressed) table entry for sample n and DT is
 * the display time for sample (n). The sample entries are ordered by time
 * stamps; therefore, the deltas are all nonnegative. The DT axis has a zero
 * origin; <code>DT(i) = SUM</code> (for <code>j=0</code> to <code>i-1</code> of
 * <code>delta(j)</code>), and the sum of all deltas gives the length of the
 * media in the track (not mapped to the overall time scale, and not considering
 * any edit list). The edit list atom provides the initial DT value if it is
 * nonempty (nonzero).
 */
public class TimeToSampleAtom extends LeafAtom {

	/**
	 * This represents the duration of a series of samples. This indicates that
	 * <code>sampleCount</code>-many consecutive samples have a duration of
	 * <code>sampleDuration</code>. (The duration is relative to an enclosing
	 * time scale.)
	 */
	public static class TimeToSampleEntry {
		long sampleCount, sampleDuration;

		public TimeToSampleEntry(long count, long duration) {
			this.sampleCount = count;
			this.sampleDuration = duration;
		}

		public TimeToSampleEntry(InputStream in) throws IOException {
			sampleCount = Atom.read32Int(in);
			sampleDuration = Atom.read32Int(in);
		}

		@Override
		public String toString() {
			return "[" + sampleCount + ", " + sampleDuration + "]";
		}

		protected void write(OutputStream out) throws IOException {
			Atom.write32Int(out, sampleCount);
			Atom.write32Int(out, sampleDuration);
		}
	}

	/** "stts" */
	public static final String ATOM_TYPE = "stts";

	protected int version = 0;
	protected int flags = 0;
	protected TimeToSampleEntry[] table = new TimeToSampleEntry[0];

	public TimeToSampleAtom(int version, int flags) {
		super(null);
		this.version = version;
		this.flags = flags;
	}

	public TimeToSampleAtom() {
		super(null);
	}

	public TimeToSampleAtom(Atom parent, InputStream in) throws IOException {
		super(parent);
		version = in.read();
		flags = read24Int(in);
		int entryCount = (int) read32Int(in);
		table = new TimeToSampleEntry[entryCount];
		for (int a = 0; a < table.length; a++) {
			table[a] = new TimeToSampleEntry(in);
		}
	}

	@Override
	protected String getIdentifier() {
		return ATOM_TYPE;
	}

	@Override
	protected long getSize() {
		return 16 + table.length * 8;
	}

	@Override
	protected void writeContents(GuardedOutputStream out) throws IOException {
		out.write(version);
		write24Int(out, flags);
		write32Int(out, table.length);
		for (int a = 0; a < table.length; a++) {
			table[a].write(out);
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[ ");
		for (int a = 0; a < table.length; a++) {
			if (a != 0) {
				sb.append(", ");
			}
			sb.append(table[a].toString());
		}
		sb.append(" ]");
		String tableString = sb.toString();

		return "TimeToSampleAtom[ version=" + version + ", " + "flags=" + flags
				+ ", " + "table=" + tableString + "]";
	}

	/**
	 * Add a new sample time to this atom.
	 * 
	 * @param duration
	 *            the new duration, relative to the enclosing media's time
	 *            scale.
	 */
	public void addSampleTime(long duration) {
		addSampleTime(1, duration);
	}

	/**
	 * Add a new sample time to this atom.
	 * 
	 * @param duration
	 *            the new duration, relative to the enclosing media's time
	 *            scale.
	 */
	public void addSampleTime(long sampleCount, long duration) {
		if (table.length == 0
				|| table[table.length - 1].sampleDuration != duration) {
			TimeToSampleEntry[] newTable = new TimeToSampleEntry[table.length + 1];
			System.arraycopy(table, 0, newTable, 0, table.length);
			newTable[newTable.length - 1] = new TimeToSampleEntry(sampleCount,
					duration);
			table = newTable;
		} else {
			table[table.length - 1].sampleCount++;
		}
	}

	public long getDurationOfSample(long sampleIndex) {
		for (int a = 0; a < table.length; a++) {
			if (sampleIndex < table[a].sampleCount) {
				return table[a].sampleDuration;
			}
			sampleIndex = sampleIndex - table[a].sampleCount;
		}
		throw new RuntimeException("Could not find a sample at index "
				+ sampleIndex);
	}

	/**
	 * Return a 1-byte specification of the version of this time-to-sample atom.
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Return a 3-byte space for time-to-sample flags. Set this field to 0.
	 */
	public int getFlags() {
		return flags;
	}

	/**
	 * Return a table that defines the duration of each sample in the media.
	 */
	public TimeToSampleEntry[] getTable() {
		TimeToSampleEntry[] copy = new TimeToSampleEntry[table.length];
		System.arraycopy(table, 0, copy, 0, table.length);
		return copy;
	}
}