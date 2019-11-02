package com.pump.icon;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JComponent;

import com.pump.plaf.AnimationManager;
import com.pump.plaf.PlafPaintUtils;
import com.pump.plaf.SwitchButtonUI;

/**
 * This is the icon used by SwitchButtonUIs.
 */
public class SwitchButtonIcon implements Icon {

	static class Colors {
		public Color outlineTop, outlineBottom, backgroundTop,
				backgroundBottom, backgroundTopArmed, backgroundBottomArmed,
				handleFill, handleFillArmed;

		Colors(int outlineTop, int outlineBottom, int backgroundTop,
				int backgroundBottom, int backgroundTopArmed,
				int backgroundBottomArmed, int handleFill, int handleFillArmed) {
			this(new Color(outlineTop), new Color(outlineBottom), new Color(
					backgroundTop), new Color(backgroundBottom), new Color(
					backgroundTopArmed), new Color(backgroundBottomArmed),
					new Color(handleFill), new Color(handleFillArmed));
		}

		Colors(Color outlineTop, Color outlineBottom, Color backgroundTop,
				Color backgroundBottom, Color backgroundTopArmed,
				Color backgroundBottomArmed, Color handleFill,
				Color handleFillArmed) {
			this.outlineTop = outlineTop;
			this.outlineBottom = outlineTop;
			this.backgroundTop = backgroundTop;
			this.backgroundBottom = backgroundBottom;
			this.backgroundTopArmed = backgroundTopArmed;
			this.backgroundBottomArmed = backgroundBottomArmed;
			this.handleFill = handleFill;
			this.handleFillArmed = handleFillArmed;
		}

		Colors tween(Colors other, double fraction) {
			fraction = Math.max(0, Math.min(1, fraction));
			return new Colors(AnimationManager.tween(outlineTop,
					other.outlineTop, fraction), AnimationManager.tween(
					outlineBottom, other.outlineBottom, fraction),
					AnimationManager.tween(backgroundTop, other.backgroundTop,
							fraction), AnimationManager.tween(backgroundBottom,
							other.backgroundBottom, fraction),
					AnimationManager.tween(backgroundTopArmed,
							other.backgroundTopArmed, fraction),
					AnimationManager.tween(backgroundBottomArmed,
							other.backgroundBottomArmed, fraction),
					AnimationManager.tween(handleFill, other.handleFill,
							fraction), AnimationManager.tween(handleFillArmed,
							other.handleFillArmed, fraction));
		}
	}

	static final String PROPERTY_SELECTED_STATE = SwitchButtonIcon.class
			.getName() + "#selectedState";
	static final String PROPERTY_ARMED_STATE = SwitchButtonIcon.class.getName()
			+ "#armedState";

	static Colors unselected = new Colors(0xd8d8d8, 0xededed, 0xebebeb,
			0xf4f4f4, 0xe0e0e0, 0xeaeaea, 0xffffff, 0xf0f0f0);
	static Colors selected = new Colors(0x275aea, 0x4684f6, 0x2963ff, 0x4787fe,
			0x2963ff, 0x4787fe, 0xffffff, 0xf0f0f0);
	int focusOffset = 3;

	@Override
	public void paintIcon(Component c0, Graphics g0, int x, int y) {
		JComponent c = (JComponent) c0;
		Rectangle iconRect = new Rectangle(x, y, getIconWidth(),
				getIconHeight());
		c.putClientProperty(SwitchButtonUI.PROPERTY_ICON_RECT, iconRect);
		Graphics2D g = (Graphics2D) g0.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		int w = getIconWidth() - 2 * focusOffset;
		int h = getIconHeight() - 2 * focusOffset;
		g.translate(focusOffset, focusOffset);
		RoundRectangle2D r = new RoundRectangle2D.Float(x, y, w, h, h, h);
		boolean isSelected = isSelected(c);
		boolean isArmed = isArmed(c);
		double selectedState = AnimationManager.setTargetProperty(c,
				PROPERTY_SELECTED_STATE, isSelected ? 1 : 0, 20, .2);
		double armedState = AnimationManager.setTargetProperty(c,
				PROPERTY_ARMED_STATE, isArmed ? 1 : 0, 20, .2);

		Colors colors = unselected.tween(selected, selectedState);
		Color handleColor = AnimationManager.tween(colors.handleFill,
				colors.handleFillArmed, armedState);

		Shape handle = new Ellipse2D.Float(x, y, h, h);

		double dx = selectedState * (r.getWidth() - (h - 1));
		AffineTransform tx = AffineTransform.getTranslateInstance(dx, 0);
		handle = tx.createTransformedShape(handle);

		g.setStroke(new BasicStroke(1));

		if (c.isFocusOwner())
			PlafPaintUtils.paintFocus(g, r, focusOffset);

		Color trackFillTop = AnimationManager.tween(colors.backgroundTop,
				colors.backgroundTopArmed, armedState);
		Color trackFillBottom = AnimationManager.tween(colors.backgroundBottom,
				colors.backgroundBottomArmed, armedState);
		g.setPaint(new GradientPaint(x, y, trackFillTop, x, y + h,
				trackFillBottom));
		g.fill(r);

		Paint outlinePaint = new GradientPaint(x, y, colors.outlineTop, x, y
				+ h, colors.outlineBottom);
		g.setPaint(outlinePaint);
		g.draw(r);

		g.setPaint(handleColor);
		g.fill(handle);

		g.setPaint(outlinePaint);
		g.draw(handle);

		g.dispose();
	}

	/**
	 * Return true if this icon should render as if the component is a selected button.
	 */
	protected boolean isSelected(Component c) {
		return c instanceof AbstractButton && ((AbstractButton) c).isSelected();
	}

	/**
	 * Return true if this icon should render as if the component is an armed button.
	 */
	protected boolean isArmed(Component c) {
		return c instanceof AbstractButton
				&& ((AbstractButton) c).getModel().isArmed();
	}

	@Override
	public int getIconWidth() {
		return 38 + 2 * focusOffset;
	}

	@Override
	public int getIconHeight() {
		return 21 + 2 * focusOffset;
	}

}