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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;

import com.pump.geom.CurvedPolyline;
import com.pump.swing.ShapeCreationPanel;
import com.pump.swing.ShapeCreationPanel.DataModel;
import com.pump.swing.ShapeCreationPanel.DataModelListener;
import com.pump.swing.ShapeCreationPanel.Selection;

/** A {@link ShapeCreationUI} to create a series of {@link CurvedPolyline} shapes.
 * 
 * @see com.bric.geom.CurvedPolyline
 */
public class CurvedPolylineCreationUI extends ShapeCreationUI {

	/** Maps to an array of CurvedPolylines for editing. */
	protected static final String SHAPES_MIRROR = CurvedPolylineCreationUI.class.getName()+".shapes-mirror";

	private static Set<Thread> changingDataModel = new HashSet<Thread>();
	
	PropertyChangeListener myPropertyListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			ShapeCreationPanel scp = (ShapeCreationPanel)evt.getSource();
			if( ShapeCreationPanel.MODE_KEY.equals(evt.getPropertyName()) ) {
				if( ShapeCreationPanel.MODE_CREATE.equals(evt.getNewValue()) ) {
					scp.getSelectionModel().select(-1, -1, null);
				} else if(ShapeCreationPanel.DATA_MODEL_KEY.equals(evt.getPropertyName())) {
					((DataModel)evt.getOldValue()).removeListener(myDataModelListener);
					invalidateMirror(scp);
					((DataModel)evt.getNewValue()).addListener(myDataModelListener);
				}
			};
		}
	};
	
	MouseInputAdapter mouseListener = new MouseInputAdapter() {
		int lastUntransformedX, lastUntransformedY;
		
		@Override
		public void mousePressed(MouseEvent evt) {
			ShapeCreationPanel scp = (ShapeCreationPanel)evt.getComponent();
			scp.requestFocus();
			lastUntransformedX = evt.getX();
			lastUntransformedY = evt.getY();
			try {
				AffineTransform tx = scp.getTransform().createInverse();
				Point2D mouseLoc = new Point2D.Float(evt.getX(), evt.getY());
				Point2D abstractLoc = tx.transform(mouseLoc, null);
				boolean isCreating = ShapeCreationPanel.MODE_CREATE.equals(scp.getMode());
				if(isCreating) {
					if(evt.getClickCount()>1) {
						scp.setMode(ShapeCreationPanel.MODE_DEFAULT);
						return;
					}
					
					int shapeIndex = scp.getSelectionModel().getSelection().getShapeIndex();
					if(shapeIndex==-1) {
						CurvedPolyline newShape = new CurvedPolyline();
						newShape.addPoint( (float)abstractLoc.getX(), (float)abstractLoc.getY() );
						shapeIndex = scp.getDataModel().addShape(newShape);
						scp.getSelectionModel().select(shapeIndex, 0, null);
					} else {
						CurvedPolyline currentShape = getMirror(scp)[shapeIndex];
						changingDataModel.add(Thread.currentThread());
						try {
							currentShape.setPoint( currentShape.getPointCount()-1,
									abstractLoc.getX(),
									abstractLoc.getY() );
							scp.getDataModel().setShape( shapeIndex, currentShape );
							scp.getSelectionModel().select(shapeIndex, currentShape.getPointCount()-1, null);
						} finally {
							changingDataModel.remove(Thread.currentThread());
						}
					}
				} else {
					Selection newSelection = getSelection(evt);
					scp.getSelectionModel().select(newSelection);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseDragged(MouseEvent evt) {
			ShapeCreationPanel scp = (ShapeCreationPanel)evt.getComponent();
			try {
				AffineTransform tx = scp.getTransform().createInverse();
				Point2D mouseLoc = new Point2D.Float(evt.getX(), evt.getY());
				Point2D abstractLoc = tx.transform(mouseLoc, null);

				boolean isCreating = ShapeCreationPanel.MODE_CREATE.equals(scp.getMode());
				if(!isCreating) {
					Selection selection = scp.getSelectionModel().getSelection();
					if(selection.getShapeIndex()!=-1) {
						if(selection.getNodeIndex()!=-1) {
							CurvedPolyline shape = getMirror(scp)[selection.getShapeIndex()];
							shape.setPoint(selection.getNodeIndex(), abstractLoc.getX(), abstractLoc.getY());
							scp.getDataModel().setShape(selection.getShapeIndex(), shape);
						} else {
							float dx = evt.getX() - lastUntransformedX;
							float dy = evt.getY() - lastUntransformedY;
							nudge(scp, dx, dy);
						}
						return;
					}
				}
				mouseMoved(evt);
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				lastUntransformedX = evt.getX();
				lastUntransformedY = evt.getY();
			}
		}

		@Override
		public void mouseMoved(MouseEvent evt) {
			ShapeCreationPanel scp = (ShapeCreationPanel)evt.getComponent();
			try {
				AffineTransform tx = scp.getTransform().createInverse();
				Point2D mouseLoc = new Point2D.Float(evt.getX(), evt.getY());
				Point2D abstractLoc = tx.transform(mouseLoc, null);
				boolean isCreating = ShapeCreationPanel.MODE_CREATE.equals(scp.getMode());
				int shapeIndex =  scp.getSelectionModel().getSelection().getShapeIndex();
				if(isCreating && shapeIndex>=0) {
					CurvedPolyline currentShape = getMirror(scp)[shapeIndex];
					
					int nodeIndex = scp.getSelectionModel().getSelection().getNodeIndex();

					changingDataModel.add(Thread.currentThread());
					try {
						if(nodeIndex==currentShape.getPointCount()-1) {
							currentShape.addPoint(
									abstractLoc.getX(),
									abstractLoc.getY() );
						} else {
							currentShape.setPoint( currentShape.getPointCount()-1,
									abstractLoc.getX(),
									abstractLoc.getY() );
						}
						
						scp.getDataModel().setShape( shapeIndex, currentShape );
					} finally {
						changingDataModel.remove(Thread.currentThread());
					}
				} else if(!isCreating) {
					Selection newIndication = getSelection(evt);
					scp.getSelectionModel().indicate(newIndication);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		private Selection getSelection(MouseEvent evt) throws NoninvertibleTransformException {
			ShapeCreationPanel scp = (ShapeCreationPanel)evt.getComponent();
			AffineTransform tx = scp.getTransform();
			double r = scp.getHandleSize()/2;
			Point2D mouseLoc = new Point2D.Float(evt.getX(), evt.getY());
			CurvedPolyline[] shapes = getMirror(scp);
			for(int shapeIndex = 0; shapeIndex<shapes.length; shapeIndex++) {
				if(scp.getHandlesActive().supports(scp, shapeIndex)) {
					for(int b = shapes[shapeIndex].getPointCount()-1; b>=0; b--) {
						Point2D p = tx.transform(shapes[shapeIndex].getPoint(b), null);
						Ellipse2D e = new Ellipse2D.Double(p.getX()-r, p.getY()-r, 2*r, 2*r);
						if(e.contains(mouseLoc))
							return new Selection(shapeIndex, b, null);
					}
				}
			}
			return getSelectedShape(scp, evt.getPoint());
		}
	};
	
	DataModelListener myDataModelListener = new DataModelListener() {

		public void shapeAdded(ShapeCreationPanel shapePanel, int shapeIndex,
				Shape shape) {
			invalidateMirror(shapePanel);
		}

		public void shapeRemoved(ShapeCreationPanel shapePanel, int shapeIndex,
				Shape shape) {
			invalidateMirror(shapePanel);
		}

		public void shapeChanged(ShapeCreationPanel shapePanel, int shapeIndex,
				Shape shape) {
			invalidateMirror(shapePanel);
		}
		
	};

	protected void invalidateMirror(ShapeCreationPanel scp) {
		if(!changingDataModel.contains(Thread.currentThread())) {
			scp.putClientProperty(SHAPES_MIRROR, null);
		}
	}
	
	@Override
	protected Shape validate(Shape shape) {
		if(shape instanceof CurvedPolyline)
			return shape;
		float[] coords = new float[6];
		CurvedPolyline copy = new CurvedPolyline();
		PathIterator iter = shape.getPathIterator(null);
		while(!iter.isDone()) {
			int k = iter.currentSegment(coords);
			if(k==PathIterator.SEG_MOVETO || k==PathIterator.SEG_LINETO) {
				copy.addPoint(coords[0], coords[1]);
			} else if(k==PathIterator.SEG_QUADTO) {
				copy.addPoint(coords[2], coords[3]);
			} else if(k==PathIterator.SEG_CUBICTO) {
				copy.addPoint(coords[4], coords[5]);
			}
			iter.next();
		}
		return copy;
	}
	
	protected CurvedPolyline[] getMirror(ShapeCreationPanel scp) {
		CurvedPolyline[] paths = (CurvedPolyline[])scp.getClientProperty( SHAPES_MIRROR );
		if(paths==null) {
			List<CurvedPolyline> mirrorList = new ArrayList<CurvedPolyline>();
			Shape[] dataModel = scp.getDataModel().getShapes();
			for(int a = 0; a<dataModel.length; a++) {
				mirrorList.add( (CurvedPolyline)validate(dataModel[a]) );
			}
			paths = mirrorList.toArray(new CurvedPolyline[mirrorList.size()]);
			scp.putClientProperty( SHAPES_MIRROR, paths );
		}
		return paths;
	}
	
	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		
		ShapeCreationPanel scp = (ShapeCreationPanel)c;
		scp.getDataModel().addListener(myDataModelListener);
		scp.addPropertyChangeListener(myPropertyListener);
		scp.addMouseListener(mouseListener);
		scp.addMouseMotionListener(mouseListener);
		invalidateMirror(scp);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);

		ShapeCreationPanel scp = (ShapeCreationPanel)c;
		scp.getDataModel().removeListener(myDataModelListener);
		scp.removePropertyChangeListener(myPropertyListener);
		scp.removeMouseListener(mouseListener);
		scp.removeMouseMotionListener(mouseListener);
	}

	@Override
	protected void paintControls(Graphics2D g0, ShapeCreationPanel scp) {
		AffineTransform tx = scp.getTransform();
		Graphics2D g = (Graphics2D)g0.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		float r = (float)( scp.getHandleSize() ) / 2f;
		Selection selection = scp.getSelectionModel().getSelection();
		Selection indication = scp.getSelectionModel().getIndication();
		Shape[] shapes = scp.getDataModel().getShapes();
		float[] coords = new float[6];
		Ellipse2D ellipse = new Ellipse2D.Float();
		for(int shapeIndex = 0; shapeIndex<shapes.length; shapeIndex++) {
			if(scp.getHandlesActive().supports(scp, shapeIndex)) {
				PathIterator iter = shapes[shapeIndex].getPathIterator(tx);
				int nodeCtr = -1;
				while(!iter.isDone()) {
					int k = iter.currentSegment(coords);
					float x, y;
					if(k==PathIterator.SEG_MOVETO || k==PathIterator.SEG_LINETO) {
						x = coords[0];
						y = coords[1];
						nodeCtr++;
					} else if(k==PathIterator.SEG_QUADTO) {
						x = coords[2];
						y = coords[3];
						nodeCtr++;
					} else if(k==PathIterator.SEG_CUBICTO) {
						x = coords[4];
						y = coords[5];
						nodeCtr++;
					} else if(k==PathIterator.SEG_CLOSE) {
						x = -1; y = -1;
					} else {
						throw new RuntimeException("unexpected segment type: "+k);
					}
					g.setColor(Color.white);
					if(selection!=null && selection.getShapeIndex()==shapeIndex && selection.getNodeIndex()==nodeCtr) {
						g.setColor(Color.darkGray);
					} else if(indication!=null && indication.getShapeIndex()==shapeIndex && indication.getNodeIndex()==nodeCtr) {
						g.setColor(Color.gray);
					}
					ellipse.setFrame(x - r, y - r, 2*r, 2*r);
					g.fill(ellipse);
					g.setColor(Color.black);
					g.draw(ellipse);
					
					iter.next();
				}
			}
		}
		g.dispose();
	}

}