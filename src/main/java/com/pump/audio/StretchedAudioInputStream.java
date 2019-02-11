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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;

import com.pump.math.function.PolynomialFunction;

/**
 * This stretches an AudioInputStream so it plays back slower or faster than
 * normal.
 */
public class StretchedAudioInputStream extends AudioInputStream {

	/**
	 * Create a StretchedAudioInputStream that distorts the incoming audio so it
	 * matches a fixed number of frames.
	 * 
	 * @param in
	 *            the AudioInputStream to stretch.
	 * @param frames
	 *            the number of frames the input stream should be stretched to.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static StretchedAudioInputStream create(AudioInputStream in,
			long frames) throws IOException {
		AudioFormat format = in.getFormat();
		if (!(format.getEncoding().equals(Encoding.PCM_SIGNED) || format
				.getEncoding().equals(Encoding.PCM_UNSIGNED)))
			throw new IllegalArgumentException(
					"the audio input must be PCM-encoded data (found "
							+ format.getEncoding() + ")");

		PipedInputStream pipedIn = new PipedInputStream();
		PipedOutputStream pipedOut = new PipedOutputStream(pipedIn);

		/**
		 * One flaw with this model is that we always generate ALL the
		 * transformed data: even if the entity working with pipedIn is trying
		 * to skip large chunks of data.
		 */

		Thread thread = new StretchThread(in, format, frames, pipedOut);
		thread.start();
		return new StretchedAudioInputStream(pipedIn, format, frames);
	}

	private StretchedAudioInputStream(InputStream input, AudioFormat format,
			long frames) {
		super(input, format, frames);
	}

	/**
	 * Create a StretchedAudioInputStream that distorts the incoming audio so it
	 * matches a fixed duration.
	 * 
	 * @param input
	 *            the AudioInputStream to stretch.
	 * @param duration
	 *            the duration in seconds the AudioInputStream should be
	 *            stretched to.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static StretchedAudioInputStream create(AudioInputStream input,
			double duration) throws IOException {
		return create(input, getFrameCount(duration, input.getFormat()));
	}

	private static long getFrameCount(double duration, AudioFormat format) {
		return (long) (format.getFrameRate() * duration);
	}

	private static class StretchThread extends Thread {
		InputStream in;
		PolynomialFunction function;
		int frameSize;
		OutputStream out;

		StretchThread(AudioInputStream in, AudioFormat format, long frameCount,
				OutputStream out) {
			super("Stretch Audio Thread");
			this.in = new BufferedInputStream(in);
			this.out = out;
			frameSize = format.getFrameSize();
			function = PolynomialFunction.createFit(0, 0, in.getFrameLength(),
					frameCount);
		}

		@Override
		public void run() {
			try {
				byte[] data = new byte[2048 * frameSize];

				int read;
				long inputFrameIndex = 0;
				long outputFrameIndex = -1;
				while ((read = in.read(data)) != -1) {
					for (int t = 0; t < read; t += frameSize) {
						long mappedFrame = (long) function
								.evaluate(inputFrameIndex);
						while (outputFrameIndex <= mappedFrame) {
							out.write(data, t, frameSize);
							outputFrameIndex++;
						}
						inputFrameIndex++;
					}
				}
			} catch (IOException e) {
				if (e.getMessage().toLowerCase().contains("read end dead")) {
					// do nothing: someone closed the receiving input stream, so
					// we're done
				} else {
					e.printStackTrace();
				}
			} finally {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}