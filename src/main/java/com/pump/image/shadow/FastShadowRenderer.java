package com.pump.image.shadow;

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
		ARGBPixels dst, src;
		int[] dstBuffer, srcBuffer;
		int shadowSize, kernelSize;

		int srcWidth, srcHeight, dstWidth, dstHeight;
		int[] aHistory;

		float shadowOpacity;

		public Renderer(ARGBPixels src, ARGBPixels dst, int kernelSize,
				float shadowOpacity) {
			this.src = src;
			this.dst = dst;
			this.shadowOpacity = shadowOpacity;
			this.kernelSize = kernelSize;

			if (dst == null)
				dst = new ARGBPixels(dstWidth, dstHeight);

			dstBuffer = dst.getPixels();
			srcBuffer = src.getPixels();

			// Written by Sebastien Petrucci
			shadowSize = kernelSize * 2;

			srcWidth = src.getWidth();
			srcHeight = src.getHeight();

			dstWidth = srcWidth + shadowSize;
			dstHeight = srcHeight + shadowSize;

			// TODO: as long as dest.getWidth() is >=, we should be able to
			// accommodate this:
			if (dst.getWidth() != dstWidth)
				throw new IllegalArgumentException(
						dst.getWidth() + " != " + dstWidth);
			if (dst.getHeight() != dstHeight)
				throw new IllegalArgumentException(
						dst.getWidth() + " != " + dstWidth);

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

			int left = kernelSize;

			// horizontal pass : extract the alpha mask from the source picture
			// and blur it into the destination picture
			for (int srcY = 0, dstOffset = left
					* dstWidth; srcY < srcHeight; srcY++) {

				// first pixels are empty
				for (historyIdx = 0; historyIdx < shadowSize;) {
					aHistory[historyIdx++] = 0;
				}

				aSum = 0;
				historyIdx = 0;
				srcOffset = srcY * srcWidth;

				// compute the blur average with pixels from the source image
				for (int srcX = 0; srcX < srcWidth; srcX++) {

					int a = hSumLookup[aSum];
					dstBuffer[dstOffset++] = a << 24; // store the alpha value
														// only the shadow color
														// will be added in the
														// next
														// pass

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
			for (int x = 0, bufferOffset = 0; x < dstWidth; x++, bufferOffset = x) {

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

				// compute the blur avera`ge with pixels from the previous pass
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

	@Override
	public ARGBPixels createShadow(ARGBPixels src, ARGBPixels dst,
			ShadowAttributes attr) {
		Renderer renderer = new Renderer(src, dst,
				getKernel(attr).getKernelRadius(), attr.getShadowOpacity());
		renderer.run();
		return renderer.dst;
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