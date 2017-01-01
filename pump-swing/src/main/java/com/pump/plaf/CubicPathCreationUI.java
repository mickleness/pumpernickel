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
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ComponentUI;

import com.pump.geom.CubicPath;
import com.pump.swing.ShapeCreationPanel;
import com.pump.swing.ShapeCreationPanel.Active;
import com.pump.swing.ShapeCreationPanel.DataModel;
import com.pump.swing.ShapeCreationPanel.DataModelListener;
import com.pump.swing.ShapeCreationPanel.Handle;
import com.pump.swing.ShapeCreationPanel.Selection;


/** A {@link ShapeCreationUI} to create a series of {@link CubicPath} shapes.
 * @see com.bric.geom.CubicPath
 */
public class CubicPathCreationUI extends ShapeCreationUI {

    public static ComponentUI createUI(JComponent c) {
        return new CubicPathCreationUI();
    }

	public static final String CONSTRAINT_KEY = CubicPathCreationUI.class.getName()+".constraint";
	public static final String SCALE_FACTOR_KEY = CubicPathCreationUI.class.getName()+".scale-factor";
	
	/** Maps to an array of CubicPaths for editing. */
	protected static final String SHAPES_MIRROR = CubicPathCreationUI.class.getName()+".shapes-mirror";

	private static Set<Thread> changingDataModel = new HashSet<Thread>();
	
	public static enum Constraint {
		NONE, ANGLE_ONLY, ANGLE_AND_DISTANCE
	}

	public CubicPathCreationUI() {}

