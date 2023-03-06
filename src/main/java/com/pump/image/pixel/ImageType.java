package com.pump.image.pixel;

import com.pump.image.pixel.converter.*;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class ImageType<T> implements Serializable {


    private static final Map<Integer, ImageType<?>> TYPES_BY_INT = new TreeMap<>();
    private static final Map<String, ImageType<?>> TYPES_BY_NAME = new TreeMap<>();

    private static final long serialVersionUID = 1L;

    public static final ImageType<int[]> INT_RGB = new ImageType<>("INT_RGB",
            BufferedImage.TYPE_INT_RGB,
            new DirectColorModel(24,
                    0x00ff0000,   // Red
                    0x0000ff00,   // Green
                    0x000000ff,   // Blue
                    0x0           // Alpha
            ),
            new IntRGBConverter(), true);

    public static final ImageType<int[]> INT_ARGB = new ImageType<>("INT_ARGB",
            BufferedImage.TYPE_INT_ARGB,
            ColorModel.getRGBdefault(), new IntARGBConverter(), true);

    public static final ImageType<int[]> INT_ARGB_PRE = new ImageType<>("INT_ARGB_PRE",
            BufferedImage.TYPE_INT_ARGB_PRE,
            new DirectColorModel(
                    ColorSpace.getInstance(ColorSpace.CS_sRGB),
                    32,
                    0x00ff0000,// Red
                    0x0000ff00,// Green
                    0x000000ff,// Blue
                    0xff000000,// Alpha
                    true,       // Alpha Premultiplied
                    DataBuffer.TYPE_INT
            ),
            new IntARGBPreConverter(), true);

    public static final ImageType<int[]> INT_BGR = new ImageType<>("INT_BGR",
            BufferedImage.TYPE_INT_BGR,
            new DirectColorModel(24,
                    0x000000ff,   // Red
                    0x0000ff00,   // Green
                    0x00ff0000    // Blue
            ),
            new IntBGRConverter(), true);

    public static final ImageType<byte[]> BYTE_BGR = new ImageType<>("3BYTE_BGR",
            BufferedImage.TYPE_3BYTE_BGR,
            new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] {8, 8, 8},
                    false, false,
                    Transparency.OPAQUE,
                    DataBuffer.TYPE_BYTE),
            new ByteBGRConverter(), true);

    public static final ImageType<byte[]> BYTE_ABGR = new ImageType<>("4BYTE_ABGR",
            BufferedImage.TYPE_4BYTE_ABGR,
            new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] {8, 8, 8, 8}, true, false,
                    Transparency.TRANSLUCENT,
                    DataBuffer.TYPE_BYTE),
            new ByteABGRConverter(), true);

    public static final ImageType<byte[]> BYTE_ABGR_PRE = new ImageType<>("4BYTE_ABGR_PRE",
            BufferedImage.TYPE_4BYTE_ABGR_PRE,
            new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] {8, 8, 8, 8}, true, true,
                    Transparency.TRANSLUCENT,
                    DataBuffer.TYPE_BYTE),
            new ByteABGRPreConverter(), true);

    /**
     * Warning: this architecture's concept of "BYTE_GRAY" is different than BufferedImage's. In both
     * cases 0 = 0 and 255 = 255, but this implementation features a plain linear scale, whereas BufferedImage's
     * implementation is more complicated (see ComponentColorModel#is_ICCGray_stdScale)
     */
    public static final ImageType<byte[]> BYTE_GRAY = new ImageType<>("BYTE_GRAY",
            BufferedImage.TYPE_BYTE_GRAY,
            new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY), new int[] { 8 }, false, true,
                    Transparency.OPAQUE,
                    DataBuffer.TYPE_BYTE),
            new ByteGrayConverter(), true);


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

    /**
     * The value of {@link #getCode()} for {@link #BYTE_RGBA}.
     */
    public static final int TYPE_4BYTE_RGBA = CUSTOM_TYPE_STARTING_INDEX + 4;

    public static final ImageType<byte[]> BYTE_BGRA = new ImageType<>("4BYTE_BGRA",
            TYPE_4BYTE_BGRA,
            new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] {8, 8, 8, 8}, true, false,
                    Transparency.TRANSLUCENT,
                    DataBuffer.TYPE_BYTE),
            new ByteBGRAConverter(), false);
    public static final ImageType<byte[]> BYTE_RGB = new ImageType<>("3BYTE_RGB",
            TYPE_3BYTE_RGB,
            new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] {8, 8, 8}, false, false,
                    Transparency.OPAQUE,
                    DataBuffer.TYPE_BYTE),
            new ByteRGBConverter(), false);
    public static final ImageType<byte[]> BYTE_ARGB = new ImageType<>("4BYTE_ARGB",
            TYPE_4BYTE_ARGB,
            new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] {8, 8, 8, 8}, true, false,
                    Transparency.TRANSLUCENT,
                    DataBuffer.TYPE_BYTE),
            new ByteARGBConverter(), false);
    public static final ImageType<byte[]> BYTE_ARGB_PRE = new ImageType<>("4BYTE_ARGB_PRE",
            TYPE_4BYTE_ARGB_PRE,
            new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] {8, 8, 8, 8}, true, true,
                    Transparency.TRANSLUCENT,
                    DataBuffer.TYPE_BYTE),
            new ByteARGBPreConverter(), false);

    public static final ImageType<byte[]> BYTE_RGBA = new ImageType<>("4BYTE_RGBA",
            TYPE_4BYTE_RGBA,
            new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] {8, 8, 8, 8}, true, false,
                    Transparency.TRANSLUCENT,
                    DataBuffer.TYPE_BYTE),
            new ByteRGBAConverter(), false);

    /**
     * Return an ImageType based on {@link BufferedImage#getType()}. Note this will return null for
     * image types that are not supported in this architecture (like {@link BufferedImage#TYPE_USHORT_565_RGB}).
     */
    public static ImageType<?> get(int bufferedImageType) {
        return TYPES_BY_INT.get(bufferedImageType);
    }

    /**
     * Return a name like "INT_ARGB_PRE" or "3BYTE_BGR" or "BYTE_GRAY" for a given image int.
     * <p>
     * This method is capable of returning names for BufferedImage types that are not represented by
     * ImageType constants, such as "USHORT_565_RGB". So calling <code>get(imageType).getName()</code>
     * will throw a NullPointerException for values like {@link BufferedImage#TYPE_USHORT_565_RGB}, but
     * calling this method will not throw a similar NullPointerException.
     * </p>
     */
    public static String toString(int imageTypeCode) {
        ImageType imageType = TYPES_BY_INT.get(imageTypeCode);
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
    private final int imageCode;

    private final PixelConverter<T> pixelConverter;

    private final ColorModel colorModel;

    private final boolean isBufferedImageType;

    private ImageType(String name, int imageCode, ColorModel colorModel, PixelConverter<T> converter, boolean isBufferedImageType) {
        this.name = Objects.requireNonNull(name);
        this.imageCode = imageCode;
        this.colorModel = Objects.requireNonNull(colorModel);
        this.isBufferedImageType = isBufferedImageType;

        if (TYPES_BY_NAME.put(name, this) != null) {
            // The only times this constructor should be used are for ImageType static fields in this class,
            // and all of those fields should have a unique name.
            throw new IllegalArgumentException("The ImageType \"" + name + "\" is already defined.");
        }

        if (TYPES_BY_INT.put(imageCode, this) != null) {
            throw new IllegalArgumentException("The ImageType code \"" + imageCode + "\" is already defined.");
        }

        this.pixelConverter = converter;
    }

    /**
     * Return true if this ImageType represents a BufferedImage TYPE constant,
     * such as {@link BufferedImage#TYPE_INT_ARGB}.
     */
    public boolean isBufferedImageType() {
        return isBufferedImageType;
    }

    /**
     * Return all ImageTypes.
     */
    public static ImageType[] values() {
        return values(false);
    }

    /**
     * Return all ImageTypes.
     *
     * @param returnOnlyBufferedImageTypes if true this method returns ImageTypes
     *                                     where {@link #isBufferedImageType()} is true.
     *                                     if false then this returns all ImageTypes
     */
    public static ImageType[] values(boolean returnOnlyBufferedImageTypes) {
        ImageType[] returnValue = TYPES_BY_INT.values().toArray(new ImageType[0]);
        if (returnOnlyBufferedImageTypes) {
            List<ImageType> z = new LinkedList<>();
            for (ImageType t : returnValue) {
                if (t.isBufferedImageType())
                    z.add(t);
            }
            returnValue = z.toArray(new ImageType[0]);
        }
        return returnValue;
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
        return imageCode;
    }

    /**
     * Return true if this ImageType represents pixels as integers.
     */
    public boolean isInt() {
        return colorModel instanceof DirectColorModel;
    }

    /**
     * Return true if this ImageType represents pixels as bytes.
     */
    public boolean isByte() {
        return !isInt();
    }

    /**
     * Returns true if this image type can only ever be opaque.
     */
    public boolean isOpaque() {
        return colorModel.getTransparency() == Transparency.OPAQUE;
    }

    /**
     * Return true if this ImageType represents pixels with a premultiplied alpha component.
     */
    public boolean isAlphaPremultiplied() {
        return colorModel.isAlphaPremultiplied();
    }

    @Override
    public String toString() {
        return "ImageType[ \"" + name + "\" (" + imageCode + ")]";
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
        if (colorModel instanceof DirectColorModel)
            return 1;
        if (colorModel instanceof ComponentColorModel) {
            return ((ComponentColorModel)colorModel).getNumComponents();
        }
        throw new IllegalStateException();
    }

    /**
     * Create a PixelIterator for an incoming image.
     * <p>
     * If you don't have a preference about which image type you receive, you can
     * call {@link BufferedImageIterator#create(BufferedImage)}. This can be faster
     * because it avoids converting any pixel data.
     */
    public PixelIterator<T> createPixelIterator(BufferedImage bufferedImage) {
        BufferedImageIterator<?> iter = BufferedImageIterator.create(bufferedImage);
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
     * Return ColorModel associated with this ImageType.
     */
    public ColorModel getColorModel() {
        return colorModel;
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
            pixelConverter.convertFromARGB( destPixels, destOffset, (int[]) srcPixels, srcOffset, pixelCount);
        } else if (srcType == ImageType.BYTE_ARGB) {
            pixelConverter.convertFromARGB( destPixels, destOffset, (byte[]) srcPixels, srcOffset, pixelCount);
        } else if (srcType == ImageType.INT_ARGB_PRE) {
            pixelConverter.convertFromARGBPre( destPixels, destOffset, (int[]) srcPixels, srcOffset, pixelCount);
        } else if (srcType == ImageType.BYTE_ARGB_PRE) {
            pixelConverter.convertFromARGBPre( destPixels, destOffset, (byte[]) srcPixels, srcOffset, pixelCount);
        } else if (srcType == ImageType.BYTE_ABGR) {
            pixelConverter.convertFromABGR( destPixels, destOffset, (byte[]) srcPixels, srcOffset, pixelCount);
        } else if (srcType == ImageType.BYTE_ABGR_PRE) {
            pixelConverter.convertFromABGRPre( destPixels, destOffset, (byte[]) srcPixels, srcOffset, pixelCount);
        } else if (srcType == ImageType.INT_BGR) {
            pixelConverter.convertFromBGR( destPixels, destOffset, (int[]) srcPixels, srcOffset, pixelCount);
        } else if (srcType == ImageType.BYTE_BGRA) {
            pixelConverter.convertFromBGRA( destPixels, destOffset, (byte[]) srcPixels, srcOffset, pixelCount);
        } else if (srcType == ImageType.BYTE_GRAY) {
            pixelConverter.convertFromGray( destPixels, destOffset, (byte[]) srcPixels, srcOffset, pixelCount);
        } else if (srcType == ImageType.INT_RGB) {
            pixelConverter.convertFromRGB( destPixels, destOffset, (int[]) srcPixels, srcOffset, pixelCount);
        } else if (srcType == ImageType.BYTE_RGB) {
            pixelConverter.convertFromRGB( destPixels, destOffset, (byte[]) srcPixels, srcOffset, pixelCount);
        } else if (srcType == ImageType.BYTE_BGR) {
            pixelConverter.convertFromBGR( destPixels, destOffset, (byte[]) srcPixels, srcOffset, pixelCount);
        } else if (srcType == ImageType.BYTE_RGBA) {
            pixelConverter.convertFromRGBA( destPixels, destOffset, (byte[]) srcPixels, srcOffset, pixelCount);
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
        ImageType returnValue = TYPES_BY_NAME.get(name);
        if (returnValue == null) {
            // This could indicate data was saved with a newer version that had additional ImageTypes
            // we don't support here (yet)?
            throw new RuntimeException("There is no ImageType named \"" + name + "\".");
        }
        return returnValue;
    }
}