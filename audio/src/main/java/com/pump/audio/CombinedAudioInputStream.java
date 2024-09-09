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
package com.pump.audio;

import com.pump.io.CombinedInputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.InputStream;

public class CombinedAudioInputStream extends AudioInputStream {

	public CombinedAudioInputStream(AudioInputStream in1, AudioInputStream in2) {
		this(new AudioInputStream[] { in1, in2 });
	}

	public CombinedAudioInputStream(AudioInputStream[] inputs) {
		super(createInputStream(inputs), inputs[0].getFormat(),
				getFrameCount(inputs));

		for (int a = 1; a < inputs.length; a++) {
			if (!equals(inputs[0].getFormat(), inputs[a].getFormat())) {
				throw new IllegalArgumentException("inputs[0] = "
						+ inputs[0].getFormat() + ", inputs[" + a + "] = "
						+ inputs[a].getFormat());
			}
		}
	}

	private boolean equals(AudioFormat format1, AudioFormat format2) {
		if (format1.getChannels() != format2.getChannels())
			return false;
		if (format1.isBigEndian() != format2.isBigEndian())
			return false;
		if (format1.getSampleRate() != format2.getSampleRate())
			return false;
		if (format1.getSampleSizeInBits() != format2.getSampleSizeInBits())
			return false;
		return format1.getEncoding().equals(format2.getEncoding());
	}

	private static InputStream createInputStream(AudioInputStream[] audioIns) {
		return new CombinedInputStream(audioIns);
	}

	private static long getFrameCount(AudioInputStream[] audioIn) {
		long sum = 0;
		for (AudioInputStream audioInputStream : audioIn) {
			sum += audioInputStream.getFrameLength();
		}
		return sum;
	}
}