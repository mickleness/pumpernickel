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
import java.io.InputStream;

/** This is a combination of multiple <code>InputStreams</code>.
 * One the first stream is finished, the next stream is read.
 */
public class CombinedInputStream extends InputStream {
	static class Input {
		InputStream stream;
		boolean closeable;
		
		public Input(InputStream in, boolean closeable) {
			this.stream = in;
			this.closeable = closeable;
		}
	}
	
	Input[] inputs;
	
	public CombinedInputStream(InputStream in1,InputStream in2,boolean close1,boolean close2) {
		this(new InputStream[] { in1, in2 }, new boolean[] { close1, close2 });
	}
	
	public CombinedInputStream(InputStream[] inputStreams,boolean[] close) {
		inputs = new Input[inputStreams.length];
		for(int a = 0; a<inputs.length; a++) {
			inputs[a] = new Input( inputStreams[a], close[a] );
		}
	}

	@Override
	public int available() throws IOException {
		if(inputs.length==0) return 0;
		int avail = inputs[0].stream.available();
		if(avail!=0) {
			return avail;
		}
		removeInput();
		return available();
	}
	
	private void removeInput() {
		try {
			if(inputs[0].closeable) {
				inputs[0].stream.close();
			}
		} catch(IOException e) {
			//tough call:
			//technically this IOException isn't a problem,
			//since we're done with this stream.
			//But really could throw it anyway?
			//When it doubt: go with the more stable option:
			e.printStackTrace();
		}
		Input[] newArray = new Input[inputs.length-1];
		System.arraycopy(inputs, 1, newArray, 0, newArray.length);
		inputs = newArray;
	}

	@Override
	public void close() throws IOException {
		for(int a = 0; a<inputs.length; a++) {
			if(inputs[a].closeable) {
				inputs[a].stream.close();
			}
		}
		inputs = new Input[0];
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	@Override
	public synchronized void reset() throws IOException {}
	
	@Override
	public synchronized void mark(int readlimit) {}


	@Override
	public int read() throws IOException {
		if(inputs.length==0) return -1;
		int k = inputs[0].stream.read();
		if(k!=-1) {
			return k;
		}
		removeInput();
		return read();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if(inputs.length==0) return -1;
		
		int k = inputs[0].stream.read(b, off, len);
		if(k!=-1) {
			return k;
		}
		removeInput();
		return read(b, off, len);
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public long skip(long n) throws IOException {
		if(inputs.length==0) return -1;
		
		long skipped = 0;
		while(skipped<n) {
			long t = inputs[0].stream.skip(n-skipped);
			if(t==-1) {
				removeInput();
				if(inputs.length==0)
					return skipped;
			}
			skipped += t;
		}
		return skipped;
	}
}