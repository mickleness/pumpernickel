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
package com.pump.animation.quicktime;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.pump.io.GuardedOutputStream;

public class TimeToSampleAtom extends LeafAtom {
	int version = 0;
	int flags = 0;
	TimeToSampleEntry[] table = new TimeToSampleEntry[0];

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
		return "stts";
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

}