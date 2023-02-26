package com.pump.image.pixel;

import com.pump.image.pixel.converter.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ImageType<T> implements Serializable {


    private static Map<Integer, ImageType<?>> BUFFERED_IMAGE_TYPES_BY_INT = new HashMap<>();
    private static Map<String, ImageType<?>> BUFFERED_IMAGE_TYPES_BY_NAME = new HashMap<>();

    private static final long serialVersionUID = 1L;

    public static final ImageType<int[]> INT_RGB = new ImageType<>("INT_RGB", true,
            BufferedImage.TYPE_INT_RGB, 1, true, false,
            new IntRGBConverter());

    public static final ImageType<int[]> INT_ARGB = new ImageType<>("INT_ARGB", true,
            BufferedImage.TYPE_INT_ARGB, 1, false, false,
            new IntARGBConverter());

    public static final ImageType<int[]> INT_ARGB_PRE = new ImageType<>("INT_ARGB_PRE", true,
            BufferedImage.TYPE_INT_ARGB_PRE, 1, false, true,
            new IntARGBPreConverter());

    public static final ImageType<int[]> INT_BGR = new ImageType<>("INT_BGR", true,
            BufferedImage.TYPE_INT_BGR, 1, true, false,
            new IntBGRConverter());

    public static final ImageType<byte[]> BYTE_BGR = new ImageType<>("3BYTE_BGR", false,
            BufferedImage.TYPE_3BYTE_BGR, 3, true, false,
            new ByteBGRConverter());

    public static final ImageType<byte[]> BYTE_ABGR = new ImageType<>("4BYTE_ABGR", false,
            BufferedImage.TYPE_4BYTE_ABGR, 4, false, false,
            new ByteABGRConverter());

    public static final ImageType<byte[]> BYTE_ABGR_PRE = new ImageType<>("4BYTE_ABGR_PRE", false,
            BufferedImage.TYPE_4BYTE_ABGR_PRE, 4, false, true,
            new ByteABGRPreConverter());

    public static final ImageType<byte[]> BYTE_GRAY = new ImageType<>("BYTE_GRAY", false,
            BufferedImage.TYPE_BYTE_GRAY, 1, true, false,
            new ByteGrayConverter());


    // Below are the ImageTypes we added that are not (currently) represented as BufferedImage.TYPE_* constant:

    private static final int CUSTOM_TYPE_STARTING_INDEX = 100;

    /**
     * The value of {@link #getCode()} for {@link #BYTE_BGRA}.
     */
    public static final int TYPE_4BYTE_BGRA = CUSTOM_TYPE_STARTING_INDEX;

    /**
     * The value of {@link #getCode()} for {@link #BYTE_RGB}.
     */
    public static final int TYPE_3BYTE_RGB = CUSTOM_TYPE_STARTING_INDEX + 1;

    /**
     * The value of {@link #getCode()} for {@link #BYTE_ARGB}.
     */
    public static final int TYPE_4BYTE_ARGB = CUSTOM_TYPE_STARTING_INDEX + 2;

    /**
     * The value of {@link #getCode()} for {@link #BYTE_ARGB_PRE}.
     */
    public static final int TYPE_4BYTE_ARGB_PRE = CUSTOM_TYPE_STARTING_INDEX + 3;

    public static final ImageType<byte[]> BYTE_BGRA = new ImageType<>("4BYTE_BGRA", false,
            TYPE_4BYTE_BGRA, 4, false, false,
            new ByteBGRAConverter());
    public static final ImageType<byte[]> BYTE_RGB = new ImageType<>("3BYTE_RGB", false,
            TYPE_3BYTE_RGB, 3, true, false,
            new ByteRGBConverter());
    public static final ImageType<byte[]> BYTE_ARGB = new ImageType<>("4BYTE_ARGB", false,
            TYPE_4BYTE_ARGB,  4, false, false,
            new ByteARGBConverter());
    public static final ImageType<byte[]> BYTE_ARGB_PRE = new ImageType<>("4BYTE_ARGB_PRE", false,
            TYPE_4BYTE_ARGB_PRE, 4, false, true,
            new ByteARGBPreConverter());

    /**
     * Return an ImageType based on {@link BufferedImage#getType()}. Note this will return null for
     * image types that are not supported in this architecture (like {@link BufferedImage#TYPE_USHORT_565_RGB}).
     */
    public static ImageType<?> get(int bufferedImageType) {
        return BUFFERED_IMAGE_TYPES_BY_INT.get(bufferedImageType);
    }

    /**
     * Return a name like "INT_ARGB_PRE" or "3BYTE_BGR" or "BYTE_GRAY" for a given image int.
     * <p>
     * This method is capable of returning names for BufferedImage types that are not represented by
     * ImageType constants, such as "USHORT_565_RGB". So calling <code>get(imageType).getName()</code>
     * will throw a NullPointerException for values like {@link BufferedImage#TYPE_USHORT_565_RGB}, but
     * calling this {@link #toString(int)} method will not throw a similar NullPointerException.
     * </p>
     */
    public static String toString(int imageTypeCode) {
        ImageType imageType = BUFFERED_IMAGE_TYPES_BY_INT.get(imageTypeCode);
        if (imageType != null)
            return imageType.name;

        return switch (imageTypeCode) {
            case BufferedImage.TYPE_BYTE_INDEXED -> "BYTE_INDEXED";
            case BufferedImage.TYPE_USHORT_565_RGB -> "USHORT_565_RGB";
            case BufferedImage.TYPE_CUSTOM -> "CUSTOM";
            case BufferedImage.TYPE_USHORT_555_RGB -> "USHORT_555_RGB";
            case BufferedImage.TYPE_USHORT_GRAY -> "USHORT_GRAY";
            case BufferedImage.TYPE_BYTE_BINARY -> "BYTE_BINARY";
            default ->
                    throw new IllegalArgumentException("The image type \"" + imageTypeCode + "\" is not recognized.");
        };
    }

    String name;
    private final int intCode, sampleCount;
    private final boolean isOpaque, isAlphaPremultiplied, isInt;

    private final PixelConverter<T> pixelConverter;

    private ImageType(String name, boolean isInt, int intCode, int sampleCount, boolean isOpaque, boolean isAlphaPremultiplied, PixelConverter<T> converter) {
        this.name = Objects.requireNonNull(name);
        this.sampleCount = sampleCount;
        this.intCode = intCode;
        this.isOpaque = isOpaque;
        this.isAlphaPremultiplied = isAlphaPremultiplied;
        this.isInt = isInt;

        if (BUFFERED_IMAGE_TYPES_BY_NAME.put(name, this) != null) {
            // The only times this constructor should be used are for ImageType static fields in this class,
            // and all of those fields should have a unique name.
            throw new IllegalArgumentException("The ImageType \"" + name + "\" is already defined.");
        }

        if (BUFFERED_IMAGE_TYPES_BY_INT.put(intCode, this) != null) {
            throw new IllegalArgumentException("The ImageType code \"" + intCode + "\" is already defined.");
        }

        this.pixelConverter = converter;
    }

    /**
     * Return a name like "INT_ARGB_PRE" or "3BYTE_BGR" or "BYTE_GRAY".
     */
    public String getName() {
        return name;
    }

    /**
     * Return the unique int code for this ImageType. Whenever possible this code matches a
     * <code>BufferedImage.TYPE_*</code> constant. But some ImageTypes don't exist as
     * BufferedImage constants (such as {@link ImageType#TYPE_4BYTE_ARGB_PRE}, so those
     * ImageTypes return a custom int.
     */
    public int getCode() {
        return intCode;
    }

    public boolean isInt() {
        return isInt;
    }

    public boolean isByte() {
        return !isInt;
    }

    /**
     * Returns true if this image type can only ever be opaque.
     */
    public boolean isOpaque() {
        return isOpaque;
    }

    public boolean isAlphaPremultiplied() {
        return isAlphaPremultiplied;
    }

    @Override
    public String toString() {
        return "ImageType[ \"" + name + "\" (" + intCode + ")]";
    }

    @Override
    public boolean equals(Object o) {
        // the only ImageType instances should be the static fields in this class, so there shouldn't
        // be any redundancy
        return o == this;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * The number of array elements used to store 1 pixel.
     * <P>
     * So in TYPE_4BYTE_ARGB this will be 4, but in TYPE_INT_ARGB this will be
     * 1.
     *
     * @return the number of array elements used to store 1 pixel.
     */
    public int getSampleCount() {
        return sampleCount;
    }

    /**
     * Create a PixelIterator for an incoming image.
     * <p>
     * If you don't have a preference about which image type you receive, you can
     * call {@link BufferedImageIterator#get(BufferedImage)}. This can be faster
     * because it avoids converting any pixel data.
     */
    public PixelIterator<T> createPixelIterator(BufferedImage bufferedImage) {
        BufferedImageIterator<?> iter = BufferedImageIterator.get(bufferedImage);
        return createPixelIterator(iter);
    }

    /**
     * Create a PixelIterator that converts the argument to this specific image type.
     */
    public PixelIterator<T> createPixelIterator(PixelIterator<?> iter) {
        if (iter.getType() == getCode())
            return (PixelIterator<T>) iter;
        return new PixelConverterIterator(iter, this, pixelConverter);
    }

    /**
     * Convert pixel data from another image type to this image type.
     *
     * @param srcType the type of the incoming pixel data
     * @param srcPixels the incoming pixel data
     * @param srcOffset the offset in srcPixels to start reading data at
     * @param destPixels an optional destination array to store the converted pixels in.
     *                   If this is null then a new array is allocated.
     * @param destOffset the offset in destPixels to store pixels to.
     * @param pixelCount the number of pixels to convert
     * @return the converted pixels, which will be the same as destPixels if that is non-null.
     *
     * @param <S> the type if incoming pixel data (an array of ints or bytes)
     */
    public <S> T convertFrom(ImageType<S> srcType, S srcPixels, int srcOffset, T destPixels, int destOffset, int pixelCount) {
        if (destPixels == null) {
            if (isInt()) {
                destPixels = (T) new int[destOffset + pixelCount * getSampleCount()];
            } else if (isByte()) {
                destPixels = (T) new byte[destOffset + pixelCount * getSampleCount()];
            }
        }
        if (srcType == ImageType.INT_ARGB) {
            pixelConverter.convertFromARGB((T) destPixels, destOffset, (int[]) srcPixels, srcOffset, pixelCount);
        } else if (srcType == ImageType.BYTE_ARGB) {
            pixelConverter.convertFromARGB( (T) destPixels, destOffset, (byte[]) srcPixels, srcOffset, pixelCount);
        } else if (srcType == ImageType.INT_ARGB_PRE) {
            pixelConverter.convertFromARGBPre( (T) destPixels, destOffset, (int[]) srcPixels, srcOffset, pixelCount);
        } else if (srcType == ImageType.BYTE_ARGB_PRE) {
            pixelConverter.convertFromARGBPre( (T) destPixels, destOffset, (byte[]) srcPixels, srcOffset, pixelCount);
        } else if (srcType == ImageType.BYTE_ABGR) {
            pixelConverter.convertFromABGR( (T) destPixels, destOffset, (byte[]) srcPixels, srcOffset, pixelCount);
        } else if (srcType == ImageType.BYTE_ABGR_PRE) {
            pixelConverter.convertFromABGRPre( (T) destPixels, destOffset, (byte[]) srcPixels, srcOffset, pixelCount);
        } else if (srcType == ImageType.INT_BGR) {
            pixelConverter.convertFromBGR( (T) destPixels, destOffset, (int[]) srcPixels, srcOffset, pixelCount);
        } else if (srcType == ImageType.BYTE_BGRA) {
            pixelConverter.convertFromBGRA( (T) destPixels, destOffset, (byte[]) srcPixels, srcOffset, pixelCount);
        } else if (srcType == ImageType.BYTE_GRAY) {
            pixelConverter.convertFromGray( (T) destPixels, destOffset, (byte[]) srcPixels, srcOffset, pixelCount);
        } else if (srcType == ImageType.INT_RGB) {
            pixelConverter.convertFromRGB( (T) destPixels, destOffset, (int[]) srcPixels, srcOffset, pixelCount);
        } else if (srcType == ImageType.BYTE_RGB) {
            pixelConverter.convertFromRGB( (T) destPixels, destOffset, (byte[]) srcPixels, srcOffset, pixelCount);
        } else if (srcType == ImageType.BYTE_BGR) {
            pixelConverter.convertFromBGR( (T) destPixels, destOffset, (byte[]) srcPixels, srcOffset, pixelCount);
        } else {
            // this method does not support IndexedColorModel conversions (which would require another argument)
            throw new UnsupportedOperationException();
        }

        return destPixels;
    }

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        // we only record our name, and on deserialization we cross-reference
        // that name in readResolve()
        out.writeInt(0);
        out.writeObject(name);
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        int internalVersion = in.readInt();
        if (internalVersion == 0) {
            // this is a hollow shell of a ImageType that will be replaced in readResolve()
            name = (String) in.readObject();
        } else {
            throw new IOException(
                    "Unsupported internal version: " + internalVersion);
        }
    }

    private Object readResolve() {
        ImageType returnValue = BUFFERED_IMAGE_TYPES_BY_NAME.get(name);
        if (returnValue == null) {
            // This could indicate data was saved with a newer version that had additional ImageTypes
            // we don't support here (yet)?
            throw new RuntimeException("There is no ImageType named \"" + name + "\".");
        }
        return returnValue;
    }
}