package com.pump.io.serialization;

public interface SerializationFilter {

	/**
	 * Return a SerializationWrapper for the argument, or null if this filter
	 * cannot create one.
	 * <p>
	 * For example: a <code>java.awt.AlphaComopsite</code> is a final class and
	 * is not Serializable. If this method receives an
	 * <code>AlphaComposite</code> it may return a special helper
	 * <code>SerializationWrapper</code> that can effectively encode/decode an
	 * AlphaComposite.
	 * 
	 * @param object
	 * @return
	 */
	SerializationWrapper<?> filter(Object object);

}
