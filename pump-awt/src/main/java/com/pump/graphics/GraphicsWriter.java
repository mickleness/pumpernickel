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
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import com.pump.blog.Blurb;
import com.pump.reflect.Reflection;
import com.pump.util.EnumerationIterator;

/**
 * This object provides a simple way to model/export layered vector graphics.
 * <P>
 * As you render objects to this writer, several {@link GraphicInstruction}
 * objects are collected in a stack. An exception is thrown if you attempt to
 * use any composite that is not a <code>AlphaComposite.SRC_OVER</code>, because
 * this assumes each element is rendered "on top of" all existing elements.
 * <P>
 * The concept here is that simple vector art can be stored and translated. This
 * object (and all instructions) should be fully serializable. Also you can pass
 * this <code>Graphics2D</code> to any existing <code>paint()</code> method and
 * then sift through the resulting instructions to export the data to another
 * format. (For example, this model was originally designed to help export
 * graphics to PPTX files.)
 * 
 */
@Blurb(title = "Graphics2D: Serializable Vector Graphics", releaseDate = "December 2008", article = "http://javagraphics.blogspot.com/2008/12/graphics2d-serializable-vector-graphics.html", summary = "This has been a real life-saver when I'm trying to debug our graphics-rich desktop apps at work.\n"
		+ "<p>Whenever something isn't rendering right this lets you see the stack trace of each graphic instruction.")
