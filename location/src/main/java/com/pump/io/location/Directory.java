/*
 * @(#)Directory.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2013 by Jeremy Wood.
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
package com.pump.io.location;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import com.pump.swing.BasicCancellable;
import com.pump.util.Receiver;

public class Directory extends IOLocation {
	IOLocation parent;
	IOLocation[] children;
	String name = "";
	String path = "";
	
	public Directory(IOLocation[] children) {
		this.children = clone(children);
		for(int a = 0; a<children.length; a++) {
			if(children[a]==null) throw new NullPointerException();
		}
	}
	
	protected static IOLocation[] clone(IOLocation[] array) {
		IOLocation[] copy = new IOLocation[array.length];
		System.arraycopy(array,0,copy,0,array.length);
		return copy;
	}
	
	public void setParent(IOLocation parent) {
		this.parent = parent;
	}
	
	public void setPath(String newPath) {
		if(newPath==null) throw new NullPointerException();
		path = newPath;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canWrite() {
		return false;
	}

	@Override
	public boolean isDirectory() {
		return true;
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
		return null;
	}

	@Override
	public IOLocation[] listChildren(Receiver<IOLocation> receiver,
			BasicCancellable cancellable) {
		receiver.add(children);
		return clone(children);
	}

	@Override
	public IOLocation setName(String s) throws IOException, SetNameException {
		if(s==null) throw new NullPointerException();
		this.name = s;
		return this;
	}

	@Override
	public InputStream createInputStream() throws IOException {
		throw new IOException();
	}

	@Override
	public OutputStream createOutputStream() throws IOException,
			FileCreationException {
		throw new IOException();
	}

	@Override
	public long length() throws IOException {
		return 0;
	}

	@Override
	public void delete() throws IOException, DeleteException {
		throw new DeleteException();
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public long getModificationDate() throws IOException {
		return -1;
	}

	@Override
	public boolean isHidden() {
		return false;
	}

	@Override
	public boolean canRead() {
		return true;
	}
}
