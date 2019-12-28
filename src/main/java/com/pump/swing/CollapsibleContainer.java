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
package com.pump.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.awt.DescendantListener;
import com.pump.icon.RotatedIcon;
import com.pump.icon.TriangleIcon;
import com.pump.plaf.AnimationManager;
import com.pump.plaf.AnimationManager.Ticket;
import com.pump.plaf.QPanelUI;
import com.pump.plaf.UIEffect;
import com.pump.plaf.button.ButtonState;
import com.pump.plaf.button.GradientButtonUI;
import com.pump.plaf.button.QButtonUI;
import com.pump.plaf.button.QButtonUI.HorizontalPosition;
import com.pump.plaf.button.QButtonUI.PaintFocus;
import com.pump.plaf.button.QButtonUI.VerticalPosition;
import com.pump.util.WeakSet;

/**
 * A vertical series of collapsible, labeled <Code>Sections</code>.
 * <p>
 * Each <code>Section</code> has an optional vertical weight (the default is
 * assumed to be zero). This determines how vertical space is distributed. If a
 * <code>Section</code> has zero weight, then it is given its preferred size
 * (but never any more).
 * <p>
 * Each header is a <code>JButton</code>. By default the user can click each
 * header to make it toggle its collapsed state. This can also be achieved by
 * directly calling: <code>header.putClientProperty(COLLAPSED, bool)</code>.
 * Also you can call <code>header.putClientProperty(COLLAPSIBLE, false)</code>
 * to prevent the user from toggling the collapsed state. (But you can still
 * programmatically alter it.)
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/resources/collapsiblecontainer.gif"
 * style="border:1px solid gray" alt="CollapsibleContainer Animation">
 * 
 * <p>
 * TODO: in addition to a weight-based model, we also need an alternative
 * priority-based model. As a use case: consider a gap that has a priority of 1,
 * and a scrollpane that has a priority of 2.
 * 
 * @see <a
 *      href="https://javagraphics.blogspot.com/2014/03/panels-collapsible-sections.html">Panels:
 *      Collapsible Sections</a>
 */
public class CollapsibleContainer extends SectionContainer {
	private static final long serialVersionUID = 1L;

	/**
	 * A property name for a Section that maps to the JButton used as a header
	 * for that Section.
	 */
	protected static final String HEADER = CollapsibleContainer.class.getName()
			+ ".header";

	/**
	 * A property name for a Section that identifies its vertical weight. When
	 * it is absent the vertical weight is assumed to be zero.
	 */
	public static final String VERTICAL_WEIGHT = CollapsibleContainer.class
			.getName() + ".vertical-weight";

	/**
	 * A client property name for the JButton header of a <code>Section</code>
	 * that determines whether a the user can collapse/expand a section.
	 */
	public static final String COLLAPSIBLE = CollapsibleContainer.class
			.getName() + ".collapsible";

	/**
	 * A client property name for the JButton header of a <code>Section</code>
	 * that determines whether a Section is collapsed.
	 */
	public static final String COLLAPSED = CollapsibleContainer.class.getName()
			+ ".collapsed";

	/**
	 * A client property name for the JButton header of a <code>Section</code>
	 * that determines the current rotation of the triangle. (This will vary a
	 * lot during animation.)
	 */
	protected static final String ROTATION = CollapsibleContainer.class
			.getName() + ".rotation";

	/**
	 * A client property name for the JButton header of a <code>Section</code>
	 * that determines the target rotation of the triangle. (This is generally 1
	 * of 2 values: open or closed.)
	 */
	protected static final String TARGET_ROTATION = CollapsibleContainer.class
			.getName() + ".target-rotation";

	/**
	 * A client property name for the JButton header that maps to the
	 * <code>Section</code> this header relates to.
	 */
	protected static final String SECTION = CollapsibleContainer.class
			.getName() + ".section";

	/**
	 * The duration in seconds for animating section heights
	 */
	private static float ANIMATION_DURATION = .2f;

	/**
	 * This contains the preferred layout of this container at a given instant.
	 * This layout can be installed in one instant or incrementally (for
	 * animation)
	 */
	class LayoutBlueprint extends AnimationManager.Adjuster<Float> {

