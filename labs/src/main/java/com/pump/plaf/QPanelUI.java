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
package com.pump.plaf;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;

import com.pump.util.ObservableProperties.Edit;
import com.pump.util.ObservableProperties.Key;

/**
 */
public class QPanelUI extends GradientPanelUI {

	/**
	 * This returns a subtly off-white UI with rounded corners and a (even more
	 * subtle) one-pixel gray border.
	 * <p>
	 * This replicates Apple's <a href=
	 * "https://developer.apple.com/macos/human-interface-guidelines/windows-and-views/boxes/"
	 * >box</a> UI. Their documentation describes a box as
	 * "a type of view that’s used to create distinct, logical groupings of controls, text fields, and other interface elements."
	 */
	public static QPanelUI createBoxUI() {
		QPanelUI ui = new QPanelUI();
		formatBoxUI(ui);
		return ui;
	}

	/**
	 * This formats a QPanelUI to render as a subtly off-white UI with rounded corners and a (even more
	 * subtle) one-pixel gray border.
	 * <p>
	 * This replicates Apple's <a href=
	 * "https://developer.apple.com/macos/human-interface-guidelines/windows-and-views/boxes/"
	 * >box</a> UI. Their documentation describes a box as
	 * "a type of view that’s used to create distinct, logical groupings of controls, text fields, and other interface elements."
	 */
	public static void formatBoxUI(QPanelUI ui) {
		ui.setCornerSize(5);
		ui.setUpperStrokeColor(new Color(0, 0, 0, 30));
		ui.setLowerStrokeColor(new Color(0, 0, 0, 22));
		ui.setFillColor(new Color(0, 0, 0, 16));
	}

	/**
	 * This returns a white UI with rounded corners, a small callout, and
	 * shadow.
	 */
	public static QPanelUI createToolTipUI() {
		QPanelUI ui = new QPanelUI(Color.white);
		ui.setStrokeColor(new Color(0, 0, 0, 40));
		ui.setCornerSize(4);
		ui.setCalloutSize(5);
		ui.setShadowSize(4);
		return ui;
	}

	/**
	 * Which direction the callout should extend from the body. If you don't
	 * want a callout to be visible, use one of the centered callouts (like
	 * TOP_CENTER) and set the callout size to zero.
	 */
	public enum CalloutType {
		// @formatter:off
		TOP_CENTER(SwingConstants.TOP, SwingConstants.CENTER), 
		TOP_RIGHT(SwingConstants.TOP, SwingConstants.RIGHT), 
		RIGHT_TOP(SwingConstants.RIGHT, SwingConstants.TOP), 
		RIGHT_CENTER(SwingConstants.RIGHT, SwingConstants.CENTER), 
		RIGHT_BOTTOM(SwingConstants.RIGHT, SwingConstants.BOTTOM), 
		BOTTOM_RIGHT(SwingConstants.BOTTOM, SwingConstants.RIGHT), 
		BOTTOM_CENTER(SwingConstants.BOTTOM, SwingConstants.CENTER), 
		BOTTOM_LEFT(SwingConstants.BOTTOM, SwingConstants.LEFT), 
		LEFT_BOTTOM(SwingConstants.LEFT, SwingConstants.BOTTOM), 
		LEFT_CENTER(SwingConstants.LEFT, SwingConstants.CENTER), 
		LEFT_TOP(SwingConstants.LEFT, SwingConstants.TOP), 
		TOP_LEFT(SwingConstants.TOP, SwingConstants.LEFT);
		// @formatter:on

		int edge, orientation;

		CalloutType(int edge, int orientation) {
			this.edge = edge;
			this.orientation = orientation;
		}

		public int getEdge() {
			return edge;
		}

		public int getOrientation() {
			return orientation;
		}
	}

	enum ClipType {
		SHOW_BODY, SHOW_BORDER
	}

	protected class Outline implements Serializable {
		private static final long serialVersionUID = 1L;

