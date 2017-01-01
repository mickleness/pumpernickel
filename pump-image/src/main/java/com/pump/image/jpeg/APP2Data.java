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