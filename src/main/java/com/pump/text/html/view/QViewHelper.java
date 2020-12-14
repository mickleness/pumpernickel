package com.pump.text.html.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.Objects;

import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.html.CSS;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.html.StyleSheet.BoxPainter;

import com.pump.geom.ShapeBounds;
import com.pump.graphics.Graphics2DContext;
import com.pump.graphics.vector.GlyphVectorOperation;
import com.pump.graphics.vector.Operation;
import com.pump.graphics.vector.StringOperation;
import com.pump.graphics.vector.VectorGraphics2D;
import com.pump.graphics.vector.VectorImage;
import com.pump.image.shadow.ShadowAttributes;
import com.pump.text.html.css.CssColorParser;
import com.pump.text.html.css.CssLength;
import com.pump.text.html.css.CssOverflowParser;
import com.pump.text.html.css.CssOverflowValue;
import com.pump.text.html.css.CssTextShadowParser;
import com.pump.text.html.css.background.CssBackgroundClipValue;
import com.pump.text.html.css.image.CssImageParser;
import com.pump.text.html.css.image.CssImageValue;
import com.pump.util.list.AddElementsEvent;
import com.pump.util.list.ListAdapter;
import com.pump.util.list.ObservableList;

/**
 * This helper class may be used by multiple View subclasses to help manage HTML
 * enhancements.
 * <p>
 * Some properties (like background images) this object renders directly. Some
 * properties (like text shadows) this object embeds as RenderingHints for the
 * QHtmlBlockView to address later.
 */
public class QViewHelper {
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

	public static java.lang.Float getPredefinedSize(View view, int axis) {
		// TODO: percents require incorporating parent size

		// TODO: consider other unit sizes

		if (axis == View.X_AXIS) {
			Object z = (Object) view.getAttributes()
					.getAttribute(CSS.Attribute.WIDTH);
			if (z != null) {
				CssLength l = new CssLength(z.toString());
				return l.getValue();
			}
			return null;
		} else if (axis == View.Y_AXIS) {
			Object z = (Object) view.getAttributes()
					.getAttribute(CSS.Attribute.HEIGHT);
			if (z != null) {
				CssLength l = new CssLength(z.toString());
				return l.getValue();
			}
			return null;
		}
		throw new IllegalArgumentException("unrecognized axis: " + axis);
	}

	protected final View view;
	protected final LegacyCssView legacyView;
	protected final StyleSheet styleSheet;

	public QViewHelper(View view, LegacyCssView legacyView,
			StyleSheet styleSheet) {
		Objects.requireNonNull(view);
		Objects.requireNonNull(legacyView);
		Objects.requireNonNull(styleSheet);
		this.view = view;
		this.legacyView = legacyView;
		this.styleSheet = styleSheet;
	}

	public Object getAttribute(Object attrKey) {
		// get value from tag declaration
		Object value = view.getElement().getAttributes().getAttribute(attrKey);

		if (value == null) {
			// get value from css rule
			AttributeSet attrs = view.getAttributes();
			value = attrs == null ? null : attrs.getAttribute(attrKey);
		}

		if (attrKey == CSS.Attribute.BACKGROUND_IMAGE && value != null) {
			String str = value.toString();
			return new CssImageParser().parse(str);
		} else if (attrKey == CSS.Attribute.BACKGROUND_COLOR && value != null) {
			String str = value.toString();
			return new CssColorParser(CSS.Attribute.BACKGROUND_COLOR)
					.parse(str);
		}

		return value;
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
				CssTextShadowParser.PROPERTY_TEXT_SHADOW);
		if (shadowValue != null)
			returnValue.setRenderingHint(HINT_KEY_TEXT_SHADOW, shadowValue);

