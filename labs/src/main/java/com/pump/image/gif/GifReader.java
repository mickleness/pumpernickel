/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.image.gif;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pump.animation.AnimationReader;
import com.pump.image.gif.block.GifBlock;
import com.pump.image.gif.block.GifColorTable;
import com.pump.image.gif.block.GifCommentExtension;
import com.pump.image.gif.block.GifGlobalColorTable;
import com.pump.image.gif.block.GifGraphicControlExtension;
import com.pump.image.gif.block.GifGraphicControlExtension.DisposalMethod;
import com.pump.image.gif.block.GifImageDataBlock;
import com.pump.image.gif.block.GifImageDescriptor;
import com.pump.image.gif.block.GifInterlace;
import com.pump.image.gif.block.GifLocalColorTable;
import com.pump.image.gif.block.GifLogicalScreenDescriptor;
import com.pump.image.gif.block.GifLoopingApplicationExtension;
import com.pump.image.gif.block.GifParser;
import com.pump.image.gif.block.GifPlainTextExtension;

/**
 * This is an efficient way to iterate through a GIF image. This efficiently
 * pipes image data from an <code>InputStream</code> and converts it into 2
 * important pieces of information:
 * <ul>
 * <li>image: a <code>BufferedImage</code> representing a frame</li>
 * <li>duration: a <code>long</code>, representing the time (in milliseconds) a
 * frame should be visible.</li>
 * </ul>
 * <P>
 * This class does not "look ahead" any more than it has to to prepare this
 * information, so it has no way of knowing how many frames there are without
 * iterating through the entire <code>InputStream</code>.
 * <P>
 * Also to minimize the memory this class allocates, this object will not keep
 * any data in memory from previous frames: once you've called
 * <code>nextFrame()</code> the previous data is discarded. This should make it
 * possible to traverse through obscenely large GIF files (I've seen 2-second
 * GIF files that are over 1 MB).
 */
