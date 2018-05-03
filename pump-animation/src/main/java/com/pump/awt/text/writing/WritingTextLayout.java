/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://raw.githubusercontent.com/mickleness/pumpernickel/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.awt.text.writing;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.pump.animation.AnimationReader;
import com.pump.animation.writing.WritingShape;
import com.pump.animation.writing.WritingStroke;
import com.pump.awt.Dimension2D;
import com.pump.util.ObservableProperties;
import com.pump.util.ObservableProperties.Key;
import com.pump.util.Resettable;

/**
 * Analogous to a <code>java.awt.font.TextLayout</code>, this helps lay out
 * glyphs in a WritingFont.
 */
public class WritingTextLayout {

	public static final Key<String> TEXT = new Key<String>("text", String.class);
	public static final Key<WritingFont> FONT = new Key<WritingFont>("font",
			WritingFont.class);
	public static final Key<Float> FONT_SIZE = new Key<Float>("font-size",
			Float.class);
	public static final Key<Float> BEATS_PER_SECOND_KEY = new Key<Float>(
			"beats-per-second", Float.class);
	public static final Key<Float> PIXELS_PER_SECOND_KEY = new Key<Float>(
			"pixels-per-second", Float.class);
	private static final Key<Object> GLYPHS_KEY = new Key<Object>("glyphs",
			Object.class);

	protected ObservableProperties properties = new ObservableProperties();

