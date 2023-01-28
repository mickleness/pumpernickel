package com.pump.image.pixel2;

import com.pump.image.pixel2.converter.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ImageType<T> implements Serializable {

    public static final ImageType<int[]> TYPE_INT_RGB = new ImageType<>("INT_RGB", true,
            BufferedImage.TYPE_INT_RGB, 3, true, false,
            new IntRGBConverter());

    public static final ImageType<int[]> TYPE_INT_ARGB = new ImageType("INT_ARGB", true,
            BufferedImage.TYPE_INT_ARGB, 4, false, false,
            new IntARGBConverter());

    public static final ImageType<int[]> TYPE_INT_ARGB_PRE = new ImageType("INT_ARGB_PRE", true,
            BufferedImage.TYPE_INT_ARGB_PRE, 4, false, true,
            new IntARGBPreConverter());

    public static final ImageType<int[]> TYPE_INT_BGR = new ImageType("INT_BGR", true,
            BufferedImage.TYPE_INT_BGR, 3, true, false,
            new IntBGRConverter());

    public static final ImageType<byte[]> TYPE_3BYTE_BGR = new ImageType("3BYTE_BGR", false,
            BufferedImage.TYPE_3BYTE_BGR, 3, true, false,
            new ByteBGRConverter());

    public static final ImageType<byte[]> TYPE_4BYTE_ABGR = new ImageType("4BYTE_ABGR", false,
            BufferedImage.TYPE_4BYTE_ABGR, 4, false, false,
            new ByteABGRConverter());

    public static final ImageType<byte[]> TYPE_4BYTE_ABGR_PRE = new ImageType("4BYTE_ABGR_PRE", false,
            BufferedImage.TYPE_4BYTE_ABGR_PRE, 4, false, true,
            new ByteABGRPreConverter());

    public static final ImageType<byte[]> TYPE_BYTE_GRAY = new ImageType("BYTE_GRAY", false,
            BufferedImage.TYPE_BYTE_GRAY, 1, true, false,
            new ByteGrayConverter());

    public static final ImageType<byte[]> TYPE_BYTE_INDEXED = new ImageType("BYTE_INDEXED", false,
            BufferedImage.TYPE_BYTE_INDEXED, 1, false, false,
            new ByteIndexedConverter());


    // custom types not from BufferedImage:

    public static final ImageType<byte[]> TYPE_4BYTE_BGRA = new ImageType("4BYTE_BGRA", false,
            -1, 4, false, false,
            new ByteBGRAConverter());
    public static final ImageType<byte[]> TYPE_3BYTE_RGB = new ImageType("3BYTE_RGB", false,
            -1, 3, true, false,
            new ByteRGBConverter());
    public static final ImageType<byte[]> TYPE_4BYTE_ARGB = new ImageType("4BYTE_ARGB", false,
            -1,  4, false, false,
            new ByteARGBConverter());
    public static final ImageType<byte[]> TYPE_4BYTE_ARGB_PRE = new ImageType("4BYTE_ARGB_PRE", false,
            -1, 4, false, true,
            new ByteARGBPreConverter());
    private static Map<Integer, ImageType> BUFFERED_IMAGE_TYPES = new HashMap<>();

    public static ImageType get(int bufferedImageType) {
        return BUFFERED_IMAGE_TYPES.get(bufferedImageType);
    }

    String name;
    int bufferedImageType, sampleCount;
    boolean isOpaque, isAlphaPremultiplied, isInt;

    final PixelConverter<T> pixelConverter;

    private ImageType(String name, boolean isInt, int bufferedImageType, int sampleCount, boolean isOpaque, boolean isAlphaPremultiplied, PixelConverter<T> converter) {
        this.name = Objects.requireNonNull(name);
        this.sampleCount = sampleCount;
        this.bufferedImageType = bufferedImageType;
        this.isOpaque = isOpaque;
        this.isAlphaPremultiplied = isAlphaPremultiplied;
        this.isInt = isInt;
        if (bufferedImageType > 0)
            BUFFERED_IMAGE_TYPES.put(bufferedImageType, this);
        this.pixelConverter = Objects.requireNonNull(converter);
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
        return "ImageType[ \"" + name + "\"]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageType<?> imageType = (ImageType<?>) o;
        return Objects.equals(name, imageType.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.writeInt(0);
        out.writeObject(name);
        out.writeInt(bufferedImageType);
        out.writeInt(sampleCount);
        out.writeBoolean(isAlphaPremultiplied);
        out.writeBoolean(isOpaque);
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        int internalVersion = in.readInt();
        if (internalVersion == 0) {
            name = (String) in.readObject();
            bufferedImageType = in.readInt();
            sampleCount = in.readInt();
            isAlphaPremultiplied = in.readBoolean();
            isOpaque = in.readBoolean();
        } else {
            throw new IOException(
                    "Unsupported internal version: " + internalVersion);
        }
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
}