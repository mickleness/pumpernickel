package com.pump.image.shadow;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * This renderer uses a Gaussian kernel to blur a shadow.
 */
public class GaussianShadowRenderer implements ShadowRenderer {

	static class Renderer {
		class VerticalPass implements Callable<Void> {
			int minX, maxX;

			public VerticalPass(int minX, int maxX) {
				this.minX = minX;
				this.maxX = maxX;
			}

			@Override
			public Void call() {
				int maxSum = kernelSum * 255;
				for (int dstX = minX, srcX = minX
						- k; dstX < maxX; dstX++, srcX++) {
					int prevSum = -1;
					for (int dstY = 0, g = dstY - 2 * k, z = srcX + (g
							+ kernel.length - 1)
							* srcWidth; dstY < dstHeight; dstY++, g++, z += srcWidth) {
						int w;
						if (z >= 0 && z < srcBuffer.length) {
							w = (srcBuffer[z] >>> 24);
						} else {
							w = 0;
						}
						if (prevSum == 0 && w == 0) {
							// leave w as 0
						} else if (prevSum == maxSum && w == 255) {
							// leave w as 255
						} else {
							w = w * kernel[kernel.length - 1];
							for (int j = 0, kernelY = g + j; j < kernel.length
									- 1; j++, kernelY++) {
								if (kernelY >= 0 && kernelY < srcHeight) {
									w += (srcBuffer[srcX
											+ kernelY * srcWidth] >>> 24)
											* kernel[j];
								}
							}
							prevSum = w;
							w = w / kernelSum;
						}
						dstBuffer[dstY * dstWidth + dstX] = w;
					}
				}
				return null;
			}
		}

		class HorizontalPass implements Callable<Void> {
			int minY, maxY;

			public HorizontalPass(int minY, int maxY) {
				this.minY = minY;
				this.maxY = maxY;
			}

			@Override
			public Void call() {
				int[] row = new int[dstWidth];
				int maxSum = kernelSum * 255;

				for (int dstY = minY; dstY < maxY; dstY++) {
					System.arraycopy(dstBuffer, dstY * dstWidth, row, 0,
							row.length);
					int prevSum = -1;
					for (int dstX = 0, z = dstX - k + kernel.length
							- 1; dstX < dstWidth; dstX++, z++) {
						int w;
						if (z >= 0 && z < row.length) {
							w = row[z];
						} else {
							w = 0;
						}
						if (prevSum == 0 && w == 0) {
							// leave w as 0
						} else if (prevSum == maxSum && w == 255) {
							// leave w as 255
						} else {
							w = w * kernel[kernel.length - 1];
							for (int j = 0, kernelX = dstX - k
									+ j; j < kernel.length
											- 1; j++, kernelX++) {
								if (kernelX >= 0 && kernelX < dstWidth) {
									w += row[kernelX] * kernel[j];
								}
							}
							prevSum = w;
							w = w / kernelSum;
						}
						dstBuffer[dstY * dstWidth + dstX] = opacityLookup[w];
					}
				}
				return null;
			}
		}

		static ExecutorService executor = Executors
				.newCachedThreadPool(new ThreadFactory() {
					int ctr = 0;

					@Override
					public Thread newThread(Runnable r) {
						return new Thread(r,
								"GaussianShadowRenderer-" + (ctr++));
					}
				});

		final int k;
		final int srcWidth, srcHeight, dstWidth, dstHeight;
		volatile int[] dstBuffer;
		final int[] srcBuffer;
		final int[] kernel;
		final int kernelSum;
		int[] opacityLookup = new int[256];

		public Renderer(ARGBPixels srcPixels, ARGBPixels dstPixels,
				GaussianKernel kernel, Color shadowColor) {
			k = kernel.getKernelRadius();
			int shadowSize = k * 2;

			srcWidth = srcPixels.getWidth();
			srcHeight = srcPixels.getHeight();

			dstWidth = srcWidth + shadowSize;
			dstHeight = srcHeight + shadowSize;

			dstBuffer = dstPixels.getPixels();
			srcBuffer = srcPixels.getPixels();

			this.kernel = kernel.getArray();
			kernelSum = kernel.getArraySum();

			int rgb = shadowColor.getRGB() & 0xffffff;
			int alpha = shadowColor.getAlpha();
			for (int a = 0; a < opacityLookup.length; a++) {
				int newAlpha = (int) (a * alpha / 255);
				opacityLookup[a] = (newAlpha << 24) + rgb;
			}
		}

		public void run() throws InterruptedException {
			int x1 = k;
			int x2 = k + srcWidth;

			int clusterSize = 16;
			List<VerticalPass> verticalPasses = new ArrayList<>(
					(x2 - x1) / clusterSize + 1);
			for (int x = x1; x < x2; x += clusterSize) {
				int myClusterSize = Math.min(clusterSize, x2 - x);
				verticalPasses.add(new VerticalPass(x, x + myClusterSize));
			}

			executor.invokeAll(verticalPasses);

			List<HorizontalPass> horizontalPasses = new ArrayList<>(
					dstHeight / clusterSize + 1);
			for (int y = 0; y < dstHeight; y += clusterSize) {
				int myClusterSize = Math.min(clusterSize, dstHeight - y);
				horizontalPasses.add(new HorizontalPass(y, y + myClusterSize));
			}

			executor.invokeAll(horizontalPasses);
		}
	}

	@Override
	public ARGBPixels createShadow(ARGBPixels src, ARGBPixels dst,
			float kernelRadius, Color shadowColor) {

		int k = getKernel(kernelRadius).getKernelRadius();
		if (k == 0) {
			return createUnblurredShadow(src, dst, 0, 0, 0, 0, src.getWidth(),
					src.getHeight(), shadowColor);
		}
		if (dst == null)
			dst = new ARGBPixels(src.getWidth() + 2 * k,
					src.getHeight() + 2 * k);
		Renderer r = new Renderer(src, dst, getKernel(kernelRadius),
				shadowColor);
		try {
			r.run();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return dst;
	}

	/**
	 * This creates a shadow layer with no blurring. This basically copies the
	 * src to the dest but it replaces the RGB data.
	 */
	static ARGBPixels createUnblurredShadow(ARGBPixels src, ARGBPixels dst,
			int srcX, int srcY, int dstX, int dstY, int width, int height,
			Color shadowColor) {
		if (dst == null)
			dst = new ARGBPixels(width, height);
		int srcIndex = 0;
		int dstIndex = 0;
		int[] srcPixels = src.getPixels();
		int[] dstPixels = dst.getPixels();

		int shadowAlpha = shadowColor.getAlpha();
		int shadowRGB = shadowColor.getRGB() & 0xffffff;

		int[] shadowLUT = new int[256];
		for (int a = 0; a < shadowLUT.length; a++) {
			int alpha = a * shadowAlpha / 255;
			shadowLUT[a] = (alpha << 24) + shadowRGB;
		}

		for (int y = 0; y < height; y++) {
			srcIndex = y * width + srcX;
			dstIndex = y * width + dstX;
			for (int x = 0; x < width; x++) {
				int inARGB = srcPixels[srcIndex];
				int inAlpha = (inARGB >> 24) & 0xff;
				int dstARGB = shadowLUT[inAlpha];
				dstPixels[dstIndex] = dstARGB;

				srcIndex++;
				dstIndex++;
			}
		}
		return dst;
	}

	@Override
	public GaussianKernel getKernel(float kernelRadius) {
		return new GaussianKernel(kernelRadius);
	}
}
