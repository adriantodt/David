/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [28/09/16 22:16]
 */

package cf.adriantodt.bot.utils;

import cf.adriantodt.bot.base.DiscordGuild;
import cf.adriantodt.bot.base.cmd.ICommand;
import cf.adriantodt.bot.base.cmd.UserCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Commands {
	public static final Map<String, ICommand> COMMANDS = new HashMap<>();

	public static void addCommand(String name, ICommand command) {
		COMMANDS.put(name, command);
	}

	public static Map<String, ICommand> getCommands(DiscordGuild guild) {
		HashMap<String, ICommand> usercmds = new HashMap<>();
		usercmds.putAll(concatMaps(getGlobalUserCommands(), getLocalUserCommands(guild)));
		return concatMaps(getBaseCommands(), usercmds);
	}

	public static Map<String, ICommand> getBaseCommands() {
		return Commands.COMMANDS;
	}

	public static Map<String, UserCommand> getGlobalUserCommands() {
		return getLocalUserCommands(DiscordGuild.GLOBAL);
	}

	public static Map<String, UserCommand> getLocalUserCommands(DiscordGuild guild) {
		return guild.commands;
	}

	private static <T, U> Map<T, U> concatMaps(Map<T, U> map1, Map<T, U> map2) {
		return Stream.concat(map1.entrySet().stream(), map2.entrySet().stream())
			.collect(Collectors.toMap(
				Map.Entry::getKey,
				Map.Entry::getValue,
				(entry1, entry2) -> (entry1 == null ? entry2 : entry1)
				)
			);
	}
}