	public WritingTextLayout() {
		setText("");
		setFont(WritingFont.COMIC_NEUE);
		setFontSize(24);
		setBeatsPerSecond(1);
		setPixelsPerSecond(10);
		addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (BEATS_PER_SECOND_KEY.matches(evt)
						|| PIXELS_PER_SECOND_KEY.matches(evt))
					return;
				invalidate();
			}
		});
	}

	public WritingTextLayout(WritingFont f, int size, String text) {
		this();
		setFont(f);
		setFontSize(size);
		setText(text);
	}

	public void setText(String newText) {
		if (newText == null)
			throw new NullPointerException();
		properties.set(TEXT, newText);
	}

	public String getText() {
		return properties.get(TEXT);
	}

	public void setFont(WritingFont font) {
		if (font == null)
			throw new NullPointerException();
		properties.set(FONT, font);
	}

	public WritingFont getFont() {
		return properties.get(FONT);
	}

	public void setFontSize(float size) {
		if (size <= 0)
			throw new IllegalArgumentException("size (" + size
					+ ") must be positive");
		properties.set(FONT_SIZE, size);
	}

	public float getFontSize() {
		return properties.get(FONT_SIZE);
	}

	public void setPixelsPerSecond(float beatsPerSecond) {
		properties.set(PIXELS_PER_SECOND_KEY, beatsPerSecond);
	}

	public void setBeatsPerSecond(float beatsPerSecond) {
		properties.set(BEATS_PER_SECOND_KEY, beatsPerSecond);
	}

	public float getBeatsPerSecond() {
		return properties.get(BEATS_PER_SECOND_KEY);
	}

	public float getPixelsPerSecond() {
		return properties.get(PIXELS_PER_SECOND_KEY);
	}

	/**
	 * Clear cached data. This is invoked when certain properties are changed,
	 * and outside callers may also choose to invoke this to flush data from
	 * memory if a layout is no longer visible.
	 */
	public void invalidate() {
		properties.clear(ObservableProperties.TRANSIENT);
	}

	public void append(char ch) {
		setText(getText() + ch);
	}

	public void addPropertyChangeListener(
			PropertyChangeListener propertyChangeListener) {
		properties.addListener(propertyChangeListener);
	}

	public void removePropertyChangeListener(
			PropertyChangeListener propertyChangeListener) {
		properties.removeListener(propertyChangeListener);
	}

	public Dimension2D getSize(Rectangle rectangle) {
		Line[] lines = getLines(rectangle.width);
		float width = 0;
		float height = 0;
		for (Line line : lines) {
			width = Math.max(width, line.width);
			height += line.getHeight();
		}
		return new Dimension2D(width, height);
	}

	public void paint(Graphics2D g, Rectangle rectangle, float time,
			Color foreground) {
		Line[] lines = getLines(rectangle.width);
		Graphics2D g2 = (Graphics2D) g.create();
		try {
			int bottom = rectangle.y + rectangle.height;
			g2.clipRect(rectangle.x, rectangle.y, rectangle.width,
					rectangle.height);
			int y = rectangle.y;
			g2.setColor(foreground);
			g2.setStroke(getFont().getRecommendedStroke(getFontSize()));
			for (Line line : lines) {
				boolean finished = line.paint(g2, rectangle.x, y, time);
				y += line.getHeight();

				// check to see if we're writing way far below the clipping,
				// leaving lots of room of non-logical-bounds overflow
				if (finished && y > bottom + getFontSize())
					break;
			}
		} finally {
			g2.dispose();
		}

	}

	class Glyph {
		WritingShape shape;
		float width;
		float ascent;
		float totalHeight;
		char ch;
		boolean isWhitespace;

		Glyph(char ch) {
			this.ch = ch;
			isWhitespace = Character.isWhitespace(ch);
			WritingFont font = properties.get(FONT);
			shape = font.getGlyph(ch);
			if (shape == null) {
				Character z = font.getProperty(WritingFont.MISSING_GLYPH_CHAR);
				if (z != null) {
					shape = font.getGlyph(z);
				}
			}
			if (shape == null) {
				shape = createEmptyGlyph(.5f);
			}

			totalHeight = getFontSize();
			ascent = totalHeight
					/ (1f + getFont().getProperty(WritingFont.DESCENT) + getFont()
							.getProperty(WritingFont.LEADING));

			Rectangle2D gb = shape.getBounds();
			width = (float) (ascent * gb.getWidth() / gb.getHeight());

		}

		protected WritingShape createEmptyGlyph(float widthToHeighRatio) {
			if (widthToHeighRatio <= 0)
				throw new IllegalArgumentException();
			WritingShape ws = new WritingShape();
			ws.setBounds(new Rectangle2D.Float(0, 0, widthToHeighRatio, 1));
			ws.getStrokes().add(new WritingStroke(.1f, null));
			return ws;
		}

		@Override
		public String toString() {
			return "'" + ch + "'";
		}
	}

	class Line {
		List<Glyph> glyphs = new ArrayList<Glyph>();
		float width = 0;
		Integer height = null;

		float append(Glyph g) {
			glyphs.add(g);
			width += g.width;
			return width;
		}

		int getHeight() {
			if (height == null) {
				int h = 0;
				for (Glyph g : glyphs) {
					h = Math.max(h, (int) Math.ceil(g.totalHeight));
				}
				height = h;
			}
			return height;
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			for (int a = 0; a < glyphs.size(); a++) {
				sb.append(glyphs.get(a).ch);
			}
			return sb.toString();
		}

		Glyph popGlyph() {
			height = null;
			Glyph g = glyphs.remove(glyphs.size() - 1);
			width -= g.width;

			// be 100% sure there's no machine error:
			if (glyphs.size() == 0)
				width = 0;

			return g;
		}

		/**
		 * 
		 * @param g
		 * @param leftX
		 *            the x-coordinate of the top-left corner of this line's
		 *            bounds.
		 * @param topY
		 *            the y-coordinate of the top-left corner of this line's
		 *            bounds.
		 * @param time
		 * @return true if this line was painted entirely, false otherwise
		 */
		boolean paint(Graphics2D g, int leftX, int topY, float time) {
			if (time < 0)
				time = Float.MAX_VALUE;
			Graphics2D g2 = (Graphics2D) g.create();
			try {
				g2.translate(leftX, topY);
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
						RenderingHints.VALUE_STROKE_PURE);
				float beatsPerSecond = getBeatsPerSecond();
				float pixelsPerSecond = getPixelsPerSecond();
				float x = 0;
				for (int a = 0; a < glyphs.size(); a++) {
					Rectangle2D r = new Rectangle2D.Double(x, 0,
							glyphs.get(a).width, glyphs.get(a).ascent);
					boolean finished = glyphs.get(a).shape.paint(g2, r, time,
							beatsPerSecond, pixelsPerSecond);
					if (finished) {
						time -= glyphs.get(a).shape.getDuration(beatsPerSecond,
								pixelsPerSecond);
					} else {
						return false;
					}
					x += glyphs.get(a).width;
				}
				return true;
			} finally {
				g2.dispose();
			}
		}
	}

	protected Glyph[] getGlyphs() {
		Glyph[] glyphs = (Glyph[]) properties.get(
				ObservableProperties.TRANSIENT, GLYPHS_KEY);
		if (glyphs == null) {
			String text = getText();
			glyphs = new Glyph[text.length()];
			for (int a = 0; a < text.length(); a++) {
				char ch = text.charAt(a);
				glyphs[a] = new Glyph(ch);
			}
			properties.set(ObservableProperties.TRANSIENT, GLYPHS_KEY, glyphs);
		}
		return glyphs;
	}

	protected Line[] getLines(int width) {
		Glyph[] glyphs = getGlyphs();
		List<Line> lines = new ArrayList<Line>();
		int gCtr = 0;
		boolean encounteredWhitespace = false;
		Line currentLine = null;
		while (gCtr < glyphs.length) {
			if (currentLine == null) {
				currentLine = new Line();
				lines.add(currentLine);
				encounteredWhitespace = false;
			}
			Glyph g = glyphs[gCtr];
			float newWidth = currentLine.append(g);
			if (newWidth > width) {
				if (encounteredWhitespace) {
					while (!currentLine.popGlyph().isWhitespace) {
						gCtr--;
					}
				} else {
					// sorry, it's going to wrap funky:
					currentLine.popGlyph();
					gCtr--;
				}
				currentLine = null;
			} else {
				if (g.isWhitespace)
					encounteredWhitespace = true;
			}

			gCtr++;
		}
		return lines.toArray(new Line[lines.size()]);
	}

	public int getHeight(int width) {
		Line[] lines = getLines(width);
		int height = 0;
		for (Line line : lines) {
			height += line.getHeight();
		}
		return height;
	}

	/**
	 * Return the duration (in seconds) of this layout, based on the current
	 * beats per second and pixels per second.
	 */
	public float getDuration() {
		Glyph[] glyphs = getGlyphs();
		float sum = 0;
		for (int a = 0; a < glyphs.length; a++) {
			sum += glyphs[a].shape.getDuration(getBeatsPerSecond(),
					getPixelsPerSecond());
		}
		return sum;
	}

	class WTLAnimationReader implements AnimationReader, Resettable {
		final int width, height, fps;
		final boolean loop, transparentBackground;
		int frame = 0;

		WTLAnimationReader(int w, boolean transparentBackground, int fps,
				boolean loop) {
			if (fps <= 0)
				throw new IllegalArgumentException(fps
						+ " must be a positive number");
			if (w <= 0)
				throw new IllegalArgumentException(w
						+ " must be a positive number");

			this.transparentBackground = transparentBackground;
			this.width = w;
			this.fps = fps;
			this.loop = loop;
			this.height = WritingTextLayout.this.getHeight(w);
		}

		transient BufferedImage bi;

		@Override
		public BufferedImage getNextFrame(boolean cloneImage)
				throws IOException {
			if (frame == getFrameCount())
				return null;

			/**
			 * This option paints the animation against a solid background, and
			 * then makes the remaining pixels transparent.
			 * 
			 */
			Color backgroundToPaintAgainst = null;

			Graphics2D g;
			if (backgroundToPaintAgainst != null) {
				if (bi == null || cloneImage) {
					bi = new BufferedImage(getWidth(), getHeight(),
							BufferedImage.TYPE_INT_ARGB);
				}
				g = bi.createGraphics();
				g.setColor(backgroundToPaintAgainst);
				g.fillRect(0, 0, width, height);
			} else if (transparentBackground) {
				if (bi == null || cloneImage) {
					bi = new BufferedImage(getWidth(), getHeight(),
							BufferedImage.TYPE_INT_ARGB);
				}
				g = bi.createGraphics();
				g.setComposite(AlphaComposite.Clear);
				g.fillRect(0, 0, width, height);
				g.setComposite(AlphaComposite.SrcOver);
			} else {
				if (bi == null || cloneImage) {
					bi = new BufferedImage(getWidth(), getHeight(),
							BufferedImage.TYPE_INT_RGB);
				}
				g = bi.createGraphics();
				g.setColor(Color.white);
				g.fillRect(0, 0, width, height);
			}
			g.setColor(Color.black);
			float time = ((float) frame) / ((float) fps);

			paint(g, new Rectangle(0, 0, width, height), time, Color.black);
			g.dispose();

			frame++;

			if (backgroundToPaintAgainst != null) {
				// TODO: speed this up, don't do one pixel at a time
				int px = backgroundToPaintAgainst.getRGB() & 0xffffff;
				for (int y = 0; y < bi.getHeight(); y++) {
					for (int x = 0; x < bi.getWidth(); x++) {
						int argb = bi.getRGB(x, y);
						if (argb == -1) {
							bi.setRGB(x, y, px);
						}
					}
				}
			}
			return bi;
		}

		@Override
		public double getDuration() {
			return WritingTextLayout.this.getDuration();
		}

		@Override
		public int getFrameCount() {
			return (int) Math.ceil(getDuration() * fps);
		}

		@Override
		public int getLoopCount() {
			if (loop) {
				return AnimationReader.LOOP_FOREVER;
			}
			return 1;
		}

		@Override
		public double getFrameDuration() {
			return 1.0 / ((double) fps);
		}

		@Override
		public int getWidth() {
			return width;
		}

		@Override
		public int getHeight() {
			return height;
		}

		@Override
		public void reset() {
			frame = 0;
		}
	}

	/**
	 * This reader may rely on this WritingTextLayout, so if the layout is
	 * changed then the reader may become unreliable.
	 */
	public AnimationReader createAnimation(int width,
			boolean transparentBackground, int fps, boolean loop) {
		return new WTLAnimationReader(width, transparentBackground, 24, loop);

	}
}