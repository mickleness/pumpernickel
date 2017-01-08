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
package com.pump.plaf;

import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;

import com.pump.geom.BasicMouseSmoothing;
import com.pump.geom.MouseSmoothing;
import com.pump.swing.ShapeCreationPanel;
import com.pump.swing.ShapeCreationPanel.Selection;

/** A {@link ShapeCreationUI} to create a series of {@link BasicMouseSmoothing} shapes.
 * 
 * @see com.pump.geom.BasicMouseSmoothing
 */
public class MouseSmoothingShapeCreationUI extends ShapeCreationUI {
	
	MouseInputAdapter mouseListener = new MouseInputAdapter() {
		MouseSmoothing v;
		
		int lastUntransformedX, lastUntransformedY;

		@Override
		public void mousePressed(MouseEvent e) {
			try {
				ShapeCreationPanel scp = (ShapeCreationPanel)e.getComponent();
				scp.requestFocus();
				boolean isCreating = ShapeCreationPanel.MODE_CREATE.equals(scp.getMode());
				if(e.getClickCount()>1 && isCreating) {
					int i = scp.getDataModel().getShapeCount()-1;
					scp.getDataModel().removeShape(i);
					scp.setMode(ShapeCreationPanel.MODE_DEFAULT);
					e.consume();
					return;
				}
				if(isCreating) {
					v = new BasicMouseSmoothing();
					addPoint(v, e, true);
				} else {
					Selection selection = getSelectedShape(scp, e.getPoint());
					scp.getSelectionModel().select(selection);
				}
			} finally {
				lastUntransformedX = e.getX();
				lastUntransformedY = e.getY();
			}			
		}
		
		private void addPoint(MouseSmoothing v,MouseEvent e,boolean addShape) {
			ShapeCreationPanel scp = (ShapeCreationPanel)e.getComponent();
			Point2D p = new Point2D.Double(e.getX(), e.getY());
			try {
				scp.getTransform().createInverse().transform(p, p);
			} catch(NoninvertibleTransformException e2) {
				throw new RuntimeException(e2);
			}
			float x = (float)p.getX();
			float y = (float)p.getY();
			v.add(x, y, e.getWhen());
			if(addShape) {
				scp.getDataModel().addShape(v.getShape());
				int i = scp.getDataModel().getShapeCount()-1;
				scp.getSelectionModel().select(i, -1, null);
			} else {
				int i = scp.getDataModel().getShapeCount()-1;
				scp.getDataModel().setShape(i, v.getShape());
			}
			e.consume();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			ShapeCreationPanel scp = (ShapeCreationPanel)e.getComponent();
			if(ShapeCreationPanel.MODE_CREATE.equals(scp.getMode()) && v!=null) {
				addPoint(v, e, false);
				v = null;
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			ShapeCreationPanel scp = (ShapeCreationPanel)e.getComponent();
			try {
				if(ShapeCreationPanel.MODE_CREATE.equals(scp.getMode()) && v!=null) {
					addPoint(v, e, false);
				} else {
					float dx = e.getX() - lastUntransformedX;
					float dy = e.getY() - lastUntransformedY;
					nudge(scp, dx, dy);
				}
			} finally {
				lastUntransformedX = e.getX();
				lastUntransformedY = e.getY();
			}
		}
		
	};


	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		ShapeCreationPanel scp = (ShapeCreationPanel)c;
		scp.addMouseListener(mouseListener);
		scp.addMouseMotionListener(mouseListener);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		ShapeCreationPanel scp = (ShapeCreationPanel)c;
		scp.removeMouseListener(mouseListener);
		scp.removeMouseMotionListener(mouseListener);
	}
}