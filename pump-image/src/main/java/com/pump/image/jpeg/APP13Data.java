/*
 * @(#)APP13Data.java
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

/** I have no understanding of what this block is, except that
 * it includes a full JPEG thumbnail.
 * One website referred to it as "Photoshop IRB", but unless I can
 * get specs from Adobe it's hard to make sense of...
 */
class APP13Data extends GenericDataWithThumbnail {
	APP13Data(JPEGMarkerInputStream in, boolean storeThumbnail) throws IOException {
		super(in, storeThumbnail);
	}
}
