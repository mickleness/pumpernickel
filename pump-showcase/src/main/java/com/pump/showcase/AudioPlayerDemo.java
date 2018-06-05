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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.pump.UserCancelledException;
import com.pump.audio.AudioPlayer;
import com.pump.inspector.InspectorGridBagLayout;
import com.pump.inspector.InspectorLayout;
import com.pump.plaf.AudioPlayerUI;
import com.pump.plaf.BasicAudioPlayerUI;
import com.pump.plaf.LabelCellRenderer;
import com.pump.plaf.WaveformSliderUI;
import com.pump.plaf.decorate.AquaAudioListUI;
import com.pump.swing.AudioPlayerComponent;
import com.pump.swing.CollapsibleContainer;
import com.pump.swing.SectionContainer.Section;

public class AudioPlayerDemo extends JPanel implements ShowcaseDemo {

	private static final long serialVersionUID = 1L;

	JComboBox<URL> comboBox = new JComboBox<>();
	AudioPlayerComponent audioPlayerComponent = new AudioPlayerComponent();

	public static final URL[] urls = new URL[] {
			AudioPlayerDemo.class.getResource("Bugaboo.wav"),
			AudioPlayerDemo.class.getResource("Ludic.wav"),
			AudioPlayerDemo.class.getResource("Unctuous.wav")

	};

	CollapsibleContainer container = new CollapsibleContainer();
	Section playerSection = container.addSection("player",
			"AudioPlayerUI, WaveformSliderUI");
	Section listSection = container.addSection("aqua-list", "AquaAudioListUI");

	public AudioPlayerDemo(PumpernickelShowcaseApp psa) {
		PumpernickelShowcaseApp.installSections(this, container, playerSection,
				listSection);

		setupPlayerComponent();
		setupAquaList();
	}

	private void setupPlayerComponent() {
		JPanel controls = new JPanel();
		InspectorLayout layout = new InspectorGridBagLayout(controls);
		layout.addRow(new JLabel("WAV File:"), comboBox, false);
		layout.addRow(audioPlayerComponent, SwingConstants.CENTER, true);

		playerSection.getBody().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		playerSection.getBody().add(controls, c);

		comboBox.setRenderer(new LabelCellRenderer<URL>(comboBox, true) {

			@Override
			protected void formatLabel(URL value) {
				if (value == null) {
					label.setText("Custom...");
					label.setFont(label.getFont().deriveFont(Font.ITALIC));
				} else {
					String s = value.toString();
					int i = s.lastIndexOf('/');
					s = s.substring(i + 1);
					label.setText(s);
					label.setFont(label.getFont().deriveFont(0));
				}
			}

		});
		comboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				URL url = (URL) comboBox.getSelectedItem();
				if (url == null) {
					try {
						audioPlayerComponent.getUI().doBrowseForFile(
								audioPlayerComponent);
					} catch (UserCancelledException u) {
						// do nothing
					}
				} else {
					audioPlayerComponent.setSource(url);
				}
			}
		});

		for (URL url : urls) {
			comboBox.addItem(url);
		}
		// the null URL is used to open a FileDialog
		comboBox.addItem(null);
	}

	private void setupAquaList() {
		DefaultListModel<URL> listModel = new DefaultListModel<>();
		JList<URL> list = new JList<>(listModel);
		for (URL url : urls) {
			listModel.addElement(url);
		}
		list.setUI(new AquaAudioListUI());
		listSection.getBody().add(list);
		list.setOpaque(false);
	}

	@Override
	public String getTitle() {
		return "AudioPlayer Demo";
	}

	@Override
	public URL getHelpURL() {
		return AudioPlayerDemo.class
				.getResource("audioPlayerComponentDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "wave", "audio", "sound", "playback", "pcm",
				"waveform" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { AudioPlayer.class, AudioPlayerComponent.class,
				AudioPlayerUI.class, BasicAudioPlayerUI.class,
				WaveformSliderUI.class };
	}

	@Override
	public boolean isSeparatorVisible() {
		return false;
	}

}