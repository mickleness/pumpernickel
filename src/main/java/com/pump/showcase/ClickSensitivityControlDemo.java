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
package com.pump.showcase;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.MouseInputAdapter;

import com.pump.awt.ClickSensitivityControl;
import com.pump.awt.TextBlock;
import com.pump.swing.BasicConsole;

/**
 * A simple demo program showing off the {@link ClickSensitivityControl}.
 * <p>
 * Here is a sample screenshot of this showcase demo:
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/showcase/ClickSensitivityControlDemo.png"
 * alt="A screenshot of the ClickSensitivityControlDemo.">
 **/
public class ClickSensitivityControlDemo implements ShowcaseDemo {

	static class ClickSensitivityControlDemoPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		class Widget extends JComponent {
			private static final long serialVersionUID = 1L;
			TextBlock block;
			Point clickLoc = null;
			MouseInputAdapter mouseListener = new MouseInputAdapter() {

				@Override
				public void mouseDragged(MouseEvent e) {
					repaint();
				}

				@Override
				public void mousePressed(MouseEvent e) {
					clickLoc = e.getPoint();
					repaint();
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					clickLoc = null;
					repaint();
				}

			};

			Widget(Color c, String name) {
				setPreferredSize(new Dimension(120, 80));
				setMinimumSize(new Dimension(120, 80));
				block = new TextBlock(name);
				block.setShadowActive(false);
				block.setBackground(c);
				addMouseListener(mouseListener);
				addMouseMotionListener(mouseListener);
			}

			@Override
			protected void paintComponent(Graphics g0) {
				super.paintComponent(g0);
				Graphics2D g = (Graphics2D) g0.create();
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);

				Graphics2D g2 = (Graphics2D) g.create();
				g2.translate(getWidth() / 2 - block.getWidth() / 2, getHeight()
						/ 2 - block.getHeight() / 2);
				block.paint(g2);
				g2.dispose();

				if (clickLoc != null) {
					int r = ((Number) spinner.getValue()).intValue();
					Shape e = new Ellipse2D.Float(clickLoc.x - r, clickLoc.y
							- r, 2 * r, 2 * r);
					if (csc.isClick(this)) {
						g.setColor(new Color(0x6655ff55, true));
					} else {
						g.setColor(new Color(0x66ff5555, true));
					}
					g.fill(e);
				}

				g.dispose();
			}
		};

		MouseListener mouseListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String msg = getMessage(e);
				console.createPrintStream(new Color(0x220000ff, true)).println(
						msg);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				String msg = getMessage(e);
				console.createPrintStream(false).println(msg);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				String msg = getMessage(e);
				console.createPrintStream(false).println(msg);
			}

			private String getMessage(MouseEvent e) {
				String msg = "";
				if (e.getID() == MouseEvent.MOUSE_CLICKED) {
					msg = "MOUSE_CLICKED on ";
				} else if (e.getID() == MouseEvent.MOUSE_RELEASED) {
					msg = "MOUSE_RELEASED on ";
				} else if (e.getID() == MouseEvent.MOUSE_PRESSED) {
					msg = "MOUSE_PRESSED on ";
				} else {
					msg = "UNKNOWN on ";
				}
				if (e.getComponent() == widgetA) {
					msg += "A";
				} else if (e.getComponent() == widgetB) {
					msg += "B";
				} else {
					msg += "Unknown";
				}
				return msg;
			}
		};

		JSpinner spinner = new JSpinner(
				new SpinnerNumberModel(
						ClickSensitivityControl.DEFAULT_CLICK_EVENT_TOLERANCE,
						0, 20, 1));

		JComponent widgetA = new Widget(new Color(0xfad390), "Widget A");
		JComponent widgetB = new Widget(new Color(0x82ccdd), "Widget B");

		BasicConsole console = new BasicConsole(false, false);
		JScrollPane scrollPane = new JScrollPane(console);

		ClickSensitivityControl csc = new ClickSensitivityControl(
				ClickSensitivityControl.DEFAULT_CLICK_EVENT_TOLERANCE) {

			@Override
			public int getClickPixelTolerance(Component c) {
				if (c == widgetA)
					return 0;
				return ((Number) spinner.getValue()).intValue();
			}
		};

		public ClickSensitivityControlDemoPanel() {
			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();

			c.gridy++;
			c.gridx = 0;
			c.weightx = 0;
			c.weighty = 0;
			c.insets = new Insets(6, 6, 6, 6);
			c.gridwidth = 1;
			c.fill = GridBagConstraints.NONE;
			c.insets = new Insets(3, 3, 3, 3);
			add(new JLabel("Tolerance:"), c);
			c.gridx++;
			add(spinner, c);
			c.gridx++;
			c.anchor = GridBagConstraints.WEST;
			c.weightx = 1;
			add(new JLabel("Pixels"), c);

			JPanel targetRow = new JPanel(new FlowLayout());
			c.gridy++;
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 0;
			c.gridwidth = GridBagConstraints.REMAINDER;
			add(targetRow, c);

			c.gridy++;
			add(new JLabel("Events:"), c);

			c.insets = new Insets(6, 6, 6, 6);
			c.gridy++;
			c.weighty = 1;
			add(scrollPane, c);

			scrollPane.setPreferredSize(new Dimension(100, 100));

			targetRow.add(widgetA);
			targetRow.add(widgetB);

			// Normally you'd just call ClickSensitivityControl#install(), but
			// since
			// this demo includes code to disable this feature for widget A
			// we're on
			// our own.
			Toolkit.getDefaultToolkit().addAWTEventListener(
					csc,
					AWTEvent.MOUSE_EVENT_MASK
							+ AWTEvent.MOUSE_MOTION_EVENT_MASK);

			widgetA.addMouseListener(mouseListener);
			widgetB.addMouseListener(mouseListener);
		}
	}

	@Override
	public JPanel createPanel(PumpernickelShowcaseApp psa) {
		return new ClickSensitivityControlDemoPanel();
	}

	@Override
	public String getTitle() {
		return "ClickSensitivityControl Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates a mechanism to alter how MOUSE_CLICKED events are generated.\n\nBy default a MOUSE_CLICKED event is only generated if the mouse doesn't move between the MOUSE_PRESSED and MOUSE_RELEASED events. The ClickSensitivityControl generates MOUSE_CLICKED events even if the mouse moves a few pixels.\n\nTry clicking each widget below and move the mouse a few pixels before releasing it. Widget A only generates a MOUSE_CLICKED event if the mouse doesn't move. (This is what Java does by default.) Widget B allows a few pixels of wiggle room.";
	}

	@Override
	public URL getHelpURL() {
		return ClickSensitivityControlDemo.class
				.getResource("clickSensitivityControlDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "ui", "press", "click", "mouse" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { ClickSensitivityControl.class, MouseEvent.class };
	}
}