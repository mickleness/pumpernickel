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
package com.pump.awt.text.writing;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.pump.animation.writing.WritingShape;
import com.pump.awt.CalligraphyStroke;
import com.pump.geom.MeasuredShape;
import com.pump.util.ObservableProperties;
import com.pump.util.ObservableProperties.Key;

/**
 * A font that can be slowly rendered stroke by stroke.
 * <p>
 * Unlike a simple wipe-right transition (which would work with any font), this
 * requires specially formatted font-like shape data. The current implementation
 * is modeled after the public domain font <a
 * href=\"http://comicneue.com/\">Comic Neue</a>, and some primitive
 * calligraphy.
 * 
 * @see <a
 *      href="https://javagraphics.blogspot.com/2014/11/text-handwriting-text-effect.html">Text:
 *      Handwriting Text Effect</a>
 */
public class WritingFont {

	public static final String FILE_EXTENSION = "writtenfont";

	protected static final Number version = 1f;

	public static final Key<Character> MISSING_GLYPH_CHAR = new Key<Character>(
			"missing-glyph", Character.class);
	public static final Key<Number> ITALICIZED_ANGLE = new Key<Number>(
			"italicized-angle", Number.class);
	public static final Key<Number> NIB_ANGLE = new Key<Number>("nib-angle",
			Number.class);
	public static final Key<Number> VERSION = new Key<Number>("version",
			Number.class);

	/** Either STYLE_PLAIN or STYLE_CALLIGRAPHY */
	public static final Key<String> STYLE = new Key<String>("style",
			String.class);

	/**
	 * The median (as a fraction from [0-1]) of this font.
	 * <p>
	 * The ascent is the distance from the baseline to the ascender line. The
	 * ascent usually represents the the height of the capital letters of the
	 * text. Some characters can extend above the ascender line.
	 * <p>
	 * Note this is represented as fraction from [0,1], so to calculate the
	 * ascent in terms of pixels: multiply this value by getSize2D().
	 */
	public static final Key<Float> MEDIAN = new Key<Float>("median",
			Float.class);

	/**
	 * The descent (as a fraction from [0-1]) of this font.
	 * <p>
	 * The descent is the distance from the baseline to the descender line. The
	 * descent usually represents the distance to the bottom of lower case
	 * letters like 'p'. Some characters can extend below the descender line.
	 * <p>
	 * Note this is represented as fraction from [0,1], so to calculate the
	 * descent in terms of pixels: multiply this value by getSize2D().
	 */
	public static final Key<Float> DESCENT = new Key<Float>("descent",
			Float.class);

	/**
	 * The leading (as a fraction from [0-1]) of this font.
	 * <p>
	 * The leading is the recommended distance from the bottom of the descender
	 * line to the top of the next line.
	 * <p>
	 * Note this is represented as fraction from [0,1], so to calculate the
	 * leading in terms of pixels: multiply this value by getSize2D().
	 */
	public static final Key<Float> LEADING = new Key<Float>("leading",
			Float.class);

	/** The name of this font. */
	public static final Key<String> NAME = new Key<String>("name", String.class);

	/** A value of the STYLE key ("plain") */
	public static final String STYLE_PLAIN = "plain";

	/** A value of the STYLE key ("calligraphy") */
	public static final String STYLE_CALLIGRAPHY = "calligraphy";

	/** A WritingFont that resembles calligraphy. */
	public static WritingFont CALLIGRAPHY = getFont(WritingFont.class
			.getResource("Calligraphy.writtenfont"));

	/**
	 * A WritingFont that resembles the public domain font <a
	 * href="http://comicneue.com/">Comic Neue</a>
	 */
	public static WritingFont COMIC_NEUE = getFont(WritingFont.class
			.getResource("ComicNeue.writtenfont"));

