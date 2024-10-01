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
import java.util.Date;

import com.pump.io.GuardedOutputStream;

/**
 * The media header atom specifies the characteristics of a media, including
 * time scale and duration. The media header atom has an atom type of 'mdhd'.
 */
public class MediaHeaderAtom extends LeafAtom {

	/** "mdhd" */
	public static final String ATOM_TYPE = "mdhd";

	protected int version = 0;
	protected int flags = 0;
	protected Date creationTime;
	protected Date modificationTime;
	protected long timeScale;
	protected long duration;
	protected int language = 0;
	protected int quality = 0;

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
	public String getIdentifier() {
		return ATOM_TYPE;
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

	/**
	 * Return one byte that specifies the version of this header atom.
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Return three bytes of space for media header flags. Set this field to 0.
	 */
	public int getFlags() {
		return flags;
	}

	/**
	 * Return when the media atom was created.
	 */
	public Date getCreationTime() {
		return creationTime;
	}

	/**
	 * Return when the media atom was changed.
	 */
	public Date getModificationTime() {
		return modificationTime;
	}

	/**
	 * Return a time value that indicates the time scale for this media—that is,
	 * the number of time units that pass per second in its time coordinate
	 * system.
	 */
	public long getTimeScale() {
		return timeScale;
	}

	/**
	 * Return the duration of this media in units of its time scale.
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * Return 16-bit integer that specifies the language code for this media.
	 * See <a href=
	 * "https://developer.apple.com/library/archive/documentation/QuickTime/QTFF/QTFFChap4/qtff4.html#//apple_ref/doc/uid/TP40000939-CH206-27005"
	 * >Language Code</a> Values for valid language codes. Also see <a href=
	 * "https://developer.apple.com/library/archive/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html#//apple_ref/doc/uid/TP40000939-CH204-SW16"
	 * >Extended Language Tag Atom</a> for the preferred code to use here if an
	 * extended language tag is also included in the media atom.
	 */
	public int getLanguage() {
		return language;
	}

	/**
	 * Return a 16-bit integer that specifies the media’s playback quality—that
	 * is, its suitability for playback in a given environment.
	 */
	public int getQuality() {
		return quality;
	}
}