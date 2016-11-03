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

import cf.adriantodt.bot.commands.base.Holder;
import cf.adriantodt.bot.data.entities.Feeds;
import cf.adriantodt.bot.data.entities.Pushes;
import cf.adriantodt.bot.gui.QueueLogAppender;
import cf.adriantodt.bot.webinterface.BotWebInterface;
import cf.adriantodt.utils.TaskManager;
import cf.adriantodt.utils.ThreadBuilder;
import com.sun.management.OperatingSystemMXBean;
import net.dv8tion.jda.core.entities.User;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

import static cf.adriantodt.utils.TaskManager.startAsyncTask;

public class Tasks {
	public static final Map<User, Integer> userTimeout = new HashMap<>();
	public static double cpuUsage = 0;

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
		TaskManager.startAsyncTask("Feed Main Task", Feeds::loop, 5);

		new ThreadBuilder().setDaemon(true).setName("Web-Interface").build(() -> new Thread(BotWebInterface::startWebServer)).start();

		new ThreadBuilder().setDaemon(true).setName("Log4j2Discord").build(() -> new Thread(() -> {
			System.out.println("Log4j2Discord Enabled!");
			Holder<String> s = new Holder<>();
			while ((s.var = QueueLogAppender.getNextLogEvent("DiscordLogListeners")) != null) {
				Pushes.pushSimple("get", channel -> "[LOG] " + s.var);
			}
			System.out.println("Log4j2Discord Disabled...");
		})).start();
	}
}
