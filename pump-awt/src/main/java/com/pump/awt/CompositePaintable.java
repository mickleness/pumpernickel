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
package com.pump.awt;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import com.pump.geom.ShapeBounds;
import com.pump.geom.TransformUtils;

/** A Paintable composed of several smaller Paintables that are rendering
 * through an AffineTransform.
 *
 * @param <T> the type of Paintables used to create this composite.
 */
public class CompositePaintable <T extends Paintable> implements Paintable {
	/**
	 * <img src="https://docs.google.com/drawings/pub?id=1aRWNEM9s2uN7qTHFKeUACYpiaDTbvP4BESG2QiRqeIo&amp;w=700&amp;h=300" alt="a diagram explaining the difference in NONE, OFFSET and GRID">
	 */
	public static enum Tiling { NONE, OFFSET, GRID };
	
	List<T> paintables = new ArrayList<>();
	List<AffineTransform> transforms = new ArrayList<>();
	
	Rectangle bounds = null;
	Tiling tiling = Tiling.NONE;
	
	public CompositePaintable() {}
	
	public synchronized void addPaintable(T paintable,Rectangle2D bounds) {
		AffineTransform tx = TransformUtils.createAffineTransform(
				0, 0,
				paintable.getWidth(), 0,
				0, paintable.getHeight(),
				bounds.getMinX(), bounds.getMinY(),
				bounds.getMaxX(), bounds.getMinY(),
				bounds.getMinX(), bounds.getMaxY() );
		addPaintable(paintable, tx);
	}
	
	public void setTiling(Tiling t) {
		if(t==null)
			throw new NullPointerException();
		tiling = t;
	}

	public synchronized void addPaintable(T paintable,AffineTransform tx) {
		paintables.add(paintable);
		transforms.add(tx);
		bounds = null;
	}
	
	public AffineTransform getTransform(int i) {
		return new AffineTransform( transforms.get(i) );
	}
	
	public synchronized int getCount() {
		return paintables.size();
	}
	
	public synchronized T getPaintable(int i) {
		return paintables.get(i);
	}

	public synchronized int getWidth() {
		if(bounds==null)
			refreshBounds();
		return bounds.width;
	}

	public synchronized int getHeight() {
		if(bounds==null)
			refreshBounds();
		return bounds.height;
	}
	
	private void refreshBounds() {
		Rectangle2D sum = null;
		Rectangle t = new Rectangle();
		Rectangle2D t2 = new Rectangle2D.Double();
		for(int a = 0; a<paintables.size(); a++) {
			t.x = 0; t.y = 0;
			t.width = paintables.get(a).getWidth();
			t.height = paintables.get(a).getHeight();
			ShapeBounds.getBounds(t, transforms.get(a), t2);
			if(sum==null) {
				sum = new Rectangle2D.Double( t2.getX(), t2.getY(), t2.getWidth(), t2.getHeight() );
			} else {
				sum.add( t2 );
			}
		}
		if(sum==null) {
			bounds = new Rectangle(0,0,0,0);
		} else {
			AffineTransform translation = AffineTransform.getTranslateInstance( -sum.getX(), -sum.getY() );
			for(int a = 0; a<transforms.size(); a++) {
				transforms.get(a).preConcatenate(translation);
			}
			bounds = sum.getBounds();
			bounds.x = 0;
			bounds.y = 0;
		}
	}

	public synchronized void paint(Graphics2D g) {
		if(tiling==Tiling.NONE) {
			paint(g, 0, 0);
			return;
		}
		int width = getWidth();
		int height = getHeight();
		if(tiling==Tiling.OFFSET) {
			paint(g, -width, -height/2);
			paint(g, -width, height/2);
			paint(g, 0, -height);
			paint(g, 0, 0);
			paint(g, 0, height);
			paint(g, width, -height/2);
			paint(g, width, height/2);
		} else if(tiling==Tiling.GRID) {
			for(int x = -1; x<=1; x++) {
				for(int y = -1; y<=1; y++) {
					paint(g, width*x, height*y);
				}
			}
		}
	}
	
	private void paint(Graphics2D g,double dx,double dy) {
		if(bounds==null)
			refreshBounds();
		
		for(int a = 0; a<paintables.size(); a++) {
			Graphics2D g2 = (Graphics2D)g.create();
			g2.translate(dx, dy);
			g2.transform(transforms.get(a));
			Paintable paintable = paintables.get(a);
			Shape clip = g2.getClip();
			if(clip==null || clip.intersects(0, 0, paintable.getWidth(), paintable.getHeight())) {
				//g2.setColor(new Color( Color.HSBtoRGB( (float)(a)/.7f, .2f, 1f) ));
				//g2.fillRect(0, 0, paintables.get(a).getWidth(), paintables.get(a).getHeight() );
				paintable.paint(g2);
			}
			g2.dispose();
		}
	}

	/** Return the transformed bounds of a child paintable.
	 * 
	 * <p>(The untransformed bounds of a paintable are assumed to be
	 * anchored at (0,0), and span to (width, height).)
	 * 
	 * @param a the index of the paintable to get the transformed bounds of.
	 * @return the transformed bounds of a specific paintable.
	 */
	public synchronized Rectangle2D getTransformedBounds(int a) {
		if(bounds==null)
			refreshBounds();
		
		Rectangle2D r = new Rectangle2D.Double(0, 0, 
				paintables.get(a).getWidth(), paintables.get(a).getHeight());
		return ShapeBounds.getBounds(r, transforms.get(a));
	}
}