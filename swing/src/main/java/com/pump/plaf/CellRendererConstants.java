/*
 * @(#)CellRendererConstants.java
 *
 * $Date: 2015-09-13 14:46:53 -0400 (Sun, 13 Sep 2015) $
 *
 * Copyright (c) 2015 by Jeremy Wood.
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
package com.pump.plaf;

import java.awt.Color;
import java.awt.Insets;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.pump.swing.DashedBorder;

public interface CellRendererConstants
{
	public static final Color background = Color.white;
	public static final Color foreground = Color.black;
	public static final Insets iconPadding = new Insets(2,10,2,4);
	
	/** A one-pixel empty border.*/
	public static final Border EMPTY_BORDER = new EmptyBorder(1,1,1,1);
	/** A one-pixel dark gray dotted border, used to indicate focus in some environments. */
	public static final Border FOCUS_BORDER = new DashedBorder(1,1,1,Color.darkGray,0);
}
