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
package com.pump.text;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.pump.io.Token;
import com.pump.io.java.JavaParser;
import com.pump.io.java.JavaParser.WhitespaceToken;

/** This abstract helper class triggers certain methods as a <code>JTextComponent</code>
 * is updated and constantly reapplies formatting rules and highlights.
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

	protected boolean	active				= true;
	private boolean		dirty				= true;
	private Runnable	rehighlightRunnable	= new Runnable()
									{
										public void run()
										{
											synchronized (docListener)
											{
												if (dirty == false)
													return;
												dirty = false;

												documentTextChanged(false);
											}
										}
									};
									
	/** Refresh the formatting and highlights.
	 * <p>
	 * It is not necessary for you to invoke this directly, unless the
	 * formatting/highlighting rules have changed. For example, if you are
	 * highlighting text based on a search text field, and the user types
	 * in that field, then you should manually invoke this method. Ultimately
	 * calling this method will trigger other protected methods to help apply
	 * formatting.
	 * 
	 * @param invokeLater if true then this refresh is queued up
	 * via {@code SwingUtilities.invokeLater()}, which may have the added
	 * benefit of coalescing redundant calls. If false then this is
	 * executed immediately. When in doubt, I recommend passing "true" here.
	 */
	public void refresh(boolean invokeLater) {
		if(invokeLater) {
			/**
			 * Note: when you call textComponent.setText(..) for several lines of text, each line is added
			 * separately, so a 500-line block of text calls this method 500 times as each line is added. So
			 * this model waits until the EDT is responsive again to call documentUpdated().
			 */
			synchronized (docListener)
			{
				dirty = true;
				SwingUtilities.invokeLater(rehighlightRunnable);
			}
		} else {
			rehighlightRunnable.run();
		}
	}
	
	CaretListener caretListener = new CaretListener() {
		boolean		dirty				= false;
		Runnable	rehighlightRunnable	= new Runnable()
										{
											public void run()
											{
												synchronized (caretListener)
												{
													if (dirty == false)
														return;
													dirty = false;

													documentTextChanged(true);
												}
											}
										};
										
		@Override
		public void caretUpdate(CaretEvent e) {
			//see notes in the similar docListener
			synchronized (caretListener)
			{
				dirty = true;
				SwingUtilities.invokeLater(rehighlightRunnable);
			}
		}
	};
	
	/** Invoke this Runnable on the EDT to call rehighlightText(..). */
	protected Runnable refreshRunnable = new Runnable() {
		public void run() {
			rehighlightText(TextComponentHighlighter.this.jtc.getText(), 
					TextComponentHighlighter.this.jtc.getSelectionStart(), 
					TextComponentHighlighter.this.jtc.getSelectionEnd());
		}
	};

	/** Create a new TextComponentHighlighter.
	 * 
	 * @param jtc the text component to apply formatting to.
	 */
	public TextComponentHighlighter(JTextComponent jtc) {
		this.jtc = jtc;
		jtc.addCaretListener(caretListener);
		addDocumentListeners();
		
		SwingUtilities.invokeLater(refreshRunnable);
	}
	
	protected List<Object> allHighlights = new ArrayList<Object>();
	
	/** This helps cache parsed data to avoid lots of redundant overhead. */
	private static class ParseResults {
		Token[] tokens;
		RuntimeException rEx;
		Error	error;
		Token[] strippedTokens;
		
		ParseResults(String inputText) {
			try {
				tokens = JavaParser.parse(inputText, true);
			} catch(RuntimeException r) {
				rEx = r;
			} catch(Error e) {
				error = e;
			}
		}
		
		protected Token[] getTokens() {
			if(rEx!=null) throw rEx;
			if(error!=null) throw error;
			return tokens;
		}
		
		protected Token[] getStrippedTokens() {
			if(rEx!=null) throw rEx;
			if(error!=null) throw error;
			if(strippedTokens==null) {
				strippedTokens = stripWhitespace(tokens);
			}
			return strippedTokens;
		}
	}
	
	/** This manages cached results. */
	private LinkedHashMap<String, ParseResults> cachedValues = new LinkedHashMap<>();
	
	/** Return the Tokens that are parsed from this text.
	 * Because JavaParser.parse(..) can be expensive for complex code, this
	 * method caches some values. When possible you should avoid calling
	 * JavaParser.parse(..) and use this method instead.
	 * 
	 * @param text the java source code to parse.
	 * @param includeWhitespace true if WhitespaceTokens should be included, false if they should be stripped away.
	 * @return Return the Tokens that are parsed from this text.
	 */
	public synchronized Token[] getTokens(String text,boolean includeWhitespace) {
		if(!active) {
 			return new Token[] {
					new Token(text, 0, 0, 0)
			};
		}
		text = text.replace("\r\n", "\n");
		ParseResults results = cachedValues.get(text);
		if(results==null) {
			while(cachedValues.size()>3) {
				cachedValues.remove( cachedValues.keySet().iterator().next() );
			}
			results = new ParseResults(text);
			cachedValues.put(text, results);
		}
		if(includeWhitespace)
			return results.getTokens();
		return results.getStrippedTokens();
	}
	
	private static Token[] stripWhitespace(Token[] tokens) {
		List<Token> returnValue = new ArrayList<>();
		for(int a = 0; a<tokens.length; a++) {
			if(!(tokens[a] instanceof WhitespaceToken)) {
				returnValue.add(tokens[a]);
			}
		}
		return returnValue.toArray(new Token[returnValue.size()]);
	}
	
	/** This method is called on the EDT after the text of the document is updated.
	 * <p>Note this method is not called <i>immediately</i>, it is wrapped in
	 * a SwingUtilities.invokeLater(..). When you call textComponent.setText(..)
	 * for a 500-line block of text, our DocumentListener is notified 500 separate
	 * times about an update (because apparently each line is appended individually).
	 * So our listener coalesces these notifications and only calls this method
	 * once the EDT is responsive again.
	 */
	protected void documentTextChanged(boolean onlyCaretChanged) {
		if(onlyCaretChanged)
			return;
		rehighlightText(jtc.getText(), jtc.getSelectionStart(), jtc.getSelectionEnd());
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
	 * @param a the new active state
	 */
	public boolean setActive(boolean a) {
		if(active==a)
			return false;
		active = a;
		if(active)
			refresh(true);
		return true;
	}
	
	/** This reapplies highlights and AttributeSets to this text component.
	 * 
	 * @param text the text to format.
	 * @param selectionStart the current selection start.
	 * @param selectionEnd the current selection end.
	 */
	protected void rehighlightText(String text,int selectionStart,int selectionEnd) {
		for(Object oldHighlight : allHighlights) {
			jtc.getHighlighter().removeHighlight(oldHighlight);
		}
		allHighlights.clear();
		
		Token[] tokens = getTokens(text, true);
		final Map<Token, AttributeSet> tokenAttributes = new TreeMap<Token, AttributeSet>();
		Runnable changeStylesRunnable = new Runnable() {
			public void run() {					
				removeDocumentListeners();
				try {
					Document doc = jtc.getDocument();
					if(! (doc instanceof StyledDocument)) {
						printOnce("TextComponentHighlighter: Attributes were provided but the document does not support styled attributes.");
						return;
					}
					SimpleAttributeSet defaultAttributes = getDefaultAttributes();
					
					StyledDocument d = (StyledDocument)doc;
					d.setCharacterAttributes(0, d.getLength(), defaultAttributes, true);
					if(active) {
    					for(Token token : tokenAttributes.keySet()) {
    						AttributeSet attr = tokenAttributes.get(token);
    						d.setCharacterAttributes(token.getDocumentStartIndex(), token.getLength(), attr, true);
    					}
					}
				} finally {
					addDocumentListeners();
				}
			}
		};
		try {
			if(active) {
    			for(int index = 0; index<tokens.length; index++) {
    				HighlightPainter painter = getHighlightPainter(tokens, index, selectionStart, selectionEnd);
    				if(painter!=null) {
    					allHighlights.add(
    							jtc.getHighlighter().addHighlight( tokens[index].getDocumentStartIndex(), 
    									tokens[index].getDocumentEndIndex(), painter) );
    				}
    				AttributeSet attributes = getAttributes(tokens, index, selectionStart, selectionEnd);
    				if(attributes!=null) {
    					tokenAttributes.put(tokens[index], attributes);
    				}
    			}
			}
			SwingUtilities.invokeLater(changeStylesRunnable);
		} catch(BadLocationException e) {
			throw new RuntimeException(e);
		}
	}
	
	private boolean hasDocListener = false;

	/** Remove document listeners. This should be followed by a call
	 * to {@link #addDocumentListeners()}.
	 * 
	 */
	protected synchronized void removeDocumentListeners() {
		jtc.getDocument().removeDocumentListener(docListener);
		hasDocListener = false;
	}

	/** Add document listeners to this text pane.
	 * This should be preceded by {@link #removeDocumentListeners()}.
	 * 
	 */
	protected synchronized void addDocumentListeners() {
		if(hasDocListener)
			return;
		jtc.getDocument().addDocumentListener(docListener);
		hasDocListener = true;
	}
	
	/** Return the current array of Tokens.
	 * 
	 * @return the current array of Tokens.
	 */
	protected Token[] getTokens() {
		return getTokens(jtc.getText(), true);
	}
	
	private SimpleAttributeSet defaultAttributes;
	
	/** Return the default attributes for this JTextComponent. */
	protected SimpleAttributeSet getDefaultAttributes() {
		if(defaultAttributes==null) {
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
	
	/** Print a message exactly once to <code>System.err</code>
	 * 
	 * @param msg the message to printl
	 */
	protected void printOnce(String msg) {
		if(printOnceMessages.add(msg)) {
			System.err.println(msg);
		}
	}

	/** @return the HighlightPainter for tokenIndex in the list of tokens. */
	protected HighlightPainter getHighlightPainter(Token[] allToken,int tokenIndex,int selectionStart,int selectionEnd) {
		return null;
	}

	/** @return the AttributeSet for tokenIndex in the list of tokens. */
	protected AttributeSet getAttributes(Token[] allTokens,int tokenIndex,int selectionStart,int selectionEnd) {
		return null;
	}
}