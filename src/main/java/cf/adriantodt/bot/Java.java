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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
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

	public static void hackStdout() {
		System.setOut(new PrintStream(new LoggerStream(LogManager.getLogger("OUT"), Level.INFO)));
		System.setErr(new PrintStream(new LoggerStream(LogManager.getLogger("ERR"), Level.ERROR)));
	}


	public static class LoggerStream extends OutputStream {
		private final Logger logger;
		private final Level logLevel;

		public LoggerStream(Logger logger, Level logLevel) {
			super();

			this.logger = logger;
			this.logLevel = logLevel;
		}

		@Override
		public void write(byte[] b) throws IOException {
			String string = new String(b);
			if (!string.trim().isEmpty())
				logger.log(logLevel, string);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			String string = new String(b, off, len);
			if (!string.trim().isEmpty())
				logger.log(logLevel, string);
		}

		@Override
		public void write(int b) throws IOException {
			String string = String.valueOf((char) b);
			if (!string.trim().isEmpty())
				logger.log(logLevel, string);
		}
	}
}
