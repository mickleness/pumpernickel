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
package com.pump.image.gif;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.pump.animation.AnimationReader;
import com.pump.animation.CachedAnimation;
import com.pump.image.gif.block.GifGlobalColorTable;
import com.pump.image.gif.block.GifHeaderBlock;
import com.pump.image.gif.block.GifLogicalScreenDescriptor;
import com.pump.image.gif.block.GifLoopingApplicationExtension;
import com.pump.image.gif.block.GifTrailerBlock;
import com.pump.image.pixel.quantize.BiasedMedianCutColorQuantization;
import com.pump.image.pixel.quantize.ColorSet;
import com.pump.util.Resettable;

public class GifWriter {
	public static enum ColorReduction { 
		/** Create one palette from the first frame. */
		FROM_FIRST_FRAME, 
		/** Create one palette from all frames. */
		FROM_ALL_FRAMES, 
		/** This indicates every frame should have a localized color palette. */
		LOCALIZE_PALETTES };
	
	public static void write(File gifFile,AnimationReader animation,ColorReduction colorReduction) throws IOException {
		if(gifFile==null) throw new NullPointerException();
		if(animation==null) throw new NullPointerException();
		if(colorReduction==null) throw new NullPointerException();
		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(gifFile);
			write(fileOut, animation, colorReduction, true);
		} finally {
			if(fileOut!=null) {
				try {
					fileOut.close();
				} catch(Exception e) {}
			}
		}
	}
	
	public static void write(OutputStream out,AnimationReader animation,ColorReduction colorReduction,boolean close) throws IOException {
		if(out==null) throw new NullPointerException();
		if(animation==null) throw new NullPointerException();
		if(colorReduction==null) throw new NullPointerException();

		BiasedMedianCutColorQuantization reducer = new BiasedMedianCutColorQuantization();

		CachedAnimation manufacturedAnimation = null;
		try {
			IndexColorModel globalColorModel;
			BufferedImage bi;
			ColorSet originalColors = new ColorSet();
			if(ColorReduction.FROM_FIRST_FRAME.equals(colorReduction) || ColorReduction.LOCALIZE_PALETTES.equals(colorReduction)) {
				bi = animation.getNextFrame(false);
				originalColors.addColors(bi);
			} else if(ColorReduction.FROM_ALL_FRAMES.equals(colorReduction)) {
				//make something the animation is resettable:
				if(!(animation instanceof Resettable)) {
					manufacturedAnimation = new CachedAnimation(animation);
					animation = manufacturedAnimation.createReader();
				}

				bi = animation.getNextFrame(false);
				while(bi!=null) {
					originalColors.addColors(bi);
					bi = animation.getNextFrame(false);
				}
				((Resettable)animation).reset();
				bi = animation.getNextFrame(false);
			} else {
				throw new IllegalArgumentException("unrecognized color reduction type: "+colorReduction);
			}
			ColorSet reducedColors = reducer.createReducedSet(originalColors, 255, false);
			
			globalColorModel = reducedColors.createIndexColorModel(true, true);
			GifWriter writer = new GifWriter(out, 
					new Dimension(bi.getWidth(), bi.getHeight()),
					globalColorModel,
					animation.getLoopCount(),
					0, null);
			
			/* In gifs: frame durations are expressed in 1/100's of a second.
			 * We'll have to round (and in some cases drop frames) to
			 * account for this.
			 */
			double carryover = 0;
			int frameIndex = 0;
			while(bi!=null) {
				double actualFrameLength = animation.getFrameDuration();
				
				double adjustedFrameLength = actualFrameLength + carryover;
				int centiseconds = (int)(adjustedFrameLength * 100);
				
				if(centiseconds>=2) {
					IndexColorModel localPalette = null;
					if(frameIndex>0 && ColorReduction.LOCALIZE_PALETTES.equals(colorReduction)) {
						ColorSet localColors = new ColorSet();
						localColors.addColors(bi);
						ColorSet localReducedColors = reducer.createReducedSet(localColors, 255, false);
						localPalette = localReducedColors.createIndexColorModel(true, true);
					}
					writer.write(bi, centiseconds*10, localPalette);
				} else {
					centiseconds = 0;
				}

				carryover = adjustedFrameLength - ((double)centiseconds)/100;
				
				bi = animation.getNextFrame(true);
				frameIndex++;
			}
			writer.close(false);
		} finally {
			if(close) {
				try {
					out.close();
				} catch(Exception e) {}
			}
			if(manufacturedAnimation!=null) {
				manufacturedAnimation.dispose();
			}
		}
	}
	
	IndexColorModel globalColorModel = null;
	Dimension size;
	OutputStream out;
	GifEncoder encoder;
	int backgroundColorIndex;
	private boolean finished = false;

	/**
	 * Constructs a <code>GifWriter</code> with a global color table.
	 * 
	 * @param out
	 *            the output stream to write to. This object will <i>not</i>
	 *            close the output stream when it is finished.
	 * @param size
	 *            the bounds of this animation. Images larger than this value
	 *            will be cut off. (Images smaller than this value will have
	 *            dead space surrounding them.)
	 * @param globalColorModel
	 *            a byte-based <code>IndexColorModel</code> to use as a global
	 *            color palette.
	 * @param loopCount
	 *            how many times this GIF file should loop. If this is zero (or
	 *            negative), then this GIF will not loop. If this is greater
	 *            than 65535, then this GIF will loop forever.
	 * @param backgroundColorIndex
	 *            the index in the global color table to use as a background
	 *            color
	 * @param customEncoder an optional encoder. If null (which is encouraged), then
	 *            <code>GifEncoderFactory.get().createEncoder()</code> is used.
	 * @throws IOException
	 *             if the underlying <code>OutputStream</code> has trouble
	 *             writing any of the GIF header information.
	 */
	public GifWriter(OutputStream out, Dimension size,
			IndexColorModel globalColorModel, int loopCount,
			int backgroundColorIndex,GifEncoder customEncoder) throws IOException {
		this.out = out;
		this.size = (Dimension) size.clone();
		this.backgroundColorIndex = backgroundColorIndex;
		encoder = customEncoder==null ? GifEncoderFactory.get().createEncoder() : customEncoder;
		this.globalColorModel = globalColorModel;
		writeHeader(loopCount);
	}
	
	@Override
	protected void finalize() throws IOException {
		close(false);
	}

	/**
	 * This marks the end of the GIF file. You must call this method,
	 * otherwise you will not have created a valid GIF.
	 * <P>Subsequent calls to add an image will throw an exception.
	 * @throws IOException if an IO problem occurs.
	 */
	public void close(boolean closeOutputStream) throws IOException {
		try {
			encoder.flush(out);
			finished = true;
			(new GifTrailerBlock()).write(out);
		} finally {
			if(closeOutputStream)
				out.close();
		}
	}

	/**
	 * Constructs a <code>GifWriter</code> without a global color table.
	 * 
	 * @param out
	 *            the output stream to write to. This object will <i>not</i>
	 *            close the output stream when it is finished.
	 * @param size
	 *            the bounds of this animation. Images larger than this value
	 *            will be cut off. (Images smaller than this value will have
	 *            dead space surrounding them.)
	 * @param loopCount
	 *            how many times this GIF file should loop. If this is zero (or
	 *            negative), then this GIF will not loop. If this is greater
	 *            than 65535, then this GIF will loop forever.
	 * @throws IOException
	 *             if the underlying <code>OutputStream</code> has trouble
	 *             writing any of the GIF header information.
	 */
	public GifWriter(OutputStream out, Dimension size, int loopCount) throws IOException {
		this(out, size, null, loopCount, -1, null);
	}

	/**
	 * Writes the header, the logical screen descriptor, and possibly the global
	 * color table and the netscape looping extension.
	 * 
	 * @throws IOException
	 */
	protected void writeHeader(int loopCount) throws IOException {
		(new GifHeaderBlock()).write(out);
		int colorSize = 0;
		if (globalColorModel != null) {
			colorSize = globalColorModel.getMapSize();
			int k = 2;
			while (colorSize > k) {
				k *= 2;
			}
			if (k > 256)
				throw new IllegalArgumentException("Illegal number of colors ("
						+ colorSize + ").  There can only be 256 at most.");
			colorSize = k;
		}
		GifLogicalScreenDescriptor lsd = new GifLogicalScreenDescriptor(size,
				colorSize);
		lsd.write(out);
		if (colorSize > 0) {
			GifGlobalColorTable ct = new GifGlobalColorTable(globalColorModel);
			ct.write(out);
		}
		if (loopCount >= 0) {
			GifLoopingApplicationExtension e = new GifLoopingApplicationExtension(
					loopCount);
			e.write(out);
		}
	}

	private int carryoverMillis = 0;
	/**
	 * 
	 * @param img
	 *            the image to write
	 * @param frameDuration
	 *            the duration of this frame, in milliseconds.
	 * @param localColorModel
	 *            an optional local color table. Note that if this is
	 *            <code>null</code>, and there is no global color table
	 *            provided, then a local color table will still be created.
	 * @throws IOException
	 *             if the underlying <code>OutputStream</code> throws an
	 *             <code>IOException</code>
	 */
	public void write(BufferedImage img, int frameDuration,
			IndexColorModel localColorModel) throws IOException {
		if (finished)
			throw new IllegalStateException(
					"This GifWriter has already received a call to finish().  You cannot add images after calling this method.");
		boolean makeLocalTable = (localColorModel != null);

		IndexColorModel colorModel = localColorModel;
		if (colorModel == null) {
			colorModel = globalColorModel;
		}

		if (colorModel == null) {
			makeLocalTable = true;
			/** We have to create a new IndexColorModel */
			ColorSet origColors = new ColorSet();
			origColors.addColors(img);
			ColorSet reduced = new BiasedMedianCutColorQuantization().createReducedSet(origColors, 255, false);
			colorModel = reduced.createIndexColorModel(true, true);
		}
		
		int frameDurationInCentiseconds = (frameDuration + carryoverMillis)/10;
		carryoverMillis += frameDuration - frameDurationInCentiseconds*10;

		encoder.writeImage(out, img, frameDurationInCentiseconds, colorModel, makeLocalTable);
	}
}