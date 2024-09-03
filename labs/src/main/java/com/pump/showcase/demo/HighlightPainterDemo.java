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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;

import com.pump.awt.TextBlock;
import com.pump.inspector.AnimatingInspectorPanel;
import com.pump.inspector.Inspector;
import com.pump.inspector.InspectorRowPanel;
import com.pump.swing.JColorWell;
import com.pump.text.TextBoxHighlightPainter;
import com.pump.text.UnderlineHighlightPainter;
import com.pump.text.WildcardPattern;

/**
 * This demonstrates the TextBoxHighlightPainter, and UnderlineHighlightPainter.
 */
public class HighlightPainterDemo extends ShowcaseExampleDemo {
	private static final long serialVersionUID = 1L;

	JLabel colorWellLabel = new JLabel("Color:");
	JLabel thicknessLabel = new JLabel("Thickness:");
	JLabel squiggleLabel = new JLabel("Squiggle:");
	JColorWell colorWell = new JColorWell(Color.red);
	JSpinner thicknessSpinner = new JSpinner(
			new SpinnerNumberModel(1, 1, 3, 1));
	JRadioButton squiggleOnRadioButton = new JRadioButton("On", true);
	JRadioButton squiggleOffRadioButton = new JRadioButton("Off", false);
	ButtonGroup squiggleButtonGroup = new ButtonGroup();

	JTextField searchPhraseField = new JTextField("chamber");
	JLabel searchPhraseLabel = new JLabel("Search Phrase:");
	JTextPane textPane = new JTextPane();
	JScrollPane scrollPane = new JScrollPane(textPane);
	JLabel includeFillLabel = new JLabel("Fill:");
	JLabel alphaLabel = new JLabel("Alpha:");
	JRadioButton includeFillOnRadioButton = new JRadioButton("On", true);
	JRadioButton includeFillOffRadioButton = new JRadioButton("Off", false);
	ButtonGroup includeFillButtonGroup = new ButtonGroup();
	JSpinner alphaSpinner = new JSpinner(
			new SpinnerNumberModel(30, 1, 100, 1));

	Collection<InspectorRowPanel> underlineControls = new ArrayList<>();
	Collection<InspectorRowPanel> blockControls = new ArrayList<>();

	DocumentListener docListener = new DocumentListener() {

		@Override
		public void insertUpdate(DocumentEvent e) {
			refreshHighlights();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			refreshHighlights();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			refreshHighlights();
		}

	};

