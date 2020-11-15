package com.pump.graphics.vector;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.renderable.RenderableImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import com.pump.awt.serialization.AWTSerializationUtils;
import com.pump.geom.Clipper;
import com.pump.geom.EmptyPathException;
import com.pump.geom.ShapeBounds;
import com.pump.geom.ShapeStringUtils;
import com.pump.geom.ShapeUtils;
import com.pump.graphics.Graphics2DContext;
import com.pump.io.HashCodeOutputStream;
import com.pump.io.serialization.FilteredObjectInputStream;
import com.pump.io.serialization.FilteredObjectOutputStream;

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
		FilteredObjectOutputStream fout = AWTSerializationUtils
				.createFilteredObjectOutputStream(out);
		fout.writeInt(0);
		fout.writeObject(context);
		writeMap(fout, coreProperties);
		writeMap(fout, clientProperties);
	}

	private void writeMap(FilteredObjectOutputStream fout,
			Map<String, Object> map) throws IOException {
		fout.writeInt(map.size());

		// our #equals() method relies on the exact order of map entries
		// being constant between two Operations, so let's sort the keys:
		SortedSet<String> sortedKeys = new TreeSet<>();
		sortedKeys.addAll(map.keySet());

		for (String key : sortedKeys) {
			fout.writeObject(key);
			Object v = map.get(key);
			if (v instanceof RenderableImage) {
				v = ((RenderableImage) v).createDefaultRendering();
			}
			fout.writeObject(v);
		}
	}

	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		FilteredObjectInputStream fin = new FilteredObjectInputStream(in);
		int version = in.readInt();
		if (version == 0) {
			context = (Graphics2DContext) in.readObject();
			coreProperties = readMap(fin);
			clientProperties = readMap(fin);
		} else {
			throw new IOException("unsupported internal version " + version);
		}
	}

	private Map<String, Object> readMap(FilteredObjectInputStream fin)
			throws IOException, ClassNotFoundException {
		int size = fin.readInt();
		Map<String, Object> returnValue = new HashMap<>(size);
		while (size > 0) {
			String key = (String) fin.readObject();
			Object value = fin.readObject();
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		// I'm not proud of this, but we put so much attention into
		// serialization that's probably the simplest way to check equality now:
		try {
			ByteArrayOutputStream b1 = new ByteArrayOutputStream();
			ObjectOutputStream obj1 = new ObjectOutputStream(b1);
			obj1.writeObject(this);
			obj1.close();

			ByteArrayOutputStream b2 = new ByteArrayOutputStream();
			ObjectOutputStream obj2 = new ObjectOutputStream(b2);
			obj2.writeObject(obj);
			obj2.close();

			byte[] z1 = b1.toByteArray();
			byte[] z2 = b2.toByteArray();

			return Arrays.equals(z1, z2);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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
							str = str.substring(0, i) + "...";
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
}
