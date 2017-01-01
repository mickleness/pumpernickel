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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

class ImageFileDirectory {
	static Map<Integer, String> TYPE_LUT = new HashMap<Integer, String>();
	static {
		TYPE_LUT.put(new Integer(256),"Image Width");
		TYPE_LUT.put(new Integer(257),"Image Length");
		TYPE_LUT.put(new Integer(258),"BitsPerSample");
		TYPE_LUT.put(new Integer(259),"Compression");
		TYPE_LUT.put(new Integer(262),"PhotometricInterpretation");
		TYPE_LUT.put(new Integer(270),"ImageDescription");
		TYPE_LUT.put(new Integer(271),"Make");
		TYPE_LUT.put(new Integer(272),"Model");
		TYPE_LUT.put(new Integer(273),"StripOffsets");
		TYPE_LUT.put(new Integer(274),"Orientation");
		TYPE_LUT.put(new Integer(277),"SamplesPerPixel");
		TYPE_LUT.put(new Integer(278),"RowsPerStrip");
		TYPE_LUT.put(new Integer(279),"StripByteCounts");
		TYPE_LUT.put(new Integer(282),"XResolution");
		TYPE_LUT.put(new Integer(283),"YResolution");
		TYPE_LUT.put(new Integer(284),"PlanarConfiguration");
		TYPE_LUT.put(new Integer(296),"ResolutionUnit");
		TYPE_LUT.put(new Integer(301),"TranserFunction");
		TYPE_LUT.put(new Integer(305),"Software");
		TYPE_LUT.put(new Integer(306),"DateTime");
		TYPE_LUT.put(new Integer(315),"Artist");
		TYPE_LUT.put(new Integer(318),"WhitePoint");
		TYPE_LUT.put(new Integer(319),"PrimaryChromaticities");
		TYPE_LUT.put(new Integer(513),"JPEGInterchangeFormat");
		TYPE_LUT.put(new Integer(514),"JPEGInterchangeFormatLength");
		TYPE_LUT.put(new Integer(529),"YCbCrCoefficients");
		TYPE_LUT.put(new Integer(530),"YCbCrSubSampling");
		TYPE_LUT.put(new Integer(531),"YCbCrPositioning");
		TYPE_LUT.put(new Integer(532),"ReferenceBlackWhite");
		TYPE_LUT.put(new Integer(33432),"Copyright");
		TYPE_LUT.put(new Integer(34665),"Exif IFD Pointer");
		TYPE_LUT.put(new Integer(34853),"GPSInfo IFD Pointer");
		TYPE_LUT.put(new Integer(33434),"ExposureTime");
		TYPE_LUT.put(new Integer(33437),"FNumber");
		TYPE_LUT.put(new Integer(34850),"ExposureProgram");
		TYPE_LUT.put(new Integer(34852),"SpectralSensitivity");
		TYPE_LUT.put(new Integer(34855),"ISOSpeedRatings");
		TYPE_LUT.put(new Integer(34856),"OECF");
		TYPE_LUT.put(new Integer(36864),"ExifVersion");
		TYPE_LUT.put(new Integer(36867),"DateTimeOriginal");
		TYPE_LUT.put(new Integer(36868),"DateTimeDigitized");
		TYPE_LUT.put(new Integer(37121),"ComponentsConfiguration");
		TYPE_LUT.put(new Integer(37122),"CompressedBitsPerPixel");
		TYPE_LUT.put(new Integer(37377),"ShutterSpeedValue");
		TYPE_LUT.put(new Integer(37378),"ApertureValue");
		TYPE_LUT.put(new Integer(37379),"BrightnessValue");
		TYPE_LUT.put(new Integer(37380),"ExposureBiasValue");
		TYPE_LUT.put(new Integer(37381),"MaxApertureValue");
		TYPE_LUT.put(new Integer(37382),"SubjectDistance");
		TYPE_LUT.put(new Integer(37383),"MeteringMode");
		TYPE_LUT.put(new Integer(37384),"LightSource");
		TYPE_LUT.put(new Integer(37385),"Flash");
		TYPE_LUT.put(new Integer(37386),"FocalLength");
		TYPE_LUT.put(new Integer(37396),"SubjectArea");
		TYPE_LUT.put(new Integer(37500),"MakerNote");
		TYPE_LUT.put(new Integer(37510),"UserComment");
		TYPE_LUT.put(new Integer(37520),"SubSecTime");
		TYPE_LUT.put(new Integer(37521),"SubSecTimeOriginal");
		TYPE_LUT.put(new Integer(37522),"SubSecTimeDigitized");

		TYPE_LUT.put(new Integer(40960),"FlashpixVersion");
		TYPE_LUT.put(new Integer(40961),"ColorSpace");
		TYPE_LUT.put(new Integer(40962),"PixelXDimension");
		TYPE_LUT.put(new Integer(40963),"PixelYDimension");
		TYPE_LUT.put(new Integer(40964),"RelatedSoundFile");
		TYPE_LUT.put(new Integer(40965),"Interoperability IFD Pointer");
		TYPE_LUT.put(new Integer(41483),"FlashEnergy");
		TYPE_LUT.put(new Integer(41484),"SpatialFrequencyResponse");
		TYPE_LUT.put(new Integer(41486),"FocalPlaneXResolution");
		TYPE_LUT.put(new Integer(41487),"FocalPlaneYResolution");
		TYPE_LUT.put(new Integer(41488),"FocalPlaneResolutionUnit");
		TYPE_LUT.put(new Integer(41492),"SubjectLocation");
		TYPE_LUT.put(new Integer(41493),"ExposureIndex");
		TYPE_LUT.put(new Integer(41495),"SensingMethod");
		TYPE_LUT.put(new Integer(41728),"FileSource");
		TYPE_LUT.put(new Integer(41729),"SceneType");
		TYPE_LUT.put(new Integer(41730),"CFAPattern");
		TYPE_LUT.put(new Integer(41985),"CustomRendered");
		TYPE_LUT.put(new Integer(41986),"ExposureMode");
		TYPE_LUT.put(new Integer(41987),"WhiteBalance");
		TYPE_LUT.put(new Integer(41988),"DigitalZoomRatio");
		TYPE_LUT.put(new Integer(41989),"FocalLengthIn35mmFilm");
		TYPE_LUT.put(new Integer(41990),"SceneCaptureType");
		TYPE_LUT.put(new Integer(41991),"GainControl");
		TYPE_LUT.put(new Integer(41992),"Contrast");
		TYPE_LUT.put(new Integer(41993),"Saturation");
		TYPE_LUT.put(new Integer(41994),"Sharpness");
		TYPE_LUT.put(new Integer(41995),"DeviceSettingDescription");
		TYPE_LUT.put(new Integer(41996),"SubjectDistanceRange");
		TYPE_LUT.put(new Integer(42016),"ImageUniqueID");
	}
	
