package com.pump.awt.serialization;

import java.awt.AlphaComposite;

import com.pump.io.serialization.AbstractSerializationWrapper;
import com.pump.io.serialization.SerializationFilter;
import com.pump.io.serialization.SerializationWrapper;

public class AlphaCompositeSerializationWrapper
		extends AbstractSerializationWrapper<AlphaComposite> {

	private static final long serialVersionUID = 1L;

	public static SerializationFilter FILTER = new SerializationFilter() {
		@Override
		public SerializationWrapper<?> filter(Object object) {
			if (object instanceof AlphaComposite) {
				AlphaComposite ac = (AlphaComposite) object;
				return new AlphaCompositeSerializationWrapper(ac);
			}
			return null;
		}
	};

	protected static final String KEY_RULE = "rule";
	protected static final String KEY_ALPHA = "alpha";

	public AlphaCompositeSerializationWrapper(AlphaComposite ac) {
		map.put(KEY_RULE, ac.getRule());
		map.put(KEY_ALPHA, ac.getAlpha());
	}

	@Override
	public AlphaComposite create() {
		int rule = ((Number) map.get(KEY_RULE)).intValue();
		float alpha = ((Number) map.get(KEY_ALPHA)).floatValue();
		return AlphaComposite.getInstance(rule, alpha);
	}
}
