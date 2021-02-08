package com.pump.text.html.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.List;
import java.util.Objects;

import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.html.CSS;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.html.StyleSheet.BoxPainter;

import com.pump.awt.Insets2D;
import com.pump.geom.ShapeBounds;
import com.pump.graphics.Graphics2DContext;
import com.pump.graphics.vector.GlyphVectorOperation;
import com.pump.graphics.vector.Operation;
import com.pump.graphics.vector.StringOperation;
import com.pump.graphics.vector.VectorGraphics2D;
import com.pump.graphics.vector.VectorImage;
import com.pump.image.shadow.ShadowAttributes;
import com.pump.text.html.css.CssLength;
import com.pump.text.html.css.CssOverflowParser;
import com.pump.text.html.css.CssOverflowValue;
import com.pump.text.html.css.CssTextShadowParser;
import com.pump.text.html.css.background.CssBackgroundClipValue;
import com.pump.text.html.css.border.CssOutlineOffsetParser;
import com.pump.text.html.css.image.CssImageValue;
import com.pump.util.list.AddElementsEvent;
import com.pump.util.list.ListAdapter;
import com.pump.util.list.ObservableList;

/**
 * This helper class may be used by multiple View subclasses to help render HTML
 * enhancements.
 * <p>
 * Some properties (like background images) this object renders directly. Some
 * properties (like text shadows) this object embeds as RenderingHints for the
 * QHtmlBlockView to address later.
 */
public class QViewRenderer extends QViewHelper {

	/**
	 * 
	 * @param g
	 * @param allocation
	 * @param boxPainter
	 *            this should be non-null if this object is responsible for
	 *            painting the background/border (and stripping out the legacy
	 *            background/border)
	 * @param isBody
	 *            if true then this is an attempt to paint the &lt;body&gt; tag.
	 *            In this case we apply special clipping regardless of the
	 *            "overflow" attribute.
	 */
	public static void paint(Graphics2D g, Shape allocation, View view,
			LegacyCssView legacyView, StyleSheet styleSheet,
			BoxPainter boxPainter, boolean isBody) {
		QViewRenderer helper = new QViewRenderer(view, styleSheet, legacyView);
		Graphics2D g2 = helper.createGraphics(g, allocation, isBody);
		helper.paintBelowContent(g2, allocation, helper, boxPainter);

		Graphics2D legacyG;
		if (boxPainter != null) {
			// this strips away the legacy rendering:
			Rectangle r = ShapeBounds.getBounds(allocation).getBounds();
			legacyG = helper.createGraphicsWithoutBoxPainter(g2, r, boxPainter);
		} else {
			legacyG = g2;
		}

		legacyView.paintLegacyCss2(legacyG, allocation);
		legacyG.dispose();
		g2.dispose();
	}

