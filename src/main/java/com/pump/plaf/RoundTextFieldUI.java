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
package com.pump.plaf;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.JTextComponent;

import com.pump.icon.SearchIcon;
import com.pump.plaf.button.ButtonShape;

/**
 * A <code>TextFieldUI</code> that features rounded horizontal endpoints.
 * <P>
 * Also if the <code>JTextField</code> has the client property "useSearchIcon"
 * defined, this may place a magnifying glass icon in the left edge of this UI
 * before the text.
 *
 * @see <a
 *      href="https://javagraphics.blogspot.com/2009/12/text-prompts-and-search-fields.html">Text:
 *      Prompts and Search Fields</a>
 */
public class RoundTextFieldUI extends BasicTextFieldUI {
	public final static ButtonShape ROUNDRECT_SHAPE = new ButtonShape(8,
			Short.MAX_VALUE);
	static Insets fieldInsets = new Insets(6, 6, 6, 6);

	JTextComponent editor;
	int focusPadding = 2;

	@Override
	public Dimension getMaximumSize(JComponent c) {
		// don't use the max height ever; it looks bizarre with a round rect
		// text field
		Dimension pref = super.getPreferredSize(c);
		Dimension max = super.getMaximumSize(c);

		return ROUNDRECT_SHAPE.getPreferredSize(null, max.width, pref.height,
				fieldInsets, null);
	}

	@Override
	public Dimension getMinimumSize(JComponent c) {
		Dimension d = super.getMinimumSize(c);
		return ROUNDRECT_SHAPE.getPreferredSize(null, d.width, d.height,
				fieldInsets, null);
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		Dimension d = super.getPreferredSize(c);
		return ROUNDRECT_SHAPE.getPreferredSize(null, d.width, d.height,
				fieldInsets, null);
	}

	static Color[] gradientColors = new Color[] { new Color(0x666666),
			new Color(0x999999) };
	static float[] gradientPositions = new float[] { 0, 1 };

	private GeneralPath path = new GeneralPath();
	private AffineTransform transform = new AffineTransform();
	private int radius = 0;
	private static Icon searchIcon = new SearchIcon(12);

	protected void updateGeometry() {
		Rectangle editorRect = getVisibleEditorRect();
		int iconExtra = includeSearchIcon() ? searchIcon.getIconWidth() : 0;
		ROUNDRECT_SHAPE.getShape(path, editorRect.width + 2 * radius
				+ iconExtra, editorRect.height);
		transform.setToTranslation(focusPadding, focusPadding);
		path.transform(transform);
	}

	protected boolean includeSearchIcon() {
		Object obj = editor.getClientProperty("useSearchIcon");
		if (obj != null)
			return obj.toString().equalsIgnoreCase("true");

		// the apple-tech-note-2196 key:
		obj = editor.getClientProperty("JTextField.variant");
		if ("search".equals(obj))
			return true;
		return false;
	}

	@Override
	protected void paintSafely(Graphics g) {
		updateGeometry();

		paintRealBackground(g);

		/**
		 * I really wish we could just completely replace this method, but it
		 * includes references to fields I don't have access to...
		 */
		super.paintSafely(g);
	}

	/**
	 * Does nothing. This will be called in super.paintSafely() if the text
	 * field is set to opaque. However we paint the real background in
	 * <code>paintRealBackground()</code>, which includes the opaque background,
	 * focus ring, and rounded border.
	 */
	@Override
	protected void paintBackground(Graphics g) {
	}

	protected void paintRealBackground(Graphics g0) {
		Graphics g = g0.create();
		Insets i = getComponent().getInsets();
		g.translate(i.left, i.top);
		if (editor.isOpaque()) {
			g.setColor(editor.getBackground());
			g.fillRect(0, 0, editor.getWidth(), editor.getHeight());
		}

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		if (editor.hasFocus()) {
			PlafPaintUtils.paintFocus(g2, path, focusPadding);
		}

		g2.setColor(editor.getBackground());
		g2.fill(path);

		Shape oldClip = g2.getClip();
		g2.clipRect(0, 0, editor.getWidth(), editor.getHeight() / 2);
		g2.translate(0, 1);
		g2.setPaint(new Color(0xBBBBBB));
		g2.draw(path);
		g2.translate(0, -1);
		g2.setClip(oldClip);
		if (editor.hasFocus() == false) {
			g2.clipRect(0, editor.getHeight() / 2, editor.getWidth(),
					editor.getHeight() / 2);
			g2.translate(0, 1);
			g2.setPaint(new Color(0x66FFFFFF, true));
			g2.draw(path);
			g2.translate(0, -1);
			g2.setClip(oldClip);
		}

		Rectangle editorRect = getVisibleEditorRect();
		g2.setPaint(new LinearGradientPaint(0, editorRect.height,
				0, focusPadding, gradientPositions,
				gradientColors));
		g2.draw(path);

		if (includeSearchIcon()) {
			searchIcon.paintIcon(
					editor,
					g0,
					editorRect.x - searchIcon.getIconWidth() - 4,
					editorRect.y + 1 + editorRect.height / 2
							- searchIcon.getIconHeight() / 2);
		}
	}

	protected static FocusListener focusListener = new FocusListener() {
		public void focusGained(FocusEvent e) {
			((Component) e.getSource()).repaint();
		}

		public void focusLost(FocusEvent e) {
			((Component) e.getSource()).repaint();
		}
	};

	protected static PropertyChangeListener iconListener = new PropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals("useSearchIcon")
					|| evt.getPropertyName().equals("JTextField.variant")) {
				JTextField tf = (JTextField) evt.getSource();
				tf.revalidate();
				tf.repaint();
			}
		}

	};

	@Override
	protected Rectangle getVisibleEditorRect() {
		Rectangle r = super.getVisibleEditorRect();
		if (r == null)
			return null;
		int left = r.x;
		int right = r.x + r.width;
		radius = (r.height - 2 * focusPadding) / 2;

		int dx = includeSearchIcon() ? searchIcon.getIconWidth() / 2 + 6 : 0;
		left += focusPadding + this.radius + dx;
		right -= focusPadding + this.radius;
		r.x = left;
		r.width = right - left;

		r.y += focusPadding;
		r.height -= 2 * focusPadding;

		return r;
	}

	@Override
	public void installUI(JComponent c) {
		editor = (JTextComponent) c;
		super.installUI(c);
		c.setBorder(null);
		c.setOpaque(false);
		c.addFocusListener(focusListener);
		editor.addPropertyChangeListener(iconListener);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		c.removeFocusListener(focusListener);
	}
}