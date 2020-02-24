package com.pump.animation.quicktime.atom;

/**
 * This is a collection of known meta text attributes in quicktime data.
 * <p>
 * This combines attributes from the QuickTime file specification and the MP4
 * file specification, and attributes observed in files that I haven't observed
 * formally in documentation. A text type can be available in either, both, or
 * neither specification.
 * <p>
 * This is not necessarily a complete list; this is just a guide of what should
 * be common types to design for.
 */
public enum TextTypes {
	// @formatter:off
	GROUPING("grp", false, false),
	GENRE("gen", false, false),
	LYRICS("lyr", false, false),
	NAME_OR_TITLE("nam", true, true), 
	COMMENT("cmt", false, true), 
	CONTENT_CREATED_YEAR("day", true, true), 
	ARTIST("ART", false, true), 
	TRACK("trk", false, true), 
	ALBUM("alb", false, true), 
	COMPOSER("com", true, true), 
	WRITER("wrt", true, true), 
	ENCODER("too", false, true), 
	ARRANGER_NAME("arg", true, false), 
	ARRANGER_KEYWORDS("ark", true, false), 
	COMPOSER_KEYWORDS("cok", true, false), 
	COPYRIGHT_STATEMENT("cpy", true, false), 
	MOVIE_DIRECTOR("dir", true, false), 
	EDIT_DATE_OR_DESCRIPTION_1("ed1", true, false), 
	EDIT_DATE_OR_DESCRIPTION_2("ed2", true, false), 
	EDIT_DATE_OR_DESCRIPTION_3("ed3", true, false), 
	EDIT_DATE_OR_DESCRIPTION_4("ed4", true, false), 
	EDIT_DATE_OR_DESCRIPTION_5("ed5", true, false), 
	EDIT_DATE_OR_DESCRIPTION_6("ed6", true, false), 
	EDIT_DATE_OR_DESCRIPTION_7("ed7", true, false), 
	EDIT_DATE_OR_DESCRIPTION_8("ed8", true, false), 
	EDIT_DATE_OR_DESCRIPTION_9("ed9", true, false), 
	MOVIE_FORMAT("fmt", true, false), 
	INFORMATION("inf", true, false), 
	ISRC_CODE("isr", true, false), 
	RECORD_LABEL("lab", true, false), 
	RECORD_LABEL_URL("lal", true, false), 
	FILE_CREATOR_OR_MAKER("mak", true, false), 
	FILE_CREATOR_OR_MAKER_URL("mal", true, false), 
	TITLE_KEYWORDS("nak", true, false), 
	PRODUCER_KEYWORDS("pdk", true, false), 
	RECORDING_COPYRIGHT_STATEMENT("phg", true, false), 
	PRODUCER("prd", true, false), 
	PERFORMERS("prf", true, false), 
	MAIN_ARTIST_AND_PERFORMERS_KEYWORDS("prk", true, false), 
	MAIN_ARTIST_AND_PERFORMERS_URL("prl", true, false), 
	HARDWARE_AND_SOFTWARE_REQUIREMENTS("req", true, false), 
	SUBTITLE_KEYWORDS("snk", true, false), 
	SUBTITLE_OF_CONTENT("snm", true, false), 
	CREDITS("src", true, false), 
	SONGWRITER_NAME("swf", true, false), 
	SONGWRITER_KEYWORDS("swk", true, false), 
	NAME_AND_VERSION_OF_SOFTWARE_CREATOR("swr", true, false);
	// @formatter:on

	String code;
	boolean qtff, mp4;

	TextTypes(String code, boolean qtff, boolean mp4) {
		this.code = code;
		this.qtff = qtff;
		this.mp4 = mp4;
	}

	/**
	 * Return the 3-letter text code that identifies this in the file. (This is
	 * written in the file preceded by a '©' character, so "cmt" is identified
	 * in the file as "©cmt")
	 */
	public String getCode() {
		return code;
	}

	/**
	 * If true then this property was described in the <a href=
	 * "https://developer.apple.com/library/archive/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html#//apple_ref/doc/uid/TP40000939-CH204-SW1"
	 * >QuickTime file format specification</a>.
	 * 
	 * @return
	 */
	public boolean isQT() {
		return qtff;
	}

	/**
	 * If true then this property was described in the <a
	 * href="http://xhelmboyx.tripod.com/formats/mp4-layout.txt">mp4 file format
	 * specification</a>.
	 */
	public boolean isMP4() {
		return mp4;
	}
}
