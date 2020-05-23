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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SliderUI;

/**
 * This UI renders a <code>JSlider</code> as a circular dial, and then value of
 * the slider represents an angle from [0,2*pi).
 * <p>
 * This class (and subclasses) can not respond to several slider properties such
 * as orientation, inversion, tickmarks, labels, etc.
 * 
 * @see <a href=
 *      "https://javagraphics.blogspot.com/2008/05/angles-need-gui-widget-for-angles.html">Angles:
 *      need GUI widget for angles?</a>
 */
public class AngleSliderUI extends SliderUI {

	public static ComponentUI createUI(JComponent c) {
		// this used to create an AquaSliderUI on Mac, but now everyone
		// (including Apple) is trending towards a flat UI.
		return new AngleSliderUI();
	}

	/** Mutable data associated with each AngleSliderUI. */
	protected static class Data {

		/**
		 * The dial that this slider paints.
		 * <P>
		 * This is updated in the <code>calculateGeometry()</code>.
		 */
		protected Ellipse2D dial;

		/**
		 * The insets to pad the dial with. If the focus is painted via a ring
		 * outside the dial, then this needs to allow for that ring.
		 */
		protected Insets insets = new Insets(4, 4, 5, 5);

		/**
		 * Indicate when the mouse is pressed and dragged around this component.
		 * 
		 */
		protected boolean mousePressed = false;

		protected Dimension lastSize = null;
	}

	protected static String DATA_KEY = AngleSliderUI.class.getName() + ".data";
	private static String REPAINT_CHANGE_LISTENER_KEY = AngleSliderUI.class
			.getName() + ".rcl";

	/** Return the Data object associated with a slider. */
	protected Data getData(JSlider slider) {
		Data data = (Data) slider.getClientProperty(DATA_KEY);
		if (data == null) {
			data = new Data();
			slider.putClientProperty(DATA_KEY, data);
		}

		Dimension size = new Dimension(slider.getWidth(), slider.getHeight());
		if (!Objects.equals(size, data.lastSize)) {
			data.lastSize = size;
			calculateGeometry(slider);
		}

		return data;
	}

