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

import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.pump.io.FileUtils;
import com.pump.io.IOUtils;
import com.pump.swing.BasicConsole;
import com.pump.swing.ContextualMenuHelper;

public class ConsoleFrame {
	static ConsoleFrame GLOBAL;

	public static synchronized ConsoleFrame get() {
		if (GLOBAL == null) {
			GLOBAL = new ConsoleFrame();
		}
		return GLOBAL;
	}

	BasicConsole console = new BasicConsole(
			BasicConsole.DefaultPrintStream.EXTEND, false);
	JScrollPane scrollPane = new JScrollPane(console);
	JFrame consoleWindow;

	public void showConsole() {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					showConsole();
				}
			});
			return;
		}
		get().consoleWindow.setVisible(true);
		get().consoleWindow.toFront();
	}

	public void toggleConsole() {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					toggleConsole();
				}
			});
			return;
		}
		get().consoleWindow.setVisible(!consoleWindow.isVisible());
	}

	private ConsoleFrame() {
		consoleWindow = new JFrame("Console");
		consoleWindow.getContentPane().add(scrollPane);
		scrollPane.setPreferredSize(new Dimension(900, 500));
		consoleWindow.pack();
		ContextualMenuHelper.add(console, "Save as...", new Runnable() {

			@Override
			public void run() {
				FileDialog fd = new FileDialog(consoleWindow, "Save TXT As...",
						FileDialog.SAVE);
				fd.pack();
				fd.setLocationRelativeTo(null);
				fd.setVisible(true);

				if (fd.getFile() == null)
					return;
				File file = new File(fd.getDirectory() + fd.getFile());
				try {
					if ((!file.exists()))
						FileUtils.createNewFile(file);
					IOUtils.write(file, console.getText(), true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		});
	}

	public JButton createShowButton() {
		JButton button = new JButton("Show Console");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showConsole();
			}
		});
		return button;
	}
}