	static class DirectoryEntry {
		int tagNumber;
		
		int dataFormat;
		
		int componentCount;
		
		/** This is a 4-byte value that either:
		 * contains the data of this entry OR
		 * points to the offset after the TIFF
		 * header when we should read this data
		 * if it is more than 4 bytes long.
		 */
		byte[] fieldValue;
		Object value;
		
		DirectoryEntry(InputStream in,boolean reverse) throws IOException {
			byte[] array = new byte[4];
			
			if(JPEGMarkerInputStream.readFully(in, array, 2, reverse)!=2)
				throw new IOException();
			tagNumber = (array[0] & 0xff)*256 + (array[1] & 0xff);

			if(JPEGMarkerInputStream.readFully(in, array, 2, reverse)!=2)
				throw new IOException();
			dataFormat = (array[0] & 0xff)*256 + (array[1] & 0xff);

			if(JPEGMarkerInputStream.readFully(in, array, 4, reverse)!=4)
				throw new IOException();
			componentCount = ((array[0] & 0xff) << 24) + ((array[1] & 0xff) << 16) +
							((array[2] & 0xff) << 8) + ((array[3] & 0xff) << 0);

			fieldValue = new byte[4];
			if(JPEGMarkerInputStream.readFully(in, fieldValue, 4, reverse)!=4)
				throw new IOException();
		}
		
		String getPropertyName() {
			Integer key = new Integer(tagNumber);
			String propertyName = (String)TYPE_LUT.get(key);
			if(propertyName!=null) return propertyName;
			return null;
		}
		
		private int getValueByteLength() throws IOException {
			int bytesPerComponent;
			switch(dataFormat) {

			case 1 : //byte
				bytesPerComponent = 1;
				break;
			case 2 : //ASCII
				bytesPerComponent = 1;
				break;
			case 3 : //short
				bytesPerComponent = 2;
				break;
			case 4 : //long
				bytesPerComponent = 4;
				break;
			case 5 : //rational
				bytesPerComponent = 8;
				break;
			case 6 : //signed byte
				bytesPerComponent = 1;
				break;
			case 7 : //undefined
				bytesPerComponent = 8;
				break;
			case 8 : //signed short
				bytesPerComponent = 2;
				break;
			case 9 : //signed long
				bytesPerComponent = 8;
				break;
			case 10 : //signed rational
				bytesPerComponent = 8;
				break;
			case 0: //unknown, but this occurred in some JPEGs on my hard disk
				return 0;
			default :
				throw new IOException("unrecognized data type ("+dataFormat+")");
			}
			return bytesPerComponent*componentCount;
		}
		
