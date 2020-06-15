package com.pump.image.shadow;

import java.util.Objects;

/**
 * This is adapted from JDesktop's ShadowRenderer class by Sebastien Petrucci
 * and Romain Guy. This is a very fast renderer, but it uses a uniform kernel
 * (instead of a bell-shaped kernel), so the blur can look a little blocky in
 * some cases.
 * <p>
 * This does not support a float-based kernel size; it rounds the kernel size
 * attribute to the nearest int.
 * 
 * @author Romain Guy <romain.guy@mac.com>
 * @author Sebastien Petrucci
 */
public class FastShadowRenderer implements ShadowRenderer {

	static class Renderer_SrcToDst {
		ARGBPixels dst, src;
		int[] dstBuffer, srcBuffer;
		int shadowSize, kernelSize;

		int srcWidth, srcHeight, dstWidth, dstHeight, srcToDstX, srcToDstY;
		int[] aHistory;

		float shadowOpacity;

		public Renderer_SrcToDst(ARGBPixels src, ARGBPixels dst, int srcToDstX,
				int srcToDstY, int kernelSize, float shadowOpacity) {
			this.src = src;
			this.dst = dst;
			this.shadowOpacity = shadowOpacity;
			this.kernelSize = kernelSize;

			this.srcToDstX = srcToDstX;
			this.srcToDstY = srcToDstY;

			shadowSize = kernelSize * 2;

			srcWidth = src.getWidth();
			srcHeight = src.getHeight();

			if (dst == null) {
				dstWidth = srcToDstX + srcWidth + kernelSize;
				dstHeight = srcToDstY + srcHeight + kernelSize;
				dst = new ARGBPixels(dstWidth, dstHeight);
			} else {
				dstWidth = dst.getWidth();
				dstHeight = dst.getHeight();
			}

			if (srcToDstX < kernelSize)
				throw new IllegalArgumentException("srcToDstX = " + srcToDstX
						+ "; kernelSize = " + kernelSize);
			if (srcToDstY < kernelSize)
				throw new IllegalArgumentException("srcToDstY = " + srcToDstY
						+ "; kernelSize = " + kernelSize);

			if (srcToDstX + srcWidth + kernelSize > dstWidth)
				throw new IllegalArgumentException("srcToDstX = " + srcToDstX
						+ "; kernelSize = " + kernelSize + "; srcWidth = "
						+ srcWidth + "; dstWidth = " + dstWidth);
			if (srcToDstY + srcHeight + kernelSize > dstHeight)
				throw new IllegalArgumentException("srcToDstY = " + srcToDstY
						+ "; kernelSize = " + kernelSize + "; srcHeight = "
						+ srcHeight + "; dstHeight = " + dstHeight);

			dstBuffer = dst.getPixels();
			srcBuffer = src.getPixels();

			aHistory = new int[shadowSize];

		}

		public void run() {
			runHorizontalPass();
			runVerticalPass();
		}

		private void runHorizontalPass() {
			float hSumDivider = 1.0f / shadowSize;

			int[] hSumLookup = new int[256 * shadowSize];
			for (int i = 0; i < hSumLookup.length; i++) {
				hSumLookup[i] = (int) (i * hSumDivider);
			}

			int historyIdx, aSum, srcOffset;

			// horizontal pass : extract the alpha mask from the source picture
			// and blur it into the destination picture
			for (int srcY = 0; srcY < srcHeight; srcY++) {

				// first pixels are empty
				for (historyIdx = 0; historyIdx < shadowSize;) {
					aHistory[historyIdx++] = 0;
				}

				aSum = 0;
				historyIdx = 0;
				srcOffset = srcY * srcWidth;
				int dstOffset = (srcToDstY + srcY) * dstWidth + srcToDstX
						- kernelSize;

				// compute the blur average with pixels from the source image
				for (int srcX = 0; srcX < srcWidth; srcX++) {

					int a = hSumLookup[aSum];
					dstBuffer[dstOffset++] = a << 24;

					aSum -= aHistory[historyIdx]; // subtract the oldest pixel
													// from the sum

					// extract the new pixel ...
					a = srcBuffer[srcOffset + srcX] >>> 24;
					aHistory[historyIdx] = a; // ... and store its value into
												// history
					aSum += a; // ... and add its value to the sum

					if (++historyIdx >= shadowSize) {
						historyIdx -= shadowSize;
					}
				}

				// blur the end of the row - no new pixels to grab
				for (int i = 0; i < shadowSize; i++) {

					int a = hSumLookup[aSum];
					dstBuffer[dstOffset++] = a << 24;

					// substract the oldest pixel from the sum ... and nothing
					// new to add !
					aSum -= aHistory[historyIdx];

					if (++historyIdx >= shadowSize) {
						historyIdx -= shadowSize;
					}
				}
			}
		}

