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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.pump.io.parser.java.JavaEncoding;
import com.pump.plaf.RoundTextFieldUI;
import com.pump.swing.TextFieldPrompt;
import com.pump.text.WildcardPattern;

/**
 * This showcase lists all properties available via System.getProperty(..). It
 * includes a search bar.
 */
public class SystemPropertiesDemo extends ShowcaseDemo {
	private static final long serialVersionUID = 1L;

	JTextPane textPane = new JTextPane();
	JScrollPane scrollPane = new JScrollPane(textPane);
	JTextField searchField = new JTextField(20);
	SortedMap<String, String> allProperties;

	public SystemPropertiesDemo() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;

		searchField.setUI(new RoundTextFieldUI());
		searchField.putClientProperty("JTextField.variant", "search");
		new TextFieldPrompt(searchField, "Search...");

		c.insets = new Insets(0, 0, 8, 0);
		add(searchField, c);

		c.weighty = 1;
		c.gridy++;
		add(scrollPane, c);

		searchField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				refresh();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				refresh();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				refresh();
			}

		});

		textPane.setEditable(false);
		refresh();
	}

	private void refresh() {
		String searchPhrase = "*" + searchField.getText() + "*";
		WildcardPattern searchPattern = new WildcardPattern(searchPhrase);

		StringBuilder sb = new StringBuilder();
		for (Entry<String, String> entry : getAllProperties().entrySet()) {
			String line = entry.getKey() + "="
					+ JavaEncoding.encode(entry.getValue());
			if (sb.length() > 0)
				line = "\n" + line;
			if (searchPattern.matches(line))
				sb.append(line);
		}
		textPane.setText(sb.toString());
		textPane.setFont(new Font("Monospaced", 0, 13));
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				textPane.scrollRectToVisible(new Rectangle(0, 0, 0, 0));
			}
		});
	}

	private SortedMap<String, String> getAllProperties() {
		if (allProperties == null) {
			allProperties = new TreeMap<>();
			Properties properties = System.getProperties();
			Enumeration e = properties.keys();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String value = System.getProperty(key);
				allProperties.put(key, value);
			}
		}
		return allProperties;
	}

	@Override
	public String getTitle() {
		return "System Properties Demo";
	}

	@Override
	public String getSummary() {
		return "This demo shows the current system properties.";
	}

	@Override
	public URL getHelpURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getKeywords() {
		Collection<String> keywords = new HashSet<>();
		keywords.addAll(Arrays.asList("properties", "runtime", "java", "JVM",
				"configuration"));
		for (Entry<String, String> entry : getAllProperties().entrySet()) {
			keywords.add(entry.getKey());
			if (entry.getValue().length() > 1)
				keywords.add(entry.getValue());
		}
		return keywords.toArray(new String[keywords.size()]);
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { System.class };
	}

}