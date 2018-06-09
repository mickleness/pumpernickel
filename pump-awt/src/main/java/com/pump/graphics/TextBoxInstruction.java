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
package com.pump.graphics;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.Serializable;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.swing.tree.TreeNode;

import com.pump.geom.ShapeBounds;
import com.pump.geom.ShapeStringUtils;

/**
 * A <code>GraphicInstruction</code> for text boxes.
 * <P>
 * This is a more complicated instruction than the others, because fonts are not
 * guaranteed to be consistent between runtimes. It is highly recommended that
 * text boxes be given several pixels of "wiggle room" when their boxes are
 * being designed, so if a font is missing or if spacing changes the text is
 * still laid out reasonably.
 * <P>
 * By default this first version assumes the text should be centered in the text
 * box, but it wouldn't be hard to offer horizontal (and vertical!) alignment if
 * needed.
 * 
 * TODO: test text boxes with multiple lines of text
 * 
 */
public class TextBoxInstruction implements GraphicInstruction, Serializable {
	private static final long serialVersionUID = 1;
	Insets insets;
	Rectangle2D box;
	Color background;
	Color frame;
	float frameThickness;
	AttributedCharacterIterator text;
	int startingIndex;
	AffineTransform transform;
	Shape clipping;
	float maxWidth;
	float opacity;

	GraphicsWriter parent = null;
	String source;

	/**
	 * Create a new <code>TextBoxInstruction</code>.
	 * 
	 * @param box
	 *            the bounds of this text box
	 * @param background
	 *            the optional background color. May be null.
	 * @param frame
	 *            the optional frame color. May be null.
	 * @param frameThickness
	 *            the optional frame thickness. May be zero.
	 * @param text
	 *            the text to display in this box.
	 * @param insets
	 *            the insets between the box bounds and the text.
	 * @param transform
	 *            the transform this box is shown through.
	 * @param clipping
	 *            the clipping. The clipping is applied <i>before</i> the
	 *            transform.
	 * @param opacity
	 *            the opacity of this text box. (This will translate into an
	 *            AlphaComposite applied to everything: the background, frame,
	 *            and text.)
	 */
	public TextBoxInstruction(Rectangle2D box, Color background, Color frame,
			float frameThickness, AttributedCharacterIterator text,
			Insets insets, AffineTransform transform, Shape clipping,
			float opacity) {
		this(box, background, frame, frameThickness, text, insets, transform,
				clipping, (float) box.getWidth(), opacity);
	}

	/**
	 * Create a new <code>TextBoxInstruction</code>.
	 * 
	 * @param box
	 *            the bounds of this text box
	 * @param background
	 *            the optional background color. May be null.
	 * @param frame
	 *            the optional frame color. May be null.
	 * @param frameThickness
	 *            the optional frame thickness. May be zero.
	 * @param text
	 *            the text to display in this box.
	 * @param insets
	 *            the insets between the box bounds and the text.
	 * @param transform
	 *            the transform this box is shown through.
	 * @param clipping
	 *            the clipping. The clipping is applied <i>before</i> the
	 *            transform.
	 * @param maxWidth
	 *            normally this would be the width of the box, but you can
	 *            provide a custom value for this if you need to. A width of
	 *            Float.MAX_VALUE indicates that all the text should be
	 *            displayed in 1 line.
	 * 
	 *            <P>
	 *            For example, in the GraphicsWriter the drawString() methods
	 *            use this constructor and pass an argument of Float.MAX_VALUE,
	 *            because those boxes are guaranteed to be 1 line of text, and
	 *            fit in the bounds provided. And, more importantly: if I set
	 *            the LineBreakMeasurer strictly to the appropriate width: it
	 *            fails. In one example I had to add 7 to the width the
	 *            resulting TextLayout actually needed to avoid my text being
	 *            split into 2 lines. So really what I'm saying is: this is a
	 *            hackish solution to a problem/bug I was facing with the
	 *            LineBreakMeasurer that made no sense.
	 * @param opacity
	 *            the opacity of this text box. (This will translate into an
	 *            AlphaComposite applied to everything: the background, frame,
	 *            and text.)
	 */
	public TextBoxInstruction(Rectangle2D box, Color background, Color frame,
			float frameThickness, AttributedCharacterIterator text,
			Insets insets, AffineTransform transform, Shape clipping,
			float maxWidth, float opacity) {
		if (box == null)
			throw new NullPointerException(
					"The bounds of this TextBox are null.");
		if (text == null)
			throw new NullPointerException("The text of this TextBox is null.");
		if (opacity < 0 || opacity > 1)
			throw new IllegalArgumentException("The opacity (" + opacity
					+ ") must be between [0,1].");
		if (transform == null)
			transform = new AffineTransform();
		this.maxWidth = maxWidth;
		this.box = box;
		this.background = background;
		this.frame = frame;
		this.frameThickness = frameThickness;
		this.text = text;
		this.startingIndex = text.getBeginIndex();
		this.insets = insets;
		if (clipping != null) {
			this.clipping = new Area(clipping);
		}
		this.transform = transform;
		this.opacity = opacity;

		if (this.insets == null)
			this.insets = new Insets(0, 0, 0, 0);

		source = GraphicsWriter.getCaller();
	}

