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
package com.pump.swing.popup;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.Window.Type;
import java.lang.reflect.Field;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToolTip;
import javax.swing.JWindow;
import javax.swing.Popup;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicSliderUI;

import com.pump.plaf.QPanelUI;
import com.pump.plaf.QPanelUI.CalloutType;

public class QPopup extends Popup {

	/**
	 * This client property on owners resolves to a CalloutType or an array of
	 * CalloutType that should be used for that particular owner. If this is
	 * undefined then any CalloutType may be used.
	 */
	public static final String PROPERTY_CALLOUT_TYPE = QPopup.class.getName()
			+ "#calloutType";

	private static final String PROPERTY_IS_QPOPUP = QPopup.class.getName()
			+ "#isQPopup";

	private static final CalloutType[] ORDERED_CALLOUT_TYPES = new CalloutType[] {
			CalloutType.TOP_CENTER, CalloutType.TOP_RIGHT,
			CalloutType.TOP_LEFT, CalloutType.BOTTOM_CENTER,
			CalloutType.BOTTOM_RIGHT, CalloutType.BOTTOM_LEFT,
			CalloutType.LEFT_CENTER, CalloutType.LEFT_TOP,
			CalloutType.LEFT_BOTTOM, CalloutType.RIGHT_CENTER,
			CalloutType.RIGHT_TOP, CalloutType.RIGHT_BOTTOM };

	private static final int CALLOUT_SIZE = 5;

	protected JPanel contents;
	protected Component owner;
	protected QPanelUI ui;
	protected Point screenLoc;

	/**
	 * Create a QPopup that will use callouts.
	 * 
	 * @param owner
	 * @param contents
	 */
	public QPopup(Component owner, JPanel contents) {
		this(owner, contents, null);
	}

	/**
	 * Create a QPopup.
	 * 
	 * @param owner
	 * @param contents
	 * @param screenLoc
	 *            if this is non-null, then the popup should be placed at these
	 *            screen coordinates. If this is null, then callouts will be
	 *            used to align the popup with the owner.
	 */
	public QPopup(Component owner, JPanel contents, Point screenLoc) {
		this.owner = owner;
		this.contents = contents;
		this.screenLoc = screenLoc == null ? null : new Point(screenLoc);
		ui = (QPanelUI) contents.getUI();
	}

	@Override
	public void show() {
		Point z = getScreenLocation();
		if (z != null) {
			ui.setCalloutSize(0);
			if (showUsingRootPaneContainer(z, null))
				return;
			showUsingWindow(z, null, true);
		} else {
			CalloutType[] calloutTypes = getCalloutTypes();
			for (CalloutType type : calloutTypes) {
				Point p = getScreenLoc(type);
				if (showUsingRootPaneContainer(p, type)) {
					return;
				}
			}

			for (CalloutType type : calloutTypes) {
				Point p = getScreenLoc(type);
				if (showUsingWindow(p, type, false))
					return;
			}
			Point p = getScreenLoc(calloutTypes[0]);
			showUsingWindow(p, calloutTypes[0], true);
		}
	}

	protected CalloutType[] getCalloutTypes() {
		if (owner instanceof JComponent) {
			Object v = ((JComponent) owner)
					.getClientProperty(PROPERTY_CALLOUT_TYPE);
			if (v instanceof CalloutType) {
				return new CalloutType[] { (CalloutType) v };
			}
			if (v != null
					&& v.getClass().isArray()
					&& v.getClass().getComponentType()
							.equals(CalloutType.class))
				return ((CalloutType[]) v);
		}

		return ORDERED_CALLOUT_TYPES;
	}

	protected Point getScreenLoc(CalloutType type) {
		if (owner instanceof JSlider) {
			return getScreenLocForSliderOwner((JSlider) owner, type);
		}
		return getScreenLocForGenericOwner(owner, type);
	}

	protected Point getScreenLocForGenericOwner(Component owner,
			CalloutType type) {
		int minX = 0;
		int maxX = owner.getWidth();
		int minY = 0;
		int maxY = owner.getHeight();
		if (owner instanceof JComponent) {
			Insets insets = ((JComponent) owner).getInsets();

			minX += Math.min(10, insets.left);
			maxX -= Math.min(10, insets.right);
			minY += Math.min(10, insets.top);
			maxY -= Math.min(10, insets.bottom);
		}

		return getScreenLocForGenericOwner(owner, minX, minY, maxX - minX, maxY
				- minY, type);
	}

	protected Point getScreenLocForSliderOwner(JSlider slider, CalloutType type) {
		Rectangle thumb = getSliderThumbRect(slider);
		if (thumb == null)
			return getScreenLocForGenericOwner(slider, type);

		return getScreenLocForGenericOwner(slider, thumb.x, thumb.y,
				thumb.width, thumb.height, type);
	}

