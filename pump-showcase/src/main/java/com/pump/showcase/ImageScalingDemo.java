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
package com.pump.showcase;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.PrintStream;
import java.util.Arrays;

import com.pump.blog.Blurb;
import com.pump.image.ImageLoader;
import com.pump.image.pixel.Scaling;

/** A demo app that shows off the <code>Scaling</code> class.
 *
 */
@Blurb (
filename = "Scaling",
title = "Images: Scaling Down",
releaseDate = "June 2010",
summary = "This improves the image quality and memory allocation when scaling large <code>BufferedImages</code> to thumbnails.",
link = "http://javagraphics.blogspot.com/2010/06/images-scaling-down.html",
sandboxDemo = true
)
public class ImageScalingDemo extends OutputDemo {

	public ImageScalingDemo() {
		super("Run Test", true);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void run() {
		runTests(console.createPrintStream(false));
	}

	protected static void runTests(PrintStream out) {
		out.println("This measures a few different approaches for creating thumbnails by the time each takes.\n");
		out.println("Starting tests...");
		out.println("Size (pixels)\tScaling (ms)\tGraphicsUtilities (ms)\tImage.getScaledInstance() (ms)");
		for(int d = 100; d<=1000; d+=100) {
			runTests(d, out);
		}
		out.println("Finished tests.");
	}

	protected static void runTests(int d,PrintStream out) {
		BufferedImage imageSource = new BufferedImage(d, d,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = imageSource.createGraphics();
		g.scale( ((double)d)/100f, ((double)d)/100f );
		g.setPaint(new GradientPaint(0,0,Color.yellow,100,100,Color.red));
		g.fillRect(0,0,100,100);
		g.setPaint(new GradientPaint(0,100,Color.green,100,0,Color.blue));
		g.fill(new Ellipse2D.Float(25,25,50,50));
		g.dispose();

		out.print(d+"\t");

		long[] time = new long[5];
		int dstW = (int)(d*.25);
		int dstH = (int)(d*.25);

		BufferedImage dest = new BufferedImage( dstW, dstH, BufferedImage.TYPE_INT_ARGB );

		for(int a = 0; a<time.length; a++) {
			time[a] = System.currentTimeMillis();
			for(int z = 0; z<10; z++) {
				Scaling.scale(imageSource, dest);
			}
			time[a] = System.currentTimeMillis()-time[a];
			
			for(int b = 0; b<5; b++) {
				try {
					Thread.sleep(1000);
				} catch(Exception e) {}
				System.gc();
				System.runFinalization();
			}
		}
		Arrays.sort(time);
		out.print(time[time.length/2]+"\t");

		for(int a = 0; a<time.length; a++) {
			time[a] = System.currentTimeMillis();
			for(int z = 0; z<10; z++) {
				createThumbnail(imageSource, dstW, dstH);
			}
			time[a] = System.currentTimeMillis()-time[a];

			for(int b = 0; b<5; b++) {
				try {
					Thread.sleep(1000);
				} catch(Exception e) {}
				System.gc();
				System.runFinalization();
			}
		}
		Arrays.sort(time);
		out.print(time[time.length/2]+"\t");
		
		for(int a = 0; a<time.length; a++) {
			time[a] = System.currentTimeMillis();
			for(int z = 0; z<10; z++) {
				Image img = imageSource.getScaledInstance(dstW, dstH, Image.SCALE_SMOOTH);
				ImageLoader.createImage(img);
			}
			time[a] = System.currentTimeMillis()-time[a];

			for(int b = 0; b<5; b++) {
				try {
					Thread.sleep(1000);
				} catch(Exception e) {}
				System.gc();
				System.runFinalization();
			}
		}
		Arrays.sort(time);
		out.print(time[time.length/2]+"\t");
		
		out.println();
	}


	/** <p>Returns a thumbnail of a source image.</p>
	 * <p>The source and javadoc for this method are
	 * copied from GraphicsUtilities.java, licensed under LGPL.
	 * I want to compare this method against other methods in this class.
	 * 
	 * <p>This method offers a good trade-off between speed and quality.
	 * The result looks better than
	 * {@link #createThumbnailFast(java.awt.image.BufferedImage, int)} when
	 * the new size is less than half the longest dimension of the source
	 * image, yet the rendering speed is almost similar.</p>
	 *
	 * @param image the source image
	 * @param newWidth the width of the thumbnail
	 * @param newHeight the height of the thumbnail
	 * @return a new compatible <code>BufferedImage</code> containing a
	 *   thumbnail of <code>image</code>
	 * @throws IllegalArgumentException if <code>newWidth</code> is larger than
	 *   the width of <code>image</code> or if code>newHeight</code> is larger
	 *   than the height of <code>image or if one the dimensions is not &gt; 0</code>
	 */
	private static BufferedImage createThumbnail(BufferedImage image,
			int newWidth, int newHeight) {
		int width = image.getWidth();
		int height = image.getHeight();

		if (newWidth >= width || newHeight >= height) {
			throw new IllegalArgumentException(
					"newWidth and newHeight cannot"
					+ " be greater than the image"
					+ " dimensions");
		} else if (newWidth <= 0 || newHeight <= 0) {
			throw new IllegalArgumentException(
					"newWidth and newHeight must"
					+ " be greater than 0");
		}

		BufferedImage thumb = image;

		do {
			if (width > newWidth) {
				width /= 2;
				if (width < newWidth) {
					width = newWidth;
				}
			}

			if (height > newHeight) {
				height /= 2;
				if (height < newHeight) {
					height = newHeight;
				}
			}
			
			GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
			BufferedImage temp = gc.createCompatibleImage(width, height);

			Graphics2D g2 = temp.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2.drawImage(thumb, 0, 0, temp.getWidth(),
					temp.getHeight(), null);
			g2.dispose();

			thumb = temp;
		} while (width != newWidth || height != newHeight);

		return thumb;
	}
}