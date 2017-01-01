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
package com.pump.jar;

import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.pump.UserCancelledException;
import com.pump.inspector.InspectorGridBagLayout;
import com.pump.swing.DialogFooter;
import com.pump.swing.QDialog;
import com.pump.window.WindowList;

/** When creating a jar we have to decide what to do with external jar
 * dependencies: should we disregard them (like we should sure javax.jnlp.* classes),
 * copy the entire jar (like we should for JFreeChart), or copy only the specific
 * classes we need?
 * <p>This class helps manage this decision.
 */
public class MissingJarUserResponseManager extends MissingJarPreferenceResponseManager {

	static class BehaviorDialog extends QDialog {
		private static final long serialVersionUID = 1L;
		
		DialogFooter footer = DialogFooter.createDialogFooter(null, 
				DialogFooter.OK_CANCEL_OPTION, 
				DialogFooter.OK_OPTION, 
				DialogFooter.EscapeKeyBehavior.TRIGGERS_CANCEL);
		
		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean passed = true;
				for(BehaviorComboBox c : allComboBoxes.values()) {
					MissingJarResponse b = (MissingJarResponse)c.getSelectedItem();
					if(b==null)
						passed = false;
				}
				footer.getButton(DialogFooter.OK_OPTION).setEnabled(passed);
			}
		};

		class BehaviorComboBox extends JComboBox<MissingJarResponse> {
			private static final long serialVersionUID = 1L;

			public BehaviorComboBox(MissingJarResponse b) {
				addItem( MissingJarResponse.BUNDLE_ENTIRE_JAR );
				addItem( MissingJarResponse.BUNDLE_ONLY_REQUIRED_CLASSES );
				addItem( MissingJarResponse.IGNORE );
				if(b==null) {
					setSelectedIndex(-1);
				} else if(MissingJarResponse.BUNDLE_ENTIRE_JAR.equals(b)) {
					setSelectedIndex(0);
				} else if(MissingJarResponse.BUNDLE_ENTIRE_JAR.equals(b)) {
					setSelectedIndex(1);
				} else if(MissingJarResponse.IGNORE.equals(b)) {
					setSelectedIndex(2);
				}
				addActionListener(actionListener);
			}
		}
		
		Map<File, MissingJarResponse> jarBehaviors;
		Map<File, BehaviorComboBox> allComboBoxes = new HashMap<File, BehaviorComboBox>();
		JCheckBox rememberCheckbox = new JCheckBox("Remember these choices next time", true);
		MissingJarResponseManager choiceModel;
		
		BehaviorDialog(Frame frame,Set<File> jarFiles,Map<File, MissingJarResponse> jarBehaviors,String primaryClassName,MissingJarResponseManager choiceModel) {
			super(frame, "Resolve Dependencies");
			this.jarBehaviors = jarBehaviors;
			this.choiceModel = choiceModel;
			
			JPanel panel = new JPanel(new GridBagLayout());
			InspectorGridBagLayout layout = new InspectorGridBagLayout(panel);
			for(File jarFile : jarFiles) {
				MissingJarResponse b = jarBehaviors.get(jarFile);
				if(b==null)
					b = choiceModel.guessBehavior(jarFile);
				BehaviorComboBox comboBox = new BehaviorComboBox( b );
				layout.addRow(new JLabel(jarFile.getName()+":"), comboBox, false);
				allComboBoxes.put(jarFile, comboBox);
			}
			layout.addRow(null, rememberCheckbox, false);
			
			JComponent innerComponent;
			if(jarBehaviors.size()>5) {
				innerComponent = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			} else {
				innerComponent = panel;
			}
			innerComponent = QDialog.createContentPanel(
					"These jars are required to compile \""+primaryClassName+"\".",
					"The javac task is complete, but what should I do with these jars?", 
					innerComponent, false);
			
			setIcon( QDialog.getIcon( QDialog.PLAIN_MESSAGE ) );
			setContent(innerComponent);
			actionListener.actionPerformed(null);
			setFooter(footer);
		}

		public void save() {
			for(File jarFile : allComboBoxes.keySet()) {
				BehaviorComboBox cb = allComboBoxes.get(jarFile);
				MissingJarResponse b = (MissingJarResponse)cb.getSelectedItem();
				jarBehaviors.put(jarFile, b);
				if(rememberCheckbox.isSelected())
					choiceModel.setBehavior(jarFile, b);
			}
		}
	}

	public MissingJarUserResponseManager(String name) {
		super(name);
	}

	/** Show a modal dialog prompting the user to associate a Behavior to one or more jar Files.
	 * <p>This "OK" button on this dialog is disabled until a behavior is chosen for each file.
	 * 
	 * @param choiceModel the choice model used.
	 * @param jarBehaviors known jar behaviors. This is where the users choices will be stored, so it may be empty but it can not be null.
	 * This is also used to set up the dialog if you want to preset certain choices.
	 * @param jarFiles the set of jar files that the user will make choices for. When this dialog is dismissed it is guaranteed that the
	 * jarBehaviors map will have a non-null value associated with each of these Files.
	 * @param primaryClassName the UI needs the name of the compiled class to explain things to the user.
	 */
	@Override
	public void resolveBehaviors(MissingJarResponseManager manager,
			Map<File, MissingJarResponse> requiredJars, Set<File> keySet,
			String name) {
		Frame[] frames = WindowList.getFrames(true, false, true);
		if(frames.length==0) {
			frames = new Frame[] { new JFrame() };
		}

		BehaviorDialog dialog = new BehaviorDialog(frames[frames.length-1], 
				new TreeSet<File>(keySet), 
				requiredJars, name,
				manager);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		if(dialog.footer.getLastSelectedComponent()==dialog.footer.getButton(DialogFooter.OK_OPTION)) {
			dialog.save();
		} else {
			throw new UserCancelledException();
		}
	}

}