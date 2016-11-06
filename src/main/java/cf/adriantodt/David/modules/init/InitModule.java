/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [05/11/16 20:42]
 */

package cf.adriantodt.David.modules.init;

import cf.adriantodt.David.loader.Module;
import cf.adriantodt.David.loader.Module.*;
import cf.adriantodt.David.modules.cmds.Pushes;
import cf.adriantodt.David.modules.db.DBModule;
import cf.adriantodt.David.modules.db.I18nModule;
import cf.adriantodt.David.utils.DiscordUtils;
import cf.adriantodt.utils.Java;
import cf.adriantodt.utils.Log4jUtils;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static cf.adriantodt.David.loader.Module.Type.STATIC;
import static cf.adriantodt.David.utils.Formatter.boldAndItalic;

@Module(STATIC)
@SubscribeJDA
public class InitModule {
	@JDAInstance
	private static JDA jda = null;

	@LoggerInstance
	private static Logger logger = null;

	@OnEnabled
	public static void init() {
		logger.info("Pre-Initializating...");
		try {
			File file = new File("./tmp/");
			if (file.exists())
				delete(file);
			file.mkdir();
			System.setProperty("java.io.tmpdir", file.getCanonicalPath());
		} catch (Exception e) {
			logger.error("Error while trying to define TMPDir: ", e);
		}
		logger.info("TMP Directory: " + System.getProperty("java.io.tmpdir"));

		Log4jUtils.hackStdout();
		DiscordUtils.hackJDALog();
	}

	@SubscribeEvent
	public static void ready(ReadyEvent event) {
		logger = LogManager.getLogger(event.getJDA().getSelfUser().getName());
		logger.info("Bot: " + event.getJDA().getSelfUser().getName() + " (#" + event.getJDA().getSelfUser().getId() + ")");
		event.getJDA().getPresence().setGame(Game.of("mention me for help"));
	}

	@PostReady
	public static void postReady() {
		User user = jda.getUserById(DBModule.getConfig().get("ownerID").getAsString());
		if (user == null) {
			logger.warn("Owner not regognized. This WILL cause issues (specially PermSystem)");
		} else {
			logger.info("Owner recognized: " + user.getName() + "#" + user.getDiscriminator() + " (ID: " + user.getId() + ")");
		}

		Pushes.pushSimple("start", channel -> I18nModule.getLocalized("bot.startup", channel));
	}

	@SuppressWarnings("ConstantConditions")
	private static void delete(File f) throws IOException {
		if (f.isDirectory()) {
			for (File c : f.listFiles())
				delete(c);
		}
		if (!f.delete())
			throw new FileNotFoundException("Failed to delete file: " + f);
	}

	public static void stopBot() {
		jda.getPresence().setGame(Game.of("Stopping..."));
		jda.getPresence().setIdle(true);
		logger.info("Bot exiting...");
		Pushes.pushSimple("stop", channel -> boldAndItalic(I18nModule.getLocalized("bot.stop", channel)));
		try {
			Thread.sleep(2 * 1000);
		} catch (Exception ignored) {
		}
		jda.shutdownNow(true);
		Java.stopApp();
	}
}