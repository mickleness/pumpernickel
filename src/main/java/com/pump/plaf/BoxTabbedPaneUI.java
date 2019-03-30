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
import java.awt.Component.BaselineResizeBehavior;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton.ToggleButtonModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.TabbedPaneUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicButtonUI;

import com.pump.awt.DescendantListener;
import com.pump.awt.SplayedLayout;
import com.pump.icon.button.MinimalDuoToneCloseIcon;
import com.pump.swing.PartialLineBorder;

/**
 * This is modeled after Safari's tab model. There is one row of tabs (and other
 * possible controls) that is always stretch to fill the available width (or
 * height for vertical tabs).
 * 
 * Features include:
 * <ul>
 * <li>The option to hide the tab row when only one tab is present (the content
 * stays visible, but the tab is hidden).
 * <li>The option to automatically include a close button on all tabs.
 * <li>The option to add custom controls before/after the tabs.
 * </ul>
 * 
 * TODO: it would be nice to add drag-and-drop reordering
 * 
 * <p>
 * This UI does not support the tab layout policy of the JTabbedPane; tabs are
 * only presented in a single continuous scrollable row. So this will only
 * support one or zero runs of tabs.<br>
 * <h3>Context / Design</h3>
 * <p>
 * I would suggest this UI is appropriate for tabbed document interfaces (TDIs),
 * which are a specific implementation of multiple document interfaces (MDIs).
 * (This is all in contrast to single document interfaces (SDIs).)
 * <p>
 * There is a lot of related reading on this online and in well-researched books
 * like <em>About Face</em>. To sum up what I've read so far:
 * <ul>
 * <li>MDIs were most popular in the 90s and early 2000s. This is partly because
 * of resource limitations at the time -- there was no alternative. Some UX
 * designers regard them with a stigma and will suggest SDIs are the better way
 * to go.
 * <li>Still TDIs don't appear to be going away anytime soon. Browsers are the
 * most obvious example. Microsoft Excel is another. Apple, who is notorious for
 * UX scrutiny, recently added tabs to Finder windows. But my favorite example
 * is Notepad vs Notepad++. Notepad is a fine tool, and maybe it's the go-to
 * choice for many users, but Notepad++ (or a similar TDI tool like Atom) is
 * where power-users gravitate to.
 * </ul>
 * <p>
 * If you're interested in displaying a fixed set of tabs (such as in a complex
 * properties or preferences dialog): you should try to make sure the tabs are
 * always visible. Depending on the number of tabs you want to display: this UI
 * may not be a good fit in that case. Horizontal scrolling is relatively subtle
 * (even if you include an indicator like "+3" to indicate 3 more tabs are
 * out-of-sight, some users won't register that).
 * <p>
 * <h3>Implementation</h3>
 * <p>
 * This section describes how parts of this class are implemented, which may be
 * useful if you want to customize either a {@link Style} object or extend the
 * {@link DefaultTab} class.
 * <p>
 * (This section is written as if using the default tab placement; the same
 * components are arranged in subtly different ways for other tab placements.)
 * <p>
 * Each BoxTabbedPaneUI has a <code>controlRow</code> panel that stretches the
 * width of the <code>JTabbedPane</code>.
 * <p>
 * If you have defined leading components (see
 * {@link #PROPERTY_LEADING_COMPONENTS}), then they are anchored to the left
 * side of the <code>controlRow</code>. If you have defined trailing components
 * (see {@link #PROPERTY_TRAILING_COMPONENTS}), then they are anchored on the
 * right side of the <code>controlRow</code>. All of these components are given
 * their preferred size, so their preferred size should be modest. Note it is
 * your responsibility to format these controls. (For example, you may want your
 * components to match the border/fill of your BoxTabbedPaneUI.)
 * <p>
 * The <code>tabContainer</code> panel is given the remaining width. This width
 * is divided up into a series of <code>TabContainer</code> panels with one
 * panel per tab. The <code>TabContainer</code> class is actually a
 * <code>AbstractButton</code>: if you click it you are selecting that tab. You
 * can also transfer keyboard focus to it and press the spacebar.
 * <p>
 * The <code>TabContainer</code> is actually blank, and it contains the
 * component used to render/control the actual tab. By default this component
 * will be the <code>DefaultTab</code> object, but if you have defined custom
 * components using {@link JTabbedPane#setTabComponentAt(int, Component)} then
 * that component is used.
 * <p>
 * The <code>DefaultTab</code> contains a close button that is visible when
 * {@link #PROPERTY_CLOSEABLE_TABS} is enabled. (That also means: this property
 * has no automatic effect if you have supplied your own custom tab components.)
 * <p>
 * <img src=
 * "data:image/png;base64,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"
 * alt="diagram of BoxTabbedPaneUI components">
 */
public class BoxTabbedPaneUI extends TabbedPaneUI {

	public interface Style {
		public void formatControlRow(JTabbedPane tabs, JPanel tabsContainer);

		/**
		 * 
		 * @param tabs
		 * @param tabContainer
		 * @param tabIndex
		 *            the tab index being formatted, or -1 if this button is
		 *            part of the control row but does not affect a tab.
		 */
		public void formatControlRowButton(JTabbedPane tabs,
				AbstractButton tabContainer, int tabIndex);

