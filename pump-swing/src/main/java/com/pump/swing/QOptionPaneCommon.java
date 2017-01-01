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

import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import com.pump.io.FileTreeIterator;
import com.pump.plaf.BasicQOptionPaneUI;
import com.pump.swing.DialogFooter.EscapeKeyBehavior;
import com.pump.util.JVM;

/** A set of static methods for common dialog needs.
 */
public class QOptionPaneCommon {
	private static final ResourceBundle strings = ResourceBundle.getBundle("com/bric/swing/resources/QOptionPaneCommon") ;
	private static boolean debugWithScreenshots = false;
	
	/** Use this for the a normal save dialog.
	 */
	public static final int FILE_NORMAL = 0;
	
	/** Use this to save a file that has experienced external changes.
	 * 
	 */
	public static final int FILE_EXTERNAL_CHANGES = 1;
	
	/** A possible return value for <code>showReviewChangesDialog()</code>
	 * indicating the user wants to review all dirty documents.
	 */
	public static final int REVIEW_CHANGES_OPTION = 444;
	
	/** A possible return value for <code>showReviewChangesDialog()</code>
	 * indicating the user wants to discard all changes to dirty documents.
	 */
	public static final int DISCARD_CHANGES_OPTION = 445;

	/** Display a dialog prompting the user to save, don't save, or cancel.
	 * 
	 * @param owner the frame that hosts this dialog.
	 * @param appName the application name
	 * @param documentName the name of the document
	 * @param filePath the path of the document
	 * @param saveProducesDialog whether clicking "Save" will produce another dialog.
	 * (Ellipses may be appended to the save button in this case.)
	 * @return one of the QDialog constants for SAVE, DONT_SAVE or CANCEL.
	 */
	public static int showSaveDialog(Frame owner,String appName,String documentName,String filePath,boolean saveProducesDialog,int fileState,boolean useSheets) {
		QOptionPane savePane = createSaveDialog(appName, documentName, filePath, saveProducesDialog, fileState);
		return savePane.showDialog(owner, useSheets);
	}
	
	/** Display a dialog prompting the user to save, don't save, or cancel.
	 * <p>Note this produces does not have a return value because the
	 * resulting JInternalFrame is not modal (so it doesn't return immediately).
	 * This method exists only as a demonstration.
	 * 
	 * @param desktopPane the desktopPane that contains this dialog.
	 * @param appName the application name
	 * @param documentName the name of the document
	 * @param filePath the path of the document
	 * @param saveProducesDialog whether clicking "Save" will produce another dialog.
	 * (Ellipses may be appended to the save button in this case.)
	 * @return the internal frame created.
	 */
	public static JInternalFrame showSaveDialog(JDesktopPane desktopPane,String appName,String documentName,String filePath,boolean saveProducesDialog,int fileState) {
		QOptionPane savePane = createSaveDialog(appName, documentName, filePath, saveProducesDialog, fileState);
		return savePane.showDialog(desktopPane);
	}

	/** Display a dialog prompting the user to review all unsaved changes, discard them, or cancel.
	 * <p>This is similar to a save dialog, except it is shown when the user has multiple 
	 * unsaved files open.
	 * <P>Pressing the escape key triggers the cancel button, but this button
	 * does not have mnemonics for "Review Changes..." and "Discard Changes".
	 * I assume this is because the most likely mnemonic for "Discard Changes" is
	 * "D", and if the user skims this dialog and automatically tries to select
	 * the "D" option without thinking: that user is risking losing much more
	 * data than the foremost window alone might suggest.
	 * 
	 * @param owner the frame that contains this dialog.
	 * @param appName the application name.
	 * @param documentCount the number of open documents.
	 * @return REVIEW_CHANGES_OPTION, DISCARD_CHANGES_OPTION, or DialogFooter.CANCEL_OPTION.
	 */
	public static int showReviewChangesDialog(Frame owner,String appName,int documentCount,boolean useSheets) {
		QOptionPane reviewPane = createReviewChangesDialog(appName, documentCount);
		return reviewPane.showDialog(owner,useSheets);
	}

	/** Display a dialog prompting the user to review all unsaved changes, discard them, or cancel.
	 * <p>This is similar to a save dialog, except it is shown when the user has multiple 
	 * unsaved files open.
	 * <P>Pressing the escape key triggers the cancel button, but this button
	 * does not have mnemonics for "Review Changes..." and "Discard Changes".
	 * I assume this is because the most likely mnemonic for "Discard Changes" is
	 * "D", and if the user skims this dialog and automatically tries to select
	 * the "D" option without thinking: that user is risking losing much more
	 * data than the foremost window alone might suggest.
	 * <p>Note this produces does not have a return value because the
	 * resulting JInternalFrame is not modal (so it doesn't return immediately).
	 * This method exists only as a demonstration.
	 * 
	 * @param desktopPane the desktopPane that contains this dialog.
	 * @param appName the application name.
	 * @param documentCount the number of open documents.
	 * @return the internal frame created.
	 */
	public static JInternalFrame showReviewChangesDialog(JDesktopPane desktopPane,String appName,int documentCount) {
		QOptionPane reviewPane = createReviewChangesDialog(appName, documentCount);
		return reviewPane.showDialog(desktopPane);
	}
	
