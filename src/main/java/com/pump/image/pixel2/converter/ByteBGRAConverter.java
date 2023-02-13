package com.pump.image.pixel2.converter;

import com.pump.image.pixel.converter.IndexColorModelLUT;

public class ByteBGRAConverter implements PixelConverter<byte[]> {
    @Override
    public void convertFromARGB(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_AXYZ_ints_to_ZYXA_bytes(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromARGB(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_AXYZ_bytes_to_ZYXA_bytes(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromARGBPre(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_AXYZPre_ints_to_ZYXA_bytes(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromARGBPre(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_AXYZPre_bytes_to_ZYXA_bytes(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromABGR(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_AXYZ_bytes_to_XYZA_bytes(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromABGRPre(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_AXYZPre_bytes_to_XYZA_bytes(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromBGR(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_XYZ_ints_to_XYZA_bytes(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromBGRA(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels == sourcePixels && destOffset == srcOffset) {
            return;
        }
        System.arraycopy(sourcePixels, srcOffset, destPixels, destOffset, 4 * pixelCount);
    }

    @Override
    public void convertFromGray(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_G_bytes_to_XYZA_bytes(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromRGB(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_XYZ_ints_to_ZYXA_bytes(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromRGB(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
         ConverterUtils.convert_XYZ_bytes_to_ZYXA_bytes(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromBGR(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_XYZ_bytes_to_XYZA_bytes(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromIndexed(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount, IndexColorModelLUT colorModel) {
        int srcEnd = srcOffset + pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int j = sourcePixels[srcIndex++] & 0xff;
            destPixels[destIndex++] = colorModel.blueTable_byte[j];
            destPixels[destIndex++] = colorModel.greenTable_byte[j];
            destPixels[destIndex++] = colorModel.redTable_byte[j];
            destPixels[destIndex++] = colorModel.alphaTable_byte[j];
        }
    }
}
