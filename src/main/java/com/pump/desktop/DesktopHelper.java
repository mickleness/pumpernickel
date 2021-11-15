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
package com.pump.desktop;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SecondaryLoop;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import com.pump.swing.DialogFooter;
import com.pump.swing.DialogFooter.EscapeKeyBehavior;
import com.pump.swing.JThrobber;
import com.pump.swing.QDialog;
import com.pump.util.JVM;

public class DesktopHelper {

	public static ResourceBundle strings = ResourceBundle
			.getBundle("com.pump.desktop.DesktopHelper");

	private static DesktopHelper desktopHelper = new DesktopHelper();

	public enum FileOperationType {
		REVEAL("reveal"), MOVE_TO_TRASH("moveToTrash");

		String id;

		FileOperationType(String id) {
			this.id = id;
		}

		public String getID() {
			return id;
		}
	}

	public static DesktopHelper get() {
		return desktopHelper;
	}

	public static void set(DesktopHelper desktopHelper) {
		Objects.requireNonNull(desktopHelper);
		DesktopHelper.desktopHelper = desktopHelper;
	}

	public interface FileOperation {
		boolean execute(File file) throws Exception;

		FileOperationType getType();
	}

	public FileOperation getOperation(FileOperationType type) {
		switch (type) {
		case REVEAL:
			if (JVM.isMac) {
				return new MacRevealOperation();
			} else if (JVM.isWindows) {
				// TODO Windows:
				// https://stackoverflow.com/questions/7357969/how-to-use-java-code-to-open-windows-file-explorer-and-highlight-the-specified-f/39445156
			}
			return null;
		case MOVE_TO_TRASH:
			if (JVM.isMac) {
				return new MacMoveToTrashOperation();
			} else if (JVM.isWindows) {
				// TODO Windows:
				// com.sun.jna.platform.win32.W32FileUtils
			}
			return null;
		}
		return null;
	}

	public abstract static class BlockingFileOperation implements FileOperation {

		protected final FileOperation op;
		private SecondaryLoop secondaryLoop;
		protected boolean operationResult;
		protected Exception operationException;

		private File file;
		private boolean threadFinished;
		private Semaphore semaphore = new Semaphore(1);

		/**
		 * The delay (in ms) we can block the EDT thread for before we create a
		 * dialog
		 */
		int delay = 200;

		public BlockingFileOperation(FileOperation op) {
			Objects.requireNonNull(op);
			this.op = op;
		}