	ChangeListener changeListener = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			refreshHighlights();
		}
	};

	ActionListener actionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			refreshHighlights();
		}
	};

	JRadioButton painterUnderlineButton = new JRadioButton("UnderlineHighlightPainter", true);
	JRadioButton painterBoxButton = new JRadioButton("TextBoxHighlightPainter", false);

	public HighlightPainterDemo() {
		super(true, true, false);

		JPanel animatingInspectorPanel = new AnimatingInspectorPanel();
		configurationPanel.add(animatingInspectorPanel);

		Inspector layout = new Inspector(animatingInspectorPanel);
		layout.setConstantHorizontalAlignment(true);
		layout.addRow(new JLabel("Type:"), painterUnderlineButton, painterBoxButton);
		layout.addRow(colorWellLabel, colorWell, false);
		layout.addRow(searchPhraseLabel, searchPhraseField, true);

		underlineControls
				.add(layout.addRow(thicknessLabel, thicknessSpinner, false));
		underlineControls.add(layout.addRow(squiggleLabel,
				squiggleOnRadioButton, squiggleOffRadioButton));

		blockControls.add(layout.addRow(includeFillLabel,
				includeFillOnRadioButton, includeFillOffRadioButton));
		blockControls.add(layout.addRow(alphaLabel, alphaSpinner, false));

		squiggleButtonGroup.add(squiggleOnRadioButton);
		squiggleButtonGroup.add(squiggleOffRadioButton);

		includeFillButtonGroup.add(includeFillOnRadioButton);
		includeFillButtonGroup.add(includeFillOffRadioButton);

		squiggleOnRadioButton.setOpaque(false);
		squiggleOffRadioButton.setOpaque(false);
		includeFillOnRadioButton.setOpaque(false);
		includeFillOffRadioButton.setOpaque(false);

		examplePanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		examplePanel.add(scrollPane, c);

		textPane.setText(
				"Once upon a midnight dreary, while I pondered, weak and weary,\n"
						+ "Over many a quaint and curious volume of forgotten lore\n"
						+ "    While I nodded, nearly napping, suddenly there came a tapping,\n"
						+ "As of some one gently rapping, rapping at my chamber door.\n"
						+ "\"Tis some visitor,\" I muttered, \"tapping at my chamber door -\n"
						+ "Only this, and nothing more.\"");

		searchPhraseField.getDocument().addDocumentListener(docListener);
		colorWell.getColorSelectionModel().addChangeListener(changeListener);
		thicknessSpinner.addChangeListener(changeListener);
		squiggleOnRadioButton.addActionListener(actionListener);
		squiggleOffRadioButton.addActionListener(actionListener);
		includeFillOnRadioButton.addActionListener(actionListener);
		includeFillOffRadioButton.addActionListener(actionListener);
		alphaSpinner.addChangeListener(changeListener);

		ActionListener typeListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshControls();
			}
		};
		painterBoxButton.addActionListener(typeListener);
		painterUnderlineButton.addActionListener(typeListener);

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(painterBoxButton);
		buttonGroup.add(painterUnderlineButton);

		refreshControls();
	}

	protected void refreshControls() {
		Collection<InspectorRowPanel> visibleComponents, hiddenComponents;
		if (painterUnderlineButton.isSelected()) {
			visibleComponents = underlineControls;
			hiddenComponents = blockControls;
		} else {
			visibleComponents = blockControls;
			hiddenComponents = underlineControls;
		}
		for (JComponent c : visibleComponents) {
			c.setVisible(true);
		}
		for (JComponent c : hiddenComponents) {
			c.setVisible(false);
		}
		refreshHighlights();
	}

	static class Word implements CharSequence {
		String text;
		int startIndex;

		Word(String text, int startIndex) {
			this.text = text;
			this.startIndex = startIndex;
		}

		public int getStartIndex() {
			return startIndex;
		}

		public int getEndIndex() {
			return getStartIndex() + length();
		}

		@Override
		public int length() {
			return text.length();
		}

		@Override
		public char charAt(int index) {
			return text.charAt(index);
		}

		@Override
		public CharSequence subSequence(int start, int end) {
			return text.subSequence(start, end);
		}

		@Override
		public String toString() {
			return text;
		}
	}

	private void refreshHighlights() {
		textPane.getHighlighter().removeAllHighlights();
		List<Word> words = getWords(textPane.getText());
		try {
			Color color = colorWell.getColorSelectionModel().getSelectedColor();
			WildcardPattern pattern = new WildcardPattern(
					searchPhraseField.getText());
			if (painterUnderlineButton.isSelected()) {
				UnderlineHighlightPainter underlinePainter = new UnderlineHighlightPainter(
						color,
						((Integer) thicknessSpinner.getValue()).intValue(),
						squiggleOnRadioButton.isSelected());
				highlight(words, pattern, underlinePainter);
			} else {
				float alpha = (((Number) alphaSpinner.getValue()).floatValue() / 100f);
				TextBoxHighlightPainter blockPainter = new TextBoxHighlightPainter(
						color, includeFillOnRadioButton.isSelected(), alpha);
				highlight(words, pattern, blockPainter);
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		textPane.repaint();
	}

	private void highlight(List<Word> words, WildcardPattern pattern,
			Highlighter.HighlightPainter painter) throws BadLocationException {
		for (Word word : words) {
			if (pattern.matches(word)) {
				textPane.getHighlighter().addHighlight(word.getStartIndex(),
						word.getEndIndex(), painter);
			}
		}
	}

	private List<Word> getWords(String text) {
		List<Word> words = new ArrayList<>();
		StringBuilder sb = null;
		for (int a = 0; a < text.length(); a++) {
			char ch = text.charAt(a);
			if (Character.isLetter(ch)) {
				if (sb == null) {
					sb = new StringBuilder();
				}
				sb.append(ch);
			} else {
				if (sb != null)
					words.add(new Word(sb.toString(), a - sb.length()));
				sb = null;
			}
		}
		if (sb != null)
			words.add(new Word(sb.toString(), text.length() - sb.length()));
		return words;
	}

	@Override
	public String getTitle() {
		return "Highlighters, WildcardPattern Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates two new HighlightPainters.";
	}

	@Override
	public URL getHelpURL() {
		return HighlightPainterDemo.class
				.getResource("highlightPainterDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "Swing", "text", "HighlightPainter" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { WildcardPattern.class,
				UnderlineHighlightPainter.class, TextBoxHighlightPainter.class,
				TextBlock.class };
	}
}