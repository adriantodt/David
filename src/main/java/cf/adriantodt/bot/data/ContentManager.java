/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [01/11/16 12:38]
 */

package cf.adriantodt.bot.data;

import cf.brforgers.core.lib.IOHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContentManager {
	private static final Logger LOGGER = LogManager.getLogger("ContentManager");
	public static String[][][] SU_THEORIES;
	public static String[] TESV_GUARDS, SU_STEVONNIE, TESV_LYDIA;
	public static boolean SU_THEORIES_LOADED = false, TESV_GUARDS_LOADED = false, SU_STEVONNIE_LOADED = false, TESV_LYDIA_LOADED = false;

	static {
		reload();
	}

	public static void reload() {
		try {
			TESV_GUARDS = resource("/assets/funny/skyrim_guards.txt").split("\\r?\\n");
			TESV_GUARDS_LOADED = true;
		} catch (Exception e) {
			LOGGER.error("Error while parsing \"skyrim_guards.txt\" resource.", e);
		}

		try {
			TESV_LYDIA = resource("/assets/funny/skyrim_lydia.txt").split("\\r?\\n");
			TESV_LYDIA_LOADED = true;
		} catch (Exception e) {
			LOGGER.error("Error while parsing \"skyrim_lydia.txt\" resource.", e);
		}

		try {
			JsonObject object = new JsonParser().parse(resource("/assets/funny/stevenuniverse_theories.json")).getAsJsonObject();
			List<List<List<String>>> SU_THEORIES_BUILD = Arrays.asList(Arrays.asList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()),Arrays.asList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
			object.get("characters").getAsJsonArray().forEach(element -> {
				SU_THEORIES_BUILD.get(0).get(0).add(element.getAsString());
				SU_THEORIES_BUILD.get(0).get(2).add(element.getAsString());
			});

			object.get("places").getAsJsonArray().forEach(element -> {
				SU_THEORIES_BUILD.get(1).get(0).add(element.getAsString());
				SU_THEORIES_BUILD.get(1).get(2).add(element.getAsString());
			});

			object.get("objects").getAsJsonArray().forEach(element -> {
				SU_THEORIES_BUILD.get(0).get(0).add(element.getAsString());
				SU_THEORIES_BUILD.get(0).get(2).add(element.getAsString());
				SU_THEORIES_BUILD.get(1).get(0).add(element.getAsString());
				SU_THEORIES_BUILD.get(1).get(2).add(element.getAsString());
			});

			object.get("gems").getAsJsonArray().forEach(element -> {
				SU_THEORIES_BUILD.get(0).get(0).add(element.getAsString());
				SU_THEORIES_BUILD.get(0).get(2).add(element.getAsString());
				SU_THEORIES_BUILD.get(1).get(0).add(element.getAsString() + "'s room");
				SU_THEORIES_BUILD.get(1).get(2).add(element.getAsString() + "'s room");
			});

			object.get("fusionGems").getAsJsonArray().forEach(element -> {
				SU_THEORIES_BUILD.get(0).get(0).add(element.getAsString());
				SU_THEORIES_BUILD.get(0).get(2).add(element.getAsString());
				SU_THEORIES_BUILD.get(1).get(0).add(element.getAsString() + "'s room");
				SU_THEORIES_BUILD.get(1).get(2).add(element.getAsString() + "'s room");
				SU_THEORIES_BUILD.get(1).get(0).add(element.getAsString() + "'s fusion realm");
				SU_THEORIES_BUILD.get(1).get(2).add(element.getAsString() + "'s fusion realm");
			});

			object.get("verb").getAsJsonArray().forEach(element -> {
				SU_THEORIES_BUILD.get(0).get(1).add(element.getAsString());
				SU_THEORIES_BUILD.get(1).get(1).add(element.getAsString());
			});
			object.get("revelation4characters").getAsJsonArray().forEach(element -> {
				SU_THEORIES_BUILD.get(0).get(2).add(element.getAsString());
			});
			object.get("revelation4places").getAsJsonArray().forEach(element -> {
				SU_THEORIES_BUILD.get(1).get(2).add(element.getAsString());
			});
			object.get("post").getAsJsonArray().forEach(element -> {
				SU_THEORIES_BUILD.get(0).get(3).add(element.getAsString());
				SU_THEORIES_BUILD.get(1).get(3).add(element.getAsString());
			});

			SU_THEORIES = SU_THEORIES_BUILD.stream().map(ll -> ll.stream().map(l -> l.stream().toArray(String[]::new)).toArray(String[][]::new)).toArray(String[][][]::new);
			SU_THEORIES_LOADED = true;
		} catch (Exception e) {
			LOGGER.error("Error while parsing \"stevenuniverse_theories.json\" resource.", e);
		}

		try {
			SU_STEVONNIE = resource("/assets/funny/stevenuniverse_stevonnie.txt").split("\\r?\\n");
			SU_STEVONNIE_LOADED = true;
		} catch (Exception e) {
			LOGGER.error("Error while parsing \"stevenuniverse_stevonnie.txt\" resource.", e);
		}
	}

	public static String resource(String file) {
		return IOHelper.toString(ContentManager.class.getResourceAsStream(file));
	}
}
