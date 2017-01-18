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
package com.pump.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.text.JTextComponent;

import com.pump.blog.Blurb;
import com.pump.blog.ResourceSample;
import com.pump.icon.CloseIcon;
import com.pump.icon.TriangleIcon;
import com.pump.plaf.RoundRectButtonUI;
import com.pump.plaf.RoundTextFieldUI;

/** A row of controls similar to the search features in Safari and Firefox.
 * By default this emulates the Firefox look, but there is a special
 * static method to make a Safari-style search bar.
 * 
 * <!-- ======== START OF AUTOGENERATED SAMPLES ======== -->
 * <p>Here are some samples:
 * <table summary="Resource&#160;Samples&#160;for&#160;com.bric.swing.TextSearchBar"><tr>
 * <td></td>
 * <td><img src="https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/samples/TextSearchBar/Safari.png" alt="com.bric.swing.TextSearchBar.createSafariStyleBar(new&#160;javax.swing.JTextArea())"></td>
 * <td><img src="https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/samples/TextSearchBar/Firefox.png" alt="com.bric.swing.TextSearchBar.createFirefoxStyleBar(new&#160;javax.swing.JTextArea())"></td>
 * </tr><tr>
 * <td></td>
 * <td>Safari</td>
 * <td>Firefox</td>
 * </tr><tr>
 * </tr></table>
 * <!-- ======== END OF AUTOGENERATED SAMPLES ======== -->
 */
@ResourceSample( 
		sample={ "com.bric.swing.TextSearchBar.createSafariStyleBar(new javax.swing.JTextArea())",
				"com.bric.swing.TextSearchBar.createFirefoxStyleBar(new javax.swing.JTextArea())"
		},
		names = {"Safari", "Firefox"} )
@Blurb (
imageName = "TextSearch.png",
title = "Text: Searching JTextComponents",
releaseDate = "December 2009",
summary = "This discusses different GUIs for the user to search for a string in a <code>JTextComponent</code>.\n"+
		"<p>This includes the common search dialog, the model Safari uses and the model Firefox uses.",
article = "http://javagraphics.blogspot.com/2009/12/text-searching-jtextcomponents.html"
)
public class TextSearchBar extends JPanel {
	private static final long serialVersionUID = 1L;
	protected static ResourceBundle strings = ResourceBundle.getBundle("com.pump.swing.TextSearchBar");
	
	/** Creates a set <code>TextSearchBar</code> that resembles the kind
	 * you'd get in Safari.  This is just a convenience method; everything
	 * that is configured here you can do yourself.
	 * @param jtc the text component to search.
	 * @return a Safari-style search bar.
	 */
	public static TextSearchBar createSafariStyleBar(JTextComponent jtc) {
		TextSearchBar tsb = new TextSearchBar(jtc);
		tsb.setUseArrowIcons(true);
		tsb.setUseCloseIcon(false);
		tsb.setAlignment(SwingConstants.RIGHT);
		tsb.setMatchesLabelVisible(true);
		tsb.setMatchCaseVisible(false);
		tsb.setHighlightAllVisible(false);
		tsb.setFindLabelVisible(false);
		tsb.setHighlightAll(true);
		return tsb;
	}

	/** Creates a set <code>TextSearchBar</code> that resembles the kind
	 * you'd get in Firefox.  This is just a convenience method; everything
	 * that is configured here you can do yourself.
	 * @param jtc the text component to search.
	 * @return a Firefox-style search bar.
	 */
	public static TextSearchBar createFirefoxStyleBar(JTextComponent jtc) {
		TextSearchBar tsb = new TextSearchBar(jtc);
		tsb.setUseArrowIcons(false);
		tsb.setUseCloseIcon(true);
		tsb.setAlignment(SwingConstants.LEFT);
		tsb.setMatchesLabelVisible(false);
		tsb.setMatchCaseVisible(true);
		tsb.setHighlightAllVisible(true);
		tsb.setFindLabelVisible(true);
		tsb.setHighlightAll(false);
		tsb.getHighlightSheet().setBackground(new Color(0x00ffffff,true));
		tsb.getHighlightSheet().setHighlightColor(new Color(0xEF0FFF));
		tsb.getHighlightSheet().setForeground(Color.white);
		tsb.getHighlightSheet().setBorderActive(false);
		tsb.getHighlightSheet().setPadding(TextHighlightSheet.FIREFOX_PADDING);
		return tsb;
	}
	
