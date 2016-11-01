/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [28/09/16 22:11]
 */

package cf.adriantodt.bot.utils;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.SimpleLog;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class DiscordUtils {
	public static String guessGuildLanguage(Guild guild) {
		switch (guild.getRegion()) {
			case BRAZIL:
			case VIP_BRAZIL:
				return "pt_BR";
			case AMSTERDAM:
			case EU_WEST:
			case EU_CENTRAL:
			case FRANKFURT:
			case LONDON:
			case VIP_AMSTERDAM:
			case VIP_EU_WEST:
			case VIP_EU_CENTRAL:
			case VIP_FRANKFURT:
			case VIP_LONDON:
				return "en_GB";
			case SINGAPORE:
			case VIP_SINGAPORE:
				return "en_SG";
			case SYDNEY:
			case VIP_SYDNEY:
				return "en_AU";
			case US_EAST:
			case US_WEST:
			case US_CENTRAL:
			case US_SOUTH:
			case VIP_US_EAST:
			case VIP_US_WEST:
			case VIP_US_CENTRAL:
			case VIP_US_SOUTH:
			case UNKNOWN:
			default:
				return "en_US";
		}
	}

	public static String name(User user, Guild guild) {
		return guild != null && guild.getMember(user) != null && guild.getMember(user).getNickname() != null ? guild.getMember(user).getNickname() : user.getName();
	}

	public static String processId(String string) {
		if (string.startsWith("<@") && string.endsWith(">")) string = string.substring(2, string.length() - 1);
		if (string.startsWith("!")) string = string.substring(1);
		return string.toLowerCase();
	}

	public static void hackJDALog() {
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