		Insets shapeInsets, shadowInsets;
		GeneralPath p;
		int x, y, w, h;
		Point calloutTip = new Point();

		final float cornerSize = getCornerSize();
		final CalloutType calloutType = getCalloutType();
		final int calloutSize = getCalloutSize();
		final int shadowSize = getShadowSize();
		final int containerWidth, containerHeight;

		public Outline(int containerWidth, int containerHeight) {
			this.containerWidth = containerWidth;
			this.containerHeight = containerHeight;
			p = new GeneralPath();

			shadowInsets = new Insets((int) Math.ceil(shadowSize / 3.0),
					(int) Math.ceil(shadowSize / 2.0), shadowSize,
					(int) Math.ceil(shadowSize / 2.0));

			int z = isStrokePainted() ? 1 : 0;
			if (calloutType.getEdge() == SwingConstants.TOP) {
				x = shadowInsets.left;
				y = calloutSize + shadowInsets.top;
				w = containerWidth - z - shadowInsets.left - shadowInsets.right;
				h = containerHeight - calloutSize - z - shadowInsets.top
						- shadowInsets.bottom;
			} else if (calloutType.getEdge() == SwingConstants.BOTTOM) {
				x = shadowInsets.left;
				y = shadowInsets.top;
				w = containerWidth - z - shadowInsets.left - shadowInsets.right;
				h = containerHeight - calloutSize - z - shadowInsets.top
						- shadowInsets.bottom;
			} else if (calloutType.getEdge() == SwingConstants.LEFT) {
				x = calloutSize + shadowInsets.left;
				y = shadowInsets.top;
				w = containerWidth - calloutSize - z - shadowInsets.left
						- shadowInsets.right;
				h = containerHeight - z - shadowInsets.top
						- shadowInsets.bottom;
			} else {
				x = shadowInsets.left;
				y = shadowInsets.top;
				w = containerWidth - calloutSize - z - shadowInsets.left
						- shadowInsets.right;
				h = containerHeight - z - shadowInsets.top
						- shadowInsets.bottom;
			}
			int min = Math.min(w, h);
			int maxR = min / 2 - calloutSize;
			float effectiveCornerSize = Math.min(cornerSize, maxR);

			p = createShape(x, y, w, h, effectiveCornerSize, calloutType,
					calloutSize, calloutTip);

			Rectangle inner = getInsetRectangle();
			shapeInsets = new Insets(inner.y, inner.x, containerHeight
					- inner.y - inner.height, containerWidth - inner.x
					- inner.width);
		}

		/**
		 * Return true if this Outline is still valid for a container with the
		 * given width/height.
		 */
		protected boolean isValid(int containerWidth, int containerHeight) {
			if (containerWidth != this.containerWidth)
				return false;
			if (containerHeight != this.containerHeight)
				return false;
			if (getCornerSize() != cornerSize)
				return false;
			if (getCalloutType() != calloutType)
				return false;
			if (getCalloutSize() != calloutSize)
				return false;
			if (getShadowSize() != shadowSize)
				return false;
			return true;
		}

		private boolean isStrokePainted() {
			Color s1 = getUpperStrokeColor();
			if (s1 != null && s1.getAlpha() > 0)
				return true;
			Color s2 = getLowerStrokeColor();
			if (s2 != null && s2.getAlpha() > 0)
				return true;
			return false;
		}

		private Rectangle getInsetRectangle() {
			Rectangle r = new Rectangle();
			int z = Math.min(w / 2, h / 2);
			for (int k = 0; k < z; k++) {
				r.x = x + k;
				r.y = y + k;
				r.width = w - 2 * k;
				r.height = h - 2 * k;
				if (p.contains(r))
					return r;
			}
			// we should only reach this in fringe cases, like when the
			// CollapsibleContainer is animating a panel down to a height of
			// zero.
			if (w > 2 && h > 2)
				return new Rectangle(x + 1, y + 1, w - 2, h - 2);
			return new Rectangle(x, y, w, h);
		}

