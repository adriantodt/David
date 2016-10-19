/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [28/09/16 22:13]
 */

package cf.adriantodt.bot.utils;

import cf.adriantodt.bot.base.cmd.Holder;
import cf.adriantodt.bot.base.gui.QueueLogAppender;
import cf.adriantodt.bot.handlers.Spy;
import cf.brforgers.core.lib.IOHelper;
import com.sun.management.OperatingSystemMXBean;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Tasks {
	public static final Holder<String> latestDrama = new Holder<String>() {{
		var = "Hold up a sec.";
	}};
	static final Map<User, Integer> userTimeout = new HashMap<>();
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
		startAsyncTask(() -> cpuUsage = (Math.floor(os.getProcessCpuLoad() * 10000) / 100), 2);

		startAsyncTask(() -> {
			synchronized (userTimeout) {
				userTimeout.replaceAll((user, integer) -> Math.max(0, integer - 1));
			}
		}, 5);

		Thread thread = new Thread(() -> {
			synchronized (Spy.channels) {
				Holder<String> s = new Holder<>();
				while ((s.var = QueueLogAppender.getNextLogEvent("DiscordListeners")) != null)
					Spy.logs().forEach(channel -> channel.sendMessage(s.var).queue());
			}
		});
		thread.setName("Discord Log Listening");
		thread.setDaemon(true);
		thread.start();
	}

	public static void startJDAAsyncTasks(JDA jda) {

	}
}
