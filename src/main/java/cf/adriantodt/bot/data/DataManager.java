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

import java.nio.file.Path;
import java.nio.file.Paths;

public class DataManager {
	public static final RethinkDB r = RethinkDB.r;
	public static Configs configs;
	public static Connection conn;
	public static Gson json = Bot.JSON_INTERNAL;
	public static ReturnHandler h = new ReturnHandler();

	public static void init() {
		Configs.loadConfig();
		conn = r.connection().hostname(configs.hostname).port(configs.port).db("bot").connect();
	}

	public static void load() {
		Guilds.loadAll();
		Guilds.all().forEach(UserCommands::loadAllFrom);
	}


	public static Path getPath(String file, String ext) {
		try {
			return Paths.get(file + "." + ext);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
