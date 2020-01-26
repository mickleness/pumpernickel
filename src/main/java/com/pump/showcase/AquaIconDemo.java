package com.pump.showcase;

import java.net.URL;

import javax.swing.Icon;

import com.pump.icon.AquaIcon;
import com.pump.plaf.LabelCellRenderer;

public class AquaIconDemo extends ShowcaseIconDemo {

	private static final long serialVersionUID = 1L;

	public AquaIconDemo() {
		images.addAll(AquaIcon.getIDs());

		list.setCellRenderer(new LabelCellRenderer<String>() {

			@Override
			protected void formatLabel(String value) {
				Icon icon = AquaIcon.get(value);
				label.setIcon(icon);
				label.setText("");
				label.setToolTipText(AquaIcon.getDescription(value));
			}

		});
	}

	@Override
	public String getTitle() {
		return "Aqua SystemIcon Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates com.apple.laf.AquaIcon.SystemIcons available only on Mac.";
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

}
