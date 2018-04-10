/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://raw.githubusercontent.com/mickleness/pumpernickel/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.io;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This class can encode and decode over 200 HTML references.
 * 
 * <p>
 * There are apache classes that do the same thing, but these libraries are
 * intended to be isolated and light, so I didn't want to introduce those
 * dependencies.
 * 
 * <p>
 * These were originally based on a list from www.w3schools.com, but that link
 * is dead now.
 * 
 * @see com.pump.io.parser.java.JavaEncoding
 */
public class HTMLEncoding {
	static class EscapeChar {
		char ch;
		int entityNumber;
		String entityName;
		String description;

		EscapeChar(char ch, int entityNumber, String entityName,
				String description) {
			this.ch = ch;
			this.entityNumber = entityNumber;
			this.entityName = entityName;
			this.description = description;

			if (entityName.startsWith("&") == false)
				throw new IllegalArgumentException(entityName);
			if (entityName.endsWith(";") == false)
				throw new IllegalArgumentException(entityName);
		}

		@Override
		public String toString() {
			return "EscapeChar[ ch=\'" + ch + "\', entityNumber="
					+ entityNumber + ", entityName=" + entityName
					+ ", description=" + description + " ]";
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof EscapeChar))
				return false;
			EscapeChar c = (EscapeChar) obj;
			return c.entityNumber == entityNumber;
		}

		@Override
		public int hashCode() {
			return entityNumber;
		}
	}

	private static final Map<String, EscapeChar> entityNameMap = new HashMap<String, EscapeChar>();
	private static final Map<Character, EscapeChar> charMap = new HashMap<Character, EscapeChar>();
	static {
		Set<EscapeChar> set = new HashSet<EscapeChar>();
		set.add(new EscapeChar('\u00c0', 192, "&Agrave;",
				"capital a, grave accent"));
		set.add(new EscapeChar('\u0022', 34, "&quot;", "quotation mark"));
		set.add(new EscapeChar('\'', 39, "&apos;", "apostrophe"));
		set.add(new EscapeChar('\u0026', 38, "&amp;", "ampersand"));
		set.add(new EscapeChar('\u003c', 60, "&lt;", "less-than"));
		set.add(new EscapeChar('\u003e', 62, "&gt;", "greater-than"));
		set.add(new EscapeChar('\u00c1', 193, "&Aacute;",
				"capital a, acute accent"));
		set.add(new EscapeChar('\u00c2', 194, "&Acirc;",
				"capital a, circumflex accent"));
		set.add(new EscapeChar('\u00c3', 195, "&Atilde;", "capital a, tilde"));
		set.add(new EscapeChar('\u00c4', 196, "&Auml;",
				"capital a, umlaut mark"));
		set.add(new EscapeChar('\u00c5', 197, "&Aring;", "capital a, ring"));
		set.add(new EscapeChar('\u00c6', 198, "&AElig;", "capital ae"));
		set.add(new EscapeChar('\u00c7', 199, "&Ccedil;", "capital c, cedilla"));
		set.add(new EscapeChar('\u00c8', 200, "&Egrave;",
				"capital e, grave accent"));
		set.add(new EscapeChar('\u00c9', 201, "&Eacute;",
				"capital e, acute accent"));
		set.add(new EscapeChar('\u00ca', 202, "&Ecirc;",
				"capital e, circumflex accent"));
		set.add(new EscapeChar('\u00cb', 203, "&Euml;",
				"capital e, umlaut mark"));
		set.add(new EscapeChar('\u00cc', 204, "&Igrave;",
				"capital i, grave accent"));
		set.add(new EscapeChar('\u00cd', 205, "&Iacute;",
				"capital i, acute accent"));
		set.add(new EscapeChar('\u00ce', 206, "&Icirc;",
				"capital i, circumflex accent"));
		set.add(new EscapeChar('\u00cf', 207, "&Iuml;",
				"capital i, umlaut mark"));
		set.add(new EscapeChar('\u00d0', 208, "&ETH;", "capital eth, Icelandic"));
		set.add(new EscapeChar('\u00d1', 209, "&Ntilde;", "capital n, tilde"));
		set.add(new EscapeChar('\u00d2', 210, "&Ograve;",
				"capital o, grave accent"));
		set.add(new EscapeChar('\u00d3', 211, "&Oacute;",
				"capital o, acute accent"));
		set.add(new EscapeChar('\u00d4', 212, "&Ocirc;",
				"capital o, circumflex accent"));
		set.add(new EscapeChar('\u00d5', 213, "&Otilde;", "capital o, tilde"));
		set.add(new EscapeChar('\u00d6', 214, "&Ouml;",
				"capital o, umlaut mark"));
		set.add(new EscapeChar('\u00d8', 216, "&Oslash;", "capital o, slash"));
		set.add(new EscapeChar('\u00d9', 217, "&Ugrave;",
				"capital u, grave accent"));
		set.add(new EscapeChar('\u00da', 218, "&Uacute;",
				"capital u, acute accent"));
		set.add(new EscapeChar('\u00db', 219, "&Ucirc;",
				"capital u, circumflex accent"));
		set.add(new EscapeChar('\u00dc', 220, "&Uuml;",
				"capital u, umlaut mark"));
		set.add(new EscapeChar('\u00dd', 221, "&Yacute;",
				"capital y, acute accent"));
		set.add(new EscapeChar('\u00de', 222, "&THORN;",
				"capital THORN, Icelandic"));
		set.add(new EscapeChar('\u00df', 223, "&szlig;",
				"small sharp s, German"));
		set.add(new EscapeChar('\u00e0', 224, "&agrave;",
				"small a, grave accent"));
		set.add(new EscapeChar('\u00e1', 225, "&aacute;",
				"small a, acute accent"));
		set.add(new EscapeChar('\u00e2', 226, "&acirc;",
				"small a, circumflex accent"));
		set.add(new EscapeChar('\u00e3', 227, "&atilde;", "small a, tilde"));
		set.add(new EscapeChar('\u00e4', 228, "&auml;", "small a, umlaut mark"));
		set.add(new EscapeChar('\u00e5', 229, "&aring;", "small a, ring"));
		set.add(new EscapeChar('\u00e6', 230, "&aelig;", "small ae"));
		set.add(new EscapeChar('\u00e7', 231, "&ccedil;", "small c, cedilla"));
		set.add(new EscapeChar('\u00e8', 232, "&egrave;",
				"small e, grave accent"));
		set.add(new EscapeChar('\u00e9', 233, "&eacute;",
				"small e, acute accent"));
		set.add(new EscapeChar('\u00ea', 234, "&ecirc;",
				"small e, circumflex accent"));
		set.add(new EscapeChar('\u00eb', 235, "&euml;", "small e, umlaut mark"));
		set.add(new EscapeChar('\u00ec', 236, "&igrave;",
				"small i, grave accent"));
		set.add(new EscapeChar('\u00ed', 237, "&iacute;",
				"small i, acute accent"));
		set.add(new EscapeChar('\u00ee', 238, "&icirc;",
				"small i, circumflex accent"));
		set.add(new EscapeChar('\u00ef', 239, "&iuml;", "small i, umlaut mark"));
		set.add(new EscapeChar('\u00f0', 240, "&eth;", "small eth, Icelandic"));
		set.add(new EscapeChar('\u00f1', 241, "&ntilde;", "small n, tilde"));
		set.add(new EscapeChar('\u00f2', 242, "&ograve;",
				"small o, grave accent"));
		set.add(new EscapeChar('\u00f3', 243, "&oacute;",
				"small o, acute accent"));
		set.add(new EscapeChar('\u00f4', 244, "&ocirc;",
				"small o, circumflex accent"));
		set.add(new EscapeChar('\u00f5', 245, "&otilde;", "small o, tilde"));
		set.add(new EscapeChar('\u00f6', 246, "&ouml;", "small o, umlaut mark"));
		set.add(new EscapeChar('\u00f8', 248, "&oslash;", "small o, slash"));
		set.add(new EscapeChar('\u00f9', 249, "&ugrave;",
				"small u, grave accent"));
		set.add(new EscapeChar('\u00fa', 250, "&uacute;",
				"small u, acute accent"));
		set.add(new EscapeChar('\u00fb', 251, "&ucirc;",
				"small u, circumflex accent"));
		set.add(new EscapeChar('\u00fc', 252, "&uuml;", "small u, umlaut mark"));
		set.add(new EscapeChar('\u00fd', 253, "&yacute;",
				"small y, acute accent"));
		set.add(new EscapeChar('\u00fe', 254, "&thorn;",
				"small thorn, Icelandic"));
		set.add(new EscapeChar('\u00ff', 255, "&yuml;", "small y, umlaut mark"));
		set.add(new EscapeChar('\u0020', 160, "&nbsp;", "non-breaking space"));
		set.add(new EscapeChar('\u00a1', 161, "&iexcl;",
				"inverted exclamation mark"));
		set.add(new EscapeChar('\u00a2', 162, "&cent;", "cent"));
		set.add(new EscapeChar('\u00a3', 163, "&pound;", "pound"));
		set.add(new EscapeChar('\u00a4', 164, "&curren;", "currency"));
		set.add(new EscapeChar('\u00a5', 165, "&yen;", "yen"));
		set.add(new EscapeChar('\u00a6', 166, "&brvbar;", "broken vertical bar"));
		set.add(new EscapeChar('\u00a7', 167, "&sect;", "section"));
		set.add(new EscapeChar('\u00a8', 168, "&uml;", "spacing diaeresis"));
		set.add(new EscapeChar('\u00a9', 169, "&copy;", "copyright"));
		set.add(new EscapeChar('\u00aa', 170, "&ordf;",
				"feminine ordinal indicator"));
		set.add(new EscapeChar('\u00ab', 171, "&laquo;",
				"angle quotation mark (left)"));
		set.add(new EscapeChar('\u00ac', 172, "&not;", "negation"));
		set.add(new EscapeChar('\u00ad', 173, "&shy;", "soft hyphen"));
		set.add(new EscapeChar('\u00ae', 174, "&reg;", "registered trademark"));
		set.add(new EscapeChar('\u00af', 175, "&macr;", "spacing macron"));
		set.add(new EscapeChar('\u00b0', 176, "&deg;", "degree"));
		set.add(new EscapeChar('\u00b1', 177, "&plusmn;", "plus-or-minus"));
		set.add(new EscapeChar('\u00b2', 178, "&sup2;", "superscript 2"));
		set.add(new EscapeChar('\u00b3', 179, "&sup3;", "superscript 3"));
		set.add(new EscapeChar('\u00b4', 180, "&acute;", "spacing acute"));
		set.add(new EscapeChar('\u00b5', 181, "&micro;", "micro"));
		set.add(new EscapeChar('\u00b6', 182, "&para;", "paragraph"));
		set.add(new EscapeChar('\u00b7', 183, "&middot;", "middle dot"));
		set.add(new EscapeChar('\u00b8', 184, "&cedil;", "spacing cedilla"));
		set.add(new EscapeChar('\u00b9', 185, "&sup1;", "superscript 1"));
		set.add(new EscapeChar('\u00ba', 186, "&ordm;",
				"masculine ordinal indicator"));
		set.add(new EscapeChar('\u00bb', 187, "&raquo;",
				"angle quotation mark (right)"));
		set.add(new EscapeChar('\u00bc', 188, "&frac14;", "fraction 1/4"));
		set.add(new EscapeChar('\u00bd', 189, "&frac12;", "fraction 1/2"));
		set.add(new EscapeChar('\u00be', 190, "&frac34;", "fraction 3/4"));
		set.add(new EscapeChar('\u00bf', 191, "&iquest;",
				"inverted question mark"));
		set.add(new EscapeChar('\u00d7', 215, "&times;", "multiplication"));
		set.add(new EscapeChar('\u00f7', 247, "&divide;", "division"));
		set.add(new EscapeChar('\u0152', 338, "&OElig;", "capital ligature OE"));
		set.add(new EscapeChar('\u0153', 339, "&oelig;", "small ligature oe"));
		set.add(new EscapeChar('\u0160', 352, "&Scaron;",
				"capital S with caron"));
		set.add(new EscapeChar('\u0161', 353, "&scaron;", "small S with caron"));
		set.add(new EscapeChar('\u0178', 376, "&Yuml;",
				"capital Y with diaeres"));
		set.add(new EscapeChar('\u0192', 402, "&fnof;", "f with hook"));
		set.add(new EscapeChar('\u02c6', 710, "&circ;",
				"modifier letter circumflex accent"));
		set.add(new EscapeChar('\u02dc', 732, "&tilde;", "small tilde"));
		set.add(new EscapeChar('\u2002', 8194, "&ensp;", "en space"));
		set.add(new EscapeChar('\u2003', 8195, "&emsp;", "em space"));
		set.add(new EscapeChar('\u2009', 8201, "&thinsp;", "thin space"));
		set.add(new EscapeChar('\u200c', 8204, "&zwnj;",
				"zero width non-joiner"));
		set.add(new EscapeChar('\u200d', 8205, "&zwj;", "zero width joiner"));
		set.add(new EscapeChar('\u200e', 8206, "&lrm;", "left-to-right mark"));
		set.add(new EscapeChar('\u200f', 8207, "&rlm;", "right-to-left mark"));
		set.add(new EscapeChar('\u2013', 8211, "&ndash;", "en dash"));
		set.add(new EscapeChar('\u2014', 8212, "&mdash;", "em dash"));
		set.add(new EscapeChar('\u2018', 8216, "&lsquo;",
				"left single quotation mark"));
		set.add(new EscapeChar('\u2019', 8217, "&rsquo;",
				"right single quotation mark"));
		set.add(new EscapeChar('\u201a', 8218, "&sbquo;",
				"single low-9 quotation mark"));
		set.add(new EscapeChar('\u201c', 8220, "&ldquo;",
				"left double quotation mark"));
		set.add(new EscapeChar('\u201d', 8221, "&rdquo;",
				"right double quotation mark"));
		set.add(new EscapeChar('\u201e', 8222, "&bdquo;",
				"double low-9 quotation mark"));
		set.add(new EscapeChar('\u2020', 8224, "&dagger;", "dagger"));
		set.add(new EscapeChar('\u2021', 8225, "&Dagger;", "double dagger"));
		set.add(new EscapeChar('\u2022', 8226, "&bull;", "bullet"));
		set.add(new EscapeChar('\u2026', 8230, "&hellip;",
				"horizontal ellipsis"));
		set.add(new EscapeChar('\u2030', 8240, "&permil;", "per mille"));
		set.add(new EscapeChar('\u2032', 8242, "&prime;", "minutes"));
		set.add(new EscapeChar('\u2033', 8243, "&Prime;", "seconds"));
		set.add(new EscapeChar('\u2039', 8249, "&lsaquo;",
				"single left angle quotation"));
		set.add(new EscapeChar('\u203a', 8250, "&rsaquo;",
				"single right angle quotation"));
		set.add(new EscapeChar('\u203e', 8254, "&oline;", "overline"));
		set.add(new EscapeChar('\u20ac', 8364, "&euro;", "euro"));
		set.add(new EscapeChar('\u2122', 8482, "&trade;", "trademark"));
		set.add(new EscapeChar('\u2190', 8592, "&larr;", "left arrow"));
		set.add(new EscapeChar('\u2191', 8593, "&uarr;", "up arrow"));
		set.add(new EscapeChar('\u2192', 8594, "&rarr;", "right arrow"));
		set.add(new EscapeChar('\u2193', 8595, "&darr;", "down arrow"));
		set.add(new EscapeChar('\u2194', 8596, "&harr;", "left right arrow"));
		set.add(new EscapeChar('\u21b5', 8629, "&crarr;",
				"carriage return arrow"));
		set.add(new EscapeChar('\u2308', 8968, "&lceil;", "left ceiling"));
		set.add(new EscapeChar('\u2309', 8969, "&rceil;", "right ceiling"));
		set.add(new EscapeChar('\u230a', 8970, "&lfloor;", "left floor"));
		set.add(new EscapeChar('\u230b', 8971, "&rfloor;", "right floor"));
		set.add(new EscapeChar('\u25ca', 9674, "&loz;", "lozenge"));
		set.add(new EscapeChar('\u2660', 9824, "&spades;", "spade"));
		set.add(new EscapeChar('\u2663', 9827, "&clubs;", "club"));
		set.add(new EscapeChar('\u2665', 9829, "&hearts;", "heart"));
		set.add(new EscapeChar('\u2666', 9830, "&diams;", "diamond"));
		set.add(new EscapeChar('\u2200', 8704, "&forall;", "for all"));
		set.add(new EscapeChar('\u2202', 8706, "&part;", "part"));
		set.add(new EscapeChar('\u2203', 8707, "&exist;", "exists"));
		set.add(new EscapeChar('\u2205', 8709, "&empty;", "empty"));
		set.add(new EscapeChar('\u2207', 8711, "&nabla;", "nabla"));
		set.add(new EscapeChar('\u2208', 8712, "&isin;", "isin"));
		set.add(new EscapeChar('\u2209', 8713, "&notin;", "notin"));
		set.add(new EscapeChar('\u220b', 8715, "&ni;", "ni"));
		set.add(new EscapeChar('\u220f', 8719, "&prod;", "prod"));
		set.add(new EscapeChar('\u2211', 8721, "&sum;", "sum"));
		set.add(new EscapeChar('\u2212', 8722, "&minus;", "minus"));
		set.add(new EscapeChar('\u2217', 8727, "&lowast;", "lowast"));
		set.add(new EscapeChar('\u221a', 8730, "&radic;", "square root"));
		set.add(new EscapeChar('\u221d', 8733, "&prop;", "proportional to"));
		set.add(new EscapeChar('\u221e', 8734, "&infin;", "infinity"));
		set.add(new EscapeChar('\u2220', 8736, "&ang;", "angle"));
		set.add(new EscapeChar('\u2227', 8743, "&and;", "and"));
		set.add(new EscapeChar('\u2228', 8744, "&or;", "or"));
		set.add(new EscapeChar('\u2229', 8745, "&cap;", "cap"));
		set.add(new EscapeChar('\u222a', 8746, "&cup;", "cup"));
		set.add(new EscapeChar('\u222b', 8747, "&int;", "integral"));
		set.add(new EscapeChar('\u2234', 8756, "&there4;", "therefore"));
		set.add(new EscapeChar('\u223c', 8764, "&sim;", "similar to"));
		set.add(new EscapeChar('\u2245', 8773, "&cong;", "congruent to"));
		set.add(new EscapeChar('\u2248', 8776, "&asymp;", "almost equal"));
		set.add(new EscapeChar('\u2260', 8800, "&ne;", "not equal"));
		set.add(new EscapeChar('\u2261', 8801, "&equiv;", "equivalent"));
		set.add(new EscapeChar('\u2264', 8804, "&le;", "less or equal"));
		set.add(new EscapeChar('\u2265', 8805, "&ge;", "greater or equal"));
		set.add(new EscapeChar('\u2282', 8834, "&sub;", "subset of"));
		set.add(new EscapeChar('\u2283', 8835, "&sup;", "superset of"));
		set.add(new EscapeChar('\u2284', 8836, "&nsub;", "not subset of"));
		set.add(new EscapeChar('\u2286', 8838, "&sube;", "subset or equal"));
		set.add(new EscapeChar('\u2287', 8839, "&supe;", "superset or equal"));
		set.add(new EscapeChar('\u2295', 8853, "&oplus;", "circled plus"));
		set.add(new EscapeChar('\u2297', 8855, "&otimes;", "circled times"));
		set.add(new EscapeChar('\u22a5', 8869, "&perp;", "perpendicular"));
		set.add(new EscapeChar('\u22c5', 8901, "&sdot;", "dot operator"));
		set.add(new EscapeChar('\u0391', 913, "&Alpha;", "Alpha"));
		set.add(new EscapeChar('\u0392', 914, "&Beta;", "Beta"));
		set.add(new EscapeChar('\u0393', 915, "&Gamma;", "Gamma"));
		set.add(new EscapeChar('\u0394', 916, "&Delta;", "Delta"));
		set.add(new EscapeChar('\u0395', 917, "&Epsilon;", "Epsilon"));
		set.add(new EscapeChar('\u0396', 918, "&Zeta;", "Zeta"));
		set.add(new EscapeChar('\u0397', 919, "&Eta;", "Eta"));
		set.add(new EscapeChar('\u0398', 920, "&Theta;", "Theta"));
		set.add(new EscapeChar('\u0399', 921, "&Iota;", "Iota"));
		set.add(new EscapeChar('\u039a', 922, "&Kappa;", "Kappa"));
		set.add(new EscapeChar('\u039b', 923, "&Lambda;", "Lambda"));
		set.add(new EscapeChar('\u039c', 924, "&Mu;", "Mu"));
		set.add(new EscapeChar('\u039d', 925, "&Nu;", "Nu"));
		set.add(new EscapeChar('\u039e', 926, "&Xi;", "Xi"));
		set.add(new EscapeChar('\u039f', 927, "&Omicron;", "Omicron"));
		set.add(new EscapeChar('\u03a0', 928, "&Pi;", "Pi"));
		set.add(new EscapeChar('\u03a1', 929, "&Rho;", "Rho"));
		set.add(new EscapeChar('\u03a3', 931, "&Sigma;", "Sigma"));
		set.add(new EscapeChar('\u03a4', 932, "&Tau;", "Tau"));
		set.add(new EscapeChar('\u03a5', 933, "&Upsilon;", "Upsilon"));
		set.add(new EscapeChar('\u03a6', 934, "&Phi;", "Phi"));
		set.add(new EscapeChar('\u03a7', 935, "&Chi;", "Chi"));
		set.add(new EscapeChar('\u03a8', 936, "&Psi;", "Psi"));
		set.add(new EscapeChar('\u03a9', 937, "&Omega;", "Omega"));
		set.add(new EscapeChar('\u03b1', 945, "&alpha;", "alpha"));
		set.add(new EscapeChar('\u03b2', 946, "&beta;", "beta"));
		set.add(new EscapeChar('\u03b3', 947, "&gamma;", "gamma"));
		set.add(new EscapeChar('\u03b4', 948, "&delta;", "delta"));
		set.add(new EscapeChar('\u03b5', 949, "&epsilon;", "epsilon"));
		set.add(new EscapeChar('\u03b6', 950, "&zeta;", "zeta"));
		set.add(new EscapeChar('\u03b7', 951, "&eta;", "eta"));
		set.add(new EscapeChar('\u03b8', 952, "&theta;", "theta"));
		set.add(new EscapeChar('\u03b9', 953, "&iota;", "iota"));
		set.add(new EscapeChar('\u03ba', 954, "&kappa;", "kappa"));
		set.add(new EscapeChar('\u03bb', 955, "&lambda;", "lambda"));
		set.add(new EscapeChar('\u03bc', 956, "&mu;", "mu"));
		set.add(new EscapeChar('\u03bd', 957, "&nu;", "nu"));
		set.add(new EscapeChar('\u03be', 958, "&xi;", "xi"));
		set.add(new EscapeChar('\u03bf', 959, "&omicron;", "omicron"));
		set.add(new EscapeChar('\u03c0', 960, "&pi;", "pi"));
		set.add(new EscapeChar('\u03c1', 961, "&rho;", "rho"));
		set.add(new EscapeChar('\u03c2', 962, "&sigmaf;", "sigmaf"));
		set.add(new EscapeChar('\u03c3', 963, "&sigma;", "sigma"));
		set.add(new EscapeChar('\u03c4', 964, "&tau;", "tau"));
		set.add(new EscapeChar('\u03c5', 965, "&upsilon;", "upsilon"));
		set.add(new EscapeChar('\u03c6', 966, "&phi;", "phi"));
		set.add(new EscapeChar('\u03c7', 967, "&chi;", "chi"));
		set.add(new EscapeChar('\u03c8', 968, "&psi;", "psi"));
		set.add(new EscapeChar('\u03c9', 969, "&omega;", "omega"));
		set.add(new EscapeChar('\u03d1', 977, "&thetasym;", "theta symbol"));
		set.add(new EscapeChar('\u03d2', 978, "&upsih;", "upsilon symbol"));
		set.add(new EscapeChar('\u03d6', 982, "&piv;", "pi symbol"));

		Iterator<EscapeChar> i = set.iterator();
		while (i.hasNext()) {
			EscapeChar c = i.next();
			entityNameMap.put(c.entityName, c);
			charMap.put(c.ch, c);
		}
	}

	/**
	 * Encode a string using HTML escape entities
	 */
	public static String encode(String unencodedString) {
		// don't allocate memory for sb if we don't need to:
		StringBuffer sb = null;
		int length = unencodedString.length();
		for (int a = 0; a < length; a++) {
			char c = unencodedString.charAt(a);
			EscapeChar ec = charMap.get(new Character(c));
			if (ec != null || c >= 255) {
				if (sb == null) {
					sb = new StringBuffer();
					if (a != 0)
						sb.append(unencodedString.substring(0, a));
				}
			}

			if (ec != null) {
				sb.append("&#" + ec.entityNumber + ";");
			} else if (c >= 255) {
				sb.append("&x" + Integer.toHexString(c) + ";");
			} else if (sb != null) {
				sb.append(c);
			}
		}

		if (sb == null)
			return unencodedString;
		return sb.toString();
	}

	/**
	 * Decode a string that uses HTML escape entities.
	 * 
	 */
	public static String decode(String encodedString) {
		// don't allocate memory for sb if we don't need to:
		StringBuffer sb = null;
		int length = encodedString.length();
		for (int a = 0; a < length; a++) {
			char c = encodedString.charAt(a);
			if (c == '&') {
				if (sb == null) {
					sb = new StringBuffer();
					if (a != 0)
						sb.append(encodedString.substring(0, a));
				}
				c = encodedString.charAt(a + 1);
				int endIndex = encodedString.indexOf(';', a);
				if (c == '#') {
					c = encodedString.charAt(a + 2);
					int i;
					if (c == 'x') {
						i = Integer.parseInt(
								encodedString.substring(a + 3, endIndex), 16);
					} else {
						i = Integer.parseInt(
								encodedString.substring(a + 2, endIndex), 10);
					}
					sb.append((char) i);
				} else {
					EscapeChar ch = entityNameMap.get(encodedString.substring(
							a, endIndex + 1));
					if (ch == null) {
						sb.append("?");
					} else {
						sb.append(ch.ch);
					}
				}
				a = endIndex;
			} else if (sb != null) {
				sb.append(c);
			}
		}
		if (sb == null)
			return encodedString;
		return sb.toString();
	}
}