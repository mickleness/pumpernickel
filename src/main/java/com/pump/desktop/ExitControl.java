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
package com.pump.desktop;

import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.pump.UserCancelledException;
import com.pump.util.JVM;
import com.pump.window.WindowList;

/**
 * This helps manage a menu item to exit an application.
 * <p>
 * When exiting is requested this dispatched a WINDOW_CLOSING event to your
 * frame. If you already have code similar to the block below, then this
 * behavior will directly complement that model:
 * <p>
 * 
 * <pre>
 * setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
 * addWindowListener(new WindowAdapter() {
 * 
 * 	&#064;Override
 * 	public void windowClosing(WindowEvent e) {
 * 		String docName = &quot;Untitled&quot;;
 * 		int choice = QDialog.showSaveChangesDialog(ButtercupFrame.this,
 * 				docName, true);
 * 
 * 		if (choice == DialogFooter.SAVE_OPTION) {
 * 			save();
 * 		}
 * 		if (choice == DialogFooter.DONT_SAVE_OPTION
 * 				|| choice == DialogFooter.SAVE_OPTION) {
 * 			setVisible(false);
 * 		} else if (choice == DialogFooter.CANCEL_OPTION) {
 * 			throw new UserCancelledException();
 * 		}
 * 	}
 * });
 * </pre>
 * 
 */
public class ExitControl {

	protected AbstractAction exitAction = new AbstractAction(JVM.isMac ? "Quit"
			: "Exit") {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent evt) {
			Window[] window = WindowList.getWindows(true, false);
			for (int a = window.length - 1; a >= 0; a--) {
				WindowEvent windowEvent = new WindowEvent(window[a],
						WindowEvent.WINDOW_CLOSING);
				try {
					window[a].dispatchEvent(windowEvent);
					window[a].setVisible(false);
				} catch (UserCancelledException e) {
				}
			}
		}
	};

	protected JMenuItem exitItem = new JMenuItem(exitAction);

	public ExitControl() {
		char exitChar = JVM.isMac ? 'Q' : KeyEvent.VK_F4;
		exitItem.setAccelerator(KeyStroke.getKeyStroke(exitChar, Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	public AbstractAction getExitAction() {
		return exitAction;
	}

	public JMenuItem getExitMenuItem() {
		return exitItem;
	}
}