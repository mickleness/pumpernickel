package com.pump.inspector;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.pump.inspector.InspectorLayoutManager.NullLayoutManager;

/**
 * This is a JPanel that contains an InspectorRow.
 * <p>
 * You should use the InspectorRow object to manipulate the contents/layout of
 * this panel. But you can add listeners, toggle the visibility, or manipulate
 * the Border of an InspectorRowPanel.
 */
public class InspectorRowPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private static Collection<String> COMPONENT_PROPERTIES = new HashSet<String>(
			Arrays.asList(InspectorRow.PROPERTY_LEAD_COMPONENT.getName(),
					InspectorRow.PROPERTY_MAIN_COMPONENT.getName()));

	protected final InspectorRow row;

	protected PropertyChangeListener propertyListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (COMPONENT_PROPERTIES.contains(evt.getPropertyName())) {
				refreshChildren();
			} else {
				invalidateInspectorParent();
			}
		}

	};

	public InspectorRowPanel(InspectorRow row) {
		super(new NullLayoutManager());
		Objects.requireNonNull(row);
		this.row = row;
		setOpaque(false);
		row.addPropertyChangeListener(propertyListener);
		refreshChildren();
	}

	/**
	 * Return the InspectorRow this panel represents.
	 */
	public InspectorRow getInspectorRow() {
		return row;
	}

	/**
	 * This method adds/removes child components. The InspectorLayoutManager is
	 * responsible to laying out these children appropriately.
	 */
	protected void refreshChildren() {
		List<JComponent> newChildren = getInspectorRow().getComponents();
		List<Component> oldChildren = Arrays.asList(getComponents());
		if (newChildren.equals(oldChildren))
			return;
		for (Component newChild : newChildren) {
			if (!oldChildren.contains(newChild))
				add(newChild);
		}
		for (Component oldChild : oldChildren) {
			if (!newChildren.contains(oldChild))
				remove(oldChild);
		}
		invalidateInspectorParent();
	}

	private void invalidateInspectorParent() {
		Container parent = getParent();
		if (parent != null)
			parent.invalidate();
	}

	/**
	 * Adding package-level access to the AnimatingInspectorPanel can call this.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	private transient Boolean isShowing;

	/**
	 * This hack (used only by the AnimatingInspectorPanel) lets you define
	 * an override for the {@link #isShowing()} method.
	 * <p>
	 * This isn't pretty, but it works. (If anyone has any better ideas I'd
	 * to hear them.)
	 */
	void setShowing(Boolean b) {
		isShowing = b;
	}

	@Override
	public boolean isShowing() {
		if (isShowing != null)
			return isShowing;
		return super.isShowing();
	}
}
