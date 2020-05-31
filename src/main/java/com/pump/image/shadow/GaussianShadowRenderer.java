package com.pump.image.shadow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This renderer uses a Gaussian kernel to blur a shadow.
 */
public class GaussianShadowRenderer implements ShadowRenderer {

	static class Kernel {
		int[] data;
		int sum = 0;

		public Kernel(int kernelSize) {
			data = new int[2 * kernelSize + 1];
			double sigma = kernelSize / 3.0;
			double k = 1f / (2 * Math.PI * sigma * sigma);

			int y = data.length / 2;
			for (int b = 0; b < data.length; b++) {
				int x = b - kernelSize;

				double exp = -(x * x + y * y) / (2 * sigma * sigma);
				double z = k * Math.pow(Math.E, exp);
				data[b] = (int) (z * 1000000);
				sum += data[b];
			}
		}
	}

	static class Renderer {
		class VerticalPass implements Callable<Void> {
			int passX1, passX2;

			public VerticalPass(int passX1, int passX2) {
				this.passX1 = passX1;
				this.passX2 = passX2;
			}

			@Override
			public Void call() {
				int y1 = k;
				int y2 = k + srcHeight;

				for (int dstX = passX1; dstX < passX2; dstX++) {
					int srcX = dstX - k;

					int y1k = y1 + k;
					int y2k = y2 - k;

					for (int dstY = y1; dstY < y1k; dstY++) {
						int srcY = dstY - k;
						int kernelYStart = srcY - k;
						int w = 0;
						for (int j = k + 1; j < kernel.data.length; j++) {
							w += (srcBuffer[srcX + kernelYStart
									+ j * srcWidth] >>> 24) * kernel.data[j];
						}
						w = w / kernel.sum;
						dstBuffer[dstY * dstWidth + dstX] = w;
					}

					int prevAlpha = -1;
					for (int dstY = y1k; dstY < y2k; dstY++) {
						int w = (srcBuffer[srcX
								+ (dstY - k - k + kernel.data.length - 1)
										* srcWidth] >>> 24);

						if (prevAlpha == 0 && w == 0) {
							w = 0;
						} else if (prevAlpha == 255 && w == 255) {
							w = 255;
						} else {
							w = w * kernel.data[kernel.data.length - 1];
							int srcY = dstY - k;
							int kernelYStart = srcY - k;
							for (int j = 0; j < kernel.data.length - 1; j++) {
								w += (srcBuffer[srcX
										+ (kernelYStart + j) * srcWidth] >>> 24)
										* kernel.data[j];
							}
							w = w / kernel.sum;
							prevAlpha = w;
						}
						dstBuffer[dstY * dstWidth + dstX] = w;
					}

					for (int dstY = y2k; dstY < y2; dstY++) {
						int srcY = dstY - k;
						int kernelYStart = srcY - k;
						int w = 0;
						for (int j = 0; j < k + 1; j++) {
							w += (srcBuffer[srcX
									+ (kernelYStart + j) * srcWidth] >>> 24)
									* kernel.data[j];
						}
						w = w / kernel.sum;
						dstBuffer[dstY * dstWidth + dstX] = w;
					}
				}
				return null;
			}
		}

		class HorizontalPass implements Callable<Void> {
			int passY1, passY2;

			public HorizontalPass(int passY1, int passY2) {
				this.passY1 = passY1;
				this.passY2 = passY2;
			}

			@Override
			public Void call() {
				int[] row = new int[dstWidth];
				for (int dstY = passY1; dstY < passY2; dstY++) {
					System.arraycopy(dstBuffer, dstY * dstWidth, row, 0,
							row.length);

					int x1k = k;
					int x2k = dstWidth - k;

					for (int dstX = 0; dstX < x1k; dstX++) {
						int w = 0;
						for (int j = k + 1; j < kernel.data.length; j++) {
							int kernelX = dstX - k + j;
							w += row[kernelX] * kernel.data[j];
						}
						w = w / kernel.sum;
						dstBuffer[dstY * dstWidth
								+ dstX] = opacityLookup[w] << 24;
					}

					int prevAlpha = -1;
					for (int dstX = x1k; dstX < x2k; dstX++) {
						int w = row[dstX - k + kernel.data.length - 1];

						if (prevAlpha == 0 && w == 0) {
							w = 0;
						} else if (prevAlpha == 255 && w == 255) {
							w = 255;
						} else {
							w = w * kernel.data[kernel.data.length - 1];

							for (int j = 0; j < kernel.data.length - 1; j++) {
								int kernelX = dstX - k + j;
								w += row[kernelX] * kernel.data[j];
							}
							w = w / kernel.sum;
							prevAlpha = w;
						}
						dstBuffer[dstY * dstWidth
								+ dstX] = opacityLookup[w] << 24;
					}

					for (int dstX = x2k; dstX < dstWidth; dstX++) {
						int w = 0;
						for (int j = 0; j < k + 1; j++) {
							int kernelX = dstX - k + j;
							w += row[kernelX] * kernel.data[j];
						}
						w = w / kernel.sum;
						dstBuffer[dstY * dstWidth
								+ dstX] = opacityLookup[w] << 24;
					}
				}
				return null;
			}
		}

		static ExecutorService executor = Executors.newCachedThreadPool();

		final int k;
		final int srcWidth, srcHeight, dstWidth, dstHeight;
		volatile int[] dstBuffer;
		final int[] srcBuffer;
		final Kernel kernel;
		int[] opacityLookup = new int[256];

		public Renderer(ARGBPixels srcPixels, ARGBPixels dstPixels,
				ShadowAttributes attr) {
			k = attr.getShadowKernelSize();
			int shadowSize = k * 2;

			srcWidth = srcPixels.getWidth();
			srcHeight = srcPixels.getHeight();

			dstWidth = srcWidth + shadowSize;
			dstHeight = srcHeight + shadowSize;

			dstBuffer = dstPixels.getPixels();
			srcBuffer = srcPixels.getPixels();

			kernel = new Kernel(k);

			float opacity = attr.getShadowOpacity();
			for (int a = 0; a < opacityLookup.length; a++) {
				opacityLookup[a] = (int) (a * opacity);
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
			ShadowAttributes attr) {
		int k = attr.getShadowKernelSize();
		if (dst == null)
			dst = new ARGBPixels(src.getWidth() + 2 * k,
					src.getHeight() + 2 * k);
		Renderer r = new Renderer(src, dst, attr);
		try {
			r.run();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return dst;
	}
}
