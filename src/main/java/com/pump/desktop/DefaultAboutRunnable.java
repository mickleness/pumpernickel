/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.desktop;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.lang.ref.WeakReference;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.pump.swing.JLink;

/**
 * This shows a JDialog containing the DesktopApplication's image, simple
 * application name, version, and copyright info.
 * <p>
 * (If any of those properties are undefined then they are skipped.)
 */
public class DefaultAboutRunnable implements Runnable {
	WeakReference<JDialog> lastDialogRef;

	@Override
	public void run() {
		JDialog lastDialog = lastDialogRef == null ? null : lastDialogRef.get();
		if (lastDialog != null && lastDialog.isShowing()) {
			lastDialog.toFront();
			return;
		}

		DesktopApplication app = DesktopApplication.get();
		String name = app.getSimpleName();
		String version = app.getVersion();
		String copyright = app.getCopyright();
		URL url = app.getURL();

		JLabel versionLabel = version == null ? null : new JLabel("Version "
				+ version);
		JLabel copyrightLabel = name == null ? null : new JLabel(copyright);

		Font bigFont = UIManager.getFont("InternalFrame.titleFont");
		Font smallFont = UIManager.getFont("IconButton.font");

		JComponent nameComponent = null;
		if (name != null) {
			if (url == null) {
				JLabel label = name == null ? null : new JLabel(name);
				if (bigFont != null)
					label.setFont(bigFont);
				label.setFont(label.getFont().deriveFont(Font.BOLD));
				nameComponent = label;
				;
			} else {
				JLink link = new JLink(name, url);
				if (bigFont != null)
					link.setFont(bigFont);
				link.setFont(link.getFont().deriveFont(Font.BOLD));
				nameComponent = link;
			}
		}

		if (versionLabel != null) {
			if (smallFont != null)
				versionLabel.setFont(smallFont);
		}

		if (copyrightLabel != null) {
			if (smallFont != null)
				copyrightLabel.setFont(smallFont);
		}

		BufferedImage image = app.getImage();

		JDialog dialog = new JDialog();
		JPanel panel = new JPanel(new GridBagLayout());
		dialog.setContentPane(panel);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(5, 5, 5, 5);
		if (image != null) {
			panel.add(new JLabel(new ImageIcon(image)), c);
		}

		if (nameComponent != null) {
			c.gridy++;
			panel.add(nameComponent, c);
		}

		if (versionLabel != null) {
			c.gridy++;
			panel.add(versionLabel, c);
		}

		if (copyrightLabel != null) {
			c.gridy++;
			panel.add(copyrightLabel, c);
		}

		panel.setBorder(new EmptyBorder(8, 8, 8, 8));

		dialog.setResizable(false);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);

		lastDialogRef = new WeakReference<>(dialog);
	}

}