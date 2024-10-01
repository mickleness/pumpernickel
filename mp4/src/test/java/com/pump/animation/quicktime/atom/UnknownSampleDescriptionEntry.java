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
import java.io.OutputStream;

/**
 * This is a SampleDescriptionEntry that is not a VideoSampleDescriptionEntry or
 * a SoundSampleDescriptionEntry.
 */
public class UnknownSampleDescriptionEntry extends SampleDescriptionEntry {

	protected byte[] data = new byte[0];

	public UnknownSampleDescriptionEntry(InputStream in) throws IOException {
		super(in);
		if (inputSize > 16) {
			data = new byte[(int) (inputSize - 16)];
			Atom.read(in, data);
		} else {
			data = new byte[0];
		}
	}

	public UnknownSampleDescriptionEntry(String type, int dataReference) {
		super(type, dataReference);
	}

	@Override
	protected long getSize() {
		return 16 + data.length;
	}

	/**
	 * If it is possible to convert this to a more specific
	 * SampleDescriptionEntry: then this method will do that. Otherwise this
	 * returns this UnknownSampleDescription.
	 * 
	 */
	public SampleDescriptionEntry convert() {
		if (data.length == 20 && type.equals("sowt")) {
			return new SoundSampleDescriptionEntry(type, dataReference, data);
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
		if (data.length == 0) {
			return "UnknownSampleDescriptionEntry[ type=\"" + type + "\", "
					+ "dataReference=" + dataReference + " ];";
		}

		String extra = "";
		if (data.length <= 8) {
			extra = " (";
			for (int a = 0; a < data.length; a++) {
				extra = extra + (data[a] & 0xff) + " ";
			}
			extra = extra + ") ";
		}

		return "UnknownSampleDescriptionEntry[ type=\"" + type + "\", "
				+ "dataReference=" + dataReference + ", " + "data=\""
				+ (new String(data)) + "\" " + extra + "]";
	}

	/**
	 * Return the byte data in this description entry.
	 */
	public byte[] getData() {
		byte[] copy = new byte[data.length];
		System.arraycopy(data, 0, copy, 0, data.length);
		return copy;
	}
}