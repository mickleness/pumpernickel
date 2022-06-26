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
package com.pump.plaf.button;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import com.pump.geom.GeneralPathWriter;
import com.pump.geom.MasterPathWriter;
import com.pump.geom.NullPathWriter;
import com.pump.geom.PathWriter;
import com.pump.geom.RectangularTransform;
import com.pump.geom.ShapeBounds;
import com.pump.plaf.PositionConstants;

/** A mechanism to control the shape of buttons with rounded edges. */
public class ButtonShape implements PositionConstants {
	protected final int maxTopRightRadius, maxTopLeftRadius,
			maxBottomLeftRadius, maxBottomRightRadius;
	protected final int prefTopRightRadius, prefTopLeftRadius,
			prefBottomLeftRadius, prefBottomRightRadius;

	/**
	 * Create a new <code>ButtonShape</code>.
	 * 
	 * @param maxRadius
	 *            the max radius for corners. If the corners should always be
	 *            rounded, then this should be a very large number.
	 * @param preferredRadius
	 *            the preferred radius for corners. This may define the
	 *            preferred height of a button.
	 */
	public ButtonShape(int preferredRadius, int maxRadius) {
		this(preferredRadius, preferredRadius, preferredRadius,
				preferredRadius, maxRadius, maxRadius, maxRadius, maxRadius);
	}

	/**
	 * This constructor gives you control over each corner of the button. Most
	 * of the time you can probably use the other (simpler) constructor, unless
	 * you need this level of control.
	 * 
	 */
	public ButtonShape(int prefTopRightRadius, int prefTopLeftRadius,
			int prefBottomLeftRadius, int prefBottomRightRadius,
			int maxTopRightRadius, int maxTopLeftRadius,
			int maxBottomLeftRadius, int maxBottomRightRadius) {
		this.maxTopRightRadius = maxTopRightRadius;
		this.maxTopLeftRadius = maxTopLeftRadius;
		this.maxBottomLeftRadius = maxBottomLeftRadius;
		this.maxBottomRightRadius = maxBottomRightRadius;
		this.prefTopRightRadius = prefTopRightRadius;
		this.prefTopLeftRadius = prefTopLeftRadius;
		this.prefBottomLeftRadius = prefBottomLeftRadius;
		this.prefBottomRightRadius = prefBottomRightRadius;
	}

	/**
	 * Returns the preferred size of a button should have.
	 * 
	 * @param d
	 *            an optional Dimension to store the results in.
	 * @param contentWidth
	 *            the width of the innards of this button.
	 * @param contentHeight
	 *            the height of the innards of this button.
	 * @param padding
	 *            the padding between the contents and the border.
	 * @param customShape
	 *            if non-null, then this shape is scaled to fit the rectangle
	 *            containing the contents and the padding. This is how you apply
	 *            a custom button shape (like a circle, diamond, arrow, etc.)
	 * @return the preferred size of this method.
	 */
	public Dimension getPreferredSize(Dimension d, int contentWidth,
			int contentHeight, Insets padding, Shape customShape) {
		if (d == null)
			d = new Dimension();

		if (customShape == null) {
			int leftSide = Math.max(prefTopLeftRadius, prefBottomLeftRadius);
			int rightSide = Math.max(prefTopRightRadius, prefBottomRightRadius);

			d.width = contentWidth + leftSide + rightSide + padding.left
					+ padding.right;
			d.height = contentHeight + padding.top + padding.bottom;
		} else {
			GeneralPath resizedShape = findShapeToFitRectangle(customShape,
					contentWidth, contentHeight);
			Rectangle2D bounds = ShapeBounds.getBounds(resizedShape);
			d.width = (int) (bounds.getWidth() + padding.left + padding.right + .99999);
			d.height = (int) (bounds.getHeight() + padding.top + padding.bottom + .99999);
		}

		return d;
	}

	private static GeneralPath findShapeToFitRectangle(Shape originalShape,
			int w, int h) {
		GeneralPath newShape = new GeneralPath();
		Rectangle2D rect = new Rectangle2D.Float();
		ShapeBounds.getBounds(originalShape, rect);
		if (originalShape.contains(rect.getX() + rect.getWidth() / 2,
				rect.getY() + rect.getHeight() / 2) == false)
			throw new IllegalArgumentException(
					"This custom shape is not allowed.  The center of this shape must be inside the shape.");
		double scale = Math.min((w) / rect.getWidth(), (h) / rect.getHeight());
		AffineTransform transform = new AffineTransform();
		while (true) {
			newShape.reset();
			newShape.append(originalShape, true);
			transform.setToScale(scale, scale);
			newShape.transform(transform);
			ShapeBounds.getBounds(newShape, rect);

			if (newShape.contains(rect.getX() + rect.getWidth() / 2 - w / 2,
					rect.getY() + rect.getHeight() / 2 - h / 2, w, h)) {
				return newShape;
			}

			scale += .01;
		}
	}