	/** Returns the optional background color of this text box. May be null. */
	public Color getBackground() {
		return background;
	}

	/** Returns the optional frame color of this text box. May be null. */
	public Color getFrame() {
		return frame;
	}

	/** Returns the opacity [0,1]. */
	public float getOpacity() {
		return opacity;
	}

	/** Returns the insets of this text box. */
	public Insets getInsets() {
		return (Insets) insets.clone();
	}

	/** Returns the width of the border around this text box. May be zero. */
	public float getFrameThickness() {
		return frameThickness;
	}

	/**
	 * Returns the maximum width of a line of text in this text box. This may be
	 * based on the bounds of this text box, or it may be Float.MAX_VALUE,
	 * indicating that all the text should fit on one line.
	 */
	public float getMaxWidth() {
		return maxWidth;
	}

	/** Returns the text displayed in this text box. */
	public AttributedString getString() {
		return new AttributedString(text);
	}

	/** Returns the AffineTransform this text box is viewed through. */
	public AffineTransform getTransform() {
		return new AffineTransform(transform);
	}

	/**
	 * Returns the clipping used in this instruction. (May be null.) Note this
	 * clipping is applied <i>before</i> the <code>AffineTransform</code>.
	 */
	public Shape getClipping() {
		if (clipping == null)
			return null;
		return new GeneralPath(clipping);
	}

	/** Returns the dimensions of the text box. */
	public Rectangle2D getBox() {
		return (Rectangle2D) box.clone();
	}

	/**
	 * Returns the rectangle enclosing this text box. Unlike
	 * <code>getBox()</code>, this takes into account the stroke width and
	 * <code>AffineTransform</code>.
	 */
	public Rectangle2D getBounds() {
		Rectangle2D r = (Rectangle2D) box.clone();
		if (frameThickness > 0) {
			float k = frameThickness / 2;
			r.setFrame(r.getX() - k, r.getY() - k, r.getWidth() + 2 * k,
					r.getHeight() + 2 * k);
		}
		return ShapeBounds.getBounds(r, transform);
	}

	/** Renders this text box. */
	public void paint(Graphics2D g) {
		g = (Graphics2D) g.create();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_ON);

