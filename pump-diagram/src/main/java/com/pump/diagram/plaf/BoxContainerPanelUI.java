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
package com.pump.diagram.plaf;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ComponentUI;

import com.pump.diagram.Box;
import com.pump.diagram.BoxContainer;
import com.pump.diagram.Connector;
import com.pump.diagram.Relationship;
import com.pump.diagram.swing.BoxContainerPanel;
import com.pump.geom.ShapeBounds;
import com.pump.geom.TransformUtils;
import com.pump.plaf.UIEffect;
import com.pump.swing.ContextualMenuHelper;
import com.pump.util.ObservableProperties;
import com.pump.util.ObservableProperties.Key;
import com.pump.util.ObservableProperties.NonNullBoundsChecker;

public class BoxContainerPanelUI extends ComponentUI
{

	protected static final String KEY_PLAF_CONTEXT = BoxContainerPanelUI.class.getName()+"#plafContext";
	
	public static final String KEY_TARGET_HANDLE_OPACITY = BoxContainerPanelUI.class.getName()+"#handleTargetOpacity";
	public static final String KEY_REAL_HANDLE_OPACITY = BoxContainerPanelUI.class.getName()+"#handleRealOpacity";
	
    public static ComponentUI createUI(JComponent c) {
        return new RightAngleBoxContainerPanelUI();
    }
	static final float handleRadius = 5;
    
    static class ScalingHandles {
    	static class Handle extends AbstractHandle {
    		int position;
    		
    		GeneralPath shape = null;
    		boolean priority;
    		String name;
    		
    		Handle(int position,boolean priority,String name) {
    			this.position = position;
    			this.priority = priority;
    			this.name = name;
    		}
    		
    		public Cursor getCursor() {
    			switch(position)
    			{
	    			case SwingConstants.NORTH:
	    			return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
	    			case SwingConstants.SOUTH:
	    			return Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
	    			case SwingConstants.EAST:
	    			return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
	    			case SwingConstants.WEST:
	    			return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
	    			case SwingConstants.NORTH_EAST:
	    			return Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
	    			case SwingConstants.SOUTH_EAST:
	    			return Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
	    			case SwingConstants.NORTH_WEST:
	    			return Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
	    			case SwingConstants.SOUTH_WEST:
	    			return Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
    			}
    			return Cursor.getDefaultCursor();
    		}
    		
    		@Override
    		public String toString() {
    			return name;
    		}
    		
    		@Override
    		public Shape getShape() {
    			return shape;
    		}

