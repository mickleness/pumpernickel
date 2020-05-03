package com.pump.awt.serialization;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.LinkedList;

import com.pump.io.serialization.FilteredObjectOutputStream;
import com.pump.io.serialization.SerializationFilter;

public class AWTSerializationUtils {

	public static final Collection<SerializationFilter> FILTERS = new LinkedList<>();
	static {
		FILTERS.add(AlphaCompositeSerializationWrapper.FILTER);
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

	public static FilteredObjectOutputStream createFilteredObjectOutputStream(
			ObjectOutputStream out) throws IOException {

		FilteredObjectOutputStream fout = new FilteredObjectOutputStream(out);
		for (SerializationFilter filter : FILTERS) {
			fout.addFilter(filter);
		}

		return fout;
	}
}
