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
package com.pump.showcase.demo;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.URL;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import com.pump.io.parser.xml.XMLParser;
import com.pump.swing.LineNumberBorder;
import com.pump.swing.XMLFormatter;

/**
 * This little demo app combines the {@link com.pump.swing.XMLFormatter} with a
 * {@link com.pump.swing.LineNumberBorder}.
 */
public class XMLFormatterDemo extends ShowcaseDemo {
	private static final long serialVersionUID = 1L;

	JTextPane textPane = new JTextPane();
	JScrollPane scrollPane = new JScrollPane(textPane);

	public XMLFormatterDemo() {
		LineNumberBorder.install(scrollPane, textPane);

		textPane.setText(
				"<?xml version=\"1.0\"?>\n<mysqldump xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n\t<database name=\"test\">\n\t\t<!--This table needs work -->\n\t\t<table_structure name=\"onerow\">\n\t\t\t<field Field=\"a\" Type=\"int(11)\" Null=\"YES\" Key=\"\" Extra=\"\" />\n\t\t</table_structure>\n\t\t<table_data name=\"onerow\">\n\t\t\t<row>\n\t\t\t\t<field name=\"a\">1</field>\n\t\t\t</row>\n\t\t</table_data>\n\t</database>\n</mysqldump>");
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

		new XMLFormatter(textPane);

		installExportJVGContextMenu(textPane, scrollPane, getTitle());
	}

	@Override
	public String getTitle() {
		return "XMLFormatter Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates a model to render XML in JTextPanes.";
	}

	@Override
	public URL getHelpURL() {
		return XMLFormatterDemo.class.getResource("xmlFormatterDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "xml", "text", "editor", "Swing", "ui", "style",
				"attribute", "format" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { XMLFormatter.class, LineNumberBorder.class,
				XMLParser.class };
	}
}