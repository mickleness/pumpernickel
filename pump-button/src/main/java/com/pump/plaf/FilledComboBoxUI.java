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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

import com.pump.icon.TriangleIcon;
import com.pump.icon.UpDownIcon;

/** A <code>BasicComboBoxUI</code> that models its appearance after a {@link com.pump.plaf.FilledButtonUI}.
 */
public class FilledComboBoxUI extends BasicComboBoxUI {
	

	/** Create a new instance of the FilledComboBoxUI.
	 * <p>Note this leaves which <code>FilledButtonUI</code> you want ambiguous.
	 * First this will check against the UIManager's <code>ButtonUI</code> property,
	 * and if that is not a <code>FilledButtonUI</code> then the {@link com.pump.plaf.RoundRectButtonUI}
	 * will be used.
	 * <p>This method is required if you want to make this ComboBoxUI the default
	 * UI by invoking:
	 * <br><code>UIManager.getDefaults().put("ComboBoxUI", "com.pump.plaf.FilledComboBoxUI");</code>
	 */
	public static ComponentUI createUI(JComponent c) {
		return new FilledComboBoxUI();
	}

	/** A small triangle pointing down. */
	protected static TriangleIcon downIcon = new TriangleIcon(SwingConstants.SOUTH, 6, 6);
	
	/** The client property that determines whether the popup menu
	 * pops down or up.
	 * @see #isPopDown()
	 * @see <a href="https://developer.apple.com/library/mac/#technotes/tn2007/tn2196.html">Apple Tech Note 2196</a>
	 * 
	 */
	public static final String IS_POP_DOWN_KEY = "JComboBox.isPopDown";
	
	protected static Icon upAndDownIcon = new UpDownIcon(5, 4, 3);

	/** Returns the widest x-arc in a path. */
	protected static int getXArc(GeneralPath path) {
		float[] coords = new float[6];
		PathIterator i = path.getPathIterator(null);
		float lastX = 0;
		float lastY = 0;
		int xarc = 0;
		while(!i.isDone()) {
			int k = i.currentSegment(coords);
			if(k==PathIterator.SEG_CUBICTO) {
				xarc = Math.max(xarc, (int)(Math.abs(coords[4]-lastX)+.5));
				lastX = coords[4];
				lastY = coords[5];
			} else if(k==PathIterator.SEG_QUADTO) {
				xarc = Math.max(xarc, (int)(Math.abs(coords[2]-lastX)+.5));
				lastX = coords[2];
				lastY = coords[3];
			} else if(k==PathIterator.SEG_LINETO || k==PathIterator.SEG_MOVETO) {
				lastX = coords[0];
				lastY = coords[1];
			}
			i.next();
		}
		return xarc;
	}
	
	/** Returns insets that represent the max inset field of each inset provided. */
	protected static Insets max(Insets i1,Insets i2) {
		return new Insets(
				Math.max(i1.top, i2.top),
				Math.max(i1.left, i2.left),
				Math.max(i1.bottom, i2.bottom),
				Math.max(i1.right, i2.right)	
		);
	}
	
	final FilledButtonUI buttonUI;

	protected Rectangle currentValueBounds;
	
