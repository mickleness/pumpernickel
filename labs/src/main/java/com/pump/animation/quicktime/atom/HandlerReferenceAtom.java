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

import com.pump.io.GuardedInputStream;
import com.pump.io.GuardedOutputStream;

/**
 * The handler reference atom specifies the media handler component that is to
 * be used to interpret the media’s data. The handler reference atom has an atom
 * type value of 'hdlr'.
 * <p>
 * Historically, the handler reference atom was also used for data references.
 * However, this use is no longer current and may now be safely ignored.
 * <p>
 * The handler atom within a media atom declares the process by which the media
 * data in the stream may be presented, and thus, the nature of the media in a
 * stream. For example, a video handler would handle a video track.
 */
public class HandlerReferenceAtom extends LeafAtom {

	public static final String ATOM_TYPE = "hdlr";

	protected int version = 0;
	protected int flags = 0;
	protected String componentType;
	protected String componentSubtype;
	protected String componentManufacturer;
	protected long componentFlags = 0;
	protected long componentFlagsMask = 0;
	protected String componentName = "";

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
	public String getIdentifier() {
		return ATOM_TYPE;
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

	/**
	 * Return a 1-byte specification of the version of this handler information.
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Return a 3-byte space for handler information flags. Set this field to 0.
	 */
	public int getFlags() {
		return flags;
	}

	/**
	 * Return a four-character code that identifies the type of the handler.
	 * Only two values are valid for this field: 'mhlr' for media handlers and
	 * 'dhlr' for data handlers.
	 */
	public String getComponentType() {
		return componentType;
	}

	/**
	 * Return a four-character code that identifies the type of the media
	 * handler or data handler. For media handlers, this field defines the type
	 * of data—for example, 'vide' for video data, 'soun' for sound data or
	 * ‘subt’ for subtitles.
	 * <p>
	 * For data handlers, this field defines the data reference type; for
	 * example, a component subtype value of 'alis' identifies a file alias.
	 */
	public String getComponentSubtype() {
		return componentSubtype;
	}

	/**
	 * Reserved. Set to 0.
	 */
	public String getComponentManufacturer() {
		return componentManufacturer;
	}
}