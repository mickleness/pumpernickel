package com.pump.text.html.css.border;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pump.text.html.css.CssLength;
import com.pump.text.html.css.CssParserUtils;
import com.pump.text.html.css.CssPropertyParser;

/**
 * The border-radius CSS property rounds the corners of an element's outer
 * border edge. You can set a single radius to make circular corners, or two
 * radii to make elliptical corners.
 * <p>
 * The radius applies to the whole background, even if the element has no
 * border; the exact position of the clipping is defined by the background-clip
 * property.
 * <p>
 * There are multiple syntaxes:
 * <ul>
 * <li>One term. The radius is set for all 4 sides.</li>
 * <li>Two terms. The first term applies to the top-left and bottom-right
 * corner, and the second term applies to the top-right and bottom-left
 * corner.</li>
 * <li>Three terms. The first term applies to the top-left corner. The second
 * term applies to the top-right and bottom-left corner. The third term applies
 * to the bottom-right corner.</li>
 * <li>Four terms. In this case the terms would apply to the top-left,
 * top-right, bottom-right and bottom-left corners, in that order.</li>
 * </ul>
 * <p>
 * Additionally after 1-4 terms there could be a "/" followed by 1-4 more terms.
 * When this syntax is used the left hand terms are used to specify the
 * horizontal corner attributes, and the right hand terms are used to specify
 * the vertical corner attributes.
 * <p>
 * This parser always produces a List of four CssBorderRadiusValue (top-left,
 * top-right, bottom-right, bottom-left) no matter what syntax is used.
 */
public class CssBorderRadiusParser
		implements CssPropertyParser<List<CssBorderRadiusValue>> {

	public static final String PROPERTY_BORDER_RADIUS = "border-radius";

	@Override
	public String getPropertyName() {
		return PROPERTY_BORDER_RADIUS;
	}

	@Override
	public List<CssBorderRadiusValue> parse(String cssString) {
		List<CssBorderRadiusValue> returnValue = new ArrayList<>(4);

		List<Object> terms = CssParserUtils.parseLengthsAndStrings(cssString,
				Collections.singleton("/"));
		int i = terms.indexOf("/");

		CssLength topLeftHoriz, topLeftVert, topRightHoriz, topRightVert,
				bottomRightHoriz, bottomRightVert, bottomLeftHoriz,
				bottomLeftVert;

		if (i == -1) {
			if (terms.size() == 1) {
				topLeftHoriz = topLeftVert = topRightHoriz = topRightVert = bottomRightHoriz = bottomRightVert = bottomLeftHoriz = bottomLeftVert = (CssLength) terms
						.get(0);
			} else if (terms.size() == 2) {
				topLeftHoriz = topLeftVert = bottomRightHoriz = bottomRightVert = (CssLength) terms
						.get(0);
				topRightHoriz = topRightVert = bottomLeftHoriz = bottomLeftVert = (CssLength) terms
						.get(1);
			} else if (terms.size() == 3) {
				topLeftHoriz = topLeftVert = (CssLength) terms.get(0);
				topRightHoriz = topRightVert = bottomLeftHoriz = bottomLeftVert = (CssLength) terms
						.get(1);
				bottomRightHoriz = bottomRightVert = (CssLength) terms.get(2);
			} else if (terms.size() == 4) {
				topLeftHoriz = topLeftVert = (CssLength) terms.get(0);
				topRightHoriz = topRightVert = (CssLength) terms.get(1);
				bottomRightHoriz = bottomRightVert = (CssLength) terms.get(2);
				bottomLeftHoriz = bottomLeftVert = (CssLength) terms.get(3);
			} else {
				throw new IllegalArgumentException(
						"Expected 1-4 terms in \"" + cssString + "\"");
			}
		} else {
			List<Object> horizontalTerms = terms.subList(0, i - 1);
			List<Object> verticalTerms = terms.subList(i + 1, terms.size() - 1);

			if (terms.size() == 1) {
				topLeftHoriz = topRightHoriz = bottomRightHoriz = bottomLeftHoriz = (CssLength) horizontalTerms
						.get(0);
			} else if (terms.size() == 2) {
				topLeftHoriz = bottomRightHoriz = (CssLength) horizontalTerms
						.get(0);
				topRightHoriz = bottomLeftHoriz = (CssLength) horizontalTerms
						.get(1);
			} else if (terms.size() == 3) {
				topLeftHoriz = (CssLength) horizontalTerms.get(0);
				topRightHoriz = bottomLeftHoriz = (CssLength) horizontalTerms
						.get(1);
				bottomRightHoriz = (CssLength) horizontalTerms.get(2);
			} else if (terms.size() == 4) {
				topLeftHoriz = (CssLength) horizontalTerms.get(0);
				topRightHoriz = (CssLength) horizontalTerms.get(1);
				bottomRightHoriz = (CssLength) horizontalTerms.get(2);
				bottomLeftHoriz = (CssLength) horizontalTerms.get(3);
			} else {
				throw new IllegalArgumentException(
						"Expected 1-4 horizontal terms in \"" + cssString
								+ "\"");
			}

			if (terms.size() == 1) {
				topLeftVert = topRightVert = bottomRightVert = bottomLeftVert = (CssLength) verticalTerms
						.get(0);
			} else if (terms.size() == 2) {
				topLeftVert = bottomRightVert = (CssLength) verticalTerms
						.get(0);
				topRightVert = bottomLeftVert = (CssLength) verticalTerms
						.get(1);
			} else if (terms.size() == 3) {
				topLeftVert = (CssLength) verticalTerms.get(0);
				topRightVert = bottomLeftVert = (CssLength) verticalTerms
						.get(1);
				bottomRightVert = (CssLength) verticalTerms.get(2);
			} else if (terms.size() == 4) {
				topLeftVert = (CssLength) verticalTerms.get(0);
				topRightVert = (CssLength) verticalTerms.get(1);
				bottomRightVert = (CssLength) verticalTerms.get(2);
				bottomLeftVert = (CssLength) verticalTerms.get(3);
			} else {
				throw new IllegalArgumentException(
						"Expected 1-4 vertical terms in \"" + cssString + "\"");
			}
		}

		returnValue.add(new CssBorderRadiusValue(topLeftHoriz, topLeftVert));
		returnValue.add(new CssBorderRadiusValue(topRightHoriz, topRightVert));
		returnValue.add(
				new CssBorderRadiusValue(bottomRightHoriz, bottomRightVert));
		returnValue
				.add(new CssBorderRadiusValue(bottomLeftHoriz, bottomLeftVert));

		return returnValue;
	}

}
