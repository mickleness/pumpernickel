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

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.io.IOException;
import java.io.Serializable;
import java.util.Random;

import com.pump.geom.InsetPathWriter;
import com.pump.geom.MeasuredShape;
import com.pump.geom.Path2DWriter;

/**
 * * This <code>Stroke</code> that resembles a brush by rendering several
 * smaller strokes in parallel along a path.
 * 
 * @see <a href=
 *      "https://javagraphics.blogspot.com/2007/04/strokes-brush-stroke.html">Strokes:
 *      a Brush Stroke</a>
 */
public class BrushStroke implements Stroke, Serializable {
	private static final long serialVersionUID = 1L;

	private static BasicStroke[] thinStrokes = null;

	float width;
	float theta;
	float thickness = 0;
	long randomSeed = 0;

	/**
	 * Creates a new BrushStroke using the default angle (pi/2).
	 * 
	 * @param width
	 *            the width (in pixels) of this stroke
	 * @param thickness
	 *            a float between zero and one indicating how "thick" this
	 *            stroke should be.
	 * @param randomSeed
	 *            the random seed for this stroke.
	 */
	public BrushStroke(float width, float thickness, long randomSeed) {
		this(width, thickness, (float) (Math.PI / 2.0), randomSeed);
	}

	/**
	 * Creates a new BrushStroke using the default angle (pi/2) and a random
	 * seed of zero.
	 * 
	 * @param width
	 *            the width (in pixels) of this stroke
	 * @param thickness
	 *            a float between zero and one indicating how "thick" this
	 *            stroke should be.
	 */
	public BrushStroke(float width, float thickness) {
		this(width, thickness, (float) (Math.PI / 2.0), 0);
	}

	/**
	 * Creates a new BrushStroke.
	 * 
	 * @param width
	 *            the width (in pixels) of this stroke
	 * @param thickness
	 *            a float between zero and one indicating how "thick" this
	 *            stroke should be.
	 * @param theta
	 *            the angle stroke is drawn at to the original path. An angle of
	 *            (pi/2) is considered "normal". A value of 0 or pi is very
	 *            extreme.
	 * @param randomSeed
	 *            the random seed for this stroke.
	 */
	public BrushStroke(float width, float thickness, float theta,
			long randomSeed) {
		if (width <= 0)
			throw new IllegalArgumentException(
					"the width (" + width + ") must be positive");
		if (thickness < 0 || thickness > 1)
			throw new IllegalArgumentException("the thickness (" + thickness
					+ ") must be between 0 and 1");
		this.width = width;
		this.thickness = thickness;
		this.randomSeed = randomSeed;
		this.theta = theta;
	}

	public long getRandomSeed() {
		return randomSeed;
	}

	public float getThickness() {
		return thickness;
	}

	public float getWidth() {
		return width;
	}

	public float getTheta() {
		return theta;
	}

	@Override
	public Shape createStrokedShape(Shape p) {
		if (thinStrokes == null) {
			thinStrokes = new BasicStroke[100];
			for (int a = 0; a < thinStrokes.length; a++) {
				float f = .15f + (2.05f - .15f) * (a) / (thinStrokes.length);
				thinStrokes[a] = new BasicStroke(f, BasicStroke.CAP_BUTT,
						BasicStroke.JOIN_BEVEL, 10);
			}
		}

		GeneralPath path = new GeneralPath();

		Random r = new Random(randomSeed);

		float h = thickness * thickness;
		int thicknessMax = Math.min(thinStrokes.length,
				(int) (thinStrokes.length * h + thinStrokes.length * .2f));
		int thicknessMin = (int) (thinStrokes.length * h / 2f);

		GeneralPath thisLayer = new GeneralPath(Path2D.WIND_NON_ZERO);
		Path2DWriter writer = new Path2DWriter(thisLayer);
		int layers = (int) (2 * width) + 2;
		for (int a = 0; a < layers; a++) {
			writer.reset();
			float k1 = a * width / (layers - 1f);
			float k2 = k1 - width / 2;
			InsetPathWriter insetWriter;
			if (k2 > 0) {
				insetWriter = new InsetPathWriter(writer, Math.abs(k2), theta);
			} else {
				insetWriter = new InsetPathWriter(writer, Math.abs(k2),
						(float) (Math.PI + theta));
			}
			insetWriter.write(p);
			MeasuredShape[] measuredLayers = MeasuredShape
					.getSubpaths(thisLayer);

			float minStreakDistance = (4 + 10 * thickness) / 1f;
			float maxStreakDistance = (40 + 10 * thickness) / 1f;
			float k3 = Math.abs(k2);
			float minGapDistance = (4 + 10 * k3) / 1f;
			float maxGapDistance = (40 + 10 * k3) / 1f;

			for (int b = 0; b < measuredLayers.length; b++) {
				r.setSeed(randomSeed + 1000 * a + 10000 * b);

				float x = 0;
				if (a != layers / 2) {
					float k4 = Math.abs(k2 / width);
					x = (maxGapDistance - minGapDistance) * r.nextFloat()
							+ k4 * (.3f * r.nextFloat() + .7f) * minGapDistance;
				}

				boolean first = true;
				while (x < measuredLayers[b].getOriginalDistance()) {
					float streakDistance = minStreakDistance
							+ (maxStreakDistance - minStreakDistance)
									* r.nextFloat();
					float gapDistance;
					if (first) {
						first = false;
						gapDistance = (.2f + .8f * r.nextFloat())
								* minGapDistance
								+ (maxGapDistance - minGapDistance)
										* r.nextFloat();
					} else {
						gapDistance = minGapDistance
								+ (maxGapDistance - minGapDistance)
										* r.nextFloat();
					}

					if (x + streakDistance > measuredLayers[b]
							.getOriginalDistance()) {
						float z = 0;
						if (a != layers / 2)
							z = (maxGapDistance - minGapDistance)
									* r.nextFloat();
						streakDistance = measuredLayers[b].getOriginalDistance()
								- x - z;
					}
					if (streakDistance > 0) {
						Path2D p2 = measuredLayers[b].getShape(
								x / measuredLayers[b].getClosedDistance(),
								streakDistance / measuredLayers[b]
										.getClosedDistance());
						path.append(
								thinStrokes[r
										.nextInt(thicknessMax - thicknessMin)
										+ thicknessMin].createStrokedShape(p2),
								false);
					}

					x = x + (streakDistance + gapDistance);
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
		out.writeFloat(theta);

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
			theta = in.readFloat();
		} else if (internalVersion == 1) {
			width = in.readFloat();
			thickness = in.readFloat();
			randomSeed = in.readLong();
			theta = in.readFloat();
		} else {
			throw new IOException(
					"Unsupported internal version: " + internalVersion);
		}
	}

	@Override
	public int hashCode() {
		return Float.hashCode(width + theta + thickness);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BrushStroke))
			return false;
		BrushStroke bs = (BrushStroke) obj;
		if (bs.getWidth() != getWidth())
			return false;
		if (bs.getTheta() != getTheta())
			return false;
		if (bs.getThickness() != getThickness())
			return false;
		if (bs.getRandomSeed() != getRandomSeed())
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BrushStroke[ width=" + width + ", thickness=" + thickness
				+ ", theta=" + theta + ", randomSeed=" + randomSeed + "]";
	}

}