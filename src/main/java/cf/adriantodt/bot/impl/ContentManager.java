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
	public static final String[][] SU_THEORIES;
	public static final String[] TESV_GUARDS;
	public static final boolean SU_THEORIES_LOADED, TESV_GUARDS_LOADED;

	private static final Logger LOGGER = LogManager.getLogger("ContentManager");

	static {
		String[] TESV_GUARDS_IN = new String[0];
		boolean TESV_GUARDS_LOAD = false;
		try {
			String TESV_GUARDS_RAW = resource("/skyrim_guards.txt");
			TESV_GUARDS_IN = TESV_GUARDS_RAW.split("\\r?\\n");
			TESV_GUARDS_LOAD = true;
		} catch (Exception e) {
			LOGGER.error("Error while parsing \"skyrim_guards.txt\" resource.", e);
		}
		TESV_GUARDS = TESV_GUARDS_IN;
		TESV_GUARDS_LOADED = TESV_GUARDS_LOAD;

		String[][] SU_THEORIES_IN = new String[0][0];
		boolean SU_THEORIES_LOAD = false;
		try {
			SU_THEORIES_IN = StreamSupport.stream(
				new JsonParser().parse(resource("/stevenuniverse_theories.json")).getAsJsonArray().spliterator(), false
			).map(
				jsonElement -> StreamSupport.stream(jsonElement.getAsJsonArray().spliterator(), false)
					.map(JsonElement::getAsString).toArray(String[]::new)
			).toArray(String[][]::new);
			SU_THEORIES_LOAD = true;
		} catch (Exception e) {
			LOGGER.error("Error while parsing \"stevenuniverse_theories.json\" resource.", e);
		}
		SU_THEORIES = SU_THEORIES_IN;
		SU_THEORIES_LOADED = SU_THEORIES_LOAD;
	}

	public static String resource(String file) {
		return IOHelper.toString(ContentManager.class.getResourceAsStream(file));
	}
}
