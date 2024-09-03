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
package com.pump.showcase.app;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import com.pump.plaf.CircularProgressBarUI;
import com.pump.showcase.demo.ShowcaseDemo;
import com.pump.swing.JLink;
import com.pump.text.html.QHTMLEditorKit;
import com.pump.data.Property;

public class ShowcaseDemoPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final String NAME_LOADING = "loading";
	private static final String NAME_DEMO = "demo";

	ShowcaseDemoInfo info;

	CardLayout contentCardLayout = new CardLayout();
	JPanel contentCardPanel = new JPanel(contentCardLayout);
	JPanel loadingPanel;
	JProgressBar progressBar = new JProgressBar();
	Property<String> searchPhrase;
	JTextArea descriptionTextArea;

	public ShowcaseDemoPanel(ShowcaseDemoInfo info,
			Property<String> searchPhrase) {
		super(new GridBagLayout());
		this.info = info;
		this.searchPhrase = searchPhrase;

		info.addPropertyChangeListener(new PropertyChangeListener() {
			Runnable refreshContentPanelRunnable = new Runnable() {
				public void run() {
					refreshContentPanel();
				}
			};

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (SwingUtilities.isEventDispatchThread()) {
					refreshContentPanelRunnable.run();
				} else {
					SwingUtilities.invokeLater(refreshContentPanelRunnable);
				}
			}
		});

		loadingPanel = createLoadingPanel();
		contentCardPanel.add(loadingPanel, NAME_LOADING);
		contentCardLayout.show(contentCardPanel, NAME_LOADING);

		progressBar.setIndeterminate(true);

		descriptionTextArea = createTextArea(info.getDemo().getSummary(), 14);

		GridBagConstraints c = new GridBagConstraints();
		c.weighty = 0;
		c.weightx = 0;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(6, 28, 3, 28);
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.BOTH;
		add(descriptionTextArea, c);

		List<Class<?>> pumpClasses = getPumpClasses(info.getDemo().getClasses());
		if (!pumpClasses.isEmpty()) {
			c.gridy++;
			c.insets = new Insets(0,24,0,28);
			add(createLinks(pumpClasses), c);
		}

		c.gridy++;
		c.weightx = 1;
		c.insets = new Insets(0, 0, 0, 0);
		add(new JSeparator(), c);

		c.gridy++;
		c.weighty = 1;
		c.insets = new Insets(3, 3, 3, 3);
		add(contentCardPanel, c);

		refreshContentPanel();
	}

	private JComponent createLinks(List<Class<?>> pumpClasses) {
		JPanel panel = new JPanel(new FlowLayout(3));
		JLabel label = new JLabel("See:");
		Font font = label.getFont();
		font = font.deriveFont(Math.max(font.getSize2D() - 2, 11));
		label.setFont(font);
		panel.add(label);
		for (Class z : pumpClasses) {
			String str = z.getName().replace(".", "/");
			String text = z.getSimpleName();
			try {
				URL url = new URL("https://github.com/mickleness/pumpernickel/blob/master/src/main/java/" + str + ".java");
				JLink link = new JLink(text, url);
				link.setFont(font);
				link.setToolTipText(url.toString());
				panel.add(link);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return panel;
	}

	private List<Class<?>> getPumpClasses(Class<?>[] classes) {
		List<Class<?>> returnValue = new ArrayList<>();
		for (Class z : classes) {
			if (z.getName().startsWith("com")) {
				returnValue.add(z);
			}
		}
		returnValue.add(info.getDemo().getClass());
		return returnValue;
	}

	private JPanel createLoadingPanel() {
		JPanel returnValue = new JPanel(new GridBagLayout());
		progressBar.setUI(new CircularProgressBarUI());
		progressBar.setPreferredSize(new Dimension(90, 90));

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;

		returnValue.add(progressBar, c);

		return returnValue;
	}

	private void refreshContentPanel() {
		if (contentCardPanel.getComponentCount() == 1 && info.isDemoLoaded()) {
			ShowcaseDemo demo = info.getDemo();
			contentCardPanel.add(demo, NAME_DEMO);
			contentCardLayout.show(contentCardPanel, NAME_DEMO);
		}
	}

	private JTextArea createTextArea(String str, float fontSize) {
		JTextArea t = new JTextArea(str);
		Font font = UIManager.getFont("Label.font");
		if (font == null)
			font = t.getFont();
		t.setFont(font.deriveFont(fontSize));
		t.setEditable(false);
		t.setOpaque(false);
		t.setLineWrap(true);
		t.setWrapStyleWord(true);
		return t;
	}

	public JEditorPane createTextPane() {
		JEditorPane textPane = new JEditorPane();
		textPane.setEditable(false);
		HTMLEditorKit kit = new QHTMLEditorKit();
		textPane.setEditorKit(kit);

		StyleSheet styleSheet = kit.getStyleSheet();

		styleSheet.addRule(
				"body {  padding: 12px 12px 12px 12px;  margin: 0;  font-family: sans-serif;  color: black;  background: white;  background-position: top left;  background-attachment: fixed;  background-repeat: no-repeat;}");

		styleSheet.addRule(
				"h1 { color: #005a9c; \n" + "           font: bold 150%; \n"
						+ "           width: max-content }");

		styleSheet.addRule("h2 { padding: 4px 10px 4px 10px; \n"
				+ "           background-color: #005a9c; \n"
				+ "           font: bold 130%; \n"
				+ "           color: white; \n"
				+ "           width: max-content; \n"
				+ "           margin-right:auto; }");

		styleSheet.addRule(
				"h3 { color: #005a9c; \n" + "           font: bold 120%; }");

		styleSheet.addRule(
				"h4 { color: #005a9c; \n" + "           font: bold 110%; }");

		styleSheet.addRule("pre { font-family: consolas, monospace;\n"
				+ "            font-size: 103%;\n"
				+ "            background-color: #ffffe9; \n"
				+ "            width: max-content; \n"
				+ "            padding: 4px;\n"
				+ "            padding-left: 8px;\n"
				+ "            margin-left: 8px;\n"
				+ "            border-left: 2px solid #83e377 }");

		styleSheet.addRule("code { font-family: consolas, monospace;\n"
				+ "            font-size: 103%; font-weight:bold; }");

		styleSheet.addRule(
				"blockquote { color: #222222; \n" + "font-style: italic;}");

		textPane.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					URL url = e.getURL();
					String str = e.getDescription();
					if (str != null && str.startsWith("resource:")) {
						str = str.substring("resource:".length());
						searchPhrase.setValue(str);
						return;
					}
					try {
						Desktop.getDesktop().browse(url.toURI());
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		return textPane;
	}
}