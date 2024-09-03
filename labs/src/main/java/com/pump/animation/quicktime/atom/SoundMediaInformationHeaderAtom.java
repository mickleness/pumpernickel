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
 * The sound media information header atom stores the sound media’s control
 * information, such as balance.
 */
public class SoundMediaInformationHeaderAtom extends LeafAtom {

	/** "smhd" */
	public static final String ATOM_TYPE = "smhd";

	/**
	 * A 1-byte specification of the version of this sound media information
	 * header atom.
	 */
	protected int version = 0;

	/** A 3-byte space for sound media information flags. Set this field to 0. */
	protected int flags = 0;

	/**
	 * A 16-bit integer that specifies the sound balance of this sound media.
	 * Sound balance is the setting that controls the mix of sound between the
	 * two speakers of a computer. This field is normally set to 0.
	 */
	protected int balance = 0;

	public SoundMediaInformationHeaderAtom() {
		super(null);
	}

	protected SoundMediaInformationHeaderAtom(Atom parent, InputStream in)
			throws IOException {
		super(parent);
		version = in.read();
		flags = Atom.read24Int(in);
		balance = Atom.read16Int(in);
		Atom.read16Int(in); // reserved unused space
	}

	@Override
	public String getIdentifier() {
		return ATOM_TYPE;
	}

	@Override
	protected long getSize() {
		return 16;
	}

	@Override
	public String toString() {
		return "SoundMediaInformationHeaderAtom[ version = " + version
				+ ", flags = " + flags + ", balance = " + balance + "]";
	}

	@Override
	protected void writeContents(GuardedOutputStream out) throws IOException {
		out.write(version);
		Atom.write24Int(out, flags);
		Atom.write16Int(out, balance);
		Atom.write16Int(out, 0);
	}

	/**
	 * Return a 1-byte specification of the version of this sound media
	 * information header atom.
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Return a 3-byte space for sound media information flags. Set this field
	 * to 0.
	 */
	public int getFlags() {
		return flags;
	}

	/**
	 * Return a 16-bit integer that specifies the sound balance of this sound
	 * media. Sound balance is the setting that controls the mix of sound
	 * between the two speakers of a computer. This field is normally set to 0.
	 */
	public int getBalance() {
		return balance;
	}
}