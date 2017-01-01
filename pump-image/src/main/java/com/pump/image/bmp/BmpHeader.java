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
package com.pump.image.bmp;

import java.awt.image.IndexColorModel;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/** This contains information in the headers of a BMP file.
 * Technically this reads the "bmp header" and the "bitmap header"
 * at the same time, but I didn't think it was necessary to make
 * 2 separate classes for this.
 */
class BmpHeader {
	public long bitmapOffset;
	public boolean topDown;
	public int width, height, planes, bitsPerPixel;
	public int compression = 0;
	public int horizontalResolution = 12462;
	public int verticalResolution = 12462;
	public IndexColorModel colorModel = null;
	
	BmpHeader(InputStream in) throws IOException {

		 /** Windows 2.x, 3.x, and 4.x BMP files contain four sections: 
		  * 1. a file header
		  * 2. a bitmap information header
		  * 3. an optional color palette
		  * 4. the bitmap data.
		  * 
		  * The BMP file header is 14 bytes in length and is nearly
		  * identical to the 1.x DDB header. The file header is
		  * followed by a second header (called the bitmap header),
		  * a variable-sized palette, and the bitmap data. 
		  */

		//read file header:

		//typedef struct _WinBMPFileHeader
		//{
		//	WORD   FileType;     /* File type, always 4D42h ("BM") */
		//	DWORD  FileSize;     /* Size of the file in bytes */
		//	WORD   Reserved1;    /* Always 0 */
		//	WORD   Reserved2;    /* Always 0 */
		//	DWORD  BitmapOffset; /* Starting position of image data in bytes */
		//} WINBMPFILEHEADER;

		byte[] block = new byte[108];
		
		read(in, block, 14);
		if (!((block[0] & 0xff) == 'B' && (block[1] & 0xff) == 'M')) {
			throw new BmpHeaderException("This does not appear to be a valid BMP image.");
		}
		

		bitmapOffset = ((block[10] & 0xff) << 0) + 
						((block[11] & 0xff) << 8) +
						((block[12] & 0xff) << 16) +
						((block[13] & 0xff) << 24);
		
		//we don't care about anything else in the file header
		
		//now move on to the bitmap header, and possibly the palette:
		read(in, block, 4);
		int bitmapHeaderSize = ((block[0] & 0xff) << 0) + 
						((block[1] & 0xff) << 8) +
						((block[2] & 0xff) << 16) +
						((block[3] & 0xff) << 24);
		bitmapHeaderSize -= 4;
		read(in, block, bitmapHeaderSize);

		
		if(bitmapHeaderSize==8) {
			width = ((block[0] & 0xff) << 0) + ((block[1] & 0xff) << 8);
			height = ((block[2] & 0xff) << 0) + ((block[3] & 0xff) << 8);
			
			/** Planes is the number of color planes used to represent the 
			 * bitmap data. BMP files contain only one color plane, so this 
			 * value is always 1. 
			 */
			planes = ((block[4] & 0xff) << 0) + ((block[5] & 0xff) << 8);
			
			if(planes!=1 && planes!=0)
				System.err.println("warning: unrecognized planes ("+planes+")");
	
			/** BitsPerPixel is the number of bits per pixel in each plane. 
			 * This value will be in the range 1 to 24; the values 1, 4, 8, and 24
				 * are the only values considered legal by the Windows 2.x API.
				 */
			bitsPerPixel = ((block[6] & 0xff) << 0) + ((block[7] & 0xff) << 8);
			
			//so now we covered the 2.x BMP bitmap header.
		} else {
			//the other headers start the same way:
			width = ((block[0] & 0xff) << 0) + 
					((block[1] & 0xff) << 8) +
					((block[2] & 0xff) << 16) +
					((block[3] & 0xff) << 24);

			height = ((block[4] & 0xff) << 0) + 
					((block[5] & 0xff) << 8) +
					((block[6] & 0xff) << 16) +
					((block[7] & 0xff) << 24);
			
			planes = ((block[8] & 0xff) << 0) + ((block[9] & 0xff) << 8);
			bitsPerPixel = ((block[10] & 0xff) << 0) + ((block[11] & 0xff) << 8);

			compression = ((block[12] & 0xff) << 0) + 
							((block[13] & 0xff) << 8) +
							((block[14] & 0xff) << 16) +
							((block[15] & 0xff) << 24);
			@SuppressWarnings("unused")
			int sizeOfBitmap = ((block[16] & 0xff) << 0) + 
							((block[17] & 0xff) << 8) +
							((block[18] & 0xff) << 16) +
							((block[19] & 0xff) << 24);
			horizontalResolution = ((block[20] & 0xff) << 0) + 
							((block[21] & 0xff) << 8) +
							((block[22] & 0xff) << 16) +
							((block[23] & 0xff) << 24);
			verticalResolution = ((block[24] & 0xff) << 0) + 
						((block[25] & 0xff) << 8) +
						((block[26] & 0xff) << 16) +
						((block[27] & 0xff) << 24);
			@SuppressWarnings("unused")
			int colorsUsed = ((block[28] & 0xff) << 0) + 
							((block[29] & 0xff) << 8) +
							((block[30] & 0xff) << 16) +
							((block[31] & 0xff) << 24);
			@SuppressWarnings("unused")
			int colorsImportant = ((block[32] & 0xff) << 0) + 
							((block[33] & 0xff) << 8) +
							((block[34] & 0xff) << 16) +
							((block[35] & 0xff) << 24);
		
			if(bitmapHeaderSize==104) {
				//DWORD RedMask;       /* Mask identifying bits of red component */
				//DWORD GreenMask;     /* Mask identifying bits of green component */
				//DWORD BlueMask;      /* Mask identifying bits of blue component */
				//DWORD AlphaMask;     /* Mask identifying bits of alpha component */
				//DWORD CSType;        /* Color space type */
				//LONG  RedX;          /* X coordinate of red endpoint */
				//LONG  RedY;          /* Y coordinate of red endpoint */
				//LONG  RedZ;          /* Z coordinate of red endpoint */
				//LONG  GreenX;        /* X coordinate of green endpoint */
				//LONG  GreenY;        /* Y coordinate of green endpoint */
				//LONG  GreenZ;        /* Z coordinate of green endpoint */
				//LONG  BlueX;         /* X coordinate of blue endpoint */
				//LONG  BlueY;         /* Y coordinate of blue endpoint */
				//LONG  BlueZ;         /* Z coordinate of blue endpoint */
				//DWORD GammaRed;      /* Gamma red coordinate scale value */
				//DWORD GammaGreen;    /* Gamma green coordinate scale value */
				//DWORD GammaBlue;     /* Gamma blue coordinate scale value */

			}
		}

		/** "Width and Height are the width and height of the image in 
		 * pixels, respectively. If Height is a positive number, then 
		 * the image is a "bottom-up" bitmap with the origin in the 
		 * lower-left corner. If Height is a negative number, then 
		 * the image is a "top-down" bitmap with the origin in the 
		 * upper-left corner. Width does not include any scan-line boundary padding."
		 * 
		 * TODO: test examples of negative heights?  Anybody have any?
		 */
		if(height<0) {
			height = -height;
			topDown = true;
		} else {
			topDown = false;
		}
		
		if(bitsPerPixel<=8) {
			//this means a color palette must be present
			int entries = 1 << bitsPerPixel;
			byte[] palette;
			if(bitmapHeaderSize==8) {
				palette = new byte[entries*3];
				read(in,palette,palette.length);
				byte swap;
				for(int a = 0; a<entries; a++) {
					swap = palette[3*a];
					palette[3*a] = palette[3*a+2];
					palette[3*a+2] = swap;
				}
			} else {
				palette = new byte[entries*4];
				read(in,palette,palette.length);
				byte[] realPalette = new byte[entries*3];
				for(int a = 0; a<entries; a++) {
					realPalette[3*a+2] = palette[4*a];
					realPalette[3*a+1] = palette[4*a+1];
					realPalette[3*a] = palette[4*a+2];
				}
				palette = realPalette;
			}
			
		    colorModel = new IndexColorModel(bitsPerPixel, entries, palette, 0, false);
		}
	}


	private static void read(InputStream in, byte[] dest, int length)
			throws IOException {
		int k = 0;
		while (k < length) {
			int read = in.read(dest, k, length - k);
			if (read == -1)
				throw new EOFException();
			k += read;
		}
	}
}