	/**
	 * Calculates the shape of this button, given certain constraints.
	 * 
	 * @param path
	 *            an optional destination for the outline.
	 * @param width
	 *            the width to stretch this fill to.
	 * @param height
	 *            the height to stretch this fill to.
	 */
	public GeneralPath getShape(GeneralPath path, int width, int height) {
		return getShape(path, null, width, height, POS_ONLY, POS_ONLY, true,
				null);
	}

	/**
	 * Calculates the shape of this button, given certain constraints. Note if
	 * this is smaller than the preferred size then, well, the results may look
	 * bad.
	 * 
	 * @param fillPath
	 *            an optional destination for the button shape.
	 * @param strokePath
	 *            an optional destination for the border. Often the border and
	 *            fill will be the same, but in some cases where line segments
	 *            are meant to be missing: they will be unique.
	 * @param width
	 *            the width to stretch this fill to.
	 * @param height
	 *            the height to stretch this fill to.
	 * @param horizontalPosition
	 *            POS_ONLY, POS_LEFT, POS_RIGHT or POS_MIDDLE
	 * @param verticalPosition
	 *            POS_ONLY, POS_TOP, POS_BOTTOM or POS_MIDDLE
	 * @param customShape
	 *            the special shape this button should take. This may be a
	 *            circle, diamond, arrow, etc.
	 * @return the shape of this button, given these constraints.
	 */
	public GeneralPath getShape(GeneralPath fillPath, GeneralPath strokePath,
			int width, int height, int horizontalPosition,
			int verticalPosition, boolean includeStrokePartitions,
			Shape customShape) {

		// if this is the case: we'll still return this object
		if (fillPath == null)
			fillPath = new GeneralPath();

		if (horizontalPosition == POS_RIGHT || horizontalPosition == POS_ONLY) {
			width--;
		}
		if (verticalPosition == POS_BOTTOM || verticalPosition == POS_ONLY) {
			height--;
		}

		fillPath.reset();
		if (strokePath != null)
			strokePath.reset();

		GeneralPathWriter fillWriter = new GeneralPathWriter(fillPath);
		PathWriter strokeWriter = strokePath == null ? new NullPathWriter()
				: new GeneralPathWriter(strokePath);

		// prevents errors with 0x0 buttons:
		fillWriter.setEliminateRedundantLines(false);

		PathWriter master = new MasterPathWriter(fillWriter, strokeWriter);

		// define the actual fill and border of the shape:
		// always fill the entire width/height we are allowed
		// (knowing that the width/height fields here are already
		// reduced to compensate for focus rings).
		// The size of this button is the responsibility of the
		// LayoutManager, and the preferred size is calculated
		// in getPreferredSize(). Here we just work with what
		// we're given.
		if (customShape != null) {
			Rectangle2D originalBounds = new Rectangle2D.Float();
			ShapeBounds.getBounds(customShape, originalBounds);
			Rectangle2D newBounds = new Rectangle2D.Float(0, 0, width, height);
			AffineTransform transform = RectangularTransform.create(
					originalBounds, newBounds);
			master.write(customShape.getPathIterator(transform));
			master.closePath();
		} else {

			int minR = Math.min(height / 2, width / 2);

			int topRightRadius = Math.min(maxTopRightRadius, minR);
			int topLeftRadius = Math.min(maxTopLeftRadius, minR);
			int bottomRightRadius = Math.min(maxBottomRightRadius, minR);
			int bottomLeftRadius = Math.min(maxBottomLeftRadius, minR);

			float k = .22385763f * 2;

			// this is based on a 4x4 grid enumerating all the possible
			// combinations:
			if (verticalPosition == POS_TOP && horizontalPosition == POS_LEFT) {
				master.moveTo(width, 0);
				if (topLeftRadius == 0) {
					master.lineTo(0, 0);
				} else {
					master.lineTo(topLeftRadius, 0);
					master.curveTo(topLeftRadius - topLeftRadius * k, 0, 0,
							topLeftRadius - topLeftRadius * k, 0, topLeftRadius);
				}
				master.lineTo(0, height);
				PathWriter w = includeStrokePartitions ? master : fillWriter;
				w.lineTo(width, height);
				w.lineTo(width, 0);
			} else if (verticalPosition == POS_TOP
					&& horizontalPosition == POS_MIDDLE) {
				master.moveTo(width, 0);
				master.lineTo(0, 0);
				PathWriter w = includeStrokePartitions ? master : fillWriter;
				w.lineTo(0, height);
				w.lineTo(width, height);
				w.lineTo(width, 0);
			} else if (verticalPosition == POS_MIDDLE
					&& horizontalPosition == POS_LEFT) {
				master.moveTo(0, 0);
				master.lineTo(0, height);
				PathWriter w = includeStrokePartitions ? master : fillWriter;
				w.lineTo(width, height);
				w.lineTo(width, 0);
				w.lineTo(0, 0);
			} else if (verticalPosition == POS_MIDDLE
					&& horizontalPosition == POS_MIDDLE) {
				PathWriter w = includeStrokePartitions ? master : fillWriter;
				w.moveTo(width, 0);
				w.lineTo(0, 0);
				w.lineTo(0, height);
				w.lineTo(width, height);
				w.lineTo(width, 0);
			} else if (verticalPosition == POS_TOP
					&& horizontalPosition == POS_RIGHT) {
				master.moveTo(width, height);
				if (topRightRadius == 0) {
					master.lineTo(width, 0);
				} else {
					master.lineTo(width, topRightRadius);
					master.curveTo(width, topRightRadius - topRightRadius * k,
							width - topRightRadius + topRightRadius * k, 0,
							width - topRightRadius, 0);
				}
				master.lineTo(0, 0);

				PathWriter w = includeStrokePartitions ? master : fillWriter;
				w.lineTo(0, height);
				w.lineTo(width, height);
			} else if (verticalPosition == POS_TOP
					&& horizontalPosition == POS_ONLY) {
				master.moveTo(width, height);
				if (topRightRadius == 0) {
					master.lineTo(width, 0);
				} else {
					master.lineTo(width, topRightRadius);
					master.curveTo(width, topRightRadius - topRightRadius * k,
							width - topRightRadius + topRightRadius * k, 0,
							width - topRightRadius, 0);
				}
				if (topLeftRadius == 0) {
					master.lineTo(0, 0);
				} else {
					master.lineTo(topLeftRadius, 0);
					master.curveTo(topLeftRadius - topLeftRadius * k, 0, 0,
							topLeftRadius - topLeftRadius * k, 0, topLeftRadius);
				}
				master.lineTo(0, height);
				PathWriter w = includeStrokePartitions ? master : fillWriter;
				w.lineTo(width, height);
			} else if (verticalPosition == POS_MIDDLE
					&& horizontalPosition == POS_RIGHT) {
				master.moveTo(width, height);
				master.lineTo(width, 0);
				PathWriter w = includeStrokePartitions ? master : fillWriter;
				w.lineTo(0, 0);
				w.lineTo(0, height);
				w.lineTo(width, height);
			} else if (verticalPosition == POS_MIDDLE
					&& horizontalPosition == POS_ONLY) {
				if (includeStrokePartitions) {
					master.moveTo(width, height);
					master.lineTo(width, 0);
					master.lineTo(0, 0);
					master.lineTo(0, height);
					master.lineTo(width, height);
				} else {
					master.moveTo(width, height);
					master.lineTo(width, 0);
					fillWriter.lineTo(0, 0);
					strokeWriter.moveTo(0, 0);
					master.lineTo(0, height);
					fillWriter.lineTo(width, height);
				}
			} else if (verticalPosition == POS_BOTTOM
					&& horizontalPosition == POS_LEFT) {
				master.moveTo(0, 0);
				if (bottomLeftRadius == 0) {
					master.lineTo(0, height);
				} else {
					master.lineTo(0, height - bottomLeftRadius);
					master.curveTo(0, height - bottomLeftRadius
							+ bottomLeftRadius * k, bottomLeftRadius
							- bottomLeftRadius * k, height, bottomLeftRadius,
							height);
				}
				master.lineTo(width, height);
				PathWriter w = includeStrokePartitions ? master : fillWriter;
				w.lineTo(width, 0);
				w.lineTo(0, 0);
			} else if (horizontalPosition == POS_MIDDLE
					&& verticalPosition == POS_BOTTOM) {
				master.moveTo(0, height);
				master.lineTo(width, height);
				PathWriter w = includeStrokePartitions ? master : fillWriter;
				w.lineTo(width, 0);
				w.lineTo(0, 0);
				w.lineTo(0, height);
			} else if (horizontalPosition == POS_MIDDLE
					&& verticalPosition == POS_ONLY) {
				if (includeStrokePartitions) {
					master.moveTo(width, 0);
					master.lineTo(0, 0);
					master.lineTo(0, height);
					master.lineTo(width, height);
					master.lineTo(width, 0);
				} else {
					master.moveTo(width, 0);
					master.lineTo(0, 0);
					fillWriter.lineTo(0, height);
					strokeWriter.moveTo(0, height);
					master.lineTo(width, height);
					fillWriter.lineTo(width, 0);
				}
			} else if (horizontalPosition == POS_RIGHT
					&& verticalPosition == POS_BOTTOM) {
				master.moveTo(0, height);
				if (bottomRightRadius == 0) {
					master.lineTo(width, height);
				} else {
					master.lineTo(width - bottomRightRadius, height);
					master.curveTo(width - bottomRightRadius
							+ bottomRightRadius * k, height, width, height
							- bottomRightRadius + bottomRightRadius * k, width,
							height - bottomRightRadius);
				}
				master.lineTo(width, 0);
				PathWriter w = includeStrokePartitions ? master : fillWriter;
				w.lineTo(0, 0);
				w.lineTo(0, height);
			} else if (horizontalPosition == POS_ONLY
					&& verticalPosition == POS_BOTTOM) {
				master.moveTo(0, 0);
				if (bottomLeftRadius == 0) {
					master.lineTo(0, height);
				} else {
					master.lineTo(0, height - bottomLeftRadius);
					master.curveTo(0, height - bottomLeftRadius
							+ bottomLeftRadius * k, bottomLeftRadius
							- bottomLeftRadius * k, height, bottomLeftRadius,
							height);
				}
				if (bottomRightRadius == 0) {
					master.lineTo(width, height);
				} else {
					master.lineTo(width - bottomRightRadius, height);
					master.curveTo(width - bottomRightRadius
							+ bottomRightRadius * k, height, width, height
							- bottomRightRadius + bottomRightRadius * k, width,
							height - bottomRightRadius);
				}
				master.lineTo(width, 0);
				PathWriter w = includeStrokePartitions ? master : fillWriter;
				w.lineTo(0, 0);
			} else if (horizontalPosition == POS_LEFT
					&& verticalPosition == POS_ONLY) {
				master.moveTo(width, 0);
				if (topLeftRadius == 0) {
					master.lineTo(0, 0);
				} else {
					master.lineTo(topLeftRadius, 0);
					master.curveTo(topLeftRadius - topLeftRadius * k, 0, 0,
							topLeftRadius - topLeftRadius * k, 0, topLeftRadius);
				}
				if (bottomLeftRadius == 0) {
					master.lineTo(0, height);
				} else {
					master.lineTo(0, height - bottomLeftRadius);
					master.curveTo(0, height - bottomLeftRadius
							+ bottomLeftRadius * k, bottomLeftRadius
							- bottomLeftRadius * k, height, bottomLeftRadius,
							height);
				}
				master.lineTo(width, height);
				PathWriter w = includeStrokePartitions ? master : fillWriter;
				w.lineTo(width, 0);
			} else if (verticalPosition == POS_ONLY
					&& horizontalPosition == POS_RIGHT) {
				master.moveTo(0, height);
				if (bottomRightRadius == 0) {
					master.lineTo(width, height);
				} else {
					master.lineTo(width - bottomRightRadius, height);
					master.curveTo(width - bottomRightRadius
							+ bottomRightRadius * k, height, width, height
							- bottomRightRadius + bottomRightRadius * k, width,
							height - bottomRightRadius);
				}
				if (topRightRadius == 0) {
					master.lineTo(width, 0);
				} else {
					master.lineTo(width, topRightRadius);
					master.curveTo(width, topRightRadius - topRightRadius * k,
							width - topRightRadius + topRightRadius * k, 0,
							width - topRightRadius, 0);
				}
				master.lineTo(0, 0);
				PathWriter w = includeStrokePartitions ? master : fillWriter;
				w.lineTo(0, height);
			} else { // if(horiziontalPosition==ONLY && verticalPosition==ONLY)
				if (topLeftRadius == 0) {
					master.moveTo(0, 0);
				} else {
					master.moveTo(topLeftRadius, 0);
					master.curveTo(topLeftRadius - topLeftRadius * k, 0, 0,
							topLeftRadius - topLeftRadius * k, 0, topLeftRadius);
				}
				if (bottomLeftRadius == 0) {
					master.lineTo(0, height);
				} else {
					master.lineTo(0, height - bottomLeftRadius);
					master.curveTo(0, height - bottomLeftRadius
							+ bottomLeftRadius * k, bottomLeftRadius
							- bottomLeftRadius * k, height, bottomLeftRadius,
							height);
				}
				if (bottomRightRadius == 0) {
					master.lineTo(width, height);
				} else {
					master.lineTo(width - bottomRightRadius, height);
					master.curveTo(width - bottomRightRadius
							+ bottomRightRadius * k, height, width, height
							- bottomRightRadius + bottomRightRadius * k, width,
							height - bottomRightRadius);
				}
				if (topRightRadius == 0) {
					master.lineTo(width, 0);
				} else {
					master.lineTo(width, topRightRadius);
					master.curveTo(width, topRightRadius - topRightRadius * k,
							width - topRightRadius + topRightRadius * k, 0,
							width - topRightRadius, 0);
				}
				master.lineTo(topLeftRadius, 0);
				master.closePath();
			}
			fillWriter.closePath();
		}
		return fillPath;
	}

}