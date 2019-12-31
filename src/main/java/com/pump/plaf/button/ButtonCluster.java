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
package com.pump.plaf.button;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicPanelUI;

import com.pump.plaf.button.QButtonUI.HorizontalPosition;
import com.pump.plaf.button.QButtonUI.VerticalPosition;

/**
 * This is a list of buttons that are physically clustered side-by-side (either
 * horizontally or vertically).
 * <P>
 * When this object is given a list of buttons, it will update the button
 * position property so all the buttons will render correctly. (This assumes the
 * buttons use a QButtonUI.).
 * <P>
 * If buttons in this cluster are made invisible: the position key for each
 * button may change (so the button that used to be a "middle" button is now a
 * "left" button, for example).
 * <P>
 * This is similar to a <code>ButtonGroup</code>: you may never need to call it
 * once it is constructed. It's job is to monitor components through listeners
 * without your intervention.
 * <P>
 * Also this optionally includes a <code>standardized</code> field. If this is
 * <code>true</code>, then the <code>QButtonUI</code> will make all the buttons
 * in a cluster the same approximate size.
 *
 */
public class ButtonCluster {
	private static final String CLUSTER_KEY = "com.pump.plaf.ButtonCluster.ClusterKey";

	private static final ComponentListener componentListener = new ComponentAdapter() {

		@Override
		public void componentHidden(ComponentEvent e) {
			AbstractButton button = (AbstractButton) e.getSource();
			ButtonCluster cluster = ButtonCluster.getCluster(button);
			cluster.updateSegmentPositions();
		}

		@Override
		public void componentShown(ComponentEvent e) {
			AbstractButton button = (AbstractButton) e.getSource();
			ButtonCluster cluster = ButtonCluster.getCluster(button);
			cluster.updateSegmentPositions();
		}

	};

	/**
	 * This is intended to create horizontal toolbars where all buttons share
	 * the same UI. The button positions are automatically kept up-to-date with
	 * a <code>ButtonCluster</code> object.
	 * 
	 * @param parent
	 *            this must be a <code>JToolBar</code> -- or something very
	 *            similar -- where the child components are all adjacent
	 *            buttons. If this is not the case then no exceptions will be
	 *            thrown, but your UI will appear incorrect.
	 * @param ui
	 *            the <code>QButtonUI</code> to install on each button.
	 * @param standardize
	 *            when <code>true</code> then the buttons in the resulting
	 *            clusters should be made the same approximate size.
	 */
	public static void install(JComponent parent, QButtonUI ui,
			boolean standardize) {
		install(parent, HORIZONTAL, ui, standardize);
	}

	/**
	 * This is intended to create horizontal or vertical toolbars where all
	 * buttons share the same UI. The button positions are automatically kept
	 * up-to-date with a <code>ButtonCluster</code> object.
	 * 
	 * @param parent
	 *            this must be a <code>JToolBar</code> -- or something very
	 *            similar -- where the child components are all adjacent
	 *            buttons. If this is not the case then no exceptions will be
	 *            thrown, but your UI will appear incorrect.
	 * @param orientation
	 *            either HORIZONTAL or VERTICAL
	 * @param ui
	 *            the <code>QButtonUI</code> to install on each button.
	 * @param standardize
	 *            when <code>true</code> then the buttons in the resulting
	 *            clusters should be made the same approximate size.
	 */
	public static void install(JComponent parent, int orientation,
			QButtonUI ui, boolean standardize) {
		List<AbstractButton> buttons = new ArrayList<AbstractButton>();
		for (int a = 0; a < parent.getComponentCount(); a++) {
			if (parent.getComponent(a) instanceof AbstractButton) {
				buttons.add((AbstractButton) parent.getComponent(a));
			} else {
				// hit something else... maybe a separator? let's make
				// everything we found thus far a cluster, and clear the buffer.
				AbstractButton[] array = buttons
						.toArray(new AbstractButton[buttons.size()]);
				install(orientation, ui, standardize, array);
				buttons.clear();
			}
		}
		// clear the buffer, make what's left a cluster.
		AbstractButton[] array = buttons.toArray(new AbstractButton[buttons
				.size()]);
		install(orientation, ui, standardize, array);
	}

	/**
	 * This is intended to create horizontal toolbars where all buttons share
	 * the same UI. The button positions are automatically kept up-to-date with
	 * a <code>ButtonCluster</code> object.
	 * 
	 * @param ui
	 *            the <code>QButtonUI</code> to install on each button.
	 * @param standardize
	 *            when <code>true</code> then the buttons in the resulting
	 *            clusters should be made the same approximate size.
	 * @param buttons
	 *            an array of horizontally adjacent buttons
	 */
	public static void install(QButtonUI ui, boolean standardize,
			AbstractButton... buttons) {
		install(HORIZONTAL, ui, standardize, buttons);
	}

	/**
	 * This is intended to create horizontal or vertical toolbars where all
	 * buttons share the same UI. The button positions are automatically kept
	 * up-to-date with a <code>ButtonCluster</code> object.
	 * 
	 * @param orientation
	 *            either HORIZONTAL or VERTICAL
	 * @param ui
	 *            the <code>QButtonUI</code> to install on each button.
	 * @param standardize
	 *            when <code>true</code> then the buttons in the resulting
	 *            clusters should be made the same approximate size.
	 * @param buttons
	 *            an array of adjacent buttons
	 */
	public static void install(int orientation, QButtonUI ui,
			boolean standardize, AbstractButton... buttons) {
		for (int a = 0; a < buttons.length; a++) {
			buttons[a].setUI(ui);
		}
		@SuppressWarnings("unused")
		ButtonCluster cluster = new ButtonCluster(orientation, standardize,
				buttons);
	}

