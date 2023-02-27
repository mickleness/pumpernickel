package com.pump.image.pixel.converter;

public interface PixelConverter<T> {

    void convertFromARGB(T destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount);

    void convertFromARGB(T destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount);

    void convertFromARGBPre(T destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount);

    void convertFromARGBPre(T destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount);

    void convertFromABGR(T destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount);

    void convertFromABGRPre(T destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount);

    void convertFromBGR(T destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount);

    void convertFromBGRA(T destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount);

    void convertFromRGBA(T destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount);

    void convertFromGray(T destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount);

    void convertFromRGB(T destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount);

    void convertFromRGB(T destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount);

    void convertFromBGR(T destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount);

    void convertFromIndexed(T destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount, IndexColorModelLUT colorModel);
}