		if (clipping != null)
			g.clip(clipping);
		g.transform(transform);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				opacity));

		text.setIndex(startingIndex);

		if (background != null) {
			g.setColor(background);
			g.fill(box);
		}
		if (frame != null && frameThickness > 0) {
			g.setColor(frame);
			g.setStroke(new BasicStroke(frameThickness));
			g.draw(box);
		}

		g.clip(box);

		FontRenderContext frc = g.getFontRenderContext();
		frc = new FontRenderContext(new AffineTransform(), frc.isAntiAliased(),
				frc.usesFractionalMetrics());
		LineBreakMeasurer lbm = new LineBreakMeasurer(text, frc);
		float y = (float) box.getY() + insets.top + frameThickness / 2f;
		while (lbm.getPosition() < text.getEndIndex()) {
			float lineWidth = this.maxWidth - frameThickness / 2 - insets.left
					- insets.right;
			TextLayout tl = lbm.nextLayout(lineWidth);

			float width = tl.getAdvance();
			y += tl.getAscent() - tl.getDescent();
			float x = (float) box.getCenterX() - width / 2;
			tl.draw(g, x, y);
			y += tl.getDescent() + tl.getLeading();
			if (y > box.getHeight() + box.getY())
				return; // don't fuss about data that isn't visible
		}
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeObject(background);
		out.writeObject(frame);
		out.writeObject(box);
		out.writeFloat(frameThickness);
		out.writeObject(insets);
		out.writeInt(startingIndex);
		out.writeObject(new TextWrapper(text));
		out.writeObject(transform);
		out.writeFloat(maxWidth);
		if (clipping == null) {
			out.writeObject(null);
		} else {
			out.writeObject(ShapeStringUtils.toString(clipping));
		}
		out.writeFloat(opacity);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		background = (Color) in.readObject();
		frame = (Color) in.readObject();
		box = (Rectangle2D) in.readObject();
		frameThickness = in.readFloat();
		insets = (Insets) in.readObject();
		startingIndex = in.readInt();
		TextWrapper w = (TextWrapper) in.readObject();
		text = w.getAttributedCharacterIterator();
		transform = (AffineTransform) in.readObject();
		maxWidth = in.readFloat();
		String clipping = (String) in.readObject();
		if (clipping != null) {
			this.clipping = ShapeStringUtils.createGeneralPath(clipping);
		}
		opacity = in.readFloat();
	}

	public void setParent(GraphicsWriter parent) {
		this.parent = parent;
	}

	public Enumeration<?> children() {
		return GraphicsWriter.EMPTY_ENUMERATION;
	}

	public boolean getAllowsChildren() {
		return false;
	}

	public GraphicInstruction getChildAt(int childIndex) {
		return null;
	}

	public int getChildCount() {
		return 0;
	}

	public int getIndex(TreeNode node) {
		return -1;
	}

	public TreeNode getParent() {
		return parent;
	}

	public boolean isLeaf() {
		return true;
	}

	public String getSource() {
		return source;
	}
}

/** This is used to serialize AttributedString data. */
class TextWrapper implements Serializable {
	private static final long serialVersionUID = 1;

	AttributedString string;

	public TextWrapper(AttributedCharacterIterator i) {
		string = new AttributedString(i);
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		AttributedCharacterIterator i = string.getIterator();
		i.setIndex(i.getBeginIndex());

		StringBuffer sb = new StringBuffer();
		int w = i.getIndex();
		while (w <= i.getEndIndex() - 1) {
			i.setIndex(w);
			sb.append(i.current());
			w++;
		}
		out.writeObject(sb.toString());

		ArrayList<Run> runs = new ArrayList<Run>();

		int z = i.getBeginIndex();
		while (z < i.getEndIndex()) {
			i.setIndex(z);
			int endOfRun = i.getRunLimit();
			Map<Attribute, Object> map = i.getAttributes();
			Iterator<Attribute> it = map.keySet().iterator();
			while (it.hasNext()) {
				Attribute at = it.next();
				Object v = map.get(at);
				runs.add(new Run(z, endOfRun, at, v));
			}
			z = endOfRun;
		}
		out.writeObject(runs);

	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		String t = (String) in.readObject();
		string = new AttributedString(t);
		@SuppressWarnings("unchecked")
		ArrayList<Run> runs = (ArrayList<Run>) in.readObject();
		for (int a = 0; a < runs.size(); a++) {
			Run run = runs.get(a);
			string.addAttribute(run.attribute, run.value, run.i1, run.i2);
		}
	}

	public AttributedCharacterIterator getAttributedCharacterIterator() {
		return string.getIterator();
	}
}

/** This represents a run of stylized text. */
class Run implements Serializable {
	private static final long serialVersionUID = 1;

	int i1, i2;
	Attribute attribute;
	Object value;

	public Run(int i1, int i2, Attribute attribute, Object value) {
		this.i1 = i1;
		this.i2 = i2;
		this.attribute = attribute;
		this.value = value;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(i1);
		out.writeInt(i2);
		out.writeObject(attribute);
		out.writeObject(value);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		i1 = in.readInt();
		i2 = in.readInt();
		attribute = (Attribute) in.readObject();
		value = in.readObject();
	}
}