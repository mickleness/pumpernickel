package com.pump.text.html.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.plaf.UIResource;
import javax.swing.text.Element;
import javax.swing.text.html.BlockView;

import com.pump.graphics.Graphics2DContext;
import com.pump.graphics.vector.FillOperation;
import com.pump.graphics.vector.GlyphVectorOperation;
import com.pump.graphics.vector.ImageOperation;
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
	public void paint(Graphics destG, Shape allocation) {
		List<Operation> inOperations = getOperations(allocation);
		List<List<Operation>> operationsByParent = getOperationsGroupedByParent(
				inOperations);

		// We want to paint all the shadows belonging to the same run
		// of Operations first, then paint the run of Operations on top.
		// This becomes important when the user has made a text selection: we
		// want the shadows to paint together (in this case below) the selection
		// highlight. Otherwise some shadows may bleed out on top of other
		// selection highlights (in the case of a multi-line selection), and it
		// looks really weird.

		for (List<Operation> opRun : operationsByParent) {

			// step 1: paint the selection highlights

			// Once a highlight is painted: remove it and never
			// worry about it again.

			Iterator<Operation> opIter = opRun.iterator();
			while (opIter.hasNext()) {
				Operation op = opIter.next();
				if (isHighlighter(op)) {
					Graphics2D g2 = (Graphics2D) destG.create();
					op.paint(g2);
					g2.dispose();
					opIter.remove();
				}
			}

			// step 2: paint shadows:

			opIter = opRun.iterator();
			while (opIter.hasNext()) {
				Operation op = opIter.next();
				if (isText(op)) {
					Graphics2DContext ctxt = op.getContext();
					List<ShadowAttributes> attrs = (List<ShadowAttributes>) ctxt
							.getRenderingHint(QViewHelper.HINT_KEY_TEXT_SHADOW);
					if (attrs != null && !attrs.isEmpty()) {
						paintShadow((Graphics2D) destG, op, attrs);
					}

					// if you have a transparent foreground: ignore
					if (isEmpty(ctxt.getPaint()))
						opIter.remove();
				}
			}

			// step 3: paint everything else

			for (Operation op : opRun) {
				Graphics2D g2 = (Graphics2D) destG.create();
				paint(g2, op, (Shape) op
						.getRenderingHint(QViewHelper.HINT_KEY_SOFT_CLIP));
				g2.dispose();
			}
		}
	}

	/**
	 * @param softClip an optional soft clip shape to apply
	 */
	private void paint(Graphics2D g, Operation op, Shape softClip) {
		if (softClip == null || isHighlighter(op)) {
			op.paint(g);
		} else {
			for (Operation subOp : op.toSoftClipOperation(softClip)) {
				subOp.paint(g);
			}
		}
	}

	private boolean isEmpty(Paint paint) {
		return paint instanceof Color && ((Color) paint).getAlpha() == 0;
	}

	private void paintShadow(Graphics2D destG, Operation textOp,
			List<ShadowAttributes> attrs) {
		Rectangle2D r2 = textOp.getBounds();
		Rectangle r = r2 == null ? null : r2.getBounds();
		if (r == null || r.width <= 0 || r.height <= 0) {
			return;
		}

		BufferedImage bi = new BufferedImage(r.width, r.height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D big = bi.createGraphics();
		big.translate(-r.x, -r.y);
		Graphics2DContext context = textOp.getContext();
		Paint origPaint = context.getPaint();

		// we should always render text's shadow as if the text is opaque.
		// (see unit test "testTextShadowWithTransparentText")

		context.setColor(Color.black);
		textOp.setContext(context);
		try {
			textOp.paint(big);
		} finally {
			context.setPaint(origPaint);
			textOp.setContext(context);
		}

		big.dispose();

		for (ShadowAttributes attr : attrs) {
			BufferedImage shadow = new DoubleBoxShadowRenderer().createShadow(
					bi, attr.getShadowKernelRadius(), attr.getShadowColor());
			int dx = (shadow.getWidth() - bi.getWidth()) / 2;
			int dy = (shadow.getHeight() - bi.getHeight()) / 2;

			ImageOperation imageOp = new ImageOperation(new Graphics2DContext(),
					shadow, (int) (r.x + attr.getShadowXOffset() - dx + .5),
					(int) (r.y + attr.getShadowYOffset() - dy + .5));
			paint(destG, imageOp, (Shape) textOp
					.getRenderingHint(QViewHelper.HINT_KEY_SOFT_CLIP));
		}
	}

	private boolean isText(Operation op) {
		return op instanceof GlyphVectorOperation
				|| op instanceof StringOperation;
	}

	private List<List<Operation>> getOperationsGroupedByParent(
			List<Operation> operations) {
		List<List<Operation>> returnValue = new ArrayList<>();
		List<Operation> currentRun = new LinkedList<>();
		Object currentParent = null;

		for (Operation op : operations) {
			Graphics2DContext ctxt = op.getContext();
			Element e = (Element) ctxt
					.getRenderingHint(QViewHelper.HINT_KEY_ELEMENT);
			Element parent = e == null ? null : e.getParentElement();
			if (parent != currentParent) {
				if (!currentRun.isEmpty()) {
					returnValue.add(currentRun);
					currentRun = new LinkedList<>();
				}
				currentParent = parent;
			}
			if (op.getBounds() != null)
				currentRun.add(op);
		}
		if (!currentRun.isEmpty())
			returnValue.add(currentRun);
		return returnValue;
	}

	private List<Operation> getOperations(Shape allocation) {
		VectorImage vi = new VectorImage();
		Graphics2D vig = vi.createGraphics();
		// if clipping isn't defined some classes throw NPE's
		vig.clipRect(0, 0, Integer.MAX_VALUE / 2, Integer.MAX_VALUE / 2);
		super.paint(vig, allocation);
		vig.dispose();
		return vi.getOperations();
	}

	/**
	 * Return true if this Operation relates to painting the selection highlight
	 */
	private boolean isHighlighter(Operation op) {
		return op instanceof FillOperation
				&& op.getContext().getPaint() instanceof UIResource;
	}
}
