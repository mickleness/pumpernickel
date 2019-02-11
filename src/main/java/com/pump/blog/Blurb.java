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
package com.pump.blog;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This is used by the BlurbIndexBuilder to write a "blurb" for display on a
 * webpage.
 * <p>
 * This was made possible with the help of <a
 * href="http://tutorials.jenkov.com/java-reflection/annotations.html">this
 * article</a>.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Blurb {

	/**
	 * This is the short human-readable title of this blurb. This usually is
	 * exactly the same as a blog article's title.
	 */
	String title();

	/** A short string identifying the release date of this project. */
	String releaseDate();

	/**
	 * This is one or two short paragraphs that appear below the title of this
	 * blurb. This should give a short summary/preview of the article identified
	 * in the "link" attribute.
	 */
	String summary();

	/**
	 * This optional attribute links to an article further discussing this
	 * project.
	 */
	String article() default "";

	/** This optional attribute is the image file name shown on the index page. */
	String imageName() default "";
}