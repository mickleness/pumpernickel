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
package com.pump.plaf;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SliderUI;

import com.pump.audio.AudioPlayer;
import com.pump.audio.AudioPlayer.StartTime;
import com.pump.icon.DarkenedIcon;
import com.pump.icon.PauseIcon;
import com.pump.icon.TriangleIcon;
import com.pump.swing.AnimationController;
import com.pump.swing.AudioPlayerComponent;

public class BasicAudioPlayerUI extends AudioPlayerUI {

	/**
	 * This method has to exist in order for to make this UI the button default
	 * by calling: <br>
	 * <code>UIManager.getDefaults().put("ButtonUI", "com.pump.plaf.BevelButtonUI");</code>
	 */
	public static ComponentUI createUI(JComponent c) {
		return new BasicAudioPlayerUI();
	}

	static class URLPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		JTextField field = new JTextField(20);
		JLabel label = new JLabel("URL:");

		public URLPanel(String fieldText) {
			super(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 0;
			c.weighty = 1;
			c.insets = new Insets(3, 3, 3, 3);
			add(label, c);
			c.gridx++;
			c.weightx = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			add(field, c);
			if (fieldText != null)
				field.setText(fieldText);
		}
	}

	public static final Icon PLAY_ICON = new TriangleIcon(SwingConstants.EAST,
			12, 12);
	public static final Icon PAUSE_ICON = new PauseIcon(12, 12);

	static class Fields {
		final AudioPlayerComponent apc;

		JButton playButton = new JButton(PLAY_ICON);
		JSlider playbackProgress = new JSlider(0, 100, 0);
		JPanel controller = new JPanel();
		JButton browseButton = new JButton("Browse...");
		URLPanel urlPanel = new URLPanel(null);
		ChangeListener sliderListener = new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (playbackProgress.getValueIsAdjusting())
					return;

