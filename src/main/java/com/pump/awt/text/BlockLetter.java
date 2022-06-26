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
package com.pump.awt.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.pump.awt.CalligraphyPathWriter;
import com.pump.geom.GeneralPathWriter;
import com.pump.geom.ShapeBounds;
import com.pump.geom.TransformUtils;

/**
 * This is a basic 3D representation of a letter. It has a fixed depth, so the
 * dimensions of this effect can be calculated as: <br>
 * double dx = getDepth()*Math.cos(angle); <br>
 * double dy = getDepth()*Math.sin(angle);
 */
public abstract class BlockLetter {

	/**
	 * This BlockLetter has no gradients; the depth of the letter is one uniform
	 * (unnuanced) Paint:
	 * <p>
	 * <img src=
	 * "https://github.com/mickleness/pumpernickel/raw/master/resources/simple-block-text.png"
	 * alt="simple block text">
	 * <p>
	 * This will be slightly faster than the <code>Gradient</code>
	 * implementation, so if you are performing animation for the user this
	 * might be the best choice.
	 * 
	 */
	public static class Simple extends BlockLetter {

		Paint block1Paint = Color.darkGray;
		Paint block2Paint = Color.darkGray;

		public Simple(char c, Font font, Paint foreground) {
			super(c, font, foreground);
		}

		/**
		 * Return the 2 shapes required to render the depth of this BlockLetter.
		 * The first shape should correspond to <code>block1Paint</code> and the
		 * second shape should correspond to <code>block2Paint</code>.
		 * 
		 * @return the two shapes required to render the depth of this
		 *         BlockLetter.
		 */
		protected Shape[] getDepthBlocks() {
			GeneralPath block1 = new GeneralPath();
			GeneralPathWriter dest1 = new GeneralPathWriter(block1);
			GeneralPath block2 = new GeneralPath();
			GeneralPathWriter dest2 = new GeneralPathWriter(block2);
			CalligraphyPathWriter cpw = new CalligraphyPathWriter(angle, 0,
					depth, dest1, dest2);
			cpw.write(outline);
			return new Shape[] { block1, block2 };
		}

		@Override
		public void paintDepth(Graphics2D g, float x, float y) {
			Graphics2D g2 = prep(g, x, y);

			Shape[] blocks = getDepthBlocks();

			g2.setPaint(block1Paint);
			g2.fill(blocks[0]);
			g2.setPaint(block2Paint);
			g2.fill(blocks[1]);

			g2.dispose();
		}

		public void setBlockPaint(Paint p) {
			block1Paint = p;
			block2Paint = p;
		}

		public void setBlockPaint(Paint p1, Paint p2) {
			block1Paint = p1;
			block2Paint = p2;
		}
	}

	/**
	 * This BlockLetter supports two different types of gradients. This renders
	 * the gradient as a series of lines (or polygons) whose fill is a 2-color
	 * gradient that can be a function of the angle of the tangent curve of the
	 * letter.
	 * <p>
	 * Here is a sample:
	 * <p>
	 * <img src=
	 * "https://github.com/mickleness/pumpernickel/raw/master/resources/gradient-block-text.png"
	 * alt="gradient block text demo">
	 */
	public static class Gradient extends BlockLetter {

		class SheetComparator implements Comparator<Sheet> {

			public int compare(Sheet o1, Sheet o2) {
				double midX1 = (o1.x1 + o1.x2) / 2;
				double midY1 = (o1.y1 + o1.y2) / 2;
				double midX2 = (o2.x1 + o2.x2) / 2;
				double midY2 = (o2.y1 + o2.y2) / 2;
				if (angle < Math.PI / 2) {
					// lower-right
					if (midX2 >= midX1 && midY2 >= midY1) {
						return -1;
					} else if (midX1 >= midX2 && midY1 >= midY2) {
						return 1;
					}
				} else if (angle < Math.PI) {
					// lower-left
					if (midX2 <= midX1 && midY2 >= midY1) {
						return -1;
					} else if (midX1 <= midX2 && midY1 >= midY2) {
						return 1;
					}
				} else if (angle < 3 * Math.PI / 2) {
					// upper-left
					if (midX2 >= midX1 && midY2 >= midY1) {
						return 1;
					} else if (midX1 >= midX2 && midY1 >= midY2) {
						return -1;
					}
				} else {
					// upper-right
					if (midX2 >= midX1 && midY2 <= midY1) {
						return -1;
					} else if (midX1 >= midX2 && midY1 <= midY2) {
						return 1;
					}
				}

				int t = compare(o1.x1, o2.x1);
				if (t == 0)
					t = compare(o1.x2, o2.x2);
				if (t == 0)
					t = compare(o1.y1, o2.y1);
				if (t == 0)
					t = compare(o1.y2, o2.y2);
				if (t == 0)
					t = compare(o1.theta, o2.theta);
				return t;
			}

