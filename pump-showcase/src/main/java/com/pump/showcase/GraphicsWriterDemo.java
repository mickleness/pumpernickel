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
package com.pump.showcase;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.pump.graphics.GraphicsWriterDebugger;

public class GraphicsWriterDemo extends JPanel {

	public GraphicsWriterDemo() {
		JTextArea textArea = new JTextArea(
				"Press the F5 key to launch the GraphicsWriterDebugger. (You can do this no matter what panel is showing in this demo.)\n\nThis is one application of the GraphicsWriter architecture. In this case it helps you identify the exact stacktraces used to render certain components. So if you've ever had trouble understanding what entity was rendering a line, shadow or image: this debugging tool can give you a starting point.");
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		textArea.setOpaque(false);

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(10, 10, 10, 10);
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;
		add(textArea, c);

		addHierarchyListener(new HierarchyListener() {

			@Override
			public void hierarchyChanged(HierarchyEvent e) {
				Window w = SwingUtilities
						.getWindowAncestor(GraphicsWriterDemo.this);

				if (w != null) {
					GraphicsWriterDebugger
							.installDebugHotkey(w, KeyEvent.VK_F5);
					removeHierarchyListener(this);
				}
			}

		});
	}
}