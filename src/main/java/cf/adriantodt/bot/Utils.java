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

import cf.adriantodt.bot.base.cmd.Holder;
import cf.adriantodt.bot.base.gui.QueueLogAppender;
import cf.adriantodt.bot.impl.Spy;
import cf.brforgers.core.lib.IOHelper;
import com.sun.management.OperatingSystemMXBean;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Utils {
	public static final Holder<String> latestDrama = new Holder<String>() {{
		var = "Hold up a sec.";
	}};
	private static final Map<User, Integer> userTimeout = new HashMap<>();
	public static double cpuUsage = 0;

	public static void startAsyncTask(Runnable scheduled, int everySeconds) {
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(scheduled, 0, everySeconds, TimeUnit.SECONDS);
	}

	public static void startAsyncTasks() {
		startAsyncTask(() -> {
			synchronized (latestDrama) {
				latestDrama.var = IOHelper.toString("https://drama.thog.eu/api/drama");
			}
		}, 10);

		final OperatingSystemMXBean os = ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean());
		startAsyncTask(() -> cpuUsage = os.getProcessCpuLoad(), 2);

		startAsyncTask(() -> {
			synchronized (userTimeout) {
				userTimeout.replaceAll((user, integer) -> Math.max(0, integer - 1));
			}
		}, 5);

		Thread thread = new Thread(() -> {
			synchronized (Spy.logListeners) {
				Holder<String> s = new Holder<>();
				while ((s.var = QueueLogAppender.getNextLogEvent("DiscordListeners")) != null)
					Spy.logListeners.forEach(channel -> channel.sendMessageAsync(s.var, null));
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

	public static boolean canExecuteCmd(MessageReceivedEvent event) {
		int count;
		synchronized (userTimeout) {
			count = userTimeout.getOrDefault(event.getAuthor(), 0);
			userTimeout.put(event.getAuthor(), count + 1);
		}
		return count + 1 < 5;
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

}
