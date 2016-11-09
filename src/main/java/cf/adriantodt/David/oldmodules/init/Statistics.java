/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [07/11/16 20:36]
 */

package cf.adriantodt.David.oldmodules.init;


import cf.adriantodt.David.oldmodules.db.GuildModule;
import cf.adriantodt.David.oldmodules.db.I18nModule;
import cf.adriantodt.David.modules.cmds.PermissionsModule;
import cf.adriantodt.David.commands.base.CommandEvent;


import com.sun.management.OperatingSystemMXBean;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.requests.RestAction;
import org.apache.logging.log4j.LogManager;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;

import static cf.adriantodt.David.utils.Formatter.boldAndItalic;
import static cf.adriantodt.David.utils.Formatter.encase;
import static cf.adriantodt.utils.TaskManager.startAsyncTask;

@SuppressWarnings("unchecked")
public class Statistics {

	public static Date startDate = null;
	//public static int loads = 0, saves = 0, crashes = 0, noperm = 0, invalidargs = 0, msgs = 0, cmds = 0, wgets = 0, toofasts = 0;
	public static int restActions = 0, toofasts = 0, cmds = 0, crashes = 0, noperm = 0, invalidargs = 0, wgets = 0;

	public static double cpuUsage = 0;

	static {
		try {
			Field field = RestAction.class.getField("DEFAULT_SUCCESS");
			field.setAccessible(true);

			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			field.set(null, RestAction.DEFAULT_SUCCESS.andThen(o -> Statistics.restActions++));
		} catch (Exception e) {
			LogManager.getLogger("Statistics-BruteReflections").error("The hacky heavy reflection static code block crashed. #BlameSpong and #BlameMinn", e);
		}

		final OperatingSystemMXBean os = ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean());
		startAsyncTask("CPU Usage", () -> cpuUsage = (Math.floor(os.getProcessCpuLoad() * 10000) / 100), 2);
	}

	public static String calculate(Date startDate, Date endDate, String language) {

		//milliseconds
		long different = endDate.getTime() - startDate.getTime();

		if (different <= 0) {
			return I18nModule.getLocalized("stats.negativeTime", language);
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
			I18nModule.getLocalized("stats.timeFormat", language),
			elapsedDays,
			elapsedHours, elapsedMinutes, elapsedSeconds);

	}

	public static int clampIfNotOwner(int value, int min, int max, User user) {
		if (PermissionsModule.havePermsRequired(GuildModule.GLOBAL, user, PermissionsModule.GUILD_OWNER)) return value;
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
		String language = I18nModule.getLocale(event);
		int mb = 1024 * 1024;
		Runtime instance = Runtime.getRuntime();
		event.getAnswers().send(
			boldAndItalic("Estatísticas da sessão") + "\n" + encase(
				"- Ligado à " + Statistics.calculate(Statistics.startDate, new Date(), language)
					+ "\n - " + Statistics.restActions + " Rest Actions enviadas"
					+ "\n - " + Statistics.cmds + " comandos executados"
					+ "\n - " + Statistics.crashes + " crashes ocorreram"
					+ "\n - " + Statistics.toofasts + " comandos bloqueados por SpamDetection"
					+ "\n - " + Statistics.wgets + " solicitações Web"
					+ "\n - " + Thread.activeCount() + " threads ativas"
					+ "\n - Sem Permissão: " + Statistics.noperm + " / Argumentos Invalidos: " + Statistics.invalidargs
					+ "\n - Guilds conhecidas: " + event.getJDA().getGuilds().size()
					+ "\n - Canais conhecidos: " + event.getJDA().getTextChannels().size()
					+ "\n - Uso de RAM(Usando/Total/Máximo): " + ((instance.totalMemory() - instance.freeMemory()) / mb) + " MB/" + (instance.totalMemory() / mb) + " MB/" + (instance.maxMemory() / mb) + " MB"
					+ "\n - Uso de CPU: " + cpuUsage + "%"
			)
		).queue();
	}
}
