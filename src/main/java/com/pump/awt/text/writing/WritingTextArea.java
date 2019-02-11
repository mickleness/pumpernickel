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
package com.pump.awt.text.writing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.plaf.PanelUI;

/**
 * A crude text box that displays text using a WritingFont. If selected: there
 * is not currently a blinking cursor, but you can type and delete text. Also
 * this offers a control to play back this text in an animation.
 */
public class WritingTextArea extends JPanel {
	private static final long serialVersionUID = 1L;

	public static final String PREFERRED_WIDTH = WritingTextArea.class
			.getName() + ".preferredWidth";
	public static final String TIME = WritingTextArea.class.getName() + ".time";
	public static final String DURATION = WritingTextArea.class.getName()
			+ ".duration";

	class WritingUI extends PanelUI {

		MouseAdapter mouseListener = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				requestFocus();
			}
		};

		KeyAdapter keyListener = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				char ch = e.getKeyChar();
				if (e.getKeyCode() == KeyEvent.VK_DELETE
						|| e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					String s = layout.getText();
					if (s.length() > 0) {
						s = s.substring(0, s.length() - 1);
						layout.setText(s);
					} else {
						Toolkit.getDefaultToolkit().beep();
					}
				} else if (Character.isDefined(ch)) {
					layout.append(ch);
				}
			}
		};

		PropertyChangeListener layoutListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				repaint();
			}
		};

		@Override
		public void installUI(JComponent c) {
			super.installUI(c);
			c.addKeyListener(keyListener);
			c.addMouseListener(mouseListener);
			layout.addPropertyChangeListener(layoutListener);
		}

		@Override
		public void uninstallUI(JComponent c) {
			super.uninstallUI(c);
			c.removeKeyListener(keyListener);
			c.removeMouseListener(mouseListener);
			layout.removePropertyChangeListener(layoutListener);
		}

		@Override
		public void paint(Graphics g, JComponent c) {
			super.paint(g, c);
			layout.paint((Graphics2D) g,
					new Rectangle(0, 0, c.getWidth(), c.getHeight()),
					getTime(), c.getForeground());
		}

		@Override
		public Dimension getPreferredSize(JComponent c) {
			int w = getPreferredWidth();
			int preferredHeight = layout.getHeight(w);
			return new Dimension(w, preferredHeight);
		}

		@Override
		public Dimension getMinimumSize(JComponent c) {
			return getPreferredSize(c);
		}

		@Override
		public Dimension getMaximumSize(JComponent c) {
			Dimension d = getPreferredSize(c);
			d.width = Integer.MAX_VALUE;
			return d;
		}

	}

	WritingTextLayout layout = new WritingTextLayout();

	public WritingTextArea(int preferredWidth, String text) {
		this(preferredWidth);
		getWritingLayout().setText(text);
	}

	public WritingTextArea(int preferredWidth) {
		setPreferredWidth(preferredWidth);
		setUI(new WritingUI());
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				layout.invalidate();
			}
		});
		addHierarchyListener(new HierarchyListener() {
			public void hierarchyChanged(HierarchyEvent e) {
				if (!isShowing()) {
					layout.invalidate();
				}
			}
		});
	}

	public void setPreferredWidth(int preferredWidth) {
		putClientProperty(PREFERRED_WIDTH, preferredWidth);
	}

	public int getPreferredWidth() {
		Number n = (Number) getClientProperty(PREFERRED_WIDTH);
		if (n == null)
			return 100;
		return n.intValue();
	}

	public void setTime(float time) {
		putClientProperty(TIME, time);
	}

	public float getTime() {
		Number n = (Number) getClientProperty(TIME);
		if (n == null)
			return -1;
		return n.floatValue();
	}

	public WritingTextLayout getWritingLayout() {
		return layout;
	}
}