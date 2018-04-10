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
package com.pump.swing;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.UIManager;

import com.pump.blog.Blurb;
import com.pump.geom.ShapeStringUtils;
import com.pump.geom.ShapeUtils;
import com.pump.plaf.ShapeCreationUI;

/**
 * A panel used to define a series of shapes.
 * <p>
 * There are a few different <code>ShapeCreationUIs</code> to choose from. Once
 * the appropriate UI is installed: just call
 * <code>shapePanel.setMode( MODE_CREATE )</code> to the user to be able to draw
 * shapes in this panel.
 * <p>
 * You may want to override the UI to paint additional components or to better
 * control the stroke/paints used.
 */
@Blurb(title = "Shapes: A ComponentUI to Create and Edit Shapes", releaseDate = "March 2014", summary = "This is a UI component that lets the user define shapes. There are currently 3 unique UIs "
		+ "to define shapes (each with its own strengths and weaknesses). "
		+ "There are a couple of different UIs that can be applied to this, and I haven't yet written up a blog article on the subject.", article = "http://javagraphics.blogspot.com/2014/03/shapes-modeling-cubic-shapes-without.html")
public class ShapeCreationPanel extends JComponent {
	private static final long serialVersionUID = 1L;

	private static final String uiClassID = "ShapeCreationUI";

	public static enum Active {
		/** Only selected shapes show handles. */
		SELECTED,
		/** All handles are inactive. */
		OFF,
		/** All handles are active. */
		ALL;

		/**
		 * @return true if this Active state supports a specific shape index.
		 * @param scp
		 *            the panel we're checking against
		 * @param shapeIndex
		 *            the index of the shape we're checking
		 */
		public boolean supports(ShapeCreationPanel scp, int shapeIndex) {
			if (this == ALL)
				return true;
			if (this == OFF)
				return false;
			Selection s = scp.getSelectionModel().getSelection();
			return s.getShapeIndex() == shapeIndex;
		}

	};

	/**
	 * The client property key used to identify the <code>SelectionModel</code>.
	 * 
	 * @see #setSelectionModel(SelectionModel)
	 */
	public static final String SELECTION_MODEL_KEY = ShapeCreationPanel.class
			.getName() + ".selection-model";

	/**
	 * The client property key used to identify the <code>DataModel</code>.
	 * 
	 * @see #setDataModel(DataModel)
	 */
	public static final String DATA_MODEL_KEY = ShapeCreationPanel.class
			.getName() + ".data-model";

	/**
	 * The client property key used to identify the <code>AffineTransform</code>
	 * .
	 * 
	 * @see #setTransform(AffineTransform)
	 */
	public static final String TRANSFORM_KEY = ShapeCreationPanel.class
			.getName() + ".transform";

	/**
	 * The client property key used to identify the handle size.
	 * 
	 * @see #setHandleSize(int)
	 */
	public static final String HANDLE_SIZE_KEY = ShapeCreationPanel.class
			.getName() + ".handle-size-key";

	/**
	 * The client property key used to identify which handles are active.
	 * 
	 * @see #setHandlesActive(Active)
	 */
	public static final String HANDLES_ACTIVE_KEY = ShapeCreationPanel.class
			.getName() + ".handle-active";

	/**
	 * The client property key used to identify the mode.
	 * 
	 * @see #setMode(String)
	 */
	public static final String MODE_KEY = ShapeCreationPanel.class.getName()
			+ ".mode";

	/**
	 * This is default mode for this panel. In this mode shapes can usually be
	 * selected, translated, and deleted: but not created.
	 * 
	 * @see #getMode()
	 */
	public static final String MODE_DEFAULT = "default";

	/**
	 * This is the mode used exclusively for creating shapes. In this mode when
	 * the user clicks in this panel: new shapes are added to the data model.
	 * 
	 * @see #getMode()
	 */
	public static final String MODE_CREATE = "create";

	/**
	 * A small on-screen handle. Note each <code>ShapePanelUI</code> renders and
	 * interacts with these controls differently. (Some UIs may not support any
	 * handle at all, others may only support the PRIMARY handle.)
	 * 
	 * @see #getHandleSize()
	 */
	public static enum Handle {
		/** This is the handle for the node itself. */
		PRIMARY,
		/**
		 * If this node represents cubic bezier data, this optional handle
		 * represents the previous control point.
		 */
		PREVIOUS_CONTROL,
		/**
		 * If this node represents cubic bezier data, this optional handle
		 * represents the next control point.
		 */
		NEXT_CONTROL;
	}

