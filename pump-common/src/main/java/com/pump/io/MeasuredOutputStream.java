/*
 * @(#)MeasuredOutputStream.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2011 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */
package com.pump.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/** This <code>OutputStream</code> passes information along to an underlying
 * <code>OutputStream</code> while counting how many bytes are written.
 * <P>At any point calling <code>getWrittenCount()</code> tells how the amount
 * of data that has been written since this object was constructed.
 * <p>Also you can add a ChangeListener to this stream to be notified every time
 * a specific amount of bytes are written.
 *
 */
public class MeasuredOutputStream extends OutputStream {
	
	private class ListenerInfo {
		final ChangeListener listener;
		final long threshold;
		final boolean consumeExceptions;
		
		long lastUpdate;
		
		ListenerInfo(ChangeListener cl,long threshold,boolean consumeExceptions) {
			listener = cl;
			this.threshold = threshold;
			this.consumeExceptions = consumeExceptions;
			lastUpdate = written;
		}
	}
	
	protected long written = 0;
	OutputStream out;
	private boolean closed = false;
	List<ListenerInfo> listeners = new ArrayList<ListenerInfo>();
	
	public MeasuredOutputStream(OutputStream out) {
		this.out = out;
	}
	
	/**
	 * 
	 * @param l the listener to add
	 * @param interval the amount of bytes to write before notifying
	 * the listener. Must be 1 or greater. The listener will be notified
	 * each time this threshold is crossed, or when this stream
	 * is closed.
	 * @param consumeExceptions is true, then exceptions that occur
	 * while calling ChangeListener.stateChanged(..) are printed to
	 * the console. If false: they are allowed to ripple outward
	 * and interrupt calls to OutputStream.write(..) or OutputStream.close().
	 */
	public void addChangeListener(ChangeListener l,long interval,boolean consumeExceptions) {
		if(!(interval>=1))
			throw new IllegalArgumentException("the listener interval ("+interval+") must be 1 or greater");
		if(l==null)
			throw new NullPointerException();
	
		synchronized(listeners) {
			for(int a = 0; a<listeners.size(); a++) {
				ListenerInfo i = listeners.get(a);
				if(i.listener==l) {
					throw new IllegalArgumentException("this listener has already been added");
				}
			}
			listeners.add(new ListenerInfo(l, interval, consumeExceptions));
		}
	}
	
	public void removeChangeListener(ChangeListener l) {
		synchronized(listeners) {
			for(int a = 0; a<listeners.size(); a++) {
				ListenerInfo i = listeners.get(a);
				if(i.listener==l) {
					listeners.remove(a);
					return;
				}
			}
		}
	}
	
	protected void fireListeners(boolean skipThreshold) {
		for(int a = 0; a<listeners.size(); a++) {
			ListenerInfo i = listeners.get(a);
			long diff = written - i.lastUpdate;
			if(diff>i.threshold || skipThreshold) {
				i.lastUpdate = written;
				if(i.consumeExceptions) { 
					try {
						i.listener.stateChanged(new ChangeEvent(this));
					} catch(Exception e) {
						e.printStackTrace();
					}
				} else {
					i.listener.stateChanged(new ChangeEvent(this));
				}
			}
		}
	}

	/** Returns the number of bytes written since this object was constructed.
	 *  
	 * @return the number of bytes written since this object was constructed.
	 */
	public long getBytesWritten() {
		return written;
	}
	
	@Override
	public void close() throws IOException {
		out.close();
		closed = true;
		fireListeners(true);
	}
	
	public boolean isClosed() {
		return closed;
	}

	@Override
	public void flush() throws IOException {
		out.flush();
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if(closed) throw new IOException("This OutputStream has already been closed.");
		written+=len;
		out.write(b, off, len);
		fireListeners(false);
	}

	@Override
	public void write(byte[] b) throws IOException {
		write(b,0,b.length);
	}

	@Override
	public void write(int b) throws IOException {
		if(closed) throw new IOException("This OutputStream has already been closed.");
		written++;
		out.write(b);
		fireListeners(false);
	}
}
