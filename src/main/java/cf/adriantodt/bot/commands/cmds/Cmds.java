/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [31/10/16 21:31]
 */

package cf.adriantodt.bot.commands.cmds;

import cf.adriantodt.bot.commands.Permissions;
import cf.adriantodt.bot.commands.base.*;
import cf.adriantodt.bot.data.entities.I18n;
import cf.adriantodt.bot.data.entities.UserCommands;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cf.adriantodt.bot.commands.CommandManager.*;
import static cf.adriantodt.bot.commands.Permissions.*;
import static cf.adriantodt.bot.data.entities.I18n.getLocalized;
import static cf.adriantodt.bot.utils.Formatter.encase;

public class Cmds {
	@ProvidesCommand("cmds")
	private static ICommand createCommand() {
		return Commands.buildTree()
			.addCommand("list",
				Commands.buildSimple("cmds.list.usage").setAction(event -> {
					List<String> cmds, userCmds, tmp = new ArrayList<>();

					cmds = getCommands(event.getGuild()).entrySet().stream().filter(entry -> canRunCommand(event.getGuild(), event.createChild(entry.getValue(), event.getArgs()))).filter(entry -> {
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
				if (!event.checkPrivateChatIsOkay()) return;

				MessageChannel channel = event.getAuthor().getPrivateChannel();
				List<String> cmds = getBaseCommands().entrySet().stream().filter(entry -> canRunCommand(event.getGuild(), event.createChild(entry.getValue(), event.getArgs()))).map(
					(entry) -> entry.getKey() + " - " + entry.getValue().toString(I18n.getLocale(event))).sorted(String::compareTo).collect(Collectors.toList());

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
						if (Stream.of("loc://", "js://", "aud://").anyMatch(args[1]::startsWith) && !Permissions.havePermsRequired(event.getGuild(), event.getAuthor(), MANAGE_SPECIAL_USER_CMDS)) {
							event.awaitTyping().getAnswers().noperm(MANAGE_SPECIAL_USER_CMDS).queue();
							return;
						}

						UserCommand cmd = getLocalUserCommands(event.getGuild()).get(args[0].toLowerCase());
						if (cmd == null) {
							UserCommand ncmd = new UserCommand();
							ncmd.responses.add(args[1]);
							UserCommands.register(ncmd, args[0].toLowerCase(), event.getGuild());
							event.getAnswers().bool(true).queue();
						} else {
							cmd.responses.add(args[1]);
							UserCommands.update(cmd);
							event.getAnswers().bool(true).queue();
						}
					}
				}).build())
			.addCommand("rm", Commands.buildSimple("cmds.rm.usage", MANAGE_USER_CMDS)
				.setAction(event -> {
					if (event.getArgs().trim().isEmpty()) event.getAnswers().invalidargs().queue();
					else {
						UserCommand command = getLocalUserCommands(event.getGuild()).get(event.getArgs().toLowerCase());
						if (command == null) event.getAnswers().invalidargs().queue();
						else {
							UserCommands.remove(command);
							event.getAnswers().bool(true).queue();
						}
					}
				}).build())
			.addCommand("debug", Commands.buildSimple("cmds.debug.usage", MANAGE_USER_CMDS).setAction(event -> {
				if (event.getArgs().trim().isEmpty()) event.getAnswers().invalidargs().queue();
				else {
					ICommand cmd = getCommands(event.getGuild()).get(event.getArgs());
					if (cmd == null) event.getAnswers().invalidargs().queue();
					else
						event.getAnswers().send("***`" + event.getArgs() + "`:*** " + cmd.toString(I18n.getLocale(event))).queue();
				}
			}).build())
			.build();
	}
}
