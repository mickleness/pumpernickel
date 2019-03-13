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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import com.pump.awt.AnimatedLayout;
import com.pump.plaf.PlafPaintUtils;
import com.pump.plaf.TexturePaintPanelUI;
import com.pump.swing.PartialLineBorder;
import com.pump.swing.toolbar.CustomizedToolbar;
import com.pump.util.JVM;

/**
 * A demo app for the {@link CustomizedToolbar}.
 * <p>
 * Here is a sample screenshot of this showcase demo:
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/showcase/CustomizedToolbarDemo.png"
 * alt="A screenshot of the CustomizedToolbarDemo.">
 */
public class CustomizedToolbarDemo extends ShowcaseDemo {
	private static final long serialVersionUID = 1L;

	private static final String TOOLBAR_NAME = "toolbar demo";

	static {
		// normally you want your toolbar to be the same across multiple
		// sessions, but we want our demo to reset:
		CustomizedToolbar.resetPreferences(TOOLBAR_NAME);
	}

	JButton customize = new JButton("Customize...");
	// these are the components we [may] display in the toolbar:
	JComponent[] list = new JComponent[] { customize,
			new JCheckBox("Check box"), new JLabel("Label"),
			new JButton("Button"), new JSlider() };
	CustomizedToolbar toolbar = new CustomizedToolbar(list, new String[] { "0",
			"\t", "1" }, TOOLBAR_NAME);

	public CustomizedToolbarDemo() {

		for (int a = 0; a < list.length; a++) {
			list[a].setName("" + a); // give every component a unique name
			list[a].setOpaque(false);
		}

		ActionListener showCustomizeAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toolbar.displayDialog(JVM.isMac ? 350 : 280);
			}
		};
		customize.addActionListener(showCustomizeAction);

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(toolbar, c);
		c.gridy++;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		JPanel fluff = new JPanel();
		fluff.setBorder(new PartialLineBorder(Color.lightGray, new Insets(1, 0,
				0, 0)));
		fluff.setUI(new TexturePaintPanelUI(PlafPaintUtils.getCheckerBoard(3)));
		fluff.setOpaque(false);
		add(fluff, c);
	}

	@Override
	public String getTitle() {
		return "CustomizedToolbar Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates a model for managing/editing a toolbar based on Apple's editable toolbars.";
	}

	@Override
	public URL getHelpURL() {
		return CustomizedToolbarDemo.class
				.getResource("customizedToolbarDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "ux", "toolbar" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { CustomizedToolbar.class, AnimatedLayout.class };
	}
}