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
package com.pump.text;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

/**
 * This abstract helper class triggers certain methods as a
 * <code>JTextComponent</code> is updated and constantly reapplies formatting
 * rules and highlights.
 */
public abstract class TextComponentHighlighter {
	protected JTextComponent jtc;

	private DocumentListener docListener = new DocumentListener() {

		@Override
		public void insertUpdate(DocumentEvent e) {
			refresh(true);
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			refresh(true);
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
		}

	};

	class RehighlightRunnable implements Runnable {
		boolean invokeLater;

		RehighlightRunnable(boolean invokeLater) {
			this.invokeLater = invokeLater;
		}

		@Override
		public void run() {
			synchronized (docListener) {
				if (dirty == false)
					return;
				dirty = false;

				documentTextChanged(false, invokeLater);
			}
		}
	}

	protected boolean active = true;
	private boolean dirty = true;

	/**
	 * Refresh the formatting and highlights.
	 * <p>
	 * It is not necessary for you to invoke this directly, unless the
	 * formatting/highlighting rules have changed. For example, if you are
	 * highlighting text based on a search text field, and the user types in
	 * that field, then you should manually invoke this method. Ultimately
	 * calling this method will trigger other protected methods to help apply
	 * formatting.
	 * 
	 * @param invokeLater
	 *            if true then this refresh is queued up via
	 *            {@code SwingUtilities.invokeLater()}, which may have the added
	 *            benefit of coalescing redundant calls. If false then this is
	 *            executed immediately. When in doubt, I recommend passing
	 *            "true" here.
	 */
	public void refresh(boolean invokeLater) {
		if (invokeLater) {
			/**
			 * Note: when you call textComponent.setText(..) for several lines
			 * of text, each line is added separately, so a 500-line block of
			 * text calls this method 500 times as each line is added. So this
			 * model waits until the EDT is responsive again to call
			 * documentUpdated().
			 */
			synchronized (docListener) {
				dirty = true;
				SwingUtilities.invokeLater(new RehighlightRunnable(true));
			}
		} else {
			new RehighlightRunnable(false).run();
		}
	}

	CaretListener caretListener = new CaretListener() {
		boolean dirty = false;
		Runnable rehighlightRunnable = new Runnable() {
			public void run() {
				synchronized (caretListener) {
					if (dirty == false)
						return;
					dirty = false;

					documentTextChanged(true, true);
				}
			}
		};

		@Override
		public void caretUpdate(CaretEvent e) {
			// see notes in the similar docListener
			synchronized (caretListener) {
				dirty = true;
				SwingUtilities.invokeLater(rehighlightRunnable);
			}
		}
	};

	/**
	 * Create a new TextComponentHighlighter.
	 * 
	 * @param jtc
	 *            the text component to apply formatting to.
	 */
	public TextComponentHighlighter(JTextComponent jtc) {
		this.jtc = jtc;
		jtc.addCaretListener(caretListener);
		addDocumentListeners();

		rehighlightText(TextComponentHighlighter.this.jtc.getText(),
				TextComponentHighlighter.this.jtc.getSelectionStart(),
				TextComponentHighlighter.this.jtc.getSelectionEnd(), true);
	}

	protected List<Object> allHighlights = new ArrayList<Object>();

	/**
	 * This method is called on the EDT after the text of the document is
	 * updated.
	 * <p>
	 * Note this method is not called <i>immediately</i>, it is wrapped in a
	 * SwingUtilities.invokeLater(..). When you call textComponent.setText(..)
	 * for a 500-line block of text, our DocumentListener is notified 500
	 * separate times about an update (because apparently each line is appended
	 * individually). So our listener coalesces these notifications and only
	 * calls this method once the EDT is responsive again.
	 */
	protected void documentTextChanged(boolean onlyCaretChanged,
			boolean invokeLater) {
		if (onlyCaretChanged)
			return;
		rehighlightText(jtc.getText(), jtc.getSelectionStart(),
				jtc.getSelectionEnd(), invokeLater);
	}

