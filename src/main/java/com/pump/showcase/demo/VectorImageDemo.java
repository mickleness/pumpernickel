package com.pump.showcase.demo;

import java.awt.AWTEvent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicPanelUI;

import com.pump.awt.Dimension2D;
import com.pump.desktop.temp.TempFileManager;
import com.pump.geom.TransformUtils;
import com.pump.graphics.Graphics2DContext;
import com.pump.graphics.vector.ImageOperation;
import com.pump.graphics.vector.Operation;
import com.pump.graphics.vector.VectorGraphics2D;
import com.pump.graphics.vector.VectorImage;
import com.pump.image.ImageSize;
import com.pump.inspector.Inspector;
import com.pump.showcase.app.PumpernickelShowcaseApp;
import com.pump.swing.AnimationController;
import com.pump.swing.popover.JPopover;
import com.pump.util.list.AddElementsEvent;
import com.pump.util.list.ChangeElementEvent;
import com.pump.util.list.ListListener;
import com.pump.util.list.RemoveElementsEvent;
import com.pump.util.list.ReplaceElementsEvent;

public class VectorImageDemo extends ShowcaseExampleDemo {
	private static final long serialVersionUID = 1L;

	/**
	 * This property is assigned to an Operation and resolves to a String
	 * stacktrace.
	 */
	private static final String PROPERTY_STACK_TRACE = VectorImageDemo.class
			.getName() + "#stacktrace";

	/**
	 * This listener attaches the PROPERTY_STACK_TRACE to each Operation.
	 */
	public static class StackTraceListener
			implements ListListener<Operation>, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public void elementsAdded(AddElementsEvent<Operation> event) {
			for (Operation op : event.getNewElements()) {
				op.setProperty(PROPERTY_STACK_TRACE, getStackTrace());
			}
		}

		@Override
		public void elementsRemoved(RemoveElementsEvent<Operation> event) {
			for (Operation op : event.getRemovedElements().values()) {
				op.setProperty(PROPERTY_STACK_TRACE, null);
			}
		}

		@Override
		public void elementChanged(ChangeElementEvent<Operation> event) {
			event.getOldElement().setProperty(PROPERTY_STACK_TRACE, null);
			event.getNewElement().setProperty(PROPERTY_STACK_TRACE,
					getStackTrace());

		}

		@Override
		public void elementsReplaced(ReplaceElementsEvent<Operation> event) {
			for (Operation op : event.getOldElements()) {
				op.setProperty(PROPERTY_STACK_TRACE, null);
			}
			for (Operation op : event.getNewElements()) {
				op.setProperty(PROPERTY_STACK_TRACE, getStackTrace());
			}
		}

