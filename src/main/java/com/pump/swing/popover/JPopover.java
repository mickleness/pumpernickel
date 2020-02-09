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
package com.pump.swing.popover;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.MenuSelectionManager;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.swing.popup.PopupTarget;
import com.pump.swing.popup.QPopup;
import com.pump.swing.popup.QPopupFactory;
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
						p.refreshVisibility(false);
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
	private PopoverVisibility<T> popoverVisibility;
	private PopupTarget popupTarget;
	private boolean rolloverContents;
	private long lastKeepVisible = System.currentTimeMillis();
	private Timer timer = new Timer(100, new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			boolean isKeepActive = getVisibility().isVisible(JPopover.this)
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
						}
					}
				} finally {
					dirty = false;
				}
			}
		};

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			System.err.println(evt);
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

		owner.getAccessibleContext().addPropertyChangeListener(pcl);
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
				refreshVisibility(false);
			}

		});

		MenuSelectionManagerListener.install(this);

		setVisibility(new BasicPopoverVisibility<T>());
	}

	/**
	 * This reevaluates whether the popover should be visible.
	 * @param refreshNow if true then this executes immediately,
	 * if false then this executes in the EDT using "invokeLater"
	 */
	public void refreshVisibility(boolean refreshNow) {
		Runnable runnable = new Runnable() {
			public void run() {
				refreshVisibility(null);
			}
		};
		if (refreshNow) {
			runnable.run();
		} else {
			SwingUtilities.invokeLater(runnable);
		}
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
		boolean v = getVisibility().isVisible(this);
		if (v) {
			visible.setValue(true);
			if (runIfVisible != null)
				runIfVisible.run();
		}
	}

	/**
	 * Return the PopoverVisibility used to determine if this popover should be
	 * visible.
	 */
	public PopoverVisibility<T> getVisibility() {
		return popoverVisibility;
	}

	/**
	 * Set the PopoverVisibility used to determine if this popover should be
	 * visible.
	 */
	public void setVisibility(PopoverVisibility<T> pv) {
		Objects.requireNonNull(pv);
		if (pv == popoverVisibility)
			return;

		if (popoverVisibility != null)
			popoverVisibility.uninstall(this);

		popoverVisibility = pv;
		popoverVisibility.install(this);
		refreshVisibility(false);
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
	 * This calls {@link #doRefreshPopup()} and automatically repositions the
	 * popover if needed.
	 */
	public final void refreshPopup() {
		doRefreshPopup();
		if (popup != null) {
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
		return qpf.getQPopup(getOwner(), popupTarget, getContents());
	}

	public void setTarget(PopupTarget popupTarget) {
		this.popupTarget = popupTarget;
		refreshPopup();
	}
}