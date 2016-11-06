/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [22/09/16 18:35]
 */

package cf.adriantodt.oldbot;

import cf.adriantodt.oldbot.data.DataManager;
import cf.adriantodt.oldbot.data.entities.I18n;
import cf.adriantodt.David.modules.cmds.Pushes;
import cf.adriantodt.David.modules.init.MergeTasksWithInitModule;
import org.apache.logging.log4j.LogManager;

public class Bot {

	public static void init() throws Exception {
		DataManager.init();
		MergeTasksWithInitModule.startAsyncTasks();

		LOADED = true;
		onLoaded.forEach(Runnable::run);
		onLoaded = null;
		LOGGER = LogManager.getLogger(SELF.getName());
		LOGGER.info("Bot: " + SELF.getName() + " (#" + SELF.getId() + ")");
		//LOGGER.info("ConfigUtils: " + DataManager.getSaveFile().toAbsolutePath().toString());
		MergeTasksWithInitModule.startJDAAsyncTasks();

		Pushes.pushSimple("start", channel -> I18n.getLocalized("bot.startup", channel));
	}


}
