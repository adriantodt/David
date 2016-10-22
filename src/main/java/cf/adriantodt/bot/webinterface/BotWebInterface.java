/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [21/10/16 15:44]
 */

package cf.adriantodt.bot.webinterface;

import cf.adriantodt.web.hooks.http.provider.ContentProvider;
import org.apache.logging.log4j.LogManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static cf.adriantodt.bot.Bot.API;
import static cf.adriantodt.bot.data.DataManager.json;

public class BotWebInterface {
	public static final Map<String, Supplier<String>> apiEndpoint = new HashMap<>();

	static {
		apiEndpoint.put("guilds", () -> {
			Map<String, Map<String, Object>> map = new HashMap<>();
			API.getGuilds().forEach(guild -> {
				Map<String, Object> info = new HashMap<>();
				info.put("name", guild.getName());
				info.put("owner", guild.getOwner().getUser().getId());
				map.put(guild.getId(), info);
			});
			return json.toJson(map);
		});
	}

	public static void startWebServer() {
		try {
			//ModularServer.start(threaded(AnnotatedContentProvider.addToManager(BotWebInterface.class, new SimpleHTTPSocketManager())), 8090).run();
		} catch (Exception e) {
			LogManager.getLogger("BotWebInterface").error("Server crashed :(", e);
		}
	}

	@ContentProvider("api")
	public static void api(Socket socket, String args, BufferedReader in, BufferedWriter out) throws Exception {
		System.out.println(args);
		out.write(apiEndpoint.getOrDefault(args, () -> "{\"error\":\"\\\"" + args + "\\\" is invalid\"").get());
	}
}
