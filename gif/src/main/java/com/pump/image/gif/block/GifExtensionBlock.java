/*
 * @(#)GifExtensionBlock.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2014 by Jeremy Wood.
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
package com.pump.image.gif.block;

/**
 * This abstract class is just a means to better classify blocks.
 * <P>
 * This was probably created by the insightful developers of the 87a
 * specification, so future specifications could include these "extensions", and
 * 87a decoders would be able to handle them.
 */
public abstract class GifExtensionBlock extends GifBlock {

}
