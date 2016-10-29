/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [28/10/16 07:45]
 */

package cf.adriantodt.bot.webinterface;

import cf.adriantodt.bot.Bot;
import cf.adriantodt.bot.data.DataManager;
import cf.adriantodt.bot.data.Guilds;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@RestController
public class GetController {
	public static final Map<String, Function<Map<String, String>, JsonElement>> api = new HashMap<>();
	public static final Function<Map<String, String>, JsonElement> error;

	static {
		error = map -> error("Not found");

		api.put("user", map -> {
			String id = map.getOrDefault("id", "");
			if (id.isEmpty()) return error("Not found");
			User user = Bot.API.getUserById(id);
			if (user == null) return error("Invalid User");
			JsonObject object = new JsonObject();
			JsonArray array = new JsonArray();
			Bot.API.getGuilds().stream().filter(guild -> guild.getOwner().getUser().equals(user)).forEach(g -> {
				if (map.containsKey("detailed")) array.add(toJson(g));
				else array.add(new JsonPrimitive(g.getId()));
			});
			object.add("guildsOwned", array);
			object.addProperty("owner", DataManager.configs.ownerID.equals(id));
			return object;
		});

		api.put("me", map -> {
			JsonObject object = new JsonObject();
			object.addProperty("avatar", Bot.SELF.getAvatarUrl());
			object.addProperty("owner", DataManager.configs.ownerID);
			return object;
		});
	}

	public static JsonElement toJson(Guild g) {
		JsonObject guild = new JsonObject();
		guild.addProperty("id", g.getId());
		guild.addProperty("name", g.getName());
		guild.addProperty("avatar", g.getIconUrl());
		guild.addProperty("vip", Guilds.fromDiscord(g).getFlag("vip"));
		return guild;
	}

	public static JsonObject error(String error) {
		JsonObject object = new JsonObject();
		object.addProperty("fine", false);
		object.addProperty("error", error);
		return object;
	}

	@RequestMapping("/get")
	public String api(@RequestParam Map<String, String> params) {
		return api.getOrDefault(params.getOrDefault("type", ""), error).apply(params).toString();
	}
}
