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
package com.pump.inspector;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSlider;

/**
 * This manages a set of controls arranged as a series of rows.
 * <p>
 * There are two columns: the smaller "lead" (or "ID") column on the left which
 * takes up only as much width as it has to, and the "main" column which takes
 * up the remaining width on the right.
 * <p>
 * All lead controls are right-aligned. All main controls should generally be
 * right-aligned.
 * 
 * @see <a
 *      href="https://javagraphics.blogspot.com/2009/06/layouts-designing-inspector.html">Layouts:
 *      Designing an Inspector</a>
 */
public class Inspector {

	/**
	 * These describe the possible position of of a component when insets are
	 * applied.
	 */
	public enum Position {
		LEAD, MAIN_ONLY_STRETCH_TO_FILL, MAIN_ONLY_NO_STRETCH, MAIN_WITH_LEAD_STRETCH_TO_FILL, MAIN_WITH_LEAD_NO_STRETCH, MAIN_WITH_LEAD_FIRST_IN_SERIES, MAIN_WITH_LEAD_MIDDLE_IN_SERIES, MAIN_WITH_LEAD_LAST_IN_SERIES, TRAIL
	}

	/**
	 * This client property on a parent panel resolves to the Inspector (if any)
	 * that is associated with it.
	 */
	public static final String PROPERTY_INSPECTOR = Inspector.class.getName()
			+ "#inspector";

	/**
	 * This client property on JPanels resolves a Boolean indicating whether
	 * this panel is a custom panel we created just to wrap other inspector
	 * elements. If this is true then our custom panel should have already taken
	 * insets into account inside the wrapped panel, and all future calls to
	 * getInsets(..) on the panel itself can return an empty Insets.
	 */
	static final String PROPERTY_WRAPPED = Inspector.class.getName()
			+ "#wrapped";

	/**
	 * This client property on JComponents resolves to negative (or zeroed)
	 * insets used to help align text.
	 */
	private static final String PROPERTY_NEGATIVE_INSETS = Inspector.class
			.getName() + "#negativeInsets";

	JPanel panel;
	InspectorLayoutManager layout;
	boolean isConstantVerticalSize = false;
	boolean isConstantHorizontalAlignment = false;

	/**
	 * Create a new Inspector.
	 */
	public Inspector() {
		this(new JPanel());
	}

	/**
	 * Create a new Inspector that populates a specific panel.
	 * 
	 * @param panel
	 *            the panel to populate.
	 */
	public Inspector(JPanel panel) {
		Objects.requireNonNull(panel);
		this.panel = panel;
		panel.putClientProperty(PROPERTY_INSPECTOR, this);
		layout = new InspectorLayoutManager(this);
		panel.setLayout(layout);
		clear();
	}

	/**
	 * Return the panel this Inspector populates.
	 */
	public JPanel getPanel() {
		return panel;
	}

	/**
	 * Add a new InspectorRow.
	 */
	public InspectorRowPanel addRow(InspectorRow row) {
		InspectorRowPanel p = new InspectorRowPanel(row);
		panel.add(p);
		return p;
	}

	/**
	 * Appends a new row containing only 1 object to this inspector.
	 * 
	 * @param component
	 *            the component to add.
	 * @param stretchToFill
	 *            whether to stretch this component to fill the space
	 *            horizontally or not.
	 */
	public InspectorRowPanel addRow(JComponent component, boolean stretchToFill) {
		Position pos = stretchToFill ? Position.MAIN_ONLY_STRETCH_TO_FILL
				: Position.MAIN_ONLY_NO_STRETCH;
		prepare(pos, component);
		return addRow(new InspectorRow(null, component, stretchToFill, 0));
	}

	/**
	 * Appends a new row containing 2 objects to this inspector.
	 * 
	 * The identifier is right-aligned, and the control is left-aligned.
	 * 
	 * @param identifier
	 *            the control on the left. This should usually contain text. A
	 *            <code>JLabel</code> or a <code>JCheckBox</code> is
	 *            recommended.
	 * @param control
	 *            any more complex control on the right.
	 * @param stretchControlToFill
	 *            whether this control should stretch to fit the remaining
	 *            width.
	 */
	public InspectorRowPanel addRow(JComponent identifier, JComponent control,
			boolean stretchControlToFill) {
		prepare(Position.LEAD, identifier);
		Position pos = stretchControlToFill ? Position.MAIN_ONLY_STRETCH_TO_FILL
				: Position.MAIN_ONLY_NO_STRETCH;
		prepare(pos, control);
		return addRow(new InspectorRow(identifier, control,
				stretchControlToFill, 0));
	}

