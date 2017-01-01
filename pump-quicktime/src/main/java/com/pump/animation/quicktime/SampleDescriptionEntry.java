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
import java.io.OutputStream;

public abstract class SampleDescriptionEntry {
	/** If this entry is read from an <code>InputStream</code>, then this
	 * is the size that this entry should be.
	 * <P>Subclasses may consult this value when reading from a stream to
	 * determine how much more data to read in this entry.
	 * <P>Otherwise this field is unused.
	 */
	long inputSize;
	String type;
	int dataReference;
	public SampleDescriptionEntry(String type,int dataReference) {
		this.type = type;
		this.dataReference = dataReference;
	}
	
	public SampleDescriptionEntry(InputStream in) throws IOException {
		inputSize = Atom.read32Int(in);
		type = Atom.read32String(in);
		Atom.skip(in,6); //reserved
		dataReference = Atom.read16Int(in);
	}
	
	protected abstract long getSize();
	
	protected abstract void write(OutputStream out) throws IOException;
}