package com.pump.awt.serialization;

import java.awt.font.GlyphJustificationInfo;

import com.pump.io.serialization.AbstractSerializationWrapper;
import com.pump.io.serialization.SerializationFilter;
import com.pump.io.serialization.SerializationWrapper;

/**
 * This is a SerializationWrapper for GlyphJustificationInfo.
 */
public class GlyphJustificationInfoSerializationWrapper
		extends AbstractSerializationWrapper<GlyphJustificationInfo> {
	private static final long serialVersionUID = 1L;

	public static SerializationFilter FILTER = new SerializationFilter() {
		@Override
		public SerializationWrapper<?> filter(Object object) {
			if (object instanceof GlyphJustificationInfo) {
				GlyphJustificationInfo gji = (GlyphJustificationInfo) object;
				return new GlyphJustificationInfoSerializationWrapper(gji);
			}
			return null;
		}
	};

	protected static final String KEY_WEIGHT = "weight";
	protected static final String KEY_GROW_ABSORB = "growAbsorb";
	protected static final String KEY_GROW_PRIORITY = "growPriority";
	protected static final String KEY_GROW_LEFT_LIMIT = "growLeftLimit";
	protected static final String KEY_GROW_RIGHT_LIMIT = "growRightLimit";
	protected static final String KEY_SHRINK_ABSORB = "shrinkAbsorb";
	protected static final String KEY_SHRINK_PRIORITY = "shrinkPriorty";
	protected static final String KEY_SHRINK_LEFT_LIMIT = "shrinkLeftLimit";
	protected static final String KEY_SHRINK_RIGHT_LIMIT = "shrinkRightLimit";

	public GlyphJustificationInfoSerializationWrapper(
			GlyphJustificationInfo gji) {
		map.put(KEY_WEIGHT, gji.weight);
		map.put(KEY_GROW_ABSORB, gji.growAbsorb);
		map.put(KEY_GROW_PRIORITY, gji.growPriority);
		map.put(KEY_GROW_LEFT_LIMIT, gji.growLeftLimit);
		map.put(KEY_GROW_RIGHT_LIMIT, gji.growRightLimit);
		map.put(KEY_SHRINK_ABSORB, gji.shrinkAbsorb);
		map.put(KEY_SHRINK_PRIORITY, gji.shrinkPriority);
		map.put(KEY_SHRINK_LEFT_LIMIT, gji.shrinkLeftLimit);
		map.put(KEY_SHRINK_RIGHT_LIMIT, gji.shrinkRightLimit);
	}

	@Override
	public GlyphJustificationInfo create() {
		float weight = ((Number) map.get(KEY_WEIGHT)).floatValue();
		boolean growAbsorb = (Boolean) map.get(KEY_GROW_ABSORB);
		int growPriority = ((Number) map.get(KEY_GROW_PRIORITY)).intValue();
		float growLeftLimit = ((Number) map.get(KEY_GROW_LEFT_LIMIT))
				.floatValue();
		float growRightLimit = ((Number) map.get(KEY_GROW_RIGHT_LIMIT))
				.floatValue();
		boolean shrinkAbsorb = (Boolean) map.get(KEY_SHRINK_ABSORB);
		int shrinkPriority = ((Number) map.get(KEY_SHRINK_PRIORITY)).intValue();
		float shrinkLeftLimit = ((Number) map.get(KEY_SHRINK_LEFT_LIMIT))
				.floatValue();
		float shrinkRightLimit = ((Number) map.get(KEY_SHRINK_RIGHT_LIMIT))
				.floatValue();

		return new GlyphJustificationInfo(weight, growAbsorb, growPriority,
				growLeftLimit, growRightLimit, shrinkAbsorb, shrinkPriority,
				shrinkLeftLimit, shrinkRightLimit);
	}

}
