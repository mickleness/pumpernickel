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
package com.pump.image.shadow;

import java.awt.Color;
import java.util.Arrays;
import java.util.Objects;

/**
 * This ShadowRenderer uses a uniform kernel and a specialized algorithm to
 * render the shadow quickly. The uniform kernel, though, means the resulting
 * shadow is blocky. (A bell-shaped kernel produces a better looking shadow.)
 * <p>
 * Although the kernel is uniform (meaning "all elements are the same"), this
 * includes an option to make the edges of the kernel weighted (meaning "the
 * first and last elements of the kernel are softer"). When you use a solid
 * integer kernel radius the kernel is truly uniform, but when you use a
 * fractional value this renderer uses weighted edges. The weighted edges are a
 * little more expensive to calculate. (The primary motivation behind supporting
 * weighted edges has to do with giving the DoubleBoxShadowRenderer a greater
 * depth of support.)
 * <p>
 * This supports using the same int array as both the source and the
 * destination.
 * <p>
 * This was originally based on JDesktop's ShadowRenderer class by Sebastien
 * Petrucci and Romain Guy, but the current implementation is a complete rewrite
 * from their original code. Also see
 * <a href="https://dbaron.org/log/20110225-blur-radius">David Baron's
 * article</a> on the subject of blurs/shadows.
 */
public class BoxShadowRenderer implements ShadowRenderer {

	/**
	 * This helper class runs the two separate blurs.
	 */
	static class Renderer {

		/**
		 * This must run after the VerticalRenderer.
		 * <p>
		 * This looks for data in the rightmost 8 bits of the dest buffer (the
		 * blue channel) and ends up blurring it and moving it to the leftmost 8
		 * bits (the alpha channel)
		 */
		class HorizontalRenderer {
			int xMin, xMin_plusHistory, xMax_minusHistory, xMax;
			int dstWidth;
			int y1, y2;
			int readIndexBase, writeIndexBase;

			public HorizontalRenderer() {
				int rgb = shadowColor.getRGB() & 0xffffff;
				int shadowMultiplier = (int) (shadowColor.getAlpha());
				int shadowDivisor = (int) (weightedShadowSize * 0xff);
				for (int i = 0; i < divideByShadowSizeLUT.length; i++) {
					divideByShadowSizeLUT[i] = (((i * shadowMultiplier
							/ shadowDivisor) & 0xff) << 24) + rgb;
				}

				xMin = dstX - kernelSize;
				xMin_plusHistory = dstX + kernelSize + 1;
				xMax_minusHistory = dstX + width - kernelSize;
				xMax = dstX + width + kernelSize;
				dstWidth = dst.getWidth();
				y1 = dstY - kernelSize;
				y2 = dstY + height + kernelSize;
				readIndexBase = y1 * dstWidth + xMin + kernelSize;
				writeIndexBase = y1 * dstWidth + xMin;
			}