public class GifReader implements AnimationReader, GifConstants {
	/**
	 * Returns <code>true</code> if this file is a supported GIF file with at
	 * least 1 frame.
	 * <P>
	 * This method is optimized to skim incoming data, so it should be a pretty
	 * light call as long as the <code>.skip()</code> method of the
	 * <code>FileInputStream</code> is efficient.
	 * 
	 * @param file
	 *            the file to check
	 * @return <code>true</code> if this package can parse out this GIF file
	 *         into at least 1 frame.
	 */
	public static boolean isGIF(File file) {
		FileInputStream in = null;
		boolean success = false;
		try {
			in = new FileInputStream(file);
			success = isGIF(in);
		} catch (IOException e) {
			return false;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		return success;
	}

	/**
	 * Returns the duration, in milliseconds, of the GIF file provided.
	 * <P>
	 * This method is optimized to skim incoming data, so it should be a pretty
	 * light call as long as the <code>.skip()</code> method of the
	 * <code>FileInputStream</code> is efficient.
	 */
	public static int getDuration(File file) {
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			return getDuration(in);
		} catch (IOException e) {
			return -1;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * Returns the number of frames in the GIF file provided.
	 * <P>
	 * This method is optimized to skim incoming data, so it should be a pretty
	 * light call as long as the <code>.skip()</code> method of the
	 * <code>FileInputStream</code> is efficient.
	 */
	public static int getFrameCount(File file) {
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			return getFrameCount(in);
		} catch (IOException e) {
			return -1;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * Returns <code>true</code> if this file is a supported GIF file with more
	 * than 1 frame.
	 * <P>
	 * This method is optimized to skim incoming data, so it should be a pretty
	 * light call.
	 * 
	 * @param file
	 *            the file to check
	 * @return <code>true</code> if this package can parse out this GIF file
	 *         into more than 1 frame.
	 */
	public static boolean isAnimatedGIF(File file) {
		if (file.isDirectory())
			return false;
		FileInputStream in = null;
		boolean success = false;
		try {
			in = new FileInputStream(file);
			success = isAnimatedGIF(in);
		} catch (IOException e) {
			return false;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		return success;
	}

	/**
	 * Returns <code>true</code> if this <code>InputStream</code> is a supported
	 * GIF with at least 1 frame. Note that this method assumes responsibility
	 * of <code>in</code>, and will close this <code>InputStream</code> after it
	 * has read what it needs to.
	 * <P>
	 * This method is optimized to skim incoming data, so it should be a pretty
	 * light call as long as the <code>.skip()</code> method of the
	 * <code>InputStream</code> is efficient.
	 * 
	 * @param in
	 *            the <code>InputStream</code> to check
	 * @return <code>true</code> if this package can parse out this GIF into at
	 *         least 1 frame.
	 */
	public static boolean isGIF(InputStream in) {
		GifParser p = null;
		try {
			p = new GifParser(in);
			int i = p.getNextBlockType();
			while (i != GifBlock.IMAGE_DATA && i != -1) {
				p.skipNextBlock();
				i = p.getNextBlockType();
			}
			if (i == GifBlock.IMAGE_DATA)
				return true;
			return false;
		} catch (IOException e) {
			return false;
		} catch (RuntimeException e) {
			return false;
		} finally {
			if (p != null) {
				try {
					p.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * Returns <code>true</code> if this <code>InputStream</code> is a supported
	 * GIF with more than 1 frame. Note that this method assumes responsibility
	 * of <code>in</code>, and will close this <code>InputStream</code> after it
	 * has read what it needs to.
	 * <P>
	 * This method is optimized to skim incoming data, so it should be a pretty
	 * light call as long as the <code>.skip()</code> method of the
	 * <code>InputStream</code> is efficient.
	 * 
	 * @param in
	 *            the <code>InputStream</code> to check
	 * @return <code>true</code> if this package can parse out this GIF file
	 *         into more than 1 frame.
	 */
	public static boolean isAnimatedGIF(InputStream in) {
		GifParser p = null;
		try {
			p = new GifParser(in);
			int i = p.getNextBlockType();
			p.readNextBlock(); // give it a chance to digest/choke on the
			// header

			int imageDataBlockCtr = 0;
			while (imageDataBlockCtr < 2 && i != -1) {
				p.skipNextBlock();
				if (i == GifBlock.IMAGE_DATA)
					imageDataBlockCtr++;
				i = p.getNextBlockType();
			}
			if (imageDataBlockCtr >= 2)
				return true;
			return false;
		} catch (IOException e) {
			return false;
		} catch (RuntimeException e) {
			return false;
		} finally {
			if (p != null) {
				try {
					p.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * Returns the number of milliseconds this GIF should last. Note that this
	 * method assumes responsibility of <code>in</code>, and will close this
	 * <code>InputStream</code> after it has read what it needs to.
	 * <P>
	 * This method is optimized to skim incoming data, so it should be a pretty
	 * light call as long as the <code>.skip()</code> method of the
	 * <code>InputStream</code> is efficient.
	 * 
	 * @param in
	 *            the <code>InputStream</code> to read
	 * @return the number of milliseconds this GIF should play. This may return
	 *         zero if this has no frame durations, or -1 if an error occurred
	 *         (for example, if the input stream provided isn't for a GIF).
	 */
	public static int getDuration(InputStream in) {
		GifParser p = null;
		int delay = 0;
		try {
			p = new GifParser(in);
			int i = p.getNextBlockType();
			boolean reset = true;
			while (i != -1) {
				i = p.getNextBlockType();
				if (i == GifBlock.GRAPHIC_CONTROL_EXTENSION && reset) {
					GifGraphicControlExtension gce = (GifGraphicControlExtension) p
							.readNextBlock();
					delay += gce.getDelayTime();
					reset = false;
				} else if (i == GifBlock.IMAGE_DATA) {
					reset = true;
					p.skipNextBlock();
				} else {
					p.skipNextBlock();
				}
			}
			return delay * 10;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		} finally {
			if (p != null) {
				try {
					p.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * Returns the number of images this GIF contains. Note that this method
	 * assumes responsibility of <code>in</code>, and will close this
	 * <code>InputStream</code> after it has read what it needs to.
	 * <P>
	 * This method is optimized to skim incoming data, so it should be a pretty
	 * light call as long as the <code>.skip()</code> method of the
	 * <code>InputStream</code> is efficient.
	 * 
	 * @param in
	 *            the <code>InputStream</code> to read
	 * @return the number of frames this GIF contains. This tries to return -1
	 *         if an error occurs.
	 */
	public static int getFrameCount(InputStream in) {
		GifParser p = null;
		int frameCount = 0;
		try {
			p = new GifParser(in);
			int i = p.getNextBlockType();
			while (i != -1) {
				i = p.getNextBlockType();
				if (i == GifBlock.IMAGE_DATA) {
					frameCount++;
					p.skipNextBlock();
				} else {
					p.skipNextBlock();
				}
			}
			return frameCount;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		} finally {
			if (p != null) {
				try {
					p.close();
				} catch (IOException e) {
				}
			}
		}
	}

	GifParser p;
	int backgroundColorIndex = -1;
	GifLogicalScreenDescriptor lsd;
	GifGlobalColorTable global;
	int cachedLooping = -1;
	boolean finished = false;
	boolean closeInputStreamWhenFinished;

	int duration = -1;
	int frameCount = -1;

	public GifReader(File file) throws IOException {
		this(new FileInputStream(file), true);
		frameCount = getFrameCount(file);
		duration = getDuration(file);
	}

	public int getFrameCount() {
		return frameCount;
	}

	public GifReader(InputStream in, boolean closeInputStreamWhenFinished)
			throws IOException {
		p = new GifParser(in);
		this.closeInputStreamWhenFinished = closeInputStreamWhenFinished;
		p.readNextBlock(); // read & ignore header.
		lsd = (GifLogicalScreenDescriptor) p.readNextBlock();
		if (lsd.hasGlobalColorTable()) {
			global = (GifGlobalColorTable) p.readNextBlock();
			backgroundColorIndex = lsd.getBackgroundColorIndex();
		}
	}

	public double getDuration() {
		return ((double) duration) / 1000.0;
	}

	/**
	 * @return the width of this image/animation, in pixels.
	 *         <P>
	 *         This information is immediately available once this object is
	 *         constructed.
	 */
	public int getWidth() {
		return lsd.getWidth();
	}

	/**
	 * @return the height of this image/animation, in pixels.
	 *         <P>
	 *         This information is immediately available once this object is
	 *         constructed.
	 */
	public int getHeight() {
		return lsd.getHeight();
	}

	/**
	 * This returns the number of times this animation should loop. This method
	 * may return -1 until the animation is completely finished loading --
	 * although it is VERY likely that this information will be ready as soon as
	 * the first frame is loaded.
	 */
	public int getLoopCount() {
		if (loopCount >= 0)
			return loopCount;
		if (finished)
			return 1;
		return -1;
	}

	GifGraphicControlExtension gce;
	GifLocalColorTable localTable;
	GifImageDescriptor imageDescriptor;
	GifImageDataBlock imageData;
	List<String> comments = new ArrayList<String>();
	GifFrame frame = null;
	BufferedImage bi;
	int loopCount = -1;

	/**
	 * This iterates through the GIF file to the next frame image, or
	 * <code>null</code> if no image data is available.
	 * 
	 * @param cloneImage
	 *            if this is <code>true</code>, this method will always return a
	 *            new <code>BufferedImage</code>. If this is <code>false</code>,
	 *            then this method may constantly return the same
	 *            <code>BufferedImage</code>, updated for each frame.
	 * @return the next frame image, or <code>null</code> if there are no more
	 *         frames.
	 */
	public BufferedImage getNextFrame(boolean cloneImage) throws IOException {
		if (frame != null)
			frame.dispose(bi);
		gce = null;
		localTable = null;
		imageDescriptor = null;
		imageData = null;
		while (finished == false && imageData == null) {
			GifBlock b = p.readNextBlock();
			if (b == null) {
				finished = true;
				if (closeInputStreamWhenFinished)
					p.close();
			} else if (b instanceof GifLoopingApplicationExtension) {
				GifLoopingApplicationExtension l = (GifLoopingApplicationExtension) b;
				loopCount = l.getLoopCount();
			} else if (b instanceof GifLocalColorTable) {
				localTable = (GifLocalColorTable) b;
			} else if (b instanceof GifCommentExtension) {
				GifCommentExtension c = (GifCommentExtension) b;
				comments.add(c.getText());
			} else if (b instanceof GifGraphicControlExtension) {
				gce = (GifGraphicControlExtension) b;
			} else if (b instanceof GifImageDataBlock) {
				imageData = (GifImageDataBlock) b;
			} else if (b instanceof GifImageDescriptor) {
				imageDescriptor = (GifImageDescriptor) b;
			} else if (b instanceof GifPlainTextExtension) {
				// TODO:
				throw new IOException(
						"This GIF decoder does not support the GIF plain text extension.");
			}
		}
		if (finished)
			return null;

		if (bi == null) {
			bi = new BufferedImage(getWidth(), getHeight(),
					BufferedImage.TYPE_INT_ARGB);
		}

		GifColorTable colorTable = localTable;
		if (colorTable == null)
			colorTable = global;
		if (colorTable == null)
			throw new RuntimeException(
					"There was no local or global color table available.");
		frame = new GifFrame(lsd, gce, colorTable, imageDescriptor, imageData);
		frame.render(bi);

		if (cloneImage) {
			BufferedImage bi2 = new BufferedImage(bi.getWidth(),
					bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics g = bi2.createGraphics();
			g.drawImage(bi, 0, 0, null);
			g.dispose();
			return bi2;
		}
		return bi;
	}

	/**
	 * If a local color table exists for the last frame returned by
	 * <code>getNextFrame()</code>, then this method returns that table. It is
	 * possible (and likely) that this method will often return
	 * <code>null</code>, indicating that a global color table is being used.
	 */
	public GifColorTable getLocalColorTable() {
		return localTable;
	}

	/**
	 * @return all comments in the GIF file, each as a separate element in an
	 *         array.
	 *         <P>
	 *         Note that it is not safe to call this until the GIF has been
	 *         completely read, because comments could appear anywhere in the
	 *         GIF file.
	 */
	public String[] getComments() {
		return (String[]) comments.toArray(new String[comments.size()]);
	}

	/**
	 * @return the duration of the last frame provided by
	 *         <code>getNextFrame()</code> in milliseconds. This information is
	 *         not required in a GIF so this method may (in rare occasions)
	 *         return zero.
	 */
	public double getFrameDuration() {
		if (gce != null)
			return ((double) gce.getDelayTime()) / 100.0;
		return 0;
	}

	/**
	 * This embodies all the elements that make a frame of an animated GIF.
	 * 
	 */
	public class GifFrame {
		GifGraphicControlExtension gce;
		GifColorTable colorTable;
		GifImageDescriptor imageDescriptor;
		GifImageDataBlock imageData;
		DisposalMethod disposalMethod = DisposalMethod.NONE;
		int[] previous;
		GifLogicalScreenDescriptor logicalScreenDescriptor;

		int x1, y1, x2, y2;

		public GifFrame(GifLogicalScreenDescriptor lgsd,
				GifGraphicControlExtension gce, GifColorTable colorTable,
				GifImageDescriptor imageDescriptor, GifImageDataBlock imageData) {
			this.gce = gce;
			this.colorTable = colorTable;
			this.imageDescriptor = imageDescriptor;
			this.imageData = imageData;
			this.logicalScreenDescriptor = lgsd;

			if (gce != null)
				disposalMethod = gce.getDisposalMethod();

			// I found a complicated GIF that overflowed its bounds... just by 1
			// pixel...
			// ... but still, we should check them here:
			x1 = imageDescriptor.getX();
			y1 = imageDescriptor.getY();
			x2 = x1 + imageDescriptor.getWidth();
			y2 = y1 + imageDescriptor.getHeight();
			if (x1 < 0)
				x1 = 0;
			if (y1 < 0)
				y1 = 0;
			if (x1 >= lgsd.getWidth())
				x1 = lgsd.getWidth();
			if (y1 >= lgsd.getHeight())
				y1 = lgsd.getHeight();
			if (x2 < 0)
				x2 = 0;
			if (y2 < 0)
				y2 = 0;
			if (x2 >= lgsd.getWidth())
				x2 = lgsd.getWidth();
			if (y2 >= lgsd.getHeight())
				y2 = lgsd.getHeight();
		}

		public String toString() {
			return ("x1=" + x1 + " x2=" + x2 + " y1=" + y1 + " y2=" + y2
					+ " interlaced=" + imageDescriptor.isInterlaced()
					+ " data.length=" + imageData.getUncompressedBytes().length);
		}

		public void render(BufferedImage image) {
			if (x2 - x1 == 0 && y2 - y1 == 0)
				return;
			if (disposalMethod == DisposalMethod.PREVIOUS) {
				previous = (int[]) image.getRaster().getDataElements(x1, y1,
						x2 - x1, y2 - y1, null);
			}

			byte[] data = imageData.getUncompressedBytes();
			if (imageDescriptor.isInterlaced()) {
				GifInterlace.decode(data, imageDescriptor.getWidth(),
						imageDescriptor.getHeight());
			}
			int ctr = 0;
			int[] array = (int[]) image.getRaster().getDataElements(x1, y1,
					x2 - x1, y2 - y1, null);

			int transparentIndex = -1;
			if (gce != null)
				transparentIndex = gce.getTransparentColorIndex();
			for (int y = y1; y < y2; y++) {
				for (int x = x1; x < x2; x++) {
					int i = (data[ctr++] & 0xFF);
					if (i != transparentIndex) {
						array[ctr - 1] = colorTable.getRGB(i);
					}
					if (ctr == data.length) {
						y = y2;
						x = x2;
					}
				}
			}

			image.getRaster().setDataElements(x1, y1, x2 - x1, y2 - y1, array);
		}

		public void dispose(BufferedImage image) {
			if (x2 - x1 == 0 && y2 - y1 == 0)
				return;
			if (previous != null) {
				image.getRaster().setDataElements(x1, y1, x2, y2, previous);
				return;
			}

			if (disposalMethod == DisposalMethod.RESTORE_BACKGROUND) {
				int[] i = new int[imageDescriptor.getWidth()];
				// restore to the background
				/**
				 * OK, so this is not documented anywhere I could find... but
				 * apparently if the disposal mode is set to "restore to
				 * background" and there's a transparent pixel for this frame,
				 * that means you need to make the image area transparent when
				 * you dispose it. I would have thought we should say:
				 * if(backgroundColorIndex==gce.getTransparentColorIndex()) THEN
				 * we make everything transparent, but no...
				 */
				if (gce.getTransparentColorIndex() != -1) {
					Arrays.fill(i, 0);
				} else {
					int rgb = colorTable.getRGB(backgroundColorIndex);
					Arrays.fill(i, rgb);
				}
				for (int y = y1; y < y2; y++) {
					image.getRaster().setDataElements(x1, y, x2 - x1, 1, i);
				}
			}
		}
	}
}