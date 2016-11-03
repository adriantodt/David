/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [02/11/16 13:39]
 */

package cf.adriantodt.utils;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.quote;

public class PatternCollection {
	public static final Pattern
		HTML_TO_PLAIN = Pattern.compile("(?s)<[^>]*>(\\s*<[^>]*>)*"),
		MULTI_TO_SINGLE_LINE = Pattern.compile("(\\r?\\n)+"),
		UNNECESSARY_NEWLINE_END = Pattern.compile("(\\r?\\n)+$"),
		UNNECESSARY_NEWLINE_START = Pattern.compile("^(\\r?\\n)+"),
		INSIDE_HTML_TAG = Pattern.compile(">[\\S\\s]+?<");

	public static Pattern compileForHTMLTag(String tag) {
		tag = quote(tag);
		return Pattern.compile("<\\/?"+tag+"[^>]*>");
	}

	public static Pattern compileForHTMLContents(String tag) {
		tag = quote(tag);
		return Pattern.compile("<"+tag+"[^>]*>[\\S\\s]+?<\\/"+tag+">");
	}

	public static Function<String, String> compileReplace(Pattern pattern, String replace) {
		return s -> pattern.matcher(s).replaceAll(replace);
	}

	public static Function<String, String> compileReplace(Pattern pattern, Function<Matcher,String> replace) {
		return s -> replace.apply(pattern.matcher(s));
	}
}
