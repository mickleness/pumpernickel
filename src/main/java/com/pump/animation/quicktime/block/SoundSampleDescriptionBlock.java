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

/**
 * This is a SampleDescriptionBlock for sounds.
 */
public class SoundSampleDescriptionBlock extends SampleDescriptionBlock {

	public SoundSampleDescriptionBlock() {
		super();
	}

	public SoundSampleDescriptionBlock(Block parent, InputStream in)
			throws IOException {
		super(parent, in);
	}

	@Override
	protected SampleDescriptionEntry readEntry(InputStream in)
			throws IOException {
		return new SoundSampleDescriptionEntry(in);
	}
}