	/**
	 * This paints everything below the "content" in the box model.
	 * <p>
	 * The "box model" used to render HTML elements consists of these concentric
	 * rectangles: margin, border, padding, content. So this method paints
	 * everything except the content. (Normally I'd call this something like
	 * "paintBackground", but the word "background" has special meaning here.)
	 * 
	 * @param g
	 * @param allocation
	 * @param helper
	 * @param boxPainter
	 * @return
	 */
	private void paintBelowContent(Graphics2D g, Shape allocation,
			QViewRenderer helper, BoxPainter boxPainter) {
		Rectangle r = ShapeBounds.getBounds(allocation).getBounds();

		float topMargin = getLength(CSS.Attribute.MARGIN_TOP, r.height);
		float bottomMargin = getLength(CSS.Attribute.MARGIN_BOTTOM, r.height);
		float leftMargin = getLength(CSS.Attribute.MARGIN_LEFT, r.width);
		float rightMargin = getLength(CSS.Attribute.MARGIN_RIGHT, r.width);

		// calculate where the border/outline paints:
		Rectangle2D borderR = new Rectangle2D.Float(r.x, r.y, r.width,
				r.height);
		borderR.setRect(borderR.getX() + leftMargin, borderR.getY() + topMargin,
				borderR.getWidth() - leftMargin - rightMargin,
				borderR.getHeight() - topMargin - bottomMargin);
		BorderRenderingConfiguration borderConfig = BorderRenderingConfiguration
				.forBorder(helper);
		BorderRendering borderRendering = new BorderRendering(borderConfig,
				borderR);

		// The legacy BoxPainter paints background color, image, then
		// Border in that order.

		// this handles the background color & image:
		helper.paintBackground(g, borderRendering.shape, boxPainter);

		// this addresses the "outline" attribute -- not previously supported in
		// Swing
		BorderRenderingConfiguration outlineConfig = BorderRenderingConfiguration
				.forOutline(helper);
		if (outlineConfig.bottomStyle != null) {
			// outlines all have a uniform style, so we only check one attribute

			CssLength offset = (CssLength) helper.getAttribute(
					CssOutlineOffsetParser.PROPERTY_OUTLINE_OFFSET, false);
			if (offset == null)
				offset = new CssLength("", 0, "px");
			Rectangle2D outlineR = new Rectangle2D.Double(
					borderR.getX() - outlineConfig.leftWidth.getValue()
							- offset.getValue(),
					borderR.getY() - outlineConfig.topWidth.getValue()
							- offset.getValue(),
					borderR.getWidth() + outlineConfig.leftWidth.getValue()
							+ outlineConfig.rightWidth.getValue()
							+ 2 * offset.getValue(),
					borderR.getHeight() + outlineConfig.topWidth.getValue()
							+ outlineConfig.bottomWidth.getValue()
							+ 2 * offset.getValue());
			BorderRendering outlineRendering = new BorderRendering(
					outlineConfig, outlineR);
			outlineRendering.paint(g);
		}

		// paint the actual "border"
		borderRendering.paint(g);
	}

	/**
	 * This RenderingHint.Key resolves to a List of ShadowAttributes (or null)
	 */
	public static final RenderingHints.Key HINT_KEY_TEXT_SHADOW = new RenderingHints.Key(
			592393) {

		@Override
		public boolean isCompatibleValue(Object val) {
			if (val == null)
				return true;

			if (!(val instanceof java.util.List))
				return false;

			for (Object e : ((List) val)) {
				if (!(e instanceof ShadowAttributes))
					return false;
			}

			return true;
		}

		@Override
		public String toString() {
			return CssTextShadowParser.PROPERTY_TEXT_SHADOW;
		}
	};

	/**
	 * This RenderingHint.Key resolves to the javax.swing.text.Element currently
	 * being rendered (or null).
	 */
	public static final RenderingHints.Key HINT_KEY_ELEMENT = new RenderingHints.Key(
			592394) {

		@Override
		public boolean isCompatibleValue(Object val) {
			if (val == null)
				return true;

			return val instanceof Element;
		}

		@Override
		public String toString() {
			return "element";
		}
	};

	/**
	 * This rendering hint should map to a Shape that is applied as a "soft
	 * clipping" for all incoming rendering operations. This lets you achieve an
	 * antialiased clipping. This is applied independently of the Graphics2D's
	 * clipping. (So the soft clipping alters what we paint to the underlying
	 * Graphics2D, and the underlying Graphics2D's clipping still applies.)
	 */
	public static final RenderingHints.Key HINT_KEY_SOFT_CLIP = new RenderingHints.Key(
			-930183) {

		@Override
		public boolean isCompatibleValue(Object val) {
			return val == null || val instanceof Shape;
		}
	};

	private static class DropOperationsAndPassThrough
			extends ListAdapter<Operation> {
		Graphics2D delegate;
		List<Operation> operationsToIgnore;

		public DropOperationsAndPassThrough(Graphics2D delegate,
				List<Operation> operationsToIgnore) {
			this.delegate = delegate;
			this.operationsToIgnore = operationsToIgnore;
		}

		@Override
		public void elementsAdded(AddElementsEvent<Operation> event) {
			for (Operation op : event.getNewElements()) {
				if (operationsToIgnore.isEmpty()) {
					op.paint(delegate);
				} else {
					// TODO: we should be able to call .remove(op), but the
					// Operation#equal method is failing
					operationsToIgnore.remove(0);
				}
			}
		}
	}

	protected final LegacyCssView legacyView;
	protected final StyleSheet styleSheet;

