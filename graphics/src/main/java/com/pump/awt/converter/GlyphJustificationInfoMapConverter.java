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

import java.awt.font.GlyphJustificationInfo;
import java.util.HashMap;
import java.util.Map;

import com.pump.data.Key;
import com.pump.data.converter.BeanMapConverter;

/**
 * This is a BeanMapConverter for GlyphJustificationInfo.
 */
public class GlyphJustificationInfoMapConverter
		implements BeanMapConverter<GlyphJustificationInfo> {

	private static final long serialVersionUID = 1L;

	/**
	 * This property defines {@link GlyphJustificationInfo#weight}.
	 */
	public static final Key<Float> PROPERTY_WEIGHT = new Key<>(Float.class,
			"weight");

	/**
	 * This property defines {@link GlyphJustificationInfo#growPriority}.
	 */
	public static final Key<Integer> PROPERTY_GROW_PRIORITY = new Key<>(
			Integer.class, "grow-priority");

	/**
	 * This property defines {@link GlyphJustificationInfo#growAbsorb}.
	 */
	public static final Key<Boolean> PROPERTY_GROW_ABSORB = new Key<>(
			Boolean.class, "grow-absorb");

	/**
	 * This property defines {@link GlyphJustificationInfo#growLeftLimit}.
	 */
	public static final Key<Float> PROPERTY_GROW_LEFT_LIMIT = new Key<>(
			Float.class, "grow-left-limit");

	/**
	 * This property defines {@link GlyphJustificationInfo#growRightLimit}.
	 */
	public static final Key<Float> PROPERTY_GROW_RIGHT_LIMIT = new Key<>(
			Float.class, "grow-right-limit");

	/**
	 * This property defines {@link GlyphJustificationInfo#shrinkPriority}.
	 */
	public static final Key<Integer> PROPERTY_SHRINK_PRIORITY = new Key<>(
			Integer.class, "shrink-priority");

	/**
	 * This property defines {@link GlyphJustificationInfo#shrinkAbsorb}.
	 */
	public static final Key<Boolean> PROPERTY_SHRINK_ABSORB = new Key<>(
			Boolean.class, "shrink-absorb");

	/**
	 * This property defines {@link GlyphJustificationInfo#shrinkLeftLimit}.
	 */
	public static final Key<Float> PROPERTY_SHRINK_LEFT_LIMIT = new Key<>(
			Float.class, "shrink-left-limit");

	/**
	 * This property defines {@link GlyphJustificationInfo#shrinkRightLimit}.
	 */
	public static final Key<Float> PROPERTY_SHRINK_RIGHT_LIMIT = new Key<>(
			Float.class, "shrink-right-limit");

	@Override
	public Class<GlyphJustificationInfo> getType() {
		return GlyphJustificationInfo.class;
	}

	@Override
	public Map<String, Object> createAtoms(GlyphJustificationInfo gji) {
		Map<String, Object> atoms = new HashMap<>(9);
		PROPERTY_WEIGHT.put(atoms, gji.weight);
		PROPERTY_GROW_ABSORB.put(atoms, gji.growAbsorb);
		PROPERTY_GROW_PRIORITY.put(atoms, gji.growPriority);
		PROPERTY_GROW_LEFT_LIMIT.put(atoms, gji.growLeftLimit);
		PROPERTY_GROW_RIGHT_LIMIT.put(atoms, gji.growRightLimit);
		PROPERTY_SHRINK_ABSORB.put(atoms, gji.shrinkAbsorb);
		PROPERTY_SHRINK_PRIORITY.put(atoms, gji.shrinkPriority);
		PROPERTY_SHRINK_LEFT_LIMIT.put(atoms, gji.shrinkLeftLimit);
		PROPERTY_SHRINK_RIGHT_LIMIT.put(atoms, gji.shrinkRightLimit);
		return atoms;
	}

	@Override
	public GlyphJustificationInfo createFromAtoms(Map<String, Object> atoms) {
		float weight = PROPERTY_WEIGHT.get(atoms);
		boolean growAbsorb = PROPERTY_GROW_ABSORB.get(atoms);
		int growPriority = PROPERTY_GROW_PRIORITY.get(atoms);
		float growLeftLimit = PROPERTY_GROW_LEFT_LIMIT.get(atoms);
		float growRightLimit = PROPERTY_GROW_RIGHT_LIMIT.get(atoms);
		boolean shrinkAbsorb = PROPERTY_SHRINK_ABSORB.get(atoms);
		int shrinkPriority = PROPERTY_SHRINK_PRIORITY.get(atoms);
		float shrinkLeftLimit = PROPERTY_SHRINK_LEFT_LIMIT.get(atoms);
		float shrinkRightLimit = PROPERTY_SHRINK_RIGHT_LIMIT.get(atoms);

		return new GlyphJustificationInfo(weight, growAbsorb, growPriority,
				growLeftLimit, growRightLimit, shrinkAbsorb, shrinkPriority,
				shrinkLeftLimit, shrinkRightLimit);
	}
}