	MouseInputAdapter mouseListener = new MouseInputAdapter() {
		float clickX, clickY;
		int lastUntransformedX, lastUntransformedY;
		@Override
		public void mousePressed(MouseEvent e) {
			ShapeCreationPanel scp = (ShapeCreationPanel)e.getComponent();
			scp.requestFocus();
			boolean isCreating = ShapeCreationPanel.MODE_CREATE.equals(scp.getMode());
			if(e.getClickCount()>1 && isCreating) {
				scp.setMode(ShapeCreationPanel.MODE_DEFAULT);
				return;
			}
			
			lastUntransformedX = e.getX();
			lastUntransformedY = e.getY();
			Point2D p = new Point2D.Double(e.getX(), e.getY());
			try {
				scp.getTransform().createInverse().transform(p, p);
			} catch(NoninvertibleTransformException e2) {
				throw new RuntimeException(e2);
			}
			float x = (float)p.getX();
			float y = (float)p.getY();
			
			clickX = x;
			clickY = y;
			
			if(isCreating) {
				int selectedShape = scp.getSelectionModel().getSelection().getShapeIndex();
				if(selectedShape==-1) {
					GeneralPath path = new GeneralPath();
					path.moveTo(x, y);
					int newIndex = scp.getDataModel().addShape(path);
					scp.getSelectionModel().select(newIndex, 0, Handle.NEXT_CONTROL);
				} else {
					/** We didn't give a next control point to the *last* node yet.
					 * Now we do.
					 * If the last node in a path has a next control point: that path
					 * will be closed.
					 * 
					 */
					CubicPath[] paths = getCubicPaths(scp);
					CubicPath path = paths[selectedShape];
					int i = path.getNodeCount()-1;
					Point2D lastPoint = path.getNode(i, null);
					Point2D lastPointCtrlPoint = path.getPrevControlForNode(i, null);
					if(lastPointCtrlPoint==null) lastPointCtrlPoint = lastPoint;
					
					double dx = lastPoint.getX()-lastPointCtrlPoint.getX();
					double dy = lastPoint.getY()-lastPointCtrlPoint.getY();
					path.setNextControlForNode(i, new Point2D.Double( lastPoint.getX()+dx, lastPoint.getY()+dy ));
					
					path.lineTo(x, y);

					changingDataModel.add(Thread.currentThread());
					try {
						scp.getDataModel().setShape(selectedShape, path);
					} finally {
						changingDataModel.remove(Thread.currentThread());
					}
				}
			} else {
				Selection selection = getSelection(scp, e.getPoint());
				scp.getSelectionModel().select(selection);
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			mouseDragged(e);
		}
		
		/**
		 * 
		 * @param mouseLoc the mouse location, relative to the panel (pre-transform)
		 */
		private Selection getSelection(ShapeCreationPanel scp,Point2D mouseLoc) {
			AffineTransform tx = scp.getTransform();
			
			CubicPath[] paths = getCubicPaths(scp);
			Integer shapeIntersection = null;
			int r = scp.getHandleSize()/2;
			Active active = scp.getHandlesActive();
			for(int shapeIndex = paths.length-1; shapeIndex>=0; shapeIndex--) {
				if(active.supports(scp, shapeIndex)) {
					CubicPath path = paths[shapeIndex];
					for(int nodeIndex = 0; nodeIndex<path.getNodeCount(); nodeIndex++) {
						Point2D p2 = path.getPrevControlForNode(nodeIndex, null);
						if(p2!=null) {
							tx.transform(p2, p2);
							if(hit(scp, mouseLoc, p2)) {
								return new Selection(shapeIndex, nodeIndex, Handle.PREVIOUS_CONTROL);
							}
						}
						p2 = path.getNode(nodeIndex, null);
						if(p2!=null) {
							tx.transform(p2, p2);
							if(hit(scp, mouseLoc, p2)) {
								return new Selection(shapeIndex, nodeIndex, Handle.PRIMARY);
							}
						}
						p2 = path.getNextControlForNode(nodeIndex, null);
						if(p2!=null) {
							tx.transform(p2, p2);
							if(hit(scp, mouseLoc, p2)) {
								return new Selection(shapeIndex, nodeIndex, Handle.NEXT_CONTROL);
							}
						}
					}
					if(shapeIntersection==null) {
						Shape outline = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND).createStrokedShape(path);
						if(outline.intersects(mouseLoc.getX()-r,mouseLoc.getY()-r,2*r, 2*r)) {
							shapeIntersection = shapeIndex;
						}
					}
				}
			}
			
			return getSelectedShape(scp, mouseLoc);
		}
		
		@Override
		public void mouseMoved(MouseEvent e) {
			ShapeCreationPanel scp = (ShapeCreationPanel)e.getComponent();
			Selection indication = getSelection(scp, e.getPoint());
			scp.getSelectionModel().indicate(indication);
		}
		
		private boolean hit(ShapeCreationPanel scp,Point2D p1,Point2D p2) {
			double size = scp.getHandleSize();
			size /= 2.0;
			return (Math.abs(p1.getX()-p2.getX())<size && Math.abs(p2.getY()-p1.getY())<size);
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
			mouseMoved(e);
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
			ShapeCreationPanel scp = (ShapeCreationPanel)e.getComponent();
			scp.getSelectionModel().indicate(new Selection());
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			try {
				if(e.getClickCount()>1)
					return;
				
				ShapeCreationPanel scp = (ShapeCreationPanel)e.getComponent();
				boolean isCreating = ShapeCreationPanel.MODE_CREATE.equals(scp.getMode());
				CubicPath[] paths = getCubicPaths(scp);
				int i = scp.getSelectionModel().getSelection().getShapeIndex();
				CubicPath path = i==-1 ? null : paths[i];
	
				
				Point2D p = new Point2D.Double(e.getX(), e.getY());
				try {
					scp.getTransform().createInverse().transform(p, p);
				} catch(NoninvertibleTransformException e2) {
					throw new RuntimeException(e2);
				}
				float x = (float)p.getX();
				float y = (float)p.getY();
				boolean replacePath = true;
				
				if(isCreating) {
					int nodeIndex = path.getNodeCount()-1;
					float dx = x-clickX;
					float dy = y-clickY;
					path.setPrevControlForNode(nodeIndex, clickX-dx, clickY-dy);
				} else {
					Selection s = scp.getSelectionModel().getSelection();
					int nodeIndex = s.getNodeIndex();
					if(nodeIndex!=-1) {
						if( Handle.PREVIOUS_CONTROL.equals(s.getHandle()) ) {
							path.setPrevControlForNode(nodeIndex, new Point2D.Double(x, y));
							if(Constraint.ANGLE_ONLY.equals(getConstraint(scp)) ) {
								path.setNextControlForNodeFromPrev(nodeIndex, false);
							} else if(Constraint.ANGLE_AND_DISTANCE.equals(getConstraint(scp))) {
								path.setNextControlForNodeFromPrev(nodeIndex, true);
							}
						} else if( Handle.PRIMARY.equals(s.getHandle()) ) {
							path.setNode(nodeIndex, new Point2D.Double(x, y), true);
						} else { //next control:
							path.setNextControlForNode(nodeIndex, new Point2D.Double(x, y));
							if(Constraint.ANGLE_ONLY.equals(getConstraint(scp))) {
								path.setPrevControlForNodeFromNext(nodeIndex, false);
							} else if(Constraint.ANGLE_AND_DISTANCE.equals(getConstraint(scp))) {
								path.setPrevControlForNodeFromNext(nodeIndex, true);
							}
						}
					} else {
						float dx = e.getX() - lastUntransformedX;
						float dy = e.getY() - lastUntransformedY;
						nudge(scp, dx, dy);
						replacePath = false;
					}
				}
	
	
				if(i>=0 && replacePath) {
					changingDataModel.add(Thread.currentThread());
					try {
						scp.getDataModel().setShape(i, path);
					} finally {
						changingDataModel.remove(Thread.currentThread());
					}
				}
			} finally {
				lastUntransformedX = e.getX();
				lastUntransformedY = e.getY();
			}
		}
	};

