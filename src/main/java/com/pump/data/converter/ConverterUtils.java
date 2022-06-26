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
package com.pump.data.converter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.pump.awt.converter.AlphaCompositeMapConverter;
import com.pump.awt.converter.AttributedCharacterIteratorMapConverter;
import com.pump.awt.converter.AttributedStringMapConverter;
import com.pump.awt.converter.BasicStrokeMapConverter;
import com.pump.awt.converter.FontMapConverter;
import com.pump.awt.converter.FontRenderContextMapConverter;
import com.pump.awt.converter.GlyphJustificationInfoMapConverter;
import com.pump.awt.converter.GlyphMetricsMapConverter;
import com.pump.awt.converter.GlyphVectorMapConverter;
import com.pump.awt.converter.GradientPaintMapConverter;
import com.pump.awt.converter.ImageMapConverter;
import com.pump.awt.converter.LinearGradientPaintMapConverter;
import com.pump.awt.converter.Point2DMapConverter;
import com.pump.awt.converter.RadialGradientPaintMapConverter;
import com.pump.awt.converter.Rectangle2DMapConverter;
import com.pump.awt.converter.RenderedImageMapConverter;
import com.pump.awt.converter.RenderingHintsMapConverter;
import com.pump.awt.converter.ShapeMapConverter;
import com.pump.awt.converter.TexturePaintMapConverter;

/**
 * This is a collection of static methods related to BeanMapConverters. For
 * example: this includes serialization, toString(), hashCode() and equals()
 * methods.
 * <p>
 * This includes a list of BeanMapConverters that you can add to by calling
 * {@link #addConverter(BeanMapConverter, boolean, boolean, boolean, boolean)}.
 * This list is ordered, where the last elements have highest priority.
 */
public class ConverterUtils {

	/**
	 * This wraps a byte array in an object with an equals() method.
	 */
	public static class ByteArray implements Serializable {

		private static final long serialVersionUID = 1L;

		public static ByteArray get(byte[] array) {
			if (array == null)
				return null;
			return new ByteArray(array);
		}

		public byte[] data;

		public ByteArray(byte[] data) {
			this.data = data;
		}

