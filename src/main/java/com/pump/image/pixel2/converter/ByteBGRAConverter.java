package com.pump.image.pixel2.converter;

import com.pump.image.pixel.converter.IndexColorModelLUT;

public class ByteBGRAConverter implements PixelConverter<byte[]> {
    @Override
    public void convertFromARGB(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        // TODO
    }

    @Override
    public void convertFromARGB(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        // TODO
    }

    @Override
    public void convertFromARGBPre(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        // TODO
    }

    @Override
    public void convertFromARGBPre(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        // TODO
    }

    @Override
    public void convertFromABGR(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        // TODO
    }

    @Override
    public void convertFromABGRPre(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        // TODO
    }

    @Override
    public void convertFromBGR(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        // TODO
    }

    @Override
    public void convertFromBGRA(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels == sourcePixels && destOffset == srcOffset) {
            return;
        }
        System.arraycopy(sourcePixels, srcOffset, destOffset, destOffset, 4 * pixelCount);
    }

    @Override
    public void convertFromGray(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        // TODO
    }

    @Override
    public void convertFromRGB(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        // TODO
    }

    @Override
    public void convertFromRGB(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        // TODO
    }

    @Override
    public void convertFromBGR(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        // TODO
    }

    @Override
    public void convertFromIndexed(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount, IndexColorModelLUT colorModel) {
        // TODO
    }
}