		public void formatCloseButton(JTabbedPane tabs, JButton closeButton);

		public void formatTabContent(JTabbedPane tabs, JComponent c);
	}

	public static DefaultStyle STYLE_DEFAULT = new DefaultStyle();

	public static Style getStyle(JTabbedPane tabs) {
		Style style = (Style) tabs.getClientProperty(PROPERTY_STYLE);
		if (style == null)
			style = STYLE_DEFAULT;
		return style;
	}

	public static class DefaultStyle implements Style {

		public class TabButtonUI extends BasicButtonUI {

			@Override
			public void paint(Graphics g0, JComponent c) {
				Graphics2D g = (Graphics2D) g0.create();
				boolean isSelected = ((AbstractButton) c).isSelected();
				g.setPaint(createContentGradient(c, isSelected));
				g.fillRect(0, 0, c.getWidth(), c.getHeight());
				if (c.isFocusOwner()) {
					int x = 0;
					int y = 0;
					int w = c.getWidth() - 1;
					int h = c.getHeight() - 1;
					Border b = c.getBorder();
					if (b != null) {
						Insets i = b.getBorderInsets(c);
						x += i.left;
						y += i.top;
						w -= i.left + i.right;
						h -= i.top + i.bottom;
					}
					Rectangle r = new Rectangle(x, y, w, h);
					PlafPaintUtils.paintFocus(g, r, 1,
							PlafPaintUtils.getFocusRingColor(), true);
				}
				g.dispose();
				super.paint(g0, c);
			}

		}

		protected Color borderNormalLight = new Color(0xa1a0a1);
		protected Color borderNormalDark = new Color(0x9e9c9e);
		protected Color borderOuterSelected = new Color(0xbab9ba);
		protected Color borderOuterUnselected = new Color(0xa1a0a1);
		protected Color contentOuterNormal = new Color(0xbdbbbd);
		protected Color contentInnerNormal = new Color(0xb5b3b5);
		protected Color contentOuterSelected = new Color(0xd9d7d9);
		protected Color contentInnerSelected = new Color(0xd1cfd1);
		private boolean outerBorder, contentBorder;

		protected GradientPaint createContentGradient(JComponent c,
				boolean isSelected) {
			JTabbedPane tabs = getTabbedPaneParent(c);
			int placement = tabs == null ? SwingConstants.TOP : tabs
					.getTabPlacement();
			Color outer = isSelected ? contentOuterSelected
					: contentOuterNormal;
			Color inner = isSelected ? contentInnerSelected
					: contentInnerNormal;
			if (placement == SwingConstants.LEFT) {
				return (new GradientPaint(0, 0, outer, c.getWidth(), 0, inner));
			} else if (placement == SwingConstants.RIGHT) {
				return (new GradientPaint(0, 0, inner, c.getWidth(), 0, outer));
			} else if (placement == SwingConstants.BOTTOM) {
				return (new GradientPaint(0, 0, inner, 0, c.getHeight(), outer));
			} else {
				return (new GradientPaint(0, 0, outer, 0, c.getHeight(), inner));
			}
		}

		private JTabbedPane getTabbedPaneParent(Container c) {
			while (c != null) {
				if (c instanceof JTabbedPane)
					return (JTabbedPane) c;
				c = c.getParent();
			}
			return null;
		}

		public DefaultStyle() {
			this(true, true);
		}

		public DefaultStyle(boolean outerBorder, boolean contentBorder) {
			this.outerBorder = outerBorder;
			this.contentBorder = contentBorder;
		}

		private GradientPaint createBorderGradient(int tabPlacement) {
			if (tabPlacement == SwingConstants.BOTTOM) {
				return new GradientPaint(0, 0, borderNormalDark, 0, 25,
						borderNormalLight);
			} else if (tabPlacement == SwingConstants.LEFT) {
				return new GradientPaint(0, 0, borderNormalLight, 25, 0,
						borderNormalDark);
			} else if (tabPlacement == SwingConstants.RIGHT) {
				return new GradientPaint(0, 0, borderNormalDark, 25, 0,
						borderNormalLight);
			} else {
				return new GradientPaint(0, 0, borderNormalLight, 0, 25,
						borderNormalDark);
			}
		}

		@Override
		public void formatControlRow(JTabbedPane tabs, JPanel tabsContainer) {

			Border border;
			Paint paint = createBorderGradient(tabs.getTabPlacement());
			if (tabs.getTabPlacement() == SwingConstants.BOTTOM) {
				border = new PartialLineBorder(paint, true, true,
						tabs.getTabCount() == 0, true);
			} else if (tabs.getTabPlacement() == SwingConstants.LEFT) {
				border = new PartialLineBorder(paint, true,
						tabs.getTabCount() == 0, true, true);
			} else if (tabs.getTabPlacement() == SwingConstants.RIGHT) {
				border = new PartialLineBorder(paint, true, true, true,
						tabs.getTabCount() == 0);
			} else {
				border = new PartialLineBorder(paint, tabs.getTabCount() == 0,
						true, true, true);
			}

			tabsContainer.setUI(new GradientPanelUI(createContentGradient(tabs,
					false)));
			tabsContainer.setBorder(border);
		}