	/**
	 * Responds to arrow keys.
	 */
	protected KeyListener keyListener = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			JSlider slider = (JSlider) e.getComponent();
			int k = e.getKeyCode();
			if (k == KeyEvent.VK_LEFT || k == KeyEvent.VK_UP) {
				int i = slider.getValue();
				i--;
				if (i < slider.getMinimum())
					i = slider.getMaximum();
				slider.setValue(i);
			} else if (k == KeyEvent.VK_RIGHT || k == KeyEvent.VK_DOWN) {
				int i = slider.getValue();
				i++;
				if (i > slider.getMaximum())
					i = slider.getMinimum();
				slider.setValue(i);
			}
		}
	};

	/**
	 * Responds to mouse events.
	 */
	protected MouseInputAdapter mouseListener = new MouseInputAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			JSlider slider = (JSlider) e.getComponent();

			if (slider.isEnabled() == false)
				return;

			if (slider.isRequestFocusEnabled()) {
				slider.requestFocus();
			}
			Data data = getData(slider);
			slider.setValueIsAdjusting(true);
			float centerX = (float) data.dial.getCenterX();
			float centerY = (float) data.dial.getCenterY();
			double angle = Math.atan2(e.getY() - centerY, e.getX() - centerX);
			angle = angle / (2 * Math.PI);
			if (angle < 0)
				angle += 1;
			int v = (int) (angle * (slider.getMaximum() - slider.getMinimum())
					+ slider.getMinimum());
			slider.setValue(v);
			data.mousePressed = true;
			e.consume();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			mousePressed(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			JSlider slider = (JSlider) e.getComponent();

			if (slider.isEnabled() == false)
				return;

			slider.setValueIsAdjusting(false);
			Data data = getData(slider);
			data.mousePressed = false;
			calculateGeometry(slider);
		}
	};

	/** Repaints when the focus changes. */
	protected FocusListener focusListener = new FocusListener() {
		public void focusGained(FocusEvent e) {
			e.getComponent().repaint();
		}

		public void focusLost(FocusEvent e) {
			e.getComponent().repaint();
		}
	};

	/**
	 * This updates/defines the <code>dial</code> field of this object, based on
	 * the size of this component and the <code>insets</code> field. The
	 * <code>paint()</code> method should use the <code>dial</code> field to
	 * render the slider.
	 * 
	 */
	protected void calculateGeometry(JSlider slider) {
		Data data = getData(slider);
		if (data.dial == null)
			data.dial = new Ellipse2D.Float();
		Dimension size = slider.getSize();
		Insets i = getBorderInsets(slider);

		float r = Math.min(
				size.width - data.insets.left - data.insets.right - i.left
						- i.right,
				size.height - data.insets.top - data.insets.bottom - i.top
						- i.bottom)
				/ 2f;
		float centerX = size.width / 2f;
		float centerY = size.height / 2f;
		data.dial.setFrame(centerX - r, centerY - r, 2 * r, 2 * r);
		slider.repaint();
	}

	@Override
	public Dimension getMaximumSize(JComponent c) {
		Dimension d = getPreferredBaseDimension(c);
		d.width *= 2;
		d.height *= 2;
		return d;
	}

	@Override
	public Dimension getMinimumSize(JComponent c) {
		return getPreferredSize(c);
	}

	/**
	 * Return the insets for the border, or an empty Insets object.
	 */
	protected Insets getBorderInsets(JComponent c) {
		Border b = c.getBorder();
		Insets i = (b == null) ? new Insets(0, 0, 0, 0) : b.getBorderInsets(c);
		return i;
	}

	/**
	 * Returns the preferred dimensions for this JSlider without a border.
	 */
	protected Dimension getPreferredBaseDimension(JComponent c) {
		return new Dimension(40, 40);
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		Dimension base = getPreferredBaseDimension(c);
		Insets i = getBorderInsets(c);
		base.width += i.left + i.right;
		base.height += i.top + i.bottom;
		return base;
	}

	private static class RepaintChangeListener implements ChangeListener {
		final JComponent jc;

		RepaintChangeListener(JComponent jc) {
			this.jc = jc;
		}

		public void stateChanged(ChangeEvent e) {
			jc.repaint();
		}
	}

	@Override
	public void installUI(JComponent c) {
		JSlider slider = (JSlider) c;
		slider.addFocusListener(focusListener);
		slider.addMouseListener(mouseListener);
		slider.addMouseMotionListener(mouseListener);
		RepaintChangeListener rcl = new RepaintChangeListener(c);
		slider.getModel().addChangeListener(rcl);
		slider.putClientProperty(REPAINT_CHANGE_LISTENER_KEY, rcl);
		slider.addKeyListener(keyListener);
		slider.setFocusable(true);
		slider.setRequestFocusEnabled(true);
		calculateGeometry((JSlider) c);
	}

	public static Color tweenColor(Color c1, Color c2, float progress) {
		if (c1.equals(c2))
			return c1;
		int r = (int) (c1.getRed() * (1 - progress) + c2.getRed() * progress
				+ .5f);
		int g = (int) (c1.getGreen() * (1 - progress) + c2.getGreen() * progress
				+ .5f);
		int b = (int) (c1.getBlue() * (1 - progress) + c2.getBlue() * progress
				+ .5f);
		int a = (int) (c1.getAlpha() * (1 - progress) + c2.getAlpha() * progress
				+ .5f);
		return new Color(r, g, b, a);
	}

	@Override
	public void paint(Graphics g0, JComponent c) {
		Graphics2D g = (Graphics2D) g0.create();
		JSlider slider = (JSlider) c;
		if (slider.isOpaque()) {
			g.setColor(slider.getBackground());
			g.fillRect(0, 0, slider.getWidth(), slider.getHeight());
		}

		if (slider.isEnabled() == false) {
			g.setComposite(
					AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f));
		}

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		// paint a circular shadow
		/*
		 * g.translate(2,2); g.setColor(new Color(0,0,0,20)); g.fill(new
		 * Ellipse2D
		 * .Double(dial.getX()-2,dial.getY()-2,dial.getWidth()+4,dial.getHeight
		 * ()+4)); g.setColor(new Color(0,0,0,40)); g.fill(new
		 * Ellipse2D.Double(dial
		 * .getX()-1,dial.getY()-1,dial.getWidth()+2,dial.getHeight()+2));
		 * g.setColor(new Color(0,0,0,80)); g.fill(new
		 * Ellipse2D.Double(dial.getX
		 * ()-0,dial.getY()-0,dial.getWidth()+0,dial.getHeight()+0));
		 * g.translate(-2,-2);
		 */

		Data data = getData(slider);
		if (slider.hasFocus()) {
			PlafPaintUtils.paintFocus(g, data.dial, 3);
		}

		// g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		// RenderingHints.VALUE_ANTIALIAS_OFF);

		Shape oldClip = g.getClip();
		g.clip(data.dial);
		int d = data.mousePressed ? -50 : 0;
		for (int a = 0; a < 5; a++) {
			float f = (a) / 4f;
			g.setColor(tweenColor(new Color(150 + d, 150 + d, 150 + d),
					new Color(212 + d, 212 + d, 212 + d), f));
			g.translate(0, a);
			g.fill(data.dial);
			g.translate(0, -a);
		}
		g.setClip(oldClip);

		g.setColor(border);
		g.draw(data.dial);

		float angle = (float) (slider.getValue() - slider.getMinimum())
				/ (((float) (slider.getMaximum() - slider.getMinimum())));
		angle = angle * (float) (2 * Math.PI);

		float centerX = (float) data.dial.getCenterX();
		float centerY = (float) data.dial.getCenterY();
		float radius = (float) Math.min(centerX - data.dial.getX(),
				centerY - data.dial.getY()) - 6;
		float x = (float) (centerX + radius * Math.cos(angle));
		float y = (float) (centerY + radius * Math.sin(angle));
		Ellipse2D knob = new Ellipse2D.Float(x - 2, y - 2, 4, 4);
		if (slider.hasFocus()) {
			g.setColor(Color.gray);
			g.fill(knob);
			g.setColor(Color.darkGray);
			g.draw(knob);
		} else {
			g.setColor(new Color(180, 180, 180));
			g.fill(knob);
			g.setColor(border);
			g.draw(knob);
		}

		g.dispose();
	}

	private static final Color border = new Color(100, 100, 100);

	@Override
	public void uninstallUI(JComponent c) {
		JSlider slider = (JSlider) c;
		slider.removeFocusListener(focusListener);
		slider.removeMouseListener(mouseListener);
		slider.removeMouseMotionListener(mouseListener);
		RepaintChangeListener rcl = (RepaintChangeListener) slider
				.getClientProperty(REPAINT_CHANGE_LISTENER_KEY);
		slider.getModel().removeChangeListener(rcl);
		slider.removeKeyListener(keyListener);
	}
}