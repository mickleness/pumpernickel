/*
 * @(#)APP2Data.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
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
package com.pump.image.jpeg;

import java.io.IOException;

/** Two of several thousand images on my computer had an APP2
 * block with a thumbnail:
 * <br>/Library/Mail Downloads/100_0824.JPG
 * <br>/Users/bricolage1/Pictures/iPhoto Library/Originals/2007/May 24, 2007/Picture 004.jpg
 */
class APP2Data extends GenericDataWithThumbnail {
	APP2Data(JPEGMarkerInputStream in, boolean storeThumbnail) throws IOException {
		super(in, storeThumbnail);
	}
}
