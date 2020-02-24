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
package com.pump.animation.quicktime.atom;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This is the SampleDescriptionEntry for a VideoSampleDescriptionAtom.
 */
public class VideoSampleDescriptionEntry extends SampleDescriptionEntry {

	/**
	 * Create a VideoSampleDescriptionEntry for JPEG-based frames.
	 */
	public static VideoSampleDescriptionEntry createJPEGDescription(int width,
			int height) {
		VideoSampleDescriptionEntry e = new VideoSampleDescriptionEntry("jpeg",
				1, width, height);
		e.compressorName = "Photo - JPEG";
		e.version = 1;
		e.revision = 1;
		e.temporalQuality = 0;
		e.spatialQuality = 512;
		return e;
	}

	/**
	 * Create a VideoSampleDescriptionEntry for PNG-based frames.
	 */
	public static VideoSampleDescriptionEntry createPNGDescription(int width,
			int height) {
		VideoSampleDescriptionEntry e = new VideoSampleDescriptionEntry("png ",
				1, width, height);
		e.compressorName = "Photo - PNG";
		e.version = 1;
		e.revision = 1;
		e.temporalQuality = 0;
		e.spatialQuality = 512;
		return e;
	}

	/**
	 * A 16-bit integer indicating the version number of the compressed data.
	 * This is set to 0, unless a compressor has changed its format.
	 */
	protected int version = 0;

	/** A 16-bit integer that must be set to 0. */
	protected int revision = 0;

	/**
	 * A 32-bit integer that specifies the developer of the compressor that
	 * generated the compressed data. Often this field contains 'appl' to
	 * indicate Apple Computer, Inc.
	 */
	protected String vendor = "pump";

	/**
	 * A 32-bit integer containing a value from 0 to 1023 indicating the degree
	 * of temporal compression.
	 */
	protected long temporalQuality = 0;

	/**
	 * A 32-bit integer containing a value from 0 to 1024 indicating the degree
	 * of spatial compression.
	 */
	protected long spatialQuality = 512;

	/** A 16-bit integer that specifies the width of the source image in pixels. */
	protected int width;

	/**
	 * A 16-bit integer that specifies the height of the source image in pixels.
	 */
	protected int height;

	/**
	 * A 32-bit fixed-point number containing the horizontal resolution of the
	 * image in pixels for inch.
	 */
	protected float horizontalResolution = 72;

	/**
	 * A 32-bit fixed-point number containing the vertical resolution of the
	 * image in pixels for inch.
	 */
	protected float verticalResolution = 72;

	/** A 32-bit integer that must be set to zero. */
	protected long dataSize = 0;

	/**
	 * A 16-bit integer that indicates how many frames of compressed data are
	 * stored in each sample. Usually set to 1.
	 */
	protected int frameCount = 1;

	/**
	 * A Pascal string containing the name of the creator that compressed an
	 * image, such as "jpeg".
	 */
	protected String compressorName = "";

	/**
	 * A 16-bit integer that indicates the pixel depth of the compressed image.
	 * Values of 1, 2, 4, 8, 16, 24, and 32 indicate the depth of color images.
	 * The value of 32 should be used only if the image contains an alpha
	 * channel. Values of 34, 36, and 40 indicate 2-, 4-, and 9-bit grayscale,
	 * respectively, for grayscale images.
	 */
	protected int depth = 24;

	/**
	 * A 16-bit integer that identifies which color table ot use. If this field
	 * is set to -1, the default color table should be used for the specified
	 * depth. For all depths below 16 bits per pixels, this indicates a standard
	 * macintosh color table for the specified depth. Depths of 16, 24, and 32
	 * have no color table.
	 */
	protected int colorTableID = 65535;

	public VideoSampleDescriptionEntry(String type, int dataReference, int w,
			int h) {
		super(type, dataReference);
		width = w;
		height = h;
	}

	public VideoSampleDescriptionEntry(InputStream in) throws IOException {
		super(in);
		version = Atom.read16Int(in);
		revision = Atom.read16Int(in);
		vendor = Atom.read32String(in);
		temporalQuality = Atom.read32Int(in);
		spatialQuality = Atom.read32Int(in);
		width = Atom.read16Int(in);
		height = Atom.read16Int(in);
		horizontalResolution = Atom.read16_16Float(in);
		verticalResolution = Atom.read16_16Float(in);
		dataSize = Atom.read32Int(in);
		frameCount = Atom.read16Int(in);
		compressorName = Atom.read32BytePascalString(in);
		depth = Atom.read16Int(in);
		colorTableID = Atom.read16Int(in);
	}

