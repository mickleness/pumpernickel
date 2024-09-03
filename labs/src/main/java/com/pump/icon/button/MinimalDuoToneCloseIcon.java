/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.icon.button;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;

import com.pump.plaf.PlafPaintUtils;
import com.pump.data.BooleanProperty;

/**
 * This close icon resembles the close icon in Safari's tabs. It featured a
 * light gray rounded rectangle that is 10x10, and inside is a single-pixel X.
 */
public class MinimalDuoToneCloseIcon extends ButtonIcon {

	public static final ButtonIconColors COLORS = new ButtonIconColors() {

		@Override
		public Map<String, Color> getColors(ButtonState state) {
			Map<String, Color> returnValue = new HashMap<>();
			Color focusColor = PlafPaintUtils.getFocusRingColor();
			Color emptyFocusColor = new Color(focusColor.getRed(),
					focusColor.getGreen(), focusColor.getBlue(), 0);
			if (state.isEnabled() && (state.isPressed() || state.isSelected())) {
				returnValue.put(COLOR_BACKGROUND, new Color(0x44000000, true));
				returnValue.put(COLOR_FOREGROUND, new Color(0xBB000000, true));
				returnValue.put(COLOR_FOCUS, state.isFocusOwner() ? focusColor
						: emptyFocusColor);
			} else if (state.isEnabled()
					&& (state.isArmed() || state.isFocusOwner())) {
				returnValue.put(COLOR_BACKGROUND, new Color(0x33000000, true));
				returnValue.put(COLOR_FOREGROUND, new Color(0x99000000, true));
				returnValue.put(COLOR_FOCUS, state.isFocusOwner() ? focusColor
						: emptyFocusColor);
			} else if (state.isEnabled()
					&& (state.isRollover() || state.isFocusOwner())) {
				returnValue.put(COLOR_BACKGROUND, new Color(0x15000000, true));
				returnValue.put(COLOR_FOREGROUND, new Color(0x88000000, true));
				returnValue.put(COLOR_FOCUS, state.isFocusOwner() ? focusColor
						: emptyFocusColor);
			} else if (state.isEnabled()
					&& Boolean.TRUE.equals(state
							.getProperty(PROPERTY_PARENT_ROLLOVER))) {
				returnValue.put(COLOR_BACKGROUND, new Color(0x00000000, true));
				returnValue.put(COLOR_FOREGROUND, new Color(0x88000000, true));
				returnValue.put(COLOR_FOCUS, emptyFocusColor);
			} else {
				returnValue.put(COLOR_BACKGROUND, new Color(0x00000000, true));
				returnValue.put(COLOR_FOREGROUND, new Color(0x00000000, true));
				returnValue.put(COLOR_FOCUS, emptyFocusColor);

			}
			return returnValue;
		}

	};

	public static final String COLOR_BACKGROUND = "background";
	public static final String COLOR_FOCUS = "focus";
	public static final String COLOR_FOREGROUND = "foreground";

	private static final String PROPERTY_PARENT_ROLLOVER = "parentRollover";

	Insets insets = new Insets(4, 4, 4, 4);
	int xWidth = 10;
	int xHeight = 10;
	BooleanProperty parentRollover;

	public MinimalDuoToneCloseIcon() {
	}

	public MinimalDuoToneCloseIcon(final AbstractButton button) {
		super(button, COLORS, new BooleanProperty(PROPERTY_PARENT_ROLLOVER));
		parentRollover = (BooleanProperty) buttonIconManager
				.getProperty(PROPERTY_PARENT_ROLLOVER);
	}

	/**
	 * Return an optional property that is used to render this icon with a
	 * half-indicated (rollover) look.
	 * <p>
	 * When this property is true it indicates the mouse is nearby (in a parent
	 * panel), but it may-or-may-not be directly over this button.
	 * <p>
	 * This property is null if construct this icon using the empty constructor.
	 */
	public BooleanProperty getParentRollover() {
		return parentRollover;
	}

	@Override
	protected void paintIcon(Component c, Graphics2D g) {
		Color background = colors.get(COLOR_BACKGROUND);
		Color foreground = colors.get(COLOR_FOREGROUND);
		Color focus = colors.get(COLOR_FOCUS);

		if (background == null)
			throw new IllegalStateException("The color \"" + COLOR_BACKGROUND
					+ "\" is required");
		if (foreground == null)
			throw new IllegalStateException("The color \"" + COLOR_BACKGROUND
					+ "\" is required");
		if (focus == null)
			throw new IllegalStateException("The color \"" + COLOR_FOCUS
					+ "\" is required");

		Shape body1 = new RoundRectangle2D.Float(0, 0, getIconWidth() - 1,
				getIconHeight() - 1, 5, 5);
		PlafPaintUtils.paintFocus(g, body1, 1, focus);

		Shape body2 = new RoundRectangle2D.Float(0, 0, getIconWidth(),
				getIconHeight(), 5, 5);

		g.setColor(background);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.fill(body2);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		g.setColor(foreground);
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);
		g.drawLine(insets.left, insets.top, insets.left + xWidth - 1,
				insets.top + xHeight - 1);
		g.drawLine(insets.left, insets.top + xHeight - 1, insets.left + xWidth
				- 1, insets.top);
	}

	@Override
	protected int getDefaultIconWidth() {
		return 18;
	}

	@Override
	protected int getDefaultIconHeight() {
		return 18;
	}
}