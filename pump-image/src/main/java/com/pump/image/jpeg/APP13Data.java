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

/**
 * I have no understanding of what this block is, except that it includes a full
 * JPEG thumbnail. One website referred to it as "Photoshop IRB", but unless I
 * can get specs from Adobe it's hard to make sense of...
 */
class APP13Data extends GenericDataWithThumbnail {
	APP13Data(JPEGMarkerInputStream in, boolean storeThumbnail)
			throws IOException {
		super(in, storeThumbnail);
	}
}