	/**
	 * Append a row containing these elements to this inspector.
	 * 
	 * @param identifier
	 *            the control on the left. This should usually contain text. A
	 *            <code>JLabel</code> or a <code>JCheckBox</code> is
	 *            recommended.
	 * @param controls
	 *            a series of controls to group together from left to right. The
	 *            cluster of components will be anchored on the left.
	 */
	public InspectorRowPanel addRow(JComponent identifier,
			JComponent... controls) {
		if (controls.length == 1) {
			return addRow(identifier, controls[0], false);
		}
		prepare(Position.LEAD, identifier);
		JPanel controlPanel = new JPanel(new GridBagLayout());
		controlPanel.setOpaque(false);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.BASELINE;
		for (int a = 0; a < controls.length; a++) {
			Position pos;

			if (a == 0 && a == controls.length - 1) {
				pos = Position.MAIN_WITH_LEAD_NO_STRETCH;
			} else if (a == 0) {
				pos = Position.MAIN_WITH_LEAD_FIRST_IN_SERIES;
			} else if (a == controls.length - 1) {
				pos = Position.MAIN_WITH_LEAD_LAST_IN_SERIES;
			} else {
				pos = Position.MAIN_WITH_LEAD_MIDDLE_IN_SERIES;
			}
			prepare(pos, controls[a]);

			c.insets = getInsets(pos, controls[a]);
			controlPanel.add(controls[a], c);
			c.gridx++;
		}
		controlPanel.putClientProperty(PROPERTY_WRAPPED, Boolean.TRUE);
		return addRow(new InspectorRow(identifier, controlPanel, false, 0));
	}

	/**
	 * Appends a new row containing 3 objects to this inspector.
	 * 
	 * The identifier is right-aligned. The leftControl is right-aligned, and
	 * the rightControl is right-aligned against the far right margin of the
	 * inspector.
	 * 
	 * @param identifier
	 *            the control on the left. This should usually contain text. A
	 *            <code>JLabel</code> or a <code>JCheckBox</code> is
	 *            recommended.
	 * @param leftControl
	 *            any other control.
	 * @param stretchToFill
	 *            whether the <code>leftControl</code> should stretch to fit the
	 *            remaining width.
	 * @param rightControl
	 *            the element to add on the right.
	 */
	public InspectorRowPanel addRow(JComponent identifier,
			JComponent leftControl, boolean stretchToFill,
			JComponent rightControl) {
		prepare(Position.LEAD, identifier);
		Position pos = stretchToFill ? Position.MAIN_WITH_LEAD_STRETCH_TO_FILL
				: Position.MAIN_WITH_LEAD_NO_STRETCH;
		prepare(pos, leftControl);
		prepare(Position.TRAIL, rightControl);

		JPanel controlPanel = new JPanel(new GridBagLayout());
		controlPanel.setOpaque(false);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.BASELINE_LEADING;
		c.fill = stretchToFill ? GridBagConstraints.BOTH
				: GridBagConstraints.NONE;
		c.insets = getInsets(pos, leftControl);
		controlPanel.add(leftControl, c);
		c.gridx++;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.BASELINE_TRAILING;
		c.insets = getInsets(Position.TRAIL, rightControl);
		controlPanel.add(rightControl, c);
		controlPanel.putClientProperty(PROPERTY_WRAPPED, Boolean.TRUE);
		return addRow(new InspectorRow(identifier, controlPanel, false, 0));
	}

	/**
	 * Appends a new separator to this inspector.
	 * 
	 */
	public InspectorRowPanel addSeparator() {
		return addRow(new InspectorRow(null, new JSeparator(), true, 0));
	}

	/**
	 * Removes all elements from this inspector, usually so elements can be
	 * re-added.
	 */
	public void clear() {
		panel.removeAll();
	}

