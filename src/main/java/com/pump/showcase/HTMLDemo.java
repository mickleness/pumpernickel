package com.pump.showcase;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.html.HTMLEditorKit;

import com.pump.desktop.error.ErrorManager;
import com.pump.inspector.Inspector;
import com.pump.inspector.InspectorRow;
import com.pump.text.html.QHTMLEditorKit;

public class HTMLDemo extends ShowcaseExampleDemo {
	private static final long serialVersionUID = 1L;

	class Preset {
		String html;
		String name;
		String description;

		public Preset(String name, String html, String description) {
			this.name = name;
			this.html = html;
			this.description = description;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	JComboBox<Preset> presetComboBox = new JComboBox<>();
	JTextArea source = new JTextArea();
	JScrollPane sourceScrollPane = new JScrollPane(source,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

	JEditorPane htmlPane = new JEditorPane();
	JScrollPane htmlScrollPane = new JScrollPane(htmlPane,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

	JTextArea error = new JTextArea();
	JScrollPane errorScrollPane = new JScrollPane(error,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

	CardLayout cardLayout = new CardLayout();
	JPanel cardPanel = new JPanel(cardLayout);

	ActionListener presetListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			Preset p = (Preset) presetComboBox.getSelectedItem();
			if (p != null) {
				source.setText(p.html);
			}
		}

	};

	ActionListener editorKitListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			refreshHTML();
		}

	};

	DocumentListener sourceListener = new DocumentListener() {

		@Override
		public void insertUpdate(DocumentEvent e) {
			refreshHTML();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			refreshHTML();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			refreshHTML();
		}
	};

	JRadioButton swingKitRadioButton = new JRadioButton("HTMLEditorKit (Swing)",
			false);
	JRadioButton qKitRadioButton = new JRadioButton("QHTMLEditorKit", true);

	public HTMLDemo() {
		super(true, true, false, true);

		initializePresets();

		source.setFont(new Font("Monospaced", 0, 14));
		error.setFont(new Font("Monospaced", 0, 14));
		sourceScrollPane.setPreferredSize(new Dimension(400, 200));
		htmlScrollPane.setPreferredSize(new Dimension(400, 200));
		errorScrollPane.setPreferredSize(new Dimension(400, 200));

		Inspector configInspector = new Inspector(configurationPanel);
		configInspector.addRow(new JLabel("Preset:"), presetComboBox);
		configInspector.addRow(new JLabel("HTML Kit:"),
				createRow(swingKitRadioButton, qKitRadioButton));
		configInspector
				.addRow(new InspectorRow(null, sourceScrollPane, true, 1));

		ButtonGroup g = new ButtonGroup();
		g.add(swingKitRadioButton);
		g.add(qKitRadioButton);

		presetComboBox.addActionListener(presetListener);
		swingKitRadioButton.addActionListener(editorKitListener);
		qKitRadioButton.addActionListener(editorKitListener);
		source.getDocument().addDocumentListener(sourceListener);

		examplePanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		examplePanel.add(cardPanel, c);

		error.setEditable(false);
		htmlPane.setEditable(false);

		cardPanel.add(htmlScrollPane, "html");
		cardPanel.add(errorScrollPane, "error");

		presetListener.actionPerformed(null);
	}

	private JComponent createRow(JComponent... components) {
		JPanel p = new JPanel(new GridBagLayout());
		p.setOpaque(false);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(0, 0, 0, 3);
		for (JComponent jc : components) {
			p.add(jc, c);
			c.gridx++;
		}
		return p;
	}

	private void refreshHTML() {
		HTMLEditorKit editorKit = qKitRadioButton.isSelected()
				? new QHTMLEditorKit()
				: new HTMLEditorKit();
		htmlPane.setEditorKit(editorKit);
		try {
			htmlPane.setText(source.getText());
			cardLayout.show(cardPanel, "html");
		} catch (Throwable t) {
			String str = ErrorManager.getStackTrace(t);
			error.setText(str);
			cardLayout.show(cardPanel, "error");
		}
	}

	private void initializePresets() {

		presetComboBox.addItem(new Preset("color, named, inline", "<html>\n"
				+ "  <h3 style=\"color: darkorchid;font-size: 150%;\">Lorem Ipsum</h3>\n"
				+ "</html>", ""));
		presetComboBox.addItem(new Preset("color, named, internal sheet",
				"<html>\n" + "  <head>\n" + "    <style>\n"
						+ "      h1   {color: darkorchid;}\n" + "    </style>\n"
						+ "  </head>\n" + "  <body>\n"
						+ "    <h1 style=\"font-size: 150%;\">Lorem Ipsum</h1>\n"
						+ "  </body>\n" + "</html>",
				""));

		presetComboBox.addItem(new Preset("color, hex, inline", "<html>\n"
				+ "  <h3 style=\"color: #321C;font-size: 150%;\">Lorem Ipsum</h3>\n"
				+ "</html>", ""));
		presetComboBox.addItem(new Preset("color, hex, internal sheet",
				"<html>\n" + "  <head>\n" + "    <style>\n"
						+ "      h1   {color: #00331199;}\n" + "    </style>\n"
						+ "  </head>\n" + "  <body>\n"
						+ "    <h1 style=\"font-size: 150%;\">Lorem Ipsum</h1>\n"
						+ "  </body>\n" + "</html>",
				""));

		presetComboBox.addItem(new Preset("text-shadow inline", "<html>\n"
				+ "  <h3 style=\"color:darkorchid; text-shadow: 2px 2px 4px plum;font-size: 150%;\">Lorem Ipsum</h3>\n"
				+ "</html>",
				"Loosely based on https://codepen.io/namho/pen/jEaXra"));

		presetComboBox.addItem(new Preset("text-shadow internal sheet",
				"<html>\n" + "  <head>\n" + "    <style>\n"
						+ "      h1   {color:darkorchid; text-shadow: 2px 2px 4px plum;}\n"
						+ "    </style>\n" + "  </head>\n" + "  <body>\n"
						+ "    <h1 style=\"font-size: 150%;\">Lorem Ipsum</h1>\n"
						+ "  </body>\n" + "</html>",
				""));

		presetComboBox.addItem(new Preset("text-shadow emboss demo", "<html>\n"
				+ "  <head>\n" + "    <style>\n"
				+ "      body { background-color: #383 }\n"
				+ "      h1   { color: rgba(0,0,0,.6);\n"
				+ "             text-shadow: 2px 8px 6px rgba(0,0,0,.3),\n"
				+ "                 0px -5px 10px rgba(255,255,255,.3); font-weight: bold;}\n"
				+ "    </style>\n" + "  </head>\n" + "  <body>\n"
				+ "    <h1 style=\"font-size: 100pt;\">LOREM IMPSUM</h1>\n"
				+ "  </body>\n" + "</html>",
				"See https://designshack.net/articles/css/12-fun-css-text-shadows-you-can-copy-and-paste/"));
	}

	@Override
	public String getTitle() {
		return "HTML Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates enhancements to Swing's default HTML renderer.";
	}

	@Override
	public URL getHelpURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getKeywords() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<?>[] getClasses() {
		// TODO Auto-generated method stub
		return null;
	}

}
