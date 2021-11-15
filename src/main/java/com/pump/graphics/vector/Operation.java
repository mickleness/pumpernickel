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
package com.pump.graphics.vector;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.renderable.RenderableImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import com.pump.data.converter.ConverterUtils;
import com.pump.geom.Clipper;
import com.pump.geom.EmptyPathException;
import com.pump.geom.ShapeBounds;
import com.pump.geom.ShapeStringUtils;
import com.pump.geom.ShapeUtils;
import com.pump.graphics.Graphics2DContext;
import com.pump.io.HashCodeOutputStream;

/**
 * Each Operation subclass corresponds to one or more painting methods in the
 * Graphics2D class.
 */
public abstract class Operation implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * The context used to prepare the Graphics2D (rendering hints, transform,
	 * stroke, etc.)
	 */
	protected Graphics2DContext context;

	/**
	 * This should only be used by getters/setters in subclasses.
	 */
	protected Map<String, Object> coreProperties = new HashMap<>();

	/**
	 * This stores data by external calls to {@link #getProperty(String)} and
	 * {@link #setProperty(String, Serializable)}
	 */
	protected Map<String, Object> clientProperties = new HashMap<>();

	protected Operation(Graphics2DContext context) {
		setContext(context);
	}

	/**
	 * Return a Graphics2DContext for this Operation.
	 */
	public Graphics2DContext getContext() {
		return context.clone();
	}

	/**
	 * Assign the Graphics2DContext for this operation. This method will clone
	 * the incoming context.
	 */
	public void setContext(Graphics2DContext context) {
		Objects.requireNonNull(context);
		this.context = context.clone();
	}

	/**
	 * Paint this Operation.
	 * <p>
	 * This public method first installs the context and then calls
	 * {@link #paintOperation(Graphics2D)}.
	 */
	public void paint(Graphics2D g) {
		g = (Graphics2D) g.create();
		context.install(g);
		paintOperation(g);
		g.dispose();
	}

	/**
	 * Paint this Operation to an incoming Graphics2D.
	 * <p>
	 * This method is usually one-line. For ex: <code>g.fill(myShape)</code> or
	 * <code>g.drawImage(..)</code>.
	 */
	protected abstract void paintOperation(Graphics2D g);

	private void writeObject(java.io.ObjectOutputStream out)
			throws IOException {
		out.writeInt(0);
		out.writeObject(context);
		writeMap(out, coreProperties);
		writeMap(out, clientProperties);
	}

	private static void writeMap(ObjectOutputStream out,
			Map<String, Object> map) throws IOException {
		out.writeInt(map.size());
		for (String key : map.keySet()) {
			out.writeObject(key);
			Object v = map.get(key);
			if (v instanceof RenderableImage) {
				v = ((RenderableImage) v).createDefaultRendering();
			}
			ConverterUtils.writeObject(out, v);
		}
	}

	private void readObject(ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		int version = in.readInt();
		if (version == 0) {
			context = (Graphics2DContext) in.readObject();
			coreProperties = readMap(in);
			clientProperties = readMap(in);
		} else {
			throw new IOException("unsupported internal version " + version);
		}
	}

	private static Map<String, Object> readMap(ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		int size = in.readInt();
		Map<String, Object> returnValue = new HashMap<>(size);
		while (size > 0) {
			String key = (String) in.readObject();
			Object value = ConverterUtils.readObject(in);
			returnValue.put(key, value);
			size--;
		}
		return returnValue;
	}

	/**
	 * Return the clipped bounds of this Operation, or null if this Operation
	 * will not result in any changes. For example: if this Operation draws an
	 * image that is outside of the current clipping, then this should return
	 * null.
	 */
	public Rectangle2D getBounds() {
		try {
			return ShapeBounds.getBounds(getOutline());
		} catch (EmptyPathException e) {
			return null;
		}
	}

	/**
	 * Return an outline of this Operation that takes into account the current
	 * clipping. The outline should err on the side of being too
	 * large/rectangular. The word "outline" is ambiguous and open to
	 * interpretation, though. As a rule of thumb it should be "what you expect
	 * to see highlight when you mouse over a rendering." For example:
	 * <ul>
	 * <li>An image should generally return a rectangular frame.</li>
	 * <li>Filling a shape (like a circle) should generally return the circle
	 * itself.</li>
	 * <li>Drawing a dotted wavy stroke should return the rectangular frame,
	 * because returning the exact dotted stroke would be too much noise.</li>
	 * <ul>
	 */
	public Shape getOutline() {
		Shape unclippedShape = getUnclippedOutline();
		Shape clipping = getContext().getClip(false);
		if (clipping == null) {
			return unclippedShape;
		}

		// a lot of clipping is simply a Rectangle:
		Rectangle2D rect = ShapeUtils.getRectangle2D(clipping);
		if (rect != null) {
			return Clipper.clipToRect(unclippedShape, rect);
		}

		Area clippingArea = new Area(clipping);
		clippingArea.intersect(new Area(unclippedShape));
		return clippingArea;
	}

	/**
	 * Return the unclipped outline of this Operation. (See notes in
	 * {@link #getOutline()}.
	 */
	public abstract Shape getUnclippedOutline();

	/**
	 * Set a property to serialize in this Operation. For example: you may want
	 * to identify a stacktrace or JComponent that produced a certain Operation
	 * for debugging purposes.
	 */
	public void setProperty(String propertyName, Serializable value) {
		clientProperties.put(propertyName, value);
	}

	/**
	 * Return a property previously assigned via
	 * {@link #setProperty(String, Serializable)}.
	 */
	public Object getProperty(String propertyName) {
		return clientProperties.get(propertyName);
	}

	/**
	 * Return a copy of all the properties assigned to this Operation.
	 */
	public Map<String, Object> getProperties() {
		return new HashMap<>(clientProperties);
	}

	@Override
	public int hashCode() {
		HashCodeOutputStream hashOut = new HashCodeOutputStream();
		try {
			ObjectOutputStream objOut = new ObjectOutputStream(hashOut);
			objOut.writeObject(this);
			objOut.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return hashOut.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Operation))
			return false;
		return equals((Operation) obj, true);
	}

	/**
	 * Return true if two Operations are equal
	 * 
	 * @param obj
	 * @param includeContext
	 *            if true then the Graphics2DContexts must be identical, if
	 *            false then the Graphics2DContexts are not consulted.
	 * @return
	 */
	public boolean equals(Operation other, boolean includeContext) {
		if (other == null)
			return false;
		if (getClass() != other.getClass())
			return false;

		if (includeContext) {
			if (!context.equals(other.context))
				return false;
		}

		if (!isMapEqual(coreProperties, other.coreProperties))
			return false;
		if (!isMapEqual(clientProperties, other.clientProperties))
			return false;
		return true;
	}

	private boolean isMapEqual(Map<String, Object> map1,
			Map<String, Object> map2) {
		Collection<String> keys1 = map1.keySet();
		Collection<String> keys2 = map2.keySet();

		if (!keys1.equals(keys2))
			return false;

		for (String key : keys1) {
			Object obj1 = map1.get(key);
			Object obj2 = map2.get(key);
			if (!ConverterUtils.equals(obj1, obj2))
				return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append("[");
		toString(sb, coreProperties);
		toString(sb, clientProperties);
		sb.append("context=" + context.toString());
		sb.append("]");
		return sb.toString();
	}

	private void toString(StringBuilder sb, Map<String, Object> map) {
		for (Entry<String, Object> entry : map.entrySet()) {
			Object v = entry.getValue();
			if (v instanceof Shape) {
				Shape s = (Shape) v;
				Rectangle r = ShapeUtils.getRectangle(s);
				if (r != null) {
					v = r.toString();
				} else {
					Rectangle2D r2 = ShapeUtils.getRectangle2D(s);
					if (r2 != null) {
						v = r2.toString();
					} else {
						String str = ShapeStringUtils.toString(s);
						if (str.length() > 100) {
							int i = str.indexOf(' ', 100);
							if (i != -1) {
								str = str.substring(0, i) + "...";
							}
						}
						v = str;
					}
				}
			}
			sb.append(entry.getKey());
			sb.append("=");
			sb.append(v);
			sb.append(" ");
		}
	}

	/**
	 * Create derivative Operations that take this Operation and intersect it
	 * with the argument shape. This can achieve a "soft clip" effect. But this
	 * has nothing to do with the {@link java.awt.Graphics#getClip()} property.
	 * <p>
	 * If the resulting operations are rendering with an antialiased rendering
	 * hint, then as far as the user is concerned this effectively achieves a
	 * soft clip. Whatever is rendered is still rendered through the Graphics's
	 * actual clipping (which is usually a rectangle).
	 * <p>
	 * This may return an empty array if this Operation doesn't intersect with
	 * the argument.
	 * <p>
	 * The result operations may not be completely identical to this original
	 * Operation. For example: StringOperations may change how they render based
	 * on rendering hints. But once a StringOperation is converted to a
	 * FillOperation (to apply the soft clipping shape): those rendering hints
	 * are no longer consulted. (Because now we're rendering a shape instead of
	 * a String). So the result will always be highly similar, but the exact
	 * pixels may vary.
	 * 
	 * @param softCippingShape
	 *            the soft clipping shape to apply to this Operation.
	 * @return zero or more Operations that represent what this Operation looks
	 *         like if it intersects with the argument.
	 */
	public abstract Operation[] toSoftClipOperation(Shape softCippingShape);

	/**
	 * Assign a RenderingHint in this operation's context.
	 * <p>
	 * Alternatively: you could call {@link #getContext()}, change the hint, and
	 * then call {@link #setContext(Graphics2DContext)}, but that clones the
	 * context twice. This call is more efficient.
	 */
	public void setRenderingHint(Key hintKey, Object hintValue) {
		context.setRenderingHint(hintKey, hintValue);
	}

	/**
	 * This is a convenience method for calling
	 * <code>getContext().getRenderingHint(x)</code>
	 */
	public Object getRenderingHint(Key hintKey) {
		return context.getRenderingHint(hintKey);
	}
}