	/**
	 * Return true if this highlighter is active.
	 * 
	 * @return true if this highlighter is active.
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Toggle whether this highlighter is active or not.
	 * 
	 * @param a
	 *            the new active state
	 */
	public boolean setActive(boolean a) {
		if (active == a)
			return false;
		active = a;
		if (active)
			refresh(true);
		return true;
	}

	/**
	 * This reapplies highlights and AttributeSets to this text component.
	 * 
	 * @param text
	 *            the text to format.
	 * @param selectionStart
	 *            the current selection start.
	 * @param selectionEnd
	 *            the current selection end.
	 */
	protected void rehighlightText(final String text, final int selectionStart,
			final int selectionEnd, boolean invokeLater) {
		for (Object oldHighlight : allHighlights) {
			jtc.getHighlighter().removeHighlight(oldHighlight);
		}
		allHighlights.clear();

		Runnable changeStylesRunnable = new Runnable() {
			public void run() {
				removeDocumentListeners();
				try {
					Document doc = jtc.getDocument();
					if (!(doc instanceof StyledDocument)) {
						printOnce(
								"TextComponentHighlighter: Attributes were provided but the document does not support styled attributes.");
						return;
					}
					SimpleAttributeSet defaultAttributes = getDefaultAttributes();

					StyledDocument d = (StyledDocument) doc;
					d.setCharacterAttributes(0, d.getLength(),
							defaultAttributes, true);
					if (active) {
						formatParagraphAttributes(text, d, selectionStart,
								selectionEnd);
						formatTextComponent(text, d, selectionStart,
								selectionEnd);
					}
				} catch (BadLocationException e) {
					throw new RuntimeException(e);
				} finally {
					addDocumentListeners();
				}
			}
		};
		if (invokeLater) {
			SwingUtilities.invokeLater(changeStylesRunnable);
		} else {
			changeStylesRunnable.run();
		}
	}

	protected abstract void formatTextComponent(String text, StyledDocument d,
			int selectionStart, int selectionEnd) throws BadLocationException;

	/**
	 * This installs paragraph attributes. Currently it only
	 * applies a TabSet to reduce Swing's default tab size.
	 */
	protected void formatParagraphAttributes(String text, StyledDocument d,
			int selectionStart, int selectionEnd) {
		TabStop[] tabs = new TabStop[50];
		for (int j = 0; j < tabs.length; j++) {
			int tab = j + 1;
			tabs[j] = new TabStop(tab * 20);
		}
		TabSet tabSet = new TabSet(tabs);

		SimpleAttributeSet paragraphAttributes = new SimpleAttributeSet();
		StyleConstants.setTabSet(paragraphAttributes, tabSet);
		d.setParagraphAttributes(0, text.length(), paragraphAttributes, false);
	}

	private boolean hasDocListener = false;

	/**
	 * Remove document listeners. This should be followed by a call to
	 * {@link #addDocumentListeners()}.
	 * 
	 */
	protected synchronized void removeDocumentListeners() {
		jtc.getDocument().removeDocumentListener(docListener);
		hasDocListener = false;
	}

	/**
	 * Add document listeners to this text pane. This should be preceded by
	 * {@link #removeDocumentListeners()}.
	 * 
	 */
	protected synchronized void addDocumentListeners() {
		if (hasDocListener)
			return;
		jtc.getDocument().addDocumentListener(docListener);
		hasDocListener = true;
	}

	private SimpleAttributeSet defaultAttributes;

	/** Return the default attributes for this JTextComponent. */
	protected SimpleAttributeSet getDefaultAttributes() {
		if (defaultAttributes == null) {
			defaultAttributes = new SimpleAttributeSet();
			Font font = UIManager.getFont("TextField.font");
			Color color = UIManager.getColor("TextField.foreground");
			StyleConstants.setFontFamily(defaultAttributes, font.getFamily());
			StyleConstants.setFontSize(defaultAttributes, font.getSize());
			StyleConstants.setForeground(defaultAttributes, color);
		}
		return defaultAttributes;
	}

	static Set<String> printOnceMessages = new HashSet<String>();

	/**
	 * Print a message exactly once to <code>System.err</code>
	 * 
	 * @param msg
	 *            the message to printl
	 */
	protected void printOnce(String msg) {
		if (printOnceMessages.add(msg)) {
			System.err.println(msg);
		}
	}
}