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

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * The JEyeDropper is a modal dialog that follows the user's mouse to select a
 * color.
 * <p>
 * There are two things clients may be interested in listening to:
 * <ul>
 * <li>When a color is selected. This can be detected by calling
 * <code>getButton().addActionListener(myListener)</code>. This indicates the
 * user clicked the mouse (or pressed the return key) and they want to commit
 * their selection. The user can also press the escape key to cancel this
 * dialog, in which case the button's ActionListeners are never notified.</li>
 * <li>When a color changes. This can be detected by calling
 * <code>getModel().addChangeListener(myChangeListener)</code>. Or you may not
 * want to listen to all the incremental changes and instead just call
 * <code>getModel().getSelectedColor()</code> when the
 * <code>ActionListener</code> is notified.</li>
 * </ul>
 * <p>
 * The action associated with selecting a color is handled as a normal JButton
 * action. This component offers 7 additional actions:
 * <ol>
 * <li>Cancel (escape key): immediately dismiss this dialog without selecting
 * the button.</li>
 * <li>Left (left key): nudge the mouse left one pixel.</li>
 * <li>Up (up key): nudge the mouse up one pixel.</li>
 * <li>Right (right key): nudge the mouse right one pixel.</li>
 * <li>Down (down key): nudge the mouse down one pixel.</li>
 * <li>Zoom In (plus key): increase the size of the magnified pixel.</li>
 * <li>Zoom Out (minus key): decrease the size of the magnified pixel.</li>
 * </ol>
 * <p>
 * This relies on a <code>java.awt.Robot</code> to grab screen pixels and
 * manipulate the mouse. This cannot detect changes to pixels that occur under
 * the current dialog. (That is: if you're playing an animation then this
 * eyedropper dialog will only show pixel data from one frame.)
 */
public class JEyeDropper extends JDialog {
	private static final long serialVersionUID = 1L;

	public static final String ACTION_MAP_KEY_CANCEL = "cancel";
	public static final String ACTION_MAP_KEY_LEFT = "left";
	public static final String ACTION_MAP_KEY_RIGHT = "right";
	public static final String ACTION_MAP_KEY_UP = "up";
	public static final String ACTION_MAP_KEY_DOWN = "down";
	public static final String ACTION_MAP_KEY_ZOOM_IN = "zoom in";
	public static final String ACTION_MAP_KEY_ZOOM_OUT = "zoom out";

	/**
	 * The minimum value of {@link #getPixelSize()}
	 */
	public static final int MAGNIFICATION_MIN = 2;

	/**
	 * The maximum value of {@link #getPixelSize()}
	 */
	public static final int MAGNIFICATION_MAX = 20;

	/**
	 * This client property for the content button relates to an optional
	 * Integer for the pixel size. You can manually set this by calling
	 * <code>getButton().putClientProperty(PROPERTY_PIXEL_SIZE, x)</code>
	 */
	public static final String PROPERTY_PIXEL_SIZE = JEyeDropper.class
			.getName() + "#pixelSize";

	private class ContentButton extends JButton {

		private static final long serialVersionUID = 1L;

		private boolean requestedFocusYet = false;

		private Cursor hiddenCursor = Toolkit.getDefaultToolkit()
				.createCustomCursor(
						new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
						new Point(0, 0), "hidden cursor");

