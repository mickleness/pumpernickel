package com.pump.image.pixel2.converter;

import com.pump.image.pixel.converter.IndexColorModelLUT;

public interface PixelConverter<T> {

    public void convertFromARGB(T destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount);
    public void convertFromARGB(T destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount);

    public void convertFromARGBPre(T destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount);
    public void convertFromARGBPre(T destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount);

    public void convertFromABGR(T destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount);

    public void convertFromABGRPre(T destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount);


    public void convertFromBGR(T destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount);

    public void convertFromBGRA(T destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount);


    public void convertFromGray(T destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount);


    public void convertFromRGB(T destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount);
    public void convertFromRGB(T destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount);


    public void convertFromBGR(T destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount);

    public void convertFromIndexed(T destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount, IndexColorModelLUT colorModel);


    default void _swapFirstAndThirdSamples(int[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels == sourcePixels && destOffset > srcOffset) {
            int destIndex = destOffset + pixelCount - 1;
            for (int srcIndex = srcOffset + pixelCount - 1; srcIndex >= srcOffset; srcIndex--, destIndex--) {
                int value = sourcePixels[srcIndex];
                int sampleA = value & 0xff;
                int sampleB = (value >> 16) & 0xff;
                destPixels[destIndex] = (value & 0xff00ff00) | sampleB | (sampleA << 16);
            }
            return;
        }
        int srcEnd = srcOffset + pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd; srcIndex++, destIndex++) {
            int value = sourcePixels[srcIndex];
            int sampleA = value & 0xff;
            int sampleB = (value >> 16) & 0xff;
            destPixels[destIndex] = (value & 0xff00ff00) | sampleB | (sampleA << 16);
        }
    }
}
