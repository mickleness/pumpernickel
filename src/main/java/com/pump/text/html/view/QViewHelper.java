package com.pump.text.html.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.List;

import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.html.CSS;
import javax.swing.text.html.StyleSheet.BoxPainter;

import com.pump.graphics.Graphics2DContext;
import com.pump.graphics.vector.Operation;
import com.pump.graphics.vector.VectorGraphics2D;
import com.pump.graphics.vector.VectorImage;
import com.pump.image.shadow.ShadowAttributes;
import com.pump.text.html.style.CssBackgroundImagePropertyHandler;
import com.pump.text.html.style.CssColorPropertyHandler;
import com.pump.text.html.style.CssTextShadowPropertyHandler;
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
			return CssTextShadowPropertyHandler.PROPERTY_TEXT_SHADOW;
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

	protected final View view;

	public QViewHelper(View view) {
		this.view = view;
	}

	private Object getAttribute(Object attrKey) {
		Object value = view.getElement().getAttributes().getAttribute(attrKey);

		if (value == null)
			value = view.getAttributes().getAttribute(attrKey);

		if (attrKey == CSS.Attribute.BACKGROUND_IMAGE && value != null) {
			String str = value.toString();
			return new CssBackgroundImagePropertyHandler().parse(str);
		} else if (attrKey == CSS.Attribute.BACKGROUND_COLOR && value != null) {
			String str = value.toString();
			return new CssColorPropertyHandler(CSS.Attribute.BACKGROUND_COLOR)
					.parse(str);
		}

		return value;
	}

	/**
	 * Return a Graphics2D to paint a View to that embeds special
	 * RenderingHints.
	 */
	public Graphics2D createGraphics(Graphics2D g) {

		Graphics2D returnValue = (Graphics2D) g.create();

		returnValue.setRenderingHint(HINT_KEY_ELEMENT, view.getElement());

		Object shadowValue = getAttribute(
				CssTextShadowPropertyHandler.PROPERTY_TEXT_SHADOW);
		if (shadowValue != null)
			returnValue.setRenderingHint(HINT_KEY_TEXT_SHADOW, shadowValue);

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
	public Graphics2D createGraphicsWithoutBoxPainter(Graphics2D g, Rectangle r,
			BoxPainter boxPainter) {
		VectorImage boxPainterRendering = new VectorImage();
		VectorGraphics2D boxPainterG = boxPainterRendering.createGraphics(r);
		boxPainter.paint(boxPainterG, r.x, r.y, r.width, r.height, view);

		ObservableList<Operation> operations = new ObservableList<>();
		operations.addListListener(new DropOperationsAndPassThrough(g,
				boxPainterRendering.getOperations()), false);
		return new VectorGraphics2D(new Graphics2DContext(g), operations);
	}

	public void paintBackground(Graphics2D g, Rectangle r) {
		Color color = (Color) getAttribute(CSS.Attribute.BACKGROUND_COLOR);
		if (color != null) {
			g.setColor(color);
			g.fillRect(r.x, r.y, r.width, r.height);
		}

		CssBackgroundImagePropertyHandler.BackgroundImage bkgndImg = (CssBackgroundImagePropertyHandler.BackgroundImage) getAttribute(
				CSS.Attribute.BACKGROUND_IMAGE);
		if (bkgndImg != null) {
			bkgndImg.paintRectangle(g, r.x, r.y, r.width, r.height);
		}
	}
}
