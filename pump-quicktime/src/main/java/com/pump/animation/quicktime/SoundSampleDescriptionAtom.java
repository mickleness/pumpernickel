/*
 * @(#)SoundSampleDescriptionAtom.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2012 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
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
