/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [28/09/16 22:09]
 */

package cf.adriantodt.bot.handlers;

import cf.adriantodt.bot.Bot;
import cf.adriantodt.bot.base.DiscordGuild;
import cf.adriantodt.bot.base.Permissions;
import cf.adriantodt.bot.base.cmd.ICommand;
import cf.adriantodt.bot.persistence.DataManager;
import cf.adriantodt.bot.utils.Commands;
import cf.adriantodt.bot.utils.Statistics;
import cf.adriantodt.bot.utils.Utils;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import static cf.adriantodt.bot.utils.Answers.*;
import static cf.adriantodt.bot.utils.Utils.splitArgs;

public class CommandHandler extends ListenerAdapter {
	public static boolean cleanup = true, toofast = true;
	private static Map<MessageReceivedEvent, ICommand> map = new WeakHashMap<>();
	private static Map<MessageReceivedEvent, DiscordGuild> map2 = new WeakHashMap<>();

	public static void execute(ICommand command, DiscordGuild guild, String arguments, MessageReceivedEvent event) {
		if (Permissions.canRunCommand(DiscordGuild.GLOBAL, event, command) || Permissions.canRunCommand(guild, event, command))
			command.run(guild, arguments, event);
		else noperm(event);
	}

	public static ICommand getSelf(MessageReceivedEvent event) {
		return map.get(event);
	}

	public static DiscordGuild getGuild(MessageReceivedEvent event) {
		return map2.get(event);
	}

	public static void onTree(MessageReceivedEvent event, ICommand command) {
		map.put(event, command);
	}

	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor() == Bot.SELF) { //Safer
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
		boolean exec = false;

		String baseCmd = splitArgs(cmd, 2)[0];
		if (!baseCmd.isEmpty() && (baseCmd.charAt(0) == '?' || baseCmd.charAt(0) == '.')) { //Is Command
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
			ICommand command = Commands.getCommands(target).get(baseCmd.toLowerCase());

			if (command != null) {
				map.put(event, command);
				map2.put(event, target);
				if (!Permissions.canRunCommand(target, event, command)) noperm(event);
				else if (toofast && !Utils.canExecuteCmd(event)) {
					toofast(event);
					Statistics.toofasts++;
					return;
				} else {
					Statistics.cmds++;
					exec = true;
					try {
						execute(command, target, splitArgs(cmd, 2)[1], event);
					} catch (Exception e) {
						exception(event, e);
					}
				}
				//remove the map and map2 key removal because WeakHashMap does it now.
			}
		}

		if (!exec) {
			List<String> list = DataManager.data.annoy.get(Permissions.processID(event.getAuthor().getId()));
			if (list != null) {
				String r = list.get(Bot.RAND.nextInt(list.size()));
				if (r != null && !r.isEmpty()) {
					send(event, r);
				}
			}
		}

		Bot.setDefault();
	}
}