	MouseInputAdapter mouseListener = new MouseInputAdapter() {

		@Override
		public void mouseMoved(MouseEvent e) {
			mouseEntered(e);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			if(!Boolean.TRUE.equals(rendererPanel.getClientProperty(FilledButtonUI.ROLLOVER))) {
				rendererPanel.putClientProperty(FilledButtonUI.ROLLOVER, Boolean.TRUE);
				arrowButton.putClientProperty(FilledButtonUI.ROLLOVER, Boolean.TRUE);
				comboBox.repaint();
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if(Boolean.TRUE.equals(rendererPanel.getClientProperty(FilledButtonUI.ROLLOVER))) {
				rendererPanel.putClientProperty(FilledButtonUI.ROLLOVER, null);
				arrowButton.putClientProperty(FilledButtonUI.ROLLOVER, null);
				comboBox.repaint();
			}
		}
	};

	FocusListener focusRenderListener = new FocusListener() {
		public void focusGained(FocusEvent e) {
			arrowButton.putClientProperty(FilledButtonUI.HAS_FOCUS, Boolean.TRUE);
			comboBox.repaint();
		}

		public void focusLost(FocusEvent e) {
			arrowButton.putClientProperty(FilledButtonUI.HAS_FOCUS, null);
			comboBox.repaint();
		}
	};
	
	PropertyChangeListener showSeparatorsPropertyListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent e) {
			Boolean v = (Boolean)comboBox.getClientProperty(FilledButtonUI.SHOW_SEPARATORS);
			if(v==null) v = Boolean.FALSE;
			arrowButton.putClientProperty(FilledButtonUI.SHOW_SEPARATORS, v);
			rendererPanel.putClientProperty(FilledButtonUI.SHOW_SEPARATORS, v);
		}
	};

	PropertyChangeListener popDownPropertyListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent e) {
			configureArrowButton();
		}
	};
	
	/** This is used to render the background, but is not part of the Swing hierarchy. */
	JButton rendererPanel = new JButton();
	
	/** Listen to the parent Window, and when that window is deactivated:
	 * hide the popup.
	 * <p>This is in response to a bug with the BasicComboBoxUI (or with
	 * the layering system of the popup?): when you have a popup visible
	 * and switch applications, then the popup stays visible (floating)
	 * on top of all other windows. Another response to this issue might
	 * be to change the z-order the popup floats in (so it is still
	 * visible when you return to your original app), but this solution
	 * works too.
	 */
	HierarchyListener hierarchyListener = new HierarchyListener() {
		WeakReference<Window> registeredWindow = null;
		WindowListener windowListener = new WindowAdapter() {
			@Override
			public void windowDeactivated(WindowEvent e) {
				if(FilledComboBoxUI.this.popup!=null)
					FilledComboBoxUI.this.setPopupVisible(comboBox, false);
			}
		};
		
		public void hierarchyChanged(HierarchyEvent e) {
			Window window = SwingUtilities.getWindowAncestor(comboBox);
			Window lastWindow = registeredWindow==null ? null : registeredWindow.get();
			if(window!=null && window!=lastWindow) {
				if(lastWindow!=null) {
					lastWindow.removeWindowListener(windowListener);
				}
				registeredWindow = new WeakReference<Window>(window);
				window.addWindowListener(windowListener);
			}
		}	
	};

	protected FilledComboBoxUI() {
		FilledButtonUI k = null;
		try {
			String ui = (String)UIManager.getDefaults().get("ButtonUI");
			Class<?> t = Class.forName(ui);
			if(FilledButtonUI.class.isAssignableFrom(t)) {
				k = (FilledButtonUI)t.newInstance();
			}
		} catch(Throwable t) {
			t.printStackTrace();
		}
		if(k==null) {
			k = new RoundRectButtonUI();
		}
		this.buttonUI = initialize(k);
	}
	
	public FilledComboBoxUI(FilledButtonUI buttonUI) {
		this.buttonUI = initialize(buttonUI);
	}
	
	private FilledButtonUI initialize(FilledButtonUI buttonUI) {
		rendererPanel.setUI(buttonUI);
		rendererPanel.putClientProperty( FilledButtonUI.HORIZONTAL_POSITION, FilledButtonUI.LEFT);
		return buttonUI;
	}

	@Override
	public void configureArrowButton() {
		super.configureArrowButton();
		arrowButton.setUI(buttonUI);
		Icon icon = isPopDown() ? downIcon : upAndDownIcon;
		arrowButton.setIcon(icon);
		arrowButton.putClientProperty( FilledButtonUI.HORIZONTAL_POSITION, FilledButtonUI.RIGHT);
	}

	@Override
	protected JButton createArrowButton() {
		JButton button = new JButton();
		button.addMouseMotionListener(mouseListener);
		button.addMouseListener(mouseListener);
		button.setName("ComboBox.arrowButton");
		button.addFocusListener(focusRenderListener);
		return button;
	}
	
	@Override
	protected ComboPopup createPopup() {
		return new BasicComboPopup(comboBox) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void configurePopup() {
				super.configurePopup();
				Paint c = buttonUI.getButtonFill().getStroke(null, null);
				if(c instanceof Color)
					setBorder(new LineBorder( (Color)c ));
			}

			@Override
			public void show() {
				if(isPopDown()) {
					super.show();
					return;
				}

				if(list.getModel().getSize()==0)
					return;
				
				int i = comboBox.getSelectedIndex();
				Rectangle cellBounds = null;
				if(i==-1) {
					list.clearSelection();
					cellBounds = list.getUI().getCellBounds(list, 0, 0);
				} else {
					list.setSelectedIndex(i);
					list.ensureIndexIsVisible(i);
					cellBounds = list.getUI().getCellBounds(list, i, i);
				}
				cellBounds = SwingUtilities.convertRectangle(list, cellBounds, scroller);
				
				Dimension popupSize = comboBox.getSize();
				Insets insets = comboBox.getInsets();
				
				popupSize.setSize(popupSize.width - (insets.right + insets.left),
		                          getPopupHeightForRowCount( comboBox.getMaximumRowCount()));
				int width = currentValueBounds==null ? comboBox.getWidth() : currentValueBounds.width;
		        Rectangle popupBounds = computePopupBounds( 0, comboBox.getBounds().height,
		                                                    width, popupSize.height);
		        Dimension scrollSize = popupBounds.getSize();
		        scroller.setMaximumSize( scrollSize );
		        scroller.setPreferredSize( scrollSize );
		        scroller.setMinimumSize( scrollSize );
				
				Point location = new Point();
				location.y = currentValueBounds.y - cellBounds.y + 2;
				location.x = currentValueBounds.x - cellBounds.x - 1;
				show( comboBox, location.x, location.y );
			}
		};
	}
	
	/** @return the {@link com.pump.plaf.ButtonFill} used by this FilledComboBoxUI. */
	public ButtonFill getButtonFill() {
		return buttonUI.getButtonFill();
	}

	/** @return the {@link com.pump.plaf.ButtonShape} used by this FilledComboBoxUI. */
	public ButtonShape getButtonShape() {
		return buttonUI.getButtonShape();
	}

	@Override
	protected Dimension getDisplaySize() {
		Dimension d = super.getDisplaySize();
		GeneralPath path = new GeneralPath();
		getButtonShape().getShape(path, null, d.width, d.height, FilledButtonUI.POS_LEFT, FilledButtonUI.POS_ONLY, true, null);
		int arcLeft = getXArc(path);
		path.reset();
		getButtonShape().getShape(path, null, d.width, d.height, FilledButtonUI.POS_RIGHT, FilledButtonUI.POS_ONLY, true, null);

		d.width += arcLeft;

		Insets i1 = buttonUI.getIconPadding();
		Insets i2 = buttonUI.getTextPadding();
		Insets max = max(i1, i2);

		d.width += max.left + max.right;
		d.height += max.top + max.bottom;

		d.width += buttonUI.focusSize*2;
		d.height += buttonUI.focusSize*2;

		return d;
	}

	@Override
	protected void installComponents() {
		arrowButton = createArrowButton();
		comboBox.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1; c.gridy = 0;
		c.weightx = 0; c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		comboBox.add(arrowButton, c);
		if(arrowButton!=null) {
			configureArrowButton();
		}
		if(comboBox.isEditable()) {
			addEditor();
		}
		c.gridx = 0; c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;
		comboBox.add( currentValuePane, c );
	}

	@Override
	protected void installListeners() {
		super.installListeners();
		comboBox.addFocusListener(focusRenderListener);
		comboBox.addMouseMotionListener(mouseListener);
		comboBox.addMouseListener(mouseListener);
		comboBox.addPropertyChangeListener(IS_POP_DOWN_KEY, popDownPropertyListener);
		comboBox.addPropertyChangeListener(FilledButtonUI.SHOW_SEPARATORS, showSeparatorsPropertyListener);
		comboBox.addHierarchyListener(hierarchyListener);
		comboBox.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				String name = evt.getPropertyName();
				if("renderer".equals(name) || "enabled".equals(name)) {
					updateRenderer();
				}
			}
		});
		updateRenderer();
	}
		
	protected void updateRenderer() {
		// For some reason the default renderer will want to paint the list cell
		// with an opaque white background when the combobox is disabled.
		ListCellRenderer renderer = comboBox.getRenderer();
		if(renderer instanceof JComponent) {
			((JComponent)renderer).setOpaque( comboBox.isEnabled() );
		}
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);

		//an unpleasant quirk of the BasicComboBoxUI:
		//the background is always used for the renderer.
		//if we set the background to a transparent color
		//then we get the expected results. (This is not
		//an ideal implementation, but it works for now.)
		comboBox.setBackground(new Color(0,0,0,0));
		comboBox.setOpaque(false);
		
		showSeparatorsPropertyListener.propertyChange(null);
		c.setBorder(null);
	}

	/** A combobox can either pop <i>down</i> (below the combobox),
	 * or it can pop <i>up</i> (or "over"). The default implementation
	 * for the BasicComboBoxUI is down, but this default implementation for
	 * this class is up. You can customize this by
	 * assigning the client property "JComboBox.isPopDown" to a Boolean.
	 * <p>The following excerpt from Apple's technical note 2196 explains
	 * when to use each:
	 * <p>This property alters the JComboBox's style to specify if it is 
	 * intended to be a pop-down or a pop-up control. Pop-downs should be 
	 * used when the user is expected to choose an action from the pop-down 
	 * menu. Pop-ups should be used when the user is expected to make a 
	 * choice from the pop-up menu that does not cause an action like a 
	 * button would. Pop-up menus always appear over the pop-up control, 
	 * whereas pop-down menus always appear below the pop-down control.
	 * 
	 * @see <a href="https://developer.apple.com/library/mac/#technotes/tn2007/tn2196.html">Apple Tech Note 2196</a>
	 */
	protected boolean isPopDown() {
		return Boolean.TRUE.equals(comboBox.getClientProperty(IS_POP_DOWN_KEY));
	}

	/**
	 * Paints the currently selected item.
	 */
	public void paintCurrentValue(Graphics g,Rectangle bounds,boolean hasFocus) {
		ButtonShape shape = getButtonShape();
		GeneralPath path = new GeneralPath();
		shape.getShape(path, null, bounds.width, bounds.height, FilledButtonUI.POS_LEFT, FilledButtonUI.POS_ONLY, true, null);

		int arc = getXArc(path);
		bounds = new Rectangle(bounds);
		bounds.x += arc;
		bounds.width -= arc;

		Insets i1 = buttonUI.getIconPadding();
		Insets i2 = buttonUI.getTextPadding();
		Insets max = max(i1, i2);
		//don't fuss about padding if the arc is generous, though:
		max.left = Math.max(0, max.left - arc);
		max.right = Math.max(0, max.right - arc);

		bounds.x += max.left;
		bounds.width -= max.left + max.right;
		bounds.y += max.top;
		bounds.height -= max.top + max.bottom;

		bounds.x += buttonUI.focusSize;
		
		currentValueBounds = new Rectangle(bounds);
		
		super.paintCurrentValue(g, bounds, false);
	}

	@Override
	public void paintCurrentValueBackground(Graphics g, Rectangle bounds,
			boolean hasFocus) {
		Graphics2D currentG = (Graphics2D)g.create();
		currentG.clipRect(bounds.x, bounds.y, bounds.width, bounds.height);

		rendererPanel.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
		if(arrowButton.hasFocus() || comboBox.hasFocus()) {
			hasFocus = true;
		}
		rendererPanel.putClientProperty(FilledButtonUI.HAS_FOCUS, hasFocus);
		rendererPanel.paint(currentG);

		currentG.dispose();
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();
		comboBox.removeFocusListener(focusRenderListener);
		comboBox.removeMouseMotionListener(mouseListener);
		comboBox.removeMouseListener(mouseListener);
		comboBox.removePropertyChangeListener(FilledButtonUI.SHOW_SEPARATORS, showSeparatorsPropertyListener);
		comboBox.removePropertyChangeListener(IS_POP_DOWN_KEY, popDownPropertyListener);
		comboBox.removeHierarchyListener(hierarchyListener);
	}

	/** Return the buttons used to render this ComboBoxUI.
	 * Note one of these buttons isn't really part of the Swing component hierarchy, but it is used for rendering.
	 * @return the buttons used to render this ComboBoxUI.
	 */
	public JButton[] getButtons()
	{
		if(arrowButton==null)
			System.err.println("getButtons() should only be called after installUI(..)");
		return new JButton[] { arrowButton, rendererPanel };
	}
}