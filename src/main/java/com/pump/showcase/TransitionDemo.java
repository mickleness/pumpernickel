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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.animation.BufferedAnimationPanel;
import com.pump.image.transition.AbstractTransition;
import com.pump.image.transition.Transition;
import com.pump.inspector.InspectorGridBagLayout;
import com.pump.inspector.InspectorLayout;
import com.pump.swing.AnimationController;
import com.pump.util.PartitionIterator;

/**
 * An abstract UI to demo a set of transitions.
 */
public abstract class TransitionDemo extends JPanel implements ShowcaseDemo {
	private static final long serialVersionUID = 1L;

	BufferedImage img1;
	BufferedImage img2;
	Map<String, List<Transition>> transitionsByFamily = new TreeMap<>();
	JComboBox<String> transitionFamilyComboBox = new JComboBox<>();
	JComboBox<Transition> transitionComboBox = new JComboBox<>();
	JComboBox<Object> renderingHintsComboBox = new JComboBox<Object>();
	AnimationController controller = new AnimationController();
	JSpinner duration = new JSpinner(new SpinnerNumberModel(2, .1, 100, .1));
	JLabel interpolationLabel = new JLabel("Interpolation Hint:");

	JPanel inspectorPanel = new JPanel();
	TransitionPanel panel;

	public TransitionDemo(Transition[][] transitions) {
		this(AbstractTransition.createImage("A", true), AbstractTransition
				.createImage("B", false), transitions);
	}