		private String getStackTrace() {
			StackTraceElement[] e = Thread.currentThread().getStackTrace();
			StringBuilder sb = new StringBuilder();
			boolean record = false;
			for (int a = 0; a < e.length; a++) {
				String str = e[a].toString();
				if (record) {
					sb.append(str + "\n");
				}
				if (str.contains("addOperation")) {
					record = true;
				}
			}
			return sb.toString().trim();
		}

	}

	AWTEventListener keyListener = new AWTEventListener() {

		@Override
		public void eventDispatched(AWTEvent event) {
			KeyEvent e = (KeyEvent) event;
			if (e.getKeyCode() == KeyEvent.VK_F6
					&& e.getID() == KeyEvent.KEY_PRESSED) {
				VectorImage img = new VectorImage();
				img.getOperations().addListListener(new StackTraceListener(),
						false);

				getFrame().paint(img.createGraphics());

				File file = TempFileManager.get().createFile("screenshot",
						"jvg");
				try (FileOutputStream fileOut = new FileOutputStream(file)) {
					img.save(fileOut);
					urls.add(file.toURI().toURL());
					refreshComboBox();
					getFrame().setSelectedDemo(getDemoInfo());
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		}
	};

	List<URL> urls = new LinkedList<>();
	VectorImage img;
	Inspector inspector;
	JComboBox<String> resourceComboBox = new JComboBox<>();
	ActionListener comboBoxListener;
	JSlider instructionSlider = new JSlider(0, 100, 0);

	JPanel previewPanel = new JPanel();
	JPanel previewControllerPanel = new JPanel();
	ChangeListener instructionSliderListener = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			previewPanel.repaint();
		}
	};

	public VectorImageDemo() {
		super(false, false, false, false);

		urls.add(this.getClass().getResource("jvg/InhabitantsPiglet.jvg"));
		urls.add(this.getClass().getResource("jvg/KindergartenTeacher.jvg"));
		urls.add(this.getClass().getResource("jvg/HardDrive.jvg"));
		urls.add(this.getClass().getResource("jvg/FlashDrive.jvg"));

		Toolkit.getDefaultToolkit().addAWTEventListener(keyListener,
				AWTEvent.KEY_EVENT_MASK);

		inspector = new Inspector(configurationPanel);
		inspector.addRow(new JLabel("Resource:"), resourceComboBox);
		inspector.addRow(new JLabel(
				"Type F6 anywhere in this app to add a screenshot to this list."),
				true);

		examplePanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;

		previewPanel.setUI(new BasicPanelUI() {

			@Override
			public void paint(Graphics g, JComponent c) {
				VectorImage img = getVectorImage();
				if (img == null)
					return;
				Rectangle2D bounds = img.getBounds();
				if (bounds == null)
					return;

				Dimension displayBounds = Dimension2D.scaleProportionally(
						new Dimension((int) bounds.getWidth(),
								(int) bounds.getHeight()),
						new Dimension(c.getWidth() - 1, c.getHeight() - 1));

				Rectangle paintBounds = new Rectangle(
						c.getWidth() / 2 - displayBounds.width / 2,
						c.getHeight() / 2 - displayBounds.height / 2,
						displayBounds.width, displayBounds.height);

				AffineTransform tx = TransformUtils
						.createAffineTransform(bounds, paintBounds);
				Graphics2D g2 = (Graphics2D) g.create();
				g2.transform(tx);

				setRenderingHint(img, RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				setRenderingHint(img, RenderingHints.KEY_ALPHA_INTERPOLATION,
						RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
				setRenderingHint(img, RenderingHints.KEY_FRACTIONALMETRICS,
						RenderingHints.VALUE_FRACTIONALMETRICS_ON);
				setRenderingHint(img, RenderingHints.KEY_STROKE_CONTROL,
						RenderingHints.VALUE_STROKE_PURE);
				setRenderingHint(img, RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				setRenderingHint(img, RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

				int selectedOperationIndex = instructionSlider.getValue();
				for (int a = 0; a <= selectedOperationIndex; a++) {
					Operation operation = img.getOperations().get(a);
					operation = filterOperation(operation);
					operation.paint(g2);
				}
				g2.dispose();

				((Graphics2D) g).setStroke(new BasicStroke(1));
				g.setColor(Color.darkGray);
				g.drawRect(0, 0, c.getWidth() - 1, c.getHeight());
			}

			/**
			 * Possibly modify Operations before we render them.
			 * <p>
			 * On Mac, for example, most Aqua L&F images render poorly when
			 * scaled down. This switches those images to BufferedImages, which
			 * render better.
			 */
			private Operation filterOperation(Operation op) {
				if (op instanceof ImageOperation) {
					Image i = ((ImageOperation) op).getImage();
					Dimension d = ImageSize.get(i);
					BufferedImage bi = new BufferedImage(d.width, d.height,
							BufferedImage.TYPE_INT_ARGB);
					Graphics2D g3 = bi.createGraphics();
					g3.drawImage(i, 0, 0, null);
					g3.dispose();
					((ImageOperation) op).setImage(bi);
				}
				return op;
			}

			/**
			 * Set a rendering hint for all existing Operations
			 * 
			 * @param hintKey
			 * @param hintValue
			 * @param changeExistingOperations
			 *            if true then this retroactively modifies the rendering
			 *            hints of all existing Operations.
			 */
			public void setRenderingHint(VectorImage img, Key hintKey,
					Object hintValue) {
				for (Operation op : img.getOperations()) {
					Graphics2DContext newContext = op.getContext();
					newContext.setRenderingHint(hintKey, hintValue);
					op.setContext(newContext);
				}
			}

		});

		examplePanel.removeAll();
		examplePanel.setBorder(null);
		examplePanel.setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.NONE;
		examplePanel.add(previewPanel, c);
		c.gridy++;
		c.fill = GridBagConstraints.BOTH;
		examplePanel.add(previewControllerPanel, c);

		comboBoxListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				refreshResource();
			}
		};

		instructionSlider.addChangeListener(instructionSliderListener);

		refreshComboBox();

		Callable<String> stacktraceTooltipGenerator = new Callable<String>() {

			@Override
			public String call() throws Exception {
				int selectedOperationIndex = instructionSlider.getValue();
				Operation op = getVectorImage().getOperations()
						.get(selectedOperationIndex);
				String stacktrace = (String) op
						.getProperty(PROPERTY_STACK_TRACE);
				if (stacktrace == null)
					return null;
				String[] lines = stacktrace.split("\n");
				for (int a = 0; a < lines.length; a++) {
					if (lines[a].startsWith("com.pump.graphics.vector"))
						continue;
					int i1 = lines[a].indexOf("(");
					int i2 = lines[a].indexOf(")", i1);
					return lines[a].substring(i1 + 1, i2);
				}
				return null;
			}

		};
		JPopover.add(instructionSlider, stacktraceTooltipGenerator);
	}

	private void refreshResource() {
		URL url = urls.get(resourceComboBox.getSelectedIndex());
		try (InputStream in = url.openStream()) {
			VectorImage img = new VectorImage(in);
			setVectorImage(img);
		} catch (RuntimeException e) {
			throw e;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	protected void setVectorImage(VectorImage img) {
		instructionSlider.removeChangeListener(instructionSliderListener);
		try {
			this.img = img;
			int max = img.getOperations().size();
			instructionSlider.getModel().setRangeProperties(max - 1,
					instructionSlider.getExtent(), 0, max - 1, false);

			Dimension d = img.getBounds().getBounds().getSize();
			Dimension maxSize = new Dimension(400, 250);
			Dimension scaledSize = Dimension2D.scaleProportionally(d, maxSize);
			previewPanel.setPreferredSize(scaledSize);

			// resize slider every time preview resizes
			AnimationController.format(previewControllerPanel, null, null,
					instructionSlider);
		} finally {
			instructionSlider.addChangeListener(instructionSliderListener);
			instructionSliderListener.stateChanged(null);
		}
	}

	protected VectorImage getVectorImage() {
		return img;
	}

	private PumpernickelShowcaseApp getFrame() {
		for (Window w : Window.getWindows()) {
			if (w instanceof PumpernickelShowcaseApp)
				return (PumpernickelShowcaseApp) w;
		}
		return null;
	}

	private void refreshComboBox() {
		resourceComboBox.removeActionListener(comboBoxListener);
		resourceComboBox.removeAllItems();
		for (URL url : urls) {
			String name = url.toString();
			name = name.substring(name.lastIndexOf("/") + 1);
			resourceComboBox.addItem(name);
		}
		resourceComboBox.setSelectedIndex(urls.size() - 1);
		resourceComboBox.addActionListener(comboBoxListener);
		refreshResource();
	}

	@Override
	public String getTitle() {
		return "VectorImage Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates the VectorImage's ability to store Graphics2D operations.";
	}

	@Override
	public URL getHelpURL() {
		return VectorImageDemo.class.getResource("vectorImageDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "vector", "Graphics2D", "image", "serializable" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { VectorGraphics2D.class, VectorImage.class,
				Graphics2DContext.class, Operation.class };
	}

}