		List<JComponent> components = new ArrayList<JComponent>();
		Map<JComponent, Integer> heightMap = new HashMap<JComponent, Integer>();
		Map<JComponent, Integer> originalHeightMap = new HashMap<JComponent, Integer>();
		Set<JComponent> visibleComponents = new HashSet<JComponent>();
		boolean initialPermitLocked;

		protected LayoutBlueprint(boolean initialPermitLocked) {
			super(ANIMATION_DURATION, 1F);
			this.initialPermitLocked = initialPermitLocked;
			Insets insets = getInsets();
			int height = getHeight() - insets.top - insets.bottom;

			int remainingHeight = height;

			float totalVerticalWeight = 0;
			Map<JComponent, Number> verticalWeightMap = new HashMap<JComponent, Number>();

			for (int a = 0; a < sections.size(); a++) {
				Section section = sections.get(a);
				JPanel body = section.getBody();
				JButton header = getHeader(section);

				Boolean collapsed = (Boolean) header
						.getClientProperty(COLLAPSED);
				if (collapsed == null)
					collapsed = Boolean.FALSE;
				if (!header.isVisible())
					collapsed = true;

				components.add(header);
				components.add(body);

				if (header.isVisible())
					visibleComponents.add(header);
				if ((!collapsed))
					visibleComponents.add(body);

				Number n = (Number) section.getProperty(VERTICAL_WEIGHT);
				if (n == null)
					n = 0;
				if (visibleComponents.contains(body)) {
					totalVerticalWeight += n.floatValue();
					if (n.floatValue() != 0)
						verticalWeightMap.put(body, n);
				}

				if (visibleComponents.contains(header)) {
					Dimension headerSize = header.getPreferredSize();
					heightMap.put(header, headerSize.height);
					remainingHeight -= headerSize.height;
				} else {
					heightMap.put(header, 0);
				}
				originalHeightMap.put(header, header.getHeight());

				if (visibleComponents.contains(body) && n.floatValue() == 0) {
					Dimension bodySize = body.getPreferredSize();
					heightMap.put(body, bodySize.height);
					remainingHeight -= bodySize.height;
				} else {
					heightMap.put(body, 0);
				}
				originalHeightMap.put(body, body.getHeight());
			}

			if (remainingHeight > 0 && totalVerticalWeight > 0) {
				// divide the remaining height based on the vertical weight
				int designatedHeight = 0;
				JComponent lastC = null;
				for (JComponent jc : verticalWeightMap.keySet()) {
					Number weight = verticalWeightMap.get(jc);
					int i = (int) (weight.floatValue() / totalVerticalWeight * remainingHeight);
					heightMap.put(jc, heightMap.get(jc) + i);
					designatedHeight += i;
					lastC = jc;
				}

				// due to rounding error, we may have a few extra pixels:
				int remainingPixels = remainingHeight - designatedHeight;
				// tack them on to someone. anyone.
				heightMap.put(lastC, heightMap.get(lastC) + remainingPixels);
			}
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("LayoutBlueprint[");
			for (int a = 0; a < components.size(); a++) {
				JComponent jc = components.get(a);
				int height = heightMap.get(jc);
				boolean visible = visibleComponents.contains(jc);
				if (a != 0)
					sb.append(", ");
				sb.append(height + " (" + jc.getClass().getName());
				if (!visible) {
					sb.append(" - hidden");
				}
				sb.append(")");
			}
			sb.append("]");
			return sb.toString();
		}

		protected void install() {
			midanimation.acquireUninterruptibly();
			try {
				Insets insets = getInsets();

				int x = insets.left;
				int y = insets.top;
				int width = getWidth() - insets.right - insets.left;

				for (int a = 0; a < components.size(); a++) {
					JComponent jc = components.get(a);
					if (visibleComponents.contains(jc)) {
						int h = heightMap.get(jc);
						jc.setBounds(x, y, width, h);
						jc.validate();
						y += h;
						jc.setVisible(true);
					} else {
						jc.setBounds(0, 0, 0, 0);
						jc.setVisible(false);
					}
				}
			} finally {
				midanimation.release();
			}
		}