	private static WritingFont getFont(URL url) {
		try {
			return new WritingFont(url);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	ObservableProperties properties = new ObservableProperties();

	/** Create an empty WritingFont. */
	public WritingFont() {
		properties.set(NAME, "Untitled");
		properties.set(MEDIAN, .6f);
		properties.set(DESCENT, .3f);
		properties.set(LEADING, .1f);
	}

	/** Create a WritingFont from a URL. */
	public WritingFont(URL url) throws IOException {
		this();
		if (url == null)
			throw new NullPointerException();

		InputStream in = null;
		try {
			in = url.openStream();
			populate(in, url.toString());
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	/** Create a WritingFont from a File. */
	public WritingFont(File file) throws IOException {
		this();
		if (file == null)
			throw new NullPointerException();

		String name = file.getName();
		int i = name.lastIndexOf('.');
		name = i == -1 ? name : name.substring(0, i);
		properties.set(NAME, name);

		InputStream in = null;
		try {
			in = new FileInputStream(file);
			populate(in, file.getAbsolutePath());
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	private void populate(InputStream in, String identifier) throws IOException {
		Properties properties = new Properties();
		properties.loadFromXML(in);
		String versionString = (String) properties.remove(VERSION.toString());
		Float versionValue = Float.parseFloat(versionString);
		boolean badVersion = versionValue.floatValue() > version.floatValue();
		if (badVersion) {
			System.err
					.println("WritingFont: The resource \""
							+ identifier
							+ "\" was created with a newer version and may use features this version does not support.");
		}

		try {
			Set<Object> keys = properties.keySet();
			Iterator<Object> iter = keys.iterator();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				if (key.length() == 1) {
					String value = (String) properties.get(key);
					char ch = key.charAt(0);
					this.properties.set(getGlyphKey(ch),
							new WritingShape(value));
				} else if (key.equals(MISSING_GLYPH_CHAR.toString())) {
					char c = ((String) properties.get(key)).charAt(0);
					this.properties.set(MISSING_GLYPH_CHAR, c);
				} else if (key.equals(DESCENT.toString())) {
					Float f = Float.parseFloat((String) properties.get(key));
					this.properties.set(DESCENT, f);
				} else if (key.equals(ITALICIZED_ANGLE.toString())) {
					Float f = Float.parseFloat((String) properties.get(key));
					this.properties.set(ITALICIZED_ANGLE, f);
				} else if (key.equals(LEADING.toString())) {
					Float f = Float.parseFloat((String) properties.get(key));
					this.properties.set(LEADING, f);
				} else if (key.equals(MEDIAN.toString())) {
					Float f = Float.parseFloat((String) properties.get(key));
					this.properties.set(MEDIAN, f);
				} else if (key.equals(NIB_ANGLE.toString())) {
					Float f = Float.parseFloat((String) properties.get(key));
					this.properties.set(NIB_ANGLE, f);
				} else {
					// let's hope this was meant to be a string:
					this.properties.set(new Key<String>(key, String.class),
							(String) properties.get(key));
				}
			}
		} catch (RuntimeException e) {
			if (badVersion)
				throw new RuntimeException(
						"The resource \""
								+ identifier
								+ "\" was created with a newer version and appears to use features this version does not support.",
						e);
			throw e;
		}
	}

	/**
	 * Write this object to a File.
	 * 
	 * @param file
	 *            the file to write to.
	 * @throws IOException
	 *             if an error occurs writing this data.
	 */
	public void write(File file) throws IOException {
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			write(out);
		} finally {
			out.close();
		}
	}

	/**
	 * Write this object to an OutputStream.
	 * 
	 * @param out
	 *            the stream to write to.
	 * @throws IOException
	 *             if an error occurs writing this data.
	 */
	public void write(OutputStream out) throws IOException {
		Map<String, Object> map = properties.getMap(false, false,
				ObservableProperties.DEFAULT);
		Properties properties = new Properties();

		// convert everything to a String:
		for (String key : map.keySet()) {
			properties.put(key, map.get(key).toString());
		}

		properties.put(VERSION.toString(), version.toString());
		properties.storeToXML(out, "");
	}

	/**
	 * Add a PropertyChangeListener to be notified when one of the Keys defined
	 * as a field in this class changes.
	 * 
	 * @param pcl
	 */
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		properties.addListener(pcl);
	}

	/**
	 * Removes a PropertyChangeListener.
	 */
	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		properties.removeListener(pcl);
	}

	/**
	 * Reassign a property.
	 * 
	 * @param key
	 *            the key to modify
	 * @param newValue
	 *            the value to assign.
	 * @return the previous value.
	 */
	public <T> T setProperty(Key<T> key, T newValue) {
		if (key.toString().length() == 1)
			throw new IllegalArgumentException(
					"Keys must be more than 1 character.");
		return properties.set(key, newValue);
	}

	/**
	 * Retrieve a property.
	 * 
	 * @param key
	 *            the key to retrieve.
	 * @return the value assigned to the given key.
	 */
	public <T> T getProperty(Key<T> key) {
		return properties.get(key);
	}

	/** @return all the defined glyphs. */
	public Set<Character> getDefinedGlyphs() {
		TreeSet<Character> set = new TreeSet<Character>();
		for (String key : properties.getMap(false, false,
				ObservableProperties.DEFAULT).keySet()) {
			if (key.length() == 1) {
				set.add(new Character(key.charAt(0)));
			}
		}
		return set;
	}

	/** This is only made public for PropertyChangeListeners. */
	public Key<WritingShape> getGlyphKey(char ch) {
		return new Key<WritingShape>("" + ch, WritingShape.class);
	}

	/** @return the shape (glyph) associated with a character, if any. */
	public WritingShape getGlyph(char c) {
		Key<WritingShape> key = getGlyphKey(c);
		return properties.get(key);
	}

	/**
	 * Reassign the shape (glyph) for a character.
	 * 
	 * @param c
	 *            the character to assign the glyph for.
	 * @param glyph
	 *            the new glyph (may be null)
	 */
	public void setGlyph(char c, WritingShape glyph) {
		Key<WritingShape> key = getGlyphKey(c);
		properties.set(key, glyph);
	}

	/**
	 * @return the recommended stroke for a font this height.
	 */
	public Stroke getRecommendedStroke(float height) {
		float strokeWidth = height / 12f;
		String style = getProperty(WritingFont.STYLE);
		if (style == null)
			style = WritingFont.STYLE_PLAIN;
		if (WritingFont.STYLE_PLAIN.equals(style)) {
			return new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_ROUND);
		} else {
			strokeWidth *= 1.2f;
			Number nibAngle = getProperty(WritingFont.NIB_ANGLE);
			if (nibAngle == null)
				nibAngle = -Math.PI / 4;

			return new CalligraphyStroke(strokeWidth, nibAngle.floatValue());
		}
	}

	/** Return the number of defined glyphs. */
	public int getGlyphCount() {
		Set<Key<?>> keys = properties.keys(ObservableProperties.DEFAULT);
		int ctr = 0;
		for (Key<?> key : keys) {
			Object t = properties.get(key);
			if (t instanceof WritingShape && key.toString().length() == 1)
				ctr++;
		}
		return ctr;
	}

	/** Return all characters that map to a glyph in this WritingFont. */
	public Iterator<Character> getDefinedChars() {
		SortedSet<Character> dest = new TreeSet<Character>();
		Set<Key<?>> keys = properties.keys(ObservableProperties.DEFAULT);
		for (Key<?> key : keys) {
			Object t = properties.get(key);
			if (t instanceof WritingShape && key.toString().length() == 1) {
				dest.add(key.toString().charAt(0));
			}
		}
		return dest.iterator();
	}

	/**
	 * Create a JavaScript file with the function "getGlyph(letter)".
	 * 
	 * @param file
	 *            the JavaScript file to write.
	 * @throws IOException
	 *             if an IO error occurs writing the file.
	 */
	public void writeJavaScript(File file) throws IOException {
		PrintStream ps = null;
		try {
			ps = new PrintStream(file);
			ps.println("var allLetters = new Object();");
			ps.println();

			ps.println("function getGlyph(letter) {");
			ps.println("\tvar keyName = \"glyph-\"+letter;");

			ps.println("\tvar g = allLetters[keyName];");
			ps.println("\tif(g==null) {");
			ps.println("\t\tg = createGlyph(letter);");
			ps.println("\t\tallLetters[keyName] = g;");
			ps.println("\t}");
			ps.println("\treturn g;");
			ps.println("}");
			ps.println("");
			ps.println("function createGlyph(letter) {");
			ps.println("\tvar glyph = new Object();");
			ps.println("\tglyph.descent = " + getProperty(WritingFont.DESCENT)
					+ ";");
			ps.println("\tglyph.leading = " + getProperty(WritingFont.LEADING)
					+ ";");
			for (Character ch : getDefinedGlyphs()) {
				if (ch == '\'') {
					ps.println("\tif( letter==\'\\\'\') {");
				} else if (ch == '\\') {
					ps.println("\tif( letter==\'\\\\\') {");
				} else {
					ps.println("\tif( letter==\'" + ch + "\') {");
				}
				WritingShape ws = getGlyph(ch);

				ps.println("\t\tglyph.bounds = new Object();");
				ps.println("\t\tglyph.unitWidth = " + ws.getBounds().getWidth()
						+ ";");
				ps.println("\t\tglyph.pixels = " + ws.getPixels() + ";");

				ps.println("\t\tglyph.paint = function(ctx, destinationBounds, percentComplete) {");
				ps.println("\t\t\tvar tx = destinationBounds.x;");
				ps.println("\t\t\tvar ty = destinationBounds.y;");
				ps.println("\t\t\tvar sx = destinationBounds.width/glyph.unitWidth;");
				ps.println("\t\t\tvar sy = destinationBounds.height");
				ps.println("\t\t\tctx.beginPath();");
				MeasuredShape[] ms = new MeasuredShape[ws.getStrokes().size()];
				float totalLength = 0;
				GeneralPath totalShape = new GeneralPath();
				for (int a = 0; a < ws.strokes.size(); a++) {
					ms[a] = new MeasuredShape(ws.strokes.get(a).getShape());
					totalShape.append(ws.strokes.get(a).getShape(), false);
					totalLength += ms[a].getOriginalDistance();
				}
				for (int j = 0; j < 4; j++) {
					Shape shapeToWrite;
					if (j == 3) {
						shapeToWrite = totalShape;
					} else {
						GeneralPath t = new GeneralPath();
						float allowedLength = totalLength * (1 + j) / 4;
						for (int a = 0; a < ms.length && allowedLength > 0; a++) {
							float myLength;
							if (allowedLength >= ms[a].getOriginalDistance()) {
								myLength = ms[a].getOriginalDistance();
								t.append(ws.strokes.get(a).getShape(), false);
							} else {
								myLength = Math.min(allowedLength,
										ms[a].getOriginalDistance());
								t.append(ms[a].getShape(0,
										myLength / ms[a].getClosedDistance()),
										false);
							}
							allowedLength = Math.max(0, allowedLength
									- myLength);
						}
						shapeToWrite = t;
					}
					if (j == 0) {
						ps.println("\t\t\tif(percentComplete<" + (j + 1) * 25
								+ ") {");
					} else if (j == 3) {
						ps.println("\t\t\t} else {");
					} else {
						ps.println("\t\t\t} else if(percentComplete<" + (j + 1)
								* 25 + ") {");
					}

					PathIterator i = shapeToWrite.getPathIterator(null);
					float[] coords = new float[6];
					while (!i.isDone()) {
						int k = i.currentSegment(coords);
						if (k == PathIterator.SEG_MOVETO) {
							ps.println("\t\t\t\tctx.moveTo( " + coords[0]
									+ "*sx+tx, " + coords[1] + "*sy+ty);");
						} else if (k == PathIterator.SEG_LINETO) {
							ps.println("\t\t\t\tctx.lineTo( " + coords[0]
									+ "*sx+tx, " + coords[1] + "*sy+ty);");
						} else if (k == PathIterator.SEG_QUADTO) {
							ps.println("\t\t\t\tctx.quadraticCurveTo( "
									+ coords[0] + "*sx+tx, " + coords[1]
									+ "*sy+ty," + coords[2] + "*sx+tx, "
									+ coords[3] + "*sy+ty);");
						} else if (k == PathIterator.SEG_CUBICTO) {
							ps.println("\t\t\t\tctx.bezierCurveTo( "
									+ coords[0] + "*sx+tx, " + coords[1]
									+ "*sy+ty," + coords[2] + "*sx+tx, "
									+ coords[3] + "*sy+ty," + coords[4]
									+ "*sx+tx, " + coords[5] + "*sy+ty);");
						} else if (k == PathIterator.SEG_CLOSE) {
							ps.println("\t\t\t\tctx.closePath();");
						}
						i.next();
					}

					if (j == 3) {
						ps.println("\t\t\t}");
					}
				}
				ps.println("\t\t\tctx.stroke();");
				ps.println("\t\t}");

				ps.println("\t\treturn glyph;");
				ps.println("\t}");
			}
			ps.println("\treturn null;");
			ps.println("}");
		} finally {
			ps.close();
		}
	}
}