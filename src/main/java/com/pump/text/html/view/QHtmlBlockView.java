package com.pump.text.html.view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.text.Element;
import javax.swing.text.html.BlockView;

import com.pump.graphics.Graphics2DContext;
import com.pump.graphics.vector.GlyphVectorOperation;
import com.pump.graphics.vector.Operation;
import com.pump.graphics.vector.StringOperation;
import com.pump.graphics.vector.VectorImage;
import com.pump.image.shadow.DoubleBoxShadowRenderer;
import com.pump.image.shadow.ShadowAttributes;

/**
 * This BlockView reinterprets all the rendering instructions used to render
 * HTML. For example: this intercepts calls to paint text and, when the
 * appropriate text-shadow tag is used, it paints a shadow below the text.
 */
public class QHtmlBlockView extends BlockView {

	public QHtmlBlockView(Element elem, int axis) {
		super(elem, axis);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void paint(Graphics g, Shape allocation) {
		VectorImage vi = new VectorImage();

		Graphics2D vig = vi.createGraphics();
		// if clipping isn't defined some classes throw NPE's
		vig.clipRect(0, 0, Integer.MAX_VALUE / 2, Integer.MAX_VALUE / 2);
		super.paint(vig, allocation);
		vig.dispose();

		// group Operations by their shared parent

		List<List<Operation>> operationRuns = new ArrayList<>();
		List<Operation> currentRun = new LinkedList<>();
		Object currentParent = null;

		for (Operation op : vi.getOperations()) {
			Graphics2DContext ctxt = op.getContext();
			Element e = (Element) ctxt
					.getRenderingHint(QViewHelper.HINT_KEY_ELEMENT);
			Element parent = e == null ? null : e.getParentElement();
			if (parent != currentParent) {
				if (!currentRun.isEmpty()) {
					operationRuns.add(currentRun);
					currentRun = new LinkedList<>();
				}
				currentParent = parent;
			}
			if (op.getBounds() != null)
				currentRun.add(op);
		}
		if (!currentRun.isEmpty())
			operationRuns.add(currentRun);

		// We want to paint all the shadows belonging to the same run
		// of Operations first, then paint the run of Operations on top.
		// This becomes important when the user has made a text selection: we
		// want the shadows to paint together (in this case below) the selection
		// highlight. Otherwise some shadows may bleed out on top of other
		// selection highlights (in the case of a multi-line selection), and it
		// looks really weird.

		for (List<Operation> opRun : operationRuns) {
			for (Operation op : opRun) {
				Graphics2DContext ctxt = op.getContext();
				Rectangle r = op.getBounds().getBounds();
				boolean isText = op instanceof GlyphVectorOperation
						|| op instanceof StringOperation;
				if (r != null && r.width > 0 && r.height > 0 && isText) {
					List<ShadowAttributes> attrs = (List<ShadowAttributes>) ctxt
							.getRenderingHint(QViewHelper.HINT_KEY_TEXT_SHADOW);
					if (attrs != null && !attrs.isEmpty()) {
						BufferedImage bi = new BufferedImage(r.width, r.height,
								BufferedImage.TYPE_INT_ARGB);
						Graphics2D big = bi.createGraphics();
						big.translate(-r.x, -r.y);
						op.paint(big);
						big.dispose();
						for (ShadowAttributes attr : attrs) {
							BufferedImage shadow = new DoubleBoxShadowRenderer()
									.createShadow(bi,
											attr.getShadowKernelRadius(),
											attr.getShadowColor());
							int dx = (shadow.getWidth() - bi.getWidth()) / 2;
							int dy = (shadow.getHeight() - bi.getHeight()) / 2;
							g.drawImage(shadow,
									(int) (r.x + attr.getShadowXOffset() - dx
											+ .5),
									(int) (r.y + attr.getShadowYOffset() - dy
											+ .5),
									null);
						}
					}
				}
			}
			for (Operation op : opRun) {
				op.paint((Graphics2D) g);
			}
		}
	}
}