		@Override
		public void increment(Ticket ticket, double fraction) {
			try {
				Insets insets = getInsets();

				int x = insets.left;
				int y = insets.top;
				int width = getWidth() - insets.right - insets.left;

				boolean moreChanges = false;
				boolean requiresValidation = false;
				for (JComponent jc : components) {
					if (visibleComponents.contains(jc)) {
						if (!jc.isVisible()) {
							jc.setSize(width, 0);
							jc.setVisible(true);
							requiresValidation = true;
						}
					}
				}

				for (int a = 0; a < components.size(); a++) {
					JComponent jc = components.get(a);
					int originalHeight = originalHeightMap.get(jc);
					int targetHeight = heightMap.get(jc);
					int newHeight = (int) (targetHeight * (fraction)
							+ originalHeight * (1 - fraction) + .5);
					if (Math.abs(targetHeight - newHeight) <= 1)
						newHeight = targetHeight;

					jc.setBounds(x, y, width, newHeight);
					jc.validate();
					y += newHeight;

					if (newHeight != targetHeight)
						moreChanges = true;
				}

				if (!moreChanges) {
					for (JComponent jc : components) {
						if (!visibleComponents.contains(jc)) {
							if (jc.isVisible()) {
								jc.setVisible(false);
								requiresValidation = true;
							}
						}
					}
				}

				if (requiresValidation) {
					CollapsibleContainer.this.invalidate();
					CollapsibleContainer.this.validate();
				}
			} finally {
				if (fraction >= 1 && initialPermitLocked)
					midanimation.release();
			}
		}
	}

	private class CollapsibleLayout implements LayoutManager {

		public void addLayoutComponent(String name, Component comp) {
		}

		public void removeLayoutComponent(Component comp) {
		}

		public Dimension preferredLayoutSize(Container parent) {
			return calculateSize(parent, true);
		}

		/**
		 * 
		 * @param usePreferred
		 *            true for preferred size, false for minimum size
		 */
		private Dimension calculateSize(Container parent, boolean usePreferred) {
			Dimension size = new Dimension();
			for (int a = 0; a < parent.getComponentCount(); a++) {
				Component comp = parent.getComponent(a);
				Dimension d = usePreferred ? comp.getPreferredSize() : comp
						.getMinimumSize();
				size.width = Math.max(size.width, d.width);
				size.height += d.height;
			}
			Insets insets = getInsets();
			size.width += insets.left + insets.right;
			size.height += insets.top + insets.bottom;
			return size;
		}

		public Dimension minimumLayoutSize(Container parent) {
			return calculateSize(parent, false);
		}

		public void layoutContainer(Container parent) {
			if (midanimation.availablePermits() == 0)
				return;

			LayoutBlueprint blueprint = new LayoutBlueprint(false);
			blueprint.install();
		}
	}

	private Set<JButton> headers = new HashSet<JButton>();
	ChangeListener sectionListener = new ChangeListener() {

		public void stateChanged(ChangeEvent e) {
			// add new headers & bodies if necessary:
			for (Section section : sections) {
				getHeader(section);
				JPanel body = section.getBody();
				if (body.getParent() != CollapsibleContainer.this) {
					add(body);
				}
			}

			// remove unused headers/bodies from this panel:
			Iterator<JButton> iter = headers.iterator();
			while (iter.hasNext()) {
				JButton header = iter.next();
				Section section = (Section) header.getClientProperty(SECTION);
				if (section.getBody().getParent() != CollapsibleContainer.this) {
					iter.remove();
					section.setProperty(HEADER, null);
					remove(header);
					remove(section.getBody());
				}
			}
			revalidate();
		}
	};

