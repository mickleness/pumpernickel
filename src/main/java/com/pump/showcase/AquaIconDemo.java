package com.pump.showcase;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Collection;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import com.pump.awt.Dimension2D;
import com.pump.icon.AquaIcon;
import com.pump.icon.ScaledIcon;
import com.pump.inspector.Inspector;

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
		Inspector inspector = new Inspector();
		for (String id : icon.ids) {
			inspector.addRow(new JLabel("Alias:"),
					new JLabel(AquaIcon.getDescription(id)), false);
		}
		return inspector.getPanel();
	}

}