	JTextComponent textComponent;
	TextHighlightSheet highlightSheet;
	JLabel findLabel = new JLabel();
	JButton nextButton = new JButton();
	JButton prevButton = new JButton();
	JToggleButton highlightAllButton = new JToggleButton();
	JButton doneButton = new JButton();
	JCheckBox matchCaseButton = new JCheckBox();
	JTextField searchField = createTextField();
	JLabel matchesLabel = new JLabel();
	Icon closeIcon = new CloseIcon(12);
	Icon closeRolloverIcon = new CloseIcon(12,CloseIcon.State.ROLLOVER);
	Icon closePressedIcon = new CloseIcon(12,CloseIcon.State.PRESSED);
	Icon nextIcon = new TriangleIcon(SwingConstants.EAST,7,7);
	Icon prevIcon = new TriangleIcon(SwingConstants.WEST,7,7);
	
	int alignment = javax.swing.SwingConstants.LEFT;
	boolean useArrowIcons;
	boolean useCloseIcon;
	
	protected static JTextField createTextField() {
		JTextField textField = null;
		String promptText = strings.getString("searchPrompt");

		/** I tried using the xswingx package here,
		 * but that somehow resulted in a JXSearchField
		 * that didn't have a border around the text field.
		 * Plus it required the SwingX project... it seemed
		 * to load to a lot of bloat.
		 */
		textField = new JTextField( "", 12);
		
		textField.setUI(new RoundTextFieldUI());
		textField.putClientProperty("useSearchIcon", "true");
		new TextFieldPrompt(textField, null, promptText);
		
		return textField;
	}
	
