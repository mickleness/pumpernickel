/**
 * The breakout project consolidates several java source code files into one
 * file.
 * <p>
 * This uses a seldom-used rule: java files can contain unlimited number of
 * definitions, as long as only one of those definitions is public.
 * <p>
 * Related reading <a href="http://stackoverflow.com/questions/2336692/java-multiple-class-declarations-in-one-file">here</a>
 * and <a href="http://stackoverflow.com/questions/968347/can-a-java-file-have-more-than-one-class">here</a>.
 * <p>
 * In general this practice is discouraged. If you don't have a compelling reason to use
 * this tool then you shouldn't.
 * <p>
 * This tool takes a primary java file, analyzes it, and searches for as many supporting
 * java files as it can. It creates a self-contained file that contains all possible
 * class definitions.
 * <p>
 * If you're in an environment where you can submit a java file but you need it to contain
 * a potentially complex architecture -- or a snapshot of an architecture -- this tool will
 * help you get there.
 */
package com.pump.breakout;