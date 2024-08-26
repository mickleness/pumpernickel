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
package com.pump.image;

import com.pump.image.pixel.ImagePixelIterator;
import com.pump.image.pixel.Scaling;

import java.awt.*;
import java.awt.image.*;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

/**
 * This is a <code>BufferedImage</code> that offers a <code>setProperty()</code>
 * method, is serializable, supports
 * {@link #equals(Object)}/{@link #hashCode()}, and offers a {@link QBufferedImageSource}.
 *
 */
public class QBufferedImage extends BufferedImage
		implements Externalizable {

	/**
	 * This is how we encode null values in {@link #extraProperties}.
	 * <p>
	 * The contract in {@link Image#getProperty(String, ImageObserver)} promises to
	 * return {@link Image#UndefinedProperty} for missing properties, so we want
	 * to be sure if you defined a property as <code>null</code> then it will
	 * be retrieved as <code>null</code> and node <code>UndefinedProperty</code>.
	 * </p>
	 */
	private static final Object NULL_PLACEHOLDER = new Object();

	/**
	 * Return a map of all known properties of a BufferedImage.
	 */
	public static Map<String, Object> getProperties(BufferedImage bi) {
		Map<String, Object> returnValue = new HashMap<>();
		if (bi != null) {
			synchronized (bi) {
				String[] propNames = bi.getPropertyNames();
				if (propNames != null) {
					for (String key : propNames) {
						Object value = bi.getProperty(key);
						if (!(value == null || value == Image.UndefinedProperty))
							returnValue.put(key, value);
					}
				}
			}
		}
		return returnValue;
	}

	private static Hashtable<String, Object> getPropertiesHashtable(
			BufferedImage bi) {
		Map<String, Object> map = getProperties(bi);
		Hashtable<String, Object> returnValue = new Hashtable<>(map.size());
		returnValue.putAll(map);
		return returnValue;
	}

	private static int[] getBandMasks(DirectColorModel colorModel) {
		if (colorModel.hasAlpha()) {
			return new int[] {
					colorModel.getRedMask(),
					colorModel.getGreenMask(),
					colorModel.getBlueMask(),
					colorModel.getAlphaMask()
			};
		} else {
			return new int[]{
					colorModel.getRedMask(),
					colorModel.getGreenMask(),
					colorModel.getBlueMask()
			};
		}
	}

	private static WritableRaster createRaster(ColorModel colorModel, int width, int height, byte[] pixels) {
		if (colorModel instanceof IndexColorModel) {
			DataBuffer dataBuffer = new DataBufferByte(pixels, width);
			return Raster.createInterleavedRaster(dataBuffer, width, height, width, 1, new int[] {0}, null);
		} else if (colorModel instanceof ComponentColorModel) {
			ComponentColorModel ccm = (ComponentColorModel) colorModel;
			DataBuffer dataBuffer = new DataBufferByte(pixels, width * height * ccm.getPixelSize() / 8);
			int[] bandOffsets = new int[ccm.getPixelSize() / 8];
			for (int i = 0; i < bandOffsets.length; i++) {
				bandOffsets[i] = bandOffsets.length - i - 1;
			}
			return Raster.createInterleavedRaster(dataBuffer, width, height, width * ccm.getPixelSize() / 8,
					ccm.getPixelSize() / 8, bandOffsets, null);
		}

		throw new UnsupportedOperationException("unsupported ColorModel: " + colorModel);
	}


	/**
	 * These are accessed via {@link #getProperty(String)}.
	 */
	Map<String, Object> extraProperties = null;

	/**
	 * This constructor should not be used. It is only available for deserialization.
	 */
	public QBufferedImage() {
		super(1, 1, BufferedImage.TYPE_INT_RGB);
	}

	/**
	 * Create a new QBufferedImage.
	 */
	public QBufferedImage(ColorModel cm, WritableRaster r,
						  boolean premultiplied, Hashtable<String, Object> properties) {
		super(cm, r, premultiplied, properties);
	}

	/**
	 * Create a new QBufferedImage.
	 */
	public QBufferedImage(int width, int height, int imageType,
						  IndexColorModel cm) {
		super(width, height, imageType, cm);
	}

	/**
	 * Create a new QBufferedImage.
	 */
	public QBufferedImage(int width, int height, int imageType) {
		super(width, height, imageType);
	}

	/**
	 * Create a QBufferedImage that stores data in the same raster as the
	 * argument. If you modify this image or the argument: the other will also
	 * be modified.
	 */
	public QBufferedImage(BufferedImage bi) {
		this(bi.getColorModel(), bi.getRaster(), bi.isAlphaPremultiplied(),
				getPropertiesHashtable(bi));
		setAccelerationPriority(bi.getAccelerationPriority());
	}

	/**
	 * Create a QBufferedImage backed by the array of ints provided.
	 */
	public QBufferedImage(ColorModel colorModel,int width, int height, int[] pixels) {
		super(colorModel,
				Raster.createPackedRaster(new DataBufferInt(pixels, width * height), width, height, width, getBandMasks( (DirectColorModel) colorModel), new Point(0,0)),
				colorModel.isAlphaPremultiplied(), new Hashtable<>());
	}

	/**
	 * Create a QBufferedImage backed by the array of bytes provided.
	 */
	public QBufferedImage(ColorModel colorModel,int width, int height, byte[] pixels) {
		super(colorModel, createRaster(colorModel, width, height, pixels), colorModel.isAlphaPremultiplied(), new Hashtable<>());
	}

	@Override
	public synchronized Object getProperty(String name) {
		if (extraProperties != null && extraProperties.containsKey(name)) {
			Object value = extraProperties.get(name);
			if (value == NULL_PLACEHOLDER)
				value = null;
			return value;
		}
		return super.getProperty(name);
	}

	@Override
	public synchronized String[] getPropertyNames() {
		Collection<String> returnValue = new LinkedHashSet<String>();
		String[] superNames = super.getPropertyNames();
		if (superNames != null)
			returnValue.addAll(Arrays.asList(superNames));
		if (extraProperties != null) {
			returnValue.addAll(extraProperties.keySet());
		}
		return returnValue.toArray(new String[0]);
	}

	/**
	 * Assign a property value.
	 */
	public synchronized void setProperty(String propertyName, Object value) {
		if (extraProperties == null)
			extraProperties = new HashMap<String, Object>();
		if (value == null)
			value = NULL_PLACEHOLDER;
		extraProperties.put(propertyName, value);
	}

	/**
	 * Assign multiple property values.
	 */
	public synchronized void setProperties(Map<String, Object> properties) {
		if (extraProperties == null)
			extraProperties = new HashMap<String, Object>();
		extraProperties.putAll(properties);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof BufferedImage))
			return false;

		int w = getWidth();
		int h = getHeight();

		BufferedImage other = (BufferedImage) o;
		if (w != other.getWidth() || h != other.getHeight()
				|| getType() != other.getType())
			return false;
		if (!getColorModel().equals(other.getColorModel()))
			return false;
		if (!getRaster().getClass().equals(other.getRaster().getClass()))
			return false;

		Object row1 = null;
		Object row2 = null;
		for (int y = 0; y < h; y++) {
			row1 = getRaster().getDataElements(0, y, w, 1, row1);
			row2 = other.getRaster().getDataElements(0, y, w, 1, row2);

			if (row1 instanceof int[]) {
				if (!Arrays.equals((int[]) row1, (int[]) row2))
					return false;
			} else if (row1 instanceof byte[]) {
				if (!Arrays.equals((byte[]) row1, (byte[]) row2))
					return false;
			} else if (row1 instanceof short[]) {
				if (!Arrays.equals((short[]) row1, (short[]) row2))
					return false;
			} else {
				// I'm not sure if this can ever happen? If it does happen, then we can scale
				// this method up as needed
				throw new IllegalStateException(row1.getClass().getName());
			}
		}

		if (!getProperties(this).equals(getProperties(other)))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int w = getWidth();
		int h = getHeight();
		int returnValue = (getWidth() << 14) + (getHeight() << 4) + getType();

		for (Point p : new Point[] { new Point(0, 0), new Point(w - 1, 0),
				new Point(0, h - 1), new Point(w - 1, h - 1),
				new Point(w / 2, h / 2) }) {
			int argb = getRGB(p.x, p.y);
			returnValue ^= argb;
		}

		return returnValue;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "["
				+ Integer.toHexString(hashCode()) + ", type = " + getType()
				+ ", colorModel = " + getColorModel() + ", raster = "
				+ getRaster() + ", properties = " + getProperties(this) + "]";
	}

	@Override
	public ImageProducer getSource() {
		return new QBufferedImageSource(this, getProperties(this), super.getSource());
	}

	@Override
	public QBufferedImage getScaledInstance(int width, int height, int hints) {
		if ((hints & (SCALE_SMOOTH | SCALE_AREA_AVERAGING)) != 0) {
			return Scaling.scale(this, new Dimension(width, height), null, null);
		}
		return ImagePixelIterator.createBufferedImage(super.getScaledInstance(width, height, hints));
	}

	///////// serialization:

	/**
	 * Return true if this object can be serialized, or false if attempting to
	 * serialize this image will throw an IOException.
	 * <p>
	 * For example: if {@link #getType()} returns
	 * {@link BufferedImage#TYPE_CUSTOM} then this method will return false.
	 */
	public boolean isSerializationSupported() {
		return getSerializationUnsupportedReason() != null;
	}

	/**
	 * Return a String for an IOException explaining why this image shouldn't be
	 * serialized, or null if this image is serializable.
	 */
	private String getSerializationUnsupportedReason() {
		switch (getType()) {
		case BufferedImage.TYPE_3BYTE_BGR:
		case BufferedImage.TYPE_4BYTE_ABGR:
		case BufferedImage.TYPE_4BYTE_ABGR_PRE:
		case BufferedImage.TYPE_BYTE_GRAY:
		case BufferedImage.TYPE_INT_ARGB:
		case BufferedImage.TYPE_INT_ARGB_PRE:
		case BufferedImage.TYPE_INT_BGR:
		case BufferedImage.TYPE_INT_RGB:
			return null;

		case BufferedImage.TYPE_BYTE_BINARY:
		case BufferedImage.TYPE_BYTE_INDEXED:
			// IndexColorModels COULD be supported, I just haven't gotten around
			// to it:
			return "This QBufferedImage is not serializable. IndexColorModels are not supported.";

		case BufferedImage.TYPE_CUSTOM:
		default:
			return "This QBufferedImage is not serializable. This image type ("
					+ getType() + ") is not supported.";

		}
	}

	private transient QBufferedImage replacementImage;

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		String msg = getSerializationUnsupportedReason();
		if (msg != null)
			throw new IOException(msg);

		out.writeInt(0);
		out.writeInt(getWidth());
		out.writeInt(getHeight());
		out.writeInt(getType());
		out.writeFloat(getAccelerationPriority());
		out.writeObject(getProperties(this));
		out.writeObject(getRaster().getDataElements(0, 0, getWidth(),
				getHeight(), null));
	}

	@Override
	public void readExternal(ObjectInput in)
			throws IOException, ClassNotFoundException {
		int internalVersion = in.readInt();
		if (internalVersion == 0) {
			int width = in.readInt();
			int height = in.readInt();
			int type = in.readInt();
			float accelerationPriority = in.readFloat();
			Map<String, Object> properties = (Map<String, Object>) in
					.readObject();
			Object rasterData = in.readObject();

			// This approach is a little bit wasteful, because we have a large
			// primitive array and now we're about to create an array of the
			// same size and populate it.

			// If this waste comes up on profilers we can save some short-term
			// memory usage and probably get a little speed increase if create
			// our own DataBuffer from the array and create our own
			// WritableRaster. But for now I don't plan on using this in a hot
			// loop so I'm not sure that optimization is necessary.

			replacementImage = new QBufferedImage(width, height, type);
			replacementImage.setAccelerationPriority(accelerationPriority);
			replacementImage.getRaster().setDataElements(0, 0, width, height,
					rasterData);
			replacementImage.setProperties(properties);
		}
	}

	private Object readResolve() {
		if (replacementImage != null) {
			return replacementImage;
		}
		return this;
	}
}