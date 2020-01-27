package com.pump.showcase;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;

import com.pump.awt.Dimension2D;
import com.pump.image.AquaImage;
import com.pump.image.pixel.Scaling;

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

}