			public void run() {
				if (!(xMin <= xMin_plusHistory
						&& xMin_plusHistory <= xMax_minusHistory
						&& xMax_minusHistory <= xMax)) {
					runHorizontalBlur_unoptimized();
					return;
				}

				if (edgeWeight == 255) {
					// no weighted edges makes this our most efficient case:
					for (int y = y1; y < y2; y++) {
						int aHistoryIdx = -1;
						int aSum = 0;

						int x = xMin;
						int readIndex = readIndexBase;
						int writeIndex = writeIndexBase;
						while (x < xMin_plusHistory) {
							int alpha = dstBuffer[readIndex];
							aHistory[++aHistoryIdx] = alpha;
							aSum += alpha;
							dstBuffer[writeIndex] = divideByShadowSizeLUT[aSum];
							readIndex++;
							writeIndex++;
							x++;
						}

						while (x < xMax_minusHistory) {
							aHistoryIdx++;
							if (aHistoryIdx == shadowSize)
								aHistoryIdx = 0;
							int alpha = dstBuffer[readIndex];
							aSum += alpha - aHistory[aHistoryIdx];
							aHistory[aHistoryIdx] = alpha;
							dstBuffer[writeIndex] = divideByShadowSizeLUT[aSum];
							readIndex++;
							writeIndex++;
							x++;
						}

						while (x < xMax) {
							aHistoryIdx++;
							if (aHistoryIdx == shadowSize)
								aHistoryIdx = 0;
							aSum -= aHistory[aHistoryIdx];
							dstBuffer[writeIndex] = divideByShadowSizeLUT[aSum];
							writeIndex++;
							x++;
						}

						writeIndexBase += dstWidth;
						readIndexBase += dstWidth;
					}
				} else {
					xMin_plusHistory--;

					for (int y = y1; y < y2; y++) {
						int aHistoryIdx = -1;
						Arrays.fill(aHistory, 0);
						int nextAlphaHistoryIndex = 0;
						int aSum = 0;

						int x = xMin;
						int readIndex = readIndexBase;
						int writeIndex = writeIndexBase;
						while (x < xMin_plusHistory) {
							int alpha = dstBuffer[readIndex];
							aHistoryIdx = nextAlphaHistoryIndex;
							aHistory[aHistoryIdx] = alpha;
							aSum += alpha;

							nextAlphaHistoryIndex++;
							if (nextAlphaHistoryIndex == shadowSize)
								nextAlphaHistoryIndex = 0;

							int z = aSum - alpha * edgeWeightComplement / 255;
							dstBuffer[writeIndex] = divideByShadowSizeLUT[z];

							readIndex++;
							writeIndex++;
							x++;
						}

						while (x < xMax_minusHistory) {
							aHistoryIdx = nextAlphaHistoryIndex;
							int alpha = dstBuffer[readIndex];
							aSum += alpha - aHistory[aHistoryIdx];
							aHistory[aHistoryIdx] = alpha;

							nextAlphaHistoryIndex++;
							if (nextAlphaHistoryIndex == shadowSize)
								nextAlphaHistoryIndex = 0;

							int z = aSum - alpha * edgeWeightComplement / 255
									- aHistory[nextAlphaHistoryIndex]
											* edgeWeightComplement / 255;
							dstBuffer[writeIndex] = divideByShadowSizeLUT[z];

							readIndex++;
							writeIndex++;
							x++;
						}

						while (x < xMax) {
							aHistoryIdx = nextAlphaHistoryIndex;
							aSum -= aHistory[aHistoryIdx];
							aHistory[aHistoryIdx] = 0;

							nextAlphaHistoryIndex++;
							if (nextAlphaHistoryIndex == shadowSize)
								nextAlphaHistoryIndex = 0;

							int z = aSum - aHistory[nextAlphaHistoryIndex]
									* edgeWeightComplement / 255;
							dstBuffer[writeIndex] = divideByShadowSizeLUT[z];

							writeIndex++;
							x++;
						}

						writeIndexBase += dstWidth;
						readIndexBase += dstWidth;
					}
				}
			}

