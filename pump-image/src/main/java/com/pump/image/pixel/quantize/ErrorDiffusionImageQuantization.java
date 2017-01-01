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
package com.pump.image.pixel.quantize;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import com.pump.image.pixel.BufferedImageIterator;
import com.pump.image.pixel.IndexedBytePixelIterator;
import com.pump.image.pixel.IntARGBConverter;
import com.pump.image.pixel.quantize.ColorLUT.Match;


/** This applies <a href="http://en.wikipedia.org/wiki/Error_diffusion">error diffusion</a>
 * to an image. Note the {@link ImageQuantization} class offers a few static fields with
 * pre-configured error diffusion settings.
 */
public class ErrorDiffusionImageQuantization extends ImageQuantization {
	final int[][] kernel;
	final int kernelSum ;

	
	/** The pixel iterator that implements the error diffusion image quantization. */
	protected class ErrorDiffusionIndexedBytePixelIterator extends AbstractIndexedBytePixelIterator {
		
		IntARGBConverter iter;
		int[] incomingRow;
		int y = 0;
		int[][] diffusionR, diffusionG, diffusionB;
		int z = kernel[0].length/2;

		ErrorDiffusionIndexedBytePixelIterator(BufferedImage source,
				ColorLUT lut) {
			super(source, lut);
			iter = new IntARGBConverter(BufferedImageIterator.get(source));
			incomingRow = new int[iter.getWidth()];
			
			diffusionR = new int[kernel.length][iter.getWidth()];
			diffusionG = new int[kernel.length][iter.getWidth()];
			diffusionB = new int[kernel.length][iter.getWidth()];
		}

		public void next(byte[] dest) {
			iter.next(incomingRow);
			if(isOpaque()) {
				for(int x = 0; x<iter.getWidth(); x++) {
					int r = (incomingRow[x] >> 16) & 0xff;
					int g = (incomingRow[x] >> 8) & 0xff;
					int b = (incomingRow[x] >> 0) & 0xff;
					
					Match match;
					
					r = Math.min(Math.max(r + diffusionR[0][x]/kernelSum, 0), 255);
					g = Math.min(Math.max(g + diffusionG[0][x]/kernelSum, 0), 255);
					b = Math.min(Math.max(b + diffusionB[0][x]/kernelSum, 0), 255);
						
					match = lut.getMatch(r, g, b);
					
					int dr = r - match.node.red;
					int dg = g - match.node.green;
					int db = b - match.node.blue;
					for(int ky = 0; ky<kernel.length; ky++) {
						for(int kx = 0; kx<kernel[ky].length; kx++) {
							if(x+kx-z>=0 && x+kx-z<iter.getWidth()) {
								diffusionR[ky][x+kx-z] += dr*kernel[ky][kx];
								diffusionG[ky][x+kx-z] += dg*kernel[ky][kx];
								diffusionB[ky][x+kx-z] += db*kernel[ky][kx];
							}
						}
					}
					
					dest[x] = (byte)( match.node.index );
				}
				
				iterateDiffusionData(diffusionR);
				iterateDiffusionData(diffusionG);
				iterateDiffusionData(diffusionB);
			} else {
				int t = icm.getTransparentPixel();
				for(int x = 0; x<iter.getWidth(); x++) {
					int a = (incomingRow[x] >> 24) & 0xff;
					if(a<128) {
						dest[x] = (byte)( t );
					} else {
						int r = (incomingRow[x] >> 16) & 0xff;
						int g = (incomingRow[x] >> 8) & 0xff;
						int b = (incomingRow[x] >> 0) & 0xff;
						
						Match match;
						
						r = Math.min(Math.max(r + diffusionR[0][x]/kernelSum, 0), 255);
						g = Math.min(Math.max(g + diffusionG[0][x]/kernelSum, 0), 255);
						b = Math.min(Math.max(b + diffusionB[0][x]/kernelSum, 0), 255);
							
						match = lut.getMatch(r, g, b);
						
						int dr = r - match.node.red;
						int dg = g - match.node.green;
						int db = b - match.node.blue;
						for(int ky = 0; ky<kernel.length; ky++) {
							for(int kx = 0; kx<kernel[ky].length; kx++) {
								if(x+kx-z>=0 && x+kx-z<iter.getWidth()) {
									diffusionR[ky][x+kx-z] += dr*kernel[ky][kx];
									diffusionG[ky][x+kx-z] += dg*kernel[ky][kx];
									diffusionB[ky][x+kx-z] += db*kernel[ky][kx];
								}
							}
						}
						dest[x] = (byte)( match.node.index );
					}
				}
				
				iterateDiffusionData(diffusionR);
				iterateDiffusionData(diffusionG);
				iterateDiffusionData(diffusionB);
			}
			
			y++;
		}

		public boolean isDone() {
			return y==getHeight();
		}

		public void skip() {
			iter.skip();
			y++;
		}
		
		private void iterateDiffusionData(int[][] data) {
			int[] swap = data[0];
			for(int a = 0; a<data.length-1; a++) {
				data[a] = data[a+1];
			}
			data[data.length-1] = swap;
			Arrays.fill(data[data.length-1], 0);
		}
	}
	
	/** Create a new ErrorDiffusionImageQuantization.
	 * 
	 * @param kernel a two-dimension kernel used to distribute error to adjacent
	 * pixels. The top-middle cell of this kernel is assumed to be the pixel
	 * currently being evaluated. Everything in the top row to the left of that
	 * cell should be zero (because those pixels have already been processed).
	 * The {@link ImageQuantization} class offers a few static fields with
	 * popular kernels.
	 */
	public ErrorDiffusionImageQuantization(int[][] kernel) {
		this.kernel = kernel;
		int sum = 0;
		for(int a = 0; a<kernel.length; a++) {
			for(int b = 0; b<kernel[a].length; b++) {
				sum += kernel[a][b];
			}
		}
		kernelSum = sum;
	}

	@Override
	public IndexedBytePixelIterator createImageData(BufferedImage source,
			ColorLUT colorLUT) {
		return new ErrorDiffusionIndexedBytePixelIterator(source, colorLUT);
	}
}