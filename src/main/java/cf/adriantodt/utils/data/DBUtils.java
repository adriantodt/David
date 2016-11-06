/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [05/11/16 22:10]
 */

package cf.adriantodt.utils.data;

import cf.adriantodt.oldbot.data.DataManager;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cf.adriantodt.utils.CollectionUtils.apply;

public class DBUtils {
	public static Path getPath(String file, String ext) {
		try {
			return Paths.get(file + "." + ext);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static List<String> encode(List<String> list) {
		return apply(list, DBUtils::encode);
	}

	public static List<String> decode(List<String> list) {
		return apply(list, DBUtils::decode);
	}

	public static String encode(String string) {
		return Base64.getEncoder().encodeToString(string.getBytes(Charset.forName("UTF-8")));
	}

	public static String decode(String string) {
		return new String(Base64.getDecoder().decode(string), Charset.forName("UTF-8"));
	}
}