	private QViewRenderer(View view, StyleSheet styleSheet,
			LegacyCssView legacyView) {
		super(view);
		Objects.requireNonNull(legacyView);
		Objects.requireNonNull(styleSheet);
		this.styleSheet = styleSheet;
		this.legacyView = legacyView;
	}

	/**
	 * Return a Graphics2D to paint a View to that embeds special RenderingHints
	 * and may set the clipping.
	 * 
	 * @param isBody
	 *            if true then this is an attempt to paint the &lt;body&gt; tag.
	 *            In this case we apply special clipping regardless of the
	 *            "overflow" attribute.
	 */
	private Graphics2D createGraphics(Graphics2D g, Shape allocation,
			boolean isBody) {

		Graphics2D returnValue = (Graphics2D) g.create();

		returnValue.setRenderingHint(HINT_KEY_ELEMENT, view.getElement());

		Object shadowValue = getAttribute(
				CssTextShadowParser.PROPERTY_TEXT_SHADOW, true);
		if (shadowValue != null)
			returnValue.setRenderingHint(HINT_KEY_TEXT_SHADOW, shadowValue);

		CssOverflowValue overflow = (CssOverflowValue) getAttribute(
				CssOverflowParser.PROPERTY_OVERFLOW, false);
		boolean requestedClipping = overflow != null
				&& overflow.getMode() == CssOverflowValue.Mode.HIDDEN;
		boolean requestedScrollbar = overflow != null
				&& overflow.getMode() == CssOverflowValue.Mode.SCROLL;
		boolean requestedAuto = overflow != null
				&& overflow.getMode() == CssOverflowValue.Mode.AUTO;
		// we don't support internal scrollpanes (yet), but if you wanted
		// a scrollpane it's probably safe to say you didn't want text to
		// spill out of view into other views, so we'll clip it.
		if (isBody || requestedClipping || requestedScrollbar
				|| requestedAuto) {
			returnValue.clip(allocation);
		}

		CssBackgroundClipValue clipValue = (CssBackgroundClipValue) getAttribute(
				CssBackgroundClipValue.PROPERTY_BACKGROUND_CLIP, false);
		if (clipValue != null) {
			CssBackgroundClipValue.Mode m = clipValue.getMode();
			if (m == CssBackgroundClipValue.Mode.TEXT) {
				Rectangle r = ShapeBounds.getBounds(allocation).getBounds();
				Shape textShape = getTextShape(r);
				returnValue.setRenderingHint(HINT_KEY_SOFT_CLIP, textShape);
			}
		}

		returnValue.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		return returnValue;
	}

	/**
	 * Create a Graphics2D that will skip instructions that come from the
	 * BoxPainter. This is a weird/hacky response to not being able to disable
	 * or replace the BoxPainter. (In Swing's default View implementations: the
	 * BoxPainter is non-null and paints immediately before anything else.)
	 * 
	 * @param g
	 *            the Graphics2D we want to eventually paint (certain)
	 *            operations on.
	 * @param r
	 *            the rectangle the BoxPainter will be asked to paint
	 * @param boxPainter
	 *            the BoxPainter we want to ignore
	 * @return a new Graphics2D that skips certain rendering operations
	 */
	private Graphics2D createGraphicsWithoutBoxPainter(Graphics2D g,
			Rectangle r, BoxPainter boxPainter) {
		VectorImage boxPainterRendering = new VectorImage();
		VectorGraphics2D boxPainterG = boxPainterRendering.createGraphics(r);
		boxPainter.paint(boxPainterG, r.x, r.y, r.width, r.height, view);

		ObservableList<Operation> operations = new ObservableList<>();
		operations.addListListener(new DropOperationsAndPassThrough(g,
				boxPainterRendering.getOperations()), false);
		return new VectorGraphics2D(new Graphics2DContext(g), operations);
	}

