package com.pump.showcase.demo;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.awt.Dimension2D;
import com.pump.icon.AquaIcon;
import com.pump.icon.IconUtils;
import com.pump.swing.popover.JPopover;

/**
 * This demonstrates the AquaIcon class and its built-in examples.
 */
public class AquaIconDemo extends ShowcaseIconDemo {

	private static final long serialVersionUID = 1L;

	JSlider sizeSlider;

	public AquaIconDemo() {
	}

	@Override
	public String getTitle() {
		return "AquaIcon Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates com.apple.laf.AquaIcons available only on Mac.";
	}

	@Override
	public URL getHelpURL() {
		return AquaIconDemo.class.getResource("aquaIconDemo.html");
	}

	@Override
	public String[] getKeywords() {
		List<String> words = new ArrayList<>();
		words.add("Aqua");
		words.add("icon");
		for (String id : AquaIcon.getIDs()) {
			String desc = AquaIcon.getDescription(id);
			if (desc != null && desc.trim().length() > 0) {
				words.addAll(Arrays.asList(desc.split(" ")));
			}
		}
		return words.toArray(new String[words.size()]);
	}

	@Override
	public Class<?>[] getClasses() {
		List<Class<?>> classes = new LinkedList<>();
		classes.add(AquaIcon.class);
		try {
			classes.add(Class.forName("com.apple.laf.AquaIcon"));
		} catch (ClassNotFoundException e) {
			// do nothing
		}
		return classes.toArray(new Class[classes.size()]);
	}

	@Override
	protected BufferedImage getImage(String id, Dimension maxConstrainingSize) {
		Icon icon = AquaIcon.get(id);
		Dimension d = new Dimension(icon.getIconWidth(), icon.getIconHeight());
		Dimension d2 = Dimension2D.scaleProportionally(d, maxConstrainingSize);
		if (d2.width != d.width || d2.height != d.height) {
			icon = IconUtils.createScaledIcon(icon, d2.width, d2.height);
		}
		BufferedImage bi = new BufferedImage(icon.getIconWidth(),
				icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		icon.paintIcon(null, g, 0, 0);
		g.dispose();
		return bi;
	}

	@Override
	protected String[] getImageIDs() {
		Collection<String> ids = AquaIcon.getIDs();
		return ids.toArray(new String[ids.size()]);
	}

	@Override
	protected JComponent createPopupContents(ShowcaseIcon icon) {
		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(3, 3, 3, 3);
		for (String id : icon.ids) {
			String desc = AquaIcon.getDescription(id);
			StringBuilder sb = new StringBuilder();
			if (desc == null || desc.trim().isEmpty()) {
				sb.append(id);
			} else {
				sb.append(desc + " (" + id + ")");
			}
			p.add(new JLabel(sb.toString()), c);
			c.gridy++;
		}
		return p;
	}

	@Override
	protected JSlider getSizeControl() {
		if (sizeSlider == null) {
			sizeSlider = new JSlider(16, 200, 48);

			JPopover.add(sizeSlider, " pixels");
			sizeSlider.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					refreshCellSize();
				}
			});
		}
		return sizeSlider;
	}

	@Override
	protected int getCellSize() {
		return getSizeControl().getValue();
	}
}
