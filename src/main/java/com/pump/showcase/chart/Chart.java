package com.pump.showcase.chart;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class Chart {
	List<Map.Entry<String, Number>> seriesData = new ArrayList<>();
	Function<Number, String> valueFormatter;
	String name;

	public Chart(String name) {
		this.name = Objects.requireNonNull(name);
	}

	public void setValueFormatter(Function<Number, String> valueFormatter) {
		this.valueFormatter = valueFormatter;
	}

	public List<Map.Entry<String, Number>> getSeriesData() {
		return seriesData;
	}

	public String getName() {
		return name;
	}

	/**
	 * Convert a chart value to a String. For example this may convert 1,000,000
	 * to "1.0"
	 */
	public String formatValue(Number value) {
		if (valueFormatter != null)
			return valueFormatter.apply(value);
		if (value instanceof Number)
			return NumberFormat.getInstance()
					.format(((Number) value).doubleValue());
		return String.valueOf(value);
	}

	/**
	 * Return the maximum value in this chart.
	 * 
	 * @return
	 */
	public Number getMax() {
		Number max = null;
		for (Map.Entry<String, Number> e : seriesData) {
			if (max == null) {
				max = e.getValue();
			} else {
				Number otherV = e.getValue();
				max = otherV.doubleValue() > max.doubleValue() ? otherV : max;
			}
		}
		return max;
	}

	public boolean isEmpty() {
		return seriesData.isEmpty();
	}

	/**
	 * Return an unmodifiable snapshot of this data as a Map.
	 */
	public Map<String, Number> toMap() {
		Map<String, Number> returnValue = new LinkedHashMap<>();
		for (Map.Entry<String, Number> e : seriesData) {
			returnValue.put(e.getKey(), e.getValue());
		}
		return Collections.unmodifiableMap(returnValue);
	}

}
