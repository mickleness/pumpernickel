/*
 * @(#)ImageContextFactory.java
 *
 * $Date: 2014-11-27 01:50:51 -0500 (Thu, 27 Nov 2014) $
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
package com.pump.image;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

/** A factory for {@link ImageContext} objects to render images.
 */
public abstract class ImageContextFactory {
	private static ImageContextFactory factory = new ImageContextFactory() {
		boolean isJFXInstalled = isJFXInstalled();
		
		ExecutorService staticService;
		
		public ImageContext create(BufferedImage bi) {
			try {
				//if(isJFXInstalled)
				//	return new JFXImageContext(bi);
			} catch(Error e) {
				//eat this
			}
			if(staticService==null) {
				staticService = new ForkJoinPool(8); 
			}
			return new BasicImageContext(bi, staticService);
		}
		
		private boolean isJFXInstalled() {
			try {
				Class<?> k = Class.forName("javafx.embed.swing.JFXPanel");
				k.newInstance();
				return true;
			} catch(Throwable t) {
				System.err.println("ImageContextFactory: JFX is not available.");
				if(!(t instanceof ClassNotFoundException))
						t.printStackTrace();
				return false;
			}
		}
	};
	
	/** Return the ImageContextFactory in use.
	 * By default this factory creates {@link BasicImageContext} contexts,
	 * but in some environments it might return alternative models.
	 */
	public static ImageContextFactory get() {
		return factory;
	}
	
	/** Assign a new ImageContextFactory.
	 */
	public static void set(ImageContextFactory f) {
		factory = f;
	}
	
	/** Create an {@link ImageContext} for a <code>BufferedImage</code>.
	 */
	public abstract ImageContext create(BufferedImage bi);
}
