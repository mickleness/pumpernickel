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
package com.pump.animation.quicktime;

import java.io.IOException;

import com.pump.io.GuardedInputStream;
import com.pump.io.GuardedOutputStream;

public class SampleSizeAtom extends LeafAtom {
	int version = 0;
	int flags = 0;
	long sampleSize = 0;
	long sampleCount;
	long[] sizeTable;
	
	public SampleSizeAtom(int version,int flags,long sampleSize,int sampleCount,long[] table) {
		super(null);
		this.version = version;
		this.flags = flags;
		this.sampleSize = sampleSize;
		this.sampleCount = sampleCount;
		this.sizeTable = table;
	}
	
	public SampleSizeAtom() {
		super(null);
		sizeTable = new long[0];
	}
	
	public SampleSizeAtom(Atom parent,GuardedInputStream in) throws IOException {
		super(parent);
		version = in.read();
		flags = read24Int(in);
		sampleSize = read32Int(in);
		sampleCount = read32Int(in);
		if(in.isAtLimit()==false) {
			sizeTable = new long[(int)sampleCount];
			for(int a = 0; a<sizeTable.length; a++) {
				sizeTable[a] = read32Int(in);
			}
		}
	}
	
	public void setSampleSize(long sampleSize) {
		this.sampleSize = sampleSize;
	}
	
	public void setSampleCount(long sampleCount) {
		this.sampleCount = sampleCount;
	}

	public void setSizeTable(long[] sizeTable) {
		this.sizeTable = sizeTable;
	}
	
	public void addSampleSize(long size) {
		long[] newArray = new long[sizeTable.length+1];
		System.arraycopy(sizeTable,0,newArray,0,sizeTable.length);
		newArray[newArray.length-1] = size;
		sizeTable = newArray;
	}
		
	@Override
	protected String getIdentifier() {
		return "stsz";
	}

	@Override
	protected long getSize() {
		if(sizeTable==null) return 20;
		return 20+sizeTable.length*4;
	}

	@Override
	protected void writeContents(GuardedOutputStream out) throws IOException {
		out.write(version);
		write24Int(out,flags);
		write32Int(out,sampleSize);
		if(sizeTable==null) {
			write32Int(out, sampleCount);
		} else {
			write32Int(out,sizeTable.length);
			for(int a = 0; a<sizeTable.length; a++) {
				write32Int(out,sizeTable[a]);
			}
		}
	}

	@Override
	public String toString() {
		String entriesString;
		if(sizeTable!=null) {
			if(sizeTable.length>50 && ABBREVIATE) {
				entriesString = "[ ... ]";
			} else {
				StringBuffer sb = new StringBuffer();
				sb.append("[ ");
				for(int a = 0; a<sizeTable.length; a++) {
					if(a!=0) {
						sb.append(", ");
					}
					sb.append(sizeTable[a]);
				}
				sb.append(" ]");
				entriesString = sb.toString();
			}
		} else {
			entriesString = "undefined";
		}
		
		return "SampleSizeAtom[ version="+version+", "+
		"flags="+flags+", "+
		"sampleSize="+sampleSize+", sampleCount = "+sampleCount+", "+
		"sizeTable="+entriesString+"]";
	}
}