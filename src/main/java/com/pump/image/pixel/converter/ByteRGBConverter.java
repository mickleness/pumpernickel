/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.image.pixel.converter;

public class ByteRGBConverter implements PixelConverter<byte[]> {
    @Override
    public void convertFromARGB(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_AXYZ_ints_to_XYZ_bytes(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromARGB(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_AXYZ_bytes_to_XYZ_bytes(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromARGBPre(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_XYZ_ints_to_XYZ_bytes(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromARGBPre(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_AXYZPre_bytes_to_XYZ_bytes(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromABGR(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_AXYZ_bytes_to_ZYX_bytes(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromABGRPre(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_AXYZPre_bytes_to_ZYX_bytes(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromBGR(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_XYZ_ints_to_ZYX_bytes(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromBGRA(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_XYZA_bytes_to_ZYX_bytes(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromRGBA(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_XYZA_bytes_to_XYZ_bytes(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromGray(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_G_bytes_to_XYZ_bytes(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromRGB(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_XYZ_ints_to_XYZ_bytes(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromRGB(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels == sourcePixels && destOffset == srcOffset) {
            return;
        }
        System.arraycopy(sourcePixels, srcOffset, destPixels, destOffset, 3 * pixelCount);
    }

    @Override
    public void convertFromBGR(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_XYZ_bytes_to_ZYX_bytes(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }

    @Override
    public void convertFromIndexed(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount, IndexColorModelLUT colorModel) {
        int srcEnd = srcOffset + pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int j = sourcePixels[srcIndex++] & 0xff;
            destPixels[destIndex++] = colorModel.redTable_pre_byte[j];
            destPixels[destIndex++] = colorModel.greenTable_pre_byte[j];
            destPixels[destIndex++] = colorModel.blueTable_pre_byte[j];
        }
    }

    @Override
    public void convertFromRGBAPre(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        ConverterUtils.convert_XYZAPre_bytes_to_XYZ_bytes(destPixels, destOffset, sourcePixels, srcOffset, pixelCount);
    }
}