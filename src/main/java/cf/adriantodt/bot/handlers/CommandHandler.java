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
import cf.adriantodt.bot.base.Permissions;
import cf.adriantodt.bot.base.cmd.ICommand;
import cf.adriantodt.bot.data.Guilds;
import cf.adriantodt.bot.utils.Commands;
import cf.adriantodt.bot.utils.Statistics;
import cf.adriantodt.bot.utils.Utils;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cf.adriantodt.bot.utils.Answers.*;
import static cf.adriantodt.bot.utils.Utils.asyncSleepThen;
import static cf.adriantodt.bot.utils.Utils.splitArgs;

public class CommandHandler {
	public static boolean toofast = true;
	private static Map<MessageReceivedEvent, ICommand> map = new HashMap<>();
	private static Map<MessageReceivedEvent, Guilds.Data> map2 = new HashMap<>();

	public static void execute(ICommand command, Guilds.Data guild, String arguments, MessageReceivedEvent event) {
		if (Permissions.canRunCommand(Guilds.GLOBAL, event, command) || Permissions.canRunCommand(guild, event, command))
			command.run(guild, arguments, event);
		else noperm(event);
	}

	public static ICommand getSelf(MessageReceivedEvent event) {
		return map.get(event);
	}

	public static Guilds.Data getGuild(MessageReceivedEvent event) {
		return map2.getOrDefault(event, Guilds.fromDiscord(event));
	}

	public static void onTree(MessageReceivedEvent event, ICommand command) {
		map.put(event, command);
	}

	@SubscribeEvent
	public static void onMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor() == Bot.SELF) { //Safer
			asyncSleepThen(15 * 1000, () -> {
				if (Guilds.fromDiscord(event).getFlag("cleanup")) event.getMessage().deleteMessage();
			}).run();
			return;
		}

		Guilds.Data local = Guilds.fromDiscord(event.getGuild()), global = Guilds.GLOBAL, target = local;
		if (!Permissions.havePermsRequired(global, event, Permissions.RUN_BASECMD) || !Permissions.havePermsRequired(local, event, Permissions.RUN_BASECMD))
			return;

		String cmd = event.getMessage().getRawContent();

		List<String> prefixes = new ArrayList<>(local.getCmdPrefixes());
		prefixes.add("<@!" + Bot.SELF.getId() + "> ");
		prefixes.add("<@" + Bot.SELF.getId() + "> ");
		boolean isCmd = false;
		for (String prefix : prefixes) {
			if (cmd.startsWith(prefix)) {
				cmd = cmd.substring(prefix.length());
				isCmd = true;
				break;
			}
		}

		//boolean exec = false;
		if (isCmd) { //Is Command
			String baseCmd = splitArgs(cmd, 2)[0];
			//GuildWorksTM
			if (baseCmd.indexOf(':') != -1) {
				String guildname = baseCmd.substring(0, baseCmd.indexOf(':'));
				baseCmd = baseCmd.substring(baseCmd.indexOf(':') + 1);

				Guilds.Data guild = Guilds.fromName(guildname);
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
//					return;
				} else {
					Statistics.cmds++;
					//exec = true;
					try {
						execute(command, target, splitArgs(cmd, 2)[1], event);
					} catch (Exception e) {
						exception(event, e);
					}
				}
				//remove the map and map2 key removal because WeakHashMap does it now.
			}
		}

//		if (!exec) {
//			List<String> list = DataManager.data.annoy.get(Permissions.processID(event.getAuthor().getId()));
//			if (list != null) {
//				String r = list.get(Bot.RAND.nextInt(list.size()));
//				if (r != null && !r.isEmpty()) {
//					send(event, r);
//				}
//			}
//		}
	}
}
