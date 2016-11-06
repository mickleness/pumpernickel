/*
 * @(#)FileLocation.java
 *
 * $Date: 2015-05-30 21:16:58 -0400 (Sat, 30 May 2015) $
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
package com.pump.io.location;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.SwingUtilities;

import com.pump.icon.FileIcon;
import com.pump.io.IOUtils;
import com.pump.swing.Cancellable;
import com.pump.util.Receiver;

/** An <code>IOLocation</code> that is associated with a <code>java.io.File</code>.
 * 
 * <p>These can only be instantiated by a <code>LocationFactory</code>.
 * 
 * @see com.bric.io.location.LocationFactory
 */
public class FileLocation extends CachedLocation {
	
	protected final File file;
	protected FileLocation(File f) {
		if(f==null) throw new NullPointerException();
		File choice;
		try {
			choice = f.getCanonicalFile();
			if(choice==null) {
				throw new NullPointerException();
			}
		} catch (IOException e) {
			choice = f;
		}
		file = choice;
		if(file==null) {
			throw new NullPointerException();
		}
	}
	
	@Override
	protected String doGetPath() {
		return file.toURI().toString();
	}
	
	@Override
	public String getParentPath() {
		String path = file.toURI().toString();
		if(path.endsWith("/"))
			path = path.substring(0,path.length()-1);
		int i = path.lastIndexOf('/');
		if(i==-1) return null;
		
		String returnValue = path.substring(0,i);
		if(returnValue.equals("file:"))
			return null;
		return path.substring(0,i);
	}
	
	public File getFile() {
		return file;
	}
	
	@Override
	protected boolean doCanWrite() {
		boolean returnValue = file.canWrite();
		if(file.isDirectory()) {	
			//the following is not safe in a sandbox
			File[] children = file.listFiles();
			if(children==null)
				returnValue = false;
		}
		return returnValue;
	}

	@Override
	protected boolean doCanRead() {
		boolean returnValue = file.canRead();
		if(file.isDirectory()) {	
			//the following is not safe in a sandbox
			File[] children = file.listFiles();
			if(children==null)
				returnValue = false;
		}
		return returnValue;
	}