			/**
			 * This addresses fringe cases where the rows are unusually short.
			 * This method uses a few more if/thens and is slightly less
			 * efficient, but since the rows are short it probably won't matter
			 * much.
			 */
			private void runHorizontalBlur_unoptimized() {
				if (edgeWeight == 255) {
					for (int y = y1; y < y2; y++) {
						int aHistoryIdx = -1;
						int aSum = 0;
						Arrays.fill(aHistory, 0);

						int readIndex = readIndexBase;
						int writeIndex = writeIndexBase;
						for (int x = xMin; x < xMax; x++) {
							aHistoryIdx++;
							if (aHistoryIdx == shadowSize)
								aHistoryIdx = 0;
							int alpha = readIndex < dstBuffer.length
									? dstBuffer[readIndex]
									: 0;
							aSum += alpha - aHistory[aHistoryIdx];
							aHistory[aHistoryIdx] = alpha;
							dstBuffer[writeIndex] = divideByShadowSizeLUT[aSum];
							readIndex++;
							writeIndex++;
						}

						writeIndexBase += dstWidth;
						readIndexBase += dstWidth;
					}
				} else {
					for (int y = y1; y < y2; y++) {
						int aHistoryIdx = -1;
						int aSum = 0;
						Arrays.fill(aHistory, 0);
						int nextAlphaHistoryIndex = 0;

						int readIndex = readIndexBase;
						int writeIndex = writeIndexBase;
						for (int x = xMin; x < xMax; x++) {
							aHistoryIdx = nextAlphaHistoryIndex;

							int alpha = readIndex < dstBuffer.length
									? dstBuffer[readIndex]
									: 0;
							aSum += alpha - aHistory[aHistoryIdx];
							aHistory[aHistoryIdx] = alpha;

							nextAlphaHistoryIndex++;
							if (nextAlphaHistoryIndex == shadowSize)
								nextAlphaHistoryIndex = 0;

							int z = aSum - alpha * edgeWeightComplement / 255
									- aHistory[nextAlphaHistoryIndex]
											* edgeWeightComplement / 255;
							dstBuffer[writeIndex] = divideByShadowSizeLUT[z];

							readIndex++;
							writeIndex++;
						}

						writeIndexBase += dstWidth;
						readIndexBase += dstWidth;
					}
				}
			}
		}

		/**
		 * This must run before the HorizontalRender.
		 * <p>
		 * This looks for data in the leftmost 8 bits of the src buffer (the
		 * alpha channel) and ends up blurring it and moving it to the rightmost
		 * 8 bits of the dest buffer (the blue channel)
		 */
		class VerticalRenderer {
			int yMin, yMin_plusHistory, yMax_minusHistory, yMax;
			int endX;
			int srcWidth, dstWidth;
			int srcIndexBase, dstIndexBase;
			int x1, x2;

			public VerticalRenderer() {
				for (int i = 0; i < divideByShadowSizeLUT.length; i++) {
					divideByShadowSizeLUT[i] = (int) (i / weightedShadowSize);
				}
				yMin = dstY - kernelSize;
				yMin_plusHistory = dstY + kernelSize + 1;
				yMax_minusHistory = dstY + height - kernelSize;
				yMax = dstY + height + kernelSize;

				endX = dstX + width;
				srcWidth = src.getWidth();
				dstWidth = dst.getWidth();
				srcIndexBase = srcY * srcWidth - dstX + srcX;
				dstIndexBase = yMin * dstWidth;
				x1 = dstX;
				x2 = endX;
			}

