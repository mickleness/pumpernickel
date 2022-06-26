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
package com.pump.plaf;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.plaf.ComponentUI;

import com.pump.geom.CubicPath;
import com.pump.geom.ShapeUtils;
import com.pump.swing.ShapeCreationPanel;
import com.pump.swing.ShapeCreationPanel.DataModel;
import com.pump.swing.ShapeCreationPanel.DataModelListener;
import com.pump.swing.ShapeCreationPanel.Selection;
import com.pump.swing.ShapeCreationPanel.SelectionModel;
import com.pump.swing.ShapeCreationPanel.SelectionModelListener;

/**
 * A UI for a {@link ShapeCreationPanel}.
 * <p>
 * This abstract superclass offers a few common functions, but subclasses most
 * implement specific <code>MouseListeners</code> and implementation of each
 * handle.
 * 
 */
public abstract class ShapeCreationUI extends ComponentUI {

	FocusListener focusListener = new FocusListener() {

		public void focusGained(FocusEvent e) {
			e.getComponent().repaint();
		}

		public void focusLost(FocusEvent e) {
			e.getComponent().repaint();
		}

	};

	AbstractAction deleteAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			ShapeCreationPanel scp = (ShapeCreationPanel) e.getSource();
			if (scp.getUI() == ShapeCreationUI.this) {
				deleteSelection(scp);
			}
		}
	};

	AbstractAction nudgeLeftAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			ShapeCreationPanel scp = (ShapeCreationPanel) e.getSource();
			if (scp.getUI() == ShapeCreationUI.this) {
				nudge(scp, -1, 0);
			}
		}
	};

	AbstractAction nudgeRightAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			ShapeCreationPanel scp = (ShapeCreationPanel) e.getSource();
			if (scp.getUI() == ShapeCreationUI.this) {
				nudge(scp, 1, 0);
			}
		}
	};

	AbstractAction nudgeUpAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			ShapeCreationPanel scp = (ShapeCreationPanel) e.getSource();
			if (scp.getUI() == ShapeCreationUI.this) {
				nudge(scp, 0, -1);
			}
		}
	};

	AbstractAction nudgeDownAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			ShapeCreationPanel scp = (ShapeCreationPanel) e.getSource();
			if (scp.getUI() == ShapeCreationUI.this) {
				nudge(scp, 0, 1);
			}
		}
	};

	SelectionModelListener selectionModelListener = new SelectionModelListener() {

		public void selectionChanged(ShapeCreationPanel shapePanel,
				Selection oldSelection, Selection newSelection) {
			updateCursor(shapePanel);
			shapePanel.repaint();
		}

		public void indicationChanged(ShapeCreationPanel shapePanel,
				Selection oldIndication, Selection newIndication) {
			updateCursor(shapePanel);
			shapePanel.repaint();
		}
	};

	/**
	 * Update the cursor by calling <code>shapePanel.setCursor(..)</code>.
	 */
	protected void updateCursor(ShapeCreationPanel shapePanel) {
		if (ShapeCreationPanel.MODE_DEFAULT.equals(shapePanel.getMode())) {
			Selection indication = shapePanel.getSelectionModel()
					.getIndication();
			if (indication.getShapeIndex() == -1) {
				shapePanel.setCursor(Cursor.getDefaultCursor());
			} else if (indication.getNodeIndex() == -1) {
				shapePanel.setCursor(Cursor
						.getPredefinedCursor(Cursor.HAND_CURSOR));
			} else {
				shapePanel.setCursor(Cursor
						.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			}
		} else {
			shapePanel.setCursor(Cursor
					.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		}
	}

	DataModelListener dataModelListener = new DataModelListener() {

		public void shapeAdded(ShapeCreationPanel shapePanel, int shapeIndex,
				Shape shape) {
			shapePanel.repaint();
		}

		public void shapeRemoved(ShapeCreationPanel shapePanel, int shapeIndex,
				Shape shape) {
			shapePanel.repaint();
		}

		public void shapeChanged(ShapeCreationPanel shapePanel, int shapeIndex,
				Shape shape) {
			shapePanel.repaint();
		}

	};

	PropertyChangeListener propertyListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			ShapeCreationPanel scp = (ShapeCreationPanel) evt.getSource();
			if (ShapeCreationPanel.TRANSFORM_KEY.equals(evt.getPropertyName())) {
				scp.repaint();
			} else if (ShapeCreationPanel.HANDLE_SIZE_KEY.equals(evt
					.getPropertyName())) {
				scp.repaint();
			} else if (ShapeCreationPanel.HANDLES_ACTIVE_KEY.equals(evt
					.getPropertyName())) {
				scp.repaint();
			} else if (ShapeCreationPanel.MODE_KEY
					.equals(evt.getPropertyName())) {
				scp.repaint();
			} else if (ShapeCreationPanel.DATA_MODEL_KEY.equals(evt
					.getPropertyName())) {
				DataModel oldModel = (DataModel) evt.getOldValue();
				DataModel newModel = (DataModel) evt.getNewValue();
				if (oldModel != null)
					oldModel.removeListener(dataModelListener);
				if (newModel != null)
					newModel.addListener(dataModelListener);
			} else if (ShapeCreationPanel.SELECTION_MODEL_KEY.equals(evt
					.getPropertyName())) {
				SelectionModel oldModel = (SelectionModel) evt.getOldValue();
				SelectionModel newModel = (SelectionModel) evt.getNewValue();
				if (oldModel != null)
					oldModel.removeListener(selectionModelListener);
				if (newModel != null)
					newModel.addListener(selectionModelListener);
			}
			updateCursor(scp);
		}
	};

	/**
	 * Delete the current selection.
	 */
	protected void deleteSelection(ShapeCreationPanel scp) {
		Selection s = scp.getSelectionModel().getSelection();
		if (s.getShapeIndex() == -1) {
			return;
		}
		if (s.getNodeIndex() == -1) {
			scp.getDataModel().removeShape(s.getShapeIndex());
			scp.getSelectionModel().select(-1, -1, null);
			scp.getSelectionModel().indicate(-1, -1, null);
			return;
		}

		Shape oldPath = scp.getDataModel().getShape(s.getShapeIndex());
		CubicPath newPath = new CubicPath();
		newPath.append(oldPath);
		newPath.removeNode(s.getNodeIndex());
		scp.getDataModel().setShape(s.getShapeIndex(), validate(newPath));
		scp.getSelectionModel().select(s.getShapeIndex(), -1, null);
		scp.getSelectionModel().indicate(-1, -1, null);
	}

	/**
	 * This converts the argument to a shape compatible with this UI.
	 * <p>
	 * For example: the CurvedPolylineCreationUI only wants to work with
	 * CurvedPolylines. This may necessarily lose some shape data in making it
	 * conform to the expected format.
	 * <p>
	 * The default implementation simply returns the argument.
	 */
	protected Shape validate(Shape shape) {
		return shape;
	}

	/**
	 * Return a Selection for a shape. This takes into account
	 * <code>getStroke(scp, shapeIndex)</code> and should be consulted after
	 * possible matches for handles have been ruled out.
	 * 
	 * @param mouseLoc
	 *            an un-transformed mouse location (that is: this comes directly
	 *            from a MouseEvent and is relative to the ShapeCreationPanel's
	 *            coordinates).
	 */
	protected Selection getSelectedShape(ShapeCreationPanel scp,
			Point2D mouseLoc) {
		Shape[] shapes = scp.getDataModel().getShapes();
		for (int shapeIndex = shapes.length - 1; shapeIndex >= 0; shapeIndex--) {
			Stroke stroke = getStroke(scp, shapeIndex);
			GeneralPath transformedShape = new GeneralPath();
			transformedShape.append(shapes[shapeIndex], false);
			transformedShape.transform(scp.getTransform());

			// If we invoke stroke.createStrokedShape on an invalid
			// shape the JVM might crash. The data model already put safeguards
			// in to help prevent this condition, but since this involves
			// crashing let's be doubly safe:
			if (!ShapeUtils.isValid(transformedShape)) {
				continue;
			}
			Shape strokedShape = stroke.createStrokedShape(transformedShape);
			if (strokedShape.intersects(mouseLoc.getX() - .5,
					mouseLoc.getY() - .5, 1, 1)) {
				return new Selection(shapeIndex, -1, null);
			}
		}
		return new Selection();
	}

	/** Nudge the selection a certain amount of pixels. */
	protected void nudge(ShapeCreationPanel scp, double dx, double dy) {
		Selection s = scp.getSelectionModel().getSelection();
		if (s.getShapeIndex() == -1) {
			return;
		}

		// process the shapepanel's transform:
		try {
			AffineTransform tx = scp.getTransform().createInverse();
			;
			double newDX = dx * tx.getScaleX() + tx.getShearX();
			double newDY = dy * tx.getScaleY() + tx.getShearY();
			dx = newDX;
			dy = newDY;
		} catch (Exception e) {
			e.printStackTrace();
		}
		AffineTransform change = AffineTransform.getTranslateInstance(dx, dy);

		if (s.getNodeIndex() == -1) {
			Shape oldPath = scp.getDataModel().getShape(s.getShapeIndex());
			GeneralPath newPath = new GeneralPath();
			newPath.append(oldPath, false);
			newPath.transform(change);
			scp.getDataModel().setShape(s.getShapeIndex(), validate(newPath));
			return;
		}

		// TODO: process node handles separately
		Shape oldPath = scp.getDataModel().getShape(s.getShapeIndex());
		CubicPath newPath = new CubicPath();
		newPath.append(oldPath);
		Point2D p = newPath.getNode(s.getNodeIndex(), null);
		change.transform(p, p);
		newPath.setNode(s.getNodeIndex(), p, true);
		scp.getDataModel().setShape(s.getShapeIndex(), validate(newPath));
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		ShapeCreationPanel scp = (ShapeCreationPanel) c;

		scp.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
				"delete");
		scp.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "delete");
		scp.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0),
				"nudgeLeft");
		scp.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0),
				"nudgeRight");
		scp.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
				"nudgeUp");
		scp.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
				"nudgeDown");
		scp.getActionMap().put("delete", deleteAction);
		scp.getActionMap().put("nudgeLeft", nudgeLeftAction);
		scp.getActionMap().put("nudgeRight", nudgeRightAction);
		scp.getActionMap().put("nudgeDown", nudgeDownAction);
		scp.getActionMap().put("nudgeUp", nudgeUpAction);
		scp.getDataModel().addListener(dataModelListener);
		scp.getSelectionModel().addListener(selectionModelListener);
		scp.addPropertyChangeListener(propertyListener);
		scp.addFocusListener(focusListener);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		ShapeCreationPanel scp = (ShapeCreationPanel) c;
		scp.getDataModel().removeListener(dataModelListener);
		scp.getSelectionModel().removeListener(selectionModelListener);
		scp.removePropertyChangeListener(propertyListener);
		scp.removeFocusListener(focusListener);
	}

	/**
	 * Invokes {@link #paintShapes(Graphics2D, ShapeCreationPanel)} and then
	 * {@link #paintControls(Graphics2D, ShapeCreationPanel)}
	 */
	@Override
	public void paint(Graphics g0, JComponent c) {
		ShapeCreationPanel scp = (ShapeCreationPanel) c;
		Graphics2D g = (Graphics2D) g0.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);
		paintShapes((Graphics2D) g, scp);
		paintControls((Graphics2D) g, scp);
		g.dispose();
	}

	/** Paints the shapes in the ShapeCreationPanel's DataModel. */
	protected void paintShapes(Graphics2D g, ShapeCreationPanel scp) {
		g = (Graphics2D) g.create();
		try {
			GeneralPath path = new GeneralPath();
			AffineTransform tx = scp.getTransform();
			Shape[] shapes = scp.getDataModel().getShapes();
			for (int a = 0; a < shapes.length; a++) {
				path.reset();
				path.append(shapes[a], false);
				path.transform(tx);
				Graphics2D g2 = (Graphics2D) g.create();
				try {
					paintShape(g2, scp, a, path);
				} finally {
					g2.dispose();
				}
			}
		} finally {
			g.dispose();
		}
	}

	/**
	 * Return the stroke used to paint a WritingStroke. This stroke is
	 * independent of the panel's AffineTransform. (So if the stroke is expected
	 * to scale with the transform, then this method needs to consult the
	 * AffineTransform and change accordingly.)
	 * 
	 * @see #paintShape(Graphics2D,ShapeCreationPanel,int,Shape)
	 */
	protected Stroke getStroke(ShapeCreationPanel panel, int shapeIndex) {
		return new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	}

	/**
	 * This paints an individual shape. Subclasses may override this to control
	 * the color/stroke of shapes.
	 * <p>
	 * The default implementation here paints a 2-pixel focus ring around a
	 * shape if it is the currently selected shape.
	 * 
	 * @see #getStroke(ShapeCreationPanel, int)
	 */
	protected void paintShape(Graphics2D g, ShapeCreationPanel panel,
			int shapeIndex, Shape transformedShape) {
		Selection selection = panel.getSelectionModel().getSelection();
		Stroke stroke = getStroke(panel, shapeIndex);
		if (selection.getShapeIndex() == shapeIndex && panel.hasFocus()) {
			Shape strokedShape = stroke.createStrokedShape(transformedShape);
			PlafPaintUtils.paintFocus(g, strokedShape, 2);
		}
		g.setStroke(stroke);
		g.setColor(Color.black);
		g.draw(transformedShape);
	}

	/**
	 * Paint handles/controls above the shape data.
	 * <p>
	 * When implementing this method: note that this is always invoked, and you
	 * need to consult the ShapeCreationPanel to determine when handles are
	 * supposed to be active.
	 */
	protected void paintControls(Graphics2D g, ShapeCreationPanel scp) {
	}
}