public class GraphicsWriter extends AbstractGraphics2D implements
		GraphicInstruction, Serializable, MutableTreeNode {

	public static final int SOURCE_LINE_LIMIT = 12;

	/**
	 * If this is true, then GraphicInstruction.getSource() will return part of
	 * the stack trace of the call that created an instruction.
	 */
	public static boolean DEBUGGING = true;

	private static final long serialVersionUID = 1;

	protected static final Enumeration<?> EMPTY_ENUMERATION = new Enumeration<Object>() {

		public boolean hasMoreElements() {
			return false;
		}

		public Object nextElement() {
			return null;
		}
	};

	List<GraphicInstruction> elements;
	boolean callingCreateMakesNewGroup;
	float opacity = 1;
	GraphicsWriter parent;
	final GraphicsWriter root;
	Object userObj;
	String source;
	int instructionCount = 0;
	int instructionLimit = Integer.MAX_VALUE;

	/**
	 * 
	 * @param createMakesNewGroup
	 *            if this is true, then when Graphics2D.create() is called for
	 *            this object, a new writer will be created that contains its
	 *            own distinct list of instructions. If this is false, then the
	 *            same instruction list is shared between this object and its
	 *            clone.
	 *            <P>
	 *            This can be used to imply the existence of groups in vector
	 *            graphics.
	 */
	public GraphicsWriter(boolean createMakesNewGroup) {
		this(createMakesNewGroup, Integer.MAX_VALUE);
	}

	/**
	 * 
	 * @param createMakesNewGroup
	 *            if this is true, then when Graphics2D.create() is called for
	 *            this object, a new writer will be created that contains its
	 *            own distinct list of instructions. If this is false, then the
	 *            same instruction list is shared between this object and its
	 *            clone.
	 *            <P>
	 *            This can be used to imply the existence of groups in vector
	 *            graphics.
	 * @param instructionLimit
	 *            the maximum number of instructions this writer will store.
	 */
	public GraphicsWriter(boolean createMakesNewGroup, int instructionLimit) {
		elements = new ArrayList<GraphicInstruction>();
		callingCreateMakesNewGroup = createMakesNewGroup;

		source = getCaller();
		root = this;
	}

	private GraphicsWriter(GraphicsWriter w, int instructionLimit) {
		super(w);
		callingCreateMakesNewGroup = w.callingCreateMakesNewGroup;
		if (callingCreateMakesNewGroup) {
			elements = new ArrayList<GraphicInstruction>();
		} else {
			elements = w.elements;
		}

		source = getCaller();
		root = w.root;
	}

	/** Returns whether there are any instructions available to render. */
	public boolean isEmpty() {
		for (int a = 0; a < elements.size(); a++) {
			GraphicInstruction i = elements.get(a);
			if (i instanceof GraphicsWriter) {
				GraphicsWriter w = (GraphicsWriter) i;
				if (w.isEmpty() == false)
					return false;
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * Exports this data as a PNG. (This is intended only for debugging.)
	 * 
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public void saveAsPNG(File file) throws IOException {
		if (elements.size() == 0)
			throw new IOException("No image data to write.");
		Rectangle r = getBounds().getBounds();

		BufferedImage bi = new BufferedImage(r.width, r.height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.translate(r.x, r.y);
		paint(g);
		g.dispose();

		ImageIO.write(bi, "png", file);
	}

	@Override
	public void draw(Shape s) {
		if (root.instructionCount >= root.instructionLimit)
			return;
		DrawInstruction drawInstr = new BasicDrawInstruction(s, transform,
				clipping, paint, stroke, opacity);
		FillInstruction fillInstr = null;
		int lastIndex = elements.size() - 1;
		if (elements.size() > 0
				&& elements.get(lastIndex) instanceof FillInstruction)
			fillInstr = (FillInstruction) elements.get(lastIndex);
		BasicShapeInstruction mergedInstruction = BasicShapeInstruction.merge(
				fillInstr, drawInstr);
		if (mergedInstruction == null) {
			add(drawInstr);
		} else {
			remove(lastIndex);
			add(mergedInstruction);
		}
	}

	@Override
	public void fill(Shape s) {
		if (root.instructionCount >= root.instructionLimit)
			return;
		add(new BasicFillInstruction(s, transform, clipping, paint, opacity));
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns a list of instructions to render to recreate this graphics.
	 * <P>
	 * Depending on how this writer was constructed, this may contain other
	 * <code>GraphicsWriter</code> objects (suggesting nested groups).
	 * 
	 * @param simplify
	 *            this removes empty groups/instructions from the instructions.
	 * @return an array of instructions that recreate this graphics.
	 */
	public GraphicInstruction[] getInstructions(boolean simplify) {
		if (simplify && elements.size() == 1
				&& (elements.get(0) instanceof GraphicsWriter)) {
			GraphicsWriter writer = (GraphicsWriter) elements.get(0);
			return writer.getInstructions(simplify);
		}
		if (!simplify) {
			return elements.toArray(new GraphicInstruction[elements.size()]);
		}
		ArrayList<GraphicInstruction> simplifiedList = new ArrayList<GraphicInstruction>();
		for (int a = 0; a < elements.size(); a++) {
			GraphicInstruction i = elements.get(a);
			boolean ok = true;
			if (i instanceof GraphicsWriter) {
				GraphicsWriter g = (GraphicsWriter) i;
				if (g.isEmpty()) {
					ok = false;
				}
			}
			if (ok)
				simplifiedList.add(i);
		}
		return simplifiedList.toArray(new GraphicInstruction[simplifiedList
				.size()]);
	}

	@Override
	public Graphics create() {
		GraphicsWriter w = new GraphicsWriter(this, instructionLimit);
		if (callingCreateMakesNewGroup) {
			add(w);
		}
		return w;
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, Color bgcolor,
			ImageObserver observer) {
		if (root.instructionCount >= root.instructionLimit)
			return true;
		Rectangle destRect = new Rectangle(dx1, dy1, dx2 - dx1, dy2 - dy1);
		Rectangle srcRect = new Rectangle(sx1, sy1, sx2 - sx1, sy2 - sy1);
		if (bgcolor != null) {
			setColor(bgcolor);
			fill(destRect);
		}
		add(new ImageInstruction(img, getTransform(), destRect, srcRect,
				clipping, opacity));
		return true;
	}

	@Override
	public void setComposite(Composite comp) {
		if (!(comp instanceof AlphaComposite))
			throw new IllegalArgumentException(
					"Only AlphaComposite of type SRC_OVER are supported.");

		AlphaComposite ac = (AlphaComposite) comp;
		int type = ac.getRule();
		if (type == AlphaComposite.CLEAR && isEmpty()) {
			// TODO: keep track of parents, and see if entire graphics tree is
			// empty or not
			setOpacity(0);
		} else if (type != AlphaComposite.SRC_OVER) {
			System.err.println("unsupported AlphaComposite: " + toString(ac));
			throw new IllegalArgumentException(
					"Only AlphaComposite of type SRC_OVER are supported.");
		} else {
			float f = ac.getAlpha();
			setOpacity(f);
		}
	}

	/**
	 * The AlphaComposite class doesn't have a useful toString() method
	 **/
	public static String toString(AlphaComposite c) {
		return "AlphaComposite[rule="
				+ Reflection.nameStaticField(AlphaComposite.class, new Integer(
						c.getRule())) + ", alpha=" + c.getAlpha() + "]";
	}

	public void setOpacity(float f) {
		if (opacity < 0 || opacity > 1)
			throw new IllegalArgumentException("The opacity (" + f
					+ ") must between [0,1].");
		opacity = f;
		super.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				f));
	}

	/** Renders all the instructions in this GraphicsWriter. */
	public void paint(Graphics2D g) {
		g = (Graphics2D) g.create();
		g.setRenderingHints(getRenderingHints());
		for (int a = 0; a < elements.size(); a++) {
			GraphicInstruction i = elements.get(a);
			i.paint(g);
		}
	}

	/**
	 * Returns the bounds that would be affected if <code>paint()</code> were
	 * called.
	 */
	public Rectangle2D getBounds() {
		Rectangle2D r = null;
		for (int a = 0; a < elements.size(); a++) {
			GraphicInstruction i = elements.get(a);
			Rectangle2D r2 = i.getBounds();
			if (r == null) {
				r = r2;
			} else if (r2 != null) {
				r.add(r2);
			}
		}
		return r;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeObject(elements);
		out.writeBoolean(callingCreateMakesNewGroup);
		out.writeFloat(opacity);
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		elements = (List<GraphicInstruction>) in.readObject();
		callingCreateMakesNewGroup = in.readBoolean();
		opacity = in.readFloat();
	}

	@Override
	public void drawString(AttributedCharacterIterator aci, float x, float y) {
		FontRenderContext frc = getFontRenderContext();
		frc = new FontRenderContext(new AffineTransform(), frc.isAntiAliased(),
				frc.usesFractionalMetrics());
		TextLayout tl = new TextLayout(aci, frc);
		Rectangle2D r = tl.getBounds();
		r.setFrame(r.getX() - 1 + x, r.getY() - 1 + y, r.getWidth() + 2,
				r.getHeight() + 2);
		drawTextBox(r, null, null, 0, aci, new Insets(1, 1, 1, 1));
	}

	@Override
	public void drawString(AttributedCharacterIterator aci, int x, int y) {
		drawString(aci, (float) x, (float) y);
	}

	@Override
	public void drawString(String s, float x, float y) {
		if (s.length() == 0)
			return;

		Map<Attribute, Object> map = new HashMap<Attribute, Object>();
		map.put(TextAttribute.FONT, getFont());
		map.put(TextAttribute.FOREGROUND, getPaint());
		AttributedString as = new AttributedString(s, map);

		drawString(as.getIterator(), x, y);
	}

	@Override
	public void drawString(String s, int x, int y) {
		drawString(s, (float) x, (float) y);
	}

	void drawTextBox(Rectangle2D box, Color background, Color frame,
			float frameThickness, AttributedCharacterIterator text,
			Insets insets) {
		if (root.instructionCount >= root.instructionLimit)
			return;
		add(new TextBoxInstruction(box, background, frame, frameThickness,
				text, insets, transform, clipping, Float.MAX_VALUE, opacity));
	}

	public void setParent(GraphicsWriter parent) {
		this.parent = parent;
	}

	public Enumeration<GraphicInstruction> children() {
		return new EnumerationIterator<GraphicInstruction>(elements.iterator());
	}

	public boolean getAllowsChildren() {
		return true;
	}

	public TreeNode getChildAt(int childIndex) {
		return elements.get(childIndex);
	}

	public int getChildCount() {
		return elements.size();
	}

	public int getIndex(TreeNode node) {
		return elements.indexOf(node);
	}

	public TreeNode getParent() {
		return parent;
	}

	public boolean isLeaf() {
		return elements.size() == 0;
	}

	public void add(GraphicInstruction instr) {
		if (root.instructionCount >= instructionLimit)
			return;
		instr.setParent(this);
		elements.add(instr);
		root.instructionCount++;
	}

	public void insert(MutableTreeNode child, int index) {
		if (root.instructionCount >= instructionLimit)
			return;
		GraphicInstruction instr = (GraphicInstruction) child;
		instr.setParent(this);
		elements.add(index, instr);
		root.instructionCount++;
	}

	public void remove(int index) {
		elements.remove(index);
		root.instructionCount--;
	}

	public void remove(MutableTreeNode node) {
		elements.remove(node);
		root.instructionCount--;
	}

	public void removeFromParent() {
		parent.remove(this);
		parent = null;
	}

	public void setParent(MutableTreeNode newParent) {
		GraphicsWriter writer = (GraphicsWriter) newParent;
		setParent(writer);
	}

	public void setUserObject(Object object) {
		userObj = object;
	}

	public Object getUserObject() {
		return userObj;
	}

	public String getSource() {
		return source;
	}

	/**
	 * Returns the current stack trace, minus all lines that involve
	 * "com.bric.graphics" objects.
	 */
	static String getCaller() {
		if (DEBUGGING == false)
			return "Unknown";
		StackTraceElement[] trace = (new RuntimeException("Dump Stack"))
				.getStackTrace();
		int ctr = 0;
		for (int a = 0; a < trace.length; a++) {
			if (trace[a].getClassName().indexOf(
					"com.bric.graphics.GraphicsWriter") == 0
					|| trace[a].getClassName().indexOf("java.lang.Thread") == 0) {
				ctr++;
				trace[a] = null;
			}
		}

		StringBuffer sb = new StringBuffer();
		ctr = 0;
		for (int a = 0; a < trace.length; a++) {
			if (trace[a] != null) {
				sb.append(trace[a].toString() + "\n");
				ctr++;
				if (ctr == SOURCE_LINE_LIMIT)
					return sb.toString().trim();
			}
		}
		return sb.toString();
	}
}