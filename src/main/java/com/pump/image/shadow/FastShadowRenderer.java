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

	static class Renderer {
		ARGBPixels src, dst;
		int[] srcBuffer, dstBuffer;
		int shadowSize, kernelSize;

		int width, height;
		int dstX, dstY, srcX, srcY;
		int[] aHistory;

		int[] divideByShadowSizeLUT;
		float shadowOpacity;

		public Renderer(ARGBPixels src, ARGBPixels dst, int srcX, int srcY,
				int dstX, int dstY, int width, int height, int kernelSize,
				float shadowOpacity) {
			Objects.requireNonNull(src);

			if (dst == null) {
				dst = new ARGBPixels(dstX + width + kernelSize,
						dstY + height + kernelSize);
			}

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

			srcBuffer = src.getPixels();
			dstBuffer = dst.getPixels();

			aHistory = new int[shadowSize];
			divideByShadowSizeLUT = new int[256 * shadowSize];
			for (int i = 0; i < divideByShadowSizeLUT.length; i++) {
				divideByShadowSizeLUT[i] = (int) (i / shadowSize);
			}

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
			runHorizontalPass();
			runVerticalPass();
		}

		private void runHorizontalPass() {
			int srcWidth = src.getWidth();
			int dstWidth = dst.getWidth();

			for (int y = 0; y < height; y++) {
				int srcOffsetBase = (srcY + y) * srcWidth + srcX;
				int dstOffsetBase = (dstY + y) * dstWidth + dstX;

				int aSum = 0;
				int aHistoryIdx = 0;

				int srcOffsetBasePlusKernel = srcOffsetBase + kernelSize;
				int dstOffsetBaseMinusKernel = dstOffsetBase - kernelSize;
				for (int srcIndex = srcOffsetBase, dstIndex = dstOffsetBaseMinusKernel; srcIndex <= srcOffsetBasePlusKernel; srcIndex++, dstIndex++) {
					int alpha = srcBuffer[srcIndex] >>> 24;
					aHistory[aHistoryIdx++] = alpha;
					aSum += alpha;

					dstBuffer[dstIndex] = divideByShadowSizeLUT[aSum] << 24;
				}

				int x = 0;
				for (; x < kernelSize; x++) {
					dstBuffer[dstOffsetBase
							+ x] = divideByShadowSizeLUT[aSum] << 24;

					int alpha = srcBuffer[srcOffsetBase + x + kernelSize
							+ 1] >>> 24;
					aSum += alpha;
					aHistory[aHistoryIdx++] = alpha;
				}

				aHistoryIdx = 0;
				for (; x < width - kernelSize; x++) {
					dstBuffer[dstOffsetBase
							+ x] = divideByShadowSizeLUT[aSum] << 24;

					aSum -= aHistory[aHistoryIdx];

					// TODO: refactor
					if (srcOffsetBase + x + kernelSize + 1 < srcBuffer.length) {
						int alpha = srcBuffer[srcOffsetBase + x + kernelSize
								+ 1] >>> 24;
						aSum += alpha;
						aHistory[aHistoryIdx] = alpha;
					} else {
						aHistory[aHistoryIdx] = 0;
					}
					aHistoryIdx = (aHistoryIdx + 1) % shadowSize;
				}

				for (; x < width; x++) {
					dstBuffer[dstOffsetBase
							+ x] = divideByShadowSizeLUT[aSum] << 24;

					aSum -= aHistory[aHistoryIdx];
					aHistoryIdx = (aHistoryIdx + 1) % shadowSize;
				}

				for (int j = 0; j < kernelSize; j++) {
					dstBuffer[dstOffsetBase + x
							+ j] = divideByShadowSizeLUT[aSum] << 24;

					aSum -= aHistory[aHistoryIdx];
					aHistoryIdx = (aHistoryIdx + 1) % shadowSize;
				}
			}
		}

		private void runVerticalPass() {
			// in this (second) pass we don't need to consult srcBuffer at all

			int x1 = dstX - kernelSize;
			int x2 = dstX + width + kernelSize;
			int dstWidth = dst.getWidth();
			for (int x = x1; x <= x2; x++) {
				int aSum = 0;
				int aHistoryIdx = 0;

				for (int y = 0; y <= kernelSize; y++) {
					int alpha = dstBuffer[(y + dstY) * dstWidth + x] >>> 24;
					aHistory[aHistoryIdx++] = alpha;
					aSum += alpha;

					dstBuffer[(y + dstY - kernelSize) * dstWidth
							+ x] = divideByShadowSizeLUT[aSum] << 24;
				}

				int y = 0;
				for (; y < kernelSize; y++) {
					dstBuffer[(y + dstY) * dstWidth
							+ x] = divideByShadowSizeLUT[aSum] << 24;

					int alpha = dstBuffer[(y + dstY + kernelSize + 1) * dstWidth
							+ x] >>> 24;
					aSum += alpha;
					aHistory[aHistoryIdx++] = alpha;
				}

				aHistoryIdx = 0;
				for (; y < height - kernelSize; y++) {
					dstBuffer[(y + dstY) * dstWidth
							+ x] = divideByShadowSizeLUT[aSum] << 24;

					aSum -= aHistory[aHistoryIdx];

					// TODO: refactor
					if ((y + dstY + kernelSize + 1) * dstWidth
							+ x < dstBuffer.length) {
						int alpha = dstBuffer[(y + dstY + kernelSize + 1)
								* dstWidth + x] >>> 24;
						aSum += alpha;
						aHistory[aHistoryIdx] = alpha;
					} else {
						aHistory[aHistoryIdx] = 0;
					}
					aHistoryIdx = (aHistoryIdx + 1) % shadowSize;
				}

				for (; y < height; y++) {
					dstBuffer[(y + dstY) * dstWidth
							+ x] = divideByShadowSizeLUT[aSum] << 24;

					aSum -= aHistory[aHistoryIdx];

					aHistoryIdx = (aHistoryIdx + 1) % shadowSize;
				}

				for (int j = 0; j < kernelSize; j++) {
					if ((y + dstY + j) * dstWidth + x < dstBuffer.length) {
						dstBuffer[(y + dstY + j) * dstWidth
								+ x] = divideByShadowSizeLUT[aSum] << 24;
					}
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
		Renderer renderer = new Renderer(src, dst, 0, 0, srcToDstX, srcToDstY,
				src.getWidth(), src.getHeight(),
				getKernel(attr).getKernelRadius(), attr.getShadowOpacity());
		renderer.run();
		return renderer.dst;
	}

	public void applyShadow(ARGBPixels pixels, int x, int y, int width,
			int height, ShadowAttributes attr) {
		Renderer renderer = new Renderer(pixels, pixels, x, y, x, y, width,
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