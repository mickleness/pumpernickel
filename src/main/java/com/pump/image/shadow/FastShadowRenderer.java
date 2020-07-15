package com.pump.image.shadow;

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
 * weighted edges has to do with giving the CompositeShadowRenderer a greater
 * depth of support.)
 * <p>
 * This supports using the same int array as both the source and the
 * destination.
 * <p>
 * This was originally based on JDesktop's ShadowRenderer class by Sebastien
 * Petrucci and Romain Guy, but the current implementation is a complete rewrite
 * from their original code.
 * <p>
 * This implementation supports assigning an (x,y) coordinate for both the
 * source and dest pixels to read/write. This lets us support several
 * consecutive passes using the same int arrays.
 * <p>
 * This class does not currently support floating-point kernel radiuses. The
 * kernel radius is rounded to the nearest int.
 */
public class FastShadowRenderer implements ShadowRenderer {

	/**
	 * This helper class runs the two separate blurs.
	 */
	static class Renderer {
		ARGBPixels src, dst;
		int[] srcBuffer, dstBuffer;
		int shadowSize, kernelSize;
		float weightedShadowSize;

		int width, height;
		int dstX, dstY, srcX, srcY;
		int edgeWeight, edgeWeightComplement;
		int[] aHistory;

		int[] divideByShadowSizeLUT;
		float shadowOpacity;

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
				int edgeWeight, float shadowOpacity) {
			Objects.requireNonNull(src);

			if (dst == null) {
				dst = new ARGBPixels(dstX + width + kernelSize,
						dstY + height + kernelSize);
			}
			if (shadowOpacity < 0 || shadowOpacity > 1)
				throw new IllegalArgumentException("Shadow opacity ("
						+ shadowOpacity + ") should be between zero and one.");

			if (edgeWeight < 1 || edgeWeight > 255)
				throw new IllegalArgumentException(
						"edgeWeight should be between [1, 255]");

			this.edgeWeight = edgeWeight;
			edgeWeightComplement = 255 - edgeWeight;

			this.src = src;
			this.dst = dst;
			this.shadowOpacity = shadowOpacity;
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
			runVerticalBlur();
			runHorizontalBlur();
		}