		private void runVerticalPass() {
			float vSumDivider = shadowOpacity / shadowSize;

			int[] vSumLookup = new int[256 * shadowSize];
			for (int i = 0; i < vSumLookup.length; i++) {
				vSumLookup[i] = (int) (i * vSumDivider);
			}

			int historyIdx, aSum;

			int left = kernelSize;
			int right = shadowSize - left;

			int yStop = dstHeight - right;
			int lastPixelOffset = right * dstWidth;

			// vertical pass
			for (int x = 0; x < dstWidth; x++) {
				int bufferOffset = x;
				aSum = 0;

				// first pixels are empty
				for (historyIdx = 0; historyIdx < left;) {
					aHistory[historyIdx++] = 0;
				}

				// and then they come from the dstBuffer
				for (int y = 0; y < right; y++, bufferOffset += dstWidth) {
					int a = dstBuffer[bufferOffset] >>> 24; // extract alpha
					aHistory[historyIdx++] = a; // store into history
					aSum += a; // and add to sum
				}

				bufferOffset = x;
				historyIdx = 0;

				// compute the blur average with pixels from the previous pass
				for (int y = 0; y < yStop; y++, bufferOffset += dstWidth) {

					int a = vSumLookup[aSum];
					dstBuffer[bufferOffset] = a << 24; // store alpha value +
														// shadow
														// color

					aSum -= aHistory[historyIdx]; // subtract the oldest pixel
													// from the sum

					a = dstBuffer[bufferOffset + lastPixelOffset] >>> 24; // extract
																			// the
																			// new
																			// pixel
																			// ...
					aHistory[historyIdx] = a; // ... and store its value into
												// history
					aSum += a; // ... and add its value to the sum

					if (++historyIdx >= shadowSize) {
						historyIdx -= shadowSize;
					}
				}

				// blur the end of the column - no pixels to grab anymore
				for (int y = yStop; y < dstHeight; y++, bufferOffset += dstWidth) {

					int a = vSumLookup[aSum];
					dstBuffer[bufferOffset] = a << 24;

					aSum -= aHistory[historyIdx]; // subtract the oldest pixel
													// from the sum

					if (++historyIdx >= shadowSize) {
						historyIdx -= shadowSize;
					}
				}
			}
		}
	}

	static class Renderer_SrcOnly {
		ARGBPixels src;
		int[] srcBuffer;
		int shadowSize, kernelSize;

		int srcWidth, srcHeight, dstWidth, dstHeight, srcX, srcY;
		int[] aHistory;

		int[] divideByShadowSizeLUT;
		float shadowOpacity;

		public Renderer_SrcOnly(ARGBPixels src, int srcX, int srcY,
				int srcWidth, int srcHeight, int kernelSize,
				float shadowOpacity) {
			Objects.requireNonNull(src);

			this.src = src;
			this.shadowOpacity = shadowOpacity;
			this.kernelSize = kernelSize;

			this.srcX = srcX;
			this.srcY = srcY;
			this.srcWidth = srcWidth;
			this.srcHeight = srcHeight;

			shadowSize = kernelSize * 2 + 1;

			dstWidth = src.getWidth();
			dstHeight = src.getHeight();

			if (srcX < kernelSize)
				throw new IllegalArgumentException(
						"srcX = " + srcX + "; kernelSize = " + kernelSize);
			if (srcY < kernelSize)
				throw new IllegalArgumentException(
						"srcY = " + srcY + "; kernelSize = " + kernelSize);

			if (srcX + srcWidth + kernelSize > dstWidth)
				throw new IllegalArgumentException("srcX = " + srcX
						+ "; kernelSize = " + kernelSize + "; srcWidth = "
						+ srcWidth + "; dstWidth = " + dstWidth);
			if (srcY + srcHeight + kernelSize > dstHeight)
				throw new IllegalArgumentException("srcY = " + srcY
						+ "; kernelSize = " + kernelSize + "; srcHeight = "
						+ srcHeight + "; dstHeight = " + dstHeight);

			srcBuffer = src.getPixels();

			aHistory = new int[shadowSize];
			divideByShadowSizeLUT = new int[256 * shadowSize];
			for (int i = 0; i < divideByShadowSizeLUT.length; i++) {
				divideByShadowSizeLUT[i] = (int) (i / shadowSize);
			}
		}

		public void run() {
			runHorizontalPass();
			runVerticalPass();
		}

