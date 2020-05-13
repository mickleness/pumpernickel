package com.pump.graphics.vector.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;

import com.pump.awt.Dimension2D;
import com.pump.geom.TransformUtils;
import com.pump.graphics.Graphics2DContext;
import com.pump.graphics.vector.ImageOperation;
import com.pump.graphics.vector.Operation;
import com.pump.graphics.vector.VectorImage;
import com.pump.image.ImageSize;
import com.pump.plaf.AnimationManager;
import com.pump.plaf.LabelCellRenderer;
import com.pump.plaf.QPanelUI.CalloutType;
import com.pump.swing.ContextualMenuHelper;
import com.pump.swing.popover.JPopover;
import com.pump.swing.popover.ListSelectionVisibility;
import com.pump.swing.popup.ListCellPopupTarget;
import com.pump.swing.popup.QPopup;
import com.pump.util.BooleanProperty;
import com.pump.util.list.AddElementsEvent;
import com.pump.util.list.ChangeElementEvent;
import com.pump.util.list.ListListener;
import com.pump.util.list.RemoveElementsEvent;
import com.pump.util.list.ReplaceElementsEvent;
import com.sun.glass.events.KeyEvent;

/**
 * This JPanel inspects all the Operations in a VectorGraphics2D.
 * <p>
 * If you add a StackTraceListener to the ObservableList of Operations: then
 * each Operation stores a copy of the stack trace that adds it. (See
 * {@link #PROPERTY_STACK_TRACE}
 * <p>
 * The user can click each operation (in a vertical list on the left) to see
 * that element render. Right-clicking an element shows the stack trace the
 * rendered that Operation.
 */
