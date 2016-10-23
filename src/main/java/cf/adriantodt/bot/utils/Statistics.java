/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [28/09/16 22:18]
 */

package cf.adriantodt.bot.utils;

import cf.adriantodt.bot.Bot;
import cf.adriantodt.bot.base.Permissions;
import cf.adriantodt.bot.base.cmd.CommandEvent;
import cf.adriantodt.bot.data.Guilds;
import cf.adriantodt.bot.data.I18n;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.requests.RestAction;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;

import static cf.adriantodt.bot.utils.Answers.send;
import static cf.adriantodt.bot.utils.Formatter.boldAndItalic;
import static cf.adriantodt.bot.utils.Formatter.encase;
import static cf.adriantodt.bot.utils.Tasks.cpuUsage;

@SuppressWarnings("unchecked")
public class Statistics {

	public static Date startDate = null;
	//public static int loads = 0, saves = 0, crashes = 0, noperm = 0, invalidargs = 0, msgs = 0, cmds = 0, wgets = 0, toofasts = 0;
	public static int restActions = 0, toofasts = 0, cmds = 0, crashes = 0, noperm = 0, invalidargs = 0, wgets = 0;

	static {
		try {
			Field field = RestAction.class.getField("DEFAULT_SUCCESS");
			field.setAccessible(true);

			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			field.set(null, RestAction.DEFAULT_SUCCESS.andThen(o -> Statistics.restActions++));
		} catch (Exception ignored) {

		}
	}

	public static String calculate(Date startDate, Date endDate, String language) {

		//milliseconds
		long different = endDate.getTime() - startDate.getTime();

		if (different <= 0) {
			return I18n.getLocalized("stats.negativeTime", language);
		}

		different = different / 1000;
		long minutesInMilli = 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;

		long elapsedDays = different / daysInMilli;
		different = different % daysInMilli;

		long elapsedHours = different / hoursInMilli;
		different = different % hoursInMilli;

		long elapsedMinutes = different / minutesInMilli;
		different = different % minutesInMilli;

		long elapsedSeconds = different;

		return String.format(
			I18n.getLocalized("stats.timeFormat", language),
			elapsedDays,
			elapsedHours, elapsedMinutes, elapsedSeconds);

	}

	public static int clampIfNotOwner(int value, int min, int max, User user) {
		if (Permissions.havePermsRequired(Guilds.GLOBAL, user, Permissions.GUILD_OWNER)) return value;
		return Math.min(max, Math.max(min, value));
	}

	public static int parseInt(String s, int onCatch) {
		try {
			return Integer.parseInt(s);
		} catch (Exception ignored) {
		}
		return onCatch;
	}

	public static void printStats(CommandEvent event) {
		String language = I18n.getLocale(event);
		int mb = 1024 * 1024;
		Runtime instance = Runtime.getRuntime();
		send(event,
			boldAndItalic("Estatísticas da sessão") + "\n" + encase(
				"- Ligado à " + Statistics.calculate(Statistics.startDate, new Date(), language)
					+ "\n - " + Statistics.restActions + " Rest Actions enviadas"
					+ "\n - " + Statistics.cmds + " comandos executados"
					+ "\n - " + Statistics.crashes + " crashes ocorreram"
					+ "\n - " + Statistics.toofasts + " comandos bloqueados por SpamDetection"
					+ "\n - " + Statistics.wgets + " solicitações Web"
					+ "\n - " + Thread.activeCount() + " threads ativas"
					+ "\n - Sem Permissão: " + Statistics.noperm + " / Argumentos Invalidos: " + Statistics.invalidargs
					+ "\n - Guilds conhecidas: " + Bot.API.getGuilds().size()
					+ "\n - Canais conhecidos: " + Bot.API.getTextChannels().size()
					+ "\n - Uso de RAM(Usando/Total/Máximo): " + ((instance.totalMemory() - instance.freeMemory()) / mb) + " MB/" + (instance.totalMemory() / mb) + " MB/" + (instance.maxMemory() / mb) + " MB"
					+ "\n - Uso de CPU: " + cpuUsage + "%"
			)
		).queue();
	}
}
