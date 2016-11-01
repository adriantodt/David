/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [01/11/16 13:22]
 */

package cf.adriantodt.utils;

public class AsyncUtils {
	public static Runnable async(final Runnable doAsync) {
		return new Thread(doAsync)::start;
	}

	public static Runnable async(final String name, final Runnable doAsync) {
		return new Thread(doAsync, name)::start;
	}

	public static void sleep(int milis) {
		try {
			Thread.sleep(milis);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Runnable asyncSleepThen(final int milis, final Runnable doAfter) {
		return async(() -> {
			sleep(milis);
			if (doAfter != null) doAfter.run();
		});
	}
}
