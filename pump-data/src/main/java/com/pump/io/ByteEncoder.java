package com.pump.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/** This object encodes a series of bytes (expressed as [0,255] integers).
 * <p>This data can be made available either in a dual-threaded push/pull model,
 * or in a single-threaded model by using the inner DataListener interface.
 */
public abstract class ByteEncoder implements AutoCloseable {

	/** This is notified when data is available or when an encoder is closed.
	 * This is is intended for single-threaded encoding.
	 */
	public static interface DataListener {
		public void chunkAvailable(ByteEncoder encoder);
		public void encoderClosed(ByteEncoder encoder);
	}
	
	protected boolean closed = false;
	DataListener listener = null;

	/** Assign a DataListener for this encoder.
	 * 
	 * @param listener the listener to assign. This replaces any
	 * previously assigned listener.
	 */
	public synchronized void setListener(DataListener listener) {
		this.listener = listener;
	}
	
	/** This indicates that no more data will be made available.
	 * <p>Subsequent calls to <code>push()</code> will result in an <code>IllegalStateException</code>.
	 * Calling this method may immediately release new data (which you can access by calling <code>pullImmediately</code>).
	 * 
	 * @see #push(int)
	 * @see #pull()
	 */
	@Override
	public synchronized void close() {
		if(closed)
			return;
		
		flush();
		closed = true;
		if(listener!=null) listener.encoderClosed(this);
		notify();
	}
	
	/** Pull a series of bytes (all within [0,255]) as
	 * they become available.
	 * 
	 * @return encoded bytes, or null if if <code>close()</code> 
	 * has been called.
	 * 
	 * @see #pullImmediately()
	 * @see #push(int)
	 * @see #close()
	 */
	public synchronized int[] pull() {
		while(outgoingData==null && (!closed)) {
			try {
				wait();
			} catch(InterruptedException e) {}
		}
		int[] returnValue = outgoingData;
		outgoingData = null;
		notify();
		return returnValue;
	}
	
	/** Push a byte for this encoder to process.
	 * <p>As data accumulates it will eventually have to be
	 * retrieved via the <code>pull()</code> method. This method
	 * may block if data is waiting to be retrieved.
	 * 
	 * @param b a byte from [0, 255]
	 * 
	 * @see #pull()
	 * @see #close()
	 */
	public abstract void push(int b);
	
	/** This is exclusively called during <code>close()</code> to give this encoder
	 * an opportunity to write any remaining data.
	 * 
	 */
	protected abstract void flush();
	
	private static final int[] EMPTY_ARRAY = new int[] {};
	/** This will return one of three things: an array of
	 * available bytes, an empty array if no data is available,
	 * or null if this encoder has been closed.
	 * 
	 * @return One of three things: an array of
	 * available bytes, an empty array if no data is available,
	 * or null if this encoder has been closed.
	 * 
	 * @see #pull()
	 * @see #push(int)
	 * @see #close()
	 */
	public synchronized int[] pullImmediately() {
		int[] returnValue = outgoingData;
		outgoingData = null;
		if(returnValue==null && (!closed))
			return EMPTY_ARRAY;
		return returnValue;
	}
	
	private int[] outgoingData;
	
	/** Make a chunk of data available to be read.
	 * This method may block until previously pushed chunks are
	 * cleared.
	 * 
	 * @param data a chunk of data that is ready to be pulled.
	 */
	protected void pushChunk(int[] data) {
		while(outgoingData!=null) {
			try {
				wait();
			} catch(InterruptedException e) {}
		}
		outgoingData = data;
		if(listener!=null) listener.chunkAvailable(this);
		notify();
	}

	/** Reencode a String using a ByteEncoder.
	 * 
	 * @param encoder the encoder to use to rewrite a String
	 * @param s the String to reencode
	 * @return the String data after passing through the encoder.
	 */
	protected static String encode(final ByteEncoder encoder,String s) {
		InputStream in = null;
		ByteArrayOutputStream out = null;
		try {
			in = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
			final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			out = bOut;
			
			encoder.setListener(new DataListener() {

				@Override
				public void chunkAvailable(ByteEncoder encoder) {
					int[] array = encoder.pullImmediately();
					for(int a = 0; a<array.length; a++) {
						bOut.write(array[a]);
					}
				}

				@Override
				public void encoderClosed(ByteEncoder encoder) {}
				
			});
			
			int k = in.read();
			while(k!=-1) {
				encoder.push(k);
				k = in.read();
			}
			encoder.close();
			return new String( out.toByteArray(), StandardCharsets.UTF_8 );
		} catch(IOException e) {
			// this is bad. why would we get an IOException if the String is in memory?
			throw new RuntimeException(e);
		} finally {
			try {
				in.close();
			} catch(IOException e) {}
			try {
				out.close();
			} catch(IOException e) {}
		}
	}
}
