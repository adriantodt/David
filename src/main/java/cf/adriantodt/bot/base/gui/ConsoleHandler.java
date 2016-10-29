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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static cf.adriantodt.bot.base.gui.GuiTranslationHandler.get;
import static cf.adriantodt.bot.utils.Utils.splitArgs;

public class ConsoleHandler {
	public static final Map<String, BiConsumer<String, Consumer<String>>> CMDS = new HashMap<>();
	public static final Map<BiConsumer<String, Consumer<String>>, String> HELP = new HashMap<>();

	static {
		CMDS.put("?", (s, in) -> CMDS.entrySet().stream().map(entry -> entry.getKey() + " - " + get("cmds." + HELP.get(entry.getValue()))).sorted().forEach(in));
		CMDS.put("help", CMDS.get("?"));
		CMDS.put("cmds", CMDS.get("?"));
		HELP.put(CMDS.get("?"), "help");

		CMDS.put("stop", (s, in) -> Bot.stopBot());
		HELP.put(CMDS.get("stop"), "stop");

		CMDS.put("threads", (s, in) -> Thread.getAllStackTraces().keySet().forEach(t -> in.accept(t.getName())));

//		CMDS.put("load", (s, in) -> {
//			in.accept(get("load"));
//			DataManager.loadData();
//			in.accept(get("done"));
//		});
//		HELP.put(CMDS.get("load"), "load");
//
//		CMDS.put("save", (s, in) -> {
//			in.accept(get("save"));
//			DataManager.saveData();
//			in.accept(get("done"));
//		});
//		HELP.put(CMDS.get("save"), "save");

		CMDS.put("lang", (s, in) -> GuiTranslationHandler.setLang(s));
		HELP.put(CMDS.get("lang"), "lang");
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
