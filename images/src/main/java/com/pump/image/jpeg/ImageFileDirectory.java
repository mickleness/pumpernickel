/**
 * This software is released as part of the Pumpernickel project.
 * <p>
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * <p>
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.image.jpeg;

import java.io.*;
import java.util.*;

class ImageFileDirectory implements JPEGPropertyConstants {

	static Map<Integer, String> TYPE_LUT = new HashMap<>();
	static {
		TYPE_LUT.put(256, PROPERTY_IMAGE_WIDTH);
		TYPE_LUT.put(257, PROPERTY_IMAGE_LENGTH);
		TYPE_LUT.put(258, PROPERTY_BITS_PER_SAMPLE);
		TYPE_LUT.put(259, PROPERTY_COMPRESSION);
		TYPE_LUT.put(262, PROPERTY_PHOTOMETRIC_INTERPRETATION);
		TYPE_LUT.put(270, PROPERTY_IMAGE_DESCRIPTION);
		TYPE_LUT.put(271, PROPERTY_MAKE);
		TYPE_LUT.put(272, PROPERTY_MODEL);
		TYPE_LUT.put(273, PROPERTY_STRIP_OFFSETS);
		TYPE_LUT.put(274, PROPERTY_ORIENTATION);
		TYPE_LUT.put(277, PROPERTY_SAMPLES_PER_PIXEL);
		TYPE_LUT.put(278, PROPERTY_ROWS_PER_STRIP);
		TYPE_LUT.put(279, PROPERTY_STRIP_BYTE_COUNTS);
		TYPE_LUT.put(282, PROPERTY_X_RESOLUTION);
		TYPE_LUT.put(283, PROPERTY_Y_RESOLUTION);
		TYPE_LUT.put(284, PROPERTY_PLANAR_CONFIGURATION);
		TYPE_LUT.put(296, PROPERTY_RESOLUTION_UNIT);
		TYPE_LUT.put(301, PROPERTY_TRANSFER_FUNCTION);
		TYPE_LUT.put(305, PROPERTY_SOFTARE);
		TYPE_LUT.put(306, PROPERTY_DATE_TIME);
		TYPE_LUT.put(315, PROPERTY_ARTIST);
		TYPE_LUT.put(318, PROPERTY_WHITE_POINT);
		TYPE_LUT.put(319, PROPERTY_PRIMARY_CHROMATICITIES);
		TYPE_LUT.put(513, PROPERTY_JPEG_INTERCHANGE_FORMAT);
		TYPE_LUT.put(514, PROPERTY_JPEG_INTERCHANGE_FORMAT_LENGTH);
		TYPE_LUT.put(529, PROPERTY_Y_CB_CR_COEFFICIENTS);
		TYPE_LUT.put(530, PROPERTY_Y_CB_CR_SUB_SAMPLING);
		TYPE_LUT.put(531, PROPERTY_Y_CB_CR_POSITIONING);
		TYPE_LUT.put(532, PROPERTY_REFERENCE_BLACK_WHITE);
		TYPE_LUT.put(33432, PROPERTY_COPYRIGHT);
		TYPE_LUT.put(34665, PROPERTY_EXIF_IFD_POINTER);
		TYPE_LUT.put(34853, PROPERTY_GPSINFO_IFD_POINTER);
		TYPE_LUT.put(33434, PROPERTY_EXPOSURE_TIME);
		TYPE_LUT.put(33437, PROPERTY_F_NUMBER);
		TYPE_LUT.put(34850, PROPERTY_EXPOSURE_PROGRAM);
		TYPE_LUT.put(34852, PROPERTY_SPECTRAL_SENSITIVITY);
		TYPE_LUT.put(34855, PROPERTY_ISO_SPEED_RATINGS);
		TYPE_LUT.put(34856, PROPERTY_OECF);
		TYPE_LUT.put(36864, PROPERTY_EXIF_VERSION);
		TYPE_LUT.put(36867, PROPERTY_DATE_TIME_ORIGINAL);
		TYPE_LUT.put(36868, PROPERTY_DATE_TIME_DIGITIZED);
		TYPE_LUT.put(37121, PROPERTY_COMPONENTS_CONFIGURATION);
		TYPE_LUT.put(37122, PROPERTY_COMPRESSED_BITS_PER_PIXEL);
		TYPE_LUT.put(37377, PROPERTY_SHUTTER_SPEED_VALUE);
		TYPE_LUT.put(37378, PROPERTY_APERTURE_VALUE);
		TYPE_LUT.put(37379, PROPERTY_BRIGHTNESS_VALUE);
		TYPE_LUT.put(37380, PROPERTY_EXPOSURE_BIAS_VALUE);
		TYPE_LUT.put(37381, PROPERTY_MAX_APERTURE_VALUE);
		TYPE_LUT.put(37382, PROPERTY_SUBJECT_DISTANCE);
		TYPE_LUT.put(37383, PROPERTY_METERING_MODE);
		TYPE_LUT.put(37384, PROPERTY_LIGHT_SOURCE);
		TYPE_LUT.put(37385, PROPERTY_FLASH);
		TYPE_LUT.put(37386, PROPERTY_FOCAL_LENGTH);
		TYPE_LUT.put(37396, PROPERTY_SUBJECT_AREA);
		TYPE_LUT.put(37500, PROPERTY_MAKER_NOTE);
		TYPE_LUT.put(37510, PROPERTY_USER_COMMENT);
		TYPE_LUT.put(37520, PROPERTY_SUB_SEC_TIME);
		TYPE_LUT.put(37521, PROPERTY_SUB_SEC_TIME_ORIGINAL);
		TYPE_LUT.put(37522, PROPERTY_SUB_SEC_TIME_DIGITIZED);

		TYPE_LUT.put(40960, PROPERTY_FLASHPIX_VERSION);
		TYPE_LUT.put(40961, PROPERTY_COLOR_SPACE);
		TYPE_LUT.put(40962, PROPERTY_PIXEL_X_DIMENSION);
		TYPE_LUT.put(40963, PROPERTY_PIXEL_Y_DIMENSION);
		TYPE_LUT.put(40964, PROPERTY_RELATED_SOUND_FILE);
		TYPE_LUT.put(40965, PROPERTY_INTEROPERABILITY_IFD_POINTER);
		TYPE_LUT.put(41483, PROPERTY_FLASH_ENERGY);
		TYPE_LUT.put(41484, PROPERTY_SPATIAL_FREQUENCY_RESPONSE);
		TYPE_LUT.put(41486, PROPERTY_FOCAL_PLANE_X_RESOLUTION);
		TYPE_LUT.put(41487, PROPERTY_FOCAL_PLANE_Y_RESOLUTION);
		TYPE_LUT.put(41488, PROPERTY_FOCAL_PLANE_RESOLUTION_UNIT);
		TYPE_LUT.put(41492, PROPERTY_SUBJECT_LOCATION);
		TYPE_LUT.put(41493, PROPERTY_EXPOSURE_INDEX);
		TYPE_LUT.put(41495, PROPERTY_SENSING_METHOD);
		TYPE_LUT.put(41728, PROPERTY_FILE_SOURCE);
		TYPE_LUT.put(41729, PROPERTY_SCENE_TYPE);
		TYPE_LUT.put(41730, PROPERTY_CFA_PATTERN);
		TYPE_LUT.put(41985, PROPERTY_CUSTOM_RENDERED);
		TYPE_LUT.put(41986, PROPERTY_EXPOSURE_MODE);
		TYPE_LUT.put(41987, PROPERTY_WHITE_BALANCE);
		TYPE_LUT.put(41988, PROPERTY_DIGITAL_ZOOM_RATIO);
		TYPE_LUT.put(41989, PROPERTY_FOCAL_LENGTH_IN_35MM_FILM);
		TYPE_LUT.put(41990, PROPERTY_SCENE_CAPTURE_TYPE);
		TYPE_LUT.put(41991, PROPERTY_GAIN_CONTROL);
		TYPE_LUT.put(41992, PROPERTY_CONTRAST);
		TYPE_LUT.put(41993, PROPERTY_SATURATION);
		TYPE_LUT.put(41994, PROPERTY_SHARPNESS);
		TYPE_LUT.put(41995, PROPERTY_DEVICE_SETTING_DESCRIPTION);
		TYPE_LUT.put(41996, PROPERTY_SUBJECT_DISTANCE_RANGE);
		TYPE_LUT.put(42016, PROPERTY_UNIQUE_ID);
	}

	static class DirectoryEntry {
		int tagNumber;

		int dataFormat;

		int componentCount;

		/**
		 * This is a 4-byte value that either: contains the data of this entry
		 * OR points to the offset after the TIFF header when we should read
		 * this data if it is more than 4 bytes long.
		 */
		byte[] fieldValue;
		Object value;

		DirectoryEntry(InputStream in, boolean reverse) throws IOException {
			byte[] array = new byte[4];

			if (JPEGMarkerInputStream.readFully(in, array, 2, reverse) != 2)
				throw new IOException();
			tagNumber = (array[0] & 0xff) * 256 + (array[1] & 0xff);

			if (JPEGMarkerInputStream.readFully(in, array, 2, reverse) != 2)
				throw new IOException();
			dataFormat = (array[0] & 0xff) * 256 + (array[1] & 0xff);

			if (JPEGMarkerInputStream.readFully(in, array, 4, reverse) != 4)
				throw new IOException();
			componentCount = ((array[0] & 0xff) << 24)
					+ ((array[1] & 0xff) << 16) + ((array[2] & 0xff) << 8)
					+ ((array[3] & 0xff) << 0);

			fieldValue = new byte[4];
			if (JPEGMarkerInputStream.readFully(in, fieldValue, 4, reverse) != 4)
				throw new IOException();
		}

		String getPropertyName() {
			Integer key = tagNumber;
			return TYPE_LUT.get(key);
		}

		private int getValueByteLength() throws IOException {
			int bytesPerComponent;
			switch (dataFormat) {
				case 1 -> // byte
						bytesPerComponent = 1;
				case 2 -> // ASCII
						bytesPerComponent = 1;
				case 3 -> // short
						bytesPerComponent = 2;
				case 4 -> // long
						bytesPerComponent = 4;
				case 5 -> // rational
						bytesPerComponent = 8;
				case 6 -> // signed byte
						bytesPerComponent = 1;
				case 7 -> // undefined
						bytesPerComponent = 8;
				case 8 -> // signed short
						bytesPerComponent = 2;
				case 9 -> // signed long
						bytesPerComponent = 8;
				case 10 -> // signed rational
						bytesPerComponent = 8;
				case 0 -> { // unknown, but this occurred in some JPEGs on my hard disk
					return 0;
				}
				default -> throw new IOException("unrecognized data type (" + dataFormat
						+ ")");
			}
			return bytesPerComponent * componentCount;
		}

		/**
		 * Returns a 1-byte unsigned int. (The names of these "read" values are
		 * based on the exif specs, not on Java's definition of primitives.)
		 */
		int readByte(byte[] data, int offset) {
			return (data[offset] & 0xff);
		}

		/**
		 * Returns a 1-byte signed int. (The names of these "read" values are
		 * based on the exif specs, not on Java's definition of primitives.)
		 */
		int readSignedByte(byte[] data, int offset) {
			return (data[offset]);
		}

		/**
		 * Returns a 2-byte unsigned int. (The names of these "read" values are
		 * based on the exif specs, not on Java's definition of primitives.)
		 */
		int readShort(byte[] data, int offset) {
			return (((data[offset] & 0xff) << 8) + ((data[offset + 1] & 0xff) << 0));
		}

		/**
		 * Returns a 2-byte signed int. (The names of these "read" values are
		 * based on the exif specs, not on Java's definition of primitives.)
		 */
		int readSignedShort(byte[] data, int offset) {
			// TODO: implement two's complement here.
			return (((data[offset] & 0xff) << 8) + ((data[offset + 1] & 0xff) << 0));
		}

		/**
		 * Returns a 4-byte unsigned int. (The names of these "read" values are
		 * based on the exif specs, not on Java's definition of primitives.)
		 */
		int readLong(byte[] data, int offset) {
			return (((data[offset + 0] & 0xff) << 24)
					+ ((data[offset + 1] & 0xff) << 16)
					+ ((data[offset + 2] & 0xff) << 8) + ((data[offset + 3] & 0xff) << 0));
		}

		/**
		 * Returns a 4-byte signed int. (The names of these "read" values are
		 * based on the exif specs, not on Java's definition of primitives.)
		 */
		int readSignedLong(byte[] data, int offset) {
			// TODO: implement two's complement here.
			return (((data[offset + 0] & 0xff) << 24)
					+ ((data[offset + 1] & 0xff) << 16)
					+ ((data[offset + 2] & 0xff) << 8) + ((data[offset + 3] & 0xff) << 0));
		}

		/**
		 * Returns the position after the TIFF header where this data resides,
		 * or -1 if the data is actually contained in the 4-byte value field
		 * already retrieved.
		 */
		int positionAfterTIFFHeader() throws IOException {
			int byteLength = getValueByteLength();
			if (byteLength > 4) {
				int positionAfterTIFFHeader = readLong(fieldValue, 0);
				return positionAfterTIFFHeader;
			}
			return -1;
		}

		/**
		 * Populate the {@link #value} field.
		 * 
		 * @param in
		 *            a BufferedInputStream that was last marked at the start of
		 *            the TIFF header.
		 */
		void resolveValue(BufferedInputStream in) throws IOException {
			int byteLength = getValueByteLength();
			byte[] data;
			if (byteLength > 4) {
				// we have to read this data:
				byte[] newData = new byte[byteLength];
				int positionAfterTIFFHeader = readLong(fieldValue, 0);
				in.reset();
				if (JPEGMarkerInputStream
						.skipFully(in, positionAfterTIFFHeader) != positionAfterTIFFHeader)
					throw new IOException();
				JPEGMarkerInputStream.readFully(in, newData, byteLength, false);
				data = newData;
			} else {
				data = fieldValue;
			}

			if (tagNumber == 274) {
				for (JPEGMetaData.Orientation t : JPEGMetaData.Orientation.values()) {
					// I had files on my local computer resembles '0 3 0 0' AND '0 0 0 3'
					// I'm not sure if I'm parsing something wrong or not, but just in
					// case I'll look for both formats for now:
					if ( (fieldValue[0] == 0 && fieldValue[1] == t.exifOrientationValue) ||
							(fieldValue[2] == 0 && fieldValue[3] == t.exifOrientationValue)) {
						value = t;
						return;
					}
				}
			}

			if (dataFormat == 7 || dataFormat == 0) { // UNDEFINED
				value = data;
				return;
			} else if (dataFormat == 2) { // ASCII
				StringBuilder buffer = new StringBuilder();
				for (int a = 0; a < componentCount; a++) {
					int i = (data[a] & 0xff);
					if (i == 0) {
						break;
					}
					char c = (char) i;
					buffer.append(c);

				}
				value = buffer.toString();
				return;
			}
			Object[] valueArray;
			if (dataFormat == 1 || // BYTE
					dataFormat == 6 || // SIGNED BYTE
					dataFormat == 3 || // SHORT
					dataFormat == 8 || // SIGNED SHORT
					dataFormat == 4 || // LONG
					dataFormat == 9) { // SIGNED LONG
				valueArray = new Integer[componentCount];
			} else if (dataFormat == 5 || // RATIONAL
					dataFormat == 10) { // SIGNED RATIONAL
				valueArray = new Double[componentCount];
			} else {
				throw new RuntimeException("unexpected data format ("
						+ dataFormat + ")");
			}

			for (int a = 0; a < componentCount; a++) {
				switch (dataFormat) {
					case 1 -> // byte
							valueArray[a] = readByte(data, a * 1);
					case 6 -> // signed byte
							valueArray[a] = readSignedByte(data, a * 1);
					case 3 -> // short
							valueArray[a] = readShort(data, a * 2);
					case 8 -> // signed short
							valueArray[a] = readSignedShort(data, a * 2);
					case 4 -> // long
							valueArray[a] = readLong(data, a * 4);
					case 9 -> // signed long
							valueArray[a] = readSignedLong(data, a * 4);
					case 5 -> { // rational
						double numerator = readLong(data, a * 4);
						double denominator = readLong(data, a * 4 + 4);
						valueArray[a] = numerator / denominator;
					}
					case 10 -> { // signed rational
						double numerator2 = readSignedLong(data, a * 4);
						double denominator2 = readSignedLong(data, a * 4 + 4);
						valueArray[a] = numerator2 / denominator2;
					}
					default -> throw new RuntimeException("Unexpected condition.");
				}
			}

			if (valueArray.length == 1) {
				value = valueArray[0];
			} else {
				value = valueArray;
			}
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("[ ");
			String typeName = TYPE_LUT.get(tagNumber);
			if (typeName == null) {
				sb.append("UNKNOWN ");
			} else {
				sb.append(typeName);
				sb.append(' ');
			}

			if (dataFormat == 1) {
				sb.append("BYTE ");
			} else if (dataFormat == 2) {
				sb.append("ASCII ");
			} else if (dataFormat == 3) {
				sb.append("SHORT ");
			} else if (dataFormat == 4) {
				sb.append("LONG ");
			} else if (dataFormat == 5) {
				sb.append("RATIONAL ");
			} else if (dataFormat == 7) {
				sb.append("UNDEFINED ");
			} else if (dataFormat == 9) {
				sb.append("SLONG ");
			} else if (dataFormat == 10) {
				sb.append("SRATIONAL ");
			} else {
				sb.append("UNRECOGNIZED ");
			}
			sb.append(componentCount).append(" ");
			sb.append(Objects.requireNonNullElseGet(value, () -> readLong(fieldValue, 0))).append(" ]");
			return sb.toString();
		}
	}

	DirectoryEntry[] entries;
	long nextIFDPosition;

	ImageFileDirectory(BufferedInputStream bufferedIn,
			long positionAfterTIFFHeader, boolean reverse) throws IOException {
		bufferedIn.reset();
		if (JPEGMarkerInputStream
				.skipFully(bufferedIn, positionAfterTIFFHeader) != positionAfterTIFFHeader)
			throw new IOException();

		byte[] array = new byte[4];
		if (JPEGMarkerInputStream.readFully(bufferedIn, array, 2, reverse) != 2)
			throw new IOException("Corrupt image file directory.");

		int numberOfEntries = (array[0] & 0xff) * 256 + (array[1] & 0xff);

		entries = new DirectoryEntry[numberOfEntries];
		for (int a = 0; a < numberOfEntries; a++) {
			try {
				DirectoryEntry entry = new DirectoryEntry(bufferedIn, reverse);
				entries[a] = (entry);
			} catch (RuntimeException e) {
				System.err.println(a + " / " + numberOfEntries);
				throw e;
			}
		}

		/*
		 * According to the specs: what is supposed to follow is a 4-byte
		 * pointer to the next IFD. However I found an APP1 block that omits
		 * these 4 bytes and instead contains some DirectoryEntries.
		 * 
		 * So first I'll scan through the entries and see if any of them believe
		 * they should start here, and if so we just assume there is no next IFD
		 * to point to. (If anything this errs on the side of omitting data,
		 * instead of trying to read data where the isn't any.)
		 * 
		 */
		long positionToExpectIFDPtr = positionAfterTIFFHeader + 2 + 12L
				* numberOfEntries;
		boolean missingIFDPtr = false;
		for (int a = 0; a < numberOfEntries && (!missingIFDPtr); a++) {
			int k = entries[a].positionAfterTIFFHeader();
			if (k != -1 && k == positionToExpectIFDPtr) {
				missingIFDPtr = true;
			}
		}

		if (missingIFDPtr) {
			// there is no space for the IFD position to be, so assume it's
			// zero.
			nextIFDPosition = 0;
		} else {
			if (JPEGMarkerInputStream.readFully(bufferedIn, array, 4, reverse) != 4)
				throw new IOException("Corrupt image file directory.");

			nextIFDPosition = ((array[0] & 0xff) << 24)
					+ ((array[1] & 0xff) << 16) + ((array[2] & 0xff) << 8)
					+ (array[3] & 0xff);
		}
	}

	/**
	 * Returns the value associated with a tag number. This should only be
	 * called after entries have been prompted to resolve.
	 * 
	 * @param tagNumber
	 *            the tag number to search for
	 * @return the value of that tag, or null if the tag was not found.
	 */
	Object getProperty(int tagNumber) {
		for (DirectoryEntry entry : entries) {
			if (entry.tagNumber == tagNumber) {
				return entry.value;
			}
		}
		return null;
	}

	public String toString() {
		return "ImageFileDirectory[ " + Arrays.toString(entries) + " ]";
	}
}