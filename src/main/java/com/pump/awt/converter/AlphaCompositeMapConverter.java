package com.pump.awt.converter;

import java.awt.AlphaComposite;
import java.util.HashMap;
import java.util.Map;

import com.pump.data.Key;
import com.pump.data.converter.BeanMapConverter;

/**
 * This is a BeanMapConverter for AlphaComposites.
 */
public class AlphaCompositeMapConverter
		implements BeanMapConverter<AlphaComposite> {

	private static final long serialVersionUID = 1L;

	/**
	 * This property defines the {@link AlphaComposite#getAlpha()}.
	 */
	public static final Key<Float> PROPERTY_ALPHA = new Key<>(Float.class,
			"alpha");

	/**
	 * This property defines the {@link AlphaComposite#getRule()}.
	 */
	public static final Key<Integer> PROPERTY_RULE = new Key<>(Integer.class,
			"rule");

	@Override
	public Class<AlphaComposite> getType() {
		return AlphaComposite.class;
	}

	@Override
	public Map<String, Object> createAtoms(AlphaComposite composite) {
		Map<String, Object> atoms = new HashMap<>(2);
		PROPERTY_RULE.put(atoms, composite.getRule());
		PROPERTY_ALPHA.put(atoms, composite.getAlpha());
		return atoms;
	}

	@Override
	public AlphaComposite createFromAtoms(Map<String, Object> atoms) {
		int rule = PROPERTY_RULE.get(atoms);
		float alpha = PROPERTY_ALPHA.get(atoms);
		return AlphaComposite.getInstance(rule, alpha);
	}
}
