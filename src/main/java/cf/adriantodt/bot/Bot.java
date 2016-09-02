/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU AFFERO GENERAL PUBLIC LICENSE Version 3:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [02/09/16 07:55]
 */

package cf.adriantodt.bot;

import cf.adriantodt.bot.impl.Commands;
import cf.adriantodt.bot.impl.EventHandler;
import cf.adriantodt.bot.spy.Spy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.dv8tion.jda.utils.SimpleLog;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.Random;

import static cf.adriantodt.bot.persistent.DataManager.*;

public class Bot extends ListenerAdapter {
	public static final Random RAND = new Random();
	public static final Gson JSON = new GsonBuilder().setPrettyPrinting().create();
	public static Logger LOGGER = LogManager.getLogger("Loader-BotCreator");
	public static JDA API = null;
	public static String BOTID = null, BOTNAME = null;
	public static User SELF = null;
	public static String GAME = "";

	public static void main(String[] args) {
		LOGGER.info("Started!");
		hackLog();
		Utils.startAsyncCpuUsage();
		Utils.startAsyncUserTimeout();
		try {
			loadOptions();
			API = new JDABuilder().addListener(new Bot()).setBotToken(options.token).setBulkDeleteSplittingEnabled(false).buildBlocking();
			BOTID = API.getSelfInfo().getId();
			BOTNAME = API.getSelfInfo().getUsername();
			SELF = API.getUserById(BOTID);
			LOGGER = LogManager.getLogger("Bot-" + BOTNAME);
			LOGGER.info("Logged in as '" + BOTNAME + "' (ID @" + BOTID + ")");
			LOGGER.info("Configs: " + getPath().toAbsolutePath().toString());
			Commands.impl();
			loadData();
			Statistics.startDate = new Date();
		} catch (Exception e) {
			LOGGER.error("An exception was caught during Initialization: ", e);
			Java.stopApp();
		}
	}

	private static void hackLog() {
		SimpleLog.addListener(new SimpleLog.LogListener() {
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
					default:
						return Level.OFF;
				}
			}

			@Override
			public void onLog(SimpleLog log, SimpleLog.Level logLevel, Object message) {
				LOGGER.log(convert(logLevel), message);
			}

			@Override
			public void onError(SimpleLog log, Throwable err) {
				LOGGER.error(err);
			}
		});
		SimpleLog.LEVEL = SimpleLog.Level.OFF;
	}


	public static void stopBot() {
		API.getAccountManager().setIdle(true);
		API.getAccountManager().setGame("Saindo...");
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
		API.getAccountManager().setGame("Reiniciando...");
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
}