		public Point getCalloutTip() {
			return new Point(calloutTip);
		}
	}

	public static final String PROPERTY_CORNER_SIZE = "corner size";
	public static final String PROPERTY_SHADOW_SIZE = "shadow size";
	public static final String PROPERTY_CALLOUT_SIZE = "callout size";
	public static final String PROPERTY_CALLOUT_TYPE = "callout type";

	/**
	 * The attribute defining the corner's radius, ranging from 0 to 100 pixels.
	 */
	protected float cornerSize;

	/**
	 * The attribute defining the callout type, which may be null.
	 */
	CalloutType calloutType;

	/**
	 * The attribute defining how many pixels the callout extends from the
	 * shape, ranging from 0 to 20 pixels.
	 */
	protected int calloutSize;

	/**
	 * The attribute defining how many pixels of shadow will be rendered on the
	 * left/right sides. (The top/bottom sides are not event distributed; the
	 * top receives fewer pixels than the bottom.)
	 */
	int shadowSize = 0;

	private static final String PROPERTY_CACHED_REAL_OUTLINE = QPanelUI.class
			.getName() + "#cachedRealOutline";
	private static final String PROPERTY_CACHED_SCRATCH_OUTLINE = QPanelUI.class
			.getName() + "#cachedScratchOutline";

	Border border = new Border() {

		@Override
		public void paintBorder(Component c, Graphics g0, int x, int y,
				int width, int height) {
			Graphics2D g = (Graphics2D) g0.create();
			g.translate(x, y);
			paintBorderAndBody(g, (JComponent) c, ClipType.SHOW_BORDER);
			g.dispose();
		}

		@Override
		public Insets getBorderInsets(Component c) {
			Outline outline = getOutline((JPanel) c, true);
			return outline.shapeInsets;
		}

		@Override
		public boolean isBorderOpaque() {
			return false;
		}

	};

	protected JPanel panel;

	/**
	 * Create a new <code>QPanelUI</code>.
	 */
	public QPanelUI() {
		this(getDefaultColor());
	}

	public QPanelUI(Color color) {
		this(color, color);
	}

