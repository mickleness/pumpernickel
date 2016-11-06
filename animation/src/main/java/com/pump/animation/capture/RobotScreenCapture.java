/*
 * @(#)RobotScreenCapture.java
 *
 * $Date: 2014-03-15 02:32:27 -0400 (Sat, 15 Mar 2014) $
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
package com.pump.animation.capture;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/** This is an implementation of <code>ScreenCapture</code> that uses
 * the <code>java.awt.Robot</code> class to capture the screen contents.
 *
 */
public class RobotScreenCapture extends ScreenCapture {
	Robot robot;
	
	/** Creates a new <code>RobotScreenCapture</code> object that will capture
	 * the entire screen.
	 * @throws AWTException if an exception occurs while creating the <code>Robot</code> object.
	 */
	public RobotScreenCapture() throws AWTException {
		robot = new Robot();
	}

	/** Creates a new <code>RobotScreenCapture</code> object that will capture
	 * the bounds provided.
	 * @param screenBounds the bounds to record.
	 * @throws AWTException if an exception occurs while creating the <code>Robot</code> object.
	 */
	public RobotScreenCapture(Rectangle screenBounds) throws AWTException {
		super(screenBounds);
		robot = new Robot();
	}

	/** This uses the <code>Robot</code> class and <code>ImageIO</code> to
	 * record a snapshot of the screen to a PNG file.
	 */
	@Override
	protected File capture(Rectangle r) throws IOException {
		BufferedImage bi = robot.createScreenCapture(r);
		File file = File.createTempFile("robotCapture",".png", tempDir);
		file.deleteOnExit();
		file.createNewFile();
		ImageIO.write(bi, "png", file);
		return file;
	}
}