public class VectorImageInspector extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final String PROPERTY_VECTOR_IMAGE = "vectorImage";
	private static final String PROPERTY_HIGHLIGHT_OPACITY = "highlightOpacity";

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

	/**
	 * If this property is assigned for Operation objects then this inspector
	 * may help access it.
	 */
	public static final String PROPERTY_STACK_TRACE = VectorImageInspector.class
			.getSimpleName() + "#stackTrace";

	static final String PROPERTY_POPUP_REQUESTED = VectorImageInspector.class
			.getSimpleName() + "#popupRequested";
	static final String PROPERTY_REFRESH_LISTENER = VectorImageInspector.class
			.getSimpleName() + "#refreshListener";
	static final Object ESCAPE_KEY_ACTION = new Object() {
	};
	static final KeyStroke ESCAPE_KEY_STROKE = KeyStroke
			.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

	class ContextualPopoverVisibility extends ListSelectionVisibility<JPanel> {
		boolean wasVisible = false;
		FocusListener focusListener = new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
			}

			@Override
			public void focusLost(FocusEvent e) {
				hide();
			}

		};

		ContextualPopoverVisibility() {
			super(operationList, operationList.getSelectedValue());
		}

		void hide() {
			BooleanProperty popupRequested = getPopupRequested(operationList);
			popupRequested.setValue(false);
		}

		@Override
		public void install(JPopover<JPanel> popover) {
			super.install(popover);
			BooleanProperty popupRequested = getPopupRequested(
					popover.getOwner());
			PropertyChangeListener pcl = new RefreshVisibilityListener(popover);
			popupRequested.addPropertyChangeListener(pcl);
			popover.getOwner().putClientProperty(PROPERTY_REFRESH_LISTENER,
					pcl);
			operationList.addFocusListener(focusListener);
			if (operationList.getInputMap().get(ESCAPE_KEY_STROKE) == null) {
				operationList.getInputMap().put(ESCAPE_KEY_STROKE,
						ESCAPE_KEY_ACTION);
				operationList.getActionMap().put(ESCAPE_KEY_ACTION,
						new AbstractAction() {
							private static final long serialVersionUID = 1L;

							@Override
							public void actionPerformed(ActionEvent e) {
								hide();
							}
						});
			}
		}

		@Override
		public void uninstall(JPopover<JPanel> popover) {
			super.uninstall(popover);
			BooleanProperty popupRequested = getPopupRequested(
					popover.getOwner());
			PropertyChangeListener pcl = (PropertyChangeListener) popover
					.getOwner().getClientProperty(PROPERTY_REFRESH_LISTENER);
			popupRequested.removePropertyChangeListener(pcl);
			operationList.removeFocusListener(focusListener);
			if (operationList.getInputMap()
					.get(ESCAPE_KEY_STROKE) == ESCAPE_KEY_ACTION) {
				operationList.getInputMap().remove(ESCAPE_KEY_STROKE);
			}
		}

		@Override
		public boolean isVisible(JPopover<JPanel> popover) {
			boolean isVisible = super.isVisible(popover);
			BooleanProperty popupRequested = getPopupRequested(operationList);
			if (wasVisible && !isVisible) {
				popupRequested.setValue(false);
				popover.dispose();
			}
			if (popupRequested.getValue() == false)
				isVisible = false;
			wasVisible = isVisible;
			return isVisible;
		}
	}

	private static class RefreshVisibilityListener
			implements PropertyChangeListener {
		JPopover<?> popover;

		RefreshVisibilityListener(JPopover<?> popover) {
			this.popover = popover;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			popover.refreshVisibility(true);
		}
	}

	PropertyChangeListener vectorGraphicsListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			VectorImage img = (VectorImage) getClientProperty(
					PROPERTY_VECTOR_IMAGE);
			DefaultListModel<Operation> newListModel = new DefaultListModel<Operation>();
			for (Operation op : img.getOperations()) {
				newListModel.addElement(op);
			}
			operationList.setModel(newListModel);
		}

	};
	JComponent operationBrowser = new JPanel(new GridBagLayout());
	JList<Operation> operationList = new JList<Operation>();
	Point2D mouseLoc = null;
	JPanel previewPanel = new JPanel() {
		private static final long serialVersionUID = 1L;

		AffineTransform tx;
		MouseInputAdapter mouseListener = new MouseInputAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				mouseMoved(e);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				mouseLoc = null;
				operationList.repaint();
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				if (tx != null) {
					try {
						AffineTransform itx = tx.createInverse();
						mouseLoc = itx.transform(e.getPoint(), null);
						operationList.repaint();
					} catch (NoninvertibleTransformException e2) {
						// in this unlikely case maybe we have no width/height?
						e2.printStackTrace();
					}
				}
			}
		};

		{
			addMouseMotionListener(mouseListener);
			addMouseListener(mouseListener);
		}

		@Override
		protected void paintComponent(Graphics g) {
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());

			VectorImage img = getVectorImage();
			if (img == null)
				return;
			Rectangle2D bounds = img.getBounds();
			if (bounds == null)
				return;

			Dimension displayBounds = Dimension2D.scaleProportionally(
					new Dimension((int) bounds.getWidth(),
							(int) bounds.getHeight()),
					new Dimension(getWidth() - 6, getHeight() - 6));

			Rectangle paintBounds = new Rectangle(
					getWidth() / 2 - displayBounds.width / 2,
					getHeight() / 2 - displayBounds.height / 2,
					displayBounds.width, displayBounds.height);

			((Graphics2D) g).setStroke(new BasicStroke(1));
			g.setColor(Color.darkGray);
			g.drawRect(paintBounds.x - 1, paintBounds.y - 1,
					paintBounds.width + 1, paintBounds.height + 1);

			tx = TransformUtils.createAffineTransform(bounds, paintBounds);
			Graphics2D g2 = (Graphics2D) g.create();
			g2.transform(tx);

			// hopefully you won't see this, but just in case:
			g2.setColor(Color.gray);
			g2.fill(bounds);

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

			Operation selectedOperation = operationList.getSelectedValue();
			for (Operation operation : img.getOperations()) {
				operation = filterOperation(operation);
				operation.paint(g2);
				if (operation == selectedOperation)
					break;
			}
			g2.dispose();

			g2 = (Graphics2D) g.create();
			Number highlightOpacity = (Number) getClientProperty(
					PROPERTY_HIGHLIGHT_OPACITY);
			if (selectedOperation != null && highlightOpacity != null) {
				Shape outline = selectedOperation.getBounds();
				if (outline != null) {
					outline = tx.createTransformedShape(outline);
					g2.setStroke(new BasicStroke(2));
					g2.setColor(new Color(0, 0, 0,
							(int) (255 * highlightOpacity.floatValue())));
					g2.draw(outline);

					g2.setColor(new Color(255, 255, 255,
							(int) (255 * highlightOpacity.floatValue())));
					g2.fill(outline);
				}
			}

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

		/**
		 * Possibly modify Operations before we render them.
		 * <p>
		 * On Mac, for example, most Aqua L&F images render poorly when scaled
		 * down. This switches those images to BufferedImages, which render
		 * better.
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
	};

	public VectorImageInspector() {
		this(new VectorImage());
	}

	public VectorImageInspector(VectorImage vi) {
		addPropertyChangeListener(PROPERTY_VECTOR_IMAGE,
				vectorGraphicsListener);
		setVectorImage(vi);
		setLayout(new GridBagLayout());
		refreshLayout();
		operationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		operationList.getSelectionModel()
				.addListSelectionListener(new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						previewPanel.putClientProperty(
								PROPERTY_HIGHLIGHT_OPACITY, 1);
						AnimationManager.setTargetProperty(previewPanel,
								PROPERTY_HIGHLIGHT_OPACITY, 0, .25f);
						previewPanel.repaint();
					}

				});
		operationList.setCellRenderer(new LabelCellRenderer<Operation>() {

			@Override
			protected void formatLabel(Operation value) {
				String str = value.getClass().getSimpleName();
				if (str.endsWith("Operation"))
					str = str.substring(0, str.length() - "Operation".length());
				getLabel().setText(str);

				if (mouseLoc != null && value != null
						&& value.getBounds() != null
						&& value.getBounds().contains(mouseLoc)) {
					getLabel().setBorder(new LineBorder(Color.red));

				} else {
					getLabel().setBorder(new LineBorder(Color.white));
				}
			}

		});

		operationList.putClientProperty(QPopup.PROPERTY_CALLOUT_TYPE,
				CalloutType.LEFT_CENTER);

		new ContextualMenuHelper(operationList) {

			@Override
			protected void showPopup(Component c, int x, int y) {
				Operation selectedOp = operationList.getSelectedValue();
				BooleanProperty popupRequested = getPopupRequested(
						operationList);
				popupRequested.setValue(true);
				if (selectedOp != null) {

					String stacktrace = (String) selectedOp
							.getProperty(PROPERTY_STACK_TRACE);
					if (stacktrace != null) {
						JPanel contents = createPopoverContents(stacktrace);
						JPopover<JPanel> popover = new JPopover<>(operationList,
								contents, true);
						popover.setVisibility(
								new ContextualPopoverVisibility());
						popover.setTarget(new ListCellPopupTarget(operationList,
								operationList.getSelectedIndex()));
					}
				}
			}

			private JPanel createPopoverContents(final String stacktrace) {
				int i = -1;
				int numberOfLines = 10;
				for (int a = 0; a < numberOfLines; a++) {
					int newIndex = stacktrace.indexOf("\n", i);
					if (newIndex == -1) {
						i = -1;
						break;
					} else {
						i = newIndex + 1;
					}
				}
				String abbreviatedStacktrace;
				if (i != -1) {
					abbreviatedStacktrace = stacktrace.substring(0, i) + "...";
				} else {
					abbreviatedStacktrace = stacktrace;
				}

				JTextArea textArea = new JTextArea(abbreviatedStacktrace);
				textArea.setEditable(false);
				textArea.setOpaque(false);
				JButton copyButton = new JButton("Copy Stack Trace");

				JPanel panel = new JPanel(new GridBagLayout());

				GridBagConstraints c = new GridBagConstraints();
				c.gridx = 0;
				c.gridy = 0;
				c.weightx = 1;
				c.weighty = 1;
				c.insets = new Insets(3, 3, 3, 3);
				c.fill = GridBagConstraints.BOTH;
				panel.add(textArea, c);
				c.gridy++;
				c.fill = GridBagConstraints.NONE;
				panel.add(copyButton, c);

				copyButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						Transferable contents = new StringSelection(stacktrace);
						Toolkit.getDefaultToolkit().getSystemClipboard()
								.setContents(contents, null);
						BooleanProperty popupRequested = getPopupRequested(
								operationList);
						popupRequested.setValue(false);
					}

				});

				return panel;
			}

		};
	}

	BooleanProperty getPopupRequested(JComponent jc) {
		BooleanProperty b = (BooleanProperty) jc
				.getClientProperty(PROPERTY_POPUP_REQUESTED);
		if (b == null) {
			b = new BooleanProperty(PROPERTY_POPUP_REQUESTED, false);
			jc.putClientProperty(PROPERTY_POPUP_REQUESTED, b);
		}
		return b;
	}

	public VectorImage getVectorImage() {
		return (VectorImage) getClientProperty(PROPERTY_VECTOR_IMAGE);
	}

	public void setVectorImage(VectorImage img) {
		Objects.requireNonNull(img);
		putClientProperty(PROPERTY_VECTOR_IMAGE, img);
	}

	protected void refreshLayout() {
		operationBrowser.removeAll();
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		operationBrowser.add(operationList, c);

		removeAll();

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		JScrollPane browserScrollPane = new JScrollPane(operationBrowser,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		browserScrollPane.setPreferredSize(new Dimension(100, 100));
		browserScrollPane.setMinimumSize(new Dimension(100, 100));
		add(browserScrollPane, c);
		c.gridx++;
		c.weightx = 1;
		c.weighty = 0;
		add(previewPanel, c);
	}
}