	final int orientation;
	final boolean standardized;
	final AbstractButton[] buttons;
	public static final int HORIZONTAL = SwingConstants.HORIZONTAL;
	public static final int VERTICAL = SwingConstants.VERTICAL;

	public ButtonCluster(AbstractButton... buttons) {
		this(SwingConstants.HORIZONTAL, false, buttons);
	}

	public ButtonCluster(int orientation, boolean standardized,
			AbstractButton... buttons) {
		if (!(orientation == HORIZONTAL || orientation == VERTICAL))
			throw new IllegalArgumentException(
					"orientation must be HORIZONTAL or VERTICAL");

		this.standardized = standardized;
		this.buttons = new AbstractButton[buttons.length];
		System.arraycopy(buttons, 0, this.buttons, 0, buttons.length);
		this.orientation = orientation;

		for (int a = 0; a < buttons.length; a++) {
			buttons[a].addComponentListener(componentListener);
			buttons[a].putClientProperty(CLUSTER_KEY, this);
		}
		updateSegmentPositions();
	}

	public AbstractButton[] getButtons() {
		AbstractButton[] copy = new AbstractButton[buttons.length];
		System.arraycopy(buttons, 0, copy, 0, buttons.length);
		return copy;
	}

	/**
	 * @return whether buttons in this cluster are supposed to be standardized.
	 *         The <code>QButtonUI</code> consults this property to determine
	 *         the size of buttons. If this is <code>true</code>, then all
	 *         buttons in a cluster will be approximately the same size.
	 */
	public boolean isStandardized() {
		return standardized;
	}

	/**
	 * Returns the <code>ButtonCluster</code> associated with a button, or
	 * <code>null</code> if this button is not part of a cluster.
	 */
	public static ButtonCluster getCluster(AbstractButton button) {
		return (ButtonCluster) button.getClientProperty(CLUSTER_KEY);
	}

	protected void updateSegmentPositions() {
		Object FIRST, LAST, MID, ONLY;
		if (orientation == SwingConstants.VERTICAL) {
			FIRST = VerticalPosition.TOP;
			LAST = VerticalPosition.BOTTOM;
			MID = VerticalPosition.MIDDLE;
			ONLY = VerticalPosition.ONLY;
		} else {
			FIRST = HorizontalPosition.LEFT;
			LAST = HorizontalPosition.RIGHT;
			MID = HorizontalPosition.MIDDLE;
			ONLY = HorizontalPosition.ONLY;
		}

		int visibleCtr = 0;
		for (int a = 0; a < buttons.length; a++) {
			if (buttons[a].isVisible()) {
				visibleCtr++;
			}
		}

		AbstractButton[] visibleButtons = new AbstractButton[visibleCtr];
		visibleCtr = 0;
		for (int a = 0; a < buttons.length; a++) {
			if (buttons[a].isVisible()) {
				visibleButtons[visibleCtr++] = buttons[a];
			}
		}

		boolean prevComponentWasButton = false;
		for (int a = 0; a < visibleButtons.length; a++) {
			Component comp = visibleButtons[a];
			if (comp instanceof AbstractButton) {
				AbstractButton button = (AbstractButton) comp;

				// now, get the position:
				Object position;
				if (a + 1 < visibleButtons.length) {
					// if there's something after this button
					if (prevComponentWasButton) {
						position = MID;
					} else {
						position = FIRST;
					}
				} else {
					// nothing after this button
					if (prevComponentWasButton) {
						position = LAST;
					} else {
						position = ONLY;
					}
				}

				if (orientation == SwingConstants.HORIZONTAL) {
					button.putClientProperty(
							QButtonUI.PROPERTY_HORIZONTAL_POSITION, position);
					button.putClientProperty(
							QButtonUI.PROPERTY_VERTICAL_POSITION,
							VerticalPosition.ONLY);
				} else {
					button.putClientProperty(
							QButtonUI.PROPERTY_VERTICAL_POSITION, position);
					button.putClientProperty(
							QButtonUI.PROPERTY_HORIZONTAL_POSITION,
							HorizontalPosition.ONLY);
				}

				// update for next iteration:
				prevComponentWasButton = true;
			} else {
				prevComponentWasButton = false;
			}
		}
	}

	/**
	 * Create a panel containing all the buttons in this ButtonCluster.
	 * <p>
	 * This reassigns the parents of all the buttons to the returned panel, so
	 * this should only be called once.
	 * 
	 * @return a new panel that contains all the buttons in this cluster.
	 */
	public JPanel createContainer() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new GridBagLayout());

		// I'm not intimately familiar with how baselines in Swing components
		// are used, and I'm surprised that this doesn't work automatically
		// (and maybe I'm doing something wrong?)... but the following
		// custom BasicPanelUI helps guarantee the baselines are consistent
		// for a row of horizontal buttons:
		if (orientation == SwingConstants.HORIZONTAL) {
			panel.setUI(new BasicPanelUI() {

				@Override
				public int getBaseline(JComponent c, int width, int height) {
					Integer baseline = null;
					for (AbstractButton button : buttons) {
						if (button.isVisible()) {
							Dimension d = button.getPreferredSize();
							int b = button.getBaseline(d.width, d.height);
							if (baseline == null) {
								baseline = Integer.valueOf(b);
							} else if (baseline.intValue() != b) {
								return -1;
							}
						}
					}
					return baseline == null ? -1 : baseline.intValue();
				}

			});
		}
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.BASELINE;
		for (AbstractButton b : getButtons()) {
			panel.add(b, c);
			if (orientation == SwingConstants.HORIZONTAL) {
				c.gridx++;
			} else {
				c.gridy++;
			}
		}
		return panel;
	}

}