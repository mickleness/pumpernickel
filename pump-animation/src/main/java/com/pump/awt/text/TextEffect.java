/*
 * @(#)TextEffect.java
 *
 * $Date: 2014-08-18 04:05:17 -0400 (Mon, 18 Aug 2014) $
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
package com.pump.awt.text;

import java.awt.Dimension;
import java.awt.Graphics2D;

/** This is a common interface for visual text effects. */
public interface TextEffect {
	
	/** Paint this effect.
	 * 
	 * @param g the Graphics2D to paint to.
	 * @param fraction a float from [0, 1] indicating how far along
	 * this effect it.
	 */
	public void paint(Graphics2D g,float fraction);
	
	/** An optional method to retrieve the preferred size. May return null.
	 * 
	 */
	public Dimension getPreferredSize();
}