	public CollapsibleContainer() {
		setLayout(new CollapsibleLayout());
		sections.addChangeListener(sectionListener, false);

		sections.addChangeListener(new ChangeListener() {
			Set<Section> mySections = new WeakSet<>();

			@Override
			public void stateChanged(ChangeEvent e) {
				for (Section s : sections) {
					if (mySections.add(s)) {
						final JPanel body = s.getBody();
						new DescendantListener(s.getBody(), false) {

							@Override
							public void register(Component c) {
								refreshSectionBody(body);
							}

							@Override
							public void unregister(Component c) {
								refreshSectionBody(body);
							}

						};
					}
				}
			}

			private void refreshSectionBody(JPanel container) {
				JPanel p = container;
				QPanelUI ui;
				if (!(p.getUI() instanceof QPanelUI)) {
					ui = new QPanelUI();
					p.setUI(ui);
				} else {
					ui = (QPanelUI) p.getUI();
				}
				ui.assign(QPanelUI.createBoxUI());

				if (p.getComponentCount() == 1) {
					Component child = p.getComponent(0);
					if (child instanceof JList || child instanceof JTree) {
						JComponent jc = (JComponent) child;
						Border b = jc.getBorder();
						Insets i = b == null ? null : b.getBorderInsets(jc);
						boolean hasBorder = i != null && i.left > 0
								&& i.right > 0 && i.top > 0 && i.bottom > 0;
						ui.setFillColor(child.getBackground());
						ui.setCornerSize(0);
						ui.setShadowSize(0);
						if (!hasBorder) {
							ui.setStrokeColor(Color.gray);
						}

					} else if (child instanceof JScrollPane) {
						ui.setFillColor(child.getBackground());
						ui.setCornerSize(0);
						ui.setShadowSize(0);
						ui.setStrokeColor(new Color(0, 0, 0, 0));
					} else if (child.isOpaque()
							&& child.getBackground().getAlpha() > 0) {
						ui.setCornerSize(1);
					}
				}
			}
		}, false);
	}

	protected JButton createHeader(Section s) {
		return createCollapsibleButton("", true);
	}

	LayoutBlueprint animatingBlueprint;
	protected Semaphore midanimation = new Semaphore(1);

