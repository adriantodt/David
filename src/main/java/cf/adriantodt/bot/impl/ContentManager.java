/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [23/10/16 19:58]
 */

package cf.adriantodt.bot.impl;

import cf.brforgers.core.lib.IOHelper;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.StreamSupport;

public class ContentManager {
	private static final Logger LOGGER = LogManager.getLogger("ContentManager");
	public static String[][] SU_THEORIES;
	public static String[] TESV_GUARDS, SU_STEVONNIE;
	public static boolean SU_THEORIES_LOADED = false, TESV_GUARDS_LOADED = false, SU_STEVONNIE_LOADED;

	static {
		reload();
	}

	public static void reload() {
		try {
			TESV_GUARDS = resource("/skyrim_guards.txt").split("\\r?\\n");
			SU_THEORIES_LOADED = true;
		} catch (Exception e) {
			LOGGER.error("Error while parsing \"skyrim_guards.txt\" resource.", e);
		}

		try {
			SU_THEORIES = StreamSupport.stream(new JsonParser().parse(resource("/stevenuniverse_theories.json")).getAsJsonArray().spliterator(), false)
				.map(jsonElement -> StreamSupport.stream(jsonElement.getAsJsonArray().spliterator(), false).map(JsonElement::getAsString).toArray(String[]::new))
				.toArray(String[][]::new);
			SU_THEORIES_LOADED = true;
		} catch (Exception e) {
			LOGGER.error("Error while parsing \"stevenuniverse_theories.json\" resource.", e);
		}

		try {
			SU_STEVONNIE = resource("/stevenuniverse_stevonnie.txt").split("\\r?\\n");
			SU_STEVONNIE_LOADED = true;
		} catch (Exception e) {
			LOGGER.error("Error while parsing \"stevenuniverse_stevonnie.txt\" resource.", e);
		}
	}

	public static String resource(String file) {
		return IOHelper.toString(ContentManager.class.getResourceAsStream(file));
	}
}
