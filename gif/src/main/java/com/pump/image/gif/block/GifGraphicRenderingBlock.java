/*
 * @(#)GifGraphicRenderingBlock.java
 *
 * $Date: 2014-06-06 14:04:49 -0400 (Fri, 06 Jun 2014) $
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
 * This is just a little layer of classification among
 * {@link com.bric.image.gif.block.GifBlock}s. The GIF file format specification
 * explains this type of block as:
 * <P>
 * "containing information and data used to render a graphic on the display
 * device"
 */
public abstract class GifGraphicRenderingBlock extends GifBlock {

}