	/**
	 * A listener for the {@link SelectionModel}.
	 */
	public static interface SelectionModelListener {
		/**
		 * Notification that the selection has changed.
		 * 
		 * @param shapePanel
		 *            the shape panel whose selection changed.
		 * @param oldSelection
		 *            the previous selection
		 * @param newSelection
		 *            the new selection
		 */
		public void selectionChanged(ShapeCreationPanel shapePanel,
				Selection oldSelection, Selection newSelection);

		/**
		 * Notification that the indication has changed.
		 * 
		 * @param shapePanel
		 *            the shape panel whose selection changed.
		 * @param oldIndication
		 *            the previous indication
		 * @param newIndication
		 *            the new indication
		 */
		public void indicationChanged(ShapeCreationPanel shapePanel,
				Selection oldIndication, Selection newIndication);
	}

	/**
	 * Data used to articulate exactly what the user has selected (or
	 * indicated). Not all UIs will support identifying all the information
	 * available in this class.
	 */
	public static class Selection {
		final int shapeIndex, nodeIndex;
		final Handle handle;

		/**
		 * Create an empty selection. This is equivalent to
		 * <code>new Selection(-1, -1, null)</code>.
		 */
		public Selection() {
			this(-1, -1, null);
		}

		/**
		 * Create a new Selection.
		 * 
		 * @param shapeIndex
		 *            the shape index being interacted with, or -1 if no shape
		 *            is involved.
		 * @param nodeIndex
		 *            the node index being interacted with, or -1 if no node is
		 *            involved.
		 * @param handle
		 *            the handle being interacted with. If null this is reset to
		 *            PRIMARY, although it is not consulted if the nodeIndex is
		 *            -1.
		 */
		public Selection(int shapeIndex, int nodeIndex, Handle handle) {
			if (handle == null)
				handle = Handle.PRIMARY;
			this.shapeIndex = shapeIndex;
			this.nodeIndex = nodeIndex;
			this.handle = handle;
		}

		/**
		 * @return the shape index (relative to the {@link DataModel}.
		 */
		public int getShapeIndex() {
			return shapeIndex;
		}

		/**
		 * @return the node index (relative to the shape index, which is relative
		 *         to the {@link DataModel}).
		 */
		public int getNodeIndex() {
			return nodeIndex;
		}

		/**
		 * @return the handle (relative to the node index, which is relative to
		 *         the shape index).
		 */
		public Handle getHandle() {
			return handle;
		}

