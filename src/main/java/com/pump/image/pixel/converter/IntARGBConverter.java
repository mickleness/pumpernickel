package com.pump.image.pixel.converter;

public class IntARGBConverter implements PixelConverter<int[]> {
    @Override
    public void convertFromARGB(int[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels == sourcePixels && destOffset == srcOffset) {
            return;
        }
        System.arraycopy(sourcePixels, srcOffset, destPixels, destOffset, pixelCount);
    }

    @Override
    public void convertFromARGB(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_AXYZ_bytes_to_AXYZ_ints(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromARGBPre(int[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_AXYZPre_ints_to_AXYZ_ints(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromARGBPre(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_AXYZPre_bytes_to_AXYZ_ints(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromABGR(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_AXYZ_bytes_to_AZYX_ints(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromABGRPre(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_AXYZPre_bytes_to_AZYX_ints(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromBGR(int[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_XYZ_ints_to_AZYX_ints(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromBGRA(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_XYZA_bytes_to_AZYX_ints(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromRGBA(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_XYZA_bytes_to_AXYZ_ints(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
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
        int srcEnd = srcOffset + pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int j = sourcePixels[srcIndex++] & 0xff;
            destPixels[destIndex++] = (colorModel.alphaTable_int[j] << 24) |
                    (colorModel.redTable_int[j] << 16) |
                    (colorModel.greenTable_int[j] << 8) |
                    (colorModel.blueTable_int[j]);
        }
    }

    @Override
    public void convertFromRGBAPre(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_XYZAPre_bytes_to_AXYZ_ints(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }
}
