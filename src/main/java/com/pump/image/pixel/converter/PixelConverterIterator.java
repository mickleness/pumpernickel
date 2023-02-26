package com.pump.image.pixel.converter;

import com.pump.image.pixel.BufferedImageIterator;
import com.pump.image.pixel.ImageType;
import com.pump.image.pixel.IndexedBytePixelIterator;
import com.pump.image.pixel.PixelIterator;

import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * This PixelIterator converts incoming data from one image type to another.
 */
public class PixelConverterIterator<T> implements PixelIterator<T> {
    private final PixelIterator src;
    private final ImageType<T> imageType;
    private final PixelConverter<T> converter;

    private int rowCtr = 0;

    private final byte[] scratchByteArray;
    private final int[] scratchIntArray;

    private IndexColorModelLUT colorModel;

    /**
     * This may be null for IndexColorModels
     */
    private final ImageType srcType;
    private final boolean isByteToByte, isIntToInt;

    public PixelConverterIterator(PixelIterator src, ImageType<T> imageType, PixelConverter<T> converter) {
        this.src = Objects.requireNonNull(src);
        this.imageType = Objects.requireNonNull(imageType);
        this.converter = Objects.requireNonNull(converter);
        if (src instanceof IndexedBytePixelIterator) {
            IndexedBytePixelIterator i = (IndexedBytePixelIterator) src;
            colorModel = new IndexColorModelLUT(i.getIndexColorModel());
            isByteToByte = imageType.isByte();
            isIntToInt = false;
            srcType = null;
        } else {
            srcType = ImageType.get(src.getType());
            isByteToByte = imageType.isByte() && srcType.isByte();
            isIntToInt = imageType.isInt() && srcType.isInt();
        }

        if (src.isByte()) {
            scratchIntArray = null;
            scratchByteArray = new byte[src.getMinimumArrayLength()];
        } else if (src.isInt()) {
            scratchIntArray = new int[src.getMinimumArrayLength()];
            scratchByteArray = null;
        } else {
            throw new UnsupportedOperationException( "This converter does not support " + ImageType.toString(src.getType()) );
        }
    }

    @Override
    public int getType() {
        return imageType.getCode();
    }

    @Override
    public boolean isDone() {
        return rowCtr < src.getHeight();
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
    public int getMinimumArrayLength() {
        if (isByteToByte || isIntToInt) {
            // we're going to reuse
            return Math.max(getWidth() * imageType.getSampleCount(), src.getMinimumArrayLength());
        }
        return getWidth() * imageType.getSampleCount();
    }

    @Override
    public void skip() {
        src.skip();
    }

    @Override
    public void next(T dest) {
        Object srcArray;
        if (scratchIntArray != null) {
            srcArray = scratchIntArray;
            if (isIntToInt) {
                int destSize = Array.getLength(dest);
                if (destSize >= scratchIntArray.length) {
                    srcArray = dest;
                }
            }
        } else {
            srcArray = scratchByteArray;
            if (isByteToByte) {
                int destSize = Array.getLength(dest);
                if (destSize >= scratchByteArray.length) {
                    srcArray = dest;
                }
            }
        }

        src.next(srcArray);

        switch (src.getType()) {
            case BufferedImage.TYPE_INT_ARGB:
                converter.convertFromARGB(dest, 0, (int[]) srcArray, 0, src.getWidth());
                break;
            case BufferedImage.TYPE_INT_RGB:
                converter.convertFromRGB(dest, 0, (int[]) srcArray, 0, src.getWidth());
                break;
            case BufferedImage.TYPE_INT_BGR:
                converter.convertFromBGR(dest, 0, (int[]) srcArray, 0, src.getWidth());
                break;
            case BufferedImage.TYPE_INT_ARGB_PRE:
                converter.convertFromARGBPre(dest, 0, (int[]) srcArray, 0, src.getWidth());
                break;
            case ImageType.TYPE_4BYTE_ARGB:
                converter.convertFromARGB(dest, 0, (byte[]) srcArray, 0, src.getWidth());
                break;
            case ImageType.TYPE_4BYTE_ARGB_PRE:
                converter.convertFromARGBPre(dest, 0, (byte[]) srcArray, 0, src.getWidth());
                break;
            case BufferedImage.TYPE_4BYTE_ABGR:
                converter.convertFromABGR(dest, 0, (byte[]) srcArray, 0, src.getWidth());
                break;
            case BufferedImage.TYPE_4BYTE_ABGR_PRE:
                converter.convertFromABGRPre(dest, 0, (byte[]) srcArray, 0, src.getWidth());
                break;
            case ImageType.TYPE_4BYTE_BGRA:
                converter.convertFromBGRA(dest, 0, (byte[]) srcArray, 0, src.getWidth());
                break;
            case BufferedImage.TYPE_BYTE_GRAY:
                converter.convertFromGray(dest, 0, (byte[]) srcArray, 0, src.getWidth());
                break;
            case ImageType.TYPE_3BYTE_RGB:
                converter.convertFromRGB(dest, 0, (byte[]) srcArray, 0, src.getWidth());
                break;
            case BufferedImage.TYPE_3BYTE_BGR:
                converter.convertFromBGR(dest, 0, (byte[]) srcArray, 0, src.getWidth());
                break;
            case BufferedImage.TYPE_BYTE_INDEXED:
                converter.convertFromIndexed(dest, 0, (byte[]) srcArray, 0, src.getWidth(), colorModel);
                break;
            default:
                throw new UnsupportedOperationException("Unable to convert from " + src.getType() + " to " + imageType.getName());
        }
    }
}
