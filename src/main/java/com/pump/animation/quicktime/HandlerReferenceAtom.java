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

import com.pump.io.GuardedInputStream;
import com.pump.io.GuardedOutputStream;

public class HandlerReferenceAtom extends LeafAtom {
	int version = 0;
	int flags = 0;
	String componentType;
	String componentSubtype;
	String componentManufacturer;
	long componentFlags = 0;
	long componentFlagsMask = 0;
	String componentName = "";

	public HandlerReferenceAtom(String componentType, String componentSubtype,
			String componentManufacturer) {
		super(null);
		this.componentType = componentType;
		this.componentSubtype = componentSubtype;
		this.componentManufacturer = componentManufacturer;
	}

	public HandlerReferenceAtom(Atom parent, GuardedInputStream in)
			throws IOException {
		super(parent);

		int bytesToRead = (int) in.getRemainingLimit();
		version = in.read();
		flags = read24Int(in);
		componentType = read32String(in);
		componentSubtype = read32String(in);
		componentManufacturer = read32String(in);
		componentFlags = read32Int(in);
		componentFlagsMask = read32Int(in);

		int stringSize = in.read();
		if (stringSize != bytesToRead - 25) {
			// this is NOT a counted string, as the API
			// suggests it is: instead it's a pascal string.
			// thanks to Chris Adamson for pointing this out.
			byte[] data = new byte[bytesToRead - 24];
			data[0] = (byte) stringSize;
			read(in, data, 1, data.length - 1);
			componentName = new String(data);
		} else {
			byte[] data = new byte[stringSize];
			read(in, data);
			componentName = new String(data);
		}
	}

	public void setComponentFlags(long v) {
		componentFlags = v;
	}

	public void setComponentFlagsMask(long v) {
		componentFlagsMask = v;
	}

	public void setComponentName(String s) {
		componentName = s;
	}

	public long getComponentFlags() {
		return componentFlags;
	}

	public long getComponentFlagsMask() {
		return componentFlagsMask;
	}

	public String getComponentName() {
		return componentName;
	}

	@Override
	protected String getIdentifier() {
		return "hdlr";
	}

	@Override
	protected long getSize() {
		byte[] data = componentName.getBytes();
		return 33 + data.length;
	}

	@Override
	protected void writeContents(GuardedOutputStream out) throws IOException {
		out.write(version);
		write24Int(out, flags);
		write32String(out, componentType);
		write32String(out, componentSubtype);
		write32String(out, componentManufacturer);
		write32Int(out, componentFlags);
		write32Int(out, componentFlagsMask);
		byte[] data = componentName.getBytes();
		out.write(data.length);
		out.write(data);
	}

	@Override
	public String toString() {
		return "HandlerReferenceAtom[ version=" + version + ", " + "flags="
				+ flags + ", " + "componentType=\"" + componentType + "\", "
				+ "componentSubtype=\"" + componentSubtype + "\", "
				+ "componentManufacturer=\"" + componentManufacturer + "\", "
				+ "componentFlags=" + componentFlags + ", "
				+ "componentFlagsMask=" + componentFlagsMask + ", "
				+ "componentName=\"" + componentName + "\" ]";
	}
}