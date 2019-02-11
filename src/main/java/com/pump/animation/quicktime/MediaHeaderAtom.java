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

import com.pump.io.GuardedOutputStream;

public class MediaHeaderAtom extends LeafAtom {
	int version = 0;
	int flags = 0;
	Date creationTime;
	Date modificationTime;
	long timeScale;
	long duration;
	int language = 0;
	int quality = 0;

	public MediaHeaderAtom(long timeScale, long duration) {
		super(null);
		creationTime = new Date();
		modificationTime = creationTime;
		this.timeScale = timeScale;
		this.duration = duration;
	}

	public MediaHeaderAtom(Atom parent, InputStream in) throws IOException {
		super(parent);
		version = in.read();
		flags = read24Int(in);
		creationTime = readDate(in);
		modificationTime = readDate(in);
		timeScale = read32Int(in);
		duration = read32Int(in);
		language = read16Int(in);
		quality = read16Int(in);
	}

	@Override
	protected String getIdentifier() {
		return "mdhd";
	}

	@Override
	protected long getSize() {
		return 32;
	}

	@Override
	protected void writeContents(GuardedOutputStream out) throws IOException {
		out.write(version);
		write24Int(out, flags);
		writeDate(out, creationTime);
		writeDate(out, modificationTime);
		write32Int(out, timeScale);
		write32Int(out, duration);
		write16Int(out, language);
		write16Int(out, quality);
	}

	@Override
	public String toString() {
		return "MediaHeaderAtom[ version=" + version + ", " + "flags=" + flags
				+ ", " + "creationTime=" + creationTime + ", "
				+ "modificationTime=" + modificationTime + ", " + "timeScale="
				+ timeScale + ", " + "duration=" + duration + ", "
				+ "language=" + language + ", " + "quality=" + quality + " ]";
	}
}