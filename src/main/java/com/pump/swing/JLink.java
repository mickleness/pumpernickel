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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.io.HTMLEncoding;
import com.pump.util.JVM;

/**
 * This button resembles a hyperlink.
 * <P>
 * A dotted border is displayed around this component when it has the keyboard
 * focus.
 *
 */
public class JLink extends JButton {
	private static final long serialVersionUID = 1L;

	private static String formatHtmlUnderline(String text) {
		return "<html><p style=\"text-decoration: underline\">"
				+ HTMLEncoding.encode(text) + "</p></html>";
	}

	/**
	 * This is a 1-pixel dotted border.
	 */
	static class DottedLineBorder implements Border {
		Color color;

		public DottedLineBorder(Color c) {
			this.color = c;
		}

		public Insets getBorderInsets(Component c) {
			return new Insets(1, 1, 1, 1);
		}

		public boolean isBorderOpaque() {
			return false;
		}

		public void paintBorder(Component c, Graphics g, int x, int y,
				int width, int height) {
			((Graphics2D) g).setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_BEVEL, 10, new float[] { 1, 1 }, 0));
			g.drawRect(x, y, width - 1, height - 1);
		}
	}

	static Border unfocusedBorder = new EmptyBorder(3, 3, 3, 3);
	static Border focusedBorder = new CompoundBorder(new DottedLineBorder(
			new Color(0, 0, 150)), new EmptyBorder(2, 2, 2, 2));

	private static boolean isVista() {
		String osName = System.getProperty("os.name").toLowerCase();
		return (osName.indexOf("vista") != -1);
	}

	private Color defaultColor = isVista() ? new Color(0, 102, 204)
			: Color.blue;
	private Color indicatedColor = new Color(0, 0, 100);
	private Color selectedColor = Color.black;

	/** Create a new, empty JLink. */
	public JLink() {
		super();
		initialize();
	}

	/** Create a JLink presenting the text provided. */
	public JLink(String text) {
		this(text, null);
	}

	/** Create a JLink presenting the text provided. */
	public JLink(String text, final URL url) {
		super(formatHtmlUnderline(text));
		getAccessibleContext().setAccessibleName(text);
		initialize();
		if (url != null) {
			setToolTipText(url.toString());
			getAccessibleContext().setAccessibleDescription(url.toString());
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						Desktop.getDesktop().browse(url.toURI());
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			});
		}
	}

	/** Adds the necessary properties/listeners. */
	private void initialize() {
		setRequestFocusEnabled(false);
		addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				//TODO: explore borders a little bit more. Why does a dotted border
				//automatically appear on Windows but not on Mac?
				setBorder(JVM.isMac ? focusedBorder : unfocusedBorder);
			}

			public void focusLost(FocusEvent e) {
				setBorder(unfocusedBorder);
			}
		});
		setFocusable(true);
		setBorder(unfocusedBorder);
		setForeground(defaultColor);
		setContentAreaFilled(false);

		getModel().addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				Color currentColor = getDefaultColor();
				if (getModel().isPressed() || getModel().isSelected()) {
					currentColor = getSelectedColor();
				} else if (getModel().isArmed() || getModel().isRollover()) {
					currentColor = getIndicatedColor();
				}
				setForeground(currentColor);
			}

		});
	}

	public void setDefaultColor(Color c) {
		defaultColor = c;
		repaint();
	}

	public void setIndicatedColor(Color c) {
		indicatedColor = c;
		repaint();
	}

	public void setSelectedColor(Color c) {
		selectedColor = c;
		repaint();
	}

	public Color getDefaultColor() {
		return defaultColor;
	}

	public Color getIndicatedColor() {
		return indicatedColor;
	}

	public Color getSelectedColor() {
		return selectedColor;
	}
}