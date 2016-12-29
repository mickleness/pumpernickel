/*
 * @(#)UnknownSampleDescriptionEntry.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2011 by Jeremy Wood.
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
import java.io.OutputStream;


public class UnknownSampleDescriptionEntry extends SampleDescriptionEntry {

	byte[] data = new byte[0];
	
	public UnknownSampleDescriptionEntry(InputStream in) throws IOException {
		super(in);
		if(inputSize>16) {
			data = new byte[(int)(inputSize-16)];
			Atom.read(in,data);
		} else {
			data = new byte[0];
		}
	}
	
	public UnknownSampleDescriptionEntry(String type,int dataReference) {
		super(type, dataReference);
	}
	
	@Override
	protected long getSize() {
		return 16+data.length;
	}
	
	/** If it is possible to convert this to a more specific SampleDescriptionEntry:
	 * then this method will do that.  Otherwise this returns this UnknownSampleDescription.
	 * 
	 */
	public SampleDescriptionEntry convert() {
		if(data.length==20 && type.equals("sowt")) {
			return new SoundSampleDescriptionEntry0(type, dataReference, data);
		}
		return this;
	}
	
	@Override
	protected void write(OutputStream out) throws IOException {
		Atom.write32Int(out, getSize());
		Atom.write32String(out, type);
		Atom.write48Int(out, 0);
		Atom.write16Int(out, dataReference);
		out.write(data);
	}
	
	@Override
	public String toString() {
		if(data.length==0) {
			return "UnknownSampleDescriptionEntry[ type=\""+type+"\", "+
			"dataReference="+dataReference+" ];";
		}
		
		String extra = "";
		if(data.length<=8) {
			extra = " (";
			for(int a = 0; a<data.length; a++) {
				extra = extra+(data[a] & 0xff)+" ";
			}
			extra = extra+") ";
		}

		return "UnknownSampleDescriptionEntry[ type=\""+type+"\", "+
		"dataReference="+dataReference+", "+
		"data=\""+(new String(data))+"\" "+extra+"]";
	}
}