	@Override
	protected void write(OutputStream out) throws IOException {
		Atom.write32Int(out, getSize());
		Atom.write32String(out, type);
		Atom.write48Int(out, 0);
		Atom.write16Int(out, dataReference);

		Atom.write16Int(out, version);
		Atom.write16Int(out, revision);
		Atom.write32String(out, vendor);
		Atom.write32Int(out, temporalQuality);
		Atom.write32Int(out, spatialQuality);
		Atom.write16Int(out, width);
		Atom.write16Int(out, height);
		Atom.write16_16Float(out, horizontalResolution);
		Atom.write16_16Float(out, verticalResolution);
		Atom.write32Int(out, dataSize);
		Atom.write16Int(out, frameCount);
		Atom.write32BytePascalString(out, compressorName);
		Atom.write16Int(out, depth);
		Atom.write16Int(out, colorTableID);
	}

	@Override
	protected long getSize() {
		return 86;
	}

	@Override
	public String toString() {
		return "VideoSampleDescriptionEntry[ type=\"" + type + "\", "
				+ "dataReference=" + dataReference + ", " + "version="
				+ version + ", " + "revision=" + revision + ", " + "vendor=\""
				+ vendor + "\", " + "temporalQuality=" + temporalQuality + ", "
				+ "spatialQuality=" + spatialQuality + ", " + "width=" + width
				+ ", " + "height=" + height + ", " + "horizontalResolution="
				+ horizontalResolution + ", " + "verticalResolution="
				+ verticalResolution + ", " + "dataSize=" + dataSize + ", "
				+ "frameCount=" + frameCount + ", " + "compressorName=\""
				+ compressorName + "\", " + "depth=" + depth + ", "
				+ "colorTableID=" + colorTableID + " ]";
	}

	/**
	 * Return a 16-bit integer indicating the version number of the compressed
	 * data. This is set to 0, unless a compressor has changed its format.
	 */
	public int getVersion() {
		return version;
	}

	/** Return a 16-bit integer that must be set to 0. */
	public int getRevision() {
		return revision;
	}

	/**
	 * Return a 32-bit integer that specifies the developer of the compressor
	 * that generated the compressed data. Often this field contains 'appl' to
	 * indicate Apple Computer, Inc.
	 */
	public String getVendor() {
		return vendor;
	}

	/**
	 * Return a 32-bit integer containing a value from 0 to 1023 indicating the
	 * degree of temporal compression.
	 */
	public long getTemporalQuality() {
		return temporalQuality;
	}

	/**
	 * Return a 32-bit integer containing a value from 0 to 1024 indicating the
	 * degree of spatial compression.
	 */
	public long getSpatialQuality() {
		return spatialQuality;
	}

	/**
	 * Return a 16-bit integer that specifies the width of the source image in
	 * pixels.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Return a 16-bit integer that specifies the height of the source image in
	 * pixels.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Return a 32-bit fixed-point number containing the horizontal resolution
	 * of the image in pixels for inch.
	 */
	public float getHorizontalResolution() {
		return horizontalResolution;
	}

	/**
	 * Return a 32-bit fixed-point number containing the vertical resolution of
	 * the image in pixels for inch.
	 */
	public float getVerticalResolution() {
		return verticalResolution;
	}

	/** Return 32-bit integer that must be set to zero. */
	public long getDataSize() {
		return dataSize;
	}

	/**
	 * Return a 16-bit integer that indicates how many frames of compressed data
	 * are stored in each sample. Usually set to 1.
	 */
	public int getFrameCount() {
		return frameCount;
	}

	/**
	 * Return a Pascal string containing the name of the creator that compressed
	 * an image, such as "jpeg".
	 */
	public String getCompressorName() {
		return compressorName;
	}

	/**
	 * Return a 16-bit integer that indicates the pixel depth of the compressed
	 * image. Values of 1, 2, 4, 8, 16, 24, and 32 indicate the depth of color
	 * images. The value of 32 should be used only if the image contains an
	 * alpha channel. Values of 34, 36, and 40 indicate 2-, 4-, and 9-bit
	 * grayscale, respectively, for grayscale images.
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * Return a 16-bit integer that identifies which color table ot use. If this
	 * field is set to -1, the default color table should be used for the
	 * specified depth. For all depths below 16 bits per pixels, this indicates
	 * a standard macintosh color table for the specified depth. Depths of 16,
	 * 24, and 32 have no color table.
	 */
	public int getColorTableID() {
		return colorTableID;
	}
}