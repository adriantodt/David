/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [02/11/16 15:19]
 */

package cf.adriantodt.utils;

import java.util.function.Function;
import java.util.regex.Matcher;

public class MatcherUtils {
	private static final Function<Matcher,CharSequence> MATCHER_TEXT = ReflectionEasyAsFuck.Virtual.getField(Matcher.class, "text", CharSequence.class);

	public static Function<Matcher,String> replaceAll(Function<String, String> replacement) {
		return matcher -> replaceAll(matcher,replacement);
	}

	public static String replaceAll(Matcher matcher, Function<String, String> replacement) {
		matcher.reset();
		boolean result = matcher.find();
		if (result) {
			StringBuffer sb = new StringBuffer();
			do {
				matcher.appendReplacement(sb, replacement.apply(matcher.group()));
				result = matcher.find();
			} while (result);
			matcher.appendTail(sb);
			return sb.toString();
		}
		return MATCHER_TEXT.apply(matcher).toString();
	}
}