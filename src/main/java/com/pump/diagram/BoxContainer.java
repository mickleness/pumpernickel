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
package com.pump.diagram;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.util.list.ObservableList;

/**
 * This contains a series of boxes, connectors, and logic information. When
 * serialized: boxes and connectors are always saved. ConnectorLogics may be
 * dropped depending on whether they implement Serializable (which is your
 * choice).
 *
 */
public class BoxContainer implements Serializable {
	private static final long serialVersionUID = 1L;

	protected ObservableList<Box> boxes = new ObservableList<>();
	protected ObservableList<ConnectorLogic> logics = new ObservableList<>();
	protected ObservableList<Connector> connectors = new ObservableList<>();

	private Set<Box> processedBoxes = new HashSet<>();

	public BoxContainer() {
		initializeListeners();
	}

	private ChangeListener createBoxListener() {
		return new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				updateConnectors();
			}
		};
	}

	private void updateConnectors() {
		Set<Box> unknownBoxes = new LinkedHashSet<>();
		Set<Box> knownBoxes = new LinkedHashSet<>();
		for (Box box : boxes) {
			if (!processedBoxes.contains(box)) {
				unknownBoxes.add(box);
			} else {
				knownBoxes.add(box);
			}
		}
		for (ConnectorLogic logic : logics) {
			for (Box box : knownBoxes) {
				for (Box newBox : unknownBoxes) {
					BinaryRelationship r = logic.getRelationship(box, newBox);
					if (r != null
							&& !(r.relationshipSideA == Relationship.NONE && r.relationshipSideB == Relationship.NONE)) {
						Connector newConnector = new Connector(box, newBox, r);
						connectors.add(newConnector);
					}
				}
			}

			for (Box newBoxA : unknownBoxes) {
				for (Box newBoxB : unknownBoxes) {
					if (newBoxA != newBoxB) {
						BinaryRelationship r = logic.getRelationship(newBoxA,
								newBoxB);
						if (r != null
								&& (!(r.relationshipSideA == Relationship.NONE && r.relationshipSideB == Relationship.NONE))) {
							Connector newConnector = new Connector(newBoxA,
									newBoxB, r);
							connectors.add(newConnector);
						}
					}
				}
			}
		}
		processedBoxes.addAll(unknownBoxes);
		validateConnectors();
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(0); // a version

		Box[] boxesArray = boxes.toArray(new Box[boxes.size()]);
		out.writeObject(boxesArray);

		Connector[] connectorsArray = connectors
				.toArray(new Connector[connectors.size()]);
		out.writeObject(connectorsArray);

		List<ConnectorLogic> serializableLogics = new ArrayList<>();
		for (ConnectorLogic cl : logics) {
			if (cl instanceof Serializable) {
				serializableLogics.add(cl);
			}
		}
		ConnectorLogic[] logicsArray = serializableLogics
				.toArray(new ConnectorLogic[serializableLogics.size()]);
		out.writeObject(logicsArray);

	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		int version = in.readInt();
		if (version != 0)
			throw new IOException("incompatible inner version: " + version);

		Box[] boxesArray = (Box[]) in.readObject();
		boxes = new ObservableList<>();
		boxes.addAll(boxes.size(), boxesArray);

		Connector[] connectorsArray = (Connector[]) in.readObject();
		connectors = new ObservableList<>();
		connectors.addAll(connectors.size(), connectorsArray);

		ConnectorLogic[] logicsArray = (ConnectorLogic[]) in.readObject();
		logics = new ObservableList<>();
		logics.addAll(logics.size(), logicsArray);

		initializeListeners();
		validateConnectors();
	}

	private void validateConnectors() {
		synchronized (connectors) {
			for (int a = 0; a < connectors.size(); a++) {
				Connector connector = connectors.get(a);
				Box box1 = connector.getBox1();
				Box box2 = connector.getBox2();
				if ((!boxes.contains(box1)) || (!boxes.contains(box2))
						|| box1 == box2) {
					connectors.remove(a);
					a--;
				} else {
					for (int b = a + 1; b < connectors.size(); b++) {
						Connector connectorB = connectors.get(b);
						Box box1b = connectorB.getBox1();
						Box box2b = connectorB.getBox2();
						if ((box1 == box1b && box2 == box2b)
								|| (box1 == box2b && box2 == box1b)) {
							connectors.remove(b);
							b--;
						}
					}
				}
			}
		}
	}

	private void initializeListeners() {
		getBoxes().addChangeListener(createBoxListener(), false);
	}

	public ObservableList<Box> getBoxes() {
		return boxes;
	}

	public ObservableList<Connector> getConnectors() {
		return connectors;
	}

	public ObservableList<ConnectorLogic> getConnectorLogic() {
		return logics;
	}

	/**
	 * Add a listener to be notified any time a box, connector, or logic is
	 * modified.
	 * 
	 * @param changeListener
	 *            the listener to be notified.
	 * @param allowsModification
	 *            false if this listener should be forbidden from modifying the
	 *            data. This is recommended to prevent cascading/looping
	 *            changes, but it may be considered a safety mechanism which
	 *            it's OK to disable if you know what you're doing.
	 */
	public void addChangeListener(ChangeListener changeListener,
			boolean allowsModification) {
		boxes.addChangeListener(changeListener, allowsModification);
		connectors.addChangeListener(changeListener, allowsModification);
		logics.addChangeListener(changeListener, allowsModification);
	}

	public void refreshConnectors() {
		processedBoxes.clear();
		connectors.clear();
		updateConnectors();
	}
}