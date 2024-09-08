package com.pump.image.shadow;

import java.awt.*;

/**
 * This is a simple (and slow) implementation of the ShadowRenderer. I recommend
 * using the {@link DoubleBoxShadowRenderer} instead.
 */
public class SimpleGaussianShadowRenderer implements ShadowRenderer {

    @Override
    public ARGBPixels createShadow(ARGBPixels src, ARGBPixels dst,
                                   float kernelRadius, Color shadowColor) {
        GaussianKernel kernel = getKernel(kernelRadius);
        int k = kernel.getKernelRadius();

        int shadowSize = k * 2;

        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();

        int dstWidth = srcWidth + shadowSize;
        int dstHeight = srcHeight + shadowSize;

        if (dst == null)
            dst = new ARGBPixels(dstWidth, dstHeight);

        if (dst.getWidth() != dstWidth)
            throw new IllegalArgumentException(
                    dst.getWidth() + " != " + dstWidth);
        if (dst.getHeight() != dstHeight)
            throw new IllegalArgumentException(
                    dst.getWidth() + " != " + dstWidth);

        int[] dstBuffer = dst.getPixels();
        int[] srcBuffer = src.getPixels();

        int[] opacityLookup = new int[256];

        {
            int rgb = shadowColor.getRGB() & 0xffffff;
            int alpha = shadowColor.getAlpha();
            for (int a = 0; a < opacityLookup.length; a++) {
                int newAlpha = (int) (a * alpha / 255);
                opacityLookup[a] = (newAlpha << 24) + rgb;
            }
        }

        int x1 = k;
        int x2 = k + srcWidth;

        int[] kernelArray = kernel.getArray();
        int kernelSum = kernel.getArraySum();

        // vertical pass:
        for (int dstX = x1; dstX < x2; dstX++) {
            int srcX = dstX - k;
            for (int dstY = 0; dstY < dstHeight; dstY++) {
                int srcY = dstY - k;
                int g = srcY - k;
                int w = 0;
                for (int j = 0; j < kernelArray.length; j++) {
                    int kernelY = g + j;
                    if (kernelY >= 0 && kernelY < srcHeight) {
                        int argb = srcBuffer[srcX + kernelY * srcWidth];
                        int alpha = argb >>> 24;
                        w += alpha * kernelArray[j];
                    }
                }
                w = w / kernelSum;
                dstBuffer[dstY * dstWidth + dstX] = w;
            }
        }

        // horizontal pass:
        int[] row = new int[dstWidth];
        for (int dstY = 0; dstY < dstHeight; dstY++) {
            System.arraycopy(dstBuffer, dstY * dstWidth, row, 0,
                    row.length);
            for (int dstX = 0; dstX < dstWidth; dstX++) {
                int w = 0;
                for (int j = 0; j < kernelArray.length; j++) {
                    int kernelX = dstX - k + j;
                    if (kernelX >= 0 && kernelX < dstWidth) {
                        w += row[kernelX] * kernelArray[j];
                    }
                }
                w = w / kernelSum;
                dstBuffer[dstY * dstWidth + dstX] = opacityLookup[w];
            }
        }

        return dst;

    }

    @Override
    public GaussianKernel getKernel(float kernelRadius) {
        return new GaussianKernel(kernelRadius, false);
    }
}