		/**
		 * This blurs pixels horizontally from the dstBuffer into the dstBuffer.
		 * This assumes that {@link #runVerticalBlur()} has already been called,
		 * and the opacity information is stored in the rightmost byte of each
		 * pixel. (So it is stored in the blue channel, and all other channels
		 * are empty).
		 */
		void runHorizontalBlur() {
			int x1 = dstX - kernelSize;
			int x2 = dstX + kernelSize + 1;
			int x3 = dstX + width - kernelSize;
			int x4 = dstX + width + kernelSize;

			if (!(x1 <= x2 && x2 <= x3 && x3 <= x4 && this.edgeWeight == 255)) {
				runHorizontalBlur_unoptimized();
				return;
			}

			// in addition to being a divisor LUT, this also takes into account
			// the final opacity multiplier and shifts the result back into the
			// alpha channel
			int shadowMultiplier = (int) (shadowOpacity * 0xff);
			int shadowDivisor = shadowSize * 0xff;
			for (int i = 0; i < divideByShadowSizeLUT.length; i++) {
				divideByShadowSizeLUT[i] = ((i * shadowMultiplier
						/ shadowDivisor) & 0xff) << 24;
			}

			int dstWidth = dst.getWidth();

			int y1 = dstY - kernelSize;
			int y2 = dstY + height + kernelSize;

			int readIndexBase = y1 * dstWidth + x1 + kernelSize;
			int writeIndexBase = y1 * dstWidth + x1;
			for (int y = y1; y < y2; y++) {
				int aHistoryIdx = -1;
				int aSum = 0;

				int x = x1;
				int readIndex = readIndexBase;
				int writeIndex = writeIndexBase;
				while (x < x2) {
					int alpha = dstBuffer[readIndex];
					aHistory[++aHistoryIdx] = alpha;
					aSum += alpha;
					dstBuffer[writeIndex] = divideByShadowSizeLUT[aSum];
					readIndex++;
					writeIndex++;
					x++;
				}

				while (x < x3) {
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

				while (x < x4) {
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
		}

		private void runHorizontalBlur_unoptimized() {
			int x1 = dstX - kernelSize;
			int x2 = dstX + width + kernelSize;

			int dstWidth = dst.getWidth();

			int y1 = dstY - kernelSize;
			int y2 = dstY + height + kernelSize;

			int readIndexBase = y1 * dstWidth + x1 + kernelSize;
			int writeIndexBase = y1 * dstWidth + x1;

			// in addition to being a divisor LUT, this also takes into account
			// the final opacity multiplier and shifts the result back into the
			// alpha channel
			int shadowMultiplier = (int) (shadowOpacity * 0xff);
			int shadowDivisor = (int) (weightedShadowSize * 0xff);
			for (int i = 0; i < divideByShadowSizeLUT.length; i++) {
				divideByShadowSizeLUT[i] = ((i * shadowMultiplier
						/ shadowDivisor) & 0xff) << 24;
			}

			for (int y = y1; y < y2; y++) {
				int aHistoryIdx = -1;
				int aSum = 0;
				Arrays.fill(aHistory, 0);
				int nextAlphaHistoryIndex = 0;

				int readIndex = readIndexBase;
				int writeIndex = writeIndexBase;
				for (int x = x1; x < x2; x++) {
					aHistoryIdx = nextAlphaHistoryIndex;

					int alpha = readIndex < dstBuffer.length
							? dstBuffer[readIndex]
							: 0;
					aSum += alpha - aHistory[aHistoryIdx];
					aHistory[aHistoryIdx] = alpha;

					nextAlphaHistoryIndex++;
					if (nextAlphaHistoryIndex == shadowSize)
						nextAlphaHistoryIndex = 0;

					int z = aSum
							- aHistory[aHistoryIdx] * edgeWeightComplement / 255
							- aHistory[nextAlphaHistoryIndex]
									* edgeWeightComplement / 255;
					try {
						dstBuffer[writeIndex] = divideByShadowSizeLUT[z];
					} catch(RuntimeException e) {
						throw e;
					}

					readIndex++;
					writeIndex++;
				}

				writeIndexBase += dstWidth;
				readIndexBase += dstWidth;
			}
		}

		/**
		 * This blurs pixels vertically from the srcBuffer into the dstBuffer.
		 * <p>
		 * The resulting value (in dstBuffer) is the unshifted alpha value. For
		 * example: if the source pixels are all 0xff000000 (opaque black), then
		 * will create destination pixels that are all 0xff. (Technically this
		 * is the blue channel, but {@link #runHorizontalBlur()} consults the
		 * blue channel and shifts it back to the alpha channel.
		 */
		void runVerticalBlur() {

			int y1 = dstY - kernelSize;
			int y2 = dstY + kernelSize + 1;
			int y3 = dstY + height - kernelSize;
			int y4 = dstY + height + kernelSize;

			if (!(y1 <= y2 && y2 <= y3 && y3 <= y4 && edgeWeight == 255)) {
				runVerticalBlur_unoptimized();
				return;
			}

			for (int i = 0; i < divideByShadowSizeLUT.length; i++) {
				divideByShadowSizeLUT[i] = (int) (i / shadowSize);
			}

			int endX = dstX + width;
			int srcWidth = src.getWidth();
			int dstWidth = dst.getWidth();

			// avoid multiplication in our loop:
			int srcIndexBase = srcY * srcWidth - dstX + srcX;
			int dstIndexBase = y1 * dstWidth;

			for (int x = dstX; x < endX; x++) {
				int aHistoryIdx = -1;
				int aSum = 0;

				int y = y1;
				int srcIndex = srcIndexBase + x;
				int dstIndex = dstIndexBase + x;
				while (y < y2) {
					int alpha = srcBuffer[srcIndex] >>> 24;
					aHistory[++aHistoryIdx] = alpha;
					aSum += alpha;
					dstBuffer[dstIndex] = divideByShadowSizeLUT[aSum];
					dstIndex += dstWidth;
					srcIndex += srcWidth;
					y++;
				}

				while (y < y3) {
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

				while (y < y4) {
					aHistoryIdx++;
					if (aHistoryIdx == shadowSize)
						aHistoryIdx = 0;
					aSum -= aHistory[aHistoryIdx];
					dstBuffer[dstIndex] = divideByShadowSizeLUT[aSum];
					dstIndex += dstWidth;
					y++;
				}
			}
		}

		/**
		 * This addresses fringe cases where the columns are unusually short.
		 * This method uses a few more if/thens and is slightly less efficient,
		 * but since the columns are short it probably won't matter much.
		 */
		private void runVerticalBlur_unoptimized() {
			int y1 = dstY - kernelSize;
			int y2 = dstY + height + kernelSize;
			int loopCount = y2 - y1;

			int endX = dstX + width;
			int srcWidth = src.getWidth();
			int dstWidth = dst.getWidth();

			// avoid multiplication in our loop:
			int srcIndexBase = srcY * srcWidth - dstX + srcX;
			int dstIndexBase = y1 * dstWidth;
			int whenAddingStops = height + 2 * kernelSize - shadowSize;

			for (int i = 0; i < divideByShadowSizeLUT.length; i++) {
				divideByShadowSizeLUT[i] = (int) (i / weightedShadowSize);
			}

			for (int x = dstX; x < endX; x++) {
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
							- aHistory[aHistoryIdx] * edgeWeightComplement / 255
							- aHistory[nextAlphaHistoryIndex]
									* edgeWeightComplement / 255];

					dstIndex += dstWidth;
				}
			}
		}
	}

	@Override
	public ARGBPixels createShadow(ARGBPixels src, ARGBPixels dst,
			ShadowAttributes attr) {
		int r = getKernel(attr).getKernelRadius();
		return createShadow(src, dst, r, r, attr);
	}

	public ARGBPixels createShadow(ARGBPixels src, ARGBPixels dst,
			int srcToDstX, int srcToDstY, ShadowAttributes attr) {
		GaussianKernel k = getKernel(attr);
		int edgeWeight = getEdgeWeight(attr.getShadowKernelRadius());
		Renderer renderer = new Renderer(src, dst, 0, 0, srcToDstX, srcToDstY,
				src.getWidth(), src.getHeight(), k.getKernelRadius(),
				edgeWeight, attr.getShadowOpacity());
		renderer.run();
		return renderer.dst;
	}

	public void applyShadow(ARGBPixels pixels, int x, int y, int width,
			int height, ShadowAttributes attr) {
		GaussianKernel k = getKernel(attr);
		int edgeWeight = getEdgeWeight(attr.getShadowKernelRadius());
		Renderer renderer = new Renderer(pixels, pixels, x, y, x, y, width,
				height, k.getKernelRadius(), edgeWeight,
				attr.getShadowOpacity());
		renderer.run();
	}

	@Override
	public GaussianKernel getKernel(ShadowAttributes attr) {
		int ceil = (int) (Math.ceil(attr.getShadowKernelRadius()) + .5);
		int[] array = new int[2 * ceil + 1];
		int edgeWeight = getEdgeWeight(attr.getShadowKernelRadius());

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