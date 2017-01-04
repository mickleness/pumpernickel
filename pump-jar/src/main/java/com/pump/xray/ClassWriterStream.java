package com.pump.xray;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.pump.io.IndentedPrintStream;

public class ClassWriterStream extends IndentedPrintStream {

	Map<String, String> nameToSimpleNameMap = new HashMap<>();
	
	public ClassWriterStream(OutputStream out, boolean autoFlush,
			String encoding) throws UnsupportedEncodingException {
		super(out, autoFlush, encoding);
	}
	
	/**
	 * Return a name that maps fully qualified classnames to their simple names.
	 */
	public Map<String, String> getNameMap() {
		return nameToSimpleNameMap;
	}
}