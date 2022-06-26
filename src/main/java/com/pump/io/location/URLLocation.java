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
package com.pump.io.location;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import com.pump.swing.Cancellable;
import com.pump.util.Receiver;

/**
 * An <code>IOLocation</code> that is associated with a
 * <code>java.net.URL</code>.
 * 
 * <p>
 * These can only be instantiated by a <code>LocationFactory</code>.
 * 
 * @see com.pump.io.location.LocationFactory
 */
public class URLLocation extends CachedLocation {
	IOLocation parent;
	final URL url;

	/**
	 * This object should be constructed via the <code>LocationFactory</code>.
	 */
	protected URLLocation(URL url) {
		this.url = url;
	}

	public void setParent(IOLocation parent) {
		this.parent = parent;
	}

	@Override
	protected void doListChildren(Receiver<IOLocation> receiver,
			Cancellable cancellable) {
	}

	@Override
	protected String doGetPath() {
		return getURL().getPath();
	}

	@Override
	protected String doGetName() {
		String path = getPath();
		int i = path.lastIndexOf('/');
		return path.substring(i + 1);
	}

	@Override
	protected boolean doCanWrite() {
		return false;
	}

	@Override
	protected boolean doCanRead() {
		return true;
	}

	@Override
	protected boolean doIsDirectory() {
		return false;
	}

	@Override
	protected boolean doIsHidden() {
		return false;
	}

	@Override
	protected boolean doIsAlias() {
		return false;
	}

	@Override
	protected boolean doIsNavigable() {
		return false;
	}

	@Override
	protected boolean doExists() {
		return true;
	}

	@Override
	protected long doLength() throws IOException {
		URLConnection connection = url.openConnection();
		return connection.getContentLength();
	}

	@Override
	protected long doGetModificationDate() {
		try {
			URLConnection connection = url.openConnection();
			return connection.getLastModified();
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public IOLocation getParent() {
		return parent;
	}

	@Override
	public void mkdir() throws IOException, MakeDirectoryException {
		throw new MakeDirectoryException();
	}

	@Override
	public URL getURL() {
		return url;
	}

	@Override
	public IOLocation setName(String s) throws IOException, SetNameException {
		throw new SetNameException();
	}

	@Override
	public InputStream createInputStream() throws IOException {
		return url.openStream();
	}

	@Override
	public OutputStream createOutputStream() throws IOException,
			FileCreationException {
		throw new IOException();
	}

	@Override
	public void delete() throws IOException, DeleteException {
		throw new DeleteException();
	}
}