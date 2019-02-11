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
package com.pump.audio;

public class PCMUtils {
	public static int decodeSample(byte[] data, int offset, int sampleSize,
			boolean isSigned, boolean isBigEndian) {
		int value;
		switch (sampleSize) {
		case 1:
			if (isSigned) {
				value = data[offset];
			} else {
				value = (data[offset] & 0xff);
			}
			break;
		case 2:
			byte mostSigByte;
			byte leastSigByte;

			if (isBigEndian) {
				mostSigByte = data[offset];
				leastSigByte = data[offset + 1];
			} else {
				mostSigByte = data[offset + 1];
				leastSigByte = data[offset];
			}

			value = ((mostSigByte & 0xff) << 8) + (leastSigByte & 0xff);
			if (isSigned && value > 0x7fff) {
				// the most significant bit is flipped, and we're
				// in two's complement notation:
				value = -(0xffff - value) - 1;
			}

			break;
		default:
			throw new IllegalArgumentException("unsupported sampleSize: "
					+ sampleSize);
		}
		return value;
	}

	public static void encodeSample(int value, byte[] data, int offset,
			int sampleSize, boolean isSigned, boolean isBigEndian) {
		switch (sampleSize) {
		case 1:
			if (isSigned) {
				if (value < -128 || value > 127)
					throw new IllegalArgumentException("value (" + value
							+ ") must be between [-128, 127]");
				data[offset] = (byte) value;
			} else {
				if (value < 0 || value > 255)
					throw new IllegalArgumentException("value (" + value
							+ ") must be between [0, 255]");
				data[offset] = (byte) (value);
			}
			break;
		case 2:
			byte mostSigByte;
			byte leastSigByte;

			if (isSigned) {
				if (value < -32768 || value > 32767)
					throw new IllegalArgumentException("value (" + value
							+ ") must be between [-32768, 32767]");
				if (value >= 0) {
					mostSigByte = (byte) ((value >> 8) & 0xff);
					leastSigByte = (byte) ((value >> 0) & 0xff);
				} else {
					value = (value + 1) + 0xffff;
					mostSigByte = (byte) ((value >> 8) & 0xff);
					leastSigByte = (byte) ((value >> 0) & 0xff);
				}
			} else {
				if (value < 0 || value > 65535)
					throw new IllegalArgumentException("value (" + value
							+ ") must be between [0, 65535]");
				mostSigByte = (byte) ((value >> 8) & 0xff);
				leastSigByte = (byte) ((value >> 0) & 0xff);
			}

			if (isBigEndian) {
				data[offset] = mostSigByte;
				data[offset + 1] = leastSigByte;
			} else {
				data[offset + 1] = mostSigByte;
				data[offset] = leastSigByte;
			}
			break;
		default:
			throw new IllegalArgumentException("unsupported sampleSize: "
					+ sampleSize);
		}
	}
}