package com.pump.image.pixel2;

import com.pump.image.pixel2.converter.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ImageType<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final ImageType<int[]> TYPE_INT_RGB = new ImageType<>("INT_RGB", true,
            BufferedImage.TYPE_INT_RGB, 3, true, false,
            new IntRGBConverter());

    public static final ImageType<int[]> TYPE_INT_ARGB = new ImageType<>("INT_ARGB", true,
            BufferedImage.TYPE_INT_ARGB, 4, false, false,
            new IntARGBConverter());

    public static final ImageType<int[]> TYPE_INT_ARGB_PRE = new ImageType<>("INT_ARGB_PRE", true,
            BufferedImage.TYPE_INT_ARGB_PRE, 4, false, true,
            new IntARGBPreConverter());

    public static final ImageType<int[]> TYPE_INT_BGR = new ImageType<>("INT_BGR", true,
            BufferedImage.TYPE_INT_BGR, 3, true, false,
            new IntBGRConverter());

    public static final ImageType<byte[]> TYPE_3BYTE_BGR = new ImageType<>("3BYTE_BGR", false,
            BufferedImage.TYPE_3BYTE_BGR, 3, true, false,
            new ByteBGRConverter());

    public static final ImageType<byte[]> TYPE_4BYTE_ABGR = new ImageType<>("4BYTE_ABGR", false,
            BufferedImage.TYPE_4BYTE_ABGR, 4, false, false,
            new ByteABGRConverter());

    public static final ImageType<byte[]> TYPE_4BYTE_ABGR_PRE = new ImageType<>("4BYTE_ABGR_PRE", false,
            BufferedImage.TYPE_4BYTE_ABGR_PRE, 4, false, true,
            new ByteABGRPreConverter());

    public static final ImageType<byte[]> TYPE_BYTE_GRAY = new ImageType<>("BYTE_GRAY", false,
            BufferedImage.TYPE_BYTE_GRAY, 1, true, false,
            new ByteGrayConverter());

    public static final ImageType<byte[]> TYPE_BYTE_INDEXED = new ImageType<>("BYTE_INDEXED", false,
            BufferedImage.TYPE_BYTE_INDEXED, 1, false, false,
            null);


    // Below are the ImageTypes we added that are not (currently) represented as BufferedImage.TYPE_* constant:

    private static final int CUSTOM_TYPE_STARTING_INDEX = 100;
    private static int CUSTOM_TYPE_CTR = CUSTOM_TYPE_STARTING_INDEX;

    public static final ImageType<byte[]> TYPE_4BYTE_BGRA = new ImageType<>("4BYTE_BGRA", false,
            ++CUSTOM_TYPE_CTR, 4, false, false,
            new ByteBGRAConverter());
    public static final ImageType<byte[]> TYPE_3BYTE_RGB = new ImageType<>("3BYTE_RGB", false,
            ++CUSTOM_TYPE_CTR, 3, true, false,
            new ByteRGBConverter());
    public static final ImageType<byte[]> TYPE_4BYTE_ARGB = new ImageType<>("4BYTE_ARGB", false,
            ++CUSTOM_TYPE_CTR,  4, false, false,
            new ByteARGBConverter());
    public static final ImageType<byte[]> TYPE_4BYTE_ARGB_PRE = new ImageType<>("4BYTE_ARGB_PRE", false,
            ++CUSTOM_TYPE_CTR, 4, false, true,
            new ByteARGBPreConverter());


    private static Map<Integer, ImageType<?>> BUFFERED_IMAGE_TYPES_BY_INT = new HashMap<>();
    private static Map<String, ImageType<?>> BUFFERED_IMAGE_TYPES_BY_NAME = new HashMap<>();

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