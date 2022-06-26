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
package com.pump.audio;

import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;

/**
 * This class alters the volume of a PCM-encoded AudioInputStream.
 */
public abstract class VolumeAdjustedPCMAudioInputStream extends
		FrameFilterAudioInputStream {
	final protected int sampleSize, minSampleValue, maxSampleValue;
	final boolean isBigEndian, isSigned;

	public VolumeAdjustedPCMAudioInputStream(AudioInputStream audioIn) {
		super(audioIn);
		int sampleSizeInBits = format.getSampleSizeInBits();
		if (!(sampleSizeInBits == 8 || sampleSizeInBits == 16))
			throw new IllegalArgumentException("sampleSizeInBits = "
					+ sampleSizeInBits);
		sampleSize = sampleSizeInBits / 8;

		isBigEndian = format.isBigEndian();
		if (format.getEncoding().equals(Encoding.PCM_SIGNED)) {
			if (sampleSize == 1) {
				minSampleValue = -128;
				maxSampleValue = 127;
			} else {
				minSampleValue = -32768;
				maxSampleValue = 32767;
			}
			isSigned = true;
		} else if (format.getEncoding().equals(Encoding.PCM_UNSIGNED)) {
			if (sampleSize == 1) {
				minSampleValue = 0;
				maxSampleValue = 255;
			} else {
				minSampleValue = 0;
				maxSampleValue = 65535;
			}
			isSigned = false;
		} else {
			throw new IllegalArgumentException("unsupported encoding: "
					+ format.getEncoding());
		}
	}

	@Override
	protected void filterFrames(byte[] data, int off, int frameCount) {
		int frameOffset = off;
		for (int frame = 0; frame < frameCount; frame++) {
			float time = (framePos - frameCount + frame)
					/ format.getFrameRate();
			float volume = getVolume(time);

			if (volume < .999f || volume > 1.001f) {
				for (int byteIndex = 0; byteIndex < frameSize; byteIndex += sampleSize) {
					int value = PCMUtils.decodeSample(data, frameOffset
							+ byteIndex, sampleSize, isSigned, isBigEndian);
					value = (int) (value * volume);
					if (value > maxSampleValue)
						value = maxSampleValue;
					if (value < minSampleValue)
						value = minSampleValue;
					PCMUtils.encodeSample(value, data, frameOffset + byteIndex,
							sampleSize, isSigned, isBigEndian);
				}
			}
			frameOffset += frameSize;
		}
	}

	/**
	 * Return the volume multiplier for a given time in this AudioInputStream.
	 * 
	 * @param time
	 *            the time (in seconds) to retrieve the volume for
	 * @return the volume multiplier. For example: 0=silent, .5=half volume,
	 *         1.0=normal volume, 2.0=double volume.
	 *         <p>
	 *         The method <code>filterFrames(..)</code> calls this method and
	 *         imposes a minimum and maximum on the returned value, so if you
	 *         double or quintuple the volume: you may get distorted audio but
	 *         you will not be encoding invalid PCM values.
	 */
	protected abstract float getVolume(float time);
}