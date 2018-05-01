package com.pump.desktop.edit;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.JTextComponent;

import com.pump.desktop.GroupedControls;

public class EditMenuControls extends GroupedControls {
	class EditMenuAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public EditMenuAction(String commandName) {
			Objects.requireNonNull(commandName);
			putValue(AbstractAction.ACTION_COMMAND_KEY, commandName);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String commandName = (String) getValue(AbstractAction.ACTION_COMMAND_KEY);
			JTextComponent jtc = (JTextComponent) KeyboardFocusManager
					.getCurrentKeyboardFocusManager().getFocusOwner();
			jtc.getActionMap().get(commandName)
					.actionPerformed(new ActionEvent(jtc, 0, commandName));
		}
	}

	enum Selection {
		EXISTS {
			@Override
			public boolean accepts(JTextComponent jtc) {
				int i1 = jtc.getSelectionStart();
				int i2 = jtc.getSelectionEnd();
				return i1 != i2;
			}
		},
		NOT_ALL {
			@Override
			public boolean accepts(JTextComponent jtc) {
				int i1 = jtc.getSelectionStart();
				int i2 = jtc.getSelectionEnd();
				int min = Math.min(i1, i2);
				int max = Math.max(i1, i2);
				boolean isAll = min == 0 && min != max
						&& max == jtc.getDocument().getLength();
				return !isAll;
			}
		};

		public abstract boolean accepts(JTextComponent jtc);
	}

	public EditMenuControls(boolean createCut, boolean createCopy,
			boolean createPaste, boolean createSelectAll) {
		if (createCut)
			registerAction(EditCommand.CUT, Selection.EXISTS, true);
		if (createCopy)
			registerAction(EditCommand.COPY, Selection.EXISTS, false);
		if (createPaste)
			registerAction(EditCommand.PASTE, null, true);
		if (createSelectAll)
			registerAction(EditCommand.SELECT_ALL, Selection.NOT_ALL, false);
	}

	private void registerAction(EditCommand<AbstractAction> command,
			final Selection selection, final boolean requiresEdits) {
		final AbstractAction action = new EditMenuAction(
				(String) command.getValue(AbstractAction.ACTION_COMMAND_KEY));
		PropertyChangeListener pcl = new PropertyChangeListener() {

			JTextComponent textComponent;
			CaretListener caretListener = new CaretListener() {

				@Override
				public void caretUpdate(CaretEvent e) {
					refresh();
				}

			};

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Component c = KeyboardFocusManager
						.getCurrentKeyboardFocusManager().getFocusOwner();

				if (c == textComponent)
					return;

				JTextComponent jtc = c instanceof JTextComponent ? ((JTextComponent) c)
						: null;

				if (textComponent != null)
					textComponent.removeCaretListener(caretListener);

				textComponent = jtc;

				if (textComponent != null)
					textComponent.addCaretListener(caretListener);

				refresh();
			}

			private void refresh() {
				if (textComponent == null) {
					action.setEnabled(false);
				} else if (!textComponent.isEditable() && requiresEdits) {
					action.setEnabled(false);
				} else if (selection == null) {
					action.setEnabled(true);
				} else {
					action.setEnabled(selection.accepts(textComponent));
				}
			}

		};
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addPropertyChangeListener("focusOwner", pcl);
		command.install(action);
		registerAction(action);
	}
}
