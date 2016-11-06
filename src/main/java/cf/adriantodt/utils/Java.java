/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [01/11/16 13:12]
 */

package cf.adriantodt.utils;

import cf.adriantodt.oldbot.Bot;

import java.io.File;
import java.util.ArrayList;

public class Java {
	public static void restartApp() {
		try {
			final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
			final File currentJar = new File(Bot.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			if (!currentJar.getName().endsWith(".jar")) throw new RuntimeException("Can't find jar!");
			final ArrayList<String> command = new ArrayList<>();
			command.add(javaBin);
			command.add("-jar");
			command.add(currentJar.getPath());

			final ProcessBuilder builder = new ProcessBuilder(command);
			builder.start();
			stopApp();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void stopApp() {
		System.exit(0);
	}
}