		@Override
		public int hashCode() {
			return (shapeIndex << 8) + nodeIndex;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Selection))
				return false;
			Selection other = (Selection) obj;
			if (other.shapeIndex != shapeIndex)
				return false;
			if (other.nodeIndex != nodeIndex)
				return false;
			if (!other.handle.equals(handle))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "Selection[ shapeIndex=" + shapeIndex + ", nodeIndex="
					+ nodeIndex + ", handle=" + handle + "]";
		}
	}

	/**
	 * This manages the selection and indication of a {@link ShapeCreationPanel}
	 * .
	 * <p>
	 * The "selection" probably needs to explanation, but "indication" is a less
	 * universal concept. Also known as "rollover" or other possible names, Jef
	 * Raskin defines indication as, "highlighting the single object pointed to
	 * as the cursor is moved, without any other user action such as clicking."
	 *
	 */
	public static class SelectionModel {
		Selection selection = new Selection();
		Selection indication = new Selection();

		List<SelectionModelListener> listeners = new ArrayList<SelectionModelListener>();

		ShapeCreationPanel shapePanel;

		/**
		 * Create a selection model for a {@link ShapeCreationPanel}.
		 * 
		 * @param shapePanel
		 *            the panel this model relates to.
		 */
		public SelectionModel(ShapeCreationPanel shapePanel) {
			this.shapePanel = shapePanel;
		}

		/**
		 * Add a listener to this model.
		 * 
		 * @param listener
		 *            the listener to add.
		 */
		public void addListener(SelectionModelListener listener) {
			if (!listeners.contains(listener))
				listeners.add(listener);
		}

		/**
		 * Remove a listener to this model.
		 * 
		 * @param listener
		 *            the listener to remove.
		 */
		public void removeListener(SelectionModelListener listener) {
			listeners.remove(listener);
		}

		/** @return the current selection. */
		public Selection getSelection() {
			return selection;
		}

		/**
		 * @return the current indication.
		 *         <p>
		 *         The "selection" probably needs no explanation, but
		 *         "indication" is a less universal concept. Also known as
		 *         "rollover" or other possible names, Jef Raskin defines
		 *         indication as, "highlighting the single object pointed to as
		 *         the cursor is moved, without any other user action such as
		 *         clicking."
		 */
		public Selection getIndication() {
			return indication;
		}

		/**
		 * Change the current selection, and notify listeners if a change
		 * occurred.
		 * 
		 * @param newSelection
		 *            the new selection
		 */
		public void select(Selection newSelection) {
			if (newSelection == null)
				throw new NullPointerException();
			if (!newSelection.equals(selection)) {
				Selection oldSelection = selection;
				selection = newSelection;
				for (SelectionModelListener l : listeners) {
					try {
						l.selectionChanged(shapePanel, oldSelection,
								newSelection);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		/**
		 * Change the current indication, and notify listeners if a change
		 * occurred.
		 * 
		 * @param newIndication
		 *            the new indication
		 */
		public void indicate(Selection newIndication) {
			if (newIndication == null)
				throw new NullPointerException();
			if (!newIndication.equals(indication)) {
				Selection oldIndication = indication;
				indication = newIndication;
				for (SelectionModelListener l : listeners) {
					try {
						l.indicationChanged(shapePanel, oldIndication,
								newIndication);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		/**
		 * Change the current selection, and notify listeners if a change
		 * occurred.
		 * 
		 * @param shapeIndex
		 *            the index of the shape to select
		 * @param nodeIndex
		 *            the index of the node within the shape to select
		 * @param handle
		 *            the handle (of the node of the shape) to select.
		 */
		public void select(int shapeIndex, int nodeIndex, Handle handle) {
			Selection newSelection = new Selection(shapeIndex, nodeIndex,
					handle);
			select(newSelection);
		}

		/**
		 * Change the current indication, and notify listeners if a change
		 * occurred.
		 * 
		 * @param shapeIndex
		 *            the index of the shape to indicate
		 * @param nodeIndex
		 *            the index of the node within the shape to indicate
		 * @param handle
		 *            the handle (of the node of the shape) to indicate.
		 */
		public void indicate(int shapeIndex, int nodeIndex, Handle handle) {
			Selection newIndication = new Selection(shapeIndex, nodeIndex,
					handle);
			indicate(newIndication);
		}
	}

	/**
	 * A simple wrapper for a shape that makes it immutable.
	 * <p>
	 * (That is: if we simply stored data as a GeneralPath then other entities
	 * may invoke <code>gpath.reset()</code>, or other methods to modify the
	 * shape data.)
	 */
	protected static class ImmutableShape implements Shape {
		Shape shape;

		public ImmutableShape(Shape shape) {
			if (shape == null)
				throw new NullPointerException();
			this.shape = shape;
		}

		public Rectangle getBounds() {
			return shape.getBounds();
		}

		public Rectangle2D getBounds2D() {
			return shape.getBounds2D();
		}

		public boolean contains(double x, double y) {
			return shape.contains(x, y);
		}

		public boolean contains(Point2D p) {
			return shape.contains(p);
		}

		public boolean intersects(double x, double y, double w, double h) {
			return shape.intersects(x, y, w, h);
		}

		public boolean intersects(Rectangle2D r) {
			return shape.intersects(r);
		}

		public boolean contains(double x, double y, double w, double h) {
			return shape.contains(x, y, w, h);
		}

		public boolean contains(Rectangle2D r) {
			return shape.contains(r);
		}

		public PathIterator getPathIterator(AffineTransform at) {
			return shape.getPathIterator(at);
		}

		public PathIterator getPathIterator(AffineTransform at, double flatness) {
			return shape.getPathIterator(at, flatness);
		}
	}

	/**
	 * A listener for the {@link DataModel}.
	 */
	public interface DataModelListener {
		/**
		 * Notification that a shape was added.
		 * 
		 * @param shapePanel
		 *            the affected panel.
		 * @param shapeIndex
		 *            the index of the shape that was added.
		 * @param shape
		 *            the shape that was added.
		 */
		public void shapeAdded(ShapeCreationPanel shapePanel, int shapeIndex,
				Shape shape);

		/**
		 * Notification that a shape was removed.
		 * 
		 * @param shapePanel
		 *            the affected panel.
		 * @param shapeIndex
		 *            the index of the shape that was removed.
		 * @param shape
		 *            the shape that was removed.
		 */
		public void shapeRemoved(ShapeCreationPanel shapePanel, int shapeIndex,
				Shape shape);

		/**
		 * Notification that a shape was changed.
		 * 
		 * @param shapePanel
		 *            the affected panel.
		 * @param shapeIndex
		 *            the index of the shape that was changed.
		 * @param shape
		 *            the shape that was changed.
		 */
		public void shapeChanged(ShapeCreationPanel shapePanel, int shapeIndex,
				Shape shape);
	}

	/**
	 * The data a {@link ShapeCreationPanel} represents. Specifically this is a
	 * list of shapes. The shapes should generally be considered immutable.
	 * Instead of changing a shape: simply replace it.
	 */
	public static class DataModel {
		List<ImmutableShape> shapes = new ArrayList<ImmutableShape>();
		List<DataModelListener> listeners = new ArrayList<DataModelListener>();
		ShapeCreationPanel shapePanel;

		/**
		 * Create a new DataModel.
		 * 
		 * @param shapePanel
		 *            the related panel.
		 */
		public DataModel(ShapeCreationPanel shapePanel) {
			this.shapePanel = shapePanel;
		}

		/**
		 * Add a listener to be notified when this data model changes.
		 * 
		 * @param listener
		 *            the listener to add.
		 */
		public void addListener(DataModelListener listener) {
			if (!listeners.contains(listener))
				listeners.add(listener);
		}

		/**
		 * Remove a listener.
		 * 
		 * @param listener
		 *            the listener to remove.
		 */
		public void removeListener(DataModelListener listener) {
			listeners.remove(listener);
		}

		/**
		 * Add a shape to this model.
		 * 
		 * @param shape
		 *            the new shape to add.
		 * @return the index of the newly added shape.
		 */
		public int addShape(Shape shape) {
			if (shape == null)
				throw new NullPointerException();
			if (!ShapeUtils.isValid(shape)) {
				System.err.println(ShapeStringUtils.toString(shape));
				throw new IllegalArgumentException();
			}
			ImmutableShape clonedShape = new ImmutableShape(shape);
			shapes.add(clonedShape);
			int index = shapes.size() - 1;
			for (DataModelListener l : listeners) {
				try {
					l.shapeAdded(shapePanel, index, clonedShape);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return index;
		}

		/**
		 * Return an array of all shapes.
		 * 
		 * @return an array of all current shapes.
		 */
		public Shape[] getShapes() {
			Shape[] array = new Shape[getShapeCount()];
			for (int a = 0; a < array.length; a++) {
				array[a] = getShape(a);
			}
			return array;
		}

		/**
		 * Individually removes all existing shapes and adds each incoming
		 * shape; this may trigger several dozen listener notifications.
		 * 
		 * @param shapes
		 */
		public void setShapes(Shape[] shapes) {
			for (int a = 0; a < shapes.length; a++) {
				if (!ShapeUtils.isValid(shapes[a])) {
					System.err.println(ShapeStringUtils.toString(shapes[a]));
					throw new IllegalArgumentException("index = " + a);
				}
			}
			for (int a = this.shapes.size() - 1; a >= 0; a--) {
				removeShape(a);
			}
			for (int a = 0; a < shapes.length; a++) {
				addShape(shapes[a]);
			}
		}

		/**
		 * Replace an existing shape in this model.
		 * 
		 * @param shapeIndex
		 *            the index of the shape to replace
		 * @param newShape
		 *            the new shape
		 */
		public void setShape(int shapeIndex, Shape newShape) {
			if (!ShapeUtils.isValid(newShape)) {
				System.err.println(ShapeStringUtils.toString(newShape));
				throw new IllegalArgumentException();
			}
			ImmutableShape clonedShape = new ImmutableShape(newShape);
			shapes.set(shapeIndex, clonedShape);
			for (DataModelListener l : listeners) {
				try {
					l.shapeChanged(shapePanel, shapeIndex, clonedShape);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * @param shapeIndex
		 *            the index of the shape to retrieve.
		 * @return a shape in this model.
		 */
		public Shape getShape(int shapeIndex) {
			return shapes.get(shapeIndex);
		}

		/** @return the number of shapes in this model. */
		public int getShapeCount() {
			return shapes.size();
		}

		/**
		 * Remove a shape from this model.
		 * 
		 * @param shapeIndex
		 *            the index of the shape to remove.
		 */
		public void removeShape(int shapeIndex) {
			Shape removedShape = shapes.remove(shapeIndex);
			for (DataModelListener l : listeners) {
				try {
					l.shapeRemoved(shapePanel, shapeIndex, removedShape);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public ShapeCreationPanel() {
		setSelectionModel(new SelectionModel(this));
		setDataModel(new DataModel(this));
		updateUI();
	}

	/**
	 * @return the {@link SelectionModel} used to monitor the selection and
	 *         indication of this panel.
	 * 
	 * @see #setSelectionModel(SelectionModel)
	 */
	public SelectionModel getSelectionModel() {
		return (SelectionModel) getClientProperty(SELECTION_MODEL_KEY);
	}

	/**
	 * Assign the {@link SelectionModel} for this panel.
	 * 
	 * @param selectionModel
	 *            the new selection model for this panel.
	 * @see #getSelectionModel()
	 */
	public void setSelectionModel(SelectionModel selectionModel) {
		if (selectionModel == null)
			throw new NullPointerException();
		putClientProperty(SELECTION_MODEL_KEY, selectionModel);
	}

	/**
	 * @return the {@link DataModel} that stores all the shape data in this
	 *         panel.
	 * 
	 * @see #setDataModel(DataModel)
	 */
	public DataModel getDataModel() {
		return (DataModel) getClientProperty(DATA_MODEL_KEY);
	}

	/**
	 * Assign the {@link DataModel} for this panel.
	 * 
	 * @param dataModel
	 *            the new DataModel for this panel.
	 * @see #getDataModel()
	 */
	public void setDataModel(DataModel dataModel) {
		if (dataModel == null)
			throw new NullPointerException();
		putClientProperty(DATA_MODEL_KEY, dataModel);
	}

	@Override
	public String getUIClassID() {
		return uiClassID;
	}

	/**
	 * @return the transform these shapes are rendered through.
	 * 
	 * @see #setTransform(AffineTransform)
	 */
	public AffineTransform getTransform() {
		AffineTransform t = (AffineTransform) getClientProperty(TRANSFORM_KEY);
		if (t == null)
			return new AffineTransform();
		return new AffineTransform(t);
	}

	/**
	 * Assign the transform these shapes are rendered through.
	 * 
	 * @param transform
	 *            the new transform these shapes are rendered through.
	 * @see #getTransform()
	 */
	public void setTransform(AffineTransform transform) {
		if (transform == null)
			transform = new AffineTransform();
		putClientProperty(TRANSFORM_KEY, new AffineTransform(transform));
	}

	/**
	 * @return the size of handles (in pixels). This is not affected by the
	 *         transform.
	 * 
	 * @see #setHandleSize(int)
	 */
	public int getHandleSize() {
		Integer i = (Integer) getClientProperty(HANDLE_SIZE_KEY);
		if (i == null)
			return 10;
		return i;
	}

	/**
	 * Assign the size of handles (in pixels).
	 * 
	 * @param i
	 *            the new handle size.
	 * 
	 * @see #getHandleSize()
	 */
	public void setHandleSize(int i) {
		// if your goal is to turn off handles, use:
		// setHandlesActive(Active.OFF)
		if (i <= 0)
			throw new IllegalArgumentException("handleSize must be positive");
		putClientProperty(HANDLE_SIZE_KEY, new Integer(i));
	}

	/**
	 * @return the Active property describing when handles should be active.
	 * 
	 * @see #setHandlesActive(Active)
	 */
	public Active getHandlesActive() {
		Active value = (Active) getClientProperty(HANDLES_ACTIVE_KEY);
		if (value == null)
			return Active.SELECTED;
		return value;
	}

	/**
	 * Control which handles should be active.
	 * 
	 * @param a
	 *            the new state of the handles
	 * @see #getHandlesActive()
	 */
	public void setHandlesActive(Active a) {
		putClientProperty(HANDLES_ACTIVE_KEY, a);
	}

	/**
	 * @return the current mode of this panel.
	 * @see #MODE_DEFAULT
	 * @see #MODE_CREATE
	 */
	public String getMode() {
		String value = (String) getClientProperty(MODE_KEY);
		if (value == null)
			return MODE_DEFAULT;
		return value;
	}

	/**
	 * Assign the current mode of this panel.
	 * 
	 * @param mode
	 *            the new mode of this panel
	 * @see #MODE_DEFAULT
	 * @see #MODE_CREATE
	 */
	public void setMode(String mode) {
		if (mode == null)
			mode = MODE_DEFAULT;
		putClientProperty(MODE_KEY, mode);
	}

	@Override
	public void updateUI() {
		if (UIManager.getDefaults().get(uiClassID) == null) {
			UIManager.getDefaults().put(uiClassID,
					"com.pump.plaf.CubicPathCreationUI");
		}
		setUI((ShapeCreationUI) UIManager.getUI(this));
	}

	public void setUI(ShapeCreationUI ui) {
		super.setUI(ui);
	}

	public ShapeCreationUI getUI() {
		return (ShapeCreationUI) ui;
	}
}