		@Override
		public void formatControlRowButton(JTabbedPane tabs,
				AbstractButton tabContainer, int tabIndex) {
			if (tabIndex >= 0) {
				Border border;
				int p = tabs.getTabPlacement();
				if (p == SwingConstants.LEFT) {
					border = new PartialLineBorder(createBorderGradient(p),
							tabIndex != 0, false, false, false);
					if (isOuterBorder()) {
						Paint paint = tabIndex == tabs.getSelectedIndex() ? borderOuterSelected
								: borderOuterUnselected;
						Border inner = new PartialLineBorder(paint, false,
								true, false, false);
						border = new CompoundBorder(border, inner);
					}
				} else if (p == SwingConstants.RIGHT) {
					border = new PartialLineBorder(createBorderGradient(p),
							tabIndex != 0, false, false, false);
					if (isOuterBorder()) {
						Paint paint = tabIndex == tabs.getSelectedIndex() ? borderOuterSelected
								: borderOuterUnselected;
						Border inner = new PartialLineBorder(paint, false,
								false, false, true);
						border = new CompoundBorder(border, inner);
					}
				} else {
					border = new PartialLineBorder(createBorderGradient(p),
							false, tabIndex != 0, false, false);
					if (isOuterBorder()) {
						Paint paint = tabIndex == tabs.getSelectedIndex() ? borderOuterSelected
								: borderOuterUnselected;
						Border inner = new PartialLineBorder(paint,
								p == SwingConstants.TOP, false,
								p == SwingConstants.BOTTOM, false);
						border = new CompoundBorder(border, inner);
					}
				}
				tabContainer.setBorder(border);
			}

			if (!(tabContainer.getUI() instanceof TabButtonUI)) {
				tabContainer.setUI(new TabButtonUI());
				tabContainer.putClientProperty(QButtonUI.HORIZONTAL_POSITION,
						QButtonUI.MIDDLE);
				tabContainer.putClientProperty(QButtonUI.VERTICAL_POSITION,
						QButtonUI.MIDDLE);
				tabContainer.putClientProperty(QButtonUI.STROKE_PAINTED,
						Boolean.FALSE);
			}
		}

		public boolean isOuterBorder() {
			return outerBorder;
		}

		@Override
		public void formatCloseButton(JTabbedPane tabs, JButton closeButton) {
			if (!(closeButton.getIcon() instanceof MinimalDuoToneCloseIcon))
				closeButton.setIcon(new MinimalDuoToneCloseIcon(closeButton));
			closeButton.setMargin(new Insets(0, 0, 0, 0));
			closeButton.setBorder(new EmptyBorder(0, 0, 0, 0));
		}

		@Override
		public void formatTabContent(JTabbedPane tabs, JComponent c) {
			if (contentBorder) {
				Border border;
				if (tabs.getTabPlacement() == SwingConstants.LEFT) {
					border = new PartialLineBorder(borderNormalDark, true,
							false, true, true);
				} else if (tabs.getTabPlacement() == SwingConstants.BOTTOM) {
					border = new PartialLineBorder(borderNormalDark, true,
							true, false, true);
				} else if (tabs.getTabPlacement() == SwingConstants.RIGHT) {
					border = new PartialLineBorder(borderNormalDark, true,
							true, true, false);
				} else {
					border = new PartialLineBorder(borderNormalDark, false,
							true, true, true);
				}
				c.setBorder(border);
			}
		}
	}

	/**
	 * An optional client property for a <code>java.util.List</code> of
	 * JComponents to always place on the trailing (right) side of the tabs.
	 */
	public static final String PROPERTY_TRAILING_COMPONENTS = BoxTabbedPaneUI.class
			.getName() + "#trailingComponents";

	/**
	 * An optional client property for a <code>java.util.List</code> of
	 * JComponents to always place on the leading (left) side of the tabs.
	 */
	public static final String PROPERTY_LEADING_COMPONENTS = BoxTabbedPaneUI.class
			.getName() + "#leadingComponents";

	/**
	 * This client property on tabs maps to the Integer index of that tab
	 * relative to the JTabbedPane.
	 */
	private static final String PROPERTY_TAB_INDEX = BoxTabbedPaneUI.class
			.getName() + "#tabIndex";

	/**
	 * This client property on JTabbedPane resolves to an internal Data object.
	 */
	private static final String PROPERTY_DATA = BoxTabbedPaneUI.class.getName()
			+ "#data";

	/**
	 * This optional property on JTabbedPane resolves to a Boolean used to
	 * indicate whether tabs should be closeable by default. Note if you have
	 * defined a custom tab component using
	 * {@link JTabbedPane#setTabComponentAt(int, Component)} then that component
	 * is used and this property is not automatically consulted. This value is
	 * assumed to be false by default.
	 */
	public static final String PROPERTY_CLOSEABLE_TABS = BoxTabbedPaneUI.class
			.getName() + "#closeableTabs";

	/**
	 * This optional property on JTabbedPane resolves to a Style object used to
	 * help format colors, borders, inner component UIs.
	 */
	public static final String PROPERTY_STYLE = BoxTabbedPaneUI.class.getName()
			+ "#style";

	/**
	 * This optional property on JTabbedPane resolves to a Boolean used to
	 * indicate whether the tab UI controls should be hidden if there is only
	 * one tab. This value is assumed to be false by default.
	 */
	public static final String PROPERTY_HIDE_SINGLE_TAB = BoxTabbedPaneUI.class
			.getName() + "#hideSingleTab";