	public QPanelUI(Color topColor, Color bottomColor) {
		super(topColor, bottomColor);
		setCornerSize(0);
		setCalloutSize(0);
		// pick one that doesn't affect corners if calloutsize=0
		setCalloutType(CalloutType.TOP_CENTER);
		setShadowSize(0);
		addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (panel == null)
					return;

				boolean geometryDirty;
				if (PROPERTY_CALLOUT_TYPE.equals(evt.getPropertyName())) {
					CalloutType t1 = (CalloutType) evt.getOldValue();
					CalloutType t2 = (CalloutType) evt.getNewValue();
					if (t1 == null || t2 == null) {
						geometryDirty = true;
					} else {
						geometryDirty = t1.getEdge() != t2.getEdge();
					}
				} else if (PROPERTY_CALLOUT_SIZE.equals(
						evt.getPropertyName())
						|| PROPERTY_CORNER_SIZE.equals(
								evt.getPropertyName())
						|| PROPERTY_SHADOW_SIZE.equals(
								evt.getPropertyName())) {
					geometryDirty = true;
				} else {
					geometryDirty = false;
				}
				if (geometryDirty) {
					panel.setBorder(null);
					panel.setBorder(border);
				}
			}
		});
	}

	/**
	 * Return a copy of this QPanelUI that is not bound to a JPanel.
	 */
	@Override
	public QPanelUI clone() {
		QPanelUI returnValue = new QPanelUI();
		returnValue.setUpperFillColor(getUpperFillColor());
		returnValue.setLowerFillColor(getLowerFillColor());
		returnValue.setUpperStrokeColor(getUpperStrokeColor());
		returnValue.setLowerStrokeColor(getLowerStrokeColor());
		returnValue.setCalloutSize(getCalloutSize());
		returnValue.setCalloutType(getCalloutType());
		returnValue.setShadowSize(getShadowSize());
		returnValue.setCornerSize(getCornerSize());
		return returnValue;
	}

	@Override
	public void uninstallUI(JComponent c) {
		if (panel == c)
			panel = null;
		super.uninstallUI(c);
		c.putClientProperty(PROPERTY_CACHED_REAL_OUTLINE, null);
		c.putClientProperty(PROPERTY_CACHED_SCRATCH_OUTLINE, null);
	}

	private Outline getOutline(JPanel c, boolean ideal) {
		String key = ideal ? PROPERTY_CACHED_SCRATCH_OUTLINE
				: PROPERTY_CACHED_REAL_OUTLINE;
		Outline outline = (Outline) c.getClientProperty(key);
		int effectiveWidth = ideal ? 1000 : c.getWidth();
		int effectiveHeight = ideal ? 1000 : c.getHeight();

		if (outline == null
				|| !outline.isValid(effectiveWidth, effectiveHeight)) {
			outline = new Outline(effectiveWidth, effectiveHeight);
			c.putClientProperty(key, outline);
		}

		return outline;
	}

	private static Color getDefaultColor() {
		Color panelColor = UIManager.getColor("Panel.background");
		if (panelColor == null)
			panelColor = new Color(0xCCCCCC);
		return panelColor;
	}

	/**
	 * 
	 * @param x
	 *            the x-coordinate of the frame
	 * @param y
	 *            the y-coordinate of the frame
	 * @param w
	 *            the width of the frame
	 * @param h
	 *            the height of the frame
	 * @param cornerSize
	 *            the radius of corners (in pixels)
	 * @param calloutType
	 *            the type of Callout. If this is null the TOP_CENTER is used.
	 *            If you don't want any callout to show, pass in zero for
	 *            calloutSize.
	 * @param calloutSize
	 *            the length the callouts extend past the frame
	 * @param calloutTip
	 *            if non-null then this is populated by this method with the
	 *            (x,y) coordinates of the callout tip.
	 * @return
	 */
	protected GeneralPath createShape(float x, float y, float w, float h,
			float cornerSize, CalloutType calloutType, float calloutSize,
			Point calloutTip) {

		// if you don't want a callout to show, set the calloutsize to zero.
		if (calloutType == null)
			calloutType = CalloutType.TOP_CENTER;

		GeneralPath p = new GeneralPath();

		// write a shape clockwise from the top-left corner
		// act as if the rect is anchored at (0,0), we'll translate it later

		float k = 0.5522847498307933f;
		if (calloutType == CalloutType.TOP_LEFT) {
			p.moveTo(0, -calloutSize);
			p.lineTo(calloutSize, 0);
			if (calloutTip != null)
				calloutTip.setLocation(0, -calloutSize);
		} else if (calloutType == CalloutType.LEFT_TOP) {
			p.moveTo(0, calloutSize);
			p.lineTo(-calloutSize, 0);
			if (calloutTip != null)
				calloutTip.setLocation(-calloutSize, 0);
		} else {
			p.moveTo(0, cornerSize);
			p.curveTo(0, cornerSize - k * cornerSize, cornerSize - k
					* cornerSize, 0, cornerSize, 0);
		}

		if (calloutType == CalloutType.TOP_CENTER) {
			p.lineTo(w / 2 - calloutSize, 0);
			p.lineTo(w / 2, -calloutSize);
			p.lineTo(w / 2 + calloutSize, 0);
			if (calloutTip != null)
				calloutTip.setLocation(w / 2, -calloutSize);
		} else {
			// do nothing
		}

		if (calloutType == CalloutType.TOP_RIGHT) {
			p.lineTo(w - calloutSize, 0);
			p.lineTo(w, -calloutSize);
			if (calloutTip != null)
				calloutTip.setLocation(w, -calloutSize);
		} else if (calloutType == CalloutType.RIGHT_TOP) {
			p.lineTo(w + calloutSize, 0);
			p.lineTo(w, calloutSize);
			if (calloutTip != null)
				calloutTip.setLocation(w + calloutSize, 0);
		} else {
			p.lineTo(w - cornerSize, 0);
			p.curveTo(w - cornerSize + cornerSize * k, 0, w, cornerSize - k
					* cornerSize, w, cornerSize);
		}

		if (calloutType == CalloutType.RIGHT_CENTER) {
			p.lineTo(w, h / 2 - calloutSize);
			p.lineTo(w + calloutSize, h / 2);
			p.lineTo(w, h / 2 + calloutSize);
			if (calloutTip != null)
				calloutTip.setLocation(w + calloutSize, h / 2);
		} else {
			// do nothing
		}

		if (calloutType == CalloutType.RIGHT_BOTTOM) {
			p.lineTo(w, h - calloutSize);
			p.lineTo(w + calloutSize, h);
			if (calloutTip != null)
				calloutTip.setLocation(w + calloutSize, h);
		} else if (calloutType == CalloutType.BOTTOM_RIGHT) {
			p.lineTo(w, h + calloutSize);
			p.lineTo(w - calloutSize, h);
			if (calloutTip != null)
				calloutTip.setLocation(w, h + calloutSize);
		} else {
			p.lineTo(w, h - cornerSize);
			p.curveTo(w, h - cornerSize + cornerSize * k, w - cornerSize + k
					* cornerSize, h, w - cornerSize, h);
		}

		if (calloutType == CalloutType.BOTTOM_CENTER) {
			p.lineTo(w / 2 + calloutSize, h);
			p.lineTo(w / 2, h + calloutSize);
			p.lineTo(w / 2 - calloutSize, h);
			if (calloutTip != null)
				calloutTip.setLocation(w / 2, h + calloutSize);
		} else {
			// do nothing
		}

		if (calloutType == CalloutType.BOTTOM_LEFT) {
			p.lineTo(calloutSize, h);
			p.lineTo(0, h + calloutSize);
			if (calloutTip != null)
				calloutTip.setLocation(0, h + calloutSize);
		} else if (calloutType == CalloutType.LEFT_BOTTOM) {
			p.lineTo(-calloutSize, h);
			p.lineTo(0, h - calloutSize);
			if (calloutTip != null)
				calloutTip.setLocation(-calloutSize, h);
		} else {
			p.lineTo(cornerSize, h);
			p.curveTo(cornerSize - cornerSize * k, h, 0, h - cornerSize
					+ cornerSize * k, 0, h - cornerSize);
		}

		if (calloutType == CalloutType.LEFT_CENTER) {
			p.lineTo(0, h / 2 + calloutSize);
			p.lineTo(-calloutSize, h / 2);
			p.lineTo(0, h / 2 - calloutSize);
			if (calloutTip != null)
				calloutTip.setLocation(-calloutSize, h / 2);
		} else {
			// do nothing
		}
		p.closePath();

		p.transform(AffineTransform.getTranslateInstance(x, y));
		if (calloutTip != null) {
			calloutTip.x += x;
			calloutTip.y += y;
		}
		return p;
	}

	/**
	 * Return the {@link Border} for this UI. This border is automatically
	 * installed when a panel installs this UI.
	 */
	public Border getBorder() {
		return border;
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		paintBorderAndBody(g, c, ClipType.SHOW_BODY);
	}

	public Point getCalloutTip(JPanel p) {
		Outline outline = getOutline(p, false);
		return outline.getCalloutTip();
	}

	protected void paintBorderAndBody(Graphics g0, JComponent c,
			ClipType clipType) {
		Graphics2D g = (Graphics2D) g0.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		int width = c.getWidth();
		int height = c.getHeight();
		if (clipType == ClipType.SHOW_BODY) {
			Insets i = border.getBorderInsets(c);
			g.clipRect(i.left, i.top, width - i.left - i.right, height - i.top
					- i.bottom);
		} else {
			Area borderOutline = new Area(new Rectangle(0, 0, width, height));
			Insets insets = border.getBorderInsets(c);
			borderOutline.subtract(new Area(new Rectangle(insets.left,
					insets.top, width - insets.left - insets.right, height
							- insets.top - insets.bottom)));
			g.clip(borderOutline);
		}
		Outline outline = getOutline((JPanel) c, false);

		int shadow = getShadowSize();
		if (shadow > 0) {
			paintShadow(g, outline, shadow);
		}

		super.paintGradient(g, outline.y, outline.w,
				outline.p);
		g.dispose();
	}

	protected void paintShadow(Graphics2D g, Outline outline, int shadow) {
		g = (Graphics2D) g.create();
		for (int a = 0; a < shadow; a++) {
			float f = ((float) (a + 1)) / ((float) shadow);
			int alpha = (int) ((1 - Math.pow(f, .63)) * 7 + 1);
			g.setColor(new Color(0, 0, 0, alpha));
			float w = 1.5f * a + 1;
			g.setStroke(new BasicStroke(w, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND));
			g.translate(0, .375);

			Shape s = createShape(outline.x + 4 * f, outline.y + 2 * f,
					outline.w - 8 * f, outline.h - 4 * f, getCornerSize(),
					getCalloutType(), getCalloutSize(), null);
			g.draw(s);

			alpha = (int) ((1 - Math.pow(f, .43)) * 17 + 1);
			g.setColor(new Color(0, 0, 0, alpha));
			g.draw(outline.p);
		}
		g.dispose();
	}

	/**
	 * Return the corner size.
	 */
	public float getCornerSize() {
		return cornerSize;
	}

	/**
	 * Set the corner size of all 4 corners.
	 */
	public void setCornerSize(float cornerSize) {
		if (cornerSize < 0 || cornerSize > 100)
			throw new IllegalArgumentException("corner size must be between [0,100]");
		float oldCornerSize = this.cornerSize;
		this.cornerSize = cornerSize;
		propertyChangeSupport.firePropertyChange(PROPERTY_CORNER_SIZE, oldCornerSize, cornerSize);
	}

	@Override
	protected void installDefaults(JPanel p) {
		if (panel != null)
			throw new IllegalStateException("This QPanelUI is already assigned to JPanel. Each QPanelUI must have a 1:1 relationship with a JPanel.");
		panel = p;
		super.installDefaults(p);
		p.setBorder(border);
	}

	public CalloutType getCalloutType() {
		return calloutType;
	}

	public void setCalloutType(CalloutType calloutType) {
		CalloutType oldCalloutType = this.calloutType;
		this.calloutType = calloutType;
		propertyChangeSupport.firePropertyChange(PROPERTY_CALLOUT_TYPE, oldCalloutType, calloutType);
	}

	public int getCalloutSize() {
		return calloutSize;
	}

	public void setCalloutSize(int calloutSize) {
		if (calloutSize < 0 || calloutSize > 20)
			throw new IllegalArgumentException("callout size (" + calloutSize + ") must be between [0, 20]");
		int oldCalloutSize = this.calloutSize;
		this.calloutSize = calloutSize;
		propertyChangeSupport.firePropertyChange(PROPERTY_CALLOUT_SIZE, oldCalloutSize, calloutSize);
	}

	public int getShadowSize() {
		return shadowSize;
	}

	public void setShadowSize(int shadowSize) {
		if (shadowSize < 0 || shadowSize > 20)
			throw new IllegalArgumentException("shadow size (" + shadowSize + ") must be between [0, 20]");
		int oldShadowSize = this.shadowSize;
		this.shadowSize = shadowSize;
		propertyChangeSupport.firePropertyChange(PROPERTY_SHADOW_SIZE, oldShadowSize, shadowSize);
	}
}