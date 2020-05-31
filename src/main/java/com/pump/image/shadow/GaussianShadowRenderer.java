package com.pump.image.shadow;

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

	@Override
	public ARGBPixels createShadow(ARGBPixels src, ARGBPixels dst,
			ShadowAttributes attr) {
		int k = attr.getShadowKernelSize();
		int shadowSize = k * 2;

		int srcWidth = src.getWidth();
		int srcHeight = src.getHeight();

		int dstWidth = srcWidth + shadowSize;
		int dstHeight = srcHeight + shadowSize;

		if (dst == null)
			dst = new ARGBPixels(dstWidth, dstHeight);

		// TODO: as long as dest.getWidth() is >=, we should be able to
		// accommodate this:
		if (dst.getWidth() != dstWidth)
			throw new IllegalArgumentException(
					dst.getWidth() + " != " + dstWidth);
		if (dst.getHeight() != dstHeight)
			throw new IllegalArgumentException(
					dst.getWidth() + " != " + dstWidth);

		int[] dstBuffer = dst.getPixels();
		int[] srcBuffer = src.getPixels();

		int[] opacityLookup = new int[256];
		float opacity = attr.getShadowOpacity();
		for (int a = 0; a < opacityLookup.length; a++) {
			opacityLookup[a] = (int) (a * opacity);
		}

		Kernel kernel = new Kernel(k);

		int y1 = k;
		int y2 = k + srcHeight;
		int x1 = k;
		int x2 = k + srcWidth;

		// vertical pass:
		for (int dstX = x1; dstX < x2; dstX++) {
			int srcX = dstX - k;
			for (int dstY = y1; dstY < y2; dstY++) {
				int srcY = dstY - k;
				int g = srcY - k;
				int w = 0;
				for (int j = 0; j < kernel.data.length; j++) {
					int kernelY = g + j;
					if (kernelY >= 0 && kernelY < srcHeight) {
						int argb = srcBuffer[srcX + kernelY * srcWidth];
						int alpha = argb >>> 24;
						w += alpha * kernel.data[j];
					}
				}
				w = w / kernel.sum;
				dstBuffer[dstY * dstWidth + dstX] = w;
			}
		}

		// horizontal pass:
		int[] row = new int[dstWidth];
		for (int dstY = 0; dstY < dstHeight; dstY++) {
			System.arraycopy(dstBuffer, dstY * dstWidth, row, 0, row.length);
			for (int dstX = 0; dstX < dstWidth; dstX++) {
				int w = 0;
				for (int j = 0; j < kernel.data.length; j++) {
					int kernelX = dstX - k + j;
					if (kernelX >= 0 && kernelX < dstWidth) {
						w += row[kernelX] * kernel.data[j];
					}
				}
				w = w / kernel.sum;
				dstBuffer[dstY * dstWidth + dstX] = opacityLookup[w] << 24;
			}
		}

		return dst;

	}

}
