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
package com.pump.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;

import javax.swing.JColorChooser;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.ComponentUI;

import com.pump.awt.ColorUtils;
import com.pump.plaf.MultiThumbSliderUI;

/**
 * This component lets the user manipulate the colors in a gradient. A
 * <code>GradientSlider</code> can contain any number of thumbs. The slider
 * itself represents a range of values from zero to one, so the thumbs must
 * always be within this range. Each thumb maps to a specific <code>Color</code>
 * .
 * <P>
 * There are some specific properties you can set to customize the look-and-feel
 * of this slider in the default {@link com.pump.plaf.GradientSliderUI} class.
 * <P>
 * The UI for each slider is loaded from the UIManager property:
 * "GradientSliderUI". By default this is "com.pump.plaf.GradientSliderUI".
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/gradientslider.jpg"
 * alt="Screenshot of the GradientSlider">
 * 
 * @see <a
 *      href="https://javagraphics.blogspot.com/2008/05/gradients-gui-widget-to-design-them.html">Gradients:
 *      a GUI Widget to Design Them</a>
 *
 */
public class JGradientSlider extends MultiThumbSlider<Color> {

	private static final long serialVersionUID = 1L;

	static {
		if (UIManager.getString("GradientSliderUI") == null)
			UIManager.put("GradientSliderUI", "com.pump.plaf.GradientSliderUI");
	}

	/**
	 * Create a horizontal <code>GradientSlider</code> that represents a
	 * gradient from white to black.
	 */
	public JGradientSlider() {
		this(HORIZONTAL);
	}

	/**
	 * Create a <code>GradientSlider</code> that represents a gradient form
	 * white to black.
	 * 
	 * @param orientation
	 *            HORIZONTAL or VERTICAL
	 */
	public JGradientSlider(int orientation) {
		this(orientation, new float[] { 0f, 1f }, new Color[] { Color.white,
				Color.black });
	}

	/**
	 * Create a new <code>GradientSlider</code>.
	 * 
	 * @param orientation
	 *            HORIZONTAL or VERTICAL
	 * @param thumbPositions
	 *            the initial positions of each thumb
	 * @param values
	 *            the initial colors at each position
	 * @throws IllegalArgumentException
	 *             if the number of elements in <code>thumbPositions</code> does
	 *             not equal the number of elements in <code>values</code>.
	 * 
	 */
	public JGradientSlider(int orientation, float[] thumbPositions,
			Color[] values) {
		super(orientation, thumbPositions, values);
	}

	/**
	 * @return the Color at the specified position.
	 */
	@Override
	public Color createValueForInsertion(float pos) {
		for (int a = 0; a < thumbPositions.length - 1; a++) {
			if (thumbPositions[a] <= pos && pos <= thumbPositions[a + 1]) {
				float v = (pos - thumbPositions[a])
						/ (thumbPositions[a + 1] - thumbPositions[a]);
				return ColorUtils.tween((Color) values[a],
						(Color) values[a + 1], v);
			}
		}
		if (pos < thumbPositions[0]) {
			return values[0];
		}
		if (pos > thumbPositions[thumbPositions.length - 1]) {
			return values[values.length - 1];
		}
		return null;
	}

	/**
	 * This invokes a <code>JColorPicker</code> dialog to edit the thumb at the
	 * selected index.
	 * 
	 */
	@Override
	public boolean doDoubleClick(int x, int y) {
		int i = getSelectedThumb();
		if (i != -1) {
			showColorPicker();
			// showJColorChooser();
			SwingUtilities.invokeLater(new SelectThumbRunnable(i));
			return true;
		} else {
			return false;
		}
	}

	class SelectThumbRunnable implements Runnable {
		int index;

		public SelectThumbRunnable(int i) {
			index = i;
		}

		public void run() {
			setSelectedThumb(index);
		}
	}

	/** The popup for contextual menus. */
	JPopupMenu popup;

	private JPopupMenu createPopup() {
		return new ColorPickerPopup();
	}

	abstract class AbstractPopup extends JPopupMenu {
		private static final long serialVersionUID = 1L;

		int lastSelectedThumb;

