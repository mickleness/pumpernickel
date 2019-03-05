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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import com.pump.graphics.DrawInstruction;
import com.pump.graphics.FillInstruction;
import com.pump.graphics.GraphicInstruction;
import com.pump.graphics.GraphicsWriter;
import com.pump.graphics.GraphicsWriterDebugger;
import com.pump.graphics.ImageInstruction;
import com.pump.graphics.TextBoxInstruction;
import com.pump.inspector.InspectorGridBagLayout;
import com.pump.inspector.InspectorLayout;
import com.pump.util.IntegerProperty;

public class GraphicsWriterDebuggerDemo implements ShowcaseDemo {

	static class GraphicsWriterDebuggerDemoPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		JTextComponent textBox = new JTextPane();
		IntegerProperty keyCodeProperty = new IntegerProperty("keyCode",
				KeyEvent.VK_5);
		JComboBox<String> keyCodeComboBox = new JComboBox<>();

		public GraphicsWriterDebuggerDemoPanel() {
			textBox.setText("The com.pump.graphics.GraphicsWriter is a java.awt.Graphics2D that stores all the instructions it (and its cloned descendants) receive.\n\nThe GraphicsWriterDebugger is a developer tool that paints the foremost window and then lets you navigate each rendered component.\n\nHave you have had a stray line, shadow or box somewhere in your UI and you couldn't figure out where it came from? This tool will help you pinpoint the exact stacktrace that's rendering the rogue instruction. The UI of the debugger is crude, but it gets the job done.\n\nThe shortcut you specify here will work for any demo in this app.\n\nYou could also use the GraphicsWriter to implement a Batik-like Graphics2D used to write graphics data. (I once used this to write PPTX graphics data years ago.)\n\nAll the instructions are serializable, so you could even serialize a snapshot of the window if your testing team is identifying artifacts that your developers can't reproduce.");
			textBox.setEditable(false);
			textBox.setOpaque(false);

			for (int i = 1; i <= 9; i++) {
				keyCodeComboBox.addItem("F" + i);
			}

			keyCodeComboBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					keyCodeProperty.setValue(KeyEvent.VK_F1
							+ keyCodeComboBox.getSelectedIndex());
				}

			});

			keyCodeComboBox.setSelectedIndex(4);

			setLayout(new GridBagLayout());
			JPanel controls = new JPanel(new GridBagLayout());
			InspectorLayout layout = new InspectorGridBagLayout(controls);
			layout.addRow(new JLabel("Shortcut:"), keyCodeComboBox);

			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(10, 10, 10, 10);
			c.gridx = 0;
			c.gridy = 0;
			c.weighty = 0;
			c.weightx = 1;
			c.fill = GridBagConstraints.BOTH;
			add(controls, c);
			c.gridy++;
			c.weighty = 1;
			add(textBox, c);

			addHierarchyListener(new HierarchyListener() {

				@Override
				public void hierarchyChanged(HierarchyEvent e) {
					Window w = SwingUtilities
							.getWindowAncestor(GraphicsWriterDebuggerDemoPanel.this);

					if (w != null) {
						GraphicsWriterDebugger.installDebugHotkey(w,
								keyCodeProperty);
						removeHierarchyListener(this);
					}
				}

			});
		}
	}

	@Override
	public JPanel createPanel(PumpernickelShowcaseApp psa) {
		return new GraphicsWriterDebuggerDemoPanel();
	}

	@Override
	public String getTitle() {
		return "GraphicsWriterDebugger Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates a new tool to identify the stacktraces of each rendered artifact in a window.";
	}

	@Override
	public URL getHelpURL() {
		return null;
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "ui", "vector-graphics", "developer-tool",
				"debugging" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { GraphicsWriterDebugger.class,
				GraphicsWriter.class, GraphicInstruction.class,
				ImageInstruction.class, TextBoxInstruction.class,
				DrawInstruction.class, FillInstruction.class };
	}
}