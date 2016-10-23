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

import cf.adriantodt.bot.Bot;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.regex.Matcher;

import static cf.adriantodt.bot.utils.Tasks.userTimeout;

public class Utils {
	public static boolean canExecuteCmd(GuildMessageReceivedEvent event) {
		int count;
		synchronized (userTimeout) {
			count = userTimeout.getOrDefault(event.getAuthor(), 0);
			userTimeout.put(event.getAuthor(), count + 1);
		}
		return count + 1 < 5;
	}

	public static String guessGuildLanguage(Guild guild) {
		switch (guild.getRegion()) {
//			case BRAZIL:
//			case VIP_BRAZIL:
//				return "pt_BR";
			case AMSTERDAM:
//			case EU_WEST:
//			case EU_CENTRAL:
			case FRANKFURT:
			case LONDON:
//			case VIP_AMSTERDAM:
//			case VIP_EU_WEST:
//			case VIP_EU_CENTRAL:
//			case VIP_FRANKFURT:
//			case VIP_LONDON:
				return "en_GB";
			case SINGAPORE:
//			case VIP_SINGAPORE:
				return "en_SG";
			case SYDNEY:
//			case VIP_SYDNEY:
				return "en_AU";
			case US_EAST:
			case US_WEST:
			case US_CENTRAL:
			case US_SOUTH:
//			case VIP_US_EAST:
//			case VIP_US_WEST:
//			case VIP_US_CENTRAL:
//			case VIP_US_SOUTH:
			case UNKNOWN:
			default:
				return "en_US";
		}
	}


	public static String[] splitArgs(String args, int expectedArgs) {
		String[] raw = args.split("\\s+", expectedArgs);
		if (expectedArgs < 1) return raw;
		String[] normalized = new String[expectedArgs];

		Arrays.fill(normalized, "");
		for (int i = 0; i < normalized.length; i++) {
			if (i < raw.length && raw[i] != null && !raw[i].isEmpty()) {
				normalized[i] = raw[i];
			}
		}
		return normalized;
	}

	public static String name(User user, Guild guild) {
		return guild != null && guild.getMember(user) != null && guild.getMember(user).getNickname() != null ? guild.getMember(user).getNickname() : user.getName();
	}

	public static String nnOrD(String str, String defaultStr) {
		if (str == null || str.trim().isEmpty()) return defaultStr;
		return str;
	}

	public static Runnable async(final Runnable doAsync) {
		return new Thread(doAsync)::start;
	}

	public static Runnable async(final String name, final Runnable doAsync) {
		return new Thread(doAsync, name)::start;
	}

	public static void sleep(int milis) {
		try {
			Thread.sleep(milis);
		} catch (Exception ignored) {
		}
	}

	public static Runnable asyncSleepThen(final int milis, final Runnable doAfter) {
		return async(() -> {
			sleep(milis);
			if (doAfter != null) doAfter.run();
		});
	}

	public static String limit(String value, int length) {
		StringBuilder buf = new StringBuilder(value);
		if (buf.length() > length) {
			buf.setLength(length - 3);
			buf.append("...");
		}

		return buf.toString();
	}

	public static <T> T random(List<T> list) {
		return list.get(Bot.RAND.nextInt(list.size()));
	}

	public static <T> T random(T[] array) {
		return array[Bot.RAND.nextInt(array.length)];
	}

	public static String processId(String string) {
		if (string.startsWith("<@") && string.endsWith(">")) string = string.substring(2, string.length() - 1);
		if (string.startsWith("!")) string = string.substring(1);
		return string.toLowerCase();
	}

	public static Iterable<String> iterate(Matcher matcher) {
		return new Iterable<String>() {
			@Override
			public Iterator<String> iterator() {
				return new Iterator<String>() {
					@Override
					public boolean hasNext() {
						return matcher.find();
					}

					@Override
					public String next() {
						return matcher.group();
					}
				};
			}

			@Override
			public void forEach(Consumer<? super String> action) {
				while (matcher.find()) {
					action.accept(matcher.group());
				}
			}

			@Override
			public Spliterator<String> spliterator() {
				System.out.println("No. No, don't. NO DON'T. DIE.");
				throw new NotImplementedException("I am too lazy to Implement this.");
			}
		};
	}
}
