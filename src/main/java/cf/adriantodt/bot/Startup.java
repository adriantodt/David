/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [22/09/16 18:37]
 */

package cf.adriantodt.bot;

import cf.adriantodt.bot.gui.BotGui;
import cf.adriantodt.bot.utils.DiscordUtils;
import cf.adriantodt.bot.utils.Tasks;
import cf.adriantodt.utils.AsyncUtils;
import cf.adriantodt.utils.Java;
import cf.adriantodt.utils.Log4jUtils;
import cf.adriantodt.utils.TaskManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class Startup {
	public static Logger LOGGER = LogManager.getLogger("Startup");
	public static BotGui UI = null;

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static void main(String[] args) {
		LOGGER.info("Pre-Initializating...");
		try {
			File file = new File("./tmp/");
			if (file.exists())
				delete(file);
			file.mkdir();
			System.setProperty("java.io.tmpdir", file.getCanonicalPath());
		} catch (Exception e) {
			LOGGER.error("Error while trying to define TMPDir: ", e);
		}
		LOGGER.info("TMP Directory: " + System.getProperty("java.io.tmpdir"));

		if (GraphicsEnvironment.isHeadless()) {
			LOGGER.info("GUI Disabled. (Headless Environiment)");
		}
		else if (Arrays.stream(args).filter("nogui"::equals).findAny().orElse(null) != null) {
			LOGGER.info("GUI Disabled. (parameter \"nogui\")");
		}
		else {
			LOGGER.info("Loading GUI...");
			UI = BotGui.createBotGui();
		}

		Log4jUtils.hackStdout();
		DiscordUtils.hackJDALog();

		try {
			Bot.init();
		} catch (Exception e) {
			LOGGER.error("An exception was caught during Initialization: ", e);
			Java.stopApp();
		}
	}

	@SuppressWarnings("ConstantConditions")
	private static void delete(File f) throws IOException {
		if (f.isDirectory()) {
			for (File c : f.listFiles())
				delete(c);
		}
		if (!f.delete())
			throw new FileNotFoundException("Failed to delete file: " + f);
	}
}
