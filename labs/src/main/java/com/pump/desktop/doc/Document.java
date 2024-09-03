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
package com.pump.desktop.doc;

import java.beans.PropertyChangeListener;
import java.io.File;

public interface Document {

	/** This property relates to the {@link #isDirty()} method. */
	public static final String PROPERTY_IS_DIRTY = Document.class.getName()
			+ "#isDirty";
	/** This property relates to the {@link #getName()} method. */
	public static final String PROPERTY_NAME = Document.class.getName()
			+ "#name";
	/** This property relates to the {@link #getFile()} method. */
	public static final String PROPERTY_FILE = Document.class.getName()
			+ "#file";

	/**
	 * Return true if this document contains unsaved changes that can be
	 * discarded if the document is closed.
	 */
	boolean isDirty();

	/**
	 * Return the user-facing name we'll identify this Document with. This is
	 * usually a file name. This should not be null, but it may be "Untitled".
	 */
	String getName();

	/**
	 * Return the File associated with this Document, which may be null.
	 */
	File getFile();

	void addPropertyChangeListener(PropertyChangeListener propertyChangeListener);

	void removePropertyChangeListener(
			PropertyChangeListener propertyChangeListener);

}