		@Override
		public int hashCode() {
			int returnValue = 0;
			for (int a = 0; a < 4; a++) {
				if (a < data.length)
					returnValue = (returnValue << 8) + data[a];
			}
			return returnValue;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ByteArray))
				return false;
			ByteArray other = (ByteArray) obj;
			return Arrays.equals(data, other.data);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("ByteArray[ length=");
			sb.append(data.length);
			sb.append(", data={");
			for (int a = 0; a <= 15; a++) {
				if (a >= data.length)
					break;
				if (a > 0)
					sb.append(", ");
				if (a == 15) {
					sb.append("...");
				} else {
					sb.append(data[a]);
				}
			}
			sb.append("}]");
			return sb.toString();
		}
	}

	/**
	 * This wraps a float array in an object with an equals() method.
	 */
	public static class FloatArray implements Serializable {

		private static final long serialVersionUID = 1L;

		public static FloatArray get(float[] array) {
			if (array == null)
				return null;
			return new FloatArray(array);
		}

		public float[] data;

		private FloatArray(float[] data) {
			this.data = data;
		}

		@Override
		public int hashCode() {
			int returnValue = 0;
			for (int a = 0; a < 4; a++) {
				if (a < data.length)
					returnValue = (returnValue << 8) + Float.hashCode(data[a]);
			}
			return returnValue;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof FloatArray))
				return false;
			FloatArray other = (FloatArray) obj;
			return Arrays.equals(data, other.data);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("FloatArray[ length=");
			sb.append(data.length);
			sb.append(", data={");
			for (int a = 0; a <= 15; a++) {
				if (a >= data.length)
					break;
				if (a > 0)
					sb.append(", ");
				if (a == 15) {
					sb.append("...");
				} else {
					sb.append(data[a]);
				}
			}
			sb.append("}]");
			return sb.toString();
		}
	}

	private static final LinkedList<BeanMapConverter<?>> CONVERTERS = new LinkedList<>();
	private static final Collection<BeanMapConverter<?>> FOR_EQUALS = new HashSet<>();
	private static final Collection<BeanMapConverter<?>> FOR_HASHCODE = new HashSet<>();
	private static final Collection<BeanMapConverter<?>> FOR_SERIALIZATION = new HashSet<>();
	private static final Collection<BeanMapConverter<?>> FOR_TO_STRING = new HashSet<>();

	private static boolean isInitialized = false;

	/**
	 * This initializes a set of known basic converters. This is automatically
	 * called the first time you invoke {@link #getConverter(Class)}. But before
	 * then you can (if you want to) add converters before this is called.
	 */
	public static void initialize() {
		if (isInitialized)
			return;
		isInitialized = true;

		// shape must come before Rectangle2D
		addConverter(new ShapeMapConverter(), true, true, true, true);

		addConverter(new AlphaCompositeMapConverter(), false, false, true,
				true);
		addConverter(new AttributedCharacterIteratorMapConverter(), true, true,
				true, true);
		addConverter(new AttributedStringMapConverter(), true, true, true,
				true);
		addConverter(new BasicStrokeMapConverter(), false, false, true, true);
		addConverter(new FontMapConverter(), true, false, true, false);
		addConverter(new FontRenderContextMapConverter(), false, false, true,
				true);
		addConverter(new GlyphJustificationInfoMapConverter(), true, true, true,
				true);
		addConverter(new GlyphMetricsMapConverter(), true, true, true, true);
		addConverter(new GlyphVectorMapConverter(), true, true, true, true);
		addConverter(new GradientPaintMapConverter(), true, true, true, true);
		addConverter(new LinearGradientPaintMapConverter(), true, true, true,
				true);
		addConverter(new Point2DMapConverter(), false, false, true, true);
		addConverter(new RadialGradientPaintMapConverter(), true, true, true,
				true);
		addConverter(new Rectangle2DMapConverter(), false, false, true, true);
		addConverter(new RenderingHintsMapConverter(), false, false, true,
				false);
		addConverter(new TexturePaintMapConverter(), true, true, true, true);

		// these image converters do the same thing; it doesn't matter which
		// comes
		// first but only one will "win".
		addConverter(new ImageMapConverter(), true, true, true, true);
		addConverter(new RenderedImageMapConverter(), true, true, true, true);
	}

	/**
	 * Add a converter to the list of converters this object manages.
	 * 
	 * @param converter
	 *            the converter to add.
	 * @param forEquals
	 *            if true then this converter can be used by
	 *            {@link #equals(Object, Object)}.
	 * @param forHashcode
	 *            if true then this converter can be used by
	 *            {@link #hashCode(Object...)}.
	 * @param forSerialization
	 *            if true then this converter can be used by
	 *            {@link #writeObject(ObjectOutputStream, Object)}.
	 * @param forToString
	 *            if true then this converter can be used by
	 *            {@link #toString(Object)}.
	 */
	public static void addConverter(BeanMapConverter<?> converter,
			boolean forEquals, boolean forHashcode, boolean forSerialization,
			boolean forToString) {
		CONVERTERS.add(converter);
		if (forEquals)
			FOR_EQUALS.add(converter);
		if (forToString)
			FOR_TO_STRING.add(converter);
		if (forHashcode)
			FOR_HASHCODE.add(converter);
		if (forSerialization)
			FOR_SERIALIZATION.add(converter);
	}

	private static boolean isForHashCode(BeanMapConverter<?> converter) {
		return FOR_HASHCODE.contains(converter);
	}

	private static boolean isForSerialization(BeanMapConverter<?> converter) {
		return FOR_SERIALIZATION.contains(converter);
	}

	private static boolean isForToString(BeanMapConverter<?> converter) {
		return FOR_TO_STRING.contains(converter);
	}

	private static boolean isForEquals(BeanMapConverter<?> converter) {
		return FOR_EQUALS.contains(converter);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> getAtoms(Object obj) {
		if (obj == null)
			return null;
		BeanMapConverter converter = getConverter(obj.getClass());
		if (converter != null)
			return converter.createAtoms(obj);
		return null;
	}

	/**
	 * Return the BeanMapConverter for the given class, or null if no
	 * BeanMapConverter is registered.
	 * <p>
	 * This identifies the most recently added BeanMapConverter that supports
	 * the given argument.
	 */
	public static BeanMapConverter<?> getConverter(Class<?> dataClass) {
		initialize();

		Iterator<BeanMapConverter<?>> iter = CONVERTERS.descendingIterator();
		while (iter.hasNext()) {
			BeanMapConverter<?> converter = iter.next();
			Class<?> z = converter.getType();
			if (z.isAssignableFrom(dataClass))
				return converter;
		}
		return null;
	}

	public static int hashCode(Object... objects) {
		int returnValue = 0;
		for (Object obj : objects) {
			returnValue = returnValue << 8;
			if (obj != null) {
				Class<?> z = obj.getClass();
				BeanMapConverter<?> converter = getConverter(z);
				if (converter == null || !isForHashCode(converter)) {
					returnValue += obj.hashCode();
				} else {
					Map<String, Object> atoms = getAtoms(obj);
					SortedSet<String> alphabetizedKeys = new TreeSet<>(
							atoms.keySet());
					for (String key : alphabetizedKeys) {
						returnValue += atoms.get(key).hashCode();
					}
				}
			}
		}
		return returnValue;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean equals(Object obj1, Object obj2) {
		if (obj1 == null && obj2 == null)
			return true;
		if (obj1 == null || obj2 == null)
			return false;

		BeanMapConverter converter1 = getConverter(obj1.getClass());
		BeanMapConverter converter2 = getConverter(obj2.getClass());

		if (converter1 != null && converter1 == converter2
				&& isForEquals(converter1)) {
			Map<String, Object> atoms1 = converter1.createAtoms(obj1);
			Map<String, Object> atoms2 = converter2.createAtoms(obj2);
			return atoms1.equals(atoms2);
		}

		return obj1.equals(obj2);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void writeObject(ObjectOutputStream objOut, Object obj)
			throws IOException {
		if (obj == null) {
			objOut.writeObject(null);
			objOut.writeObject(null);
		} else {
			if (obj instanceof Serializable) {
				objOut.writeObject(null);
				objOut.writeObject(obj);
			} else {
				Class<?> objClass = obj.getClass();
				BeanMapConverter converter = getConverter(objClass);
				if (converter == null || !isForSerialization(converter)) {
					objOut.writeObject(null);
					objOut.writeObject(obj);
				} else {
					Map<String, Object> atoms = converter.createAtoms(obj);
					objOut.writeObject(converter);
					objOut.writeObject(atoms);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static Object readObject(ObjectInputStream objIn)
			throws ClassNotFoundException, IOException {
		BeanMapConverter<?> converter = (BeanMapConverter<?>) objIn
				.readObject();
		if (converter == null) {
			return objIn.readObject();
		}
		Map<String, Object> atoms = (Map<String, Object>) objIn.readObject();
		return converter.createFromAtoms(atoms);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String toString(Object obj) {
		if (obj == null)
			return String.valueOf(null);

		Class z = obj.getClass();
		BeanMapConverter converter = getConverter(z);
		if (converter == null || !isForToString(converter))
			return obj.toString();
		Map<String, Object> atoms = converter.createAtoms(obj);
		SortedSet<String> alphabetizedKeys = new TreeSet<>(atoms.keySet());
		if (z.isAnonymousClass()) {
			// convert from a name like "$1" to "Shape":
			z = converter.getType();
		}

		StringBuilder sb = new StringBuilder();
		sb.append(z.getSimpleName());
		sb.append("[ ");
		boolean containsData = false;
		for (String key : alphabetizedKeys) {
			if (containsData)
				sb.append(", ");
			sb.append(key + "=" + atoms.get(key));
			containsData = true;
		}
		sb.append("]");
		return sb.toString();
	}
}