				AudioPlayer player = apc.getAudioPlayer();
				if (player != null && player.isPlaying())
					play();
			}

		};

		protected Fields(AudioPlayerComponent apc) {
			this.apc = apc;
			playbackProgress.addChangeListener(sliderListener);
		}

		protected void uninstall() {
			apc.remove(controller);
			apc.remove(browseButton);
			apc.remove(urlPanel);
		}

		protected void install() {
			apc.removeAll();
			apc.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 0;
			gbc.weighty = 1;
			gbc.insets = new Insets(3, 3, 3, 3);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			apc.add(urlPanel, gbc);
			gbc.gridx++;
			gbc.weightx = 0;
			apc.add(browseButton, gbc);
			gbc.gridy++;
			gbc.gridx = 0;
			gbc.weighty = 0;
			gbc.weightx = 1;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.fill = GridBagConstraints.BOTH;

			apc.add(controller, gbc);

			AnimationController.format(controller, playButton,
					new JButton[] {}, playbackProgress);

			browseButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					apc.getUI().doBrowseForFile(apc);
				}
			});
			playButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (apc.getAudioPlayer() == null
							|| !apc.getAudioPlayer().isPlaying()) {
						play();
					} else {
						apc.getUI().doPause(apc);
					}
				}
			});

			updateUI();
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					updateUI();
				}
			});
		}

		private void play() {
			float startTime = 0;
			if (playbackProgress.getValue() < playbackProgress.getMaximum()) {
				startTime = (((float) (playbackProgress.getValue() - playbackProgress
						.getMinimum())) / ((float) (playbackProgress
						.getMaximum() - playbackProgress.getMinimum())));
			}
			StartTime t = new StartTime(startTime, true);
			apc.getUI().doPlay(apc, t);
		}

		protected void updateUI() {
			if (!SwingUtilities.isEventDispatchThread()) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						updateUI();
					}
				});
				return;
			}

			URL source = apc.getSource();
			String text = source == null ? "" : source.toString();
			urlPanel.field.setText(text);
			boolean playing = false;
			setIcon(playButton, playing ? PAUSE_ICON : PLAY_ICON);

			SliderUI ui = playbackProgress.getUI();
			boolean assignDefault = false;
			if (source == null) {
				assignDefault = true;
			} else {
				WaveformSliderUI wsi = null;
				if (ui instanceof WaveformSliderUI) {
					wsi = (WaveformSliderUI) ui;
				}
				if (wsi == null || !wsi.getSource().equals(source)) {
					try {
						playbackProgress.setUI(new WaveformSliderUI(
								playbackProgress, source));
					} catch (Throwable t) {
						assignDefault = true;
					}
				}
			}
			if (assignDefault) {
				ui = (SliderUI) UIManager.getUI(playbackProgress);
				playbackProgress.setUI(ui);
			}

			Window w = SwingUtilities.getWindowAncestor(apc);
			browseButton.setEnabled(w != null);
			if (browseButton.isEnabled()) {
				browseButton.setToolTipText("Select an audio file to play...");
			} else {
				browseButton
						.setToolTipText("This button is not supported unless this component is in an accessible java.awt.Window.");
			}
		}
	}

	private static final String FIELDS_KEY = BasicAudioPlayerUI.class.getName()
			+ ".fields";

	PropertyChangeListener updateSourceListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			JComponent src = evt.getSource() instanceof JComponent ? (JComponent) evt
					.getSource() : null;
			updateComponents((AudioPlayerComponent) src);
		}
	};

	HierarchyListener hierarchyListener = new HierarchyListener() {

		@Override
		public void hierarchyChanged(HierarchyEvent e) {
			Component c = e.getComponent();
			if (c instanceof AudioPlayerComponent) {
				AudioPlayerComponent jc = (AudioPlayerComponent) c;
				if (jc.getUI() instanceof BasicAudioPlayerUI) {
					((BasicAudioPlayerUI) jc.getUI())
							.updateComponents((AudioPlayerComponent) jc);
				}
			}
		}

	};

	protected void updateComponents(AudioPlayerComponent apc) {
		Fields fields = getFields(apc);
		fields.updateUI();
	}

	public BasicAudioPlayerUI() {
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);

		getFields(c).install();
		c.addPropertyChangeListener(AudioPlayerComponent.SOURCE_KEY,
				updateSourceListener);
		c.addHierarchyListener(hierarchyListener);
	}

	protected Fields getFields(JComponent c) {
		if (c == null)
			return null;
		Fields fields = (Fields) c.getClientProperty(FIELDS_KEY);
		if (fields == null) {
			fields = new Fields((AudioPlayerComponent) c);
			c.putClientProperty(FIELDS_KEY, fields);
		}
		return fields;
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);

		getFields(c).uninstall();
		c.removePropertyChangeListener(AudioPlayerComponent.SOURCE_KEY,
				updateSourceListener);
		c.removeHierarchyListener(hierarchyListener);
	}

	@Override
	protected void notifyPlaybackStarted(AudioPlayerComponent apc) {
		Fields fields = getFields(apc);
		setIcon(fields.playButton, PAUSE_ICON);
	}

	@Override
	protected void notifyPlaybackProgress(AudioPlayerComponent apc,
			float timeElapsed, float timeAsFraction) {
		Fields fields = getFields(apc);
		int span = fields.playbackProgress.getMaximum()
				- fields.playbackProgress.getMinimum();
		int v = (int) (span * timeAsFraction + fields.playbackProgress
				.getMinimum());
		SliderUI ui = fields.playbackProgress.getUI();
		boolean isDragging = ui instanceof WaveformSliderUI ? ((WaveformSliderUI) ui)
				.isDragging() : false;
		if (!isDragging) {
			fields.playbackProgress.removeChangeListener(fields.sliderListener);
			fields.playbackProgress.setValue(v);
			fields.playbackProgress.addChangeListener(fields.sliderListener);
		}
		setIcon(fields.playButton, PAUSE_ICON);
	}

	@Override
	protected void notifyPlaybackStopped(AudioPlayerComponent apc, Throwable t) {
		Fields fields = getFields(apc);
		setIcon(fields.playButton, PLAY_ICON);
	}

	// TODO: see setIcon(..) in AnimationController
	protected static void setIcon(AbstractButton button, Icon icon) {
		button.setIcon(icon);
		button.setRolloverIcon(new DarkenedIcon(icon, .5f));
		button.setPressedIcon(new DarkenedIcon(icon, .75f));
	}
}