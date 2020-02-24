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
package com.pump.animation.quicktime.block;

import java.io.IOException;
import java.io.InputStream;

import com.pump.io.GuardedOutputStream;

/**
 * The sound media information header block stores the sound media’s control
 * information, such as balance.
 */
public class SoundMediaInformationHeaderBlock extends LeafBlock {

	/** "smhd" */
	public static final String BLOCK_TYPE = "smhd";

	/**
	 * A 1-byte specification of the version of this sound media information
	 * header block.
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

	public SoundMediaInformationHeaderBlock() {
		super(null);
	}

	protected SoundMediaInformationHeaderBlock(Block parent, InputStream in)
			throws IOException {
		super(parent);
		version = in.read();
		flags = Block.read24Int(in);
		balance = Block.read16Int(in);
		Block.read16Int(in); // reserved unused space
	}

	@Override
	public String getBlockType() {
		return BLOCK_TYPE;
	}

	@Override
	protected long getSize() {
		return 16;
	}

	@Override
	public String toString() {
		return "SoundMediaInformationHeaderBlock[ version = " + version
				+ ", flags = " + flags + ", balance = " + balance + "]";
	}

	@Override
	protected void writeContents(GuardedOutputStream out) throws IOException {
		out.write(version);
		Block.write24Int(out, flags);
		Block.write16Int(out, balance);
		Block.write16Int(out, 0);
	}

	/**
	 * Return a 1-byte specification of the version of this sound media
	 * information header block.
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