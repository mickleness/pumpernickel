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
package com.pump.plaf.combobox;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

import com.pump.icon.TriangleIcon;
import com.pump.icon.UpDownIcon;
import com.pump.plaf.AnimationManager;
import com.pump.plaf.Background;
import com.pump.plaf.button.BevelButtonUI;
import com.pump.plaf.button.ButtonState;
import com.pump.plaf.button.QButtonUI;

/**
 * This renders a JComboBox so it matches a QButtonUI in fill/shape.
 */
public class QComboBoxUI extends BasicComboBoxUI {

	/**
	 * Create a new instance of the QComboBoxUI.
	 * <p>
	 * Note this leaves which <code>QButtonUI</code> you want ambiguous. First
	 * this will check against the UIManager's <code>ButtonUI</code> property,
	 * and if that is not a <code>QButtonUI</code> then the
	 * {@link com.pump.plaf.button.RoundRectButtonUI} will be used.
	 * <p>
	 * This method is required if you want to make this ComboBoxUI the default
	 * UI by invoking: <br>
	 * <code>UIManager.getDefaults().put("ComboBoxUI",
	 "com.pump.plaf.QComboBoxUI");</code>
	 */
	public static ComponentUI createUI(JComponent c) {
		return new QComboBoxUI();
	}

	/** A small triangle pointing down. */
	protected static TriangleIcon downIcon = new TriangleIcon(
			SwingConstants.SOUTH, 6, 6);

	/**
	 * The client property that determines whether the popup menu pops down or
	 * up.
	 *
	 * @see #isPopDown()
	 * @see <a href=
	 *      "https://developer.apple.com/library/mac/#technotes/tn2007/tn2196.html">Apple
	 *      Tech Note 2196</a>
	 *
	 */
	public static final String PROPERTY_IS_POP_DOWN = "JComboBox.isPopDown";

	private static String PROPERTY_IS_MOUSE_PRESSED = QComboBoxUI.class
			.getName() + "#mousePressed";
	private static String PROPERTY_IS_MOUSE_ROLLOVER = QComboBoxUI.class
			.getName() + "#mouseRollover";

	protected static Icon upAndDownIcon = new UpDownIcon(5, 4, 3);

	protected abstract static class QComboBoxUIAction extends AbstractAction
			implements UIResource {
		private static final long serialVersionUID = 1L;
	};

	protected static final String ACTION_SELECT_PREVIOUS = "selectPrevious";
	protected static final String ACTION_SELECT_NEXT = "selectNext";
	protected static final String ACTION_SELECT_FIRST = "selectFirst";
	protected static final String ACTION_SELECT_LAST = "selectLast";
	protected static final String ACTION_SELECT_PAGE_UP = "selectPageUp";
	protected static final String ACTION_SELECT_PAGE_DOWN = "selectPageDown";
	protected static final String ACTION_POPUP_CANCEL = "popupCancel";
	protected static final String ACTION_POPUP_SHOW = "popupShow";
	protected static final String ACTION_POPUP_TOGGLE = "popupToggle";

	protected final QButtonUI buttonUI;
	private Dimension buttonMinSize = null;
	protected Rectangle currentValueBounds;
	private int isPainting = 0;

