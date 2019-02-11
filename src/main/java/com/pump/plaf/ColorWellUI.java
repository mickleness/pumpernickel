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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.TexturePaint;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ComponentUI;

import com.pump.swing.JColorPicker;
import com.pump.swing.JColorWell;
import com.pump.swing.JPalette;

public class ColorWellUI extends ComponentUI {

	/**
	 * This client property maps to an ActionListener that is notified when the
	 * down arrow key is pressed.
	 */
	public static final String DOWN_KEY_ACTION_PROPERTY = "downKeyAction";

	/**
	 * This client property maps to an ActionListener that is notified when the
	 * space key is pressed.
	 */
	public static final String SPACE_KEY_ACTION_PROPERTY = "spaceKeyAction";

	/**
	 * This client property maps to an ActionListener that is notified when user
	 * clicks on a ColorWell exactly once.
	 */
	public static final String SINGLE_CLICK_ACTION_PROPERTY = "singleClickAction";

	/**
	 * This client property maps to an ActionListener that is notified when user
	 * double-clicks on a ColorWell.
	 */
	public static final String DOUBLE_CLICK_ACTION_PROPERTY = "doubleClickAction";

	/**
	 * This client property maps to a JPopupMenu.
	 */
	public static final String POPUP_PALETTE_PROPERTY = "palettePopup";

	static int DOUBLE_CLICK_THRESHOLD = 500;

