/*
 * @(#)ColorQuantization.java
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
package com.pump.image.pixel.quantize;

/** This is an algorithm that converts a large <code>ColorSet</code> to a smaller one.
 * <p>The new set is not required to be a subset: it could contain new
 * colors that are the average/approximations of entries in the original set.
 */
public abstract class ColorQuantization {
	/** Create a reduced ColorSet.
	 * 
	 * @param originalSet the large color set to reduce.
	 * @param maximumColorCount the number of colors to reduce to.
	 * @param retainOriginalIntegrity if true then the argument <code>originalSet</code> should not
	 * be modified. If false: then this method may change the original color set at its discretion.
	 * (In some algorithms: this may save a lot of time.)
	 * @return a <code>ColorSet</code> that conforms to maximumColorCount. (This method
	 * may return the <code>originalSet</code> argument if it was already small enough.)
	 */
	public abstract ColorSet createReducedSet(ColorSet originalSet,int maximumColorCount,boolean retainOriginalIntegrity);
}
