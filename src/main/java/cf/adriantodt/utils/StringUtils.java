/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [01/11/16 13:23]
 */

package cf.adriantodt.utils;

import java.util.*;

public class StringUtils {
	public static String[] splitArgs(String args, int expectedArgs) {
		String[] raw = args.split("\\s+", expectedArgs);
		if (expectedArgs < 1) return raw;
		return normalizeArray(raw, expectedArgs);
	}

	public static String[] normalizeArray(String[] raw, int expectedSize) {
		String[] normalized = new String[expectedSize];

		Arrays.fill(normalized, "");
		for (int i = 0; i < normalized.length; i++) {
			if (i < raw.length && raw[i] != null && !raw[i].isEmpty()) {
				normalized[i] = raw[i];
			}
		}
		return normalized;
	}

	public static String[] advancedSplitArgs(String args, int expectedArgs) {
		List<String> result = new ArrayList<>();
		boolean inAString = false;
		StringBuilder currentBlock = new StringBuilder();
		for (int i = 0; i < args.length(); i++) {
			if (args.charAt(i) == '"' && (i == 0 || args.charAt(i - 1) != '\\' || args.charAt(i - 2) == '\\')) //Entered a String Init/End
				inAString = !inAString;

			if (inAString) //We're at a String. Keep Going
				currentBlock.append(args.charAt(i));
			else if (Character.isSpaceChar(args.charAt(i))) //We found a Code Block
			{
				if (currentBlock.length() != 0) {
					if (currentBlock.charAt(0) == '"' && currentBlock.charAt(currentBlock.length() - 1) == '"') {
						currentBlock.deleteCharAt(0);
						currentBlock.deleteCharAt(currentBlock.length() - 1);
					}

					result.add(currentBlock.toString());
					currentBlock = new StringBuilder();
				}
			} else currentBlock.append(args.charAt(i));
		}

		if (currentBlock.length() != 0) {
			if (currentBlock.charAt(0) == '"' && currentBlock.charAt(currentBlock.length() - 1) == '"') {
				currentBlock.deleteCharAt(0);
				currentBlock.deleteCharAt(currentBlock.length() - 1);
			}

			result.add(currentBlock.toString());
		}

		String[] raw = result.toArray(new String[result.size()]);

		if (expectedArgs < 1) return raw;
		return normalizeArray(raw, expectedArgs);
	}

	public static Map<String, String> parse(String[] args) {
		Map<String, String> options = new HashMap<>();

		for (int i = 0; i < args.length; i++) {
			if (args[i].charAt(0) == '-' || args[i].charAt(0) == '/') //This start with - or /
			{
				args[i] = args[i].substring(1);
				if (i + 1 >= args.length || args[i + 1].charAt(0) == '-' || args[i + 1].charAt(0) == '/') //Next start with - (or last arg)
				{
					options.put(args[i], "null");
				} else {
					options.put(args[i], args[i + 1]);
					i++;
				}
			} else {
				options.put(null, args[i]);
			}
		}

		return options;
	}

	public static String notNullOrDefault(String str, String defaultStr) {
		if (str == null || str.trim().isEmpty()) return defaultStr;
		return str;
	}

	public static String limit(String value, int length) {
		StringBuilder buf = new StringBuilder(value);
		if (buf.length() > length) {
			buf.setLength(length - 3);
			buf.append("...");
		}

		return buf.toString();
	}
}
