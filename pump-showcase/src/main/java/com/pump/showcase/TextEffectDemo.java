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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.pump.awt.text.ExplodeTextEffect;
import com.pump.awt.text.OutlineTextEffect;
import com.pump.awt.text.PunchTextEffect;
import com.pump.awt.text.TextEffect;
import com.pump.awt.text.WaveTextEffect;
import com.pump.awt.text.WriteTextEffect;
import com.pump.awt.text.writing.WritingFont;

/**
 * An applet demonstrating a few simple text effects.
 * 
 */
public class TextEffectDemo extends JPanel {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame f = new JFrame();
				f.add(new TextEffectDemo());
				f.pack();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setVisible(true);
			}
		});
	}

	class PreviewPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		PreviewPanel() {
			setPreferredSize(new Dimension(300, 150));
		}

		@Override
		protected void paintComponent(Graphics g0) {
			super.paintComponent(g0);
			if (effect != null) {
				Graphics2D g = (Graphics2D) g0;
				effect.paint(g, fraction);
			}
		}
	}

	JTextField textField = new JTextField("Type Text Here!");
	JButton outlineEffect = new JButton("Outline");
	JButton punchEffect = new JButton("Punch");
	JButton writeEffect = new JButton("Write");
	JButton waveEffect = new JButton("Wave");
	JButton explodeEffect = new JButton("Explode");

	TextEffect effect;
	float fraction;
	Timer timer = new Timer(20, new ActionListener() {
		long startTime = -1;

		public void actionPerformed(ActionEvent e) {
			long t = System.currentTimeMillis();
			if (startTime == -1) {
				startTime = t;
				setEnabled(false);
				return;
			}
			long elapsed = t - startTime;
			long duration;
			if (effect instanceof PunchTextEffect
					|| effect instanceof ExplodeTextEffect
					|| effect instanceof WriteTextEffect) {
				duration = textField.getText().length() * 90;
			} else {
				duration = textField.getText().length() * 50;
			}
			float fraction = ((float) elapsed) / ((float) duration);
			if (fraction >= 1) {
				startTime = -1;
				timer.stop();
				setEnabled(true);
				fraction = 1;
			}
			TextEffectDemo.this.fraction = fraction;
			preview.repaint();
		}

		private void setEnabled(boolean b) {
			textField.setEnabled(b);
			explodeEffect.setEnabled(b);
			outlineEffect.setEnabled(b);
			punchEffect.setEnabled(b);
			writeEffect.setEnabled(b);
			waveEffect.setEnabled(b);
		}
	});

	ActionListener actionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if (src == outlineEffect) {
				doOutline();
			} else if (src == punchEffect) {
				doPunch();
			} else if (src == writeEffect) {
				doWrite();
			} else if (src == waveEffect) {
				doWave();
			} else if (src == explodeEffect) {
				doExplode();
			}
		}
	};

	private void doWrite() {
		String text = textField.getText();
		effect = new WriteTextEffect(WritingFont.COMIC_NEUE, text,
				preview.getWidth(), preview.getHeight());
		timer.start();
	}

	private void doOutline() {
		Font font = new Font("Impact", 0, 55);
		String text = textField.getText();
		effect = new OutlineTextEffect(font, text, preview.getWidth(),
				preview.getHeight());
		timer.start();
	}

	private void doPunch() {
		Font font = new Font("Impact", 0, 48);
		String text = textField.getText();
		effect = new PunchTextEffect(font, text, preview.getWidth(),
				preview.getHeight());
		timer.start();
	}

	private void doWave() {
		Font font = new Font("Impact", 0, 55);
		String text = textField.getText();
		effect = new WaveTextEffect(font, text, preview.getWidth(),
				preview.getHeight());
		timer.start();
	}

	private void doExplode() {
		Font font = new Font("Impact", 0, 55);
		String text = textField.getText();
		effect = new ExplodeTextEffect(font, text, preview.getWidth(),
				preview.getHeight());
		timer.start();
	}

	PreviewPanel preview = new PreviewPanel();

	public TextEffectDemo() {
		setLayout(new GridBagLayout());

		JPanel flowPanel1 = new JPanel(new FlowLayout());
		JPanel flowPanel2 = new JPanel(new FlowLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(3, 3, 3, 3);
		add(flowPanel1, c);
		c.gridy++;
		add(flowPanel2, c);

		flowPanel1.add(new JLabel("Text:"));
		flowPanel1.add(textField);
		flowPanel2.add(outlineEffect);
		flowPanel2.add(punchEffect);
		flowPanel2.add(waveEffect);
		flowPanel2.add(explodeEffect);
		flowPanel2.add(writeEffect);

		c.gridy++;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		add(preview, c);

		outlineEffect.addActionListener(actionListener);
		punchEffect.addActionListener(actionListener);
		writeEffect.addActionListener(actionListener);
		waveEffect.addActionListener(actionListener);
		explodeEffect.addActionListener(actionListener);

		flowPanel1.setOpaque(false);
		flowPanel2.setOpaque(false);
		preview.setOpaque(false);
		outlineEffect.setOpaque(false);
		punchEffect.setOpaque(false);
		writeEffect.setOpaque(false);
		waveEffect.setOpaque(false);
		explodeEffect.setOpaque(false);
	}
}