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
package com.pump.image.jpeg;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

/** Use this data block when we expect a small thumbnail JPEG
 * is inside a larger block of data we don't know how to parse.
 */
class GenericDataWithThumbnail {

	BufferedImage thumbnail;
	final static byte[] start = new byte[] { (byte)0xff, (byte)0xd8, (byte)0xff};
	final static byte[] end = new byte[] { (byte)0xff, (byte)0xd9};
	
	GenericDataWithThumbnail(JPEGMarkerInputStream in, boolean storeThumbnail) throws IOException {
		//For now the only thing we do here is load this thumbnail, so return
		//immediately if we aren't searching for a graphic:
		if(!storeThumbnail)
			return;
		
		//TODO: if we could understand the format of this data block we wouldn't
		//have to load the entire byte array into memory just to process the thumbnail.
		thumbnail = readJPEG(in);
	}
	
	protected static BufferedImage readJPEG(JPEGMarkerInputStream in) throws IOException {
		byte[] dest = new byte[in.remainingMarkerLength];
		if(in.readFully(dest, dest.length)!=dest.length)
			throw new IOException();
		int startIndex = indexOf(dest, start);
		if(startIndex!=-1) {
			int endIndex = lastIndexOf(dest, end)+end.length;
			if(endIndex!=-1) {
				ByteArrayInputStream imageData = new ByteArrayInputStream(dest, startIndex, endIndex-startIndex);
				return ImageIO.read(imageData);
			}
		}
		return null;
	}
	
	protected static final int indexOf(byte[] searchable,byte[] phrase) {
		boolean match = true;
		for(int a = 0; a<searchable.length-phrase.length; a++) {
			match = true;
			for(int b = 0; b<phrase.length && match; b++) {
				if(searchable[a+b]!=phrase[b])
					match = false;
			}
			if(match)
				return a;
		}
		return -1;
	}
	
	protected static final int lastIndexOf(byte[] searchable,byte[] phrase) {
		boolean match = true;
		for(int a = searchable.length-phrase.length; a>=0; a--) {
			match = true;
			for(int b = 0; b<phrase.length && match; b++) {
				if(searchable[a+b]!=phrase[b])
					match = false;
			}
			if(match)
				return a;
		}
		return -1;
	}

	/** This returns the thumbnail found in this APP1 block if it exists.
	 * 
	 */
	public BufferedImage getThumbnail() {
		return thumbnail;
	}
}