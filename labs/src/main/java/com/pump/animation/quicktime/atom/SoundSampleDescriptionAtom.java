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
 * This is a SampleDescriptionAtom for sounds.
 */
public class SoundSampleDescriptionAtom extends SampleDescriptionAtom {

	public SoundSampleDescriptionAtom() {
		super();
	}

	public SoundSampleDescriptionAtom(Atom parent, InputStream in)
			throws IOException {
		super(parent, in);
	}

	@Override
	protected SoundSampleDescriptionEntry readEntry(InputStream in)
			throws IOException {
		MeasuredInputStream measuredIn = new MeasuredInputStream(in);
		SoundSampleDescriptionEntry returnValue = new SoundSampleDescriptionEntry(
				measuredIn);
		long remainingBytes = returnValue.inputSize - measuredIn.getReadBytes();
		if (remainingBytes > 0)
			Atom.skip(in, remainingBytes);
		return returnValue;
	}

	public SoundSampleDescriptionEntry[] getSampleDescriptionEntries() {
		SoundSampleDescriptionEntry[] copy = new SoundSampleDescriptionEntry[entries.length];
		for (int a = 0; a < entries.length; a++) {
			copy[a] = (SoundSampleDescriptionEntry) entries[a];
		}
		return copy;
	}
}