			public void run() {
				if (!(yMin <= yMin_plusHistory
						&& yMin_plusHistory <= yMax_minusHistory
						&& yMax_minusHistory <= yMax)) {
					runVerticalBlur_unoptimized();
					return;
				}

				if (edgeWeight == 255) {
					// no weighted edges makes this our most efficient case:
					for (int x = x1; x < x2; x++) {
						int aHistoryIdx = -1;
						int aSum = 0;

						int y = yMin;
						int srcIndex = srcIndexBase + x;
						int dstIndex = dstIndexBase + x;
						while (y < yMin_plusHistory) {
							int alpha = srcBuffer[srcIndex] >>> 24;
							aHistory[++aHistoryIdx] = alpha;
							aSum += alpha;
							dstBuffer[dstIndex] = divideByShadowSizeLUT[aSum];
							dstIndex += dstWidth;
							srcIndex += srcWidth;
							y++;
						}

						while (y < yMax_minusHistory) {
							aHistoryIdx++;
							if (aHistoryIdx == shadowSize)
								aHistoryIdx = 0;
							int alpha = srcBuffer[srcIndex] >>> 24;
							aSum += alpha - aHistory[aHistoryIdx];
							aHistory[aHistoryIdx] = alpha;
							dstBuffer[dstIndex] = divideByShadowSizeLUT[aSum];
							dstIndex += dstWidth;
							srcIndex += srcWidth;
							y++;
						}

						while (y < yMax) {
							aHistoryIdx++;
							if (aHistoryIdx == shadowSize)
								aHistoryIdx = 0;
							aSum -= aHistory[aHistoryIdx];
							dstBuffer[dstIndex] = divideByShadowSizeLUT[aSum];
							dstIndex += dstWidth;
							y++;
						}
					}
				} else {
					yMin_plusHistory--;

					for (int x = x1; x < x2; x++) {
						int aHistoryIdx = -1;
						Arrays.fill(aHistory, 0);
						int nextAlphaHistoryIndex = 0;
						int aSum = 0;

						int y = yMin;
						int srcIndex = srcIndexBase + x;
						int dstIndex = dstIndexBase + x;
						while (y < yMin_plusHistory) {
							int alpha = srcBuffer[srcIndex] >>> 24;
							aHistoryIdx = nextAlphaHistoryIndex;
							aHistory[aHistoryIdx] = alpha;
							aSum += alpha;

							nextAlphaHistoryIndex++;
							if (nextAlphaHistoryIndex == shadowSize)
								nextAlphaHistoryIndex = 0;

							int z = aSum - alpha * edgeWeightComplement / 255;
							dstBuffer[dstIndex] = divideByShadowSizeLUT[z];

							dstIndex += dstWidth;
							srcIndex += srcWidth;
							y++;
						}

						while (y < yMax_minusHistory) {
							aHistoryIdx = nextAlphaHistoryIndex;
							int alpha = srcBuffer[srcIndex] >>> 24;
							aSum += alpha - aHistory[aHistoryIdx];

							nextAlphaHistoryIndex++;
							if (nextAlphaHistoryIndex == shadowSize)
								nextAlphaHistoryIndex = 0;

							aHistory[aHistoryIdx] = alpha;

							int z = aSum - alpha * edgeWeightComplement / 255
									- aHistory[nextAlphaHistoryIndex]
											* edgeWeightComplement / 255;
							dstBuffer[dstIndex] = divideByShadowSizeLUT[z];

							dstIndex += dstWidth;
							srcIndex += srcWidth;
							y++;
						}

						while (y < yMax) {
							aHistoryIdx = nextAlphaHistoryIndex;
							aSum -= aHistory[aHistoryIdx];
							aHistory[aHistoryIdx] = 0;

							nextAlphaHistoryIndex++;
							if (nextAlphaHistoryIndex == shadowSize)
								nextAlphaHistoryIndex = 0;

							int z = aSum - aHistory[nextAlphaHistoryIndex]
									* edgeWeightComplement / 255;
							dstBuffer[dstIndex] = divideByShadowSizeLUT[z];

							dstIndex += dstWidth;
							y++;
						}
					}
				}
			}

