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
package com.pump.diagram.swing;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;

import com.pump.diagram.Box;
import com.pump.diagram.BoxContainer;
import com.pump.diagram.plaf.BoxContainerPanelUI;
import com.pump.util.ObservableSet;

public class BoxContainerPanel extends JComponent {
	private static final long serialVersionUID = 1L;

	public static final String KEY_BOX_CONTAINER = BoxContainerPanel.class
			.getName() + "#boxContainerPanel";

	private static final String uiClassID = "BoxContainerPanelUI";

	public static class SelectionModel {
		ObservableSet<Box> selection = new ObservableSet<>(Box.class);

		public boolean add(Box box) {
			return selection.add(box);
		}

		public boolean remove(Box box) {
			if (!selection.contains(box))
				return false;
			selection.remove(box);
			return true;
		}

		public boolean clear() {
			if (selection.size() == 0)
				return false;
			selection.clear();
			return true;
		}

		public boolean set(Collection<Box> newSelection) {
			return selection.set(newSelection);
		}

		public Collection<Box> get() {
			return new HashSet<>(selection);
		}

		public boolean contains(Box box) {
			return selection.contains(box);
		}

		public void addChangeListener(ChangeListener changeListener) {
			selection.addChangeListener(changeListener);
		}

		public void removeChangeListener(ChangeListener changeListener) {
			selection.removeChangeListener(changeListener);
		}

		public void set(Box... boxes) {
			set(Arrays.asList(boxes));
		}
	}

	protected SelectionModel selectionModel = new SelectionModel();

	public BoxContainerPanel() {
		updateUI();
		setBoxContainer(new BoxContainer());
	}

	public BoxContainer getBoxContainer() {
		return (BoxContainer) getClientProperty(KEY_BOX_CONTAINER);
	}

	public SelectionModel getSelectionModel() {
		return selectionModel;
	}

	public void setBoxContainer(BoxContainer boxContainer) {
		putClientProperty(KEY_BOX_CONTAINER, boxContainer);
	}

	@Override
	public String getUIClassID() {
		return uiClassID;
	}

	@Override
	public void updateUI() {
		if (UIManager.getDefaults().get(uiClassID) == null) {
			UIManager.getDefaults().put(uiClassID,
					BoxContainerPanelUI.class.getName());
		}
		setUI((BoxContainerPanelUI) UIManager.getUI(this));
	}

	public void setUI(BoxContainerPanelUI ui) {
		super.setUI(ui);
	}

	public BoxContainerPanelUI getUI() {
		return (BoxContainerPanelUI) ui;
	}
}