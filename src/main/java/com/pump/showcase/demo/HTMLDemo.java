package com.pump.showcase.demo;

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
import com.pump.text.html.QHTMLFactory;
import com.pump.text.html.QStyleSheet;

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

		presetComboBox.addItem(new Preset("text-shadow emboss demo", "<html>\n"
				+ "  <head>\n" + "    <style>\n"
				+ "      body { background-color: #383 }\n"
				+ "      h1   { color: rgba(0,0,0,.6);\n"
				+ "             text-shadow: 2px 8px 6px rgba(0,0,0,.3),\n"
				+ "                 0px -5px 10px rgba(255,255,255,.3); font-weight: bold;}\n"
				+ "    </style>\n" + "  </head>\n" + "  <body>\n"
				+ "    <h1 style=\"font-size: 100pt;\">LOREM IPSUM</h1>\n"
				+ "  </body>\n" + "</html>",
				"See https://designshack.net/articles/css/12-fun-css-text-shadows-you-can-copy-and-paste/"));

		presetComboBox.addItem(new Preset("background-image plaid demo",
				"<html>\n" + "  <head>\n" + "    <style>\n"
						+ "      body { background-image:\n"
						+ "      repeating-linear-gradient(90deg, transparent, transparent 50px,\n"
						+ "        rgba(255, 127, 0, 0.25) 50px, rgba(255, 127, 0, 0.25) 56px,\n"
						+ "        transparent 56px, transparent 63px,\n"
						+ "        rgba(255, 127, 0, 0.25) 63px, rgba(255, 127, 0, 0.25) 69px,\n"
						+ "        transparent 69px, transparent 116px,\n"
						+ "        rgba(255, 206, 0, 0.25) 116px, rgba(255, 206, 0, 0.25) 166px),\n"
						+ "      repeating-linear-gradient(0deg, transparent, transparent 50px,\n"
						+ "        rgba(255, 127, 0, 0.25) 50px, rgba(255, 127, 0, 0.25) 56px,\n"
						+ "        transparent 56px, transparent 63px,\n"
						+ "        rgba(255, 127, 0, 0.25) 63px, rgba(255, 127, 0, 0.25) 69px,\n"
						+ "        transparent 69px, transparent 116px,\n"
						+ "        rgba(255, 206, 0, 0.25) 116px, rgba(255, 206, 0, 0.25) 166px),\n"
						+ "      repeating-linear-gradient(-45deg, transparent, transparent 5px,\n"
						+ "        rgba(143, 77, 63, 0.25) 5px, rgba(143, 77, 63, 0.25) 10px),\n"
						+ "      repeating-linear-gradient(45deg, transparent, transparent 5px,\n"
						+ "        rgba(143, 77, 63, 0.25) 5px, rgba(143, 77, 63, 0.25) 10px); }\n"
						+ "      h1   { font-size: 100pt;font-weight: bold;}\n"
						+ "    </style>\n" + "  </head>\n" + "  <body>\n"
						+ "    <h1>LOREM IPSUM</h1>\n" + "  </body>\n"
						+ "</html>",
				"See https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Images/Using_CSS_gradients"));

		presetComboBox.addItem(new Preset("background-image gradient demo",
				"<html>\n" + "  <head>\n" + "    <style>\n"
						+ "      body { background-image:\n"
						+ "      repeating-linear-gradient(190deg, rgba(255, 0, 0, 0.5) 40px,\n"
						+ "        rgba(255, 153, 0, 0.5) 80px, rgba(255, 255, 0, 0.5) 120px,\n"
						+ "        rgba(0, 255, 0, 0.5) 160px, rgba(0, 0, 255, 0.5) 200px,\n"
						+ "        rgba(75, 0, 130, 0.5) 240px, rgba(238, 130, 238, 0.5) 280px,\n"
						+ "        rgba(255, 0, 0, 0.5) 300px),\n"
						+ "      repeating-linear-gradient(-190deg, rgba(255, 0, 0, 0.5) 30px,\n"
						+ "        rgba(255, 153, 0, 0.5) 60px, rgba(255, 255, 0, 0.5) 90px,\n"
						+ "        rgba(0, 255, 0, 0.5) 120px, rgba(0, 0, 255, 0.5) 150px,\n"
						+ "        rgba(75, 0, 130, 0.5) 180px, rgba(238, 130, 238, 0.5) 210px,\n"
						+ "        rgba(255, 0, 0, 0.5) 230px),\n"
						+ "      repeating-linear-gradient(23deg, red 50px, orange 100px,\n"
						+ "        yellow 150px, green 200px, blue 250px,\n"
						+ "        indigo 300px, violet 350px, red 370px); }\n"
						+ "      h1   { font-size: 100pt;font-weight: bold;}\n"
						+ "    </style>\n" + "  </head>\n" + "  <body>\n"
						+ "    <h1>LOREM IPSUM</h1>\n" + "  </body>\n"
						+ "</html>",
				"See https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Images/Using_CSS_gradients"));

		presetComboBox.addItem(new Preset("background-clip text inset demo",
				"<html>\n" + "  <head>\n" + "    <style>\n"
						+ "      body { background-color: #cbcbcb }\n"
						+ "      h1   { \n" + "           color: transparent;\n"
						+ "           background-color: #666666;\n"
						+ "           background-clip: text; \n"
						+ "           text-shadow: 2px 3px 3px rgba(255,255,255,0.5) ;\n"
						+ "}\n" + "    </style>\n" + "  </head>\n"
						+ "  <body>\n"
						+ "    <h1 style=\"font-size: 100pt;\">LOREM IPSUM</h1>\n"
						+ "  </body>\n" + "</html>",
				"See https://www.webcodegeeks.com/css/css-text-shadow-example/"));

		presetComboBox.addItem(new Preset(
				"border-radius background-clip max-content header demo",
				"<html>\n" + "  <head>\n" + "    <style>\n"
						+ "      body { background-color: white; }\n" + "\n"
						+ "      h1 { color: #005a9c; \n"
						+ "           font: bold 160% sans-serif; \n"
						+ "           padding: 6px 20px 6px 20px; \n"
						+ "           width: max-content;\n"
						+ "           background-clip: border-box; \n"
						+ "           border: 2px solid #005a9c; \n"
						+ "           border-radius:32px; }\n" + "\n"
						+ "      h2 { padding: 4px 10px 4px 10px; \n"
						+ "           background-color: #005a9c; \n"
						+ "           font: bold 130% sans-serif; \n"
						+ "           color: white; \n"
						+ "           width: max-content; \n"
						+ "           background-clip: border-box; \n"
						+ "           border: transparent; \n"
						+ "           border-radius:10px;}\n" + "    </style>\n"
						+ "  </head>\n" + "  <body>\n"
						+ "    <h1>Header 1</h1>\n" + "    <h2>Header 2</h2>\n"
						+ "  </body>\n" + "</html>",
				""));
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
		return getClass().getResource("qhtmlDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "html", "css" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { QHTMLEditorKit.class, HTMLEditorKit.class,
				QStyleSheet.class, QHTMLFactory.class };
	}

}