		/** Returns a 1-byte unsigned int.
		 * (The names of these "read" values are based on the exif specs,
		 * not on Java's definition of primitives.)
		 */
		int readByte(byte[] data,int offset) {
			return ( data[offset] & 0xff );
		}
		
		/** Returns a 1-byte signed int.
		 * (The names of these "read" values are based on the exif specs,
		 * not on Java's definition of primitives.)
		 */
		int readSignedByte(byte[] data,int offset) {
			return ( data[offset] );
		}

		/** Returns a 2-byte unsigned int.
		 * (The names of these "read" values are based on the exif specs,
		 * not on Java's definition of primitives.)
		 */
		int readShort(byte[] data,int offset) {
			return ( ((data[offset] & 0xff) << 8) + ((data[offset+1] & 0xff) << 0) );
		}

		/** Returns a 2-byte signed int.
		 * (The names of these "read" values are based on the exif specs,
		 * not on Java's definition of primitives.)
		 */
		int readSignedShort(byte[] data,int offset) {
			//TODO: implement two's complement here.
			return ( ((data[offset] & 0xff) << 8) + ((data[offset+1] & 0xff) << 0) );
		}

		/** Returns a 4-byte unsigned int.
		 * (The names of these "read" values are based on the exif specs,
		 * not on Java's definition of primitives.)
		 */
		int readLong(byte[] data,int offset) {
			return ( ((data[offset+0] & 0xff) << 24) + ((data[offset+1] & 0xff) << 16) +
					((data[offset+2] & 0xff) << 8) + ((data[offset+3] & 0xff) << 0) );
		}

		/** Returns a 4-byte signed int.
		 * (The names of these "read" values are based on the exif specs,
		 * not on Java's definition of primitives.)
		 */
		int readSignedLong(byte[] data,int offset) {
			//TODO: implement two's complement here.
			return ( ((data[offset+0] & 0xff) << 24) + ((data[offset+1] & 0xff) << 16) +
					((data[offset+2] & 0xff) << 8) + ((data[offset+3] & 0xff) << 0) );
		}
		
		/** Returns the position after the TIFF header where this data
		 * resides, or -1 if the data is actually contained in the 4-byte
		 * value field already retrieved.
		 */
		int positionAfterTIFFHeader() throws IOException {
			int byteLength = getValueByteLength();
			if(byteLength>4) {
				int positionAfterTIFFHeader = readLong(fieldValue, 0);
				return positionAfterTIFFHeader;
			}
			return -1;
		}

		/**
		 * 
		 * @param in a BufferedInputStream that was last marked at the start of
		 * the TIFF header.
		 * @throws IOException
		 */
		void resolveValue(BufferedInputStream in) throws IOException {
			int byteLength = getValueByteLength();
			byte[] data;
			if(byteLength>4) {
				//we have to read this data:
				byte[] newData = new byte[byteLength];
				int positionAfterTIFFHeader = readLong(fieldValue, 0);
				in.reset();
				if(JPEGMarkerInputStream.skipFully(in, positionAfterTIFFHeader)!=positionAfterTIFFHeader)
					throw new IOException();
				JPEGMarkerInputStream.readFully(in, newData, byteLength, false);
				data = newData;
			} else {
				data = fieldValue;
			}

			if(dataFormat==7 || dataFormat==0) { //UNDEFINED
				value = data;
				return;
			} else if(dataFormat==2) { //ASCII
				StringBuffer buffer = new StringBuffer();
				for(int a = 0; a<componentCount; a++) {
					int i = (data[a] & 0xff);
					if(i==0) {
						break;
					}
					char c = (char)i;
					buffer.append(c );
					
				}
				value = buffer.toString();
				return;
			}
			Object[] valueArray;
			if(dataFormat==1 || //BYTE
					dataFormat==6 || //SIGNED BYTE
					dataFormat==3 || //SHORT
					dataFormat==8 || //SIGNED SHORT
					dataFormat==4 || //LONG
					dataFormat==9) { //SIGNED LONG
				valueArray = new Integer[componentCount];
			} else if(dataFormat==5 || //RATIONAL
					dataFormat==10) { //SIGNED RATIONAL 
				valueArray = new Double[componentCount];
			} else {
				throw new RuntimeException("unexpected data format ("+dataFormat+")");
			}
			
			for(int a = 0; a<componentCount; a++) {
				switch(dataFormat) {
					case 1 : //byte
						valueArray[a] = new Integer( readByte(data, a*1) );
						break;
					case 6 : //signed byte
						valueArray[a] = new Integer( readSignedByte(data, a*1) );
						break;
					case 3 : //short
						valueArray[a] = new Integer( readShort(data, a*2) );
						break;
					case 8 : //signed short
						valueArray[a] = new Integer( readSignedShort(data, a*2) );
						break;
					case 4 : //long
						valueArray[a] = new Integer( readLong(data, a*4) );
						break;
					case 9 : //signed long
						valueArray[a] = new Integer( readSignedLong(data, a*4) );
						break;
					case 5 : //rational
						double numerator = readLong( data, a*4);
						double denominator = readLong( data, a*4+4);
						valueArray[a] = new Double( numerator / denominator );
						break;
					case 10 : //signed rational
						double numerator2 = readSignedLong( data, a*4);
						double denominator2 = readSignedLong( data, a*4+4);
						valueArray[a] = new Double( numerator2 / denominator2 );
						break;
					default :
						throw new RuntimeException("Unexpected condition.");
				}
			}
			
			if(valueArray.length==1) {
				value = valueArray[0];
			} else {
				value = valueArray;
			}
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("[ ");
			String typeName = (String)TYPE_LUT.get(new Integer(tagNumber));
			if(typeName==null) {
				sb.append("UNKNOWN ");
			} else {
				sb.append(typeName);
				sb.append(' ');
			}
			

			if(dataFormat==1) {
				sb.append("BYTE ");
			} else if(dataFormat==2) {
				sb.append("ASCII ");
			} else if(dataFormat==3) {
				sb.append("SHORT ");
			} else if(dataFormat==4) {
				sb.append("LONG ");
			} else if(dataFormat==5) {
				sb.append("RATIONAL ");
			} else if(dataFormat==7) {
				sb.append("UNDEFINED ");
			} else if(dataFormat==9) {
				sb.append("SLONG ");
			} else if(dataFormat==10) {
				sb.append("SRATIONAL ");
			} else {
				sb.append("UNRECOGNIZED ");
			}
			sb.append(componentCount+" ");
			if(value==null) {
				sb.append( readLong(fieldValue,0)+" ]");
			} else {
				sb.append(value+" ]");
			}
			return sb.toString();
		}
	}

