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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.URL;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import com.pump.io.parser.xml.XMLParser;
import com.pump.swing.LineNumberBorder;
import com.pump.swing.XMLTextComponentHighlighter;

/**
 * This little demo app combines the
 * {@link com.pump.swing.XMLTextComponentHighlighter} with a
 * {@link com.pump.swing.LineNumberBorder}.
 */
public class XMLTextComponentHighlighterDemo extends JPanel implements
		ShowcaseDemo {
	private static final long serialVersionUID = 1L;

	JTextPane textPane = new JTextPane();
	JScrollPane scrollPane = new JScrollPane(textPane);
	XMLTextComponentHighlighter highlighter;

	public XMLTextComponentHighlighterDemo(boolean includeHighlightControls) {
		LineNumberBorder.install(scrollPane, textPane);

		textPane.setText("<?xml version=\"1.0\"?>\n<mysqldump xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n<database name=\"test\">\n\t<table_structure name=\"onerow\">\n\t\t<field Field=\"a\" Type=\"int(11)\" Null=\"YES\" Key=\"\" Extra=\"\" />\n\t</table_structure>\n\t<table_data name=\"onerow\">\n\t<row>\n\t\t<field name=\"a\">1</field>\n\t</row>\n\t</table_data>\n</database>\n</mysqldump>");
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 0, 0, 0);
		add(scrollPane, c);
		scrollPane.setPreferredSize(new Dimension(400, 200));

		highlighter = new XMLTextComponentHighlighter(textPane);
	}

	@Override
	public String getTitle() {
		return "XMLTextComponentHighlighter Demo";
	}

	@Override
	public URL getHelpURL() {
		return XMLTextComponentHighlighterDemo.class
				.getResource("xmlTextComponentHighlighterDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "xml", "text", "editor", "Swing", "ui" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { XMLTextComponentHighlighterDemo.class,
				LineNumberBorder.class, XMLParser.class };
	}
}