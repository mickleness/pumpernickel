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
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/**
 * This resembles the L&amp;F used on XP for buttons that have no visible
 * L&amp;F until they are rolled over.
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/resources/filledbuttonui/XPSubtleButtonUI.png"
 * alt="XPSubtleButtonUI Screenshot">
 **/
public class XPSubtleButtonUI extends QButtonUI {
	private static final Color strokeColor = new Color(0xCECDC3);
	private static final Color shadowHighlight = new Color(255, 255, 255, 120);
	private static final Color[] normalColors = new Color[] {
			new Color(255, 255, 255), new Color(240, 240, 240) };
	private static final Color[] pressedColors = new Color[] {
			new Color(230, 230, 224), new Color(224, 224, 215) };

	public static final ButtonShape XP_SHAPE = new ButtonShape(3, 3);
	public static final ButtonFill XP_FILL = new ButtonFill() {
		@Override
		public Paint getStroke(AbstractButton button, Rectangle fillRect) {
			return strokeColor;
		}

		@Override
		public Paint getFill(AbstractButton button, Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("xp.subtle.normal",
					fillRect.height, fillRect.y, weights, normalColors);
		}

		@Override
		public Color getShadowHighlight(AbstractButton button) {
			return shadowHighlight;
		}
	};

	private static XPSubtleButtonUI xpSubtleButtonUI = new XPSubtleButtonUI();

	/**
	 * Create a new instance of this ButtonUI for a component.
	 * <p>
	 * This method is required if you want to make this ButtonUI the default UI
	 * by invoking: <br>
	 * <code>UIManager.getDefaults().put("ButtonUI", "com.pump.plaf.XPSubtleButtonUI");</code>
	 */
	public static ComponentUI createUI(JComponent c) {
		return xpSubtleButtonUI;
	}

	public XPSubtleButtonUI() {
		super(XP_FILL, XP_SHAPE);
	}

	private static final float[] weights = new float[] { 0, 1 };

	private static final Color[] shadow = new Color[] { new Color(0, 0, 0, 25),
			new Color(0, 0, 0, 18), new Color(0, 0, 0, 10) };

	private static final Color[] highlight = new Color[] {
			new Color(234, 233, 227), new Color(242, 241, 238) };

	/**
	 * This returns PaintFocus.NONE, because paintBackground() handles rendering
	 * the focus.
	 */
	@Override
	public PaintFocus getFocusPainting(AbstractButton button) {
		return PaintFocus.NONE;
	}

	@Override
	public void paintBackground(Graphics2D g, ButtonInfo info) {
		super.paintBackground(g, info);

		g = (Graphics2D) g.create();

		g.clip(info.fill);

		if (info.button.getModel().isPressed()
				|| info.button.getModel().isArmed()
				|| info.button.getModel().isSelected()
				|| QButtonUI.isSpacebarPressed(info.button)) {
			if (info.button.isContentAreaFilled()) {
				g.setPaint(PlafPaintUtils.getVerticalGradient(
						"xp.subtle.pressed", info.fillBounds.height,
						info.fillBounds.y, weights, pressedColors));
				g.fill(info.fillBounds);
			}
			g.setStroke(new BasicStroke(1));
			g.setColor(shadow[0]);
			g.translate(0, 1);
			g.draw(info.fill);
			g.setColor(shadow[1]);
			g.translate(1, 1);
			g.draw(info.fill);
			g.setColor(shadow[2]);
			g.translate(1, 1);
			g.draw(info.fill);
			g.translate(-2, -3);

			if (info.button.isContentAreaFilled()) {
				// this just looks weird if there's no content...
				g.setColor(highlight[0]);
				g.translate(0, -2);
				g.draw(info.fill);
				g.setColor(highlight[1]);
				g.translate(0, 1);
				g.draw(info.fill);
			}
		}
		g.dispose();
	}

	MouseListener rolloverListener = new MouseAdapter() {

		@Override
		public void mouseEntered(MouseEvent e) {
			AbstractButton c = (AbstractButton) e.getSource();
			c.putClientProperty(ROLLOVER, Boolean.TRUE);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			AbstractButton c = (AbstractButton) e.getSource();
			c.putClientProperty(ROLLOVER, Boolean.FALSE);
		}

	};

	protected void updateContentAndBorder(AbstractButton c) {
		boolean mouseInside = isRollover(c);
		boolean focus = hasFocus(c);
		c.setContentAreaFilled((mouseInside || focus) && c.isEnabled());
		c.setBorderPainted((mouseInside || focus) && c.isEnabled());
	}

	@Override
	public boolean isFillOpaque() {
		return false;
	}

	PropertyChangeListener enabledListener = new PropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent evt) {
			updateContentAndBorder((AbstractButton) evt.getSource());
		}

	};

	FocusListener focusListener = new FocusListener() {

		public void focusGained(FocusEvent e) {
			updateContentAndBorder((AbstractButton) e.getSource());
		}

		public void focusLost(FocusEvent e) {
			updateContentAndBorder((AbstractButton) e.getSource());
		}

	};

	PropertyChangeListener updatePropertyListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent e) {
			updateContentAndBorder((AbstractButton) e.getSource());
		}
	};

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.addMouseListener(rolloverListener);
		c.addPropertyChangeListener("enabled", enabledListener);
		c.addFocusListener(focusListener);
		c.addPropertyChangeListener(QButtonUI.ROLLOVER, updatePropertyListener);
		updateContentAndBorder((AbstractButton) c);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		c.removeMouseListener(rolloverListener);
		c.removeFocusListener(focusListener);
		c.removePropertyChangeListener("enabled", enabledListener);
		c.removePropertyChangeListener(QButtonUI.ROLLOVER,
				updatePropertyListener);
	}
}
