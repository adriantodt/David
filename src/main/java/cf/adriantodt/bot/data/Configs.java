/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [07/10/16 07:53]
 */

package cf.adriantodt.bot.data;

import cf.adriantodt.utils.Java;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

import static cf.adriantodt.bot.Bot.JSON;
import static cf.adriantodt.bot.Bot.LOGGER;
import static cf.adriantodt.bot.data.DataManager.getPath;

public class Configs {
	private static JsonObject configs = null;

	public static JsonObject getConfigs() {
		loadConfig();
		return configs;
	}

	private static Path path() {
		return getPath("configs", "json");
	}

	private static void loadConfig() {
		if (configs != null) return;
		try {
			JsonElement input = new JsonParser().parse(new String(Files.readAllBytes(path()), Charset.forName("UTF-8")));
			Configs.requireNonNull(input, "Config object is null (How do you  managed to even do that?)");

			configs = input.getAsJsonObject();
			Configs.requireNonNull(configs, "hostname", Configs::isJsonString, "RethinkDB Hostname doesn't exists/is invalid");
			Configs.requireNonNull(configs, "token", Configs::isJsonString, "Token doesn't exists/is null");
			Configs.requireNonNull(configs, "ownerID", Configs::isJsonString, "OwnerID doesn't exists/is null");
			Configs.requireNonNull(configs, "tumblrCKey", Configs::isJsonString, "Tumblr Consumer Key doesn't exists/is null");
			Configs.requireNonNull(configs, "tumblrSKey", Configs::isJsonString, "Tumblr Secret Key doesn't exists/is null");
			Configs.requireNonNull(configs, "port", Configs::isJsonNumber, "RethinkDB Port doesn't exists/is null");
			if (configs.get("port").getAsInt() == 0) throw new IllegalStateException("Port number can't be 0");
		} catch (Exception e) {
			LOGGER.error("Configuration File not found or damaged. Generating one at " + path() + "...");
			JsonObject configs = new JsonObject();
			configs.add("hostname", null);
			configs.add("token", null);
			configs.add("ownerID", null);
			configs.add("tumblrCKey", null);
			configs.add("tumblrSKey", null);
			configs.add("port", null);

			try {
				Files.write(path(), JSON.toJson(configs).getBytes(Charset.forName("UTF-8")));
				LOGGER.error("Configuration File generated. Please fill the 2 required configs. The App will now Exit.");
			} catch (Exception ex) {
				LOGGER.error("Configuration File could not be generated. Please fix the permissions. The App will now Exit.");
			}

			Java.stopApp();
		}
	}

	private static boolean isJsonString(JsonElement element) {
		return element.isJsonPrimitive() && element.getAsJsonPrimitive().isString();
	}

	private static boolean isJsonNumber(JsonElement element) {
		return element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber();
	}

	private static void requireNonNull(JsonObject object, String element, Predicate<JsonElement> correctState, String message) {
		if (object == null || object.get(element).isJsonNull() || !correctState.test(object.get(element)))
			throw new NullPointerException(message);
	}

	private static void requireNonNull(JsonElement element, String message) {
		if (element == null || element.isJsonNull())
			throw new NullPointerException(message);
	}
}
