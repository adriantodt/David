/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [01/11/16 12:36]
 */

package cf.adriantodt.bot.commands;

import cf.adriantodt.bot.Bot;
import cf.adriantodt.bot.commands.base.CommandEvent;
import cf.adriantodt.bot.commands.base.ICommand;
import cf.adriantodt.bot.commands.base.ProvidesCommand;
import cf.adriantodt.bot.commands.base.UserCommand;
import cf.adriantodt.bot.commands.cmds.*;
import cf.adriantodt.bot.commands.utils.Statistics;
import cf.adriantodt.bot.data.entities.Guilds;
import cf.adriantodt.bot.data.entities.UserCommands;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static cf.adriantodt.bot.commands.Permissions.canExecuteCmd;
import static cf.adriantodt.utils.AsyncUtils.async;
import static cf.adriantodt.utils.AsyncUtils.asyncSleepThen;
import static cf.adriantodt.utils.CollectionUtils.concatMaps;
import static cf.adriantodt.utils.StringUtils.splitArgs;

public class CommandManager {
	public static final Map<String, ICommand> COMMANDS = new HashMap<>();
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
		} else if (msgEvent.getAuthor().isBot()) {
			return;
		}

		async(() -> onCommand(msgEvent)).run();
	}

	public static void onCommand(GuildMessageReceivedEvent msgEvent) {
		Guilds.Data local = Guilds.fromDiscord(msgEvent.getGuild()), global = Guilds.GLOBAL, target = local;
		if (!Permissions.havePermsRequired(global, msgEvent.getAuthor(), Permissions.RUN_CMDS) || !Permissions.havePermsRequired(local, msgEvent.getAuthor(), Permissions.RUN_CMDS))
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

			ICommand command = getCommands(target).get(baseCmd.toLowerCase());

			if (command != null) {
				CommandEvent event = new CommandEvent(msgEvent, target, command, splitArgs(cmd, 2)[1]);
				if (!Permissions.canRunCommand(target, event)) event.getAnswers().noperm().queue();
				else if (toofast && !canExecuteCmd(msgEvent)) event.getAnswers().toofast().queue();
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

	@SubscribeEvent
	public static void onReady(ReadyEvent event) {
		Arrays.asList(
			BotCmd.class, CmdsCmd.class, FeedCmd.class, FunnyCmd.class, GuildCmd.class, PushCmd.class, UserCmd.class, TestCmds.class, UtilsCmd.class
		).forEach(CommandManager::load);
	}

	public static void load(Class clazz) {
		for (Method m : clazz.getDeclaredMethods()) {
			if (!m.isAnnotationPresent(ProvidesCommand.class) || !Modifier.isStatic(m.getModifiers())) continue;
			if (m.getParameterTypes().length == 0 && ICommand.class.isAssignableFrom(m.getReturnType())) {
				m.setAccessible(true);
				try {
					addCommand(m.getAnnotation(ProvidesCommand.class).value(), (ICommand) m.invoke(null));
				} catch (Exception e) {
					LogManager.getLogger("CommandManager - AnnotationLoader").error("Error while creating new Command: ", e);
				}
			}
		}
	}

	public static void addCommand(String name, ICommand command) {
		COMMANDS.put(name.toLowerCase(), command);
	}

	public static Map<String, ICommand> getCommands(Guilds.Data guild) {
		return concatMaps(getBaseCommands(), new HashMap<>(getUserCommands(guild)));
	}

	public static Map<String, ICommand> getBaseCommands() {
		return COMMANDS;
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

}
