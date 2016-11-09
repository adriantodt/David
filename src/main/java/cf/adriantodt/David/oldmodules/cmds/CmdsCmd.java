/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [07/11/16 20:36]
 */

package cf.adriantodt.David.oldmodules.cmds;

import cf.adriantodt.David.commands.base.Commands;
import cf.adriantodt.David.commands.base.Holder;
import cf.adriantodt.David.commands.base.ICommand;
import cf.adriantodt.David.commands.base.UserCommand;
import cf.adriantodt.David.loader.Module;
import cf.adriantodt.David.loader.Module.Command;
import cf.adriantodt.David.loader.Module.SubscribeJDA;
import cf.adriantodt.David.loader.Module.Type;
import cf.adriantodt.David.oldmodules.db.UserCommandsModule;
import cf.adriantodt.David.oldmodules.db.I18nModule;
import cf.adriantodt.David.modules.cmds.PermissionsModule;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cf.adriantodt.David.oldmodules.db.I18nModule.getLocalized;
import static cf.adriantodt.David.utils.Formatter.encase;

@Module(Type.STATIC)
@SubscribeJDA
public class CmdsCmd {
	@Command("cmds")
	private static ICommand createCommand() {
		return Commands.buildTree()
			.addCommand("list",
				Commands.buildSimple("cmds.list.usage").setAction(event -> {
					List<String> cmds, userCmds, tmp = new ArrayList<>();
					cmds = CommandManager.getCommands(event.getGuild()).entrySet().stream().filter(entry -> canRunCommand(event.getGuild(), event.createChild(entry.getValue(), event.getArgs()))).filter(entry -> {
						if (entry.getValue() instanceof UserCommand) {
							tmp.add(entry.getKey());
							return false;
						}
						return true;
					}).map(Map.Entry::getKey).sorted(String::compareTo).collect(Collectors.toList());

					userCmds = tmp.stream().sorted(String::compareTo).collect(Collectors.toList());

					Holder<StringBuilder> b = new Holder<>();
					Holder<Boolean> first = new Holder<>();

					b.var = new StringBuilder().append("**").append(getLocalized("cmds.commandsAvailable", event)).append(":**\n *");
					first.var = true;
					cmds.forEach(s -> {
						if (first.var) {
							first.var = false;
							b.var.append(s);
						} else {
							String a = " " + s;
							if (b.var.length() + a.length() >= 1999) {
								b.var.append("*");
								event.getAnswers().send(b.var.toString()).queue();
								b.var = new StringBuilder("*");
							}
							b.var.append(a);
						}

					});
					if (first.var) b.var.append("(").append(getLocalized("cmds.noneAvailable", event)).append(")");
					b.var.append("*");
					event.getAnswers().send(b.var.toString()).queue();

					b.var = new StringBuilder().append("**").append(getLocalized("cmds.userCommandsAvailable", event)).append(":**\n *");
					first.var = true;

					userCmds.forEach(s -> {
						if (first.var) {
							first.var = false;
							b.var.append(s);
						} else {
							String a = " " + s;
							if (b.var.length() + a.length() >= 1999) {
								b.var.append("*");
								event.getAnswers().send(b.var.toString()).queue();
								b.var = new StringBuilder("*");
							}
							b.var.append(a);
						}

					});
					if (first.var) b.var.append("(").append(getLocalized("cmds.noneAvailable", event)).append(")");
					b.var.append("*");
					event.getAnswers().send(b.var.toString()).queue();
				}).build())
			.addDefault("list")
			.addCommand("detailed", Commands.buildSimple("cmds.detailed.usage").setAction(event -> {
				if (!event.tryOpenPrivateChannel()) return;

				MessageChannel channel = event.getAuthor().getPrivateChannel();
				List<String> cmds = CommandManager.getBaseCommands().entrySet().stream().filter(entry -> canRunCommand(event.getGuild(), event.createChild(entry.getValue(), event.getArgs()))).map(
					(entry) -> entry.getKey() + " - " + entry.getValue().toString(I18nModule.getLocale(event))).sorted(String::compareTo).collect(Collectors.toList());

				Holder<StringBuilder> b = new Holder<>(new StringBuilder().append("**").append(getLocalized("cmds.commandsAvailable", event)).append(":**\n"));
				Holder<Boolean> first = new Holder<>(true);

				cmds.forEach(s -> {
					String v = encase(s);
					if (first.var) {
						first.var = false;
						b.var.append(v);
					} else {
						if (b.var.length() + v.length() >= 1995) {
							channel.sendMessage(b.var.toString()).queue();
							b.var = new StringBuilder();
						}
						b.var.append(v);
					}

				});
				if (first.var) b.var.append("(").append(getLocalized("cmds.noneAvailable", event)).append(")");
				channel.sendMessage(b.var.toString()).queue();
				event.getAnswers().send(event.getAuthor().getAsMention() + " :mailbox_with_mail:").queue();
			}).build())
			.addCommand("add", Commands.buildSimple("cmds.add.usage", MANAGE_USER_CMDS)
				.setAction(event -> {
					String[] args = event.getArgs(2); //COMMAND_NAME RESPONSE
					if (args[0].isEmpty() | args[1].isEmpty()) event.getAnswers().invalidargs().queue();
					else {
						if (Stream.of("loc://", "js://", "aud://").anyMatch(args[1]::startsWith) && !PermissionsModule.havePermsRequired(event.getGuild(), event.getAuthor(), MANAGE_SPECIAL_USER_CMDS)) {
							event.awaitTyping().getAnswers().noperm(MANAGE_SPECIAL_USER_CMDS).queue();
							return;
						}

						UserCommand cmd = CommandManager.getLocalUserCommands(event.getGuild()).get(args[0].toLowerCase());
						if (cmd == null) {
							UserCommand ncmd = new UserCommand();
							ncmd.responses.add(args[1]);
							UserCommandsModule.register(ncmd, args[0].toLowerCase(), event.getGuild());
							event.getAnswers().bool(true).queue();
						} else {
							cmd.responses.add(args[1]);
							UserCommandsModule.update(cmd);
							event.getAnswers().bool(true).queue();
						}
					}
				}).build())
			.addCommand("rm", Commands.buildSimple("cmds.rm.usage", MANAGE_USER_CMDS)
				.setAction(event -> {
					if (event.getArgs().trim().isEmpty()) event.getAnswers().invalidargs().queue();
					else {
						UserCommand command = CommandManager.getLocalUserCommands(event.getGuild()).get(event.getArgs().toLowerCase());
						if (command == null) event.getAnswers().invalidargs().queue();
						else {
							UserCommandsModule.remove(command);
							event.getAnswers().bool(true).queue();
						}
					}
				}).build())
			.addCommand("debug", Commands.buildSimple("cmds.debug.usage", MANAGE_USER_CMDS).setAction(event -> {
				if (event.getArgs().trim().isEmpty()) event.getAnswers().invalidargs().queue();
				else {
					ICommand cmd = CommandManager.getCommands(event.getGuild()).get(event.getArgs());
					if (cmd == null) event.getAnswers().invalidargs().queue();
					else
						event.getAnswers().send("***`" + event.getArgs() + "`:*** " + cmd.toString(I18nModule.getLocale(event))).queue();
				}
			}).build())
			.build();
	}
}
