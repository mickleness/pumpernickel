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
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Highlighter.HighlightPainter;

import com.pump.io.parser.Token;
import com.pump.swing.LineNumberBorder;
import com.pump.swing.XMLTextComponentHighlighter;
import com.pump.text.TextBoxHighlightPainter;

/**
 * This little demo app combines the
 * {@link com.pump.swing.XMLTextComponentHighlighter} with a
 * {@link com.pump.swing.LineNumberBorder} and demos a
 * {@link TextBoxHighlightPainter}.
 */
public class XMLTextComponentHighlighterDemo extends JPanel {
	private static final long serialVersionUID = 1L;

	JTextPane textPane = new JTextPane();
	JScrollPane scrollPane = new JScrollPane(textPane);
	Map<String, Float> boxHues = new HashMap<String, Float>();
	JLabel highlightLabel = new JLabel("Highlight:");
	JTextField highlightTextField = new JTextField("class");
	XMLTextComponentHighlighter highlighter;

	public XMLTextComponentHighlighterDemo(boolean includeHighlightControls) {
		LineNumberBorder.install(scrollPane, textPane);

		textPane.setText("<?xml version=\"1.0\"?>\n<mysqldump xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n<database name=\"test\">\n\t<table_structure name=\"onerow\">\n\t\t<field Field=\"a\" Type=\"int(11)\" Null=\"YES\" Key=\"\" Extra=\"\" />\n\t</table_structure>\n\t<table_data name=\"onerow\">\n\t<row>\n\t\t<field name=\"a\">1</field>\n\t</row>\n\t</table_data>\n</database>\n</mysqldump>");
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		c.insets = new Insets(4, 4, 4, 4);
		c.fill = GridBagConstraints.BOTH;
		add(highlightLabel, c);
		c.gridx++;
		c.weightx = 1;
		add(highlightTextField, c);
		c.gridx = 0;
		c.gridy++;
		c.weighty = 1;
		c.insets = new Insets(0, 0, 0, 0);
		c.gridwidth = GridBagConstraints.REMAINDER;
		add(scrollPane, c);
		scrollPane.setPreferredSize(new Dimension(400, 200));

		highlightLabel.setVisible(includeHighlightControls);
		highlightTextField.setVisible(includeHighlightControls);

		highlightTextField.getDocument().addDocumentListener(
				new DocumentListener() {

					@Override
					public void insertUpdate(DocumentEvent e) {
						highlighter.refresh(true);
					}

					@Override
					public void removeUpdate(DocumentEvent e) {
						highlighter.refresh(true);
					}

					@Override
					public void changedUpdate(DocumentEvent e) {
					}

				});

		highlighter = new XMLTextComponentHighlighter(textPane) {

			@Override
			protected HighlightPainter getHighlightPainter(Token[] allTokens,
					int tokenIndex, int selectionStart, int selectionEnd) {
				if (allTokens[tokenIndex].getText().equalsIgnoreCase(
						highlightTextField.getText())) {
					return new TextBoxHighlightPainter(.2f, true);
				}

				return null;
			}
		};
	}
}