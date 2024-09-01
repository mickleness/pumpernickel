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
package com.pump.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This <code>OutputStream</code> passes information along to an underlying
 * <code>OutputStream</code> while counting how many bytes are written.
 * <P>
 * At any point calling <code>getWrittenCount()</code> tells how the amount of
 * data that has been written since this object was constructed.
 * <p>
 * Also you can add a ChangeListener to this stream to be notified every time a
 * specific amount of bytes are written.
 *
 */
public class MeasuredOutputStream extends OutputStream {
	// TODO: should this extend FilteredOutputStream?
	// TODO: merge with GuardedOutputStream?

	private class ListenerInfo {
		final ChangeListener listener;
		final long threshold;
		final boolean consumeExceptions;

		long lastUpdate;

		ListenerInfo(ChangeListener cl, long threshold,
				boolean consumeExceptions) {
			listener = cl;
			this.threshold = threshold;
			this.consumeExceptions = consumeExceptions;
			lastUpdate = written;
		}
	}

	protected long written = 0;
	OutputStream out;
	private boolean closed = false;
	boolean closeable = true;
	List<ListenerInfo> listeners = new ArrayList<ListenerInfo>();

	public MeasuredOutputStream(OutputStream out) {
		this.out = out;
	}

	// TODO: rename this method. Mention closing the parent/underlying stream.
	/**
	 * Control whether calling <code>{@link #close()}</code> affects the
	 * underlying OutputStream. This is useful in cases when you pass an
	 * OutputStream to a 3rd party decoder that helpfully tries to close the
	 * stream as it wraps up, but there is still data to be read later (such as
	 * when working with a ZipInputStream).
	 * 
	 * @param b
	 *            whether calling <code>close()</code> will close the underlying
	 *            OutputStream.
	 */
	public void setCloseable(boolean b) {
		closeable = b;
	}

	public boolean isCloseable() {
		return closeable;
	}

	/**
	 * 
	 * @param l
	 *            the listener to add
	 * @param interval
	 *            the amount of bytes to write before notifying the listener.
	 *            Must be 1 or greater. The listener will be notified each time
	 *            this threshold is crossed, or when this stream is closed.
	 * @param consumeExceptions
	 *            is true, then exceptions that occur while calling
	 *            ChangeListener.stateChanged(..) are printed to the console. If
	 *            false: they are allowed to ripple outward and interrupt calls
	 *            to OutputStream.write(..) or OutputStream.close().
	 */
	public void addChangeListener(ChangeListener l, long interval,
			boolean consumeExceptions) {
		if (!(interval >= 1))
			throw new IllegalArgumentException("the listener interval ("
					+ interval + ") must be 1 or greater");
		if (l == null)
			throw new NullPointerException();

		synchronized (listeners) {
			for (int a = 0; a < listeners.size(); a++) {
				ListenerInfo i = listeners.get(a);
				if (i.listener == l) {
					throw new IllegalArgumentException(
							"this listener has already been added");
				}
			}
			listeners.add(new ListenerInfo(l, interval, consumeExceptions));
		}
	}

	public void removeChangeListener(ChangeListener l) {
		synchronized (listeners) {
			for (int a = 0; a < listeners.size(); a++) {
				ListenerInfo i = listeners.get(a);
				if (i.listener == l) {
					listeners.remove(a);
					return;
				}
			}
		}
	}

	protected void fireListeners(boolean skipThreshold) {
		for (int a = 0; a < listeners.size(); a++) {
			ListenerInfo i = listeners.get(a);
			long diff = written - i.lastUpdate;
			if (diff > i.threshold || skipThreshold) {
				i.lastUpdate = written;
				if (i.consumeExceptions) {
					try {
						i.listener.stateChanged(new ChangeEvent(this));
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					i.listener.stateChanged(new ChangeEvent(this));
				}
			}
		}
	}

	/**
	 * Returns the number of bytes written since this object was constructed.
	 * 
	 * @return the number of bytes written since this object was constructed.
	 */
	public long getBytesWritten() {
		return written;
	}

	@Override
	public void close() throws IOException {
		if (isCloseable())
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
		if (closed)
			throw new IOException("This OutputStream has already been closed.");
		written += len;
		out.write(b, off, len);
		fireListeners(false);
	}

	@Override
	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}

	@Override
	public void write(int b) throws IOException {
		if (closed)
			throw new IOException("This OutputStream has already been closed.");
		written++;
		out.write(b);
		fireListeners(false);
	}
}