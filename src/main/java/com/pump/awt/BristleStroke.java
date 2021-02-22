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
package com.pump.awt;

import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.Serializable;
import java.util.Random;

import com.pump.geom.MeasuredShape;

/**
 * This <code>Stroke</code> that resembles a bristle by splattering tiny
 * triangles and dots over a path.
 * 
 * @see <a href=
 *      "https://javagraphics.blogspot.com/2008/05/strokes-bristle-stroke.html">Strokes:
 *      a Bristle Stroke</a>
 */
public class BristleStroke implements Stroke, Serializable {
	private static final long serialVersionUID = 1L;

	private float width;
	private float thickness;
	private long randomSeed;

	/**
	 * Creates a new BristleStroke.
	 * <P>
	 * This constructor always uses a random seed of zero.
	 * 
	 * @param width
	 *            the width (in pixels) of this stroke.
	 * @param thickness
	 *            a float between zero and one indicating how "thick" this
	 *            stroke should be. (1 = "very thick", and 0 = "very thin")
	 */
	public BristleStroke(float width, float thickness) {
		this(width, thickness, 0);
	}

	/**
	 * Creates a new BristleStroke.
	 * 
	 * @param width
	 *            the width (in pixels) of this stroke.
	 * @param thickness
	 *            a float between zero and one indicating how "thick" this
	 *            stroke should be. (1 = "very thick", and 0 = "very thin")
	 * @param randomSeed
	 *            the random seed for this stroke.
	 */
	public BristleStroke(float width, float thickness, long randomSeed) {
		if (width <= 0)
			throw new IllegalArgumentException(
					"the width (" + width + ") must be positive");
		if (thickness < 0)
			throw new IllegalArgumentException(
					"the thickness (" + thickness + ") must be greater than 0");
		this.width = width;
		this.thickness = thickness;
		this.randomSeed = randomSeed;
	}

	private static float getGrain(float width, float thickness) {
		double k = width;
		if (width > 1) {
			k = Math.pow(width, .5f);
			if (k > 4)
				k = 4;
			return (float) (k * (.75 + .25 * thickness));
		} else {
			return Math.max(width, .1f);
		}
	}

	/**
	 * @return the random seed used in this stroke
	 */
	public long getRandomSeed() {
		return randomSeed;
	}

	/**
	 * @return the thickness of this stroke (a float between zero and one).
	 */
	public float getThickness() {
		return thickness;
	}

	/**
	 * @return the width (in pixels) of this stroke.
	 */
	public float getWidth() {
		return width;
	}

	@Override
	public Shape createStrokedShape(Shape p) {
		GeneralPath path = new GeneralPath();
		Random r = new Random(randomSeed);

		MeasuredShape[] paths = MeasuredShape.getSubpaths(p);

		float grain = getGrain(width, thickness);
		float spacing = .5f + .5f * thickness;
		int layers = 20;

		for (int a = 0; a < layers; a++) {
			float k1 = ((float) a) / ((float) (layers - 1));
			float k2 = (k1 - .5f) * 2; // range from [-1,1]

			float k3 = thickness;
			float minGapDistance = (4 + 10 * k3) / (1 + 9 * spacing);
			float maxGapDistance = (40 + 10 * k3) / (1 + 9 * spacing);

			Point2D p2 = new Point2D.Float();
			float x, y;

			for (int b = 0; b < paths.length; b++) {
				r.setSeed(randomSeed + 1000 * a + 10000 * b);

				float d = r.nextFloat() * (maxGapDistance - minGapDistance)
						+ minGapDistance * (1
								+ 20 * (1 - thickness) * Math.abs(k2 * k2));
				while (d < paths[b].getOriginalDistance()) {
					float gapDistance = r.nextFloat()
							* (maxGapDistance - minGapDistance)
							+ minGapDistance * (1
									+ 20 * (1 - thickness) * Math.abs(k2 * k2));

					paths[b].getPoint(d, p2);
					float angle = paths[b].getTangentSlope(d);
					float dx = (float) (k2 * width
							* Math.cos(angle + Math.PI / 2) / 2.0);
					float dy = (float) (k2 * width
							* Math.sin(angle + Math.PI / 2) / 2.0);

					p2.setLocation(p2.getX() + dx, p2.getY() + dy);

					x = (float) p2.getX();
					y = (float) p2.getY();

					float rotation = r.nextFloat() * 2 * 3.145f;

					boolean isTriangle = r.nextBoolean();

					if (isTriangle) {
						path.moveTo(
								(float) (x + grain / 2.0
										* Math.cos(rotation + 2 * Math.PI / 3)),
								(float) (y + grain / 2.0 * Math
										.sin(rotation + 2 * Math.PI / 3)));
						path.lineTo(
								(float) (x + grain / 2.0
										* Math.cos(rotation + 4 * Math.PI / 3)),
								(float) (y + grain / 2.0 * Math
										.sin(rotation + 4 * Math.PI / 3)));
						path.lineTo(
								(float) (x + grain / 2.0 * Math.cos(rotation)),
								(float) (y + grain / 2.0 * Math.sin(rotation)));
						path.closePath();
					} else {
						path.moveTo(
								(float) (x + grain / 2.0
										* Math.cos(rotation + 2 * Math.PI / 4)),
								(float) (y + grain / 2.0 * Math
										.sin(rotation + 2 * Math.PI / 4)));
						path.lineTo(
								(float) (x + grain / 2.0
										* Math.cos(rotation + 4 * Math.PI / 4)),
								(float) (y + grain / 2.0 * Math
										.sin(rotation + 4 * Math.PI / 4)));
						path.lineTo(
								(float) (x + grain / 2.0
										* Math.cos(rotation + 6 * Math.PI / 4)),
								(float) (y + grain / 2.0 * Math
										.sin(rotation + 6 * Math.PI / 4)));
						path.lineTo(
								(float) (x + grain / 2.0 * Math.cos(rotation)),
								(float) (y + grain / 2.0 * Math.sin(rotation)));
						path.closePath();
					}

					d = d + gapDistance;
				}
			}
		}
		return path;
	}

	private void writeObject(java.io.ObjectOutputStream out)
			throws IOException {
		out.writeInt(1);
		out.writeFloat(width);
		out.writeFloat(thickness);
		out.writeLong(randomSeed);
	}

	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		int internalVersion = in.readInt();
		if (internalVersion == 0) {
			width = in.readFloat();
			thickness = in.readFloat();

			// we used to embed layers:
			in.readInt();

			randomSeed = in.readLong();

			// we used to embed grain/spacing:
			in.readFloat();
			in.readFloat();
		} else if (internalVersion == 1) {
			width = in.readFloat();
			thickness = in.readFloat();
			randomSeed = in.readLong();
		} else {
			throw new IOException(
					"Unsupported internal version: " + internalVersion);
		}
	}

	@Override
	public int hashCode() {
		return Float.hashCode(width + thickness);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BristleStroke))
			return false;
		BristleStroke bs = (BristleStroke) obj;
		if (bs.getWidth() != getWidth())
			return false;
		if (bs.getRandomSeed() != getRandomSeed())
			return false;
		if (bs.getThickness() != getThickness())
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BristleStroke[ width=" + width + ", thickness=" + thickness
				+ ", randomSeed=" + randomSeed + "]";
	}
}