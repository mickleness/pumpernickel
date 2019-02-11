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
import java.util.Date;

import javax.media.jai.PerspectiveTransform;

import com.pump.io.GuardedOutputStream;

public class TrackHeaderAtom extends LeafAtom {
	public static final int FLAG_ENABLED = 0x001;
	public static final int FLAG_IN_MOVIE = 0x002;
	public static final int FLAG_IN_PREVIEW = 0x004;
	public static final int FLAG_IN_POSTER = 0x008;
	int version = 0;
	int flags = FLAG_ENABLED + FLAG_IN_MOVIE + FLAG_IN_PREVIEW + FLAG_IN_POSTER;
	Date creationTime;
	Date modificationTime;
	long trackID;
	long duration;
	int layer = 0;
	int alternateGroup = 0;
	float volume = 1;
	PerspectiveTransform matrix;
	float width;
	float height;

	public TrackHeaderAtom(long trackID, long duration, float width,
			float height) {
		super(null);
		this.trackID = trackID;
		this.duration = duration;
		creationTime = new Date();
		modificationTime = creationTime;
		matrix = new PerspectiveTransform();
		this.width = width;
		this.height = height;
	}

	public TrackHeaderAtom(Atom parent, InputStream in) throws IOException {
		super(parent);
		version = in.read();
		flags = read24Int(in);
		creationTime = readDate(in);
		modificationTime = readDate(in);
		trackID = read32Int(in);
		skip(in, 4); // reserved
		duration = read32Int(in);
		skip(in, 8); // more reserved
		layer = read16Int(in);
		alternateGroup = read16Int(in);
		volume = read8_8Float(in);
		skip(in, 2); // even more reserved
		matrix = readMatrix(in);
		width = read16_16Float(in);
		height = read16_16Float(in);
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public int getFlags() {
		return flags;
	}

	@Override
	protected String getIdentifier() {
		return "tkhd";
	}

	@Override
	protected long getSize() {
		return 92;
	}

	@Override
	protected void writeContents(GuardedOutputStream out) throws IOException {
		out.write(version);
		write24Int(out, flags);
		writeDate(out, creationTime);
		writeDate(out, modificationTime);
		write32Int(out, trackID);
		write32Int(out, 0);
		write32Int(out, duration);
		write32Int(out, 0);
		write32Int(out, 0);
		write16Int(out, layer);
		write16Int(out, alternateGroup);
		write8_8Float(out, volume);
		write16Int(out, 0);
		writeMatrix(out, matrix);
		write16_16Float(out, width);
		write16_16Float(out, height);
	}

	@Override
	public String toString() {
		return "TrackHeaderAtom[ version=" + version + ", " + "flags=" + flags
				+ ", " + "creationTime=" + creationTime + ", "
				+ "modificationTime=" + modificationTime + ", " + "trackID="
				+ trackID + ", " + "duration=" + duration + ", " + "layer="
				+ layer + ", " + "alternateGroup=" + alternateGroup + ", "
				+ "volume=" + volume + ", " + "matrix=" + matrix + ", "
				+ "width=" + width + ", " + "height=" + height + "]";
	}
}