			public void defineShape(Rectangle r) {
				shape = new GeneralPath();
				int k1 = 15;
				int k2 = 8;
				switch(position) {
				case SwingConstants.NORTH_WEST:
					shape.moveTo(r.x, r.y);
					shape.lineTo(r.x + k1, r.y);
					shape.lineTo(r.x + k1, r.y - k2);
					shape.lineTo(r.x - k2, r.y - k2);
					shape.lineTo(r.x - k2, r.y + k1);
					shape.lineTo(r.x, r.y + k1);
					shape.closePath();
					break;
				case SwingConstants.NORTH_EAST:
					shape.moveTo(r.x + r.width, r.y);
					shape.lineTo(r.x+ r.width - k1, r.y);
					shape.lineTo(r.x+ r.width - k1, r.y - k2);
					shape.lineTo(r.x+ r.width + k2, r.y - k2);
					shape.lineTo(r.x+ r.width + k2, r.y + k1);
					shape.lineTo(r.x+ r.width, r.y + k1);
					shape.closePath();
					break;
				case SwingConstants.SOUTH_WEST:
					shape.moveTo(r.x, r.y + r.height);
					shape.lineTo(r.x + k1, r.y + r.height);
					shape.lineTo(r.x + k1, r.y + r.height + k2);
					shape.lineTo(r.x - k2, r.y + r.height + k2);
					shape.lineTo(r.x - k2, r.y + r.height - k1);
					shape.lineTo(r.x, r.y + r.height - k1);
					shape.closePath();
					break;
				case SwingConstants.SOUTH_EAST:
					shape.moveTo(r.x + r.width, r.y + r.height);
					shape.lineTo(r.x+ r.width - k1, r.y + r.height);
					shape.lineTo(r.x+ r.width - k1, r.y + r.height + k2);
					shape.lineTo(r.x+ r.width + k2, r.y + r.height + k2);
					shape.lineTo(r.x+ r.width + k2, r.y + r.height - k1);
					shape.lineTo(r.x+ r.width, r.y + r.height - k1);
					shape.closePath();
					break;
				case SwingConstants.NORTH:
					shape.moveTo(r.x + r.width/2 - k1/2, r.y);
					shape.lineTo(r.x + r.width/2 + k1/2, r.y);
					shape.lineTo(r.x + r.width/2 + k1/2, r.y - k2);
					shape.lineTo(r.x + r.width/2 - k1/2, r.y - k2);
					break;
				case SwingConstants.SOUTH:
					shape.moveTo(r.x + r.width/2 - k1/2, r.y + r.height);
					shape.lineTo(r.x + r.width/2 + k1/2, r.y + r.height);
					shape.lineTo(r.x + r.width/2 + k1/2, r.y + r.height + k2);
					shape.lineTo(r.x + r.width/2 - k1/2, r.y + r.height + k2);
					break;
				case SwingConstants.EAST:
					shape.moveTo(r.x, r.y + r.height/2 - k1/2);
					shape.lineTo(r.x, r.y + r.height/2 + k1/2);
					shape.lineTo(r.x - k2, r.y + r.height/2 + k1/2);
					shape.lineTo(r.x - k2, r.y + r.height/2 - k1/2);
					break;
				case SwingConstants.WEST:
					shape.moveTo(r.x + r.width, r.y + r.height/2 - k1/2);
					shape.lineTo(r.x + r.width, r.y + r.height/2 + k1/2);
					shape.lineTo(r.x + r.width + k2, r.y + r.height/2 + k1/2);
					shape.lineTo(r.x + r.width + k2, r.y + r.height/2 - k1/2);
					break;
				default:
					throw new RuntimeException("unexpected position: "+position);
				}
			}

			public void paint(BoxContainerPanel bcp, Graphics2D g) {
				g = (Graphics2D)g.create();
				g.setColor(new Color(0,0,0,(int)(200*bcp.getUI().getHandleOpacity(bcp, this))));
				g.fill(shape);
				g.dispose();
			}

			Map<Box, Rectangle> originalBoxBounds;
			Rectangle originalBounds;
			Rectangle baseRect;
			
			@Override
			public void prepareForDrag(BoxContainerPanel bcp) {
				originalBoxBounds = new HashMap<>();
				for(Box box : bcp.getSelectionModel().get()) {
					originalBoxBounds.put(box, new Rectangle(box.getBounds()));
				}
				originalBounds = getBoxBounds(bcp.getSelectionModel().get());
				switch(position) {
				case SwingConstants.NORTH:
					baseRect = new Rectangle(originalBounds.x, originalBounds.y+originalBounds.height, originalBounds.width, 0);
					break;
				case SwingConstants.SOUTH:
					baseRect = new Rectangle(originalBounds.x, originalBounds.y, originalBounds.width, 0);
					break;
				case SwingConstants.WEST:
					baseRect = new Rectangle(originalBounds.x, originalBounds.y, 0, originalBounds.height);
					break;
				case SwingConstants.EAST:
					baseRect = new Rectangle(originalBounds.x + originalBounds.width, originalBounds.y, 0, originalBounds.height);
					break;
				case SwingConstants.NORTH_EAST:
					baseRect = new Rectangle(originalBounds.x, originalBounds.y + originalBounds.height, 0, 0);
					break;
				case SwingConstants.NORTH_WEST:
					baseRect = new Rectangle(originalBounds.x + originalBounds.width, originalBounds.y + originalBounds.height, 0, 0);
					break;
				case SwingConstants.SOUTH_EAST:
					baseRect = new Rectangle(originalBounds.x, originalBounds.y, 0, 0);
					break;
				case SwingConstants.SOUTH_WEST:
					baseRect = new Rectangle(originalBounds.x + originalBounds.width, originalBounds.y, 0, 0);
					break;
				}
			}

