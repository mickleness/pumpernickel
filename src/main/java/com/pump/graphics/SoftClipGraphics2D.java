package com.pump.graphics;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.util.Objects;

import com.pump.graphics.vector.Operation;
import com.pump.graphics.vector.VectorGraphics2D;

/**
 * This intercepts and delegates all rendering operations to optionally apply
 * the {@link #KEY_SOFT_CLIP} rendering hint.
 * <p>
 * The default Graphics2D's clipping is aliased. The KEY_SOFT_CLIP rendering
 * hint is antialiased. So for example: this property lets you render an image
 * through a a circular soft clip and have an antialiased border.
 */
public class SoftClipGraphics2D extends VectorGraphics2D {

	/**
	 * This rendering hint should map to a Shape that is applied as a "soft
	 * clipping" for all incoming rendering operations. This lets you achieve an
	 * antialiased clipping. This is applied independently of the Graphics2D's
	 * clipping. (So the soft clipping alters what we paint to the underlying
	 * Graphics2D, and the underlying Graphics2D's clipping still applies.)
	 */
	public static final RenderingHints.Key KEY_SOFT_CLIP = new RenderingHints.Key(
			-930183) {

		@Override
		public boolean isCompatibleValue(Object val) {
			return val instanceof Shape;
		}
	};

	Graphics2D delegate;

	public SoftClipGraphics2D(Graphics2D delegate) {
		Objects.requireNonNull(delegate);
		this.delegate = delegate;
	}

	@Override
	protected void addOperation(Operation operation) {
		Shape softClipShape = (Shape) getRenderingHint(KEY_SOFT_CLIP);
		if (softClipShape != null) {
			for (Operation newOp : operation
					.toSoftClipOperation(softClipShape)) {
				newOp.paint(delegate);
			}
			return;
		}
		operation.paint(delegate);
	}

	@Override
	public SoftClipGraphics2D create() {
		return new SoftClipGraphics2D((Graphics2D) delegate.create());
	}
}
