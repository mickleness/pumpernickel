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

import com.pump.io.MeasuredInputStream;

/**
 * This is a SampleDescriptionAtom for video data.
 */
public class VideoSampleDescriptionAtom extends SampleDescriptionAtom {

	public VideoSampleDescriptionAtom() {
		super();
	}

	public VideoSampleDescriptionAtom(Atom parent, InputStream in)
			throws IOException {
		super(parent, in);
	}

	@Override
	protected VideoSampleDescriptionEntry readEntry(InputStream in)
			throws IOException {
		MeasuredInputStream measuredIn = new MeasuredInputStream(in);
		VideoSampleDescriptionEntry returnValue = new VideoSampleDescriptionEntry(
				measuredIn);
		long remainingBytes = returnValue.inputSize - measuredIn.getReadBytes();
		if (remainingBytes > 0) {
			byte[] b = new byte[(int)remainingBytes];
			read(in, b);
			returnValue.extraBytes = b;
		}
		return returnValue;
	}

	public VideoSampleDescriptionEntry[] getSampleDescriptionEntries() {
		VideoSampleDescriptionEntry[] copy = new VideoSampleDescriptionEntry[entries.length];
		for (int a = 0; a < entries.length; a++) {
			copy[a] = (VideoSampleDescriptionEntry) entries[a];
		}
		return copy;
	}
}