	/**
	 * This caches a TabContainer on each Tab object. If we recreate the
	 * TabContainer with every refresh: the icons/animations may flicker a
	 * little.
	 */
	private static final String PROPERTY_TAB_CONTAINER = BoxTabbedPaneUI.class
			.getName() + "#tabContainer";

	private static class UIResourcePanel extends JPanel implements UIResource {
		private static final long serialVersionUID = 1L;

		UIResourcePanel(LayoutManager layoutManager) {
			super(layoutManager);
		}
	}

	private static class BoxTabbedPaneUILayoutManager implements LayoutManager {

		@Override
		public void addLayoutComponent(String name, Component comp) {
		}

		@Override
		public void removeLayoutComponent(Component comp) {
		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			return getLayoutSize(parent, true);
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			return getLayoutSize(parent, false);
		}

		private Dimension getLayoutSize(Container parent, boolean preferred) {
			int tabPlacement = ((JTabbedPane) parent).getTabPlacement();
			boolean verticalPlacement = tabPlacement == JTabbedPane.TOP
					|| tabPlacement == JTabbedPane.BOTTOM;
			Dimension additional = new Dimension(0, 0);
			Dimension max = new Dimension(0, 0);
			for (int a = 0; a < parent.getComponentCount(); a++) {
				Component c = parent.getComponent(a);
				Dimension d = preferred ? c.getPreferredSize() : c
						.getMinimumSize();
				if (c instanceof UIResourcePanel) {
					if (verticalPlacement) {
						additional.height += d.height;
						additional.width = Math.max(additional.width, d.width);
					} else {
						additional.width += d.width;
						additional.height = Math.max(additional.height,
								d.height);
					}
				} else {
					max.width = Math.max(max.width, d.width);
					max.height = Math.max(max.height, d.height);
				}
			}
			if (verticalPlacement) {
				return new Dimension(Math.max(additional.width, max.width),
						additional.height + max.height);
			}
			return new Dimension(additional.width + max.width, Math.max(
					additional.height, max.height));
		}

		@Override
		public void layoutContainer(Container parent) {
			int y = 0;
			int x = 0;
			int width = parent.getWidth();
			int height = parent.getHeight();

			int tabPlacement = ((JTabbedPane) parent).getTabPlacement();
			for (int a = 0; a < parent.getComponentCount(); a++) {
				Component c = parent.getComponent(a);
				if (c instanceof UIResourcePanel && c.isVisible()) {
					Dimension d = c.getPreferredSize();
					if (tabPlacement == JTabbedPane.TOP) {
						c.setBounds(x, y, width, d.height);
						y += d.height;
						height -= d.height;
					} else if (tabPlacement == JTabbedPane.BOTTOM) {
						c.setBounds(x, height - d.height, width, d.height);
						height -= d.height;
					} else if (tabPlacement == JTabbedPane.LEFT) {
						c.setBounds(x, y, d.width, height);
						x += d.width;
						width -= d.width;
					} else if (tabPlacement == JTabbedPane.RIGHT) {
						c.setBounds(width - d.width, y, d.width, height);
						width -= d.width;
					}
				}
			}
			for (int a = 0; a < parent.getComponentCount(); a++) {
				Component c = parent.getComponent(a);
				if (!(c instanceof UIResourcePanel)) {
					c.setBounds(x, y, width, height);
				}
			}
		}

	}

	private static final String[] REFRESH_PROPERTIES = new String[] {
			"mnemonicAt", "displayedMnemonicIndexAt", "indexForTitle",
			"tabLayoutPolicy", "opaque", "background", "indexForTabComponent",
			"indexForNullComponent", "font", PROPERTY_HIDE_SINGLE_TAB,
			PROPERTY_CLOSEABLE_TABS };

	/**
	 * This is a cluster of data related to a specific JTabbedPane.
	 * <p>
	 * In theory a TabbedPaneUI can be applied to multiple JTabbedPanes. If that
	 * happens each pane gets its own Data object to control it.
	 */
	class Data {

		JPanel controlRow = new UIResourcePanel(new GridBagLayout());
		JPanel leadingComponents = new JPanel(new GridBagLayout());
		JPanel tabsContainer = new JPanel();
		JPanel trailingComponents = new JPanel(new GridBagLayout());
		JTabbedPane tabs;