	static ActionListener colorPickerActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			JColorWell well = (JColorWell) e.getSource();
			Window owner = SwingUtilities.getWindowAncestor(well);
			Color newValue = JColorPicker.showDialog(owner, well
					.getColorSelectionModel().getSelectedColor());
			if (newValue != null) {
				well.getColorSelectionModel().setSelectedColor(newValue);
			}
		}
	};

	public static class ShowColorPaletteActionListener implements
			ActionListener {

		JColorWell colorWell;
		JPalette colorPalette;
		JPopupMenu popup;

		KeyListener popupKeyListener = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE
						|| e.getKeyCode() == KeyEvent.VK_ENTER
						|| e.getKeyCode() == KeyEvent.VK_TAB) {
					popup.setVisible(false);
				}
			}
		};

		Color[][] colorGrid;

		public ShowColorPaletteActionListener() {
			this(JPalette.getFlatUIColors());
		}

		public ShowColorPaletteActionListener(Color[][] colorGrid) {
			this.colorGrid = colorGrid;

			colorPalette = new JPalette(colorGrid);
			colorPalette.putClientProperty(PaletteUI.PROPERTY_HIGHLIGHT,
					PaletteUI.VALUE_HIGHLIGHT_BEVEL);
			colorPalette.setCellSize(20);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (popup != null && popup.isVisible()) {
				popup.setVisible(false);
			}

			colorWell = (JColorWell) e.getSource();
			colorPalette.setColorSelectionModel(colorWell
					.getColorSelectionModel());
			Point p = new Point(0, colorWell.getHeight());
			SwingUtilities.convertPointToScreen(p, colorWell);

			if (popup == null) {
				popup = new JPopupMenu();
				popup.add(colorPalette);
				colorWell.addPropertyChangeListener("Frame.active",
						new PropertyChangeListener() {
							public void propertyChange(PropertyChangeEvent evt) {
								popup.setVisible(false);
							}
						});
				colorWell.putClientProperty(POPUP_PALETTE_PROPERTY, popup);
				colorPalette.addKeyListener(popupKeyListener);
			}
			popup.show(colorWell, 1, colorWell.getHeight() - 1);

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					colorPalette.requestFocus();
				}
			});
		}
	};

	static {
		int uiThreshold = UIManager.getInt("doubleClickThreshold");
		if (uiThreshold != 0)
			DOUBLE_CLICK_THRESHOLD = uiThreshold;
	}

	static class ColorChangeListener implements ChangeListener {
		Component c;

		ColorChangeListener(Component c) {
			this.c = c;
		}

		public void stateChanged(ChangeEvent e) {
			c.repaint();
		}
	};

	protected static final FocusListener repaintFocusListener = new FocusListener() {
		public void focusGained(FocusEvent e) {
			JComponent jc = (JComponent) e.getSource();
			jc.repaint();
		}

		public void focusLost(FocusEvent e) {
			JComponent jc = (JComponent) e.getSource();
			jc.repaint();
		}
	};

	protected static final MouseInputAdapter mouseListener = new MouseInputAdapter() {

		@Override
		public void mouseClicked(MouseEvent e) {
			JColorWell well = (JColorWell) e.getSource();
			well.getUI().processMouseEvent(e);
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			JColorWell well = (JColorWell) e.getSource();
			well.getUI().processMouseEvent(e);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			JColorWell well = (JColorWell) e.getSource();
			well.getUI().processMouseEvent(e);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			JColorWell well = (JColorWell) e.getSource();
			well.getUI().processMouseEvent(e);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			JColorWell well = (JColorWell) e.getSource();
			well.getUI().processMouseEvent(e);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			JColorWell well = (JColorWell) e.getSource();
			well.getUI().processMouseEvent(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			JColorWell well = (JColorWell) e.getSource();
			well.getUI().processMouseEvent(e);
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			JColorWell well = (JColorWell) e.getSource();
			well.getUI().processMouseEvent(e);
		}
	};
	protected static final KeyListener keyListener = new KeyListener() {

		public void keyPressed(KeyEvent e) {
			JColorWell well = (JColorWell) e.getSource();
			well.getUI().processKeyEvent(e);
		}

		public void keyReleased(KeyEvent e) {
			JColorWell well = (JColorWell) e.getSource();
			well.getUI().processKeyEvent(e);
		}

		public void keyTyped(KeyEvent e) {
			JColorWell well = (JColorWell) e.getSource();
			well.getUI().processKeyEvent(e);
		}
	};

	protected void processKeyEvent(KeyEvent e) {
		int code = e.getKeyCode();
		JColorWell well = (JColorWell) e.getSource();
		if (!well.isEnabled())
			return;

		if (code == KeyEvent.VK_SPACE) {
			ActionListener actionListener = (ActionListener) well
					.getClientProperty(SPACE_KEY_ACTION_PROPERTY);
			if (actionListener != null)
				actionListener.actionPerformed(new ActionEvent(well, e.getID(),
						"space", e.getWhen(), e.getModifiers()));
		} else if (code == KeyEvent.VK_DOWN) {
			ActionListener actionListener = (ActionListener) well
					.getClientProperty(DOWN_KEY_ACTION_PROPERTY);
			if (actionListener != null)
				actionListener.actionPerformed(new ActionEvent(well, e.getID(),
						"down", e.getWhen(), e.getModifiers()));
		}
	}

	protected void processMouseEvent(MouseEvent e) {
		JColorWell well = (JColorWell) e.getSource();
		if (!well.isEnabled())
			return;

		ActionListener singleClickListener = (ActionListener) well
				.getClientProperty(SINGLE_CLICK_ACTION_PROPERTY);
		ActionListener doubleClickListener = (ActionListener) well
				.getClientProperty(DOUBLE_CLICK_ACTION_PROPERTY);

		if (e.getID() == MouseEvent.MOUSE_PRESSED) {
			well.requestFocus();
		}

		if (e.getID() == MouseEvent.MOUSE_PRESSED
				&& e.getButton() == MouseEvent.BUTTON1) {
			Long lastPress = (Long) well.getClientProperty("lastMousePress");
			Long currentPress = new Long(e.getWhen());
			well.putClientProperty("lastMousePress", currentPress);

			if (doubleClickListener == null) {
				if (singleClickListener != null)
					singleClickListener.actionPerformed(new ActionEvent(well, e
							.getID(), "single click", e.getWhen(), e
							.getModifiers()));
			} else {
				if (lastPress == null
						|| currentPress.longValue() - lastPress.longValue() > DOUBLE_CLICK_THRESHOLD) {
					if (singleClickListener != null) {
						SingleClickTimer singleClickTimer = new SingleClickTimer(
								well, DOUBLE_CLICK_THRESHOLD, currentPress,
								singleClickListener, e);
						singleClickTimer.start();
					}
				} else {
					doubleClickListener.actionPerformed(new ActionEvent(well, e
							.getID(), "double click", e.getWhen(), e
							.getModifiers()));
				}
			}
		}
	}

	private static ActionListener timerListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SingleClickTimer timer = (SingleClickTimer) e.getSource();
			timer.checkTime();
		}
	};

	static class SingleClickTimer extends Timer {
		private static final long serialVersionUID = 1L;

		JColorWell well;
		Long timeStamp;
		MouseEvent trigger;
		ActionListener actionListener;

		public SingleClickTimer(JColorWell well, int doubleClickThreshold,
				Long timeStamp, ActionListener actionListener,
				MouseEvent trigger) {
			super(doubleClickThreshold, timerListener);
			this.well = well;
			this.timeStamp = timeStamp;
			this.actionListener = actionListener;
			this.trigger = trigger;
			setRepeats(false);
		}

		protected void checkTime() {
			Long currentStamp = (Long) well.getClientProperty("lastMousePress");
			if (currentStamp != null && currentStamp.equals(timeStamp)) {
				actionListener.actionPerformed(new ActionEvent(well, trigger
						.getID(), "single click", trigger.getWhen(), trigger
						.getModifiers()));
			}
			stop();
		}
	}

	@Override
	public void paint(Graphics g0, JComponent c) {
		Graphics2D g = (Graphics2D) g0;
		JColorWell well = (JColorWell) c;
		Color color = well.getColorSelectionModel().getSelectedColor();
		Border border = c.getBorder();
		Insets borderInsets = border.getBorderInsets(c);
		if (color.getAlpha() < 255) {
			TexturePaint checkers = PlafPaintUtils.getCheckerBoard(8);
			g.setPaint(checkers);
			g.fillRect(borderInsets.left, borderInsets.top, c.getWidth()
					- borderInsets.left - borderInsets.right, c.getHeight()
					- borderInsets.top - borderInsets.bottom);
		}
		g.setColor(color);
		g.fillRect(borderInsets.left, borderInsets.top, c.getWidth()
				- borderInsets.left - borderInsets.right, c.getHeight()
				- borderInsets.top - borderInsets.bottom);
	}

	Map<Component, ColorChangeListener> listenerMap = new HashMap<>();

	@Override
	public void installUI(JComponent c) {
		c.addFocusListener(repaintFocusListener);
		c.addMouseListener(mouseListener);
		c.addMouseMotionListener(mouseListener);
		c.addKeyListener(keyListener);

		JColorWell well = (JColorWell) c;
		ColorChangeListener ccl = new ColorChangeListener(c);
		listenerMap.put(well, ccl);
		well.getColorSelectionModel().addChangeListener(ccl);

		ShowColorPaletteActionListener colorPaletteActionListener = new ShowColorPaletteActionListener();
		c.putClientProperty(DOUBLE_CLICK_ACTION_PROPERTY,
				colorPickerActionListener);
		c.putClientProperty(SPACE_KEY_ACTION_PROPERTY,
				colorPickerActionListener);
		c.putClientProperty(SINGLE_CLICK_ACTION_PROPERTY,
				colorPaletteActionListener);
		c.putClientProperty(DOWN_KEY_ACTION_PROPERTY,
				colorPaletteActionListener);
	}

	@Override
	public void uninstallUI(JComponent c) {
		c.removeFocusListener(repaintFocusListener);
		c.removeMouseListener(mouseListener);
		c.removeMouseMotionListener(mouseListener);
		c.removeKeyListener(keyListener);

		JColorWell well = (JColorWell) c;
		ColorChangeListener ccl = listenerMap.remove(well);
		if (ccl != null)
			well.getColorSelectionModel().removeChangeListener(ccl);
	}
}