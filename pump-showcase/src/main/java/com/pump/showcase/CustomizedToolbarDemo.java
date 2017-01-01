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

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import com.pump.blog.Blurb;
import com.pump.plaf.PlafPaintUtils;
import com.pump.plaf.TexturePaintPanelUI;
import com.pump.swing.PartialLineBorder;
import com.pump.swing.toolbar.CustomizedToolbar;
import com.pump.util.JVM;

/** A demo app for the {@link CustomizedToolbar}.
 *
 */
@Blurb (
filename = "CustomizedToolbar",
title = "Customize Toolbar: Implementing Mac-Like Toolbars",
releaseDate = "June 2008",
summary = "In lots of Apple's software there's a handy menu option called \"Customize Toolbar\" that "+
"lets users drag and drop components in a toolbar.  It's functional <i>and</i> great eye candy.\n"+
"<p>This article presents a similar mechanism for Java.  A single thumbnail doesn't do justice to this feature, but "+
"if you go to the article you'll see a screencast.",
link = "http://javagraphics.blogspot.com/2008/06/customize-toolbar-implementing-mac-like.html",
sandboxDemo = true
)
public class CustomizedToolbarDemo extends JPanel {
	private static final long serialVersionUID = 1L;

	JButton customize = new JButton("Customize...");
	//these are the components we [may] display in the toolbar:
	JComponent[] list = new JComponent[] {
			customize,
			new JCheckBox("Check box"),
			new JLabel("Label"),
			new JButton("Button"),
			new JSlider()
	};
	CustomizedToolbar toolbar = new CustomizedToolbar(list,new String[] {"0","\t","1"},"toolbar demo");
	

	public CustomizedToolbarDemo() {

		for(int a = 0; a<list.length; a++) {
			list[a].setName( ""+a ); //give every component a unique name
			list[a].setOpaque(false);
		}

		ActionListener showCustomizeAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toolbar.displayDialog( JVM.isMac ? 350 : 280);
			}
		};
		customize.addActionListener(showCustomizeAction);

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0; c.weightx = 1; c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(toolbar, c);
		c.gridy++; c.weighty = 1; c.fill = GridBagConstraints.BOTH;
		JPanel fluff = new JPanel();
		fluff.setBorder(new PartialLineBorder(Color.DARK_GRAY, new Insets(1,0,0,0)));
		fluff.setUI(new TexturePaintPanelUI(PlafPaintUtils.getCheckerBoard(3)));
		fluff.setOpaque(false);
		add(fluff, c);
	}
}