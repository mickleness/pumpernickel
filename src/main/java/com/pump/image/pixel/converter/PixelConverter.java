package com.pump.image.pixel.converter;

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

}
