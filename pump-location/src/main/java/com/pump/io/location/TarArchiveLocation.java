/*
 * @(#)TarArchiveLocation.java
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
import java.util.HashMap;
import java.util.Map;

import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

import com.pump.io.GuardedInputStream;
import com.pump.io.IOUtils;
import com.pump.io.MeasuredInputStream;
import com.pump.util.Receiver;

public class TarArchiveLocation extends ArchiveLocation<TarEntry> {
	
	static class EntryLocation {
		final long startPtr;
		final long length;
		EntryLocation(long startPtr,long length) {
			this.startPtr = startPtr;
			this.length = length;
		}
	}
	
	/** The root node will catalog every TarEntry's name to an
	 * <code>EntryLocation</code> for easy retrieval.
	 */
	Map<String, EntryLocation> archiveEntryToLocation;

	public TarArchiveLocation(IOLocation archive) {
		super(archive);
		setParent(archive.getParent());
	}
	
	private TarArchiveLocation(ArchiveLocation<TarEntry> root,TarEntry tarEntry) {
		super(root, tarEntry);
	}
	
	@Override
	public String getArchivePath() {
		TarEntry entry = getArchiveEntry();
		if(entry==null) return null;
		
		return entry.getName();
	}

	@Override
	protected char getSeparatorChar() {
		return '/';
	}

	@Override
	protected void listArchiveEntries(Receiver<TarEntry> receiver) throws IOException {
		archiveEntryToLocation = new HashMap<String, EntryLocation>();
		InputStream in = null;
		try {
			in = archive.createInputStream();
			TarInputStream tarIn = null;
			try {
				tarIn = new TarInputStream(in);
				TarEntry entry = tarIn.getNextEntry();
				
				//the current position in the InputStream:
				long pos = 512; //all TarEntry headers are 512 bytes
				
				while(entry!=null) {
					long entryStartPtr = pos;
					long entryLength = entry.getSize();
					
					EntryLocation loc = new EntryLocation(entryStartPtr, entryLength);
					archiveEntryToLocation.put(entry.getName(), loc);
					receiver.add(entry);
					
					//All the data of a TarEntry is expressed in blocks of 512 bytes, rounded up:
					if(entryLength%512!=0) {
						long d = entryLength/512L;
						entryLength = (d+1)*512L;
					}
					
					pos += entryLength;
					
					entry = tarIn.getNextEntry();
					pos += 512;
				}
			} finally {
				if(tarIn!=null) {
					try {
						tarIn.close();
					} catch(Throwable t) {
						t.printStackTrace();
					}
				}
			}
		} finally {
			if(in!=null) {
				try {
					in.close();
				} catch(Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}
	
	@Override
	protected TarEntry createDirectory(String path) {
		TarEntry e = new TarEntry(path);
		return e;
	}

	@Override
	protected ArchiveLocation<TarEntry> createArchiveLocation(ArchiveLocation<TarEntry> root,TarEntry t) {
		return new TarArchiveLocation(root, t);
	}

	@Override
	public boolean isDirectory(TarEntry entry) {
		return entry.isDirectory();
	}

	@Override
	public long getModificationDate(TarEntry entry) {
		return entry.getModTime().getTime();
	}

	@Override
	public boolean isHidden(TarEntry entry) {
		return false;
	}

	@Override
	public long length(TarEntry entry) {
		return entry.getSize();
	}

	/** Create an InputStream for a TarEntry.
	 * <p>The root TarArchiveLocation catalogs every TarEntry,
	 * so this method provides (very nearly) random access
	 * to entries.
	 */
	@Override
	public InputStream createInputStream(TarEntry entry) throws IOException {
		/* The slow way here would be to create a new TarInputStream
		 * and iterate over all TarEntries until we identified the
		 * correct entry and return that input.
		 * 
		 * ... but since we cataloged this up front, we can skip all that.
		 */
		EntryLocation loc = archiveEntryToLocation.get(entry.getName());
		if(loc==null) throw new IllegalArgumentException("unrecognized tar entry \""+entry.getName()+"\"");

		InputStream in = null;
		try {
			in = root.createInputStream();
			MeasuredInputStream measuredIn = new MeasuredInputStream(in);
			IOUtils.skipFully(measuredIn, loc.startPtr);
			return new GuardedInputStream(measuredIn, loc.length, true);
		} catch(IOException e) {
			if(in!=null) {
				try {
					in.close();
				} catch(IOException e2) {
					e2.printStackTrace();
				}
			}
			throw e;
		}
	}
}
