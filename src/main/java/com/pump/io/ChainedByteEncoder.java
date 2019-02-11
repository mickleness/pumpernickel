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
package com.pump.io;

import java.io.IOException;

/**
 * A series of ByteEncoders linked together.
 * <p>
 * As an abstract example: consider a series of translators. [ German to French,
 * French to English, English to Chinese]. By inputting German into this model, you
 * would eventually receive Chinese.
 */
public class ChainedByteEncoder extends ByteEncoder {

	class MyDataListener implements DataListener {
		int index;

		MyDataListener(int index) {
			this.index = index;
		}

		@Override
		public void chunkAvailable(ByteEncoder encoder) throws IOException {
			int[] chunk;
			do {
				chunk = encoder.pullImmediately();
				if (chunk != null && chunk.length > 0) {
					if (index == 0) {
						pushChunk(chunk);
					} else {
						for (int a = 0; a < chunk.length; a++) {
							encoders[index - 1].push(chunk[a]);
						}
					}
				}
			} while (chunk != null && chunk.length > 0);
		}

		@Override
		public void encoderClosed(ByteEncoder encoder) throws IOException {
			chunkAvailable(encoder);
		}
	}

	private boolean addedData = false;
	protected ByteEncoder[] encoders;

	public ChainedByteEncoder(ByteEncoder... encoders) {
		addEncoders(encoders);
	}

	/** Add one or more encoders to this ChainedByteEncoder. */
	public synchronized void addEncoders(ByteEncoder... newEncoders) {
		if (addedData)
			throw new IllegalStateException(
					"You cannot add encoders after push(b) has been called. Encoders must be configured before writing data.");

		int k = encoders == null ? 0 : encoders.length;
		for (int a = 0; a < newEncoders.length; a++) {
			if (newEncoders[a].getDataListener() != null)
				throw new IllegalArgumentException();
			newEncoders[a].setListener(new MyDataListener(a + k));
		}

		ByteEncoder[] copy;
		if (encoders == null) {
			copy = new ByteEncoder[newEncoders.length];
			System.arraycopy(newEncoders, 0, copy, 0, newEncoders.length);
		} else {
			copy = new ByteEncoder[encoders.length + newEncoders.length];
			System.arraycopy(encoders, 0, copy, 0, encoders.length);
			System.arraycopy(newEncoders, 0, copy, encoders.length,
					newEncoders.length);
		}

		encoders = copy;
	}

	@Override
	public synchronized void push(int b) throws IOException {
		addedData = true;
		encoders[encoders.length - 1].push(b);
	}

	@Override
	protected void flush() throws IOException {
		for (int a = encoders.length - 1; a >= 0; a--) {
			encoders[a].close();
		}
	}
}