	private static QOptionPane createSaveDialog(String appName,String documentName,String filePath,boolean saveProducesDialog,int fileState) {
		String mainMessage, comment, screenshot = null;
		Integer textWidth = null;
		int options = DialogFooter.SAVE_DONT_SAVE_CANCEL_OPTION;
		EscapeKeyBehavior escapeBehavior = EscapeKeyBehavior.TRIGGERS_CANCEL;
		int iconType;
		
		if(JVM.isWindowsXP) {
			iconType = QOptionPane.ICON_WARNING;
		} else if(JVM.isWindows) {
			iconType = QOptionPane.ICON_NONE;
		} else {
			iconType = QOptionPane.ICON_QUESTION;
		}
		
		if(documentName==null)
			documentName = strings.getString("untitledDocumentName");
		if(fileState==FILE_EXTERNAL_CHANGES) {
			mainMessage = strings.getString("dialogMacSaveExternalChangesMessage").replace("^0", documentName);
			comment = strings.getString("dialogMacSaveExternalChangesComment");
			screenshot = "save_mac_external.png";
			textWidth = new Integer(300);
			options = DialogFooter.DONT_SAVE_SAVE_OPTION;
			escapeBehavior = EscapeKeyBehavior.TRIGGERS_DEFAULT;
		} else if(fileState==FILE_NORMAL) {
			if(JVM.isMac) {
				mainMessage = strings.getString("dialogMacSavePromptMessage").replace("^0", documentName);
				comment = strings.getString("dialogMacSavePromptComment");
			} else {
				String documentString = filePath;
				if(documentString==null) {
					documentString = documentName;
				}
				if(documentString!=null) {
					mainMessage = strings.getString("dialogVistaSavePromptTitled");
				} else {
					mainMessage = strings.getString("dialogVistaSavePromptUntitled");
				}
				mainMessage = mainMessage.replace("^0", documentString);
				comment = null;
			}
			if(JVM.isMac)
				screenshot = "save_mac.png";
			if(JVM.isWindows7)
				screenshot = "save_windows7.png";
		} else {
			throw new IllegalArgumentException("unrecognized file state ("+fileState+")");
		}
		QOptionPane pane = new QOptionPane( 
					mainMessage, comment, iconType );
		if(textWidth!=null)
			pane.putClientProperty( BasicQOptionPaneUI.KEY_MESSAGE_WIDTH, textWidth);
		pane.setDialogTitle( appName );
		if(debugWithScreenshots && screenshot!=null) {
			try {
				File dir = new File(System.getProperty("user.dir"));
				File screenshotFile = FileTreeIterator.find(dir, screenshot);
				BufferedImage img = ImageIO.read(screenshotFile);
				pane.putClientProperty("debug.ghost.image", img);
			} catch(IOException e) {
				RuntimeException e2 = new RuntimeException();
				e2.initCause(e);
				throw e2;
			}
		}
		
		DialogFooter footer = DialogFooter.createDialogFooter(options, 
				escapeBehavior);
		pane.setDialogFooter(footer);
		if(saveProducesDialog)
			footer.getButton(DialogFooter.SAVE_OPTION).setText( DialogFooter.strings.getString("dialogSaveButton.produceDialog") );
		return pane;
	}

	
	private static QOptionPane createReviewChangesDialog(String appName,int documentCount) {
		String mainMessage, comment, screenshot;
		Integer textWidth = null;
		int iconType;
		
		if(JVM.isWindowsXP) {
			iconType = QOptionPane.ICON_WARNING;
		} else if(JVM.isWindows) {
			iconType = QOptionPane.ICON_NONE;
		} else {
			iconType = QOptionPane.ICON_QUESTION;
		}
		
		JButton reviewChangesButton = new JButton(strings.getString("dialogReviewChangesButton"));
		JButton discardChangesButton = new JButton(strings.getString("dialogDiscardChangesButton"));
		JButton cancelButton = DialogFooter.createCancelButton(true);
		JButton[] actionButtons;
		if(JVM.isMac) {
			actionButtons = new JButton[] { reviewChangesButton, cancelButton, discardChangesButton};
		} else {
			actionButtons = new JButton[] { reviewChangesButton, discardChangesButton, cancelButton};
		}
		DialogFooter.setUnsafe(discardChangesButton, true);
		reviewChangesButton.putClientProperty(DialogFooter.PROPERTY_OPTION, new Integer(REVIEW_CHANGES_OPTION));
		discardChangesButton.putClientProperty(DialogFooter.PROPERTY_OPTION, new Integer(DISCARD_CHANGES_OPTION));
		
		DialogFooter footer = new DialogFooter(null,actionButtons,true,reviewChangesButton);
		
		mainMessage = strings.getString("dialogMacMultipleUnsavedMessage");
		mainMessage = mainMessage.replace("^0", Integer.toString(documentCount));
		mainMessage = mainMessage.replace("^1", appName);
		comment = strings.getString("dialogMacMultipleUnsavedComment");
		
		screenshot = "save_mac_multiple.png";
		textWidth = new Integer(480);
		QOptionPane pane = new QOptionPane( 
					mainMessage, comment, iconType );
		pane.putClientProperty( BasicQOptionPaneUI.KEY_MESSAGE_WIDTH, textWidth);
		pane.setDialogTitle( appName );
		if(debugWithScreenshots) {
			try {
				File dir = new File(System.getProperty("user.dir"));
				File screenshotFile = FileTreeIterator.find(dir, screenshot);
				BufferedImage img = ImageIO.read(screenshotFile);
				pane.putClientProperty("debug.ghost.image", img);
			} catch(IOException e) {
				RuntimeException e2 = new RuntimeException();
				e2.initCause(e);
				throw e2;
			}
		}
		
		pane.setDialogFooter(footer);
		return pane;
	}
}