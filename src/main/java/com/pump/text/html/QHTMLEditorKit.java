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
	protected QStyleSheet mySheet;

	public QHTMLEditorKit() {
		super();

		// so... I'm not entirely why this is, but super.getStyleSheet
		// returns a STATIC StyleSheet. And it comes installed with all
		// sorts of important basic properties (like "ul { margin-left: 50px;
		// }"). So we want to incorporate that stylesheet, but we also want OUR
		// style sheet to be a locally maintained QStyleSheet:

		QStyleSheet newSheet = new QStyleSheet();

		StyleSheet superSheet = super.getStyleSheet();
		if (superSheet != null) {
			// do a null check just in case something changes in the future
			newSheet.addStyleSheet(superSheet);
		}
		setStyleSheet(newSheet);
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

	/**
	 * The incoming StyleSheet must be a QStyleSheet, or a ClassCastException
	 * will be thrown.
	 */
	@Override
	public void setStyleSheet(StyleSheet s) {
		mySheet = (QStyleSheet) s;
	}

	@Override
	public QStyleSheet getStyleSheet() {
		return mySheet;
	}

	@Override
	public ViewFactory getViewFactory() {
		return defaultFactory;
	}
}
