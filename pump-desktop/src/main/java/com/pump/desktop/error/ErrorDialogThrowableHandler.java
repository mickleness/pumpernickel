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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class ErrorDialogThrowableHandler implements ThrowableHandler {

	List<ThrowableDescriptor> queue = new LinkedList<>();
	List<JComponent> leftComponents = new LinkedList<>();

	Runnable processQueue = new Runnable() {
		public void run() {
			ThrowableDescriptor[] throwables;
			synchronized (queue) {
				throwables = queue
						.toArray(new ThrowableDescriptor[queue.size()]);
				queue.clear();
			}
			if (throwables.length == 0)
				return;

			ErrorDialog errorDialog = ErrorDialog.get();
			errorDialog.addThrowables(throwables);
			synchronized (leftComponents) {
				errorDialog.getFooter().setLeftComponents(
						leftComponents.toArray(new JComponent[leftComponents
								.size()]));
			}
			if (!errorDialog.isVisible()) {
				errorDialog.pack();
				errorDialog.setLocationRelativeTo(null);
				errorDialog.setVisible(true);
			}
		}
	};

	@Override
	public boolean processThrowable(ThrowableDescriptor throwable) {
		synchronized (queue) {
			queue.add(throwable);
		}
		ErrorManager.println(throwable.throwable);
		if (SwingUtilities.isEventDispatchThread()) {
			processQueue.run();
		} else {
			SwingUtilities.invokeLater(processQueue);
		}
		return true;
	}

	/**
	 * Add a component to the left side of the error dialog footer.
	 * 
	 */
	public void addLeftComponent(JComponent component) {
		synchronized (leftComponents) {
			leftComponents.add(component);
		}
	}

	public ThrowableDescriptor[] getThrowables() {
		ErrorDialog[] errorDialogs = ErrorDialog.getAll();
		List<ThrowableDescriptor> returnValue = new ArrayList<>();
		for (ErrorDialog d : errorDialogs) {
			returnValue.addAll(d.getThrowables());
		}
		return returnValue.toArray(new ThrowableDescriptor[returnValue.size()]);
	}

	@Override
	public boolean processThrowable(Throwable throwable) {
		return processThrowable(new ThrowableDescriptor(throwable));
	}
}