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
import java.util.Date;
import com.pump.geom.PerspectiveTransform;

import com.pump.io.GuardedOutputStream;

/**
 * The track header atom specifies the characteristics of a single track within
 * a movie. A track header atom contains a size field that specifies the number
 * of bytes and a type field that indicates the format of the data (defined by
 * the atom type 'tkhd').
 */
public class TrackHeaderAtom extends LeafAtom {
	public static final int FLAG_ENABLED = 0x001;
	public static final int FLAG_IN_MOVIE = 0x002;
	public static final int FLAG_IN_PREVIEW = 0x004;
	public static final int FLAG_IN_POSTER = 0x008;

	/** "tkhd" */
	public static final String ATOM_TYPE = "tkhd";

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

	/**
	 * Return three bytes that are reserved for the track header flags. These
	 * flags indicate how the track is used in the movie. The following flags
	 * are valid (all flags are enabled when set to 1).
	 * <p>
	 * Track enabled: Indicates that the track is enabled. Flag value is 0x0001.
	 * Track in movie
	 * <p>
	 * Indicates that the track is used in the movie. Flag value is 0x0002.
	 * <p>
	 * Track in preview: Indicates that the track is used in the movie’s
	 * preview. Flag value is 0x0004.
	 * <p>
	 * Track in poster: Indicates that the track is used in the movie’s poster.
	 * Flag value is 0x0008.
	 */
	public int getFlags() {
		return flags;
	}

	@Override
	public String getIdentifier() {
		return ATOM_TYPE;
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

	/**
	 * Return a 1-byte specification of the version of this track header.
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Return the calendar date and time when the track header was created.
	 */
	public Date getCreationTime() {
		return creationTime;
	}

	/**
	 * Return the calendar date and time when the track header was changed.
	 */
	public Date getModificationTime() {
		return modificationTime;
	}

	/**
	 * Return 32-bit integer that uniquely identifies the track. The value 0
	 * cannot be used.
	 */
	public long getTrackID() {
		return trackID;
	}

	/**
	 * Return a time value that indicates the duration of this track (in the
	 * movie’s time coordinate system). Note that this property is derived from
	 * the track’s edits. The value of this field is equal to the sum of the
	 * durations of all of the track’s edits. If there is no edit list, then the
	 * duration is the sum of the sample durations, converted into the movie
	 * timescale.
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * Return a 16-bit integer that indicates this track’s spatial priority in
	 * its movie. The QuickTime Movie Toolbox uses this value to determine how
	 * tracks overlay one another. Tracks with lower layer values are displayed
	 * in front of tracks with higher layer values.
	 */
	public int getLayer() {
		return layer;
	}

	/**
	 * Return a 16-bit integer that identifies a collection of movie tracks that
	 * contain alternate data for one another. This same identifier appears in
	 * each 'tkhd' atom of the other tracks in the group. QuickTime chooses one
	 * track from the group to be used when the movie is played. The choice may
	 * be based on such considerations as playback quality, language, or the
	 * capabilities of the computer.
	 * <p>
	 * A value of zero indicates that the track is not in an alternate track
	 * group.
	 * <p>
	 * The most common reason for having alternate tracks is to provide versions
	 * of the same track in different languages.
	 */
	public int getAlternateGroup() {
		return alternateGroup;
	}

	/**
	 * Return a 16-bit fixed-point value that indicates how loudly this track’s
	 * sound is to be played. A value of 1.0 indicates normal volume.
	 */
	public float getVolume() {
		return volume;
	}

	/**
	 * Return the matrix structure associated with this track.
	 */
	public PerspectiveTransform getMatrix() {
		return new PerspectiveTransform(matrix.getMatrix(new double[3][3]));
	}

	/**
	 * Return a 32-bit fixed-point number that specifies the width of this track
	 * in pixels.
	 */
	public float getWidth() {
		return width;
	}

	/**
	 * Return a 32-bit fixed-point number that indicates the height of this
	 * track in pixels.
	 */
	public float getHeight() {
		return height;
	}

	public void setVolume(float newVolume) {
		volume = newVolume;
	}

	public void setWidth(float newWidth) {
		width = newWidth;
	}

	public void setHeight(float newHeight) {
		height = newHeight;
	}
}