			@Override
			public void drag(BoxContainerPanel bcp,MouseEvent e) {
				Rectangle newSelectionBounds = new Rectangle(baseRect);
				newSelectionBounds.add(e.getPoint());
				AffineTransform tx = TransformUtils.createAffineTransform(originalBounds, newSelectionBounds);

				for(Box box : bcp.getSelectionModel().get()) {
					Rectangle bounds = new Rectangle(originalBoxBounds.get(box));
					bounds = tx.createTransformedShape(bounds).getBounds();
					box.setBounds(bounds);
				}
			}
    		
    	
    		
    		
    	}
    	
    	Handle topLeft = new Handle(SwingConstants.NORTH_WEST, true, "northwest");
    	Handle top = new Handle(SwingConstants.NORTH, false, "north");
    	Handle topRight = new Handle(SwingConstants.NORTH_EAST, true, "northeast");
    	Handle left = new Handle(SwingConstants.WEST, false, "west");
    	Handle right = new Handle(SwingConstants.EAST, false, "east");
    	Handle bottomLeft = new Handle(SwingConstants.SOUTH_WEST, true, "southwest");
    	Handle bottom = new Handle(SwingConstants.SOUTH, false, "south");
    	Handle bottomRight = new Handle(SwingConstants.SOUTH_EAST, true, "southeast");
    	Handle[] handles = new Handle[] {
    			topLeft, top, topRight,
    			left, right,
    			bottomLeft, bottom, bottomRight
    	};
    	
    	public ScalingHandles() {
    		
    	}
    	
    	private Rectangle grow(Rectangle r) {
    		r.x -= 5;
    		r.y -= 5;
    		r.width += 10;
    		r.height += 10;
    		return r;
    	}
    	
    	public void selectionChanged(Collection<Box> newSelection) {
    		Rectangle r = getBoxBounds(newSelection);
    		for(Handle handle : handles) {
    			handle.defineShape(r);
    		}
    		//if the handles overlap because the space is confined, then drop the smaller handles:
    		for(Handle a : handles) {
        		for(Handle b : handles) {
        			if(a.priority && (!b.priority)) {
        				Rectangle r1 = grow(a.shape.getBounds());
        				Rectangle r2 = grow(b.shape.getBounds());
        				if(r1.intersects(r2)) {
        					b.shape = new GeneralPath();
        				}
        			}
        		}
    		}
    	}
    	
