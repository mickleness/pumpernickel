package com.pump.showcase.chart;

import java.util.List;
import java.util.Map;

public interface ChartDataGenerator {

	enum ExecutionMode {
		/**
		 * In this mode {@link ChartDataGenerator#runMemorySample(Map)} and
		 * {@link ChartDataGenerator#runTimedSample(Map) do exactly the same
		 * thing, and only one will be invoked to generate chart data.
		 */
		RECORD_TIME_AND_MEMORY_SIMULTANEOUSLY,
		/**
		 * In this mode only {@link ChartDataGenerator#runTimedSample(Map)} is
		 * invoked.
		 */
		RECORD_TIME_ONLY,
		/**
		 * In this mode only {@link ChartDataGenerator#runMemorySample(Map)} is
		 * invoked.
		 */
		RECORD_MEMORY_ONLY,
		/**
		 * In this mode {@link ChartDataGenerator#runTimedSample(Map)} and
		 * {@link ChartDataGenerator#runMemorySample(Map)} are invoked
		 * separately.
		 */
		RECORD_TIME_AND_MEMORY_SEPARATELY
	}

	ExecutionMode getExecutionMode();

	/**
	 * Return the number of times {@link #runTimedSample(Map)} will be called
	 * for the same parameters. (Usually we should collect several samples and
	 * report the median sample.)
	 */
	int getTimedSampleCount();

	/**
	 * Return the number of times {@link #runMemorySample(Map)} will be called
	 * for the same parameters. (Usually we should collect several samples and
	 * report the median sample.)
	 */
	int getMemorySampleCount();

	/**
	 * Return a list of unique parameters to later pass to
	 * {@link #runTimedSample(Map)}.
	 */
	List<Map<String, ?>> getTimedParameters();

	/**
	 * Return a list of unique parameters to later pass to
	 * {@link #runMemorySample(Map)}.
	 */
	List<Map<String, ?>> getMemoryParameters();

	/**
	 * Run a specific operation with a given set of parameters. This method is
	 * timed.
	 */
	void runTimedSample(Map<String, ?> parameters) throws Exception;

	/**
	 * Run a specific operation with a given set of parameters. The memory usage
	 * before and after this operation is recorded.
	 */
	void runMemorySample(Map<String, ?> parameters) throws Exception;

	/**
	 * This optional method is invoked before {@link #runTimedSample(Map)} but
	 * its performance is NOT reported.
	 */
	default void setupTimedSample(Map<String, ?> parameters) {
	}

	/**
	 * This optional method is invoked before {@link #runMemorySample(Map)} but
	 * its performance is NOT reported.
	 */
	default void setupMemorySample(Map<String, ?> parameters) {
	}

	/**
	 * This optional method is invoked after {@Link #runTimedSample(Map)} but
	 * its performance is NOT reported.
	 */
	default void tearDownTimedSample(Map<String, ?> parameters) {
	}

	/**
	 * This optional method is invoked after {@Link #runMemorySample(Map)} but
	 * its performance is NOT reported.
	 */
	default void tearDownMemorySample(Map<String, ?> parameters) {
	}

}