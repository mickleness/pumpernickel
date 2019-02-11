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

import java.util.HashSet;
import java.util.Set;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;

/**
 * This converts PCM audio to a wav-friendly AudioFormat. This filter can
 * perform three conversions:
 * <ul>
 * <li>Convert big endian to little endian.</li>
 * <li>Convert signed 8-bit to unsigned 8-bit.</li>
 * <li>Convert unsigned 16-bit to signed 16-bit.</li>
 * </ul>
 * <p>
 * It is technically possible for a wav file to have big-endian encoding if the
 * header begins with "RIFX" instead of "RIFF", but this class assumes you want
 * to write the traditional "RIFF" header.
 * <p>
 * This class is not capable of converting non-PCM audio samples to a
 * wav-friendly AudioFormat, though.
 */
public class WavPCMAudioInputStreamFilter extends FrameFilterAudioInputStream {

	private static enum Conversion {
		TO_LITTLE_ENDIAN, TO_SIGNED, TO_UNSIGNED
	};

	/**
	 * @return true if this AudioFormat is ready to be output to a wav audio
	 *         file. This will return false if the format is big-endian, or if
	 *         the sign doesn't match the sample size. In wav files: 16-bit
	 *         audio is signed, and 8-bit audio is unsigned. So if this format
	 *         represents 16-bit unsigned or 8-bit signed: then this method
	 *         returns false.
	 * @param format
	 */
	public static boolean isWavCompatible(AudioFormat format) {
		if (!(format.getEncoding().equals(Encoding.PCM_SIGNED) || format
				.getEncoding().equals(Encoding.PCM_UNSIGNED))) {
			System.err.println("encoding: " + format.getEncoding());
			throw new IllegalArgumentException(
					"this class only works with PCM encoded data");
		}
		Set<Conversion> conversions = getRequiredConversions(format);
		return conversions.isEmpty();
	}

	private static Set<Conversion> getRequiredConversions(AudioFormat format) {
		Set<Conversion> set = new HashSet<Conversion>();
		if (format.isBigEndian())
			set.add(Conversion.TO_LITTLE_ENDIAN);
		if (format.getEncoding().equals(Encoding.PCM_SIGNED)
				&& format.getSampleSizeInBits() == 8)
			set.add(Conversion.TO_UNSIGNED);
		if (format.getEncoding().equals(Encoding.PCM_UNSIGNED)
				&& format.getSampleSizeInBits() == 16)
			set.add(Conversion.TO_SIGNED);
		return set;
	}

	final boolean applyConversions;
	final AudioFormat sourceFormat, destFormat;
	final int sampleSize;
	final int samplesPerFrame;
	final boolean isSourceBigEndian, isDestBigEndian, isSourceSigned,
			isDestSigned;
	final int valueDelta;

	public WavPCMAudioInputStreamFilter(AudioInputStream audioIn) {
		super(audioIn);
		sourceFormat = audioIn.getFormat();
		Set<Conversion> conversions = getRequiredConversions(sourceFormat);
		applyConversions = conversions.size() > 0;
		frameSize = sourceFormat.getFrameSize();
		sampleSize = sourceFormat.getSampleSizeInBits() / 8;
		Encoding encoding;
		if (conversions.contains(Conversion.TO_SIGNED)) {
			encoding = Encoding.PCM_SIGNED;
			if (sampleSize == 1) {
				// values are input unsigned as [0, 255], and output to
				// [-128,127]
				valueDelta = -128;
			} else {
				// values are input unsigned as [0, 65535], and output to
				// [-32768,32767]
				valueDelta = -32768;
			}
		} else if (conversions.contains(Conversion.TO_UNSIGNED)) {
			encoding = Encoding.PCM_UNSIGNED;
			if (sampleSize == 1) {
				// values are input unsigned as [-128,127], and output to [0,
				// 255]
				valueDelta = 128;
			} else {
				// values are input unsigned as [-32768,32767], and output to
				// [0, 65535]
				valueDelta = 32768;
			}
		} else {
			encoding = sourceFormat.getEncoding();
			valueDelta = 0;
		}
		destFormat = new AudioFormat(encoding, sourceFormat.getSampleRate(),
				sourceFormat.getSampleSizeInBits(), sourceFormat.getChannels(),
				sourceFormat.getFrameSize(), sourceFormat.getFrameRate(),
				false, sourceFormat.properties());
		samplesPerFrame = frameSize / sampleSize;

		isSourceBigEndian = sourceFormat.isBigEndian();
		isDestBigEndian = destFormat.isBigEndian();
		isSourceSigned = sourceFormat.getEncoding().equals(Encoding.PCM_SIGNED);
		isDestSigned = destFormat.getEncoding().equals(Encoding.PCM_SIGNED);
	}

	@Override
	public AudioFormat getFormat() {
		return destFormat;
	}

	@Override
	protected void filterFrames(byte[] data, int off, int frameCount) {
		if (applyConversions == false) {
			return;
		} else {
			int sampleCount = frameCount * samplesPerFrame;
			if (valueDelta != 0) {
				for (int i = 0; i < sampleCount; i++) {
					int value = PCMUtils.decodeSample(data, off, sampleSize,
							isSourceSigned, isSourceBigEndian);
					value += valueDelta;
					PCMUtils.encodeSample(value, data, off, sampleSize,
							isDestSigned, isDestBigEndian);
					off += sampleSize;
				}
			} else {
				for (int i = 0; i < sampleCount; i++) {
					int value = PCMUtils.decodeSample(data, off, sampleSize,
							isSourceSigned, isSourceBigEndian);
					PCMUtils.encodeSample(value, data, off, sampleSize,
							isDestSigned, isDestBigEndian);
					off += sampleSize;
				}
			}
		}
	}
}