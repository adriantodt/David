/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [18/10/16 21:21]
 */

package cf.adriantodt.bot.utils;

@FunctionalInterface
public interface CheckedRunnable<T, E extends Exception> {
	T run() throws E;

	default T runOrThrowRuntime() {
		try {
			return run();
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}
}