	ChangeListener arrowButtonModelChangeListener = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			refreshButtonState();
		}

	};

	FocusListener focusListener = new FocusListener() {

		@Override
		public void focusGained(FocusEvent e) {
			refreshButtonState();
		}

		@Override
		public void focusLost(FocusEvent e) {
			refreshButtonState();
		}

	};

	MouseInputAdapter mouseListener = new MouseInputAdapter() {

		@Override
		public void mousePressed(MouseEvent e) {
			JComponent jc = (JComponent) e.getComponent();
			if (!SwingUtilities.isLeftMouseButton(e) || !comboBox.isEnabled())
				return;
			jc.putClientProperty(PROPERTY_IS_MOUSE_PRESSED, Boolean.TRUE);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			JComponent jc = (JComponent) e.getComponent();
			jc.putClientProperty(PROPERTY_IS_MOUSE_PRESSED, Boolean.FALSE);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			JComponent jc = (JComponent) e.getComponent();
			if (!comboBox.isEnabled())
				return;
			jc.putClientProperty(PROPERTY_IS_MOUSE_ROLLOVER, Boolean.TRUE);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			JComponent jc = (JComponent) e.getComponent();
			jc.putClientProperty(PROPERTY_IS_MOUSE_ROLLOVER, Boolean.FALSE);
		}

	};

	PropertyChangeListener refreshPropertyChangeListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			refreshButtonState();
		}

	};

	PropertyChangeListener popDownPropertyListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent e) {
			updateArrowButtonIcon();
		}
	};

	protected QComboBoxUI() {
		QButtonUI k = null;
		try {
			String ui = (String) UIManager.getDefaults().get("ButtonUI");
			Class<?> t = Class.forName(ui);
			if (QButtonUI.class.isAssignableFrom(t)) {
				k = (QButtonUI) t.newInstance();
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		if (k == null) {
			k = new BevelButtonUI();
		}
		buttonUI = k;
	}

	public QComboBoxUI(QButtonUI buttonUI) {
		Objects.requireNonNull(buttonUI);
		this.buttonUI = buttonUI;
	}

	//
	/**
	 * This method should only be called once.
	 */
	@Override
	public void configureArrowButton() {
		super.configureArrowButton();
		arrowButton.setUI(new EmptyButtonUI());
		arrowButton.setFocusable(false);
		updateArrowButtonIcon();
	}

	/**
	 * This method may be called multiple times.
	 */
	protected void updateArrowButtonIcon() {
		Icon icon = isPopDown() ? downIcon : upAndDownIcon;
		arrowButton.setIcon(icon);
	}

	@Override
	protected ComboPopup createPopup() {
		return new BasicComboPopup(comboBox) {
			private static final long serialVersionUID = 1L;

			@Override
			public void hide() {
				try {
					super.hide();
				} finally {
					refreshButtonState();
				}
			}

			@Override
			protected void configurePopup() {
				super.configurePopup();
				Paint c = buttonUI.getButtonFill().getStroke(null, null);
				if (c instanceof Color)
					setBorder(new LineBorder((Color) c));
			}

			@Override
			public void show() {
				try {
					if (isPopDown()) {
						super.show();
						return;
					}

					showPopUp();
				} finally {
					refreshButtonState();
				}
			}

			private void showPopUp() {
				if (list.getModel().getSize() == 0)
					return;

				int i = comboBox.getSelectedIndex();
				Rectangle cellBounds = null;
				if (i == -1) {
					list.clearSelection();
					cellBounds = list.getUI().getCellBounds(list, 0, 0);
				} else {
					list.setSelectedIndex(i);
					list.ensureIndexIsVisible(i);
					cellBounds = list.getUI().getCellBounds(list, i, i);
				}
				cellBounds = SwingUtilities.convertRectangle(list, cellBounds,
						scroller);

				Dimension popupSize = comboBox.getSize();
				Insets insets = comboBox.getInsets();

				popupSize.setSize(
						popupSize.width - (insets.right + insets.left),
						getPopupHeightForRowCount(
								comboBox.getMaximumRowCount()));
				int width = currentValueBounds == null ? comboBox.getWidth()
						: currentValueBounds.width;

				int dx = 0;
				if (comboBox.getRenderer() instanceof QComboBoxRenderer) {
					QComboBoxRenderer r = (QComboBoxRenderer) comboBox
							.getRenderer();
					Icon icon = r.getLabel().getIcon();
					if (icon != null) {
						dx = -(icon.getIconWidth()
								+ r.getLabel().getIconTextGap());
					}
				}

				Rectangle popupBounds = computePopupBounds(0,
						comboBox.getBounds().height, width - dx + 12,
						popupSize.height);
				Dimension scrollSize = popupBounds.getSize();
				scroller.setMaximumSize(scrollSize);
				scroller.setPreferredSize(scrollSize);
				scroller.setMinimumSize(scrollSize);

				Point location = new Point();
				location.y = currentValueBounds.y - cellBounds.y;
				location.x = currentValueBounds.x - cellBounds.x;
				show(comboBox, location.x + dx - 1, location.y - 1);
			}
		};
	};

	@Override
	protected JButton createArrowButton() {
		JButton b = new JButton();
		return b;
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);

		// an unpleasant quirk of the BasicComboBoxUI:
		// the background is always used for the renderer.
		// if we set the background to a transparent color
		// then we get the expected results.
		comboBox.setBackground(new Color(0, 0, 0, 0));
		comboBox.setOpaque(false);

		// add some left padding because when you click an item: the popup is
		// much wider than the original JComboBox because it includes a
		// checkmark. The popup will still jut out to the left of the popup a
		// little bit, but with this offset it's a little less conspicuous
		comboBox.setBorder(
				new CompoundBorder(buttonUI.createBackground(comboBox),
						new EmptyBorder(0, 8, 0, 0)));

		// this can't be called in installListeners because arrowButton is not
		// initialized there
		arrowButton.getModel()
				.addChangeListener(arrowButtonModelChangeListener);

		comboBox.setRenderer(new QComboBoxRenderer(this));
		refreshButtonState();
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		comboBox.addFocusListener(focusListener);
		comboBox.addMouseListener(mouseListener);

		comboBox.addPropertyChangeListener(PROPERTY_IS_MOUSE_PRESSED,
				refreshPropertyChangeListener);
		comboBox.addPropertyChangeListener(PROPERTY_IS_MOUSE_ROLLOVER,
				refreshPropertyChangeListener);
		comboBox.addPropertyChangeListener(PROPERTY_IS_POP_DOWN,
				popDownPropertyListener);
	}

	@Override
	public Dimension getMinimumSize(JComponent c) {
		Dimension d = super.getMinimumSize(c);

		// if it's not a large variance: use the height of
		// a button. This way our combobox and buttons are a uniform
		// height when they're in the same row.

		if (buttonMinSize == null) {
			JButton button = new JButton("X");
			button.setUI(buttonUI);
			buttonMinSize = button.getPreferredSize();
		}
		int distance = Math.abs(d.height - buttonMinSize.height);
		if (distance < 5)
			d.height = Math.min(d.height, buttonMinSize.height);
		return d;
	}

	/**
	 * A combobox can either pop <i>down</i> (below the combobox), or it can pop
	 * <i>up</i> (or "over"). The default implementation for the BasicComboBoxUI
	 * is down, but this default implementation for this class is up. You can
	 * customize this by assigning the client property "JComboBox.isPopDown" to
	 * a Boolean.
	 * <p>
	 * The following excerpt from Apple's technical note 2196 explains when to
	 * use each:
	 * <p>
	 * This property alters the JComboBox's style to specify if it is intended
	 * to be a pop-down or a pop-up control. Pop-downs should be used when the
	 * user is expected to choose an action from the pop-down menu. Pop-ups
	 * should be used when the user is expected to make a choice from the pop-up
	 * menu that does not cause an action like a button would. Pop-up menus
	 * always appear over the pop-up control, whereas pop-down menus always
	 * appear below the pop-down control.
	 *
	 * @see <a href=
	 *      "https://developer.apple.com/library/mac/#technotes/tn2007/tn2196.html">Apple
	 *      Tech Note 2196</a>
	 */
	protected boolean isPopDown() {
		return Boolean.TRUE
				.equals(comboBox.getClientProperty(PROPERTY_IS_POP_DOWN));
	}

	/**
	 * Paints the currently selected item.
	 */
	public void paintCurrentValue(Graphics g, Rectangle bounds,
			boolean hasFocus) {
		currentValueBounds = new Rectangle(bounds);
		super.paintCurrentValue(g, bounds, false);
	}

	@Override
	public void paintCurrentValueBackground(Graphics g, Rectangle bounds,
			boolean hasFocus) {
		// do nothing
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();

		arrowButton.getModel()
				.removeChangeListener(arrowButtonModelChangeListener);

		comboBox.removeFocusListener(focusListener);
		comboBox.removeMouseListener(mouseListener);

		comboBox.removePropertyChangeListener(PROPERTY_IS_MOUSE_PRESSED,
				refreshPropertyChangeListener);
		comboBox.removePropertyChangeListener(PROPERTY_IS_MOUSE_ROLLOVER,
				refreshPropertyChangeListener);
		comboBox.removePropertyChangeListener(PROPERTY_IS_POP_DOWN,
				popDownPropertyListener);
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		isPainting++;
		try {
			Background.paintBackgroundContents(g, c);
			super.paint(g, c);
		} finally {
			isPainting--;
		}
	}

	@Override
	protected void installKeyboardActions() {
		// It appears if you start off with an AquaComboBoxUI and then
		// install this UI: these leftover action map keys still
		// trigger the *old* AquaComboBoxUI actions, which ends up
		// triggering a NPE because that AquaComboBoxUI is unused:
		ActionMap actionMap = comboBox.getActionMap();
		for (String aquaKey : new String[] { "aquaSelectNext",
				"aquaSelectPrevious", "aquaEnterPressed", "aquaSpacePressed",
				"aquaSelectHome", "aquaSelectEnd", "aquaSelectPageUp",
				"aquaSelectPageDown" }) {
			actionMap.put(aquaKey, null);
		}

		super.installKeyboardActions();

		comboBox.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
				ACTION_SELECT_PREVIOUS);
		comboBox.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_UP, 0),
				ACTION_SELECT_PREVIOUS);
		comboBox.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
				ACTION_SELECT_NEXT);
		comboBox.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_KP_DOWN, 0),
				ACTION_SELECT_NEXT);
		comboBox.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_CLEAR, 0),
				ACTION_POPUP_CANCEL);
		comboBox.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				ACTION_POPUP_CANCEL);
		comboBox.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_CANCEL, 0),
				ACTION_POPUP_CANCEL);
		comboBox.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
				ACTION_POPUP_CANCEL);
		comboBox.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0),
				ACTION_POPUP_CANCEL);
		comboBox.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_STOP, 0),
				ACTION_POPUP_CANCEL);

		comboBox.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0),
				ACTION_SELECT_FIRST);
		comboBox.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0),
				ACTION_SELECT_LAST);
		comboBox.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0),
				ACTION_SELECT_PAGE_UP);
		comboBox.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0),
				ACTION_SELECT_PAGE_DOWN);

		comboBox.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
				ACTION_POPUP_TOGGLE);
		comboBox.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0),
				ACTION_POPUP_TOGGLE);
		comboBox.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_BEGIN, 0),
				ACTION_POPUP_SHOW);

		listBox.setAutoscrolls(true);
		comboBox.getActionMap().put(ACTION_SELECT_PREVIOUS,
				new QComboBoxUIAction() {

					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						if (!listBox.isShowing()) {
							popup.show();
							return;
						}

						int i = listBox.getSelectedIndex();

						if (i == 0)
							return;

						listBox.setSelectedIndex(i - 1);
					}

				});

		comboBox.getActionMap().put(ACTION_SELECT_NEXT,
				new QComboBoxUIAction() {

					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						if (!listBox.isShowing()) {
							popup.show();
							return;
						}

						int i = listBox.getSelectedIndex();

						if (i == listBox.getModel().getSize() - 1)
							return;

						listBox.setSelectedIndex(i + 1);
					}

				});

		comboBox.getActionMap().put(ACTION_SELECT_FIRST,
				new QComboBoxUIAction() {

					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						if (!listBox.isShowing()) {
							popup.show();
							return;
						}

						listBox.setSelectedIndex(0);
					}

				});

		comboBox.getActionMap().put(ACTION_SELECT_LAST,
				new QComboBoxUIAction() {

					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						if (!listBox.isShowing()) {
							popup.show();
							return;
						}

						listBox.setSelectedIndex(
								listBox.getModel().getSize() - 1);
					}

				});

		comboBox.getActionMap().put(ACTION_SELECT_PAGE_UP,
				new QComboBoxUIAction() {

					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						if (!listBox.isShowing()) {
							popup.show();
							return;
						}

						int i = listBox.getSelectedIndex();
						int pageStart = listBox.getFirstVisibleIndex();
						if (pageStart != i) {
							listBox.setSelectedIndex(pageStart);
						} else {
							int pageEnd = listBox.getLastVisibleIndex();
							int pageSize = pageEnd - pageStart;
							int newIndex = Math.max(pageStart - pageSize, 0);
							listBox.setSelectedIndex(newIndex);
						}
					}

				});

		comboBox.getActionMap().put(ACTION_SELECT_PAGE_DOWN,
				new QComboBoxUIAction() {

					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						if (!listBox.isShowing()) {
							popup.show();
							return;
						}

						int i = listBox.getSelectedIndex();
						int pageEnd = listBox.getLastVisibleIndex();
						if (pageEnd != i) {
							listBox.setSelectedIndex(pageEnd);
						} else {
							int pageStart = listBox.getFirstVisibleIndex();
							int pageSize = pageEnd - pageStart;
							int newIndex = Math.min(pageStart + pageSize,
									listBox.getModel().getSize() - 1);
							listBox.setSelectedIndex(newIndex);
						}
					}

				});

		comboBox.getActionMap().put(ACTION_POPUP_CANCEL,
				new QComboBoxUIAction() {

					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						popup.hide();
					}

				});

		comboBox.getActionMap().put(ACTION_POPUP_SHOW, new QComboBoxUIAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				popup.show();
			}

		});

		comboBox.getActionMap().put(ACTION_POPUP_TOGGLE,
				new QComboBoxUIAction() {

					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						if (popup.isVisible()) {
							comboBox.setSelectedIndex(
									listBox.getSelectedIndex());
							popup.hide();
						} else {
							popup.show();
						}
					}

				});
	}

	@Override
	protected void uninstallKeyboardActions() {
		super.uninstallKeyboardActions();

		// purge all our actions
		ActionMap actionMap = comboBox.getActionMap();
		for (Object key : actionMap.allKeys()) {
			if (actionMap.get(key) instanceof QComboBoxUIAction) {
				actionMap.put(key, null);
			}
		}
	}

	/**
	 * Return true if the mouse is pressed on the JComboBox. (But this does not
	 * relate to the popup.)
	 */
	public boolean isPressed() {
		return Boolean.TRUE
				.equals(comboBox.getClientProperty(PROPERTY_IS_MOUSE_PRESSED));
	}

	/**
	 * Return true if the mouse is hovering over the JComboBox. (But this does
	 * not relate to the popup.)
	 */
	public boolean isRollover() {
		return Boolean.TRUE
				.equals(comboBox.getClientProperty(PROPERTY_IS_MOUSE_ROLLOVER));
	}

	/**
	 * Updates the ButtonState.Float associated with a popup (which is used to
	 * control how it is rendered).
	 */
	protected void refreshButtonState() {
		ButtonState.Float state = new ButtonState.Float(
				new ButtonState.Boolean(comboBox.isEnabled(),
						arrowButton.getModel().isSelected()
								|| popup.isVisible(),
						arrowButton.getModel().isPressed() || isPressed(),
						arrowButton.getModel().isArmed(),
						arrowButton.getModel().isRollover() || isRollover()));
		AnimationManager.setTargetProperty(comboBox,
				QButtonUI.PROPERTY_FLOAT_BUTTON_STATE,
				new AnimationManager.ButtonStateAdjuster(.1f, state));

	}

	@Override
	protected Dimension getDisplaySize() {
		isPainting++;
		try {
			return super.getDisplaySize();
		} finally {
			isPainting--;
		}
	}

	/**
	 * Return true if we're current inside this object's
	 * {@link #paint(Graphics, JComponent)} method. This is used to distinguish
	 * when the cell renderer is rendering for the JComboBox or for the popup
	 * JList.
	 */
	public boolean isPaintingComboBox() {
		return isPainting > 0;
	}

	/**
	 * Return the JComboBox this UI controls.
	 */
	public JComboBox<?> getComboBox() {
		// this method was added for the benefit of the QComboBoxRenderer
		return comboBox;
	}

	/**
	 * Return the JList this combobox shows when the combobox's popup is
	 * visible. This will always return a non-null value, but the returned value
	 * may not be showing.
	 */
	public JList getList() {
		return listBox;
	}
}