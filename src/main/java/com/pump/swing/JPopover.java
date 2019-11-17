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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.Objects;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.MenuSelectionManager;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;

import com.pump.awt.DescendantListener;
import com.pump.util.BooleanProperty;

/**
 * This is similar to a tooltip, except each JPopover is responsible for its own
 * visibility rules. Since tooltips have such a well-defined meaning in Swing
 * this class branches out with a new name for a similar feature.
 * <p>
 * This class is inspired by Apple's <a href=
 * "https://developer.apple.com/macos/human-interface-guidelines/windows-and-views/popovers/"
 * >popover</a> and Microsoft's <a href=
 * "https://msdn.microsoft.com/en-us/library/windows/desktop/dn742400(v=vs.85).aspx"
 * >balloon</a> components.
 * <p>
 * By default a JPopover will appear when the mouse hovers over or transfers
 * keyboard focus to the owner component. You can override this behavior by
 * overriding the {@link #isStayVisible()} method. There can be only one
 * JPopover visible at any time, so when one opens the previous one will close.
 *
 * @param <T>
 */
public class JPopover<T extends JComponent> {

	/**
	 * This calculates whether a popover should be visible.
	 * <p>
	 * The default implementation takes into account whether the owner or the
	 * content has the keyboard focus or mouse rollover.
	 * <p>
	 * Also for JComboBoxes we restrict visibility when a component is expanded.
	 */
	public static class VisibleCalculator {

		/**
		 * This calls {@link #calculateVisible(JPopover)} and may make the
		 * popover visible.
		 * <p>
		 * A separate timer is responsible for making the popover hidden.
		 * 
		 * @param popover
		 *            the popover to evaluate
		 * @param runIfVisible
		 *            an optional runnable to immediately invoke if
		 *            {@link #calculateVisible(JPopover)} returns true.
		 */
		public final void run(JPopover<?> popover, Runnable runIfVisible) {
			boolean visible = calculateVisible(popover);
			if (visible)
				popover.visible.setValue(visible);

			// there's a separate timer that will set visible to false if needed

			if (visible && runIfVisible != null) {
				runIfVisible.run();
			}
		}

		/**
		 * Calculate whether a popover should be visible or not.
		 * <p>
		 * The default implementation takes into account the keybord focus,
		 * mouse position, and expanded state.
		 * 
		 * @param popover
		 *            the JPopover to evaluate
		 * @return true if the popover should be visible.
		 */
		protected boolean calculateVisible(
				JPopover<? extends JComponent> popover) {
			JComponent owner = popover.getOwner();
			JComponent contents = popover.getContents();

			boolean newVisible;

			if (owner.isShowing() && owner.isEnabled()) {
				newVisible = isFocusOwnerOrAncestor(owner) || isRollover(owner);
				if (!newVisible && popover.isRolloverContents()) {
					newVisible = isFocusOwnerOrAncestor(contents)
							|| (isRollover(contents));
				}
			} else {
				newVisible = false;
			}

			if (isExpanded(owner)) {
				newVisible = false;
			}

			return newVisible;
		}

		/**
		 * Return true if the argument is the focus owner or is an ancestor of
		 * the focus owner.
		 */
		protected boolean isFocusOwnerOrAncestor(JComponent jc) {
			Component focusOwner = KeyboardFocusManager
					.getCurrentKeyboardFocusManager().getFocusOwner();
			if (focusOwner == null)
				return false;
			return jc == focusOwner
					|| SwingUtilities.isDescendingFrom(focusOwner, jc);
		}

		/**
		 * Return true if the mouse is currently over the argument.
		 */
		protected boolean isRollover(JComponent jc) {
			if (!jc.isShowing())
				return false;
			Point p = jc.getLocationOnScreen();
			int w = jc.getWidth();
			int h = jc.getHeight();

			Point mouse = MouseInfo.getPointerInfo().getLocation();

			return mouse.x >= p.x && mouse.y >= p.y && mouse.x < p.x + w
					&& mouse.y < p.y + h;
		}

		/**
		 * Return true if the component is expanded, such as when you open a
		 * JComboBox.
		 */
		protected boolean isExpanded(JComponent jc) {
			boolean expanded = false;
			AccessibleContext c = jc.getAccessibleContext();
			if (c != null) {
				AccessibleStateSet axSet = c.getAccessibleStateSet();
				if (axSet.contains(AccessibleState.EXPANDED)) {
					expanded = true;
				}
			}
			return expanded;

		}
	}

	private static VisibleCalculator DEFAULT_VISIBLE_CALCULATOR = new VisibleCalculator();

