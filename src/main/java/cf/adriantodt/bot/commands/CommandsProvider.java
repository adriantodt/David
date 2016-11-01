/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [01/11/16 12:35]
 */

package cf.adriantodt.bot.commands;

import cf.adriantodt.bot.commands.base.ICommand;
import cf.adriantodt.bot.commands.base.UserCommand;
import cf.adriantodt.bot.data.entities.Guilds;
import cf.adriantodt.bot.data.entities.UserCommands;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandsProvider {
	public static final Map<String, ICommand> COMMANDS = new HashMap<>();

	public static void addCommand(String name, ICommand command) {
		COMMANDS.put(name, command);
	}

	public static Map<String, ICommand> getCommands(Guilds.Data guild) {
		return concatMaps(getBaseCommands(), new HashMap<>(getUserCommands(guild)));
	}

	public static Map<String, ICommand> getBaseCommands() {
		return CommandsProvider.COMMANDS;
	}

	public static Map<String, UserCommand> getUserCommands(Guilds.Data guild) {
		return concatMaps(getGlobalUserCommands(), getLocalUserCommands(guild));
	}

	public static Map<String, UserCommand> getGlobalUserCommands() {
		return getLocalUserCommands(Guilds.GLOBAL);
	}

	public static Map<String, UserCommand> getLocalUserCommands(Guilds.Data guild) {
		return UserCommands.allFrom(guild);
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
