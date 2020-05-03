package com.pump.awt.serialization;

import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphJustificationInfo;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Objects;

import com.pump.geom.ShapeBounds;
import com.pump.io.serialization.AbstractSerializationWrapper;
import com.pump.io.serialization.SerializationFilter;
import com.pump.io.serialization.SerializationWrapper;

public class GlyphVectorSerializationWrapper
		extends AbstractSerializationWrapper<GlyphVector> {

	static class GlyphElement implements Serializable {
		private static final long serialVersionUID = 1L;

		protected int glyphCode;

		protected AffineTransform transform;

		protected ShapeSerializationWrapper outline;

		protected Point2DSerializationWrapper position;

		protected ShapeSerializationWrapper logicalBounds;

		/**
		 * May be null
		 */
		protected GlyphJustificationInfoSerializationWrapper justificationInfo;

		protected GlyphMetricsSerializationWrapper glyphMetrics;

		protected ShapeSerializationWrapper visualBounds;

		public GlyphElement(GlyphVector gv, int index) {
			glyphCode = gv.getGlyphCode(index);
			transform = gv.getGlyphTransform(index);
			outline = new ShapeSerializationWrapper(gv.getGlyphOutline(index));
			position = new Point2DSerializationWrapper(
					gv.getGlyphPosition(index));
			logicalBounds = new ShapeSerializationWrapper(
					gv.getGlyphLogicalBounds(index));

			GlyphJustificationInfo gji = gv.getGlyphJustificationInfo(index);
			justificationInfo = gji == null ? null
					: new GlyphJustificationInfoSerializationWrapper(gji);
			glyphMetrics = new GlyphMetricsSerializationWrapper(
					gv.getGlyphMetrics(index));
			visualBounds = new ShapeSerializationWrapper(
					gv.getGlyphVisualBounds(index));
		}

		@Override
		public int hashCode() {
			return position.hashCode() + outline.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof GlyphElement))
				return false;
			GlyphElement other = (GlyphElement) obj;
			if (glyphCode != other.glyphCode)
				return false;
			if (!Objects.equals(transform, other.transform))
				return false;
			if (!Objects.equals(outline, other.outline))
				return false;
			if (!Objects.equals(position, other.position))
				return false;
			if (!Objects.equals(logicalBounds, other.logicalBounds))
				return false;
			if (!Objects.equals(justificationInfo, other.justificationInfo))
				return false;
			if (!Objects.equals(glyphMetrics, other.glyphMetrics))
				return false;
			if (!Objects.equals(visualBounds, other.visualBounds))
				return false;

			return true;
		}
	}

	static class BasicGlyphVector extends GlyphVector {

		protected Font font;
		protected FontRenderContext frc;
		protected GlyphElement[] glyphs;

		public BasicGlyphVector(Font font, FontRenderContext frc,
				GlyphElement[] glyphs) {
			Objects.requireNonNull(font);
			Objects.requireNonNull(frc);
			Objects.requireNonNull(glyphs);
			this.font = font;
			this.frc = frc;
			this.glyphs = glyphs;
		}

		@Override
		public Font getFont() {
			return font;
		}

		@Override
		public FontRenderContext getFontRenderContext() {
			return frc;
		}

		@Override
		public void performDefaultLayout() {
			// intentionally empty
		}

		@Override
		public int getNumGlyphs() {
			return glyphs.length;
		}

		@Override
		public int getGlyphCode(int glyphIndex) {
			return glyphs[glyphIndex].glyphCode;
		}

		@Override
		public Rectangle2D getLogicalBounds() {
			Rectangle2D sum = null;
			for (GlyphElement e : glyphs) {
				Shape s = e.logicalBounds.create();
				Rectangle2D r = ShapeBounds.getBounds(s);
				if (sum == null) {
					sum = new Rectangle2D.Double(r.getX(), r.getY(),
							r.getWidth(), r.getHeight());
				} else {
					sum.add(r);
				}
			}
			if (sum == null)
				sum = new Rectangle2D.Double();
			return sum;
		}

		@Override
		public Rectangle2D getVisualBounds() {
			Rectangle2D sum = null;
			for (GlyphElement e : glyphs) {
				Shape s = e.visualBounds.create();
				Rectangle2D r = ShapeBounds.getBounds(s);
				if (sum == null) {
					sum = new Rectangle2D.Double(r.getX(), r.getY(),
							r.getWidth(), r.getHeight());
				} else {
					sum.add(r);
				}
			}
			if (sum == null)
				sum = new Rectangle2D.Double();
			return sum;
		}

		@Override
		public Shape getOutline() {
			return getOutline(0, 0);
		}

		@Override
		public Shape getOutline(float x, float y) {
			Path2D sum = new Path2D.Float();
			for (GlyphElement e : glyphs) {
				Shape s = e.outline.create();
				sum.append(s, false);
			}
			sum.transform(AffineTransform.getTranslateInstance(x, y));
			return sum;

		}

		@Override
		public Shape getGlyphOutline(int glyphIndex) {
			return glyphs[glyphIndex].outline.create();
		}

		@Override
		public Point2D getGlyphPosition(int glyphIndex) {
			return glyphs[glyphIndex].position.create();
		}

		@Override
		public void setGlyphPosition(int glyphIndex, Point2D newPos) {
			glyphs[glyphIndex].position = new Point2DSerializationWrapper(
					newPos);
		}

		@Override
		public AffineTransform getGlyphTransform(int glyphIndex) {
			return new AffineTransform(glyphs[glyphIndex].transform);
		}

		@Override
		public void setGlyphTransform(int glyphIndex, AffineTransform newTX) {
			glyphs[glyphIndex].transform = new AffineTransform(newTX);
		}

		@Override
		public Shape getGlyphLogicalBounds(int glyphIndex) {
			return glyphs[glyphIndex].logicalBounds.create();
		}

		@Override
		public Shape getGlyphVisualBounds(int glyphIndex) {
			return glyphs[glyphIndex].visualBounds.create();
		}

		@Override
		public GlyphMetrics getGlyphMetrics(int glyphIndex) {
			return glyphs[glyphIndex].glyphMetrics.create();
		}

		@Override
		public GlyphJustificationInfo getGlyphJustificationInfo(
				int glyphIndex) {
			if (glyphs[glyphIndex].justificationInfo == null)
				return null;
			return glyphs[glyphIndex].justificationInfo.create();
		}

		@Override
		public boolean equals(GlyphVector set) {
			if (!(set instanceof GlyphVector))
				return false;
			if (set instanceof BasicGlyphVector)
				return equals((BasicGlyphVector) set);

			GlyphVector gv = (GlyphVector) set;
			GlyphVectorSerializationWrapper wrapper = new GlyphVectorSerializationWrapper(
					gv);
			BasicGlyphVector other = (BasicGlyphVector) wrapper.create();

			return equals(other);
		}

		protected boolean equals(BasicGlyphVector set) {
			if (!frc.equals(set.frc))
				return false;
			if (!font.equals(set.font))
				return false;
			if (glyphs.length != set.glyphs.length)
				return false;
			for (int a = 0; a < glyphs.length; a++) {
				if (!glyphs[a].equals(set.glyphs[a]))
					return false;
			}
			return true;
		}

		@Override
		public int[] getGlyphCodes(int beginGlyphIndex, int numEntries,
				int[] codeReturn) {
			if (codeReturn == null || codeReturn.length < numEntries)
				codeReturn = new int[numEntries];
			for (int a = 0; a < numEntries; a++) {
				codeReturn[a] = getGlyphCode(beginGlyphIndex + a);
			}
			return codeReturn;
		}

		@Override
		public float[] getGlyphPositions(int beginGlyphIndex, int numEntries,
				float[] positionReturn) {
			if (positionReturn == null
					|| positionReturn.length < numEntries * 2)
				positionReturn = new float[numEntries * 2];
			for (int a = 0; a < numEntries; a++) {
				Point2D p = getGlyphPosition(beginGlyphIndex + 1);
				positionReturn[2 * a + 0] = (float) p.getX();
				positionReturn[2 * a + 1] = (float) p.getY();
			}
			return positionReturn;
		}

	}

	private static final long serialVersionUID = 1L;

	public static SerializationFilter FILTER = new SerializationFilter() {
		@Override
		public SerializationWrapper<?> filter(Object object) {
			if (object instanceof GlyphVector) {
				GlyphVector gv = (GlyphVector) object;
				return new GlyphVectorSerializationWrapper(gv);
			}
			return null;
		}
	};

	protected static final String KEY_FONT = "font";
	protected static final String KEY_FONT_RENDER_CONTEXT = "fontRenderContext";
	protected static final String KEY_GLYPHS = "glyphs";

	public GlyphVectorSerializationWrapper(GlyphVector gv) {
		Objects.requireNonNull(gv);
		map.put(KEY_FONT, gv.getFont());
		map.put(KEY_FONT_RENDER_CONTEXT,
				new FontRenderContextSerializationWrapper(
						gv.getFontRenderContext()));

		GlyphElement[] glyphs = new GlyphElement[gv.getNumGlyphs()];
		for (int a = 0; a < glyphs.length; a++) {
			glyphs[a] = new GlyphElement(gv, a);
		}
		map.put(KEY_GLYPHS, glyphs);
	}

	@Override
	public GlyphVector create() {
		Font font = (Font) map.get(KEY_FONT);
		FontRenderContextSerializationWrapper frc = (FontRenderContextSerializationWrapper) map
				.get(KEY_FONT_RENDER_CONTEXT);
		GlyphElement[] glyphs = (GlyphElement[]) map.get(KEY_GLYPHS);

		return new BasicGlyphVector(font, frc.create(), glyphs);
	}
}