	/**
	 * Return the insets a given component should use. This method may be called
	 * every time the inspector panel is revalidated/resized.
	 * 
	 * @param position
	 *            the position of this JComponent.
	 * @param c
	 *            the component to identify the insets for.
	 * @return
	 */
	public Insets getInsets(Position position, JComponent c) {
		Boolean wrapped = (Boolean) c.getClientProperty(PROPERTY_WRAPPED);
		if (wrapped != null && wrapped.booleanValue())
			return new Insets(0, 0, 0, 0);

		Insets i = new Insets(3, 3, 3, 3);
		if (position == Position.LEAD) {
			i.left = 5;
			i.right = 6;
		}
		if (position == Position.TRAIL
				|| position == Position.MAIN_ONLY_NO_STRETCH
				|| position == Position.MAIN_ONLY_STRETCH_TO_FILL
				|| position == Position.MAIN_WITH_LEAD_NO_STRETCH
				|| position == Position.MAIN_WITH_LEAD_STRETCH_TO_FILL
				|| position == Position.MAIN_WITH_LEAD_LAST_IN_SERIES) {
			i.right = 5;
		}

		Insets negativeInsets = (Insets) c
				.getClientProperty(PROPERTY_NEGATIVE_INSETS);
		if (negativeInsets != null) {
			i.left += negativeInsets.left;
			i.right += negativeInsets.right;
			i.top += negativeInsets.top;
			i.bottom += negativeInsets.bottom;
		}

		return i;
	}

	/**
	 * Process new components. This may change opacity, borders, or other
	 * properties. This method should only be called once per component, and it
	 * should be called before {@link #getInsets(Position, JComponent)}.
	 * 
	 * @param position
	 *            the position of this JComponent.
	 * @param component
	 *            the component to prepare for installation.
	 */
	protected void prepare(Position position, JComponent component) {
		if (component instanceof JSlider || component instanceof JRadioButton
				|| component instanceof JCheckBox)
			component.setOpaque(false);

		if (component instanceof JCheckBox || component instanceof JLabel
				|| component instanceof JRadioButton
				|| component instanceof JSlider) {
			component.setBorder(null);
		}

		if (component instanceof JCheckBox || component instanceof JRadioButton) {
			AbstractButton b = (AbstractButton) component;
			prepareButton(b);
		}
	}

	protected Insets prepareButton(AbstractButton b) {
		if (b.isFocusPainted()) {
			// if painting the focus makes a button larger: then we need
			// to acknowledge that so the getInsets method still
			// right-aligns our labels and checkboxes correctly.
			Dimension d1 = b.getPreferredSize();
			b.setFocusPainted(false);
			Dimension d2 = b.getPreferredSize();
			b.setFocusPainted(true);
			Insets negativeInsets = new Insets(0, 0, d2.width - d1.width,
					d2.height - d1.height);
			b.putClientProperty(PROPERTY_NEGATIVE_INSETS, negativeInsets);
			return negativeInsets;
		}
		return null;
	}

	/**
	 * If true then the columns should occupy a fixed width regardless of which
	 * rows are visible.
	 * <p>
	 * If false then the columns may change width depending on which rows are
	 * visible.
	 * <p>
	 * (This assumes the InspectorRows/InspectorRowPanels in this Inspector are
	 * constant but their visibility may be toggled over time. This property may
	 * not achieve the desired affect if you're frequently adding new rows.)
	 */
	public boolean isConstantHorizontalAlignment() {
		return isConstantHorizontalAlignment;
	}

	public void setConstantHorizontalAlignment(boolean b) {
		if (isConstantHorizontalAlignment == b)
			return;
		isConstantHorizontalAlignment = b;
		panel.invalidate();
	}

	/**
	 * Return the InspectorRowPanels in this Inspector.
	 */
	public InspectorRowPanel[] getRows() {
		List<InspectorRowPanel> rows = new ArrayList<>(
				panel.getComponentCount());
		for (Component child : panel.getComponents()) {
			if (child instanceof InspectorRowPanel)
				rows.add((InspectorRowPanel) child);
		}
		return rows.toArray(new InspectorRowPanel[rows.size()]);
	}

	/**
	 * If true then the height of this inspector should stay constant regardless
	 * of which rows are visible.
	 * <p>
	 * If false then the height of this inspector may change when rows are
	 * hidden.
	 * <p>
	 * (This assumes the InspectorRows/InspectorRowPanels in this Inspector are
	 * constant but their visibility may be toggled over time. This property may
	 * not achieve the desired affect if you're frequently adding new rows.)
	 */
	public boolean isConstantVerticalSize() {
		return isConstantVerticalSize;
	}

	public void setConstantVerticalSize(boolean b) {
		if (b == isConstantVerticalSize)
			return;
		isConstantVerticalSize = b;
		panel.invalidate();
	}
}