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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.URL;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import com.pump.io.parser.java.JavaParser;
import com.pump.swing.JavaFormatter;
import com.pump.swing.LineNumberBorder;

/**
 * This little demo app combines the {@link com.pump.swing.JavaFormatter} with a
 * {@link com.pump.swing.LineNumberBorder}.
 */
public class JavaFormatterDemo extends ShowcaseDemo {
	private static final long serialVersionUID = 1L;

	JTextPane textPane = new JTextPane();
	JScrollPane scrollPane = new JScrollPane(textPane);

	public JavaFormatterDemo() {
		LineNumberBorder.install(scrollPane, textPane);

		textPane.setText(
				"package com.org.net;\n\nimport java.util.*;\n\n/**\n * Documentation\n */\npublic class Foo {\n\n\tFoo() {\n\t\t// another comment\n\t\tSystem.out.println(\"Foo\");\n\t}\n}");
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 0, 0, 0);
		c.gridwidth = GridBagConstraints.REMAINDER;
		add(scrollPane, c);
		scrollPane.setPreferredSize(new Dimension(400, 200));

		new JavaFormatter(textPane);

		installExportJVGContextMenu(textPane, scrollPane, getTitle());
	}

	@Override
	public String getTitle() {
		return "JavaFormatter Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates a model to render Java source code in JTextPanes.";
	}

	@Override
	public URL getHelpURL() {
		return JavaFormatterDemo.class.getResource("javaFormatterDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "source-code", "text", "editor", "Swing", "ui",
				"java", "style", "attribute", "format" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { JavaFormatter.class, LineNumberBorder.class,
				JavaParser.class };
	}
}