	@Override
	protected long doGetModificationDate() {
		return file.lastModified();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj!=null && obj.getClass().equals(this.getClass())) {
			FileLocation fl = (FileLocation)obj;
			return fl.file.equals(file);
		}
		return false;
	}

	@Override
	public IOLocation getChild(String name) throws IOException {
		IOLocation returnValue = super.getChild(name);
		if(returnValue!=null) return returnValue;
		
		File newFile = new File(file,name);
		return LocationFactory.get().create(newFile);
	}

	@Override
	public void mkdir() throws MakeDirectoryException {
		try {
			if(file.mkdir()==false) {
				if(file.exists() && file.isDirectory()) {
					//what the heck?  mkdir returns false but the operation worked...?
					return;
				}
				//hey, I wish I knew more details too. Don't blame me.
				throw new MakeDirectoryException("could not create directory \""+getURL()+"\"");
			}
		} finally {
			flush();
		}
	}

	@Override
	public InputStream createInputStream() throws IOException {
		return new FileInputStream(file);
	}

	/** Returns the parent of a File.
	 * <P>The method <code>java.io.File.getParentFile()</code> can
	 * mysteriously return <code>null</code> on Vista for files
	 * that have a very real parent.  (This was first discovered for
	 * "C:\Users\jeremy\workspace\Tech4Learning\Tech4Learning.pref".)
	 * @param f
	 * @return
	 */
	private static File getParentFile(File f) {
		File parent = f.getParentFile();
		if(parent!=null) {
			return parent;
		}

		String path = f.getAbsolutePath();
		int i = path.lastIndexOf(File.separator);
		if(i==0) {
			return null;
		} else if(i!=-1) {
			path = path.substring(0,i);
			File returnValue = new File(path);
			
			/** On Vista if you ask for the parent of "C:\" then
			 * the code above will create a file for "C:".
			 * But if you call getAbsolutePath() on this file, it will
			 * return your classpath.  Sooo... here's an attempted
			 * workaround for this case:
			 */
			if(returnValue.getPath().equals(returnValue.getAbsolutePath())==false) {
				//A lack of a separator says we're pretty near the root level anyway, right?
				if(path.indexOf(File.separator)==-1)
					return null;
			}
			return returnValue;
		}
		return null;
	}

	@Override
	public OutputStream createOutputStream() throws MissingParentException, FileCreationException, IOException {
		File parent = getParentFile(file);
		if(parent!=null) {
			parent.mkdirs();
		}
		if(parent.exists()==false)
			throw new MissingParentException();
		if(file.exists()==false && file.createNewFile()==false)
			throw new FileCreationException("the file \""+getPath()+"\" could not be created");
		if(file.exists()==false)
			throw new FileCreationException("the file \""+getPath()+"\" did not exist");
		flush();
		return new FileOutputStream(file);
	}

	@Override
	protected String doGetName() {
		String name = file.getName();
		if(name.equals("") && file.getAbsolutePath().equals("/")) {
			//special case for UNIX/Macs:
			File v = new File("/Volumes/");
			File[] volumes = v.listFiles();
			try {
				for(int a = 0; volumes!=null && a<volumes.length; a++) {
					if(volumes[a].getCanonicalFile().equals(file)) {
						return volumes[a].getName();
					}
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		return name;
	}

	@Override
	public IOLocation getParent() {
		File parent = getParentFile(file);
		if(parent!=null) {
			return LocationFactory.get().create(parent);
		}
		return null;
	}

	@Override
	protected boolean doIsDirectory() {
		return file.isDirectory();
	}

	@Override
	protected boolean doIsNavigable() {
		return isDefaultNavigable(this);
	}

	public Icon getIcon() {
		final Icon[] iconWrapper = new Icon[] { null };
		final Throwable[] errorWrapper = new Throwable[] { null };
		Runnable runnable = new Runnable() {
			public void run() {
				try {
					iconWrapper[0] = FileIcon.getIcon(file);
				} catch(Throwable t) {
					errorWrapper[0] = t;
				} finally {
					synchronized(iconWrapper) {
						iconWrapper.notify();
					}
				}
			}
		};
		if(SwingUtilities.isEventDispatchThread()) {
			runnable.run();
		} else {
			SwingUtilities.invokeLater(runnable);
			while(iconWrapper[0]==null && errorWrapper[0]==null) {
				synchronized(iconWrapper) {
					try {
						iconWrapper.wait();
					} catch (InterruptedException e) {}
				}
			}
		}
		if(errorWrapper[0] instanceof RuntimeException) {
			throw (RuntimeException)errorWrapper[0];
		} else if(errorWrapper[0] instanceof Error) {
			throw (Error)errorWrapper[0];
		}
		return iconWrapper[0];
	}

	@Override
	protected void doListChildren(Receiver<IOLocation> receiver,Cancellable cancellable) {
		File[] files = file.listFiles();

		if(files==null)
			files = new File[] {};

		for(int a = 0; a<files.length; a++) {
			if(cancellable!=null && cancellable.isCancelled())
				return;

			IOLocation loc = LocationFactory.get().create(files[a]);
			receiver.add(loc);
		}
	}

	@Override
	protected boolean doIsHidden() {
		return file.isHidden();
	}

	@Override
	public IOLocation setName(String s) throws SetNameException {
		try {
			File dest = new File(getParentFile(file), s);
			if(file.renameTo(dest))
				return LocationFactory.get().create(dest);
			throw new SetNameException("renaming \""+file.getAbsolutePath()+"\" failed");
		} finally {
			flush();
		}
	}

	@Override
	public void delete() throws IOException {
		try {
			file.delete();
			if(file.exists())
				throw new DeleteException("the file \""+getPath()+"\" still exists");
		} finally {
			flush();
		}
	}

	@Override
	protected boolean doExists() {
		return file.exists();
	}

	@Override
	protected long doLength() {
		return file.length();
	}

	@Override
	public int hashCode() {
		return file.hashCode();
	}

	@Override
	public String toString() {
		return "FileLocation[ path = \""+file.getAbsolutePath()+"\" ]";
	}

	@Override
	protected boolean doIsAlias() {
		return IOUtils.isAlias(file);
	}


	public URL getURL() {
		try {
			//Mike assures me that by calling toURI().toURL() we're encoding spaces correctly automatically
			return file.toURI().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
