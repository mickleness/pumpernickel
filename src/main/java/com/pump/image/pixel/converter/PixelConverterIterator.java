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

import com.pump.image.pixel.ImageType;
import com.pump.image.pixel.IndexedBytePixelIterator;
import com.pump.image.pixel.PixelIterator;

import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.util.Objects;

/**
 * This PixelIterator converts incoming data from one image type to another.
 */
public class PixelConverterIterator<T> implements PixelIterator<T> {
    private final PixelIterator src;
    private final ImageType<T> imageType;
    private final PixelConverter<T> converter;

    private int rowCtr = 0;

    private byte[] scratchByteArray;
    private int[] scratchIntArray;

    private IndexColorModelLUT colorModel;

    private boolean isClosed = false;

    /**
     * This may be null for IndexColorModels
     */
    private final ImageType srcType;
    private final boolean canWriteSrcToDest;

    public PixelConverterIterator(PixelIterator src, ImageType<T> imageType, PixelConverter<T> converter) {
        this.src = Objects.requireNonNull(src);
        this.imageType = Objects.requireNonNull(imageType);
        this.converter = Objects.requireNonNull(converter);

        boolean isSameArrayType = false;
        if (src instanceof IndexedBytePixelIterator) {
            IndexedBytePixelIterator i = (IndexedBytePixelIterator) src;
            colorModel = new IndexColorModelLUT(i.getIndexColorModel());
            isSameArrayType = imageType.isByte();
            srcType = null; // there is no ImageType for indexed pixel iterators
        } else {
            srcType = ImageType.get(src.getType());
            isSameArrayType = (imageType.isByte() && srcType.isByte()) || (imageType.isInt() && srcType.isInt());
        }
        canWriteSrcToDest = isSameArrayType && src.getPixelSize() <= imageType.getSampleCount();
    }

    @Override
    public int getType() {
        return imageType.getCode();
    }

    @Override
    public boolean isDone() {
        return rowCtr >= src.getHeight();
    }

    @Override
    public boolean isTopDown() {
        return src.isTopDown();
    }

    @Override
    public int getWidth() {
        return src.getWidth();
    }

    @Override
    public int getHeight() {
        return src.getHeight();
    }

    @Override
    public void skip() {
        if (isClosed)
            throw new ClosedException();
        src.skip();
        rowCtr++;
        if (isDone())
            close();
    }

    @Override
    public void next(T dest, int destOffset) {
        if (isClosed)
            throw new ClosedException();

        Object srcArray = null;
        if (canWriteSrcToDest) {
            int destSize = Array.getLength(dest);
            if (destOffset + srcType.getSampleCount() * src.getWidth() < destSize) {
                srcArray = dest;
            }
        }

        if (srcArray == null) {
            if (srcType.isInt()) {
                if (scratchIntArray == null)
                    scratchIntArray = new int[src.getWidth() * src.getPixelSize()];
                srcArray = scratchIntArray;
            } else if (srcType.isByte()) {
                if (scratchByteArray == null)
                    scratchByteArray = new byte[src.getWidth() * src.getPixelSize()];
                srcArray = scratchByteArray;
            }
        }

        int srcArrayOffset;
        if (srcArray == dest) {
            srcArrayOffset = destOffset;
            src.next(srcArray, destOffset);
        } else {
            srcArrayOffset = 0;
            src.next(srcArray, 0);
        }

        switch (src.getType()) {
            case BufferedImage.TYPE_INT_ARGB:
                converter.convertFromARGB(dest, destOffset, (int[]) srcArray, srcArrayOffset, src.getWidth());
                break;
            case BufferedImage.TYPE_INT_RGB:
                converter.convertFromRGB(dest, destOffset, (int[]) srcArray, srcArrayOffset, src.getWidth());
                break;
            case BufferedImage.TYPE_INT_BGR:
                converter.convertFromBGR(dest, destOffset, (int[]) srcArray, srcArrayOffset, src.getWidth());
                break;
            case BufferedImage.TYPE_INT_ARGB_PRE:
                converter.convertFromARGBPre(dest, destOffset, (int[]) srcArray, srcArrayOffset, src.getWidth());
                break;
            case ImageType.TYPE_4BYTE_ARGB:
                converter.convertFromARGB(dest, destOffset, (byte[]) srcArray, srcArrayOffset, src.getWidth());
                break;
            case ImageType.TYPE_4BYTE_ARGB_PRE:
                converter.convertFromARGBPre(dest, destOffset, (byte[]) srcArray, srcArrayOffset, src.getWidth());
                break;
            case BufferedImage.TYPE_4BYTE_ABGR:
                converter.convertFromABGR(dest, destOffset, (byte[]) srcArray, srcArrayOffset, src.getWidth());
                break;
            case BufferedImage.TYPE_4BYTE_ABGR_PRE:
                converter.convertFromABGRPre(dest, destOffset, (byte[]) srcArray, srcArrayOffset, src.getWidth());
                break;
            case ImageType.TYPE_4BYTE_BGRA:
                converter.convertFromBGRA(dest, destOffset, (byte[]) srcArray, srcArrayOffset, src.getWidth());
                break;
            case ImageType.TYPE_4BYTE_RGBA:
                converter.convertFromRGBA(dest, destOffset, (byte[]) srcArray, srcArrayOffset, src.getWidth());
                break;
            case ImageType.TYPE_4BYTE_RGBA_PRE:
                converter.convertFromRGBAPre(dest, destOffset, (byte[]) srcArray, srcArrayOffset, src.getWidth());
                break;
            case BufferedImage.TYPE_BYTE_GRAY:
                converter.convertFromGray(dest, destOffset, (byte[]) srcArray, srcArrayOffset, src.getWidth());
                break;
            case ImageType.TYPE_3BYTE_RGB:
                converter.convertFromRGB(dest, destOffset, (byte[]) srcArray, srcArrayOffset, src.getWidth());
                break;
            case BufferedImage.TYPE_3BYTE_BGR:
                converter.convertFromBGR(dest, destOffset, (byte[]) srcArray, srcArrayOffset, src.getWidth());
                break;
            case BufferedImage.TYPE_BYTE_INDEXED:
                converter.convertFromIndexed(dest, destOffset, (byte[]) srcArray, srcArrayOffset, src.getWidth(), colorModel);
                break;
            default:
                throw new UnsupportedOperationException("Unable to convert from " + src.getType() + " to " + imageType.getName());
        }

        rowCtr++;

        if (isDone())
            close();
    }

    @Override
    public void close() {
        isClosed = true;
        rowCtr = src.getHeight();
        src.close();
    }
}