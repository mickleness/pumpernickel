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
package com.pump.showcase.demo;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.inspector.Inspector;
import com.pump.swing.JColorWell;
import com.pump.swing.JEyeDropper;

/**
 * This demos the JEyeDropper.
 * <p>
 * Here is a sample screenshot of this showcase demo:
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/showcase/JEyeDropperDemo.png"
 * alt="A screenshot of the JEyeDropperDemo.">
 */
public class JEyeDropperDemo extends ShowcaseExampleDemo {
	private static final long serialVersionUID = 1L;

	JButton showEyeDropper = new JButton("Show Eyedropper");
	JSpinner diameterSpinner = new JSpinner(
			new SpinnerNumberModel(120, 20, 400, 10));
	JSpinner magSpinner = new JSpinner(new SpinnerNumberModel(10,
			JEyeDropper.MAGNIFICATION_MIN, JEyeDropper.MAGNIFICATION_MAX, 1));
	JColorWell colorWell = new JColorWell();

	public JEyeDropperDemo() {
		super(true, true, false);
		exampleLabel.setVisible(false);
		showEyeDropper.setOpaque(false);

		Inspector layout = new Inspector(configurationPanel);
		layout.addRow(new JLabel("Diameter:"), diameterSpinner);
		layout.addRow(new JLabel("Magnification:"), magSpinner);
		layout.addRow(new JLabel("Color:"), colorWell);
		layout.addRow(showEyeDropper, false);

		showEyeDropper.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// wait for the button to fully
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						// we need the eyedropper to capture the correct
						// image,
						// so we need to:

						// step 1: remove the focus ring
						KeyboardFocusManager.getCurrentKeyboardFocusManager()
								.clearGlobalFocusOwner();

						// step 2: repaint immediately
						showEyeDropper.paintImmediately(0, 0,
								showEyeDropper.getWidth(),
								showEyeDropper.getHeight());

						showEyeDropper();
					}
				});
			}
		});
		colorWell.getColorSelectionModel().setSelectedColor(Color.black);
		colorWell.setEnabled(false);
	}

	protected void showEyeDropper() {
		try {
			JFrame owner = (JFrame) SwingUtilities
					.getWindowAncestor(showEyeDropper);
			int diameter = (int) diameterSpinner.getValue();
			final JEyeDropper d = new JEyeDropper(owner, diameter);
			d.getButton().putClientProperty(JEyeDropper.PROPERTY_PIXEL_SIZE,
					magSpinner.getValue());
			d.getButton().addPropertyChangeListener(
					JEyeDropper.PROPERTY_PIXEL_SIZE,
					new PropertyChangeListener() {

						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							magSpinner.setValue(evt.getNewValue());
						}

					});
			ChangeListener changeListener = new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					Color color = d.getModel().getSelectedColor();
					colorWell.getColorSelectionModel().setSelectedColor(color);
				}

			};
			d.getModel().addChangeListener(changeListener);
			d.setVisible(true);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getTitle() {
		return "JEyeDropper Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates a new Swing component that lets you select a color from the current screen.";
	}

	@Override
	public URL getHelpURL() {
		return JEyeDropperDemo.class.getResource("jeyeDropperDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "color", "ux", "gui", "dialog", "eyedropper",
				"Swing" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { JEyeDropper.class };
	}

}