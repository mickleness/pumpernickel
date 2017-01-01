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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.plaf.ComponentUI;

import com.pump.swing.JThrobber;

/** The ComponentUI for {@link com.bric.swing.JThrobber}.
 */
public abstract class ThrobberUI extends ComponentUI {
	
	protected static String TIMER_KEY = ThrobberUI.class.getName()+"Timer";
	
	/** An optional client property to define the period of this animation (if the animation
	 * has a period).
	 */
	public static final String PERIOD_KEY = ThrobberUI.class.getName()+".period";
	
	/** An optional client property to slow the default period by a fixed interval.
	 */
	public static final String PERIOD_MULTIPLIER_KEY = ThrobberUI.class.getName()+".period-multiplier";

    public static ComponentUI createUI(JComponent c) {
        return new AquaThrobberUI();
    }
    
    protected final int repaintInterval;

    /**
     * @param repaintInterval The number of milliseconds between repaints.
     */
    protected ThrobberUI(int repaintInterval) {
    	this.repaintInterval = repaintInterval;
    }
    
    /** Return the period (in milliseconds) of this throbber.
     * <p>This is a convenience method that takes the argument
     * defaultPeriod and modifies it according to {@link #PERIOD_KEY}
     * or {@link #PERIOD_MULTIPLIER_KEY}
     * @param jc the component to inspect. If null then the default
     * period is immediately returned.
     * @param defaultPeriod the value to return if no customizations
     * are defined..
     */
    public int getPeriod(JComponent jc,int defaultPeriod) {
    	if(jc!=null) {
	    	Number n = (Number)jc.getClientProperty(PERIOD_KEY);
	    	if(n!=null) return n.intValue();
	    	n = (Number)jc.getClientProperty(PERIOD_MULTIPLIER_KEY);
	    	if(n!=null) {
	    		return (int)(defaultPeriod*n.floatValue()+.5);
	    	}
    	}
    	return defaultPeriod;
    }

	@Override
	public void paint(Graphics g, JComponent jc) {
		paint(g, jc, null);
	}

	/**
	 * 
	 * @param g0
	 * @param jc the component may be null
	 * @param fixedFraction an optional fixed fraction from [0, 1] representing
	 * the state of this animation. If null: then this should be derived from
	 * the current time.
	 */
	protected void paint(Graphics g0, JComponent jc,Float fixedFraction) {
		Graphics2D g = (Graphics2D) g0.create();
		try {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
			g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			paintBackground(g,jc);

			if( jc!=null && !((JThrobber)jc).isActive() )
				return;

			Dimension d = getPreferredSize(jc);
			if(jc!=null) {
				if(d==null) d = new Dimension(16, 16);
				double sx = ((double)jc.getWidth())/((double)d.width);
				double sy = ((double)jc.getHeight())/((double)d.height);
				double scale = Math.min(sx,sy);
				g.scale(scale,scale);
			}
			
			paintForeground(g,jc,d,fixedFraction);
		} finally {
			g.dispose();
		}
	}
	
	/** Paint the background color, if the argument is opaque. */
	protected void paintBackground(Graphics2D g,JComponent jc) {
		if(jc!=null && jc.isOpaque()) {
			g.setColor(jc.getBackground());
			g.fillRect(0, 0, jc.getWidth(), jc.getHeight());
		}
	}

	/** Paints the foreground. The Graphics2D passed to this object
	 * is configured as if you were painting <code>size</code> argument
	 * (which is the preferred size). So if the preferred size of this
	 * ThrobberUI is 16x16, you always paint as if you're painting to a
	 * 16x16 area.
	 * 
	 * @param g the graphics to paint to.
	 * @param jc the component to paint, this may be null if
	 * used as an Icon.
	 * @param size the dimensions to paint to.
	 * Assume these are the dimensions of the component you
	 * are painting (the Graphics2D has been transformed to
	 * work within these dimensions).
	 * @param fixedFraction an optional fixed fraction from [0, 1] representing
	 * the state of this animation. If null: then this should be derived from
	 * the current time.
	 */
	protected abstract void paintForeground(Graphics2D g,JComponent jc,Dimension size,Float fixedFraction);

	@Override
	public void installUI(final JComponent c) {
		super.installUI(c);
		ActionListener repaintActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if( ((JThrobber)c).isActive() )
					c.repaint();
			}
		};
		Color foreground = getDefaultForeground();
		if(foreground!=null) {
			c.setForeground(foreground);
		}
		c.addPropertyChangeListener( JThrobber.KEY_ACTIVE, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				c.repaint();
			}
		});
		Timer timer = new Timer( repaintInterval, repaintActionListener);
		timer.start();
		c.putClientProperty(TIMER_KEY, timer);
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		return getPreferredSize();
	}
	
	/** Return the default foreground color for this throbber. If
	 * this is not null: then during <code>installUI()</code>
	 * this will be assigned as the new foreground color.
	 */
	public abstract Color getDefaultForeground();
	
	public abstract Dimension getPreferredSize();

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		Timer timer = (Timer)c.getClientProperty(TIMER_KEY);
		if(timer!=null)
			timer.stop();
	}

	/** Create an Icon representation of this ThrobberUI.
	 * 
	 * @param fixedFraction the fraction from [0,1] indicating this icon's animation progress,
	 * or null if this icon should update based on <code>System.currentTimeMillis()</code>.
	 * @param size the dimensions of this icon, or null if the default size should be used.
	 */
	public Icon createIcon(Float fixedFraction,Dimension size) {
		return new ThrobberIcon(this, fixedFraction, size);
	}

	private static class ThrobberIcon implements Icon {
		final int width;
		final int height;
		final Float fixedFraction;
		final ThrobberUI ui;
		final Dimension defaultSize;

		/**
		 * 
		 * @param fraction the fraction from [0,1] indicating this icon's animation progress,
		 * or null if this icon should update based on <code>System.currentTimeMillis()</code>.
		 */
		public ThrobberIcon(ThrobberUI ui,Float fraction,Dimension size) {
			if(ui==null) {
				throw new NullPointerException();
			}
			if(fraction!=null) {
				if(fraction<0 || fraction>1) throw new IllegalArgumentException("fraction ("+fraction+") must be null or a float between [0, 1]");
			}
			
			defaultSize = ui.getPreferredSize();
			this.ui = ui;
			this.fixedFraction = fraction;
			if(size==null) {
				size = defaultSize;
			}
			width = size.width;
			height = size.height;
		}

		public int getIconHeight() {
			return height;
		}

		public int getIconWidth() {
			return width;
		}

		public void paintIcon(Component c, Graphics g0, int x, int y) {
			Graphics2D g = (Graphics2D)g0.create();
			try {
				g.translate(x, y);
				double sx = (width)/((float)defaultSize.width);
				double sy = (height)/((float)defaultSize.height);
				g.scale(sx, sy);
				
				ui.paint(g, null, fixedFraction);
			} finally {
				g.dispose();
			}
		}
	}
}