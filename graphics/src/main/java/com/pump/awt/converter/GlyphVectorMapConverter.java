/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.awt.converter;

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
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.pump.data.Key;
import com.pump.data.converter.BeanMapConverter;
import com.pump.data.converter.ConverterUtils;
import com.pump.geom.ShapeBounds;

/**
 * This is a BeanMapConverter for GlyphVectors.
 */
public class GlyphVectorMapConverter implements BeanMapConverter<GlyphVector> {

	/**
	 * This is a serializable bean containing all the information for one glyph
	 * in a GlyphVector.
	 */
	public static class GlyphElementBean implements Serializable {
		private static final long serialVersionUID = 1L;

		protected int glyphCode;

		/**
		 * May be null
		 */
		protected AffineTransform transform;

		protected Shape outline;

		protected Point2D position;

		protected Shape logicalBounds;

		/**
		 * May be null
		 */
		protected GlyphJustificationInfo justificationInfo;

		protected GlyphMetrics glyphMetrics;

		protected Shape visualBounds;

		public GlyphElementBean(GlyphVector gv, int index) {
			glyphCode = gv.getGlyphCode(index);
			transform = gv.getGlyphTransform(index);
			outline = gv.getGlyphOutline(index);
			position = gv.getGlyphPosition(index);
			logicalBounds = gv.getGlyphLogicalBounds(index);

			justificationInfo = gv.getGlyphJustificationInfo(index);
			glyphMetrics = gv.getGlyphMetrics(index);
			visualBounds = gv.getGlyphVisualBounds(index);
		}

		@Override
		public int hashCode() {
			return ConverterUtils.hashCode(position, outline);
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof GlyphElementBean))
				return false;
			GlyphElementBean other = (GlyphElementBean) obj;
			if (glyphCode != other.glyphCode)
				return false;
			if (!Objects.equals(transform, other.transform))
				return false;
			if (!ConverterUtils.equals(outline, other.outline))
				return false;
			if (!ConverterUtils.equals(position, other.position))
				return false;
			if (!ConverterUtils.equals(logicalBounds, other.logicalBounds))
				return false;
			if (!ConverterUtils.equals(justificationInfo,
					other.justificationInfo))
				return false;
			if (!ConverterUtils.equals(glyphMetrics, other.glyphMetrics))
				return false;
			if (!ConverterUtils.equals(visualBounds, other.visualBounds))
				return false;

