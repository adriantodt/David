/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [05/10/16 15:54]
 */

package cf.adriantodt.bot.data;

import cf.adriantodt.bot.Bot;
import com.google.gson.Gson;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DataManager {
	public static final RethinkDB r = RethinkDB.r;

	public static Connection conn;
	public static Gson json = Bot.JSON_INTERNAL;
	public static ReturnHandler h = ReturnHandler.h;

	public static void init() {
		conn = r.connection().hostname(Configs.getConfigs().get("hostname").getAsString()).port(Configs.getConfigs().get("port").getAsInt()).db("bot").connect();
	}

	public static Path getPath(String file, String ext) {
		try {
			return Paths.get(file + "." + ext);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static List<String> encode(List<String> list) {
		return apply(list, DataManager::encode);
	}

	public static List<String> decode(List<String> list) {
		return apply(list, DataManager::decode);
	}

	public static <T, R> List<R> apply(List<T> list, Function<T, R> mapper) {
		return list.stream().map(mapper).collect(Collectors.toList());
	}

	public static String encode(String string) {
		return Base64.getEncoder().encodeToString(string.getBytes(Charset.forName("UTF-8")));
	}

	public static String decode(String string) {
		return new String(Base64.getDecoder().decode(string), Charset.forName("UTF-8"));
	}
}
