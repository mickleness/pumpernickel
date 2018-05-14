package com.pump.plaf;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.WeakHashMap;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.SourceDataLine;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;

import com.pump.audio.AudioPlayer;
import com.pump.icon.MusicIcon;

/**
 * This uses a <code>DecoratedListUI</code> and customer decorations to emulate
 * Mac's playback controls for sound files.
 */
public class AquaAudioListUI extends DecoratedListUI {
	class SoundUI {
		URL soundURL;

		float trackSize = 0;
		float targetTrackSize = 0;
		float opacity = 1;
		float targetOpacity = 1;

		SourceDataLine line;
		float completion = 0;
		long totalFrames = 1;

		List<ChangeListener> completionListeners = new ArrayList<ChangeListener>();

		Timer timer = new Timer(50, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean active = false;

				float newCompletion = 0;
				if (line != null) {
					newCompletion = ((float) line.getFramePosition())
							/ ((float) totalFrames);
				}
				if (setCompletion(newCompletion))
					active = true;

				if (trackSize < targetTrackSize) {
					if (setTrackSize(Math
							.min(targetTrackSize, trackSize + .25f)))
						active = true;
				} else if (trackSize > targetTrackSize) {
					if (setTrackSize(Math
							.max(targetTrackSize, trackSize - .25f)))
						active = true;
				}

				if (opacity < targetOpacity) {
					if (setOpacity(Math.min(targetOpacity, opacity + .25f)))
						active = true;
				} else if (opacity > targetOpacity) {
					if (setOpacity(Math.max(targetOpacity, opacity - .25f)))
						active = true;
				}

				if (!active) {
					timer.stop();
				}
			}
		});

		SoundUI(URL url) {
			Objects.requireNonNull(url);
			this.soundURL = url;
		}

		private boolean setCompletion(float f) {
			if (completion == f)
				return false;
			completion = f;
			timer.start();
			repaintListCell();
			return true;
		}

		public float getCompletion() {
			return completion;
		}

		public boolean isPlaying() {
			return line != null && line.isOpen() && line.isActive();
		}

		public void play() {
			try {
				if (line != null && line.isOpen()) {
					line.start();
				} else {
					AudioInputStream audioIn = AudioSystem
							.getAudioInputStream(soundURL);
					totalFrames = audioIn.getFrameLength();
					line = AudioPlayer.playAudioStream(audioIn);
					line.addLineListener(new LineListener() {
						public void update(LineEvent e) {
							updateTargetTrackSize();
						}
					});
					timer.start();
					updateTargetTrackSize();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void updateTargetTrackSize() {
			setTargetTrackSize(line.isOpen() && line.isActive() ? 1 : 0);
		}

		public void pause() {
			if (line != null) {
				line.stop();
			}
		}

		protected void setTargetTrackSize(float s) {
			if (targetTrackSize == s)
				return;
			targetTrackSize = s;
			timer.start();
		}

		protected void setTargetOpacity(float s) {
			if (targetOpacity == s)
				return;
			targetOpacity = s;
			timer.start();
		}

		public float getTrackSize() {
			return trackSize;
		}

		public float getOpacity() {
			return opacity;
		}

		private void repaintListCell() {
			int index = -1;
			for (int a = 0; a < list.getModel().getSize(); a++) {
				if (list.getModel().getElementAt(a) == soundURL)
					index = a;
			}

			Rectangle cellBounds = list.getCellBounds(index, index);
			if (cellBounds != null)
				list.repaint(cellBounds);
		}

		private boolean setTrackSize(float f) {
			if (trackSize == f)
				return false;
			trackSize = f;
			repaintListCell();
			return true;
		}

		private boolean setOpacity(float f) {
			if (opacity == f)
				return false;
			opacity = f;
			repaintListCell();
			return true;
		}
	}

	class SoundCellRenderer implements ListCellRenderer {
		JLabel label = new JLabel();

		SoundCellRenderer() {
			label.setIcon(new MusicIcon(128));
			label.setVerticalTextPosition(SwingConstants.BOTTOM);
			label.setHorizontalTextPosition(SwingConstants.CENTER);
			label.setBorder(new EmptyBorder(12, 12, 12, 12));
		}

		@Override
		public Component getListCellRendererComponent(JList list,
				Object soundObject, int row, boolean isSelected,
				boolean hasFocus) {
			label.setText(getSoundName(soundObject));

			if (isSelected) {
				label.setOpaque(true);
				label.setBackground(SystemColor.textHighlight);
				label.setForeground(SystemColor.textHighlightText);
			} else {
				label.setOpaque(list.isOpaque());
				label.setBackground(SystemColor.text);
				label.setForeground(SystemColor.textText);
			}
			return label;
		}
	}

	ListDecoration playbackDecoration = new ListDecoration() {
		MusicIcon.PlayToggleIcon icon = new MusicIcon.PlayToggleIcon(
				MusicIcon.PlayToggleIcon.DEFAULT_WIDTH);

		WeakHashMap<URL, SoundUI> map = new WeakHashMap<>();

		private SoundUI getSoundUI(URL soundSource) {
			SoundUI soundUI = map.get(soundSource);
			if (soundUI == null) {
				soundUI = new SoundUI(soundSource);
				map.put(soundSource, soundUI);
			}
			return soundUI;
		}

		@Override
		public Icon getIcon(JList list, Object value, int row,
				boolean isSelected, boolean cellHasFocus, boolean isRollover,
				boolean isPressed) {
			URL sound = (URL) value;
			SoundUI soundUI = getSoundUI(sound);

			icon.setPressed(isPressed);
			if (soundUI.isPlaying()) {
				soundUI.setTargetOpacity(1);
				icon.setPauseIconVisible(true);
				icon.setTrackCompletion(soundUI.getCompletion());
			} else {
				soundUI.setTargetOpacity((isRollover || isPressed) ? 1 : 0);
				icon.setPauseIconVisible(false);
			}
			icon.setTrackSize(soundUI.getTrackSize());
			icon.setOpacity(soundUI.getOpacity());
			return icon;
		}

		@Override
		public boolean isVisible(JList list, Object value, int row,
				boolean isSelected, boolean cellHasFocus) {
			URL sound = (URL) value;
			SoundUI ui = getSoundUI(sound);

			if (!isSelected) {
				SourceDataLine dataLine = ui.line;
				if (dataLine != null)
					dataLine.close();
			}

			boolean visible = isSelected;
			if (!visible) {
				ui.setTargetOpacity(0);
			}
			if (ui.targetOpacity > 0)
				visible = true;
			return visible;
		}

		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int i = list.getLeadSelectionIndex();

				URL sound = (URL) list.getModel().getElementAt(i);
				SoundUI soundUI = getSoundUI(sound);
				if (soundUI.isPlaying()) {
					soundUI.pause();
				} else {
					soundUI.play();
				}
			}
		};

		@Override
		public ActionListener getActionListener(JList list, Object value,
				int row, boolean isSelected, boolean cellHasFocus) {
			return actionListener;
		}

		@Override
		public Point getLocation(JList list, Object value, int row,
				boolean isSelected, boolean cellHasFocus) {
			Rectangle r = list.getCellBounds(row, row);
			return new Point(r.width / 2 - icon.getIconWidth() / 2, r.height
					/ 2 - icon.getIconHeight() / 2 - 10);
		}

	};

	class RemoveActionListener implements ActionListener {
		Object element;

		RemoveActionListener(Object element) {
			this.element = element;
		}

		public void actionPerformed(ActionEvent e) {
			((DefaultListModel) list.getModel()).removeElement(element);
		}
	}

	CloseDecoration closeDecoration = new CloseDecoration() {

		@Override
		public ActionListener getActionListener(JList list, Object value,
				int row, boolean isSelected, boolean cellHasFocus) {
			return new RemoveActionListener(value);
		}
	};

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);

		list.setCellRenderer(new SoundCellRenderer());
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(1);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setPreferredSize(new Dimension(152 * 3, 172 * 1));

		list.putClientProperty(DecoratedListUI.KEY_DECORATIONS,
				new ListDecoration[] { closeDecoration, playbackDecoration });
	}

	public String getSoundName(Object soundObject) {
		if (soundObject instanceof URL) {
			String s = soundObject.toString();
			int i = s.lastIndexOf("/");
			return s.substring(i + 1);
		}
		throw new IllegalArgumentException("Unsupported element type: "
				+ soundObject.getClass().getName());
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
	}

}