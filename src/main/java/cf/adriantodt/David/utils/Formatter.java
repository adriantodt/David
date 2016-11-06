/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [30/09/16 08:33]
 */

package cf.adriantodt.David.utils;

public class Formatter {
	public static String encase(String content) {
		return encase(content, "");
	}

	public static String encase(String content, String language) {
		return "```" + language + "\n" + content + "\n```";
	}

	public static String italic(String content) {
		return "*" + content + "*";
	}

	public static String bold(String content) {
		return italic(italic(content));
	}

	public static String boldAndItalic(String content) {
		return bold(italic(content));
	}
}