	protected Rectangle getSliderThumbRect(JSlider slider) {
		BasicSliderUI sliderUI = slider.getUI() instanceof BasicSliderUI ? (BasicSliderUI) slider
				.getUI() : null;
		if (ui == null)
			return null;

		try {
			Field f = BasicSliderUI.class.getDeclaredField("thumbRect");
			f.setAccessible(true);
			Rectangle thumbRect = (Rectangle) f.get(sliderUI);
			if (thumbRect != null && !thumbRect.isEmpty())
				return new Rectangle(thumbRect);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

		// this... kinda worked. It derives the thumb location.
		// But it was still a little imprecise, and just
		// grabbing the thumbRect is simplest.

		// int value = slider.getValue();
		// if (slider.getOrientation() == JSlider.HORIZONTAL) {
		// float bestX = slider.getWidth() / 2;
		// int span = 0;
		// int bestDiff = Integer.MAX_VALUE;
		// for (int x = 0; x < slider.getWidth(); x++) {
		// int v = sliderUI.valueForXPosition(x);
		// int diff = (Math.abs(value - v));
		// if (diff < bestDiff) {
		// bestX = x;
		// span = 1;
		// bestDiff = diff;
		// } else if (diff == bestDiff) {
		// bestX = (bestX * span + x) / (++span);
		// }
		// }
		// return new Point((int) Math.round(bestX), slider.getHeight());
		// } else if (slider.getOrientation() == JSlider.VERTICAL) {
		// float bestY = slider.getHeight() / 2;
		// int span = 0;
		// int bestDiff = Integer.MAX_VALUE;
		// for (int y = 0; y < slider.getHeight(); y++) {
		// int v = ui.valueForYPosition(y);
		// int diff = (Math.abs(value - v));
		// if (diff < bestDiff) {
		// bestY = y;
		// span = 1;
		// bestDiff = diff;
		// } else if (diff == bestDiff) {
		// bestY = (bestY * span + y) / (++span);
		// }
		// }
		// return new Point(slider.getWidth(), (int) Math.round(bestY));
		// }
		// throw new RuntimeException("Unexpected orientation: "
		// + slider.getOrientation());

	}

	protected Point getScreenLocForGenericOwner(Component owner, int x, int y,
			int w, int h, CalloutType type) {
		int minX = x;
		int minY = y;
		int maxX = x + w;
		int maxY = y + h;
		int midX = (minX + maxX) / 2;
		int midY = (minY + maxY) / 2;

		Point p;
		if (type == CalloutType.TOP_LEFT) {
			p = new Point(minX, maxY);
		} else if (type == CalloutType.TOP_RIGHT) {
			p = new Point(maxX, maxY);
		} else if (type == CalloutType.RIGHT_TOP) {
			p = new Point(minX, minY);
		} else if (type == CalloutType.RIGHT_CENTER) {
			p = new Point(minX, midY);
		} else if (type == CalloutType.RIGHT_BOTTOM) {
			p = new Point(minX, maxY);
		} else if (type == CalloutType.BOTTOM_RIGHT) {
			p = new Point(maxX, minY);
		} else if (type == CalloutType.BOTTOM_CENTER) {
			p = new Point(midX, minY);
		} else if (type == CalloutType.BOTTOM_LEFT) {
			p = new Point(minX, minY);
		} else if (type == CalloutType.LEFT_BOTTOM) {
			p = new Point(maxX, maxY);
		} else if (type == CalloutType.LEFT_CENTER) {
			p = new Point(maxX, midY);
		} else if (type == CalloutType.LEFT_TOP) {
			p = new Point(maxX, minY);
		} else { // TOP_CENTER:
			p = new Point(midX, maxY);
		}
		SwingUtilities.convertPointToScreen(p, owner);
		return p;
	}

	private boolean showUsingRootPaneContainer(Point screenLoc,
			CalloutType calloutType) {
		Window ownerWindow = owner instanceof Window ? (Window) owner
				: SwingUtilities.getWindowAncestor(owner);
		RootPaneContainer rpc = ownerWindow instanceof RootPaneContainer ? (RootPaneContainer) ownerWindow
				: null;

		if (rpc == null)
			return false;

		JLayeredPane layeredPane = rpc.getLayeredPane();
		Point layeredPaneLoc = new Point(screenLoc);
		SwingUtilities.convertPointFromScreen(layeredPaneLoc, layeredPane);

		if (calloutType == null) {
			ui.setCalloutType(CalloutType.TOP_CENTER);
			ui.setCalloutSize(0);
		} else {
			ui.setCalloutType(calloutType);
			ui.setCalloutSize(CALLOUT_SIZE);
		}

		contents.validate();
		contents.setSize(contents.getPreferredSize());

		Rectangle layeredPaneBounds = new Rectangle(layeredPaneLoc.x,
				layeredPaneLoc.y, contents.getWidth(), contents.getHeight());
		if (calloutType != null) {
			Point calloutTip = ui.getCalloutTip(contents);
			layeredPaneBounds.x -= calloutTip.x;
			layeredPaneBounds.y -= calloutTip.y;
		}

		if (new Rectangle(0, 0, layeredPane.getWidth(), layeredPane.getHeight())
				.contains(layeredPaneBounds)) {

			if (contents.getParent() != layeredPane) {
				hide();
				layeredPane.add(contents, JLayeredPane.POPUP_LAYER);
			}

			contents.setBounds(layeredPaneBounds);
			contents.setVisible(true);

			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param screenLoc
	 * @param calloutType
	 * @param forceShow
	 *            if true then this method will always return true and try to
	 *            show a window. If false then this method may decide to return
	 *            false and not show the popup. For example: if the popup would
	 *            fall far outside the window, this may return false in hopes
	 *            that another attempt will get better coverage.
	 * @return
	 */
	private boolean showUsingWindow(Point screenLoc, CalloutType calloutType,
			boolean forceShow) {
		Point windowLoc = new Point(screenLoc);
		if (calloutType == null) {
			ui.setCalloutType(CalloutType.TOP_CENTER);
			ui.setCalloutSize(0);
		} else {
			ui.setCalloutType(calloutType);
			ui.setCalloutSize(CALLOUT_SIZE);
		}

		contents.validate();
		contents.setSize(contents.getPreferredSize());

		if (calloutType != null) {
			Point calloutTip = ui.getCalloutTip(contents);
			windowLoc.x -= calloutTip.x;
			windowLoc.y -= calloutTip.y;
		}

		Rectangle windowBounds = new Rectangle(windowLoc, contents.getSize());

		if (!forceShow && !isScreenRectVisible(windowBounds))
			return false;

		// closing and creating new windows to constantly reposition causes
		// flickering; we should reuse the existing window if we're already
		// visible.
		JWindow window = null;
		if (contents.getParent() != null) {
			Window w = SwingUtilities.getWindowAncestor(contents);
			if (w instanceof JWindow) {
				JWindow jw = (JWindow) w;
				Boolean b = (Boolean) jw.getRootPane().getClientProperty(
						PROPERTY_IS_QPOPUP);
				if (Boolean.TRUE.equals(b)) {
					window = jw;
				}
			}

			if (window == null) {
				hide();
			}
		}
		if (window == null) {
			window = createWindow();
		}

		if (isToolTip())
			window.setFocusable(false);
		window.setBounds(windowBounds);
		window.setVisible(true);
		window.setAlwaysOnTop(true);
		window.toFront();
		return true;
	}

	private boolean isScreenRectVisible(Rectangle screenRect) {
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice[] gds = ge.getScreenDevices();
		for (GraphicsDevice gd : gds) {
			for (GraphicsConfiguration gc : gd.getConfigurations()) {
				if (gc.getBounds().contains(screenRect)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Return true if this popup is being used to display a tooltip.
	 */
	protected boolean isToolTip() {
		for (int a = 0; a < contents.getComponentCount(); a++) {
			Component child = contents.getComponent(a);
			if (!(child instanceof JToolTip))
				return false;
		}
		return contents.getComponentCount() > 0;
	}

	/**
	 * Create a transparent window
	 */
	protected JWindow createWindow() {
		JWindow window = new JWindow();
		window.getRootPane().putClientProperty(PROPERTY_IS_QPOPUP, true);
		window.setType(Type.POPUP);
		window.getRootPane().putClientProperty("Window.shadow", Boolean.FALSE);
		float k = .95f;
		window.getRootPane().putClientProperty("Window.opacity", k);
		window.setOpacity(k);
		window.setBackground(new Color(0, 0, 0, 0));
		window.getRootPane().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		window.getRootPane().add(contents, c);
		return window;
	}

	@Override
	public void hide() {
		Window w = SwingUtilities.getWindowAncestor(contents);
		Container parent = contents.getParent();
		if (parent != null) {
			Rectangle r = contents.getBounds();
			parent.remove(contents);
			parent.repaint(r.x, r.y, r.width, r.height);
		}
		if (w instanceof RootPaneContainer) {
			RootPaneContainer rpc = (RootPaneContainer) w;
			Boolean b = (Boolean) rpc.getRootPane().getClientProperty(
					PROPERTY_IS_QPOPUP);
			if (Boolean.TRUE.equals(b)) {
				w.setVisible(false);
				w.dispose();
			}
		}
	}

	public Component getOwner() {
		return owner;
	}

	public JPanel getContents() {
		return contents;
	}

	public Point getScreenLocation() {
		if (screenLoc == null)
			return null;
		return new Point(screenLoc);
	}
}