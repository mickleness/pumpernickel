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
package com.pump.desktop.error;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.pump.swing.DialogFooter;
import com.pump.swing.QDialog;
import com.pump.window.WindowList;

public class ErrorDialog extends QDialog {
	private static final long serialVersionUID = 1L;

	static WeakHashMap<Frame, WeakReference<ErrorDialog>> map = new WeakHashMap<>();

	public static ErrorDialog get() {
		Frame[] frames = WindowList.getFrames(true, false, false);
		Frame frame;
		if (frames.length == 0) {
			frame = new JFrame();
		} else {
			frame = frames[frames.length - 1];
		}

		ErrorDialog dialog = null;
		WeakReference<ErrorDialog> ref = map.get(frame);
		if (ref != null) {
			dialog = ref.get();
		}
		if (dialog == null) {
			dialog = new ErrorDialog(frame);
			map.put(frame, new WeakReference<>(dialog));
		}
		return dialog;
	}

	public static ErrorDialog[] getAll() {
		List<ErrorDialog> returnValue = new ArrayList<>();
		for (WeakReference<ErrorDialog> ref : map.values()) {
			ErrorDialog d = ref.get();
			if (d != null && d.isShowing())
				returnValue.add(d);
		}
		return returnValue.toArray(new ErrorDialog[returnValue.size()]);
	}

	static int idCtr = 0;

	Map<ThrowableDescriptor, JComponent> throwableMap = new LinkedHashMap<>();
	int id = idCtr++;

	public ErrorDialog(Frame frame) {
		super(frame, "Error");
		setIcon(QDialog.getIcon(QDialog.ERROR_MESSAGE));
		DialogFooter myFooter = DialogFooter.createDialogFooter(
				DialogFooter.OK_OPTION,
				DialogFooter.EscapeKeyBehavior.TRIGGERS_DEFAULT);
		setFooter(myFooter);
		myFooter.getButton(DialogFooter.OK_OPTION).addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						throwableMap.clear();
					}
				});

		setResizable(true);
		refreshContents();
	}

	JPanel throwablePanelContainer = new JPanel(new GridBagLayout());
	JScrollPane scrollPane = new JScrollPane(
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

	protected void refreshContents() {
		LinkedHashSet<JComponent> panels = new LinkedHashSet<>(
				throwableMap.values());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(3, 3, 10, 3);
		throwablePanelContainer.removeAll();
		for (JComponent panel : panels) {
			throwablePanelContainer.add(panel, c);
			c.gridy++;
		}
		if (panels.size() >= 2) {
			scrollPane.setViewportView(throwablePanelContainer);
			setContent(scrollPane);
		} else {
			scrollPane.setViewportView(null);
			setContent(throwablePanelContainer);
		}
		throwablePanelContainer.revalidate();
		throwablePanelContainer.invalidate();
		throwablePanelContainer.validate();
		getContentPane().revalidate();
		// TODO: resize dialog if necessary
	}

	@Override
	public String toString() {
		return "ErrorDialog-" + id + " frame=\""
				+ ((Frame) getOwner()).getTitle() + "\"";
	}

	public void addThrowables(ThrowableDescriptor[] throwables) {
		for (ThrowableDescriptor t : throwables) {
			JComponent jc = getComponent(t);
			throwableMap.put(t, jc);
		}
		refreshContents();
	}

	class ThrowablePanel extends JPanel {
		private static final long serialVersionUID = 1L;
		List<ThrowableDescriptor> throwables = new ArrayList<>();
		JLabel multiplierLabel = new JLabel();
		String msg;

		protected ThrowablePanel(ThrowableDescriptor t) {
			addThrowable(t);
			msg = getMessage(t);

			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
			c.fill = GridBagConstraints.BOTH;
			add(QDialog.createContentPanel(msg, t.throwable.getClass()
					.getName(), null, true), c);
			c.gridx++;
			c.weightx = 0;
			c.insets = new Insets(3, 3, 3, 3);
			add(multiplierLabel, c);

			multiplierLabel.setFont(multiplierLabel.getFont().deriveFont(
					Font.BOLD));
			multiplierLabel.setForeground(Color.lightGray);
		}

		protected void addThrowable(ThrowableDescriptor t) {
			throwables.add(t);
			multiplierLabel.setText(throwables.size() >= 2 ? "x"
					+ throwables.size() : " ");
		}

		protected String getMessage() {
			return msg;
		}

		protected String getMessage(ThrowableDescriptor throwable) {
			String msg = throwable.getUserFriendlyMessage();
			if (msg == null || msg.length() == 0)
				msg = throwable.throwable.getLocalizedMessage();
			if (msg == null || msg.length() == 0)
				msg = "An unknown error occurred.";
			return msg;
		}

		protected boolean matches(ThrowableDescriptor t) {
			return getMessage().equals(getMessage(t));
		}
	}

	protected JComponent getComponent(ThrowableDescriptor t) {
		for (JComponent jc : throwableMap.values()) {
			if (jc instanceof ThrowablePanel) {
				ThrowablePanel tp = (ThrowablePanel) jc;
				if (tp.matches(t)) {
					tp.addThrowable(t);
					return tp;
				}
			}
		}
		return new ThrowablePanel(t);
	}

	public Set<ThrowableDescriptor> getThrowables() {
		return new HashSet<ThrowableDescriptor>(throwableMap.keySet());
	}
}