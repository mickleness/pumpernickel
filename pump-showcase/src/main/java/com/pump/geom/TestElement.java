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