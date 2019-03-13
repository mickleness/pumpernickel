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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.text.NumberFormat;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.pump.awt.Scribbler;
import com.pump.awt.text.BlockLetter;
import com.pump.awt.text.ExplodeTextEffect;
import com.pump.awt.text.OutlineTextEffect;
import com.pump.awt.text.PunchTextEffect;
import com.pump.awt.text.TextEffect;
import com.pump.awt.text.WaveTextEffect;
import com.pump.awt.text.WriteTextEffect;
import com.pump.awt.text.writing.WritingFont;
import com.pump.inspector.InspectorGridBagLayout;
import com.pump.inspector.InspectorLayout;
import com.pump.swing.AnimationController;
import com.pump.swing.FontComboBox;
import com.pump.swing.JColorWell;
import com.pump.swing.PartialLineBorder;

/**
 * This demo showcases a few different TextEffects.
 * <p>
 * Here is a sample screenshot of this showcase demo:
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/showcase/TextEffectDemo.png"
 * alt="A screenshot of the TextEffectDemo.">
 *
 */
public class TextEffectDemo extends ShowcaseDemo {
	private static final long serialVersionUID = 1L;

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
				float fraction = (float) (controller.getTime() / controller
						.getDuration());
				if (fraction > 1)
					fraction = 1;
				g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
						RenderingHints.VALUE_STROKE_PURE);
				effect.paint(g, fraction);
			}
		}
	}

	AnimationController controller = new AnimationController();

	FontComboBox fontComboBox = new FontComboBox();
	JSpinner fontSize = new JSpinner(new SpinnerNumberModel(55, 20, 100, 5));
	JComboBox<String> effectType = new JComboBox<>();
	JLabel duration = new JLabel();
	JLabel durationLabel = new JLabel("Duration:");
	JLabel fillLabel = new JLabel("Color:");
	JLabel strokeLabel = new JLabel("Border:");
	JLabel shadowLabel = new JLabel("Shadow:");
	JColorWell fill = new JColorWell(OutlineTextEffect.DEFAULT_FILL);
	JColorWell shadow = new JColorWell(OutlineTextEffect.DEFAULT_FILL.darker());
	JColorWell stroke = new JColorWell(OutlineTextEffect.DEFAULT_STROKE);

	JTextField textField = new JTextField("Type Text Here!");

	TextEffect effect;
	float fraction;

	ChangeListener changeListener = new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
			refreshControls();
		}
	};

	ActionListener actionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			refreshControls();
		}
	};

	PreviewPanel preview = new PreviewPanel();
	JPanel controls = new JPanel();
	JLabel fontLabel1 = new JLabel("Font:");
	JLabel fontSizeLabel = new JLabel("Size:");
	JLabel fontLabel2 = new JLabel("Font:");
	JRadioButton fontComicNeue = new JRadioButton("Comic Neue", true);
	JRadioButton fontCalligraphy = new JRadioButton("Calligraphy", false);

	public TextEffectDemo() {
		setLayout(new GridBagLayout());

		InspectorLayout layout = new InspectorGridBagLayout(controls);
		layout.addRow(new JLabel("Text:"), textField, true);
		layout.addRow(new JLabel("Type:"), effectType, false);
		layout.addRow(fontLabel1, fontComboBox, false);
		layout.addRow(fontLabel2, fontComicNeue, fontCalligraphy);
		layout.addRow(fontSizeLabel, fontSize, false);
		layout.addRow(fillLabel, fill, false);
		layout.addRow(strokeLabel, stroke, false);
		layout.addRow(shadowLabel, shadow, false);

		ButtonGroup g = new ButtonGroup();
		g.add(fontComicNeue);
		g.add(fontCalligraphy);

		// always last, since it's uneditable
		layout.addRow(durationLabel, duration, false);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		add(controls, c);
		c.gridy++;
		c.weighty = 1;
		c.insets = new Insets(20, 20, 0, 20);
		add(preview, c);
		c.gridy++;
		c.weighty = 0;
		c.insets = new Insets(0, 20, 20, 20);
		add(controller, c);
		preview.setBorder(new PartialLineBorder(Color.gray, true, true, false,
				true));

		controller.addPropertyChangeListener(AnimationController.TIME_PROPERTY,
				new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						preview.repaint();
					}

				});

		effectType.addItem("Outline");
		effectType.addItem("Punch");
		effectType.addItem("Wave");
		effectType.addItem("Explode");
		effectType.addItem("Write");

		textField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				refreshControls();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				refreshControls();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				refreshControls();
			}

		});

		effectType.addActionListener(actionListener);
		fontComboBox.addActionListener(actionListener);
		fontComicNeue.addActionListener(actionListener);
		fontCalligraphy.addActionListener(actionListener);
		fontSize.addChangeListener(changeListener);
		shadow.getColorSelectionModel().addChangeListener(changeListener);
		fill.getColorSelectionModel().addChangeListener(changeListener);
		stroke.getColorSelectionModel().addChangeListener(changeListener);

		fontComboBox.selectFont("Impact");

		fontCalligraphy
				.setToolTipText("This doesn't support capital letters yet, sorry.");

		preview.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				refreshControls();
			}

		});

		refreshControls();
	}

	private void refreshControls() {
		String effectName = (String) effectType.getSelectedItem();

		String text = textField.getText();
		if ("Outline".equals(effectName)) {
			effect = new OutlineTextEffect(getEffectFont(), text,
					preview.getWidth(), preview.getHeight(), fill
							.getColorSelectionModel().getSelectedColor(),
					stroke.getColorSelectionModel().getSelectedColor(), shadow
							.getColorSelectionModel().getSelectedColor());
			stroke.setVisible(true);
			strokeLabel.setVisible(true);
			shadowLabel.setVisible(true);
			shadow.setVisible(true);
		} else if ("Punch".equals(effectName)) {
			effect = new PunchTextEffect(getEffectFont(), text,
					preview.getWidth(), preview.getHeight(), fill
							.getColorSelectionModel().getSelectedColor(),
					shadow.getColorSelectionModel().getSelectedColor());
			stroke.setVisible(false);
			strokeLabel.setVisible(false);
			shadowLabel.setVisible(true);
			shadow.setVisible(true);
		} else if ("Wave".equals(effectName)) {
			effect = new WaveTextEffect(getEffectFont(), text,
					preview.getWidth(), preview.getHeight(), fill
							.getColorSelectionModel().getSelectedColor(),
					stroke.getColorSelectionModel().getSelectedColor());
			stroke.setVisible(true);
			strokeLabel.setVisible(true);
			shadowLabel.setVisible(false);
			shadow.setVisible(false);
		} else if ("Explode".equals(effectName)) {
			effect = new ExplodeTextEffect(getEffectFont(), text,
					preview.getWidth(), preview.getHeight(), fill
							.getColorSelectionModel().getSelectedColor());
			stroke.setVisible(false);
			strokeLabel.setVisible(false);
			shadowLabel.setVisible(false);
			shadow.setVisible(false);
		} else {
			WritingFont font = fontComicNeue.isSelected() ? WritingFont.COMIC_NEUE
					: WritingFont.CALLIGRAPHY;
			// TODO: calligraphy doesn't support uppercase yet
			if (!fontComicNeue.isSelected())
				text = text.toLowerCase();
			effect = new WriteTextEffect(font, getEffectFont().getSize(), text,
					preview.getWidth(), preview.getHeight(), fill
							.getColorSelectionModel().getSelectedColor());
			stroke.setVisible(false);
			strokeLabel.setVisible(false);
			shadowLabel.setVisible(false);
			shadow.setVisible(false);
		}
		boolean writing = effect instanceof WriteTextEffect;

		fontLabel1.setVisible(!writing);
		fontComboBox.setVisible(!writing);
		fontLabel2.setVisible(writing);
		fontComicNeue.setVisible(writing);
		fontCalligraphy.setVisible(writing);

		int d;
		if (effect instanceof PunchTextEffect
				|| effect instanceof ExplodeTextEffect
				|| effect instanceof WriteTextEffect) {
			d = textField.getText().length() * 90;
		} else {
			d = textField.getText().length() * 50;
		}
		float seconds = ((float) d) / 1000f;
		duration.setText(NumberFormat.getInstance().format(seconds) + " s");
		controller.setDuration(seconds);

		preview.repaint();
	}

	private Font getEffectFont() {
		Font font = (Font) fontComboBox.getSelectedItem();
		float size = ((Number) fontSize.getValue()).floatValue();
		font = font.deriveFont(size);
		return font;
	}

	@Override
	public String getTitle() {
		return "TextEffect Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates a few new animations to render text.";
	}

	@Override
	public URL getHelpURL() {
		return TextEffectDemo.class.getResource("textEffectDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "animation", "text", "effect", "font",
				"comic-neue", "calligraphy" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { TextEffect.class, PunchTextEffect.class,
				WaveTextEffect.class, OutlineTextEffect.class,
				ExplodeTextEffect.class, Scribbler.class, FontComboBox.class,
				WriteTextEffect.class, WritingFont.class, BlockLetter.class };
	}
}