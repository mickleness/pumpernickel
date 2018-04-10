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

/**
 * This is not a public class because I expect to make some significant changes
 * to this project in the next year.
 * <P>
 * Use at your own risk. This class (and its package) may change in future
 * releases.
 * <P>
 * Not that I'm promising there will be future releases. There may not be. :)
 */
class VideoMediaInformationHeaderAtom extends LeafAtom {
	int version = 0;

	/**
	 * This should always be 1, unless you're dealing with a QT v1.0 file.
	 */
	int flags = 1;

	/** The most standard graphics mode is DITHER_COPY. */
	int graphicsMode = GraphicsModeConstants.DITHER_COPY;
	long opColor = 0x800080008000L;

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
	protected String getIdentifier() {
		return "vmhd";
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
}