	ActionListener actionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==nextButton || e.getSource()==searchField) {
				SwingSearch.find(textComponent, searchField.getText(), true, matchCaseButton.isSelected());
			} else if(e.getSource()==prevButton) {
				SwingSearch.find(textComponent, searchField.getText(), false, matchCaseButton.isSelected());
			} else if(e.getSource()==doneButton) {
				TextSearchBar.this.setVisible(false);
			} else if(e.getSource()==highlightAllButton) {
				TextSearchBar.this.setHighlightAll( highlightAllButton.isSelected() );
			} else if(e.getSource()==matchCaseButton) {
				setMatchCase( matchCaseButton.isSelected() );
			}
		}
	};
	
	KeyListener searchFieldKeyListener = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode()==KeyEvent.VK_ESCAPE) {
				setVisible(false);
				e.consume();
			} else if(e.getKeyCode()==KeyEvent.VK_ENTER && e.isShiftDown()==false) {
				nextButton.doClick();
				e.consume();
			} else if(e.getKeyCode()==KeyEvent.VK_ENTER && e.isShiftDown()) {
				prevButton.doClick();
				e.consume();
			}
		}
	};
	
	/** Listens to both the searchField and the textComponent we're searching.
	 */
	DocumentListener documentListener = new DocumentListener() {

		@Override
		public void changedUpdate(DocumentEvent e) {
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			updateMatchLabel();
			if(highlightSheet!=null)
				highlightSheet.setSearchPhrase( searchField.getText() );
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			insertUpdate(e);
		}
		
	};
	
	FocusListener focusListener = new FocusListener() {
		public void focusGained(FocusEvent e) {
			if(highlightAllButton.isVisible()==false)
				setHighlightAll(true);
		}
		
		public void focusLost(FocusEvent e) {
			if(highlightAllButton.isVisible()==false)
				setHighlightAll(false);
		}
	};
	
	/** Creates a new <code>TextSearchBar</code>.
	 * By default this emulates Firefox's search bar.
	 */
	public TextSearchBar() {
		super(new GridBagLayout());

		setUseCloseIcon(true);
		setMatchesLabelVisible(false);
		
		updateLayout();
		
		setButtonUI(new RoundRectButtonUI(5) {
			@Override
			protected Insets getTextPadding() {
				return new Insets(0,0,0,0);
			}
		});
		
		Font preferredFont = UIManager.getFont("ToolTip.font");
		if(preferredFont!=null)
			setFont(preferredFont);
		
		highlightAllButton.addActionListener(actionListener);
		nextButton.addActionListener(actionListener);
		prevButton.addActionListener(actionListener);
		doneButton.addActionListener(actionListener);
		searchField.addActionListener(actionListener);
		matchCaseButton.addActionListener(actionListener);
		
		for(int a = 0; a<getComponentCount(); a++) {
			Component c = getComponent(a);
			if((! (c instanceof JTextField)) && (c instanceof JComponent)) {
				//see apple tech note 2196
				//esp useful for the JCheckBox
				((JComponent)c).putClientProperty("JComponent.sizeVariant","small");
			}
		}
		
		searchField.getDocument().addDocumentListener(documentListener);
		searchField.addKeyListener(searchFieldKeyListener);
		searchField.addFocusListener(focusListener);
		
		matchCaseButton.setOpaque(false);
	}
	
	/*
	 * @param newAlignment should be SwingConstants.RIGHT or SwingConstants.LEFT
	 */
	public void setAlignment(int newAlignment) {
		int oldAlignment = alignment;
		try {
			alignment = newAlignment;
			updateLayout();
		} catch(RuntimeException e) {
			alignment = oldAlignment;
			updateLayout();
		}
	}
	
	/** Controls the visible of the label that reads "X matches found"
	 * @param b whether the "X matches found" label should be visible
	 */
	public void setMatchesLabelVisible(boolean b) {
		matchesLabel.setVisible(b);
	}
	
	/** Controls the visible of the "Find:" label.
	 * @param b whether the "Find:" label should be visible.
	 */
	public void setFindLabelVisible(boolean b) {
		findLabel.setVisible(b);
	}
	
	public AbstractButton getDoneButton() {
		return doneButton;
	}
	
	public void setMatchCaseVisible(boolean b) {
		matchCaseButton.setVisible(b);
	}
	
	public void setHighlightAllVisible(boolean b) {
		highlightAllButton.setVisible(b);
	}

	/** Sets the font for all the components in this search bar.
	 */
	@Override
	public void setFont(Font f) {
		for(int a = 0; a<getComponentCount(); a++) {
			Component c = getComponent(a);
			if(c instanceof JButton) {
				((JButton)c).setFont(f);
			} else if(c instanceof JCheckBox) {
				//do nothing
			} else if(c instanceof JToggleButton) {
				((JToggleButton)c).setFont(f);
			} else if(c instanceof JLabel) {
				((JLabel)c).setFont(f);
			} else if(c instanceof JTextField) {
				((JTextField)c).setFont(f);
			}
		}
	}
	
	/** Controls whether icons are used for the next/previous buttons.
	 * If false, then the words "Next" and "Previous" are used.
	 * @param b whether arrow icons are used for the next and previous buttons.
	 */
	public void setUseArrowIcons(boolean b) {
		useArrowIcons = b;
		updateLayout();
	}

	/** Controls whether an icon is used for the close button.
	 * If false, then the word "Done" is used.
	 * @param b whether the close button uses an icon or the word "Done".
	 */
	public void setUseCloseIcon(boolean b) {
		useCloseIcon = b;
		updateLayout();
	}
	
	protected void updateLayout() {
		removeAll();
		
		GridBagConstraints c = new GridBagConstraints();
		
		if(alignment==SwingConstants.LEFT) {
			c.gridx = 0; c.gridy = 0;
			c.weightx = 0; c.fill = GridBagConstraints.VERTICAL;
			c.insets = new Insets(3,8,3,6);
			add(doneButton,c);
			c.insets = new Insets(3,3,3,3);
			c.gridx++;
			add(findLabel,c);
			c.gridx++;
			add(searchField,c);
			c.gridx++; c.insets = new Insets(3,3,3,0);
			add(nextButton,c);
			c.gridx++; c.insets = new Insets(3,0,3,3);
			add(prevButton,c);
			c.gridx++; c.insets = new Insets(3,3,3,3);
			add(highlightAllButton,c);
			c.gridx++;
			add(matchCaseButton,c);
			c.gridx++;
			add(matchesLabel,c);
			c.gridx++; c.weightx = 1;
			c.insets = new Insets(0,0,0,0);
			nextButton.putClientProperty("JButton.segmentPosition","first");
			prevButton.putClientProperty("JButton.segmentPosition","last");
			JPanel fluff = new JPanel();
			fluff.setOpaque(false);
			add(fluff,c);
		} else if(alignment==SwingConstants.RIGHT) {
			c.gridx = 0; c.gridy = 0;
			c.weightx = 1;
			c.insets = new Insets(0,0,0,0);
			JPanel fluff = new JPanel();
			fluff.setOpaque(false);
			add(fluff,c);
			c.weightx = 0; c.fill = GridBagConstraints.VERTICAL;
			c.insets = new Insets(3,3,3,3);
			c.gridx++;
			add(matchesLabel,c);
			c.gridx++; c.insets = new Insets(3,3,3,0);
			add(prevButton,c);
			c.gridx++; c.insets = new Insets(3,0,3,3);
			add(nextButton,c);
			c.insets = new Insets(3,3,3,3);
			c.gridx++;
			add(highlightAllButton,c);
			c.gridx++;
			add(matchCaseButton,c);
			c.gridx++;
			add(findLabel,c);
			c.gridx++;
			add(searchField,c);
			c.gridx++;
			add(doneButton,c);
			
			prevButton.putClientProperty("JButton.segmentPosition","first");
			nextButton.putClientProperty("JButton.segmentPosition","last");
		} else {
			throw new IllegalArgumentException("the alignment must be SwingConstants.LEFT or SwingConstants.RIGHT");
		}
		
		updateLocale();
		
		invalidate();
	}
	
	/** Sets the ButtonUI for all the non-checkbox buttons
	 * in this search bar.
	 * @param ui the button UI to use for all non-checkbox buttons.
	 */
	public void setButtonUI(ButtonUI ui) {
		for(int a = 0; a<getComponentCount(); a++) {
			if(getComponent(a) instanceof JButton) {
				((JButton)getComponent(a)).setUI(ui);
			} else if(getComponent(a) instanceof JCheckBox) {
				//do nothing
			} else if(getComponent(a) instanceof JToggleButton) {
				((JToggleButton)getComponent(a)).setUI(ui);
			}
		}
		updateLayout(); //must update the UI of the close button correctly
	}
	
	private static BasicButtonUI basicUI = new BasicButtonUI();
	
	protected void updateLocale() {
		findLabel.setText(strings.getString("find"));
		highlightAllButton.setText(strings.getString("highlightAll"));
		matchCaseButton.setText(strings.getString("matchCase"));
		nextButton.setToolTipText(strings.getString("nextTip"));
		prevButton.setToolTipText(strings.getString("previousTip"));
		highlightAllButton.setToolTipText(strings.getString("highlightAllTip"));
		matchCaseButton.setToolTipText(strings.getString("matchCaseTip"));
		
		if(useArrowIcons) {
			nextButton.setIcon(nextIcon);
			prevButton.setIcon(prevIcon);
			nextButton.setText("");
			prevButton.setText("");
		} else {
			nextButton.setIcon(null);
			prevButton.setIcon(null);
			nextButton.setText(strings.getString("next"));
			prevButton.setText(strings.getString("previous"));
		}

		if(useCloseIcon) {
			doneButton.setBorderPainted(false);
			doneButton.setContentAreaFilled(false);
			doneButton.setIcon(closeIcon);
			doneButton.setRolloverIcon(closeRolloverIcon);
			doneButton.setPressedIcon(closePressedIcon);
			doneButton.setText("");
			doneButton.setUI(basicUI);
			doneButton.setBorder(null);
		} else {
			doneButton.setUI(nextButton.getUI());
			doneButton.setBorderPainted(true);
			doneButton.setContentAreaFilled(true);
			doneButton.setIcon(null);
			doneButton.setRolloverIcon(null);
			doneButton.setPressedIcon(null);
			doneButton.setText(strings.getString("done"));
			doneButton.setBorder(null);
		}
		doneButton.setToolTipText(strings.getString("doneTip"));
	}
	
	public void setMatchCase(boolean b) {
		matchCaseButton.setSelected(b);
		updateMatchLabel();
		getHighlightSheet().setMatchCase(b);
	}
	
	/** Creates a new <code>TextSearchBar</code> that affects
	 * the text component provided.
	 * @param jtc the text component this search bar searches.
	 */
	public TextSearchBar(JTextComponent jtc) {
		this();
		setTextComponent(jtc);
	}
	
	/** Activates the "highlight all" feature.
	 * @param b whether "highlight all" is active
	 */
	public void setHighlightAll(boolean b) {
		highlightAllButton.setSelected(b);
		highlightSheet.setActive(b);
	}
	
	/**
	 * Return the search text field.
	 * 
	 * @return the search text field.
	 */
	public JTextField getSearchField() {
		return searchField;
	}

	/**
	 * Return the next button.
	 * 
	 * @return the next button.
	 */
	public JButton getNextButton() {
		return nextButton;
	}

	/**
	 * Return the previous button.
	 * 
	 * @return the previous button.
	 */
	public JButton getPrevButton() {
		return prevButton;
	}
	
	/** @return the <code>TextHighlightSheet</code>
	 * that this search bar works with.
	 */
	public TextHighlightSheet getHighlightSheet() {
		return highlightSheet;
	}
	
	/** Assigns the <code>JTextComponent</code> this search bar
	 * affects.
	 * @param jtc the new text component this search bar controls. This may not be null.
	 * 
	 */
	public void setTextComponent(JTextComponent jtc) {
		if(textComponent!=null)
			textComponent.getDocument().removeDocumentListener(documentListener);
		
		textComponent = jtc;
		textComponent.getDocument().addDocumentListener(documentListener);
		highlightSheet = new TextHighlightSheet(textComponent);
	}
	
	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		if(b==false)
			SwingSearch.clearHighlights(textComponent);
	}

	private static DecimalFormat format = new DecimalFormat();
	
	protected void updateMatchLabel() {
		String phrase = searchField.getText();
		if(matchesLabel.isVisible()) {
			if(phrase.length()==0) {
				matchesLabel.setText("");
			} else{
				int occurrences = SwingSearch.countOccurrence(textComponent, phrase, matchCaseButton.isSelected());
				if(occurrences==0) {
					matchesLabel.setText(strings.getString("notFound"));
				} else if(occurrences==1) {
					matchesLabel.setText(strings.getString("oneOccurrence"));
				} else {
					String s = strings.getString("occurrences");
					s = s.replaceAll("#", format.format(occurrences));
					matchesLabel.setText(s);
				}
			}
		}		
	}
}