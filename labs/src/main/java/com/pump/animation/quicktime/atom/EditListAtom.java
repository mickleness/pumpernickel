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
import java.io.OutputStream;

import com.pump.io.GuardedOutputStream;

/**
 * You use the edit list atom to map from a time in a movie to a time in a
 * media, and ultimately to media data. This information is in the form of
 * entries in an edit list table. Edit list atoms have an atom type value of
 * 'elst'.
 */
public class EditListAtom extends LeafAtom {

	public static class EditListTableEntry {
		/**
		 * A 32-bit integer that specifies the duration of this edit segment in
		 * units of the movie's time scale.
		 */
		long trackDuration;

		/**
		 * A 32-bit integer containing the starting time within the media of
		 * this edit segment (in media timescale units). If this field is set to
		 * -1, it is an empty edit. The last edit in a track should never be an
		 * empty edit. Any difference between the movie's duration and the
		 * track's duration is expressed as an implicit empty edit.
		 */
		long mediaTime;

		/**
		 * A 32-bit fixed-point number that specifies the relative rate at which
		 * to play the media corresponding to this edit segment. This rate value
		 * cannot be 0 or negative.
		 */
		float mediaRate;

		EditListTableEntry(InputStream in) throws IOException {
			trackDuration = Atom.read32Int(in);
			mediaTime = Atom.read32Int(in);
			mediaRate = Atom.read16_16Float(in);
		}

		EditListTableEntry(long trackDuration, long mediaTime,
				float mediaRate) {
			this.trackDuration = trackDuration;
			this.mediaTime = mediaTime;
			this.mediaRate = mediaRate;
		}

		public long getTrackDuration() {
			return trackDuration;
		}

		public long getMediaTime() {
			return mediaTime;
		}

		public float getMediaRate() {
			return mediaRate;
		}

		void write(OutputStream out) throws IOException {
			Atom.write32Int(out, trackDuration);
			Atom.write32Int(out, mediaTime);
			Atom.write16_16Float(out, mediaRate);
		}

		@Override
		public String toString() {
			return "EditListTableEntry[ trackDuration=" + trackDuration
					+ ", mediaTime=" + mediaTime + ", mediaRate=" + mediaRate
					+ "]";
		}
	}

	public static final String ATOM_TYPE = "elst";

	/** A 1-byte specification of the version of this edit list atom. */
	protected int version = 0;

	/** Three bytes of space for flags. Set this field to 0. */
	protected int flags = 0;

	protected EditListTableEntry[] table = new EditListTableEntry[] {};

	protected EditListAtom(Atom parent, InputStream in) throws IOException {
		super(parent);
		version = Atom.read8Int(in);
		flags = Atom.read24Int(in);

		/**
		 * A 32-bit integer that specifies the number of entries in the edit
		 * list atom that follows.
		 */
		int numberOfEntries = (int) Atom.read32Int(in);

		table = new EditListTableEntry[numberOfEntries];
		for (int a = 0; a < numberOfEntries; a++) {
			table[a] = new EditListTableEntry(in);
		}
	}

	public EditListAtom() {
		super(null);
	}

	public void addEditListTableEntry(long trackDuration, long mediaTime,
			float mediaRate) {
		EditListTableEntry[] newTable = new EditListTableEntry[table.length
				+ 1];
		System.arraycopy(table, 0, newTable, 0, table.length);
		newTable[newTable.length - 1] = new EditListTableEntry(trackDuration,
				mediaTime, mediaRate);
		table = newTable;

	}

	@Override
	protected long getSize() {
		return 16 + 12 * table.length;
	}

	@Override
	public String getIdentifier() {
		return ATOM_TYPE;
	}

	@Override
	protected void writeContents(GuardedOutputStream out) throws IOException {
		Atom.write8Int(out, version);
		Atom.write24Int(out, flags);
		Atom.write32Int(out, table.length);
		for (int a = 0; a < table.length; a++) {
			table[a].write(out);
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("EditListAtom[ version=" + version
				+ ", flags=" + flags + ", data=[");
		for (int a = 0; a < table.length; a++) {
			sb.append(table[a] + " ");
		}
		sb.append("]]");
		return sb.toString();
	}

	/**
	 * Return a 1-byte specification of the version of this edit list atom.
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Return three bytes of space for flags. Set this field to 0.
	 */
	public int getFlags() {
		return flags;
	}

	/**
	 * Return the size of the edit list table.
	 */
	public int getEditListTableEntryCount() {
		return table.length;
	}

	/**
	 * Return an element of the edit list table.
	 */
	public EditListTableEntry getEditListTableEntry(int index) {
		return table[index];
	}
}