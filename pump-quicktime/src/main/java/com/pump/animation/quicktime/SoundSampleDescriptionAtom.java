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

class SoundSampleDescriptionAtom extends SampleDescriptionAtom {

	public SoundSampleDescriptionAtom() {
		super();
	}
	
	public SoundSampleDescriptionAtom(Atom parent, InputStream in)
			throws IOException {
		super(parent, in);
	}
	
	@Override
	protected SampleDescriptionEntry readEntry(InputStream in) throws IOException {
		return new SoundSampleDescriptionEntry(in);
	}
}