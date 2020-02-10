package com.pump.showcase;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Collection;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.pump.awt.Dimension2D;
import com.pump.icon.AquaIcon;
import com.pump.icon.ScaledIcon;

public class AquaIconDemo extends ShowcaseIconDemo {

	private static final long serialVersionUID = 1L;

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getKeywords() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<?>[] getClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected BufferedImage getImage(String id) {
		Icon icon = AquaIcon.get(id);
		Dimension d = new Dimension(icon.getIconWidth(), icon.getIconHeight());
		Dimension d2 = Dimension2D.scaleProportionally(d, maxConstrainingSize);
		if (d2.width != d.width || d2.height != d.height) {
			icon = new ScaledIcon(icon, d2.width, d2.height);
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

}