			private int compare(double d1, double d2) {
				if (d1 < d2)
					return -1;
				if (d1 > d2)
					return 1;
				return 0;
			}

		}

		static class Sheet {
			double x1, y1, x2, y2, theta;

			public Sheet(float x1, float y1, float x2, float y2) {
				theta = Math.atan2(y2 - y1, x2 - x1);
				if (theta < 0)
					theta = theta + 2 * Math.PI;
				double d = Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1)
						* (x2 - x1));

				this.x2 = x1 + (d + .5f) * Math.cos(theta);
				this.y2 = y1 + (d + .5f) * Math.sin(theta);
				this.x1 = x2 - (d + .5f) * Math.cos(theta);
				this.y1 = y2 - (d + .5f) * Math.sin(theta);
			}

			public void apply(GeneralPath body, BlockLetter gbl) {
				body.reset();
				int dx = (int) (gbl.depth * Math.cos(gbl.angle));
				int dy = (int) (gbl.depth * Math.sin(gbl.angle));
				body.moveTo(x1, y1);
				body.lineTo(x2, y2);
				body.lineTo(x2 + dx, y2 + dy);
				body.lineTo(x1 + dx, y1 + dy);
				body.closePath();
			}
		}

		public Gradient(char c, Font font, Paint foreground) {
			super(c, font, foreground);
		}

		@Override
		public void paintDepth(Graphics2D g, float x, float y) {
			Graphics2D g2 = prep(g, x, y);

			Set<Sheet> sheets = new TreeSet<Sheet>(new SheetComparator());

			PathIterator iter = outline.getPathIterator(null, .1);
			float[] coords = new float[6];
			float lastX = 0;
			float lastY = 0;
			float moveX = 0;
			float moveY = 0;
			while (!iter.isDone()) {
				int k = iter.currentSegment(coords);
				if (k == PathIterator.SEG_CLOSE) {
					k = PathIterator.SEG_LINETO;
					coords[0] = moveX;
					coords[1] = moveY;
				}
				if (k == PathIterator.SEG_MOVETO) {
					lastX = coords[0];
					lastY = coords[1];
					moveX = lastX;
					moveY = lastY;
				} else if (k == PathIterator.SEG_LINETO) {
					sheets.add(new Sheet(lastX, lastY, coords[0], coords[1]));
					lastX = coords[0];
					lastY = coords[1];
				}
				iter.next();
			}

			GeneralPath body = new GeneralPath();
			for (Sheet sh : sheets) {
				sh.apply(body, this);
				Color[] colors = getGradientColors(sh.theta);

				if (colors.length == 1) {
					g2.setColor(colors[0]);
					g2.fill(body);
				} else {
					final AffineTransform tx = TransformUtils
							.createAffineTransform(0, 0, 1, 0, 0, 1, sh.x1,
									sh.y1, sh.x1 + Math.cos(angle) * depth,
									sh.y1 + Math.sin(angle) * depth, sh.x2,
									sh.y2);
					GradientPaint paint = new GradientPaint(0, 0, colors[0], 1,
							0, colors[1]) {

						@Override
						public PaintContext createContext(ColorModel cm,
								Rectangle deviceBounds, Rectangle2D userBounds,
								AffineTransform xform, RenderingHints hints) {
							AffineTransform t = new AffineTransform();
							t.concatenate(xform);
							t.concatenate(tx);
							return super.createContext(cm, deviceBounds,
									userBounds, t, hints);
						}
					};
					g2.setPaint(paint);

					g2.fill(body);
				}
			}

			g2.dispose();
		}

		/**
		 * Return the colors used to color this sheet.
		 * <p>
		 * These colors might be a function of theta.
		 * <p>
		 * This may return 1 color, in which case the sheet will be a solid
		 * color. Or this might return 2 colors, in which case the first color
		 * will be the color touching the character glyph and the second color
		 * will be the color furthest away.
		 * 
		 * @param theta
		 *            the angle (in radians) tangent to the glyph shape.
		 * @return the colors used to color this sheet.
		 */
		protected Color[] getGradientColors(double theta) {
			int gray = (int) (theta * 255 / (2 * Math.PI));
			return new Color[] { new Color(gray, gray, gray), Color.black };
		}

	}

	static final FontRenderContext frc = new FontRenderContext(
			new AffineTransform(), true, true);
	protected final char c;
	protected final Font font;
	protected Shape outline;
	protected Paint foreground;

	protected float angle;
	protected float depth;
	protected float charWidth = -1;
	protected float charHeight = -1;
	protected Map<String, Object> table = null;

	public BlockLetter(char c, Font font, Paint foreground) {
		this.c = c;
		this.font = font;
		GlyphVector gv = font.createGlyphVector(frc, new char[] { c });
		outline = gv.getOutline(0, 0);
		setAngle((float) (Math.PI / 4));
		setForeground(foreground);
	}

	/**
	 * Return the total depth of this BlockLetter. This is the hypotenuse of
	 * displacement; to identify the x and y displacement use: <br>
	 * double dx = getDepth()*Math.cos(angle); <br>
	 * double dy = getDepth()*Math.sin(angle);
	 * <p>
	 * If this is zero, then this BlockLetter should simply render as the
	 * original character glyph.
	 * 
	 * @return the total depth of this BlockLetter.
	 */
	public float getDepth() {
		if (charWidth == -1) {
			getCharWidth();
		}
		return charHeight;
	}

	/** @return the width of this character. */
	public float getCharWidth() {
		if (charWidth == -1) {
			Rectangle2D r = ShapeBounds.getBounds(outline);
			charWidth = (float) r.getWidth();
			charHeight = (float) r.getHeight();
		}
		return charWidth;
	}

	public Object get(String key) {
		if (table == null)
			return null;
		return table.get(key);
	}

	public void put(String key, Object value) {
		if (table == null)
			table = new HashMap<String, Object>();
		table.put(key, value);
	}

	/**
	 * Assign the depth of this BlockLetter.
	 * 
	 * @param depth
	 *            the new depth.
	 * 
	 * @see #getDepth()
	 */
	public void setDepth(float depth) {
		this.depth = depth;
	}

	public void setForeground(Paint p) {
		foreground = p;
	}

	/**
	 * Return the angle of this BlockLetter.
	 * 
	 * @param angle
	 *            the new angle.
	 * 
	 * @see #getAngle()
	 */
	public void setAngle(float angle) {
		this.angle = angle;
	}

	public float getAngle() {
		return angle;
	}

	/**
	 * Calls to <code>paintDepth(g, x, y)</code> and
	 * <code>paintForeground(g, x, y)</code>.
	 * 
	 * @param g
	 *            the graphics to draw to
	 * @param x
	 *            the x-coordinate indicating where to paint.
	 * @param y
	 *            the y-coordinate indicating where to paint.
	 */
	public void paint(Graphics2D g, float x, float y) {
		paintDepth(g, x, y);
		paintForeground(g, x, y);
	}

	/**
	 * Paint the depth ("the 3D part") of this BlockLetter.
	 * 
	 * @param g
	 *            the graphics to draw to
	 * @param x
	 *            the x-coordinate indicating where to paint.
	 * @param y
	 *            the y-coordinate indicating where to paint.
	 */
	public abstract void paintDepth(Graphics2D g, float x, float y);

	/**
	 * Paint the foreground of this BlockLetter.
	 * <P>
	 * If the method <code>paintDepth()</code> isn't invoked, then this method
	 * will still present normal-looking (non-3D) text.
	 * 
	 * @param g
	 *            the graphics to draw to
	 * @param x
	 *            the x-coordinate indicating where to paint.
	 * @param y
	 *            the y-coordinate indicating where to paint.
	 */
	public void paintForeground(Graphics2D g, float x, float y) {
		Graphics2D g2 = prep(g, x, y);
		g2.setPaint(foreground);
		g2.fill(outline);
		g2.dispose();
	}

	/**
	 * Prepares this Graphics2D to paint this BlockLetter at the given (x,y)
	 * coordinate.
	 * 
	 * @param g
	 *            the Graphics2D to paint to
	 * @param x
	 *            the x-coordinate to paint to
	 * @param y
	 *            the y-coordinate to paint to
	 * @return a cloned Graphics2D to paint to. When complete you should dispose
	 *         this Graphics2D.
	 */
	protected Graphics2D prep(Graphics2D g, float x, float y) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.translate(x, y);

		float dx = (float) (depth * Math.cos(angle));
		float dy = (float) (depth * Math.sin(angle));
		g2.translate(-dx, -dy);
		return g2;
	}
}