			/**
			 * This addresses fringe cases where the columns are unusually
			 * short. This method uses a few more if/thens and is slightly less
			 * efficient, but since the columns are short it probably won't
			 * matter much.
			 */
			private void runVerticalBlur_unoptimized() {
				int loopCount = yMax - yMin;

				int whenAddingStops = height + 2 * kernelSize - shadowSize;

				if (edgeWeight == 255) {
					for (int x = x1; x < x2; x++) {
						int aHistoryIdx = -1;
						int aSum = 0;
						Arrays.fill(aHistory, 0);

						int srcIndex = srcIndexBase + x;
						int dstIndex = dstIndexBase + x;

						for (int loopCtr = 0; loopCtr < loopCount; loopCtr++) {
							aHistoryIdx++;
							if (aHistoryIdx == shadowSize)
								aHistoryIdx = 0;
							if (loopCtr <= whenAddingStops) {
								int alpha = srcBuffer[srcIndex] >>> 24;
								aSum += alpha;
								aHistory[aHistoryIdx] = alpha;
								srcIndex += srcWidth;
							}
							if (loopCtr >= shadowSize)
								aSum -= aHistory[aHistoryIdx];
							dstBuffer[dstIndex] = divideByShadowSizeLUT[aSum];
							dstIndex += dstWidth;
						}
					}
				} else {
					for (int x = x1; x < x2; x++) {
						int aHistoryIdx = -1;
						int aSum = 0;
						Arrays.fill(aHistory, 0);
						int nextAlphaHistoryIndex = 0;

						int srcIndex = srcIndexBase + x;
						int dstIndex = dstIndexBase + x;

						for (int loopCtr = 0; loopCtr < loopCount; loopCtr++) {
							aHistoryIdx = nextAlphaHistoryIndex;

							if (loopCtr >= shadowSize)
								aSum -= aHistory[aHistoryIdx];
							if (loopCtr <= whenAddingStops) {
								int alpha = srcBuffer[srcIndex] >>> 24;
								aSum += alpha;
								aHistory[aHistoryIdx] = alpha;
								srcIndex += srcWidth;
							} else {
								aHistory[aHistoryIdx] = 0;
							}

							nextAlphaHistoryIndex++;
							if (nextAlphaHistoryIndex == shadowSize)
								nextAlphaHistoryIndex = 0;

							dstBuffer[dstIndex] = divideByShadowSizeLUT[aSum
									- aHistory[aHistoryIdx]
											* edgeWeightComplement / 255
									- aHistory[nextAlphaHistoryIndex]
											* edgeWeightComplement / 255];

							dstIndex += dstWidth;
						}
					}
				}
			}
		}

		ARGBPixels src, dst;
		int[] srcBuffer, dstBuffer;
		int shadowSize, kernelSize;
		float weightedShadowSize;

		int width, height;
		int dstX, dstY, srcX, srcY;
		int edgeWeight, edgeWeightComplement;
		int[] aHistory;

		int[] divideByShadowSizeLUT;
		Color shadowColor;

		/**
		 * 
		 * @param src
		 * @param dst
		 * @param srcX
		 * @param srcY
		 * @param dstX
		 * @param dstY
		 * @param width
		 * @param height
		 * @param kernelSize
		 * @param edgeWeight
		 *            a value from [1,255] indicating how much weight the edge
		 *            pixels in a kernel should receive.
		 * @param shadowOpacity
		 */
		public Renderer(ARGBPixels src, ARGBPixels dst, int srcX, int srcY,
				int dstX, int dstY, int width, int height, int kernelSize,
				int edgeWeight, Color shadowColor) {
			Objects.requireNonNull(src);
			Objects.requireNonNull(shadowColor);

			if (dst == null) {
				dst = new ARGBPixels(dstX + width + kernelSize,
						dstY + height + kernelSize);
			}
			if (edgeWeight < 1 || edgeWeight > 255)
				throw new IllegalArgumentException(
						"edgeWeight should be between [1, 255]");

			this.edgeWeight = edgeWeight;
			edgeWeightComplement = 255 - edgeWeight;

			this.src = src;
			this.dst = dst;
			this.shadowColor = shadowColor;
			this.kernelSize = kernelSize;

			this.srcX = srcX;
			this.srcY = srcY;
			this.dstX = dstX;
			this.dstY = dstY;

			this.width = width;
			this.height = height;

			shadowSize = kernelSize * 2 + 1;

			weightedShadowSize = shadowSize - 2f
					+ 2f * ((float) edgeWeight) / 255f;

			srcBuffer = src.getPixels();
			dstBuffer = dst.getPixels();

			aHistory = new int[shadowSize];
			divideByShadowSizeLUT = new int[256 * shadowSize];

			// make sure our src bounds will fit within src:
			if (srcX < 0)
				throw new IllegalArgumentException("srcX = " + srcX);
			if (srcY < 0)
				throw new IllegalArgumentException("srcY = " + srcY);
			if (srcX + width > src.getWidth())
				throw new IllegalArgumentException(
						"srcX = " + srcX + "; width = " + width
								+ "; src.getWidth() = " + src.getWidth());
			if (srcY + height > src.getHeight())
				throw new IllegalArgumentException(
						"srcY = " + srcY + "; height = " + height
								+ "; src.getHeight() = " + src.getHeight());

			// make sure our dst bounds will fit within dst:
			if (dstX < kernelSize)
				throw new IllegalArgumentException(
						"dstX = " + dstX + "; kernelSize = " + kernelSize);
			if (dstY < kernelSize)
				throw new IllegalArgumentException(
						"dstY = " + dstY + "; kernelSize = " + kernelSize);
			if (dstX + width + kernelSize > dst.getWidth())
				throw new IllegalArgumentException("dstX = " + dstX
						+ "; kernelSize = " + kernelSize + "; width = " + width
						+ "; dst.getWidth() = " + dst.getWidth());
			if (dstY + height + kernelSize > dst.getHeight())
				throw new IllegalArgumentException("dstY = " + dstY
						+ "; kernelSize = " + kernelSize + "; height = "
						+ height + "; dst.getHeight() = " + dst.getHeight());
		}

