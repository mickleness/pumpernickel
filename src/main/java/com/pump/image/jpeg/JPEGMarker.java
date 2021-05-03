package com.pump.image.jpeg;

/**
 * A set of JPEG marker identifiers. This is not meant to be all-inclusive, but
 * it should cover all the markers the adjacent code supports.
 */
public enum JPEGMarker {
	START_OF_IMAGE_MARKER("FFD8"), END_OF_IMAGE_MARKER("FFD9"), APP0_MARKER(
			"FFE0"), APP1_MARKER("FFE1"), APP2_MARKER("FFE2"), APP13_MARKER(
					"FFED"), COMMENT_MARKER("FFFE"), DEFINE_QUANTIZATION_MARKER(
							"FFDB"), BASELINE_MARKER(
									"FFC0"), DEFINE_HUFFMAN_MARKER("FFC4"),

	/**
	 * The start of scan marker is tricky. It technically has a small number of
	 * byte data (12 bytes), but following that header is all the image data for
	 * this JPEG until the end of image marker is reached. We can't really
	 * accurately predict how much data this will be, though, so basically when
	 * we hit this marker this input stream is done being useful.
	 * 
	 */
	START_OF_SCAN_MARKER("FFDA");

	public static JPEGMarker getMarkerForByteCode(String byteCode) {
		for (JPEGMarker m : JPEGMarker.values()) {
			if (m.getByteCode().equals(byteCode))
				return m;
		}
		return null;
	}

	private final String byteCode;

	JPEGMarker(String byteCode) {
		this.byteCode = byteCode;
	}

	/**
	 * The 4-digit hex code for this marker.
	 */
	public String getByteCode() {
		return byteCode;
	}
}