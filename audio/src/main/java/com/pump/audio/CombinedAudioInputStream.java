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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import com.pump.io.CombinedInputStream;

public class CombinedAudioInputStream extends AudioInputStream {
	List<AudioInputStream> streams = new ArrayList<AudioInputStream>();

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
		if (!format1.getEncoding().equals(format2.getEncoding()))
			return false;

		return true;
	}

	private static InputStream createInputStream(AudioInputStream[] audioIns) {
		boolean[] b = new boolean[audioIns.length];
		for (int a = 0; a < audioIns.length; a++) {
			b[a] = true;
		}
		return new CombinedInputStream(audioIns, b);
	}

	private static long getFrameCount(AudioInputStream[] audioIn) {
		long sum = 0;
		for (int a = 0; a < audioIn.length; a++) {
			sum += audioIn[a].getFrameLength();
		}
		return sum;
	}
}