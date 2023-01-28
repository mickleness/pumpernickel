package com.pump.image.pixel2.converter;

import com.pump.image.pixel.converter.IndexColorModelLUT;

public class IntRGBConverter implements PixelConverter<int[]> {
    @Override
    public void convertFromARGB(int[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        // TODO
    }

    @Override
    public void convertFromARGB(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        // TODO
    }

    @Override
    public void convertFromARGBPre(int[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        // TODO
    }

    @Override
    public void convertFromARGBPre(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        // TODO
    }

    @Override
    public void convertFromABGR(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        // TODO
    }

    @Override
    public void convertFromABGRPre(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        // TODO
    }

    @Override
    public void convertFromBGR(int[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.swapFirstAndThirdSamples(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromBGRA(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        // TODO
    }

    @Override
    public void convertFromGray(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        // TODO
    }

    @Override
    public void convertFromRGB(int[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels == sourcePixels && destOffset == srcOffset) {
            return;
        }
        System.arraycopy(sourcePixels, srcOffset, destOffset, destOffset, pixelCount);
    }

    @Override
    public void convertFromRGB(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        // TODO
    }

    @Override
    public void convertFromBGR(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        // TODO
    }

    @Override
    public void convertFromIndexed(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount, IndexColorModelLUT colorModel) {
        // TODO
    }
}
