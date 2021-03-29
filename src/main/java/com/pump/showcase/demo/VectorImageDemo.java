package com.pump.showcase.demo;

import java.awt.AWTEvent;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.pump.graphics.Graphics2DContext;
import com.pump.graphics.vector.Operation;
import com.pump.graphics.vector.VectorGraphics2D;
import com.pump.graphics.vector.VectorImage;
import com.pump.graphics.vector.swing.VectorImageInspector;
import com.pump.showcase.app.PumpernickelShowcaseApp;
import com.pump.swing.DialogFooter;
import com.pump.swing.QDialog;

public class VectorImageDemo extends ShowcaseDemo {
	private static final long serialVersionUID = 1L;

	VectorImageInspector inspector = new VectorImageInspector();

	AWTEventListener keyListener = new AWTEventListener() {

		@Override
		public void eventDispatched(AWTEvent event) {
			KeyEvent e = (KeyEvent) event;
			if (e.getKeyCode() == KeyEvent.VK_F6
					&& e.getID() == KeyEvent.KEY_PRESSED) {
				PumpernickelShowcaseApp frame = null;
				for (Window w : Window.getWindows()) {
					if (w instanceof PumpernickelShowcaseApp)
						frame = (PumpernickelShowcaseApp) w;
				}
				VectorImage img = new VectorImage();
				img.getOperations().addListListener(
						new VectorImageInspector.StackTraceListener(), false);

				frame.paint(img.createGraphics());

				inspector.setVectorImage(img);
				cardLayout.show(cardPanel, "inspector");

				if (!VectorImageDemo.this.isShowing()) {
					JComponent content = QDialog.createContentPanel(
							"Would you like to switch to the VectorGraphics2D demo?",
							"Pressing F6 took a snapshot of the current window as a VectorGraphics2D.",
							null, // innerComponent,
							true);
					int option = QDialog.showDialog(frame,
							"Show VectorGraphics Demo",
							QDialog.QUESTION_MESSAGE, content, null, // leftControls,
							DialogFooter.OK_CANCEL_OPTION,
							DialogFooter.OK_OPTION, null, // dontShowKey,
							null, // alwaysApplyKey,
							DialogFooter.EscapeKeyBehavior.TRIGGERS_CANCEL);
					if (option == DialogFooter.OK_OPTION) {
						// TODO: update this
						// frame.showDemo(VectorImageDemo.this);
					}
				}
			}
		}
	};

	CardLayout cardLayout = new CardLayout();
	JPanel cardPanel = new JPanel(cardLayout);
	JPanel promptPanel = new JPanel(new GridBagLayout());

	public VectorImageDemo() {
		Toolkit.getDefaultToolkit().addAWTEventListener(keyListener,
				AWTEvent.KEY_EVENT_MASK);

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;
		add(cardPanel, c);

		cardPanel.add(promptPanel, "prompt");
		cardPanel.add(inspector, "inspector");

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 0;
		c.weightx = 0;
		c.insets = new Insets(5, 5, 5, 5);
		promptPanel.add(new JLabel(
				"Press F6 anywhere in this app to capture this Frame as a VectorImage."),
				c);
		c.gridy++;
		promptPanel.add(new JLabel(
				"(This panel will change to a VectorImageInspector when you do.)."),
				c);
	}

	@Override
	public String getTitle() {
		return "VectorImage Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates the VectorImage's ability to store Graphics2D operations.";
	}

	@Override
	public URL getHelpURL() {
		return VectorImageDemo.class.getResource("vectorImageDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "vector", "Graphics2D", "image", "serializable" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { VectorGraphics2D.class, VectorImage.class,
				Graphics2DContext.class, Operation.class };
	}

}