	/**
	 * This listener will call {@link JPopover#refreshVisibility()} when
	 * <code>stateChanged</code> is called.
	 * <p>
	 * This keeps a <code>WeakReference</code> to a JPopover, and this listener
	 * will automatically detach from the MenuSelectionManager if the reference
	 * is lost.
	 */
	protected static class MenuSelectionManagerListener implements
			ChangeListener {

		/**
		 * This installs a new MenuSelectionManagerListener on the
		 * MenuSelectionManager.
		 */
		public static void install(JPopover<?> p) {
			MenuSelectionManager.defaultManager().addChangeListener(
					new MenuSelectionManagerListener(p));
		}

		WeakReference<JPopover<?>> ref;

		/**
		 * Do not call this constructor. Use the {@link #install(JPopover)}
		 * method instead.
		 */
		private MenuSelectionManagerListener(JPopover<?> p) {
			ref = new WeakReference<JPopover<?>>(p);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JPopover<?> p = ref.get();
					if (p != null) {
						p.refreshVisibility();
					} else {
						MenuSelectionManager.defaultManager()
								.removeChangeListener(
										MenuSelectionManagerListener.this);
					}
				}
			});
		}
	}

	static JPopover<?> activePopover;

	public static JPopover<?> getActivePopover() {
		return activePopover;
	}

	protected final JComponent owner;
	protected QPopup popup;
	protected final T contents;
	private final BooleanProperty visible = new BooleanProperty("visible",
			false);
	private VisibleCalculator visibleCalculator;

	private boolean rolloverContents;
	private long lastKeepVisible = System.currentTimeMillis();
	private Timer timer = new Timer(100, new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			boolean isKeepActive = getVisibleCalculator().calculateVisible(
					JPopover.this)
					&& JPopover.this == getActivePopover();

			if (!isKeepActive) {
				if (System.currentTimeMillis() - lastKeepVisible > 50) {
					visible.setValue(false);
				}
			} else {
				lastKeepVisible = System.currentTimeMillis();
			}
		}
	});

	private PropertyChangeListener pcl = new PropertyChangeListener() {
		boolean dirty = false;
		Runnable refreshRunnable = new Runnable() {
			public void run() {
				if (!dirty)
					return;

				try {
					if (popup != null) {
						if (popup.getOwner().isShowing()) {
							refreshPopup();
							popup.show();
						} else {
							popup.hide();
						}
					}
				} finally {
					dirty = false;
				}
			}
		};

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			dirty = true;
			SwingUtilities.invokeLater(refreshRunnable);
		}

	};

	/**
	 * Create a new JPopover.
	 * 
	 * @param owner
	 *            the owner/trigger of this JPopover.
	 * @param contents
	 *            the contents of this JPopover.
	 * @param rolloverContents
	 *            if true this popover stays visible when the user moves the
	 *            mouse over it. If your popover contains components like
	 *            buttons or textfields that the user needs to interact with
	 *            then this should be true. If the contents are not interactive
	 *            (like a label or tooltip) then this should be false.
	 */
	public JPopover(JComponent owner, T contents, boolean rolloverContents) {
		Objects.requireNonNull(owner);
		Objects.requireNonNull(contents);
		this.owner = owner;
		this.contents = contents;
		this.rolloverContents = rolloverContents;
		if (contents instanceof JToolTip) {
			((JToolTip) contents).setComponent(owner);
		}

		addPropertyChangeListeners(owner.getAccessibleContext());
		visible.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (visible.getValue()) {
					refreshPopup();
					if (popup == null) {
						popup = createPopup();
					}
					if (visible.getValue()) {
						activePopover = JPopover.this;
						popup.show();
						lastKeepVisible = System.currentTimeMillis();
						timer.start();
						return;
					}
				}

				popup.hide();
				timer.stop();
				if (getActivePopover() == JPopover.this)
					activePopover = null;
				popup = null;
			}

		});

		installOwnerTriggers();
		getOwner().addComponentListener(new ComponentAdapter() {

			Runnable successRunnable = new Runnable() {

				@Override
				public void run() {
					refreshPopup();
					popup.show();
				}
			};

			@Override
			public void componentResized(ComponentEvent e) {
				refreshVisibility(successRunnable);
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				refreshVisibility();
			}

		});

		MenuSelectionManagerListener.install(this);
	}

	/**
	 * This reevaluates whether the popover should be visible.
	 * <p>
	 * This is shorthand for <code>refreshVisibility(this, null)</code>.
	 */
	public void refreshVisibility() {
		refreshVisibility(null);
	}

	/**
	 * This reevaluates whether the popover should be visible.
	 * <p>
	 * This is shorthand for <code>refreshVisibility(this, runIfVisible)</code>.
	 * 
	 * @param runIfVisible
	 *            if this is non-null then this runnable is immediately invoked
	 *            if the popover should be visible. (This is invoked whether or
	 *            not the popover is already visible.)
	 */
	public void refreshVisibility(Runnable runIfVisible) {
		getVisibleCalculator().run(this, runIfVisible);
	}

	/**
	 * Return the VisibleCalculator used to determine if this popover should be
	 * visible.
	 */
	public VisibleCalculator getVisibleCalculator() {
		VisibleCalculator c = visibleCalculator;
		if (c == null)
			c = DEFAULT_VISIBLE_CALCULATOR;
		return c;
	}

	/**
	 * Set the VisibleCalculator used to determine if this popover should be
	 * visible.
	 */
	public void setVisibleCalculator(VisibleCalculator vc) {
		if (vc == visibleCalculator)
			return;

		visibleCalculator = vc;
		refreshVisibility();
	}

	/**
	 * Install the listeners on the owner that make this popover visible. By
	 * default this method calls {@link #installOwnerFocusTrigger()} and
	 * {@link #installOwnerMouseTrigger()}.
	 */
	protected void installOwnerTriggers() {
		installOwnerFocusTrigger();
		installOwnerMouseTrigger();
	}

	/**
	 * Install a FocusListener on the owner (and its descendants) to show this
	 * popover when they have the keyboard focus.
	 */
	protected void installOwnerFocusTrigger() {
		DescendantListener.addFocusListener(owner, new FocusAdapter() {

			@Override
			public void focusGained(FocusEvent e) {
				refreshVisibility();
			}

		}, false);
	}

	/**
	 * Install a MouseListener on the owner (and its descendants) to show this
	 * popover when the mouse hovers over the owner.
	 */
	protected void installOwnerMouseTrigger() {
		MouseInputAdapter ml = new MouseInputAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				refreshVisibility();
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				refreshVisibility();
			}

		};
		DescendantListener.addMouseListener(owner, ml, false);
	}

	/**
	 * Return the contents of this JPopover.
	 */
	public T getContents() {
		return contents;
	}

	/**
	 * Return the owner/trigger of this JPopover.
	 */
	public JComponent getOwner() {
		return owner;
	}

	/**
	 * Attach listeners to a context and its descendants so we'll refresh the
	 * popup when interesting events happen. This is a generic way to listen to
	 * any interesting event in a JComponent subclass without knowing what that
	 * subclass is. (For example: if you drag a JSlider around, this generates
	 * accessible changes we listen to.)
	 */
	private void addPropertyChangeListeners(AccessibleContext accessibleContext) {
		accessibleContext.addPropertyChangeListener(pcl);
		for (int a = 0; a < accessibleContext.getAccessibleChildrenCount(); a++) {
			Accessible z = accessibleContext.getAccessibleChild(a);
			AccessibleContext ax = z.getAccessibleContext();
			if (ax != null) {
				addPropertyChangeListeners(ax);
			}
		}
	}

	/**
	 * This calls {@link #doRefreshPopup()} and automatically repositions the
	 * popover if needed.
	 */
	public final void refreshPopup() {
		Dimension d = getContents().getPreferredSize();
		doRefreshPopup();
		Dimension d2 = getContents().getPreferredSize();
		if (!d.equals(d2) && popup != null) {
			popup.show();
		}
	}

	/**
	 * This refreshes the content of this popup. This is automatically called
	 * when the owner is updated and the popup is visible. For example: if a
	 * JSlider is modified, this will be called so you can update the contents
	 * of this popover based on the new slider value.
	 */
	protected void doRefreshPopup() {

	}

	/**
	 * Return true if the popover is showing. This is mostly equivalent to
	 * calling <code>getContents().isShowing()</code>.
	 * <p>
	 * To make the popover visible: override {@link #calculateVisibility()} to
	 * return true and call {@link #refreshVisibility()}.
	 */
	public boolean isVisible() {
		return visible.getValue();
	}

	/**
	 * Return true if this popover stays visible when the user moves the mouse
	 * over it. If your popover contains components like buttons or textfields
	 * that the user needs to interact with then this should be true. If the
	 * contents are not interactive (like a label or tooltip) then this should
	 * be false.
	 */
	public boolean isRolloverContents() {
		return rolloverContents;
	}

	/**
	 * Create a new QPopup to display this popover's {@link #getContents()}.
	 */
	protected QPopup createPopup() {
		PopupFactory pf = PopupFactory.getSharedInstance();
		QPopupFactory qpf = null;
		if (pf instanceof QPopupFactory) {
			qpf = (QPopupFactory) pf;
		} else {
			qpf = new QPopupFactory(pf);
		}
		return qpf.getQPopup(getOwner(), getContents());
	}
}