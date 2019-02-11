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
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicGraphicsUtils;

import com.pump.geom.ShapeBounds;
import com.pump.plaf.UIEffect.State;

/**
 * A ButtonUI that includes an enclosed filled shape.
 * <P>
 * This <code>ButtonUI</code> supports several options for controlling the shape
 * of separate buttons. (Note one instance of each UI can be assigned to several
 * buttons, and each button can be configured differently).
 * <P>
 * Each button can have a horizontal and vertical position, allowing grids of
 * buttons to be seamlessly connected. Horizontal positions can be LEFT, MIDDLE,
 * RIGHT and ONLY, and vertical positions can be TOP, MIDDLE, BOTTOM, and ONLY.
 * You can set these positions by called:
 * <P>
 * <code>myButton.putClientProperty(HORIZONTAL_POSITION, LEFT)</code>
 * <P>
 * Also you can define an arbitrary shape for each button. If this is defined,
 * that shape takes priority over all other curvature/segment information. You
 * can define this by calling something like:
 * <P>
 * <code>myButton.putClientProperty(SHAPE, new Ellipse2D.Float(0,0,100,100));</code>
 * <P>
 * The size of the shape is not important: it will be scaled as necessary to fit
 * the icon and text of each button. It is essential that the center of this
 * shape be inside the shape. (That is, a 'C' or 'O' shape is not valid.)
 * <P>
 * This layout is not tested with HTML-rendered text.
 * 
 * @see com.pump.showcase.QButtonUIDemo
 * @see <a
 *      href="https://javagraphics.blogspot.com/2009/08/buttons-new-uis.html">Buttons:
 *      New UIs</a>
 * 
 */
public abstract class QButtonUI extends ButtonUI implements PositionConstants {

	/**
	 * Assign this client property to control whether the path stroke should be
	 * painted. The default is assigned to be true.
	 */
	public static final String STROKE_PAINTED = "stroke-painted";

	/**
	 * Assign this client property to control whether the button should be
	 * rendered as enabled or not.
	 * <p>
	 * When this is null: the enabled state is taken directly from the button.
	 * But if the button is disabled and this property is true: then the button
	 * is still rendered as if it were enabled (and vice versa).
	 */
	public static final String ENABLED_STATE = "enabled-rendering";

	/**
	 * This client property resolves to a ButtonState (or null).
	 */
	public static final String PROPERTY_BUTTON_STATE = QButtonUI.class
			.getName() + "#buttonState";

	protected static class ButtonState {

		float rollover, selected, armed, spacebarPressed;

		public ButtonState(AbstractButton button) {
			rollover = QButtonUI.isRollover(button) ? 1 : 0;
			selected = button.getModel().isSelected() ? 1 : 0;
			armed = button.getModel().isArmed() ? 1 : 0;
			spacebarPressed = QButtonUI.isSpacebarPressed(button) ? 1 : 0;
		}

		public ButtonState(float armed, float rollover, float selected,
				float spacebarPressed) {
			this.armed = armed;
			this.rollover = rollover;
			this.selected = selected;
			this.spacebarPressed = spacebarPressed;
		}