	DirectoryEntry[] entries;
	long nextIFDPosition;
	
	ImageFileDirectory(BufferedInputStream bufferedIn,long positionAfterTIFFHeader,boolean reverse) throws IOException {
		bufferedIn.reset();
		if(JPEGMarkerInputStream.skipFully(bufferedIn, positionAfterTIFFHeader)!=positionAfterTIFFHeader)
			throw new IOException();
		
		byte[] array = new byte[4];
		if(JPEGMarkerInputStream.readFully(bufferedIn, array,2,reverse)!=2) 
			throw new IOException("Corrupt image file directory.");
		
		int numberOfEntries = (array[0] & 0xff)*256+(array[1] & 0xff);

		entries = new DirectoryEntry[ numberOfEntries ];
		for(int a = 0; a<numberOfEntries; a++) {
			try {
				DirectoryEntry entry = new DirectoryEntry(bufferedIn, reverse);
				entries[a] = (entry);
			} catch(RuntimeException e) {
				System.err.println(a+" / "+numberOfEntries);
				throw e;
			}
		}
		
		/** According to the specs: what is supposed to follow is a 4-byte
		 * pointer to the next IFD.  However I found an APP1 block
		 * that omits these 4 bytes and instead contains some DirectoryEntries.
		 * 
		 * So first I'll scan through the entries and see if any of them believe
		 * they should start here, and if so we just assume there is no
		 * next IFD to point to.  (If anything this errs on the side of
		 * omitting data, instead of trying to read data where the isn't any.)
		 * 
		 */
		long positionToExpectIFDPtr = positionAfterTIFFHeader+2+12*numberOfEntries;
		boolean missingIFDPtr = false;
		for(int a = 0; a<numberOfEntries && (!missingIFDPtr); a++) {
			int k = entries[a].positionAfterTIFFHeader();
			if(k!=-1 && k==positionToExpectIFDPtr) {
				missingIFDPtr = true;
			}
		}
		
		if(missingIFDPtr) {
			//there is no space for the IFD position to be, so assume it's zero.
			nextIFDPosition = 0;
		} else {
			if(JPEGMarkerInputStream.readFully(bufferedIn, array,4,reverse)!=4) 
				throw new IOException("Corrupt image file directory.");
	
			nextIFDPosition = ((array[0] & 0xff) << 24)+
				((array[1] & 0xff) << 16)+
				((array[2] & 0xff) << 8)+
				(array[3] & 0xff);
		}
	}
	
	/** Returns the value associated with a tag number.
	 * This should only be called after entries have been prompted
	 * to resolve.
	 * 
	 * @param tagNumber the tag number to search for
	 * @return the value of that tag, or null if the tag
	 * was not found.
	 */
	Object getProperty(int tagNumber) {
		for(int a = 0; a<entries.length; a++) {
			if(entries[a].tagNumber==tagNumber) {
				return entries[a].value;
			}
		}
		return null;
	}
	
	public String toString() {
		return "ImageFileDirectory[ "+entries+" ]";
	}
}