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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ComponentUI;

import com.pump.math.MutableInteger;
import com.pump.swing.ColorPalette;
import com.pump.swing.FocusedBorder;

public abstract class ColorPaletteUI extends ComponentUI {
	
	/** The client property for the relative point. This maps to a Point2D indicating
	 * where the selected color is physically located. This is especially useful
	 * in the cases where a color is represented in several possible coordinates
	 * (such as the color black if the bottom row of a HSB spectrum is 100% black).
	 */
	public static final String RELATIVE_POINT_PROPERTY = "paletteRelativePoint";

	/** The client property for the preferred cell width (as an integer).
	 */
	public static final String PREFERRED_CELL_WIDTH_PROPERTY = "preferredCellWidth";

	/** The client property for the preferred cell height (as an integer).
	 */
	public static final String PREFERRED_CELL_HEIGHT_PROPERTY = "preferredCellHeight";
	
	static ChangeListener colorPaletteListener = new ChangeListener() {
		public void stateChanged(ChangeEvent evt) {
			ColorPalette cp = (ColorPalette)evt.getSource();
			cp.repaint();
			
			MutableInteger i = getAdjustingLock(cp);
			
			if(i.value>0) {
				//when this is > 0 it means this object (the UI) is the thing
				//doing the changes, so we don't have to update anything.
				return;
			}

			ColorPaletteUI ui = cp.getUI();
			ui.updateColor(cp);
		}
	};
	
	static KeyListener keyListener = new KeyListener() {

		public void keyPressed(KeyEvent e) {
			ColorPalette cp = (ColorPalette)e.getSource();
			ColorPaletteUI ui = cp.getUI();
			int dx = 0;
			int dy = 0;
			int code = e.getKeyCode();
			if(code==KeyEvent.VK_LEFT) {
				dx = -1;
			} else if(code==KeyEvent.VK_RIGHT) {
				dx = 1;
			} else if(code==KeyEvent.VK_UP) {
				dy = -1;
			} else if(code==KeyEvent.VK_DOWN) {
				dy = 1;
			} else {
				return;
			}
			ui.processKeyEvent(e,dx,dy);
		}

		public void keyReleased(KeyEvent e) {}

		public void keyTyped(KeyEvent e) {}
	};

