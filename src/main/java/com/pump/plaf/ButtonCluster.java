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

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;

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
				install(array, orientation, ui, standardize);
				buttons.clear();
			}
		}
		// clear the buffer, make what's left a cluster.
		AbstractButton[] array = buttons.toArray(new AbstractButton[buttons
				.size()]);
		install(array, orientation, ui, standardize);
	}

	/**
	 * This is intended to create horizontal toolbars where all buttons share
	 * the same UI. The button positions are automatically kept up-to-date with
	 * a <code>ButtonCluster</code> object.
	 * 
	 * @param buttons
	 *            an array of horizontally adjacent buttons
	 * @param ui
	 *            the <code>QButtonUI</code> to install on each button.
	 * @param standardize
	 *            when <code>true</code> then the buttons in the resulting
	 *            clusters should be made the same approximate size.
	 */
	public static void install(AbstractButton[] buttons, QButtonUI ui,
			boolean standardize) {
		install(buttons, HORIZONTAL, ui, standardize);
	}

	/**
	 * This is intended to create horizontal or vertical toolbars where all
	 * buttons share the same UI. The button positions are automatically kept
	 * up-to-date with a <code>ButtonCluster</code> object.
	 * 
	 * @param buttons
	 *            an array of adjacent buttons
	 * @param orientation
	 *            either HORIZONTAL or VERTICAL
	 * @param ui
	 *            the <code>QButtonUI</code> to install on each button.
	 * @param standardize
	 *            when <code>true</code> then the buttons in the resulting
	 *            clusters should be made the same approximate size.
	 */
	public static void install(AbstractButton[] buttons, int orientation,
			QButtonUI ui, boolean standardize) {
		for (int a = 0; a < buttons.length; a++) {
			buttons[a].setUI(ui);
		}
		@SuppressWarnings("unused")
		ButtonCluster cluster = new ButtonCluster(buttons, orientation,
				standardize);
	}

	final int orientation;
	final boolean standardized;
	final AbstractButton[] buttons;
	public static final int HORIZONTAL = SwingConstants.HORIZONTAL;
	public static final int VERTICAL = SwingConstants.VERTICAL;

	public ButtonCluster(AbstractButton[] buttons, int orientation,
			boolean standardized) {
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
		String MID = QButtonUI.MIDDLE;
		String ONLY = QButtonUI.ONLY;
		String FIRST, LAST;
		if (orientation == SwingConstants.VERTICAL) {
			FIRST = QButtonUI.TOP;
			LAST = QButtonUI.BOTTOM;
		} else {
			FIRST = QButtonUI.LEFT;
			LAST = QButtonUI.RIGHT;
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
				String position;
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
					button.putClientProperty(QButtonUI.HORIZONTAL_POSITION,
							position);
					button.putClientProperty(QButtonUI.VERTICAL_POSITION, ONLY);
				} else {
					button.putClientProperty(QButtonUI.VERTICAL_POSITION,
							position);
					button.putClientProperty(QButtonUI.HORIZONTAL_POSITION,
							ONLY);
				}

				// update for next iteration:
				prevComponentWasButton = true;
			} else {
				prevComponentWasButton = false;
			}
		}
	}

}