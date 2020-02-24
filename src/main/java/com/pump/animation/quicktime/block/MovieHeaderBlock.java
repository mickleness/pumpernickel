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
import java.util.Date;

import javax.media.jai.PerspectiveTransform;

import com.pump.io.GuardedOutputStream;

/**
 * You use the movie header block to specify the characteristics of an entire
 * QuickTime movie. The data contained in this block defines characteristics of
 * the entire QuickTime movie, such as time scale and duration. It has an block
 * type value of 'mvhd'.
 */
public class MovieHeaderBlock extends LeafBlock {

	/** "mvhd" */
	public static final String BLOCK_TYPE = "mvhd";

	protected int version = 0;
	protected int flags = 0;
	protected Date creationTime;
	protected Date modificationTime;
	protected long timeScale;
	protected long duration;
	protected float preferredRate = 1;
	protected float preferredVolume = 1;
	protected PerspectiveTransform matrix;
	protected long previewTime = 0;
	protected long previewDuration = 0;
	protected long posterTime = 0;
	protected long selectionTime = 0;
	protected long selectionDuration = 0;
	protected long currentTime = 0;
	protected long nextTrackID = -1;

	public MovieHeaderBlock(Block parent, InputStream in) throws IOException {
		super(parent);

		version = in.read();
		flags = read24Int(in);
		creationTime = readDate(in);
		modificationTime = readDate(in);
		timeScale = read32Int(in);
		duration = read32Int(in);
		preferredRate = read16_16Float(in);
		preferredVolume = read8_8Float(in);
		skip(in, 10); // reserved
		matrix = readMatrix(in);
		previewTime = read32Int(in);
		previewDuration = read32Int(in);
		posterTime = read32Int(in);
		selectionTime = read32Int(in);
		selectionDuration = read32Int(in);
		currentTime = read32Int(in);
		nextTrackID = read32Int(in);
	}

	public MovieHeaderBlock(long timeScale, long duration) {
		super(null);
		creationTime = new Date();
		modificationTime = creationTime;
		this.duration = duration;
		this.timeScale = timeScale;
		matrix = new PerspectiveTransform();
	}

	public void setNextTrackID(int id) {
		nextTrackID = id;
	}

	/**
	 * Return a 32-bit integer that indicates a value to use for the track ID
	 * number of the next track added to this movie. Note that 0 is not a valid
	 * track ID value.
	 */
	public long getNextTrackID() {
		return nextTrackID;
	}

	@Override
	public String getBlockType() {
		return BLOCK_TYPE;
	}

	@Override
	protected long getSize() {
		return 108;
	}

	@Override
	protected void writeContents(GuardedOutputStream out) throws IOException {
		out.write(version);
		write24Int(out, flags);
		writeDate(out, creationTime);
		writeDate(out, modificationTime);
		write32Int(out, timeScale);
		write32Int(out, duration);
		write16_16Float(out, preferredRate);
		write8_8Float(out, preferredVolume);
		write32Int(out, 0);
		write32Int(out, 0);
		write16Int(out, 0);
		writeMatrix(out, matrix);
		write32Int(out, previewTime);
		write32Int(out, previewDuration);
		write32Int(out, posterTime);
		write32Int(out, selectionTime);
		write32Int(out, selectionDuration);
		write32Int(out, currentTime);
		write32Int(out, getRoot().getHighestTrackID() + 1);
	}

	@Override
	public String toString() {
		return "MovieHeaderBlock[ " + "version = " + version + ", "
				+ "flags = " + flags + ", " + "creationTime = " + creationTime
				+ ", " + "modificationTime = " + modificationTime + ", "
				+ "timeScale = " + timeScale + ", " + "duration = " + duration
				+ ", " + "preferredRate = " + preferredRate + ", "
				+ "preferredVolume = " + preferredVolume + ", " + "matrix = "
				+ matrix + ", " + "previewTime = " + previewTime + ", "
				+ "previewDuration = " + previewDuration + ", "
				+ "posterTime = " + posterTime + ", " + "selectionTime = "
				+ selectionTime + ", " + "selectionDuration = "
				+ selectionDuration + ", " + "currentTime = " + currentTime
				+ ", " + "nextTrackID = " + nextTrackID + "]";
	}

	/**
	 * Return a 1-byte specification of the version of this movie header block.
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Return three bytes of space for future movie header flags.
	 */
	public int getFlags() {
		return flags;
	}

	/**
	 * Return when the movie block was created.
	 */
	public Date getCreationTime() {
		return creationTime;
	}

	/**
	 * Return when the movie block was changed.
	 */
	public Date getModificationTime() {
		return modificationTime;
	}

	/**
	 * Return time value that indicates the time scale for this movie—that is,
	 * the number of time units that pass per second in its time coordinate
	 * system. A time coordinate system that measures time in sixtieths of a
	 * second, for example, has a time scale of 60.
	 */
	public long getTimeScale() {
		return timeScale;
	}

	/**
	 * Return a time value that indicates the duration of the movie in time
	 * scale units. Note that this property is derived from the movie’s tracks.
	 * The value of this field corresponds to the duration of the longest track
	 * in the movie.
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * Return a 32-bit fixed-point number that specifies the rate at which to
	 * play this movie. A value of 1.0 indicates normal rate.
	 */
	public float getPreferredRate() {
		return preferredRate;
	}

	/**
	 * Return a 16-bit fixed-point number that specifies how loud to play this
	 * movie’s sound. A value of 1.0 indicates full volume.
	 */
	public float getPreferredVolume() {
		return preferredVolume;
	}

	/**
	 * Return the matrix structure associated with this movie.
	 */
	public PerspectiveTransform getMatrix() {
		return new PerspectiveTransform(matrix.getMatrix(new double[3][3]));
	}

	/**
	 * Return time value in the movie at which the preview begins.
	 */
	public long getPreviewTime() {
		return previewTime;
	}

	/**
	 * Return the duration of the movie preview in movie time scale units.
	 */
	public long getPreviewDuration() {
		return previewDuration;
	}

	/**
	 * Return the time value of the time of the movie poster.
	 */
	public long getPosterTime() {
		return posterTime;
	}

	/**
	 * Return the time value for the start time of the current selection.
	 */
	public long getSelectionTime() {
		return selectionTime;
	}

	/**
	 * Return the duration of the current selection in movie time scale units.
	 */
	public long getSelectionDuration() {
		return selectionDuration;
	}

	/**
	 * Return the time value for current time position within the movie.
	 */
	public long getCurrentTime() {
		return currentTime;
	}
}