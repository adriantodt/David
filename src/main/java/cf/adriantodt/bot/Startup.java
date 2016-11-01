/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [22/09/16 18:37]
 */

package cf.adriantodt.bot;

import cf.adriantodt.bot.gui.BotGui;
import net.dv8tion.jda.core.utils.SimpleLog;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Startup {
	public static Logger LOGGER = LogManager.getLogger("Startup");
	public static BotGui UI = null;

	public static void main(String[] args) {
		LOGGER.info("Pre-Initializating...");
		if (GraphicsEnvironment.isHeadless()) LOGGER.info("GUI Disabled. (Headless Environiment)");
		else if (Arrays.stream(args).filter("nogui"::equals).findAny().orElse(null) == null && !GraphicsEnvironment.isHeadless())
			UI = BotGui.createBotGui();
		else LOGGER.info("GUI Disabled. (parameter \"nogui\")");
		hackJDALog();
		Java.hackStdout();

		try {
			Bot.init();
		} catch (Exception e) {
			LOGGER.error("An exception was caught during Initialization: ", e);
			Java.stopApp();
		}
	}

	private static void hackJDALog() {
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
}