		ChangeListener refreshChangeListener = new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				refreshTabStates();
			}
		};

		PropertyChangeListener refreshPropertyListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				refreshTabStates();
			}

		};

		PropertyChangeListener refreshExtraComponentsListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				refreshExtraComponents();
			}

		};

		PropertyChangeListener refreshStyleListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				refreshStyle();
				refreshTabStates();
			}

		};

		PropertyChangeListener refreshContentBorderListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				refreshContentBorder();
			}

		};

		PropertyChangeListener tabPlacementPropertyListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				relayoutControlRow();
				refreshExtraComponents();
				refreshTabStates();
				refreshStyle();
				refreshContentBorder();
			}

		};

		ContainerListener containerListener = new ContainerAdapter() {

			@Override
			public void componentAdded(ContainerEvent e) {
				refreshTabStates();
				refreshContentBorder();
				refreshStyle();
			}

			@Override
			public void componentRemoved(ContainerEvent e) {
				refreshTabStates();
				refreshContentBorder();
				refreshStyle();
			}

		};

		public Data(JTabbedPane tabs) {
			this.tabs = tabs;
			controlRow.setOpaque(false);
			leadingComponents.setOpaque(false);
			tabsContainer.setOpaque(false);
			trailingComponents.setOpaque(false);
		}

		public void install() {
			tabs.setLayout(new BoxTabbedPaneUILayoutManager());
			removeNonUIResources(tabs);
			tabs.add(controlRow);

			relayoutControlRow();

			for (String property : new String[] { PROPERTY_STYLE }) {
				tabs.addPropertyChangeListener(property, refreshStyleListener);
			}

			for (String property : new String[] { PROPERTY_TRAILING_COMPONENTS,
					PROPERTY_LEADING_COMPONENTS }) {
				tabs.addPropertyChangeListener(property,
						refreshExtraComponentsListener);
			}

			tabs.addPropertyChangeListener("tabPlacement",
					tabPlacementPropertyListener);

			for (String property : REFRESH_PROPERTIES) {
				tabs.addPropertyChangeListener(property,
						refreshPropertyListener);
			}
			tabs.addContainerListener(containerListener);
			tabs.getModel().addChangeListener(refreshChangeListener);

			refreshExtraComponents();
			refreshTabStates();
			refreshContentBorder();
			refreshStyle();
		}

		private void refreshContentBorder() {
			for (int a = 0; a < tabs.getComponentCount(); a++) {
				Component c = tabs.getComponent(a);
				if (!(c instanceof UIResource) && (c instanceof JComponent)) {
					getStyle(tabs).formatTabContent(tabs, (JComponent) c);
				}
			}
		}

		private void refreshStyle() {
			getStyle(tabs).formatControlRow(tabs, tabsContainer);
		}

		private void relayoutControlRow() {
			controlRow.removeAll();
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 0;
			c.weighty = 0;
			c.fill = GridBagConstraints.BOTH;
			if (tabs.getTabPlacement() == SwingConstants.LEFT) {
				controlRow.add(trailingComponents, c);
				c.gridy++;
				c.weighty = 1;
				controlRow.add(tabsContainer, c);
				c.gridy++;
				c.weighty = 0;
				controlRow.add(leadingComponents, c);
			} else if (tabs.getTabPlacement() == SwingConstants.RIGHT) {
				controlRow.add(leadingComponents, c);
				c.gridy++;
				c.weighty = 1;
				controlRow.add(tabsContainer, c);
				c.gridy++;
				c.weighty = 0;
				controlRow.add(trailingComponents, c);
			} else {
				controlRow.add(leadingComponents, c);
				c.gridx++;
				c.weightx = 1;
				controlRow.add(tabsContainer, c);
				c.gridx++;
				c.weightx = 0;
				controlRow.add(trailingComponents, c);
			}

			controlRow.revalidate();
		}

		int lastTabPlacement = -1;

		@SuppressWarnings("unchecked")
		private void refreshExtraComponents() {
			List<JComponent> newLeadingComponents = (List<JComponent>) tabs
					.getClientProperty(PROPERTY_LEADING_COMPONENTS);
			List<JComponent> newTrailingComponents = (List<JComponent>) tabs
					.getClientProperty(PROPERTY_TRAILING_COMPONENTS);
			int tabPlacement = tabs.getTabPlacement();
			boolean forceReinstall = tabPlacement != lastTabPlacement;
			lastTabPlacement = tabPlacement;
			installExtraComponents(leadingComponents, newLeadingComponents,
					forceReinstall);
			installExtraComponents(trailingComponents, newTrailingComponents,
					forceReinstall);
		}

		ComponentListener extraComponentListener = new ComponentAdapter() {

			@Override
			public void componentShown(ComponentEvent e) {
				refreshExtraContainerVisibility();
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				refreshExtraContainerVisibility();
			}

		};

		private void installExtraComponents(Container container,
				List<JComponent> components, boolean forceReinstall) {
			if (components == null)
				components = new ArrayList<>();
			Component[] oldComponents = container.getComponents();
			if (!Arrays.asList(oldComponents).equals(components)) {
				forceReinstall = true;
			}

			if (forceReinstall) {
				container.removeAll();
				GridBagConstraints c = new GridBagConstraints();
				c.gridx = 0;
				c.gridy = 100;
				c.weightx = 1;
				c.weighty = 1;
				c.fill = GridBagConstraints.BOTH;
				for (JComponent jc : components) {
					container.add(jc, c);
					if (tabs.getTabPlacement() == SwingConstants.LEFT) {
						c.gridy--;
					} else if (tabs.getTabPlacement() == SwingConstants.RIGHT) {
						c.gridy++;
					} else {
						c.gridx++;
					}
					jc.removeComponentListener(extraComponentListener);
					jc.addComponentListener(extraComponentListener);

					for (Component oldComponent : oldComponents) {
						if (components.contains(oldComponent)) {
							oldComponent
									.removeComponentListener(extraComponentListener);
						}
					}
				}
				container.revalidate();
			}
			refreshExtraContainerVisibility();
		}

		private void refreshExtraContainerVisibility() {
			leadingComponents
					.setVisible(containsVisibleComponent(leadingComponents));
			trailingComponents
					.setVisible(containsVisibleComponent(trailingComponents));
		}

		private boolean containsVisibleComponent(Container container) {
			for (Component c : container.getComponents()) {
				if (c.isVisible())
					return true;
			}
			return false;
		}

		public void uninstall() {
			tabs.remove(controlRow);

			for (String property : new String[] { PROPERTY_STYLE }) {
				tabs.removePropertyChangeListener(property,
						refreshStyleListener);
			}

			for (String property : new String[] { PROPERTY_TRAILING_COMPONENTS,
					PROPERTY_LEADING_COMPONENTS }) {
				tabs.removePropertyChangeListener(property,
						refreshExtraComponentsListener);
			}

			tabs.removePropertyChangeListener("tabPlacement",
					tabPlacementPropertyListener);

			for (String property : REFRESH_PROPERTIES) {
				tabs.removePropertyChangeListener(property,
						refreshPropertyListener);
			}
			tabs.removeContainerListener(containerListener);
			tabs.getModel().removeChangeListener(refreshChangeListener);
		}

		private void removeNonUIResources(Container c) {
			for (Component comp : c.getComponents()) {
				if (!(comp instanceof UIResource)) {
					c.remove(comp);
				}
			}
		}

		protected void refreshTabStates() {
			List<Component> newTabs = new ArrayList<>();
			for (int a = 0; a < tabs.getTabCount(); a++) {
				JComponent tab = (JComponent) tabs.getTabComponentAt(a);
				if (tab == null)
					tab = getDefaultTab(a);

				TabContainer tabContainer = (TabContainer) tab
						.getClientProperty(PROPERTY_TAB_CONTAINER);
				if (tabContainer == null) {
					tabContainer = new TabContainer(tabs, a, tab);
					tab.putClientProperty(PROPERTY_TAB_CONTAINER, tabContainer);
				}

				newTabs.add(tabContainer);
				tab.putClientProperty(PROPERTY_TAB_INDEX, a);
				getStyle(tabs).formatControlRowButton(tabs, tabContainer, a);
			}

			Boolean hideSingleTab = (Boolean) tabs
					.getClientProperty(PROPERTY_HIDE_SINGLE_TAB);
			if (hideSingleTab == null)
				hideSingleTab = Boolean.FALSE;
			controlRow.setVisible(!(tabs.getTabCount() <= 1 && hideSingleTab));

			if (!(tabsContainer.getLayout() instanceof SplayedLayout)) {
				SplayedLayout l = new SplayedLayout(true) {

					@Override
					protected Collection<JComponent> getEmphasizedComponents(
							JComponent container) {
						Collection<JComponent> returnValue = super
								.getEmphasizedComponents(container);

						for (Component c : container.getComponents()) {
							if (c instanceof AbstractButton) {
								AbstractButton ab = (AbstractButton) c;
								if (ab.isSelected())
									returnValue.add(ab);
							}
						}
						return returnValue;
					}

				};
				tabsContainer.setLayout(l);
			}
			int orientation = tabs.getTabPlacement() == SwingConstants.LEFT
					|| tabs.getTabPlacement() == SwingConstants.RIGHT ? SwingConstants.VERTICAL
					: SwingConstants.HORIZONTAL;
			((SplayedLayout) tabsContainer.getLayout()).setOrientation(null,
					orientation);

			setComponents(tabsContainer, newTabs);

			tabsContainer.revalidate();
		}

		/**
		 * This is basically
		 * <code>java.awt.Container.setComponents(Component[])</code>. This is
		 * functionally the same as removing all of a container's children and
		 * then adding them back again, but this method makes individual changes
		 * to minimize the number of container/hierarchy changes that listeners
		 * hear.
		 */
		private void setComponents(Container container,
				List<Component> components) {
			Component[] oldComponents = container.getComponents();
			for (int a = 0; a < oldComponents.length; a++) {
				if (!components.contains(oldComponents[a])) {
					container.remove(oldComponents[a]);
				}
			}
			oldComponents = container.getComponents();
			int oldCtr = 0;
			int newCtr = 0;
			while (newCtr < components.size()) {
				Component oldComponent = oldCtr < oldComponents.length ? oldComponents[oldCtr]
						: null;
				if (oldComponent != components.get(newCtr)) {
					container.add(components.get(newCtr), oldCtr);
				} else {
					oldCtr++;
				}
				newCtr++;
			}
		}

		Map<Integer, DefaultTab> tabMap = new HashMap<>();

		private DefaultTab getDefaultTab(int a) {
			DefaultTab returnValue = tabMap.get(a);
			if (returnValue == null) {
				returnValue = createDefaultTab(tabs, a);
				tabMap.put(a, returnValue);
			} else {
				returnValue.decorateLabel(a);
			}
			return returnValue;
		}

		public int getTabRunCount() {
			return 1;
		}

		public Rectangle getTabBounds(int index) {
			for (int a = 0; a < controlRow.getComponentCount(); a++) {
				Component c = controlRow.getComponent(a);
				if (c instanceof JComponent) {
					JComponent jc = (JComponent) c;
					Integer i = (Integer) jc
							.getClientProperty(PROPERTY_TAB_INDEX);
					if (i != null && i == index)
						return SwingUtilities.convertRectangle(controlRow,
								jc.getBounds(), tabs);
				}
			}
			return null;
		}

		public int getTabForCoordinate(int x, int y) {
			Component c = SwingUtilities.getDeepestComponentAt(tabs, x, y);
			while (c != tabs) {
				if (c instanceof JComponent) {
					JComponent jc = (JComponent) c;
					Integer i = (Integer) jc
							.getClientProperty(PROPERTY_TAB_INDEX);
					if (i != null)
						return i;
				}
				c = c.getParent();
			}
			return -1;
		}
	}

	static class TabContainer extends AbstractButton {
		/**
		 * This converts MouseEvent sources to an AbstractButton.
		 */
		private static class MyBasicButtonListener extends BasicButtonListener {

			AbstractButton button;

			public MyBasicButtonListener(AbstractButton b) {
				super(b);
				button = b;
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				super.mouseMoved(convert(e));
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				super.mouseDragged(convert(e));
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(convert(e));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(convert(e));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(convert(e));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(convert(e));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(convert(e));
			}

			private MouseEvent convert(MouseEvent e) {
				if (e.getSource() == button)
					return e;
				return SwingUtilities.convertMouseEvent(e.getComponent(), e,
						button);
			}
		}

		JTabbedPane tabs;
		int tabIndex;

		public TabContainer(JTabbedPane tabs, int tabIndex, JComponent tab) {
			this.tabs = tabs;
			this.tabIndex = tabIndex;

			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
			c.fill = GridBagConstraints.BOTH;
			add(tab, c);
			setFocusable(true);
			setFocusPainted(true);
			setModel(new ToggleButtonModel());
			refreshSelectedState();
			getModel().addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					refreshSelectedState();
				}
			});

			tabs.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					refreshSelectedState();
				}
			});

			BasicButtonListener buttonListener = new MyBasicButtonListener(this);
			buttonListener.installKeyboardActions(this);
			addFocusListener(buttonListener);
			DescendantListener
					.addMouseListener(this, (MouseListener) buttonListener,
							false, AbstractButton.class);
			DescendantListener.addMouseListener(this,
					(MouseMotionListener) buttonListener, false,
					AbstractButton.class);

			addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					TabContainer.this.tabs
							.setSelectedIndex(TabContainer.this.tabIndex);
				}

			});

			setFocusable(true);
			setRequestFocusEnabled(false);
		}

		private void refreshSelectedState() {
			getModel().setSelected(tabs.getSelectedIndex() == tabIndex);
		}

		private static final long serialVersionUID = 1L;

	}

	public static class DefaultTab extends JPanel {
		private static final long serialVersionUID = 1L;

		protected final JTabbedPane tabs;
		private JLabel label = new JLabel();
		private JButton closeButton = new JButton();
		private int tabIndex = -1;

		private PropertyChangeListener closeableTabsListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				refreshCloseableButton();
			}

		};

		public DefaultTab(JTabbedPane tabs, int tabIndex) {
			setLayout(new GridBagLayout());
			this.tabs = tabs;

			closeButton.setContentAreaFilled(false);
			closeButton.setBorderPainted(false);

			label.setHorizontalAlignment(SwingConstants.CENTER);

			Font font = UIManager.getFont("TabbedPane.smallFont");
			if (font != null)
				label.setFont(font);

			setOpaque(false);
			decorateLabel(tabIndex);

			closeButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					doCloseTab(DefaultTab.this.tabIndex);
				}

			});

			tabs.addPropertyChangeListener(PROPERTY_CLOSEABLE_TABS,
					closeableTabsListener);
			refreshCloseableButton();
		}

		/**
		 * Remove a tab at a given index.
		 * <p>
		 * The default implementation simply calls
		 * <code>tabs.removeTabAt(tabIndex)</code>, but subclasses can override
		 * this as needed. For example, you may need to prompt the user to
		 * confirm discarding unsaved changes.
		 * 
		 * @param tabIndex
		 *            the index of the tab to close.
		 */
		protected void doCloseTab(int tabIndex) {
			tabs.removeTabAt(tabIndex);
		}

		private void refreshCloseableButton() {
			Boolean b = (Boolean) tabs
					.getClientProperty(PROPERTY_CLOSEABLE_TABS);
			if (b == null)
				b = Boolean.FALSE;
			closeButton.setVisible(b);
			int i;
			if (b) {
				Dimension d = closeButton.getPreferredSize();
				i = Math.max(d.width, d.height);
			} else {
				i = 0;
			}
			i += 2;
			label.setBorder(new EmptyBorder(0, i, 0, i));

			getStyle(tabs).formatCloseButton(tabs, closeButton);
		}

		protected JLabel getLabel() {
			return label;
		}

		protected JButton getCloseButton() {
			return closeButton;
		}

		protected GridBagConstraints createLabelConstraints() {
			GridBagConstraints labelConstraints = new GridBagConstraints();
			labelConstraints.gridx = 0;
			labelConstraints.gridy = 0;
			labelConstraints.weightx = 1;
			labelConstraints.weighty = 1;
			labelConstraints.fill = GridBagConstraints.BOTH;
			labelConstraints.insets = new Insets(3, 3, 3, 3);
			return labelConstraints;
		}

		protected GridBagConstraints createCloseButtonConstraints() {
			GridBagConstraints closeButtonConstraints = new GridBagConstraints();
			closeButtonConstraints.gridx = 0;
			closeButtonConstraints.gridy = 0;
			closeButtonConstraints.weightx = 1;
			closeButtonConstraints.weighty = 1;
			closeButtonConstraints.fill = GridBagConstraints.NONE;
			if (tabs.getTabPlacement() == SwingConstants.LEFT) {
				closeButtonConstraints.anchor = GridBagConstraints.SOUTH;
			} else if (tabs.getTabPlacement() == SwingConstants.RIGHT) {
				closeButtonConstraints.anchor = GridBagConstraints.NORTH;
			} else {
				closeButtonConstraints.anchor = GridBagConstraints.WEST;
			}
			if (tabs.getTabPlacement() == SwingConstants.LEFT) {
				closeButtonConstraints.insets = new Insets(0, 0, 3, 0);
			} else if (tabs.getTabPlacement() == SwingConstants.RIGHT) {
				closeButtonConstraints.insets = new Insets(3, 0, 0, 0);
			} else {
				closeButtonConstraints.insets = new Insets(0, 3, 0, 0);
			}
			return closeButtonConstraints;
		}

		public void decorateLabel(int index) {
			tabIndex = index;
			Color background = tabs.getBackgroundAt(index);
			setBackground(background);

			Icon disabledIcon = tabs.getDisabledIconAt(index);
			Icon icon = tabs.getIconAt(index);

			if (!tabs.isEnabledAt(index) || !tabs.isEnabled()) {
				if (disabledIcon != null)
					icon = disabledIcon;
			}
			getLabel().setIcon(icon);

			// TODO: we don't current support mnemonics
			tabs.getDisplayedMnemonicIndexAt(index);
			tabs.getMnemonicAt(index);

			Color foreground = tabs.getForegroundAt(index);
			if (foreground == null)
				foreground = UIManager.getColor("Label.foreground");
			getLabel().setForeground(foreground);

			setToolTipText(tabs.getToolTipTextAt(index));

			String title = tabs.getTitleAt(index);
			if (title == null)
				title = "";
			getLabel().setText(title);

			getStyle(tabs).formatCloseButton(tabs, closeButton);

			addChild(tabs.getTabPlacement());
		}

		/**
		 * Add the label to this DefaultTab; if necessary wrap it inside a
		 * RotatedComponent.
		 * 
		 * @param tabPlacement
		 *            the tab placement (SwingConstants.TOP,
		 *            SwingConstants.LEFT, SwingConstants.BOTTOM,
		 *            SwingConstants.RIGHT)
		 */
		protected void addChild(int tabPlacement) {
			RotatedPanel.Rotation rotation = RotatedPanel.Rotation.NONE;
			if (tabPlacement == SwingConstants.LEFT) {
				rotation = RotatedPanel.Rotation.COUNTER_CLOCKWISE;
			} else if (tabPlacement == SwingConstants.RIGHT) {
				rotation = RotatedPanel.Rotation.CLOCKWISE;
			}

			Component child = getComponentCount() == 0 ? null : getComponent(0);
			if (rotation == RotatedPanel.Rotation.NONE) {
				if (child == getLabel()) {
					return;
				} else {
					removeAll();
					add(getCloseButton(), createCloseButtonConstraints());
					add(getLabel(), createLabelConstraints());
					revalidate();
				}
			} else {
				if (child instanceof RotatedPanel) {
					RotatedPanel rc = (RotatedPanel) child;
					rc.setRotation(rotation);
				} else {
					removeAll();
					add(getCloseButton(), createCloseButtonConstraints());
					add(new RotatedPanel(getLabel(), rotation),
							createLabelConstraints());
					revalidate();
				}
			}
		}

	}

	protected Data getData(JTabbedPane tabs) {
		Data d = (Data) tabs.getClientProperty(PROPERTY_DATA);
		if (d == null) {
			d = new Data(tabs);
			tabs.putClientProperty(PROPERTY_DATA, d);
		}
		return d;
	}

	public DefaultTab createDefaultTab(JTabbedPane tabs, int a) {
		return new DefaultTab(tabs, a);
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		Data data = getData((JTabbedPane) c);
		data.install();
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		Data data = getData((JTabbedPane) c);
		data.uninstall();
	}

	@Override
	public boolean contains(JComponent c, int x, int y) {
		return x >= 0 && x < c.getWidth() && y >= 0 && y < c.getHeight();
	}

	@Override
	public int getBaseline(JComponent c, int width, int height) {
		// TODO
		return -1;
	}

	@Override
	public BaselineResizeBehavior getBaselineResizeBehavior(JComponent c) {
		return BaselineResizeBehavior.OTHER;
	}

	@Override
	public int tabForCoordinate(JTabbedPane pane, int x, int y) {
		return getData(pane).getTabForCoordinate(x, y);
	}

	@Override
	public Rectangle getTabBounds(JTabbedPane pane, int index) {
		return getData(pane).getTabBounds(index);
	}

	@Override
	public int getTabRunCount(JTabbedPane pane) {
		return getData(pane).getTabRunCount();
	}

}