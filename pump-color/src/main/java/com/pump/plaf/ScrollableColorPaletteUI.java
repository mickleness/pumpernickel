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

import java.awt.Adjustable;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JScrollBar;

import com.pump.swing.ColorPalette;

public abstract class ScrollableColorPaletteUI extends ColorPaletteUI {
	
	private static ComponentListener componentListener = new ComponentListener() {

		public void componentHidden(ComponentEvent e) {}

		public void componentMoved(ComponentEvent e) {}

		public void componentResized(ComponentEvent e) {
			ColorPalette cp = (ColorPalette)e.getSource();
			ScrollableColorPaletteUI ui = (ScrollableColorPaletteUI)cp.getUI();
			ui.updateScrollBarBounds(cp);
		}

		public void componentShown(ComponentEvent e) {}
		
	};
	
	static PropertyChangeListener showScrollBarsListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			ColorPalette cp = (ColorPalette)evt.getSource();
			ScrollableColorPaletteUI ui = (ScrollableColorPaletteUI)cp.getUI();
			ui.updateScrollBarBounds(cp);
		}
	};
	
	protected static AdjustmentListener scrollBarListener = new AdjustmentListener() {
		public void adjustmentValueChanged(AdjustmentEvent e) {
			JScrollBar b = (JScrollBar)e.getSource();
			ColorPalette cp = (ColorPalette)b.getParent();
			cp.requestFocus();
			cp.repaint();
		}
	};
	
	public ScrollableColorPaletteUI() {}
	
	protected final int getVerticalScrollValue(ColorPalette cp) {
		JScrollBar vBar = getVerticalScrollBar(cp);
		if(vBar==null)
			return 0;
		return vBar.getValue();
	}
	
	protected final int getHorizontalScrollValue(ColorPalette cp) {
		JScrollBar hBar = getHorizontalScrollBar(cp);
		if(hBar==null)
			return 0;
		return hBar.getValue();
	}
	
	protected abstract int getVerticalScrollMax(ColorPalette cp);
	
	protected abstract int getHorizontalScrollMax(ColorPalette cp);
	
	static final MouseWheelListener mouseWheelListener = new MouseWheelListener() {
		public void mouseWheelMoved(MouseWheelEvent e) {
			ColorPalette cp = (ColorPalette)e.getSource();
			ScrollableColorPaletteUI ui = (ScrollableColorPaletteUI)cp.getUI();
			
			int dx = 0;
			int dy = 0;
			if( ( e.getModifiers() & InputEvent.SHIFT_MASK ) == 0 ) {
				dy = e.getWheelRotation();
			} else {
				dx = e.getWheelRotation();
			}
			ui.scroll(cp,dx,dy);
		}
	};
	
	protected boolean isWrapping(ColorPalette cp) {
		Boolean b = (Boolean)cp.getClientProperty("wrapAround");
		if(b==null) b = Boolean.FALSE;
		return b.booleanValue();
	}
	
	protected void scroll(ColorPalette cp,int dx,int dy) {
		JScrollBar hBar = getHorizontalScrollBar(cp);
		JScrollBar vBar = getVerticalScrollBar(cp);
		
		int x = (hBar==null) ? dx : hBar.getValue()+dx;
		int y = (vBar==null) ? dy : vBar.getValue()+dy;
		
		int xMax = (hBar==null) ? 1 : hBar.getMaximum();
		int yMax = (vBar==null) ? 1 : vBar.getMaximum();
		if(isWrapping(cp)) {
			while(x<0) {
				x = x+(xMax+1);
			}
			if(x>xMax) {
				x = x%(xMax+1);
			}
			while(y<0) {
				y = y+(yMax+1);
			}
			if(y>yMax) {
				y = y%(yMax+1);
			}
		} else {
			if(x<0)
				x = 0;
			if(x>xMax)
				x = xMax;
			if(y<0)
				y = 0;
			if(y>yMax)
				y = yMax;
		}
		
		if(hBar!=null)
			hBar.setValue(x);
		if(vBar!=null)
			vBar.setValue(y);
		
		cp.repaint();
	}
	
	public final JScrollBar getHorizontalScrollBar(ColorPalette cp) {
		for(int index = 0; index<cp.getComponentCount(); index++) {
			Component comp = cp.getComponent(index);
			if(comp instanceof JScrollBar) {
				JScrollBar jsb = (JScrollBar)comp;
				if(jsb.getOrientation()==Adjustable.HORIZONTAL) {
					return jsb;
				}
			}
		}
		
		int max = getHorizontalScrollMax(cp);
		if(max>1) {
			JScrollBar hBar = new JScrollBar(Adjustable.HORIZONTAL, 0, 0, 0, max);
			hBar.addAdjustmentListener(scrollBarListener);
			cp.add(hBar);
			hBar.putClientProperty("JComponent.sizeVariant", "mini");
			hBar.setCursor(Cursor.getDefaultCursor());
			updateScrollBarBounds(cp);
			return hBar;
		}
		
		
		return null;
	}
	
	public final JScrollBar getVerticalScrollBar(ColorPalette cp) {
		for(int index = 0; index<cp.getComponentCount(); index++) {
			Component comp = cp.getComponent(index);
			if(comp instanceof JScrollBar) {
				JScrollBar jsb = (JScrollBar)comp;
				if(jsb.getOrientation()==Adjustable.VERTICAL) {
					return jsb;
				}
			}
		}
		
		int max = getVerticalScrollMax(cp);
		if(max>1) {
			JScrollBar vBar = new JScrollBar(Adjustable.VERTICAL, 0, 0, 0, max);
			vBar.addAdjustmentListener(scrollBarListener);
			cp.add(vBar);
			vBar.putClientProperty("JComponent.sizeVariant", "mini");
			vBar.setCursor(Cursor.getDefaultCursor());
			updateScrollBarBounds(cp);
			return vBar;
		}
		
		
		return null;
	}
	
	protected final void updateScrollBarBounds(ColorPalette cp) {
		Boolean show = (Boolean)cp.getClientProperty("showScrollBars");
		if(show==null) show = Boolean.TRUE;
		
		JScrollBar hBar = getHorizontalScrollBar(cp);
		JScrollBar vBar = getVerticalScrollBar(cp);
		
		Rectangle paletteBounds = getImageBounds(cp);

		if(hBar!=null && vBar!=null && show.booleanValue()) {
			Dimension dh = hBar.getPreferredSize();
			Dimension dv = vBar.getPreferredSize();
			hBar.setBounds(paletteBounds.x,
					paletteBounds.y+paletteBounds.height,
					paletteBounds.width, dh.height);

			vBar.setBounds(paletteBounds.x+paletteBounds.width,
					paletteBounds.y, dv.width, paletteBounds.height);
		} else if(hBar!=null && show.booleanValue()) {
			Dimension d = hBar.getPreferredSize();
			hBar.setBounds(paletteBounds.x,
					paletteBounds.y+paletteBounds.height,
					paletteBounds.width, d.height);
		} else if(vBar!=null && show.booleanValue()) {
			Dimension d = vBar.getPreferredSize();

			vBar.setBounds(paletteBounds.x+paletteBounds.width,
					paletteBounds.y, d.width, paletteBounds.height);
		}
	}
	
	@Override
	protected final Insets getImageInsets(ColorPalette cp) {
		Insets insets = super.getImageInsets(cp);

		Boolean show = (Boolean)cp.getClientProperty("showScrollBars");
		if(show==null) show = Boolean.TRUE;
		
		if(show.booleanValue()) {
			JScrollBar hBar = getHorizontalScrollBar(cp);
			JScrollBar vBar = getVerticalScrollBar(cp);
			
			if(hBar!=null) {
				insets.bottom += hBar.getHeight();
			}
			if(vBar!=null) {
				insets.right += vBar.getWidth();
			}
		}
		return insets;
	}

	@Override
	public void installUI(final JComponent c) {
		super.installUI(c);
		c.addPropertyChangeListener("showScrollBars", showScrollBarsListener);
		c.addMouseWheelListener(mouseWheelListener);
		c.addComponentListener(componentListener);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		c.removePropertyChangeListener("showScrollBars", showScrollBarsListener);
		c.removeMouseWheelListener(mouseWheelListener);
		c.removeComponentListener(componentListener);
	}

	@Override
	protected void processKeyEvent(KeyEvent keyEvent,int dx,int dy) {
		if( (keyEvent.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) > 0) {
			ColorPalette cp = (ColorPalette)keyEvent.getSource();
			
			keyEvent.consume();
			
			scroll(cp, dx, dy);
			return;
		}
		super.processKeyEvent(keyEvent, dx, dy);
	}
}