	public Constraint getConstraint(ShapeCreationPanel scp) {
		Constraint value = (Constraint)scp.getClientProperty(CONSTRAINT_KEY);
		if(value==null) return Constraint.NONE;
		return value;
	}
	
	public void setConstraint(ShapeCreationPanel scp,Constraint c) {
		if(c==null) c = Constraint.NONE;
		scp.putClientProperty(CONSTRAINT_KEY, c);
	}	


	public double getScaleFactor(ShapeCreationPanel scp) {
		Number value = (Number)scp.getClientProperty(SCALE_FACTOR_KEY);
		if(value==null) return 1;
		return value.doubleValue();
	}
	
	public void setScaleFactor(ShapeCreationPanel scp,double d) {
		scp.putClientProperty( SCALE_FACTOR_KEY, new Double(d) );
	}
	
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
	
	PropertyChangeListener myPropertyListener = new PropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent evt) {
			ShapeCreationPanel scp = (ShapeCreationPanel)evt.getSource();
			if(SCALE_FACTOR_KEY.equals(evt.getPropertyName())) {
				refreshScaleFactor(scp);
				scp.repaint();
			} else if(CONSTRAINT_KEY.equals(evt.getPropertyName())) {
				scp.repaint();
			} else if(ShapeCreationPanel.DATA_MODEL_KEY.equals(evt.getPropertyName())) {
				((DataModel)evt.getOldValue()).removeListener(myDataModelListener);
				invalidateMirror(scp);
				((DataModel)evt.getNewValue()).addListener(myDataModelListener);
			} else if(ShapeCreationPanel.MODE_KEY.equals(evt.getPropertyName())) {
				if( ShapeCreationPanel.MODE_CREATE.equals(evt.getNewValue())) {
					scp.getSelectionModel().select(-1, -1, null);
				}
				updateCursor(scp);
			}
		}
		
	};

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		ShapeCreationPanel scp = (ShapeCreationPanel)c;
		scp.getDataModel().addListener(myDataModelListener);
		scp.addMouseListener(mouseListener);
		scp.addMouseMotionListener(mouseListener);
		scp.addPropertyChangeListener(myPropertyListener);
		invalidateMirror(scp);
	}

	protected void invalidateMirror(ShapeCreationPanel scp) {
		if(!changingDataModel.contains(Thread.currentThread())) {
			scp.putClientProperty(SHAPES_MIRROR, null);
		}
	}
	
	protected CubicPath[] getCubicPaths(ShapeCreationPanel scp) {
		CubicPath[] paths = (CubicPath[])scp.getClientProperty( SHAPES_MIRROR );
		if(paths==null) {
			List<CubicPath> mirrorList = new ArrayList<CubicPath>();
			Shape[] dataModel = scp.getDataModel().getShapes();
			for(int a = 0; a<dataModel.length; a++) {
				CubicPath copy = new CubicPath();
				copy.append(dataModel[a]);
				mirrorList.add(copy);
			}
			paths = mirrorList.toArray(new CubicPath[mirrorList.size()]);
			scp.putClientProperty( SHAPES_MIRROR, paths );
			refreshScaleFactor(scp);
		}
		return paths;
	}
	
	protected void refreshScaleFactor(ShapeCreationPanel scp) {
		CubicPath[] paths = getCubicPaths(scp);
		for(CubicPath path : paths) {
			path.setScaleFactor(getScaleFactor(scp));
		}
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		ShapeCreationPanel scp = (ShapeCreationPanel)c;
		scp.getDataModel().removeListener(myDataModelListener);
		scp.removeMouseListener(mouseListener);
		scp.removeMouseMotionListener(mouseListener);
		scp.removePropertyChangeListener(myPropertyListener);
	}

	@Override
	protected void paintControls(Graphics2D g, ShapeCreationPanel scp) {
		g = (Graphics2D)g.create();

		Rectangle2D r = new Rectangle2D.Float();
		Ellipse2D e = new Ellipse2D.Float();
		Line2D line = new Line2D.Float();
		double z = ((double)scp.getHandleSize())/2.0;
		
		AffineTransform tx = scp.getTransform();
		
		try {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(Color.black);
			g.setStroke(new BasicStroke(1));
	
			CubicPath[] paths = getCubicPaths(scp);
			Selection selection = scp.getSelectionModel().getSelection();
			Selection indication = scp.getSelectionModel().getIndication();
			for(int shapeIndex = 0; shapeIndex<paths.length; shapeIndex++) {
				if(scp.getHandlesActive().supports(scp, shapeIndex)) {
					CubicPath path = paths[shapeIndex];
	
					if(path!=null && path.isEmpty()==false) {
						for(int nodeIndex = 0; nodeIndex<path.getNodeCount(); nodeIndex++) {
							Point2D nodePoint = path.getNode(nodeIndex, null);
							nodePoint = tx.transform(nodePoint, null);
							
							Point2D p = path.getPrevControlForNode(nodeIndex, null);
							
							if(p!=null) {
								p = tx.transform(p, null);
								
								g.setColor(Color.lightGray);
								line.setLine(nodePoint, p);
								g.draw(line);
								
								e.setFrame(p.getX()-z,p.getY()-z,2*z,2*z);
	
								if(selection.getShapeIndex()==shapeIndex && 
										selection.getNodeIndex()==nodeIndex && 
										Handle.PREVIOUS_CONTROL.equals(selection.getHandle()) ) {
									g.setColor(Color.black);
								} else if(indication.getShapeIndex()==shapeIndex && 
										indication.getNodeIndex()==nodeIndex && 
										Handle.PREVIOUS_CONTROL.equals(indication.getHandle()) ) {
									g.setColor(Color.gray);
								} else {
									g.setColor(Color.white);
								}
								g.fill(e);
								g.setColor(Color.black);
								g.draw(e);
							}
							
							p = path.getNextControlForNode(nodeIndex, null);
							if(p!=null) {
								p = tx.transform(p, null);
								
								g.setColor(Color.lightGray);
								line.setLine(nodePoint, p);
								g.draw(line);
								
								e.setFrame(p.getX()-z,p.getY()-z,2*z,2*z);
	
								if(selection.getShapeIndex()==shapeIndex && 
										selection.getNodeIndex()==nodeIndex && 
										Handle.NEXT_CONTROL.equals(selection.getHandle()) ) {
									g.setColor(Color.black);
								} else if(indication.getShapeIndex()==shapeIndex && 
										indication.getNodeIndex()==nodeIndex && 
										Handle.NEXT_CONTROL.equals(indication.getHandle()) ) {
								} else {
									g.setColor(Color.white);
								}
								g.fill(e);
								g.setColor(Color.black);
								g.draw(e);
							}
	
							
							r.setFrame(nodePoint.getX()-z,nodePoint.getY()-z,2*z,2*z);
	
							if(selection.getShapeIndex()==shapeIndex && 
									selection.getNodeIndex()==nodeIndex && 
									Handle.PRIMARY.equals(selection.getHandle()) ) {
								g.setColor(Color.black);
							} else if(indication.getShapeIndex()==shapeIndex && 
									indication.getNodeIndex()==nodeIndex && 
									Handle.PRIMARY.equals(indication.getHandle()) ) {
								g.setColor(Color.gray);
							} else {
								g.setColor(Color.white);
							}
							g.fill(r);
							g.setColor(Color.black);
							g.draw(r);
						}
					}
				}
			}
		} finally {
			g.dispose();
		}
	}
}