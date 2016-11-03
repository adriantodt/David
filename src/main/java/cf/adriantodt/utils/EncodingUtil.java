/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [02/11/16 20:10]
 */

package cf.adriantodt.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class EncodingUtil {
	private EncodingUtil() {
	}

	/**
	 * Decodes the passed UTF-8 String using an algorithm that's compatible with
	 * JavaScript's <code>decodeURIComponent</code> function. Returns
	 * <code>null</code> if the String is <code>null</code>.
	 *
	 * @param s The UTF-8 encoded String to be decoded
	 * @return the decoded String
	 */
	public static String decodeURIComponent(String s) {
		if (s == null) {
			return null;
		}

		String result = null;

		try {
			result = URLDecoder.decode(s, "UTF-8");
		}

		// This exception should never occur.
		catch (UnsupportedEncodingException e) {
			result = s;
		}

		return result;
	}

	/**
	 * Encodes the passed String as UTF-8 using an algorithm that's compatible
	 * with JavaScript's <code>encodeURIComponent</code> function. Returns
	 * <code>null</code> if the String is <code>null</code>.
	 *
	 * @param s The String to be encoded
	 * @return the encoded String
	 */
	public static String encodeURIComponent(String s) {
		String result = null;

		try {
			result = URLEncoder.encode(s, "UTF-8")
				.replaceAll("\\+", "%20")
				.replaceAll("\\%21", "!")
				.replaceAll("\\%27", "'")
				.replaceAll("\\%28", "(")
				.replaceAll("\\%29", ")")
				.replaceAll("\\%7E", "~");
		}

		// This exception should never occur.
		catch (UnsupportedEncodingException e) {
			result = s;
		}

		return result;
	}
}