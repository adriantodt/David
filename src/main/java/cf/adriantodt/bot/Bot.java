/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [22/09/16 18:35]
 */

package cf.adriantodt.bot;

import cf.adriantodt.bot.data.*;
import cf.adriantodt.bot.handlers.BotGreeter;
import cf.adriantodt.bot.handlers.CommandHandler;
import cf.adriantodt.bot.handlers.ReadyBuilder;
import cf.adriantodt.bot.impl.CmdsAndInterfaces;
import cf.adriantodt.bot.impl.I18nHardImpl;
import cf.adriantodt.bot.utils.Statistics;
import cf.adriantodt.bot.utils.Tasks;
import cf.adriantodt.jda.port.AnnotatedEventManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static cf.adriantodt.bot.utils.Formatter.boldAndItalic;

public class Bot {
	public static final Random RAND = new Random();
	public static final Gson JSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
	public static final Gson JSON_INTERNAL = new GsonBuilder().serializeNulls().create();
	public static Logger LOGGER = LogManager.getLogger("Bot");
	public static JDA API = null;
	public static User SELF = null;
	public static boolean LOADED = false;
	public static List<Runnable> onLoaded = new ArrayList<>();

	static {
		onLoaded.add(() -> {
			User user = API.getUserById(Configs.getConfigs().get("ownerID").getAsString());
			if (user == null) {
				LOGGER.warn("Owner not regognized. This WILL cause issues (specially PermSystem)");
			} else {
				LOGGER.info("Owner recognized: " + user.getName() + "#" + user.getDiscriminator() + " (ID: " + user.getId() + ")");
			}
		});
	}

	public static void init() throws Exception {
		DataManager.init();
		Tasks.startAsyncTasks();
		new JDABuilder(AccountType.BOT)
			.setToken(Configs.getConfigs().get("token").getAsString())
			.setBulkDeleteSplittingEnabled(false)
			.setAudioEnabled(false)
			.setEventManager(new AnnotatedEventManager())
			.addListener(ReadyBuilder.lamdba(event -> {
				API = event.getJDA();
				SELF = event.getJDA().getSelfInfo();
				//API.getSelfInfo().setGame("mention me for help");
				I18nHardImpl.impl();
				I18nHardImpl.implLocal();
				Statistics.startDate = new Date();
			}))
			.addListener(CommandHandler.class)
			.addListener(BotGreeter.class)
			.addListener(Guilds.class)
			.buildBlocking();
		LOADED = true;
		onLoaded.forEach(Runnable::run);
		onLoaded = null;
		LOGGER = LogManager.getLogger(SELF.getName());
		LOGGER.info("Bot: " + SELF.getName() + " (#" + SELF.getId() + ")");
		//LOGGER.info("Configs: " + DataManager.getSaveFile().toAbsolutePath().toString());
		Tasks.startJDAAsyncTasks();
		CmdsAndInterfaces.impl();

		//TODO WAIT DV8'S IMPL
		((JDAImpl) API).getClient().send(new JSONObject()
			.put("op", 3)
			.put("d", new JSONObject()
				.put("game", new JSONObject()
					.put("name", "mention me for help")
					.put("type", 0))
				.put("since", System.currentTimeMillis())
				.put("afk", false)
				.put("status", "online")).toString()
		);

		Push.pushSimple("start", channel -> I18n.getLocalized("bot.startup", channel));
	}

	public static void stopBot() {
		//API.getSelfInfo().setIdle(true);
		//API.getAccountManager().update();
		LOGGER.info("Bot exiting...");
		Push.pushSimple("stop", channel -> boldAndItalic(I18n.getLocalized("bot.stop", channel)));
		try {
			Thread.sleep(2 * 1000);
		} catch (Exception ignored) {
		}
		API.shutdownNow(true);
		Java.stopApp();
	}
}