	public TransitionDemo(BufferedImage bi1, BufferedImage bi2,
			Transition[][] transitions) {
		img1 = bi1;
		img2 = bi2;
		panel = new TransitionPanel(null);

		transitionFamilyComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				transitionComboBox.removeAllItems();
				String familyName = (String) transitionFamilyComboBox
						.getSelectedItem();
				if (familyName != null) {
					List<Transition> t = transitionsByFamily.get(familyName);
					transitionComboBox.setEnabled(t.size() > 1);
					for (int a = 0; a < t.size(); a++) {
						transitionComboBox.addItem(t.get(a));
					}
				}
			}
		});

		transitionComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Transition t = (Transition) transitionComboBox
						.getSelectedItem();
				panel.setTransition(t);
			}
		});

		renderingHintsComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.refresh();
			}
		});

		for (Transition[] t : transitions) {
			String familyName = getFamilyName(t);
			if (transitionsByFamily.put(familyName, Arrays.asList(t)) != null)
				throw new IllegalArgumentException(
						"Multiple transitions had the same family name: \""
								+ familyName + "\"");
		}
		for (String familyName : transitionsByFamily.keySet()) {
			transitionFamilyComboBox.addItem(familyName);
		}

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.SOUTHWEST;
		add(inspectorPanel, c);
		c.weighty = 1;
		c.gridy++;
		c.fill = GridBagConstraints.NONE;
		add(panel, c);
		c.weightx = 0;
		c.gridy++;
		c.anchor = GridBagConstraints.NORTHWEST;
		add(controller, c);

		Dimension d = controller.getPreferredSize();
		d.width = panel.getPreferredSize().width;
		controller.setPreferredSize(d);

		InspectorLayout layout = new InspectorGridBagLayout(inspectorPanel);
		layout.addRow(new JLabel("Transition Type:"), transitionFamilyComboBox,
				false);
		layout.addRow(new JLabel("Transition:"), transitionComboBox, false);
		layout.addRow(new JLabel("Duration (s):"), duration, false);
		layout.addRow(new JLabel("Rendering Hints"), renderingHintsComboBox,
				false);

		inspectorPanel.setOpaque(false);

		controller.addPropertyChangeListener(AnimationController.TIME_PROPERTY,
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent e) {
						panel.refresh();
					}
				});
		controller.setLooping(true);

		addHierarchyListener(new HierarchyListener() {

			@Override
			public void hierarchyChanged(HierarchyEvent e) {
				if (isShowing()) {
					if (!controller.isPlaying()) {
						controller.play();
					}
				} else {
					if (controller.isPlaying()) {
						controller.pause();
					}
				}
			}

		});

		ChangeListener durationListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				float d = ((Number) duration.getValue()).floatValue();
				controller.setDuration(2 * d); // once for A->B, once for B->A
			}
		};
		duration.addChangeListener(durationListener);
		durationListener.stateChanged(null);

		renderingHintsComboBox.addItem(RenderingHints.VALUE_RENDER_SPEED);
		renderingHintsComboBox.addItem(RenderingHints.VALUE_RENDER_QUALITY);
	}

	/**
	 * Given several Transitions, this identifies the run of words they have in
	 * common. Sometimes there will only be one word (like "Bars"), but
	 * sometimes there may be multiple words (like "Funky Wipe")
	 */
	private String getFamilyName(Transition[] t) {
		String name = t[0].toString();
		String[] words = name.split("\\s");
		PartitionIterator<String> iter = new PartitionIterator<>(
				Arrays.asList(words), 3, 0);
		String bestCandidate = null;
		scanNames: while (iter.hasNext()) {
			List<List<String>> n = iter.next();
			List<String> m = n.get(1);
			StringBuilder sb = new StringBuilder();
			for (int a = 0; a < m.size(); a++) {
				if (a != 0) {
					sb.append(' ');
				}
				sb.append(m.get(a));
			}
			String candidate = sb.toString();

			for (Transition z : t) {
				name = z.toString();
				if (!name.contains(candidate))
					continue scanNames;
			}

			if (bestCandidate == null
					|| candidate.length() > bestCandidate.length())
				bestCandidate = candidate;
		}

		return bestCandidate;

	}

	public RenderingHints getRenderingHints() {
		if (renderingHintsComboBox.getSelectedIndex() == 0) {
			return createSpeedHints();
		} else {
			return createQualityHints();
		}
	}

	public static RenderingHints createQualityHints() {
		RenderingHints hints = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		hints.put(RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		// WARNING: set this to bicubic interpolation brings Windows Vista to
		// its knees.
		hints.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		hints.put(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		hints.put(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		hints.put(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		return hints;
	}

	public static RenderingHints createSpeedHints() {
		RenderingHints hints = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		hints.put(RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_SPEED);
		hints.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		hints.put(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		hints.put(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		hints.put(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);

		return hints;
	}

	class TransitionPanel extends BufferedAnimationPanel {
		private static final long serialVersionUID = 1L;

		Transition transition;

		public TransitionPanel(Transition transition) {
			setTransition(transition);
			setPreferredSize(new Dimension(img1.getWidth(), img1.getHeight()));
		}

		public void setTransition(Transition transition) {
			this.transition = transition;
			refresh();
		}

		Font font = new Font("Mono", 0, 12);
		DecimalFormat format = new DecimalFormat("#.##");

		@Override
		protected void paintAnimation(Graphics2D g, int width, int height) {
			g = (Graphics2D) g.create();
			g.setColor(Color.black);
			g.fillRect(0, 0, width, height);
			float t = controller.getTime() / controller.getDuration() * 2;
			BufferedImage frameA, frameB;
			if (t >= 2) { // for the very last frame
				t = 0;
				frameA = img1;
				frameB = img2;
			} else if (t >= 1) {
				t = t % 1;
				frameA = img2;
				frameB = img1;
			} else {
				frameA = img1;
				frameB = img2;
			}
			g.setRenderingHints(getRenderingHints());
			if (transition != null) {
				transition.paint((Graphics2D) g, frameA, frameB, t);
				Graphics2D g2 = (Graphics2D) g;
				TextLayout tl = new TextLayout(format.format((t * 100)) + "%",
						font, g2.getFontRenderContext());
				Shape outline = tl.getOutline(AffineTransform
						.getTranslateInstance(5, 18));
				g2.setColor(Color.black);
				g2.setStroke(new BasicStroke(2));
				g2.draw(outline);
				g2.setColor(Color.white);
				g2.fill(outline);
			}
		}
	}
}