		@Override
		public int hashCode() {
			return (int) (rollover + selected + armed + spacebarPressed);
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ButtonState))
				return false;
			ButtonState other = (ButtonState) obj;
			if (other.getArmed() != getArmed())
				return false;
			if (other.getRollover() != getRollover())
				return false;
			if (other.getSelected() != getSelected())
				return false;
			if (other.getSpacebarPressed() != getSpacebarPressed())
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "ButtonState[ armed=" + getArmed() + ", rollover="
					+ getRollover() + ", selected=" + getSelected()
					+ " spacebarPressed=" + getSpacebarPressed() + "]";
		}

		public float getRollover() {
			return rollover;
		}

		public float getSelected() {
			return selected;
		}

		public float getArmed() {
			return armed;
		}

		public float getSpacebarPressed() {
			return spacebarPressed;
		}

		public ButtonState iterate(ButtonState targetState) {
			// rollover has a slower increment, it's a more casual change
			float newRollover = iterate(getRollover(),
					targetState.getRollover(), .1f);
			float newArmed = iterate(getArmed(), targetState.getArmed(), .2f);
			float newSelected = iterate(getSelected(),
					targetState.getSelected(), .2f);
			float newSpacebarPressed = iterate(getSpacebarPressed(),
					targetState.getSpacebarPressed(), .2f);
			return new ButtonState(newArmed, newRollover, newSelected,
					newSpacebarPressed);
		}

		private float iterate(float startingValue, float targetValue, float incr) {
			if (startingValue == targetValue)
				return targetValue;
			if (startingValue < targetValue)
				return Math.min(targetValue, startingValue + incr);
			return Math.max(targetValue, startingValue - incr);
		}
	}

	/**
	 * Basic information about the geometry/layout of buttons. The members are
	 * final, and should be redefined instead of replaced.
	 */
	static class ButtonInfo {

		/**
		 * This handles most -- but not all -- of the basic workings of a
		 * generic button.
		 */
		final BasicButtonListener basicListener;

		/** The button this information relates to. */
		final AbstractButton button;

		Timer buttonStateTimer = new Timer(20, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ButtonState oldState = (ButtonState) button
						.getClientProperty(PROPERTY_BUTTON_STATE);
				ButtonState targetState = new ButtonState(button);
				ButtonState newState = oldState.iterate(targetState);
				button.putClientProperty(PROPERTY_BUTTON_STATE, newState);
				button.repaint();
				if (targetState.equals(newState)) {
					buttonStateTimer.stop();
				}
			}

		});

		ChangeListener buttonStateListener = new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				refreshButtonState();
			}
		};

		private void refreshButtonState() {
			ButtonState newState = new ButtonState(button);
			ButtonState oldState = (ButtonState) button
					.getClientProperty(PROPERTY_BUTTON_STATE);
			if (oldState == null) {
				button.putClientProperty(PROPERTY_BUTTON_STATE, newState);
			} else if (!newState.equals(oldState)) {
				buttonStateTimer.start();
			}
		}

		/**
		 * A list of <code>PaintUIEffect</code>, in the order they should be
		 * rendered.
		 */
		final protected List<PaintUIEffect> effects = new ArrayList<PaintUIEffect>();

		final protected Rectangle fillBounds = new Rectangle();

		/** The rectangle the icon is painted to. */
		final protected Rectangle iconRect = new Rectangle();

		/** The rectangle the text is painted to. */
		final protected Rectangle textRect = new Rectangle();

		/** The UI that relates to this ButtonInfo object. */
		final QButtonUI ui;

		/** Completely untested. */
		final protected Rectangle viewRect = new Rectangle();

		/** The area that makes up this button. */
		final protected GeneralPath fill = new GeneralPath();

		/**
		 * The stroke/border of this button. This is the shape that is rendered
		 * with a <code>java.awt.Stroke</code>. It is generally the same -- or
		 * very similar to -- the fill shape.
		 */
		final protected GeneralPath stroke = new GeneralPath();

		public ButtonInfo(AbstractButton b, QButtonUI buttonUI) {
			button = b;
			ui = buttonUI;

			basicListener = new BasicButtonListener(button) {
				Timer focusBlinker;

				String BLINK_STATE_KEY = "QButtonUI.blinkState";

				@Override
				public void focusGained(FocusEvent e) {
					super.focusGained(e);
					if (focusBlinker == null) {
						int rate = UIManager.getInt("TextField.caretBlinkRate");
						if (rate < 100)
							rate = 500;

						focusBlinker = new Timer(rate, new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								Boolean blinkState = (Boolean) button
										.getClientProperty(BLINK_STATE_KEY);
								if (blinkState == null)
									blinkState = Boolean.TRUE;
								button.putClientProperty(HAS_FOCUS, blinkState
										&& button.hasFocus());
								button.putClientProperty(BLINK_STATE_KEY,
										!blinkState);
							}
						});
					}
					button.putClientProperty(BLINK_STATE_KEY, null);
					Boolean blinkActive = (Boolean) button
							.getClientProperty("Focus.blink");
					if (blinkActive != null && blinkActive)
						focusBlinker.start();
				}

				@Override
				public void focusLost(FocusEvent e) {
					super.focusLost(e);
					button.putClientProperty(SPACEBAR_PRESSED, Boolean.FALSE);
					button.putClientProperty(HAS_FOCUS, null);
					if (focusBlinker != null)
						focusBlinker.stop();
					button.repaint();
				}

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					super.propertyChange(evt);
					if (evt.getPropertyName()
							.equals(AbstractButton.CONTENT_AREA_FILLED_CHANGED_PROPERTY)) {
						if (button.isOpaque()
								&& evt.getOldValue().equals(Boolean.FALSE)
								&& evt.getNewValue().equals(Boolean.TRUE)) {
							// the BasicButtonListener may set the opacity to
							// true
							// we want to reverse that here:
							button.setOpaque(false);
						}
					} else if (evt.getPropertyName().equals(SPACEBAR_PRESSED)
							|| evt.getPropertyName().equals(ROLLOVER)) {
						refreshButtonState();
					} else if (evt.getPropertyName().equals(HAS_FOCUS)) {
						button.repaint();
					} else if (evt.getPropertyName().equals("text")
							|| evt.getPropertyName().equals("icon")) {
						ui.updateLayout(button, getButtonInfo(button));
						button.repaint();
					}
				}
			};
		}

		public void updateFillBounds() {
			ShapeBounds.getBounds(fill, fillBounds);
		}
	}

	private static Map<Font, Integer> ascentTable = new HashMap<Font, Integer>();

	/** A possible value for the VERTICAL_POSITION client property. */
	public static final String BOTTOM = "bottom";

	private static final String BUTTON_INFO_KEY = "com.pump.plaf.QButtonUI.ButtonInfo";

	/** When a component is resized, <code>updateLayout</code> is called. */
	protected static final ComponentListener componentListener = new ComponentListener() {

		public void componentHidden(ComponentEvent e) {
		}

		public void componentMoved(ComponentEvent e) {
			componentResized(e);
		}

		public void componentResized(ComponentEvent e) {
			AbstractButton button = (AbstractButton) e.getSource();
			ButtonInfo info = getButtonInfo(button);
			info.ui.updateLayout(button, info);
			button.repaint();
		}

		public void componentShown(ComponentEvent e) {
			componentResized(e);
		}
	};

	/**
	 * If this client-property is non-null: then it overrides whether focus is
	 * actually present when deciding to render the focus.
	 */
	protected static final String HAS_FOCUS = "QButtonUI.focusPainted";

	/** A static KeyListener for using focus arrow keys. */
	protected static KeyListener focusArrowListener = new FocusArrowListener();

	/**
	 * The client property for the horizontal position of this segment. The
	 * recognized values are "left", "middle", "last" and "only". The default
	 * value is assumed to be "only".
	 */
	public static final String HORIZONTAL_POSITION = "JButton.segmentHorizontalPosition";

	/**
	 * This client property defines an arbitrary shape for each button.
	 */
	public static final String SHAPE = "JButton.shape";

	protected static KeyListener keyArmingListener = new KeyListener() {
		public void keyPressed(KeyEvent e) {
			int code = e.getKeyCode();
			if (code == KeyEvent.VK_SPACE) {
				AbstractButton button = (AbstractButton) e.getSource();
				Boolean wasPressed = (Boolean) button
						.getClientProperty(SPACEBAR_PRESSED);
				if (wasPressed == null || wasPressed.booleanValue() == false) {
					button.putClientProperty(SPACEBAR_PRESSED, Boolean.TRUE);
					button.doClick();
				}
			}
		}

		public void keyReleased(KeyEvent e) {
			int code = e.getKeyCode();
			if (code == KeyEvent.VK_SPACE) {
				AbstractButton button = (AbstractButton) e.getSource();
				button.putClientProperty(SPACEBAR_PRESSED, Boolean.FALSE);
			}
		}

		public void keyTyped(KeyEvent e) {
		}
	};
	/** A possible value for the HORIZONTAL_POSITION client property. */
	public static final String LEFT = "left";

	/**
	 * A possible value for the HORIZONTAL_POSITION or VERTICAL_POSITION client
	 * property.
	 */
	public static final String MIDDLE = "middle";

	/**
	 * A possible value for the HORIZONTAL_POSITION or VERTICAL_POSITION client
	 * property.
	 */
	public static final String ONLY = "only";

	/**
	 * An optional client property that map to a {@link PaintFocus} constant to
	 * control how to paint the focus of a button.
	 */
	public static final String PAINT_FOCUS_BEHAVIOR_KEY = "QButtonUI.PaintFocusBehavior";

	/**
	 * Different behaviors to paint the keyboard focus.
	 */
	public static enum PaintFocus {
		/**
		 * Focus should be painted along the edge of the filled shape -- both
		 * inside and outside.
		 */
		BOTH,
		/**
		 * Focus should be painted just inside the filled shape.
		 */
		INSIDE,

		/**
		 * Focus should be painted just outside the filled shape. This is taken
		 * into account when allocating the button size, because it adds extra
		 * pixels to the width/height.
		 */
		OUTSIDE,

		/**
		 * No focus is explicitly painted.
		 * <P>
		 * You may use this constant when you aren't happy with how the
		 * <code>paint()</code> method handles focus by default, and paint your
		 * own focus by override <code>paintBackground()</code> or
		 * <code>paintForeground()</code>.
		 */
		NONE
	};

	/**
	 * When the position properties change in a button, we need to update the
	 * UI.
	 */
	protected static PropertyChangeListener positionAndShapeListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			String name = evt.getPropertyName();
			if (name.equals(VERTICAL_POSITION)
					|| name.equals(HORIZONTAL_POSITION) || name.equals(SHAPE)
					|| name.equals("JButton.segmentPosition")) { // see Apple
																	// Tech Note
																	// 2196
				AbstractButton button = (AbstractButton) evt.getSource();
				ButtonUI ui = button.getUI();
				if (ui instanceof QButtonUI) {
					QButtonUI s = (QButtonUI) ui;
					s.updateLayout(button, getButtonInfo(button));
					button.invalidate();
					button.repaint();
				}
			}
		}
	};

	/** A possible value for the HORIZONTAL_POSITION client property. */
	public static final String RIGHT = "right";

	/**
	 * This client property is set to a Boolean indicating whether the spacebar
	 * is currently pressed or not.
	 */
	protected static final String SPACEBAR_PRESSED = "QButtonUI.spacebarPressed";

	/**
	 * This client property is set to a Boolean indicating whether to render a
	 * button as if the mouse is rolled over it or not.
	 */
	protected static final String ROLLOVER = "QButtonUI.rollover";

	/**
	 * A translucent composite used to render parts the UI when a button is
	 * disabled.
	 */
	protected static AlphaComposite SRC_OVER_TRANSLUCENT = AlphaComposite
			.getInstance(AlphaComposite.SRC_OVER, .5f);

	/** A possible value for the VERTICAL_POSITION client property. */
	public static final String TOP = "top";

	/**
	 * The client property for the vertical position of this segment. The
	 * recognized values are "top", "middle", "bottom" and "only". The default
	 * value is assumed to be "only".
	 */
	public static final String VERTICAL_POSITION = "JButton.segmentVerticalPosition";

	protected static ButtonInfo getButtonInfo(AbstractButton button) {
		ButtonInfo i = (ButtonInfo) button.getClientProperty(BUTTON_INFO_KEY);
		if (i == null)
			throw new NullPointerException("installUI was not called");
		return i;
	}

	/**
	 * The client property that maps to a Boolean indicating whether segmented
	 * buttons should show a partition or not. The default value is assumed to
	 * be true.
	 */
	public static final String SHOW_SEPARATORS = "QButtonUI.showSeparators";

	protected boolean isShowingSeparators(AbstractButton button) {
		Boolean showSeparators = (Boolean) button
				.getClientProperty(SHOW_SEPARATORS);
		if (showSeparators == null)
			return true;
		return showSeparators;
	}

	protected void updateLayout(AbstractButton button, ButtonInfo buttonInfo) {
		Shape customShape = (Shape) button.getClientProperty(SHAPE);
		int width = button.getWidth();
		int height = button.getHeight();
		int horizontalPosition = getHorizontalPosition(button);
		int verticalPosition = getVerticalPosition(button);

		String key = width + " " + height + " " + horizontalPosition + " "
				+ verticalPosition;
		button.putClientProperty("QButtonUI.validationKey", key);

		int dx = 0;
		int dy = 0;

		if (getFocusPainting(button) == PaintFocus.OUTSIDE
				|| getFocusPainting(button) == PaintFocus.BOTH) {
			if (horizontalPosition == POS_LEFT
					|| horizontalPosition == POS_ONLY) {
				dx += focusSize;
				width -= focusSize;
			}
			if (horizontalPosition == POS_RIGHT
					|| horizontalPosition == POS_ONLY) {
				width -= focusSize;
			}
			if (verticalPosition == POS_TOP || verticalPosition == POS_ONLY) {
				dy += focusSize;
				height -= focusSize;
			}
			if (verticalPosition == POS_BOTTOM || verticalPosition == POS_ONLY) {
				height -= focusSize;
			}
		} else {
			if ((verticalPosition == POS_BOTTOM || verticalPosition == POS_ONLY)
					&& fill.getShadowHighlight(button) != null) {
				height--;
			}
		}

		ButtonInfo info = getButtonInfo(button);
		boolean showSeparators = isShowingSeparators(button);

		shape.getShape(info.fill, info.stroke, width, height,
				horizontalPosition, verticalPosition, showSeparators,
				customShape);

		AffineTransform translation = AffineTransform.getTranslateInstance(dx,
				dy);
		info.fill.transform(translation);
		info.stroke.transform(translation);

		Font font = button.getFont();
		if (font == null)
			font = new Font("Default", 0, 12);
		FontMetrics fm = button.getFontMetrics(font);

		info.viewRect.x = info.viewRect.y = info.textRect.x = info.textRect.y = info.textRect.width = info.textRect.height = 0;
		info.iconRect.x = info.iconRect.y = info.iconRect.width = info.iconRect.height = 0;
		info.viewRect.width = Short.MAX_VALUE;
		info.viewRect.height = Short.MAX_VALUE;

		SwingUtilities.layoutCompoundLabel(fm, button.getText(),
				button.getIcon(), button.getVerticalAlignment(),
				button.getHorizontalAlignment(),
				button.getVerticalTextPosition(),
				button.getHorizontalTextPosition(), info.viewRect,
				info.iconRect, info.textRect, button.getIconTextGap());

		Insets textInsets = getTextPadding();
		Insets iconInsets = getIconPadding();

		Rectangle tempTextRect = new Rectangle(info.textRect);
		Rectangle tempIconRect = new Rectangle(info.iconRect);
		if (info.textRect.width > 0) {
			tempTextRect.y -= textInsets.top;
			tempTextRect.x -= textInsets.left;
			tempTextRect.width += textInsets.left + textInsets.right;
			tempTextRect.height += textInsets.top + textInsets.bottom;
		}
		if (info.iconRect.width > 0) {
			tempIconRect.y -= iconInsets.top;
			tempIconRect.x -= iconInsets.left;
			tempIconRect.width += iconInsets.left + iconInsets.right;
			tempIconRect.height += iconInsets.top + iconInsets.bottom;
		}

		Rectangle sum = getSum(new Rectangle[] { tempIconRect, tempTextRect });

		Insets padding = getContentInsets(button);

		float centerX, centerY;
		if (button.getHorizontalAlignment() == SwingConstants.LEFT
				|| button.getHorizontalAlignment() == SwingConstants.LEADING) {
			centerX = padding.left + sum.width / 2;
		} else if (button.getHorizontalAlignment() == SwingConstants.RIGHT
				|| button.getHorizontalAlignment() == SwingConstants.TRAILING) {
			centerX = button.getWidth() - padding.right - sum.width / 2;
		} else {
			centerX = ((button.getWidth() - padding.left - padding.right)) / 2f;
		}
		// TODO: also take into account vertical alignment:
		centerY = ((button.getHeight() - padding.top - padding.bottom)) / 2f;

		float shiftX = centerX - (sum.width) / 2f - sum.x + padding.left;
		float shiftY = centerY - (sum.height) / 2f - sum.y + padding.top;

		if (customShape == null) {
			if (button.getVerticalAlignment() == SwingConstants.CENTER
					&& button.getVerticalTextPosition() == SwingConstants.CENTER
					&& info.textRect.width > 0) {
				int unusedAscent = getUnusedAscent(fm, font);
				int ascent = fm.getAscent() - unusedAscent;

				shiftY = (int) (-sum.y + centerY - ascent / 2 - unusedAscent
						+ padding.top - textInsets.top);
			}
		}

		info.iconRect.setFrame(info.iconRect.x + shiftX, info.iconRect.y
				+ shiftY, info.iconRect.width, info.iconRect.height);
		info.textRect.setRect((int) (info.textRect.x + shiftX + .5f),
				(int) (info.textRect.y + shiftY + .5f), info.textRect.width,
				info.textRect.height);

		info.updateFillBounds();
	}

	/**
	 * This looks at the client properties "JButton.segmentPosition" and
	 * HORIZONTAL_POSITION to return on the constants in this class (LEFT_POS,
	 * POS_MIDDLE, RIGHT_POS or POS_ONLY).
	 * <P>
	 * This returns POS_ONLY if a shape is defined, because in this context a
	 * position doesn't make sense.
	 */
	protected static int getHorizontalPosition(JComponent b) {
		if (b == null)
			return POS_ONLY;

		Shape shape = (Shape) b.getClientProperty(SHAPE);
		if (shape != null) {
			return POS_ONLY;
		}

		String s = (String) b.getClientProperty("JButton.segmentPosition");
		if (s == null)
			s = (String) b.getClientProperty(HORIZONTAL_POSITION);
		if (s == null)
			s = "only";
		if (s.equalsIgnoreCase("first") || s.equalsIgnoreCase("left"))
			return POS_LEFT;
		if (s.equalsIgnoreCase("middle"))
			return POS_MIDDLE;
		if (s.equalsIgnoreCase("last") || s.equalsIgnoreCase("right"))
			return POS_RIGHT;
		return POS_ONLY;
	}

	/**
	 * Returns a rectangle enclosing all the rectangles in the argument.
	 */
	private static Rectangle getSum(Rectangle[] array) {
		Rectangle sum = null;
		for (int a = 0; a < array.length; a++) {
			if (array[a].width > 0 && array[a].height > 0) {
				if (sum == null) {
					sum = new Rectangle(array[a]);
				} else {
					sum = sum.union(array[a]);
				}
			}
		}
		if (sum == null)
			sum = new Rectangle(0, 0, 0, 0);
		return sum;
	}

	/**
	 * This deals with a bug/peculiarity for the default Mac font: several
	 * pixels of the ascent are actually empty. This screws up certain
	 * measurements which assume the font is actually a few pixels taller than
	 * it really is.
	 */
	private static int getUnusedAscent(FontMetrics fm, Font font) {
		Integer value = ascentTable.get(font);
		if (value == null) {
			int recordedAscent = fm.getAscent();
			FontRenderContext frc = new FontRenderContext(
					new AffineTransform(), false, false);
			GlyphVector gv = font.createGlyphVector(frc, "XYZ");
			Rectangle2D bounds = ShapeBounds.getBounds(gv.getOutline());
			int observedAscent = (int) (Math.ceil(bounds.getHeight()) + .5);
			value = new Integer(recordedAscent - observedAscent);
			ascentTable.put(font, value);
		}
		return value.intValue();
	}

	/**
	 * This looks at the client property VERTICAL_POSITION to return on the
	 * constants in this class (POS_TOP, POS_MIDDLE, POS_BOTTOM or POS_ONLY).
	 * <P>
	 * This returns POS_ONLY if a shape is defined, because in this context a
	 * position doesn't make sense.
	 */
	protected static int getVerticalPosition(JComponent b) {
		if (b == null)
			return POS_ONLY;

		Shape shape = (Shape) b.getClientProperty(SHAPE);
		if (shape != null) {
			return POS_ONLY;
		}

		String s = (String) b.getClientProperty(VERTICAL_POSITION);
		if (s == null)
			s = "only";
		if (s.equalsIgnoreCase("top"))
			return POS_TOP;
		if (s.equalsIgnoreCase("middle"))
			return POS_MIDDLE;
		if (s.equalsIgnoreCase("bottom"))
			return POS_BOTTOM;
		return POS_ONLY;
	}

	/**
	 * Return true if the spacebar is pressed. This consults the
	 * SPACEBAR_PRESSED property, which is correctly controlled if a QButtonUI
	 * is installed in a given button.
	 * <P>
	 * This is visually the same as setting the ButtonModel's pressed state to
	 * true. However if that property were used and the mouse was interacting
	 * with this button: the two agents (the KeyListener and the MouseListener)
	 * would constantly be overriding the other's work.
	 */
	protected static boolean isSpacebarPressed(AbstractButton button) {
		Boolean b = (Boolean) button.getClientProperty(SPACEBAR_PRESSED);
		if (b == null)
			return false;
		return b.booleanValue();
	}

	/**
	 * Return true if this button should render as if the mouse is over it. This
	 * first checks the ROLLOVER client property to determine if an artificial
	 * state is being induced; if that is undefined then the ButtonModel's
	 * isRollover() method is used.
	 */
	protected static boolean isRollover(AbstractButton button) {
		Boolean b = (Boolean) button.getClientProperty(ROLLOVER);
		if (b == null)
			return button.getModel().isRollover();
		return b;
	}

	/**
	 * The <code>ButtonFill</code> that controls the main paints used in this
	 * L&amp;F.
	 */
	protected final ButtonFill fill;

	/** The number of pixels thick the focus ring should be. */
	protected int focusSize = 3;

	/** The ButtonShape responsible for the shape of this button. */
	protected final ButtonShape shape;

	/**
	 * Creates a new <code>QButtonUI</code>.
	 * 
	 * @param buttonFill
	 *            the <code>ButtonFill</code> this UI uses.
	 * @param buttonShape
	 *            the <code>ButtonShape</code> this UI uses.
	 */
	public QButtonUI(ButtonFill buttonFill, ButtonShape buttonShape) {
		shape = buttonShape;
		fill = buttonFill;
	}

	/**
	 * The padding between the "content area" (that is, the icon rect and text
	 * rect) and the edges of this button. This is a calculated value.
	 */
	protected Insets getContentInsets(AbstractButton button) {
		int horizontalPosition = getHorizontalPosition(button);
		int verticalPosition = getVerticalPosition(button);

		Insets i = new Insets(0, 0, 0, 0);
		if (getFocusPainting(button) == PaintFocus.OUTSIDE
				|| getFocusPainting(button) == PaintFocus.BOTH) {
			if (horizontalPosition == POS_LEFT
					|| horizontalPosition == POS_ONLY) {
				i.left += focusSize;
			}
			if (horizontalPosition == POS_RIGHT
					|| horizontalPosition == POS_ONLY) {
				i.right += focusSize;
			}
			if (verticalPosition == POS_TOP || verticalPosition == POS_ONLY) {
				i.top += focusSize;
			}
			if (verticalPosition == POS_BOTTOM || verticalPosition == POS_ONLY) {
				i.bottom += focusSize;
			}
		} else {
			if (fill.getShadowHighlight(button) != null
					&& (verticalPosition == POS_BOTTOM || verticalPosition == POS_ONLY)) {
				i.bottom++;
			}
		}
		return i;
	}

	/**
	 * Returns the list of <code>PaintUIEffect</code> this button is rendering.
	 */
	public List<PaintUIEffect> getEffects(AbstractButton button) {
		ButtonInfo info = getButtonInfo(button);
		return info.effects;
	}

	/**
	 * Returns one of the PAINT_FOCUS constants declared in this object. If
	 * PAINT_NO_FOCUS is used, then this class automatically paints no focus. In
	 * that case a subclass would be responsible for any rendering of focus.
	 * <P>
	 * If PAINT_FOCUS_OUTSIDE is used, then a few rings of focus are painted
	 * outside this component under this component. The order of painting is: <BR>
	 * 1. Shadow Highlight <BR>
	 * 2. Focus <BR>
	 * 3. Background Fill <BR>
	 * 4. Stroke
	 * <P>
	 * If PAINT_FOCUS_INSIDE is used, then a few rings of focus are painted
	 * inside this component. The order of painting is: <BR>
	 * 1. Shadow Highlight <BR>
	 * 2. Background Fill <BR>
	 * 3. Focus <BR>
	 * 4. Stroke
	 * <P>
	 * If PAINT_FOCUS_BOTH is used, then the focus appears both inside and
	 * outside the filled area. The order of painting is: <BR>
	 * 1. Shadow Highlight <BR>
	 * 2. Background Fill <BR>
	 * 3. Stroke <BR>
	 * 4. Focus
	 * 
	 * <P>
	 * By default this returns PAINT_FOCUS_OUTSIDE if a shape is defined, or if
	 * this button is in a single row or column of buttons. (That is, if the
	 * horizontal or vertical position is "only"). Otherwise this returns
	 * PAINT_FOCUS_INSIDE, because once you get to inner buttons the focus
	 * <i>has</i> to be painted on the inside to remain visible.
	 */
	public PaintFocus getFocusPainting(AbstractButton button) {
		PaintFocus f = (PaintFocus) button
				.getClientProperty(PAINT_FOCUS_BEHAVIOR_KEY);
		if (f != null)
			return f;

		Shape shape = (Shape) button.getClientProperty(SHAPE);
		if (shape != null)
			return PaintFocus.OUTSIDE;

		int horizontalPosition = getHorizontalPosition(button);
		int verticalPosition = getVerticalPosition(button);

		if (!(horizontalPosition == POS_ONLY || verticalPosition == POS_ONLY)) {
			return PaintFocus.INSIDE;
		}
		return PaintFocus.OUTSIDE;
	}

	/** Return a copy of the rectangle used to render the icon. */
	public Rectangle getIconBounds(AbstractButton button) {
		return new Rectangle(getButtonInfo(button).iconRect);
	}

	/**
	 * This padding is added to the icon rectangle when the content area is
	 * being calculated. That is: this is the minimum number of pixels that
	 * should be between the icon and the edge of this button.
	 * <P>
	 * This is a constant value that other calculations use.
	 */
	protected Insets getIconPadding() {
		return new Insets(2, 3, 2, 3);
	}

	/**
	 * This returns the preferred size.
	 */
	@Override
	public Dimension getMaximumSize(JComponent jc) {
		// if this method returns
		// return new Dimension(Integer.MAX_VALUE/2, Integer.MAX_VALUE/2);
		return getPreferredSize(jc);
	}

	/** Returns the preferred size. */
	@Override
	public Dimension getMinimumSize(JComponent jc) {
		return getPreferredSize(jc);
	}

	/**
	 * This is the minimum height of the content area of this button. That is:
	 * if this button has a solid gray fill: this is the height of those gray
	 * pixels.
	 * <P>
	 * This height will only be used if it is <i>less than</i> the preferred
	 * height the text and icon would otherwise require.
	 * <P>
	 * Also this will not be used for buttons with a custom shape.
	 */
	protected int getPreferredHeight() {
		return 0;
	}

	/** Calculates the preferred size of this button and UI. */
	@Override
	public Dimension getPreferredSize(JComponent c) {
		AbstractButton button = (AbstractButton) c;
		ButtonCluster cluster = ButtonCluster.getCluster(button);

		Rectangle scratchIconRect = new Rectangle();
		Rectangle scratchTextRect = new Rectangle();
		Rectangle scratchViewRect = new Rectangle(Short.MAX_VALUE,
				Short.MAX_VALUE);

		FontMetrics fm = button.getFontMetrics(button.getFont());
		SwingUtilities.layoutCompoundLabel(fm, button.getText(),
				button.getIcon(), button.getVerticalAlignment(),
				button.getHorizontalAlignment(),
				button.getVerticalTextPosition(),
				button.getHorizontalTextPosition(), scratchViewRect,
				scratchIconRect, scratchTextRect, button.getIconTextGap());

		Insets textInsets = getTextPadding();
		scratchTextRect.y -= textInsets.top;
		scratchTextRect.x -= textInsets.left;
		scratchTextRect.width += textInsets.left + textInsets.right;
		scratchTextRect.height += textInsets.top + textInsets.bottom;

		Insets iconInsets = getIconPadding();
		scratchIconRect.y -= iconInsets.top;
		scratchIconRect.x -= iconInsets.left;
		scratchIconRect.width += iconInsets.left + iconInsets.right;
		scratchIconRect.height += iconInsets.top + iconInsets.bottom;

		Rectangle sum = getSum(new Rectangle[] { scratchIconRect,
				scratchTextRect });

		if (cluster != null && cluster.isStandardized()) {
			/**
			 * If standardize: the dimensions of this button need to make room
			 * for all other buttons in the cluster.
			 */
			AbstractButton[] buttons = cluster.getButtons();
			for (int a = 0; a < buttons.length; a++) {
				ButtonUI ui = buttons[a].getUI();
				if (ui instanceof QButtonUI) {
					QButtonUI qui = (QButtonUI) ui;
					Dimension contentSize = qui.getContentSize(buttons[a]);
					sum.width = Math.max(sum.width, contentSize.width);
					sum.height = Math.max(sum.height, contentSize.height);
				}
			}
		}

		Insets padding = getContentInsets(button);

		Shape customShape = (Shape) button.getClientProperty(SHAPE);

		if (customShape == null) {
			int minHeight = getPreferredHeight();
			if (sum.height < minHeight)
				sum.height = minHeight;
		}

		int horizontalPosition = getHorizontalPosition(button);
		int verticalPosition = getVerticalPosition(button);
		Dimension size = shape.getPreferredSize(null, sum.width, sum.height,
				padding, customShape);

		if (customShape == null) {
			PaintFocus focus = getFocusPainting(button);
			if (focus == PaintFocus.OUTSIDE || focus == PaintFocus.BOTH) {
				if (horizontalPosition == POS_ONLY) {
					size.width += 2 * focusSize;
				} else if (horizontalPosition != POS_MIDDLE) {
					size.width += focusSize;
				}
				if (verticalPosition == POS_ONLY) {
					size.height += 2 * focusSize;
				} else if (horizontalPosition != POS_MIDDLE) {
					size.height += focusSize;
				}
			}
		}

		return size;
	}

	/**
	 * Returns the dimensions required to display the icon and label.
	 *
	 */
	private Dimension getContentSize(AbstractButton button) {
		Rectangle scratchIconRect = new Rectangle();
		Rectangle scratchTextRect = new Rectangle();
		Rectangle scratchViewRect = new Rectangle(Short.MAX_VALUE,
				Short.MAX_VALUE);

		FontMetrics fm = button.getFontMetrics(button.getFont());
		SwingUtilities.layoutCompoundLabel(fm, button.getText(),
				button.getIcon(), button.getVerticalAlignment(),
				button.getHorizontalAlignment(),
				button.getVerticalTextPosition(),
				button.getHorizontalTextPosition(), scratchViewRect,
				scratchIconRect, scratchTextRect, button.getIconTextGap());

		Insets textInsets = getTextPadding();
		scratchTextRect.y -= textInsets.top;
		scratchTextRect.x -= textInsets.left;
		scratchTextRect.width += textInsets.left + textInsets.right;
		scratchTextRect.height += textInsets.top + textInsets.bottom;

		Insets iconInsets = getIconPadding();
		scratchIconRect.y -= iconInsets.top;
		scratchIconRect.x -= iconInsets.left;
		scratchIconRect.width += iconInsets.left + iconInsets.right;
		scratchIconRect.height += iconInsets.top + iconInsets.bottom;

		Rectangle sum = getSum(new Rectangle[] { scratchIconRect,
				scratchTextRect });
		return new Dimension(sum.width, sum.height);
	}

	/**
	 * This returns the filled shape used to draw this button.
	 */
	public GeneralPath getShape(AbstractButton button) {
		ButtonInfo info = getButtonInfo(button);
		return new GeneralPath(info.fill);
	}

	/** Return a copy of the rectangle used to render the text. */
	public Rectangle getTextBounds(AbstractButton button) {
		return new Rectangle(getButtonInfo(button).textRect);
	}

	/**
	 * This padding is added to the text rectangle when the content area is
	 * being calculated. That is: this is the minimum number of pixels that
	 * should be between the text and the edge of this button.
	 * <P>
	 * This is a constant value that other calculations use.
	 */
	protected Insets getTextPadding() {
		return new Insets(2, 4, 2, 4);
	}

	@Override
	public void installUI(JComponent c) {
		AbstractButton button = (AbstractButton) c;

		ButtonInfo info = new ButtonInfo(button, this);
		button.putClientProperty(BUTTON_INFO_KEY, info);

		button.addMouseListener(info.basicListener);
		button.addMouseMotionListener(info.basicListener);
		button.addFocusListener(info.basicListener);
		button.addPropertyChangeListener(info.basicListener);
		button.addChangeListener(info.basicListener);
		button.addKeyListener(focusArrowListener);
		button.addComponentListener(componentListener);
		button.addKeyListener(keyArmingListener);
		button.setRequestFocusEnabled(false);
		button.setFocusable(true);
		button.addPropertyChangeListener(positionAndShapeListener);
		button.setOpaque(false);
		button.setRolloverEnabled(true);
		button.getModel().addChangeListener(info.buttonStateListener);

		if (button.getIcon() != null) {
			Font font = UIManager.getFont("IconButton.font");
			if (font != null)
				button.setFont(font); // miniature-ish
		}

		super.installUI(c);

		updateLayout(button, info);

		if (button.getFont() == null) {
			Font font = UIManager.getFont("Button.font");
			if (font == null) {
				font = new Font("Default", 0, 13);
			}
			button.setFont(font);
		}
	}

	/**
	 * Controls whether the stroke is painted with antialiased rendering hints.
	 * <P>
	 * Returns <code>true</code>.
	 */
	public boolean isStrokeAntialiased() {
		return true;
	}

	/**
	 * This checks to see if updateLayout() needs to be called. Generally this
	 * returns true if the width, height, or segment position has changed.
	 */
	protected boolean isLayoutValid(AbstractButton button) {
		int horizontalPosition = getHorizontalPosition(button);
		int verticalPosition = getVerticalPosition(button);
		int width = button.getWidth();
		int height = button.getHeight();

		String key = width + " " + height + " " + horizontalPosition + " "
				+ verticalPosition;

		String oldKey = (String) button
				.getClientProperty("QButtonUI.validationKey");
		if (oldKey == null)
			return false;

		if (oldKey.equals(key))
			return true;

		return false;
	}

	protected boolean isEnabled(AbstractButton button) {
		Boolean b = (Boolean) button.getClientProperty(ENABLED_STATE);
		if (b != null)
			return b;
		return button.isEnabled();
	}

	protected Composite getComposite(AbstractButton button) {
		if (isEnabled(button) == false) {
			return SRC_OVER_TRANSLUCENT;
		}
		return AlphaComposite.SrcOver;
	}

	/**
	 * Return true if this button should render as if it has the keyboard focus.
	 * This first checks the HAS_FOCUS client property to determine if an
	 * artificial state is being induced; if that is undefined then the button's
	 * hasFocus() method is consulted.
	 */
	protected static boolean hasFocus(AbstractButton button) {
		Boolean hasFocus = (Boolean) button.getClientProperty(HAS_FOCUS);
		if (hasFocus == null)
			hasFocus = button.hasFocus() && button.isEnabled();
		return hasFocus;
	}

	protected static boolean isStrokePainted(AbstractButton b) {
		Boolean t = (Boolean) b.getClientProperty(STROKE_PAINTED);
		if (t != null)
			return t;
		return b.isBorderPainted();
	}

	/**
	 * This calls the other relevant <code>paint...()</code> methods in this
	 * object. The layering of the focus varies based on whether it should be
	 * painted outside or inside the filled shape, but otherwise the layers are:
	 * <ul>
	 * <li>Filling the bounds with <code>button.getBackground()</code> (if
	 * <code>button.isOpaque()</code> is true).</li>
	 * <li>If <code>getShadowHighlight()</code> is non-null, painting the
	 * stroke/border 1 pixel below its usual location.</li>
	 * <li><code>paintBackground(g)</code></li>
	 * <li><code>paintEffects(g,false)</code></li>
	 * <li><code>paintIcon(g)</code></li>
	 * <li><code>paintText(g)</code></li>
	 * <LI><code>paintForeground(g)</code></li>
	 * <LI><code>paintEffects(g,true)</code></li>
	 * </ul>
	 * 
	 */
	@Override
	public void paint(Graphics g0, JComponent c) {
		AbstractButton button = (AbstractButton) c;

		if (isLayoutValid(button) == false)
			updateLayout(button, getButtonInfo(button));

		if (button.isOpaque()) {
			g0.setColor(button.getBackground());
			g0.fillRect(0, 0, button.getWidth(), button.getHeight());
		}

		Graphics2D g = (Graphics2D) g0.create();

		g.setComposite(getComposite(button));

		ButtonInfo info = getButtonInfo(button);

		Color highlight = fill.getShadowHighlight(button);
		if (highlight != null && isStrokePainted(button)) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g.translate(0, 1);
			g.setColor(highlight);
			g.draw(info.fill);
			g.translate(0, -1);
		}

		PaintFocus focus = getFocusPainting(button);
		boolean hasFocus = hasFocus(button);
		if (Boolean.FALSE.equals(hasFocus) || button.isFocusPainted() == false)
			focus = PaintFocus.NONE;

		// this shouldn't be an issue, but just in case:
		if (isEnabled(button) == false)
			focus = PaintFocus.NONE;

		if (focus == PaintFocus.OUTSIDE) {
			if (isFillOpaque()) {
				// the opaque fill will overwrite the inner part of
				// this stroke...
				PlafPaintUtils.paintFocus(g, info.stroke, focusSize);
			} else {
				// this still has some rendering quirks in
				// Quartz (remove the clipping to study closely)
				// ... but other than the top horizontal & vertical
				// line it's OK. And even those are ... partly there.
				Graphics2D focusG = (Graphics2D) g.create();
				GeneralPath outsideClip = new GeneralPath(Path2D.WIND_EVEN_ODD);
				outsideClip.append(new Rectangle(0, 0, button.getWidth(),
						button.getHeight()), false);
				outsideClip.append(info.fill, false);
				focusG.clip(outsideClip);
				PlafPaintUtils.paintFocus(focusG, info.stroke, focusSize);
				focusG.dispose();
			}
		}
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);

		paintBackground(g, info);
		paintEffects(g, info, true);

		g.setStroke(new BasicStroke(1));
		if (focus == PaintFocus.INSIDE) {
			Graphics2D focusG = (Graphics2D) g.create();
			focusG.clip(info.fill);
			PlafPaintUtils.paintFocus(focusG, info.stroke, focusSize);
			focusG.dispose();
			paintStroke(g, info);
		} else if (focus == PaintFocus.BOTH) {
			paintStroke(g, info);
			PlafPaintUtils.paintFocus(g, info.stroke, focusSize);
		} else {
			paintStroke(g, info);
		}

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		paintIcon(g, info);
		paintText(g, info);

		g.setComposite(isEnabled(button) ? AlphaComposite.SrcOver
				: SRC_OVER_TRANSLUCENT);
		paintForeground(g, info);
		paintEffects(g, info, false);

		g.dispose();
	}

	public void paintBackground(Graphics2D g, ButtonInfo info) {
		if (info.button.isContentAreaFilled()) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			Paint fillPaint = fill.getFill(info.button, info.fillBounds);
			g.setPaint(fillPaint);
			g.fill(info.fill);
		}
	}

	public void paintStroke(Graphics2D g, ButtonInfo info) {
		if (isStrokePainted(info.button)) {
			Paint strokeColor = fill.getStroke(info.button, info.fillBounds);
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
					RenderingHints.VALUE_STROKE_NORMALIZE);
			if (isStrokeAntialiased()) {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
			} else {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_OFF);
			}
			g.setPaint(strokeColor);
			g.draw(info.stroke);
		}
	}

	/**
	 * Paints effects for this button. The effects, if any, are listed in the
	 * <code>effects</code> field of this object.
	 * 
	 * @param g
	 *            the Graphics2D to paint to.
	 * @param background
	 *            if true, then only the effects for the background are painted.
	 *            If false, then only the effects for the foreground are
	 *            painted.
	 */
	protected void paintEffects(Graphics2D g, ButtonInfo info,
			boolean background) {
		int ctr = 0;
		while (ctr < info.effects.size()) {
			PaintUIEffect effect = info.effects.get(ctr);
			effect.paint((Graphics2D) g.create());
			if (effect.getState() == State.FINISHED) {
				info.effects.remove(ctr);
			} else {
				ctr++;
			}
		}
	}

	public void paintForeground(Graphics2D g, ButtonInfo info) {
	}

	public void paintIcon(Graphics2D g, ButtonInfo info) {
		AbstractButton button = info.button;
		Icon icon = button.getIcon();
		ButtonModel model = button.getModel();

		if (model.isRollover() && button.getRolloverIcon() != null)
			icon = button.getRolloverIcon();
		if (model.isPressed() && button.getPressedIcon() != null)
			icon = button.getPressedIcon();
		if (model.isSelected() && button.getSelectedIcon() != null)
			icon = button.getSelectedIcon();
		if (model.isRollover() && model.isSelected()
				&& button.getRolloverSelectedIcon() != null)
			icon = button.getRolloverSelectedIcon();
		if (isEnabled(button) == false && button.getDisabledIcon() != null)
			icon = button.getDisabledIcon();
		if (isEnabled(button) == false && model.isSelected()
				&& button.getDisabledIcon() != null)
			icon = button.getDisabledSelectedIcon();

		if (icon != null) {
			g.setComposite(isEnabled(button) ? AlphaComposite.SrcOver
					: SRC_OVER_TRANSLUCENT);
			icon.paintIcon(button, g, info.iconRect.x, info.iconRect.y);
		}
	}

	public void paintText(Graphics2D g, ButtonInfo info) {
		ButtonModel model = info.button.getModel();
		FontMetrics fm = info.button.getFontMetrics(info.button.getFont());
		int mnemonicIndex = info.button.getDisplayedMnemonicIndex();
		String text = info.button.getText();
		int textShiftOffset = 0;

		g.setComposite(AlphaComposite.SrcOver);
		/* Draw the Text */
		if (isEnabled(info.button)) {
			/*** paint the text normally */
			g.setColor(info.button.getForeground());
			BasicGraphicsUtils.drawStringUnderlineCharAt(g, text,
					mnemonicIndex, info.textRect.x + textShiftOffset,
					info.textRect.y + fm.getAscent() + textShiftOffset);
		} else {
			/*** paint the text disabled ***/
			g.setColor(info.button.getBackground().brighter());
			BasicGraphicsUtils.drawStringUnderlineCharAt(g, text,
					mnemonicIndex, info.textRect.x,
					info.textRect.y + fm.getAscent());
			g.setColor(info.button.getBackground().darker());
			BasicGraphicsUtils.drawStringUnderlineCharAt(g, text,
					mnemonicIndex, info.textRect.x - 1,
					info.textRect.y + fm.getAscent() - 1);
		}
	}

	@Override
	public void uninstallUI(JComponent c) {
		AbstractButton button = (AbstractButton) c;

		ButtonInfo info = getButtonInfo(button);

		button.removeMouseListener(info.basicListener);
		button.removeMouseMotionListener(info.basicListener);
		button.removeFocusListener(info.basicListener);
		button.removePropertyChangeListener(info.basicListener);
		button.removeChangeListener(info.basicListener);
		button.removeKeyListener(focusArrowListener);
		button.removeComponentListener(componentListener);
		button.removeKeyListener(keyArmingListener);
		button.removePropertyChangeListener(positionAndShapeListener);
		button.getModel().addChangeListener(info.buttonStateListener);

		super.uninstallUI(c);
	}

	/** Return the {@link com.pump.plaf.ButtonFill} used by this QButtonUI. */
	public ButtonFill getButtonFill() {
		return fill;
	}

	/**
	 * Return the {@link com.pump.plaf.ButtonShape} used by this QButtonUI.
	 */
	public ButtonShape getButtonShape() {
		return shape;
	}

	/**
	 * This may be used to take some shortcuts in rendering the button if it is
	 * assumed that -- when enabled and isContentArea is true -- the filled area
	 * of this button is opaque.
	 */
	public abstract boolean isFillOpaque();

	@Override
	public boolean contains(JComponent c, int x, int y) {
		AbstractButton button = (AbstractButton) c;
		ButtonInfo info = getButtonInfo(button);
		if (isLayoutValid(button) == false)
			updateLayout(button, info);
		return info.fill.contains(x, y);
	}

	/**
	 * Create a {@link com.pump.plaf.QComboBoxUI} modeled after this
	 * <code>QButtonUI</code>.
	 */
	public QComboBoxUI createComboBoxUI() {
		return new QComboBoxUI(this);
	}
}