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
package com.pump.plaf;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

import com.pump.icon.IconUtils;
import com.pump.io.location.IOLocation;
import com.pump.swing.io.GraphicCache;

public class IOLabelCellRenderer extends LabelCellRenderer {
	final JComboBox comboBox;
	final GraphicCache graphicCache;

	public IOLabelCellRenderer(JComboBox jc, GraphicCache graphicsCache) {
		this.comboBox = jc;
		this.graphicCache = graphicsCache;
		graphicCache.addPropertyChangeListener(new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) {
				SwingUtilities.invokeLater(new RepaintRunnable(evt));
			}
		});
	}

	class RepaintRunnable implements Runnable {
		PropertyChangeEvent evt;

		public RepaintRunnable(PropertyChangeEvent e) {
			evt = e;
		}

		public void run() {
			if (evt.getPropertyName().equals(GraphicCache.ICON_PROPERTY)) {
				IOLocation loc = (IOLocation) evt.getSource();
				for (int a = 0; a < comboBox.getItemCount(); a++) {
					if (comboBox.getItemAt(a).equals(loc)) {
						comboBox.repaint();
						return;
					}
				}
			}
		}
	}

	@Override
	protected void formatLabel(Object value) {
		String text;
		Icon icon = null;
		if (value instanceof IOLocation) {
			IOLocation l = (IOLocation) value;
			text = l.getName();

			icon = graphicCache.requestIcon(l);
		} else if (value != null) {
			text = value.toString();
		} else {
			text = "";
		}
		if (icon == null) {
			icon = IOLocation.FOLDER_ICON;
		}

		icon = IconUtils.createPaddedIcon(icon, iconPadding);

		label.setIcon(icon);
		label.setText(text);
	}

}