		private void runHorizontalPass() {
			for (int y = 0; y < srcHeight; y++) {
				int srcOffsetBase = (srcY + y) * dstWidth + srcX;

				int aSum = 0;
				int aHistoryIdx = 0;
				for (int x = 0; x <= kernelSize; x++) {
					int alpha = srcBuffer[srcOffsetBase + x] >>> 24;
					aHistory[aHistoryIdx++] = alpha;
					aSum += alpha;
				}

				int x = 0;
				for (; x < kernelSize; x++) {
					srcBuffer[srcOffsetBase
							+ x] = divideByShadowSizeLUT[aSum] << 24;

					int alpha = srcBuffer[srcOffsetBase + x + kernelSize
							+ 1] >>> 24;
					aSum += alpha;
					aHistory[aHistoryIdx++] = alpha;
				}

				aHistoryIdx = 0;
				for (; x < srcWidth - kernelSize; x++) {
					srcBuffer[srcOffsetBase
							+ x] = divideByShadowSizeLUT[aSum] << 24;

					aSum -= aHistory[aHistoryIdx];

					int alpha = srcBuffer[srcOffsetBase + x + kernelSize
							+ 1] >>> 24;
					aSum += alpha;
					aHistory[aHistoryIdx] = alpha;
					aHistoryIdx = (aHistoryIdx + 1) % shadowSize;
				}

				for (; x < srcWidth; x++) {
					srcBuffer[srcOffsetBase
							+ x] = divideByShadowSizeLUT[aSum] << 24;

					aSum -= aHistory[aHistoryIdx];
					aHistoryIdx = (aHistoryIdx + 1) % shadowSize;
				}
			}
		}

		private void runVerticalPass() {

			int x1 = srcX - kernelSize;
			int x2 = srcX + srcWidth + kernelSize;
			for (int x = x1; x <= x2; x++) {
				int aSum = 0;
				int aHistoryIdx = 0;

				for (int y = 0; y <= kernelSize; y++) {
					int alpha = srcBuffer[(y + srcY) * dstWidth + x] >>> 24;
					aHistory[aHistoryIdx++] = alpha;
					aSum += alpha;
				}

				int y = 0;
				for (; y < kernelSize; y++) {
					srcBuffer[(y + srcY) * dstWidth
							+ x] = divideByShadowSizeLUT[aSum] << 24;

					int alpha = srcBuffer[(y + srcY + kernelSize + 1) * dstWidth
							+ x] >>> 24;
					aSum += alpha;
					aHistory[aHistoryIdx++] = alpha;
				}

				aHistoryIdx = 0;
				for (; y < srcHeight - kernelSize; y++) {
					srcBuffer[(y + srcY) * dstWidth
							+ x] = divideByShadowSizeLUT[aSum] << 24;

					aSum -= aHistory[aHistoryIdx];

					int alpha = srcBuffer[(y + srcY + kernelSize + 1) * dstWidth
							+ x] >>> 24;
					aSum += alpha;
					aHistory[aHistoryIdx] = alpha;
					aHistoryIdx = (aHistoryIdx + 1) % shadowSize;
				}

				for (; y < srcHeight; y++) {
					srcBuffer[(y + srcY) * dstWidth
							+ x] = divideByShadowSizeLUT[aSum] << 24;

					aSum -= aHistory[aHistoryIdx];

					aHistoryIdx = (aHistoryIdx + 1) % shadowSize;
				}
			}
		}
	}

	@Override
	public ARGBPixels createShadow(ARGBPixels src, ARGBPixels dst,
			ShadowAttributes attr) {
		int r = (int) attr.getShadowKernelRadius();
		return createShadow(src, dst, r, r, attr);
	}

	public ARGBPixels createShadow(ARGBPixels src, ARGBPixels dst,
			int srcToDstX, int srcToDstY, ShadowAttributes attr) {
		Renderer_SrcToDst renderer = new Renderer_SrcToDst(src, dst, srcToDstX,
				srcToDstY, getKernel(attr).getKernelRadius(),
				attr.getShadowOpacity());
		renderer.run();
		return renderer.dst;
	}

	public void applyShadow(ARGBPixels pixels, int x, int y, int width,
			int height, ShadowAttributes attr) {
		Renderer_SrcOnly renderer = new Renderer_SrcOnly(pixels, x, y, width,
				height, getKernel(attr).getKernelRadius(),
				attr.getShadowOpacity());
		renderer.run();
	}

	@Override
	public GaussianKernel getKernel(ShadowAttributes attr) {
		int r = (int) attr.getShadowKernelRadius();
		int[] array = new int[2 * r + 1];
		for (int a = 0; a < array.length; a++) {
			array[a] = 1;
		}
		return new GaussianKernel(array);
	}
}