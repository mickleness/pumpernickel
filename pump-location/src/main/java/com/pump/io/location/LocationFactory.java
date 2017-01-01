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
package com.pump.io.location;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.pump.util.JVM;

/** A factory for common <code>IOLocations</code>.
 */
public class LocationFactory {
	
	private static LocationFactory factory = new LocationFactory();
	
	public synchronized static LocationFactory get() {
		return factory;
	}
	
	public synchronized static void set(LocationFactory f) {
		if(f==null)
			throw new NullPointerException();
		factory = f;
	}
	
	protected boolean zipNavigation = true;
	protected boolean tarNavigation = true;
	protected SuffixIOLocationFilter zipFilter = new SuffixIOLocationFilter(false, false, true, "jar", "zip");
	protected SuffixIOLocationFilter tarFilter = new SuffixIOLocationFilter(false, false, true, "tar");
	
	public boolean isZipArchiveNavigable() {
		return zipNavigation;
	}

	public boolean isTarArchiveNavigable() {
		return tarNavigation;
	}
	
	public void setTarArchiveNavigable(boolean b) {
		tarNavigation = b;
	}
	
	public void setZipArchiveNavigable(boolean b) {
		zipNavigation = b;
	}
	
	/** Create an <code>IOLocation</code> based on a <code>File</code>.
	 * This factory may not return a <code>FileLocation</code>. (For
	 * example: it might return a <code>ZipArchiveLocation</code>, or
	 * a special subclass of <code>FileLocation</code>.)
	 * @param file
	 * @return a new IOLocation based on a File.
	 */
	public IOLocation create(File file) {
		if(file==null) throw new NullPointerException();
		FileLocation fileLoc = createFileLocation(file);
		return filter(fileLoc);
	}
	
	/** Create a <code>FileLocation</code> based on a <code>File</code>. 
	 * This is not recommended unless you explicitly need
	 * a <code>FileLocation</code>.
	 * 
	 * @param file
	 * @return a new FileLocation based on a File.
	 * @see #create(File)
	 */
	public FileLocation createFileLocation(File file) {
		if(file==null) throw new NullPointerException();
		return new FileLocation(file);
	}

	/** Create a <code>URLLocation</code> based on a <code>URL</code>. 
	 * This is not recommended unless you explicitly need
	 * a <code>URLLocation</code>.
	 * 
	 * @param url
	 * @return a new URLLocation based on a URL
	 * @see #create(URL)
	 */
	public URLLocation createURLLocation(URL url) {
		if(url==null) throw new NullPointerException();
		return new URLLocation(url);
	}

	/** @return an <code>IOLocation</code> based on a <code>URL</code>.
	 * This factory may not return a <code>URLLocation</code>. (For
	 * example: it might return a <code>FileLocation</code>, a <code>ZipArchiveLocation</code>, or
	 * a special subclass of <code>URLLocation</code>.)
	 * @param url the url this is based on.
	 */
	public IOLocation create(URL url) {
		if(url==null) throw new NullPointerException();
		URLLocation urlLoc = createURLLocation(url);
		return filter(urlLoc);
	}
	
	public IOLocation[] getRoots() {
		File[] files;
		if(JVM.isMac) {
			files = (new File("/Volumes/")).listFiles();
		} else {
			files = File.listRoots();
		}
		List<IOLocation> list = new ArrayList<IOLocation>(files.length);
		for(File f : files) {
			list.add( create(f) );
		}
		return list.toArray(new IOLocation[list.size()]);
	}
	
	protected IOLocation filter(IOLocation loc) {
		if(loc==null) throw new NullPointerException();
		
		if(loc instanceof URLLocation) {
			String url = loc.getURL().toString();
			//TODO: look at queries/fragments/etc
			if(url.toLowerCase().startsWith("file:")) {
				String path = url.substring("file:".length());
				path = path.replace("/", File.separator);
				//TODO: improve this
				path = path.replace("%20", " ");
				File file = new File(path);
				return create(file);
			}
		}
		/* TODO: consider making public, and let users navigate zips inside zips?
		 * 
		 */
		//TODO: test queries/fragments/etc
		if( zipFilter.filter(loc)!=null && (!(loc instanceof ZipArchiveLocation))) {
			return filter(new ZipArchiveLocation(loc));
		} else if( tarFilter.filter(loc)!=null && (!(loc instanceof TarArchiveLocation))) {
			return filter(new TarArchiveLocation(loc));
		}
		return loc;
	}
}