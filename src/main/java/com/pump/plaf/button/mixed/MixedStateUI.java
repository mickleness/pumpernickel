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
package com.pump.plaf.button.mixed;

import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeListener;

/**
 * This installs/uninstalls a mixed state look on a JCheckBox. Different subclasses will require
 * a "mixed state" sets the selected state to TRUE or FALSE, depending on how the rendering requirements.
 * (That is: in a mixed state <code>myCheckBox.isSelected()</code> may return <code>true</code>
 * or <code>false</code> depending on the specific MixedStateUI in use. But in either case
 * {@link MixedState#getState(JCheckBox)} will return {@link MixedState#MIXED}.)
 */
public abstract class MixedStateUI {
	
	class MixedStateUIButtonModel implements ButtonModel, Serializable {
		
		private static final long serialVersionUID = 1L;

		private final ButtonModel delegateModel;

		public MixedStateUIButtonModel(ButtonModel delegateModel) {
			this.delegateModel = delegateModel;
		}

		@Override
		public Object[] getSelectedObjects() {
			return delegateModel.getSelectedObjects();
		}

		@Override
		public boolean isArmed() {
			return delegateModel.isArmed();
		}

		@Override
		public boolean isSelected() {
			return delegateModel.isSelected();
		}

		@Override
		public boolean isEnabled() {
			return delegateModel.isEnabled();
		}

		@Override
		public boolean isPressed() {
			return delegateModel.isPressed();
		}

		@Override
		public boolean isRollover() {
			return delegateModel.isRollover();
		}

		@Override
		public void setArmed(boolean b) {
			delegateModel.setArmed(b);
		}
		
		@Override
		public void setSelected(boolean b) {
			Thread currentThread = Thread.currentThread();
			if (editingThreads.add(currentThread)) {
				try {
					MixedState newState = b ? MixedState.SELECTED : MixedState.UNSELECTED;
					MixedState.set(checkBox, newState);
				} finally {
					editingThreads.remove(currentThread);
				}
			} else {
				delegateModel.setSelected(b);
			}
		}

		@Override
		public void setEnabled(boolean b) {
			delegateModel.setEnabled(b);
		}

		@Override
		public void setPressed(boolean b) {
			delegateModel.setPressed(b);
		}

		@Override
		public void setRollover(boolean b) {
			delegateModel.setRollover(b);
		}

		@Override
		public void setMnemonic(int key) {
			delegateModel.setMnemonic(key);
		}

		@Override
		public int getMnemonic() {
			return delegateModel.getMnemonic();
		}

		@Override
		public void setActionCommand(String s) {
			delegateModel.setActionCommand(s);
		}

		@Override
		public String getActionCommand() {
			return delegateModel.getActionCommand();
		}

		@Override
		public void setGroup(ButtonGroup group) {
			delegateModel.setGroup(group);
		}

		@Override
		public void addActionListener(ActionListener l) {
			delegateModel.addActionListener(l);
		}

		@Override
		public void removeActionListener(ActionListener l) {
			delegateModel.removeActionListener(l);
		}

		@Override
		public void addItemListener(ItemListener l) {
			delegateModel.addItemListener(l);
		}

		@Override
		public void removeItemListener(ItemListener l) {
			delegateModel.removeItemListener(l);
		}

		@Override
		public void addChangeListener(ChangeListener l) {
			delegateModel.addChangeListener(l);
		}

		@Override
		public void removeChangeListener(ChangeListener l) {
			delegateModel.removeChangeListener(l);
		}
	}
	
	/**
	 * This listener is attached when a MixedStateUI is installed. If it identifies
	 * a SELECTED or DESELECTED event: it updates the MixedState appropriately so
	 * the MixedStateUI will be uninstalled.
	 */
	private static ItemListener ITEM_LISTENER = new ItemListener() {
		
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				MixedState.set( (JCheckBox) e.getSource(), MixedState.SELECTED);
			} else if(e.getStateChange() == ItemEvent.DESELECTED) {
				MixedState.set( (JCheckBox) e.getSource(), MixedState.UNSELECTED);
			}
		}
		
	};

	private final Collection<Thread> editingThreads = Collections.synchronizedCollection(new HashSet<>());
	protected final JCheckBox checkBox;
	protected final ButtonModel originalModel;
	protected final ButtonModel newModel;
	
	protected MixedStateUI(JCheckBox checkBox) {
		this.checkBox = Objects.requireNonNull(checkBox);
		originalModel = checkBox.getModel();
		newModel = new MixedStateUIButtonModel(originalModel);
	}

	protected final void install() {
		if (isInstalled())
			return;
		
		Thread currentThread = Thread.currentThread();
		boolean outerEdit = editingThreads.add(currentThread);
		try {
			checkBox.setModel(newModel);
			doInstall();
			checkBox.addItemListener(ITEM_LISTENER);
		} finally {
			if (outerEdit)
				editingThreads.remove(currentThread);
		}
	}

	protected abstract void doInstall();

	protected boolean isInstalled() {
		return checkBox.getModel() == newModel;
	}

	protected final void uninstall() {
		if (!isInstalled())
			return;
		
		Thread currentThread = Thread.currentThread();
		boolean outerEdit = editingThreads.add(currentThread);
		try {
			checkBox.removeItemListener(ITEM_LISTENER);
			checkBox.setModel(originalModel);
			doUninstall();
		} finally {
			if (outerEdit)
				editingThreads.remove(currentThread);
		}
	}

	protected abstract void doUninstall();

}