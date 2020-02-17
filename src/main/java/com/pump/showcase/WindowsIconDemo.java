package com.pump.showcase;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.pump.icon.Win32ShellIcon;

/**
 * This demonstrates the Win32ShellIcon class.
 */
public class WindowsIconDemo extends ShowcaseIconDemo {

	private static final long serialVersionUID = 1L;

	JComboBox<String> sizeComboBox;

	public WindowsIconDemo() {
	}

	@Override
	public String getTitle() {
		return "WindowsIcon Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates the icons accessible via the Win32ShellIcon class.";
	}

	@Override
	public URL getHelpURL() {
		return WindowsIconDemo.class.getResource("windowsIconDemo.html");
	}

	@Override
	public String[] getKeywords() {
		List<String> words = new ArrayList<>();
		words.add("Windows");
		words.add("icon");
		for (String id : Win32ShellIcon.getIDs()) {
			String name = Win32ShellIcon.get(id).getName();
			words.add(name);
			
		}
		return words.toArray(new String[words.size()]);
	}

	@Override
	public Class<?>[] getClasses() {
		List<Class<?>> classes = new LinkedList<>();
		classes.add(Win32ShellIcon.class);
		return classes.toArray(new Class[classes.size()]);
	}

	@Override
	protected BufferedImage getImage(String id, Dimension maxConstrainingSize) {
		Win32ShellIcon i = Win32ShellIcon.get(id);
		Icon icon;
		
		if(maxConstrainingSize.width>16) {
			icon = i.getIcon(Win32ShellIcon.Size.WIDTH_32);
		} else {
			icon = i.getIcon(Win32ShellIcon.Size.WIDTH_16);
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
		Collection<String> ids = Win32ShellIcon.getIDs();
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
			Win32ShellIcon i = Win32ShellIcon.get(id);
			String name = i.getName();
			p.add(new JLabel(name), c);
			c.gridy++;
		}
		return p;
	}
	
	@Override
	protected JComboBox<String> getSizeControl() {
		if(sizeComboBox==null) {
			sizeComboBox = new JComboBox<>();
			sizeComboBox.addItem("16 Pixels");
			sizeComboBox.addItem("32 Pixels");
			sizeComboBox.setSelectedIndex(1);
			sizeComboBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					refreshCellSize();
				}
			});
		}
		return sizeComboBox;
	}

	@Override
	protected int getCellSize() {
		return getSizeControl().getSelectedIndex()==0 ? 16 : 32;
	}

}
