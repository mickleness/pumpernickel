package com.pump.desktop.doc;

public class MissingSelectedDocumentException extends IllegalStateException {
	private static final long serialVersionUID = 1L;
	Document[] documents;
	Document selectedDocument;

	public MissingSelectedDocumentException(Document selectedDocument,
			Document... documents) {
		super(
				"The selected document is not in the list of currently open documents.");
		this.documents = documents;
		this.selectedDocument = selectedDocument;
	}

	public Document getSelectedDocument() {
		return selectedDocument;
	}

	public Document[] getDocuments() {
		return documents;
	}
}