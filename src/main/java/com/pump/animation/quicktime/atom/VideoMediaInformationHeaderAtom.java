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
package com.pump.animation.quicktime.atom;

import java.io.IOException;
import java.io.InputStream;

import com.pump.io.GuardedOutputStream;

/**
 * Video media information header atoms define specific color and graphics mode
 * information.
 */
public class VideoMediaInformationHeaderAtom extends LeafAtom {

	/** "vmhd" */
	public static final String ATOM_TYPE = "vmhd";

	protected int version = 0;

	/**
	 * This should always be 1, unless you're dealing with a QT v1.0 file.
	 */
	protected int flags = 1;

	/** The most standard graphics mode is DITHER_COPY. */
	protected int graphicsMode = GraphicsModeConstants.DITHER_COPY;

	protected long opColor = 0x800080008000L;

	public VideoMediaInformationHeaderAtom() {
		super(null);
	}

	public VideoMediaInformationHeaderAtom(Atom parent, InputStream in)
			throws IOException {
		super(parent);
		version = in.read();
		flags = read24Int(in);
		graphicsMode = read16Int(in);
		opColor = read48Int(in);
	}

	@Override
	public String getIdentifier() {
		return ATOM_TYPE;
	}

	@Override
	protected long getSize() {
		return 20;
	}

	@Override
	protected void writeContents(GuardedOutputStream out) throws IOException {
		out.write(version);
		write24Int(out, flags);
		write16Int(out, graphicsMode);
		write48Int(out, opColor);
	}

	@Override
	public String toString() {
		return "VideoMediaInformationHeaderAtom[ version=" + version + ", "
				+ "flags=" + flags + ", " + "graphicsMode="
				+ getFieldName(GraphicsModeConstants.class, graphicsMode)
				+ ", " + "opColor=0x" + Long.toString(opColor, 16) + "]";
	}

	/**
	 * Return a 1-byte specification of the version of this video media
	 * information header atom.
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Return a 3-byte int for video media information flags. There is one
	 * defined flag.
	 * <p>
	 * No lean ahead: This is a compatibility flag that allows QuickTime to
	 * distinguish between movies created with QuickTime 1.0 and newer movies.
	 * You should always set this flag to 1, unless you are creating a movie
	 * intended for playback using version 1.0 of QuickTime. This flag’s value
	 * is 0x0001.
	 */
	public int getFlags() {
		return flags;
	}

	/**
	 * Return 16-bit integer that specifies the transfer mode. The transfer mode
	 * specifies which Boolean operation QuickDraw should perform when drawing
	 * or transferring an image from one location to another.
	 * <p>
	 * See {@link GraphicsModeConstants}.
	 */
	public int getGraphicsMode() {
		return graphicsMode;
	}

	/**
	 * Return three 16-bit values that specify the red, green, and blue colors
	 * for the transfer mode operation indicated in the graphics mode field
	 */
	public long getOpColor() {
		return opColor;
	}
}