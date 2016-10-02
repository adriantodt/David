/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [02/10/16 17:21]
 */

package cf.adriantodt.bot.base.gui;

import cf.adriantodt.bot.Bot;
import cf.adriantodt.bot.persistence.DataManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static cf.adriantodt.bot.utils.Utils.splitArgs;

public class ConsoleHandler {
	public static final Map<String, BiConsumer<String, Consumer<String>>> CMDS = new HashMap<>();
	public static final Map<BiConsumer<String, Consumer<String>>, String> HELP = new HashMap<>();

	static {
		CMDS.put("?", (s, in) -> CMDS.entrySet().stream().map(entry -> entry.getKey() + " - " + HELP.get(entry.getValue())).sorted().forEach(in));
		CMDS.put("help", CMDS.get("?"));
		CMDS.put("cmds", CMDS.get("?"));
		HELP.put(CMDS.get("?"), "Show all Console Commands");

		CMDS.put("stop", (s, in) -> Bot.stopBot());
		HELP.put(CMDS.get("stop"), "Stop the Bot");

		CMDS.put("restart", (s, in) -> Bot.restartBot());
		HELP.put(CMDS.get("restart"), "Restart the Bot");

		CMDS.put("load", (s, in) -> DataManager.loadData());
		HELP.put(CMDS.get("load"), "Load the Bot Data from Disk");

		CMDS.put("save", (s, in) -> DataManager.saveData());
		HELP.put(CMDS.get("save"), "Save the Bot Data to Disk");
	}

	public static void handle(String command, Consumer<String> out) {
		String[] parts = splitArgs(command, 2);
		BiConsumer<String, Consumer<String>> cmd = CMDS.getOrDefault(parts[0].toLowerCase(), CMDS.get("?"));
		cmd.accept(parts[1], in -> out.accept("<" + parts[0] + "> " + in));
	}

	public static Consumer<String> wrap(Consumer<String> c) {
		SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
		return s -> c.accept(
			"[" + f.format(new Date()) + "] [Console]: " + s
		);
	}
}