		private MouseInputAdapter mouseListener = new MouseInputAdapter() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.getWheelRotation() < 0) {
					getZoomInAction().actionPerformed(
							new ActionEvent(ContentButton.this, 0,
									ACTION_MAP_KEY_ZOOM_IN));
				} else if (e.getWheelRotation() > 0) {
					getZoomOutAction().actionPerformed(
							new ActionEvent(ContentButton.this, 0,
									ACTION_MAP_KEY_ZOOM_OUT));
				}
			}

		};

		/**
		 * This timer constantly checks to see if the window needs
		 * repositioning.
		 * <p>
		 * MOUSE_MOVED events are unreliable for this goal: the user can trick
		 * Java's MouseEvent system by dragging the component below the dock or
		 * menubar.
		 */
		private Timer timer = new Timer(20, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Point p = MouseInfo.getPointerInfo().getLocation();
				adjustLocation(p);
			}

		});

		private BufferedImage bi;
		private Rectangle screenRect;

		private void adjustLocation(Point mouseLocOnScreen) {
			JEyeDropper w = JEyeDropper.this;
			if (isShowing() && !isFocusOwner() && !requestedFocusYet) {
				requestedFocusYet = true;
				requestFocus();
			}

			int x1 = screenRect == null ? -1 : screenRect.x + screenRect.width
					/ 2;
			int y1 = screenRect == null ? -1 : screenRect.y + screenRect.height
					/ 2;
			if (screenRect == null || x1 != mouseLocOnScreen.x
					|| y1 != mouseLocOnScreen.y) {
				Rectangle newScreenRect = new Rectangle(mouseLocOnScreen.x
						- w.getWidth() / 2, mouseLocOnScreen.y - w.getHeight()
						/ 2, w.getWidth(), w.getHeight());
				if (screenRect == null) {
					screenRect = newScreenRect;
					bi = robot.createScreenCapture(screenRect);
				} else if (!screenRect.equals(newScreenRect)) {
					BufferedImage t = robot.createScreenCapture(newScreenRect);
					Graphics2D g = t.createGraphics();
					g.drawImage(bi, screenRect.x - newScreenRect.x,
							screenRect.y - newScreenRect.y, null);
					g.dispose();
					screenRect = newScreenRect;
					bi = t;
				}

				JEyeDropper.this.setLocation(mouseLocOnScreen.x - w.getWidth()
						/ 2, mouseLocOnScreen.y - w.getHeight() / 2);
				repaint();
				// when we switch apps the cursor reappears, so let's
				// constantly remind the component what its cursor should be
				setCursor(hiddenCursor);

				int x = bi.getWidth() / 2;
				int y = bi.getHeight() / 2;
				Color color = new Color(bi.getRGB(x, y));
				JEyeDropper.this.getModel().setSelectedColor(color);
			}
		}

		protected ContentButton() {
			setPreferredSize(new Dimension(JEyeDropper.this.circleDiameter,
					JEyeDropper.this.circleDiameter));
			setOpaque(false);
			setBackground(new Color(0, 0, 0, 0));
			addMouseMotionListener(mouseListener);
			addMouseListener(mouseListener);
			addMouseWheelListener(mouseListener);
			setCursor(hiddenCursor);
			addHierarchyListener(new HierarchyListener() {

				@Override
				public void hierarchyChanged(HierarchyEvent e) {
					boolean shouldRunTimer = isShowing();
					if (shouldRunTimer && !timer.isRunning()) {
						timer.start();
						repaint();
					} else if (!shouldRunTimer && timer.isRunning()) {
						timer.stop();
					}
				}

			});

			getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_CANCEL, 0),
					ACTION_MAP_KEY_CANCEL);
			getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
					ACTION_MAP_KEY_CANCEL);
			getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0),
					ACTION_MAP_KEY_LEFT);
			getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0),
					ACTION_MAP_KEY_RIGHT);
			getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
					ACTION_MAP_KEY_UP);
			getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
					ACTION_MAP_KEY_DOWN);
			getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0),
					ACTION_MAP_KEY_ZOOM_IN);
			getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0),
					ACTION_MAP_KEY_ZOOM_OUT);

			// but on US keyboards you can't type "plus", you have to type
			// "shift =". This feels hacky; I'm not sure what the better way to
			// achieve this is...
			getInputMap().put(
					KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS,
							KeyEvent.SHIFT_MASK), ACTION_MAP_KEY_ZOOM_IN);
			getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, 0),
					ACTION_MAP_KEY_ZOOM_IN);

			getActionMap().put(ACTION_MAP_KEY_CANCEL, getCancelAction());
			getActionMap().put(ACTION_MAP_KEY_LEFT, getLeftAction());
			getActionMap().put(ACTION_MAP_KEY_RIGHT, getRightAction());
			getActionMap().put(ACTION_MAP_KEY_UP, getUpAction());
			getActionMap().put(ACTION_MAP_KEY_DOWN, getDownAction());
			getActionMap().put(ACTION_MAP_KEY_ZOOM_IN, getZoomInAction());
			getActionMap().put(ACTION_MAP_KEY_ZOOM_OUT, getZoomOutAction());

			setUI(new BasicButtonUI() {

				@Override
				public void paint(Graphics g0, JComponent c) {
					Graphics2D g = (Graphics2D) g0;
					int pixelSize = getPixelSize();
					if (bi != null && pixelSize > 1) {
						g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
								RenderingHints.VALUE_ANTIALIAS_ON);
						g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
								RenderingHints.VALUE_STROKE_PURE);

						g.setPaint(new TexturePaint(bi, new Rectangle(
								getWidth() / 2 - bi.getWidth() / 2 * pixelSize
										- pixelSize / 2, getHeight() / 2
										- bi.getHeight() / 2 * pixelSize
										- pixelSize / 2, bi.getWidth()
										* pixelSize, bi.getHeight() * pixelSize

						)));
						Shape circle = new Ellipse2D.Float(1, 1,
								bi.getWidth() - 3, bi.getHeight() - 3);
						g.fill(circle);
						g.setColor(Color.gray);
						g.setStroke(new BasicStroke(1f));
						g.draw(circle);

						g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
								RenderingHints.VALUE_STROKE_NORMALIZE);
						g.setColor(Color.white);
						g.drawRect(getWidth() / 2 - 1 - pixelSize / 2,
								getHeight() / 2 - 1 - pixelSize / 2,
								pixelSize + 1, pixelSize + 1);
						g.setColor(Color.black);
						g.drawRect(getWidth() / 2 - 2 - pixelSize / 2,
								getHeight() / 2 - 2 - pixelSize / 2,
								pixelSize + 3, pixelSize + 3);
					}
				}
			});
		}

		class EyedropperAccessibleAction implements AccessibleAction {

			AccessibleAction parentAction;

			EyedropperAccessibleAction(AccessibleAction parentAction) {
				this.parentAction = parentAction;
			}

			@Override
			public int getAccessibleActionCount() {
				return parentAction.getAccessibleActionCount() + 7;
			}

			@Override
			public String getAccessibleActionDescription(int i) {
				if (i < parentAction.getAccessibleActionCount()) {
					return parentAction.getAccessibleActionDescription(i);
				}
				i -= parentAction.getAccessibleActionCount();
				switch (i) {
				// TODO: these should be localized
				case 0:
					return "Cancel";
				case 1:
					return "Move left";
				case 2:
					return "Move up";
				case 3:
					return "Move right";
				case 4:
					return "Move down";
				case 5:
					return "Zoom in";
				case 6:
					return "Zoom out";
				}
				return null;
			}

			@Override
			public boolean doAccessibleAction(int i) {
				if (i < parentAction.getAccessibleActionCount()) {
					return parentAction.doAccessibleAction(i);
				}

				String desc = getAccessibleActionDescription(i);
				ActionEvent event = new ActionEvent(ContentButton.this, i, desc);

				i -= parentAction.getAccessibleActionCount();
				switch (i) {
				case 0:
					getCancelAction().actionPerformed(event);
					return true;
				case 1:
					getLeftAction().actionPerformed(event);
					return true;
				case 2:
					getUpAction().actionPerformed(event);
					return true;
				case 3:
					getRightAction().actionPerformed(event);
					return true;
				case 4:
					getDownAction().actionPerformed(event);
					return true;
				case 5:
					getZoomInAction().actionPerformed(event);
					return true;
				case 6:
					getZoomOutAction().actionPerformed(event);
					return true;
				}
				return false;
			}

		}

		@Override
		public AccessibleContext getAccessibleContext() {
			if (accessibleContext == null) {
				accessibleContext = new AccessibleAbstractButton() {
					private static final long serialVersionUID = 1L;
					AccessibleAction accessibleAction;

					@Override
					public AccessibleRole getAccessibleRole() {
						// is this a COLOR_CHOOSER or a PUSH_BUTTON? I could go
						// either way.

						// if we call this a COLOR_CHOOSER then VoiceOver on Mac
						// won't let cmd+opt+space click, so... why not let that
						// be the deciding factor?
						return AccessibleRole.PUSH_BUTTON;
					}

					@Override
					public AccessibleAction getAccessibleAction() {
						if (accessibleAction == null)
							accessibleAction = new EyedropperAccessibleAction(
									super.getAccessibleAction());
						return accessibleAction;
					}
				};
			}
			return accessibleContext;
		}
	}

	/**
	 * This action changes the pixel magnification of the eyedropper renderer.
	 */
	class ZoomAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		int zoomDelta;

		ZoomAction(int zoomDelta) {
			this.zoomDelta = zoomDelta;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			setPixelSize(getPixelSize() + zoomDelta);
		}
	}

	/**
	 * This action nudges the dialog up/down/left/right.
	 */
	class NudgeAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		int dx = 0;
		int dy = 0;

		NudgeAction(int dx, int dy) {
			this.dx = dx;
			this.dy = dy;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			robot.setAutoWaitForIdle(false);
			Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
			robot.mouseMove(mouseLoc.x + dx, mouseLoc.y + dy);
		}
	}

	protected Robot robot;
	protected int circleDiameter;
	protected ContentButton content;
	protected Action cancelAction, leftAction, upAction, rightAction,
			downAction, zoomInAction, zoomOutAction;
	protected AdjustableColorSelectionModel model;

	/**
	 * Create a new JEyeDropper.
	 * 
	 * @param owner
	 *            the frame that owns this dialog.
	 * @param circleDiameter
	 *            the diameter (in pixels) of this dialog.
	 * 
	 * @throws AWTException
	 *             if the Robot class could not be constructed.
	 */
	public JEyeDropper(Frame owner, int circleDiameter) throws AWTException {
		super(owner);
		initialize(circleDiameter);
	}

	/**
	 * Create a new JEyeDropper.
	 * 
	 * @param owner
	 *            the dialog that owns this dialog.
	 * @param circleDiameter
	 *            the diameter (in pixels) of this dialog.
	 * 
	 * @throws AWTException
	 *             if the Robot class could not be constructed.
	 */
	public JEyeDropper(Dialog owner, int circleDiameter) throws AWTException {
		super(owner);
		initialize(circleDiameter);
	}

	/**
	 * Create a new JEyeDropper.
	 * 
	 * @param owner
	 *            the window that owns this dialog.
	 * @param circleDiameter
	 *            the diameter (in pixels) of this dialog.
	 * 
	 * @throws AWTException
	 *             if the Robot class could not be constructed.
	 */
	public JEyeDropper(Window owner, int circleDiameter) throws AWTException {
		super(owner);
		initialize(circleDiameter);
	}

	/**
	 * Create a new JEyeDropper. This is technically owned by a hidden shared
	 * JFrame for Swing components.
	 * 
	 * @param circleDiameter
	 *            the diameter (in pixels) of this dialog.
	 * 
	 * @throws AWTException
	 *             if the Robot class could not be constructed.
	 */
	public JEyeDropper(int circleDiameter) throws AWTException {
		super();
		initialize(circleDiameter);
	}

	private void initialize(int circleDiameter) throws AWTException {
		model = new DefaultAdjustableColorSelectionModel();
		model.setValueIsAdjusting(true);
		this.circleDiameter = circleDiameter;
		robot = new Robot();
		setAlwaysOnTop(true);
		setType(Window.Type.POPUP);
		setUndecorated(true);
		setBackground(new Color(0, 0, 0, 0));
		getRootPane().setOpaque(false);
		getRootPane().putClientProperty("Window.shadow", Boolean.FALSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModalExclusionType(ModalExclusionType.NO_EXCLUDE);
		setFocusable(true);
		setFocusableWindowState(true);
		getRootPane().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;

		content = new ContentButton();
		content.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getModel().setValueIsAdjusting(false);
				setVisible(false);
			}
		});
		getModel().addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				Color color = getModel().getSelectedColor();
				String colorHex = Integer.toHexString(color.getRGB());
				while (colorHex.length() < 6) {
					colorHex = "0" + colorHex;
				}
				// strip leading alpha component
				if (colorHex.length() == 8)
					colorHex = colorHex.substring(2);
				colorHex = "#" + colorHex.toUpperCase();
				content.getAccessibleContext().setAccessibleDescription(
						colorHex);
			}

		});

		getRootPane().add(content);
		content.addPropertyChangeListener(PROPERTY_PIXEL_SIZE,
				new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						content.repaint();
					}
				});
		pack();
		Point p = MouseInfo.getPointerInfo().getLocation();
		setLocation(p.x - getWidth() / 2, p.y - getHeight() / 2);
	}

	/**
	 * Return the action used to hide this dialog without making a selection.
	 */
	protected Action getCancelAction() {
		if (cancelAction == null)
			cancelAction = new AbstractAction() {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
				}
			};
		return cancelAction;
	}

	/**
	 * Return the action used to nudge this dialog one pixel to the left.
	 */
	protected Action getLeftAction() {
		if (leftAction == null)
			leftAction = new NudgeAction(-1, 0);
		return leftAction;
	}

	/**
	 * Return the action used to nudge this dialog one pixel to the right.
	 */
	protected Action getRightAction() {
		if (rightAction == null)
			rightAction = new NudgeAction(1, 0);
		return rightAction;
	}

	/**
	 * Return the action used to nudge this dialog one pixel up.
	 */
	protected Action getUpAction() {
		if (upAction == null)
			upAction = new NudgeAction(0, -1);
		return upAction;
	}

	/**
	 * Return the action used to nudge this dialog one pixel down.
	 */
	protected Action getDownAction() {
		if (downAction == null)
			downAction = new NudgeAction(0, 1);
		return downAction;
	}

	/**
	 * Return the action used to increase the zoom of this dialog.
	 */
	protected Action getZoomInAction() {
		if (zoomInAction == null)
			zoomInAction = new ZoomAction(1);
		return zoomInAction;
	}

	/**
	 * Return the action used to decrease the zoom of this dialog.
	 */
	protected Action getZoomOutAction() {
		if (zoomOutAction == null)
			zoomOutAction = new ZoomAction(-1);
		return zoomOutAction;
	}

	/**
	 * Returns the {@code AdjustableColorSelectionModel} that handles this
	 * dialog's color.
	 *
	 * @return the data model for this component
	 */
	public AdjustableColorSelectionModel getModel() {
		return model;
	}

	/**
	 * Return the button the user selects to confirm their choice.
	 */
	public AbstractButton getButton() {
		return content;
	}

	/**
	 * Return the number of pixels used to represent a pixel during
	 * magnification.
	 */
	public int getPixelSize() {
		Integer i = (Integer) content.getClientProperty(PROPERTY_PIXEL_SIZE);
		if (i == null)
			i = 10;
		return boundPixelSize(i);
	}

	/**
	 * Make sure a pixel size is within the min/max boundaries.
	 */
	private int boundPixelSize(int pixelSize) {
		return Math.min(MAGNIFICATION_MAX,
				Math.max(MAGNIFICATION_MIN, pixelSize));
	}

	/**
	 * Assign the number of pixels used to represent a magnified pixel.
	 */
	public void setPixelSize(int newPixelSize) {
		content.putClientProperty(PROPERTY_PIXEL_SIZE,
				boundPixelSize(newPixelSize));
	}
}