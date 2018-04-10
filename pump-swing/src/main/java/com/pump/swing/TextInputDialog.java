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
import java.io.File;
import java.io.FileFilter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * This presents a {@link QDialog} showing a JTextField.
 */
public class TextInputDialog {

	/**
	 * This helps determine how UI controls should behave in a TextInputDialog.
	 */
	public static interface StringInputHandler {

		/**
		 * @return the number of milliseconds before
		 *         {@link #getAutoComplete(String)} should be invoked.
		 */
		public long getAutoCompleteDelay();

		/**
		 * @return the number of milliseconds before {@link #isAccept(String)}
		 *         should be invoked.
		 */
		public long getAcceptDelay();

		/**
		 * Converted text based on the input String, or null if no conversion is
		 * possible. For example if the input is "San Franci", then the output
		 * of this method might be "San Francisco".
		 * 
		 * @param input
		 *            the String the user has already typed.
		 * @return a suggestion for the completed String the user is trying to
		 *         type.
		 */
		public String getAutoComplete(String input);

		/**
		 * Return true if this String is acceptable input. This is used to
		 * enable/disable the "OK" button.
		 * 
		 * @param input
		 *            input to evaluate.
		 * @return true if the input is accepted.
		 */
		public boolean isAccept(String input);
	}

	/**
	 * This StringInputHandler helps format a String that matches a file path.
	 */
	public static class FilePathInputHandler implements StringInputHandler {

		FileFilter fileFilter;

		/**
		 * Create a FilePathInputHandler.
		 * 
		 * @param fileFilter
		 *            the optional FileFilter this handler uses to determine
		 *            what is and isn't an acceptable file.
		 */
		public FilePathInputHandler(FileFilter fileFilter) {
			if (fileFilter == null) {
				fileFilter = new FileFilter() {

					@Override
					public boolean accept(File pathname) {
						return true;
					}

				};
			}
			this.fileFilter = fileFilter;
		}

		@Override
		public long getAutoCompleteDelay() {
			return 250;
		}

		@Override
		public long getAcceptDelay() {
			return 0;
		}

		@Override
		public String getAutoComplete(String input) {
			File f = new File(input);
			if (fileFilter.accept(f))
				return null;

			int i = input.lastIndexOf(File.separator);
			if (i == -1) {
				return getAutocomplete("", input, File.listRoots());
			}

			String parentPath = input.substring(0, i);
			File parent = new File(parentPath);
			if (!parent.exists()) {
				return null;
			}
			return getAutocomplete(parentPath + File.separator,
					input.substring(i + 1), parent.listFiles());
		}

		private String getAutocomplete(String prefix, String searchPhrase,
				File[] candidates) {
			if (candidates == null)
				candidates = new File[] {};
			for (File file : candidates) {
				/*
				 * The FileFilter shouldn't be consulted here because the user
				 * might be typing in the first few folders in the file path.
				 */
				if (file.getName().startsWith(searchPhrase)) {
					return prefix + file.getName();
				}
			}
			return null;
		}

		@Override
		public boolean isAccept(String input) {
			File file = new File(input);
			return file.exists() && file.isDirectory();
		}
	}

