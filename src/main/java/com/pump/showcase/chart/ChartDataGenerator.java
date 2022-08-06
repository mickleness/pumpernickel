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
package com.pump.showcase.chart;

import java.util.List;
import java.util.Map;

public interface ChartDataGenerator {

	/**
	 * Return a list of unique parameters to later pass to
	 * {@link #runSample(Map)}.
	 */
	List<Map<String, Object>> getParameters();

	/**
	 * Run a specific operation with a given set of parameters.
	 */
	void runSample(Map<String, Object> parameters) throws Exception;

	/**
	 * This optional method is invoked before {@link #runSample(Map)} but
	 * its performance is NOT reported.
	 */
	default void setupSample(Map<String, ?> parameters) {
	}

	/**
	 * This optional method is invoked after {@link #runSample(Map)} but
	 * its performance is NOT reported.
	 */
	default void tearDownSample(Map<String, ?> parameters) {
	}

}