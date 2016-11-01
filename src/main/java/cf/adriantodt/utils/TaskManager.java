/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [01/11/16 13:09]
 */

package cf.adriantodt.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TaskManager {
	private static final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 5);

	public static ExecutorService getThreadPool() {
		return threadPool;
	}

	public static void startAsyncTask(String task, Runnable scheduled, int everySeconds) {
		Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, task + "Executor")).scheduleAtFixedRate(scheduled, 0, everySeconds, TimeUnit.SECONDS);
	}
}