	/**
	 * Show a QDialog prompting the user for text input. This method reads and
	 * writes a value from the Preferences object provided. (If you don't want
	 * to use Preferences, use another static method.)
	 * 
	 * @param frame
	 *            the frame that will own the dialog.
	 * @param dialogTitle
	 *            the title of the dialog.
	 * @param boldMessage
	 *            the optional bold message for the content pane.
	 * @param plainMessage
	 *            the optional plain message for the content pane to display
	 *            below the bold message.
	 * @param textFieldPrompt
	 *            the optional text field prompt.
	 * @param textFieldToolTip
	 *            the optional text field tooltip.
	 * @param defaultTextFieldText
	 *            the text to show in the text field if the preferences don't
	 *            offer any starting text.
	 * @param prefs
	 *            the preferences to consult.
	 * @param preferenceKey
	 *            the key to consult in the Preferences argument.
	 * @param handler
	 *            the optional handler used to control UI elements in the
	 *            dialog.
	 * @return the String the user entered, or null if the user cancelled the
	 *         dialog.
	 */
	public static String show(Frame frame, String dialogTitle,
			String boldMessage, String plainMessage, String textFieldPrompt,
			String textFieldToolTip, String defaultTextFieldText,
			Preferences prefs, String preferenceKey, StringInputHandler handler) {
		String initialText = prefs.get(preferenceKey, defaultTextFieldText);
		String returnValue = show(frame, dialogTitle, boldMessage,
				plainMessage, textFieldPrompt, textFieldToolTip, initialText,
				handler);

		if (returnValue != null) {
			prefs.put(preferenceKey, returnValue);
		}
		try {
			prefs.sync();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		return returnValue;

	}

	/**
	 * Show a QDialog prompting the user for text input.
	 * 
	 * @param frame
	 *            the frame that will own the dialog.
	 * @param dialogTitle
	 *            the title of the dialog.
	 * @param boldMessage
	 *            the optional bold message for the content pane.
	 * @param plainMessage
	 *            the optional plain message for the content pane to display
	 *            below the bold message.
	 * @param textFieldPrompt
	 *            the optional text field prompt.
	 * @param textFieldToolTip
	 *            the optional text field tooltip.
	 * @param initialTextFieldText
	 *            the initial text to populate the text field with.
	 * @param handler
	 *            the optional handler used to control UI elements in the
	 *            dialog.
	 * @return the String the user entered, or null if the user cancelled the
	 *         dialog.
	 */
	public static String show(Frame frame, String dialogTitle,
			String boldMessage, String plainMessage, String textFieldPrompt,
			String textFieldToolTip, String initialTextFieldText,
			StringInputHandler handler) {
		TextInputDialog textInputDialog = new TextInputDialog(frame,
				dialogTitle, boldMessage, plainMessage, textFieldPrompt,
				textFieldToolTip, initialTextFieldText, handler);
		return textInputDialog.show();
	}

	private class MyDocumentListener implements DocumentListener {

		private class RefreshAcceptTask extends TimerTask {
			String text;

			RefreshAcceptTask(String text) {
				this.text = text;
			}

			@Override
			public void run() {
				synchronized (MyDocumentListener.this) {
					boolean accept = handler.isAccept(text);
					refreshAcceptTask = null;
					refreshAccept(accept);
				}
			}

		}

		private class RefreshAutocompleteTask extends TimerTask {
			String text;

			RefreshAutocompleteTask(String text) {
				this.text = text;
			}

			@Override
			public void run() {
				synchronized (MyDocumentListener.this) {
					String autocompleteText = handler.getAutoComplete(text);
					refreshAutocompleteTask = null;
					if (autocompleteText != null) {
						refreshAutocomplete(autocompleteText);
					}
				}
			}

		}

		StringInputHandler handler;
		RefreshAcceptTask refreshAcceptTask;
		RefreshAutocompleteTask refreshAutocompleteTask;

		MyDocumentListener(StringInputHandler handler) {
			this.handler = handler;
			refreshUI();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			refreshUI();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			refreshUI();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			refreshUI();
		}

		private void refreshUI() {
			String text = textField.getText();

			long acceptDelay = handler.getAcceptDelay();
			if (acceptDelay <= 0) {
				okButton.setEnabled(handler.isAccept(text));
			} else {
				okButton.setEnabled(false);

				synchronized (this) {
					if (refreshAcceptTask != null)
						refreshAcceptTask.cancel();
					refreshAcceptTask = new RefreshAcceptTask(text);
					timer.schedule(refreshAcceptTask, acceptDelay);
				}
			}

			if (autocompleteCtr == 0) {
				long autocompleteDelay = handler.getAutoCompleteDelay();
				if (autocompleteDelay <= 0) {
					String autocompleteText = handler.getAutoComplete(text);
					if (autocompleteText != null)
						refreshAutocomplete(autocompleteText);
				} else {
					synchronized (this) {
						if (refreshAutocompleteTask != null)
							refreshAutocompleteTask.cancel();
						refreshAutocompleteTask = new RefreshAutocompleteTask(
								text);
						timer.schedule(refreshAutocompleteTask,
								autocompleteDelay);
					}
				}
			}
		}

		void refreshAutocomplete(final String autocompleteText) {
			if (!SwingUtilities.isEventDispatchThread()) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						refreshAutocomplete(autocompleteText);
					}
				});
				return;
			}

			if (autocompleteText.equals(textField.getText()))
				return;

			autocompleteCtr++;
			try {
				int s1 = textField.getSelectionStart();
				int s2 = textField.getSelectionEnd();
				String origText = textField.getText();
				textField.setText(autocompleteText);
				if (s1 == origText.length() && s2 == origText.length()) {
					textField.setSelectionStart(s1);
					textField.setSelectionEnd(autocompleteText.length());
				} else {
					textField.setSelectionStart(Math.max(0,
							Math.min(s1, autocompleteText.length())));
					textField.setSelectionEnd(Math.max(0,
							Math.min(s2, autocompleteText.length())));
				}
			} finally {
				autocompleteCtr--;
			}
		}

		void refreshAccept(final boolean accept) {
			if (!SwingUtilities.isEventDispatchThread()) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						refreshAccept(accept);
					}
				});
				return;
			}
			okButton.setEnabled(accept);
		}

	};

	/**
	 * A static Timer to help with UI and TimerTasks. TODO: migrate to a helper
	 * class for multiple UI elements.
	 */
	static Timer timer = new Timer();

	JTextField textField = new JTextField();
	JButton okButton;
	DialogFooter footer = DialogFooter.createDialogFooter(
			DialogFooter.OK_CANCEL_OPTION,
			DialogFooter.EscapeKeyBehavior.TRIGGERS_CANCEL);
	JComponent content;
	QDialog dialog;
	DocumentListener docListener;
	private int autocompleteCtr = 0;

	/**
	 * Create a QDialog to prompt the user for text input. This method should be
	 * called on the EDT.
	 * 
	 * @param frame
	 *            the frame that will own the dialog.
	 * @param dialogTitle
	 *            the title of the dialog.
	 * @param boldMessage
	 *            the optional bold message for the content pane.
	 * @param plainMessage
	 *            the optional plain message for the content pane to display
	 *            below the bold message.
	 * @param textFieldPrompt
	 *            the optional text field prompt.
	 * @param textFieldToolTip
	 *            the optional text field tooltip.
	 * @param initialTextFieldText
	 *            the initial text to populate the text field with.
	 * @param handler
	 *            the optional handler used to control UI elements in the
	 *            dialog.
	 */
	public TextInputDialog(Frame frame, String dialogTitle, String boldMessage,
			String plainMessage, String textFieldPrompt,
			String textFieldToolTip, String initialTextFieldText,
			StringInputHandler handler) {
		if (textFieldPrompt != null && !textFieldPrompt.trim().isEmpty()) {
			new TextFieldPrompt(textField, textFieldPrompt);
		}

		if (textFieldToolTip != null && textFieldToolTip.trim().length() > 0) {
			textField.setToolTipText(textFieldToolTip);
		}

		if (initialTextFieldText != null) {
			textField.setText(initialTextFieldText);
		}

		content = QDialog.createContentPanel(boldMessage, plainMessage,
				textField, true);
		dialog = new QDialog(frame, dialogTitle,
				QDialog.getIcon(QDialog.PLAIN_MESSAGE), content, footer, true);
		okButton = footer.getButton(DialogFooter.OK_OPTION);

		if (handler != null) {
			docListener = new MyDocumentListener(handler);
			textField.getDocument().addDocumentListener(docListener);
		}

	}

	/**
	 * Show this dialog. This method should be called on the EDT.
	 * 
	 * @return the String the user input, or null if the user cancelled out of
	 *         this dialog.
	 */
	public String show() {
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		if (okButton == footer.getLastSelectedComponent()) {
			return textField.getText();
		}
		return null;
	}
}