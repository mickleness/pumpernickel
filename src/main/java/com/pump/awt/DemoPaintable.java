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
package com.pump.awt;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import com.pump.blog.ResourceSample;
import com.pump.geom.ShapeBounds;

/**
 * A simple paintable object to test with.
 * 
 *
 * 
 * <!-- ======== START OF AUTOGENERATED SAMPLES ======== -->
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/samples/DemoPaintable/sample.png"
 * alt="new&#160;com.pump.awt.DemoPaintable(360,&#160;240,&#160;&#34;xyz&#34;)">
 * <!-- ======== END OF AUTOGENERATED SAMPLES ======== -->
 */
@ResourceSample(sample = { "new com.pump.awt.DemoPaintable(360, 240, \"xyz\")" })
public class DemoPaintable implements Paintable {
	final int width, height;
	final String id;

	public DemoPaintable(int width, int height, String id) {
		this.width = width;
		this.height = height;
		this.id = id;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	private static final Color[] colors = new Color[] {
			new Color(220, 220, 220), new Color(200, 200, 200)
	/*
	 * new Color(255,140,140), new Color(255,255,140), new Color(140,255,140),
	 * new Color(140,255,255), new Color(140,140,255), new Color(255,140,255)
	 */
	};

	private static final Font font = new Font("Default", 0, 12);
	private static final FontRenderContext frc = new FontRenderContext(
			new AffineTransform(), true, true);

	public void paint(Graphics2D g) {
		paint(g, width, height, colors, id);
	}

	/**
	 * Paint a demo paintable.
	 * 
	 * @param g
	 *            the Graphics2D to paint to.
	 * @param width
	 *            the width of the painted area
	 * @param height
	 *            the height of the painted area
	 * @param colors
	 *            two colors to paint in a checkerboard pattern on the
	 *            background
	 * @param id
	 *            text to paint in dark gray in the center
	 */
	public static void paint(Graphics2D g, int width, int height,
			Color[] colors, String id) {
		if (colors == null)
			colors = DemoPaintable.colors;

		if (colors.length == 2) {
			g.setColor(colors[0]);
			g.fillRect(0, 0, width / 2, height / 2);
			g.setColor(colors[1]);
			g.fillRect(width / 2, 0, width / 2, height / 2);
			g.setColor(colors[1]);
			g.fillRect(0, height / 2, width / 2, height / 2);
			g.setColor(colors[0]);
			g.fillRect(width / 2, height / 2, width / 2, height / 2);
		} else {
			g.setColor(colors[(id.hashCode() + 0) % colors.length]);
			g.fillRect(0, 0, width / 2, height / 2);
			g.setColor(colors[(id.hashCode() + 1) % colors.length]);
			g.fillRect(width / 2, 0, width / 2, height / 2);
			g.setColor(colors[(id.hashCode() + 2) % colors.length]);
			g.fillRect(0, height / 2, width / 2, height / 2);
			g.setColor(colors[(id.hashCode() + 3) % colors.length]);
			g.fillRect(width / 2, height / 2, width / 2, height / 2);
		}

		// When testing printing, we should allow for
		// double-digit numbers, so let's make sure "MM" can fit
		GlyphVector maxGv = font.createGlyphVector(frc, "MM");
		Shape maxShape = maxGv.getOutline();
		GeneralPath maxPath = new GeneralPath(maxShape);
		Rectangle2D maxR = ShapeBounds.getBounds(maxPath.getPathIterator(null),
				null);

		GlyphVector gv = font.createGlyphVector(frc, id);
		Shape shape = gv.getOutline();
		GeneralPath path = new GeneralPath(shape);
		Rectangle2D r = ShapeBounds.getBounds(path.getPathIterator(null), null);

		maxR = maxR.createUnion(r); // in case we're not testing printing
									// anymore

		AffineTransform magnify = createAffineTransform(maxR,
				new Rectangle2D.Double(0, 0, width, height));
		// don't stretch the text to fill the page:
		// instead stretch it to fill the page proportionally

		double zoom = Math.min(magnify.getScaleX(), magnify.getScaleY());
		zoom = zoom * .8; // add some padding on the sides
		magnify = createAffineTransform(r,
				new Rectangle2D.Double(width / 2 - r.getWidth() / 2 * zoom,
						height / 2 - r.getHeight() / 2 * zoom, r.getWidth()
								* zoom, r.getHeight() * zoom));

		path.transform(magnify);

		g.setColor(Color.darkGray);
		g.fill(path);
	}

	/**
	 * Create an AffineTransform that transforms from r1 to r2.
	 * 
	 * @param r1
	 *            the initial rectangle
	 * @param r2
	 *            the rectangle to transform r1 into.
	 * @return the transform mapping r1 to r2
	 */
	public static AffineTransform createAffineTransform(Rectangle2D r1,
			Rectangle2D r2) {
		AffineTransform transform = new AffineTransform();
		transform.translate(r2.getCenterX(), r2.getCenterY());
		transform.scale(r2.getWidth() / r1.getWidth(),
				r2.getHeight() / r1.getHeight());
		transform.translate(-r1.getCenterX(), -r1.getCenterY());
		return transform;
	}
}