	static PropertyChangeListener repaintPropertyListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			JComponent jc = (JComponent)evt.getSource();
			jc.repaint();
		}
	};

	static FocusListener repaintFocusListener = new FocusListener() {
		public void focusGained(FocusEvent evt) {
			JComponent jc = (JComponent)evt.getSource();
			jc.repaint();
		}
		public void focusLost(FocusEvent evt) {
			JComponent jc = (JComponent)evt.getSource();
			jc.repaint();
		}
	};

	static MouseInputAdapter mouseListener = new MouseInputAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			ColorPalette cp = (ColorPalette)e.getSource();
			ColorPaletteUI ui = cp.getUI();
			
			Rectangle imageBounds = ui.getImageBounds(cp);
			
			int mouseX = e.getX()-imageBounds.x;
			int mouseY = e.getY()-imageBounds.y;
			
			if(mouseX<0) mouseX = 0;
			if(mouseX>imageBounds.width)
				mouseX = imageBounds.width;
			if(mouseY<0) mouseY = 0;
			if(mouseY>imageBounds.height)
				mouseY = imageBounds.height;
			
			float fractionX = ((float)mouseX)/((float)imageBounds.width);
			float fractionY = ((float)mouseY)/((float)imageBounds.height);
			
			ui.processMouseEvent(e,fractionX,fractionY);
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			mousePressed(e);
		}
	};
	
	public final Color getColor(ColorPalette cp,int x,int y) {
		Rectangle imageBounds = getImageBounds(cp);
		x = x-imageBounds.x;
		y = y-imageBounds.y;

		ColorSet colorSet = getColorSet(cp);
		float xFraction = ((float)x)/((float)imageBounds.width);
		float yFraction = ((float)y)/((float)imageBounds.height);
		return new Color(colorSet.getRGB(xFraction, yFraction));
	}

	@Override
	public final boolean contains(JComponent c, int x, int y) {
		Rectangle imageBounds = getImageBounds((ColorPalette)c);
		if(imageBounds.contains(x,y))
			return true;
		
		for(int a = 0; a<c.getComponentCount(); a++) {
			Point loc = c.getComponent(a).getLocation();
			if(c.getComponent(a).contains(x-loc.x,y-loc.y))
				return true;
		}
		return false;
	}
	
	protected final Rectangle getImageBounds(ColorPalette cp) {
		Insets insets = getImageInsets(cp);
		Rectangle r = new Rectangle(0,0,cp.getWidth(),cp.getHeight());
		r.x += insets.left;
		r.y += insets.top;
		r.width -= insets.left+insets.right;
		r.height -= insets.top+insets.bottom;
		r.width = Math.max(r.width, 1);
		r.height = Math.max(r.height, 1);
		return r;
	}
	
	protected abstract ColorSet getColorSet(ColorPalette cp);
	
	@Override
	public Dimension getMaximumSize(JComponent c) {
		return getPreferredSize(c);
	}
	
	@Override
	public Dimension getMinimumSize(JComponent c) {
		Dimension d = getPreferredSize(c);
		d.width/=2;
		d.height/=2;
		return d;
	}
	
	@Override
	public Dimension getPreferredSize(JComponent c) {
		ColorSet set = getColorSet((ColorPalette)c);
		int cellWidth = -1;
		int cellHeight = -1;
		Number n = (Number)c.getClientProperty(PREFERRED_CELL_WIDTH_PROPERTY);
		if(n!=null)
			cellWidth = n.intValue();
		n = (Number)c.getClientProperty(PREFERRED_CELL_HEIGHT_PROPERTY);
		if(n!=null)
			cellHeight = n.intValue();
		if(cellWidth==-1 && cellHeight==-1) {
			cellWidth = 12;
			cellHeight = 12;
		} else if(cellWidth!=-1) {
			cellHeight = cellWidth;
		} else if(cellHeight!=-1) {
			cellWidth = cellHeight;
		}
		Insets insets = getImageInsets((ColorPalette)c);
		return new Dimension(set.columns*cellWidth+insets.left+insets.right, set.rows*cellHeight+insets.top+insets.bottom);
	}
	
	protected Insets getImageInsets(ColorPalette cp) {
		Border b = cp.getBorder();
		if(b!=null) {
			Insets insets = b.getBorderInsets(cp);
			return insets;
		}
		return new Insets(0,0,0,0);
	}
	
	@Override
	public void installUI(JComponent c) {
		ColorPalette cp = (ColorPalette)c;
		cp.addFocusListener(repaintFocusListener);
		cp.addMouseListener(mouseListener);
		cp.addMouseMotionListener(mouseListener);
		cp.addPropertyChangeListener(ColorSet.PALETTE_STYLE_PROPERTY,repaintPropertyListener);
		cp.addPropertyChangeListener(ColorSet.PALETTE_CELL_STYLE_PROPERTY,repaintPropertyListener);
		cp.addPropertyChangeListener(RELATIVE_POINT_PROPERTY, repaintPropertyListener);
		cp.addChangeListener(colorPaletteListener);
		cp.addKeyListener(keyListener);
		cp.setCursor( Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR) );
		cp.setBorder(new FocusedBorder(getDefaultBorder()));
		
		updateColor( cp );
	}
	
	@Override
	public void uninstallUI(JComponent c) {
		c.removeFocusListener(repaintFocusListener);
		c.removeMouseListener(mouseListener);
		c.removeMouseMotionListener(mouseListener);
		c.removePropertyChangeListener(ColorSet.PALETTE_STYLE_PROPERTY,repaintPropertyListener);
		c.removePropertyChangeListener(ColorSet.PALETTE_CELL_STYLE_PROPERTY,repaintPropertyListener);
		c.removePropertyChangeListener(RELATIVE_POINT_PROPERTY, repaintPropertyListener);
		((ColorPalette)c).removeChangeListener(colorPaletteListener);
		c.removeKeyListener(keyListener);

		ColorPalette cp = (ColorPalette)c;
		cp.removeChangeListener(colorPaletteListener);
	}

	
	protected Border getDefaultBorder() {
		return new BevelBorder(BevelBorder.LOWERED, Color.lightGray, Color.darkGray);
	}


	protected void processKeyEvent(KeyEvent keyEvent,int dx,int dy) {
		ColorPalette cp = (ColorPalette)keyEvent.getSource();
		Point2D p = (Point2D)cp.getClientProperty(RELATIVE_POINT_PROPERTY);
		
		MutableInteger adjusting = getAdjustingLock(cp);
		double newX, newY;
		Rectangle r = getImageBounds(cp);
		ColorSet colors = getColorSet(cp);
		if(colors.grid) {
			int row, column;
			if(p!=null) {
				row = (int)(p.getY()*colors.rows);
				column = (int)(p.getX()*colors.columns);
			} else {
				if(dx<0) {
					column = colors.columns;
				} else if(dx==0) {
					column = colors.columns/2;
				} else {
					column = -1;
				}

				if(dy<0) {
					row = colors.rows;
				} else if(dy==0) {
					row = colors.rows/2;
				} else {
					row = -1;
				}
			}
			row += dy;
			column += dx;
			if(row<0) row = 0;
			if(row>=colors.rows) row = colors.rows-1;
			if(column<0) column = 0;
			if(column>=colors.columns) column = colors.columns-1;
			newX = (column+.5f)/(colors.columns);
			newY = (row+.5f)/(colors.rows);
		} else {
			double stepX = 1.0/(r.width);
			double stepY = 1.0/(r.height);
			if(p!=null) {
				newX = p.getX()+stepX*dx;
				newY = p.getY()+stepY*dy;
			} else {
				if(dx<0) {
					newX = 1-stepX;
				} else if(dx==0) {
					newX = .5f;
				} else {
					newX = stepX;
				}

				if(dy<0) {
					newY = 1-stepY;
				} else if(dy==0) {
					newY = .5;
				} else {
					newY = stepY;
				}
			}
		}
		newX = Math.max(0,Math.min(1,newX));
		newY = Math.max(0,Math.min(1,newY));
		Color color = new Color( getColorSet(cp).getRGB( (float)newX, (float)newY ), true );

		adjusting.value++;
		try {
			cp.setColor(color);
		} finally {
			adjusting.value--;
		}

		cp.putClientProperty(RELATIVE_POINT_PROPERTY, new Point2D.Double(newX, newY));
		keyEvent.consume();
	}

	protected void processMouseEvent(MouseEvent e, float xFraction,
			float yFraction) {
		ColorPalette cp = (ColorPalette)e.getSource();
		cp.requestFocus();
		Point2D p = new Point2D.Float( xFraction, yFraction );
		ColorSet colors = getColorSet(cp);
		if(colors.grid) {
			float row = (int)(yFraction*colors.rows);
			float column = (int)(xFraction*colors.columns);
			row = Math.max(0, Math.min(colors.rows-1, row));
			column = Math.max(0, Math.min(colors.columns-1, column));

			p.setLocation( (column+.5f)/(colors.columns), 
					(row+.5f)/(colors.rows) );
		}
		p.setLocation( Math.max(0, Math.min(p.getX(),1)), Math.max(0, Math.min(p.getY(),1)) );
		Color color = new Color( getColorSet(cp).getRGB( (float)p.getX(), (float)p.getY() ), true );
		MutableInteger adjusting = getAdjustingLock(cp);
		adjusting.value++;
		try {
			cp.setColor( color );
		} finally {
			adjusting.value--;
		}
		cp.putClientProperty(RELATIVE_POINT_PROPERTY, p);
		e.consume();
	}
	
	/** Resets the UI to feature the color returned by
	 * <code>cp.getColor()</code>, if possible.
	 */
	protected void updateColor(ColorPalette cp) {
		Color color = cp.getColor();
		ColorSet colorSet = getColorSet(cp);
		Point2D p = colorSet.getRelativePoint(color.getRGB());
		if(p!=null) {
			if(isRelativePointValid(cp)) {
				cp.putClientProperty(RELATIVE_POINT_PROPERTY, p);
			} else {
				cp.putClientProperty(RELATIVE_POINT_PROPERTY, null);
			}
		} else {
			cp.putClientProperty(RELATIVE_POINT_PROPERTY, null);
		}
	}
	
	protected static MutableInteger getAdjustingLock(ColorPalette cp) {
		MutableInteger i = (MutableInteger)cp.getClientProperty("uiAdjusting");
		if(i==null) {
			i = new MutableInteger(0);
			cp.putClientProperty("uiAdjusting", i);
		}
		return i;
	}
	
	@Override
	public void paint(Graphics g0,JComponent c) {
		if(c.getBorder()!=null) {
			Insets borderInsets = c.getBorder().getBorderInsets(c);
			g0.setColor(c.getForeground());
			g0.fillRect(borderInsets.left,borderInsets.top,
					c.getWidth()-borderInsets.left-borderInsets.right,
					c.getHeight()-borderInsets.top-borderInsets.bottom);
		}
		
		Graphics2D g = (Graphics2D)g0;
		ColorPalette cp = (ColorPalette)c;
		
		ColorSet colorSet = getColorSet(cp);
		Rectangle r = getImageBounds(cp);
		TexturePaint checkerboard = PlafPaintUtils.getCheckerBoard(10);
		g.setPaint(checkerboard);
		g.fillRect(r.x, r.y, r.width, r.height);
		BufferedImage bi = colorSet.getImage(r.width, r.height, cp);
		g.drawImage( bi, r.x, r.y, null);
		
		if(isRelativePointValid(cp)) {
			Point2D p = (Point2D)cp.getClientProperty(RELATIVE_POINT_PROPERTY);
			g.clipRect(r.x, r.y, r.width, r.height);
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			Ellipse2D e = new Ellipse2D.Double(p.getX()*r.width-4+r.x,
					p.getY()*r.height-4+r.y,8,8);
			g.setColor(Color.black);
			g.draw(e);
			e.setFrame(p.getX()*r.width-3+r.x,
					p.getY()*r.height-3+r.y,6,6);
			g.setColor(Color.white);
			g.draw(e);
		}
		
		//code to test getRelativePoint()
		/*Point2D zz = colorSet.getRelativePoint(cp.getColor().getRGB());
		System.out.println(cp.getColor());
		g.setColor(Color.black);
		g.fillRect( (int)(zz.getX()*r.width-2)+r.x, (int)(zz.getY()*r.height-2)+r.y, 4, 4);*/
	}
	
	protected boolean isRelativePointValid(ColorPalette cp) {
		Point2D p = (Point2D)cp.getClientProperty(RELATIVE_POINT_PROPERTY);
		if(p==null) return false;
		
		ColorPaletteUI ui = cp.getUI();
		ColorSet colors = ui.getColorSet(cp);
		int rgb1 = colors.getRGB( (float)p.getX(), (float)p.getY() );
		int rgb2 = cp.getColor().getRGB();
		int d = getRGBDistanceSquared(rgb1,rgb2);
		return d<4*3*3;
	}
	
	protected static int getRGBDistanceSquared(int rgb1,int rgb2) {
		int a1 = (rgb1 >> 24) & 0xff;
		int r1 = (rgb1 >> 16) & 0xff;
		int g1 = (rgb1 >> 8) & 0xff;
		int b1 = (rgb1 >> 0) & 0xff;
		int a2 = (rgb2 >> 24) & 0xff;
		int r2 = (rgb2 >> 16) & 0xff;
		int g2 = (rgb2 >> 8) & 0xff;
		int b2 = (rgb2 >> 0) & 0xff;
		int distanceSquared = (a1-a2)*(a1-a2)+(r1-r2)*(r1-r2)+(g1-g2)*(g1-g2)+(b1-b2)*(b1-b2);
		
		return distanceSquared;
	}
}