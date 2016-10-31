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
import cf.adriantodt.bot.data.Push;
import cf.adriantodt.bot.webinterface.BotWebInterface;
import cf.adriantodt.utils.ThreadBuilder;
import com.sun.management.OperatingSystemMXBean;
import net.dv8tion.jda.core.entities.User;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Tasks {
	static final Map<User, Integer> userTimeout = new HashMap<>();
	private static final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 5);
	public static double cpuUsage = 0;

	public static ExecutorService getThreadPool() {
		return threadPool;
	}

	public static void startAsyncTask(String task, Runnable scheduled, int everySeconds) {
		Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, task + "Executor")).scheduleAtFixedRate(scheduled, 0, everySeconds, TimeUnit.SECONDS);
	}

	public static void startAsyncTasks() {

		final OperatingSystemMXBean os = ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean());
		startAsyncTask("CPU Usage", () -> cpuUsage = (Math.floor(os.getProcessCpuLoad() * 10000) / 100), 2);

		startAsyncTask("User Timeout", () -> {
			synchronized (userTimeout) {
				userTimeout.replaceAll((user, integer) -> Math.max(0, integer - 1));
			}
		}, 5);
	}

	public static void startJDAAsyncTasks() {
		new ThreadBuilder().setDaemon(true).setName("Web-Interface").build(() -> new Thread(BotWebInterface::startWebServer)).start();

		new ThreadBuilder().setDaemon(true).setName("Log4j2Discord").build(() -> new Thread(() -> {
			System.out.println("Log4j2Discord Enabled!");
			Holder<String> s = new Holder<>();
			while ((s.var = QueueLogAppender.getNextLogEvent("DiscordLogListeners")) != null) {
				Push.pushSimple("log", channel -> "[LOG] " + s.var);
			}
			System.out.println("Log4j2Discord Disabled...");
		})).start();
	}
}
