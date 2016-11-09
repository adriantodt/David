/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [07/11/16 20:36]
 */

package cf.adriantodt.David.oldmodules.gui;

import cf.adriantodt.David.Loader;
import cf.adriantodt.David.loader.Module;
import cf.adriantodt.David.loader.Module.LoggerInstance;
import cf.adriantodt.David.loader.Module.OnDisabled;
import cf.adriantodt.David.loader.Module.OnEnabled;
import cf.adriantodt.David.loader.Module.Predicate;
import cf.adriantodt.David.oldmodules.gui.impl.BotGui;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.Arrays;

import static cf.adriantodt.David.loader.Module.Type.STATIC;

@Module(STATIC)
public class GUIModule {
	@LoggerInstance
	private static Logger logger = null;
	private static BotGui UI;

	@Predicate
	public static boolean enable() {
		return !GraphicsEnvironment.isHeadless() && Arrays.stream(Loader.args).filter("nogui"::equals).findAny().isPresent();
	}

	@OnEnabled
	public static void enabled() {
		logger.info("Loading GUI...");
		UI = BotGui.createBotGui();
	}

	@OnDisabled
	public static void disabled() {
		if (GraphicsEnvironment.isHeadless()) {
			logger.info("GUI Disabled. (Headless Environiment)");
		} else logger.info("GUI Disabled. (parameter \"nogui\")");
	}
}
