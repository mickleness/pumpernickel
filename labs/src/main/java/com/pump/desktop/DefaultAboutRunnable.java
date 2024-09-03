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

import javax.swing.*;
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

	protected String appSimpleName, appVersion, appCopyright;
	protected URL appURL;

	/**
	 * Any of these may be null
	 */
	protected JLabel versionLabel, copyrightLabel, imageLabel;

	/**
	 * This may be either a JLabel or a JLink.
	 */
	protected JComponent appNameLabel;

	@Override
	public void run() {
		JDialog lastDialog = lastDialogRef == null ? null : lastDialogRef.get();
		if (lastDialog != null && lastDialog.isShowing()) {
			lastDialog.toFront();
			return;
		}

		DesktopApplication app = DesktopApplication.get();
		appSimpleName = app.getSimpleName();
		appVersion = app.getVersion();
		appCopyright = app.getCopyright();
		appURL = app.getURL();

		versionLabel = appVersion == null ? null
				: new JLabel("Version " + appVersion);
		copyrightLabel = appCopyright == null ? null : new JLabel(appCopyright);

		Font bigFont = UIManager.getFont("InternalFrame.titleFont");
		Font smallFont = UIManager.getFont("IconButton.font");

		appNameLabel = null;
		if (appSimpleName != null) {
			if (appURL == null) {
				JLabel label = appSimpleName == null ? null : new JLabel(appSimpleName);
				if (bigFont != null)
					label.setFont(bigFont);
				label.setFont(label.getFont().deriveFont(Font.BOLD));
				appNameLabel = label;
				;
			} else {
				JLink link = new JLink(appSimpleName, appURL);
				if (bigFont != null)
					link.setFont(bigFont);
				link.setFont(link.getFont().deriveFont(Font.BOLD));
				appNameLabel = link;
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
		imageLabel = null;
		if (image != null) {
			imageLabel = new JLabel(new ImageIcon(image));
		}

		JDialog dialog = showDialog(imageLabel, appNameLabel, versionLabel, copyrightLabel);

		lastDialogRef = new WeakReference<>(dialog);
	}

	protected JDialog showDialog(JComponent... components) {
		JDialog dialog = new JDialog();
		JPanel panel = new JPanel(new GridBagLayout());
		dialog.setContentPane(panel);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = c.weighty = 1;
		c.insets = new Insets(5, 5, 5, 5);

		for (JComponent component : components) {
			if (component != null) {
				panel.add(component, c);
				c.gridy++;
			}
		}

		panel.setBorder(new EmptyBorder(8, 8, 8, 8));

		dialog.setResizable(false);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);

		return dialog;
	}

}