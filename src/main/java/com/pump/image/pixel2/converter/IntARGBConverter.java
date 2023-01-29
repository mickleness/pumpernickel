package com.pump.image.pixel2.converter;

import com.pump.image.pixel.converter.IndexColorModelLUT;

public class IntARGBConverter implements PixelConverter<int[]> {
    @Override
    public void convertFromARGB(int[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels == sourcePixels && destOffset == srcOffset) {
            return;
        }
        System.arraycopy(sourcePixels, srcOffset, destOffset, destOffset, pixelCount);
    }

    @Override
    public void convertFromARGB(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_AXYZ_bytes_to_AXYZ_ints(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
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
        ConverterUtils.convert_AXYZ_bytes_to_AZYX_ints(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromABGRPre(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        // TODO
    }

    @Override
    public void convertFromBGR(int[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_XYZ_ints_to_AZYX_ints(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromBGRA(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        // TODO
    }

    @Override
    public void convertFromGray(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_G_bytes_to_AXYZ_ints(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromRGB(int[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_XYZ_ints_to_AXYZ_ints(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromRGB(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_XYZ_bytes_to_AXYZ_ints(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromBGR(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_XYZ_bytes_to_AZYX_ints(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromIndexed(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount, IndexColorModelLUT colorModel) {
        // TODO
    }
}
