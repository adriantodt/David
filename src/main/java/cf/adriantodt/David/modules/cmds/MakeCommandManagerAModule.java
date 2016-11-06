/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [05/11/16 22:53]
 */

package cf.adriantodt.David.modules.cmds;

import cf.adriantodt.David.modules.db.MakePermissionsAModule;
import cf.adriantodt.oldbot.Bot;
import cf.adriantodt.David.commands.base.CommandEvent;
import cf.adriantodt.David.commands.base.ICommand;
import cf.adriantodt.David.commands.base.ProvidesCommand;
import cf.adriantodt.David.commands.base.UserCommand;
import cf.adriantodt.David.commands.utils.Statistics;
import cf.adriantodt.oldbot.data.entities.Guilds;
import cf.adriantodt.oldbot.data.entities.UserCommands;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static cf.adriantodt.utils.AsyncUtils.async;
import static cf.adriantodt.utils.AsyncUtils.asyncSleepThen;
import static cf.adriantodt.utils.CollectionUtils.concatMaps;
import static cf.adriantodt.utils.Log4jUtils.logger;
import static cf.adriantodt.utils.StringUtils.splitArgs;

public class MakeCommandManagerAModule {
	public static final Logger LOGGER = logger();
	public static final Map<String, ICommand> COMMANDS = new HashMap<>();
	public static boolean toofast = true;

	public static void execute(CommandEvent event) {
		if (MakePermissionsAModule.canRunCommand(Guilds.GLOBAL, event) || MakePermissionsAModule.canRunCommand(event.getGuild(), event))
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
		if (!MakePermissionsAModule.havePermsRequired(global, msgEvent.getAuthor(), MakePermissionsAModule.RUN_CMDS) || !MakePermissionsAModule.havePermsRequired(local, msgEvent.getAuthor(), MakePermissionsAModule.RUN_CMDS))
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
				if (guild != null && (MakePermissionsAModule.havePermsRequired(guild, msgEvent.getAuthor(), MakePermissionsAModule.GUILD_PASS) || MakePermissionsAModule.havePermsRequired(global, msgEvent.getAuthor(), MakePermissionsAModule.GUILD_PASS)))
					target = guild;
			}

			ICommand command = getCommands(target).get(baseCmd.toLowerCase());

			if (command != null) {
				CommandEvent event = new CommandEvent(msgEvent, target, command, splitArgs(cmd, 2)[1]);
				if (!MakePermissionsAModule.canRunCommand(target, event)) event.getAnswers().noperm().queue();
				else if (toofast && !TooFast.canExecuteCmd(msgEvent)) event.getAnswers().toofast().queue();
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
		).forEach(Loader::load);
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

	public static class Loader {
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
	}

	public static class TooFast {
		public static final Map<User, Integer> userTimeout = new HashMap<>();

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
