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

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.handle.EntityBuilder;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static cf.adriantodt.bot.utils.Tasks.userTimeout;

public class Utils {
	public static boolean canExecuteCmd(MessageReceivedEvent event) {
		int count;
		synchronized (userTimeout) {
			count = userTimeout.getOrDefault(event.getAuthor(), 0);
			userTimeout.put(event.getAuthor(), count + 1);
		}
		return count + 1 < 5;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, JSONObject> cachedJdaGuildJsons(JDA jda) throws NoSuchFieldException, IllegalAccessException {
		Field f = EntityBuilder.class.getField("cachedJdaGuildJsons");
		f.setAccessible(true);
		HashMap<JDA, HashMap<String, JSONObject>> base = (HashMap<JDA, HashMap<String, JSONObject>>) f.get(null);
		return base.get(jda);
	}

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


	public static String[] splitArgs(String args, int expectedArgs) {
		String[] raw = args.split("\\s+", expectedArgs), normalized = new String[expectedArgs];

		Arrays.fill(normalized, "");
		for (int i = 0; i < normalized.length; i++) {
			if (i < raw.length && raw[i] != null && !raw[i].isEmpty()) {
				normalized[i] = raw[i];
			}
		}
		return normalized;
	}

	public static String name(User user, Guild guild) {
		return (guild.getNicknameForUser(user) == null ? user.getUsername() : guild.getNicknameForUser(user));
	}

	public static String nnOrD(String str, String defaultStr) {
		if (str == null || str.trim().isEmpty()) return defaultStr;
		return str;
	}

	public static Runnable asyncSleepThen(final int milis, final Runnable doAfter) {
		return new Thread(() -> {
			try {
				Thread.sleep(milis);
				if (doAfter != null) doAfter.run();
			} catch (Exception ignored) {
			}
		})::start;
	}

	public static String limit(String value, int length) {
		StringBuilder buf = new StringBuilder(value);
		if (buf.length() > length) {
			buf.setLength(length - 3);
			buf.append("...");
		}

		return buf.toString();
	}
}
