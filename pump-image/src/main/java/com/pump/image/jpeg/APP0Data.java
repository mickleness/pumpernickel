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
import java.io.IOException;

class APP0Data {
	protected static final int UNITS_NONE = 0;
	protected static final int UNITS_DOTS_PER_INCH = 1;
	protected static final int UNITS_DOTS_PER_CM = 2;

	int versionMajor;
	int versionMinor;
	int units;
	int horizontalDensity, verticalDensity;
	int thumbnailWidth, thumbnailHeight;
	BufferedImage thumbnail;
	
	APP0Data(JPEGMarkerInputStream in,boolean storeThumbnail) throws IOException {
		//TODO: also support JFXX ("JFIF Extention")
		//http://en.wikipedia.org/wiki/JPEG_File_Interchange_Format
		//the problem is: I can't find a single file that uses this.
		byte[] array = new byte[9];
		if(in.readFully(array, 5)!=5)
			throw new IOException("APP0 expected to begin with \"JFIF_\".");
		if(array[0]!=74 || array[1]!=70 || array[2]!=73 || array[3]!=70 || array[4]!=0)
			throw new IOException("APP0 expected to begin with \"JFIF_\".");
		if(in.readFully(array, 9)!=9) {
			throw new IOException("APP0 expected to at least 9 bytes of data.");
		}
		setVersionMajor( array[0] & 0xff);
		setVersionMinor( array[1] & 0xff);
		setUnits( array[2] & 0xff );
		setHorizontalDensity( ((array[3] & 0xff) << 16) + (array[4] & 0xff) );
		setVerticalDensity( ((array[5] & 0xff) << 16) + (array[6] & 0xff) );
		setThumbnailWidth( array[7] & 0xff );
		setThumbnailHeight( array[8] & 0xff );
		if( thumbnailWidth*thumbnailHeight>0 && storeThumbnail) {
			//TODO: test this.  I haven't found a single file that uses
			//an APP0 thumbnail, so this code has never been tested.
			byte[] dataByte = new byte[ thumbnailWidth*3 ];
			int[] dataInt = new int[ thumbnailWidth ];
			in.readFully(dataByte, dataByte.length);
			BufferedImage image = new BufferedImage(getThumbnailWidth(), getThumbnailHeight(), BufferedImage.TYPE_INT_RGB);
			for(int y = 0; y<thumbnailHeight; y++) {
				for(int x = 0; x<thumbnailWidth; x++) {
					int r = (dataByte[x*3] & 0xff);
					int g = (dataByte[x*3+1] & 0xff);
					int b = (dataByte[x*3+2] & 0xff);
					dataInt[x] = (r << 16) + (g << 8) + (b);
				}
				image.getRaster().setDataElements(0, y, thumbnailWidth, 1, array);
			}
			setThumbnail(image);
		}
	}
	
	public int getVersionMajor() {
		return versionMajor;
	}

	public void setVersionMajor(int versionMajor) {
		this.versionMajor = versionMajor;
	}

	public int getVersionMinor() {
		return versionMinor;
	}

	public void setVersionMinor(int versionMinor) {
		this.versionMinor = versionMinor;
	}

	public int getUnits() {
		return units;
	}

	public void setUnits(int units) {
		this.units = units;
	}

	public int getHorizontalDensity() {
		return horizontalDensity;
	}

	public void setHorizontalDensity(int horizontalDensity) {
		this.horizontalDensity = horizontalDensity;
	}

	public int getVerticalDensity() {
		return verticalDensity;
	}

	public void setVerticalDensity(int verticalDensity) {
		this.verticalDensity = verticalDensity;
	}

	public int getThumbnailWidth() {
		return thumbnailWidth;
	}

	public void setThumbnailWidth(int thumbnailWidth) {
		this.thumbnailWidth = thumbnailWidth;
	}

	public int getThumbnailHeight() {
		return thumbnailHeight;
	}

	public void setThumbnailHeight(int thumbnailHeight) {
		this.thumbnailHeight = thumbnailHeight;
	}

	/** This returns the thumbnail found in this APP0 block if it exists.
	 * 
	 */
	public BufferedImage getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(BufferedImage thumbnail) {
		this.thumbnail = thumbnail;
		setThumbnailWidth( thumbnail.getWidth() );
		setThumbnailHeight( thumbnail.getHeight() );
	}
}