package com.pump.image.shadow;

import java.awt.image.BufferedImage;

/**
 * This is adapted from JDesktop's ShadowRenderer class by Sebastien Petrucci
 * and Romain Guy. This is a very fast renderer, but it uses a uniform kernel
 * (instead of a bell-shaped kernel), so the blur can look a little blocky in
 * some cases.
 * 
 * @author Romain Guy <romain.guy@mac.com>
 * @author Sebastien Petrucci
 */
public class FastShadowRenderer implements ShadowRenderer {

	@Override
	public BufferedImage createShadow(final BufferedImage image,
			ShadowAttributes attr) {
		if (image.getType() != BufferedImage.TYPE_INT_ARGB)
			throw new IllegalArgumentException(
					"only ARGB images are currently supported");

		// Written by Sebastien Petrucci
		int shadowSize = attr.getShadowKernelSize() * 2;

		int srcWidth = image.getWidth();
		int srcHeight = image.getHeight();

		int dstWidth = srcWidth + shadowSize;
		int dstHeight = srcHeight + shadowSize;

		int left = attr.getShadowKernelSize();
		int right = shadowSize - left;

		int yStop = dstHeight - right;

		int[] aHistory = new int[shadowSize];
		int historyIdx;

		int aSum;

		BufferedImage dst = new BufferedImage(dstWidth, dstHeight,
				BufferedImage.TYPE_INT_ARGB);

		int[] dstBuffer = new int[dstWidth * dstHeight];
		int[] srcBuffer = new int[srcWidth * srcHeight];

		image.getRaster().getDataElements(0, 0, srcWidth, srcHeight, srcBuffer);

		int lastPixelOffset = right * dstWidth;
		float hSumDivider = 1.0f / shadowSize;
		float vSumDivider = attr.getShadowOpacity() / shadowSize;

		int[] hSumLookup = new int[256 * shadowSize];
		for (int i = 0; i < hSumLookup.length; i++) {
			hSumLookup[i] = (int) (i * hSumDivider);
		}

		int[] vSumLookup = new int[256 * shadowSize];
		for (int i = 0; i < vSumLookup.length; i++) {
			vSumLookup[i] = (int) (i * vSumDivider);
		}

		int srcOffset;

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
													// will be added in the next
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
				dstBuffer[bufferOffset] = a << 24; // store alpha value + shadow
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

		dst.getRaster().setDataElements(0, 0, dstWidth, dstHeight, dstBuffer);
		return dst;
	}
}