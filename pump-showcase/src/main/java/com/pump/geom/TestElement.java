/*
 * @(#)TestElement.java
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
package com.pump.geom;

import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public abstract class TestElement {
	public static final int ADD = 1;
	public static final int SUBTRACT = 2;
	public static final int INTERSECT = 3;
	public static final int XOR = 4;

	final static BufferedImage image1 = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
	final static BufferedImage image2 = new BufferedImage(image1.getWidth(), image1.getHeight(), BufferedImage.TYPE_INT_ARGB);
	
	/** The human-readable name of this test. */
	public abstract String getName();
	
	/** The component that appears when this test is selected. */
	public abstract JComponent getComponent();
	
	/** This will be called if the user navigates away from this TestElement. */
	public abstract void cancel();
}
