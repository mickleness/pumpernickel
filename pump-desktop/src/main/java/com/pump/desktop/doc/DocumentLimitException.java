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
package com.pump.desktop.doc;

/**
 * This exception is thrown when the number of currently open document exceeds
 * is either too low or too high.
 * <p>
 * This is a serious exception that may make a {@link DocumentControls} object
 * unusable. The UI should be constantly consulting the minimum and maximum
 * number of allowable documents and prevent the user from ever encountering
 * this exception.
 */
public class DocumentLimitException extends IllegalStateException {
	private static final long serialVersionUID = 1L;

	private static String getMessage(int documentCount, int documentMinimum,
			int documentMaximum) {
		if (documentCount < documentMinimum) {
			if (documentMinimum == 1 && documentCount == 0)
				return "At least one document must be open.";
			return "Document minimum is " + documentMinimum
					+ ", but the number of open documents is " + documentCount
					+ ".";
		}

		return "The maximum of " + documentMaximum
				+ " has been reached; you cannot open more documents.";
	}

	int documentCount, documentMinimum, documentMaximum;

	public DocumentLimitException(int documentCount, int documentMinimum,
			int documentMaximum) {
		super(getMessage(documentCount, documentMinimum, documentMaximum));
		this.documentCount = documentCount;
	}

	public int getDocumentCount() {
		return documentCount;
	}

	public int getDocumentMinimum() {
		return documentMinimum;
	}

	public int getDocumentMaximum() {
		return documentMaximum;
	}
}