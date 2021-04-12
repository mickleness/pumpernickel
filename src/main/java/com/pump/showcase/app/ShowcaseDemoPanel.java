package com.pump.showcase.app;

import java.awt.CardLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import com.pump.math.Fraction;
import com.pump.plaf.CircularProgressBarUI;
import com.pump.showcase.demo.ShowcaseDemo;
import com.pump.text.html.QHTMLEditorKit;
import com.pump.util.Property;

public class ShowcaseDemoPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final String NAME_LOADING = "loading";
	private static final String NAME_DEMO = "demo";

	ShowcaseDemoInfo info;

	CardLayout contentCardLayout = new CardLayout();
	JPanel contentCardPanel = new JPanel(contentCardLayout);
	JPanel loadingPanel;
	JProgressBar progressBar = new JProgressBar(0, 11);
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
		Fraction fraction = info.getLoadingProgress();
		if (contentCardPanel.getComponentCount() == 1 && info.isDemoLoaded()
				&& fraction.getNumerator() == fraction.getDenominator()) {
			ShowcaseDemo demo = info.getDemo();
			contentCardPanel.add(demo, NAME_DEMO);
			contentCardLayout.show(contentCardPanel, NAME_DEMO);
		} else {
			if (fraction.getNumerator() == 0) {
				progressBar.setIndeterminate(true);
			} else {
				progressBar.setIndeterminate(false);
				int value = (int) fraction.getNumerator();
				int max = (int) fraction.getDenominator();
				progressBar.getModel().setRangeProperties(value, max, 0, max,
						false);
			}
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

		styleSheet.addRule("h1, h2, h3, h4, h5, h6 { text-align: left }");
		
		styleSheet.addRule(
				"h1 { color: #005a9c; font: bold 160% sans-serif; padding: 6px 20px 6px 20px; width: max-content; background-clip: border-box; border: border: 2px solid #005a9c; border-radius:15px;}");

		styleSheet.addRule(
				"h2 { padding: 4px 15px 4px 15px; background-color: #005a9c; color: white; width: max-content; background-clip: border-box; border: transparent; border-radius:15px;}");
		
		styleSheet.addRule(
				"h3 { font: 120% sans-serif; color: #005a9c; }");
		
		styleSheet.addRule("h4 { font: bold 100% sans-serif }");
		styleSheet.addRule("h5 { font: italic 100% sans-serif }");
		styleSheet.addRule("h6 { font: small-caps 100% sans-serif }");

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
