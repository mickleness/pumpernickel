/**
 * This software is released as part of the Pumpernickel project.
 * <p>
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * <p>
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.io.parser.html;

import java.util.HashMap;
import java.util.HashSet;
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

			if (!entityName.startsWith("&"))
				throw new IllegalArgumentException(entityName);
			if (!entityName.endsWith(";"))
				throw new IllegalArgumentException(entityName);
		}

		@Override
		public String toString() {
			return "EscapeChar[ ch='" + ch + "', entityNumber="
					+ entityNumber + ", entityName=" + entityName
					+ ", description=" + description + " ]";
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof EscapeChar c))
				return false;
			return c.entityNumber == entityNumber;
		}

		@Override
		public int hashCode() {
			return entityNumber;
		}
	}

	private static final Map<String, EscapeChar> entityNameMap = new HashMap<>();
	private static final Map<Character, EscapeChar> charMap = new HashMap<>();
	static {
		Set<EscapeChar> set = new HashSet<>();
		set.add(new EscapeChar('À', 192, "&Agrave;",
				"capital a, grave accent"));
		set.add(new EscapeChar('"', 34, "&quot;", "quotation mark"));
		set.add(new EscapeChar('\'', 39, "&apos;", "apostrophe"));
		set.add(new EscapeChar('&', 38, "&amp;", "ampersand"));
		set.add(new EscapeChar('<', 60, "&lt;", "less-than"));
		set.add(new EscapeChar('>', 62, "&gt;", "greater-than"));
		set.add(new EscapeChar('Á', 193, "&Aacute;",
				"capital a, acute accent"));
		set.add(new EscapeChar('Â', 194, "&Acirc;",
				"capital a, circumflex accent"));
		set.add(new EscapeChar('Ã', 195, "&Atilde;", "capital a, tilde"));
		set.add(new EscapeChar('Ä', 196, "&Auml;",
				"capital a, umlaut mark"));
		set.add(new EscapeChar('Å', 197, "&Aring;", "capital a, ring"));
		set.add(new EscapeChar('Æ', 198, "&AElig;", "capital ae"));
		set.add(new EscapeChar('Ç', 199, "&Ccedil;", "capital c, cedilla"));
		set.add(new EscapeChar('È', 200, "&Egrave;",
				"capital e, grave accent"));
		set.add(new EscapeChar('É', 201, "&Eacute;",
				"capital e, acute accent"));
		set.add(new EscapeChar('Ê', 202, "&Ecirc;",
				"capital e, circumflex accent"));
		set.add(new EscapeChar('Ë', 203, "&Euml;",
				"capital e, umlaut mark"));
		set.add(new EscapeChar('Ì', 204, "&Igrave;",
				"capital i, grave accent"));
		set.add(new EscapeChar('Í', 205, "&Iacute;",
				"capital i, acute accent"));
		set.add(new EscapeChar('Î', 206, "&Icirc;",
				"capital i, circumflex accent"));
		set.add(new EscapeChar('Ï', 207, "&Iuml;",
				"capital i, umlaut mark"));
		set.add(new EscapeChar('Ð', 208, "&ETH;", "capital eth, Icelandic"));
		set.add(new EscapeChar('Ñ', 209, "&Ntilde;", "capital n, tilde"));
		set.add(new EscapeChar('Ò', 210, "&Ograve;",
				"capital o, grave accent"));
		set.add(new EscapeChar('Ó', 211, "&Oacute;",
				"capital o, acute accent"));
		set.add(new EscapeChar('Ô', 212, "&Ocirc;",
				"capital o, circumflex accent"));
		set.add(new EscapeChar('Õ', 213, "&Otilde;", "capital o, tilde"));
		set.add(new EscapeChar('Ö', 214, "&Ouml;",
				"capital o, umlaut mark"));
		set.add(new EscapeChar('Ø', 216, "&Oslash;", "capital o, slash"));
		set.add(new EscapeChar('Ù', 217, "&Ugrave;",
				"capital u, grave accent"));
		set.add(new EscapeChar('Ú', 218, "&Uacute;",
				"capital u, acute accent"));
		set.add(new EscapeChar('Û', 219, "&Ucirc;",
				"capital u, circumflex accent"));
		set.add(new EscapeChar('Ü', 220, "&Uuml;",
				"capital u, umlaut mark"));
		set.add(new EscapeChar('Ý', 221, "&Yacute;",
				"capital y, acute accent"));
		set.add(new EscapeChar('Þ', 222, "&THORN;",
				"capital THORN, Icelandic"));
		set.add(new EscapeChar('ß', 223, "&szlig;",
				"small sharp s, German"));
		set.add(new EscapeChar('à', 224, "&agrave;",
				"small a, grave accent"));
		set.add(new EscapeChar('á', 225, "&aacute;",
				"small a, acute accent"));
		set.add(new EscapeChar('â', 226, "&acirc;",
				"small a, circumflex accent"));
		set.add(new EscapeChar('ã', 227, "&atilde;", "small a, tilde"));
		set.add(new EscapeChar('ä', 228, "&auml;", "small a, umlaut mark"));
		set.add(new EscapeChar('å', 229, "&aring;", "small a, ring"));
		set.add(new EscapeChar('æ', 230, "&aelig;", "small ae"));
		set.add(new EscapeChar('ç', 231, "&ccedil;", "small c, cedilla"));
		set.add(new EscapeChar('è', 232, "&egrave;",
				"small e, grave accent"));
		set.add(new EscapeChar('é', 233, "&eacute;",
				"small e, acute accent"));
		set.add(new EscapeChar('ê', 234, "&ecirc;",
				"small e, circumflex accent"));
		set.add(new EscapeChar('ë', 235, "&euml;", "small e, umlaut mark"));
		set.add(new EscapeChar('ì', 236, "&igrave;",
				"small i, grave accent"));
		set.add(new EscapeChar('í', 237, "&iacute;",
				"small i, acute accent"));
		set.add(new EscapeChar('î', 238, "&icirc;",
				"small i, circumflex accent"));
		set.add(new EscapeChar('ï', 239, "&iuml;", "small i, umlaut mark"));
		set.add(new EscapeChar('ð', 240, "&eth;", "small eth, Icelandic"));
		set.add(new EscapeChar('ñ', 241, "&ntilde;", "small n, tilde"));
		set.add(new EscapeChar('ò', 242, "&ograve;",
				"small o, grave accent"));
		set.add(new EscapeChar('ó', 243, "&oacute;",
				"small o, acute accent"));
		set.add(new EscapeChar('ô', 244, "&ocirc;",
				"small o, circumflex accent"));
		set.add(new EscapeChar('õ', 245, "&otilde;", "small o, tilde"));
		set.add(new EscapeChar('ö', 246, "&ouml;", "small o, umlaut mark"));
		set.add(new EscapeChar('ø', 248, "&oslash;", "small o, slash"));
		set.add(new EscapeChar('ù', 249, "&ugrave;",
				"small u, grave accent"));
		set.add(new EscapeChar('ú', 250, "&uacute;",
				"small u, acute accent"));
		set.add(new EscapeChar('û', 251, "&ucirc;",
				"small u, circumflex accent"));
		set.add(new EscapeChar('ü', 252, "&uuml;", "small u, umlaut mark"));
		set.add(new EscapeChar('ý', 253, "&yacute;",
				"small y, acute accent"));
		set.add(new EscapeChar('þ', 254, "&thorn;",
				"small thorn, Icelandic"));
		set.add(new EscapeChar('ÿ', 255, "&yuml;", "small y, umlaut mark"));
		set.add(new EscapeChar(' ', 160, "&nbsp;", "non-breaking space"));
		set.add(new EscapeChar('¡', 161, "&iexcl;",
				"inverted exclamation mark"));
		set.add(new EscapeChar('¢', 162, "&cent;", "cent"));
		set.add(new EscapeChar('£', 163, "&pound;", "pound"));
		set.add(new EscapeChar('¤', 164, "&curren;", "currency"));
		set.add(new EscapeChar('¥', 165, "&yen;", "yen"));
		set.add(new EscapeChar('¦', 166, "&brvbar;", "broken vertical bar"));
		set.add(new EscapeChar('§', 167, "&sect;", "section"));
		set.add(new EscapeChar('¨', 168, "&uml;", "spacing diaeresis"));
		set.add(new EscapeChar('©', 169, "&copy;", "copyright"));
		set.add(new EscapeChar('ª', 170, "&ordf;",
				"feminine ordinal indicator"));
		set.add(new EscapeChar('«', 171, "&laquo;",
				"angle quotation mark (left)"));
		set.add(new EscapeChar('¬', 172, "&not;", "negation"));
		set.add(new EscapeChar('\u00ad', 173, "&shy;", "soft hyphen"));
		set.add(new EscapeChar('®', 174, "&reg;", "registered trademark"));
		set.add(new EscapeChar('¯', 175, "&macr;", "spacing macron"));
		set.add(new EscapeChar('°', 176, "&deg;", "degree"));
		set.add(new EscapeChar('±', 177, "&plusmn;", "plus-or-minus"));
		set.add(new EscapeChar('²', 178, "&sup2;", "superscript 2"));
		set.add(new EscapeChar('³', 179, "&sup3;", "superscript 3"));
		set.add(new EscapeChar('´', 180, "&acute;", "spacing acute"));
		set.add(new EscapeChar('µ', 181, "&micro;", "micro"));
		set.add(new EscapeChar('¶', 182, "&para;", "paragraph"));
		set.add(new EscapeChar('·', 183, "&middot;", "middle dot"));
		set.add(new EscapeChar('¸', 184, "&cedil;", "spacing cedilla"));
		set.add(new EscapeChar('¹', 185, "&sup1;", "superscript 1"));
		set.add(new EscapeChar('º', 186, "&ordm;",
				"masculine ordinal indicator"));
		set.add(new EscapeChar('»', 187, "&raquo;",
				"angle quotation mark (right)"));
		set.add(new EscapeChar('¼', 188, "&frac14;", "fraction 1/4"));
		set.add(new EscapeChar('½', 189, "&frac12;", "fraction 1/2"));
		set.add(new EscapeChar('¾', 190, "&frac34;", "fraction 3/4"));
		set.add(new EscapeChar('¿', 191, "&iquest;",
				"inverted question mark"));
		set.add(new EscapeChar('×', 215, "&times;", "multiplication"));
		set.add(new EscapeChar('÷', 247, "&divide;", "division"));
		set.add(new EscapeChar('Œ', 338, "&OElig;", "capital ligature OE"));
		set.add(new EscapeChar('œ', 339, "&oelig;", "small ligature oe"));
		set.add(new EscapeChar('Š', 352, "&Scaron;",
				"capital S with caron"));
		set.add(new EscapeChar('š', 353, "&scaron;", "small S with caron"));
		set.add(new EscapeChar('Ÿ', 376, "&Yuml;",
				"capital Y with diaeres"));
		set.add(new EscapeChar('ƒ', 402, "&fnof;", "f with hook"));
		set.add(new EscapeChar('ˆ', 710, "&circ;",
				"modifier letter circumflex accent"));
		set.add(new EscapeChar('˜', 732, "&tilde;", "small tilde"));
		set.add(new EscapeChar('\u2002', 8194, "&ensp;", "en space"));
		set.add(new EscapeChar('\u2003', 8195, "&emsp;", "em space"));
		set.add(new EscapeChar('\u2009', 8201, "&thinsp;", "thin space"));
		set.add(new EscapeChar('\u200c', 8204, "&zwnj;",
				"zero width non-joiner"));
		set.add(new EscapeChar('\u200d', 8205, "&zwj;", "zero width joiner"));
		set.add(new EscapeChar('\u200e', 8206, "&lrm;", "left-to-right mark"));
		set.add(new EscapeChar('\u200f', 8207, "&rlm;", "right-to-left mark"));
		set.add(new EscapeChar('–', 8211, "&ndash;", "en dash"));
		set.add(new EscapeChar('—', 8212, "&mdash;", "em dash"));
		set.add(new EscapeChar('‘', 8216, "&lsquo;",
				"left single quotation mark"));
		set.add(new EscapeChar('’', 8217, "&rsquo;",
				"right single quotation mark"));
		set.add(new EscapeChar('‚', 8218, "&sbquo;",
				"single low-9 quotation mark"));
		set.add(new EscapeChar('“', 8220, "&ldquo;",
				"left double quotation mark"));
		set.add(new EscapeChar('”', 8221, "&rdquo;",
				"right double quotation mark"));
		set.add(new EscapeChar('„', 8222, "&bdquo;",
				"double low-9 quotation mark"));
		set.add(new EscapeChar('†', 8224, "&dagger;", "dagger"));
		set.add(new EscapeChar('‡', 8225, "&Dagger;", "double dagger"));
		set.add(new EscapeChar('•', 8226, "&bull;", "bullet"));
		set.add(new EscapeChar('…', 8230, "&hellip;",
				"horizontal ellipsis"));
		set.add(new EscapeChar('‰', 8240, "&permil;", "per mille"));
		set.add(new EscapeChar('′', 8242, "&prime;", "minutes"));
		set.add(new EscapeChar('″', 8243, "&Prime;", "seconds"));
		set.add(new EscapeChar('‹', 8249, "&lsaquo;",
				"single left angle quotation"));
		set.add(new EscapeChar('›', 8250, "&rsaquo;",
				"single right angle quotation"));
		set.add(new EscapeChar('‾', 8254, "&oline;", "overline"));
		set.add(new EscapeChar('€', 8364, "&euro;", "euro"));
		set.add(new EscapeChar('™', 8482, "&trade;", "trademark"));
		set.add(new EscapeChar('←', 8592, "&larr;", "left arrow"));
		set.add(new EscapeChar('↑', 8593, "&uarr;", "up arrow"));
		set.add(new EscapeChar('→', 8594, "&rarr;", "right arrow"));
		set.add(new EscapeChar('↓', 8595, "&darr;", "down arrow"));
		set.add(new EscapeChar('↔', 8596, "&harr;", "left right arrow"));
		set.add(new EscapeChar('↵', 8629, "&crarr;",
				"carriage return arrow"));
		set.add(new EscapeChar('⌈', 8968, "&lceil;", "left ceiling"));
		set.add(new EscapeChar('⌉', 8969, "&rceil;", "right ceiling"));
		set.add(new EscapeChar('⌊', 8970, "&lfloor;", "left floor"));
		set.add(new EscapeChar('⌋', 8971, "&rfloor;", "right floor"));
		set.add(new EscapeChar('◊', 9674, "&loz;", "lozenge"));
		set.add(new EscapeChar('♠', 9824, "&spades;", "spade"));
		set.add(new EscapeChar('♣', 9827, "&clubs;", "club"));
		set.add(new EscapeChar('♥', 9829, "&hearts;", "heart"));
		set.add(new EscapeChar('♦', 9830, "&diams;", "diamond"));
		set.add(new EscapeChar('∀', 8704, "&forall;", "for all"));
		set.add(new EscapeChar('∂', 8706, "&part;", "part"));
		set.add(new EscapeChar('∃', 8707, "&exist;", "exists"));
		set.add(new EscapeChar('∅', 8709, "&empty;", "empty"));
		set.add(new EscapeChar('∇', 8711, "&nabla;", "nabla"));
		set.add(new EscapeChar('∈', 8712, "&isin;", "isin"));
		set.add(new EscapeChar('∉', 8713, "&notin;", "notin"));
		set.add(new EscapeChar('∋', 8715, "&ni;", "ni"));
		set.add(new EscapeChar('∏', 8719, "&prod;", "prod"));
		set.add(new EscapeChar('∑', 8721, "&sum;", "sum"));
		set.add(new EscapeChar('−', 8722, "&minus;", "minus"));
		set.add(new EscapeChar('∗', 8727, "&lowast;", "lowast"));
		set.add(new EscapeChar('√', 8730, "&radic;", "square root"));
		set.add(new EscapeChar('∝', 8733, "&prop;", "proportional to"));
		set.add(new EscapeChar('∞', 8734, "&infin;", "infinity"));
		set.add(new EscapeChar('∠', 8736, "&ang;", "angle"));
		set.add(new EscapeChar('∧', 8743, "&and;", "and"));
		set.add(new EscapeChar('∨', 8744, "&or;", "or"));
		set.add(new EscapeChar('∩', 8745, "&cap;", "cap"));
		set.add(new EscapeChar('∪', 8746, "&cup;", "cup"));
		set.add(new EscapeChar('∫', 8747, "&int;", "integral"));
		set.add(new EscapeChar('∴', 8756, "&there4;", "therefore"));
		set.add(new EscapeChar('∼', 8764, "&sim;", "similar to"));
		set.add(new EscapeChar('≅', 8773, "&cong;", "congruent to"));
		set.add(new EscapeChar('≈', 8776, "&asymp;", "almost equal"));
		set.add(new EscapeChar('≠', 8800, "&ne;", "not equal"));
		set.add(new EscapeChar('≡', 8801, "&equiv;", "equivalent"));
		set.add(new EscapeChar('≤', 8804, "&le;", "less or equal"));
		set.add(new EscapeChar('≥', 8805, "&ge;", "greater or equal"));
		set.add(new EscapeChar('⊂', 8834, "&sub;", "subset of"));
		set.add(new EscapeChar('⊃', 8835, "&sup;", "superset of"));
		set.add(new EscapeChar('⊄', 8836, "&nsub;", "not subset of"));
		set.add(new EscapeChar('⊆', 8838, "&sube;", "subset or equal"));
		set.add(new EscapeChar('⊇', 8839, "&supe;", "superset or equal"));
		set.add(new EscapeChar('⊕', 8853, "&oplus;", "circled plus"));
		set.add(new EscapeChar('⊗', 8855, "&otimes;", "circled times"));
		set.add(new EscapeChar('⊥', 8869, "&perp;", "perpendicular"));
		set.add(new EscapeChar('⋅', 8901, "&sdot;", "dot operator"));
		set.add(new EscapeChar('Α', 913, "&Alpha;", "Alpha"));
		set.add(new EscapeChar('Β', 914, "&Beta;", "Beta"));
		set.add(new EscapeChar('Γ', 915, "&Gamma;", "Gamma"));
		set.add(new EscapeChar('Δ', 916, "&Delta;", "Delta"));
		set.add(new EscapeChar('Ε', 917, "&Epsilon;", "Epsilon"));
		set.add(new EscapeChar('Ζ', 918, "&Zeta;", "Zeta"));
		set.add(new EscapeChar('Η', 919, "&Eta;", "Eta"));
		set.add(new EscapeChar('Θ', 920, "&Theta;", "Theta"));
		set.add(new EscapeChar('Ι', 921, "&Iota;", "Iota"));
		set.add(new EscapeChar('Κ', 922, "&Kappa;", "Kappa"));
		set.add(new EscapeChar('Λ', 923, "&Lambda;", "Lambda"));
		set.add(new EscapeChar('Μ', 924, "&Mu;", "Mu"));
		set.add(new EscapeChar('Ν', 925, "&Nu;", "Nu"));
		set.add(new EscapeChar('Ξ', 926, "&Xi;", "Xi"));
		set.add(new EscapeChar('Ο', 927, "&Omicron;", "Omicron"));
		set.add(new EscapeChar('Π', 928, "&Pi;", "Pi"));
		set.add(new EscapeChar('Ρ', 929, "&Rho;", "Rho"));
		set.add(new EscapeChar('Σ', 931, "&Sigma;", "Sigma"));
		set.add(new EscapeChar('Τ', 932, "&Tau;", "Tau"));
		set.add(new EscapeChar('Υ', 933, "&Upsilon;", "Upsilon"));
		set.add(new EscapeChar('Φ', 934, "&Phi;", "Phi"));
		set.add(new EscapeChar('Χ', 935, "&Chi;", "Chi"));
		set.add(new EscapeChar('Ψ', 936, "&Psi;", "Psi"));
		set.add(new EscapeChar('Ω', 937, "&Omega;", "Omega"));
		set.add(new EscapeChar('α', 945, "&alpha;", "alpha"));
		set.add(new EscapeChar('β', 946, "&beta;", "beta"));
		set.add(new EscapeChar('γ', 947, "&gamma;", "gamma"));
		set.add(new EscapeChar('δ', 948, "&delta;", "delta"));
		set.add(new EscapeChar('ε', 949, "&epsilon;", "epsilon"));
		set.add(new EscapeChar('ζ', 950, "&zeta;", "zeta"));
		set.add(new EscapeChar('η', 951, "&eta;", "eta"));
		set.add(new EscapeChar('θ', 952, "&theta;", "theta"));
		set.add(new EscapeChar('ι', 953, "&iota;", "iota"));
		set.add(new EscapeChar('κ', 954, "&kappa;", "kappa"));
		set.add(new EscapeChar('λ', 955, "&lambda;", "lambda"));
		set.add(new EscapeChar('μ', 956, "&mu;", "mu"));
		set.add(new EscapeChar('ν', 957, "&nu;", "nu"));
		set.add(new EscapeChar('ξ', 958, "&xi;", "xi"));
		set.add(new EscapeChar('ο', 959, "&omicron;", "omicron"));
		set.add(new EscapeChar('π', 960, "&pi;", "pi"));
		set.add(new EscapeChar('ρ', 961, "&rho;", "rho"));
		set.add(new EscapeChar('ς', 962, "&sigmaf;", "sigmaf"));
		set.add(new EscapeChar('σ', 963, "&sigma;", "sigma"));
		set.add(new EscapeChar('τ', 964, "&tau;", "tau"));
		set.add(new EscapeChar('υ', 965, "&upsilon;", "upsilon"));
		set.add(new EscapeChar('φ', 966, "&phi;", "phi"));
		set.add(new EscapeChar('χ', 967, "&chi;", "chi"));
		set.add(new EscapeChar('ψ', 968, "&psi;", "psi"));
		set.add(new EscapeChar('ω', 969, "&omega;", "omega"));
		set.add(new EscapeChar('ϑ', 977, "&thetasym;", "theta symbol"));
		set.add(new EscapeChar('ϒ', 978, "&upsih;", "upsilon symbol"));
		set.add(new EscapeChar('ϖ', 982, "&piv;", "pi symbol"));

		for (EscapeChar c : set) {
			entityNameMap.put(c.entityName, c);
			charMap.put(c.ch, c);
		}
	}

	/**
	 * Encode a string using HTML escape entities
	 */
	public static String encode(String unencodedString) {
		// don't allocate memory for sb if we don't need to:
		StringBuilder sb = null;
		int length = unencodedString.length();
		for (int a = 0; a < length; a++) {
			char c = unencodedString.charAt(a);
			EscapeChar ec = charMap.get(c);
			if (ec != null || c >= 255) {
				if (sb == null) {
					sb = new StringBuilder();
					if (a != 0)
						sb.append(unencodedString, 0, a);
				}
			}

			if (ec != null) {
				sb.append("&#").append(ec.entityNumber).append(";");
			} else if (c >= 255) {
				sb.append("&#x").append(Integer.toHexString(c)).append(";");
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
		StringBuilder sb = null;
		int length = encodedString.length();
		for (int a = 0; a < length; a++) {
			char c = encodedString.charAt(a);
			if (c == '&') {
				if (sb == null) {
					sb = new StringBuilder();
					if (a != 0)
						sb.append(encodedString, 0, a);
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