		@Override
		public boolean execute(File file) throws Exception {
			Objects.requireNonNull(file);
			if (this.file != null)
				throw new IllegalStateException(
						"This operation should only be invoked once.");
			this.file = file;

			Thread thread = new Thread(op.getClass().getName() + " "
					+ file.getAbsolutePath()) {
				public void run() {
					try {
						operationResult = op
								.execute(BlockingFileOperation.this.file);
					} catch (Exception e) {
						operationException = e;
					} finally {
						semaphore.acquireUninterruptibly();
						try {
							threadFinished = true;
							if (secondaryLoop != null)
								secondaryLoop.exit();
						} finally {
							semaphore.release();
						}

						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								unblockUI();
							}
						});
					}
				}
			};
			if (SwingUtilities.isEventDispatchThread()) {
				thread.start();
				joinUninterruptibly(thread, delay);

				semaphore.acquireUninterruptibly();
				if (threadFinished) {
					semaphore.release();
				} else {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							semaphore.release();

							blockUI();
						}
					});
					secondaryLoop = Toolkit.getDefaultToolkit()
							.getSystemEventQueue().createSecondaryLoop();
					secondaryLoop.enter();
				}

			} else {
				thread.run();
			}

			if (operationException != null)
				throw operationException;

			return operationResult;
		}

		protected abstract void blockUI();

		protected abstract void unblockUI();

		protected File getFile() {
			return file;
		}

		private void joinUninterruptibly(Thread thread, int msDelay) {
			long start = System.currentTimeMillis();
			long elapsed = 0;
			while ((elapsed = System.currentTimeMillis() - start) < msDelay) {
				try {
					thread.join(delay - elapsed);
				} catch (InterruptedException e) {
					// do nothing
				}
			}
		}

		@Override
		public FileOperationType getType() {
			return op.getType();
		}
	}

	public static class ModalDialogFileOperation extends BlockingFileOperation {
		Frame frame;
		QDialog dialog;
		DialogFooter footer;
		String dialogTitle = getString("dialog", "title");
		String labelMsg = getString("dialog", "inProgressMessage");

		public ModalDialogFileOperation(Frame frame, FileOperation op) {
			super(op);
			this.frame = frame;
		}

		@Override
		protected void blockUI() {
			createDialog(true);
			dialog.setVisible(true);
		}

		private String getString(String leftHandSide, String rightHandSide) {
			leftHandSide = getType().getID() + "." + leftHandSide;

			String os = null;
			if (JVM.isMac)
				os = "mac";
			if (JVM.isWindows)
				os = "windows";

			if (os != null) {
				try {
					return strings.getString(leftHandSide + "." + os + "."
							+ rightHandSide);
				} catch (MissingResourceException mre) {
					// do nothing
				}
			}

			return strings.getString(leftHandSide + "." + rightHandSide);
		}

		private void createDialog(boolean pack) {
			JProgressBar progressBar = new JProgressBar();
			progressBar.setIndeterminate(true);

			Dimension d = progressBar.getPreferredSize();
			if (d.width < 250) {
				d.width = 250;
				progressBar.setPreferredSize(d);
			}

			JPanel content = new JPanel(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
			c.fill = GridBagConstraints.BOTH;
			c.insets = new Insets(3, 3, 3, 3);
			content.add(new JLabel(labelMsg), c);
			c.gridx++;
			c.weightx = 0;
			content.add(new JThrobber(), c);
			c.gridy++;
			c.gridx = 0;
			c.gridwidth = 2;
			content.add(progressBar, c);

			footer = DialogFooter.createDialogFooter(
					DialogFooter.OK_CANCEL_OPTION,
					EscapeKeyBehavior.TRIGGERS_DEFAULT);
			footer.getButton(DialogFooter.CANCEL_OPTION).setEnabled(false);
			footer.getButton(DialogFooter.OK_OPTION).setVisible(false);
			dialog = new QDialog(frame, dialogTitle, null, content, footer,
					false);
			if (pack)
				dialog.pack();
			dialog.setLocationRelativeTo(null);
		}

		@Override
		protected void unblockUI() {
			if (dialog != null && operationResult && operationException == null) {
				dialog.setVisible(false);
			} else if (operationResult == false || operationException != null) {
				boolean createDialog = dialog == null;

				String message, informativeText;
				if (operationException != null) {
					if (operationException instanceof FileNotFoundException) {
						message = getString("dialog.error.notFound", "message");
						informativeText = getString("dialog.error.notFound",
								"informativeText");
					} else {
						message = getString("dialog.error.generic", "message");
						informativeText = getString("dialog.error.generic",
								"informativeText");
					}

					if (isHumanReadable(operationException
							.getLocalizedMessage()))
						informativeText = operationException
								.getLocalizedMessage();
				} else {
					message = getString("dialog.failure.generic", "message");
					informativeText = getString("dialog.failure.generic",
							"informativeText");
				}
				message = message.replace("[0]", getFile().getName());
				informativeText = informativeText.replace("[0]", getFile()
						.getName());
				JComponent content = QDialog.createContentPanel(message,
						informativeText, null, true);

				Icon errorIcon = QDialog.getIcon(QDialog.ERROR_MESSAGE);
				if (createDialog) {
					footer = DialogFooter.createDialogFooter(
							DialogFooter.OK_OPTION,
							EscapeKeyBehavior.TRIGGERS_DEFAULT);
					dialog = new QDialog(frame, dialogTitle, errorIcon,
							content, footer, false);
					dialog.pack();
					dialog.setLocationRelativeTo(null);
				} else {
					dialog.setContent(content);
					dialog.setIcon(errorIcon);
					footer.getButton(DialogFooter.CANCEL_OPTION).setVisible(
							false);
					footer.getButton(DialogFooter.OK_OPTION).setVisible(true);
					dialog.pack();
				}
				dialog.setVisible(true);
			}
		}

		/**
		 * This is a vague guess at whether a message from an exception is a
		 * human-readable error message.
		 * 
		 * @param msg
		 *            a message from an exception.
		 * @return true if we should show this to the user as-is.
		 */
		private boolean isHumanReadable(String msg) {
			if (msg == null || msg.trim().length() == 0)
				return false;

			double score = 0;
			boolean lastCharWasWhitespace = false;
			for (int a = 0; a < msg.length(); a++) {
				char ch = msg.charAt(a);
				boolean isWhitespace = Character.isWhitespace(ch);
				if (isWhitespace & !lastCharWasWhitespace) {
					score++;
				} else if (isWhitespace && lastCharWasWhitespace) {
					score--;
				}

				lastCharWasWhitespace = isWhitespace;
			}
			char ch = msg.charAt(msg.length() - 1);
			if (ch == '.' || ch == '?' || ch == '!')
				score += 2;
			return score > 3;
		}
	}
}