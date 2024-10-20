/**
 * This software is released as part of the Pumpernickel project.
 * <p>
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * <p>
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.animation.quicktime.atom;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.pump.io.GuardedOutputStream;

/**
 * The sample description atom stores information that allows you to decode
 * samples in the media. The data stored in the sample description varies,
 * depending on the media type. For example, in the case of video media, the
 * sample descriptions are image description structures. See subclasses
 * {@link SoundSampleDescriptionAtom} and {@link VideoSampleDescriptionAtom}
 * <p>
 * The sample description atom has an atom type of 'stsd'. The sample
 * description atom contains a table of sample descriptions. A media may have
 * one or more sample descriptions, depending upon the number of different
 * encoding schemes used in the media and on the number of files used to store
 * the data. The sample-to-chunk atom identifies the sample description for each
 * sample in the media by specifying the index into this table for the
 * appropriate description (see {@link SampleToChunkAtom}).
 */
public class SampleDescriptionAtom extends LeafAtom {

	/** "stsd" */
	public static final String ATOM_TYPE = "stsd";

	protected int version = 0;
	protected int flags = 0;
	protected SampleDescriptionEntry[] entries = new SampleDescriptionEntry[0];

	public SampleDescriptionAtom(int version, int flags) {
		super(null);
		this.version = version;
		this.flags = flags;
	}

	public SampleDescriptionAtom() {
		super(null);
	}

	public SampleDescriptionAtom(Atom parent, InputStream in)
			throws IOException {
		super(parent);
		version = in.read();
		flags = read24Int(in);
		int tableSize = (int) read32Int(in);
		entries = new SampleDescriptionEntry[tableSize];
		for (int a = 0; a < entries.length; a++) {
			entries[a] = readEntry(in);
		}
	}

	public void addEntry(SampleDescriptionEntry e) {
		SampleDescriptionEntry[] newArray = new SampleDescriptionEntry[entries.length + 1];
		System.arraycopy(entries, 0, newArray, 0, entries.length);
		newArray[newArray.length - 1] = e;
		entries = newArray;
	}

	protected SampleDescriptionEntry readEntry(InputStream in)
			throws IOException {
		UnknownSampleDescriptionEntry entry = new UnknownSampleDescriptionEntry(
				in);
		return entry.convert();
	}

	@Override
	public String getIdentifier() {
		return ATOM_TYPE;
	}

	@Override
	protected long getSize() {
		long sum = 16;
		for (SampleDescriptionEntry entry : entries) {
			sum += entry.getSize();
		}
		return sum;
	}

	@Override
	protected void writeContents(GuardedOutputStream out) throws IOException {
		out.write(version);
		write24Int(out, flags);
		write32Int(out, entries.length);
		for (SampleDescriptionEntry entry : entries) {
			entry.write(out);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		for (int a = 0; a < entries.length; a++) {
			if (a != 0) {
				sb.append(", ");
			}
			sb.append(entries[a].toString());
		}
		sb.append(" ]");
		String entriesString = sb.toString();

		return getClassName() + "[ version=" + version + ", " + "flags="
				+ flags + ", " + "entries=" + entriesString + "]";
	}

	protected String getClassName() {
		String s = this.getClass().getName();
		if (s.indexOf('.') != -1)
			s = s.substring(s.lastIndexOf('.') + 1);
		return s;
	}

	/**
	 * Return a 1-byte specification of the version of this sample description
	 * atom.
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Return a 3-byte space for sample description flags. Set this field to 0.
	 */
	public int getFlags() {
		return flags;
	}

	/**
	 * Return an array of sample descriptions.
	 */
	public SampleDescriptionEntry[] getSampleDescriptionEntries() {
		return Arrays.copyOf(entries, entries.length);
	}
}