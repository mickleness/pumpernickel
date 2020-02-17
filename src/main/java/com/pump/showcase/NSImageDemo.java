package com.pump.showcase;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.awt.Dimension2D;
import com.pump.image.NSImage;
import com.pump.image.pixel.Scaling;
import com.pump.inspector.Inspector;

/**
 * This demonstrates the NSImage class
 */
public class NSImageDemo extends ShowcaseIconDemo {
	private static final long serialVersionUID = 1L;
	
	JSlider sizeSlider;

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
		return NSImageDemo.class.getResource("nsImageDemo.html");
	}

	@Override
	public String[] getKeywords() {
		List<String> words = new ArrayList<>();
		words.add("NSImage");
		words.add("Image");
		for (String id : getImageIDs()) {
			String name = NSImage.get(id).getName();
			words.addAll(splitCamelCase(name));
		}
		return words.toArray(new String[words.size()]);
	}

	private static List<String> splitCamelCase(String word) {
		List<String> words = new LinkedList<>();
		StringBuilder sb = new StringBuilder();
		for (int a = 0; a < word.length(); a++) {
			char ch = word.charAt(a);
			if (Character.isUpperCase(ch)) {
				if (sb.length() > 0) {
					words.add(sb.toString());
					sb.delete(0, sb.length());
				}
			}
			sb.append(ch);
		}
		if (sb.length() > 0) {
			words.add(sb.toString());
		}
		return words;
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { Image.class };
	}

	@Override
	protected BufferedImage getImage(String id, Dimension maxConstrainingSize) {
		BufferedImage bi = NSImage.get(id)
				.getBufferedImage(maxConstrainingSize);
		Dimension d = new Dimension(bi.getWidth(), bi.getHeight());
		Dimension d2 = Dimension2D.scaleProportionally(d, maxConstrainingSize);
		if (d2.width < d.width || d2.height < d.height) {
			bi = Scaling.scale(bi, d2);
		}
		return bi;
	}

	@Override
	protected String[] getImageIDs() {
		Collection<String> ids = NSImage.getIDs();
		return ids.toArray(new String[ids.size()]);
	}

	@Override
	protected JComponent createPopupContents(ShowcaseIcon icon) {
		// TODO: configure animating inspector to animate on first reveal

		Collection<Dimension> sizes = new HashSet<>();
		Map<String, Dimension> sizeMap = new HashMap<>();
		for (String id : icon.ids) {
			NSImage img = NSImage.get(id);
			BufferedImage bi = img.getBufferedImage();
			Dimension size = new Dimension(bi.getWidth(), bi.getHeight());
			sizes.add(size);
			sizeMap.put(id, size);
		}

		Inspector inspector = new Inspector();
		boolean printSeparateSizes = sizes.size() > 1;

		if (!printSeparateSizes) {
			Dimension size = sizes.iterator().next();
			inspector.addRow(new JLabel("Default Size:"), new JLabel(
					toString(size)));
		}

		for (String id : icon.ids) {
			String desc = NSImage.get(id).getDescription();
			JLabel nameLabel = new JLabel(id + ":");
			JLabel descLabel = new JLabel();
			if (desc != null && !desc.trim().isEmpty()) {
				descLabel.setText(desc);
			} else {
				descLabel.setText("(No description)");
				descLabel.setFont(nameLabel.getFont().deriveFont(Font.ITALIC));
			}
			nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
			inspector.addRow(nameLabel, descLabel, false);

			String availability = NSImage.get(id).getAvailability();
			if (availability != null && !availability.trim().isEmpty()) {
				inspector.addRow(new JLabel(""), new JLabel(availability),
						false);
			}

			if (printSeparateSizes) {
				Dimension size = sizeMap.get(id);
				inspector.addRow(new JLabel("Size:"),
						new JLabel(toString(size)));
			}
		}
		return inspector.getPanel();
	}

	private String toString(Dimension size) {
		return size.width + "x" + size.height;
	}

	@Override
	protected JSlider getSizeControl() {
		if(sizeSlider==null) {
			sizeSlider = new JSlider(16, 200, 48);
		
			addSliderPopover(sizeSlider, " pixels");
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