		public void run() {
			new VerticalRenderer().run();
			new HorizontalRenderer().run();
		}
	}

	@Override
	public ARGBPixels createShadow(ARGBPixels src, ARGBPixels dst,
			float kernelRadius, Color shadowColor) {
		int r = getKernel(kernelRadius).getKernelRadius();
		return createShadow(src, dst, r, r, kernelRadius, shadowColor);
	}

	public ARGBPixels createShadow(ARGBPixels src, ARGBPixels dst,
			int srcToDstX, int srcToDstY, float kernelRadius,
			Color shadowColor) {
		GaussianKernel k = getKernel(kernelRadius);
		if (k.getKernelRadius() == 0) {
			return GaussianShadowRenderer.createUnblurredShadow(src, dst, 0, 0,
					srcToDstX, srcToDstY, src.getWidth(), src.getHeight(),
					shadowColor);
		}
		int edgeWeight = getEdgeWeight(kernelRadius);
		Renderer renderer = new Renderer(src, dst, 0, 0, srcToDstX, srcToDstY,
				src.getWidth(), src.getHeight(), k.getKernelRadius(),
				edgeWeight, shadowColor);
		renderer.run();
		return renderer.dst;
	}

	public void applyShadow(ARGBPixels pixels, int x, int y, int width,
			int height, float kernelRadius, Color shadowColor) {
		Objects.requireNonNull(pixels);
		GaussianKernel k = getKernel(kernelRadius);
		if (k.getKernelRadius() == 0) {
			GaussianShadowRenderer.createUnblurredShadow(pixels, pixels, x, y,
					x, y, width, height, shadowColor);
			return;
		}
		int edgeWeight = getEdgeWeight(kernelRadius);
		Renderer renderer = new Renderer(pixels, pixels, x, y, x, y, width,
				height, k.getKernelRadius(), edgeWeight, shadowColor);
		renderer.run();
	}

	@Override
	public GaussianKernel getKernel(float kernelRadius) {
		int ceil = (int) (Math.ceil(kernelRadius) + .5);
		int[] array = new int[2 * ceil + 1];
		int edgeWeight = getEdgeWeight(kernelRadius);

		if (edgeWeight == 255) {
			Arrays.fill(array, 1);
		} else {
			Arrays.fill(array, 255);
			array[0] = edgeWeight;
			array[array.length - 1] = edgeWeight;
		}
		return new GaussianKernel(array);
	}

	/**
	 * Return a value from 1-255 indicating how much weight the edge pixels in
	 * the kernel should have.
	 */
	protected int getEdgeWeight(float shadowKernelRadius) {
		int ceil = (int) (Math.ceil(shadowKernelRadius) + .5);
		int floor = (int) shadowKernelRadius;
		int edgeWeight;
		if (floor == ceil) {
			edgeWeight = 255;
		} else {
			float remaining = shadowKernelRadius - floor;
			edgeWeight = (int) (255 * remaining + .5);
			edgeWeight = Math.max(1, edgeWeight);
		}
		return edgeWeight;
	}
}