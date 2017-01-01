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

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;

import com.pump.image.ImageBounds;

/** This retrieves information about the empty pixels
 * in a JComponent.  This class paints the component
 * in an offscreen image and measures the empty pixels.
 * <P>Other subclasses may implement a more efficient
 * means of determining this padding.  (See <code>StoredPaddingInfo</code>.)
 *
 */
public class PaddingInfo {
	
	public static PaddingInfo staticInfo = new StoredPaddingInfo();

	private static BufferedImage scrapImage = null;
	
	/** This returns the empty pixels that pad a component.
	 * If the insets can't be determined empty
	 * insets are returned (0,0,0,0).
	 * 
	 * @param c the component to study.
	 * @return the empty pixels that pad a component
	 * as insets.
	 */
	public synchronized Insets get(Component c) {
		Dimension d = c.getSize();
		if(d.width==0 || d.height==0) {
			c.setSize(c.getPreferredSize());
		}
		
		/** We don't want the focus rings to be part of what we
		 * measure, so we'll try to disable the focus soon:
		 */
		boolean hadFocus = false;
		KeyboardFocusManager m = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		Component focusOwner = m.getFocusOwner();
		
		try {
			
			prep(c);
			
			if(contains(c,focusOwner) || c==focusOwner) {
				/** This is a little heavy-handed, but it's necessary
				 * because "normal" calls to transferFocus() or cycleFocus()
				 * only put events on the event queue, which means the focus
				 * would only be manipulated AFTER this method is finished...
				 * 
				 * But if we dispatch an event ourselves we can 
				 * make sure the focus owner temporarily behaves
				 */
				m.dispatchEvent(new FocusEvent(focusOwner,FocusEvent.FOCUS_LOST,true));
				hadFocus = true;
			}
			
			if(scrapImage==null || 
					scrapImage.getWidth()<c.getWidth() ||
					scrapImage.getHeight()<c.getHeight() ) {
				int w = c.getWidth();
				int h = c.getHeight();
				if(scrapImage!=null) {
					w = Math.max(w, scrapImage.getWidth());
					h = Math.max(h, scrapImage.getHeight());
				}
				if(w==0 || h==0)
					return new Insets(0,0,0,0);
				
				scrapImage = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
			}
			Graphics2D g = scrapImage.createGraphics();
			g.setComposite(AlphaComposite.Clear);
			g.fillRect(0,0,scrapImage.getWidth(),scrapImage.getHeight());
			g.setComposite(AlphaComposite.SrcOver);
			
			c.paint(g);
			g.dispose();
			Rectangle r = ImageBounds.getBounds(scrapImage);
			if(r==null) {
				//boo!  an empty image?  What does that mean?
				return new Insets(0,0,0,0);
			}
			Insets insets = new Insets(r.y, r.x,
					c.getHeight()-r.y-r.height,
					c.getWidth()-r.x-r.width
					);
			
			return insets;
		} finally {
			restore(c);
			if(hadFocus) {	
				m.dispatchEvent(new FocusEvent(focusOwner,FocusEvent.FOCUS_GAINED,true));
			}
		}
	}

	/** Returns true if the parent component contains the child.
	 * (Does not return true if the parent and child arguments are
	 * the same.)
	 * 
	 * @param parent
	 * @param child
	 * @return
	 */
	private static boolean contains(Component parent,Component child) {
		Component c = child;
		while(c!=null) {
			c = c.getParent();
			
			if(parent==c) {
				return true;
			}
		}
		return false;
	}

	/** A key used for the makeNonOpaque() and restoreOpacity() methods. */
	private static final String USED_TO_BE_OPAQUE = "PaddingInfo.usedToBeOpaque";
	private static final String SLIDER_VALUE = "PaddingInfo.sliderValue";
	private static final String CHANGE_LISTENERS = "PaddingInfo.changeListeners";
	private static final String SIZE = "PaddingInfo.size";
	
	private static boolean greaterThanOrEqualTo(Dimension d1,Dimension d2) {
		return (d1.width>=d2.width && d1.height>=d2.height);
	}
	
	/** This makes some visual changes to the component
	 * and its children.  Shortly after calling this method,
	 * the <code>restore()</code> method needs to be called.
	 */
	private static void prep(Component c) {
		if(c instanceof JComponent) {
			JComponent jc = (JComponent)c;
			if(jc.isOpaque()) {
				jc.setOpaque(false);
				jc.putClientProperty(USED_TO_BE_OPAQUE, Boolean.TRUE);
			}

			Dimension preferredSize = c.getPreferredSize();
			if(greaterThanOrEqualTo(jc.getSize(),preferredSize)==false ) {
				jc.putClientProperty(SIZE,c.getSize());
				jc.setSize(preferredSize);
			}
		}
		if(c instanceof JSlider) {
			JSlider s = (JSlider)c;
			ChangeListener[] listeners = s.getChangeListeners();
			int mid = (s.getMinimum()+s.getMaximum())/2;
			if(mid!=s.getValue()) {
				s.putClientProperty(CHANGE_LISTENERS, listeners);
				for(int a = 0; a<listeners.length; a++) {
					s.removeChangeListener(listeners[a]);
				}
				s.putClientProperty(SLIDER_VALUE, new Integer(s.getValue()));
				s.setValue(mid);
			}
		}
		if(c instanceof Container) {
			Container c2 = (Container)c;
			for(int a = 0; a<c2.getComponentCount(); a++) {
				prep(c2.getComponent(a));
			}
		}
		if(c.isValid()==false)
			c.validate();
	}
	
	/** Should be called shortly after <code>prep()</code>.
	 */
	private static void restore(Component c) {
		if(c instanceof JComponent) {
			JComponent jc = (JComponent)c;
			Boolean b = (Boolean)jc.getClientProperty(USED_TO_BE_OPAQUE);
			if(b!=null && b.booleanValue()) {
				jc.setOpaque(true);
			}
			jc.putClientProperty(USED_TO_BE_OPAQUE, null);
			
			Dimension d = (Dimension)jc.getClientProperty(SIZE);
			if(d!=null) {
				jc.setSize(d);
				jc.putClientProperty(SIZE, null);
			}
		}
		if(c instanceof JSlider) {
			JSlider s = (JSlider)c;
			ChangeListener[] listeners = (ChangeListener[])s.getClientProperty(CHANGE_LISTENERS);
			Integer i = (Integer)s.getClientProperty(SLIDER_VALUE);
			if(i!=null)
				s.setValue(i.intValue());
			if(listeners!=null) {
				for(int a = 0; a<listeners.length; a++) {
					s.addChangeListener(listeners[a]);
				}
			}
			s.putClientProperty(SLIDER_VALUE, null);
			s.putClientProperty(CHANGE_LISTENERS, null);
		}
		if(c instanceof Container) {
			Container c2 = (Container)c;
			for(int a = 0; a<c2.getComponentCount(); a++) {
				restore(c2.getComponent(a));
			}
		}
	}
}