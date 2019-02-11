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

import com.pump.io.GuardedOutputStream;

public class SoundMediaInformationHeaderAtom extends LeafAtom {

	/**
	 * A 1-byte specification of the version of this sound media information
	 * header atom.
	 */
	int version = 0;
	/** A 3-byte space for sound media information flags. Set this field to 0. */
	int flags = 0;
	/**
	 * A 16-bit integer that specifies the sound balance of this sound media.
	 * Sound balance is the setting that controls the mix of sound between the
	 * two speakers of a computer. This field is normally set to 0.
	 */
	int balance = 0;

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
	protected String getIdentifier() {
		return "smhd";
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

}