/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [07/11/16 21:22]
 */

package cf.adriantodt.David.modules.cmds.manager;

import cf.adriantodt.David.commands.base.CommandEvent;
import cf.adriantodt.David.commands.base.ICommand;
import cf.adriantodt.David.commands.base.UserCommand;
import cf.adriantodt.David.loader.Module;
import cf.adriantodt.David.loader.Module.LoggerInstance;
import cf.adriantodt.David.loader.Module.SubscribeJDA;
import cf.adriantodt.David.modules.db.GuildModule;
import cf.adriantodt.David.modules.db.UserCommandsModule;
import cf.adriantodt.David.oldmodules.init.Statistics;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cf.adriantodt.David.loader.Module.Type.STATIC;
import static cf.adriantodt.utils.AsyncUtils.async;
import static cf.adriantodt.utils.AsyncUtils.asyncSleepThen;
import static cf.adriantodt.utils.CollectionUtils.concatMaps;
import static cf.adriantodt.utils.StringUtils.splitArgs;

@Module(name = "cmdmanager", type = STATIC)
@SubscribeJDA
public class CommandManager {
	private static final Map<String, ICommand> COMMANDS = new HashMap<>();
	@LoggerInstance
	private static Logger logger = null;

	@SubscribeEvent
	public static void onMessageReceived(GuildMessageReceivedEvent msgEvent) {
		if (msgEvent.getAuthor().equals(msgEvent.getJDA().getSelfUser())) {
			asyncSleepThen(15 * 1000, () -> {
				if (GuildModule.fromDiscord(msgEvent.getGuild()).getFlag("cleanup"))
					msgEvent.getMessage().deleteMessage();
			}).run();
			return;
		} else if (msgEvent.getAuthor().isBot()) {
			return;
		}

		async(() -> onCommand(msgEvent)).run();
	}

	public static void onCommand(GuildMessageReceivedEvent msgEvent) {
		GuildModule.Data local = GuildModule.fromDiscord(msgEvent.getGuild()), global = GuildModule.GLOBAL, target = local;
		if (!PermissionsModule.havePermsRequired(global, msgEvent.getAuthor(), PermissionsModule.RUN_CMDS) || !PermissionsModule.havePermsRequired(local, msgEvent.getAuthor(), PermissionsModule.RUN_CMDS))
			return;

		String cmd = msgEvent.getMessage().getRawContent();

		List<String> prefixes = new ArrayList<>(local.getCmdPrefixes());
		prefixes.add("<@!" + msgEvent.getJDA().getSelfUser().getId() + "> ");
		prefixes.add("<@" + msgEvent.getJDA().getSelfUser().getId() + "> ");
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

				GuildModule.Data guild = GuildModule.fromName(guildname);
				if (guild != null && (PermissionsModule.havePermsRequired(guild, msgEvent.getAuthor(), PermissionsModule.GUILD_PASS) || PermissionsModule.havePermsRequired(global, msgEvent.getAuthor(), PermissionsModule.GUILD_PASS)))
					target = guild;
			}

			ICommand command = getCommands(target).get(baseCmd.toLowerCase());

			if (command != null) {
				CommandEvent event = new CommandEvent(msgEvent, target, command, splitArgs(cmd, 2)[1]);
				if (!PermissionsModule.canRunCommand(target, event)) event.getAnswers().noperm().queue();
				else if (TooFast.enabled && !TooFast.canExecuteCmd(msgEvent)) event.getAnswers().toofast().queue();
				else {
					if (event.getCommand().sendStartTyping()) event.sendAwaitableTyping();
					Statistics.cmds++;
					Thread.currentThread().setName(event.getAuthor().getName() + ">" + baseCmd);
					try {
						execute(event);
					} catch (Exception e) {
						event.getAnswers().exception(e).queue();
					}
				}
			}
		}
	}

	public static void addCommand(String name, ICommand command) {
		COMMANDS.put(name.toLowerCase(), command);
	}

	public static Map<String, ICommand> getCommands(GuildModule.Data guild) {
		return concatMaps(getBaseCommands(), new HashMap<>(getUserCommands(guild)));
	}

	public static Map<String, ICommand> getBaseCommands() {
		return COMMANDS;
	}

	public static Map<String, UserCommand> getUserCommands(GuildModule.Data guild) {
		return concatMaps(getGlobalUserCommands(), getLocalUserCommands(guild));
	}

	public static Map<String, UserCommand> getGlobalUserCommands() {
		return getLocalUserCommands(GuildModule.GLOBAL);
	}

	public static Map<String, UserCommand> getLocalUserCommands(GuildModule.Data guild) {
		return UserCommandsModule.allFrom(guild);
	}

	public static void execute(CommandEvent event) {
		if (PermissionsModule.canRunCommand(GuildModule.GLOBAL, event) || PermissionsModule.canRunCommand(event.getGuild(), event))
			event.getCommand().run(event);
		else event.getAnswers().noperm().queue();
	}

	public static class TooFast {
		public static final Map<User, Integer> userTimeout = new HashMap<>();
		public static boolean enabled = true;

		public static boolean canExecuteCmd(GuildMessageReceivedEvent event) {
			int count;
			synchronized (userTimeout) {
				count = userTimeout.getOrDefault(event.getAuthor(), 0);
				userTimeout.put(event.getAuthor(), count + 1);
			}
			return count + 1 < 5;
		}
	}
}
