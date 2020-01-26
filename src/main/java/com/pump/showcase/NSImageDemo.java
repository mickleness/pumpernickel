package com.pump.showcase;

import java.lang.reflect.Field;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.pump.image.AquaImage;
import com.pump.plaf.LabelCellRenderer;

public class NSImageDemo extends ShowcaseIconDemo {

	public NSImageDemo() {
		for (Field field : AquaImage.class.getFields()) {
			if (AquaImage.class.isAssignableFrom(field.getType())) {
				try {
					AquaImage i = (AquaImage) field.get(null);
					images.add(i.getName());
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		list.setCellRenderer(new LabelCellRenderer<String>() {

			@Override
			protected void formatLabel(String name) {
				try {
					AquaImage img = AquaImage.get(name);
					Icon icon = new ImageIcon(img.getBufferedImage());
					label.setIcon(icon);
					label.setText("");
					label.setToolTipText(name);
				} catch (Exception e) {
					throw new RuntimeException(
							"An error occurred processing \"" + name + "\"");
				}
			}

		});
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

}