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
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
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
import com.pump.inspector.InspectorGridBagLayout;
import com.pump.swing.CollapsibleContainer;
import com.pump.swing.JColorWell;
import com.pump.swing.SectionContainer.Section;
import com.pump.text.TextBoxHighlightPainter;
import com.pump.text.UnderlineHighlightPainter;
import com.pump.text.WildcardPattern;

/**
 * This demonstrates the WildcardPattern, TextBoxHighlightPainter, and
 * UnderlineHighlightPainter.
 * <p>
 * Here is a sample screenshot of this showcase demo:
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/showcase/WildcardPatternHighlighterDemo.png"
 * alt="A screenshot of the WildcardPatternHighlighterDemo.">
 */
public class WildcardPatternHighlighterDemo extends JPanel implements
		ShowcaseDemo {
	private static final long serialVersionUID = 1L;

	CollapsibleContainer sectionContainer = new CollapsibleContainer();

	JPanel inspector = new JPanel();

	JTextField underlinePatternField = new JTextField("*pon*");
	JLabel underlinePatternLabel = new JLabel("Underline Pattern:");
	JLabel colorWellLabel = new JLabel("Color:");
	JLabel thicknessLabel = new JLabel("Thickness:");
	JLabel squiggleLabel = new JLabel("Squiggle:");
	JColorWell colorWell = new JColorWell(Color.red);
	JSpinner thicknessSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 3, 1));
	JRadioButton squiggleOnRadioButton = new JRadioButton("On", true);
	JRadioButton squiggleOffRadioButton = new JRadioButton("Off", false);
	ButtonGroup squiggleButtonGroup = new ButtonGroup();

	JTextField blockPatternField = new JTextField("????ing");
	JLabel blockPatternLabel = new JLabel("Block Pattern:");
	JTextPane textPane = new JTextPane();
	JScrollPane scrollPane = new JScrollPane(textPane);
	JLabel hueLabel = new JLabel("Hue:");
	JLabel includeFillLabel = new JLabel("Fill:");
	JLabel alphaLabel = new JLabel("Alpha:");
	JSpinner hueSpinner = new JSpinner(new SpinnerNumberModel(180, 1, 360, 5));
	JRadioButton includeFillOnRadioButton = new JRadioButton("On", true);
	JRadioButton includeFillOffRadioButton = new JRadioButton("Off", false);
	ButtonGroup includeFillButtonGroup = new ButtonGroup();
	JSpinner alphaSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 100, 1));

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

	public WildcardPatternHighlighterDemo() {
		super(new GridBagLayout());

		Section underlineSection = sectionContainer.addSection("underline",
				"Underline Highlighter");
		sectionContainer.getHeader(underlineSection).putClientProperty(
				CollapsibleContainer.COLLAPSIBLE, Boolean.FALSE);
		InspectorGridBagLayout layout = new InspectorGridBagLayout(
				underlineSection.getBody());
		layout.addRow(underlinePatternLabel, underlinePatternField, true);
		layout.addRow(colorWellLabel, colorWell, false);
		layout.addRow(thicknessLabel, thicknessSpinner, false);
		layout.addRow(squiggleLabel, squiggleOnRadioButton,
				squiggleOffRadioButton);

		Section blockSection = sectionContainer.addSection("block",
				"Text Block Highlighter");
		sectionContainer.getHeader(blockSection).putClientProperty(
				CollapsibleContainer.COLLAPSIBLE, Boolean.FALSE);
		layout = new InspectorGridBagLayout(blockSection.getBody());
		layout.addRow(blockPatternLabel, blockPatternField, true);
		layout.addRow(hueLabel, hueSpinner, false);
		layout.addRow(includeFillLabel, includeFillOnRadioButton,
				includeFillOffRadioButton);
		layout.addRow(alphaLabel, alphaSpinner, false);

		squiggleButtonGroup.add(squiggleOnRadioButton);
		squiggleButtonGroup.add(squiggleOffRadioButton);

		includeFillButtonGroup.add(includeFillOnRadioButton);
		includeFillButtonGroup.add(includeFillOffRadioButton);

		Section textSection = sectionContainer.addSection("text", "Text");
		sectionContainer.getHeader(textSection).putClientProperty(
				CollapsibleContainer.COLLAPSIBLE, Boolean.FALSE);
		textSection.setProperty(CollapsibleContainer.VERTICAL_WEIGHT, 1.0);
		textSection.getBody().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		textSection.getBody().add(scrollPane, c);

		c.insets = new Insets(0, 0, 0, 5);
		add(sectionContainer, c);

		textPane.setText("Once upon a midnight dreary, while I pondered, weak and weary,\n"
				+ "Over many a quaint and curious volume of forgotten lore—\n"
				+ "    While I nodded, nearly napping, suddenly there came a tapping,\n"
				+ "As of some one gently rapping, rapping at my chamber door.\n"
				+ "“’Tis some visitor,” I muttered, “tapping at my chamber door—\n"
				+ "            Only this and nothing more.”");

		underlinePatternField.getDocument().addDocumentListener(docListener);
		blockPatternField.getDocument().addDocumentListener(docListener);
		colorWell.getColorSelectionModel().addChangeListener(changeListener);
		thicknessSpinner.addChangeListener(changeListener);
		squiggleOnRadioButton.addActionListener(actionListener);
		squiggleOffRadioButton.addActionListener(actionListener);
		hueSpinner.addChangeListener(changeListener);
		includeFillOnRadioButton.addActionListener(actionListener);
		includeFillOffRadioButton.addActionListener(actionListener);
		alphaSpinner.addChangeListener(changeListener);

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
			WildcardPattern underlinePattern = new WildcardPattern(
					underlinePatternField.getText());
			UnderlineHighlightPainter underlinePainter = new UnderlineHighlightPainter(
					colorWell.getColorSelectionModel().getSelectedColor(),
					((Integer) thicknessSpinner.getValue()).intValue(),
					squiggleOnRadioButton.isSelected());
			highlight(words, underlinePattern, underlinePainter);

			WildcardPattern blockPattern = new WildcardPattern(
					blockPatternField.getText());
			float hue = ((Number) hueSpinner.getValue()).floatValue() / 360f;
			float alpha = ((Number) alphaSpinner.getValue()).floatValue() / 100f;
			TextBoxHighlightPainter blockPainter = new TextBoxHighlightPainter(
					hue, includeFillOnRadioButton.isSelected(), alpha);
			highlight(words, blockPattern, blockPainter);
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
		return "This demonstrates two new Highlighters and a simpler regex alternative.";
	}

	@Override
	public URL getHelpURL() {
		return WildcardPatternHighlighterDemo.class
				.getResource("wildcardPatternHighlighterDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "swing", "text", "pattern" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { WildcardPattern.class,
				UnderlineHighlightPainter.class, TextBoxHighlightPainter.class,
				TextBlock.class };
	}
}