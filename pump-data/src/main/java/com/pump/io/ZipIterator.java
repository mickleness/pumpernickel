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

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;

import com.pump.util.CloseableMeasurableIterator;

/**
 * This iterates over data in a ZipInputStream.
 *
 * @param <T>
 */
public abstract class ZipIterator<T> implements CloseableMeasurableIterator<T> {

	DataSource dataSource;
	float progress = -1;
	T nextElement;
	boolean closed = false;

	MeasuredInputStream measuredIn;
	ZipInputStream zipIn;
	Float fileSize;

	public ZipIterator(File file) throws Exception {
		this(new FileDataSource(file));
	}

	public ZipIterator(URL url) throws Exception {
		this(new URLDataSource(url));
	}

	public ZipIterator(DataSource dataSource) throws Exception {
		if (dataSource == null)
			throw new NullPointerException();
		this.dataSource = dataSource;
		initialize();

		measuredIn = new MeasuredInputStream(getDataSource().getInputStream());
		zipIn = new ZipInputStream(measuredIn);
		fileSize = getFileSize();

		queueNext();
	}

	/**
	 * Return the file size of the data being read, or null if that is
	 * indeterminate.
	 * <p>
	 * The default implementation only returns a non-null value if this iterator
	 * interacts with a File or FileDataSource.
	 */
	protected Float getFileSize() {
		DataSource ds = getDataSource();
		if (ds instanceof FileDataSource) {
			return Float.valueOf(((FileDataSource) ds).getFile().length());
		}
		return null;
	}

	@Override
	protected void finalize() {
		close();
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * Convert the data from an InputStream into an element for this iterator.
	 * 
	 * @param inputStream
	 *            the stream to read the data from. Although this stream is
	 *            derived directly from the File/DataSource this iterator
	 *            relates to: it is safe to call <code>close()</code> on it.
	 * @param zipEntry
	 *            the ZipEntry this stream represents.
	 * @return an element parsed from the InputStream.
	 */
	protected abstract T parseZipEntry(InputStream inputStream,
			ZipEntry zipEntry) throws Exception;

	/**
	 * This is an optional hook subclasses can use to do a preprocessing filter
	 * on the incoming data if needed.
	 * 
	 * @param dataSource
	 */
	protected void initialize() {

	}

	private void queueNext() throws Exception {
		if (fileSize != null) {
			progress = measuredIn.getReadBytes() / fileSize.floatValue();
		}
		ZipEntry entry = zipIn.getNextEntry();
		if (entry != null) {
			MeasuredInputStream entryStream = new MeasuredInputStream(zipIn);
			entryStream.setCloseable(false);
			nextElement = parseZipEntry(entryStream, entry);
		} else {
			nextElement = null;
		}
	}

	@Override
	public boolean hasNext() {
		return nextElement != null && !closed;
	}

	@Override
	public T next() {
		T returnValue = nextElement;
		try {
			queueNext();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return returnValue;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() {
		closed = true;
	}

	@Override
	public float getProgress() {
		return progress;
	}
}