package com.pump.showcase;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.JComponent;
import javax.swing.JLabel;

import com.pump.awt.Dimension2D;
import com.pump.image.AquaImage;
import com.pump.image.pixel.Scaling;
import com.pump.inspector.Inspector;

public class NSImageDemo extends ShowcaseIconDemo {

	public NSImageDemo() {
	}

	@Override
	public String getTitle() {
		return "NSImage Demo";
	}

	@Override
	public String getSummary() {
		return "This shows several NSImages available only on Mac.";
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
		BufferedImage bi = AquaImage.get(id).getBufferedImage();
		Dimension d = new Dimension(bi.getWidth(), bi.getHeight());
		Dimension d2 = Dimension2D.scaleProportionally(d, maxConstrainingSize);
		if (d2.width < d.width || d2.height < d.height) {
			bi = Scaling.scale(bi, d2);
		}
		return bi;
	}

	@Override
	protected String[] getImageIDs() {
		Collection<String> ids = new HashSet<>();
		for (Field field : AquaImage.class.getFields()) {
			if (AquaImage.class.isAssignableFrom(field.getType())) {
				try {
					AquaImage i = (AquaImage) field.get(null);
					ids.add(i.getName());
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return ids.toArray(new String[ids.size()]);
	}

	@Override
	protected JComponent createPopupContents(ShowcaseIcon icon) {
		// TODO: configure animating inspector to animate on first reveal
		Inspector inspector = new Inspector();
		for (String id : icon.ids) {
			String desc = AquaImage.get(id).getDescription();
			JLabel nameLabel = new JLabel(id + ":");
			JLabel descLabel = new JLabel();
			if (desc != null && !desc.trim().isEmpty()) {
				descLabel.setText(desc);
			} else {
				descLabel.setText("(No description)");
				descLabel.setFont(nameLabel.getFont().deriveFont(Font.ITALIC));
			}
			nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
			nameLabel.setLabelFor(descLabel);
			inspector.addRow(nameLabel, descLabel, false);

			String availability = AquaImage.get(id).getAvailability();
			if (availability != null && !availability.trim().isEmpty()) {
				inspector.addRow(new JLabel(""), new JLabel(availability),
						false);
			}
		}
		return inspector.getPanel();
	}

}