package com.pump.showcase;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import com.pump.desktop.DesktopHelper;
import com.pump.desktop.DesktopHelper.FileOperation;
import com.pump.desktop.DesktopHelper.FileOperationType;
import com.pump.desktop.temp.TempFileManager;
import com.pump.inspector.ControlGridLayout;
import com.pump.inspector.Inspector;
import com.pump.util.JVM;

public class DesktopHelperDemo extends ShowcaseExampleDemo {
	private static final long serialVersionUID = 1L;

	class DelayedOperation implements FileOperation {
		FileOperation op;

		public DelayedOperation(FileOperation op) {
			this.op = op;
		}

		@Override
		public boolean execute(File file) throws Exception {
			long delayMS = (long) (((Number) delaySpinner.getValue())
					.doubleValue() * 1000);
			Thread.sleep(delayMS);

			if (falseReturnValue.isSelected())
				return false;
			if (error.isSelected())
				throw new RuntimeException("This is a simulated error.");

			return op.execute(file);
		}

		@Override
		public FileOperationType getType() {
			return op.getType();
		}

	}

	JSpinner delaySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10.0, 1));
	JRadioButton nothing = new JRadioButton("Nothing", true);
	JRadioButton falseReturnValue = new JRadioButton("False Return Value",
			false);
	JRadioButton error = new JRadioButton("Error", false);

	JRadioButton reveal = new JRadioButton("Reveal File", true);
	JRadioButton moveToTrash = new JRadioButton("Move File to "
			+ (JVM.isWindows ? "Recycle Bin" : "Trash"), false);

	JButton executeButton = new JButton("Execute");

	public DesktopHelperDemo() {

		Inspector inspector = new Inspector(configurationPanel);
		ControlGridLayout grid = new ControlGridLayout(3);
		inspector.addRow(new JLabel("Action:"),
				grid.createGrid(reveal, moveToTrash));
		inspector.addRow(new JLabel("Delay:"), delaySpinner);
		inspector.addRow(new JLabel("Simulate:"),
				grid.createGrid(nothing, falseReturnValue, error));

		reveal.setToolTipText("Reveal user.dir");
		moveToTrash.setToolTipText("Delete a temporary file");

		ButtonGroup g1 = new ButtonGroup();
		g1.add(nothing);
		g1.add(falseReturnValue);
		g1.add(error);

		ButtonGroup g2 = new ButtonGroup();
		g2.add(reveal);
		g2.add(moveToTrash);

		executeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				FileOperationType type = reveal.isSelected() ? FileOperationType.REVEAL
						: FileOperationType.MOVE_TO_TRASH;
				File file;
				if (type == FileOperationType.REVEAL) {
					file = new File(System.getProperty("user.dir"));
				} else {
					file = TempFileManager.get().createFile("throwawayFile",
							"txt");
					try {
						if (!file.createNewFile())
							throw new IOException("The file \""
									+ file.getAbsolutePath()
									+ "\" could not be created.");
					} catch (Exception e2) {
						e2.printStackTrace();
						return;
					}
				}
				FileOperation op = DesktopHelper.get().getOperation(type);
				op = new DelayedOperation(op);

				Frame frame = (Frame) SwingUtilities
						.getWindowAncestor(executeButton);
				try {
					new DesktopHelper.ModalDialogFileOperation(frame, op)
							.execute(file);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}

		});

		examplePanel.add(executeButton);
	}

	@Override
	public String getTitle() {
		return "DesktopHelper Demo";
	}

	@Override
	public String getSummary() {
		String dest = JVM.isWindows ? "recycle bin" : "trash";
		return "This demonstrates the DesktopHelper's ability to reveal a file or move a file to the "
				+ dest;
	}

	@Override
	public URL getHelpURL() {
		return DesktopHelperDemo.class.getResource("desktopHelperDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "trash", "recycle bin", "reveal", "show",
				"dialog", "feedback", "ux", "determinate", "thread" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { DesktopHelper.class };
	}

}
