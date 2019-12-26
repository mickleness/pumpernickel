package com.pump.plaf.button;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.image.ImageBounds;

public class MixedCheckBoxState {
	private static final String PROPERTY_MIXED = MixedCheckBoxState.class
			.getName() + "#mixedActive";
	private static final String PROPERTY_REGISTERED = MixedCheckBoxState.class
			.getName() + "#registered";
	private static Map<Object, BufferedImage> mixedStateIcons = new HashMap<>();
	static {
		JCheckBox normalCheckBox = new JCheckBox();
		JCheckBox focusedCheckBox = new JCheckBox() {
			@Override
			public boolean hasFocus() {
				return true;
			}

			@Override
			public boolean isFocusOwner() {
				return true;
			}
		};
		boolean[] booleanStates = new boolean[] { false, true };
		int xMin = Integer.MAX_VALUE;
		int yMin = Integer.MAX_VALUE;
		int xMax = -1;
		int yMax = -1;
		for (boolean armed : booleanStates) {
			for (boolean rollover : booleanStates) {
				for (boolean pressed : booleanStates) {
					for (boolean enabled : booleanStates) {
						for (boolean focused : booleanStates) {
							JCheckBox b = focused ? focusedCheckBox
									: normalCheckBox;
							b.getModel().setArmed(armed);
							b.getModel().setRollover(rollover);
							b.getModel().setPressed(pressed);
							b.setEnabled(enabled);
							Dimension d = b.getPreferredSize();
							BufferedImage bi = new BufferedImage(d.width,
									d.height, BufferedImage.TYPE_INT_ARGB);
							b.setSize(d);
							Graphics2D g = bi.createGraphics();
							b.paint(g);
							g.dispose();
							Rectangle r = ImageBounds.getBounds(bi, 10);
							xMin = Math.min(xMin, r.x);
							yMin = Math.min(yMin, r.y);
							xMax = Math.max(xMax, r.x + r.width);
							yMax = Math.max(yMax, r.y + r.height);
							ButtonState.Boolean state = new ButtonState.Boolean(
									enabled, false, pressed, armed, rollover);
							Object key = Arrays.asList(state, focused);
							mixedStateIcons.put(key, bi);
						}
					}
				}
			}
		}
		for (Entry<Object, BufferedImage> entry : mixedStateIcons.entrySet()) {
			BufferedImage bi = entry.getValue();
			bi = bi.getSubimage(xMin, yMin, xMax - xMin, yMax - yMin);
			Graphics2D g = bi.createGraphics();
			g.setColor(SystemColor.controlHighlight);
			g.setStroke(new BasicStroke(2.1f));
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
					RenderingHints.VALUE_STROKE_PURE);
			int w = bi.getWidth();
			int h = bi.getHeight();
			int k = Math.min(w, h) / 5;
			g.draw(new Line2D.Float(w / 2 - k, h / 2 - k, w / 2 + k, h / 2 + k));
			g.dispose();
			entry.setValue(bi);
		}
	}

	private static void register(final JCheckBox checkBox) {
		if (checkBox.getClientProperty(PROPERTY_REGISTERED) == null) {
			checkBox.putClientProperty(PROPERTY_REGISTERED, true);
			checkBox.addPropertyChangeListener(PROPERTY_MIXED,
					new PropertyChangeListener() {
						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							refreshMixedStateIcon(checkBox);
						}
					});
			checkBox.getModel().addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					refreshMixedStateIcon(checkBox);
				}
			});
			checkBox.addFocusListener(new FocusListener() {
				@Override
				public void focusGained(FocusEvent e) {
					refreshMixedStateIcon(checkBox);
				}

				@Override
				public void focusLost(FocusEvent e) {
					refreshMixedStateIcon(checkBox);
				}
			});
		}
	}

	private static void refreshMixedStateIcon(JCheckBox b) {
		Font oldFont = b.getFont();
		try {
			if (isMixedState(b)) {
				ButtonState.Boolean state = new ButtonState.Boolean(
						b.getModel());
				Object key = Arrays.asList(state, b.isFocusOwner());
				BufferedImage bi = mixedStateIcons.get(key);
				b.setIcon(new ImageIcon(bi));
			} else {
				b.setIcon(null);
			}
		} finally {
			b.setFont(oldFont);
		}
	}

	public static boolean isMixedState(JCheckBox checkBox) {
		if (checkBox.isSelected())
			return false;
		return Boolean.TRUE.equals(checkBox.getClientProperty(PROPERTY_MIXED));
	}

	public static boolean setMixed(JCheckBox checkBox, boolean mixedState) {
		register(checkBox);
		if (mixedState) {
			Boolean oldValue = (Boolean) checkBox
					.getClientProperty(PROPERTY_MIXED);
			if (Boolean.TRUE.equals(oldValue)) {
				return false;
			} else {
				checkBox.putClientProperty(PROPERTY_MIXED, mixedState);
			}
		} else {
			Boolean oldValue = (Boolean) checkBox
					.getClientProperty(PROPERTY_MIXED);
			if (oldValue == null) {
				return false;
			} else {
				checkBox.putClientProperty(PROPERTY_MIXED, null);
			}
		}
		return true;
	}
}