			return true;
		}

		private void writeObject(java.io.ObjectOutputStream out)
				throws IOException {
			out.writeInt(0);
			out.writeInt(glyphCode);
			out.writeObject(transform);
			ConverterUtils.writeObject(out, outline);
			ConverterUtils.writeObject(out, position);
			ConverterUtils.writeObject(out, logicalBounds);
			ConverterUtils.writeObject(out, justificationInfo);
			ConverterUtils.writeObject(out, glyphMetrics);
			ConverterUtils.writeObject(out, visualBounds);

		}

		private void readObject(java.io.ObjectInputStream in)
				throws IOException, ClassNotFoundException {
			int internalVersion = in.readInt();
			if (internalVersion == 0) {
				glyphCode = in.readInt();
				transform = (AffineTransform) in.readObject();
				outline = (Shape) ConverterUtils.readObject(in);
				position = (Point2D) ConverterUtils.readObject(in);
				logicalBounds = (Shape) ConverterUtils.readObject(in);
				justificationInfo = (GlyphJustificationInfo) ConverterUtils
						.readObject(in);
				glyphMetrics = (GlyphMetrics) ConverterUtils.readObject(in);
				visualBounds = (Shape) ConverterUtils.readObject(in);
			} else {
				throw new IOException(
						"Unsupported internal version: " + internalVersion);
			}
		}
	}

	/**
	 * This is a recreated GlyphVector.
	 */
	static class BasicGlyphVector extends GlyphVector {

		protected Font font;
		protected FontRenderContext frc;
		protected GlyphElementBean[] glyphs;

		public BasicGlyphVector(Font font, FontRenderContext frc,
				GlyphElementBean[] glyphs) {
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
			for (GlyphElementBean e : glyphs) {
				Rectangle2D r = ShapeBounds.getBounds(e.logicalBounds);
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
			for (GlyphElementBean e : glyphs) {
				Rectangle2D r = ShapeBounds.getBounds(e.visualBounds);
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
			for (GlyphElementBean e : glyphs) {
				sum.append(e.outline, false);
			}
			sum.transform(AffineTransform.getTranslateInstance(x, y));
			return sum;

		}

		@Override
		public Shape getGlyphOutline(int glyphIndex) {
			return glyphs[glyphIndex].outline;
		}

		@Override
		public Point2D getGlyphPosition(int glyphIndex) {
			return glyphs[glyphIndex].position;
		}

		@Override
		public void setGlyphPosition(int glyphIndex, Point2D newPos) {
			glyphs[glyphIndex].position = newPos;
		}

		@Override
		public AffineTransform getGlyphTransform(int glyphIndex) {
			if (glyphs[glyphIndex].transform == null)
				return null;
			return new AffineTransform(glyphs[glyphIndex].transform);
		}

		@Override
		public void setGlyphTransform(int glyphIndex, AffineTransform newTX) {
			glyphs[glyphIndex].transform = newTX == null ? null
					: new AffineTransform(newTX);
		}

		@Override
		public Shape getGlyphLogicalBounds(int glyphIndex) {
			return glyphs[glyphIndex].logicalBounds;
		}

		@Override
		public Shape getGlyphVisualBounds(int glyphIndex) {
			return glyphs[glyphIndex].visualBounds;
		}

		@Override
		public GlyphMetrics getGlyphMetrics(int glyphIndex) {
			return glyphs[glyphIndex].glyphMetrics;
		}

		@Override
		public GlyphJustificationInfo getGlyphJustificationInfo(
				int glyphIndex) {
			return glyphs[glyphIndex].justificationInfo;
		}

		@Override
		public boolean equals(GlyphVector gv) {
			if (!(gv instanceof GlyphVector))
				return false;
			if (gv instanceof BasicGlyphVector)
				return equals((BasicGlyphVector) gv);

			Map<String, Object> atoms = new GlyphVectorMapConverter()
					.createAtoms(gv);
			BasicGlyphVector other = new GlyphVectorMapConverter()
					.createFromAtoms(atoms);

			return equals(other);
		}

		protected boolean equals(BasicGlyphVector set) {
			if (!frc.equals(set.frc))
				return false;
			if (!ConverterUtils.equals(font, set.font))
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

	/**
	 * This property defines {@link GlyphVector#getFont()}. See
	 * FontMapConverter.
	 */
	@SuppressWarnings("rawtypes")
	public static final Key<Map> PROPERTY_FONT = new Key<>(Map.class, "font");

	/**
	 * This property defines {@link GlyphVector#getFontRenderContext()}. See
	 * FontRenderContextMapConverter.
	 */
	@SuppressWarnings("rawtypes")
	public static final Key<Map> PROPERTY_FONTRENDERCONTEXT = new Key<>(
			Map.class, "font-render-context");

	/**
	 * This property defines a List of GlyphElementBeans.
	 */
	@SuppressWarnings("rawtypes")
	public static final Key<List> PROPERTY_GLYPHS = new Key<>(List.class,
			"glyphs");

	@Override
	public Class<GlyphVector> getType() {
		return GlyphVector.class;
	}

	@Override
	public Map<String, Object> createAtoms(GlyphVector gv) {
		Map<String, Object> atoms = new HashMap<>(3);
		PROPERTY_FONT.put(atoms,
				new FontMapConverter().createAtoms(gv.getFont()));
		PROPERTY_FONTRENDERCONTEXT.put(atoms,
				new FontRenderContextMapConverter()
						.createAtoms(gv.getFontRenderContext()));

		List<GlyphElementBean> glyphs = new ArrayList<>(gv.getNumGlyphs());
		for (int a = 0; a < gv.getNumGlyphs(); a++) {
			glyphs.add(new GlyphElementBean(gv, a));
		}
		PROPERTY_GLYPHS.put(atoms, glyphs);
		return atoms;
	}

	@SuppressWarnings("unchecked")
	@Override
	public BasicGlyphVector createFromAtoms(Map<String, Object> atoms) {
		Font font = new FontMapConverter()
				.createFromAtoms(PROPERTY_FONT.get(atoms));
		FontRenderContext frc = new FontRenderContextMapConverter()
				.createFromAtoms(PROPERTY_FONTRENDERCONTEXT.get(atoms));
		List<GlyphElementBean> glyphs = PROPERTY_GLYPHS.get(atoms);
		return new BasicGlyphVector(font, frc,
				glyphs.toArray(new GlyphElementBean[glyphs.size()]));
	}
}