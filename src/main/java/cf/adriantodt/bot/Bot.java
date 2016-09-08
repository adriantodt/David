/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [02/09/16 08:18]
 */

package cf.adriantodt.bot;

import cf.adriantodt.bot.gui.BotGui;
import cf.adriantodt.bot.impl.Audio;
import cf.adriantodt.bot.impl.Commands;
import cf.adriantodt.bot.impl.EventHandler;
import cf.adriantodt.bot.impl.Spy;
import cf.adriantodt.bot.impl.i18n.I18n;
import cf.adriantodt.bot.impl.i18n.I18nHardImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.guild.GuildJoinEvent;
import net.dv8tion.jda.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.dv8tion.jda.utils.SimpleLog;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static cf.adriantodt.bot.impl.persistence.DataManager.*;

public class Bot extends ListenerAdapter {
	public static final Random RAND = new Random();
	public static final Gson JSON = new GsonBuilder().setPrettyPrinting().create();
	public static Logger LOGGER = LogManager.getLogger("BotCreator");
	public static JDA API = null;
	public static String BOTID = null, BOTNAME = null;
	public static User SELF = null;
	public static String GAME = "";
	public static Bot INSTANCE = null;

	public static void main(String[] args) {
		LOGGER.info("Started!");
		hackLog();
		if (Arrays.stream(args).filter("nogui"::equals).findAny().orElse(null) == null) BotGui.createBotGui();
		else LOGGER.info("UI Disabled");
		Utils.startAsyncCpuUsage();
		Utils.startAsyncUserTimeout();
		try {
			INSTANCE = new Bot();
			loadConfig();
			API = new JDABuilder().addListener(INSTANCE).setBotToken(configs.token).setBulkDeleteSplittingEnabled(false).buildBlocking();
			BOTID = API.getSelfInfo().getId();
			BOTNAME = API.getSelfInfo().getUsername();
			SELF = API.getUserById(BOTID);
			LOGGER = LogManager.getLogger("Bot-" + BOTNAME);
			LOGGER.info("Logged in as '" + BOTNAME + "' (ID @" + BOTID + ")");
			LOGGER.info("Configs: " + getSaveFile().toAbsolutePath().toString());
			Audio.setup();
			Commands.impl();
			loadData();
			loadI18n();
			I18nHardImpl.impl();
			Statistics.startDate = new Date();
		} catch (Exception e) {
			LOGGER.error("An exception was caught during Initialization: ", e);
			Java.stopApp();
		}
	}

	private static void hackLog() {
		Java.hackStdout();
		SimpleLog.addListener(new SimpleLog.LogListener() {
			private Map<String, Logger> logs = new HashMap<>();

			private Level convert(SimpleLog.Level level) {
				switch (level) {
					case ALL:
						return Level.ALL;
					case TRACE:
						return Level.TRACE;
					case DEBUG:
						return Level.DEBUG;
					case INFO:
						return Level.INFO;
					case WARNING:
						return Level.WARN;
					case FATAL:
						return Level.FATAL;
					case OFF:
						return Level.OFF;
					default:
						return Level.OFF;
				}
			}

			private Logger getLogger(String name) {
				if (!logs.containsKey(name)) logs.put(name, LogManager.getLogger(name));
				return logs.get(name);
			}

			@Override
			public void onLog(SimpleLog log, SimpleLog.Level logLevel, Object message) {
				getLogger(log.name).log(convert(logLevel), message);
			}

			@Override
			public void onError(SimpleLog log, Throwable err) {
				getLogger(log.name).error(err);
			}
		});
		SimpleLog.LEVEL = SimpleLog.Level.OFF;
	}


	public static void stopBot() {
		API.getAccountManager().setIdle(true);
		API.getAccountManager().setGame(I18n.getLocalized("bot.stop", "en_US"));
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
		API.getAccountManager().setGame(I18n.getLocalized("bot.restart", "en_US"));
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

	@Override
	public void onMessageReceived(final MessageReceivedEvent event) {
		Spy.spy(event);
		EventHandler.handle(event);
	}

	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		try {
			Spy.spy(event);
			event.getGuild().getPublicChannel().sendMessage(I18n.getLocalized("bot.hello1", "en_US"));
			event.getGuild().getPublicChannel().sendMessage(String.format(I18n.getLocalized("bot.hello2", "en_US"), event.getGuild().getOwner().getAsMention()));
		} catch (Exception e) {
			event.getGuild().getManager().leave();
		}

	}

	@Override
	public void onGuildLeave(GuildLeaveEvent event) {
		Spy.spy(event);
	}
}
