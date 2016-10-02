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

import cf.adriantodt.bot.base.ReadyBuilder;
import cf.adriantodt.bot.handlers.BotGreeter;
import cf.adriantodt.bot.handlers.BotIntercommns;
import cf.adriantodt.bot.handlers.CommandHandler;
import cf.adriantodt.bot.hardimpl.CmdsAndInterfaces;
import cf.adriantodt.bot.hardimpl.I18nHardImpl;
import cf.adriantodt.bot.persistence.DataManager;
import cf.adriantodt.bot.utils.Statistics;
import cf.adriantodt.bot.utils.Tasks;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.Random;

import static cf.adriantodt.bot.persistence.DataManager.loadData;
import static cf.adriantodt.bot.persistence.DataManager.loadI18n;

public class Bot {
	public static final Random RAND = new Random();
	public static final Gson JSON = new GsonBuilder().setPrettyPrinting().create();
	public static final Gson JSON_INTERNAL = new GsonBuilder().create();
	public static Logger LOGGER = LogManager.getLogger("Bot");
	public static JDA API = null;
	public static User SELF = null;
	public static String GAME = "";

	public static void init() throws Exception {
		DataManager.loadConfig();
		Tasks.startAsyncTasks();
		new JDABuilder()
			.setBotToken(DataManager.configs.token)
			.setBulkDeleteSplittingEnabled(false)
			.setAudioEnabled(false)
			.addListener(
				new ReadyBuilder()
					.add(event -> API = event.getJDA())
					.add(event -> SELF = event.getJDA().getSelfInfo())
					.add(event -> {
						loadData();
						loadI18n();
					})
					.add(event -> I18nHardImpl.impl())
					.build(),
				new CommandHandler(), new BotIntercommns(), new BotGreeter()
			).buildBlocking();
		LOGGER = LogManager.getLogger(SELF.getUsername());
		LOGGER.info("Logged in as '" + SELF.getUsername() + "' (ID @" + SELF.getId() + ")");
		LOGGER.info("Configs: " + DataManager.getSaveFile().toAbsolutePath().toString());
		if (Startup.UI != null) {
			Startup.UI.frame.setTitle(SELF.getUsername() + " - GUI");
		}
		Tasks.startJDAAsyncTasks(API);
		CmdsAndInterfaces.impl();
		Statistics.startDate = new Date();
	}

	public static void stopBot() {
		API.getAccountManager().setIdle(true);
		API.getAccountManager().update();
		LOGGER.info("Bot exiting...");
		try {
			Thread.sleep(2 * 1000);
		} catch (Exception ignored) {
		}
		Java.stopApp();
	}

	public static void restartBot() {
		API.getAccountManager().setIdle(true);
		API.getAccountManager().update();
		LOGGER.info("Bot restarting...");
		try {
			Thread.sleep(2 * 1000);
		} catch (Exception ignored) {
		}
		Java.restartApp();
	}

	public static void setDefault() {
		API.getAccountManager().setGame(GAME);
		API.getAccountManager().setIdle(false);
	}
}
