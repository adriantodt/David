/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [04/11/16 22:42]
 */

package cf.adriantodt.David;

import cf.adriantodt.David.loader.ModuleManager;
import cf.adriantodt.David.oldmodules.db.DBModule;
import cf.brforgers.core.lib.IOHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;
import org.apache.logging.log4j.Logger;

import static cf.adriantodt.utils.Log4jUtils.logger;

public class Loader {
	public static final Logger LOGGER = logger();
	public static String[] args;

	public static void main(String[] args) throws Exception {
		Loader.args = args;
		JsonElement src = new JsonParser().parse(resource("/assets/loader/main.json"));

		if (!src.isJsonArray()) {
			LOGGER.error("\"/assets/loader/main.json\" is in a incorrect form. Expected \"" + JsonArray.class + "\", got \"" + src.getClass() + "\"");
			return;
		}

		src.getAsJsonArray().forEach(element -> {
			try {
				ModuleManager.add(Class.forName(element.getAsString()));
			} catch (Exception e) {
				LOGGER.error("Failed to load Module " + element, e);
			}
		});

		ModuleManager.firePreReadyEvents();

		new JDABuilder(AccountType.BOT)
			.setToken(DBModule.getConfig().get("token").getAsString())
			.setEventManager(new AnnotatedEventManager())
			.addListener(ModuleManager.jdaListeners())
			.buildBlocking();

		ModuleManager.firePostReadyEvents();
	}

	public static String resource(String file) {
		return IOHelper.toString(Loader.class.getResourceAsStream(file));
	}
}