	private void paintBackground(Graphics2D g, RectangularShape shape,
			BoxPainter boxPainter) {

		CssBackgroundClipValue clipValue = (CssBackgroundClipValue) getAttribute(
				CssBackgroundClipValue.PROPERTY_BACKGROUND_CLIP, false);

		Graphics2D g2 = (Graphics2D) g.create();

		if (clipValue != null) {
			CssBackgroundClipValue.Mode m = clipValue.getMode();
			if (m == CssBackgroundClipValue.Mode.BORDER_BOX) {
				// do nothing, this is the the biggest/normal option
			} else if (m == CssBackgroundClipValue.Mode.PADDING_BOX) {
				Insets2D i = getBorderInsets(shape.getWidth(),
						shape.getHeight());
				Rectangle2D clipRect = new Rectangle2D.Double(
						shape.getX() + i.left, shape.getY() + i.top,
						shape.getWidth() - i.left - i.right,
						shape.getHeight() - i.top - i.bottom);
				g2.clip(clipRect);
			} else if (m == CssBackgroundClipValue.Mode.CONTENT_BOX) {
				Insets2D i1 = getBorderInsets(shape.getWidth(),
						shape.getHeight());
				Insets2D i2 = getPaddingInsets(shape.getWidth(),
						shape.getHeight());
				Rectangle2D clipRect = new Rectangle2D.Double(
						shape.getX() + i1.left + i2.left,
						shape.getY() + i1.top + i2.top,
						shape.getWidth() - i1.left - i1.right - i2.left
								- i2.right,
						shape.getHeight() - i1.top - i1.bottom - i2.top
								- i2.bottom);
				g2.clip(clipRect);
			} else if (m == CssBackgroundClipValue.Mode.TEXT) {
				// do nothing, the SoftClipGraphics2D.KEY_SOFT_CLIP should
				// already be set up in createGraphics(..)
			}
		}

		if (boxPainter != null) {
			// Erg. We shouldn't need to check the existence of a BoxPainter
			// here. But if we remove this check then a unit test fails (because
			// we redundantly paint a background rectangle twice). So this is an
			// awkward hack I'd love to eliminate some day.

			Color bgColor = styleSheet
					.getBackground(styleSheet.getViewAttributes(view));
			if (bgColor != null) {
				g2.setColor(bgColor);
				g2.fill(shape);
			}
		}

		List<CssImageValue> bkgndImgs = (List<CssImageValue>) getAttribute(
				CSS.Attribute.BACKGROUND_IMAGE, false);
		if (bkgndImgs != null) {
			Rectangle r = ShapeBounds.getBounds(shape).getBounds();
			for (int a = bkgndImgs.size() - 1; a >= 0; a--) {
				Graphics2D g3 = (Graphics2D) g2.create();
				bkgndImgs.get(a).paintRectangle(g3, this, a, r.x, r.y, r.width,
						r.height);
				g3.dispose();
			}
		}
		g2.dispose();
	}

	/**
	 * Return a Shape outline of all the text. This is used to isolate when the
	 * "background-clip" property is "text".
	 */
	private Shape getTextShape(Rectangle r) {
		VectorImage vi = new VectorImage();
		Graphics2D g = vi.createGraphics(r);
		legacyView.paintLegacyCss2(g, r);
		g.dispose();
		Path2D returnValue = new Path2D.Float();
		for (Operation op : vi.getOperations()) {
			if (op instanceof StringOperation) {
				StringOperation so = (StringOperation) op;
				Shape outline = so.getShape();
				returnValue.append(outline, false);
			} else if (op instanceof GlyphVectorOperation) {
				GlyphVectorOperation gvo = (GlyphVectorOperation) op;
				returnValue.append(gvo.getOutline(), false);
			}
		}
		return returnValue;
	}

	private Insets2D getPaddingInsets(double width, double height) {
		float top = getLength(CSS.Attribute.PADDING_TOP, height);
		float left = getLength(CSS.Attribute.PADDING_LEFT, width);
		float bottom = getLength(CSS.Attribute.PADDING_BOTTOM, height);
		float right = getLength(CSS.Attribute.PADDING_RIGHT, width);

		return new Insets2D(top, left, bottom, right);
	}

	private Insets2D getBorderInsets(double width, double height) {
		float top = getLength(CSS.Attribute.BORDER_TOP_WIDTH, height);
		float left = getLength(CSS.Attribute.BORDER_LEFT_WIDTH, width);
		float bottom = getLength(CSS.Attribute.BORDER_BOTTOM_WIDTH, height);
		float right = getLength(CSS.Attribute.BORDER_RIGHT_WIDTH, width);

		return new Insets2D(top, left, bottom, right);
	}
}
