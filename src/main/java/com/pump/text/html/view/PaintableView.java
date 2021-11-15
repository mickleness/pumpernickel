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
package com.pump.text.html.view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.Position.Bias;
import javax.swing.text.View;
import javax.swing.text.html.HTML;

import com.pump.awt.Paintable;
import com.pump.geom.ShapeBounds;

/**
 * This View shows a Paintable.
 * <p>
 * This is similar to the ImageView. Key differences include:
 * <ul>
 * <li>The ImageView supports repainting animations, and the PaintableView does
 * not.</li>
 * <li>The ImageView supports a few different states (error, loading, loaded,
 * etc), but the PaintableView does not.</li>
 * <li>The PaintableView more easily supports high-resolution monitors. The
 * ImageView only supports them if you know to pass in a
 * MultiResolutionImage (and even you lose some level of control over
 * rendering).</li>
 * </ul>
 */
public class PaintableView extends View {

	protected Paintable paintable;
	protected int width, height;

	/**
	 * Create a view that always shows a Paintable.
	 */
	public PaintableView(Element elem, Paintable paintable) {
		super(elem);
		this.paintable = paintable;
		width = paintable.getWidth();
		height = paintable.getHeight();
	}

	@Override
	public float getPreferredSpan(int axis) {
		if (axis == View.X_AXIS)
			return width;
		if (axis == View.Y_AXIS)
			return height;
		throw new IllegalArgumentException("unsupported axis: " + axis);
	}

	@Override
	public void paint(Graphics g, Shape allocation) {
		Rectangle2D r = ShapeBounds.getBounds(allocation);

		Graphics2D g2 = (Graphics2D) g.create();
		g2.translate(r.getX(), r.getY());
		paintable.paint(g2);
		g2.dispose();
	}

	// the following is copied and pasted from ImageView:

	@Override
	public Shape modelToView(int pos, Shape a, Bias b)
			throws BadLocationException {
		int p0 = getStartOffset();
		int p1 = getEndOffset();
		if ((pos >= p0) && (pos <= p1)) {
			Rectangle r = a.getBounds();
			if (pos == p1) {
				r.x += r.width;
			}
			r.width = 0;
			return r;
		}
		return null;
	}

	@Override
	public int viewToModel(float x, float y, Shape a, Bias[] bias) {
		Rectangle alloc = (Rectangle) a;
		if (x < alloc.x + alloc.width) {
			bias[0] = Position.Bias.Forward;
			return getStartOffset();
		}
		bias[0] = Position.Bias.Backward;
		return getEndOffset();
	}

	/**
	 * Returns the text to display if the image cannot be loaded. This is
	 * obtained from the Elements attribute set with the attribute name
	 * <code>HTML.Attribute.ALT</code>.
	 *
	 * @return the test to display if the image cannot be loaded.
	 */
	public String getAltText() {
		return (String) getElement().getAttributes()
				.getAttribute(HTML.Attribute.ALT);
	}

	/**
	 * For images the tooltip text comes from text specified with the
	 * <code>ALT</code> attribute. This is overriden to return
	 * <code>getAltText</code>.
	 *
	 * @see JTextComponent#getToolTipText
	 */
	@Override
	public String getToolTipText(float x, float y, Shape allocation) {
		return getAltText();
	}
}