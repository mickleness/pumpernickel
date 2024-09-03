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
package com.pump.graphics.vector;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.event.ChangeListener;

import com.pump.graphics.Graphics2DContext;
import com.pump.io.HashCodeOutputStream;
import com.pump.util.list.ListListener;
import com.pump.util.list.ObservableList;
import com.pump.util.list.ObservableList.ArrayListener;

/**
 * A VectorImage is a list of Operations. It doesn't have clearly defined
 * bounds; when you ask the bounds they are dynamically calculated.
 * <p>
 * <h3>Serialization</h3> All the Operations in this image are always
 * serialized.
 * <p>
 * If an ObservableList listener is serializable then this class will preserve
 * it. Otherwise non-serializable listeners are ignored during serialization.
 */
public class VectorImage implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * This ("JVG") is the recommended file format if you read/write
	 * VectorImages to files. (It stands for "java vector graphics".)
	 */
	public static final String FILE_EXTENSION = "JVG";

	protected ObservableList<Operation> operations = new ObservableList<>();

	/**
	 * Create an empty VectorImage.
	 */
	public VectorImage() {
	}

	/**
	 * Read a VectorImage that was previously saved by calling
	 * {@link #save(OutputStream)}.
	 */
	public VectorImage(InputStream in)
			throws IOException, ClassNotFoundException {
		try (GZIPInputStream zipIn = new GZIPInputStream(in)) {
			try (ObjectInputStream objIn = new ObjectInputStream(zipIn)) {
				VectorImage i = (VectorImage) objIn.readObject();
				operations = i.operations;
			}
		}
	}

	/**
	 * Create a new VectorGraphics2D that modifies this VectorImage.
	 */
	public VectorGraphics2D createGraphics() {
		return new VectorGraphics2D(new Graphics2DContext(), operations);
	}

	/**
	 * Create a new VectorGraphics2D with an initial clipRect
	 * 
	 * @param clipRect
	 *            the clip rect to assign to the VectorGraphics2D
	 */
	public VectorGraphics2D createGraphics(Rectangle clipRect) {
		VectorGraphics2D g = createGraphics();
		g.clipRect(clipRect.x, clipRect.y, clipRect.width, clipRect.height);
		return g;
	}

	/**
	 * Paint all the operations in this VectorImage.
	 * 
	 * @param g
	 */
	public void paint(Graphics2D g) {
		for (Operation operation : operations) {
			operation.paint(g);
		}
	}

	/**
	 * Return the Operations in this image. You can attach listeners to this
	 * list to be notified as it changes.
	 */
	public ObservableList<Operation> getOperations() {
		return operations;
	}

	/**
	 * Return the bounds of all operations in {@link #getOperations()}, or null
	 * if there are no defined bounds.
	 * <p>
	 * For example: if this contains no operations, or an operation that falls
	 * outside (For example: an operation that renders outside of its clipped
	 * area will have null bounds.)
	 */
	public Rectangle2D getBounds() {
		Rectangle2D sum = null;
		for (Operation op : getOperations()) {
			Rectangle2D r = op.getBounds();
			if (r != null) {
				if (sum == null) {
					sum = r;
				} else {
					sum.add(r);
				}
			}
		}
		return sum;
	}

	private void writeObject(java.io.ObjectOutputStream out)
			throws IOException {
		out.writeInt(0);
		out.writeObject(operations.toArray(new Operation[operations.size()]));
		out.writeInt(operations.getTimeoutSeconds());

		ArrayListener<Operation>[] arrayListeners = operations
				.getArrayListeners();
		ChangeListener[] changeListeners = operations.getChangeListeners();
		ListListener<Operation>[] listListeners = operations.getListListeners();

		nullifyUnserializable(arrayListeners);
		nullifyUnserializable(changeListeners);
		nullifyUnserializable(listListeners);

		out.writeObject(arrayListeners);
		out.writeObject(changeListeners);
		out.writeObject(listListeners);

		UncaughtExceptionHandler ueh = operations
				.getListenerUncaughtExceptionHandler();
		if (!(ueh instanceof Serializable))
			ueh = null;

		out.writeObject(ueh);
	}

	private void nullifyUnserializable(Object[] array) {
		for (int a = 0; a < array.length; a++) {
			if (!(array[a] instanceof Serializable))
				array[a] = null;
		}
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		int version = in.readInt();
		if (version == 0) {
			Operation[] array = (Operation[]) in.readObject();
			operations = new ObservableList<>();
			operations.addAll(Arrays.asList(array));
			operations.setTimeoutSeconds(in.readInt());

			ArrayListener<Operation>[] arrayListeners = (ArrayListener<Operation>[]) in
					.readObject();
			ChangeListener[] changeListeners = (ChangeListener[]) in
					.readObject();
			ListListener<Operation>[] listListeners = (ListListener<Operation>[]) in
					.readObject();

			// Ideally we also ought to tag which listeners allowed
			// modification:
			for (ArrayListener<Operation> arrayListener : arrayListeners) {
				if (arrayListener != null)
					operations.addArrayListener(arrayListener, true);
			}
			for (ChangeListener changeListener : changeListeners) {
				if (changeListener != null)
					operations.addChangeListener(changeListener, true);
			}
			for (ListListener<Operation> listListener : listListeners) {
				if (listListener != null)
					operations.addListListener(listListener, true);
			}

			UncaughtExceptionHandler ueh = (UncaughtExceptionHandler) in
					.readObject();
			if (ueh != null)
				operations.setListenerUncaughtExceptionHandler(ueh);

		} else {
			throw new IOException("unsupported internal version " + version);
		}
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
		if (obj.getClass() != getClass())
			return false;
		if (obj == this)
			return true;

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

	/**
	 * Write this VectorImage to an OutputStream. This uses GZIP compression and
	 * Java serialization.
	 */
	public void save(OutputStream out) throws IOException {
		try (GZIPOutputStream zipOut = new GZIPOutputStream(out)) {
			try (ObjectOutputStream objOut = new ObjectOutputStream(zipOut)) {
				objOut.writeObject(this);
			}
		}
	}

	/**
	 * Create a BufferedImage rendering of this image.
	 */
	public BufferedImage toBufferedImage() {
		Rectangle bounds = getBounds().getBounds();
		BufferedImage bi = new BufferedImage(bounds.width, bounds.height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.translate(-bounds.x, -bounds.y);
		paint(g);
		g.dispose();
		return bi;
	}
}