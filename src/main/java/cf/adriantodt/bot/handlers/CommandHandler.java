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
import cf.adriantodt.bot.base.cmd.CommandEvent;
import cf.adriantodt.bot.base.cmd.ICommand;
import cf.adriantodt.bot.data.Guilds;
import cf.adriantodt.bot.utils.Commands;
import cf.adriantodt.bot.utils.Statistics;
import cf.adriantodt.bot.utils.Utils;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

import static cf.adriantodt.bot.utils.Utils.asyncSleepThen;
import static cf.adriantodt.bot.utils.Utils.splitArgs;

public class CommandHandler {
	public static boolean toofast = true;

	public static void execute(CommandEvent event) {
		if (Permissions.canRunCommand(Guilds.GLOBAL, event) || Permissions.canRunCommand(event.getGuild(), event))
			event.getCommand().run(event);
		else event.getAnswers().noperm().queue();
	}

	@SubscribeEvent
	public static void onMessageReceived(GuildMessageReceivedEvent msgEvent) {
		if (msgEvent.getAuthor().equals(Bot.SELF)) {
			asyncSleepThen(15 * 1000, () -> {
				if (Guilds.fromDiscord(msgEvent.getGuild()).getFlag("cleanup")) msgEvent.getMessage().deleteMessage();
			}).run();
			return;
		}

		Guilds.Data local = Guilds.fromDiscord(msgEvent.getGuild()), global = Guilds.GLOBAL, target = local;
		if (!Permissions.havePermsRequired(global, msgEvent.getAuthor(), Permissions.RUN_BASECMD) || !Permissions.havePermsRequired(local, msgEvent.getAuthor(), Permissions.RUN_BASECMD))
			return;

		String cmd = msgEvent.getMessage().getRawContent();

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

		if (isCmd) {
			String baseCmd = splitArgs(cmd, 2)[0];
			//GuildWorksTM
			if (baseCmd.indexOf(':') != -1) {
				String guildname = baseCmd.substring(0, baseCmd.indexOf(':'));
				baseCmd = baseCmd.substring(baseCmd.indexOf(':') + 1);

				Guilds.Data guild = Guilds.fromName(guildname);
				if (guild != null && (Permissions.havePermsRequired(guild, msgEvent.getAuthor(), Permissions.GUILD_PASS) || Permissions.havePermsRequired(global, msgEvent.getAuthor(), Permissions.GUILD_PASS)))
					target = guild;
			}

			ICommand command = Commands.getCommands(target).get(baseCmd.toLowerCase());

			if (command != null) {
				CommandEvent event = new CommandEvent(msgEvent, target, command, splitArgs(cmd, 2)[1]);
				if (!Permissions.canRunCommand(target, event)) event.getAnswers().noperm().queue();
				else if (toofast && !Utils.canExecuteCmd(msgEvent)) event.getAnswers().toofast().queue();
				else {
					if (event.getCommand().sendStartTyping()) event.sendAwaitableTyping();
					Statistics.cmds++;
					Utils.async(event.getAuthor().getName() + ">" + baseCmd, () -> {
						try {
							execute(event);
						} catch (Exception e) {
							event.getAnswers().exception(e).queue();
						}
					}).run();
				}
			}
		}
	}
}
