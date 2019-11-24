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
package com.pump.inspector;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.Objects;

/**
 * This is the LayoutManager used by the Inspector class.
 */
class InspectorLayoutManager implements LayoutManager {

	/**
	 * This is a no-op LayoutManager used for InspectorRowPanels. The
	 * InspectorRowPanels are also managed by the parent InspectorLayoutManager.
	 * So as far as Swing is concerned: the NullLayoutManager is their
	 * LayoutManager. But in reality: their parent container (which uses a
	 * InspectorLayoutManager) is managing their layout.
	 */
	static class NullLayoutManager implements LayoutManager {

		@Override
		public void addLayoutComponent(String name, Component comp) {
		}

		@Override
		public void removeLayoutComponent(Component comp) {
		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			return new Dimension(0, 0);
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			return new Dimension(0, 0);
		}

		@Override
		public void layoutContainer(Container parent) {
		}
	}

	Inspector inspector;

	InspectorLayoutManager(Inspector inspector) {
		Objects.requireNonNull(inspector);
		this.inspector = inspector;
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
	}

	@Override
	public void removeLayoutComponent(Component comp) {
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		if (parent != inspector.getPanel())
			throw new IllegalArgumentException();
		InspectorLayoutPlacement p = new InspectorLayoutPlacement(inspector);
		return p.getPreferredSize();
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		if (parent != inspector.getPanel())
			throw new IllegalArgumentException();
		InspectorLayoutPlacement p = new InspectorLayoutPlacement(inspector);
		return p.getMinimumSize();
	}

	@Override
	public void layoutContainer(Container parent) {
		if (parent != inspector.getPanel())
			throw new IllegalArgumentException();
		InspectorLayoutPlacement p = new InspectorLayoutPlacement(inspector);
		p.install();
	}
}