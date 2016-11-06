/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [05/11/16 22:09]
 */

package cf.adriantodt.utils.data;

import com.google.gson.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;



public class ConfigUtils {
	private static final Charset UTF8 = Charset.forName("UTF-8");
	private static final Logger LOGGER = LogManager.getLogger("ConfigUtils");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

	static {
		File file = new File("./configs/");
		if (!file.isDirectory()) //noinspection ResultOfMethodCallIgnored
			file.delete();
		if (!file.exists()) //noinspection ResultOfMethodCallIgnored
			file.mkdir();
	}

	public static JsonObject get(String name, Map<String, Predicate<JsonElement>> validator, Supplier<JsonObject> defaultGenerator, boolean returnGenerated, boolean generateFileOnError) {
		Path path = path(name);
		try {
			JsonElement input = new JsonParser().parse(new String(Files.readAllBytes(path), UTF8));
			JsonObject object = requireObject(input);
			validator.forEach((s, predicate) -> requireValid(object, s, predicate));
			return object;
		} catch (IOException | NullPointerException | IllegalStateException e) {
			LOGGER.error("Error while loading Config file: ", e);
			Optional<Supplier<JsonObject>> optionalGenerator = Optional.ofNullable(defaultGenerator);
			LOGGER.error("Configuration File not found or damaged." + (optionalGenerator.isPresent() ? (generateFileOnError ? " Generating one at " + path + "..." : "") + (returnGenerated ? " The Generated Default Object will be returned." : "") : " No Generator found."));
			if (!optionalGenerator.isPresent()) {
				return null;
			} else {
				JsonObject defaultObj = optionalGenerator.get().get();

				if (generateFileOnError) {
					try {
						Files.write(path, GSON.toJson(defaultObj).getBytes(Charset.forName("UTF-8")));
						LOGGER.error("Configuration File generated.");
					} catch (Exception ex) {
						LOGGER.error("Configuration File could not be generated. Please fix the permissions.");
					}
				}

				if (returnGenerated) return defaultObj;
			}
		}

		return null;
	}

	private static Path path(String name) {
		return getPath("configs/" + name, "json");
	}

	public static boolean isJsonString(JsonElement element) {
		return element.isJsonPrimitive() && element.getAsJsonPrimitive().isString();
	}

	public static boolean isJsonNumber(JsonElement element) {
		return element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber();
	}

	public static void requireValid(JsonObject object, String element, Predicate<JsonElement> correctState) {
		if (!correctState.test(object.get(element)))
			throw new IllegalStateException("\"" + element + "\" is invalid");
	}

	public static JsonObject requireObject(JsonElement element) {
		if (element == null || !element.isJsonObject())
			throw new NullPointerException("the provided " + JsonElement.class + " is not an " + JsonObject.class);

		return element.getAsJsonObject();
	}
}
