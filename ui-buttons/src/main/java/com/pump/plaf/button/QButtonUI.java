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
package com.pump.plaf.button;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicButtonUI;

import com.pump.geom.ShapeBounds;
import com.pump.geom.ShapeUtils;
import com.pump.plaf.Background;
import com.pump.plaf.FocusArrowListener;
import com.pump.plaf.PlafPaintUtils;
import com.pump.plaf.PositionConstants;
import com.pump.plaf.button.ButtonState.Float;
import com.pump.plaf.combobox.QComboBoxUI;

/**
 * A ButtonUI that includes an enclosed filled shape.
 * <P>
 * This <code>ButtonUI</code> supports several options for controlling the shape
 * of separate buttons. (Note one instance of each QButtonUI object can be
 * assigned to several buttons, and each button can be configured differently).
 *
 * @see <a
 *      href="https://javagraphics.blogspot.com/2009/08/buttons-new-uis.html">Buttons:
 *      New UIs</a>
 * 
 */
public abstract class QButtonUI extends BasicButtonUI implements
		PositionConstants {

	/**
	 * Assign this client property to control whether the path stroke should be
	 * painted. The default is assigned to be true.
	 */
	public static final String PROPERTY_STROKE_PAINTED = QButtonUI.class
			.getSimpleName() + "#stroke-painted";

	/**
	 * This optional client property may resolve to a ButtonState.Boolean that
	 * is our target state.
	 */
	public static final String PROPERTY_BOOLEAN_BUTTON_STATE = QButtonUI.class
			.getSimpleName() + "#booleanButtonState";

	/**
	 * This client property resolves to a ButtonState.Float that is our current
	 * rendered state. A timer for animation should automatically update this.
	 */
	public static final String PROPERTY_FLOAT_BUTTON_STATE = QButtonUI.class
			.getSimpleName() + "#floatButtonState";

	static final String PROPERTY_BUTTON_INFO = QButtonUI.class
			.getSimpleName() + "#buttonInfo";

	/**
	 * This client property resolves a Boolean indicating whether a button
	 * should render using a circle or not. This UI assumes only an icon (no
	 * text) will be rendered.
	 */
	public static final String PROPERTY_IS_CIRCLE = QButtonUI.class
			.getSimpleName() + "#isCircle";

	/**
	 * The client property for the horizontal position of this segment resolves
	 * to a HorizontalPosition.
	 */
	public static final String PROPERTY_HORIZONTAL_POSITION = "JButton.segmentHorizontalPosition";

	/**
	 * The client property for the vertical position of this segment resolves to
	 * a VerticalPosition.
	 */
	public static final String PROPERTY_VERTICAL_POSITION = "JButton.segmentVerticalPosition";

	public static enum HorizontalPosition {
		ONLY(PositionConstants.POS_ONLY), LEFT(PositionConstants.POS_LEFT), MIDDLE(
				PositionConstants.POS_MIDDLE), RIGHT(
				PositionConstants.POS_RIGHT);

		int position;

		HorizontalPosition(int position) {
			this.position = position;
		}

		/**
		 * Return the PositionConstants constant associated with this value.
		 */
		public int getPosition() {
			return position;
		}
	}

	public static enum VerticalPosition {
		ONLY(PositionConstants.POS_ONLY), TOP(PositionConstants.POS_TOP), MIDDLE(
				PositionConstants.POS_MIDDLE), BOTTOM(
				PositionConstants.POS_BOTTOM);

		int position;

		VerticalPosition(int position) {
			this.position = position;
		}

		/**
		 * Return the PositionConstants constant associated with this value.
		 */
		public int getPosition() {
			return position;
		}
	}

	/**
	 * Different behaviors to paint the keyboard focus.
	 */
	public static enum PaintFocus {

		/**
		 * Focus should be painted just inside the filled shape.
		 */
		INSIDE,

		/**
		 * Focus should be painted just outside the filled shape. This is taken
		 * into account when allocating the button size, because it adds extra
		 * pixels to the width/height.
		 */
		OUTSIDE;
	};

	protected static HorizontalPosition getHorizontalPosition(JComponent c) {
		Object h = c == null ? null : c
				.getClientProperty(PROPERTY_HORIZONTAL_POSITION);
		if (h == null)
			return HorizontalPosition.ONLY;
		if (h instanceof String) {
			for (HorizontalPosition p : HorizontalPosition.values()) {
				if (p.name().equalsIgnoreCase((String) h))
					return p;
			}
			throw new IllegalArgumentException("Unsupported String \"" + h
					+ "\". Supported values are: "
					+ Arrays.asList(HorizontalPosition.values()));
		}
		return (HorizontalPosition) h;
	}

	protected static VerticalPosition getVerticalPosition(JComponent c) {
		Object v = c == null ? null : c
				.getClientProperty(PROPERTY_VERTICAL_POSITION);
		if (v == null)
			return VerticalPosition.ONLY;
		if (v instanceof String) {
			for (VerticalPosition p : VerticalPosition.values()) {
				if (p.name().equalsIgnoreCase((String) v))
					return p;
			}
			throw new IllegalArgumentException("Unsupported String \"" + v
					+ "\". Supported values are: "
					+ Arrays.asList(VerticalPosition.values()));
		}
		return (VerticalPosition) v;
	}

	/** A static KeyListener for using focus arrow keys. */
	protected static KeyListener focusArrowListener = new FocusArrowListener();

	abstract static class AbstractButtonBackground extends Background {
		protected int focusRingSize;
		protected PaintFocus paintFocus;
		protected ButtonFill buttonFill;

		protected AbstractButtonBackground(ButtonFill buttonFill,
				int focusRingSize, PaintFocus paintFocus) {
			this.buttonFill = buttonFill;
			this.focusRingSize = focusRingSize;
			this.paintFocus = paintFocus;
		}

		@Override
		protected final void paint(Component c, Graphics2D g, int x, int y,
				int width, int height) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			JComponent jc = (JComponent) c;
			AbstractButton button = (jc instanceof AbstractButton) ? (AbstractButton) jc
					: null;
			ButtonState.Float state = (ButtonState.Float) jc
					.getClientProperty(PROPERTY_FLOAT_BUTTON_STATE);
			if (state == null && button != null) {
				state = new ButtonState.Float(button.getModel());
			} else if (state == null) {
				state = new ButtonState.Float(1, 0, 0, 0, 0);
			}

			GeneralPath fillShape = new GeneralPath();
			GeneralPath strokeShape = new GeneralPath();
			GeneralPath paintedPartitions = new GeneralPath();
			GeneralPath focusedPartitions = new GeneralPath();

			defineShapes(jc, button, state, g, x, y, width, height, fillShape,
					strokeShape, paintedPartitions, focusedPartitions);

			Color shadowHighlight = buttonFill.getShadowHighlight(state);
			if (shadowHighlight != null) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.translate(0, 1);
				Color empty = new Color(shadowHighlight.getRed(),
						shadowHighlight.getGreen(), shadowHighlight.getBlue(),
						0);
				GradientPaint gradient = new GradientPaint(x, y, empty, x, y
						+ height, shadowHighlight);
				g2.setPaint(gradient);
				g2.draw(fillShape);
				g2.dispose();
			}

			boolean isFocusPainted = button == null || button.isFocusPainted();
			boolean isStrokePainted = !Boolean.FALSE.equals(jc
					.getClientProperty(PROPERTY_STROKE_PAINTED));
			boolean isContentAreaFilled = button == null
					|| button.isContentAreaFilled();

			Rectangle fillRect = new Rectangle(x, y, width - 1, height - 1);
			Paint fill = isContentAreaFilled ? buttonFill.getFill(state,
					fillRect) : null;

			if (jc.isFocusOwner() && isFocusPainted
					&& paintFocus == PaintFocus.OUTSIDE) {
				Graphics2D g2 = (Graphics2D) g.create();
				if (fill == null
						|| fill.getTransparency() != Transparency.OPAQUE) {
					Area area = new Area(new Rectangle(0, 0, jc.getWidth(),
							jc.getHeight()));
					area.subtract(new Area(fillShape));
					g2.clip(area);
				}
				PlafPaintUtils.paintFocus(g2, fillShape, focusRingSize);
				g2.dispose();
			}

			if (isContentAreaFilled) {
				g.setPaint(fill);
				g.fill(fillShape);
			}
			g.setStroke(new BasicStroke(1));
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g.setPaint(buttonFill.getStroke(state, fillRect));

			Rectangle2D r = ShapeBounds.getBounds(fillShape);

			if (isStrokePainted) {
				g.draw(strokeShape);
				g.draw(paintedPartitions);
			}

			if (jc.isFocusOwner() && isFocusPainted) {
				if (paintFocus == PaintFocus.INSIDE) {
					Graphics2D g2 = (Graphics2D) g.create();
					g2.clip(fillShape);
					PlafPaintUtils.paintFocus(g2, fillShape, focusRingSize);
					g2.dispose();
				} else if (paintFocus == PaintFocus.OUTSIDE) {
					if (!ShapeUtils.isEmpty(focusedPartitions)) {
						Graphics2D g2 = (Graphics2D) g.create();
						g2.clip(r);
						PlafPaintUtils.paintFocus(g2, focusedPartitions,
								focusRingSize);
						g2.dispose();
					}
				}
			}
		}

		protected abstract void defineShapes(JComponent jc,
				AbstractButton button, ButtonState.Float state, Graphics2D g,
				int x, int y, int width, int height, GeneralPath fillShape,
				GeneralPath strokeShape, GeneralPath paintedPartitions,
				GeneralPath focusedPartitions);
	}

	static class ButtonCircleBackground extends AbstractButtonBackground {

		public ButtonCircleBackground(ButtonFill buttonFill, int focusRingSize,
				PaintFocus paintFocus) {
			super(buttonFill, focusRingSize, paintFocus);
		}

		@Override
		public Insets getBorderInsets(Component c) {
			Icon icon;
			boolean isFocusPainted = true;
			if (c instanceof AbstractButton) {
				icon = ((AbstractButton) c).getIcon();
				isFocusPainted = ((AbstractButton) c).isFocusPainted();
			} else if (c instanceof JLabel) {
				icon = ((JLabel) c).getIcon();
			} else {
				icon = null;
			}
			int iconWidth = icon == null ? 10 : icon.getIconWidth();
			int iconHeight = icon == null ? 10 : icon.getIconHeight();
			double diameter = Math.sqrt(iconWidth * iconWidth + iconHeight
					* iconHeight);
			double radius = diameter / 2;
			radius += 5;
			if (paintFocus == PaintFocus.OUTSIDE && isFocusPainted) {
				radius += focusRingSize;
			}
			int radiusInt = (int) (radius + .5);

			int top = radiusInt - iconHeight / 2;
			int bottom = 2 * radiusInt - top - iconHeight;
			int left = radiusInt - iconWidth / 2;
			int right = 2 * radiusInt - left - iconWidth;
			return new Insets(top, left, bottom, right);
		}

		@Override
		protected void defineShapes(JComponent jc, AbstractButton button,
				Float state, Graphics2D g, int x, int y, int width, int height,
				GeneralPath fillShape, GeneralPath strokeShape,
				GeneralPath paintedPartitions, GeneralPath focusedPartitions) {

			boolean isFocusPainted = button == null || button.isFocusPainted();
			if (paintFocus == PaintFocus.OUTSIDE && isFocusPainted) {
				x += focusRingSize;
				y += focusRingSize;
				width -= 2 * focusRingSize;
				height -= 2 * focusRingSize;
			}

			Ellipse2D circle = new Ellipse2D.Float(x, y, width - 1, height - 1);
			fillShape.append(circle, false);
			strokeShape.append(circle, false);
		}

	}

	static class ButtonSegmentBackground extends AbstractButtonBackground {
		int cornerRadius;
		HorizontalPosition horizontalPosition;
		VerticalPosition verticalPosition;

		public ButtonSegmentBackground(ButtonFill buttonFill, int cornerRadius,
				int focusRingSize, PaintFocus paintFocus,
				HorizontalPosition horizontalPosition,
				VerticalPosition verticalPosition) {
			super(buttonFill, focusRingSize, paintFocus);
			this.cornerRadius = cornerRadius;
			this.horizontalPosition = horizontalPosition;
			this.verticalPosition = verticalPosition;
		}

		@Override
		public Insets getBorderInsets(Component c) {
			JComponent jc = (JComponent) c;
			AbstractButton button = (jc instanceof AbstractButton) ? (AbstractButton) jc
					: null;
			Insets i = new Insets(0, 0, 0, 0);

			boolean isFocusPainted = button == null || button.isFocusPainted();
			if (paintFocus == PaintFocus.OUTSIDE && isFocusPainted) {
				i.top += focusRingSize;
				i.left += focusRingSize;
				i.bottom += focusRingSize;
				i.right += focusRingSize;
			}

			if (buttonFill.getShadowHighlight(new ButtonState.Float(0, 0, 0, 0,
					0)) != null) {
				i.bottom = Math.max(1, i.bottom);
			}

			// 1 pixel for the stroke itself
			i.top++;
			i.left++;
			i.bottom++;
			i.right++;

			int interior = cornerRadius;
			if (paintFocus == PaintFocus.INSIDE && isFocusPainted)
				interior = Math.max(interior, focusRingSize);
			interior = Math.max(interior, 3);

			int interiorSegment = Math.max(interior - 4, 2);

			HorizontalPosition horizontalPosition = getHorizontalPosition(jc);
			switch (horizontalPosition) {
			case ONLY:
				i.left += interior;
				i.right += interior;
				break;
			case LEFT:
				i.left += interior;
				i.right += interiorSegment;
				break;
			case MIDDLE:
				i.left += interiorSegment;
				i.right += interiorSegment;
				break;
			case RIGHT:
				i.left += interiorSegment;
				i.right += interior;
			}

			VerticalPosition verticalPosition = getVerticalPosition(jc);
			switch (verticalPosition) {
			case ONLY:
				i.top += interior;
				i.bottom += interior;
				break;
			case TOP:
				i.top += interior;
				i.bottom += interiorSegment;
				break;
			case MIDDLE:
				i.top += interiorSegment;
				i.bottom += interiorSegment;
				break;
			case BOTTOM:
				i.top += interiorSegment;
				i.bottom += interior;
			}

			// TODO: maybe rethink how we suport margins? Instead of setting it once
			// in installUI, we could update it with the "interior" calculations above
			Insets margin = button == null ? null : button.getMargin();
			if (margin != null) {
				i.top += margin.top;
				i.left += margin.left;
				i.bottom += margin.bottom;
				i.right += margin.right;
			}

			return i;
		}

		@Override
		protected void defineShapes(JComponent jc, AbstractButton button,
				ButtonState.Float state, Graphics2D g, int x, int y, int width,
				int height, GeneralPath fillShape, GeneralPath strokeShape,
				GeneralPath paintedPartitions, GeneralPath focusedPartitions) {
			HorizontalPosition hp = getHorizontalPosition(jc);
			VerticalPosition vp = getVerticalPosition(jc);

			boolean isFocusPainted = button == null || button.isFocusPainted();
			if (paintFocus == PaintFocus.OUTSIDE && isFocusPainted) {
				if (hp == HorizontalPosition.ONLY
						|| hp == HorizontalPosition.LEFT) {
					x += focusRingSize;
					width -= focusRingSize;
				}
				if (vp == VerticalPosition.ONLY || vp == VerticalPosition.TOP) {
					y += focusRingSize;
					height -= focusRingSize;
				}

				if (hp == HorizontalPosition.ONLY
						|| hp == HorizontalPosition.RIGHT) {
					width -= focusRingSize;
				}
				if (vp == VerticalPosition.ONLY
						|| vp == VerticalPosition.BOTTOM) {
					height -= focusRingSize;
				}
			}

			ButtonShape buttonShape = new ButtonShape(cornerRadius,
					cornerRadius);
			int hp2 = hp.getPosition();
			int vp2 = vp.getPosition();
			buttonShape.getShape(fillShape, strokeShape, width - 1, height - 1,
					hp2, vp2, false, null);

			fillShape.transform(AffineTransform.getTranslateInstance(x, y));
			strokeShape.transform(AffineTransform.getTranslateInstance(x, y));

			Rectangle2D r = ShapeBounds.getBounds(fillShape);
			if (hp == HorizontalPosition.RIGHT
					|| hp == HorizontalPosition.MIDDLE) {
				focusedPartitions.moveTo(r.getMinX(), r.getMinY());
				focusedPartitions.lineTo(r.getMinX(), r.getMaxY());
			}

			if (hp == HorizontalPosition.LEFT
					|| hp == HorizontalPosition.MIDDLE) {
				focusedPartitions.moveTo(r.getMaxX(), r.getMinY());
				focusedPartitions.lineTo(r.getMaxX(), r.getMaxY());
				paintedPartitions.moveTo(r.getMaxX(), r.getMinY());
				paintedPartitions.lineTo(r.getMaxX(), r.getMaxY());
			}

			if (vp == VerticalPosition.TOP || vp == VerticalPosition.MIDDLE) {
				focusedPartitions.moveTo(r.getMinX(), r.getMaxY());
				focusedPartitions.lineTo(r.getMaxX(), r.getMaxY());
				paintedPartitions.moveTo(r.getMinX(), r.getMaxY());
				paintedPartitions.lineTo(r.getMaxX(), r.getMaxY());
			}

			if (vp == VerticalPosition.BOTTOM || vp == VerticalPosition.MIDDLE) {
				focusedPartitions.moveTo(r.getMinX(), r.getMinY());
				focusedPartitions.lineTo(r.getMaxX(), r.getMinY());
			}
		}

		private List<Border> getBorders(Border b) {
			List<Border> list = new ArrayList<>();
			if (b instanceof CompoundBorder) {
				CompoundBorder cb = (CompoundBorder) b;
				list.addAll(getBorders(cb.getOutsideBorder()));
				list.addAll(getBorders(cb.getInsideBorder()));
			} else if (b != null) {
				list.add(b);
			}
			return list;
		}

	}

	static class ButtonInfo {
		ChangeListener buttonStateListener = new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				refreshButtonState();
			}
		};

		PropertyChangeListener booleanStateListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				refreshButtonState();
			}

		};

		PropertyChangeListener refreshBorderListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				refreshBorder();
			}
		};

		protected final AbstractButton button;

		protected ButtonInfo(AbstractButton button) {
			this.button = button;
			refreshButtonState();
			refreshBorder();
		}

		protected void refreshButtonState() {
			ButtonState.Boolean targetState = (ButtonState.Boolean) button
					.getClientProperty(PROPERTY_BOOLEAN_BUTTON_STATE);
			if (targetState == null) {
				targetState = new ButtonState.Boolean(button.getModel());
			}
			ButtonState.Float f = new ButtonState.Float(targetState);
			ButtonState.setTargetProperty(button,
					PROPERTY_FLOAT_BUTTON_STATE, f, .1f);
		}

		protected void refreshBorder() {
			if (!(button.getUI() instanceof QButtonUI))
				return;
			QButtonUI ui = (QButtonUI) button.getUI();
			button.setBorder(ui.createBackground(button));
		}
	}

	/** A list of buttons that use this UI. */
	List<AbstractButton> buttons = new ArrayList<>();

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		AbstractButton button = (AbstractButton) c;
		ButtonInfo i = getButtonInfo(button, true);
		button.getModel().addChangeListener(i.buttonStateListener);
		button.addPropertyChangeListener(PROPERTY_BOOLEAN_BUTTON_STATE,
				i.booleanStateListener);
		button.addPropertyChangeListener(PROPERTY_HORIZONTAL_POSITION,
				i.refreshBorderListener);
		button.addPropertyChangeListener(PROPERTY_VERTICAL_POSITION,
				i.refreshBorderListener);
		button.addPropertyChangeListener(PROPERTY_STROKE_PAINTED,
				i.refreshBorderListener);
		button.addPropertyChangeListener(PROPERTY_IS_CIRCLE,
				i.refreshBorderListener);
		button.addKeyListener(focusArrowListener);
		button.setRolloverEnabled(true);
		buttons.add(button);
		button.setOpaque(false);
		button.setMargin(new Insets(0,4,0,4));
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		AbstractButton button = (AbstractButton) c;
		ButtonInfo i = getButtonInfo(button, false);
		button.getModel().removeChangeListener(i.buttonStateListener);
		if (i != null) {
			button.removePropertyChangeListener(PROPERTY_BOOLEAN_BUTTON_STATE,
					i.booleanStateListener);
			button.removePropertyChangeListener(PROPERTY_HORIZONTAL_POSITION,
					i.refreshBorderListener);
			button.removePropertyChangeListener(PROPERTY_VERTICAL_POSITION,
					i.refreshBorderListener);
			button.removePropertyChangeListener(PROPERTY_STROKE_PAINTED,
					i.refreshBorderListener);
			button.removePropertyChangeListener(PROPERTY_IS_CIRCLE,
					i.refreshBorderListener);
		}
		button.removeKeyListener(focusArrowListener);
		button.putClientProperty(PROPERTY_BUTTON_INFO, null);
		buttons.remove(button);
	}

	protected ButtonInfo getButtonInfo(AbstractButton b, boolean createIfMissing) {
		ButtonInfo i = (ButtonInfo) b.getClientProperty(PROPERTY_BUTTON_INFO);
		if (i == null && createIfMissing) {
			i = new ButtonInfo(b);
			b.putClientProperty(PROPERTY_BUTTON_INFO, i);
		}
		return i;
	}

	int cornerRadius = 2;
	PaintFocus paintFocus = PaintFocus.OUTSIDE;
	ButtonFill buttonFill = BevelButtonUI.BEVEL_FILL;

	/** The number of pixels thick the focus ring should be. */
	int focusRingSize = 3;

	public int getCornerRadius() {
		return cornerRadius;
	}

	public ButtonFill getButtonFill() {
		return buttonFill;
	}

	public int getFocusRingSize() {
		return focusRingSize;
	}

	public PaintFocus getPaintFocus() {
		return paintFocus;
	}

	public void setPaintFocus(PaintFocus paintFocus) {
		if (this.paintFocus == paintFocus)
			return;
		this.paintFocus = paintFocus;
		refreshBorders();
	}

	public void setCornerRadius(int cornerRadius) {
		if (this.cornerRadius == cornerRadius)
			return;
		this.cornerRadius = cornerRadius;
		refreshBorders();
	}

	public void setFocusRingSize(int focusRingSize) {
		if (this.focusRingSize == focusRingSize)
			return;
		this.focusRingSize = focusRingSize;
		refreshBorders();
	}

	public void setButtonFill(ButtonFill buttonFill) {
		if (this.buttonFill == buttonFill)
			return;
		this.buttonFill = buttonFill;
		repaintButtons();
	}

	protected void repaintButtons() {
		for (AbstractButton button : buttons) {
			button.repaint();
		}
	}

	protected void refreshBorders() {
		for (AbstractButton button : buttons) {
			ButtonInfo i = getButtonInfo(button, false);
			if (i != null)
				i.refreshBorder();
		}
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		Background.paintBackgroundContents(g, c);
		super.paint(g, c);
	}

	private static transient JButton scratchButton = new JButton();

	@Override
	protected void paintText(Graphics g, AbstractButton b, Rectangle textRect,
			String text) {
		// this hack helps us render a button in a different enabled state
		// than what it really is:
		ButtonState.Boolean state = (ButtonState.Boolean) b
				.getClientProperty(PROPERTY_BOOLEAN_BUTTON_STATE);
		if (state != null && state.isEnabled() != b.isEnabled()) {
			scratchButton.setText(b.getText());
			scratchButton.setDisplayedMnemonicIndex(b
					.getDisplayedMnemonicIndex());
			scratchButton.setForeground(b.getForeground());
			scratchButton.setFont(b.getFont());
			scratchButton.setBackground(b.getBackground());
			scratchButton.setEnabled(state.isEnabled());
			super.paintText(g, scratchButton, textRect, text);
			return;
		}
		super.paintText(g, b, textRect, text);
	}

	/**
	 * Create a {@link com.pump.plaf.combobox.QComboBoxUI} modeled after this
	 * <code>QButtonUI</code>.
	 */
	public QComboBoxUI createComboBoxUI() {
		return new QComboBoxUI(this);
	}

	public Background createBackground(JComponent c) {
		if (Boolean.TRUE.equals(c.getClientProperty(PROPERTY_IS_CIRCLE))) {
			return createCircleBackground();
		}
		return createButtonSegmentBackground(getHorizontalPosition(c),
				getVerticalPosition(c));
	}

	public ButtonCircleBackground createCircleBackground() {
		ButtonFill fill = getButtonFill();
		PaintFocus paintFocus = getPaintFocus();
		int focusRingSize = getFocusRingSize();
		return new ButtonCircleBackground(fill, focusRingSize, paintFocus);
	}

	public ButtonSegmentBackground createButtonSegmentBackground(
			HorizontalPosition horizontalPosition,
			VerticalPosition verticalPosition) {
		ButtonFill fill = getButtonFill();
		int cornerRadius = getCornerRadius();
		int focusRingSize = getFocusRingSize();
		PaintFocus paintFocus = getPaintFocus();
		return new ButtonSegmentBackground(fill, cornerRadius, focusRingSize,
				paintFocus, horizontalPosition, verticalPosition);
	}
}