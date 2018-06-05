package com.pump.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.KeyboardFocusManager;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

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

	static JPopover<?> activePopover;

	public static JPopover<?> getActivePopover() {
		return activePopover;
	}

	protected final JComponent owner;
	protected QPopup popup;
	protected final T contents;
	protected final BooleanProperty visible = new BooleanProperty("visible",
			false);

	private boolean rolloverContents;
	private long lastKeepVisible = System.currentTimeMillis();
	private Timer timer = new Timer(100, new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			boolean isKeepActive = isStayVisible()
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
	 *            if true then this popover stays visible when the mouse moves
	 *            the mouse over it. If your popover contains components like
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

			@Override
			public void componentResized(ComponentEvent e) {
				if (visible.getValue()) {
					refreshPopup();
					popup.show();
				}
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				visible.setValue(false);
			}

		});

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
				visible.setValue(true);
			}

		}, false);
	}

	/**
	 * Install a MouseListener on the owner (and its descendants) to show this
	 * popover when the mouse hovers over the owner.
	 */
	protected void installOwnerMouseTrigger() {
		DescendantListener.addMouseListener(owner, new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				visible.setValue(true);
			}

		}, false);
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
	 * This is constantly called on a repeating timer while this JPopover is
	 * visible. If this returns false for over 50 ms then this popover is
	 * hidden.
	 * 
	 * @return true if the popover should stay visible, false if it should be
	 *         hidden.
	 */
	protected boolean isStayVisible() {
		Container ownerParent = getOwner().getParent();
		Container contentsParent = getContents().getParent();

		boolean mouseover = false;

		Point p1 = MouseInfo.getPointerInfo().getLocation();
		Point p2 = new Point(p1);
		if (ownerParent != null) {
			SwingUtilities.convertPointFromScreen(p1, ownerParent);
			if (getOwner().getBounds().contains(p1))
				return true;
		}

		if (contentsParent != null && !mouseover && rolloverContents) {
			SwingUtilities.convertPointFromScreen(p2, contentsParent);
			if (getContents().getBounds().contains(p2))
				return true;
		}

		Component focusOwner = KeyboardFocusManager
				.getCurrentKeyboardFocusManager().getFocusOwner();
		if (focusOwner != null) {
			if (focusOwner == getOwner()
					|| focusOwner == getContents()
					|| SwingUtilities.isDescendingFrom(focusOwner, getOwner())
					|| SwingUtilities.isDescendingFrom(focusOwner,
							getContents()))
				return true;
		}

		return false;
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
	 * Refresh the contents and position of the Popup. This is automatically
	 * called when the owner is updated and the popup is visible. For example:
	 * if a JSlider is modified, this will be called so you can update the
	 * contents of this popover based on the new slider value.
	 */
	protected void refreshPopup() {
	}

	/**
	 * Toggle the visibility of this popover.
	 */
	public void setVisible(boolean popoverVisible) {
		visible.setValue(popoverVisible);
	}

	/**
	 * Return true if the popover is showing. This is mostly equivalent to
	 * calling <code>getContents().isShowing()</code>.
	 */
	public boolean isVisible() {
		return visible.getValue();
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