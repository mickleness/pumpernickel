package com.pump.text.html;

import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 * This is an enhanced HTMLEditorKit.
 */
public class QHTMLEditorKit extends HTMLEditorKit {
	private static final long serialVersionUID = 1L;

	private static final ViewFactory defaultFactory = new QHTMLFactory();

	/**
	 * This class uses one style sheet per EditorKit.
	 * <p>
	 * The super classes uses a constant style sheet for the whole app. (I don't
	 * know what the backstory is there.)
	 */
	protected StyleSheet mySheet;

	public QHTMLEditorKit() {
		super();
		setStyleSheet(new QStyleSheet());
	}

	@Override
	public HTMLDocument createDefaultDocument() {
		// this is copied and pasted from HTMLEditorKit, see comments for
		// changes:

		StyleSheet styles = getStyleSheet();

		// create a QStyleSheet to handle special styles
		StyleSheet ss = createDocumentStyleSheet();

		ss.addStyleSheet(styles);

		HTMLDocument doc = new HTMLDocument(ss);
		doc.setParser(getParser());

		doc.setAsynchronousLoadPriority(4);
		doc.setTokenThreshold(100);
		return doc;
	}

	/**
	 * Create the base StyleSheet used when constructing the HTMLDocument.
	 */
	protected StyleSheet createDocumentStyleSheet() {
		return new QStyleSheet();
	}

	@Override
	public void setStyleSheet(StyleSheet s) {
		mySheet = s;
	}

	@Override
	public StyleSheet getStyleSheet() {
		return mySheet;
	}

	@Override
	public ViewFactory getViewFactory() {
		return defaultFactory;
	}
}
