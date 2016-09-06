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

package cf.adriantodt.bot.impl;

import cf.adriantodt.bot.Statistics;
import cf.adriantodt.bot.Utils;
import cf.adriantodt.bot.base.cmd.ICommand;
import cf.adriantodt.bot.base.cmd.UserCommand;
import cf.adriantodt.bot.base.guild.DiscordGuild;
import cf.adriantodt.bot.base.perm.Permissions;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cf.adriantodt.bot.Answers.*;
import static cf.adriantodt.bot.Bot.BOTID;
import static cf.adriantodt.bot.Bot.setDefault;
import static cf.adriantodt.bot.Statistics.toofasts;
import static cf.adriantodt.bot.Utils.splitArgs;

public class EventHandler {
	public static boolean cleanup = true;
	private static Map<MessageReceivedEvent, ICommand> map = new HashMap<>();
	private static Map<MessageReceivedEvent, DiscordGuild> map2 = new HashMap<>();
	private static Map<MessageReceivedEvent, String> map3 = new HashMap<>();

	public static void handle(MessageReceivedEvent event) {
		if (BOTID.equals(event.getAuthor().getId())) { //Safer
			new Thread(() -> {
				try {
					Thread.sleep(15 * 1000);
					if (cleanup) event.getMessage().deleteMessage();
				} catch (Exception ignored) {
				}
			}).start();
			return;
		}

		DiscordGuild local = DiscordGuild.fromDiscord(event.getGuild()), global = DiscordGuild.GLOBAL, target = local;
		if (!Permissions.havePermsRequired(global, event, Permissions.RUN_BASECMD) || !Permissions.havePermsRequired(local, event, Permissions.RUN_BASECMD))
			return;


		String cmd = event.getMessage().getRawContent();

		String baseCmd = splitArgs(cmd, 2)[0];
		if (!baseCmd.isEmpty() && (baseCmd.charAt(0) == '?' || baseCmd.charAt(0) == '&')) { //Is Command
			baseCmd = baseCmd.substring(1); //We don't need the Slash Char

			//GuildWorksTM
			if (baseCmd.indexOf(':') != -1) {
				String guildname = baseCmd.substring(0, baseCmd.indexOf(':'));
				baseCmd = baseCmd.substring(baseCmd.indexOf(':') + 1);

				DiscordGuild guild = DiscordGuild.fromName(guildname);
				if (guild != null && (Permissions.havePermsRequired(guild, event, Permissions.GUILD_PASS) || Permissions.havePermsRequired(global, event, Permissions.GUILD_PASS)))
					target = guild;
			}

			//Oldest code that exists
			ICommand command = getCommands(target).get(baseCmd.toLowerCase());

			if (command != null) {
				map.put(event, command);
				map2.put(event, target);
				if (!Permissions.canRunCommand(target, event, command)) noperm(event);
				else if (!Utils.canExecuteCmd(event)) {
					toofast(event);
					toofasts++;
					return;
				} else {
					Statistics.cmds++;

					try {
						execute(command, target, splitArgs(cmd, 2)[1], event);
					} catch (Exception e) {
						exception(event, e);
					}
				}
				map.remove(event);
				map2.remove(event);
			}
		}
		setDefault();
	}

	public static void execute(ICommand command, DiscordGuild guild, String arguments, MessageReceivedEvent event) {
		if (Permissions.canRunCommand(DiscordGuild.GLOBAL, event, command) || Permissions.canRunCommand(guild, event, command))
			command.run(guild, arguments, event);
		else noperm(event);
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

	public static ICommand getSelf(MessageReceivedEvent event) {
		return map.get(event);
	}

	public static DiscordGuild getGuild(MessageReceivedEvent event) {
		return map2.get(event);
	}
}
