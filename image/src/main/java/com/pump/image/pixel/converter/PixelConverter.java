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

    void convertFromRGBAPre(T destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount);
}