		CssOverflowValue overflow = (CssOverflowValue) getAttribute(
				CssOverflowParser.PROPERTY_OVERFLOW);
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
				CssBackgroundClipValue.PROPERTY_BACKGROUND_CLIP);
		if (clipValue != null) {
			CssBackgroundClipValue.Mode m = clipValue.getMode();
			if (m == CssBackgroundClipValue.Mode.TEXT) {
				Rectangle r = ShapeBounds.getBounds(allocation).getBounds();
				Shape textShape = getTextShape(r);
				returnValue.setRenderingHint(HINT_KEY_SOFT_CLIP, textShape);
			}
		}

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

	private void paintBackground(Graphics2D g, Rectangle r) {
		Color bgColor = styleSheet
				.getBackground(styleSheet.getViewAttributes(view));

		CssBackgroundClipValue clipValue = (CssBackgroundClipValue) getAttribute(
				CssBackgroundClipValue.PROPERTY_BACKGROUND_CLIP);

		Graphics2D g2 = (Graphics2D) g.create();

		if (clipValue != null) {
			CssBackgroundClipValue.Mode m = clipValue.getMode();
			if (m == CssBackgroundClipValue.Mode.BORDER_BOX) {
				// do nothing, this is the the biggest/normal option
			} else if (m == CssBackgroundClipValue.Mode.PADDING_BOX) {
				Insets i = getBorderInsets(r.width, r.height);
				Rectangle clipRect = new Rectangle(r.x + i.left, r.y + i.top,
						r.width - i.left - i.right,
						r.height - i.top - i.bottom);
				g2.clipRect(clipRect.x, clipRect.y, clipRect.width,
						clipRect.height);
			} else if (m == CssBackgroundClipValue.Mode.CONTENT_BOX) {
				Insets i1 = getBorderInsets(r.width, r.height);
				Insets i2 = getPaddingInsets(r.width, r.height);
				Rectangle clipRect = new Rectangle(r.x + i1.left + i2.left,
						r.y + i1.top + i2.top,
						r.width - i1.left - i1.right - i2.left - i2.right,
						r.height - i1.top - i1.bottom - i2.top - i2.bottom);
				g2.clipRect(clipRect.x, clipRect.y, clipRect.width,
						clipRect.height);
			} else if (m == CssBackgroundClipValue.Mode.TEXT) {
				// do nothing, the SoftClipGraphics2D.KEY_SOFT_CLIP should
				// already be set up in createGraphics(..)
			}
		}

		if (bgColor != null) {
			g2.setColor(bgColor);
			g2.fillRect(r.x, r.y, r.width, r.height);
		}

		List<CssImageValue> bkgndImgs = (List<CssImageValue>) getAttribute(
				CSS.Attribute.BACKGROUND_IMAGE);
		if (bkgndImgs != null) {
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

	private Insets getPaddingInsets(int width, int height) {
		int top = getLength(CSS.Attribute.PADDING_TOP, height);
		int left = getLength(CSS.Attribute.PADDING_LEFT, width);
		int bottom = getLength(CSS.Attribute.PADDING_BOTTOM, height);
		int right = getLength(CSS.Attribute.PADDING_RIGHT, width);

		return new Insets(top, left, bottom, right);
	}

	private int getLength(Object attributeKey, int range) {
		Object attr = view.getAttributes().getAttribute(attributeKey);
		if (attr == null)
			return 0;

		CssLength l = new CssLength(attr.toString().toString());
		if (l.getUnit().equals("%")) {
			return (int) (l.getValue() * range / 100);
		} else if (l.getUnit().equals("px")) {
			return (int) (l.getValue() + .5f);
		}
		throw new IllegalArgumentException(
				"Unsupported unit in \"" + attr.toString() + "\"");
	}

	private Insets getBorderInsets(int width, int height) {
		int top = getLength(CSS.Attribute.BORDER_TOP_WIDTH, height);
		int left = getLength(CSS.Attribute.BORDER_LEFT_WIDTH, width);
		int bottom = getLength(CSS.Attribute.BORDER_BOTTOM_WIDTH, height);
		int right = getLength(CSS.Attribute.BORDER_RIGHT_WIDTH, width);

		return new Insets(top, left, bottom, right);
	}

	public View getView() {
		return view;
	}

	/**
	 * 
	 * @param g
	 * @param allocation
	 * @param boxPainter
	 *            this should be non-null if this object is responsible for
	 *            paint the background/border (and stripping out the legacy
	 *            background/border)
	 * @param isBody
	 */
	public void paint(Graphics2D g, Shape allocation, BoxPainter boxPainter,
			boolean isBody) {
		Rectangle r = ShapeBounds.getBounds(allocation).getBounds();
		Graphics2D g2 = createGraphics(g, allocation, isBody);
		if (boxPainter != null) {
			paintBackground(g2, r);
			g2 = createGraphicsWithoutBoxPainter(g2, r, boxPainter);
		}
		legacyView.paintLegacyCss2(g2, allocation);
		g2.dispose();
	}
}