		PopupMenuListener popupMenuListener = new PopupMenuListener() {
			public void popupMenuCanceled(PopupMenuEvent e) {
				setValueIsAdjusting(false);
				SwingUtilities.invokeLater(new SelectThumbRunnable(
						lastSelectedThumb));
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				setValueIsAdjusting(false);
				SwingUtilities.invokeLater(new SelectThumbRunnable(
						lastSelectedThumb));
			}

			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				setValueIsAdjusting(true);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						getFocusableComponent().requestFocus();
					}
				});
			}
		};

		public AbstractPopup() {
			addPopupMenuListener(popupMenuListener);
		}

		public abstract Component getFocusableComponent();

		@Override
		public void show(Component c, int x, int y) {
			Color[] colors = getValues();
			lastSelectedThumb = getSelectedThumb();
			if (lastSelectedThumb != -1) {
				setColor(colors[lastSelectedThumb]);
				super.show(c, x, y);
			}
		}

		public abstract void setColor(Color c);
	}

	class ColorPickerPopup extends AbstractPopup {
		private static final long serialVersionUID = 1L;

		JColorPicker mini;
		KeyListener commitListener = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE
						|| e.getKeyCode() == KeyEvent.VK_ENTER) {
					ColorPickerPopup.this.setVisible(false);
				}
			}
		};

		public ColorPickerPopup() {
			super();
			boolean includeOpacity = MultiThumbSliderUI.getProperty(
					JGradientSlider.this, "GradientSlider.includeOpacity",
					"true").equals("true");

			mini = new JColorPicker(false, includeOpacity);
			mini.setMode(JColorPicker.HUE);
			mini.setPreferredSize(new Dimension(220, 200));
			PropertyChangeListener p = new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					JColorPicker p = (JColorPicker) evt.getSource();
					Color[] colors = getValues();
					colors[lastSelectedThumb] = p.getColor();
					setValues(getThumbPositions(), colors);
				}
			};
			mini.addPropertyChangeListener(
					JColorPicker.SELECTED_COLOR_PROPERTY, p);
			mini.addPropertyChangeListener(JColorPicker.OPACITY_PROPERTY, p);
			for (int a = 0; a < mini.getComponentCount(); a++) {
				Component c = mini.getComponent(a);
				c.addKeyListener(commitListener);
			}
			add(mini);
		}

		@Override
		public Component getFocusableComponent() {
			return mini.getColorPanel();
		}

		@Override
		public void setColor(Color c) {
			mini.setRGB(c.getRed(), c.getGreen(), c.getBlue());
			mini.setOpacity(c.getAlpha());
		}
	}

	/**
	 * This shows a mini JColorPicker panel to let the user change the selected
	 * color.
	 */
	@Override
	public boolean doPopup(int x, int y) {
		if (popup == null) {
			popup = createPopup();
		}
		popup.show(this, x, y);
		return true;
	}

	private Frame getFrame() {
		Window w = SwingUtilities.getWindowAncestor(this);
		if (w instanceof Frame)
			return ((Frame) w);
		return null;
	}

	private boolean showColorPicker() {
		Color[] colors = getValues();
		int i = getSelectedThumb();

		Frame frame = getFrame();

		boolean includeOpacity = MultiThumbSliderUI.getProperty(this,
				"GradientSlider.colorPickerIncludesOpacity", "true").equals(
				"true");
		colors[i] = JColorPicker.showDialog(frame, colors[i], includeOpacity);
		if (colors[i] != null)
			setValues(getThumbPositions(), colors);
		return true;
	}

	/**
	 * TODO: If developers don't want to bundle the JColorPicker with their
	 * programs, they can use this method instead of
	 * <code>showColorPicker()</code>.
	 */
	@SuppressWarnings("unused")
	private void showJColorChooser() {
		Color[] colors = getValues();
		int i = getSelectedThumb();
		if (i >= 0 && i < colors.length) {
			colors[i] = JColorChooser.showDialog(this, "Choose a Color",
					colors[i]);
			if (colors[i] != null)
				setValues(getThumbPositions(), colors);
		}
	}

	@Override
	public void updateUI() {
		String name = UIManager.getString("GradientSliderUI");
		if (name == null)
			name = "com.pump.plaf.GradientSliderUI";
		try {
			Class<?> c = Class.forName(name);
			Constructor<?>[] constructors = c.getConstructors();
			for (int a = 0; a < constructors.length; a++) {
				Class<?>[] types = constructors[a].getParameterTypes();
				if (types.length == 1 && types[0].equals(JGradientSlider.class)) {
					ComponentUI ui = (ComponentUI) constructors[a]
							.newInstance(new Object[] { this });
					setUI(ui);
					return;
				}
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("The class \"" + name
					+ "\" could not be found.");
		} catch (Throwable t) {
			RuntimeException e = new RuntimeException("The class \"" + name
					+ "\" could not be constructed.");
			e.initCause(t);
			throw e;
		}
	}

	public void setMinimumThumbnailCount(int i) {
		if (i < 2)
			throw new IllegalArgumentException(
					"The GradientSlider must have at least two thumbnails ("
							+ i + ")");
		putClientProperty(THUMB_MINIMUM_PROPERTY, i);
	}

	public int getMinimumThumbnailCount() {
		Integer i = (Integer) getClientProperty(THUMB_MINIMUM_PROPERTY);
		if (i == null)
			return 2;
		return i;
	}
}