	/** Return the header button associated with a Section. */
	public JButton getHeader(final Section section) {
		JButton header = (JButton) section.getProperty(HEADER);
		if (header == null) {
			header = createHeader(section);
			final JButton headerRef = header;

			PropertyChangeListener nameListener = new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt == null || NAME.equals(evt.getPropertyName())) {
						headerRef.setText(section.getName());
					}
				}
			};
			section.addPropertyChangeListener(nameListener);
			nameListener.propertyChange(null);

			header.addPropertyChangeListener(COLLAPSED,
					new PropertyChangeListener() {
						public void propertyChange(PropertyChangeEvent evt) {
							midanimation.acquireUninterruptibly();
							animatingBlueprint = new LayoutBlueprint(true);
							String p = CollapsibleContainer.class.getName()
									+ "#temp";
							putClientProperty(p, 0f);
							AnimationManager.setTargetProperty(
									CollapsibleContainer.this, p,
									animatingBlueprint);
						}
					});

			section.setProperty(HEADER, header);
			headers.add(header);
			header.putClientProperty(SECTION, section);
			add(header);
		}
		return header;
	}

	/**
	 * @return a button that can toggle the property <code>COLLAPSED</code> if
	 *         the property <code>COLLAPSIBLE</code> is <code>true</code>.
	 *         <p>
	 *         This button (by default) uses a <code>QButtonUI</code>, so if you
	 *         configure it's position then you can toggle off certain parts of
	 *         the border.
	 *         <p>
	 *         The following property of this button are automatically
	 *         maintained through a set of listeners:
	 *         <ul>
	 *         <li>Request Focus Enabled</li>
	 *         <li>Focusable</li>
	 *         <li>Icon</li>
	 *         <li>COLLAPSED</li>
	 *         <li>Internal properties including ROTATION and TARGET_ROTATION</li>
	 *         </ul>
	 */
	public static JButton createCollapsibleButton() {
		return createCollapsibleButton("", true);
	}

	/**
	 * 
	 * @param text
	 *            the text in this button
	 * @param includeLeftAndRightEdges
	 *            whether the left and right edges should be visible
	 * @param collapsible
	 *            whether this button should initially be collapsible.
	 * @return a button that can toggle the property <code>COLLAPSED</code> if
	 *         the property <code>COLLAPSIBLE</code> is <code>true</code>.
	 *         <p>
	 *         This button (by default) uses a <code>QButtonUI</code>, so if you
	 *         configure it's position then you can toggle off certain parts of
	 *         the border.
	 *         <p>
	 *         The following property of this button are automatically
	 *         maintained through a set of listeners:
	 *         <ul>
	 *         <li>Request Focus Enabled</li>
	 *         <li>Focusable</li>
	 *         <li>Icon</li>
	 *         <li>COLLAPSED</li>
	 *         <li>Internal properties including ROTATION and TARGET_ROTATION</li>
	 *         </ul>
	 */
	public static JButton createCollapsibleButton(String text,
			boolean collapsible) {
		final JButton button = new JButton();
		QButtonUI ui = new GradientButtonUI();
		button.setContentAreaFilled(false);
		button.setUI(ui);
		button.putClientProperty(QButtonUI.PROPERTY_STROKE_PAINTED,
				Boolean.FALSE);

		button.setFont(UIManager.getFont("Label.font"));
		button.setText(text);
		button.putClientProperty(QButtonUI.PROPERTY_HORIZONTAL_POSITION,
				HorizontalPosition.MIDDLE);

		// TODO: the focus ring is 1 pixel off when the stroke is not painted.
		// Setting the vertical position to middle helps hide this a little, but
		// it's still a bug.
		button.putClientProperty(QButtonUI.PROPERTY_VERTICAL_POSITION,
				VerticalPosition.MIDDLE);

		ui.setPaintFocus(PaintFocus.INSIDE);
		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.setIconTextGap(2);
		button.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if ((Boolean) button.getClientProperty(COLLAPSIBLE)) {
					if (e.getKeyCode() == KeyEvent.VK_LEFT
							|| e.getKeyCode() == KeyEvent.VK_UP) {
						button.putClientProperty(COLLAPSED, true);
						e.consume();
					} else if (e.getKeyCode() == KeyEvent.VK_RIGHT
							|| e.getKeyCode() == KeyEvent.VK_DOWN) {
						button.putClientProperty(COLLAPSED, false);
						e.consume();
					}
				}
			}
		});
		button.setOpaque(true);

		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Boolean b = (Boolean) button.getClientProperty(COLLAPSED);
				button.putClientProperty(COLLAPSED, !b);
			}
		});

		// this is always rendered as enabled, whether it is or not
		button.putClientProperty(QButtonUI.PROPERTY_BOOLEAN_BUTTON_STATE,
				new ButtonState.Boolean(true, false, false, false, false));

		button.addPropertyChangeListener(new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) {
				if (COLLAPSIBLE.equals(evt.getPropertyName())) {
					boolean collapsible = ((Boolean) evt.getNewValue());
					button.setEnabled(collapsible);
					updateIcon();
				} else if (COLLAPSED.equals(evt.getPropertyName())) {
					boolean collapsed = ((Boolean) evt.getNewValue());
					if (collapsed) {
						button.putClientProperty(TARGET_ROTATION, new Double(0));
					} else {
						button.putClientProperty(TARGET_ROTATION, new Double(
								Math.PI / 2.0));
					}
				} else if (ROTATION.equals(evt.getPropertyName())) {
					updateIcon();
				}
			}

			private Icon triangleIcon = new TriangleIcon(SwingConstants.EAST,
					10, 10);

			private void updateIcon() {
				if ((Boolean) button.getClientProperty(COLLAPSIBLE)) {
					Number n = (Number) button.getClientProperty(ROTATION);
					if (n == null)
						n = 0;
					float rotation = n.floatValue();
					button.setIcon(new RotatedIcon(triangleIcon, rotation));
				} else {
					button.setIcon(null);
				}
			}

		});

		button.putClientProperty(COLLAPSIBLE, collapsible);
		button.putClientProperty(COLLAPSED, false);

		UIEffect.installTweenEffect(button, TARGET_ROTATION, ROTATION, .15f, 20);

		return button;
	}
}