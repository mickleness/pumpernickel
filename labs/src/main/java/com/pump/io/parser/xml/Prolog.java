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
package com.pump.io.parser.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.pump.io.CombinedInputStream;
import com.pump.io.parser.Parser.SkipWhiteSpaceReceiver;
import com.pump.io.parser.Parser.StringToken;
import com.pump.io.parser.Token;
import com.pump.io.parser.xml.XMLParser.AssignmentToken;
import com.pump.io.parser.xml.XMLParser.EndPrologToken;
import com.pump.io.parser.xml.XMLParser.StartPrologToken;
import com.pump.io.parser.xml.XMLParser.WordToken;
import com.pump.util.BasicReceiver;

public class Prolog {

	static InputStream parseEncoding(InputStream in, Prolog prolog)
			throws IOException {
		int k;
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		do {
			k = in.read();
			byteOut.write(k);

			// abort if the prolog isn't the first thing we read:
			if (byteOut.size() == 1 && k != '<') {
				break;
			} else if (byteOut.size() == 2 && k != '?') {
				break;
			}

			if (k == '>') {
				String prologString = new String(byteOut.toByteArray(), "UTF-8");
				prolog.initialize(prologString);
				break;
			}
		} while (k != -1);

		return new CombinedInputStream(new ByteArrayInputStream(
				byteOut.toByteArray()), in, true, true);
	}

	public void initialize(String str) {
		XMLParser parser = new XMLParser();
		BasicReceiver<Token> receiver = new BasicReceiver<>();
		try (StringReader reader = new StringReader(str)) {
			parser.parse(reader, new SkipWhiteSpaceReceiver(receiver));
		} catch (IOException e) {
			// this shouldn't happen for a StringReader
			throw new RuntimeException(e);
		}

		List<Token> tokens = new LinkedList<>(Arrays.asList(receiver
				.toArray(new Token[receiver.getSize()])));

		Token t = receiver.getSize() >= 1 ? tokens.remove(0) : null;
		if (!(t instanceof StartPrologToken && t.getText().equals("<?"))) {
			throw new RuntimeException("The prolog \'" + str
					+ "\' did not begin with '<?'.");
		}
		t = receiver.getSize() >= 1 ? tokens.remove(0) : null;
		if (!(t instanceof WordToken && t.getText().equals("xml"))) {
			throw new RuntimeException("The prolog \'" + str
					+ "\' did not begin with '<?xml'.");
		}

		while (tokens.size() > 0) {
			t = tokens.remove(0);
			if (t instanceof WordToken) {
				String attribute = t.getText();
				t = receiver.getSize() >= 1 ? tokens.remove(0) : null;
				if (!(t instanceof AssignmentToken && t.getText().equals("="))) {
					throw new RuntimeException("Unrecognized token \""
							+ t.getText() + "\" after attribute \"" + attribute
							+ "\".");
				}
				t = receiver.getSize() >= 1 ? tokens.remove(0) : null;
				if (!(t instanceof StringToken)) {
					throw new RuntimeException("Unexpected token \""
							+ t.getText() + "\" after attribute \"" + attribute
							+ "\".");
				}
				setAttribute(attribute, ((StringToken) t).getDecodedString());
			} else if (t instanceof EndPrologToken) {
				break;
			} else {
				throw new RuntimeException("Unexpected token: " + t);
			}
		}
		if (tokens.size() > 0) {
			// this should never happen
			throw new RuntimeException("Unprocessed tokens: " + tokens);
		}
	}

	Map<String, String> attributes = new LinkedHashMap<>();

	public Map<String, String> getAttributes() {
		return Collections.unmodifiableMap(attributes);
	}

	public void setAttribute(String key, String value) {
		attributes.put(key, value);
	}

	public String getAttribute(String key) {
		return attributes.get(key);
	}

	@Override
	public int hashCode() {
		return attributes.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Prolog))
			return false;

		Prolog other = (Prolog) obj;
		return attributes.equals(other.attributes);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml");
		for (Entry<String, String> entry : attributes.entrySet()) {
			sb.append(" " + entry.getKey() + "=\"" + entry.getValue() + "\"");
		}
		sb.append("?>");
		return sb.toString();
	}
}