    	public void paint(BoxContainerPanel bcp,Graphics2D g) {
    		for(Handle handle : handles) {
    			Graphics2D g2 = (Graphics2D)g.create();
    			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, bcp.getUI().getHandleOpacity(bcp, handle)));
    			handle.paint(bcp, g2);
    			g2.dispose();
    		}
    	}
    	
    	public static Rectangle getBoxBounds(Collection<Box> boxes) {
    		Rectangle r = null;
    		for(Box box : boxes) {
    			if(r==null) {
    				r = new Rectangle(box.getBounds());
    			} else {
    				r.add(box.getBounds());
    			}
    		}
    		return r;
    	}
    	
    }
    
    public static class ConnectorHandle extends AbstractHandle {
    	Shape shape;
    	Connector connector;
    	
    	public ConnectorHandle(Connector connector,Point center,Shape shape) {
    		if(shape==null)
    			throw new NullPointerException();
    		this.connector = connector;
    		this.shape = shape;
    		setCenter(center);
    	}
    	
    	public Cursor getCursor() {
    		return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
    	}
    	
    	@Override
    	public Shape getShape() {
    		if(centerDirty) {
    			Rectangle2D r = ShapeBounds.getBounds(shape);
    			Point center = getCenter();
    			double dx = center.x - r.getCenterX();
    			double dy = center.y - r.getCenterY();
    			AffineTransform tx = AffineTransform.getTranslateInstance(dx, dy);
    			shape = tx.createTransformedShape(shape);
    			centerDirty = false;
    		}
    		return shape;
    	} 	
    	
    	public void setCenter(Point p) {
    		properties.set(KEY_CENTER, p);
    		centerDirty = true;
    	}
    	
    	public Point getCenter() {
    		return new Point( properties.get(KEY_CENTER) );
    	}

		@Override
		public void prepareForDrag(BoxContainerPanel bcp) {
			// nothing to do here
		}

		@Override
		public void drag(BoxContainerPanel bcp, MouseEvent e) {
			setCenter(e==null ? null : e.getPoint());
		}
    }
    
    protected abstract static class AbstractHandle {
    	public static final Key<Point> KEY_CENTER = new Key<>("center", Point.class, new NonNullBoundsChecker());
    	ObservableProperties properties = new ObservableProperties();
    	
    	transient boolean centerDirty = true;
    	
    	public AbstractHandle() {
    	}
    	public abstract Shape getShape();  
    	
    	public abstract Cursor getCursor();

    	public <T> void set(Key<T> key, T value) {
    		properties.set(key, value);
    	}
    	
    	public <T> T get(Key<T> key) {
    		return properties.get(key);
    	}

		public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener)
		{
			properties.addListener(propertyChangeListener);
		}

		public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener)
		{
			properties.removeListener(propertyChangeListener);
		}
		
		public abstract void prepareForDrag(BoxContainerPanel bcp);
		
		public abstract void drag(BoxContainerPanel bcp,MouseEvent e);
    }
    
    protected class PlafContext {
    	public final BoxContainerPanel bcp;
    	Map<String, Object> properties = new HashMap<>();
    	Rectangle selectionRectangle = null;
    	ScalingHandles handles = null;
    	List<JMenuItem> contextMenuItems = new ArrayList<>();

		JMenuItem deleteConnectorItem = new JMenuItem("Delete");
		AbstractHandle clickedHandle;
    	KeyListener keyListener = new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode()==KeyEvent.VK_RIGHT) {
					nudge(1, 0);
				} else if(e.getKeyCode()==KeyEvent.VK_LEFT) {
					nudge(-1, 0);
				} else if(e.getKeyCode()==KeyEvent.VK_UP) {
					nudge(0, -1);
				} else if(e.getKeyCode()==KeyEvent.VK_DOWN) {
					nudge(0, 1);
				}
			}
			
			private void nudge(int dx,int dy) {
				for(Box box : bcp.getSelectionModel().get()) {
					Rectangle r = box.getBounds();
					r.x += dx;
					r.y += dy;
					box.setBounds(r);
				}
			}    		
    	};
    	
		MouseInputAdapter mouseListener = new MouseInputAdapter() {
			Box clickedBox;
			Point clickedLoc;
			Point lastLoc;
			
			@Override
			public void mousePressed(MouseEvent e) {
				clickedLoc = e.getPoint();
				clickedBox = getBox(e, null);
				lastLoc = e.getPoint();
				bcp.requestFocus();
				
				if(clickedBox==null) {
					clickedHandle = getHandle(PlafContext.this, e);
					if(clickedHandle!=null) {
						if(clickedHandle instanceof ConnectorHandle)
							contextMenuItems.add(deleteConnectorItem);
    					clickedHandle.prepareForDrag(bcp);
					} else {
						clickedHandle = null;
						bcp.getSelectionModel().set( new ArrayList<Box>(0) );
					}
				} else {
					if(e.isShiftDown()) {
						if(bcp.getSelectionModel().contains(clickedBox)) {
							bcp.getSelectionModel().remove(clickedBox);
						} else {
							bcp.getSelectionModel().add(clickedBox);
						}
					} else {
						bcp.getSelectionModel().set( Collections.singleton(clickedBox) );
					}
				}
			}
			
			@Override
			public void mouseMoved(MouseEvent e) {
				Cursor cursor = getCursor(e);
				if(cursor==null)
				{
					cursor = Cursor.getDefaultCursor();
				}
				bcp.setCursor(cursor);
				bcp.putClientProperty( KEY_TARGET_HANDLE_OPACITY, 1f);
			}
			
			private Cursor getCursor(MouseEvent e) {
				AbstractHandle handle = getHandle(PlafContext.this, e);
				if(handle!=null)
					return handle.getCursor();
				return null;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				boolean dirty = false;
				clickedLoc = null;
				clickedBox = null;
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						contextMenuItems.remove(deleteConnectorItem);
					}
				});
				if(selectionRectangle!=null) {
					dirty = true;
				}
				setSelectionRectangle(null);
				if(dirty)
					bcp.repaint();
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				if(clickedLoc==null) {
					bcp.setCursor(Cursor.getDefaultCursor());
					bcp.putClientProperty( KEY_TARGET_HANDLE_OPACITY, 0f);
				}
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				boolean dirty = false;
				if(lastLoc!=null && clickedLoc!=null) {
					int dx = e.getX() - lastLoc.x;
					int dy = e.getY() - lastLoc.y;
					if(clickedBox!=null) {
						Rectangle r = clickedBox.getBounds();
						r.x += dx;
						r.y += dy;
						clickedBox.setBounds(r);
						dirty = true;
					} else if(clickedHandle!=null) {
						clickedHandle.drag(bcp, e);
						dirty = true;
					} else {
						Rectangle r = new Rectangle(clickedLoc.x, clickedLoc.y, 0, 0);
						r.add(e.getPoint());
						setSelectionRectangle(r);
						dirty = true;
					}
				}
				lastLoc = e.getPoint();
				if(dirty) {
					bcp.repaint();
					bcp.getUI().refreshConnectors(bcp);
				}
			}
			
			protected Box getBox(MouseEvent e,Box preferredBox) {
				BoxContainer bc = bcp.getBoxContainer();
				
				if(preferredBox!=null && preferredBox.getBounds().contains(e.getPoint())) {
					return preferredBox;
				}
				
				for(int a = bc.getBoxes().size()-1; a>=0; a--) {
					Box box = bc.getBoxes().get(a);
					if(box.getBounds().contains(e.getPoint())) {
						return box;
					}
				}
				return null;
			}
		};
		
		protected void setSelectionRectangle(Rectangle r) {
			if(selectionRectangle!=null) {
				bcp.repaint(selectionRectangle.x-1, selectionRectangle.y-1, selectionRectangle.width+2, selectionRectangle.height+2);
			}
			if(r!=null) {
				bcp.repaint(r.x-1, r.y-1, r.width+2, r.height+2);
			}
			selectionRectangle = r;
			
			Set<Box> newSelection = new HashSet<>();
			for(int a = 0; a<bcp.getBoxContainer().getBoxes().size(); a++) {
				Box box = bcp.getBoxContainer().getBoxes().get(a);
				Rectangle boxBounds = box.getBounds();
				if(r!=null && boxBounds.intersects(r)) {
					newSelection.add(box);
				}
			}
			if(r!=null)
				bcp.getSelectionModel().set(newSelection);
		}

		protected void refreshScalingHandles() {
			Collection<Box> selection = bcp.getSelectionModel().get();
			if(selection.size()>0) {
				handles = new ScalingHandles();
				handles.selectionChanged(selection);
			} else {
				handles = null;
			}
		}
		
		PropertyChangeListener refreshListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				if(Box.KEY_BOUNDS.matches(evt) || 
						Connector.KEY_BOX1.matches(evt) || 
						Connector.KEY_BOX2.matches(evt) || 
						Connector.KEY_CONTROL_POINT.matches(evt) || 
						Connector.KEY_RELATIONSHIP.matches(evt)) {
					refreshConnectors(bcp);
				}
			}
		};
		
		ListDataListener boxContainerListener = new ListDataListener() {

			Set<Box> allBoxes = new HashSet<>();
			Set<Connector> allConnectors = new HashSet<>();
			
			@Override
			public void intervalAdded(ListDataEvent e)
			{
				contentsChanged(e);
			}

			@Override
			public void intervalRemoved(ListDataEvent e)
			{
				contentsChanged(e);
			}

			@Override
			public void contentsChanged(ListDataEvent e)
			{
				BoxContainer bc = bcp.getBoxContainer();
				boolean dirty = false;
				if(bc!=null) {
					if(e.getSource()==bc.getConnectors()) {
						for(Connector connector : bc.getConnectors()) {
							if(allConnectors.add(connector)) {
								connector.addPropertyChangeListener(refreshListener);
								dirty = true;
							}
						}
						Iterator<Connector> myConnectorIter = allConnectors.iterator();
						while(myConnectorIter.hasNext()) {
							Connector myC = myConnectorIter.next();
							if(!bc.getConnectors().contains(myC)) {
								myC.removePropertyChangeListener(refreshListener);
								myConnectorIter.remove();
								dirty = true;
							}
						}
					} else if(e.getSource()==bc.getBoxes()) {
						for(Box box : bc.getBoxes()) {
							if(allBoxes.add(box)) {
								dirty = true;
								box.addPropertyChangeListener(refreshListener);
								bindBox(box);
							}
						}
						Iterator<Box> myBoxIter = allBoxes.iterator();
						while(myBoxIter.hasNext()) {
							Box myBox = myBoxIter.next();
							if(!bc.getBoxes().contains(myBox)) {
								JComponent jc = myBox.getComponent();
								if(jc!=null) {
									bcp.remove(jc);
								}
								myBox.removePropertyChangeListener(refreshListener);
								myBoxIter.remove();
								dirty = true;
							}
						}
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								bcp.getSelectionModel().set();
							}
						});
					}
				} else {
					for(Connector connector : allConnectors) {
						connector.removePropertyChangeListener(refreshListener);
						dirty = true;
					}
					allConnectors.clear();
					
					for(Box box : allBoxes) {
						box.removePropertyChangeListener(refreshListener);
						dirty = true;
					}
					allBoxes.clear();
				}
				if(dirty)
					refreshConnectors(bcp);
			}
			
			protected void bindBox(final Box box) {
				JComponent jc = box.getComponent();
				if(jc!=null) {
					bindBox(box, jc);
				}
				box.addPropertyChangeListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						JComponent jc = box.getComponent();
						if(jc!=null && Box.KEY_COMPONENT.matches(evt)) {
							bindBox(box, jc);
						}
					}
				});
			}
			
			protected void bindBox(final Box box,final JComponent jc) {
				bcp.add(jc);
				box.addPropertyChangeListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if(Box.KEY_BOUNDS.matches(evt)) {
							bcp.getLayout().layoutContainer(bcp);
							bcp.revalidate();
							refreshScalingHandles();
						}
					}
				});
				bcp.revalidate();
			}
			
		};
		
		/** This is the master listener that is notified when a BoxContainerPanel changes
		 * the BoxContainer it should be displaying. This is relatively rare.
		 */
		PropertyChangeListener componentBoxContainerListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				BoxContainer c1 = (BoxContainer)evt.getOldValue();
				BoxContainer c2 = (BoxContainer)evt.getNewValue();
				if(c1!=null) {
					c1.getBoxes().removeSynchronizedListener(boxContainerListener);
					c1.getConnectors().removeSynchronizedListener(boxContainerListener);
				}
				if(c2!=null) {
					c2.getBoxes().addSynchronizedListener(boxContainerListener, true);
					c2.getConnectors().addSynchronizedListener(boxContainerListener, true);
				}
			}
		};
		
    	protected PlafContext(BoxContainerPanel bcp) {
    		this.bcp = bcp;
    		UIEffect.installTweenEffect(bcp, KEY_TARGET_HANDLE_OPACITY, KEY_REAL_HANDLE_OPACITY, .05f, 20);
    		this.bcp.getSelectionModel().addChangeListener(new ChangeListener() {
    			public void stateChanged(ChangeEvent e) {
    				refreshScalingHandles();
    				PlafContext.this.bcp.repaint();
    			}
    		});

    		new ContextualMenuHelper(bcp) {
				@Override
				protected void showPopup(Component c, int x, int y)
				{
					if(contextMenuItems.size()==0)
						return;
					getPopupMenu().removeAll();
					for(JMenuItem i : contextMenuItems) {
						getPopupMenu().add(i);
					}
					super.showPopup(c, x, y);
				}
			};
			
    		deleteConnectorItem.addActionListener(new ActionListener() {
    			@Override
    			public void actionPerformed(ActionEvent e) {
    				((ConnectorHandle)clickedHandle).connector.setControlPoint(null);
    			}
    		});
    		
    		bcp.setLayout(new LayoutManager() {

				@Override
				public void addLayoutComponent(String name, Component comp) {}

				@Override
				public void removeLayoutComponent(Component comp) {}

				@Override
				public Dimension preferredLayoutSize(Container parent) {
					return null;
				}

				@Override
				public Dimension minimumLayoutSize(Container parent) {
					return null;
				}

				@Override
				public void layoutContainer(Container parent) {
					BoxContainer bc = PlafContext.this.bcp.getBoxContainer();
					for(int a = 0; a<bc.getBoxes().size(); a++) {
						Box box = bc.getBoxes().get(a);
						JComponent jc = box.getComponent();
						if(jc!=null) {
							Rectangle r = box.getBounds();
							jc.setBounds(r);
						}
					}
				}
    			
    		});
    	}
		public void installUI()
		{
			bcp.addMouseListener(mouseListener);
			bcp.addMouseMotionListener(mouseListener);
			bcp.addKeyListener(keyListener);
    		bcp.addPropertyChangeListener(BoxContainerPanel.KEY_BOX_CONTAINER, componentBoxContainerListener);
    		refreshConnectors(bcp);
    		BoxContainer bc = bcp.getBoxContainer();
    		if(bc!=null) {
				bc.getBoxes().addSynchronizedListener(boxContainerListener, true);
				bc.getConnectors().addSynchronizedListener(boxContainerListener, true);
    		}
		}
		public void uninstallUI()
		{
			bcp.removeMouseListener(mouseListener);
			bcp.removeMouseMotionListener(mouseListener);
			bcp.removeKeyListener(keyListener);
    		bcp.removePropertyChangeListener(BoxContainerPanel.KEY_BOX_CONTAINER, componentBoxContainerListener);
    		BoxContainer bc = bcp.getBoxContainer();
    		if(bc!=null) {
				bc.getBoxes().removeSynchronizedListener(boxContainerListener);
				bc.getConnectors().removeSynchronizedListener(boxContainerListener);
    		}
		}
		
		public <T> void set(Key<T> key,T value)
		{
			properties.put(key.getKeyName(), value);
		}
		
		public void set(String keyName,Object value) {
			properties.put(keyName, value);
		}
		
		public Object get(String keyName) {
			return properties.get(keyName);
		}
		
		public <T> T get(Key<T> key) {
			return (T)properties.get(key.getKeyName());
		}
    }
    
    protected PlafContext getContext(BoxContainerPanel bcp) {
    	PlafContext plafContext = (PlafContext)bcp.getClientProperty(KEY_PLAF_CONTEXT);
    	if(plafContext==null) {
    		plafContext = new PlafContext(bcp);
    		bcp.putClientProperty(KEY_PLAF_CONTEXT, plafContext);
    	}
    	return plafContext;
    }

	protected float getHandleOpacity(BoxContainerPanel bcp,AbstractHandle handle) {
		Number n = (Number)bcp.getClientProperty(KEY_REAL_HANDLE_OPACITY);
		if(n==null)
			return 1;
		return n.floatValue();
	}
	
    /** Outside classes should never need to call this; this is made public mostly
     * for debugging.
     */
	public void refreshConnectors(BoxContainerPanel bcp)
	{
		bcp.repaint();
	}


	@Override
	public void installUI(JComponent c)
	{
		getContext( (BoxContainerPanel)c ).installUI();
	}


	@Override
	public void uninstallUI(JComponent c)
	{
		getContext( (BoxContainerPanel)c ).uninstallUI();
	}


	@Override
	public Dimension getPreferredSize(JComponent c)
	{
		return new Dimension(800,600);
	}
	
	@Override
	public Dimension getMinimumSize(JComponent c)
	{
		return new Dimension(200,200);
	}


	@Override
	public void paint(Graphics g0, JComponent c)
	{
		Graphics2D g = (Graphics2D)g0.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		try {
			BoxContainerPanel bcp = (BoxContainerPanel)c;
			BoxContainer bc = bcp.getBoxContainer();
			
			PlafContext context = getContext(bcp);
			if(context.selectionRectangle!=null) {
				g.setColor(new Color(0,0,0,50));
				g.fill( context.selectionRectangle );
			}
			
			if(bc!=null) {
				for(Connector connector : bc.getConnectors()) {
					paintConnector(g, bcp, connector);
				}
				
				if(context.handles!=null) {
					context.handles.paint(bcp, g);
				}
				
				paintScalingHandles(bcp);
				
				for(Box box : bc.getBoxes()) {
					paintBox(g, box);
				}
				
				List<Connector> allConnectors = bc.getConnectors();
				List<Connector> dashedConnectors = new ArrayList<>();
				List<Connector> solidConnectors = new ArrayList<>();
				for(Connector connector : allConnectors) {
					if(connector.getControlPoint(true)==null) {
						dashedConnectors.add(connector);
					} else {
						solidConnectors.add(connector);
					}
				}
				paintHandles(g, bcp, dashedConnectors, bc.getBoxes(), true);
				paintHandles(g, bcp, solidConnectors, bc.getBoxes(), false);
			}
		} finally {
			g.dispose();
		}
	}
	
	protected void paintScalingHandles(BoxContainerPanel bcp) {
		PlafContext context = getContext(bcp);
		Collection<Box> selectedBoxes = context.bcp.getSelectionModel().get();
		Rectangle selectionBounds = null;
		for(Box box : selectedBoxes) {
			if(selectionBounds==null) {
				selectionBounds = new Rectangle(box.getBounds());
			} else {
				selectionBounds.add( box.getBounds() );
			}
		}
		
		
	}
	
	protected AbstractHandle getHandle(PlafContext context, MouseEvent e) {
		Point p = e.getPoint();
		
		for(final Connector connector : context.bcp.getBoxContainer().getConnectors()) {
			Point controlPoint = connector.getControlPoint(false);
			Ellipse2D handleShape = new Ellipse2D.Float( controlPoint.x - handleRadius, controlPoint.y - handleRadius, 2*handleRadius, 2*handleRadius);
			if(handleShape.contains(e.getPoint())) {
				AbstractHandle handle = new ConnectorHandle(connector, controlPoint, handleShape);
				handle.addPropertyChangeListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt)
					{
						if(AbstractHandle.KEY_CENTER.matches(evt)) {
							connector.setControlPoint( (Point)evt.getNewValue() );
						}
					}
				});
				return handle;
			}
		}
		
		if(context.handles!=null) {
			for(int a = 0; a<context.handles.handles.length; a++) {
				if(context.handles.handles[a].shape.contains(p)) {
					return context.handles.handles[a];
				}
			}
		}
		return null;
	}
	
	protected void paintBox(Graphics2D g,Box box) {
		if(box.getComponent()!=null) {
			return;
		}
		Rectangle r = box.getBounds();
		g.setColor(Color.lightGray);
		g.setPaint(new GradientPaint(0,r.y,new Color(0xffffff),0,r.y+r.height,new Color(0xdddddd)));
		g.fill(r);
		g.setColor(Color.darkGray);
		g.draw(r);
		
		g.setColor(Color.black);
		String boxName = box.toString();
		Rectangle2D bounds = g.getFont().getStringBounds(boxName, g.getFontRenderContext());
		if(bounds.getWidth()+10>r.width) {
			r.setBounds(r.x, r.y, (int)(Math.ceil(bounds.getWidth())+10.5), r.height);
		}
		g.drawString(boxName, r.x+5, r.y+15);
	}
	
	protected void paintConnector(Graphics2D g,BoxContainerPanel bcp,Connector connector) {
		Rectangle r1 = connector.getBox1().getBounds();
		Rectangle r2 = connector.getBox2().getBounds();
		g.setColor(Color.black);
		g.drawLine(r1.x+r1.width/2, r1.y+r1.height/2, r2.x+r2.width/2, r2.y+r2.height/2);
	}

	protected void paintHandles(Graphics2D g,BoxContainerPanel bcp,List<Connector> connectors,List<Box> boxes,boolean dashStroke) {
		g = (Graphics2D)g.create();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getHandleOpacity(bcp, null)));
		for(Connector connector : connectors) {
			Point cp = connector.getControlPoint(false);
			paintEllipseHandle(g, cp, dashStroke);
		}
		g.dispose();
	}
	
	protected void paintEllipseHandle(Graphics2D g,Point p,boolean dashed) {
		g.setColor(new Color(0,0,0,100));
		if(dashed) {
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
			g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 5, new float[] { 2f, 2f}, 0));
		} else {
			g.setStroke(new BasicStroke(1));
		}
		Ellipse2D handle = new Ellipse2D.Float(p.x-4, p.y-4, 8, 8);
		g.draw(handle);
	}

	public void addConnectorDecoration(GeneralPath path, Relationship relationship, Point target, Point source)
	{
		relationship.appendDecoration(path, target, source);
	}

}