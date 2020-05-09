package com.pump.awt.serialization;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.LinkedList;

import com.pump.io.serialization.FilteredObjectOutputStream;
import com.pump.io.serialization.SerializationFilter;

/**
 * This class contains static help methods related to serializing AWT classes
 * that are not already serializable.
 */
public class AWTSerializationUtils {

	/**
	 * This is a list of SerializationFilters that can help serialize
	 * AWT-related classes that are not already serializable. This is a mutable
	 * collection so you can add new filters to this as needed.
	 */
	public static final Collection<SerializationFilter> FILTERS = new LinkedList<>();
	static {
		FILTERS.add(AlphaCompositeSerializationWrapper.FILTER);
		FILTERS.add(AttributedCharacterIteratorSerializationWrapper.FILTER);
		FILTERS.add(AttributedStringSerializationWrapper.FILTER);
		FILTERS.add(BasicStrokeSerializationWrapper.FILTER);
		FILTERS.add(FontRenderContextSerializationWrapper.FILTER);
		FILTERS.add(GlyphJustificationInfoSerializationWrapper.FILTER);
		FILTERS.add(GlyphMetricsSerializationWrapper.FILTER);
		FILTERS.add(GlyphVectorSerializationWrapper.FILTER);
		FILTERS.add(GradientPaintSerializationWrapper.FILTER);
		FILTERS.add(ImageSerializationWrapper.FILTER);
		FILTERS.add(LinearGradientPaintSerializationWrapper.FILTER);
		FILTERS.add(Point2DSerializationWrapper.FILTER);
		FILTERS.add(RadialGradientPaintSerializationWrapper.FILTER);
		FILTERS.add(Rectangle2DSerializationWrapper.FILTER);
		FILTERS.add(RenderingHintsSerializationWrapper.FILTER);
		FILTERS.add(ShapeSerializationWrapper.FILTER);
		FILTERS.add(TexturePaintSerializationWrapper.FILTER);
	}

	/**
	 * Create a FilteredObjectOutputStream that includes everything in
	 * {@link #FILTERS}.
	 */
	public static FilteredObjectOutputStream createFilteredObjectOutputStream(
			ObjectOutputStream out) throws IOException {

		FilteredObjectOutputStream fout = new FilteredObjectOutputStream(out);
		for (SerializationFilter filter : FILTERS) {
			fout.addFilter(filter);
		}

		return fout;
	}
}
